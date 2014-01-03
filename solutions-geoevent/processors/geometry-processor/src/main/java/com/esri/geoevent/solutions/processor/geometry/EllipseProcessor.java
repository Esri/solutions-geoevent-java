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

import java.util.HashMap;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;
import com.esri.ges.spatial.GeometryException;
import com.esri.ges.spatial.Spatial;

public class EllipseProcessor extends GeoEventProcessorBase {
	Spatial spatial;
	private static HashMap<String, Integer> wkidLookup = new HashMap<String, Integer>();
	public EllipseProcessor(GeoEventProcessorDefinition definition, Spatial s)
			throws ComponentException {
		super(definition);
		wkidLookup.put("METER", 9001);
		wkidLookup.put("KILOMETER", 9036);
		wkidLookup.put("FOOT_US", 9003);
		wkidLookup.put("MILE_US", 9035);
		wkidLookup.put("NAUTICAL_MILE", 9030);
		spatial = s;
		geoEventMutator= true;
	}

	@Override
	public GeoEvent process(GeoEvent ge) throws Exception {
		double majorAxisRadius;
		String majAxisSource = properties.get("majorAxisSource").getValue().toString();
		if(majAxisSource.equals("Constant"))
		{
			majorAxisRadius = (Double) properties.get("majorAxisRadius").getValue();
		}
		else
		{
			String eventfld = properties.get("majorAxisEvent").getValue().toString();
			String[] arr = eventfld.split(":");
			majorAxisRadius = (Double)ge.getField(arr[1]);
		}
		double minorAxisRadius;
		String minAxisSource = properties.get("minorAxisSource").getValue().toString();
		if(minAxisSource.equals("Constant"))
		{
			minorAxisRadius = (Double) properties.get("minorAxisRadius").getValue();
		}
		else
		{
			String eventfld = properties.get("minorAxisEvent").getValue().toString();
			String[] arr = eventfld.split(":");
			minorAxisRadius = (Double)ge.getField(arr[1]);
		}
		double rotation;
		String rotSource = properties.get("rotationSource").getValue().toString();
		if(rotSource.equals("Constant"))
		{
			rotation = (Double) properties.get("rotation").getValue();
		}
		else
		{
			String eventfld = properties.get("rotationEvent").getValue().toString();
			String[] arr = eventfld.split(":");
			rotation = (Double)ge.getField(arr[1]);
		}
		
		int inwkid = (Integer) properties.get("wkidin").getValue();
		int outwkid = (Integer) properties.get("wkidout").getValue();
		int bufferwkid = (Integer) properties.get("wkidbuffer").getValue();
		
		com.esri.ges.spatial.Point eventGeo = (com.esri.ges.spatial.Point) ge.getGeometry();
		double x = eventGeo.getX();
		double y = eventGeo.getY();
		double rdeg = GeometryUtility.Geo2Arithmetic(rotation);
		double r = Math.toRadians(rdeg);
		com.esri.ges.spatial.Geometry ellipse = constructEllipse(x, y, majorAxisRadius, minorAxisRadius, r, inwkid, bufferwkid, outwkid);
		ge.setGeometry(ellipse);
		return ge;
	}
	
	private com.esri.ges.spatial.Geometry constructEllipse(double x, double y, double majorAxis, double minorAxis, double rotation, int wkidin, int wkidbuffer, int wkidout) throws GeometryException
	{
		Point center = new Point();
		center.setX(x);
		center.setY(y);
		SpatialReference srIn = SpatialReference.create(wkidin);
		SpatialReference srBuffer = SpatialReference.create(wkidbuffer);
		SpatialReference srOut = SpatialReference.create(wkidout);
		Point centerProj = (Point) GeometryEngine.project(center, srIn, srBuffer);
		GeometryUtility geoutil = new GeometryUtility();
		Polygon ellipse = geoutil.GenerateEllipse(centerProj, majorAxis, minorAxis, rotation);
		Geometry ellipseOut = GeometryEngine.project(ellipse, srBuffer, srOut);
		String json = GeometryEngine.geometryToJson(srOut, ellipseOut);
		return spatial.fromJson(json);
	}

}
