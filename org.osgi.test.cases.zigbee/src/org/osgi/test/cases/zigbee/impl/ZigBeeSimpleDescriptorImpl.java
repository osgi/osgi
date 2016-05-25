
package org.osgi.test.cases.zigbee.impl;

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
	private int[]	InputClusters;
	private int[]	outputClusters;

	/**
	 * @param deviceId
	 * @param version
	 * @param profileId
	 */
	public ZigBeeSimpleDescriptorImpl(int deviceId, byte version, int profileId) {
		this.deviceId = deviceId;
		this.version = version;
		this.profileId = profileId;
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
		// TODO Auto-generated method stub
		return 0;
	}

	public int[] getInputClusters() {
		// TODO Auto-generated method stub
		return InputClusters;
	}

	public int[] getOutputClusters() {
		// TODO Auto-generated method stub
		return outputClusters;
	}

	public void setInputClusters(int[] InputClusters) {
		this.InputClusters = InputClusters;
	}

	public void setOutputClusters(int[] outputClusters) {
		this.outputClusters = outputClusters;
	}

	public boolean providesInputCluster(int clusterId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean providesOutputCluster(int clusterId) {
		// TODO Auto-generated method stub
		return false;
	}

	public String toString() {
		return "" + this.getClass().getName() + "[deviceId: " + deviceId
				+ ", version: " + version + ", profileId: " + profileId + "]";
	}

}
