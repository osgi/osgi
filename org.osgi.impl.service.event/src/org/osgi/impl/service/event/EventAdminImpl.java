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
package org.osgi.impl.service.event;

import org.eclipse.osgi.framework.eventmgr.*;
import org.osgi.framework.*;
import org.osgi.service.event.*;

/**
 * Implementation of org.osgi.service.event.EventAdmin. EventAdminImpl uses
 * LogProxy and org.eclipse.osgi.framework.eventmgr.EventManager. It is assumeed
 * org.eclipse.osgi.framework.eventmgr package is exported by some other bundle.
 */
public class EventAdminImpl implements EventAdmin {
	/**
	 * Class EventDispatcher is used to dispatch events to EventHandlers.
	 * Dispatch events only when the receiver bundle is ACTIVE, and the receiver
	 * service is registered.
	 */
	private class EventAdminDispatcher implements EventDispatcher {
		/**
		 * 
		 * Dispatches Event to EventHandlers
		 * 
		 * @param eventListener
		 * @param listenerObject
		 * @param eventAction
		 * @param eventObject
		 * @see org.eclipse.osgi.framework.eventmgr.EventDispatcher#dispatchEvent(java.lang.Object,
		 *      java.lang.Object, int, java.lang.Object)
		 */
		public void dispatchEvent(Object eventListener, Object listenerObject,
				int eventAction, Object eventObject) {
			ServiceReference ref = null;
			ref = (ServiceReference) eventListener;
			Bundle bundle = ref.getBundle();
			if ((bundle == null) || (bundle.getState() != Bundle.ACTIVE)) {
				// the receiver service or bundle being not active, no need
				// to dispatch
				return;
			}
			Object serviceObject = bc.getService(ref);
			if ((serviceObject == null)
					|| (!(serviceObject instanceof EventHandler))) {
				// the service being unregistered or invalid, no need to
				// dispatch
				return;
			}
			try {
				((EventHandler) serviceObject).handleEvent((Event) eventObject);
			}
			catch (Throwable t) {
				// log/handle any Throwable thrown by the listener
				printError("Exception thrown while dispatching an Event to an EventHandler);");
				printError(" 	Event = " + eventObject);
				printError("  	EventHandler = " + serviceObject, t);
			}
		}
	}

	private BundleContext		bc;
	private EventManager		eventManager;
	private static final char	TOPIC_SEPARATOR	= '/';

	/**
	 * Constructer for EventAdminImpl.
	 * 
	 * @param bc BundleContext
	 */
	protected EventAdminImpl(BundleContext bc) {
		super();
		this.bc = bc;
		eventManager = new EventManager(
				"EventAdmin Async Event Dispatcher Thread");
	}

	/**
	 * This method should be called when stopping EventAdmin service
	 */
	void stop() {
		if (eventManager != null) {
			eventManager.close();
			eventManager = null;
		}
		bc = null;
	}

	/**
	 * @param event
	 * @see org.osgi.service.event.EventAdmin#postEvent(org.osgi.service.event.Event)
	 */
	public void postEvent(Event event) {
		dispatchEvent(event, true);
	}

	/**
	 * @param event
	 * @see org.osgi.service.event.EventAdmin#sendEvent(org.osgi.service.event.Event)
	 */
	public void sendEvent(Event event) {
		dispatchEvent(event, false);
	}

	/**
	 * Internal main method for sendEvent() and postEvent(). Dispatching an
	 * event to EventHandler. All exceptions are logged except when dealing with
	 * LogEntry.
	 * 
	 * @param event to be delivered
	 * @param isAsync must be set to true for syncronous event delivery, false
	 *        for asyncronous delivery.
	 */
	protected void dispatchEvent(Event event, boolean isAsync) {
		try {
			if (eventManager == null) {
				// EventAdmin is stopped
				return;
			}
			if (event == null) {
				printError("Null event is passed to EventAdmin. Ignored.");
				return;
			}
			if (!isCallerPermittedTopicPermission(event.getTopic())) {
				printError("Caller bundle doesn't have TopicPermission for topic="
						+ event.getTopic());
				return;
			}
			ServiceReference[] refsForEventHandler = getAllEventHandlers();
			if (refsForEventHandler == null) {
				//No EventHandler exists. Do nothing.
				return;
			}
			EventListeners listeners = retrieveMatchedAndPermittedListeners(
					event, refsForEventHandler);
			if (listeners == null) {
				//No permitted EventHandler exists. Do nothing.
				return;
			}
			// Create the listener queue for this event delivery
			ListenerQueue listenerQueue = new ListenerQueue(eventManager);
			// Add the listeners to the queue and associate them with the event
			// dispatcher
			listenerQueue.queueListeners(listeners, new EventAdminDispatcher());
			// Deliver the event to the listeners.
			if (isAsync) {
				listenerQueue.dispatchEventAsynchronous(0, event);
			}
			else {
				listenerQueue.dispatchEventSynchronous(0, event);
			}
			// Remove the listener from the listener list
			listeners.removeAllListeners();
		}
		catch (Throwable t) {
			printError("Exception thrown while dispatching an event, event = "
					+ event, t);
		}
	}

	/**
	 * Filtering EventHandlers, represented by the param references, to listners
	 * that have the same topic as the event.getTopic(), and whose Filter
	 * property match the event when the property "Filter" is set. This method
	 * also filters out listners that do not have appropriate TopicPermission.
	 * Results are wrapped and put into EventListeners.
	 * 
	 * @param event This object is used to filter EventHandlers
	 * @param references EventHandlers to be filtered
	 * @return EventListeners which contains filtered EventHandlers.
	 */
	protected EventListeners retrieveMatchedAndPermittedListeners(Event event,
			ServiceReference[] references) {
		EventListeners listeners = null;
		for (int i = 0; i < references.length; i++) {
			ServiceReference ref = references[i];
			if (isEventMatchingListener(event, ref)
					&& isHandlerGrantedTopicPermission(ref, event.getTopic())) {
				if (listeners == null)
					listeners = new EventListeners();
				listeners.addListener(ref, null);
			}
		}
		return listeners;
	}

	/**
	 * Checks if the EventHandler represented by the parameter ref has right
	 * SUBSCRIBE TopicPermission.
	 * 
	 * @param ref This object represents ServiceReference which implement
	 *        EventHandler
	 * @param topic This string represents TopicPermission.
	 * @return
	 */
	protected boolean isHandlerGrantedTopicPermission(ServiceReference ref,
			String topic) {
		Bundle bundle = ref.getBundle();
		return bundle.hasPermission(new TopicPermission(topic,
				TopicPermission.SUBSCRIBE));
	}

	/**
	 * Checks if the caller bundle has right PUBLISH TopicPermision.
	 * 
	 * @param topic
	 * @return true if it has the right permission, false otherwise.
	 */
	protected boolean isCallerPermittedTopicPermission(String topic) {
		SecurityManager sm = System.getSecurityManager();
		if (topic == null)
			return false;
		if (sm != null) {
			TopicPermission topicPermission = new TopicPermission(topic,
					TopicPermission.PUBLISH);
			try {
				sm.checkPermission(topicPermission);
			}
			catch (SecurityException e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if a topic filter string matches the target topic string.
	 * 
	 * @param topicProperty A topic filter like "company/product/*"
	 * @param topic Target topic to be checked against like
	 *        "company/product/topicA"
	 * @return true if topicProperty matches topic, false if otherwise.
	 */
	protected boolean topicPropertyIncludes(String topicProperty, String topic) {
		if (topicProperty.startsWith("*")) {
			return true;
		}
		if (topicProperty.equals(topic)) {
			return true;
		}
		int index = topicProperty.indexOf(TOPIC_SEPARATOR);
		int index2 = topic.indexOf(TOPIC_SEPARATOR);
		if (index == -1)
			return false;
		if (index2 == -1)
			return false;
		if (index == 0) {
			printError("topicProperty '" + topicProperty + "'starts with '"
					+ TOPIC_SEPARATOR + "'. Ignored.");
			return false;
		}
		if (index2 == 0) {
			printError("topic '" + topic + "'starts with '" + TOPIC_SEPARATOR
					+ "'. Ignored.");
			return false;
		}
		if (index == topicProperty.length() - 1) {
			printError("topicProperty '" + topicProperty + "'ends with '"
					+ TOPIC_SEPARATOR + "'. Ignored.");
			return false;
		}
		if (index2 == topic.length() - 1) {
			printError("topic '" + topic + "'ends with '" + TOPIC_SEPARATOR
					+ "'. Ignored.");
			return false;
		}
		String str = topicProperty.substring(0, index);
		String str2 = topic.substring(0, index2);
		if (str.equals(str2)) { // if first names of topic category match
			String str3 = topicProperty.substring(index + 1);
			String str4 = topic.substring(index2 + 1);
			return topicPropertyIncludes(str3, str4); // recursive call to test
			// the rest of the names
		}
		else {
			return false;
		}
	}

	/**
	 * Checks if a EventHandler's SUBSCRIBE topics contains the target event's
	 * topic. Also checks if the listener's EVENT_FILTER matches the event if
	 * the filter property of the listener is set.
	 * 
	 * @param event The event to be checked against
	 * @param ref This ServiceReference represents EventHandler to be checked
	 * @return true if the listener matches the event
	 */
	protected boolean isEventMatchingListener(Event event, ServiceReference ref) {
		String eventTopic = event.getTopic();
		Object o = ref.getProperty(EventConstants.EVENT_TOPIC);
		if (o == null) {
			// means no topics, no need to investigate further
			return false;
		}
		if (!(o instanceof String[])) {
			// EVENT_TOPIC property must be String[], ignored.
			return false;
		}
		String listenerTopics[] = (String[]) o;
		boolean found = false;
		for (int i = 0; i < listenerTopics.length; i++) {
			if (topicPropertyIncludes(listenerTopics[i], eventTopic)) {
				found = true;
				break;
			}
		}
		if (!found) {
			return false;
		}
		Object o2 = ref.getProperty(EventConstants.EVENT_FILTER);
		if ((o2 == null) || (!(o2 instanceof String))) {
			// means no need to investigate further
			return true;
		}
		String filterString = (String) o2;
		try {
			Filter filter = bc.createFilter(filterString);
			return (event.matches(filter));
		}
		catch (InvalidSyntaxException e) {
			printError("exception thrown in BundleContext.createfilter(\""
					+ filterString + "\". Ignored.", e);
			return false;
		}
	}

	/**
	 * Returns all the EventHandler's ServiceReferences that are currently
	 * registered.
	 * 
	 * @return all the EventHandler's ServiceReferences that are currently
	 *         registered.
	 */
	protected ServiceReference[] getAllEventHandlers() {
		ServiceReference[] references = null;
		try {
			// find all the eventHandlers
			references = bc.getServiceReferences(EventHandler.class.getName(),
					null);
		}
		catch (InvalidSyntaxException e) {
			// exception never be thrown, since filter is null in
			// getServiceReferences() call
		}
		return references;
	}

	private void printError(String msg, Throwable t) {
		System.out.println("EventAdmin: " + msg);
		if (t != null) {
			System.out.println("EventAdmin: exception = " + t);
			t.printStackTrace(System.out);
		}
	}

	private void printError(String msg) {
		printError(msg, null);
	}
}