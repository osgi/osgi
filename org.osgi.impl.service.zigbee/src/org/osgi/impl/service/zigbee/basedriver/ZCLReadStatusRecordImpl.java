
package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZCLAttributeInfo;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZCLReadStatusRecord;
import org.osgi.service.zigbee.ZigBeeException;

public class ZCLReadStatusRecordImpl implements ZCLReadStatusRecord {

	private ZCLAttributeInfo	attributeInfo;
	private ZCLException		failure;
	private Object				value;

	public ZCLReadStatusRecordImpl(ZCLAttributeInfo attributeInfo, ZCLException failure) {
		this.attributeInfo = attributeInfo;
		this.failure = failure;
	}

	public ZCLReadStatusRecordImpl(ZCLAttributeInfo attributeInfo, ZCLException failure, Object value) {
		this.attributeInfo = attributeInfo;
		this.failure = failure;
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public ZigBeeException getFailure() {
		return failure;
	}

	public ZCLAttributeInfo getAttributeInfo() {
		return attributeInfo;
	}
}
