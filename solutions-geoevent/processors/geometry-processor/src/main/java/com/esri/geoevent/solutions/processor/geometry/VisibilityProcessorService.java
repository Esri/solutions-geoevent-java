package com.esri.geoevent.solutions.processor.geometry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessor;
import com.esri.ges.processor.GeoEventProcessorServiceBase;
import com.esri.ges.spatial.Spatial;

public class VisibilityProcessorService extends GeoEventProcessorServiceBase {
	public Spatial spatial;
	private static final Log LOG = LogFactory
			.getLog(VisibilityProcessorService.class);
	public GeoEventDefinitionManager manager;

	public VisibilityProcessorService() {
		definition = new VisibilityProcessorDefinition();
	}

	@Override
	public GeoEventProcessor create() throws ComponentException {
		try {
			return new VisibilityProcessor(definition, spatial, manager);
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

	public void setSpatial(Spatial s) {
		spatial = s;
	}

	public void setManager(GeoEventDefinitionManager m) {
		manager = m;
	}

	public void start() {
		try {
			VisibilityProcessorDefinition vDef = (VisibilityProcessorDefinition) definition;
			vDef.setManager(manager);
		} catch (Exception e) {
			LOG.error("Geometry processor");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
		}
	}

}
