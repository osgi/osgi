
package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeUserDescriptor;

public class ZigBeeNodeImpl implements ZigBeeNode {

	private Long					IEEEAddress;
	private short					nwkAddress;
	private ZigBeeEndpoint[]		endpoints;
	private ZigBeeNodeDescriptor	nodeDescriptor;
	private ZigBeePowerDescriptor	powerDescriptor;

	public ZigBeeNodeImpl(Long IEEEAddress, short nwkAddress, ZigBeeEndpoint[] endpoints) {
		this.IEEEAddress = IEEEAddress;
		this.nwkAddress = nwkAddress;
		this.endpoints = endpoints;
	}

	public ZigBeeNodeImpl(Long IEEEAddress, short nwkAddress, ZigBeeEndpoint[] endpoints, ZigBeeNodeDescriptor nodeDesc, ZigBeePowerDescriptor powerDesc) {
		this.IEEEAddress = IEEEAddress;
		this.nwkAddress = nwkAddress;
		this.endpoints = endpoints;
		this.powerDescriptor = powerDesc;
		this.nodeDescriptor = nodeDesc;
	}

	public Long getIEEEAddress() {
		return IEEEAddress;
	}

	public short getNetworkAddress() {
		return nwkAddress;
	}

	public String getHostPId() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPanId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Long getExtendedPanId() {
		// TODO Auto-generated method stub
		return null;
	}

	public ZigBeeEndpoint getEndpoint(short id) {
		// TODO Auto-generated method stub
		return null;
	}

	public ZigBeeEndpoint[] getEndpoints() {
		return endpoints;
	}

	public ZigBeeComplexDescriptor getComplexDescriptor()
			throws ZigBeeException {
		// TODO Auto-generated method stub
		return null;
	}

	public ZigBeeNodeDescriptor getNodeDescriptor() throws ZigBeeException {
		// TODO Auto-generated method stub
		return nodeDescriptor;
	}

	public ZigBeePowerDescriptor getPowerDescriptor() throws ZigBeeException {
		// TODO Auto-generated method stub
		return powerDescriptor;
	}

	public ZigBeeUserDescriptor getUserDescriptor() throws ZigBeeException {
		// TODO Auto-generated method stub
		return null;
	}

	public void leave(boolean rejoin, boolean removeChildren,
			ZigBeeHandler handler) throws ZigBeeException {
		// TODO Auto-generated method stub
	}

	public void leave(ZigBeeHandler handler) throws ZigBeeException {
		// TODO Auto-generated method stub
	}
}
