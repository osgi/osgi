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

/**
 * This interface represents a ZCL Cluster
 * 
 * @version 1.0
 * 
 * @author see RFC 192 authors: Andre Bottaro, Arnaud Rinquin, Jean-Pierre
 *         Poutcheu, Fabrice Blache, Christophe Demottie, Antonin Chazalet,
 *         Evgeni Grigorov, Nicola Portinaro, Stefano Lenzi.
 */
public interface ZCLCluster {

	/**
	 * Property key for the optional cluster id. A ZigBee Event Listener service
	 * can announce for what ZigBee clusters it wants notifications.
	 */
	public final static String	ID		= "zigbee.cluster.id";

	/**
	 * Property key for the optional cluster domain. A ZigBee Event Listener
	 * service can announce for what ZigBee clusters domains it wants
	 * notifications.
	 */
	public final static String	DOMAIN	= "zigbee.cluster.domain";

	/**
	 * Property key for the optional cluster name. A ZigBee Event Listener
	 * service can announce for what ZigBee clusters it wants notifications.
	 */
	public final static String	NAME	= "zigbee.cluster.name";

	/**
	 * @return the cluster identifier
	 */
	int getId();

	/**
	 * Get the cluster attribute identified corresponding to given attributeId.
	 * 
	 * @param attributeId an Attribute identifier
	 * @param handler the response handler
	 */
	void getAttribute(int attributeId, ZigBeeHandler handler);

	/**
	 * Get an array of all this Cluster's Attributes.
	 * 
	 * @param handler the response handler
	 */
	void getAttributes(ZigBeeMapHandler handler);

	/**
	 * Read a list of attributes.
	 * 
	 * As described in "Table 2.11 APSME-GET.confirm Parameters" of the ZigBee
	 * specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * APSME-GET.confirm can have the following status: SUCCESS, or
	 * UNSUPPORTED_ATTRIBUTE (see {@link APSException}).
	 * 
	 * @param attributesIds An array of attributes ids
	 * @param handler the response handler
	 */
	void readAttributes(int[] attributesIds, ZigBeeMapHandler handler);

	/**
	 * Write a list of attributes.
	 * 
	 * As described in "Table 2.13 APSME-SET.confirm Parameters" of the ZigBee
	 * specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * APSME-SET.confirm can have the following status: SUCCESS,
	 * INVALID_PARAMETER or UNSUPPORTED_ATTRIBUTE (see {@link APSException}).
	 * 
	 * @param undivided The write command is undivided or not
	 * @param attributesRecords An array of attributes records
	 * @param handler the response handler
	 */
	void writeAttributes(boolean undivided, ZCLAttributeRecord[] attributesRecords, ZigBeeMapHandler handler);

	/**
	 * Get an array of all the commandIds of the ZigBeeCluster.
	 * 
	 * This method is implemented for devices implementing a version equal or
	 * later than 1.2 of the Home Automation Profile or other profiles that
	 * enables the discovery of command IDs as a general command. When the
	 * device implements a profile that does not support this feature, the
	 * method call throws a ZCLException with code
	 * GENERAL_COMMAND_NOT_SUPPORTED.
	 * 
	 * @param handler the response handler
	 */
	void getCommandIds(ZigBeeMapHandler handler);

	/**
	 * Invokes the action. The handler will provide the invocation response in
	 * an asynchronously way.
	 * 
	 * The source endpoint is not specified in this method call. To send the
	 * appropriate message on the network, the base driver must generate a
	 * source endpoint. The latter must not correspond to any exported endpoint.
	 * 
	 * @param frame a command frame sequence.
	 * @param handler The handler that manages the command response.
	 */
	void invoke(ZCLFrame frame, ZCLCommandHandler handler);

	/**
	 * This method is to be used by applications when the targeted device has to
	 * distinguish between source endpoints of the message. For instance, alarms
	 * cluster (see 3.11 Alarms Cluster in [ZCL]) generated events are
	 * differently interpreted if they come from the oven or from the intrusion
	 * alert system.
	 * 
	 * @param frame a command frame sequence.
	 * @param handler The handler that manages the command response.
	 * @param exportedServicePID : the source endpoint of the command request.
	 *        In targeted situations, the source endpoint is the valid service
	 *        PID of an exported endpoint.
	 */
	void invoke(ZCLFrame frame, ZCLCommandHandler handler, String exportedServicePID);

}
