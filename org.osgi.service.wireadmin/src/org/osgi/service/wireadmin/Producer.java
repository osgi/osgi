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
 * Data Producer, a service that can generate values to be used by
 * {@link Consumer}services.
 * 
 * <p>
 * Service objects registered under the Producer interface are expected to
 * produce values (internally generated or from external sensors). The value can
 * be of different types. When delivering a value to a <tt>Wire</tt> object,
 * the Producer service should coerce the value to be an instance of one of the
 * types specified by {@link Wire#getFlavors}. The classes are specified in
 * order of preference.
 * 
 * <p>
 * When the data represented by the Producer object changes, this object should
 * send the updated value by calling the <tt>update</tt> method on each of
 * <tt>Wire</tt> objects passed in the most recent call to this object's
 * {@link #consumersConnected}method. These <tt>Wire</tt> objects will pass
 * the value on to the associated <tt>Consumer</tt> service object.
 * 
 * <p>
 * The Producer service may use the information in the <tt>Wire</tt> object's
 * properties to schedule the delivery of values to the <tt>Wire</tt> object.
 * 
 * <p>
 * Producer service objects must register with a <tt>service.pid</tt> and a
 * {@link WireConstants#WIREADMIN_PRODUCER_FLAVORS}property. It is recommended
 * that a Producer service object also registers with a
 * <tt>service.description</tt> property. Producer service objects must
 * register with a {@link WireConstants#WIREADMIN_PRODUCER_FILTERS}property if
 * the Producer service will be performing filtering instead of the
 * <tt>Wire</tt> object.
 * 
 * <p>
 * If an exception is thrown by a Producer object method, a
 * <tt>WireAdminEvent</tt> of type {@link WireAdminEvent#PRODUCER_EXCEPTION}
 * is broadcast by the Wire Admin service.
 * 
 * <p>
 * Security Considerations. Data producing bundles will require
 * <tt>ServicePermission[REGISTER,Producer]</tt> to register a Producer
 * service. In general, only the Wire Admin service should have
 * <tt>ServicePermission[GET,Producer]</tt>. Thus only the Wire Admin service
 * may directly call a Producer service. Care must be taken in the sharing of
 * <tt>Wire</tt> objects with other bundles.
 * <p>
 * Producer services must be registered with scope names when they can send
 * different types of objects (composite) to the Consumer service. The Producer
 * service should have <tt>WirePermission</tt> for each of these scope names.
 * 
 * @version $Revision$
 */
public interface Producer {
	/**
	 * Return the current value of this <tt>Producer</tt> object.
	 * 
	 * <p>
	 * This method is called by a <tt>Wire</tt> object in response to the
	 * Consumer service calling the <tt>Wire</tt> object's <tt>poll</tt>
	 * method. The Producer should coerce the value to be an instance of one of
	 * the types specified by {@link Wire#getFlavors}. The types are specified
	 * in order of of preference. The returned value should be as new or newer
	 * than the last value furnished by this object.
	 * 
	 * <p>
	 * Note: This method may be called by a <tt>Wire</tt> object prior to this
	 * object being notified that it is connected to that <tt>Wire</tt> object
	 * (via the {@link #consumersConnected}method).
	 * <p>
	 * If the Producer service returns an <tt>Envelope</tt> object that has an
	 * unpermitted scope name, then the Wire object must ignore (or remove) the
	 * transfer.
	 * <p>
	 * If the <tt>Wire</tt> object has a scope set, the return value must be
	 * an array of <tt>Envelope</tt> objects (<tt>Envelope[]</tt>). The
	 * <tt>Wire</tt> object must have removed any <tt>Envelope</tt> objects
	 * that have a scope name that is not in the Wire object's scope.
	 * 
	 * @param wire The <tt>Wire</tt> object which is polling this service.
	 * @return The current value of the Producer service or <tt>null</tt> if
	 *         the value cannot be coerced into a compatible type. Or an array
	 *         of <tt>Envelope</tt> objects.
	 */
	public Object polled(Wire wire);

	/**
	 * Update the list of <tt>Wire</tt> objects to which this
	 * <tt>Producer</tt> object is connected.
	 * 
	 * <p>
	 * This method is called when the Producer service is first registered and
	 * subsequently whenever a <tt>Wire</tt> associated with this Producer
	 * becomes connected, is modified or becomes disconnected.
	 * 
	 * <p>
	 * The Wire Admin service must call this method asynchronously. This implies
	 * that implementors of a Producer service can be assured that the callback
	 * will not take place during registration when they execute the
	 * registration in a synchronized method.
	 * 
	 * @param wires An array of the current and complete list of <tt>Wire</tt>
	 *        objects to which this Producer service is connected. May be
	 *        <tt>null</tt> if the Producer is not currently connected to any
	 *        <tt>Wire</tt> objects.
	 */
	public void consumersConnected(Wire[] wires);
}
