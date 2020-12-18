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

package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

/**
 * Mocked impl.
 * 
 * @author $Id$
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
		/*
		 * Here we need to assign a value of the correct data type, so in order
		 * to make it working we use null, so we are sure that it contains an
		 * invalid value.
		 */

		Class< ? > javaType = desc.getDataType().getJavaDataType();

		if (javaType.equals(Integer.class)) {
			value = Integer.valueOf(0);
		} else if (javaType.equals(String.class)) {
			value = desc.getDefaultValue();
		} else {
			value = null;
		}
		description = desc;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public ZCLDataTypeDescription getDataType() {
		return description.getDataType();
	}

	@Override
	public Promise<Object> getValue() {
		return Promises.resolved(value);
	}

	protected Object getInternalValue() {
		return value;
	}

	@Override
	public Promise<Void> setValue(Object value) {
		try {
			this.setInternalValue(value);
		} catch (Throwable e) {
			return Promises.failed(e);
		}
		return Promises.resolved(null);
	}

	protected void setInternalValue(Object value) throws Throwable {
		if (description.isReadOnly()) {
			throw new ZCLException(ZCLException.READ_ONLY, "can't set the value of a read only attribute");
		}
		if (!description.getDataType().getJavaDataType().isInstance(value)) {
			throw new ZCLException(ZCLException.INVALID_DATA_TYPE, "can't set the value, invalid dataType");
		}

		this.value = value;
	}

	@Override
	public String toString() {
		return ZCLAttributeImpl.class.getName() + ":[id: " + getId() + ", dataType: " + getDataType()
				+ ", manufacturer: " + (description.isManufacturerSpecific()
						? Integer.toString(description.getManufacturerCode()) : "standard")
				+ ", description: " + description + ", value: " + value + "]";
	}

	@Override
	public boolean isManufacturerSpecific() {
		return description.isManufacturerSpecific();
	}

	@Override
	public int getManufacturerCode() {
		return description.getManufacturerCode();
	}

	public ZCLAttributeDescription getAttributeDescription() {
		return description;
	}
}
