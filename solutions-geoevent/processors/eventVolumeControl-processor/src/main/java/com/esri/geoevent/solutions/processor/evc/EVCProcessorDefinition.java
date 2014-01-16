package com.esri.geoevent.solutions.processor.evc;

import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class EVCProcessorDefinition extends GeoEventProcessorDefinitionBase
{
	public EVCProcessorDefinition()
	{
		try {
			PropertyDefinition pdInterval = new PropertyDefinition("interval", PropertyType.Long, 10000, "Interval (miliseconds)", "Amount of time between which new messages with the same track id will be dropped", true, false);
			propertyDefinitions.put(pdInterval.getPropertyName(), pdInterval);
			
			PropertyDefinition pdFilterType = new PropertyDefinition("filterType", PropertyType.String, "", "FilterType", "Type of filtering \n(By interval: No greater than 1 event of given trackid per interval in milliseconds\nMax per Interval: Max number of evnts of given id per interval ", true, false);
			pdFilterType.addAllowedValue("By Interval");
			pdFilterType.addAllowedValue("Max per Interval");
			propertyDefinitions.put(pdFilterType.getPropertyName(), pdFilterType);
			
			PropertyDefinition pdEPI = new PropertyDefinition("epi", PropertyType.Long, 100, "Max items per interval", "Max number of events during interval", true, false);
			pdEPI.setDependsOn("filterType=Max per Interval");
			propertyDefinitions.put(pdEPI.getPropertyName(), pdEPI);
			
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getName()
	{
		return "EVCProcessor";
	}

	@Override
	public String getDomain()
	{
		return "com.esri.geoevent.solutions.processor.evc";
	}

	@Override
	public String getVersion()
	{
		return "10.2.0";
	}

	@Override
	public String getLabel()
	{
		return "Event Volume Control Processor";
	}

	@Override
	public String getDescription()
	{
		return "This is an event volume control processor.";
	}

	@Override
	public String getContactInfo()
	{
		return "geoeventprocessor@esri.com";
	}
}
