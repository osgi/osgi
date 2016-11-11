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
import org.osgi.impl.service.zigbee.event.EndResponse;
import org.osgi.impl.service.zigbee.event.ZCLCommandResponseImpl;
import org.osgi.impl.service.zigbee.event.ZCLCommandResponseStreamImpl;
import org.osgi.service.zigbee.ZCLCommandResponseStream;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZDPException;
import org.osgi.service.zigbee.ZDPFrame;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeHost;
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

	private ZigBeeHost				host;
	private BigInteger				IEEEAddress;
	private int						nwkAddress;

	private ZigBeeEndpointImpl[]	endpoints	= new ZigBeeEndpointImpl[0];

	private ZigBeeNodeDescriptor	nodeDescriptor;
	private ZigBeePowerDescriptor	powerDescriptor;
	private ZigBeeComplexDescriptor	complexDescriptor;
	private String					userDescription;

	public ZigBeeNodeImpl(BigInteger IEEEAddress, int nwkAddress, ZigBeeEndpointImpl[] endpoints,
			ZigBeeNodeDescriptor nodeDescriptor, ZigBeePowerDescriptor powerDescriptor, String userdescription) {
		this.IEEEAddress = IEEEAddress;
		this.endpoints = endpoints;
		this.powerDescriptor = powerDescriptor;
		this.nodeDescriptor = nodeDescriptor;
		this.userDescription = userdescription;
		this.nwkAddress = nwkAddress;

		for (int i = 0; i < endpoints.length; i++) {
			endpoints[i].setZigBeeNode(this);
		}
	}

	public ZigBeeNodeImpl(ZigBeeHost host, BigInteger IEEEAddress, int nwkAddress, ZigBeeEndpointImpl[] endpoints,
			ZigBeeNodeDescriptor nodeDesc, ZigBeePowerDescriptor powerDesc, String userdescription) {
		this(IEEEAddress, nwkAddress, endpoints, nodeDesc, powerDesc, userdescription);

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

	public int getPanId() {
		return host.getPanId();
	}

	public BigInteger getExtendedPanId() {
		return host.getExtendedPanId();
	}

	public Promise getNodeDescriptor() {
		// FIXME: CT check type of returned value or exception
		return Promises.resolved(nodeDescriptor);
	}

	public Promise getPowerDescriptor() {
		// FIXME: CT check type of returned value or exception
		return Promises.resolved(powerDescriptor);
	}

	public Promise getComplexDescriptor() {
		// FIXME: CT check type of returned value or exception
		return Promises.resolved(complexDescriptor);
	}

	public Promise getLinksQuality() throws ZDPException {
		// FIXME: Implement it. CT check type of returned value or exception
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise getRoutingTable() {
		// FIXME: Implement it. CT check type of returned value or exception
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise leave() {
		// FIXME: Implement it. CT check type of returned value or exception
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise leave(boolean rejoin, boolean removeChildren) {
		// FIXME: Implement it. CT check type of returned value or exception
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise invoke(int clusterIdReq, int expectedClusterIdRsp, ZDPFrame message) {
		// FIXME: Implement it. CT check type of returned value or exception
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise invoke(int clusterIdReq, ZDPFrame message) {
		// FIXME: Implement it. CT check type of returned value or exception
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public String toString() {
		return "" + this.getClass().getName() + "[IEEEAddress: " + IEEEAddress + ", nwkAddress: " + nwkAddress
				+ ", hostPId: " + host.getHostPid() + ", endpoints: " + endpoints + ", nodeDescriptor: " + nodeDescriptor
				+ ", powerDescriptor: " + powerDescriptor + "]";
	}

	public Promise getUserDescription() {
		// FIXME: CT check type of returned value or exception
		return Promises.resolved(userDescription);
	}

	public Promise setUserDescription(String userDescriptor) {
		// FIXME: Implement it. CT check type of returned value or exception
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame) {
		// FIXME: Implement it. CT check type of returned value or exception
		ZCLCommandResponseStreamImpl impl = new ZCLCommandResponseStreamImpl();

		// Stub out the response by immediately filling it with an Unsupported
		// Operation Exception and ending it
		impl.handleResponse(new ZCLCommandResponseImpl(Promises.failed(
				new UnsupportedOperationException("Not yet implemented"))));
		impl.handleResponse(new EndResponse());

		return impl;
	}

	public ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame, String exportedServicePID) {
		// FIXME: Implement it. CT check type of returned value or exception
		ZCLCommandResponseStreamImpl impl = new ZCLCommandResponseStreamImpl();

		// Stub out the response by immediately filling it with an Unsupported
		// Operation Exception and ending it
		impl.handleResponse(new ZCLCommandResponseImpl(Promises.failed(
				new UnsupportedOperationException("Not yet implemented"))));
		impl.handleResponse(new EndResponse());

		return impl;
	}

	public ZigBeeEndpoint[] getEndpoints() {
		// FIXME: how to handle endpoints.
		if (endpoints == null) {
			return new ZigBeeEndpoint[0];
		}
		return endpoints;
	}
}
