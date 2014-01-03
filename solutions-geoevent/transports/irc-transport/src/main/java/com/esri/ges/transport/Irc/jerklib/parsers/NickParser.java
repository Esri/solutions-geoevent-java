package com.esri.ges.transport.Irc.jerklib.parsers;

import com.esri.ges.transport.Irc.jerklib.EventToken;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;
import com.esri.ges.transport.Irc.jerklib.events.impl.NickChangeEventImpl;

public class NickParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		return new NickChangeEventImpl
		(
				token.data(), 
				session, 
				token.nick(), // old
				token.arg(0), // new nick
				token.hostName(), // hostname
				token.userName() // username
		); 
	}
}
