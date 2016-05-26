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

package org.osgi.test.cases.zigbee.impl;

import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;

/**
 * Mocked impl of ZigBeeComplexDescriptor.
 * 
 * @author $Id$
 */
public class ZigBeeComplexDescriptorImpl implements ZigBeeComplexDescriptor {

	/**
	 * @return the language code used for character strings.
	 */
	public String getLanguageCode() {
		return null;
	}

	/**
	 * @return the encoding used by characters in the character set.
	 */
	public String getCharacterSetIdentifier() {
		return null;
	}

	/**
	 * @return the manufacturer name field.
	 */
	public String getManufacturerName() {
		return null;
	}

	/**
	 * @return the model name field
	 */
	public String getModelName() {
		return null;
	}

	/**
	 * @return the serial number field.
	 */
	public String getSerialNumber() {
		return null;
	}

	/**
	 * @return the Device URL field.
	 */
	public String getDeviceURL() {
		return null;
	}

	/**
	 * @return the icon field.
	 */
	public byte[] getIcon() {
		return null;
	}

	/**
	 * @return the icon field URL.
	 */
	public String getIconURL() {
		return null;
	}

}
