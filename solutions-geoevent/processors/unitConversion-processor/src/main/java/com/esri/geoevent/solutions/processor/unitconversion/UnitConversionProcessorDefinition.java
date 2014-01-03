package com.esri.geoevent.solutions.processor.unitconversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.geoevent.Tag;
import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.manager.tag.TagManager;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class UnitConversionProcessorDefinition extends GeoEventProcessorDefinitionBase {
	private GeoEventDefinitionManager manager;
	private TagManager tagManager;
	public UnitConversionProcessorDefinition()
	{
		;
	}
	
	public void setManager(GeoEventDefinitionManager m, TagManager tm) {
		tagManager = tm;
		manager = m;
		try {
			
			PropertyDefinition pdInputType = new PropertyDefinition("input", PropertyType.String, "TAG", "Input Type", "Input Type", true, false);
			pdInputType.addAllowedValue("TAG");
			pdInputType.addAllowedValue("MANUAL");
			propertyDefinitions.put(pdInputType.getPropertyName(), pdInputType);
			
			PropertyDefinition pdVDef = new PropertyDefinition("velocity-tag",
					PropertyType.String, null, "Velocity Tag",
					"Tag defining velocity values", false, false);
			pdVDef.setDependsOn("input=TAG");
			SetTagAllowedFields(pdVDef);
			propertyDefinitions.put(pdVDef.getPropertyName(), pdVDef);
			
			PropertyDefinition pdVMan = new PropertyDefinition("velocity-manual",
					PropertyType.String, null, "Velocity field",
					"Field defining velocity values", false, false);
			pdVMan.setDependsOn("input=MANUAL");
			propertyDefinitions.put(pdVMan.getPropertyName(), pdVMan);
			
			PropertyDefinition pdVIn = new PropertyDefinition("vin", PropertyType.String, "Miles/Hour", "Default Velocity Input Unit", "Default Velocity Input Unit", true, false);
			pdVIn.addAllowedValue("Miles/Hour");
			pdVIn.addAllowedValue("Meters/Hour");
			pdVIn.addAllowedValue("Kilometers/Hour");
			pdVIn.addAllowedValue("Hectometers/Hour");
			pdVIn.addAllowedValue("Knots");
			pdVIn.addAllowedValue("Feet/Hour");
			pdVIn.addAllowedValue("Miles/Minute");
			pdVIn.addAllowedValue("Meters/Minute");
			pdVIn.addAllowedValue("Kilometers/Minute");
			pdVIn.addAllowedValue("Hectometers/Minute");
			pdVIn.addAllowedValue("Nautical Miles/Minute");
			pdVIn.addAllowedValue("Feet/Minute");
			pdVIn.addAllowedValue("Miles/Second");
			pdVIn.addAllowedValue("Meters/Second");
			pdVIn.addAllowedValue("Kilometers/Second");
			pdVIn.addAllowedValue("Hectometers/Second");
			pdVIn.addAllowedValue("Nautical Miles/Second");
			pdVIn.addAllowedValue("Feet/Second");
			propertyDefinitions.put(pdVIn.getPropertyName(), pdVIn);
			
			PropertyDefinition pdVOut = new PropertyDefinition("vout", PropertyType.String, "Miles/Hour", "Velocity Output Unit", "Velocity Output Unit", true, false);
			pdVOut.addAllowedValue("Miles/Hour");
			pdVOut.addAllowedValue("Meters/Hour");
			pdVOut.addAllowedValue("Kilometers/Hour");
			pdVOut.addAllowedValue("Hectometers/Hour");
			pdVOut.addAllowedValue("Knots");
			pdVOut.addAllowedValue("Feet/Hour");
			pdVOut.addAllowedValue("Miles/Minute");
			pdVOut.addAllowedValue("Meters/Minute");
			pdVOut.addAllowedValue("Kilometers/Minute");
			pdVOut.addAllowedValue("Hectometers/Minute");
			pdVOut.addAllowedValue("Nautical Miles/Minute");
			pdVOut.addAllowedValue("Feet/Minute");
			pdVOut.addAllowedValue("Miles/Second");
			pdVOut.addAllowedValue("Meters/Second");
			pdVOut.addAllowedValue("Kilometers/Second");
			pdVOut.addAllowedValue("Hectometers/Second");
			pdVOut.addAllowedValue("Nautical Miles/Second");
			pdVOut.addAllowedValue("Feet/Second");
			propertyDefinitions.put(pdVOut.getPropertyName(), pdVOut);

			PropertyDefinition pdAltDef = new PropertyDefinition(
					"altitude-tag", PropertyType.String, null,
					"Altitude Tag",
					"Tag describing the altitude value", false, false);
			pdAltDef.setDependsOn("input=TAG");
			SetTagAllowedFields(pdAltDef);
			propertyDefinitions.put(pdAltDef.getPropertyName(), pdAltDef);
			
			PropertyDefinition pdAMan = new PropertyDefinition("alt-manual",
					PropertyType.String, null, "Altitude field",
					"Field defining altitude values", false, false);
			pdAMan.setDependsOn("input=MANUAL");
			propertyDefinitions.put(pdAMan.getPropertyName(), pdAMan);
			
			PropertyDefinition pdAltIn = new PropertyDefinition("altin", PropertyType.String, "Meters", "Default Altitude Input Unit", "Default Altitude Input Unit", true, false);
			pdAltIn.addAllowedValue("Miles");
			pdAltIn.addAllowedValue("Feet");
			pdAltIn.addAllowedValue("Meters");
			pdAltIn.addAllowedValue("Kilometers");
			pdAltIn.addAllowedValue("Hectometers");
			pdAltIn.addAllowedValue("Nautical Miles");
			propertyDefinitions.put(pdAltIn.getPropertyName(), pdAltIn);
			
			PropertyDefinition pdAltOut = new PropertyDefinition("altout", PropertyType.String, "Meters", "Altitude Output Unit", "Altitude Output Unit", true, false);
			pdAltOut.addAllowedValue("Miles");
			pdAltOut.addAllowedValue("Feet");
			pdAltOut.addAllowedValue("Meters");
			pdAltOut.addAllowedValue("Kilometers");
			pdAltOut.addAllowedValue("Hectometers");
			pdAltOut.addAllowedValue("Nautical Miles");
			propertyDefinitions.put(pdAltOut.getPropertyName(), pdAltOut);
			
			PropertyDefinition pdFreqDef = new PropertyDefinition(
					"frequency-tag", PropertyType.String, null,
					"Frequency Tag",
					"Tag describing frequency value", false, false);
			SetTagAllowedFields(pdFreqDef);
			pdFreqDef.setDependsOn("input=TAG");
			propertyDefinitions.put(pdFreqDef.getPropertyName(), pdFreqDef);
			
			PropertyDefinition pdFMan = new PropertyDefinition("freq-manual",
					PropertyType.String, null, "Frequency field",
					"Field defining frequency values", false, false);
			pdFMan.setDependsOn("input=MANUAL");
			propertyDefinitions.put(pdFMan.getPropertyName(), pdFMan);
			
			PropertyDefinition pdFIn = new PropertyDefinition("freqin", PropertyType.String, "Hz", "Default Frequency Input Unit", "Default Frequency Input Unit", true, false);
			pdFIn.addAllowedValue("Hz");
			pdFIn.addAllowedValue("KHz");
			pdFIn.addAllowedValue("MHz");
			pdFIn.addAllowedValue("GHz");
			propertyDefinitions.put(pdFIn.getPropertyName(), pdFIn);
			
			PropertyDefinition pdFOut = new PropertyDefinition("freqout", PropertyType.String, "Hz", "Frequency Output Unit", "Frequency Output Unit", true, false);
			pdFOut.addAllowedValue("Hz");
			pdFOut.addAllowedValue("KHz");
			pdFOut.addAllowedValue("MHz");
			pdFOut.addAllowedValue("GHz");
			propertyDefinitions.put(pdFOut.getPropertyName(), pdFOut);
			
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void SetGeoEventAllowedFields(PropertyDefinition pd)
	{
		Collection<GeoEventDefinition> geodefs = this.manager.listAllGeoEventDefinitions();
		ArrayList<String> names = new ArrayList<String>();
		HashMap<String, GeoEventDefinition> defMap = new HashMap<String, GeoEventDefinition>();
		Iterator<GeoEventDefinition> it = geodefs.iterator();
		GeoEventDefinition geoEventDef;
		while (it.hasNext())
		{
			geoEventDef = it.next();
			String defName = geoEventDef.getName();
			names.add(defName);
			defMap.put(defName, geoEventDef);
		}
		Collections.sort(names);
		for(String name: names)
		{
			geoEventDef = defMap.get(name);
			List<FieldDefinition> fldDefs = geoEventDef.getFieldDefinitions();
			for(FieldDefinition f: fldDefs)
			{
				pd.addAllowedValue(name + ":" + f.getName());
			}
		}
	}
	
	private void SetTagAllowedFields(PropertyDefinition pd)
	{
		Collection<?extends Tag> allTags = this.tagManager.getAllTag();
		ArrayList<String> names = new ArrayList<String>();
		//HashMap<String, GeoEventDefinition> tagMap = new HashMap<String, GeoEventDefinition>();
		Iterator<?extends Tag> it = allTags.iterator();
		Tag tag;
		while(it.hasNext())
		{
			tag = it.next();
			String tagName = tag.getName();
			names.add(tagName);
		}
		Collections.sort(names);
		for(String name: names)
		{
			pd.addAllowedValue(name);
		}
	}
	
	@Override
	public String getName()
	{
		return "UnitConversionProcessor";
	}

	@Override
	public String getDomain()
	{
		return "com.esri.geoevent.solutions.processor.unitconversion";
	}

	@Override
	public String getVersion()
	{
		return "10.2.0";
	}

	@Override
	public String getLabel()
	{
		return "Unit Conversion Processor";
	}

	@Override
	public String getDescription()
	{
		return "Converts units in a geoevent definition";
	}

	@Override
	public String getContactInfo()
	{
		return "geoeventprocessor@esri.com";
	}

}
