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

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessor;
import com.esri.ges.processor.GeoEventProcessorServiceBase;

public class SymbolNameToIdProcessorService extends GeoEventProcessorServiceBase
{
  public GeoEventDefinitionManager manager;
	  
  public SymbolNameToIdProcessorService()
  {
    definition = new SymbolNameToIdProcessorDefinition();
  }

  public void setManager(GeoEventDefinitionManager m)
  {
	manager = m;
  }
  
  @Override
  public GeoEventProcessor create() throws ComponentException
  {
    return new SymbolNameToIdProcessor(definition, manager);
  }
  
  public void start() throws PropertyException{
		
	SymbolNameToIdProcessorDefinition name2SidcDef = (SymbolNameToIdProcessorDefinition)definition;
	name2SidcDef.setManager(manager);
  }   
}