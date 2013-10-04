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

package org.osgi.service.zigbee.types;

import org.osgi.service.zigbee.descriptions.ZigBeeDataTypeDescription;

/**
 * This interface represents a ZigBeeFloatingSingle as described in the ZigBee
 * Specification.
 * 
 * @version 1.0
 */
public class ZigBeeFloatingSingle implements ZigBeeDataTypeDescription {

	private static ZigBeeFloatingSingle	singletonInstance	= new ZigBeeFloatingSingle();

	private ZigBeeFloatingSingle() {

	}

	/**
	 * @return the singleton instance.
	 */
	public static ZigBeeFloatingSingle getInstance() {
		return singletonInstance;
	}

	public byte[] serialize(Object param) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return 0x09;
	}

	public Object deserialize(byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}

}
