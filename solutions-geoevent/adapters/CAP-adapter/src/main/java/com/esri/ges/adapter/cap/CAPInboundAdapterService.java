package com.esri.ges.adapter.cap;

import java.io.InputStream;

import com.esri.ges.adapter.Adapter;
import com.esri.ges.adapter.AdapterServiceBase;
import com.esri.ges.adapter.util.XmlAdapterDefinition;
import com.esri.ges.core.component.ComponentException;

public class CAPInboundAdapterService extends AdapterServiceBase
{
	public CAPInboundAdapterService()
	{	
		definition = new XmlAdapterDefinition(getResourceAsStream("cap-inbound-adapter-definition.xml"));		
	}
	
	@Override
	public Adapter createAdapter() throws ComponentException
	{
		return new CAPInboundAdapter(definition);
	}
}