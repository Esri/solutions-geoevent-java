package com.esri.ges.transport.Irc.jerklib.parsers;

import com.esri.ges.transport.Irc.jerklib.Channel;
import com.esri.ges.transport.Irc.jerklib.EventToken;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;
import com.esri.ges.transport.Irc.jerklib.events.JoinEvent;
import com.esri.ges.transport.Irc.jerklib.events.impl.JoinCompleteEventImpl;
import com.esri.ges.transport.Irc.jerklib.events.impl.JoinEventImpl;

public class JoinParser implements CommandParser
{

	// :r0bby!n=wakawaka@guifications/user/r0bby JOIN :#com.esri.ges.transport.Irc.jerklib
	// :mohadib_!~mohadib@68.35.11.181 JOIN &test
	
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		
		JoinEvent je = new JoinEventImpl
		(
			token.data(), 
			session, 
			token.nick(), // nick
			token.userName(), // user name
			token.hostName(), // host
			token.arg(0), // channel name
			session.getChannel(token.arg(0)) // channel
		);
		
		if(je.getNick().equalsIgnoreCase(event.getSession().getNick()))
		{
			return new JoinCompleteEventImpl
			(
				je.getRawEventData(), 
				je.getSession(),
				new Channel(je.getChannelName() , event.getSession())
			);
		}
		return je;
	}
}
