package com.esri.ges.transport.Irc.jerklib.parsers;

import com.esri.ges.transport.Irc.jerklib.Channel;
import com.esri.ges.transport.Irc.jerklib.EventToken;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;
import com.esri.ges.transport.Irc.jerklib.events.MessageEvent;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent.Type;
import com.esri.ges.transport.Irc.jerklib.events.dcc.DccEventFactory;
import com.esri.ges.transport.Irc.jerklib.events.impl.CtcpEventImpl;
import com.esri.ges.transport.Irc.jerklib.events.impl.MessageEventImpl;

public class PrivMsgParser implements CommandParser
{
	/*
	 * :gh00p!~ghoti@nix-58E3BFC5.cpe.net.cable.rogers.com PRIVMSG #tvtorrents :gotcha
	 * :NeWtoz!jimmy@nix-2F996C9F.dhcp.aldl.mi.charter.com PRIVMSG #tvtorrents :No problem
	 * :cute_bettong!n=elphias@about/apple/IIe/B0FH PRIVMSG #ubuntu :Elphias (elphias)
	 */
	
	public MessageEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		Type type = session.isChannelToken(token.arg(0))?Type.CHANNEL_MESSAGE:Type.PRIVATE_MESSAGE;
		Channel chan = type == Type.CHANNEL_MESSAGE? session.getChannel(token.arg(0)):null;
		
		MessageEvent me =  new MessageEventImpl
		(
			chan,
			token.hostName(),
			token.arg(1), 
			token.nick(),
			token.data(), 
			session, 
			type, 
			token.userName()
		);
		
		String msg = me.getMessage();
		if (msg.startsWith("\u0001"))
		{
			String ctcpString = msg.substring(1, msg.length() - 1);
			if (ctcpString.startsWith("DCC "))
			{
				me = DccEventFactory.dcc(me, ctcpString);
			}
			else
			{
				return new CtcpEventImpl
				(
					ctcpString, 
					me.getHostName(), 
					me.getMessage(), 
					me.getNick(), 
					me.getUserName(), 
					me.getRawEventData(), 
					me.getChannel(), 
					me.getSession()
				);
			}
		}
		
		return me;
	}
}
