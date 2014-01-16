package com.esri.ges.transport.Irc.jerklib.events.impl.dcc;

import com.esri.ges.transport.Irc.jerklib.Channel;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.dcc.DccAcceptEvent;
import com.esri.ges.transport.Irc.jerklib.events.dcc.DccEvent;

/**
 * 
 * @author Andres N. Kievsky
 */
public class DccAcceptEventImpl extends DccEventImpl implements DccAcceptEvent
{

	private String filename;
	private int port;
	private long position;

	public DccAcceptEventImpl(String filename, int port, long position, String ctcpString, String hostName, String message, String nick, String userName, String rawEventData, Channel channel,
			Session session)
	{
		super(ctcpString, hostName, message, nick, userName, rawEventData, channel, session);
		this.filename = filename;
		this.port = port;
		this.position = position;
	}

	public DccType getDccType()
	{
		return DccEvent.DccType.ACCEPT;
	}

	public String getFilename()
	{
		return this.filename;
	}

	public int getPort()
	{
		return this.port;
	}

	public long getPosition()
	{
		return this.position;
	}

}
