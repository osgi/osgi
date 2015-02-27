
package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZDPException;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeUserDescriptor;

/**
 * Mocked impl.
 */
public class ZigBeeNodeImpl implements ZigBeeNode {

	private Long					IEEEAddress;
	private int						nwkAddress;
	private String					hostPId;
	private ZigBeeEndpoint[]		endpoints;
	private ZigBeeNodeDescriptor	nodeDescriptor;
	private ZigBeePowerDescriptor	powerDescriptor;
	private ZigBeeComplexDescriptor	complexDescriptor;
	private ZigBeeUserDescriptor	userDescriptor;

	/**
	 * @param IEEEAddress
	 * @param nwkAddress
	 * @param hostPId
	 * @param endpoints
	 */
	public ZigBeeNodeImpl(Long IEEEAddress, int nwkAddress, String hostPId, ZigBeeEndpoint[] endpoints) {
		this.IEEEAddress = IEEEAddress;
		this.nwkAddress = nwkAddress;
		this.hostPId = hostPId;
		this.endpoints = endpoints;
	}

	/**
	 * @param IEEEAddress
	 * @param nwkAddress
	 * @param hostPId
	 * @param endpoints
	 * @param nodeDesc
	 * @param powerDesc
	 */
	public ZigBeeNodeImpl(Long IEEEAddress, int nwkAddress, String hostPId, ZigBeeEndpoint[] endpoints, ZigBeeNodeDescriptor nodeDesc, ZigBeePowerDescriptor powerDesc) {
		this.IEEEAddress = IEEEAddress;
		this.nwkAddress = nwkAddress;
		this.hostPId = hostPId;
		this.endpoints = endpoints;
		this.powerDescriptor = powerDesc;
		this.nodeDescriptor = nodeDesc;
	}

	public Long getIEEEAddress() {
		return this.IEEEAddress;
	}

	public int getNetworkAddress() {
		return this.nwkAddress;
	}

	public String getHostPId() {
		return this.hostPId;
	}

	public int getPanId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getExtendedPanId() {
		// TODO Auto-generated method stub
		return -1;
	}

	public void getNodeDescriptor(ZigBeeHandler handler) throws ZCLException {
		// TODO Auto-generated method stub
		handler.onSuccess(nodeDescriptor);
	}

	public void getPowerDescriptor(ZigBeeHandler handler) throws ZCLException {
		// TODO Auto-generated method stub
		handler.onSuccess(powerDescriptor);
	}

	public void getComplexDescriptor(ZigBeeHandler handler) throws ZCLException {
		// TODO Auto-generated method stub
		handler.onSuccess(complexDescriptor);
	}

	public void getUserDescriptor(ZigBeeHandler handler) throws ZCLException {
		// TODO Auto-generated method stub
		handler.onSuccess(userDescriptor);
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

	public void leave(boolean rejoin, boolean removeChildren, ZigBeeHandler handler) {
		// TODO Auto-generated method stub
	}

	public String toString() {
		return "" + this.getClass().getName() + "[IEEEAddress: " + IEEEAddress + ", nwkAddress: " + nwkAddress + ", hostPId: " + hostPId + ", endpoints: " + endpoints + ", nodeDescriptor: "
				+ nodeDescriptor
				+ ", powerDescriptor: " + powerDescriptor + "]";
	}

}
