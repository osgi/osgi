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
 * This interface represents a ZigBee Group.
 * 
 * @noimplement
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
	 * Returns the 16 bit group address.
	 * 
	 * @return the 16 bit group address.
	 */
	int getGroupAddress();

	/**
	 * Requests an endpoint to join this group. This method may be invoked on
	 * exported and imported endpoints. In the former case, the ZigBee Base
	 * Driver should rely on the <i>APSME-ADD-GROUP</i> API defined by the
	 * ZigBee Specification, or it will use the proper commands of the
	 * <i>Groups</i> cluster of the ZigBee Specification Library.
	 * 
	 * As described in "Table 2.15 APSME-ADD-GROUP.confirm Parameters" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, an
	 * add_group request can have the following status: SUCCESS,
	 * INVALID_PARAMETER or TABLE_FULL (see {@link APSException}). When the
	 * joining is performed remotely on an imported {@link ZigBeeEndpoint}, it
	 * may also fail because the command is not supported by the remote
	 * endpoint, or because the remote device cannot perform the operation at
	 * the moment (see {@link ZCLException}).
	 * 
	 * @param pid {@link String} representing the service PID of the
	 *        {@link ZigBeeEndpoint} to add to this Group.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         {@link Promise#getFailure()} returns null if the cluster has been
	 *         successfully bound. The adequate {@link ZigBeeException} is
	 *         returned otherwise.
	 */
	Promise /* <void> */ joinGroup(String pid);

	/**
	 * Requests an endpoint to leave this group. This method may be invoked on
	 * exported and imported endpoints. In the former case, the ZigBee Base
	 * Driver should rely on the <i>APSME-REMOVE-GROUP </i> API defined by the
	 * ZigBee Specification, or it will use the proper commands of the
	 * <i>Groups</i> cluster of the ZigBee Specification Library.
	 * 
	 * As described in "Table 2.17 APSME-REMOVE-GROUP.confirm Parameters" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * remove_group request can have the following status: SUCCESS,
	 * INVALID_GROUP or INVALID_PARAMETER (see {@link APSException}). When the
	 * command is invoked remotely on an imported {@link ZigBeeEndpoint}, it may
	 * also fail because the command is not supported by the remote endpoint, or
	 * because the remote device cannot perform the operation at the moment (see
	 * {@link ZCLException}).
	 * 
	 * @param pid {@link String} representing the service PID of the
	 *        {@link ZigBeeEndpoint} to remove from this Group.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         {@link Promise#getFailure()} returns null if the cluster has been
	 *         successfully bound. The adequate {@link ZigBeeException} is
	 *         returned otherwise.
	 */
	Promise /* <void> */ leaveGroup(String pid);

	/**
	 * Sends a ZCL frame to the group represented by this service. The returned
	 * stream will provide the invocation response(s) in an asynchronous way.
	 * 
	 * <p>
	 * The source endpoint is not specified in this method call. To send the
	 * appropriate message on the network, the base driver must generate a
	 * source endpoint. The latter must not correspond to any exported endpoint.
	 * 
	 * @param clusterId a cluster identifier.
	 * @param frame a command frame sequence.
	 * 
	 * @return a {@link ZCLCommandResponseStream} to collect every ZCL frame one
	 *         after the other in case of multiple responses.
	 */
	ZCLCommandResponseStream groupcast(int clusterId, ZCLFrame frame);

	/**
	 * Sends a ZCL frame to the ZigBee group represented by this service. The
	 * returned stream will provide the invocation response(s) in an
	 * asynchronous way.
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
	 * @return a {@link ZCLCommandResponseStream} to collect every ZCL frame one
	 *         after the other in case of multiple responses.
	 */
	ZCLCommandResponseStream groupcast(int clusterId, ZCLFrame frame, String exportedServicePID);

}
