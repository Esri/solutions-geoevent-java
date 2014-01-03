package com.esri.geoevent.solutions.processor.geometry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessor;
import com.esri.ges.processor.GeoEventProcessorServiceBase;
import com.esri.ges.spatial.Spatial;

public class BufferProcessorService extends GeoEventProcessorServiceBase {
	Spatial spatial;
	private static final Log LOG = LogFactory
			.getLog(BufferProcessorService.class);
	public GeoEventDefinitionManager manager;
	public BufferProcessorService() throws PropertyException {
		definition = new BufferProcessorDefinition();
	}

	@Override
	public GeoEventProcessor create() {
		try {
			return new BufferProcessor(definition, spatial);
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
	
	public void start() {
		try {
			BufferProcessorDefinition bDef = (BufferProcessorDefinition) definition;
			bDef.setManager(manager);
		} catch (Exception e) {
			LOG.error("Geometry processor");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
		}
	}

}
