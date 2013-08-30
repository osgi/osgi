
package org.osgi.impl.service.zigbee.descriptions;

import org.osgi.service.zigbee.descriptions.ZigBeeDataTypeDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeParameterDescription;

public class ZigBeeParameterDescriptionImpl implements ZigBeeParameterDescription {

	private ZigBeeDataTypeDescription	type;

	public ZigBeeParameterDescriptionImpl(ZigBeeDataTypeDescription type) {
		this.type = type;
	}

	public ZigBeeDataTypeDescription getDataTypeDescription() {
		return type;
	}

	public boolean checkValue(Object value) {
		return false;
	}
}
