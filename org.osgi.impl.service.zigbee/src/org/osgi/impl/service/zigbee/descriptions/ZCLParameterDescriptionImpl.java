
package org.osgi.impl.service.zigbee.descriptions;

import org.osgi.service.zigbee.descriptions.ZCLParameterDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeDataTypeDescription;

/**
 * Mocked impl.
 */
public class ZCLParameterDescriptionImpl implements ZCLParameterDescription {

	private ZigBeeDataTypeDescription	type;

	/**
	 * @param type
	 */
	public ZCLParameterDescriptionImpl(ZigBeeDataTypeDescription type) {
		this.type = type;
	}

	public ZigBeeDataTypeDescription getDataTypeDescription() {
		return type;
	}

	public boolean checkValue(Object value) {
		return false;
	}
}
