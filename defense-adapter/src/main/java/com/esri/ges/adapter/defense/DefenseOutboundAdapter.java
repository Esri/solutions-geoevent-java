package com.esri.ges.adapter.defense;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.adapter.AdapterDefinition;
import com.esri.ges.adapter.OutboundAdapterBase;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.spatial.Geometry;
import com.esri.ges.spatial.GeometryType;
import com.esri.ges.spatial.Point;

public class DefenseOutboundAdapter extends OutboundAdapterBase
{
  private static final Log LOG = LogFactory.getLog(DefenseOutboundAdapter.class);
  private StringBuffer stringBuffer = new StringBuffer(10*1024);
  private ByteBuffer byteBuffer = ByteBuffer.allocate(10*1024);
  private Charset charset = Charset.forName("UTF-8");
  private String dateFormat = "yyyy-MM-dd HH:mm:ss";
  private SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

  public DefenseOutboundAdapter(AdapterDefinition definition) throws ComponentException
  {
    super(definition);
  }

  @SuppressWarnings("incomplete-switch")
  @Override
  public void receive(GeoEvent geoEvent)
  {
    String wkid = null;
    stringBuffer.setLength(0);
    stringBuffer.append("<geomessages>");
    stringBuffer.append("<geomessage>");
    GeoEventDefinition definition = geoEvent.getGeoEventDefinition();
    for (FieldDefinition fieldDefinition : definition.getFieldDefinitions())
    {
      String attributeName = fieldDefinition.getName();
      Object value = geoEvent.getField(attributeName);
      stringBuffer.append("<" + attributeName + ">");
      FieldType t = fieldDefinition.getType();
      switch (t)
      {
        case String:
          stringBuffer.append((String) value);
          break;
        case Date:
          Date date = (Date) value;
          stringBuffer.append(formatter.format(date));
          break;
        case Double:
          Double doubleValue = (Double) value;
          stringBuffer.append(doubleValue);
          break;
        case Float:
          Float floatValue = (Float) value;
          stringBuffer.append(floatValue);
          break;
        case Geometry:
          if (definition.getIndexOf(attributeName) == definition.getGeometryId())
          {
            Geometry geom = geoEvent.getGeometry();
            if (geom.getType() == GeometryType.Point)
            {
              Point p = (Point) geom;
              stringBuffer.append(p.getX());
              stringBuffer.append(",");
              stringBuffer.append(p.getY());
              wkid = String.valueOf(p.getSpatialReference().getWkid());
            }
          }
          else
          {
            LOG.error("unable to parse the value for the secondary geometry field \"" + attributeName + "\"");
          }
          break;
        case Integer:
          Integer intValue = (Integer) value;
          stringBuffer.append(intValue);
          break;
        case Long:
          Long longValue = (Long) value;
          stringBuffer.append(longValue);
          break;
        case Short:
          Short shortValue = (Short) value;
          stringBuffer.append(shortValue);
          break;
        case Boolean:
          Boolean booleanValue = (Boolean) value;
          stringBuffer.append(booleanValue);
          break;

      }
      stringBuffer.append("</" + attributeName + ">");
      if (wkid != null)
      {
        String wkidValue = wkid;
        wkid = null;
        stringBuffer.append("<_wkid>");
        stringBuffer.append(wkidValue);
        stringBuffer.append("</_wkid>");
      }
    }
    stringBuffer.append("</geomessage>");
    stringBuffer.append("</geomessages>");
    stringBuffer.append("\r\n");

    ByteBuffer buf = charset.encode(stringBuffer.toString());
    if (buf.position() > 0)
      buf.flip();

    try
    {
      byteBuffer.put(buf);
    }
    catch (BufferOverflowException ex)
    {
      LOG.error("Csv Outbound Adapter does not have enough room in the buffer to hold the outgoing data.  Either the receiving transport object is too slow to process the data, or the data message is too big.");
    }
    byteBuffer.flip();
    byteListener.receive(byteBuffer, geoEvent.getTrackId());
    byteBuffer.clear();
  }
}