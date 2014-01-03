/*
 | Copyright 2013 Esri
 |
 | Licensed under the Apache License, Version 2.0 (the "License");
 | you may not use this file except in compliance with the License.
 | You may obtain a copy of the License at
 |
 |    http://www.apache.org/licenses/LICENSE-2.0
 |
 | Unless required by applicable law or agreed to in writing, software
 | distributed under the License is distributed on an "AS IS" BASIS,
 | WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 | See the License for the specific language governing permissions and
 | limitations under the License.
 */
package com.esri.geoevent.solutions.processor.geometry.test;

/*
 * #%L
 * Esri :: AGES :: Solutions :: Processor :: Geometry
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2014 Esri
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.esri.geoevent.solutions.processor.geometry.BufferProcessor;
import com.esri.geoevent.solutions.processor.geometry.BufferProcessorDefinition;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.property.PropertyException;
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
