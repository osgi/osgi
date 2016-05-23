
package org.osgi.impl.service.zigbee.basedriver;

import java.math.BigInteger;

import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;

/**
 * Mocked impl of ZigBeeEndpoint.
 * 
 * @author $Id$
 */
public class ZigBeeEndpointImpl implements ZigBeeEndpoint {

	private short id;
	private ZigBeeSimpleDescriptor desc;
	private ZCLCluster[] inputs;
	private ZCLCluster[] outputs;

	/**
	 * @param id
	 * @param inputs
	 *            i.e. ServerClusters
	 * @param ouputs
	 *            i.e. ClientClusters
	 * @param desc
	 */
	public ZigBeeEndpointImpl(short id, ZCLCluster[] inputs, ZCLCluster[] ouputs, ZigBeeSimpleDescriptor desc) {
		this.id = id;
		this.inputs = inputs;
		this.outputs = ouputs;
		this.desc = desc;
	}

	public short getId() {
		return this.id;
	}

	public BigInteger getNodeAddress() {
		// TODO Auto-generated method stub
		return BigInteger.valueOf(-1);
	}

	public void getSimpleDescriptor(ZigBeeHandler handler) {
		// TODO Auto-generated method stub
		handler.onSuccess(desc);
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
		System.out.println("### not exported has been called");

	}

	public void getBoundEndPoints(int clusterId, ZigBeeHandler handler) {
		// TODO Auto-generated method stub
	}

	public String toString() {
		return "" + this.getClass().getName() + "[id: " + id + ", desc: " + desc + ", inputs: " + inputs + ", outputs: "
				+ outputs + "]";
	}

}
