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
 * This class represents a ZigBee Attribute Record
 * 
 * @version 1.0
 */
public final class ZigBeeAttributeRecord {

	private short						id;
	private ZigBeeDataTypeDescription	dataType;
	private Object						value;

	/**
	 * @param id the ZigBeeAttributeRecord id
	 * @param dataType the ZigBeeAttributeRecord dataType
	 * @param value the ZigBeeAttributeRecord value
	 */
	public ZigBeeAttributeRecord(short id, ZigBeeDataTypeDescription dataType, Object value) {
		this.id = id;
		this.dataType = dataType;
		this.value = value;
	}

	/**
	 * @return the ZigBeeAttributeRecord id
	 */
	public short getId() {
		return id;
	}

	/**
	 * @return the ZigBeeAttributeRecord dataType
	 */
	public ZigBeeDataTypeDescription getDataType() {
		return dataType;
	}

	/**
	 * @return the ZigBeeAttributeRecord value
	 */
	public Object getValue() {
		return value;
	}

}
