/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.zigbee.basedriver;

import java.math.BigInteger;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

/**
 * Mocked impl of ZigBeeEndpoint.
 * 
 * @author $Id$
 */
public class ZigBeeEndpointImpl implements ZigBeeEndpoint {

	private short					endpointId;
	private ZigBeeSimpleDescriptor	simpleDescriptor;
	private ZCLCluster[]			inputs;
	private ZCLCluster[]			outputs;
	private ZigBeeNodeImpl			node;

	public ZigBeeEndpointImpl(short endpointId, ZCLCluster[] inputs, ZCLCluster[] outputs, ZigBeeSimpleDescriptor simpleDescriptor) {
		this.endpointId = endpointId;
		this.inputs = inputs;
		this.outputs = outputs;
		this.simpleDescriptor = simpleDescriptor;
	}

	public short getId() {
		return this.endpointId;
	}

	public BigInteger getNodeAddress() {
		return node.getIEEEAddress();
	}

	public Promise getSimpleDescriptor() {
		return Promises.resolved(simpleDescriptor);
	}

	public ZigBeeSimpleDescriptor getSimpleDescriptorInternal() {
		return simpleDescriptor;
	}

	public ZCLCluster getServerCluster(int clusterId) {
		if (clusterId < 0 || clusterId > 0xffff) {
			throw new IllegalArgumentException("clusterId must be in the range [0, 0xffff]. Asked for clusterId " + clusterId);
		}

		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i].getId() == clusterId) {
				return inputs[i];
			}
		}

		return null;
	}

	public ZCLCluster[] getClientClusters() {
		return outputs;
	}

	public ZCLCluster getClientCluster(int clusterId) {

		if (clusterId < 0 || clusterId > 0xffff) {
			throw new IllegalArgumentException("clusterId must be in the range [0, 0xffff]. Asked for clusterId " + clusterId);
		}

		for (int i = 0; i < outputs.length; i++) {
			if (outputs[i].getId() == clusterId) {
				return outputs[i];
			}
		}

		return null;
	}

	public ZCLCluster[] getServerClusters() {
		return inputs;
	}

	public Promise bind(String servicePid, int clusterId) {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise unbind(String servicePid, int clusterId) {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public void notExported(ZigBeeException e) {

	}

	public Promise getBoundEndPoints(int clusterId) {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	/**
	 * Sets the ZigBeeNode this endpoint is part of.
	 * 
	 * @param node The ZigBee node.
	 */

	public void setZigBeeNode(ZigBeeNodeImpl node) {
		this.node = node;
	}

	public String toString() {
		return "" + this.getClass().getName() + "[id: " + endpointId + ", desc: " + simpleDescriptor + ", inputs: " + inputs + ", outputs: "
				+ outputs + "]";
	}

}
