package com.esri.geoevent.solutions.adapter.geomessage;

import com.esri.ges.adapter.Adapter;
import com.esri.ges.adapter.AdapterServiceBase;
import com.esri.ges.adapter.util.XmlAdapterDefinition;
import com.esri.ges.core.component.ComponentException;

public class DefenseOutboundAdapterService extends AdapterServiceBase
{
	public DefenseOutboundAdapterService()
	{
		definition = new XmlAdapterDefinition(getResourceAsStream("outbound-adapter-definition.xml"));
	}

	@Override
	public Adapter createAdapter() throws ComponentException
	{
		return new DefenseOutboundAdapter(definition);
	}
}