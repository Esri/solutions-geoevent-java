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
package com.esri.geoevent.solutions.processor.geometry;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.processor.GeoEventProcessor;
import com.esri.ges.processor.GeoEventProcessorServiceBase;
import com.esri.ges.spatial.Spatial;

public class BufferProcessorService extends GeoEventProcessorServiceBase {
	Spatial spatial;
	public BufferProcessorService() throws PropertyException {
		definition = new BufferProcessorDefinition();
	}

	@Override
	public GeoEventProcessor create() throws ComponentException {
		return new BufferProcessor(definition, spatial);
	}
	
	public void setSpatial(Spatial s)
	{
		spatial = s;
	}

}
