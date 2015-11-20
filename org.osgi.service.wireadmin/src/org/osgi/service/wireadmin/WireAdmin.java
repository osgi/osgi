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

import java.util.Dictionary;
import org.osgi.framework.InvalidSyntaxException;

/**
 * Wire Administration service.
 * 
 * <p>
 * This service can be used to create {@code Wire} objects connecting a Producer
 * service and a Consumer service. {@code Wire} objects also have wire
 * properties that may be specified when a {@code Wire} object is created. The
 * Producer and Consumer services may use the {@code Wire} object's properties
 * to manage or control their interaction. The use of {@code Wire} object's
 * properties by a Producer or Consumer services is optional.
 * 
 * <p>
 * Security Considerations. A bundle must have
 * {@code ServicePermission[WireAdmin,GET]} to get the Wire Admin service to
 * create, modify, find, and delete {@code Wire} objects.
 * 
 * @noimplement
 * @author $Id$
 */
public interface WireAdmin {
	/**
	 * Create a new {@code Wire} object that connects a Producer service to a
	 * Consumer service.
	 * 
	 * The Producer service and Consumer service do not have to be registered
	 * when the {@code Wire} object is created.
	 * 
	 * <p>
	 * The {@code Wire} configuration data must be persistently stored. All
	 * {@code Wire} connections are reestablished when the {@code WireAdmin}
	 * service is registered. A {@code Wire} can be permanently removed by using
	 * the {@link #deleteWire(Wire)} method.
	 * 
	 * <p>
	 * The {@code Wire} object's properties must have case insensitive
	 * {@code String} objects as keys (like the Framework). However, the case of
	 * the key must be preserved.
	 * 
	 * <p>
	 * The {@code WireAdmin} service must automatically add the following
	 * {@code Wire} properties:
	 * <ul>
	 * <li>{@link WireConstants#WIREADMIN_PID} set to the value of the
	 * {@code Wire} object's persistent identity (PID). This value is generated
	 * by the Wire Admin service when a {@code Wire} object is created.</li>
	 * <li>{@link WireConstants#WIREADMIN_PRODUCER_PID} set to the value of
	 * Producer service's PID.</li>
	 * <li>{@link WireConstants#WIREADMIN_CONSUMER_PID} set to the value of
	 * Consumer service's PID.</li>
	 * </ul>
	 * If the {@code properties} argument already contains any of these keys,
	 * then the supplied values are replaced with the values assigned by the
	 * Wire Admin service.
	 * 
	 * <p>
	 * The Wire Admin service must broadcast a {@code WireAdminEvent} of type
	 * {@link WireAdminEvent#WIRE_CREATED} after the new {@code Wire} object
	 * becomes available from {@link #getWires(String)}.
	 * 
	 * @param producerPID The {@code service.pid} of the Producer service to be
	 *        connected to the {@code Wire} object.
	 * @param consumerPID The {@code service.pid} of the Consumer service to be
	 *        connected to the {@code Wire} object.
	 * @param properties The {@code Wire} object's properties. This argument may
	 *        be {@code null} if the caller does not wish to define any
	 *        {@code Wire} object's properties.
	 * @return The {@code Wire} object for this connection.
	 * 
	 * @throws java.lang.IllegalArgumentException If {@code properties} contains
	 *         invalid wire types or case variants of the same key name.
	 */
	public Wire createWire(String producerPID, String consumerPID, Dictionary<String, ?> properties);

	/**
	 * Delete a {@code Wire} object.
	 * 
	 * <p>
	 * The {@code Wire} object representing a connection between a Producer
	 * service and a Consumer service must be removed. The persistently stored
	 * configuration data for the {@code Wire} object must destroyed. The
	 * {@code Wire} object's method {@link Wire#isValid()} will return
	 * {@code false} after it is deleted.
	 * 
	 * <p>
	 * The Wire Admin service must broadcast a {@code WireAdminEvent} of type
	 * {@link WireAdminEvent#WIRE_DELETED} after the {@code Wire} object becomes
	 * invalid.
	 * 
	 * @param wire The {@code Wire} object which is to be deleted.
	 */
	public void deleteWire(Wire wire);

	/**
	 * Update the properties of a {@code Wire} object.
	 * 
	 * The persistently stored configuration data for the {@code Wire} object is
	 * updated with the new properties and then the Consumer and Producer
	 * services will be called at the respective
	 * {@link Consumer#producersConnected(Wire[])} and
	 * {@link Producer#consumersConnected(Wire[])} methods.
	 * 
	 * <p>
	 * The Wire Admin service must broadcast a {@code WireAdminEvent} of type
	 * {@link WireAdminEvent#WIRE_UPDATED} after the updated properties are
	 * available from the {@code Wire} object.
	 * 
	 * @param wire The {@code Wire} object which is to be updated.
	 * @param properties The new {@code Wire} object's properties or
	 *        {@code null} if no properties are required.
	 * 
	 * @throws java.lang.IllegalArgumentException If {@code properties} contains
	 *         invalid wire types or case variants of the same key name.
	 */
	public void updateWire(Wire wire, Dictionary<String, ?> properties);

	/**
	 * Return the {@code Wire} objects that match the given {@code filter}.
	 * 
	 * <p>
	 * The list of available {@code Wire} objects is matched against the
	 * specified {@code filter}.{@code Wire} objects which match the
	 * {@code filter} must be returned. These {@code Wire} objects are not
	 * necessarily connected. The Wire Admin service should not return invalid
	 * {@code Wire} objects, but it is possible that a {@code Wire} object is
	 * deleted after it was placed in the list.
	 * 
	 * <p>
	 * The filter matches against the {@code Wire} object's properties including
	 * {@link WireConstants#WIREADMIN_PRODUCER_PID},
	 * {@link WireConstants#WIREADMIN_CONSUMER_PID} and
	 * {@link WireConstants#WIREADMIN_PID}.
	 * 
	 * @param filter Filter string to select {@code Wire} objects or
	 *        {@code null} to select all {@code Wire} objects.
	 * @return An array of {@code Wire} objects which match the {@code filter}
	 *         or {@code null} if no {@code Wire} objects match the
	 *         {@code filter}.
	 * @throws org.osgi.framework.InvalidSyntaxException If the specified
	 *         {@code filter} has an invalid syntax.
	 * @see org.osgi.framework.Filter
	 */
	public Wire[] getWires(String filter) throws InvalidSyntaxException;
}
