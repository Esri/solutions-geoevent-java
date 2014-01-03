package com.esri.geoevent.solutions.processor.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class BufferProcessorDefinition extends GeoEventProcessorDefinitionBase {
	GeoEventDefinitionManager manager;
	private static final Log LOG = LogFactory
			.getLog(BufferProcessorDefinition.class);

	public BufferProcessorDefinition() {

	}

	public void setManager(GeoEventDefinitionManager m) {
		try {
			this.manager = m;
			List<String> unitsAllowedTypes = new ArrayList<String>();
			unitsAllowedTypes.add("Meters");
			unitsAllowedTypes.add("Kilometers");
			unitsAllowedTypes.add("Feet");
			unitsAllowedTypes.add("Miles");
			unitsAllowedTypes.add("Nautical Miles");

			PropertyDefinition procRadiusSource = new PropertyDefinition(
					"radiusSource", PropertyType.String, "Constant",
					"Radius Source", "Source of Radius Value", true, false);
			procRadiusSource.addAllowedValue("Constant");
			procRadiusSource.addAllowedValue("Event");
			propertyDefinitions.put(procRadiusSource.getPropertyName(),
					procRadiusSource);

			PropertyDefinition procRadius = new PropertyDefinition("radius",
					PropertyType.Double, 0, "Radius", "Buffer Radius", true,
					false);
			procRadius.setDependsOn("radiusSource=Constant");
			propertyDefinitions.put(procRadius.getPropertyName(), procRadius);

			PropertyDefinition procRadiusEvent = new PropertyDefinition(
					"radiusEvent", PropertyType.String, "",
					"Major Axis Event Field",
					"Geoevent field containing major axis data", true, false);
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
		return "BufferProcessor";
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
		return "Buffer Processor";
	}

	@Override
	public String getDescription() {
		return "Returns a polygon representing all points a fixed distance from the input geometry";
	}

	@Override
	public String getContactInfo() {
		return "geoeventprocessor@esri.com";
	}

}
