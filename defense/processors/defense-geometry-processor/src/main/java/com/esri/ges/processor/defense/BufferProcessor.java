package com.esri.ges.processor.defense;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;
import com.esri.ges.spatial.GeometryException;
import com.esri.ges.spatial.Spatial;

public class BufferProcessor extends GeoEventProcessorBase {
	Spatial spatial;
	public BufferProcessor(GeoEventProcessorDefinition definition, Spatial s)
			throws ComponentException {
		super(definition);
		spatial = s;
		geoEventMutator= true;
		
	}

	@Override
	public GeoEvent process(GeoEvent ge) throws Exception {
		double radius = (Double)properties.get("radius").getValue();
		String units = properties.get("units").getValue().toString();
		int inwkid = (Integer) properties.get("wkidin").getValue();
		int outwkid = (Integer) properties.get("wkidout").getValue();
		int bufferwkid = (Integer) properties.get("wkidbuffer").getValue();
		com.esri.ges.spatial.Point eventGeo = (com.esri.ges.spatial.Point) ge.getGeometry();
		double x = eventGeo.getX();
		double y = eventGeo.getY();
		com.esri.ges.spatial.Geometry buffer = constructBuffer(x,y,radius,units,inwkid,bufferwkid,outwkid);
		ge.setGeometry(buffer);
		return ge;
	}
	
	private com.esri.ges.spatial.Geometry constructBuffer(double x, double y, double radius, String units, int wkidin, int wkidbuffer, int wkidout) throws GeometryException
	{
		Point center = new Point();
		center.setX(x);
		center.setY(y);
		SpatialReference srIn = SpatialReference.create(wkidin);
		SpatialReference srBuffer = SpatialReference.create(wkidbuffer);
		SpatialReference srOut = SpatialReference.create(wkidout);
		UnitConverter uc = new UnitConverter();
		int unitout = uc.findWkid(units);
		Unit  u = new LinearUnit(unitout);
		Point centerProj = (Point) GeometryEngine.project(center, srIn, srBuffer);
		Geometry buffer = GeometryEngine.buffer(centerProj, srBuffer, radius, u);
		Geometry bufferout = GeometryEngine.project(buffer, srBuffer, srOut);
		String json = GeometryEngine.geometryToJson(srOut, bufferout);
		return spatial.fromJson(json);
		
	}
	

}
