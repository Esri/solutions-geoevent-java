package com.esri.ges.transport.Irc.jerklib.events;


import java.util.Date;

import com.esri.ges.transport.Irc.jerklib.Channel;


/**
 * 
 * Event fired when topic is received
 * @author mohadib
 * @see Channel
 */
public interface TopicEvent extends IRCEvent
{
    /**
     * Gets the topic
     *
     * @return the topic
     */
    public String getTopic();

    /**
     * Gets who set the topic
     *
     * @return topic setter
     */
    public String getSetBy();

    /**
     * Gets when topic was set
     *
     * @return when
     */
    public Date getSetWhen();

    /**
     * Gets Channel
     *
     * @return Channel
     * @see Channel
     */
    public Channel getChannel();

}
