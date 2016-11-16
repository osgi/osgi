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

package org.osgi.test.cases.zigbee.mock;

import org.osgi.service.zigbee.ZCLAttributeInfo;
import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;

public class ZCLAttributeInfoImpl implements ZCLAttributeInfo {

	private int						manufacturerCode;
	private ZCLDataTypeDescription	dataType;
	private int						attributeId;

	/**
	 * Implementation of the ZCLAttributeInfo interface.
	 * 
	 * @param attributeId The attributeId
	 * @param manufacturerCode The manufacturerCode of the attribute. -1 if the
	 *        attribute is NOT manufacturer specific.
	 * @param dataType The data type of the attribute.
	 */
	public ZCLAttributeInfoImpl(int attributeId, int manufacturerCode, ZCLDataTypeDescription dataType) {
		this.attributeId = attributeId;
		this.manufacturerCode = manufacturerCode;
		this.dataType = dataType;
	}

	public ZCLAttributeInfoImpl(ZCLAttributeDescription attributeDescription) {
		this.attributeId = attributeDescription.getId();
		this.manufacturerCode = attributeDescription.getManufacturerCode();
		this.dataType = attributeDescription.getDataType();
	}

	public boolean isManufacturerSpecific() {
		return manufacturerCode >= 0 ? true : false;
	}

	public int getManufacturerCode() {
		return manufacturerCode;
	}

	public int getId() {
		return attributeId;
	}

	public ZCLDataTypeDescription getDataType() {
		return dataType;
	}
}
