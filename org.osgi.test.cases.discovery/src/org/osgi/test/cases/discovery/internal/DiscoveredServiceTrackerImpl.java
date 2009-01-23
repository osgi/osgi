/*
 * Copyright (c) 2008 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.discovery.internal;

import org.osgi.service.discovery.DiscoveredServiceNotification;
import org.osgi.service.discovery.DiscoveredServiceTracker;
import org.osgi.test.support.compatibility.Semaphore;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @since 1.0.0
 */
public class DiscoveredServiceTrackerImpl implements DiscoveredServiceTracker {
	private DiscoveredServiceNotification event;
	private Semaphore semaphore = new Semaphore();

	/**
	 * @see org.osgi.service.discovery.DiscoveredServiceTracker#serviceChanged(org.osgi.service.discovery.DiscoveredServiceNotification)
	 */
	public void serviceChanged(DiscoveredServiceNotification n) {
		event = n;
		semaphore.signal();
	}

	public DiscoveredServiceNotification waitForEvent(long timeout) throws InterruptedException {
		semaphore.waitForSignal(timeout);
		DiscoveredServiceNotification rv = event;
		event = null;
		return rv;
	}
}
