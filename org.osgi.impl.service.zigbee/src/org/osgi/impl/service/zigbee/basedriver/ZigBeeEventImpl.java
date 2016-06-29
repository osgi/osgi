/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.zigbee.basedriver;

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
		return ZigBeeEventImpl.class.getName() + "[ieeeAddress: " + ieeeAddress + ", endpointId:" + endpointId
				+ ", clusterId:" + clusterId + ", attributeId:" + attributeId + ", value:" + value + "]";
	}

}
