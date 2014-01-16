package com.esri.ges.transport.Irc;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.transport.Transport;
import com.esri.ges.transport.TransportServiceBase;
import com.esri.ges.transport.util.XmlTransportDefinition;

public class IrcOutboundTransportService extends TransportServiceBase
{
  public IrcOutboundTransportService()
  {
    definition = new XmlTransportDefinition(getResourceAsStream("irc-outbound-transport-definition.xml"));
  }

  public Transport createTransport() throws ComponentException
  {
    return new IrcOutboundTransport(definition);
  }
}