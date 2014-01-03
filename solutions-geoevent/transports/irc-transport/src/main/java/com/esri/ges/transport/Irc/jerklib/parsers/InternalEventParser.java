package com.esri.ges.transport.Irc.jerklib.parsers;

import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;

public interface InternalEventParser
{
	public IRCEvent receiveEvent(IRCEvent e);
}
