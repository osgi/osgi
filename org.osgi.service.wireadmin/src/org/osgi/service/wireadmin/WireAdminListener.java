/*
 * $Header$
 *
 * Copyright (c) The Open Services Gateway Initiative (2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
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
 * <p><tt>WireAdminListener</tt> objects are registered with the Framework service registry and
 * are notified with a <tt>WireAdminEvent</tt> object when an event
 * is broadcast.
 * <p><tt>WireAdminListener</tt> objects can inspect the received <tt>WireAdminEvent</tt> object to
 * determine its type, the <tt>Wire</tt> object with which it is associated, and the Wire Admin service that
 * broadcasts the event.
 *
 * <p><tt>WireAdminListener</tt> objects must be registered with a
 * service property {@link WireConstants#WIREADMIN_EVENTS}
 * whose value is a bitwise OR of all the event types the listener
 * is interested in receiving.
 * <p>For example:
 * <pre>
 *  Integer mask = new Integer( WIRE_TRACE
 *   | WIRE_CONNECTED
 *   | WIRE_DISCONNECTED );
 *  Hashtable ht = new Hashtable();
 *  ht.put( WIREADMIN_EVENTS, mask );
 *  context.registerService(
 *     WireAdminListener.class.getName(), this, ht );
 * </pre>
 * If a <tt>WireAdminListener</tt> object is registered without a
 * service property {@link WireConstants#WIREADMIN_EVENTS},
 * then the <tt>WireAdminListener</tt> will receive no events.
 *
 * <p>Security Considerations. Bundles wishing to monitor <tt>WireAdminEvent</tt> objects
 * will require <tt>ServicePermission[REGISTER,WireAdminListener]</tt> to register a <tt>WireAdminListener</tt> service.
 * Since <tt>WireAdminEvent</tt> objects contain <tt>Wire</tt> objects, care must be taken
 * in assigning permission to register a <tt>WireAdminListener</tt> service.
 *
 * @see WireAdminEvent
 *
 * @version $Revision$
 */

public interface WireAdminListener
{
    /**
     * Receives notification of a broadcast <tt>WireAdminEvent</tt> object.
	 *
     * The event object will be of an event type specified in this
     * <tt>WireAdminListener</tt> service's {@link WireConstants#WIREADMIN_EVENTS}
     * service property.
     *
     * @param event The <tt>WireAdminEvent</tt> object.
     */
    void wireAdminEvent(WireAdminEvent event);
}


