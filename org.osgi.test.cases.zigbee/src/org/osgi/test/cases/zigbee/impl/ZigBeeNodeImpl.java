
package org.osgi.test.cases.zigbee.impl;

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

	private BigInteger					IEEEAddress;
	private int							nwkAddress;
	private String						hostPId;
	private ZigBeeEndpoint[]			endpoints;
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
	public ZigBeeNodeImpl(BigInteger IEEEAddress, String hostPId,
			ZigBeeEndpoint[] endpoints) {
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
	public ZigBeeNodeImpl(BigInteger IEEEAddress, String hostPId,
			ZigBeeEndpoint[] endpoints, ZigBeeNodeDescriptor nodeDesc,
			ZigBeePowerDescriptor powerDesc, String userdescription) {
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
		// TODO Auto-generated method stub
		return 0;
	}

	public BigInteger getExtendedPanId() {
		// TODO Auto-generated method stub
		return BigInteger.valueOf(-1);
	}

	public void getNodeDescriptor(ZigBeeHandler handler) {
		// TODO Auto-generated method stub
		handler.onSuccess(nodeDescriptor);
	}

	public void getPowerDescriptor(ZigBeeHandler handler) {
		// TODO Auto-generated method stub
		handler.onSuccess(powerDescriptor);
	}

	public void getComplexDescriptor(ZigBeeHandler handler) {
		// TODO Auto-generated method stub
		handler.onSuccess(complexDescriptor);
	}

	public void getLinksQuality(ZigBeeHandler handler) throws ZDPException {
		// TODO Auto-generated method stub
	}

	public void getRoutingTable(ZigBeeHandler handler) {
		// TODO Auto-generated method stub
	}

	public void leave(ZigBeeHandler handler) {
		// TODO Auto-generated method stub
	}

	public void leave(boolean rejoin, boolean removeChildren,
			ZigBeeHandler handler) {
		// TODO Auto-generated method stub
	}

	public void invoke(int clusterIdReq, int expectedClusterIdRsp,
			ZDPFrame message, ZDPHandler handler) {
		// TODO Auto-generated method stub
	}

	public void invoke(int clusterIdReq, ZDPFrame message, ZDPHandler handler) {
		// TODO Auto-generated method stub
	}

	public String toString() {
		return "" + this.getClass().getName() + "[IEEEAddress: " + IEEEAddress
				+ ", nwkAddress: " + nwkAddress + ", hostPId: " + hostPId
				+ ", endpoints: " + endpoints + ", nodeDescriptor: "
				+ nodeDescriptor + ", powerDescriptor: " + powerDescriptor
				+ "]";
	}

	public void getUserDescription(ZigBeeHandler handler) {
		handler.onSuccess(userDescription);
	}

	public void setUserDescription(String userDescriptor, ZigBeeHandler handler) {
		// TODO Auto-generated method stub
	}

	public void broadcast(int clusterID, ZCLFrame frame,
			ZCLCommandHandler handler) {
		// TODO Auto-generated method stub
	}

	public void broadcast(int clusterID, ZCLFrame frame,
			ZCLCommandHandler handler, String exportedServicePID) {
		// TODO Auto-generated method stub
	}

	public ZigBeeEndpoint[] getEndpoints() {
		// TODO Auto-generated method stub
		return null;
	}

}
