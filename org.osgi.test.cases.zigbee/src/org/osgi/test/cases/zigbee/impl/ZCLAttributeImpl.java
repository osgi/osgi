package org.osgi.test.cases.zigbee.impl;

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
 * @author $Id$
 */
public class ZCLAttributeImpl implements ZCLAttribute {

	private int id;
	private Object value;
	private ZCLAttributeDescription description;

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
			handler.onFailure(new ZCLException(ZCLException.READ_ONLY,
					"can't set the value of a read only attribute"));
		} else if (!description.getDataType().getJavaDataType()
				.isInstance(value)) {
			handler.onFailure(new ZCLException(ZCLException.INVALID_DATA_TYPE,
					"can't set the value, invalid dataType"));
		}

		else {
			handler.onSuccess(response);
		}
	}

	public String toString() {
		return ZCLAttributeImpl.class.getName()
				+ ":[id: "
				+ getId()
				+ ", dataType: "
				+ getDataType()
				+ ", manufacturer: "
				+ (description.isManufacturerSpecific() ? Integer
						.toString(description.getManufacturerCode())
						: "standard") + ", description: " + description
				+ ", value: " + value + "]";
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
