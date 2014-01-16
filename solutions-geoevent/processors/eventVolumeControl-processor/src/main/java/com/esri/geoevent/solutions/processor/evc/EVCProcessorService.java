package com.esri.geoevent.solutions.processor.evc;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.processor.GeoEventProcessor;
import com.esri.ges.processor.GeoEventProcessorServiceBase;

public class EVCProcessorService extends GeoEventProcessorServiceBase
{
  public EVCProcessorService()
  {
    definition = new EVCProcessorDefinition();
  }

  @Override
  public GeoEventProcessor create() throws ComponentException
  {
    return new EVCProcessor(definition);
  }
}