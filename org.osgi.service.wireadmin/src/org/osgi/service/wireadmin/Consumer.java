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
 * Data Consumer, a service that can receive udpated values from {@link Producer} services.
 *
 * <p>Service objects registered under the <tt>Consumer</tt> interface are expected to consume
 * values from a Producer service via a <tt>Wire</tt> object.
 * A Consumer service may poll the Producer service by calling the {@link Wire#poll} method.
 * The Consumer service will also receive an updated value when called at it's {@link #updated}
 * method.
 * The Producer service should have coerced the value to be an instance of one of the types
 * specified by the {@link Wire#getFlavors} method, or one of their subclasses.
 *
 * <p>Consumer service objects must register with a <tt>service.pid</tt>
 * and a {@link WireConstants#WIREADMIN_CONSUMER_FLAVORS} property.
 * It is recommended that Consumer service objects also register with a
 * <tt>service.description</tt> property.
 *
 * <p>If an <tt>Exception</tt> is thrown by any of the <tt>Consumer</tt> methods,
 * a <tt>WireAdminEvent</tt> of type {@link WireAdminEvent#CONSUMER_EXCEPTION} is
 * broadcast by the Wire Admin service.
 *
 * <p>Security Considerations - Data consuming bundles will require <tt>ServicePermission[REGISTER,Consumer]</tt>.
 * In general, only the Wire Admin service
 * bundle should have this permission. Thus only
 * the Wire Admin service may directly call a Consumer service.
 * Care must be taken in the sharing of <tt>Wire</tt> objects with other bundles.
 * <p>
 * Consumer services must be registered with their scope when they can receive different types of
 * objects from the Producer service. The Consumer service should have <tt>WirePermission</tt> for
 * each of these scope names.
 *
 * @version $Revision$
 */

public interface Consumer
{

    /**
     * Update the value. This Consumer service is called by the <tt>Wire</tt> object
     * with an updated value from the Producer service.
     *
     * <p>Note: This method may be called by a <tt>Wire</tt> object
     * prior to this object being notified
     * that it is connected to that <tt>Wire</tt> object (via
     * the {@link #producersConnected} method).
	 * <p>When the Consumer service can receive <tt>Envelope</tt> objects, it must have registered
	 * all scope names together with the service object, and each of those names
	 * must be permitted by the bundle's <tt>WirePermission</tt>. If an <tt>Envelope</tt> object
	 * is delivered with the <tt>updated</tt> method, then the Consumer service should assume
	 * that the security check has been performed.
     * @param wire The <tt>Wire</tt> object which is delivering the updated value.
     * @param value The updated value. The value should be an instance of one of the types
     * specified by the {@link Wire#getFlavors} method.
     */
    public void updated(Wire wire, Object value);

    /**
     * Update the list of <tt>Wire</tt> objects to which this Consumer service
     * is connected.
     *
     * <p>This method is called when the Consumer service is first registered
     * and subsequently whenever
     * a <tt>Wire</tt> associated with this Consumer service becomes connected,
     * is modified or becomes disconnected.
     *
     * <p>The Wire Admin service must call this method asynchronously. This
     * implies that implementors of Consumer can be assured
     * that the callback will not take place during registration
     * when they execute the registration in a synchronized method.
     *
     * @param wires An array of the current and complete list of <tt>Wire</tt> objects
     * to which this Consumer service is connected.
     * May be <tt>null</tt> if the Consumer service is not currently connected to any
     * <tt>Wire</tt> objects.
     */
    public void producersConnected(Wire[] wires);
}


