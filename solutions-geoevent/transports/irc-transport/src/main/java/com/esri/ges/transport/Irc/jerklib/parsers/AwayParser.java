package com.esri.ges.transport.Irc.jerklib.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esri.ges.transport.Irc.jerklib.EventToken;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.AwayEvent;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;
import com.esri.ges.transport.Irc.jerklib.events.AwayEvent.EventType;
import com.esri.ges.transport.Irc.jerklib.events.impl.AwayEventImpl;


public class AwayParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s+(\\S+)\\s:(.*)$");
		Matcher m = p.matcher(event.getRawEventData());
		Session session = event.getSession();
		if (m.matches())
		{
			switch (Integer.parseInt(token.command()))
			{
			case 305:
				return new AwayEventImpl
				(
					session, 
					EventType.RETURNED_FROM_AWAY, 
					false, 
					true, 
					session.getNick(),
					event.getRawEventData()
				);
			case 306:
			{
				return new AwayEventImpl
				(
					session, 
					EventType.WENT_AWAY, 
					true, 
					true, 
					session.getNick(), 
					event.getRawEventData()
				);
			}
			}
		}
		
		// :card.freenode.net 301 r0bby_ r0bby :foo
		p = Pattern.compile("^:\\S+\\s+\\d{3}\\s+\\S+\\s+(\\S+)\\s+:(.*)$");
		m = p.matcher(event.getRawEventData());
		m.matches();
		return new AwayEventImpl(m.group(2), AwayEvent.EventType.USER_IS_AWAY, true, false, m.group(1), event.getRawEventData(), session);
	}
}
