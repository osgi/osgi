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

import org.osgi.framework.ServiceReference;

/**
 * A Wire Admin Event.
 *
 * <p><tt>WireAdminEvent</tt> objects are delivered to all
 * registered <tt>WireAdminListener</tt> service objects which specify an
 * interest in the <tt>WireAdminEvent</tt> type.
 * Events must be delivered in chronological order with respect to each listener.
 * For example, a <tt>WireAdminEvent</tt> of type {@link #WIRE_CONNECTED}
 * must be delivered before a <tt>WireAdminEvent</tt> of type
 * {@link #WIRE_DISCONNECTED} for a particular <tt>Wire</tt> object.
 *
 * <p>A type code is used to identify the type of event. The following event types are
 * defined:
 * <ul>
 *  <li>{@link #WIRE_CREATED}
 *  <li>{@link #WIRE_CONNECTED}
 *  <li>{@link #WIRE_UPDATED}
 *  <li>{@link #WIRE_TRACE}
 *  <li>{@link #WIRE_DISCONNECTED}
 *  <li>{@link #WIRE_DELETED}
 *  <li>{@link #PRODUCER_EXCEPTION}
 *  <li>{@link #CONSUMER_EXCEPTION}
 * </ul>
 * Additional event types may be defined in the future.
 *
 * <p>Event type values must be unique and disjoint bit values.
 * Event types must be defined as a bit in a 32 bit integer and can thus be
 * bitwise OR'ed together.
 * <p>Security Considerations.
 * <tt>WireAdminEvent</tt> objects contain <tt>Wire</tt> objects.
 * Care must be taken in the sharing of <tt>Wire</tt> objects with other bundles.
 *
 * @see WireAdminListener
 *
 * @version $Revision$
 */

public class WireAdminEvent
{
    /**
     * The WireAdmin service which created this event.
     */
    private ServiceReference reference;

    /**
     * The <tt>Wire</tt> object associated with this event.
     */
    private Wire wire;

    /**
     * Type of this event.
     *
     * @see #getType
     */
    private int type;

    /**
     * Exception associates with this the event.
     */
    private Throwable throwable;

    /**
     * A Producer service method has thrown an exception.
     *
     * <p>This <tt>WireAdminEvent</tt> type indicates that a Producer service
     * method has thrown an exception. The {@link WireAdminEvent#getThrowable} method
     * will return the exception that the Producer service method raised.
     *
     * <p>The value of <tt>PRODUCER_EXCEPTION</tt> is 0x00000001.
     */
    public final static int PRODUCER_EXCEPTION = 0x00000001;

    /**
     * A Consumer service method has thrown an exception.
     *
     * <p>This <tt>WireAdminEvent</tt> type indicates that a Consumer service
     * method has thrown an exception. The {@link WireAdminEvent#getThrowable} method
     * will return the exception that the Consumer service method raised.
     *
     * <p>The value of <tt>CONSUMER_EXCEPTION</tt> is 0x00000002.
     */
    public final static int CONSUMER_EXCEPTION = 0x00000002;

    /**
     * A <tt>Wire</tt> has been created.
     *
     * <p>This <tt>WireAdminEvent</tt> type that indicates that
     * a new <tt>Wire</tt> object has been created.
     *
     * An event is broadcast when {@link WireAdmin#createWire} is called.
     * The {@link WireAdminEvent#getWire} method will return the <tt>Wire</tt> object that
     * has just been created.
     *
     * <p>The value of <tt>WIRE_CREATED</tt> is 0x00000004.
     */
    public final static int WIRE_CREATED = 0x00000004;

    /**
     * A <tt>Wire</tt> has been updated.
     *
     * <p>This <tt>WireAdminEvent</tt> type that indicates that an existing <tt>Wire</tt> object has been
     * updated with new properties.
     *
     * An event is broadcast when {@link WireAdmin#updateWire} is
     * called with a valid wire. The {@link WireAdminEvent#getWire} method will
     * return the <tt>Wire</tt> object that
     * has just been updated.
     *
     * <p>The value of <tt>WIRE_UPDATED</tt> is 0x00000008.
     */
    public final static int WIRE_UPDATED = 0x00000008;

    /**
     * A <tt>Wire</tt> has been deleted.
     *
     * <p>This <tt>WireAdminEvent</tt> type that indicates that an existing wire has been
     * deleted.
     *
     * An event is broadcast when {@link WireAdmin#deleteWire} is
     * called with a valid wire. {@link WireAdminEvent#getWire} will
     * return the <tt>Wire</tt> object that
     * has just been deleted.
     *
     * <p>The value of <tt>WIRE_DELETED</tt> is 0x00000010.
     */
    public final static int WIRE_DELETED = 0x00000010;

    /**
     * The <tt>WireAdminEvent</tt> type that indicates that an existing <tt>Wire</tt> object
     * has become connected.
     *
     * The Consumer object and the Producer object that are
     * associated with the <tt>Wire</tt> object
     * have both been registered and the <tt>Wire</tt> object is connected.
     * See {@link Wire#isConnected} for a description of the connected state.
     * This event may come before the  <tt>producersConnected</tt> and <tt>consumersConnected</tt>
     * method have returned or called to allow synchronous delivery of the events. Both
     * methods can cause other <tt>WireAdminEvent</tt>s to take place and requiring this
     * event to be send before these methods are returned would mandate asynchronous
     * delivery.
     *
     * <p>The value of <tt>WIRE_CONNECTED</tt> is 0x00000020.
     */
    public final static int WIRE_CONNECTED = 0x00000020;

    /**
     * The <tt>WireAdminEvent</tt> type that indicates that an existing <tt>Wire</tt> object
     * has become disconnected.
     *
     * The Consumer object or/and Producer object
     * is/are unregistered breaking the connection between the two.
     * See {@link Wire#isConnected} for a description of the connected state.
     *
     * <p>The value of <tt>WIRE_DISCONNECTED</tt> is 0x00000040.
     */
    public final static int WIRE_DISCONNECTED = 0x00000040;

    /**
     * The <tt>WireAdminEvent</tt> type that indicates that a new value is transferred
     * over the <tt>Wire</tt> object.
     *
     * This event is sent after the Consumer service has been notified by calling the
     * {@link Consumer#updated} method or the Consumer service requested
     * a new value with the {@link Wire#poll} method. This is an advisory
     * event meaning that when this event is received, another update
     * may already have occurred and this the {@link Wire#getLastValue} method returns
     * a newer value then the value that was communicated for this event.
     *
     * <p>The value of <tt>WIRE_TRACE</tt> is 0x00000080.
     */
    public final static int WIRE_TRACE = 0x00000080;

    /**
     * Constructs a <tt>WireAdminEvent</tt> object from the given
     * <tt>ServiceReference</tt> object, event type,
     * <tt>Wire</tt> object and exception.
     *
     * @param reference The <tt>ServiceReference</tt> object of
     * the Wire Admin service that created this event.
     * @param type The event type. See {@link #getType}.
     * @param wire The <tt>Wire</tt> object associated with this event.
     * @param exception An exception associated with this event.
     * This may be <tt>null</tt> if no exception is associated with this event.
     */
    public WireAdminEvent(ServiceReference reference, int type, Wire wire, Throwable exception) {
        this.reference = reference;
        this.wire = wire;
        this.type = type;
        this.throwable = exception;
    }

    /**
     * Return the <tt>ServiceReference</tt> object of
     * the Wire Admin service that created this event.
     *
     * @return The <tt>ServiceReference</tt> object for
     * the Wire Admin service that created this event.
     */
    public ServiceReference getServiceReference() {
        return reference;
    }

    /**
     * Return the <tt>Wire</tt> object associated with this event.
     *
     * @return The <tt>Wire</tt> object associated with this event or <tt>null</tt>
     * when no <tt>Wire</tt> object is associated with the event.
     */
    public Wire getWire() {
        return wire;
    }

    /**
     * Return the type of this event.
     * <p>The type values are:
     * <ul>
     *  <li>{@link #WIRE_CREATED}
     *  <li>{@link #WIRE_CONNECTED}
     *  <li>{@link #WIRE_UPDATED}
     *  <li>{@link #WIRE_TRACE}
     *  <li>{@link #WIRE_DISCONNECTED}
     *  <li>{@link #WIRE_DELETED}
     *  <li>{@link #PRODUCER_EXCEPTION}
     *  <li>{@link #CONSUMER_EXCEPTION}
     * </ul>
     *
     * @return The type of this event.
     */

    public int getType() {
        return type;
    }

    /**
     * Returns the exception associated with the event, if any.
     *
     * @return An exception or <tt>null</tt> if no exception
     * is associated with this event.
     */
    public Throwable getThrowable() {
        return throwable;
    }
}


