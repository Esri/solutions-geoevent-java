package com.esri.geoevent.solutions.transport.tcpsquirt;

import static com.esri.geoevent.solutions.transport.tcpsquirt.ClientServerMode.CLIENT;
import static com.esri.geoevent.solutions.transport.tcpsquirt.ClientServerMode.SERVER;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.component.RunningException;
import com.esri.ges.core.component.RunningState;
import com.esri.ges.core.property.Property;
import com.esri.ges.transport.OutboundTransportBase;
import com.esri.ges.transport.TransportDefinition;

public class TcpSquirtOutboundTransport extends OutboundTransportBase implements Runnable{
	private static final int CLIENT_BUFFER_SIZE = 500 * 1024;
	private static final int MAIN_BUFFER_SIZE = 500 * 1024;
	static final private Log log = LogFactory.getLog(TcpSquirtOutboundTransport.class);
	private ClientServerMode mode;
	private String host;
	private int port;
	private SocketChannel socketChannel     = null;
	private Thread thread;
	private String errorMessage;
	private final ByteBuffer buffer = ByteBuffer.allocate(MAIN_BUFFER_SIZE);
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private final Map<SocketChannel, ByteBuffer> connectionBuffers = new HashMap<SocketChannel, ByteBuffer>();
	private boolean haveDataInIndividualBuffers;
	private boolean receiving = false;
	private boolean socketOpened = false;
	private int clientConnectionTimeout;
	private long timeStartedConnectingAsClient;
	private ArrayList<ByteBuffer> bufferCache = new ArrayList<ByteBuffer>();
	public TcpSquirtOutboundTransport(TransportDefinition definition)
			throws ComponentException {
		super(definition);
	}
	Integer activity = 0;
	
	//@Override
	public void start() throws RunningException {
		switch (getRunningState())
		{
		case STARTING:
		case STARTED:
			return;
		}

		setRunningState(RunningState.STARTING);
		thread = new Thread(this);
		thread.setPriority(thread.getPriority()+1);
		thread.start();

	}
	
	private void processBuffer(ByteBuffer bb)
	{
		
	}
	//@Override
	public void receive(ByteBuffer bb, String channelId) {
		if (getRunningState() == RunningState.STARTED) {
			
				receiving = true;
				synchronized (buffer) {

					try {
						if (buffer.remaining() < bb.remaining())
							log.error("The TCP/IP outbound transport is unable to keep up with the incoming data rate, dropping "
									+ bb.remaining() + " bytes.");
						else
							buffer.put(bb);
					} catch (BufferOverflowException ex) {
						log.error("The TCP/IP outbound transport is unable to keep up with the incoming data rate, dropping "
								+ bb.remaining() + " bytes.");
					}
				}
				wakupThread();
				
			}
		

	}

	private synchronized void OpenSockets() {
		if (mode == CLIENT) {
			try
			{
				attemptClientConnection();
				receiving = true;
			} catch (IOException e) {
				log.error(e);
			}
		} else if (mode == SERVER) {
			try {

				selector = Selector.open();
				serverSocketChannel = ServerSocketChannel.open();
				serverSocketChannel.configureBlocking(false);
				serverSocketChannel.socket().bind(new InetSocketAddress(port));
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
				receiving = true;
			} catch (IOException ex) {
				selector = null;
			}

		}
	}
	//@Override
	public void run() {
		try {
			errorMessage = null;
			applyProperties();
			setRunningState(RunningState.STARTED);
			while (getRunningState() == RunningState.STARTED) {
				try {
					if (receiving) {
						synchronized (this) {
							
							activity = 0;
							while (activity > -1) {
								if(!socketOpened)
								{
									OpenSockets();
									socketOpened = true;
								}
								activity = manageAllSockets();

								if (haveDataInIndividualBuffers
										&& connectionBuffers.isEmpty())
									haveDataInIndividualBuffers = false;
								if (activity == 0
										&& !haveDataInIndividualBuffers) {
									receiving = false;
									activity = -1;
									closeConnection();
									socketOpened = false;
								}
								
							}
						}
					}
				} catch (Exception e) {
					log.error("Error trying to write buffer", e);
					setRunningState(RunningState.ERROR);
				}

			}
			cleanup();
			if (getRunningState() == RunningState.STOPPING)
				setRunningState(RunningState.STOPPED);
		}
		catch (Exception ex)
		{
			errorMessage = ex.getMessage();
			log.error("Exiting TCP Transport due to unforeseen error", ex);
			setRunningState(RunningState.ERROR);
			return;
		}
		
	}
	
	private synchronized void wakupThread()
	{
		notifyAll();
	}
	
	private Integer manageAllSockets()
	{
		int a = 0;
		if (mode == CLIENT)
		{
			if( ! socketChannel.isConnected() )
			{
				try
				{
					attemptClientConnection();
				}catch(IOException ex)
				{
					if( (System.currentTimeMillis() - timeStartedConnectingAsClient) > (clientConnectionTimeout * 1000) )
					{

						errorMessage = "Error connecting to the host "+host+":"+port+" ("+ex.getMessage()+").";
						log.error( errorMessage, ex );
						setRunningState(RunningState.ERROR);
					}
				}

			}
			else
			{
				synchronized(buffer)
				{
					if( buffer.position() != 0 )
					{
						buffer.flip();
						int bytesWritten = 0;

						try
						{
							bytesWritten = socketChannel.write( buffer );
						} catch (IOException e)
						{
							if(e.getMessage().contains("An existing connection was forcibly closed by the remote host"))
							{
								try
								{
									socketChannel.close();
								} catch (IOException e1)
								{
									// Do nothing.
								}
							}
							log.error( "Error writing to the client "+host+":"+port+".", e );
						}
						buffer.compact();
						a = bytesWritten;
					}
				}
			}
		}
		else if (mode == SERVER)
		{
			int dataMoved = moveDataIntoIndividualChannelBuffers();
			// Service all the sockets
			int activeChannels = manageSelector();
			a = activeChannels + dataMoved;
			//if( haveDataInIndividualBuffers && connectionBuffers.isEmpty() )
				//haveDataInIndividualBuffers = false;
		}
		//if( activity == 0 && !haveDataInIndividualBuffers )
		//{
			//receiving=false;
			//closeConnection();
		//}
		return a;
			
	}

	private void attemptClientConnection() throws IOException
	{
		if( timeStartedConnectingAsClient == 0 )
		{
			timeStartedConnectingAsClient = System.currentTimeMillis();
		}
		socketChannel = SocketChannel.open();
		socketChannel.connect(new InetSocketAddress(host, port));
		timeStartedConnectingAsClient = 0;
	}

	private synchronized void snooze(long timer)
	{
		try
		{
			wait(timer);
		}catch(InterruptedException ex)
		{
		}
	}
	
	private synchronized void closeConnection()
	{	
		if (mode == CLIENT)
		{
			if (socketChannel != null)
			{
				try
				{
					socketChannel.close();
					socketChannel = null;
				}
				catch (IOException ioe)
				{
					log.debug("Ignoring Exception", ioe);
				}
			}
		}
		else if (mode == SERVER)
		{
			for (SocketChannel chan : connectionBuffers.keySet())
			{
				try
				{
					if( chan.isOpen() )
						chan.close();
				}catch(IOException ioe)
				{
					log.debug("Exception while closing all sockets as part of the transport shutdown process.", ioe);
				}
			}
			try
			{
				if(serverSocketChannel != null && serverSocketChannel.isOpen())
				{
					serverSocketChannel.close();
					serverSocketChannel=null;
				}
				if( selector != null && selector.isOpen() ){
					selector.close();
					selector = null;
				}
			} catch (IOException e)
			{
				log.debug("Exception while trying to stop listening for client connections. " + e);
			}
		}
	}

	private int moveDataIntoIndividualChannelBuffers()
	{
		byte[] dst = {};
		synchronized( buffer )
		{
			if (buffer.position() == 0)
				return 0;

			buffer.flip();
			dst = new byte[buffer.remaining()];
			buffer.get(dst);
			buffer.compact();
		}

		for( SocketChannel chan : connectionBuffers.keySet() )
		{
		  if(!chan.isConnected())
		  {
		    connectionBuffers.remove(chan);
		    continue;
		  }
		  ByteBuffer connectionBuffer = connectionBuffers.get(chan);
			try
			{
				if( connectionBuffer.remaining() > dst.length )
				{
					connectionBuffer.put(dst);
					haveDataInIndividualBuffers = true;
				}
				else
				{
					String remoteClientAddress = chan.toString();
					log.error( "Overflow while trying to write to the output buffer associated with address "+remoteClientAddress+".");
				}
			}catch(BufferOverflowException ex)
			{
				log.error( "Overflow while trying to write to an output buffer.");
			}
		}
		return dst.length;

	}

	private int manageSelector()
	{
		int activeSelectors = 0;
		int activeChannels = 0;
		try
		{
			activeSelectors = selector.selectNow();
			if( activeSelectors > 0 )
			{

				for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator.hasNext();)
				{
					SelectionKey selectionKey = iterator.next();
					iterator.remove();

					try
					{
						activeChannels += processSelectionKey(selectionKey);
					}
					catch (IOException ex)
					{
						log.error(ex);
						selectionKey.cancel();
					}
				}
			}
		}
		catch (IOException ex)
		{
			log.error(ex);
		}
		return activeChannels;
	}

	private int processSelectionKey(SelectionKey key) throws IOException
	{
		if (!key.isValid())
			return 0;

		int count = 0;
		if (key.isAcceptable())
		{
			ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
			SocketChannel socketChannel = serverSocketChannel.accept();
			if (socketChannel != null)
			{
				socketChannel.configureBlocking(false);
				socketChannel.register(selector, SelectionKey.OP_WRITE);
				connectionBuffers.put(socketChannel, ByteBuffer.allocate(CLIENT_BUFFER_SIZE));
				count++;
			}
		}
		if (key.isWritable())
		{
			SocketChannel channel = (SocketChannel) key.channel();
			ByteBuffer buf = connectionBuffers.get(channel);
			try
			{
				buf.flip();
				int numberOfBytesWritten = channel.write(buf);
				buf.compact();
				if (numberOfBytesWritten == -1)
				{
					// The channel is closed.
					connectionBuffers.remove(channel);
					channel.register(selector, 0);
					channel.close();
				}
				else
					count += numberOfBytesWritten;

				// Now we are going to see if all the individual buffers are empty
				boolean foundOne = false;
				for(SocketChannel chan : connectionBuffers.keySet())
				{
					ByteBuffer individualBuffer = connectionBuffers.get(chan);
					if( individualBuffer.position() > 0 )
					{
						foundOne = true;
						break;
					}
				}
				haveDataInIndividualBuffers = foundOne;
			}
			catch (Exception ex)
			{
				if( !ex.getMessage().contains("An existing connection was forcibly closed by the remote host"))
					log.error("Exception while writing to a socket.", ex);
				connectionBuffers.remove(channel);
				channel.register(selector, 0);
				channel.close();
			}
		}
		return count;
	}
	
	private void readProperties()
	{
		Property prop = getProperty( "host" );
		host = prop.getValueAsString();
		prop = getProperty( "port" );
		port = (Integer)prop.getValue();
		prop = getProperty( "mode" );
		String modeString  = prop.getValueAsString();
		clientConnectionTimeout = hasProperty("clientConnectionTimeout") ? (Integer) getProperty("clientConnectionTimeout").getValue() : 60;
		if( modeString != null && modeString.toUpperCase().trim().equals("CLIENT") )
			mode = CLIENT;
		else if( modeString != null && modeString.toUpperCase().trim().equals("SERVER") )
			mode = SERVER;
		else
		{
			log.error("Setting the TCP Transport to mode \""+modeString+"\" is not allowed. Must be SERVER or CLIENT.");
			setRunningState(RunningState.ERROR);
			return;
		}
	}
	
	/*private void applyProperties() throws IOException
	{
		if (mode == CLIENT)
		{
			attemptClientConnection();
		}
		else if (mode == SERVER)
		{
			try
			{
				selector = Selector.open();
				serverSocketChannel = ServerSocketChannel.open();
				serverSocketChannel.configureBlocking(false);
				serverSocketChannel.socket().bind(new InetSocketAddress(port));
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			}
			catch (IOException ex)
			{
				selector = null;
				throw ex;
			}
		}
		
	}*/
	
	private void applyProperties() throws IOException
	{
		if(mode==SERVER)
		{
			selector = Selector.open();
		}
	}

	@Override
	public void afterPropertiesSet()
	{
		try
		{
			readProperties();
			if( getRunningState() == RunningState.STARTED )
			{
				cleanup();
				applyProperties();
			}
		}catch( IOException ex )
		{
			errorMessage = ex.getMessage();
			log.error(errorMessage);
			setRunningState(RunningState.ERROR);
		}
	}

	public synchronized void cleanup()
	{
		if(receiving)
			receiving = false;
		if (mode == CLIENT)
		{
			if (socketChannel != null)
			{
				try
				{
					socketChannel.close();
				}
				catch (IOException ioe)
				{
					log.debug("Ignoring Exception", ioe);
				}
			}
		}
		else if (mode == SERVER)
		{
			for (SocketChannel chan : connectionBuffers.keySet())
			{
				try
				{
					if( chan.isOpen() )
						chan.close();
				}catch(IOException ioe)
				{
					log.debug("Exception while closing all sockets as part of the transport shutdown process.", ioe);
				}
			}
			try
			{
				if(serverSocketChannel != null && serverSocketChannel.isOpen())
					serverSocketChannel.close();
				if( selector != null && selector.isOpen() )
					selector.close();
			} catch (IOException e)
			{
				log.debug("Exception while trying to stop listening for client connections. " + e);
			}
		}
	}

	@Override
	public void stop()
	{
		super.stop();
		errorMessage = null;
		if( getRunningState() == RunningState.ERROR )
			setRunningState( RunningState.STOPPED );
	}

	@Override
	public String getStatusDetails()
	{
		return errorMessage;
	}

}
