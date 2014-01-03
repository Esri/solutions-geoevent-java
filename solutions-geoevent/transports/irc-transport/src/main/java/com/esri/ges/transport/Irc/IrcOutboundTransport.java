package com.esri.ges.transport.Irc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.component.RunningException;
import com.esri.ges.core.component.RunningState;
import com.esri.ges.transport.OutboundTransportBase;
import com.esri.ges.transport.TransportDefinition;
import com.esri.ges.transport.Irc.jerklib.Channel;
import com.esri.ges.transport.Irc.jerklib.ConnectionManager;
import com.esri.ges.transport.Irc.jerklib.Profile;
import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;
import com.esri.ges.transport.Irc.jerklib.events.JoinCompleteEvent;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent.Type;
import com.esri.ges.transport.Irc.jerklib.listeners.IRCEventListener;

public class IrcOutboundTransport extends OutboundTransportBase implements IRCEventListener
{

static final private Log log = LogFactory.getLog(IrcOutboundTransport.class);
  private String[] channelList ={"#pircbot"};
  private String nickName = "EsriGEP";
  private String serverName = "irc.freenode.net";
  private ConnectionManager manager;
	
  public IrcOutboundTransport(TransportDefinition definition) throws ComponentException
  {
    super(definition);
  }


  private void applyProperties() 
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

  public void receive(ByteBuffer buffer, String channelId)
  {
    // This function is called whenever a GeoEvent is processed by an adapter,
    // and translated into raw bytes.
    // we take the raw bytes, turn it into a string and 
	// send that string to all of the Channels listed in the properties

    Session session = manager.getSession(serverName);
    Channel channel = null;

    Charset charset = Charset.defaultCharset();
    CharsetDecoder decoder = charset.newDecoder();
    
    while (buffer.hasRemaining())
    {
    	try {
    		String message = decoder.decode(buffer).toString();

    		for (int i=0;i<channelList.length;i++)
    		{
    		    if (session != null)
    		    {
    		    	channel = session.getChannel(channelList[i]);
    		    }
    		    if (channel != null)
    		    {
    		    	session.sayChannel(message,channel);
    		    }
    		}
    	} catch (CharacterCodingException e) {

    		log.error("Exception reading outgoing IrcMessage: "+e.getMessage());
    	}
    }

  }

  @Override
public void afterPropertiesSet() {
	super.afterPropertiesSet();
	applyProperties();
}

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
	  applyProperties();
  }

  
  @Override
	public synchronized void stop() {
		super.stop();
		setRunningState(RunningState.STOPPED);
		manager.quit();
	}
  
	public void receiveEvent(IRCEvent e) {
		if (e.getType() == Type.CONNECT_COMPLETE)
		{
			for (int i=0;i< channelList.length; i++)
			{
				e.getSession().join(channelList[i]);
			}
		}
		else if (e.getType() == Type.JOIN_COMPLETE)
		{
			JoinCompleteEvent jce = (JoinCompleteEvent) e;
			/* say hello */
			//jce.getChannel().say("Hello from "+nickName);
			setRunningState(RunningState.STARTED);
		}
		else if (e.getType() == Type.KICK_EVENT)
		{
			log.error("KICKed out of Chatroom.  Stopping Transport");
			stop();
		}
		else if (e.getType() == Type.CONNECTION_LOST)
		{
			log.error("Lost Connection. Stopping");
			stop();
		}
		else if (e.getType() == Type.ERROR)
		{
			log.error("Error returned (possibly Host not found). Stopping");
			stop();
		}
//		else		
//		{
//			
//		}
		
	}
}