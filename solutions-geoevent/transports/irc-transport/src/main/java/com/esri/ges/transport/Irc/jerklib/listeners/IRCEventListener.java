
package com.esri.ges.transport.Irc.jerklib.listeners;

import com.esri.ges.transport.Irc.jerklib.events.IRCEvent;

/**
 * IRCEventListener - Listener to receive IRCEvents
 *
 * @author mohadib
 */
public interface IRCEventListener
{

    /**
     * recieveEvent() - receive IRCEvents
     *
     * @param e <code>IRCEvent<code> the event
     */
    public void receiveEvent(IRCEvent e);
}
