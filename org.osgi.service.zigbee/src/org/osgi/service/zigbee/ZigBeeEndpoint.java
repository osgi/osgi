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
	public int getId();

	/**
	 * @return The IEEE Address of the node containing this endpoint
	 */
	public Long getNodeAddress();

	/**
	 * @param handler that will be used in order to return the node simple
	 *        descriptor {@link ZigBeeSimpleDescriptor}.
	 * @throws ZCLException
	 */
	public void getSimpleDescriptor(ZigBeeHandler handler) throws ZCLException;

	/**
	 * @return An array of servers(inputs) clusters, returns an empty array if
	 *         does not provides any servers clusters.
	 */
	public ZCLCluster[] getServerClusters();

	/**
	 * @param serverClusterId The server(input) cluster identifier
	 * @return the server(input) cluster identified by id
	 * @throws ZCLException if the given id is not listed in the simple
	 *         descriptor
	 */
	public ZCLCluster getServerCluster(int serverClusterId) throws ZCLException;

	/**
	 * @return An array of clients(outputs) clusters, returns an empty array if
	 *         does not provides any clients clusters.
	 */
	public ZCLCluster[] getClientClusters();

	/**
	 * @param clientClusterId The client(output) cluster identifier
	 * @return the client(output) cluster identified by id
	 * @throws ZCLException if the given id is not listed in the simple
	 *         descriptor
	 */
	public ZCLCluster getClientCluster(int clientClusterId) throws ZCLException;

	/**
	 * This method modify the <i>Binding Table</i> of physical device by adding
	 * the following entry:
	 * 
	 * <pre>
	 * this.getNodeAddress(), this.getId(), clusterId, device.getNodeAddress(), device.getId()
	 * </pre>
	 * 
	 * @param servicePid to bound to
	 * @param clusterId the cluster identifier to bound to
	 * @param handler
	 */
	public void bind(String servicePid, int clusterId, ZigBeeHandler handler);

	/**
	 * This method modify the <i>Binding Table</i> of physical device by
	 * removing the entry if exists:
	 * 
	 * <pre>
	 * this.getNodeAddress(), this.getId(), clusterId, device.getNodeAddress(), device.getId()
	 * </pre>
	 * 
	 * @param servicePid to unbound from
	 * @param clusterId The cluster identifier to unbound from
	 * @param handler
	 */
	public void unbind(String servicePid, int clusterId, ZigBeeHandler handler);

	/**
	 * This method is used to get details about problems when an error occurs
	 * during exporting an endpoint
	 * 
	 * @param e A device {@link ZCLException} the occurred exception
	 */
	public void notExported(ZCLException e);

	/**
	 * This method is used to get bound endpoints (identified by their service
	 * PIDs). It is implemented on the base driver with Mgmt_Bind_req command.
	 * It is implemented without a command request in local endpoints. If the
	 * local method or command request is not supported, then an exception with
	 * the following reason is thrown: GENERAL_COMMAND_NOT_SUPPORTED. If the
	 * method fails to retrieve the full binding table (that could require
	 * several Mgmt_Bind_req command), then an exception with the error code
	 * that was sent on the last response is thrown.
	 * 
	 * @param clusterId
	 * @param handler
	 */
	public void getBoundEndPoints(int clusterId, ZigBeeHandler handler);

}
