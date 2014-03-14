
package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;

/**
 * Mocked impl of ZigBeeEndpoint.
 */
public class ZigBeeEndpointImpl implements ZigBeeEndpoint {

	private int						id;
	private ZigBeeSimpleDescriptor	desc;
	private ZCLCluster[]			inputs;
	private ZCLCluster[]			outputs;

	/**
	 * @param id
	 * @param inputs i.e. ServerClusters
	 * @param ouputs i.e. ClientClusters
	 * @param desc
	 */
	public ZigBeeEndpointImpl(int id, ZCLCluster[] inputs, ZCLCluster[] ouputs, ZigBeeSimpleDescriptor desc) {
		this.id = id;
		this.inputs = inputs;
		this.outputs = ouputs;
		this.desc = desc;
	}

	public int getId() {
		return this.id;
	}

	public Long getNodeAddress() {
		// TODO Auto-generated method stub
		return Long.valueOf("-1");
	}

	public ZigBeeSimpleDescriptor getSimpleDescriptor() throws ZigBeeException {
		// TODO Auto-generated method stub
		return desc;
	}

	public ZCLCluster[] getServerClusters() {
		return inputs;
	}

	public ZCLCluster getServerCluster(int serverClusterId) {
		return inputs[serverClusterId];
	}

	public ZCLCluster[] getClientClusters() {
		return outputs;
	}

	public ZCLCluster getClientCluster(int clientClusterId) {
		return outputs[clientClusterId];
	}

	public void bind(String servicePid, int clusterId, ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}

	public void unbind(String servicePid, int clusterId, ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}

	public void notExported(ZigBeeException e) {
		// TODO Auto-generated method stub
	}

	public void getBoundEndPoints(int clusterId, ZigBeeHandler handler) {
		// TODO Auto-generated method stub
	}

	public String toString() {
		return "" + this.getClass().getName() + "[id: " + id + ", desc: " + desc + ", inputs: " + inputs + ", outputs: " + outputs + "]";
	}

}
