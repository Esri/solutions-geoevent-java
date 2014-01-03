package com.esri.geoevent.solutions.processor.geometry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessor;
import com.esri.ges.processor.GeoEventProcessorServiceBase;
import com.esri.ges.spatial.Spatial;

public class RangeFanProcessorService extends GeoEventProcessorServiceBase {
	public Spatial spatial;
	public GeoEventDefinitionManager manager;
	private static final Log LOG = LogFactory
			.getLog(RangeFanProcessorService.class);
	//public TagManager tagManager;
	public RangeFanProcessorService() throws PropertyException {
		definition = new RangeFanProcessorDefinition();
	}

	@Override
	public GeoEventProcessor create() {
		try {
			return new RangeFanProcessor(definition, spatial, manager);
		} catch (ComponentException e) {
			LOG.error("Geometry processor");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			return null;
		} catch (Exception e) {
			LOG.error("Geometry processor");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			return null;
		}

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
	
	public void start() {

		try {
			RangeFanProcessorDefinition rfpDef = (RangeFanProcessorDefinition) definition;
			rfpDef.setManager(manager);

		} catch (Exception e) {
			LOG.error("Geometry processor");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
		}
	}

}
