/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.service.zigbee;

import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;

/**
 * This interface represents a ZigBee EndPoint
 * 
 * @version 1.0
 */
public interface ZigBeeEndpoint {

	/**
	 * Key of the {@link String} containing the EndPoint Address of the device <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	ID						= "zigbee.endpoint.id";

	/**
	 * Key of the {@link String} profile id implemented by the device. <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	PROFILE_ID				= "zigbee.device.profile.id";

	/**
	 * Key of the {@link String} containing the DeviceId of the device <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	DEVICE_ID				= "zigbee.device.id";

	/**
	 * Key of the {@link String} containing the DeviceVersion of the device <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	DEVICE_VERSION			= "zigbee.device.version";

	/**
	 * Constant used by all ZigBee devices indicating the device category <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	DEVICE_CATEGORY			= "ZigBee";

	/**
	 * Key of the int array of containing the ids of each input cluster <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	INPUT_CLUSTERS			= "zigbee.endpoint.clusters.input";

	/**
	 * Key of the int array of containing the ids of each output cluster <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	OUTPUT_CLUSTERS			= "zigbee.endpoint.clusters.output";

	/**
	 * Key of {@link String} containing the targeted network host pid It is an
	 * <b>optional</b> property for this service. If provided, HOST_PID property
	 * must have the priority to identify the host that is targeted for export.
	 */
	public static final String	HOST_PID_TARGET			= "zigbee.device.target.host.pid";

	/**
	 * Key of {@link String} containing the targeted network PAN ID
	 */
	public static final String	PAN_ID_TARGET			= "zigbee.device.target.pan.id";

	/**
	 * Key of {@link String} containing the targeted network extended PAN ID
	 */
	public static final String	EXTENDED_PAN_ID_TARGET	= "zigbee.device.target.extended.pan.id";

	/**
	 * Key of the {@link String} mentioning that an endpoint an exported one or
	 * not <br>
	 * It is <b>optional</b> property for this service
	 */
	public static final String	ZIGBEE_EXPORT			= "zigbee.export";

	/**
	 * @return identifier of the endpoint represented by this object, value
	 *         ranges from 1 to 240.
	 */
	public Integer getId();

	/**
	 * @return The IEEE Address of the node containing this endpoint
	 */
	public Long getNodeAddress();

	/**
	 * @return the node simple descriptor.
	 * @throws ZigBeeException
	 */
	public ZigBeeSimpleDescriptor getSimpleDescriptor() throws ZigBeeException;

	/**
	 * @return An array of servers(inputs) clusters, returns an empty array if
	 *         does not provides any servers clusters.
	 */
	public ZigBeeCluster[] getServerClusters();

	/**
	 * @param serverClusterId The server(input) cluster identifier
	 * @return the server(input) cluster identified by id
	 */
	public ZigBeeCluster getServerCluster(int serverClusterId);

	/**
	 * @return An array of clients(outputs) clusters, returns an empty array if
	 *         does not provides any clients clusters.
	 */
	public ZigBeeCluster[] getClientClusters();

	/**
	 * @param clientClusterId The client(output) cluster identifier
	 * @return the client(output) cluster identified by id
	 */
	public ZigBeeCluster getClientCluster(int clientClusterId);

	/**
	 * This method modify the <i>Binding Table</i> of physical device by adding
	 * the following entry:
	 * 
	 * <pre>
	 * this.getNodeAddress(), this.getId(), clusterId, device.getNodeAddress(), device.getId()
	 * </pre>
	 * 
	 * @param endpoint The device {@link ZigBeeEndpoint} that we want to bound
	 *        to
	 * @param clusterId the cluster identifier that we want to bound to
	 * @return <code>true</code> if and only if the operation succeeded
	 */
	public boolean bindTo(ZigBeeEndpoint endpoint, int clusterId);

	/**
	 * This method modify the <i>Binding Table</i> of physical device by
	 * removing the entry if exists
	 * 
	 * <pre>
	 * this.getNodeAddress(), this.getId(), clusterId, device.getNodeAddress(), device.getId()
	 * </pre>
	 * 
	 * @param endpoint The device {@link ZigBeeEndpoint} that we are bounded to
	 * @param clusterId The cluster identifier that we want to unbound from
	 * @return <code>true</code> if and only if the operation succeeded
	 */
	public boolean unbindFrom(ZigBeeEndpoint endpoint, int clusterId);

	/**
	 * This method is used to get details about problems when an error occurs
	 * during exporting an endpoint
	 * 
	 * @param e A device {@link ZigBeeException} the occurred exception
	 */
	public void notExported(ZigBeeException e);

}
