/*
 | Copyright 2013 Esri
 |
 | Licensed under the Apache License, Version 2.0 (the "License");
 | you may not use this file except in compliance with the License.
 | You may obtain a copy of the License at
 |
 |    http://www.apache.org/licenses/LICENSE-2.0
 |
 | Unless required by applicable law or agreed to in writing, software
 | distributed under the License is distributed on an "AS IS" BASIS,
 | WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 | See the License for the specific language governing permissions and
 | limitations under the License.
 */
package com.esri.geoevent.solutions.adapter.geomessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.esri.ges.adapter.AdapterDefinition;
import com.esri.ges.adapter.InboundAdapterBase;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.spatial.Point;
import com.esri.ges.util.DateUtil;

public class DefenseInboundAdapter extends InboundAdapterBase
{
	private static final Log LOG = LogFactory.getLog(DefenseInboundAdapter.class);
	private final MessageParser messageParser;
	private final SAXParserFactory saxFactory;
	private final SAXParser saxParser;
	//  private SimpleDateFormat dateFormatLong = new SimpleDateFormat("dd HHmmss'Z' MMM yyyy");
	//  private SimpleDateFormat dateFormatShort = new SimpleDateFormat("dd HHmmss'Z' MMM yy");
	private byte[] bytes = null;
	private final ArrayList<GeoEvent> queue = new ArrayList<GeoEvent>();
	private final boolean tryingToRecoverPartialMessages = false;

	public DefenseInboundAdapter(AdapterDefinition definition) throws ComponentException, ParserConfigurationException, SAXException, IOException
	{
		super(definition);
		messageParser = new MessageParser(this);
		saxFactory = SAXParserFactory.newInstance();
		saxParser = saxFactory.newSAXParser();
	}

	@Override
	public void receive(ByteBuffer buffer, String channelId)
	{
		try
		{
			int remaining = buffer.remaining();
			if (remaining <= 0)
				return;
			if (bytes == null)
			{
				bytes = new byte[remaining];
				buffer.get(bytes);
			}
			else
			{
				byte[] temp = new byte[bytes.length + remaining];
				System.arraycopy(bytes, 0, temp, 0, bytes.length);
				buffer.get(temp, bytes.length, remaining);
				bytes = temp;
			}
			try
			{
				saxParser.parse(new ByteArrayInputStream(bytes), messageParser);
				bytes = null;
				commit();
			}
			catch (SAXException e)
			{
				LOG.error("SAXException while trying to parse the incoming xml.", e);

				// TODO : figure out a way to recover the lost bytes. for now, just
				// throwing them away.
				if (tryingToRecoverPartialMessages)
				{
					queue.clear();
				}
				else
				{
					bytes = null;
					commit();
				}
			}
		}
		catch (IOException e)
		{
			LOG.error("IOException while trying to route data from the byte buffer to the pipe.", e);
		}
	}

	private void commit()
	{
		for (GeoEvent geoEvent : queue)
		{
			if( geoEvent != null )
				geoEventListener.receive(geoEvent);
		}
		queue.clear();
	}

	@SuppressWarnings("incomplete-switch")
  public void queueGeoEvent(HashMap<String, String> fields)
	{
		// in.mark(4 * 1024);
		if (fields.containsKey("_type"))
		{
			String geoEventTypeName = fields.get("_type");
			GeoEvent geoEvent = findAndCreate(geoEventTypeName);
			if (geoEvent == null)
			{
				LOG.error("The incoming GeoEvent of type \"" + geoEventTypeName + "\" does not have a corresponding Event Definition in the ArcGIS GeoEvent server.");
			}
			else
			{
				GeoEventDefinition definition = geoEvent.getGeoEventDefinition();
				for (String fieldName : fields.keySet())
				{
					String fieldValue = fields.get(fieldName);
					try
					{
						FieldDefinition fieldDefinition = definition.getFieldDefinition(fieldName);
						if (fieldDefinition == null)
						{
							LOG.error("The incoming GeoEvent of type \"" + geoEventTypeName + "\" had an attribute called \"" + fieldName + "\"that does not exist in the corresponding Event Definition.");
							continue;
						}
						switch (fieldDefinition.getType())
						{
						case Integer:
							geoEvent.setField(fieldName, Integer.parseInt(fieldValue));
							break;
						case Long:
							geoEvent.setField(fieldName, Long.parseLong(fieldValue));
							break;
						case Short:
							geoEvent.setField(fieldName, Short.parseShort(fieldValue));
							break;
						case Double:
							geoEvent.setField(fieldName, Double.parseDouble(fieldValue));
							break;
						case Float:
              geoEvent.setField(fieldName, Float.parseFloat(fieldValue));
              break;
						case Boolean:
							geoEvent.setField(fieldName, Boolean.parseBoolean(fieldValue));
							break;
						case Date:
							geoEvent.setField(fieldName, DateUtil.convert(fieldValue));
							break;
						case String:
							geoEvent.setField(fieldName, fieldValue);
							break;
						case Geometry:
							String geometryString = fieldValue;
							if (geometryString.contains(";"))
								geometryString = geometryString.substring(0, geometryString.indexOf(';') - 1);
							String[] g = geometryString.split(",");
							double x = Double.parseDouble(g[0]);
							double y = Double.parseDouble(g[1]);
							double z = 0;
							if (g.length > 2)
								z = Double.parseDouble(g[2]);
							int wkid = Integer.parseInt(fields.get("_wkid"));
							Point point = spatial.createPoint(x, y, z, wkid);
							int geometryID = geoEvent.getGeoEventDefinition().getGeometryId();
							geoEvent.setField(geometryID, point.toJson());
							break;
						}
					}
					catch (Exception ex)
					{
						LOG.warn("Error wile trying to parse the GeoEvent field " + fieldName + ":" + fieldValue, ex);
					}
				}
			}
			queue.add(geoEvent);
		}
	}

	private GeoEvent findAndCreate(String name)
	{
		Collection<GeoEventDefinition> results = geoEventCreator.getGeoEventDefinitionManager().searchGeoEventDefinitionByName(name);
		if (!results.isEmpty())
		{
			try
			{
				return geoEventCreator.create(results.iterator().next().getGuid());
			}
			catch (MessagingException e)
			{
				LOG.error("GeoEvent creation failed: " + e.getMessage());
			}
		}
		else
			LOG.error("GeoEvent creation failed: GeoEvent definition '" + name + "' not found.");
		return null;
	}

	@Override
	protected GeoEvent adapt(ByteBuffer buffer, String channelId)
	{
		// Don't need to implement this class because we are overriding the base class's implementation of the receive() function, which prevents this method from being called.
		return null;
	}

}