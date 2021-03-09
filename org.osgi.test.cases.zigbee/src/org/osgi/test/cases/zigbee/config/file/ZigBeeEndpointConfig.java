/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.zigbee.config.file;

import java.math.BigInteger;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;

/**
 * Stores the information related to an endpoint read from the configuration
 * file.
 * 
 * @author $id$
 */
public class ZigBeeEndpointConfig {

	private short					endpointId;
	private ZigBeeSimpleDescriptor	simpleDescriptor;
	private ZCLClusterDescription[]	inputs;
	private ZCLClusterDescription[]	outputs;
	private ZigBeeNodeConfig		node;
	private int						serverClustersNumber;
	private int						clientClustersNumber;

	public ZigBeeEndpointConfig(short endpointId, ZCLClusterDescription[] inputs, ZCLClusterDescription[] outputs, ZigBeeSimpleDescriptor simpleDescriptor, int clientClustersNumber,
			int serverClustersNumber) {
		this.endpointId = endpointId;
		this.inputs = inputs;
		this.outputs = outputs;
		this.simpleDescriptor = simpleDescriptor;
		this.clientClustersNumber = clientClustersNumber;
		this.serverClustersNumber = serverClustersNumber;
	}

	public short getId() {
		return this.endpointId;
	}

	public BigInteger getNodeAddress() {
		return node.getIEEEAddress();
	}

	public ZigBeeSimpleDescriptor getSimpleDescriptor() {
		return simpleDescriptor;
	}

	public ZCLClusterDescription[] getServerClusters() {
		return inputs;
	}

	public ZCLClusterDescription getServerCluster(int serverClusterId) {
		return inputs[serverClusterId];
	}

	public ZCLClusterDescription[] getClientClusters() {
		return outputs;
	}

	public ZCLClusterDescription getClientCluster(int clientClusterId) {
		return outputs[clientClusterId];
	}

	public void setZigBeeNodeConfig(ZigBeeNodeConfig node) {
		this.node = node;
	}

	public int getServerClustersNumber() {
		return serverClustersNumber;
	}

	public int getClientClustersNumber() {
		return clientClustersNumber;
	}
}
