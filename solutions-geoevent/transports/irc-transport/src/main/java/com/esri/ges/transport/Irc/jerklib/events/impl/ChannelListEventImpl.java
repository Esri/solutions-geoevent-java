package com.esri.ges.transport.Irc.jerklib.events.impl;

import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.ChannelListEvent;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;

/**
 * 
 * @author mohadib
 *@see ChannelListEvent
 */
public class ChannelListEventImpl implements ChannelListEvent
{

	private final Session session;
	private final String rawEventData, channelName, topic;
	private final int numUsers;
	private final Type type = IRCEvent.Type.CHANNEL_LIST_EVENT;

	public ChannelListEventImpl(String rawEventData, String channelName, String topic, int numUsers, Session session)
	{
		this.rawEventData = rawEventData;
		this.session = session;
		this.channelName = channelName;
		this.topic = topic;
		this.numUsers = numUsers;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.ChannelListEvent#getChannelName()
	 */
	public String getChannelName()
	{
		return channelName;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.ChannelListEvent#getNumberOfUser()
	 */
	public int getNumberOfUser()
	{
		return numUsers;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.ChannelListEvent#getTopic()
	 */
	public String getTopic()
	{
		return topic;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getType()
	 */
	public Type getType()
	{
		return type;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getRawEventData()
	 */
	public String getRawEventData()
	{
		return rawEventData;
	}

	public Session getSession()
	{
		return session;
	}

}
