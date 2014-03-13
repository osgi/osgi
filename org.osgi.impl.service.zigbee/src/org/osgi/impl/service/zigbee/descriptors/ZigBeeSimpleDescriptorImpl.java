
package org.osgi.impl.service.zigbee.descriptors;

import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;

/**
 * Mocked impl of ZigBeeSimpleDescriptor.
 */
public class ZigBeeSimpleDescriptorImpl implements ZigBeeSimpleDescriptor {

	private int		deviceId;
	private byte	version;
	private int		profileId;

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
		return null;
	}

	public int[] getOutputClusters() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean providesInputCluster(int clusterId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean providesOutputCluster(int clusterId) {
		// TODO Auto-generated method stub
		return false;
	}

}
