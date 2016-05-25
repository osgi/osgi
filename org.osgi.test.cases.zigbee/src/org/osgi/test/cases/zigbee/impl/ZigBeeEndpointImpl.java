/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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

package org.osgi.test.cases.zigbee.impl;

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

	private short					id;
	private ZigBeeSimpleDescriptor	desc;
	private ZCLCluster[]			inputs;
	private ZCLCluster[]			outputs;

	/**
	 * @param id
	 * @param inputs i.e. ServerClusters
	 * @param ouputs i.e. ClientClusters
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

	}

	public void getBoundEndPoints(int clusterId, ZigBeeHandler handler) {
		// TODO Auto-generated method stub
	}

	public String toString() {
		return "" + this.getClass().getName() + "[id: " + id + ", desc: " + desc + ", inputs: " + inputs + ", outputs: "
				+ outputs + "]";
	}

}
