package com.esri.ges.solutions.adapter.cap;

import java.util.ArrayList;
import java.util.List;

import com.esri.ges.adapter.AdapterDefinitionBase;
import com.esri.ges.adapter.AdapterType;
import com.esri.ges.core.ConfigurationException;
import com.esri.ges.core.geoevent.DefaultFieldDefinition;
import com.esri.ges.core.geoevent.DefaultGeoEventDefinition;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.core.geoevent.GeoEventDefinition;

public class CAPInboundAdapterDefinition extends AdapterDefinitionBase
{
	public CAPInboundAdapterDefinition()
	{
		super(AdapterType.INBOUND);
		try
		{
			GeoEventDefinition md = new DefaultGeoEventDefinition();
			md.setName("SampleGeoEventDefinition");
			List<FieldDefinition> fieldDefinitions = new ArrayList<FieldDefinition>();
			fieldDefinitions.add(new DefaultFieldDefinition("track_id", FieldType.Long));
			fieldDefinitions.add(new DefaultFieldDefinition("location", FieldType.Geometry));
			md.setFieldDefinitions(fieldDefinitions);
			geoEventDefinitions.put(md.getName(), md);
		}
		catch (ConfigurationException ex)
		{
			;
		}
	}

	@Override
	public String getName()
	{
		return "Sample";
	}
	
	@Override
  public String getLabel()
  {
    return "Sample Inbound Adapter";
  }
	
	@Override
	public String getDomain()
	{
		return "com.esri.ges.adapter.inbound";
	}

	@Override
	public String getDescription()
	{
		return "This is a sample adapter.";
	}

	@Override
	public String getContactInfo()
	{
		return "yourname@yourcompany.com";
	}
}