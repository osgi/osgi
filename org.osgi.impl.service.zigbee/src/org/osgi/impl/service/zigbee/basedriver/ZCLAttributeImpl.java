
package org.osgi.impl.service.zigbee.basedriver;

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeDataTypeDescription;

/**
 * Mocked impl.
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

	public ZigBeeDataTypeDescription getDataType() {
		return description.getDataTypeDescription();
	}

	public void getValue(ZigBeeHandler handler) {
		Map<Integer, Object> response = null;
		response = new HashMap<Integer, Object>();
		response.put(id, value);
		handler.onSuccess(response);
	}

	public void setValue(Object value, ZigBeeHandler handler) {
		this.value = value;
		Map<Integer, Object> response = null;
		response = new HashMap<Integer, Object>();
		response.put(id, this.value);
		handler.onSuccess(response);
	}

	public String toString() {
		return ZCLAttributeImpl.class.getName() + ":[id: " + getId() + ", dataType: " + getDataType() + ", description: " + description + ", value: " + value + "]";
	}

}
