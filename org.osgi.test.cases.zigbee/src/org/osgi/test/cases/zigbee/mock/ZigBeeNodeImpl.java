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
import org.osgi.service.zigbee.ZCLCommandMultiResponse;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZDPException;
import org.osgi.service.zigbee.ZDPFrame;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

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

	public Promise getNodeDescriptor() {

		return Promises.resolved(nodeDescriptor);
	}

	public Promise getPowerDescriptor() {

		return Promises.resolved(powerDescriptor);
	}

	public Promise getComplexDescriptor() {

		return Promises.resolved(complexDescriptor);
	}

	public Promise getLinksQuality() throws ZDPException {
		return Promises.failed(new UnsupportedOperationException());
	}

	public Promise getRoutingTable() {
		return Promises.failed(new UnsupportedOperationException());
	}

	public Promise leave() {
		return Promises.failed(new UnsupportedOperationException());
	}

	public Promise leave(boolean rejoin, boolean removeChildren) {
		return Promises.failed(new UnsupportedOperationException());
	}

	public Promise invoke(int clusterIdReq, int expectedClusterIdRsp, ZDPFrame message) {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise invoke(int clusterIdReq, ZDPFrame message) {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public String toString() {
		return "" + this.getClass().getName() + "[IEEEAddress: " + IEEEAddress + ", nwkAddress: " + nwkAddress
				+ ", hostPId: " + hostPId + ", endpoints: " + endpoints + ", nodeDescriptor: " + nodeDescriptor
				+ ", powerDescriptor: " + powerDescriptor + "]";
	}

	public Promise getUserDescription() {
		return Promises.resolved(userDescription);
	}

	public Promise setUserDescription(String userDescriptor) {
		return Promises.failed(new UnsupportedOperationException());
	}

	public ZCLCommandMultiResponse broadcast(int clusterID, ZCLFrame frame) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public ZCLCommandMultiResponse broadcast(int clusterID, ZCLFrame frame, String exportedServicePID) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public ZigBeeEndpoint[] getEndpoints() {

		return null;
	}

}
