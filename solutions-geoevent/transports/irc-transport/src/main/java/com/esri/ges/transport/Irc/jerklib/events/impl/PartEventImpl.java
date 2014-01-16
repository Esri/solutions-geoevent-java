package com.esri.ges.transport.Irc.jerklib.events.impl;

import com.esri.ges.transport.Irc.jerklib.Channel;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;
import com.esri.ges.transport.Irc.jerklib.events.PartEvent;

/**
 * @see PartEvent
 * @author mohadib
 *
 */
public class PartEventImpl implements PartEvent
{

	private final Type type = IRCEvent.Type.PART;
	private final String rawEventData, channelName, who, partMessage, userName, hostName;
	private final Session session;
	private final Channel channel;

	public PartEventImpl(String rawEventData, Session session, String who, String user, String host, String channelName, Channel channel, String partMessage)
	{
		this.rawEventData = rawEventData;
		this.session = session;
		this.channelName = channelName;
		this.channel = channel;
		this.who = who;
		this.userName = user;
		this.hostName = host;
		this.partMessage = partMessage;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.PartEvent#getWho()
	 */
	public final String getWho()
	{
		return who;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.PartEvent#getHostName()
	 */
	public String getHostName()
	{
		return hostName;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.PartEvent#getUserName()
	 */
	public String getUserName()
	{
		return userName;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.PartEvent#getChannelName()
	 */
	public final String getChannelName()
	{
		return channelName;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.PartEvent#getChannel()
	 */
	public final Channel getChannel()
	{
		return channel;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getType()
	 */
	public final Type getType()
	{
		return type;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getRawEventData()
	 */
	public final String getRawEventData()
	{
		return rawEventData;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getSession()
	 */
	public final Session getSession()
	{
		return session;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.PartEvent#getPartMessage()
	 */
	public final String getPartMessage()
	{
		return this.partMessage;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return rawEventData;
	}

}
