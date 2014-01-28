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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.property.Property;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;

public class SymbolNameToIdProcessor extends GeoEventProcessorBase
{
	private static final Log LOG = LogFactory.getLog(SymbolNameToIdProcessor.class);
	
	private SymbolLookup symbolLookup = null;
	private GeoEventDefinitionManager manager;
	
	private static final String DEFAULT_SYMBOLNAME = "Unknown";
	private static final String DEFAULT_SYMBOLID_FIELDNAME = "SymbolId";
	private static final String DEFAULT_SYMBOLNAME_FIELDNAME = "SymbolName";	

	protected SymbolNameToIdProcessor(GeoEventProcessorDefinition definition,
			GeoEventDefinitionManager m) throws ComponentException
	{			
		super(definition);
		
		symbolLookup = new SymbolLookup(); 
		
		manager = m;		
	}

	@Override
	public boolean isGeoEventMutator() 
	{
		return true;
	}
	
	@Override
	public GeoEvent process(GeoEvent geoEvent) throws Exception
	{
		String symbolNameToConvert = DEFAULT_SYMBOLNAME;
		if (geoEvent == null)
			return geoEvent;
		
		GeoEventDefinition geoDef = geoEvent.getGeoEventDefinition();		
		if (geoDef == null) 
			return geoEvent;		
		
		// 1: Simplest Case Fields have default names 
		String symbolIdField = DEFAULT_SYMBOLID_FIELDNAME;
		String symbolNameField = DEFAULT_SYMBOLNAME_FIELDNAME;
		
		FieldDefinition fieldDefinitionSymbolName = 
		  geoDef.getFieldDefinition(DEFAULT_SYMBOLNAME_FIELDNAME);		
		FieldDefinition fieldDefinitionSymbolId   =  
		  geoDef.getFieldDefinition(DEFAULT_SYMBOLID_FIELDNAME);
				
		// 2: If not the defaults, then get from the Event 
		if (fieldDefinitionSymbolId == null)
		{
			String SYMBOL_ID_EVENT_NAME = "symbolIdEvent";
			String eventfld = properties.get(SYMBOL_ID_EVENT_NAME).getValue().toString();
			String[] arr = eventfld.split(":");
			if (arr.length >= 2)
				symbolIdField = arr[1]; 
		}
		
		if (fieldDefinitionSymbolName == null)
		{
			String SYMBOL_NAME_EVENT_NAME = "symbolNameEvent";
			String eventfld = properties.get(SYMBOL_NAME_EVENT_NAME).getValue().toString();
			String[] arr = eventfld.split(":");
			if (arr.length >= 2)
				symbolNameField = arr[1]; 			
		}

		Object oSymbolName = geoEvent.getField(symbolNameField);
		Object oSymbolId   = geoEvent.getField(symbolIdField);		
		
		if ((oSymbolName== null) || (oSymbolId == null))
			return geoEvent;		
		
		symbolNameToConvert = oSymbolName.toString();
		
		String symbolId = symbolLookup.symbolNameToId(symbolNameToConvert);
		
		@SuppressWarnings("unused")
		String currentId = geoEvent.getField(symbolIdField).toString();

		try
		{
			geoEvent.setField(symbolIdField, symbolId);
	    }
	    catch (Exception ex) {
			ex.printStackTrace();
		} 
		
		LOG.debug("Converting : " + symbolNameToConvert + "-->" + symbolId);			

		return geoEvent;
	}
    
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(definition.getName());
		sb.append("/");
		sb.append(definition.getVersion());
		sb.append("[");
		for (Property p : getProperties())
		{
			sb.append(p.getDefinition().getPropertyName());
			sb.append(":");
			sb.append(p.getValue());
			sb.append(" ");
		}
		sb.append("]");
		return sb.toString();
	}
	
	public GeoEventDefinitionManager getManager() {
		return manager;
	}

	public void setManager(GeoEventDefinitionManager manager) {
		this.manager = manager;
	}	
	
}