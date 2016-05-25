
package org.osgi.test.cases.zigbee.impl;

import java.math.BigInteger;
import org.osgi.service.zigbee.ZigBeeEvent;

/**
 * Mocked impl of ZigBeeEvent.
 * 
 * @author $Id$
 */
public class ZigBeeEventImpl implements ZigBeeEvent {

	private BigInteger	ieeeAddress;
	private short		endpointId;
	private int			clusterId;
	private int			attributeId;
	private Object		value;

	/**
	 * @param ieeeAddress
	 * @param endpointId
	 * @param clusterId
	 * @param attributeId
	 * @param value
	 */
	public ZigBeeEventImpl(BigInteger ieeeAddress, short endpointId, int clusterId, int attributeId, Object value) {
		this.ieeeAddress = ieeeAddress;
		this.endpointId = endpointId;
		this.clusterId = clusterId;
		this.attributeId = attributeId;
		this.value = value;
	}

	public BigInteger getIEEEAddress() {
		return ieeeAddress;
	}

	public short getEndpointId() {
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
