package com.esri.ges.transport.Irc.jerklib.events.impl;

import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.NumericErrorEvent;

/**
 * @author mohadib
 * @see NumericErrorEvent
 *
 */
public class NumericEventImpl implements NumericErrorEvent
{
	private final String errMsg, rawEventData;
	private final Session session;
	private final int numeric;

	public NumericEventImpl(String errMsg, String rawEventData, int numeric, Session session)
	{
		super();
		this.errMsg = errMsg;
		this.rawEventData = rawEventData;
		this.session = session;
		this.numeric = numeric;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.NumericErrorEvent#getErrorMsg()
	 */
	public String getErrorMsg()
	{
		return errMsg;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.NumericErrorEvent#getNumeric()
	 */
	public int getNumeric()
	{
		return numeric;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.ErrorEvent#getErrorType()
	 */
	public ErrorType getErrorType()
	{
		return ErrorType.NUMERIC_ERROR;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getRawEventData()
	 */
	public String getRawEventData()
	{
		return rawEventData;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getSession()
	 */
	public Session getSession()
	{
		return session;
	}

	/* (non-Javadoc)
	 * @see com.esri.ges.transport.Irc.jerklib.events.IRCEvent#getType()
	 */
	public Type getType()
	{
		return Type.ERROR;
	}

}
