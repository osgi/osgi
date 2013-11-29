
package org.osgi.impl.service.zigbee.descriptors;

import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;

/**
 * Mocked impl of ZigBeeSimpleDescriptor.
 */
public class ZigBeeSimpleDescriptorImpl implements ZigBeeSimpleDescriptor {

	private int		deviceId;
	private byte	version;
	private Integer	profileId;

	/**
	 * @param deviceId
	 * @param version
	 * @param profileId
	 */
	public ZigBeeSimpleDescriptorImpl(int deviceId, byte version, Integer profileId) {
		// TODO Auto-generated constructor stub
		this.deviceId = deviceId;
		this.version = version;
		this.profileId = profileId;
	}

	public int getApplicationDeviceId() {
		// TODO Auto-generated method stub
		return deviceId;
	}

	public byte getApplicationDeviceVersion() {
		// TODO Auto-generated method stub
		return version;
	}

	public Integer getApplicationProfileId() {
		// TODO Auto-generated method stub
		return profileId;
	}

	public short getEndpoint() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int[] getInputClusterList() {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getOutputClusterList() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean providesInputCluster(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean providesOutputCluster(int id) {
		// TODO Auto-generated method stub
		return false;
	}

}
