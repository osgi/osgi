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

package org.osgi.service.zigbee.types;

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Unsigned Integer 48-bits' Data Type, as
 * described in the ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeUnsignedInteger48
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeUnsignedInteger48 instance = new ZigBeeUnsignedInteger48();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeUnsignedInteger48 getInstance() {
		return instance;
	}

	public String getName() {
		return "UnsignedInteger48";
	}

	public boolean isAnalog() {
		return true;
	}

	public Class getJavaDataType() {
		return Long.class;
	}

	public short getId() {
		return ZigBeeDataTypes.UNSIGNED_INTEGER_48;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeZCLDefaultSerializer.serializeDataType(os, ZigBeeDataTypes.UNSIGNED_INTEGER_48, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeZCLDefaultSerializer.deserializeDataType(is, ZigBeeDataTypes.UNSIGNED_INTEGER_48);
	}

}
