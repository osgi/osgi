/*
 * $Header$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */

package org.osgi.impl.service.event.mapper;

import org.osgi.framework.*;
import org.osgi.service.event.EventChannel;
import org.osgi.service.log.*;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Main class for redeliver special events like FrameworkEvents via
 * EventChannel.
 * 
 * 
 * @version $Revision$
 */
public class EventRedeliverer implements FrameworkListener, BundleListener,
		ServiceListener, LogListener, LogReaderServiceListener {

	private ServiceTracker			channelTracker;
	private LogReaderServiceTracker	logTracker;
	private LogReaderService		reader;
	private final static boolean	DEBUG	= true;

	public EventRedeliverer(BundleContext bc) {
		channelTracker = new ServiceTracker(bc, EventChannel.class.getName(),
				null);
		channelTracker.open();
		logTracker = new LogReaderServiceTracker(bc, this);
		logTracker.open();
		bc.addFrameworkListener(this);
		bc.addBundleListener(this);
		bc.addServiceListener(this);
		reader = (LogReaderService) logTracker.getService();
		if (reader != null) {
			reader.addLogListener(this);
		}
	}

	public void close() {
		if (logTracker != null) {
			logTracker.close();
			logTracker = null;
		}
		if (channelTracker != null) {
			channelTracker.close();
			channelTracker = null;
		}
		reader = null;
	}

	private EventChannel getChannel() {
		return (EventChannel) channelTracker.getService();
	}

	/**
	 * @param event
	 * @see org.osgi.framework.FrameworkListener#frameworkEvent(org.osgi.framework.FrameworkEvent)
	 */
	public void frameworkEvent(FrameworkEvent event) {
		EventChannel channel = getChannel();
		if (channel != null) {
			(new FrameworkEventAdapter(event, channel)).redeliver();
		}
		else {
			printNoEventChannelError();
		}
	}

	private void printNoEventChannelError() {
		if (DEBUG) {
			System.out.println(this.getClass().getName()
					+ ": Cannot find the EventChannel.");
		}
	}

	/**
	 * @param event
	 * @see org.osgi.framework.BundleListener#bundleChanged(org.osgi.framework.BundleEvent)
	 */
	public void bundleChanged(BundleEvent event) {
		EventChannel channel = getChannel();
		if (channel != null) {
			(new BundleEventAdapter(event, channel)).redeliver();
		}
		else {
			printNoEventChannelError();
		}
	}

	/**
	 * @param event
	 * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
	 */
	public void serviceChanged(ServiceEvent event) {
		EventChannel channel = getChannel();
		if (channel != null) {
			(new ServiceEventAdapter(event, channel)).redeliver();
		}
		else {
			printNoEventChannelError();
		}
	}

	/**
	 * @param entry
	 * @see org.osgi.service.log.LogListener#logged(org.osgi.service.log.LogEntry)
	 */
	public void logged(LogEntry entry) {
		EventChannel channel = getChannel();
		if (channel != null) {
			(new LogEntryAdapter(entry, channel)).redeliver();
		}
		else {
			printNoEventChannelError();
		}
	}

	/**
	 * @param reference
	 * @param service
	 * @see org.osgi.impl.service.event.mapper.LogReaderServiceListener#logReaderServiceModified(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void logReaderServiceModified(ServiceReference reference,
			Object service) {
		if (reader != null) {
			return;
		}
		reader = (LogReaderService) service;
		reader.addLogListener(this);
	}

	/**
	 * @param reference
	 * @param service
	 * @see org.osgi.impl.service.event.mapper.LogReaderServiceListener#logReaderServiceRemoved(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void logReaderServiceRemoved(ServiceReference reference,
			Object service) {
		if (reader == service) {
			reader = null;
		}
		// ungetService() will be called after returning to
		// LogReaderServiceTracker's removedService() method.
	}
}
