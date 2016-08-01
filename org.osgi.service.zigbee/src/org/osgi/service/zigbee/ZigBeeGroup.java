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

import org.osgi.util.promise.Promise;

/**
 * This interface represents a ZigBee Group
 * 
 * @author $Id$
 */
public interface ZigBeeGroup {

	/**
	 * Key of the {@link String} containing the Group Address of the device.<br>
	 * It is a <b>mandatory</b> property for this service.
	 */
	public static final String ID = "zigbee.group.id";

	/**
	 * @return The 16 bit group address.
	 */
	int getGroupAddress();

	/**
	 * This method is used for adding an endpoint to a Group, it may be invoked
	 * on exported endpoints or even on imported endpoints. In the former case,
	 * the ZigBee Base Driver should rely on the <i>APSME-ADD-GROUP</i> API
	 * defined by the ZigBee Specification, or it will use the proper commands
	 * of the <i>Groups</i> cluster of the ZigBee Specification Library.
	 * 
	 * As described in "Table 2.15 APSME-ADD-GROUP.confirm Parameters" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * add_group request can have the following status: SUCCESS,
	 * INVALID_PARAMETER or TABLE_FULL (see {@link APSException}).
	 * 
	 * @param pid {@link String} representing the service PID of the
	 *        {@link ZigBeeEndpoint} to add to this Group.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         The expected object is always a {@link Boolean} indicating a
	 *         failure or a success.
	 * 
	 * @throws APSException when the joining is performed locally on an exported
	 *         {@link ZigBeeEndpoint} and it fails either with error code
	 *         INVALID_PARAMETER or TABLE_FULL. This exception is also generated
	 *         when the joining is performed remotely on an imported
	 *         {@link ZigBeeEndpoint} and the communication with it fails.
	 * 
	 * @throws ZCLException when the joining is performed remotely on an
	 *         imported {@link ZigBeeEndpoint} and it fails either because the
	 *         command is not supported by the remote End Point, or the remote
	 *         device cannot perform the operation at the moment.
	 */
	Promise /* <Boolean> */ joinGroup(String pid);

	/**
	 * This method is used for adding an Endpoint to a Group, it may be invoked
	 * on exported Endpoints or even on imported Endpoints. In the former case,
	 * the ZigBee Base Driver should rely on the <i>APSME-REMOVE-GROUP </i> API
	 * defined by the ZigBee Specification, or it will use the proper commands
	 * of the <i>Groups</i> cluster of the ZigBee Specification Library.
	 * 
	 * As described in "Table 2.17 APSME-REMOVE-GROUP.confirm Parameters" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * remove_group request can have the following status: SUCCESS,
	 * INVALID_GROUP or INVALID_PARAMETER (see {@link APSException}).
	 * 
	 * @param pid {@link String} representing the service PID of the
	 *        {@link ZigBeeEndpoint} to remove from this Group.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         The expected object is always a {@link Boolean} indicating a
	 *         failure or a success.
	 * 
	 * @throws APSException when the joining is performed locally on an exported
	 *         {@link ZigBeeEndpoint} and it fails either with error code
	 *         INVALID_PARAMETER or INVALID_GROUP. This exception is also
	 *         generated when the joining is performed remotely on an imported
	 *         {@link ZigBeeEndpoint} and the communication with it fails.
	 * 
	 * @throws ZCLException when the joining is performed remotely on an
	 *         imported {@link ZigBeeEndpoint} and it fails either because the
	 *         command is not supported by the remote End Point, or the remote
	 *         device cannot perform the operation at the moment.
	 */
	Promise /* <Boolean> */ leaveGroup(String pid);

	/**
	 * Send a ZCL frame to the ZigBee group represented by this service. The
	 * handler will provide the invocation response(s) in an asynchronous way.
	 * 
	 * <p>
	 * The source endpoint is not specified in this method call. To send the
	 * appropriate message on the network, the base driver must generate a
	 * source endpoint. The latter must not correspond to any exported endpoint.
	 * 
	 * @param clusterId a cluster identifier.
	 * @param frame a command frame sequence.
	 * 
	 * @return a {@link ZCLCommandResponseStream} to collect all the ZCL frames
	 *         in case of multiple responses.
	 */
	ZCLCommandResponseStream groupcast(int clusterId, ZCLFrame frame);

	/**
	 * Send a ZCL frame to the ZigBee group represented by this service. The
	 * handler will provide the invocation response(s) in an asynchronous way.
	 * 
	 * <p>
	 * This method is to be used by applications when the targeted device has to
	 * distinguish between source endpoints of the message. For instance, alarms
	 * cluster (see 3.11 Alarms Cluster in [ZCL]) generated events are
	 * differently interpreted if they come from the oven or from the intrusion
	 * alert system.
	 * 
	 * @param clusterId a cluster identifier.
	 * @param frame a command frame sequence.
	 * @param exportedServicePID : the source endpoint of the command request.
	 *        In targeted situations, the source endpoint is the valid service
	 *        PID of an exported endpoint.
	 * @return a {@link ZCLCommandResponseStream} to collect the ZCL frames in
	 *         case of multiple responses.
	 */
	ZCLCommandResponseStream groupcast(int clusterId, ZCLFrame frame, String exportedServicePID);

}
