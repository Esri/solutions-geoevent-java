/**
 * 
 */
package com.esri.geoevent.solutions.adapter.cot;

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
