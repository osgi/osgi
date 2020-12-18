/*
 * Copyright (c) OSGi Alliance (2016, 2020). All Rights Reserved.
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

package org.osgi.impl.service.zigbee.event;

import java.math.BigInteger;

import org.osgi.impl.service.zigbee.util.Hex;
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
	@SuppressWarnings("unused")
	private short		dataType;

	public ZigBeeEventImpl(BigInteger ieeeAddress, short endpointId, int clusterId, int attributeId, short dataType, Object value) {
		this.ieeeAddress = ieeeAddress;
		this.endpointId = endpointId;
		this.clusterId = clusterId;
		this.attributeId = attributeId;
		this.dataType = dataType;
		this.value = value;
	}

	@Override
	public BigInteger getIEEEAddress() {
		return ieeeAddress;
	}

	@Override
	public short getEndpointId() {
		return endpointId;
	}

	@Override
	public int getClusterId() {
		return clusterId;
	}

	@Override
	public int getAttributeId() {
		return attributeId;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "[ieeeAddress: " + Hex.toHexString(ieeeAddress, 16) + ", endpointId: 0x" + Hex.toHexString(endpointId, 2) + ", clusterId: 0x" + Hex.toHexString(clusterId, 4) + ", attributeId: 0x"
				+ Hex.toHexString(attributeId, 4) + ", value: " + value + "]";
	}
}
