
package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeUserDescriptor;

/**
 * Mocked impl of ZigBeeNode.
 */
public class ZigBeeNodeImpl implements ZigBeeNode {

	private Long					IEEEAddress;
	private int						nwkAddress;
	private String					hostPId;
	private ZigBeeEndpoint[]		endpoints;
	private ZigBeeNodeDescriptor	nodeDescriptor;
	private ZigBeePowerDescriptor	powerDescriptor;

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

	public Long getExtendedPanId() {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getEndpoints() {
		int i = endpoints.length;
		int[] endpointsIds = new int[i];
		for (int j = 0; j < i; j = j + 1) {
			endpointsIds[j] = endpoints[j].getId();
		}
		return endpointsIds;
	}

	public ZigBeeEndpoint getEndpoint(int id) {
		for (int j = 0; j < endpoints.length; j = j + 1) {
			if (id == endpoints[j].getId()) {
				return endpoints[j];
			}
		}
		return null;
	}

	public ZigBeeNodeDescriptor getNodeDescriptor() throws ZigBeeException {
		return nodeDescriptor;
	}

	public ZigBeePowerDescriptor getPowerDescriptor() throws ZigBeeException {
		return powerDescriptor;
	}

	public ZigBeeComplexDescriptor getComplexDescriptor()
			throws ZigBeeException {
		// TODO Auto-generated method stub
		return null;
	}

	public ZigBeeUserDescriptor getUserDescriptor() throws ZigBeeException {
		// TODO Auto-generated method stub
		return null;
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
