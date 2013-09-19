
package org.osgi.impl.service.zigbee.basedriver;

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.zigbee.ZigBeeAttribute;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHandler;
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

	/**
	 * FOR TESTCASES ONLY!
	 * 
	 * @return the attribute value.
	 */
	public Object getValue() {
		return value;
	}

	public ZigBeeDataTypeDescription getDataType() {
		return description.getDataTypeDescription();
	}

	public ZigBeeAttributeDescription getDescription() {
		return description;
	}

	public void getValue(ZigBeeHandler handler) {
		// TODO: AAA: Auto-generated method stub
		Map response = null;
		response = new HashMap();
		response.put(id, value);
		handler.onSuccess(response);
	}

	public void getValue(ZigBeeDataTypeDescription outputType,
			ZigBeeHandler handler) throws ZigBeeException {
		// TODO: AAA: Auto-generated method stub
		Map response = null;
		response = new HashMap();
		response.put(id, value);
		handler.onSuccess(response);
	}

	public void setValue(Object value, ZigBeeHandler handler)
			throws ZigBeeException {
		// TODO: AAA: Auto-generated method stub
	}

	public void setValue(byte[] value, ZigBeeHandler handler)
			throws ZigBeeException {
		// TODO: AAA: Auto-generated method stub
	}

	public String toString() {
		// TODO: AAA: Auto-generated method stub
		return description.getName();
	}

}
