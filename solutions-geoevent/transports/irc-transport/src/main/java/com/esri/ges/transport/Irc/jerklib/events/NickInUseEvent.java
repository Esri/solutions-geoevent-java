package com.esri.ges.transport.Irc.jerklib.events;

/**
 * NickInUseEvent is fired when com.esri.ges.transport.Irc.jerklib is trying to use a nick
 * that is in use on a given server.
 *
 * @author mohadib
 */
public interface NickInUseEvent extends IRCEvent
{

    /**
     * returns nick that was in use
     *
     * @return nick that was in use.
     */
    public String getInUseNick();
}
