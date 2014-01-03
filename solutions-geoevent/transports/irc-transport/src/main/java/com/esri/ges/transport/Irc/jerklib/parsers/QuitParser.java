package com.esri.ges.transport.Irc.jerklib.parsers;

import java.util.List;

import com.esri.ges.transport.Irc.jerklib.Channel;
import com.esri.ges.transport.Irc.jerklib.EventToken;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;
import com.esri.ges.transport.Irc.jerklib.events.QuitEvent;
import com.esri.ges.transport.Irc.jerklib.events.impl.QuitEventImpl;


public class QuitParser implements CommandParser
{
	public QuitEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		String nick = token.nick();
		List<Channel> chanList = event.getSession().removeNickFromAllChannels(nick);
		return new QuitEventImpl
		(
			token.data(), 
			session, 
			nick, // who
			token.userName(), // username
			token.hostName(), // hostName
			token.arg(0), // message
			chanList
		);
	}
}
