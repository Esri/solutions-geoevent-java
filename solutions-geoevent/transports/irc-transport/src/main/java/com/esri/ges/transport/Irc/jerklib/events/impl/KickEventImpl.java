package com.esri.ges.transport.Irc.jerklib.events.impl;

import com.esri.ges.transport.Irc.jerklib.Channel;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.KickEvent;

/**
 * @see KickEvent
 * @author mohadib
 *
 */
public class KickEventImpl implements KickEvent
{

	private final Type type = Type.KICK_EVENT;
	private final String byWho, who, message, rawEventData, userName, hostName;
	private final Channel channel;
	private final Session session;

	public KickEventImpl
	(
		String rawEventData, 
		Session session, 
		String byWho, 
		String userName, 
		String hostName, 
		String who, 
		String message, 
		Channel channel
	)
	{
		this.rawEventData = rawEventData;
		this.session = session;
		this.byWho = byWho;
		this.userName = userName;
		this.hostName = hostName;
		this.who = who;
		this.message = message;
		this.channel = channel;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.KickEvent#byWho()
	 */
	public String byWho()
	{
		return byWho;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.KickEvent#getWho()
	 */
	public String getWho()
	{
		return who;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.KickEvent#getHostName()
	 */
	public String getHostName()
	{
		return hostName;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.KickEvent#getUserName()
	 */
	public String getUserName()
	{
		return userName;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.KickEvent#getMessage()
	 */
	public String getMessage()
	{
		return message;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.KickEvent#getChannel()
	 */
	public Channel getChannel()
	{
		return channel;
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
		return type;
	}

}
