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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.ges.core.ConfigurationException;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;
import com.esri.ges.spatial.GeometryException;
import com.esri.ges.spatial.Spatial;
import com.esri.core.geometry.Geometry;
import com.esri.core.map.Graphic;
import com.esri.core.tasks.ags.geoprocessing.GPDouble;
import com.esri.core.tasks.ags.geoprocessing.GPFeatureRecordSetLayer;
import com.esri.core.tasks.ags.geoprocessing.GPLong;
import com.esri.core.tasks.ags.geoprocessing.GPParameter;
import com.esri.core.tasks.ags.geoprocessing.GPString;
import com.esri.core.tasks.ags.geoprocessing.Geoprocessor;

public class RangeFanProcessor extends GeoEventProcessorBase {
	private static final Log LOG = LogFactory.getLog(RangeFanProcessor.class);
	public Spatial spatial;
	public GeoEventDefinitionManager manager;

	private SpatialReference srIn;
	private SpatialReference srBuffer;
	private SpatialReference srOut;

	public RangeFanProcessor(GeoEventProcessorDefinition definition, Spatial s, GeoEventDefinitionManager m)
			throws ComponentException {
		super(definition);
		spatial = s;
		manager = m;

		geoEventMutator= true;
	}
	
	@Override
	public GeoEvent process(GeoEvent ge) throws Exception {
		
    try 
    {
		double range;
		String rangeSource = properties.get("rangeSource").getValue().toString();
		if(rangeSource.equals("Constant"))
		{
			range = (Double) properties.get("range").getValue();
		}
		else
		{			
			String eventfld = properties.get("rangeEvent").getValue().toString();
			String[] arr = eventfld.split(":");
			range = (Double)ge.getField(arr[1]);				
		}
		String unit = properties.get("units").getValue().toString();
		
		double bearing;
		String bearingSource = properties.get("bearingSource").getValue().toString();
		if(bearingSource.equals("Constant"))
		{
			bearing = (Double) properties.get("bearing").getValue();
		}
		else
		{
			String eventfld = properties.get("bearingEvent").getValue().toString();
			String[] arr = eventfld.split(":");
			bearing = (Double)ge.getField(arr[1]);
		}
		
		double traversal;
		String traversalSource = properties.get("traversalSource").getValue().toString();
		if(traversalSource.equals("Constant"))
		{
			traversal = (Double) properties.get("traversal").getValue();
		}
		else
		{
			String eventfld = properties.get("traversalEvent").getValue().toString();
			String[] arr = eventfld.split(":");
			traversal = (Double)ge.getField(arr[1]);
		}
		
		
		int inwkid = (Integer) properties.get("wkidin").getValue();
		int outwkid = (Integer) properties.get("wkidout").getValue();
		int bufferwkid = (Integer) properties.get("wkidbuffer").getValue();
		srIn = SpatialReference.create(inwkid);
		srBuffer = SpatialReference.create(bufferwkid);
		srOut = SpatialReference.create(outwkid);
		
		com.esri.ges.spatial.Point eventGeo = (com.esri.ges.spatial.Point) ge.getGeometry();
		double x = eventGeo.getX();
		double y = eventGeo.getY();
		Geometry fan = constructRangeFan(x, y, range, unit, bearing, traversal);
		
		if((Boolean)properties.get("showVisibility").getValue())
		{
			String gp = properties.get("gpservice").getValue().toString();
			String is = properties.get("imageservice").getValue().toString();
			double elevation = (Double)properties.get("elevation").getValue();
			String units_elev = properties.get("units_elev").getValue().toString();
			constructVisibility(ge, gp, is, range, unit,  elevation, units_elev, fan, bufferwkid);
		}
		Geometry fanout = GeometryEngine.project(fan, srBuffer, srOut);
		String json = GeometryEngine.geometryToJson(srOut, fanout);
		com.esri.ges.spatial.Geometry outfan = spatial.fromJson(json);
		ge.setGeometry(outfan);
		
	}
	catch (Exception e) 
	{
	  LOG.warn("Parse exception in process:" + e.getMessage());
	  LOG.debug(e.getMessage(), e);
	}		
		return ge;
	}
	private void constructVisibility(GeoEvent ge, String gpservice, String imageservice, double range, String unit,  double elevation, String units_elev, Geometry mask, int wkid) throws FieldException, ConfigurationException, GeometryException
	{
		try {
			UnitConverter uc = new UnitConverter();
			range = uc.Convert(range, unit, srBuffer);
			String procUnitName = srBuffer.getUnit().getName();
			// normalize horizontal and vertical units to z-factor = 1
			//double inElev = elevation;
			if (procUnitName.equals("Meter")) {
				if (units_elev.equals("Feet")) {
					elevation = elevation * 0.3048;
				}
			} else {
				if (units_elev.equals("Meters")) {
					elevation = elevation * 3.28084;
				}
			}

			Geoprocessor gp = new Geoprocessor(gpservice);
			List<GPParameter> parameters = new ArrayList<GPParameter>();
			com.esri.ges.spatial.Point eventGeo = (com.esri.ges.spatial.Point) ge
					.getGeometry();

			// Set params
			Double x = (Double) eventGeo.getX();
			Double y = (Double) eventGeo.getY();
			Point center = new Point();
			center.setX(x);
			center.setY(y);
			Point centerProj = (Point) GeometryEngine.project(center, srIn,
					srBuffer);
			String cx = ((Double)centerProj.getX()).toString();
			String cy = ((Double)centerProj.getY()).toString();
			String obs = cx + " " + cy;
			GPString paramObserver = new GPString();
			paramObserver.setParamName("observers");
			paramObserver.setValue(obs);
			parameters.add(paramObserver);

			GPString paramIS = new GPString();
			paramIS.setParamName("image_service_url");
			paramIS.setValue(imageservice);
			parameters.add(paramIS);

			GPDouble paramRadius = new GPDouble();
			paramRadius.setParamName("radius");
			paramRadius.setValue(range);
			parameters.add(paramRadius);

			GPDouble paramHeight = new GPDouble();
			paramHeight.setParamName("height");
			paramHeight.setValue(elevation);
			parameters.add(paramHeight);

			String json = GeometryEngine.geometryToJson(srBuffer, mask);
			GPString paramMask = new GPString();
			paramMask.setParamName("json_mask");
			paramMask.setValue(json);
			parameters.add(paramMask);

			GPLong paramWkid = new GPLong();
			paramWkid.setParamName("wkid");
			paramWkid.setValue(wkid);
			parameters.add(paramWkid);
			com.esri.ges.spatial.Geometry visible = null;
			com.esri.ges.spatial.Geometry nonvisible = null;
			GPParameter[] result = null;
			try
			{
				result = gp.execute(parameters);
			}
			catch(Exception ex)
			{
				LOG.error(ex.getMessage());
				LOG.error(ex.getStackTrace());
				gp.cancelJob();
				
			}
			if (result != null) {
				for (GPParameter outputParameter : result) {
					if (outputParameter instanceof GPFeatureRecordSetLayer) {
						GPFeatureRecordSetLayer gpResults = (GPFeatureRecordSetLayer) outputParameter;
						for (Graphic graphic : gpResults.getGraphics()) {

							int code = (Integer) graphic.getAttributes().get(
									"gridcode");
							// com.esri.ges.spatial.Geometry tmpgesVis = null;
							// com.esri.ges.spatial.Geometry tmpgesNonVis =
							// null;
							if (code == 0) {
								Geometry tmpvis = graphic.getGeometry();
								Geometry vis = GeometryEngine.project(tmpvis,
										srBuffer, srOut);
								json = GeometryEngine
										.geometryToJson(srOut, vis);

								visible = spatial.fromJson(json);

							} else {
								Geometry tmpnonvis = graphic.getGeometry();
								Geometry nonvis = GeometryEngine.project(
										tmpnonvis, srBuffer, srOut);
								json = GeometryEngine.geometryToJson(srOut,
										nonvis);

								nonvisible = spatial.fromJson(json);

							}

						}
					}
				}
			}
			else
			{
				NullPointerException e = new NullPointerException();
				throw e;
			}
			/*ArrayList<FieldDefinition> augmentList = new ArrayList<FieldDefinition>();
			ArrayList<String> tagTypes = new ArrayList<String>();
			tagTypes.add("Geometry");
			Tag visTag = tagMgr.createNewTag("VISIBLE", "Visible polygons returned from visibility analysis", tagTypes);
			tagMgr.addTag(visTag);
			Tag nonvisTag = tagMgr.createNewTag("NONVISIBLE", "Visible polygons returned from visibility analysis", tagTypes);
			tagMgr.addTag(nonvisTag);
			addToAugmentList(augmentList, "elevation", FieldType.Double,
					(String) null);
			addToAugmentList(augmentList, "elev_units", FieldType.String,
					(String) null);
			addToAugmentList(augmentList, "gridcode", FieldType.Short,
					(String) null);
			addToAugmentList(augmentList, "visible", FieldType.Geometry,
					visTag.getName());
			addToAugmentList(augmentList, "nonvisible", FieldType.Geometry,
					nonvisTag.getName());

			GeoEventDefinition geDef = ge.getGeoEventDefinition();
			geDef.augment(augmentList);*/

			ge.setField("visible", visible);
			ge.setField("nonvisible", nonvisible);
			//ge.setField("elevation", inElev);
			//ge.setField("elevUnits", units_elev);

			
		} catch (Exception e) {
			
		}
	}

	private Geometry constructRangeFan(double x, double y, double range,
			String unit, double bearing, double traversal)
			throws GeometryException {
		Polygon fan = new Polygon();
		Point center = new Point();
		center.setX(x);
		center.setY(y);
		// SpatialReference srIn = SpatialReference.create(wkidin);
		// SpatialReference srBuffer = SpatialReference.create(wkidbuffer);
		// SpatialReference srOut = SpatialReference.create(wkidout);
		Point centerProj = (Point) GeometryEngine.project(center, srIn,
				srBuffer);

		double centerX = centerProj.getX();
		double centerY = centerProj.getY();
		bearing = GeometryUtility.Geo2Arithmetic(bearing);
		double leftAngle = bearing - (traversal / 2);
		double rightAngle = bearing + (traversal / 2);
		int count = (int) Math.round(Math.abs(leftAngle - rightAngle));
		fan.startPath(centerProj);
		UnitConverter uc = new UnitConverter();
		range = uc.Convert(range, unit, srBuffer);
		for (int i = 0; i < count; ++i) {
			double d = Math.toRadians(leftAngle + i);
			double arcX = centerX + (range * Math.cos(d));
			double arcY = centerY + (range * Math.sin(d));
			Point arcPt = new Point(arcX, arcY);
			// arcPt = (Point) GeometryEngine.project(arcPt, srBuffer, srOut);
			fan.lineTo(arcPt);
		}
		fan.closeAllPaths();
		return fan;
	}
}
