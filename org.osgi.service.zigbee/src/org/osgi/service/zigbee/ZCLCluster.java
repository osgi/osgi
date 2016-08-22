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
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;
import org.osgi.util.promise.Promise;

/**
 * This interface represents a ZCL Cluster. Along with methods to retrieve the
 * cluster information, like its ID, it provides methods to asynchronously send
 * commands to the cluster and other methods that wrap most of the ZCL general
 * commands.
 * 
 * <p>
 * Every asynchronous method defined in this interface returns back its result
 * trough the use of a {@link Promise}.
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
	 * Get the cluster {@link ZCLAttribute} identifying that matches the given
	 * attributeId. {@link ZCLCluster#getAttribute(int, int)} method retrieves
	 * manufacturer-specific attributes.
	 * 
	 * @param attributeId the ZCL attribute identifier
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         In case of success in getting the attribute, the promise will be
	 *         resolved with a {@link ZCLAttribute} instance. If attributeId do
	 *         not exist in the cluster, then the promise fails with a
	 *         {@link ZCLException} with status code
	 *         {@link ZCLException#UNSUPPORTED_ATTRIBUTE}
	 */
	Promise /* <ZCLAttribute> */ getAttribute(int attributeId);

	/**
	 * Retrieve a {@link ZCLAttribute} object for a manufacturer specific
	 * attribute. If the {@code code} parameter is -1 it behaves like the
	 * {@link ZCLCluster#getAttribute(int)} and retrieves the non-manufacturer
	 * specific attribute {@code attributeId}.
	 * 
	 * @param attributeId the ZCL attribute identifier
	 * @param code the manufacturer code of the attribute to be retrieved. If -1
	 *        is used, the method behaves exactly like
	 *        {@link ZCLCluster#getAttribute(int)}
	 * 
	 * @return A {@link Promise} representing the completion of this
	 *         asynchronous call. The promise will be resolved with the
	 *         requested {@link ZCLAttribute}. If a command such as ZCL Read
	 *         Attributes or Discover Attributes has already been called once by
	 *         the ZigBee host, the Promise can be quickly resolved. The
	 *         resolution may be longer the first time one of the ZCLCluster
	 *         methods to get one or all attributes is successfully called. If
	 *         attributeId do not exist in the cluster, then the promise fails
	 *         with a {@link ZCLException} with status code
	 *         {@link ZCLException#UNSUPPORTED_ATTRIBUTE}
	 */
	Promise /* <ZCLAttribute> */ getAttribute(int attributeId, int code);

	/**
	 * Get an array of {@link ZCLAttribute} objects representing all this
	 * cluster's attributes.
	 * 
	 * <p>
	 * This method returns only standard attributes. To retrieve manufacturer
	 * specific attributes use method {@link ZCLCluster#getAttributes(int)}
	 * 
	 * @return A {@link Promise} representing the completion of this
	 *         asynchronous call. The promise will be resolved with an array of
	 *         {@link ZCLAttribute} objects.
	 *
	 */
	Promise /* <ZCLAttribute[]> */ getAttributes();

	/**
	 * Get an array of {@link ZCLAttribute} objects representing all the
	 * specific manufacturer attributes available on the cluster.
	 * 
	 * <p>
	 * This method behaves like the {@link ZCLCluster#getAttributes()} method if
	 * the passed {@code} value is -1.
	 * 
	 * @param code The the manufacturer code. Pass -1 to retrieve standard (i.e.
	 *        non-manufacturer specific) attributes.
	 * 
	 * @return A {@link Promise} representing the completion of this
	 *         asynchronous call. The promise will be resolved with an array of
	 *         {@link ZCLAttribute} objects. If a command such as ZCL Read
	 *         Attributes or Discover Attributes has already been called once by
	 *         the ZigBee host, the Promise can be quickly resolved. The
	 *         resolution may be longer the first time one of the ZCLCluster
	 *         methods to get one or all attributes is successfully called.
	 */
	Promise /* <ZCLAttribute[]> */ getAttributes(int code);

	/**
	 * Read a list of attributes by issuing a ZCL Read Attributes command. The
	 * attribute list is provided in terms of an array of
	 * {@link ZCLAttributeInfo} objects.
	 * 
	 * <p>
	 * As described in <em>§2.4.1.3 Effect on Receipt</em> section of the ZCL
	 * specification, a <em>Read Attributes</em> command results in a list of
	 * attribute status records comprising a mix of successful and unsuccessful
	 * attribute reads.
	 * 
	 * <p>
	 * The method returns a promise. The object used to resolve the
	 * {@link Promise} is a {@code Map<Integer,
	 * Object>}. For each Map entry, the key contains the attribute identifier
	 * and the value, the attribute value in the corresponding java wrapper type
	 * (or null in case of an unsupported attribute or in case of an invalid
	 * value). For attributes which data type serialization is not supported
	 * (i.e, {@link ZCLDataTypeDescription#getJavaDataType()} returns null), the
	 * value is of type byte[].
	 * 
	 * <p>
	 * When the list of attributes do not fit into a single ZCLFrame, ZigBee
	 * clusters truncate the list of attributes returned in the response. In
	 * import situations, the base driver send multiple read attributes commands
	 * until being able to resolve the promise with a full table of all the
	 * attribute values requested by the ZigBee client. In export situations,
	 * the base driver truncates the read attribute command response as
	 * necessary.
	 * 
	 * <p>
	 * <b>NOTE:</b> According to the ZigBee Specification all the attributes
	 * must be standard attributes or belong to the same manufacturer code,
	 * otherwise the promise must fail with a {@link IllegalArgumentException}
	 * exception .
	 * 
	 * @param attributes An array of {@link ZCLAttributeInfo}.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         The promise may fail with an {@link IllegalArgumentException}
	 *         exception, if some of {@link ZCLAttributeInfo} are manufacturer
	 *         specific and other are standard, or even if there are mix of
	 *         attributes with different manufacturer specific code.
	 * 
	 */
	Promise /* <Map<Integer,Object>> */ readAttributes(ZCLAttributeInfo[] attributes);

	/**
	 * Write a set of attributes on the cluster using the ZCL <em>Write
	 * Attributes</em> or the <em>Write Attributes Undivided</em> commands,
	 * according to the passed {@code undivided} parameter.
	 * 
	 * <p>
	 * The promise resolves with a {@code Map<Integer, Integer>}. If all the
	 * attributes have been written successfully, the map is empty. In case of
	 * failure in writing specific attribute(s), the map is filled with entries
	 * related to those attributes. Every key is set with the id of an attribute
	 * that was not written successfully, every value with the status returned
	 * in the associated <em>write attribute response record</em> accordingly
	 * re-mapped to one of the constants defined in the {@link ZCLException}
	 * class.
	 * 
	 * <p>
	 * According to the ZigBee Specification all the attributes must be standard
	 * attributes or, if manufacturer-specific they must have the same
	 * manufacturer code, otherwise an {@link IllegalArgumentException} occurs.
	 * 
	 * @param undivided {@code true} if an undivided write attributes command is
	 *        requested, {@code false} if not.
	 * @param attributesAndValues A {@code Map<ZCLAttributeInfo, Object>} of
	 *        attributes and values to be written. For ZCLAttributeInfo objects
	 *        which serialization is not supported (i.e,
	 *        <code>getDataType().getJavaDataType()</code> returns null), the
	 *        value must be of type byte[].
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         If resolved successfully the promise may return an empty {@code
	 *         Map<Integer, Integer>}. Otherwise the map will be filled with the
	 *         status information about the attributes that were not written.
	 *         The key represents the attributeID and the value the status
	 *         present in the corresponding attribute record returned by the ZCL
	 *         Write Attributes response message. The original ZCL status values
	 *         must be re-mapped to the list of status values listed in the
	 *         {@link ZCLException} class. The promise may fail with an
	 *         {@link IllegalArgumentException} if some of
	 *         {@link ZCLAttributeInfo} are manufacturer specific and other are
	 *         standard, or even if there are mix of attributes with different
	 *         manufacturer specific code.
	 */
	Promise /* <Map<Integer, Short>> */ writeAttributes(boolean undivided, Map attributesAndValues);

	/**
	 * Get an array of all the commandIds of the ZCLCluster.
	 * 
	 * <p>
	 * This method is implemented for ZCL devices compliant version equal or
	 * later than 1.2 of the Home Automation Profile or other profiles that adds
	 * a general command that enables discovery of command identifiers. When the
	 * device implements a profile that does not support this feature, the
	 * promise fails with a {@code ZCLException} with code
	 * {@code ZCLException.GENERAL_COMMAND_NOT_SUPPORTED}.
	 * 
	 * @return A {@link Promise} representing the completion of this
	 *         asynchronous call. The promise will be resolved with
	 *         {@code Integer[]} containing the the command identifiers
	 *         supported by the cluster.
	 */
	Promise /* <Integer[]> */ getCommandIds();

	/**
	 * Invokes a command on this cluster with a {@link ZCLFrame}. The returned
	 * promise provides the invocation response in an asynchronous way.
	 * 
	 * The source endpoint is not specified in this method call. To send the
	 * appropriate message on the network, the base driver must generate a
	 * source endpoint. The latter must not correspond to any exported endpoint.
	 * 
	 * @param frame The frame containing the command to issue.
	 * @return A promise representing the completion of this asynchronous call.
	 *         {@link Promise#getValue()} returns the response {@link ZCLFrame}.
	 */
	Promise /* <ZCLFrame> */ invoke(ZCLFrame frame);

	/**
	 * This method is to be used by applications when the targeted device has to
	 * distinguish between source endpoints of the message. For instance, alarms
	 * cluster (see 3.11 Alarms Cluster in [ZCL]) generated events are
	 * differently interpreted if they come from the oven or from the intrusion
	 * alert system.
	 * 
	 * @param frame The frame containing the command to issue.
	 * @param exportedServicePID : the source endpoint of the command request.
	 *        In targeted situations, the source endpoint is the valid service
	 *        PID of an exported endpoint.
	 * @return A promise representing the completion of this asynchronous call.
	 *         {@link Promise#getValue()} returns the response {@link ZCLFrame}.
	 */
	Promise /* <ZCLFrame> */ invoke(ZCLFrame frame, String exportedServicePID);

}
