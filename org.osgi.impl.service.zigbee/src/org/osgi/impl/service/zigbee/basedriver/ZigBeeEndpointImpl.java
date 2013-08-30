
package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZigBeeCluster;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptions.ZigBeeDeviceDescription;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;

public class ZigBeeEndpointImpl implements ZigBeeEndpoint {

	private short					id;
	private short					address;
	private int						profileId;
	private ZigBeeSimpleDescriptor	desc;
	private ZigBeeCluster[]			inputs;
	private ZigBeeCluster[]			outputs;
	private ZigBeeNode				deviceNode;

	public ZigBeeEndpointImpl(short id, ZigBeeCluster[] inputs, ZigBeeCluster[] ouputs, ZigBeeSimpleDescriptor desc) {
		this.id = id;
		this.inputs = inputs;
		this.outputs = ouputs;
		this.desc = desc;
	}

	public short getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public short getAddress() {
		return address;
	}

	public Long getNodeAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAddress(short address) {
		this.address = address;
	}

	public int getProfileId() {
		return profileId;
	}

	public ZigBeeNode getDeviceNode() {
		return deviceNode;
	}

	public void setDeviceNode(ZigBeeNode deviceNode) {
		this.deviceNode = deviceNode;
	}

	public boolean bindTo(ZigBeeEndpoint endpoint, int clusterId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean unbindFrom(ZigBeeEndpoint endpoint, int clusterId) {
		// TODO Auto-generated method stub
		return false;
	}

	public ZigBeeDeviceDescription getDeviceDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public ZigBeeCluster getClientCluster(int id) {
		// TODO Auto-generated method stub
		return outputs[id];
	}

	public ZigBeeCluster[] getClientClusters() {
		// TODO Auto-generated method stub
		return outputs;
	}

	public ZigBeeCluster getServerCluster(int id) {
		// TODO Auto-generated method stub
		return inputs[id];
	}

	public ZigBeeCluster[] getServerClusters() {
		// TODO Auto-generated method stub
		return inputs;
	}

	public ZigBeeSimpleDescriptor getSimpleDescriptor() throws ZigBeeException {
		// TODO Auto-generated method stub
		return desc;
	}

	public void notExported(ZigBeeException zbe) {
		// TODO Auto-generated method stub
	}
}
