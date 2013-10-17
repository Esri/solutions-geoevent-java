package com.esri.ges.solutions.processor;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessor;
import com.esri.ges.processor.GeoEventProcessorServiceBase;
import com.esri.ges.spatial.Spatial;

public class VisibilityProcessorService extends GeoEventProcessorServiceBase {
	public Spatial spatial;
	public GeoEventDefinitionManager manager;
	public VisibilityProcessorService() {
		definition = new VisibilityProcessorDefinition();
	}

	@Override
	public GeoEventProcessor create() throws ComponentException {
		return new VisibilityProcessor(definition, spatial, manager);
	}
	
	public void setSpatial(Spatial s)
	{
		spatial = s;
	}
	
	public void setManager(GeoEventDefinitionManager m)
	{
		manager = m;
	}
	
	public void start() throws PropertyException{
		
		VisibilityProcessorDefinition vDef = (VisibilityProcessorDefinition)definition;
		vDef.setManager(manager);
	}

}
