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

import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;

/**
 * This interface provides information about the attribute, like its ZCL
 * attribute ID, if it manufacturer specific and about its data type (see
 * {@link #getDataType}).
 * 
 * @author $Id$
 */
public interface ZCLAttributeInfo {

	/**
	 * Property key for the optional attribute id of a ZigBee Event Listener.
	 */
	public final static String ID = "zigbee.attribute.id";

	/**
	 * Checks if the attribute is manufacturer specific.
	 * 
	 * @return {@code true} if and only if this attribute is related to a
	 *         manufacturer extension.
	 */
	public boolean isManufacturerSpecific();

	/**
	 * Returns the manufacturer code of this attribute.
	 * 
	 * @return The manufacturer code that defined this attribute, if the
	 *         attribute does not belong to any manufacture extension then it
	 *         returns -1.
	 */
	public int getManufacturerCode();

	/**
	 * Returns the ID of this attribute.
	 * 
	 * @return the attribute identifier (that is, the attribute's ID).
	 */
	public int getId();

	/**
	 * Returns the data type of this attribute.
	 * 
	 * @return The attribute data type. It may be null if the data type is not
	 *         retrievable (issue with read attribute and discover attributes
	 *         commands).
	 */
	public ZCLDataTypeDescription getDataType();

}
