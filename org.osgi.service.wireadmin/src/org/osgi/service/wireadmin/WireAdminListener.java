/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
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
