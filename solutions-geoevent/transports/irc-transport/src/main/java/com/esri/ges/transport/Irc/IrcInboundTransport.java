package com.esri.ges.transport.Irc;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.component.RunningException;
import com.esri.ges.core.component.RunningState;
import com.esri.ges.transport.InboundTransportBase;
import com.esri.ges.transport.TransportDefinition;
import com.esri.ges.transport.Irc.jerklib.ConnectionManager;
import com.esri.ges.transport.Irc.jerklib.Profile;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;
import com.esri.ges.transport.Irc.jerklib.events.JoinCompleteEvent;
import com.esri.ges.transport.Irc.jerklib.events.MessageEvent;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent.Type;
import com.esri.ges.transport.Irc.jerklib.listeners.IRCEventListener;

public class IrcInboundTransport extends InboundTransportBase implements IRCEventListener
{
	private static final Log log = LogFactory.getLog(IrcInboundTransport.class);

	private String[] channelList ={"#pircbot"};
	private String nickName = "EsriGEP";
	private String serverName = "irc.freenode.net";
	private ConnectionManager manager;
	
	private ByteBuffer byteBuffer = ByteBuffer.allocate(512+256); // max message is 512 unknown max nick size but we will clip to 256

	public IrcInboundTransport(TransportDefinition definition) throws ComponentException
	{
		super(definition);
	}

	public void applyProperties()
	{
		if (getProperty("channelList").isValid())
		{
			String value = (String) getProperty("channelList").getValue();
			if( value.length() > 0 )
			{
				channelList = value.split(",");
			}
		}
		if (getProperty("nickName").isValid())
		{
			String value = (String) getProperty("nickName").getValue();
			if( value.length() > 0 )
			{
				nickName = value;
			}
		}
		if (getProperty("serverName").isValid())
		{
			String value = (String) getProperty("serverName").getValue();
			if( value.length() > 0 )
			{
				serverName = value;
			}
		}
	}

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		applyProperties();
	}

	public void receiveEvent(IRCEvent e) {
		
		if (e.getType() == Type.CONNECT_COMPLETE)
		{
			for (int i=0;i< channelList.length; i++)
			{
				e.getSession().join(channelList[i]);
			}
		}
		else if (e.getType() == Type.CHANNEL_MESSAGE)
		{
			// got a standard message send it and the nickname that posted it
			try
			{
				MessageEvent me = (MessageEvent) e;
				String channel = me.getChannel().getName().substring(0, Math.min(me.getChannel().getName().length(), 50));
				String text = me.getMessage();
				String nick = me.getNick().substring(0, Math.min(me.getNick().length(), 50));
				 // kind of a hack but I want to make sure the message does not contain Pipe characters which would mess up downstream parsers
				text = text.replaceAll("\\|", "!");
				String message = channel + "|" + nick + "|" + text+"\n";
				byteBuffer.put(message.getBytes());
				byteBuffer.flip();
				byteListener.receive(byteBuffer, channel);
				byteBuffer.compact();
				log.debug(me.getChannel().getName() + ":" + message);
			}

			catch (BufferOverflowException boe)
			{
				log.error("Buffer overflow.  Flushing the buffer and continuing.", boe);
				byteBuffer.clear();
			}
			catch (Exception ex)
			{
				log.error("Unexpected error, stopping the Transport.", ex);
				stop();
			}

		}
		else if (e.getType() == Type.JOIN_COMPLETE)
		{
			JoinCompleteEvent jce = (JoinCompleteEvent) e;
			/* say hello */
			jce.getChannel().say("Hello. "+ nickName + " bot is now listining on this channel.");
			setRunningState(RunningState.STARTED);
		}
		else if (e.getType() == Type.KICK_EVENT)
		{
			log.error("KICKed out of Chatroom.  Stopping Transport");
			setErrorMessage("KICKed out of Chatroom.  Stopping Transport");
			stop();
		}
		else if (e.getType() == Type.CONNECTION_LOST)
		{
			log.error("Lost Connection. Stopping");
			setErrorMessage("Lost Connection. Stopping");
			stop();
		}
		else if (e.getType() == Type.ERROR)
		{
			log.error("Error returned (possibly Host not found). Stopping");
			stop();
		}
//		else		
//		{
//			log.debug("Lost Connection. Stopping");		
//		}

	}
	
	@Override
	public synchronized void stop() {
		super.stop();
		setRunningState(RunningState.STOPPED);	
		if (manager != null)
		{
			manager.quit();
		}
	}

	@SuppressWarnings("incomplete-switch")
  public void start() throws RunningException
	{
    switch (getRunningState())
		{
		case STARTING:
		case STARTED:
		case STOPPING:
			return;
		}
		setRunningState(RunningState.STARTING);
		applyProperties();
		/*
		 * ConnectionManager takes a Profile to use for new connections.
		 */
		manager = new ConnectionManager(new Profile(nickName));
		
		/*
		 * One instance of ConnectionManager can connect to many IRC networks.
		 * ConnectionManager#requestConnection(String) will return a Session object.
		 * The Session is the main way users will interact with this library and IRC
		 * networks
		 */
		Session session = manager.requestConnection(serverName);
		
		/*
		 * JerkLib fires IRCEvents to notify users of the lib of incoming events
		 * from a connected IRC server.
		 */
		session.addIRCEventListener(this);
	}


}