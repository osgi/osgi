/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
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

/**
 * This interface represents a ZigBee Group
 * 
 * @version 1.0
 * 
 * @author see RFC 192 authors: Andre Bottaro, Arnaud Rinquin, Jean-Pierre
 *         Poutcheu, Fabrice Blache, Christophe Demottie, Antonin Chazalet,
 *         Evgeni Grigorov, Nicola Portinaro, Stefano Lenzi.
 */
public interface ZigBeeGroup {

	/**
	 * Key of the {@link String} containing the Group Address of the device.<br>
	 * It is a <b>mandatory</b> property for this service.
	 */
	public static final String	ID	= "zigbee.group.id";

	/**
	 * @return The 16bit group address.
	 */
	short getGroupAddress();

	/**
	 * This method is used for adding an Endpoint to a Group, it may be invoked
	 * on exported Endpoint or even on import Endpoint. In the former case, the
	 * ZigBee Base Driver should rely on the <i>APSME-ADD-GROUP</i> API defined
	 * by the ZigBee Specification, in the former case it will use the proper
	 * commands of the <i>Groups</i> cluster of the ZigBee Specification
	 * Library.
	 * 
	 * As described in "Table 2.15 APSME-ADD-GROUP.confirm Parameters" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * add_group request can have the following status: SUCCESS,
	 * INVALID_PARAMETER or TABLE_FULL (see {@link APSException}).
	 * 
	 * @param pid {@link String} representing the PID (see
	 *        {@link org.osgi.framework.Constants#SERVICE_PID} ) of the
	 *        {@link ZigBeeEndpoint} that we want add to this Group.
	 * @param handler the handler that will notified of the result of "joining".
	 */
	void joinGroup(String pid, ZCLCommandHandler handler);

	/**
	 * This method is used for adding an Endpoint to a Group, it may be invoked
	 * on exported Endpoint or even on import Endpoint. In the former case, the
	 * ZigBee Base Driver should rely on the <i>APSME-REMOVE-GROUP </i> API
	 * defined by the ZigBee Specification, in the former case it will use the
	 * proper commands of the <i>Groups</i> cluster of the ZigBee Specification
	 * Library.
	 * 
	 * As described in "Table 2.17 APSME-REMOVE-GROUP.confirm Parameters" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * remove_group request can have the following status: SUCCESS,
	 * INVALID_GROUP or INVALID_PARAMETER (see {@link APSException}).
	 * 
	 * @param pid {@link String} representing the PID (see
	 *        {@link org.osgi.framework.Constants#SERVICE_PID} ) of the
	 *        {@link ZigBeeEndpoint} that we want leave to this Group.
	 * @param handler the handler that will notified of the result of "leaving".
	 */
	void leaveGroup(String pid, ZCLCommandHandler handler);

	/**
	 * Invokes the action on a Group. The handler will provide the invocation
	 * response in an asynchronously way.
	 * 
	 * The source endpoint is not specified in this method call. To send the
	 * appropriate message on the network, the base driver must generate a
	 * source endpoint. The latter must not correspond to any exported endpoint.
	 * 
	 * @param clusterId a cluster identifier.
	 * @param frame a command frame sequence.
	 * @param handler The handler that manages the command response.
	 */
	void invoke(Integer clusterId, ZCLFrame frame, ZCLCommandHandler handler);

	/**
	 * This method is to be used by applications when the targeted device has to
	 * distinguish between source endpoints of the message. For instance, alarms
	 * cluster (see 3.11 Alarms Cluster in [ZCL]) generated events are
	 * differently interpreted if they come from the oven or from the intrusion
	 * alert system.
	 * 
	 * @param clusterId a cluster identifier.
	 * @param frame a command frame sequence.
	 * @param handler The handler that manages the command response.
	 * @param exportedServicePID : the source endpoint of the command request.
	 *        In targeted situations, the source endpoint is the valid service
	 *        PID of an exported endpoint.
	 */
	void invoke(Integer clusterId, ZCLFrame frame, ZCLCommandHandler handler, String exportedServicePID);

}
