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
public class ZigBeeAttributeRecord {

	private short						id;

	private ZigBeeDataTypeDescription	dataType;

	private Object						value;

	/**
	 * @return the ZigBeeAttributeRecord id
	 */
	public short getId() {
		return id;
	}

	/**
	 * @param id the ZigBeeAttributeRecord id
	 */
	public void setId(short id) {
		this.id = id;
	}

	/**
	 * @return the ZigBeeAttributeRecord dataType
	 */
	public ZigBeeDataTypeDescription getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the ZigBeeAttributeRecord dataType
	 */
	public void setDataType(ZigBeeDataTypeDescription dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the ZigBeeAttributeRecord value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the ZigBeeAttributeRecord value
	 */
	public void setValue(Object value) {
		this.value = value;
	}

}
