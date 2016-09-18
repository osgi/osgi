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

package org.osgi.service.zigbee.descriptors;

/**
 * This interface represents a Complex Descriptor as described in the ZigBee
 * Specification The Complex Descriptor contains extended information for each
 * of the device descriptions contained in the node. The use of the Complex
 * Descriptor is optional.
 * 
 * @noimplement
 * 
 * @author $Id$
 */
public interface ZigBeeComplexDescriptor {

	/**
	 * Returns the language code used for character strings.
	 * 
	 * @return the language code used for character strings.
	 */
	public String getLanguageCode();

	/**
	 * Returns the encoding used by characters in the character set.
	 * 
	 * @return the encoding used by characters in the character set.
	 */
	public String getCharacterSetIdentifier();

	/**
	 * Returns the manufacturer name.
	 * 
	 * @return the manufacturer name.
	 */
	public String getManufacturerName();

	/**
	 * Returns the model name.
	 * 
	 * @return the model name.
	 */
	public String getModelName();

	/**
	 * Returns the serial number.
	 * 
	 * @return the serial number.
	 */
	public String getSerialNumber();

	/**
	 * Returns the Device URL.
	 * 
	 * @return the Device URL.
	 */
	public String getDeviceURL();

	/**
	 * Returns the icon field.
	 * 
	 * @return the icon field.
	 */
	public byte[] getIcon();

	/**
	 * Returns the icon URL.
	 * 
	 * @return the icon URL.
	 */
	public String getIconURL();

}
