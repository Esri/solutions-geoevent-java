package com.esri.ges.processor.defense;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.manager.tag.TagManager;
import com.esri.ges.processor.GeoEventProcessor;
import com.esri.ges.processor.GeoEventProcessorServiceBase;
import com.esri.ges.spatial.Spatial;

public class RangeFanProcessorService extends GeoEventProcessorServiceBase {
	public Spatial spatial;
	public GeoEventDefinitionManager manager;
	//public TagManager tagManager;
	public RangeFanProcessorService() throws PropertyException {
		definition = new RangeFanProcessorDefinition();
	}

	@Override
	public GeoEventProcessor create() throws ComponentException {
		//return new RangeFanProcessor(definition, spatial, manager, tagManager);
		return new RangeFanProcessor(definition, spatial, manager);
	}
	
	public void setSpatial(Spatial s)
	{
		spatial = s;
	}
	
	public void setManager(GeoEventDefinitionManager m)
	{
		manager = m;
	}
	
	/*public void setTagManager(TagManager tm)
	{
		tagManager=tm;
	}*/
	
	public void start() throws PropertyException{
		
		RangeFanProcessorDefinition rfpDef = (RangeFanProcessorDefinition)definition;
		rfpDef.setManager(manager);
	}

}
