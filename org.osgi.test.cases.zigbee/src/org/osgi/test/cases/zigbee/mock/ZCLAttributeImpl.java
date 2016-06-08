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

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;

/**
 * Mocked impl.
 * 
 * @author $Id: 948fd24bef60fa9723eb4c6be5ed62168a175b3b $
 */
public class ZCLAttributeImpl implements ZCLAttribute {

	private int						id;
	private Object					value;
	private ZCLAttributeDescription	description;

	/**
	 * @param desc
	 */
	public ZCLAttributeImpl(ZCLAttributeDescription desc) {
		id = desc.getId();
		value = desc.getDefaultValue();
		description = desc;
	}

	public int getId() {
		return id;
	}

	public ZCLDataTypeDescription getDataType() {
		return description.getDataType();
	}

	public void getValue(ZigBeeHandler handler) {
		// Map<Integer, Object> response = null;
		Map response = null;
		response = new HashMap();
		response.put(Integer.valueOf(Integer.toString(id)), value);
		handler.onSuccess(response);
	}

	public void setValue(Object value, ZigBeeHandler handler) {
		this.value = value;
		// Map<Integer, Object> response = null;
		Map response = null;
		response = new HashMap();
		response.put(Integer.valueOf(Integer.toString(id)), this.value);
		if (description.isReadOnly()) {
			handler.onFailure(new ZCLException(ZCLException.READ_ONLY, "can't set the value of a read only attribute"));
		} else if (!description.getDataType().getJavaDataType().isInstance(value)) {
			handler.onFailure(
					new ZCLException(ZCLException.INVALID_DATA_TYPE, "can't set the value, invalid dataType"));
		}

		else {
			handler.onSuccess(response);
		}
	}

	public String toString() {
		return ZCLAttributeImpl.class.getName() + ":[id: " + getId() + ", dataType: " + getDataType()
				+ ", manufacturer: " + (description.isManufacturerSpecific()
						? Integer.toString(description.getManufacturerCode()) : "standard")
				+ ", description: " + description + ", value: " + value + "]";
	}

	public boolean isManufacturerSpecific() {
		return description.isManufacturerSpecific();
	}

	public int getManufacturerCode() {
		return description.getManufacturerCode();
	}

	public ZCLAttributeDescription getAttributeDescription() {
		return description;
	}
}
