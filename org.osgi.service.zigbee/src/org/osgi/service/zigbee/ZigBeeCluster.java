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

import org.osgi.service.zigbee.descriptions.ZigBeeClusterDescription;

/**
 * This interface represents a ZigBee Cluster
 * 
 * @version 1.0
 */
public interface ZigBeeCluster {
	/**
	 * Property key for the optional cluster id. A ZigBee Event Listener service
	 * can announce for what ZigBee clusters it wants notifications.
	 */
	public final static String	ID		= "zigbee.listener.cluster.id";

	/**
	 * Property key for the optional cluster domain. A ZigBee Event Listener
	 * service can announce for what ZigBee clusters domains it wants
	 * notifications.
	 */
	public final static String	DOMAIN	= "zigbee.listener.cluster.domain";

	/**
	 * Property key for the optional cluster name. A ZigBee Event Listener
	 * service can announce for what ZigBee clusters it wants notifications.
	 */
	public final static String	NAME	= "zigbee.listener.cluster.name";

	/**
	 * @return the cluster identifier
	 */
	public int getId();

	/**
	 * @param attributeId an Attribute identifier
	 * @return the cluster attribute identified by id
	 */
	public ZigBeeAttribute getAttribute(int attributeId);

	/**
	 * @return an array of all the attributes of the cluster.
	 */
	public ZigBeeAttribute[] getAttributes();

	/**
	 * @param commandId command identifier
	 * @return the command identified by id
	 */
	public ZigBeeCommand getCommand(int commandId);

	/**
	 * @return an array of all the commands of the cluster
	 */
	public ZigBeeCommand[] getCommands();

	/**
	 * @return if exists, the cluster description - otherwise returns null.
	 */
	public ZigBeeClusterDescription getDescription();

	/**
	 * Read a list of attributes
	 * 
	 * @param attributesIds An array of attributes ids
	 * @param handler The response handler
	 */
	public void readAttributes(int[] attributesIds, ZigBeeHandler handler);

	/**
	 * Read a list of attributes. Each readed attributes will be represented as
	 * a byte array
	 * 
	 * @param attibutesIds An array of attributes ids
	 * @param handler The response handler
	 */
	public void readAttributesAsBytes(int[] attibutesIds, ZigBeeHandler handler);

	/**
	 * Write a list of attributes
	 * 
	 * @param undivided The write command is undivided or not
	 * @param attributesRecords An array of attributes records
	 * @param handler The response handler
	 */
	public void writeAttributes(boolean undivided, ZigBeeAttributeRecord[] attributesRecords, ZigBeeHandler handler);

	/**
	 * Write a list of attributes
	 * 
	 * @param undivided The write command is undivided or not
	 * @param attributesIds An array of attributes ids
	 * @param values A byte array representing attributes values
	 * @param handler The response handler
	 * @throws ZigBeeNoDescriptionAvailableException
	 */
	public void writeAttributes(boolean undivided, int[] attributesIds, byte[] values, ZigBeeHandler handler) throws ZigBeeNoDescriptionAvailableException;

}
