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

package org.osgi.test.cases.zigbee.mock;

import java.math.BigInteger;
import org.osgi.service.zigbee.ZCLCommandHandler;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZDPException;
import org.osgi.service.zigbee.ZDPFrame;
import org.osgi.service.zigbee.ZDPHandler;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;

/**
 * Mocked impl.
 * 
 * @author $Id$
 */
public class ZigBeeNodeImpl implements ZigBeeNode {

	protected BigInteger				IEEEAddress;
	protected int						nwkAddress;
	protected String					hostPId;
	protected ZigBeeEndpoint[]			endpoints;
	protected ZigBeeNodeDescriptor		nodeDescriptor;
	protected ZigBeePowerDescriptor		powerDescriptor;
	protected ZigBeeComplexDescriptor	complexDescriptor;
	protected String					userDescription;

	/**
	 * @param IEEEAddress
	 * @param nwkAddress
	 * @param hostPId
	 * @param endpoints
	 */
	public ZigBeeNodeImpl(BigInteger IEEEAddress, String hostPId, ZigBeeEndpoint[] endpoints) {
		this.IEEEAddress = IEEEAddress;
		this.hostPId = hostPId;
		this.endpoints = endpoints;
	}

	/**
	 * 
	 * @param IEEEAddress
	 * @param hostPId
	 * @param endpoints
	 * @param nodeDesc
	 * @param powerDesc
	 * @param userdescription
	 */
	public ZigBeeNodeImpl(BigInteger IEEEAddress, String hostPId, ZigBeeEndpoint[] endpoints,
			ZigBeeNodeDescriptor nodeDesc, ZigBeePowerDescriptor powerDesc, String userdescription) {
		this.IEEEAddress = IEEEAddress;
		this.hostPId = hostPId;
		this.endpoints = endpoints;
		this.powerDescriptor = powerDesc;
		this.nodeDescriptor = nodeDesc;
		this.userDescription = userdescription;
	}

	public BigInteger getIEEEAddress() {
		return this.IEEEAddress;
	}

	public int getNetworkAddress() {
		return this.nwkAddress;
	}

	public String getHostPid() {
		return this.hostPId;
	}

	public ZigBeeEndpoint[] getEnpoints() {
		return endpoints;
	}

	public int getPanId() {

		return 0;
	}

	public BigInteger getExtendedPanId() {

		return BigInteger.valueOf(-1);
	}

	public void getNodeDescriptor(ZigBeeHandler handler) {

		handler.onSuccess(nodeDescriptor);
	}

	public void getPowerDescriptor(ZigBeeHandler handler) {

		handler.onSuccess(powerDescriptor);
	}

	public void getComplexDescriptor(ZigBeeHandler handler) {

		handler.onSuccess(complexDescriptor);
	}

	public void getLinksQuality(ZigBeeHandler handler) throws ZDPException {

	}

	public void getRoutingTable(ZigBeeHandler handler) {

	}

	public void leave(ZigBeeHandler handler) {

	}

	public void leave(boolean rejoin, boolean removeChildren, ZigBeeHandler handler) {

	}

	public void invoke(int clusterIdReq, int expectedClusterIdRsp, ZDPFrame message, ZDPHandler handler) {

	}

	public void invoke(int clusterIdReq, ZDPFrame message, ZDPHandler handler) {

	}

	public String toString() {
		return "" + this.getClass().getName() + "[IEEEAddress: " + IEEEAddress + ", nwkAddress: " + nwkAddress
				+ ", hostPId: " + hostPId + ", endpoints: " + endpoints + ", nodeDescriptor: " + nodeDescriptor
				+ ", powerDescriptor: " + powerDescriptor + "]";
	}

	public void getUserDescription(ZigBeeHandler handler) {
		handler.onSuccess(userDescription);
	}

	public void setUserDescription(String userDescriptor, ZigBeeHandler handler) {

	}

	public void broadcast(int clusterID, ZCLFrame frame, ZCLCommandHandler handler) {

	}

	public void broadcast(int clusterID, ZCLFrame frame, ZCLCommandHandler handler, String exportedServicePID) {

	}

	public ZigBeeEndpoint[] getEndpoints() {

		return null;
	}

}
