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

package org.osgi.test.cases.zigbee.descriptors;

import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;

/**
 * Mocked impl of ZigBeeSimpleDescriptor.
 * 
 * @author $Id$
 */
public class ZigBeeSimpleDescriptorImpl implements ZigBeeSimpleDescriptor {

	private int		deviceId;
	private byte	version;
	private int		profileId;
	private int[]	inputClusters	= new int[0];
	private int[]	outputClusters	= new int[0];

	private short	endpointId;

	public ZigBeeSimpleDescriptorImpl(short endpointId, int deviceId, int profileId, byte version, int[] inputClusters, int[] outputClusters) {
		this.deviceId = deviceId;
		this.version = version;
		this.profileId = profileId;
		this.endpointId = endpointId;

		if (inputClusters == null) {
			this.inputClusters = new int[0];
		} else {
			this.inputClusters = inputClusters;
		}

		if (outputClusters == null) {
			this.outputClusters = new int[0];
		} else {
			this.outputClusters = outputClusters;
		}
	}

	public int getApplicationDeviceId() {
		return deviceId;
	}

	public byte getApplicationDeviceVersion() {
		return version;
	}

	public int getApplicationProfileId() {
		return profileId;
	}

	public short getEndpoint() {
		return endpointId;
	}

	public int[] getInputClusters() {
		return this.inputClusters;
	}

	public int[] getOutputClusters() {
		return this.outputClusters;
	}

	public void setInputClusters(int[] inputClusters) {
		if (inputClusters == null) {
			throw new NullPointerException("input clusters array cannot be null");
		}
		this.inputClusters = inputClusters;
	}

	public void setOutputClusters(int[] outputClusters) {
		if (outputClusters == null) {
			throw new NullPointerException("output clusters array cannot be null");
		}
		this.outputClusters = outputClusters;
	}

	public boolean providesInputCluster(int clusterId) {
		for (int i = 0; i < inputClusters.length; i++) {
			if (inputClusters[i] == clusterId) {
				return true;
			}
		}
		return false;
	}

	public boolean providesOutputCluster(int clusterId) {
		for (int i = 0; i < outputClusters.length; i++) {
			if (outputClusters[i] == clusterId) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return "" + this.getClass().getName() + "[deviceId: " + deviceId + ", version: " + version + ", profileId: "
				+ profileId + "]";
	}
}
