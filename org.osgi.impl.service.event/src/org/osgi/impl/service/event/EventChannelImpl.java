/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.event;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.event.*;
import org.osgi.service.log.LogService;

class AsynchronQueueItem {
	private ChannelEvent		event;
	private ServiceReference[]	references;

	public AsynchronQueueItem(ChannelEvent event, ServiceReference[] references) {
		this.event = event;
		this.references = references;
	}

	public ChannelEvent getEvent() {
		return event;
	}

	public ServiceReference[] getServiceReferences() {
		return references;
	}
}
/**
 * The realization of the EventChannel class
 */

public class EventChannelImpl implements EventChannel, FrameworkListener,
		BundleListener, ServiceListener, Runnable {
	private BundleContext	bc;
	private LinkedList		asynchronEventQueue;
	private boolean			stopped;
	private Thread			asynchronThread;
	public final String		FRAMEWORK_EVENT	= "Framework Event";
	public final String		BUNDLE_EVENT	= "Bundle Event";
	public final String		SERVICE_EVENT	= "Service Event";

	public EventChannelImpl(BundleContext bc) {
		this.bc = bc;
		this.bc.addFrameworkListener(this);
		this.bc.addBundleListener(this);
		this.bc.addServiceListener(this);
		asynchronEventQueue = new LinkedList();
		asynchronThread = new Thread(this);
		asynchronThread.start();
		stopped = false;
	}

	public void sendEvent(ChannelEvent event) {
		try {
			ServiceReference[] references = bc.getServiceReferences(
					"org.osgi.service.event.ChannelListener", null);
			sendEvent(event, references);
		}
		catch (Exception e) {
			log(LogService.LOG_ERROR,
					"Exception occurred at sending the event!", e);
		}
	}

	public void sendEvent(ChannelEvent event, ServiceReference[] references) {
		if (references == null)
			return;
		for (int i = 0; i != references.length; i++) {
			String topic = (String) references[i].getProperty("topic");
			String filterString = (String) references[i].getProperty("filter");
			try {
				if (topic != null) {
					Filter topicFilter = bc.createFilter("(topic=" + topic
							+ ")");
					if (!event.matches(topicFilter))
						continue;
				}
				if (filterString != null) {
					Filter filter = bc.createFilter(filterString);
					if (!event.matches(filter))
						continue;
				}
				Bundle bundle = references[i].getBundle();
				if (bundle != null && bundle.getState() == Bundle.ACTIVE) {
					ChannelListener listener = (ChannelListener) bc
							.getService(references[i]);
					listener.channelEvent(event);
					bc.ungetService(references[i]);
				}
			}
			catch (InvalidSyntaxException e) {
				continue;
			}
			catch (Exception e) {
				log(LogService.LOG_ERROR,
						"Exception occurred at sending the event!", e);
			}
		}
	}

	public synchronized void postEvent(ChannelEvent event) {
		try {
			if (event != null) {
				ServiceReference[] references = bc.getServiceReferences(
						"org.osgi.service.event.ChannelListener", null);
				asynchronEventQueue.addLast(new AsynchronQueueItem(event,
						references));
			}
			notify();
		}
		catch (Exception e) {
			log(LogService.LOG_ERROR,
					"Exception occurred at sending the event!", e);
		}
	}

	public synchronized Object getEvent() throws InterruptedException {
		while (asynchronEventQueue.isEmpty()) {
			if (stopped)
				return null;
			wait();
		}
		return asynchronEventQueue.removeFirst();
	}

	public void frameworkEvent(FrameworkEvent event) {
		Hashtable props = new Hashtable();
		props.put("bundle.id", new Long(event.getBundle().getBundleId()));
		props.put("bundle.location", event.getBundle().getLocation());
		//  -------------- OSGI R4, OSGI R4, OSGI R4, OSGI R4, OSGI R4, OSGI R4
		// --------------------------
		//  props.put( "bundle.symbolicName", );
		//  -------------- OSGI R4, OSGI R4, OSGI R4, OSGI R4, OSGI R4, OSGI R4
		// --------------------------
		props.put("bundle.object", event.getBundle());
		if (event.getThrowable() != null) {
			props.put("exception.class", event.getThrowable().getClass()
					.getName());
			props.put("exception.message", event.getThrowable().getMessage());
			props.put("exception.object", event.getThrowable());
		}
		props.put("event.object", event);
		try {
			ChannelEvent channelEvent = new ChannelEvent(FRAMEWORK_EVENT, props);
			sendEvent(channelEvent);
		}
		catch (Exception e) {
		}
	}

	public void bundleChanged(BundleEvent event) {
		Hashtable props = new Hashtable();
		props.put("bundle.id", new Long(event.getBundle().getBundleId()));
		props.put("bundle.location", event.getBundle().getLocation());
		//  -------------- OSGI R4, OSGI R4, OSGI R4, OSGI R4, OSGI R4, OSGI R4
		// --------------------------
		//  props.put( "bundle.symbolicName", );
		//  -------------- OSGI R4, OSGI R4, OSGI R4, OSGI R4, OSGI R4, OSGI R4
		// --------------------------
		props.put("bundle.object", event.getBundle());
		props.put("event.object", event);
		try {
			ChannelEvent channelEvent = new ChannelEvent(BUNDLE_EVENT, props);
			sendEvent(channelEvent);
		}
		catch (Exception e) {
		}
	}

	public void serviceChanged(ServiceEvent event) {
		Hashtable props = new Hashtable();
		//  -------------- OSGI R4, OSGI R4, OSGI R4, OSGI R4, OSGI R4, OSGI R4
		// --------------------------
		//  props.put( "service.id", ) );
		//  props.put( "service.pid", ) );
		//  props.put( "object.class", ) );
		//  -------------- OSGI R4, OSGI R4, OSGI R4, OSGI R4, OSGI R4, OSGI R4
		// --------------------------
		props.put("event.object", event);
		try {
			ChannelEvent channelEvent = new ChannelEvent(SERVICE_EVENT, props);
			sendEvent(channelEvent);
		}
		catch (Exception e) {
		}
	}

	public void run() {
		while (!stopped) {
			try {
				AsynchronQueueItem item = (AsynchronQueueItem) getEvent();
				if (item != null)
					sendEvent(item.getEvent(), item.getServiceReferences());
			}
			catch (InterruptedException e) {
			}
		}
	}

	public void stop() {
		stopped = true;
		postEvent(null);
		try {
			asynchronThread.join();
		}
		catch (InterruptedException e) {
		};
		bc = null;
	}

	protected boolean log(int severity, String message, Throwable throwable) {
		ServiceReference serviceRef = bc
				.getServiceReference("org.osgi.service.log.LogService");
		if (serviceRef != null) {
			LogService logService = (LogService) bc.getService(serviceRef);
			if (logService != null) {
				try {
					logService.log(severity, message, throwable);
					return true;
				}
				finally {
					bc.ungetService(serviceRef);
				}
			}
		}
		System.out.println("Serverity:" + severity + " Message:" + message
				+ " Throwable:" + throwable);
		return false;
	}
}
