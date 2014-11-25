/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;

/**
 * This interface represents a ZigBeeBitmap40 as described in the ZigBee
 * Specification.
 * 
 * @version 1.0
 * 
 * @author see RFC 192 authors: Andre Bottaro, Arnaud Rinquin, Jean-Pierre
 *         Poutcheu, Fabrice Blache, Christophe Demottie, Antonin Chazalet,
 *         Evgeni Grigorov, Nicola Portinaro, Stefano Lenzi.
 */
public class ZigBeeBitmap40 implements ZCLDataTypeDescription {

	private static ZigBeeBitmap40	singletonInstance	= new ZigBeeBitmap40();

	private ZigBeeBitmap40() {

	}

	/**
	 * @return the singleton instance.
	 */
	public static ZigBeeBitmap40 getInstance() {
		return singletonInstance;
	}

	public boolean isAnalog() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class getJavaDataType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getInvalidNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	public short getId() {
		return ZigBeeDataTypes.BITMAP_40;
	}

	public void serialize(Object param, ByteArrayOutputStream outdata) {
		ZigBeeDataTypes.encode(ZigBeeDataTypes.BITMAP_40, param, outdata);
	}

	public Object deserialize(ByteArrayInputStream data) {
		return ZigBeeDataTypes.decode(ZigBeeDataTypes.BITMAP_40, data);
	}
}
