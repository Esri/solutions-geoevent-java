package com.esri.geoevent.solutions.transport.tcpsquirt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.transport.TransportDefinitionBase;
import com.esri.ges.transport.TransportType;

public class TcpSquirtOutboundTransportDefinition extends TransportDefinitionBase
{
	final static private Log LOG = LogFactory.getLog(TcpSquirtOutboundTransportDefinition.class);

	public TcpSquirtOutboundTransportDefinition()
	{
		super(TransportType.OUTBOUND);
		try
		{
			propertyDefinitions.put("host", new PropertyDefinition("host", PropertyType.String, "localhost", "Host (Client Mode)", "In TCP Client Mode, this is the name or IP address of the host server that GeoEvent Server will connect to. In TCP Server Mode, this parameter should be left blank.", "mode=CLIENT", false, false));
			propertyDefinitions.put("port", new PropertyDefinition("port", PropertyType.Integer, new Integer(5000), "Server Port", "In TCP Server Mode, this is the local port number that GeoEvent Server is accepting client connections on. In TCP Client Mode, this is the remote port number that GeoEvent Server will establish a connection to.", true, false));
			propertyDefinitions.put("mode", new PropertyDefinition("mode", PropertyType.String, "SERVER", "Mode", "This parameter should be set to SERVER for GeoEvent Server to run in TCP Server Mode (accepts socket connections) and CLIENT for TCP Client Mode (initiates socket connections).", true, false, "SERVER", "CLIENT"));
		}
		catch (PropertyException e)
		{
			LOG.error("Failed to define properties of TCPSquirtOutboundTransportDefinition: ", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getName()
	{
		return "TCPSquirt";
	}
	
	@Override
  public String getLabel()
  {
    return "TCP-Squirt Outbound Transport";
  }
	
	@Override
	public String getDomain()
	{
		return "com.esri.geoevent.solutions.transport.tcpsquirt.outbound";
	}

	@Override
	public String getDescription()
	{
		return "ESRI TCP-Squirt Transport for outbound streams.";
	}
}