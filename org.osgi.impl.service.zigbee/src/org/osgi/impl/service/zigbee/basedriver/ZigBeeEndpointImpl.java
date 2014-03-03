
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
	 * @param inputs
	 * @param ouputs
	 * @param desc
	 */
	public ZigBeeEndpointImpl(int id, ZCLCluster[] inputs, ZCLCluster[] ouputs, ZigBeeSimpleDescriptor desc) {
		this.id = id;
		this.inputs = inputs;
		this.outputs = ouputs;
		this.desc = desc;
	}

	public Integer getId() {
		// TODO Auto-generated method stub
		return id;
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
		// TODO Auto-generated method stub
		return inputs;
	}

	public ZCLCluster getServerCluster(int serverClusterId) {
		// TODO Auto-generated method stub
		return inputs[serverClusterId];
	}

	public ZCLCluster[] getClientClusters() {
		// TODO Auto-generated method stub
		return outputs;
	}

	public ZCLCluster getClientCluster(int clientClusterId) {
		// TODO Auto-generated method stub
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
}
