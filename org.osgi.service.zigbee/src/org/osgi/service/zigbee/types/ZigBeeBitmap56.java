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
 * A singleton class that represents the 'Bitmap 56-bits' data type, as it is
 * defined in the ZigBee Cluster Library specification.
 * 
 * @author $Id$
 * 
 */
public class ZigBeeBitmap56
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeBitmap56 instance = new ZigBeeBitmap56();

	private ZigBeeBitmap56() {
	}

	/**
	 * @return the singleton instance of this class.
	 */
	public static ZigBeeBitmap56 getInstance() {
		return instance;
	}

	public String getName() {
		return "Bitmap56";
	}

	public boolean isAnalog() {
		return false;
	}

	public Class getJavaDataType() {
		return Long.class;
	}

	public short getId() {
		return ZigBeeDataTypes.BITMAP_56;
	}

	public void serialize(ZigBeeDataOutput os, Object value) throws IOException {
		ZigBeeDefaultSerializer.serializeDataType(os, ZigBeeDataTypes.BITMAP_56, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDefaultSerializer.deserializeDataType(is, ZigBeeDataTypes.BITMAP_56);
	}

}
