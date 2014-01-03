package com.esri.ges.transport.Irc.jerklib.events.impl;

import com.esri.ges.transport.Irc.jerklib.Channel;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.CtcpEvent;

/**
 * @author mohadib
 * @see CtcpEvent
 *
 */
public class CtcpEventImpl implements CtcpEvent
{

	private String ctcpString, hostName, message, nick, userName, rawEventData;
	private Channel channel;
	private Session session;

	public CtcpEventImpl
	(
		String ctcpString, 
		String hostName, 
		String message, 
		String nick, 
		String userName, 
		String rawEventData, 
		Channel channel, 
		Session session
	)
	{
		super();
		this.ctcpString = ctcpString;
		this.hostName = hostName;
		this.message = message;
		this.nick = nick;
		this.userName = userName;
		this.rawEventData = rawEventData;
		this.channel = channel;
		this.session = session;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.CtcpEvent#getCtcpString()
	 */
	public String getCtcpString()
	{
		return ctcpString;
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
	 * @see com.esri.ges.transport.Irc.jerklib.events.MessageEvent#getUserName()
	 */
	public String getUserName()
	{
		return userName;
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
		return Type.CTCP_EVENT;
	}

}
