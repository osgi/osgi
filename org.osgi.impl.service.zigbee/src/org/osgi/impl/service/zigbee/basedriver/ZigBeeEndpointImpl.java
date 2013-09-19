
package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZigBeeCluster;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.descriptions.ZigBeeDeviceDescription;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;

/**
 * Mocked impl of ZigBeeEndpoint.
 */
public class ZigBeeEndpointImpl implements ZigBeeEndpoint {

	private short					id;
	// private short address;
	// private int profileId;
	private ZigBeeSimpleDescriptor	desc;
	private ZigBeeCluster[]			inputs;
	private ZigBeeCluster[]			outputs;
	// private ZigBeeNode deviceNode;

	/**
	 * @param id
	 * @param inputs
	 * @param ouputs
	 * @param desc
	 */
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

	// public short getAddress() {
	// return address;
	// }

	public Long getNodeAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	// public void setAddress(short address) {
	// this.address = address;
	// }

	public ZigBeeDeviceDescription getDeviceDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public ZigBeeSimpleDescriptor getSimpleDescriptor() throws ZigBeeException {
		// TODO Auto-generated method stub
		return desc;
	}

	public ZigBeeCluster[] getServerClusters() {
		// TODO Auto-generated method stub
		return inputs;
	}

	public ZigBeeCluster getServerCluster(int serverClusterId) {
		// TODO Auto-generated method stub
		return inputs[serverClusterId];
	}

	public ZigBeeCluster[] getClientClusters() {
		// TODO Auto-generated method stub
		return outputs;
	}

	public ZigBeeCluster getClientCluster(int clientClusterId) {
		// TODO Auto-generated method stub
		return outputs[clientClusterId];
	}

	// public int getProfileId() {
	// return profileId;
	// }

	// public ZigBeeNode getDeviceNode() {
	// return deviceNode;
	// }

	// public void setDeviceNode(ZigBeeNode deviceNode) {
	// this.deviceNode = deviceNode;
	// }

	public boolean bindTo(ZigBeeEndpoint endpoint, int clusterId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean unbindFrom(ZigBeeEndpoint endpoint, int clusterId) {
		// TODO Auto-generated method stub
		return false;
	}

	public void notExported(ZigBeeException e) {
		// TODO Auto-generated method stub
	}
}
