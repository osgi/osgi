/*
 * Copyright (c) OSGi Alliance (2004, 2015). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.event.secure.tb2;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.event.secure.service.ReceiverService;

/**
 * A bundle that registers a ReceiverService service.
 *
 * @author $Id$
 */
public class Activator implements BundleActivator, ReceiverService, EventHandler {

	BundleContext									context;
	private ServiceRegistration<ReceiverService>	receiverServiceReg;
	private ServiceRegistration<EventHandler>		handlerServiceReg;

	private final LinkedList<Event>	lastEvents	= new LinkedList<Event>();
	private String[]				topics;

	public void start(BundleContext c) throws Exception {
		this.context = c;
		receiverServiceReg = context.registerService(ReceiverService.class, this, null);
	}

	public void stop(BundleContext c) throws Exception {
		receiverServiceReg.unregister();
		if (handlerServiceReg != null) {
			handlerServiceReg.unregister();
		}
	}

	/**
	 * Sets the array with event topics in which the event handler is
	 * interested.
	 * 
	 * @see ReceiverService#setTopics(java. lang.String[])
	 */
	public void setTopics(String... topics) {
		this.topics = topics;
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, (topics.length == 1) ? topics[0] : topics);
		if (handlerServiceReg == null) {
			handlerServiceReg = AccessController
					.doPrivileged(new PrivilegedAction<ServiceRegistration<EventHandler>>() {
						public ServiceRegistration<EventHandler> run() {
							return context.registerService(EventHandler.class, Activator.this, properties);
						}
					});
		}
		else {
			handlerServiceReg.setProperties(properties);
		}
	}

	/**
	 * Returns the array with all set event topics in which the event handler is
	 * interested.
	 * 
	 * @see ReceiverService#getTopics()
	 */
	public String[] getTopics() {
		return topics;
	}

	/**
	 * Called by the {@code EventAdmin} service to notify the listener of an
	 * event.
	 * 
	 * @param event The event that occurred.
	 */
	public void handleEvent(Event event) {
		synchronized (lastEvents) {
			lastEvents.addLast(event);
		}
	}

	/**
	 * Returns the last received event and then elements in the list with last
	 * events are removed.
	 * 
	 * @see ReceiverService#getLastReceivedEvent()
	 */
	public Event getLastReceivedEvent() {
		Event event = null;
		synchronized (lastEvents) {
			if (!lastEvents.isEmpty()) {
				event = lastEvents.removeLast();
				lastEvents.clear();
			}
		}
		return event;
	}

	/**
	 * Returns the last received events and then elements in the list with last
	 * events are removed.
	 * 
	 * @see ReceiverService#getLastReceivedEvents()
	 */
	public List<Event> getLastReceivedEvents() {
		List<Event> events = new ArrayList<Event>();
		synchronized (lastEvents) {
			if (!lastEvents.isEmpty()) {
				events.addAll(lastEvents);
				lastEvents.clear();
			}
		}
		return events;
	}
}
