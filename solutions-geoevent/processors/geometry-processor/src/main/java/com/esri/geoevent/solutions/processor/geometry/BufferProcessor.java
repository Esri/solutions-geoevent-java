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
package com.esri.geoevent.solutions.processor.geometry;

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
