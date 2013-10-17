package com.esri.ges.solutions.processor;

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
