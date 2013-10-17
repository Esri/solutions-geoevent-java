package com.esri.ges.solutions.processor.geometry;

import java.util.ArrayList;
import java.util.List;

import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class BufferProcessorDefinition extends GeoEventProcessorDefinitionBase {

	public BufferProcessorDefinition() throws PropertyException {
		List<String> unitsAllowedTypes = new ArrayList<String>();
		unitsAllowedTypes.add("Meters");
		unitsAllowedTypes.add("Kilometers");
		unitsAllowedTypes.add("Feet");
		unitsAllowedTypes.add("Miles");
		unitsAllowedTypes.add("Nautical Miles");
		
		PropertyDefinition procRadius = new PropertyDefinition("radius", PropertyType.Double, 0, "Radius", "Buffer Radius", true, false);
		propertyDefinitions.put(procRadius.getPropertyName(), procRadius);
		
		PropertyDefinition procUnits = new PropertyDefinition("units", PropertyType.String, 0, "Units", "Units of measurement", true, false);
		procUnits.addAllowedValue("Meters");
		procUnits.addAllowedValue("Kilometers");
		procUnits.addAllowedValue("Feet");
		procUnits.addAllowedValue("Miles");
		procUnits.addAllowedValue("Nautical Miles");
		propertyDefinitions.put(procUnits.getPropertyName(), procUnits);
		
		PropertyDefinition procWKIDIn = new PropertyDefinition("wkidin", PropertyType.Integer, 4326, "Input WKID", "Coordinate system of input feature", true, false);
		propertyDefinitions.put(procWKIDIn.getPropertyName(), procWKIDIn);
		
		PropertyDefinition procWKIDBuffer = new PropertyDefinition("wkidbuffer", PropertyType.Integer, 3857, "Processor WKID", "Coordinate system to calculate the buffer", true, false);
		propertyDefinitions.put(procWKIDBuffer.getPropertyName(), procWKIDBuffer);
		
		PropertyDefinition procWKIDOut = new PropertyDefinition("wkidout", PropertyType.Integer, 4326, "Output WKID", "Output Coordinate system", true, false);
		propertyDefinitions.put(procWKIDOut.getPropertyName(), procWKIDOut);
	}
	
	@Override
	public String getName() {
		return "BufferProcessor";
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
