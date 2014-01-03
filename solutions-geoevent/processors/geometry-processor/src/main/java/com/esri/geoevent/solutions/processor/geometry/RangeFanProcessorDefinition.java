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

import java.util.Collection;
import java.util.Iterator;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class RangeFanProcessorDefinition extends GeoEventProcessorDefinitionBase {
	GeoEventDefinitionManager manager;
	public RangeFanProcessorDefinition() {
			
	}
	
	public void setManager(GeoEventDefinitionManager m) throws PropertyException {
		this.manager = m;
		
		PropertyDefinition procRangeSource = new PropertyDefinition("rangeSource", PropertyType.String, "", "Range Source", "Source of range Value", true, false);
		procRangeSource.addAllowedValue("Constant");
		procRangeSource.addAllowedValue("Event");
		propertyDefinitions.put(procRangeSource.getPropertyName(), procRangeSource);
		
		PropertyDefinition procRange = new PropertyDefinition("range", PropertyType.Double, 1000, "Range", "Maximum distance from event for analysis", true, false);
		procRange.setDependsOn("rangeSource=Constant");
		propertyDefinitions.put(procRange.getPropertyName(), procRange);
		
		PropertyDefinition procRangeEvent = new PropertyDefinition("rangeEvent", PropertyType.String, "", "Range Event Field", "Geoevent field containing range data", true, false);
		procRangeEvent.setDependsOn("rangeSource=Event");
		SetGeoEventAllowedFields(procRangeEvent);
		propertyDefinitions.put(procRangeEvent.getPropertyName(), procRangeEvent);
		
		PropertyDefinition procUnits = new PropertyDefinition("units", PropertyType.String, 0, "Units", "Units of measurement", true, false);
		procUnits.addAllowedValue("Meters");
		procUnits.addAllowedValue("Kilometers");
		procUnits.addAllowedValue("Feet");
		procUnits.addAllowedValue("Miles");
		procUnits.addAllowedValue("Nautical Miles");
		propertyDefinitions.put(procUnits.getPropertyName(), procUnits);
	
		PropertyDefinition procBearingSource = new PropertyDefinition("bearingSource", PropertyType.String, "", "Bearing Source", "Source of Bearing Value", true, false);
		procBearingSource.addAllowedValue("Constant");
		procBearingSource.addAllowedValue("Event");
		propertyDefinitions.put(procBearingSource.getPropertyName(), procBearingSource);
		
		PropertyDefinition procBearingC = new PropertyDefinition("bearing", PropertyType.Double, 0, "Bearing", "Bearing Angle", true, false);
		procBearingC.setDependsOn("bearingSource=Constant");
		propertyDefinitions.put(procBearingC.getPropertyName(), procBearingC);
		
		PropertyDefinition procBearingEvent = new PropertyDefinition("bearingEvent", PropertyType.String, "", "Bearing Event Field", "Geoevent field containing bearing data", true, false);
		procBearingEvent.setDependsOn("bearingSource=Event");
		SetGeoEventAllowedFields(procBearingEvent);
		propertyDefinitions.put(procBearingEvent.getPropertyName(), procBearingEvent);
		
		PropertyDefinition procTraversalSource = new PropertyDefinition("traversalSource", PropertyType.String, "", "Traversal Source", "Source of Traversal Value", true, false);
		procTraversalSource.addAllowedValue("Constant");
		procTraversalSource.addAllowedValue("Event");
		propertyDefinitions.put(procTraversalSource.getPropertyName(), procTraversalSource);
		
		PropertyDefinition procTraversal = new PropertyDefinition("traversal", PropertyType.Double, 0, "Traversal", "Traversal Angle - (0 - 359 degrees)", true, false);
		procTraversal.setDependsOn("traversalSource=Constant");
		propertyDefinitions.put(procTraversal.getPropertyName(), procTraversal);
		
		PropertyDefinition procTraversalEvent = new PropertyDefinition("traversalEvent", PropertyType.String, "", "Traversal Event Field", "Geoevent field containing traversal angle data", true, false);
		procTraversalEvent.setDependsOn("traversalSource=Event");
		SetGeoEventAllowedFields(procTraversalEvent);
		propertyDefinitions.put(procTraversalEvent.getPropertyName(), procTraversalEvent);
		
		PropertyDefinition procWKIDIn = new PropertyDefinition("wkidin", PropertyType.Integer, 4326, "Input WKID", "Coordinate system of input feature", true, false);
		propertyDefinitions.put(procWKIDIn.getPropertyName(), procWKIDIn);
		
		PropertyDefinition procWKIDBuffer = new PropertyDefinition("wkidbuffer", PropertyType.Integer, 3857, "Processor WKID", "Coordinate system to calculate the buffer", true, false);
		propertyDefinitions.put(procWKIDBuffer.getPropertyName(), procWKIDBuffer);
		
		PropertyDefinition procWKIDOut = new PropertyDefinition("wkidout", PropertyType.Integer, 4326, "Output WKID", "Output Coordinate system", true, false);
		propertyDefinitions.put(procWKIDOut.getPropertyName(), procWKIDOut);
		
		PropertyDefinition procShowVis = new PropertyDefinition("showVisibility", PropertyType.Boolean, false, "Process Visibility", "Process visibility polygons", true, false);
		propertyDefinitions.put(procShowVis.getPropertyName(), procShowVis);
		
		PropertyDefinition procGpService = new PropertyDefinition("gpservice", PropertyType.String, 0, "Viewshed Service", "Url to viewshed gp service", true, false);
		procGpService.setDependsOn("showVisibility=true");
		propertyDefinitions.put(procGpService.getPropertyName(), procGpService);
		
		PropertyDefinition procImageService = new PropertyDefinition("imageservice", PropertyType.String, 0, "Elevation Service", "Url to elevation service", true, false);
		procImageService.setDependsOn("showVisibility=true");
		propertyDefinitions.put(procImageService.getPropertyName(), procImageService);
		
		PropertyDefinition procElev = new PropertyDefinition("elevation", PropertyType.Double, 0, "Elevation", "Elevation above surface", true, false);
		procElev.setDependsOn("showVisibility=true");
		propertyDefinitions.put(procElev.getPropertyName(), procElev);
		
		PropertyDefinition procUnitsElev = new PropertyDefinition("units_elev", PropertyType.String, 0, "Elevation Units", "Units of elevation", true, false);
		procUnitsElev.addAllowedValue("Meters");
		procUnitsElev.addAllowedValue("Feet");
		procUnitsElev.setDependsOn("showVisibility=true");
		propertyDefinitions.put(procUnitsElev.getPropertyName(), procUnitsElev);	
		
		
	}
	
	private void SetGeoEventAllowedFields(PropertyDefinition pd)
	{
		Collection<GeoEventDefinition> geodefs = this.manager.listAllGeoEventDefinitions();
		Iterator<GeoEventDefinition> it = geodefs.iterator();
		GeoEventDefinition geoEventDef;
		while (it.hasNext())
		{
			geoEventDef = it.next();
			String defName = geoEventDef.getName();
			for(int i = 0; i < geoEventDef.getFieldDefinitions().size(); ++i)
			{
				String fld = geoEventDef.getFieldDefinitions().get(i).getName();
				pd.addAllowedValue(defName + ":" + fld);
			}
		}
	}
	
	@Override
	public String getName() {
		return "RangeFanProcessor";
	}

	@Override
	public String getDomain() {
		return "com.esri.ges.processor";
	}

	@Override
	public String getVersion() {
		return "10.2.0";
	}

	@Override
	public String getLabel() {
		return "Range Fan Processor";
	}

	@Override
	public String getDescription() {
		return "Returns range fan derived from event center, range, bearing, and traversal angle";
	}

	@Override
	public String getContactInfo() {
		return "geoeventprocessor@esri.com";
  }
}
