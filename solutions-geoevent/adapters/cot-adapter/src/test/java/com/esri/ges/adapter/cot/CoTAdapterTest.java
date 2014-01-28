package com.esri.ges.adapter.cot;

/*
 * #%L
 * CoTAdapterTest.java - Esri :: AGES :: Solutions :: Adapter :: CoT - Esri - 2013
 * org.codehaus.mojo-license-maven-plugin-1.5
 * $Id: update-file-header-config.apt.vm 17764 2012-12-12 10:22:04Z tchemit $
 * $HeadURL: https://svn.codehaus.org/mojo/tags/license-maven-plugin-1.5/src/site/apt/examples/update-file-header-config.apt.vm $
 * %%
 * Copyright (C) 2013 - 2014 Esri
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.After;
import org.junit.Before;

import com.esri.geoevent.solutions.adapter.cot.CoTAdapterInbound;
import com.esri.geoevent.solutions.adapter.cot.CoTAdapterServiceInbound;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.FieldGroup;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.geoevent.GeoEventPropertyName;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.messaging.GeoEventCreator;
import com.esri.ges.messaging.GeoEventListener;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.spatial.Envelope;
import com.esri.ges.spatial.Geometry;
import com.esri.ges.spatial.GeometryException;
import com.esri.ges.spatial.GeometryType;
import com.esri.ges.spatial.MultiPoint;
import com.esri.ges.spatial.Point;
import com.esri.ges.spatial.Polygon;
import com.esri.ges.spatial.Polyline;
import com.esri.ges.spatial.Spatial;
import com.esri.ges.spatial.SpatialReference;

public class CoTAdapterTest {
	CoTAdapterInbound adapter;
	private GeoEventDefinition geoEventDefinition;

	private class MyPoint implements Point {
		private GeometryType type;
		private int id;
		private double x;
		private double y;

		@Override
		public GeometryType getType() {
			return type;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public void setX(double x) {
			this.x = x;
		}

		@Override
		public double getY() {
			return y;
		}

		@Override
		public void setY(double y) {
			this.y = y;
		}

		@Override
		public String toJson() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SpatialReference getSpatialReference() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setSpatialReference(SpatialReference sr) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean equals(Point arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public double getZ() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setZ(double arg0) {
			// TODO Auto-generated method stub

		}
	}

	private class TestFieldGroup implements FieldGroup
	{

		@Override
		public void setField(int i, Object value)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void setField(String attName, Object value)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public FieldGroup createFieldGroup(String groupName) throws FieldException
		{
			return new TestFieldGroup();
		}

		@Override
		public Object getField(int i)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getField(String attName)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List getFields(int i) throws FieldException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List getFields(String attName) throws FieldException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FieldGroup getFieldGroup(int i) throws FieldException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FieldGroup getFieldGroup(String attName) throws FieldException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<FieldGroup> getFieldGroups(int i) throws FieldException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<FieldGroup> getFieldGroups(String attName) throws FieldException
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FieldGroup clone(GeoEvent arg0) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	@Before
	public void setUp() throws Exception
	{
		GeoEventCreator geoEventCreator = new GeoEventCreator()
		{

			@Override
			public GeoEventDefinitionManager getGeoEventDefinitionManager()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public GeoEvent create(String guid) throws MessagingException
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public GeoEvent create(String name, String owner) throws MessagingException
			{
				return new GeoEvent()
				{
					Object[] array = new Object[32767];

					@Override
					public Object[] getAllFields() {
						return array;
					}

					@Override
					public void setField(int i, Object value) {
						// TODO Auto-generated method stub
						array[i] = value;
					}

					@Override
					public void setField(String attName, Object value) {

					}

					@Override
					public Set<Entry<GeoEventPropertyName, Object>> getProperties() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public boolean hasProperty(GeoEventPropertyName name) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public Object getProperty(GeoEventPropertyName name) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public void setProperty(String name, Object value) {
						// TODO Auto-generated method stub

					}

					@Override
					public void setProperty(GeoEventPropertyName name,
							Object value) {
						// TODO Auto-generated method stub

					}

					@Override
					public String getTrackId() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Geometry getGeometry() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public byte[] toByteArray() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public void setGeometry(Geometry arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public String getGuid() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Date getStartTime() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Object getField(int i)
					{
						return array[i];
					}

					@Override
					public Object getField(String attName)
					{
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public List getFields(int i) throws FieldException
					{
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public List getFields(String attName) throws FieldException
					{
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public FieldGroup getFieldGroup(int i) throws FieldException
					{
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public FieldGroup getFieldGroup(String attName) throws FieldException
					{
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public List<FieldGroup> getFieldGroups(int i) throws FieldException
					{
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public List<FieldGroup> getFieldGroups(String attName) throws FieldException
					{
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public GeoEventDefinition getGeoEventDefinition()
					{
						return geoEventDefinition;
					}

					@Override
					public void setAllFields(Object[] atts)
					{
						this.array = atts;
					}

					@Override
					public FieldGroup createFieldGroup(String groupName)
					{
						return new TestFieldGroup();
					}

					@Override
					public Geometry getGeometry(String name)
					{
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public void setGeometry(String name, Geometry geometry)
					{
						// TODO Auto-generated method stub

					}

					@Override
					public FieldGroup clone(GeoEvent arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public boolean isMutable() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void setAsImmutable() {
						// TODO Auto-generated method stub
						
					}
				};
			}

			@Override
			public GeoEvent create(String guid, Object[] values) throws MessagingException
			{
				return null;
			}
		};
		/*return new GeoEvent() */

		Map<String, GeoEventDefinition> geoEventDefinitions = new HashMap<String, GeoEventDefinition>();
		Spatial spatial = new Spatial()
		{

			@Override
			public Point createPoint(double x, double y, double z, int wkid)
			{
				Point point = new MyPoint();
				point.setX(x);
				point.setY(y);
				point.setZ(z);
				return point;
			}

			@Override
			public Point createPoint(double x, double y, int wkid)
			{
				Point point = new MyPoint();
				point.setX(x);
				point.setY(y);
				return point;
			}

			@Override
			public Geometry fromJson(String json) throws GeometryException
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean enter(Geometry arg0, Geometry arg1, Geometry arg2)
			{
				return false;
			}

			@Override
			public boolean equals(Geometry arg0, Geometry arg1)
			{
				return false;
			}

			@Override
			public boolean exit(Geometry arg0, Geometry arg1, Geometry arg2)
			{
				return false;
			}

			@Override
			public int getWkid()
			{
				return 0;
			}

			@Override
			public void setWkid(int wkid)
			{
			}

			@Override
			public SpatialReference getSpatialReference()
			{
				return null;
			}

			@Override
			public Envelope queryEnvelope(Geometry g)
			{
				return null;
			}

			@Override
			public boolean inside(Geometry g1, Geometry g2)
			{
				return false;
			}

			@Override
			public boolean outside(Geometry g1, Geometry g2)
			{
				return false;
			}

			@Override
			public Geometry aggregate(List<Geometry> geometries, GeometryType type)
			{
				return null;
			}

			@Override
			public MultiPoint createMultiPoint()
			{
				return null;
			}

			@Override
			public Polyline createPolyline()
			{
				return null;
			}

			@Override
			public Polygon createPolygon()
			{
				return null;
			}

			@Override
			public Envelope createEnvelope(double xmin, double ymin, double xmax, double ymax, int wkid)
			{
				return null;
			}

			@Override
			public int getWkidFromText(String wkText)
			{
				return 0;
			}

		};

		String guid = "...";
		CoTAdapterServiceInbound srvc = new CoTAdapterServiceInbound();
		//Map<String,GeoEventDefinition> defs = srvc.getGeoEventDefinitions();
		//if( ! defs.isEmpty() )
		//{
			//geoEventDefinition = defs.values().iterator().next();
			//guid = geoEventDefinition.getGuid();
		//}
		//adapter = new CoTAdapterInbound(srvc, guid);
		//adapter.setSpatial(spatial);
		//adapter.setGeoEventCreator(geoEventCreator);
	}

	@After
	public void tearDown() throws Exception {
	}
	//Ryan said the tests were broken
	//@Test
	public void testProcessBuffer() throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(1024);
		String channelId = "";
		GeoEventListener dml = new GeoEventListener() {
			@Override
			public void receive(GeoEvent dm) {
				// TODO Auto-generated method stub

			}
		};

		String xml = "<?xml version='1.0'?><event stale=\"2012-03-02T17:33:50.19Z\" "
				+ "			how=\"m-r-p\""
				+ "			opex=\"e-JEFX06\""
				+ "			qos=\"0-r-c\""
				+ " type=\"a-f-G-U-C-V-R-U-E\" uid=\"Debug.002\" >"
				+ "<point lat=\"23.12\" lon=\"54.78\"/>"
				+ "<detail >"
				+ "     <_flow-tags_ debug=\"2012-03-02T17:32:37.00Z\"  />"
				+ "     <spatial>"
				+ "          <attitude pitch=\"-1.05\" roll=\"15.64\" yaw=\"-0.23\" />"
				+ "          <spin pitch=\"-4.46\" roll=\"-9.02\" yaw=\"-0.91\" />"
				+ "     </spatial>"
				+ "</detail>"
				+ "</event>";
		bb.put(xml.getBytes());
		bb.flip();

		adapter.setGeoEventListener(dml);
		adapter.receive(bb, channelId);

	}
	//Ryan said the tests were broken
	//@Test
	public void testProcessBuffer2() throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(1024);
		String channelId = "";
		GeoEventListener dml = new GeoEventListener() {
			@Override
			public void receive(GeoEvent dm) {
				// TODO Auto-generated method stub

			}
		};

		String xml = "<?xml version='1.0'?><event stale=\"2012-03-02T17:33:50.19Z\" how=\"h-e\" type=\"a-h-A-M-H-U-O\" uid=\"Debug.002\">"
				+ "<point lat=\"23.12\" lon=\"54.78\"/><detail ><_flow-tags_ debug=\"2012-03-02T17:32:37.00Z\" debug2=\"something great!\" />"
				+ "</detail></event>";
		bb.put(xml.getBytes());
		bb.flip();

		adapter.setGeoEventListener(dml);
		adapter.receive(bb, channelId);

	}

	//Ryan said the tests were broken
	//@Test
	public void testProcessBuffer3() throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(1024);
		String channelId = "";
		GeoEventListener dml = new GeoEventListener() {
			@Override
			public void receive(GeoEvent dm) {
				// TODO Auto-generated method stub

			}
		};

		String xml = "<?xml version=\"1.0\" standalone=\"yes\"?>"
				+ "<event version=\"2.0\" uid=\"example-uid.02\" type=\"a-k-A-M-F-P\" stale=\"2004-12-30T18:51:06.00Z\" start=\"2004-12-30T18:41:06.00Z\" time=\"2004-12-30T18:41:06.00Z\""
				+ " qos=\"0-r-c\" opex=\"e-JEFX06\" how=\"m-p\" access=\"\"> "
				+ "<point lon=\"-85\" le=\"70\" lat=\"33.4\" hae=\"130\" ce=\"100\"/> "
				+ "<detail> "
				+ "<uid c2pc=\"DVD075074365\"/> "
				+ "<track slope=\"2\" speed=\"30.3\" course=\"23.0\"/>"
				+ "<_flow-tags_ gccs=\"2005-01-20T18:41:10.00Z\"/>"
				+ "<spatial> <attitude yaw=\"-0.23\" roll=\"15.64\" pitch=\"-1.05\"/> <spin yaw=\"-0.91\" roll=\"-9.02\" pitch=\"-4.46\"/> </spatial>"
				+ "<shape> "
				+ "<ellipse extrude=\"0\" level=\"0\" angle=\"45.\" minor=\"222\" major=\"400\"/> "
				+ "<polyline level=\"1\" closed=\"true\"> "
				+ "<vertex lon=\"-86.383333\" lat=\"34.34992\" hae=\"2.144\"/> "
				+ "<vertex lon=\"-86.383330\" lat=\"34.34992\" hae=\"2.15\"/> "
				+ "<vertex lon=\"-86.383330\" lat=\"34.350\" hae=\"2.13\"/> "
				+ "</polyline> " + "</shape>" + "</detail> " + "</event> ";

		bb.put(xml.getBytes());
		bb.flip();

		adapter.setGeoEventListener(dml);
		adapter.receive(bb, channelId);

	}
}
