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
package com.esri.geoevent.solutions.processor.symbollookup;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class SymbolNameToIdProcessorDefinition extends GeoEventProcessorDefinitionBase
{
	private GeoEventDefinitionManager manager = null;	
	
	public SymbolNameToIdProcessorDefinition()
	{
		;
	}

	public void setManager(GeoEventDefinitionManager m) throws PropertyException {
		this.manager = m;
				
		PropertyDefinition procSymbolNameSource = new PropertyDefinition("symbolNameSource", 
				PropertyType.String, "", "SymbolName Source", "Source of SymbolName Value", true, false);
		procSymbolNameSource.addAllowedValue("Event");
		propertyDefinitions.put(procSymbolNameSource.getPropertyName(), procSymbolNameSource);
		
		PropertyDefinition procSymbolNameEvent = new PropertyDefinition("symbolNameEvent", 
				PropertyType.String, "", "SymbolName Event Field", "Geoevent field containing SymbolName data", true, false);
		procSymbolNameEvent.setDependsOn("symbolNameSource=Event");
		SetGeoEventAllowedFields(procSymbolNameEvent);
		propertyDefinitions.put(procSymbolNameEvent.getPropertyName(), procSymbolNameEvent);
		
		PropertyDefinition procSymbolIdSource = new PropertyDefinition("symbolIdSource", 
				PropertyType.String, "", "SymbolId Source", "Source of SymbolId Value", true, false);
		procSymbolIdSource.addAllowedValue("Event");
		propertyDefinitions.put(procSymbolIdSource.getPropertyName(), procSymbolIdSource);
		
		PropertyDefinition procSymbolIdEvent = new PropertyDefinition("symbolIdEvent", 
				PropertyType.String, "", "SymbolId Event Field", "Geoevent field containing SymbolId data", true, false);
		procSymbolIdEvent.setDependsOn("symbolIdSource=Event");
		SetGeoEventAllowedFields(procSymbolIdEvent);
		propertyDefinitions.put(procSymbolIdEvent.getPropertyName(), procSymbolIdEvent);
		
	}	
	
	private void SetGeoEventAllowedFields(PropertyDefinition pd)
	{
		if (this.manager == null)
			return;
		
		Collection<GeoEventDefinition> geodefs = this.manager.listAllGeoEventDefinitions();
		if (geodefs == null)
			return;
		
		Iterator<GeoEventDefinition> it = geodefs.iterator();
		GeoEventDefinition geoEventDef;
		while (it.hasNext())
		{
			geoEventDef = it.next();
			String defName = geoEventDef.getName();
			List<FieldDefinition> fieldDefs = geoEventDef.getFieldDefinitions();
			
			if (fieldDefs != null)
			{
				int fieldDefSize = fieldDefs.size();
			
				for(int i = 0; i < fieldDefSize; ++i)
				{
					String fld = geoEventDef.getFieldDefinitions().get(i).getName();
					pd.addAllowedValue(defName + ":" + fld);
				}
			}
		}
	}
	
	@Override
	public String getName()
	{
		return "SymbolNameToIdProcessor";
	}

	@Override
	public String getDomain()
	{
		return "com.esri.geoevent.solutions.processor.symbollookup";
	}

	@Override
	public String getVersion()
	{
		return "10.2.0";
	}

	@Override
	public String getLabel()
	{
		return "Symbol Name To Id Processor";
	}

	@Override
	public String getDescription()
	{
		return "Converts well known symbol names to Symbol ID Codes (SIDCs).";
	}

	@Override
	public String getContactInfo()
	{
		return "geoeventprocessor@esri.com";
	}
}