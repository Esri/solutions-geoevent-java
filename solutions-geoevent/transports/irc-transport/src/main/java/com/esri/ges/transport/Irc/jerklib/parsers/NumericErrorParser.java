package com.esri.ges.transport.Irc.jerklib.parsers;

import com.esri.ges.transport.Irc.jerklib.EventToken;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;
import com.esri.ges.transport.Irc.jerklib.events.impl.NumericEventImpl;

public class NumericErrorParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		return new NumericEventImpl
		(
				token.arg(0), 
				token.data(), 
				token.numeric(), 
				event.getSession()
		); 
	}
}
