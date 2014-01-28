package com.esri.geoevent.solutions.processor.geometry;

/*
 * #%L
 * Esri :: AGES :: Solutions :: Processor :: Geometry
 * $Id:$
 * $HeadURL:$
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


import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class VisibilityProcessorDefinition extends GeoEventProcessorDefinitionBase {
	GeoEventDefinitionManager manager;
	private static final Log LOG = LogFactory
			.getLog(VisibilityProcessorDefinition.class);
	public VisibilityProcessorDefinition() {
		// TODO Auto-generated constructor stub
	}
	
	public void setManager(GeoEventDefinitionManager m) {
		try {
			this.manager = m;
			PropertyDefinition procGpService = new PropertyDefinition(
					"gpservice", PropertyType.String, "", "Viewshed Service",
					"Url to viewshed gp service", true, false);
			propertyDefinitions.put(procGpService.getPropertyName(),
					procGpService);

			PropertyDefinition procImageService = new PropertyDefinition(
					"imageservice", PropertyType.String, "",
					"Elevation Service", "Url to elevation service", true,
					false);
			propertyDefinitions.put(procImageService.getPropertyName(),
					procImageService);

			PropertyDefinition procObserverSource = new PropertyDefinition(
					"observerSource", PropertyType.String, "Geoevent",
					"Observer Source", "Source of observer value(s)", true,
					false);
			procObserverSource.addAllowedValue("Geoevent");
			procObserverSource.addAllowedValue("Constant");
			procObserverSource.addAllowedValue("Event Field");
			propertyDefinitions.put(procObserverSource.getPropertyName(),
					procObserverSource);

			PropertyDefinition procObserverX = new PropertyDefinition(
					"observerX", PropertyType.Double, 0.0, "Observer X",
					"X coordinate of observer location", true, false);
			procObserverX.setDependsOn("observerSource=Constant");
			propertyDefinitions.put(procObserverX.getPropertyName(),
					procObserverX);

			PropertyDefinition procObserverY = new PropertyDefinition(
					"observerY", PropertyType.Double, 0.0, "Observer Y",
					"Y coordinate of observer location", true, false);
			procObserverY.setDependsOn("observerSource=Constant");
			propertyDefinitions.put(procObserverY.getPropertyName(),
					procObserverY);

			PropertyDefinition procObserverZ = new PropertyDefinition(
					"observerZ", PropertyType.Double, 0.0, "Observer Z",
					"Z coordinate of observer location", true, false);
			procObserverZ.setDependsOn("observerSource=Constant");
			propertyDefinitions.put(procObserverZ.getPropertyName(),
					procObserverZ);

			PropertyDefinition procEventX = new PropertyDefinition(
					"observerXEvent", PropertyType.String, 0.0,
					"Observer X Event", "X coordinate of observer location",
					true, false);
			procEventX.setDependsOn("observerSource=Event Field");
			SetGeoEventAllowedFields(procEventX);
			propertyDefinitions.put(procEventX.getPropertyName(), procEventX);

			PropertyDefinition procEventY = new PropertyDefinition(
					"observerYEvent", PropertyType.String, 0.0,
					"Observer Y Event", "Y coordinate of observer location",
					true, false);
			procEventY.setDependsOn("observerSource=Event Field");
			SetGeoEventAllowedFields(procEventY);
			propertyDefinitions.put(procEventY.getPropertyName(), procEventY);

			PropertyDefinition procEventZ = new PropertyDefinition(
					"observerZEvent", PropertyType.String, 0.0,
					"Observer Z Event", "Z coordinate of observer location",
					true, false);
			procEventZ.setDependsOn("observerSource=Event Field");
			SetGeoEventAllowedFields(procEventZ);
			propertyDefinitions.put(procEventZ.getPropertyName(), procEventZ);

			PropertyDefinition procRadiusSource = new PropertyDefinition(
					"radiusSource", PropertyType.String, "", "Radius Source",
					"Source of radius value", true, false);
			procRadiusSource.addAllowedValue("Constant");
			procRadiusSource.addAllowedValue("Event");
			propertyDefinitions.put(procRadiusSource.getPropertyName(),
					procRadiusSource);

			PropertyDefinition procRadius = new PropertyDefinition("radius",
					PropertyType.Double, 1000, "Radius",
					"Maximum distance from event for analysis", true, false);
			procRadius.setDependsOn("radiusSource=Constant");
			propertyDefinitions.put(procRadius.getPropertyName(), procRadius);

			PropertyDefinition procRadiusEvent = new PropertyDefinition(
					"radiusEvent", PropertyType.String, "",
					"Radius Event Field",
					"Geoevent field containing radius data", true, false);
			procRadiusEvent.setDependsOn("radiusSource=Event");
			SetGeoEventAllowedFields(procRadiusEvent);
			propertyDefinitions.put(procRadiusEvent.getPropertyName(),
					procRadiusEvent);

			PropertyDefinition procUnits = new PropertyDefinition("units",
					PropertyType.String, 0, "Units", "Units of measurement",
					true, false);
			procUnits.addAllowedValue("Meters");
			procUnits.addAllowedValue("Kilometers");
			procUnits.addAllowedValue("Feet");
			procUnits.addAllowedValue("Miles");
			procUnits.addAllowedValue("Nautical Miles");
			propertyDefinitions.put(procUnits.getPropertyName(), procUnits);

			PropertyDefinition procElevSource = new PropertyDefinition(
					"elevationSource", PropertyType.String, "",
					"Elevation Source", "Source of elevation value", true,
					false);
			procElevSource.addAllowedValue("Constant");
			procElevSource.addAllowedValue("Event");
			propertyDefinitions.put(procElevSource.getPropertyName(),
					procElevSource);

			PropertyDefinition procElev = new PropertyDefinition("elevation",
					PropertyType.Double, 0, "Elevation",
					"Elevation above surface", true, false);
			procElev.setDependsOn("elevationSource=Constant");
			propertyDefinitions.put(procElev.getPropertyName(), procElev);

			PropertyDefinition procElevEvent = new PropertyDefinition(
					"elevationEvent", PropertyType.String, "",
					"Elevation Event Field",
					"Geoevent field containing elevation data", true, false);
			procElevEvent.setDependsOn("elevationSource=Event");
			SetGeoEventAllowedFields(procElevEvent);
			propertyDefinitions.put(procElevEvent.getPropertyName(),
					procElevEvent);

			PropertyDefinition procUnitsElev = new PropertyDefinition(
					"units_elev", PropertyType.String, 0, "Elevation Units",
					"Units of elevation", true, false);
			procUnitsElev.addAllowedValue("Meters");
			procUnitsElev.addAllowedValue("Feet");
			propertyDefinitions.put(procUnitsElev.getPropertyName(),
					procUnitsElev);

			PropertyDefinition procWKIDIn = new PropertyDefinition("wkidin",
					PropertyType.Integer, 4326, "Input WKID",
					"Coordinate system of input feature", true, false);
			propertyDefinitions.put(procWKIDIn.getPropertyName(), procWKIDIn);

			PropertyDefinition procWKIDBuffer = new PropertyDefinition(
					"wkidbuffer", PropertyType.Integer, 3857, "Processor WKID",
					"Coordinate system to calculate the buffer", true, false);
			propertyDefinitions.put(procWKIDBuffer.getPropertyName(),
					procWKIDBuffer);

			PropertyDefinition procWKIDOut = new PropertyDefinition("wkidout",
					PropertyType.Integer, 4326, "Output WKID",
					"Output Coordinate system", true, false);
			propertyDefinitions.put(procWKIDOut.getPropertyName(), procWKIDOut);
		} catch (PropertyException e) {
			LOG.error("Geometry processor");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());

		} catch (Exception e) {
			LOG.error("Geometry processor");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());

		}
	}

	private void SetGeoEventAllowedFields(PropertyDefinition pd) {
		Collection<GeoEventDefinition> geodefs = this.manager
				.listAllGeoEventDefinitions();
		Iterator<GeoEventDefinition> it = geodefs.iterator();
		GeoEventDefinition geoEventDef;
		while (it.hasNext()) {
			geoEventDef = it.next();
			String defName = geoEventDef.getName();
			for (int i = 0; i < geoEventDef.getFieldDefinitions().size(); ++i) {
				String fld = geoEventDef.getFieldDefinitions().get(i).getName();
				pd.addAllowedValue(defName + ":" + fld);
			}
		}
	}

	@Override
	public String getName() {
		return "VisibilityProcessor";
	}

	@Override
	public String getDomain() {
		return "com.esri.geoevent.solutions.processor.geometry";
	}

	@Override
	public String getVersion() {
		return "10.2.0";
	}

	@Override
	public String getLabel() {
		return "Visibility Processor";
	}

	@Override
	public String getDescription() {
		return "Returns visibility polygons";
	}

	@Override
	public String getContactInfo() {
		return "geoeventprocessor@esri.com";
	}
}
