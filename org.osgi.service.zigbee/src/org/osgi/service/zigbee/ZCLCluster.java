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
	 * Get the ZigBeeAttribute corresponding to given attributeId.
	 * 
	 * @param attributeId an Attribute identifier
	 * @param handler the response handler
	 * @return the cluster attribute identified by id
	 */
	void getAttribute(int attributeId, ZigBeeMapHandler handler);

	/**
	 * Get an array of all this Cluster's ZigBeeAttributes.
	 * 
	 * @param handler the response handler
	 */
	void getAttributes(ZigBeeMapHandler handler);

	/**
	 * Read a list of attributes
	 * 
	 * @param attributesIds An array of attributes ids
	 * @param handler the response handler
	 */
	void readAttributes(int[] attributesIds, ZigBeeMapHandler handler);

	/**
	 * Write a list of attributes
	 * 
	 * @param undivided The write command is undivided or not
	 * @param attributesRecords An array of attributes records
	 * @param handler the response handler
	 */
	void writeAttributes(boolean undivided, ZigBeeAttributeRecord[] attributesRecords, ZigBeeMapHandler handler);

	/**
	 * Get an array of all the commandIds of the ZigBeeCluster.
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
	 * @throws ZCLException
	 */
	void invoke(ZCLFrame frame, ZigBeeCommandHandler handler) throws ZCLException;

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
	 * @throws ZCLException
	 */
	void invoke(ZCLFrame frame, ZigBeeCommandHandler handler, String exportedServicePID) throws ZCLException;

}
