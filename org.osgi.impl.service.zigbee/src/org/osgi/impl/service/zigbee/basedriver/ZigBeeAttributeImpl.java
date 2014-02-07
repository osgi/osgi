
package org.osgi.impl.service.zigbee.basedriver;

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.zigbee.ZigBeeAttribute;
import org.osgi.service.zigbee.ZigBeeAttributesHandler;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.descriptions.ZigBeeAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeDataTypeDescription;

/**
 * Mocked impl of ZigBeeAttribute.
 */
public class ZigBeeAttributeImpl implements ZigBeeAttribute {

	private int							id;
	private Object						value;
	private ZigBeeAttributeDescription	description;

	/**
	 * @param desc
	 */
	public ZigBeeAttributeImpl(ZigBeeAttributeDescription desc) {
		id = desc.getId();
		value = desc.getDefaultValue();
		description = desc;
	}

	public int getId() {
		return id;
	}

	public ZigBeeDataTypeDescription getDataType() {
		return description.getDataTypeDescription();
	}

	public void getValue(ZigBeeAttributesHandler handler) {
		Map<Integer, Object> response = null;
		response = new HashMap<Integer, Object>();
		response.put(id, value);
		handler.onSuccess(response);
	}

	public void setValue(Object value, ZigBeeAttributesHandler handler)
			throws ZigBeeException {
		this.value = value;
		Map<Integer, Object> response = null;
		response = new HashMap<Integer, Object>();
		response.put(id, this.value);
		handler.onSuccess(response);
	}

	public String toString() {
		return ZigBeeAttributeImpl.class.getName() + ":[id:" + getId() + ",dataType:" + getDataType() + ",description:" + description + ",value:" + value + "]";
	}

}
