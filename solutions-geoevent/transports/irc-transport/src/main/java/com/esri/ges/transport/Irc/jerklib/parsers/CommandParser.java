package com.esri.ges.transport.Irc.jerklib.parsers;

import com.esri.ges.transport.Irc.jerklib.EventToken;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;

public interface CommandParser
{
	public IRCEvent createEvent(EventToken token , IRCEvent event);
}
