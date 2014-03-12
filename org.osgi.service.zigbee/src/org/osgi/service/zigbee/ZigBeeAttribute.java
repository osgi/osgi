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

import org.osgi.service.zigbee.descriptions.ZigBeeDataTypeDescription;

/**
 * This interface represents a ZigBee Attribute
 * 
 * @version 1.0
 */
public interface ZigBeeAttribute {

	/**
	 * Property key for the optional attribute id of a ZigBee Event Listener.
	 */
	public final static String	ID	= "zigbee.attribute.id";

	/**
	 * @return the attribute identifier (i.e. the attribute's ID)
	 */
	public int getId();

	/**
	 * Gets the current value of the attribute.
	 * 
	 * @param handler the handler
	 * @throws ZigBeeException
	 */
	public void getValue(ZigBeeMapHandler handler) throws ZigBeeException;

	/**
	 * Sets the current value of the attribute.
	 * 
	 * @param value the Java value to set
	 * @param handler the handler
	 * @throws ZigBeeException when the data type is not known
	 *         ("Unknown data type").
	 */
	public void setValue(Object value, ZigBeeMapHandler handler) throws ZigBeeException;

	/**
	 * @return the Attribute data type. It may be null if the data type is not
	 *         retrievable (issue with read attribute and discover attributes
	 *         commands).
	 */
	public ZigBeeDataTypeDescription getDataType();

}
