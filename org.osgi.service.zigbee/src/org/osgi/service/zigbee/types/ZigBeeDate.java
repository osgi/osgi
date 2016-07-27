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
 * This interface represents the 'Date' Data Type, as described in the ZigBeb
 * Cluster Library specification.
 * 
 * <p>
 * The ZigBee data type is mapped to a byte[4] array. where byte[0] must contain
 * the Year field (be careful that in the ZCL specification this byte do not
 * contain the actual year, but an offset) whereas byte[3] the Day of Week. The
 * array is marshaled/unmarshaled starting from byte[0].
 * 
 * @author $Id$
 * 
 */
public class ZigBeeDate
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeDate instance = new ZigBeeDate();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeDate getInstance() {
		return instance;
	}

	public String getName() {
		return "Date";
	}

	public boolean isAnalog() {
		return true;
	}

	public Class getJavaDataType() {
		return byte[].class;
	}

	public short getId() {
		return ZigBeeDataTypes.DATE;
	}

	public void serialize(ZigBeeDataOutput os, Object value) throws IOException {
		ZigBeeZCLDefaultSerializer.serializeDataType(os, ZigBeeDataTypes.DATE, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeZCLDefaultSerializer.deserializeDataType(is, ZigBeeDataTypes.DATE);
	}

}
