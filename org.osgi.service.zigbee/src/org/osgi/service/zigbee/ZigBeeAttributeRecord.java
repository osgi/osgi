/*
 * Copyright (c) OSGi Alliance (${year}). All Rights Reserved.
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

import org.osgi.service.zigbee.datatype.ZigBeeDataTypeDescription;

/**
 * This class represents a ZigBee Attribute Record
 * 
 * @version 1.0
 */
public class ZigBeeAttributeRecord {

	private short						id;

	private ZigBeeDataTypeDescription	dataType;

	private Object						value;

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public ZigBeeDataTypeDescription getDataType() {
		return dataType;
	}

	public void setDataType(ZigBeeDataTypeDescription dataType) {
		this.dataType = dataType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
