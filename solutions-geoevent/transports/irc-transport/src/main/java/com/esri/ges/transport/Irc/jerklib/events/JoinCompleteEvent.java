package com.esri.ges.transport.Irc.jerklib.events;

import com.esri.ges.transport.Irc.jerklib.Channel;

/**
 * Event that occurs when a join to a channel is complete
 *
 * @author mohadib
 */
public interface JoinCompleteEvent extends IRCEvent
{

    /**
     * getChannel() returns Channel object for event
     *
     * @return <code>Channel</code>
     * @see Channel
     */
    public Channel getChannel();
}
