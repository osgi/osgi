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

/**
 * This interface represents a ZCLAttribute and adds to the ZCLAttributeInfo
 * interface the methods to read and write the ZCL attribute from and to the
 * ZigBee node with respectively the {@link #getValue(ZigBeeHandler)} and
 * {@link #setValue(Object, ZigBeeHandler)} methods
 * 
 * @author $Id$
 */
public interface ZCLAttribute extends ZCLAttributeInfo {

	/**
	 * Property key for the optional attribute id of a ZigBee Event Listener.
	 */
	public final static String ID = "zigbee.attribute.id";

	/**
	 * Gets the current value of the attribute.
	 * 
	 * <p>
	 * As described in "2.4.1.3 Effect on Receipt" chapter of the ZCL, a
	 * "read attribute" can have the following status: SUCCESS, or
	 * UNSUPPORTED_ATTRIBUTE (see {@link ZCLException}).
	 * 
	 * <p>
	 * The response object given to the handler is the attribute's Java data
	 * type (see {@link #getDataType()} method) that will contain the current
	 * attribute value (or null if an UNSUPPORTED_ATTRIBUTE occurred or in case
	 * of an invalid value).
	 * 
	 * @param handler the handler
	 * 
	 */
	public void getValue(ZigBeeHandler handler);

	/**
	 * Sets the current value of the attribute.
	 * 
	 * <p>
	 * As described in "2.4.3.3 Effect on Receipt" chapter of the ZCL, a
	 * "write attribute" can have the following status: SUCCESS,
	 * UNSUPPORTED_ATTRIBUTE, INVALID_DATA_TYPE, READ_ONLY, INVALID_VALUE (see
	 * {@link ZCLException}), or NOT_AUTHORIZED (see {@link ZDPException}).
	 * 
	 * <p>
	 * The response object given to the handler is a Boolean set to true if the
	 * attribute value has been written. A null value is processed as an invalid
	 * number. In case of an error has occurred, onFailure is called with a
	 * ZCLException.
	 * 
	 * @param value the Java value to set
	 * 
	 * @param handler the handler
	 */
	public void setValue(Object value, ZigBeeHandler handler);

}
