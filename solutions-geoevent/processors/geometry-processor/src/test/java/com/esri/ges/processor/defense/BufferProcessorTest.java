package com.esri.ges.processor.defense;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.solutions.processor.BufferProcessor;
import com.esri.ges.solutions.processor.BufferProcessorDefinition;
import com.esri.ges.spatial.Spatial;

public class BufferProcessorTest {
	static Spatial spatial;
	static BufferProcessorDefinition definition;
	static BufferProcessor testclass;
	@BeforeClass
	  public static void testSetup() throws ComponentException, PropertyException {
		spatial=createMock(Spatial.class);
		definition = new BufferProcessorDefinition();
		
	  }
	@Test
	public void testBufferProcessor() throws ComponentException {
		testclass = new BufferProcessor(definition, spatial);
		assertNotNull("Buffer Processor test created: Buffer processor not null",testclass);
	}

	/*@Test
	public void testProcess() throws ComponentException {
		testclass = new BufferProcessor(definition, spatial);
	}*/

}
