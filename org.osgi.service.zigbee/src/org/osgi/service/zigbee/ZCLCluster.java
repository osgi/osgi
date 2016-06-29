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

import java.util.Map;

/**
 * This interface represents a ZCL Cluster
 * 
 * @author $Id$
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
	 * Get the cluster {@link ZCLAttribute} identifying corresponding attribute
	 * that matches the given attributeId.
	 * 
	 * @param attributeId an Attribute identifier
	 * @param handler the response handler. The
	 *        {@link ZigBeeHandler#onSuccess(Object)} will be invoked with the
	 *        proper {@link ZCLAttribute}
	 * 
	 * @see ZCLCluster#getAttribute(int, int, ZigBeeHandler) To get Manufacturer
	 *      specific attribute use ZCLCluster#getAttribute(int, int,
	 *      ZigBeeHandler)
	 */
	void getAttribute(int attributeId, ZigBeeHandler handler);

	/**
	 * Get the cluster {@link ZCLAttribute} identifying corresponding attribute
	 * of a specific Manufacturer or the standard attribute
	 * 
	 * @param attributeId an Attribute identifier
	 * @param code the int representing the Manufacturer code for getting the
	 *        vendor specific attribute, use -1 if looking for standard
	 *        attribute
	 * @param handler the response handler. The
	 *        {@link ZigBeeHandler#onSuccess(Object)} will be invoked with the
	 *        proper {@link ZCLAttribute}
	 */
	void getAttribute(int attributeId, int code, ZigBeeHandler handler);

	/**
	 * Get an array of {@link ZCLAttribute} representing all this Cluster's
	 * Attributes. This method returns only standard attributes
	 * 
	 * @param handler the response handler. The
	 *        {@link ZigBeeHandler#onSuccess(Object)} will be invoked an array
	 *        of {@link ZCLAttribute}
	 * 
	 * @see ZCLCluster#getAttributes(int, ZigBeeHandler) To get Manufacturer
	 *      specific attribute use ZCLCluster#getAttributes(int, ZigBeeHandler)
	 */
	void getAttributes(ZigBeeHandler handler);

	/**
	 * Get an array of {@link ZCLAttribute} representing all this Cluster's
	 * Attributes. This method returns only standard attributes when using -1 as
	 * code or vendor specific attribute when invoked with the proper code
	 * 
	 * @param code the int representing the Manufacturer code for getting the
	 *        vendor specific attribute, use -1 if looking for standard
	 *        attribute
	 * @param handler the response handler. The
	 *        {@link ZigBeeHandler#onSuccess(Object)} will be invoked an array
	 *        of {@link ZCLAttribute}
	 */
	void getAttributes(int code, ZigBeeHandler handler);

	/**
	 * Read a list of attributes.
	 * 
	 * <p>
	 * As described in "2.4.1.3 Effect on Receipt" chapter of the ZCL, a
	 * "read attribute" can have the following status: SUCCESS, or
	 * UNSUPPORTED_ATTRIBUTE (see {@link ZCLException}).
	 * 
	 * <p>
	 * The response object given to the handler is a Map. For each Map entry,
	 * the key is the attribute identifier of Integer type and the value is the
	 * associated attribute value in the corresponding Java wrapper type (or
	 * null if an UNSUPPORTED_ATTRIBUTE occurred or in case of an invalid
	 * value).
	 * 
	 * <p>
	 * <b>NOTE</b> Considering the ZigBee Specification all the attributes must
	 * be standard attributes or belong to the same Manufacturer otherwise
	 * {@link IllegalArgumentException} will be thrown
	 * 
	 * @param attributes An array of ZCLAttributeInfo
	 * @param handler the response handler
	 * @throws NullPointerException the attribute array cannot be null
	 * 
	 * @throws IllegalArgumentException if some of {@link ZCLAttributeInfo} are
	 *         manufacturer specific and other are standard, or even if there
	 *         are mix of attributes with different manufacturer specific code,
	 *         Or if the attributes array is empty
	 */
	void readAttributes(ZCLAttributeInfo[] attributes, ZigBeeHandler handler);

	/**
	 * Write a list of attributes.
	 * 
	 * <p>
	 * As described in "2.4.3.3 Effect on Receipt" chapter of the ZCL, a
	 * "write attribute" can have the following status: SUCCESS,
	 * UNSUPPORTED_ATTRIBUTE, INVALID_DATA_TYPE, READ_ONLY, INVALID_VALUE (see
	 * {@link ZCLException}), or NOT_AUTHORIZED (see {@link ZDPException}).
	 * 
	 * <p>
	 * The response object given to the handler is a Map. For each Map entry,
	 * the key is the attribute identifier of Integer type and the value is the
	 * associated attribute status (see above). Every null value in the Map is
	 * considered as an invalid number. In case undivided equals false,
	 * onSuccess() is always called to notify the response. In case undivided
	 * equals true and an error has occurred, onFailure is called with a
	 * ZCLException.
	 * 
	 * <p>
	 * <b>NOTE</b>Considering the ZigBee Specification all the attributes must
	 * be standard attributes or belong to the same Manufacturer otherwise
	 * {@link IllegalArgumentException} will be thrown
	 * 
	 * @param undivided The write command is undivided or not
	 * @param attributesAndValues A Map<ZCLAttributeInfo, Object> of attributes,
	 *        and values to be written.
	 * @param handler the response handler
	 * 
	 * @throws IllegalArgumentException if some of {@link ZCLAttributeInfo} are
	 *         manufacturer specific and other are standard, or even if there
	 *         are mix of attributes with different manufacturer specific code
	 */
	void writeAttributes(boolean undivided, Map attributesAndValues, ZigBeeHandler handler);

	/**
	 * Get an array of all the commandIds of the ZCLCluster.
	 * 
	 * <p>
	 * This method is implemented for ZCL devices compliant version equal or
	 * later than 1.2 of the Home Automation Profile or other profiles that
	 * enable the discovery of command IDs as a general command. When the device
	 * implements a profile that does not support this feature, the method call
	 * throws a {@code ZCLException} with code
	 * {@code ZCLException.GENERAL_COMMAND_NOT_SUPPORTED}.
	 * 
	 * <p>
	 * The response object given to the handler is an array containing the
	 * commandIds. Each commandId is of Integer type.
	 * 
	 * @param handler the response handler
	 */
	void getCommandIds(ZigBeeHandler handler);

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
