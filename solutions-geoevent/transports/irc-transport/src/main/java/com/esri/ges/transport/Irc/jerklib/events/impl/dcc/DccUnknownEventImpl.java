package com.esri.ges.transport.Irc.jerklib.events.impl.dcc;

import com.esri.ges.transport.Irc.jerklib.Channel;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.dcc.DccEvent;
import com.esri.ges.transport.Irc.jerklib.events.dcc.DccUnknownEvent;

/**
 * 
 * @author Andres N. Kievsky
 */
public class DccUnknownEventImpl extends DccEventImpl implements DccUnknownEvent
{

	public DccUnknownEventImpl(String ctcpString, String hostName, String message, String nick, String userName, String rawEventData, Channel channel, Session session)
	{
		super(ctcpString, hostName, message, nick, userName, rawEventData, channel, session);
	}

	public DccType getDccType()
	{
		return DccEvent.DccType.UNKNOWN;
	}

}
