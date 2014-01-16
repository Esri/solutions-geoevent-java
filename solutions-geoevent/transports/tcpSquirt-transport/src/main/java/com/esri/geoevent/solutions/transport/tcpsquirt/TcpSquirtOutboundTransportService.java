package com.esri.geoevent.solutions.transport.tcpsquirt;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.transport.Transport;
import com.esri.ges.transport.TransportServiceBase;
import com.esri.ges.transport.util.XmlTransportDefinition;

public class TcpSquirtOutboundTransportService extends TransportServiceBase
{
  public TcpSquirtOutboundTransportService()
  {
    definition = new XmlTransportDefinition( getResourceAsStream( "tcpsquirt-outbound-transport-definition.xml" ) );
  }
  
  //@Override
  public Transport createTransport() throws ComponentException
  {
    return new TcpSquirtOutboundTransport(definition);
  }
}