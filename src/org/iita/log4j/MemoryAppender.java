/**
 * iita-common-web.struts Feb 12, 2011
 */
package org.iita.log4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author mobreza
 */
public class MemoryAppender extends AppenderSkeleton {
	public static final int MAX_LOGGED_EVENTS = 500;
	private Queue<LoggingEvent> events = new LinkedList<LoggingEvent>();
	private int maxEvents = 100;

	/**
	 * @see org.apache.log4j.Appender#close()
	 */
	@Override
	public void close() {
		this.events.clear();
	}

	/**
	 * @param maxEvents the maxEvents to set
	 */
	public void setMaxEvents(int maxEvents) {
		// maxevents limited to 500
		if (maxEvents > MAX_LOGGED_EVENTS)
			maxEvents = MAX_LOGGED_EVENTS;
		this.maxEvents = maxEvents;

		synchronized (this.events) {
			while (this.events.size() >= this.maxEvents)
				this.events.poll();
		}
	}

	/**
	 * @return the maxEvents
	 */
	public int getMaxEvents() {
		return this.maxEvents;
	}

	/**
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	@Override
	public boolean requiresLayout() {
		return true;
	}

	/**
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void append(LoggingEvent loggingEvent) {

		synchronized (this.events) {
			while (this.events.size() >= this.maxEvents)
				this.events.poll();

			this.events.add(loggingEvent);
		}
	}

	public List<LoggingEvent> getEventLog() {
		synchronized (this.events) {
			return new ArrayList<LoggingEvent>(this.events);
		}
	}

	/**
	 * Return events queue in reverse order
	 * 
	 * @return
	 */
	public List<LoggingEvent> getRecentEventLog() {
		ArrayList<LoggingEvent> copy = null;
		synchronized (this.events) {
			copy = new ArrayList<LoggingEvent>(this.events);
		}
		Collections.reverse(copy);
		return copy;
	}

	/**
	 * Remove all buffered events
	 */
	public void clearBuffer() {
		synchronized (this.events) {
			this.events.clear();
		}
	}
}
