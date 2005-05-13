/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2002, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.wireadmin;

/**
 * Listener for Wire Admin Events.
 * 
 * <p>
 * <code>WireAdminListener</code> objects are registered with the Framework
 * service registry and are notified with a <code>WireAdminEvent</code> object
 * when an event is broadcast.
 * <p>
 * <code>WireAdminListener</code> objects can inspect the received
 * <code>WireAdminEvent</code> object to determine its type, the <code>Wire</code>
 * object with which it is associated, and the Wire Admin service that
 * broadcasts the event.
 * 
 * <p>
 * <code>WireAdminListener</code> objects must be registered with a service
 * property {@link WireConstants#WIREADMIN_EVENTS}whose value is a bitwise OR
 * of all the event types the listener is interested in receiving.
 * <p>
 * For example:
 * 
 * <pre>
 * Integer mask = new Integer(WIRE_TRACE | WIRE_CONNECTED | WIRE_DISCONNECTED);
 * Hashtable ht = new Hashtable();
 * ht.put(WIREADMIN_EVENTS, mask);
 * context.registerService(WireAdminListener.class.getName(), this, ht);
 * </pre>
 * 
 * If a <code>WireAdminListener</code> object is registered without a service
 * property {@link WireConstants#WIREADMIN_EVENTS}, then the
 * <code>WireAdminListener</code> will receive no events.
 * 
 * <p>
 * Security Considerations. Bundles wishing to monitor <code>WireAdminEvent</code>
 * objects will require <code>ServicePermission[REGISTER,WireAdminListener]</code>
 * to register a <code>WireAdminListener</code> service. Since
 * <code>WireAdminEvent</code> objects contain <code>Wire</code> objects, care must
 * be taken in assigning permission to register a <code>WireAdminListener</code>
 * service.
 * 
 * @see WireAdminEvent
 * 
 * @version $Revision$
 */
public interface WireAdminListener {
	/**
	 * Receives notification of a broadcast <code>WireAdminEvent</code> object.
	 * 
	 * The event object will be of an event type specified in this
	 * <code>WireAdminListener</code> service's
	 * {@link WireConstants#WIREADMIN_EVENTS}service property.
	 * 
	 * @param event The <code>WireAdminEvent</code> object.
	 */
	void wireAdminEvent(WireAdminEvent event);
}
