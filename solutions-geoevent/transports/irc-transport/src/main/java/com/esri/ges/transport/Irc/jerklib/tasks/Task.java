package com.esri.ges.transport.Irc.jerklib.tasks;

import com.esri.ges.transport.Irc.jerklib.Session;
import com.esri.ges.transport.Irc.jerklib.events.IRCEvent.Type;
import com.esri.ges.transport.Irc.jerklib.listeners.IRCEventListener;

/**
 *Task is a job that can be ran by the Session when 
 *certain types of events are received. This class is
 *very much like IRCEventListener , but it can be
 *associated with Types of events. See Session's onEvent
 *methods for details.
 *
 *<a href="http://com.esri.ges.transport.Irc.jerklib.wikia.com/wiki/Tasks">Task Tutorial</a>
 * 
 * @see Session#onEvent(Task)
 * @see Session#onEvent(com.esri.ges.transport.Irc.jerklib.tasks.Task, com.esri.ges.transport.Irc.jerklib.events.IRCEvent.Type...)
 * @see Type
 * @author mohadib
 *
 */
public interface Task extends IRCEventListener
{
    /**
     * Gets the name of a task
     * @return name of task
     */
    public String getName();

    /**
     * Cancel a task. This task will not run again. If the task is currently running
     * it will finish then not run again.
     */
    public void cancel();

    /**
     * @return true if task is canceled , else false
     */
    public boolean isCanceled();
}
