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
		return "CAP";
	}
	
	@Override
    public String getLabel()
    {
      return "CAP Inbound Adapter";
    }
	
	@Override
	public String getDomain()
	{
		return "com.esri.ges.solutions.adapter.cap";
	}

	@Override
	public String getDescription()
	{
		return "Common Alerting Protocol(CAP) adapter.";
	}

	@Override
	public String getContactInfo()
	{
		return "geoeventprocessor@esri.com";
	}
}