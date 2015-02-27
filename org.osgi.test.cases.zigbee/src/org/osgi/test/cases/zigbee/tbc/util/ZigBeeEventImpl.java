
package org.osgi.test.cases.zigbee.tbc.util;

import org.osgi.service.zigbee.ZigBeeEvent;

/**
 * Mocked impl of ZigBeeEvent.
 */
public class ZigBeeEventImpl implements ZigBeeEvent {

	private Long	ieeeAddress;
	private int		endpointId;
	private int		clusterId;
	private int		attributeId;
	private Object	value;

	/**
	 * @param ieeeAddress
	 * @param endpointId
	 * @param clusterId
	 * @param attributeId
	 * @param value
	 */
	public ZigBeeEventImpl(Long ieeeAddress, int endpointId, int clusterId, int attributeId, Object value) {
		this.ieeeAddress = ieeeAddress;
		this.endpointId = endpointId;
		this.clusterId = clusterId;
		this.attributeId = attributeId;
		this.value = value;
	}

	public Long getIEEEAddress() {
		return ieeeAddress;
	}

	public int getEndpointId() {
		return endpointId;
	}

	public int getClusterId() {
		return clusterId;
	}

	public int getAttributeId() {
		return attributeId;
	}

	public Object getValue() {
		return value;
	}

	public String toString() {
		return ZigBeeEventImpl.class.getName() + "[ieeeAddress: " + ieeeAddress + ", endpointId:" + endpointId + ", clusterId:" + clusterId + ", attributeId:" + attributeId + ", value:" + value + "]";
	}

}
