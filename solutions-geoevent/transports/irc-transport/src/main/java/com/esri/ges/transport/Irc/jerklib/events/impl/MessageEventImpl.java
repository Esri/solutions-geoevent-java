package com.esri.ges.transport.Irc.jerklib.events.impl;

import com.esri.ges.transport.Irc.jerklib.Channel;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;
import com.esri.ges.transport.Irc.jerklib.events.MessageEvent;

/**
 * @author mohadib
 * @see MessageEvent
 * 
 */
public class MessageEventImpl implements MessageEvent
{

	private final String nick, userName, hostName, message, rawEventData;
	private final Channel channel;
	private final Type type;
	private final Session session;

	public MessageEventImpl
	(
		Channel channel, 
		String hostName, 
		String message, 
		String nick, 
		String rawEventData, 
		Session session, 
		IRCEvent.Type type, 
		String userName
	)
	{
		this.channel = channel;
		this.hostName = hostName;
		this.message = message;
		this.nick = nick;
		this.rawEventData = rawEventData;
		this.session = session;
		this.type = type;
		this.userName = userName;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.MessageEvent#getChannel()
	 */
	public Channel getChannel()
	{
		return channel;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.MessageEvent#getHostName()
	 */
	public String getHostName()
	{
		return hostName;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.MessageEvent#getMessage()
	 */
	public String getMessage()
	{
		return message;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.MessageEvent#getNick()
	 */
	public String getNick()
	{
		return nick;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getRawEventData()
	 */
	public String getRawEventData()
	{
		return rawEventData;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getType()
	 */
	public Type getType()
	{
		return type;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.MessageEvent#getUserName()
	 */
	public String getUserName()
	{
		return userName;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getSession()
	 */
	public Session getSession()
	{
		return session;
	}
}
