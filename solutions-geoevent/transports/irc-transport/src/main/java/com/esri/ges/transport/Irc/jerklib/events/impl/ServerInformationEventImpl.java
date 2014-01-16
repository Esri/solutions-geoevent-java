package com.esri.ges.transport.Irc.jerklib.events.impl;

import com.esri.ges.transport.Irc.jerklib.ServerInformation;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.ServerInformationEvent;

/**
 * @author mohadib
 * @see ServerInformationEvent
 *
 */
public class ServerInformationEventImpl implements ServerInformationEvent
{

	private final Session session;
	private final String rawEventData;
	private final ServerInformation serverInfo;

	public ServerInformationEventImpl(Session session, String rawEventData, ServerInformation serverInfo)
	{
		this.session = session;
		this.rawEventData = rawEventData;
		this.serverInfo = serverInfo;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.ServerInformationEvent#getServerInformation()
	 */
	public ServerInformation getServerInformation()
	{
		return serverInfo;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getRawEventData()
	 */
	public String getRawEventData()
	{
		return rawEventData;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getSession()
	 */
	public Session getSession()
	{
		return session;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getType()
	 */
	public Type getType()
	{
		return Type.SERVER_INFORMATION;
	}

}
