package com.esri.ges.solutions.processor.geometry;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessor;
import com.esri.ges.processor.GeoEventProcessorServiceBase;
import com.esri.ges.spatial.Spatial;

public class PolygonProcessorService extends GeoEventProcessorServiceBase {
	public Spatial spatial;
	public GeoEventDefinitionManager manager;
	public PolygonProcessorService() {
		definition = new PolygonProcessorDefinition();
	}

	@Override
	public GeoEventProcessor create() throws ComponentException {
		return new PolygonProcessor(definition, spatial);
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
		PolygonProcessorDefinition pDef = (PolygonProcessorDefinition)definition;
		pDef.setManager(manager);
	}

}
