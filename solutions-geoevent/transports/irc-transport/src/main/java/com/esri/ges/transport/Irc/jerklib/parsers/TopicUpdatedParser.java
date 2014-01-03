package com.esri.ges.transport.Irc.jerklib.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esri.ges.transport.Irc.jerklib.EventToken;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;


public class TopicUpdatedParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		Pattern p = Pattern.compile("^.+?TOPIC\\s+(.+?)\\s+.*$");
		Matcher m = p.matcher(token.data());
		m.matches();
		event.getSession().sayRaw("TOPIC " + m.group(1));
		return event;
	}
}
