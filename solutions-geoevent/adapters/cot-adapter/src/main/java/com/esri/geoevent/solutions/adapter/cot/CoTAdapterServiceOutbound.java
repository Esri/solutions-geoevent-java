/**
 * 
 */
package com.esri.geoevent.solutions.adapter.cot;

/*
 * #%L
 * CoTAdapterServiceOutbound.java - Esri :: AGES :: Solutions :: Adapter :: CoT - Esri - 2013
 * org.codehaus.mojo-license-maven-plugin-1.5
 * $Id: update-file-header-config.apt.vm 17764 2012-12-12 10:22:04Z tchemit $
 * $HeadURL: https://svn.codehaus.org/mojo/tags/license-maven-plugin-1.5/src/site/apt/examples/update-file-header-config.apt.vm $
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


import javax.xml.bind.JAXBException;

import com.esri.ges.adapter.Adapter;
import com.esri.ges.adapter.AdapterServiceBase;
import com.esri.ges.adapter.util.XmlAdapterDefinition;
import com.esri.ges.core.component.ComponentException;

/**
 * @author jp
 *
 */
public class CoTAdapterServiceOutbound extends AdapterServiceBase {

	/**
	 * 
	 */
	public CoTAdapterServiceOutbound() {
		XmlAdapterDefinition xmlAdapterDef = new XmlAdapterDefinition(getResourceAsStream("outbound-adapter-definition.xml"));
		
		try
		{
			xmlAdapterDef.loadConnector(getResourceAsStream("output-connector-definition.xml"));
		}
		catch (JAXBException e)
	    {
			throw new RuntimeException(e);
	    }
		definition =xmlAdapterDef;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.adapter.AdapterService#createAdapter()
	 */
	@Override
	public Adapter createAdapter() throws ComponentException {
		return new CoTAdapterOutbound(definition);
		//return null;
	}

}
