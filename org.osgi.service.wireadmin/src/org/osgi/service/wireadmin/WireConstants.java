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
 * Defines standard names for <tt>Wire</tt> properties, wire filter attributes,
 * Consumer and Producer service properties.
 *
 * @version $Revision$
 */

public interface WireConstants
{

    /**
	 * <tt>Wire</tt> property key (named <tt>wireadmin.pid</tt>)
	 * specifying the persistent identity (PID) of this <tt>Wire</tt> object.
	 *
	 * <p>Each <tt>Wire</tt> object has a PID to allow unique and persistent
	 * identification of a specific <tt>Wire</tt> object. The PID must be generated
	 * by the {@link WireAdmin} service when the <tt>Wire</tt> object is created.
	 *
	 * <p>This wire property is automatically set
	 * by the Wire Admin service.
	 * The value of the property must be of type <tt>String</tt>.
	 */
    public final static String WIREADMIN_PID = "wireadmin.pid";
	
	/**
	 * A service registration property for a Producer service that
	 * is composite. It contains the names of the composite Consumer services it can
	 * inter-operate with. Inter-operability exists when any name in this array
	 * matches any name in the array set by the Consumer service. The type of this property must be <tt>String[]</tt>.
	 */
	public final static String WIREADMIN_PRODUCER_COMPOSITE = "wireadmin.producer.composite";
	
	/**
	 * A service registration property for a Consumer service that
	 * is composite. It contains the names of the composite Producer services it can
	 * cooperate with. Inter-operability exists when any name in this array
	 * matches any name in the array set by the Producer service. The type of this property must be <tt>String[]</tt>.
	 */
	public final static String WIREADMIN_CONSUMER_COMPOSITE = "wireadmin.consumer.composite";
	
	
	
	
	/**
	 * Service registration property key (named <tt>wireadmin.producer.scope</tt>)
	 * specifying a list of names that may be used to define the
	 * scope of this <tt>Wire</tt> object. A Producer
	 * service should set this service property when it can produce
	 * more than one kind of value. This property is only used
	 * during registration, modifying the property must not have
	 * any effect of the <tt>Wire</tt> object's scope. Each name in the
	 * given list mist have <tt>WirePermission[PRODUCE,name]</tt> or else is
	 * ignored. The type of
	 * this service registration property must be <tt>String[]</tt>.
	 * @see Wire#getScope
	 * @see #WIREADMIN_CONSUMER_SCOPE
	 */
	public final static String WIREADMIN_PRODUCER_SCOPE = "wireadmin.producer.scope";
	/**
	 * Service registration property key (named <tt>wireadmin.consumer.scope</tt>)
	 * specifying a list of names that may be used to define the
	 * scope of this <tt>Wire</tt> object. A <tt>Consumer</tt>
	 * service should set this service property when it can produce
	 * more than one kind of value. This property is only used
	 * during registration, modifying the property must not have
	 * any effect of the <tt>Wire</tt> object's scope. Each name in the
	 * given list mist have <tt>WirePermission[CONSUME]</tt> or else is
	 * ignored. The type of
	 * this service registration property must be <tt>String[]</tt>.
	 * @see Wire#getScope
	 * @see #WIREADMIN_PRODUCER_SCOPE
	 */
	public final static String WIREADMIN_CONSUMER_SCOPE = "wireadmin.consumer.scope";
	
	/**
	 * Matches all scope names.
	 */
	public final static String WIREADMIN_SCOPE_ALL[] = { "*" };
	
    /**
	 * <tt>Wire</tt> property key (named <tt>wireadmin.producer.pid</tt>)
	 * specifying the <tt>service.pid</tt> of the associated Producer service.
	 *
	 * <p>This wire property is automatically set
	 * by the WireAdmin service.
	 * The value of the property must be of type <tt>String</tt>.
	 */
    public final static String WIREADMIN_PRODUCER_PID = "wireadmin.producer.pid";

    /**
	 * <tt>Wire</tt> property key (named <tt>wireadmin.consumer.pid</tt>)
	 * specifying the <tt>service.pid</tt> of the associated Consumer service.
	 *
	 * <p>This wire property is automatically set
	 * by the Wire Admin service.
	 * The value of the property must be of type <tt>String</tt>.
	 */
    public final static String WIREADMIN_CONSUMER_PID = "wireadmin.consumer.pid";

    /**
	 * <tt>Wire</tt> property key (named <tt>wireadmin.filter</tt>)
	 * specifying a filter used to control the delivery rate of data between
	 * the Producer and the Consumer service.
	 *
	 * <p>This property should contain a filter as described in
	 * the <tt>Filter</tt> class. The filter can be used to specify when an updated
	 * value from the Producer service should be delivered to the Consumer service.
	 * In many cases the Consumer service does not need
	 * to receive the data with the same rate that the Producer service can
	 * generate data. This property can be used to control the delivery rate.
	 * <p>The filter can use a number of pre-defined attributes that can
	 * be used to control the delivery of new data values. If the filter produces
	 * a match upon the wire filter attributes, the Consumer service should be
	 * notifed of the updated data value.
	 * <p>If the Producer service was registered with the {@link #WIREADMIN_PRODUCER_FILTERS}
	 * service property indicating that the Producer service will perform the data filtering
	 * then the <tt>Wire</tt> object will not perform data filtering.
	 * Otherwise, the <tt>Wire</tt> object must perform
	 * basic filtering. Basic filtering includes supporting the following
	 * standard wire filter attributes:
	 * <ul>
	 *  <li>{@link #WIREVALUE_CURRENT} - Current value
	 *  <li>{@link #WIREVALUE_PREVIOUS} - Previous value
	 *  <li>{@link #WIREVALUE_DELTA_ABSOLUTE} - Absolute delta
	 *  <li>{@link #WIREVALUE_DELTA_RELATIVE} - Relative delta
	 *  <li>{@link #WIREVALUE_ELAPSED} - Elapsed time
	 * </ul>
	 *
	 * @see org.osgi.framework.Filter
	 */
    public final static String WIREADMIN_FILTER                 = "wireadmin.filter";


    /* Wire filter attribute names. */

    /**
	 * <tt>Wire</tt> object's filter attribute (named <tt>wirevalue.current</tt>)
	 * representing the current value.
	 */
    public final static String WIREVALUE_CURRENT                = "wirevalue.current";

    /**
	 * <tt>Wire</tt> object's filter attribute (named <tt>wirevalue.previous</tt>)
	 * representing the previous value.
	 */
    public final static String WIREVALUE_PREVIOUS               = "wirevalue.previous";

    /**
	 * <tt>Wire</tt> object's filter attribute (named <tt>wirevalue.delta.absolute</tt>)
	 * representing the absolute delta.
	 * The absolute (always positive) difference between the last update and the
	 * current value (only when numeric).
	 * This attribute must not be used when the values are not numeric.
	 */
    public final static String WIREVALUE_DELTA_ABSOLUTE         = "wirevalue.delta.absolute";

    /**
	 * <tt>Wire</tt> object's filter attribute (named <tt>wirevalue.delta.relative</tt>)
	 * representing the relative delta.
	 * The relative difference is |<tt>previous</tt>-<tt>current</tt>|/|<tt>current</tt>| (only when numeric).
	 * This attribute must not be used when the values are not numeric.
	 */
    public final static String WIREVALUE_DELTA_RELATIVE         = "wirevalue.delta.relative";

    /**
	 * <tt>Wire</tt> object's filter attribute (named <tt>wirevalue.elapsed</tt>)
	 * representing the elapsed
	 * time, in ms, between this filter evaluation and the last update of the <tt>Consumer</tt> service.
	 */
    public final static String WIREVALUE_ELAPSED                = "wirevalue.elapsed";


    /* Service registration property key names. */

    /**
	 * Service Registration property
	 * (named <tt>wireadmin.producer.filters</tt>).
	 * A <tt>Producer</tt> service registered with this property
	 * indicates to the Wire Admin service
	 * that the Producer service implements at least the filtering as described
	 * for the {@link #WIREADMIN_FILTER} property. If the Producer service
	 * is not registered with this property,
	 * the <tt>Wire</tt> object must perform the basic filtering
	 * as described in {@link #WIREADMIN_FILTER}.
	 *
	 * <p>The type of the property value is not relevant. Only its presence is relevant.
	 */
    public final static String WIREADMIN_PRODUCER_FILTERS = "wireadmin.producer.filters";

    /**
	 * Service Registration property
	 * (named <tt>wireadmin.consumer.flavors</tt>)
	 * specifying the list of data types understood by this Consumer service.
	 *
	 * <p>The Consumer service object must be registered with this service
	 * property. The list must be in the order of preference with the first type
	 * being the most preferred.
	 * The value of the property must be of type <tt>Class[]</tt>.
	 */
    public final static String WIREADMIN_CONSUMER_FLAVORS       = "wireadmin.consumer.flavors";

    /**
	 * Service Registration property
	 * (named <tt>wireadmin.producer.flavors</tt>)
	 * specifying the list of data types available from this Producer service.
	 *
	 * <p>The Producer service object should be registered with this service
	 * property.
	 *
	 * <p>The value of the property must be of type <tt>Class[]</tt>.
	 */
    public final static String WIREADMIN_PRODUCER_FLAVORS       = "wireadmin.producer.flavors";


    /**
	 * Service Registration property
	 * (named <tt>wireadmin.events</tt>)
	 * specifying the <tt>WireAdminEvent</tt> type of interest to
	 * a Wire Admin Listener service.
	 * The value of the property
	 * is a bitwise OR of all the <tt>WireAdminEvent</tt> types
	 * the Wire Admin Listener service wishes to receive
	 * and must be of type <tt>Integer</tt>.
	 * @see WireAdminEvent
	 */
    public final static String WIREADMIN_EVENTS = "wireadmin.events";
}

