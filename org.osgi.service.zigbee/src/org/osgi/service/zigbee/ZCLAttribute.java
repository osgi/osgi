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

import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;

/**
 * This interface represents a ZCLAttribute
 * 
 * @version 1.0
 * 
 * @author see RFC 192 authors: Andre Bottaro, Arnaud Rinquin, Jean-Pierre
 *         Poutcheu, Fabrice Blache, Christophe Demottie, Antonin Chazalet,
 *         Evgeni Grigorov, Nicola Portinaro, Stefano Lenzi.
 */
public interface ZCLAttribute {

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
	 * Only one Map entry, the key is the attribute identifier of Integer type
	 * and the value is the associated attribute value of byte[] type. In case
	 * of a failure, onFailure is called with a ZCLException.
	 * 
	 * @param handler the handler
	 * @throws ZCLException
	 */
	public void getValue(ZigBeeHandler handler) throws ZCLException;

	/**
	 * Sets the current value of the attribute.
	 * 
	 * Only one Map entry, the key is the attribute identifier of Integer type
	 * and the value is true if the attribute value has been written or false
	 * otherwise.
	 * 
	 * @param value the Java value to set
	 * @param handler the handler
	 */
	public void setValue(Object value, ZigBeeHandler handler);

	/**
	 * @return the Attribute data type. It may be null if the data type is not
	 *         retrievable (issue with read attribute and discover attributes
	 *         commands).
	 */
	public ZCLDataTypeDescription getDataType();

}
