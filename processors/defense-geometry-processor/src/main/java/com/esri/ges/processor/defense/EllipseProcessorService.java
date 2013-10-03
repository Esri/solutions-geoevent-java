package com.esri.ges.processor.defense;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessor;
import com.esri.ges.processor.GeoEventProcessorServiceBase;
import com.esri.ges.spatial.Spatial;

public class EllipseProcessorService extends GeoEventProcessorServiceBase {
	public Spatial spatial;
	public GeoEventDefinitionManager manager;
	public EllipseProcessorService() throws PropertyException{
		definition = new EllipseProcessorDefinition();
	}

	@Override
	public GeoEventProcessor create() throws ComponentException {
		return new EllipseProcessor(definition, spatial);
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
		EllipseProcessorDefinition eDef = (EllipseProcessorDefinition)definition;
		eDef.setManager(manager);
	}

}
