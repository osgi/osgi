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
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;

/**
 * This class is used to store the node configuration read from the ZigBee
 * configuration file.
 * 
 * @author portinaro
 */
public class ZigBeeNodeConfig {

	private BigInteger				IEEEAddress;
	private int						nwkAddress;
	private ZigBeeEndpointConfig[]	endpoints;
	private ZigBeeNodeDescriptor	nodeDescriptor;
	private ZigBeePowerDescriptor	powerDescriptor;
	private ZigBeeComplexDescriptor	complexDescriptor;
	private String					userDescription;
	private int						actualEndpointsNumber;

	private ZigBeeHostConfig		host;

	protected ZigBeeNodeConfig(BigInteger IEEEAddress, ZigBeeEndpointConfig[] endpoints,
			ZigBeeNodeDescriptor nodeDescriptor, ZigBeePowerDescriptor poswerDescriptor, String userDescription) {
		this.IEEEAddress = IEEEAddress;
		this.endpoints = endpoints;
		this.powerDescriptor = poswerDescriptor;
		this.nodeDescriptor = nodeDescriptor;
		this.userDescription = userDescription;
	}

	public ZigBeeNodeConfig(ZigBeeHostConfig host, BigInteger IEEEAddress, ZigBeeEndpointConfig[] endpoints,
			ZigBeeNodeDescriptor nodeDescriptor, ZigBeePowerDescriptor powerDescriptor, String userDescription) {
		this(IEEEAddress, endpoints, nodeDescriptor, powerDescriptor, userDescription);

		/**
		 * The endpoint objects need to know about the ZigBeeNodeConfig object.
		 */

		for (int i = 0; i < endpoints.length; i++) {
			endpoints[i].setZigBeeNodeConfig(this);
		}

		this.host = host;
	}

	public BigInteger getIEEEAddress() {
		return this.IEEEAddress;
	}

	public int getNetworkAddress() {
		return this.nwkAddress;
	}

	public String getHostPid() {
		return host.getHostPid();
	}

	public ZigBeeEndpointConfig[] getEndpoints() {
		return endpoints;
	}

	public int getPanId() {
		return host.getPanId();
	}

	public BigInteger getExtendedPanId() {
		return null;
	}

	public ZigBeeNodeDescriptor getNodeDescriptor() {
		return nodeDescriptor;
	}

	public String getUserDescription() {
		return userDescription;
	}

	public ZigBeePowerDescriptor getPowerDescriptor() {
		return powerDescriptor;
	}

	public ZigBeeComplexDescriptor getComplexDescriptor() {
		return complexDescriptor;
	}

	public void setActualEndpointsNumber(int actualEndpointsNumber) {
		this.actualEndpointsNumber = actualEndpointsNumber;
	}

	public int getActualEndpointsNumber() {
		return actualEndpointsNumber;
	}
}
