/*
 * Copyright (c) OSGi Alliance (2002, 2015). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.wireadmin;

/**
 * Data Producer, a service that can generate values to be used by
 * {@link Consumer} services.
 * 
 * <p>
 * Service objects registered under the Producer interface are expected to
 * produce values (internally generated or from external sensors). The value can
 * be of different types. When delivering a value to a {@code Wire} object, the
 * Producer service should coerce the value to be an instance of one of the
 * types specified by {@link Wire#getFlavors()}. The classes are specified in
 * order of preference.
 * 
 * <p>
 * When the data represented by the Producer object changes, this object should
 * send the updated value by calling the {@code update} method on each of
 * {@code Wire} objects passed in the most recent call to this object's
 * {@link #consumersConnected(Wire[])} method. These {@code Wire} objects will
 * pass the value on to the associated {@code Consumer} service object.
 * 
 * <p>
 * The Producer service may use the information in the {@code Wire} object's
 * properties to schedule the delivery of values to the {@code Wire} object.
 * 
 * <p>
 * Producer service objects must register with a {@code service.pid} and a
 * {@link WireConstants#WIREADMIN_PRODUCER_FLAVORS} property. It is recommended
 * that a Producer service object also registers with a
 * {@code service.description} property. Producer service objects must register
 * with a {@link WireConstants#WIREADMIN_PRODUCER_FILTERS} property if the
 * Producer service will be performing filtering instead of the {@code Wire}
 * object.
 * 
 * <p>
 * If an exception is thrown by a Producer object method, a
 * {@code WireAdminEvent} of type {@link WireAdminEvent#PRODUCER_EXCEPTION} is
 * broadcast by the Wire Admin service.
 * 
 * <p>
 * Security Considerations. Data producing bundles will require
 * {@code ServicePermission[Producer,REGISTER]} to register a Producer service.
 * In general, only the Wire Admin service should have
 * {@code ServicePermission[Producer,GET]}. Thus only the Wire Admin service may
 * directly call a Producer service. Care must be taken in the sharing of
 * {@code Wire} objects with other bundles.
 * <p>
 * Producer services must be registered with scope names when they can send
 * different types of objects (composite) to the Consumer service. The Producer
 * service should have {@code WirePermission} for each of these scope names.
 * 
 * @author $Id$
 */
public interface Producer {
	/**
	 * Return the current value of this {@code Producer} object.
	 * 
	 * <p>
	 * This method is called by a {@code Wire} object in response to the
	 * Consumer service calling the {@code Wire} object's {@code poll} method.
	 * The Producer should coerce the value to be an instance of one of the
	 * types specified by {@link Wire#getFlavors()}. The types are specified in
	 * order of preference. The returned value should be as new or newer than
	 * the last value furnished by this object.
	 * 
	 * <p>
	 * Note: This method may be called by a {@code Wire} object prior to this
	 * object being notified that it is connected to that {@code Wire} object
	 * (via the {@link #consumersConnected(Wire[])} method).
	 * <p>
	 * If the Producer service returns an {@code Envelope} object that has an
	 * impermissible scope name, then the Wire object must ignore (or remove)
	 * the transfer.
	 * <p>
	 * If the {@code Wire} object has a scope set, the return value must be an
	 * array of {@code Envelope} objects ({@code Envelope[]}). The {@code Wire}
	 * object must have removed any {@code Envelope} objects that have a scope
	 * name that is not in the Wire object's scope.
	 * 
	 * @param wire The {@code Wire} object which is polling this service.
	 * @return The current value of the Producer service or {@code null} if the
	 *         value cannot be coerced into a compatible type. Or an array of
	 *         {@code Envelope} objects.
	 */
	public Object polled(Wire wire);

	/**
	 * Update the list of {@code Wire} objects to which this {@code Producer}
	 * object is connected.
	 * 
	 * <p>
	 * This method is called when the Producer service is first registered and
	 * subsequently whenever a {@code Wire} associated with this Producer
	 * becomes connected, is modified or becomes disconnected.
	 * 
	 * <p>
	 * The Wire Admin service must call this method asynchronously. This implies
	 * that implementors of a Producer service can be assured that the callback
	 * will not take place during registration when they execute the
	 * registration in a synchronized method.
	 * 
	 * @param wires An array of the current and complete list of {@code Wire}
	 *        objects to which this Producer service is connected. May be
	 *        {@code null} if the Producer is not currently connected to any
	 *        {@code Wire} objects.
	 */
	public void consumersConnected(Wire[] wires);
}
