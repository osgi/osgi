/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

import java.math.BigInteger;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.osgi.util.promise.Promise;

/**
 * This interface represents a ZigBee EndPoint.
 * 
 * A ZigBeeEndpoint must be registered as a OSGi service with
 * ZigBeeNode.IEEE_ADDRESS, and ZigBeeEndpoint.ENDPOINT_ID properties.
 * 
 * @author $Id$
 */
public interface ZigBeeEndpoint {

	/**
	 * Key of the {@link String} property containing the EndPoint Address of the
	 * device <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	ENDPOINT_ID		= "zigbee.endpoint.id";

	/**
	 * Key of the {@link String} property containing the profile id implemented
	 * by the device. <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	PROFILE_ID		= "zigbee.device.profile.id";

	/**
	 * Key of {@link String} containing the {@link ZigBeeHost}'s pid.<br>
	 * The ZigBee local host identifier is intended to uniquely identify the
	 * ZigBee local host, since there could be many hosts on the same platform.
	 * All the nodes that belong to a specific network MUST specify the value of
	 * the associated host number. It is mandatory for imported endpoints,
	 * optional for exported endpoints.
	 */
	public static final String	HOST_PID		= "zigbee.endpoint.host.pid";

	/**
	 * Key of the {@link String} property containing the DeviceId of the device
	 * <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	DEVICE_ID		= "zigbee.device.id";

	/**
	 * Key of the {@link String} property containing the DeviceVersion of the
	 * device <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	DEVICE_VERSION	= "zigbee.device.version";

	/**
	 * Key of the int array of containing the ids of each input cluster <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	INPUT_CLUSTERS	= "zigbee.endpoint.clusters.input";

	/**
	 * Key of the int array of containing the ids of each output cluster <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	OUTPUT_CLUSTERS	= "zigbee.endpoint.clusters.output";

	/**
	 * Property used to mark if a ZigBeeEndPoint service is an exported one or
	 * not. Imported endpoints do not have this property set. This service
	 * property requires no value.
	 */
	public static final String	ZIGBEE_EXPORT	= "zigbee.export";

	/**
	 * Constant used by all ZigBee devices indicating the device category. It is
	 * a <b>mandatory</b> property for this service.
	 */
	public static final String	DEVICE_CATEGORY	= "ZigBee";

	/**
	 * @return identifier of the endpoint represented by this object, value
	 *         ranges from 1 to 240.
	 */
	public short getId();

	/**
	 * @return The IEEE Address of the node containing this endpoint
	 */
	public BigInteger getNodeAddress();

	/**
	 * As described in "Table 2.93 Fields of the Simple_Desc_rsp Command" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * simple_decr request can have the following status: SUCCESS, INVALID_EP,
	 * NOT_ACTIVE, DEVICE_NOT_FOUND, INV_REQUESTTYPE or NO_DESCRIPTOR.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         It will be used in order to return the node simple descriptor
	 *         {@link ZigBeeSimpleDescriptor}.
	 * 
	 */
	public Promise /* <ZigBeeSimpleDescriptor> */ getSimpleDescriptor();

	/**
	 * @return An array of servers(inputs) clusters, returns an empty array if
	 *         it does not provide any server cluster.
	 */
	public ZCLCluster[] getServerClusters();

	/**
	 * @param serverClusterId The server(input) cluster identifier
	 * @return the server(input) cluster identified by id, or null if the given
	 *         id is not listed in the simple descriptor
	 */
	public ZCLCluster getServerCluster(int serverClusterId);

	/**
	 * @return An array of clients(outputs) clusters, returns an empty array if
	 *         does not provides any clients clusters.
	 */
	public ZCLCluster[] getClientClusters();

	/**
	 * @param clientClusterId The client(output) cluster identifier
	 * @return the client(output) cluster identified by id, or null if the given
	 *         id is not listed in the simple descriptor
	 */
	public ZCLCluster getClientCluster(int clientClusterId);

	/**
	 * This method modifies the <i>Binding Table</i> of physical device by
	 * adding the following entry:
	 * 
	 * <pre>this.getNodeAddress(), this.getId(), clusterId, device.getNodeAddress(), device.getId()</pre>
	 * 
	 * As described in "Table 2.7 APSME-BIND.confirm Parameters" of the ZigBee
	 * specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a binding
	 * request can have the following results: SUCCESS, ILLEGAL_REQUEST,
	 * TABLE_FULL, NOT_SUPPORTED (see {@link APSException}). <br>
	 * 
	 * @param servicePid the PID of the endpoint to bind to
	 * @param clusterId the cluster identifier to bind to
	 * @return A promise representing the completion of this asynchronous call.
	 *         {@link Promise#getFailure()} returns null if the cluster has been
	 *         successfully bound. The adequate APSException is returned
	 *         otherwise.
	 */
	public Promise /* <void> */ bind(String servicePid, int clusterId);

	/**
	 * This method modifies the <i>Binding Table</i> of physical device by
	 * removing the entry if exists:
	 * 
	 * <pre>this.getNodeAddress(), this.getId(), clusterId, device.getNodeAddress(), device.getId()</pre>
	 * 
	 * <p>
	 * As described in "Table 2.9 APSME-UNBIND.confirm Parameters" of the ZigBee
	 * specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, an unbind
	 * request can have the following results: SUCCESS, ILLEGAL_REQUEST,
	 * INVALID_BINDING (see {@link APSException}).
	 * 
	 * @param servicePid The pid of the service to unbind.
	 * @param clusterId The cluster identifier to unbind.
	 * @return A promise representing the completion of this asynchronous call.
	 *         {@link Promise#getFailure()} returns null if the cluster has been
	 *         successfully bound. The adequate APSException is returned
	 *         otherwise.
	 */
	public Promise /* <void> */ unbind(String servicePid, int clusterId);

	/**
	 * This method is called by the base driver and used to give details about
	 * issues preventing the export of an endpoint.
	 * 
	 * @param e A device {@link ZigBeeException} the occurred exception
	 */
	public void notExported(ZigBeeException e);

	/**
	 * This method is used to get bound endpoints (identified by their service
	 * PIDs). It is implemented on the base driver with Mgmt_Bind_req command.
	 * It is implemented without a command request in local endpoints.
	 * 
	 * <p>
	 * As described in "Table 2.129 Fields of the Mgmt_Bind_rsp Command" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * Mgmt_Bind_rsp command can have the following status: NOT_SUPPORTED or any
	 * status code returned from the APSME-GET.confirm primitive (see
	 * {@link APSException}).
	 * 
	 * @param clusterId
	 * @return A promise representing the completion of this asynchronous call.
	 *         {@link Promise#getValue()} returns a List of the bound endpoint
	 *         service PIDs if the command is successful. The response object is
	 *         null and the adequate APSException is returned by
	 *         {@link Promise#getFailure()} otherwise.
	 */
	public Promise/* <List<String>> */ getBoundEndPoints(int clusterId);

}
