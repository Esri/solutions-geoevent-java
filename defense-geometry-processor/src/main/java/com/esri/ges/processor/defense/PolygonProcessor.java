package com.esri.ges.processor.defense;

import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;

import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;
import com.esri.ges.spatial.Geometry;
import com.esri.ges.spatial.GeometryException;
import com.esri.ges.spatial.Spatial;

public class PolygonProcessor extends GeoEventProcessorBase {
	Spatial spatial;
	private SpatialReference srIn;
	private SpatialReference srBuffer;
	private SpatialReference srOut;
	public PolygonProcessor(GeoEventProcessorDefinition definition, Spatial s)
			throws ComponentException {
		super(definition);
		spatial = s;
		geoEventMutator= true;
	}

	@Override
	public GeoEvent process(GeoEvent evt) throws Exception {
		int inwkid = (Integer) properties.get("wkidin").getValue();
		int outwkid = (Integer) properties.get("wkidout").getValue();
		int bufferwkid = (Integer) properties.get("wkidbuffer").getValue();
		srIn = SpatialReference.create(inwkid);
		srBuffer = SpatialReference.create(bufferwkid);
		srOut = SpatialReference.create(outwkid);
		
		String eventfld = properties.get("polyfld").getValue().toString();
		String[] arr = eventfld.split(":");
		String geostring = (String)evt.getField(arr[1]);
		
		String format = properties.get("polyformat").getValue().toString();
		com.esri.ges.spatial.Geometry geo = null;
		if(format.equals("Json"))	
		{
			geo=constructJsonGeometry(geostring);
		}
		else if(format.equals("CAP"))
		{
			geo=constructCAPGeometry(geostring);
		}
		evt.setGeometry(geo);
		return evt;
	}
	
	private com.esri.ges.spatial.Geometry constructJsonGeometry(String jsongeo) throws JsonParseException, IOException, GeometryException
	{
		
		String jsonOut = jsongeo;
		JsonFactory jf = new JsonFactory();
		JsonParser json = jf.createJsonParser(jsongeo);
		MapGeometry mgeo = GeometryEngine.jsonToGeometry(json);
		if (!mgeo.getSpatialReference().equals(srOut))
		{
			com.esri.core.geometry.Geometry geo = mgeo.getGeometry();
			com.esri.core.geometry.Geometry projGeo = GeometryEngine.project(geo, srIn, srOut);
			jsonOut = GeometryEngine.geometryToJson(srOut, projGeo);
		}
		return spatial.fromJson(jsonOut);
	}
	
	private com.esri.ges.spatial.Geometry constructCAPGeometry(String geoString) throws GeometryException
	{
		String[] pairs = geoString.split(" ");
		
		Polygon polygon = new Polygon();
		Boolean firstit = true;
		for(String coords: pairs)
		{
			
			String[] tuple = coords.split(",");
			Double x = Double.parseDouble(tuple[0]);
			Double y = Double.parseDouble(tuple[1]);
			Point p = new Point(x,y);
			Double z = Double.NaN;
			if (tuple.length>2)
			{
				z = Double.parseDouble(tuple[2]);
				p.setZ(z);
			}
			if(firstit)
			{
				polygon.startPath(p);
				firstit=false;
			}
			else
			{
				polygon.lineTo(p);
			}
		}
		polygon.closeAllPaths();
		String json = GeometryEngine.geometryToJson(srIn, polygon);
		return spatial.fromJson(json);
	}

}
