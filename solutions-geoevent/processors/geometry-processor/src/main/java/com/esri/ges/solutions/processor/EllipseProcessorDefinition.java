package com.esri.ges.solutions.processor;

import java.util.Collection;
import java.util.Iterator;

import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class EllipseProcessorDefinition extends GeoEventProcessorDefinitionBase {
	GeoEventDefinitionManager manager;
	public EllipseProcessorDefinition() throws PropertyException {
		
	}

	public void setManager(GeoEventDefinitionManager m) throws PropertyException {
		this.manager = m;
		
		PropertyDefinition procMajAxisSource = new PropertyDefinition("majorAxisSource", PropertyType.String, "", "Major Axis Source", "Source of Major Axis Value", true, false);
		procMajAxisSource.addAllowedValue("Constant");
		procMajAxisSource.addAllowedValue("Event");
		propertyDefinitions.put(procMajAxisSource.getPropertyName(), procMajAxisSource);
		
		PropertyDefinition procMajorAxisRadius = new PropertyDefinition("majorAxisRadius", PropertyType.Double, 1000, "Major Axis Radius", "Length of ellipse's major axis", true, false);
		procMajorAxisRadius.setDependsOn("majorAxisSource=Constant");
		propertyDefinitions.put(procMajorAxisRadius.getPropertyName(), procMajorAxisRadius);
		
		PropertyDefinition procMajAxisEvent = new PropertyDefinition("majorAxisEvent", PropertyType.String, "", "Major Axis Event Field", "Geoevent field containing major axis data", true, false);
		procMajAxisEvent.setDependsOn("majorAxisSource=Event");
		SetGeoEventAllowedFields(procMajAxisEvent);
		propertyDefinitions.put(procMajAxisEvent.getPropertyName(), procMajAxisEvent);
		
		PropertyDefinition procMinAxisSource = new PropertyDefinition("minorAxisSource", PropertyType.String, "", "Minor Axis Source", "Source of Minor Axis Value", true, false);
		procMinAxisSource.addAllowedValue("Constant");
		procMinAxisSource.addAllowedValue("Event");
		propertyDefinitions.put(procMinAxisSource.getPropertyName(), procMinAxisSource);
		
		PropertyDefinition procMinorAxisRadius = new PropertyDefinition("minorAxisRadius", PropertyType.Double, 0, "Minor Axis Radius", "Length of ellipse's minor axis radius", true, false);
		procMinorAxisRadius.setDependsOn("minorAxisSource=Constant");
		propertyDefinitions.put(procMinorAxisRadius.getPropertyName(), procMinorAxisRadius);
		
		PropertyDefinition procMinAxisEvent = new PropertyDefinition("minorAxisEvent", PropertyType.String, "", "Minor Axis Event Field", "Geoevent field containing minor axis data", true, false);
		procMinAxisEvent.setDependsOn("minorAxisSource=Event");
		SetGeoEventAllowedFields(procMinAxisEvent);
		propertyDefinitions.put(procMinAxisEvent.getPropertyName(), procMinAxisEvent);
		
		PropertyDefinition procUnits = new PropertyDefinition("units", PropertyType.String, 0, "Units", "Units of measurement", true, false);
		procUnits.addAllowedValue("Meters");
		procUnits.addAllowedValue("Kilometers");
		procUnits.addAllowedValue("Feet");
		procUnits.addAllowedValue("Miles");
		procUnits.addAllowedValue("Nautical Miles");
		propertyDefinitions.put(procUnits.getPropertyName(), procUnits);
		
		PropertyDefinition procRotationSource = new PropertyDefinition("rotationSource", PropertyType.String, "", "Rotation Source", "Source of Rotation Value", true, false);
		procRotationSource.addAllowedValue("Constant");
		procRotationSource.addAllowedValue("Event");
		propertyDefinitions.put(procRotationSource.getPropertyName(), procRotationSource);
		
		PropertyDefinition procRotationAngle = new PropertyDefinition("rotation", PropertyType.Double, 0, "rotation", "Angle of rotation (geographic)", true, false);
		procRotationAngle.setDependsOn("rotationSource=Constant");
		propertyDefinitions.put(procRotationAngle.getPropertyName(), procRotationAngle);
		
		PropertyDefinition procRotationEvent = new PropertyDefinition("rotationEvent", PropertyType.String, "", "Rotation Event Field", "Geoevent field containing rotation data", true, false);
		procRotationEvent.setDependsOn("rotationSource=Event");
		SetGeoEventAllowedFields(procRotationEvent);
		propertyDefinitions.put(procRotationEvent.getPropertyName(), procRotationEvent);
		
		PropertyDefinition procWKIDIn = new PropertyDefinition("wkidin", PropertyType.Integer, 4326, "Input WKID", "Coordinate system of input feature", true, false);
		propertyDefinitions.put(procWKIDIn.getPropertyName(), procWKIDIn);
		
		PropertyDefinition procWKIDBuffer = new PropertyDefinition("wkidbuffer", PropertyType.Integer, 3857, "Processor WKID", "Coordinate system to calculate the buffer", true, false);
		propertyDefinitions.put(procWKIDBuffer.getPropertyName(), procWKIDBuffer);
		
		PropertyDefinition procWKIDOut = new PropertyDefinition("wkidout", PropertyType.Integer, 4326, "Output WKID", "Output Coordinate system", true, false);
		propertyDefinitions.put(procWKIDOut.getPropertyName(), procWKIDOut);
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
		return "EllipseProcessor";
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
		return "Ellipse Processor";
	}

	@Override
	public String getDescription() {
		return "Returns ellipse derived from event center, major axis radial length, minor axis radial length, and rotation angle";
	}

	@Override
	public String getContactInfo() {
		return "geoeventprocessor@esri.com";
  }
}

