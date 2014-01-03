package com.esri.geoevent.solutions.adapter.geomessage;

import com.esri.ges.adapter.Adapter;
import com.esri.ges.adapter.AdapterServiceBase;
import com.esri.ges.adapter.util.XmlAdapterDefinition;
import com.esri.ges.core.component.ComponentException;

public class DefenseInboundAdapterService extends AdapterServiceBase
{
	public DefenseInboundAdapterService()
	{
		definition = new XmlAdapterDefinition(getResourceAsStream("inbound-adapter-definition.xml"));
	}

	@Override
	public Adapter createAdapter() throws ComponentException
	{
		try
		{
			return new DefenseInboundAdapter(definition);
		}
		catch (Exception e)
		{
			throw new ComponentException("DefenseInboundAdapter instantiation failed: " + e.getMessage());
		}
	}
}