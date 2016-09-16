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

package org.osgi.test.cases.zigbee.config.file;

import java.math.BigInteger;

/**
 * This class is used to identify an attribute Id in the network
 * 
 * @author $Id$
 */
public class NetworkAttributeIds {

	private BigInteger	ieeeAddresss;
	private short		endpointId;
	private int			clusterId;
	private int			attributeId;

	public NetworkAttributeIds(BigInteger ieeeAddresss, short endpointId, int clusterId, int attributeId) {
		this.ieeeAddresss = ieeeAddresss;
		this.endpointId = endpointId;
		this.clusterId = clusterId;
		this.attributeId = attributeId;
	}

	public BigInteger getIeeeAddresss() {
		return ieeeAddresss;
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
}
