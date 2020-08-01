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

import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

/**
 * @author $Id$
 */
public class ZCLAttributeImpl implements ZCLAttribute {

	private Object					value;
	private ZCLAttributeDescription	description;

	/**
	 * Creates a ZCLAttribute class from a ZCLAttributeDescription object
	 * 
	 * @param description A {@link ZCLAttributeDescription} object.
	 */
	public ZCLAttributeImpl(ZCLAttributeDescription description) {
		value = description.getDefaultValue();
		this.description = description;
	}

	public int getId() {
		return description.getId();
	}

	public ZCLDataTypeDescription getDataType() {
		return description.getDataType();
	}

	public Promise<Object> getValue() {
		return Promises.resolved(value);
	}

	public Promise<Void> setValue(Object value) {
		if (description.isReadOnly()) {
			return Promises.failed(new ZCLException(ZCLException.READ_ONLY, "can't set the value of a read only attribute"));
		}
		if (!description.getDataType().getJavaDataType().isInstance(value)) {
			return Promises.failed(
					new ZCLException(ZCLException.INVALID_DATA_TYPE, "can't set the value, invalid dataType"));
		}
		this.value = value;
		return Promises.resolved(null);
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
