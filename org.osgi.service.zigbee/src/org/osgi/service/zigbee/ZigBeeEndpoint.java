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
	 * Property containing the EndPoint ID of the device. This property is of
	 * type {@link Short} and its value must be in the range allowed by the
	 * ZigBee specifications for Zigbee endpoints identifiers.
	 * <p>
	 * It is <b>mandatory</b> service property for ZigBeeEndpoint services.
	 */
	public static final String	ENDPOINT_ID		= "zigbee.endpoint.id";

	/**
	 * Property containing the application profile identifier also contained in
	 * the ZigBee Simple Descriptor. This property is of type {@link Integer}.
	 * <p>
	 * It is <b>mandatory</b> service property for this service.
	 */
	public static final String	PROFILE_ID		= "zigbee.device.profile.id";

	/**
	 * Property containing the {@link ZigBeeHost}'s pid. This property is of
	 * type {@link String}.
	 * <p>
	 * The ZigBee local host identifier is intended to uniquely identify the
	 * ZigBee local host, since there could be many hosts on the same platform.
	 * <p>
	 * All the endpoints that belong to a specific network MUST specify the
	 * value of the associated host pid. It is mandatory for imported endpoints,
	 * optional for exported endpoints.
	 */
	public static final String	HOST_PID		= "zigbee.endpoint.host.pid";

	/**
	 * Property containing the application device identifier. This identifier is
	 * also contained in the ZigBee Simple Descriptor. This property is of type
	 * {@link Integer}.
	 * <p>
	 * It is <b>mandatory</b> property for this service.
	 */
	public static final String	DEVICE_ID		= "zigbee.device.id";

	/**
	 * Property containing the application device version. The application
	 * device version is also contained in the ZigBee endpoint Simple
	 * Descriptor. This property is of type {@link Byte}.
	 * <p>
	 * It is <b>mandatory</b> property for this service.
	 */
	public static final String	DEVICE_VERSION	= "zigbee.device.version";

	/**
	 * Property containing a list of input clusters. This list is contained also
	 * in the ZigBee Simple Descriptor returned by the ZigBeeEndpoint service.
	 * This property is of type int[].
	 * <p>
	 * It is <b>mandatory</b> service property for this service.
	 */
	public static final String	INPUT_CLUSTERS	= "zigbee.endpoint.clusters.input";

	/**
	 * Property containing a list of output clusters. This list is contained
	 * also in the ZigBee Simple Descriptor of the ZigBeeEndpoint service.
	 * property is of type int[].
	 * <p>
	 * It is a <b>mandatory</b> service property for this service.
	 */
	public static final String	OUTPUT_CLUSTERS	= "zigbee.endpoint.clusters.output";

	/**
	 * Property used to mark if a ZigBeeEndPoint service is an exported one or
	 * not. Imported endpoints do not have this property set. This service
	 * property requires no specific values.
	 */
	public static final String	ZIGBEE_EXPORT	= "zigbee.export";

	/**
	 * Constant used by all ZigBee devices indicating the device category. It is
	 * a <b>mandatory</b> service property for this service.
	 */
	public static final String	DEVICE_CATEGORY	= "ZigBee";

	/**
	 * Returns the identifier of this endpoint, that is the Endpoint ID.
	 * 
	 * @return the identifier of this endpoint, value ranges from 1 to 240.
	 */
	public short getId();

	/**
	 * Returns the IEEE Address of the node containing this endpoint.
	 * 
	 * @return the IEEE Address of the node containing this endpoint.
	 */
	public BigInteger getNodeAddress();

	/**
	 * Returns the simple descriptor of this endpoint. As described in "Table
	 * 2.93 Fields of the Simple_Desc_rsp Command" of the ZigBee specification
	 * 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a simple_decr request can
	 * have the following status: {@link ZDPException#SUCCESS},
	 * {@link ZDPException#INVALID_EP}, {@link ZDPException#NOT_ACTIVE},
	 * {@link ZDPException#DEVICE_NOT_FOUND},
	 * {@link ZDPException#INV_REQUESTTYPE} or
	 * {@link ZDPException#NO_DESCRIPTOR}.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         {@link Promise#getValue()} returns the node simple descriptor
	 *         {@link ZigBeeSimpleDescriptor} in case of success and
	 *         {@link Promise#getFailure()} returns the adequate
	 *         {@link ZDPException} otherwise.
	 * 
	 */
	public Promise /* <ZigBeeSimpleDescriptor> */ getSimpleDescriptor();

	/**
	 * Returns an array of server (input) clusters.
	 * 
	 * @return an array of server (input) clusters, returns an empty array if it
	 *         does not provide any server cluster.
	 */
	public ZCLCluster[] getServerClusters();

	/**
	 * Returns the server (input) cluster identified by the given identifier.
	 * 
	 * @param serverClusterId The server(input) cluster identifier.
	 * @throws IllegalArgumentException If the passed argument is outside the
	 *         range [0, 0xffff].
	 * @return the server (input) cluster identified by the given identifier, or
	 *         null if the given id is not listed in the simple descriptor.
	 */
	public ZCLCluster getServerCluster(int serverClusterId);

	/**
	 * Returns an array of client (output) clusters.
	 * 
	 * @return an array of client (output) clusters, returns an empty array if
	 *         does not provides any clients clusters.
	 */
	public ZCLCluster[] getClientClusters();

	/**
	 * Returns the client cluster identified by the cluster identifier.
	 * 
	 * @param clientClusterId The client(output) cluster identifier.
	 * 
	 * @throws IllegalArgumentException If the passed argument is outside the
	 *         range [0, 0xffff].
	 * @return the client(output) cluster identified by the cluster identifier,
	 *         or null if the given id is not listed in the simple descriptor.
	 * 
	 */
	public ZCLCluster getClientCluster(int clientClusterId);

	/**
	 * Adds the following entry in the <i>Binding Table</i> of the device:
	 * <p>
	 * <code>this.getNodeAddress()</code>, <code>this.getId()</code>,
	 * <code>clusterId</code>, <code>device.getNodeAddress()</code>,
	 * <code>device.getId()</code>
	 * <p>
	 * As described in "Table 2.7 APSME-BIND.confirm Parameters" of the ZigBee
	 * specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a binding
	 * request can have the following results: {@link APSException#SUCCESS},
	 * {@link APSException#ILLEGAL_REQUEST}, {@link APSException#TABLE_FULL},
	 * {@link APSException#NOT_SUPPORTED}.
	 * 
	 * @param servicePid the PID of the endpoint to bind to
	 * @param clusterId the cluster identifier to bind to
	 * @return A promise representing the completion of this asynchronous call.
	 *         {@link Promise#getFailure()} returns null if the cluster has been
	 *         successfully bound. The adequate {@link ZigBeeEndpoint} is
	 *         returned otherwise.
	 */
	public Promise /* <Void> */ bind(String servicePid, int clusterId);

	/**
	 * Removes the following entry in the <i>Binding Table</i> of the device if
	 * it exists:
	 * <p>
	 * <code>this.getNodeAddress()</code>, <code>this.getId()</code>,
	 * <code>clusterId</code>, <code>device.getNodeAddress()</code>,
	 * <code>device.getId()</code>
	 * 
	 * <p>
	 * As described in "Table 2.9 APSME-UNBIND.confirm Parameters" of the ZigBee
	 * specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, an unbind
	 * request can have the following results: {@link APSException#SUCCESS},
	 * {@link APSException#ILLEGAL_REQUEST},
	 * {@link APSException#INVALID_BINDING}.
	 * 
	 * @param servicePid The pid of the service to unbind.
	 * @param clusterId The cluster identifier to unbind.
	 * @return A promise representing the completion of this asynchronous call.
	 *         {@link Promise#getFailure()} returns null if the cluster has been
	 *         successfully bound. The adequate {@link APSException} is returned
	 *         otherwise.
	 */
	public Promise /* <Void> */ unbind(String servicePid, int clusterId);

	/**
	 * Notifies that the base driver is unable to export this endpoint. This
	 * method is called by the base driver and used to give details about issues
	 * preventing the export of an endpoint.
	 * 
	 * @param e A device {@link ZigBeeException} the occurred exception.
	 */
	public void notExported(ZigBeeException e);

	/**
	 * Returns bound endpoints (identified by their service PIDs) on a specific
	 * cluster ID. It is implemented on the base driver with Mgmt_Bind_req
	 * command. It is implemented without a command request in local endpoints.
	 * 
	 * <p>
	 * As described in "Table 2.129 Fields of the Mgmt_Bind_rsp Command" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * Mgmt_Bind_rsp command can have the following status:
	 * {@link APSException#NOT_SUPPORTED} or any status code returned from the
	 * APSME-GET.confirm primitive (see {@link APSException}).
	 * 
	 * @param clusterId the cluster identifier of the targeted bindings.
	 * @return A promise representing the completion of this asynchronous call.
	 *         {@link Promise#getValue()} returns a List of the bound endpoint
	 *         service PIDs if the command is successful. The response object is
	 *         null and the adequate {@link APSException} is returned by
	 *         {@link Promise#getFailure()} otherwise.
	 */
	public Promise/* <List<String>> */ getBoundEndPoints(int clusterId);

}
