
package org.osgi.impl.service.zigbee.descriptions;

import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;
import org.osgi.service.zigbee.descriptions.ZCLParameterDescription;

/**
 * Mocked impl.
 */
public class ZCLParameterDescriptionImpl implements ZCLParameterDescription {

	private ZCLDataTypeDescription	type;

	/**
	 * @param type
	 */
	public ZCLParameterDescriptionImpl(ZCLDataTypeDescription type) {
		this.type = type;
	}

	public ZCLDataTypeDescription getDataTypeDescription() {
		return type;
	}

	public boolean checkValue(Object value) {
		return false;
	}
}
