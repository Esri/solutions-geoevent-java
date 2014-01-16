package com.esri.ges.transport.Irc.jerklib.events.impl.dcc;

import java.net.InetAddress;

import com.esri.ges.transport.Irc.jerklib.Channel;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.dcc.DccEvent;
import com.esri.ges.transport.Irc.jerklib.events.dcc.DccSendEvent;


/**
 * 
 * @author Andres N. Kievsky
 */
public class DccSendEventImpl extends DccEventImpl implements DccSendEvent
{

	private InetAddress ip;
	private String filename;
	private int port;
	// fileSize < 0 means "not known".
	private long fileSize;

	public DccSendEventImpl(String filename, InetAddress ip, int port, long fileSize, String ctcpString, String hostName, String message, String nick, String userName, String rawEventData,
			Channel channel, Session session)
	{
		super(ctcpString, hostName, message, nick, userName, rawEventData, channel, session);
		this.ip = ip;
		this.filename = filename;
		this.port = port;
		this.fileSize = fileSize;
	}

	public String getFilename()
	{
		return this.filename;
	}

	public InetAddress getIp()
	{
		return this.ip;
	}

	public int getPort()
	{
		return this.port;
	}

	public long getFileSize()
	{
		return this.fileSize;
	}

	public boolean isFileSizeKnown()
	{
		return this.fileSize >= 0;
	}

	public DccType getDccType()
	{
		return DccEvent.DccType.SEND;
	}

}
