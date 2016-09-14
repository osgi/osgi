
package org.osgi.test.cases.zigbee.config.file;

import java.math.BigInteger;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;

/**
 * Stores the information related to an endpoint read from the configuration
 * file.
 * 
 * @author $id$
 */
public class ZigBeeEndpointConfig {

	private short					id;
	private ZigBeeSimpleDescriptor	simpleDescriptor;
	private ZCLClusterConfig[]		inputs;
	private ZCLClusterConfig[]		outputs;

	private ZigBeeNodeConfig		node;

	public ZigBeeEndpointConfig(ZigBeeNodeConfig node, short id, ZCLClusterConfig[] inputs, ZCLClusterConfig[] ouputs, ZigBeeSimpleDescriptor desc) {
		this.id = id;
		this.inputs = inputs;
		this.outputs = ouputs;
		this.simpleDescriptor = desc;
		this.node = node;
	}

	public short getId() {
		return this.id;
	}

	public BigInteger getNodeAddress() {
		return node.getIEEEAddress();
	}

	public ZigBeeSimpleDescriptor getSimpleDescriptor() {
		return simpleDescriptor;
	}

	public ZCLClusterConfig[] getServerClusters() {
		return inputs;
	}

	public ZCLClusterConfig getServerCluster(int serverClusterId) {
		return inputs[serverClusterId];
	}

	public ZCLClusterConfig[] getClientClusters() {
		return outputs;
	}

	public ZCLClusterConfig getClientCluster(int clientClusterId) {
		return outputs[clientClusterId];
	}
}
