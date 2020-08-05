/*
 * Copyright (c) OSGi Alliance (2016, 2020). All Rights Reserved.
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
 * A singleton class that represents the 'Bitmap 24-bit' data type, as it is
 * defined in the ZigBee Cluster Library specification.
 * 
 * @author $Id$
 * 
 */
public class ZigBeeBitmap24
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeBitmap24 instance = new ZigBeeBitmap24();

	private ZigBeeBitmap24() {
	}

	/**
	 * Gets a singleton instance of this class.
	 * 
	 * @return the singleton instance.
	 */
	public static ZigBeeBitmap24 getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "Bitmap24";
	}

	@Override
	public boolean isAnalog() {
		return false;
	}

	@Override
	public Class< ? > getJavaDataType() {
		return Integer.class;
	}

	@Override
	public short getId() {
		return ZigBeeDataTypes.BITMAP_24;
	}

	@Override
	public void serialize(ZigBeeDataOutput os, Object value) throws IOException {
		ZigBeeDefaultSerializer.serializeDataType(os, ZigBeeDataTypes.BITMAP_24, value);
	}

	@Override
	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDefaultSerializer.deserializeDataType(is, ZigBeeDataTypes.BITMAP_24);
	}

}
