/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.event;

/**
 * Listener for Events.
 * 
 * <p>
 * <code>EventHandler</code> objects are registered with the Framework service
 * registry and are notified with an <code>Event</code> object when an
 * event is broadcast.
 * <p>
 * <code>EventHandler</code> objects can inspect the received
 * <code>Event</code> object to determine its topic and properties.
 * 
 * <p>
 * <code>EventHandler</code> objects must be registered with a service
 * property {@link EventConstants#EVENT_TOPIC} whose value is the list of
 * topics in which the event handler is interesed.
 * <p>
 * For example:
 * 
 * <pre>
 * String[] topics = new String[] {EventConstants.EVENT_TOPIC, &quot;com/isv/*&quot;};
 * Hashtable ht = new Hashtable();
 * ht.put(EVENT_TOPIC, topics);
 * context.registerService(EventHandler.class.getName(), this, ht);
 * </pre>
 * 
 * <p>
 * Security Considerations. Bundles wishing to monitor <code>Event</code>
 * objects will require <code>ServicePermission[EventHandler,REGISTER]</code>
 * to register an <code>EventHandler</code> service. The bundle must also have
 * <code>TopicPermission[topic,SUBSCRIBE]</code> for the topic specified in the
 * event in order to receive the event.
 * 
 * @see Event
 * 
 * @version $Revision$
 */
public interface EventHandler {
	/**
	 * Called by the {@link EventAdmin} service to notify the listener of an event.
	 * 
	 * @param event The event that occurred.
	 */
	void handleEvent(Event event);
}