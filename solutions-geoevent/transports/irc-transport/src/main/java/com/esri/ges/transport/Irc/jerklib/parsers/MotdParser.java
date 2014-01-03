package com.esri.ges.transport.Irc.jerklib.parsers;

import com.esri.ges.transport.Irc.jerklib.EventToken;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;
import com.esri.ges.transport.Irc.jerklib.events.impl.MotdEventImpl;

public class MotdParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		return new MotdEventImpl
		(
			token.data(), 
			event.getSession(), 
			token.arg(1), 
			token.prefix()
		);
	}
}
