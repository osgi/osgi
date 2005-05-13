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
 * The Event Admin service. Bundles wishing to publish events must obtain the
 * Event Admin service and call one of the event delivery methods.
 * 
 * @version $Revision$
 */
public interface EventAdmin {
	/**
	 * Initiate asynchronous delivery of an event. This method returns to
	 * the caller before delivery of the event is completed.
	 * 
	 * @param event The event to send to all listeners which subscribe
	 *        to the topic of the event.
	 * 
	 * @exception SecurityException If the caller does not have
	 *            <code>TopicPermission[topic,PUBLISH]</code> for the topic
	 *            specified in the event.
	 */
	void postEvent(Event event);

	/**
	 * Initiate synchronous delivery of an event. This method does not
	 * return to the caller until delivery of the event is completed.
	 * 
	 * @param event The event to send to all listeners which subscribe
	 *        to the topic of the event.
	 * 
	 * @exception SecurityException If the caller does not have
	 *            <code>TopicPermission[topic,PUBLISH]</code> for the topic
	 *            specified in the event.
	 */
	void sendEvent(Event event);
}