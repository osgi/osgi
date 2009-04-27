/*
 * Copyright (c) 2008 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.discovery.internal;

import java.util.Collection;
import java.util.LinkedList;

import org.osgi.service.discovery.DiscoveredServiceNotification;
import org.osgi.service.discovery.DiscoveredServiceTracker;
import org.osgi.test.support.compatibility.Semaphore;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @since 1.0.0
 */
public class DiscoveredServiceTrackerImpl implements DiscoveredServiceTracker {
	private Collection events;
	private Semaphore semaphore = new Semaphore();

	/**
	 * 
	 */
	public DiscoveredServiceTrackerImpl() {
		events = new LinkedList();
	}

	/**
	 * @see org.osgi.service.discovery.DiscoveredServiceTracker#serviceChanged(org.osgi.service.discovery.DiscoveredServiceNotification)
	 */
	public synchronized void serviceChanged(DiscoveredServiceNotification n) {
		this.events.add(n);
		semaphore.signal();
	}

	/**
	 * Block on the semaphore for the given amount of time (in millisec) and return the notification.
	 *  
	 * @param timeout Timeout to maximal wait for in milliseconds
	 * @throws InterruptedException if interrupted while blocking on semaphore
	 */
	public void waitForEvent(long timeout) throws InterruptedException {
		semaphore.waitForSignal(timeout);
	}
	
	/**
	 * Return the list of received events and create a new list.
	 * @return The collection with the events received since the last call to getEvents().
	 */
	public synchronized Collection getEvents() {
		Collection c = events;
		events = new LinkedList();
		return c;
	}
}
