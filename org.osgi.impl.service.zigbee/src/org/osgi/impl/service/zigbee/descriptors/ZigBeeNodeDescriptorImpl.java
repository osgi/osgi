
package org.osgi.impl.service.zigbee.descriptors;

import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;

/**
 * Mocked impl of ZigBeeNodeDescriptor.
 */
public class ZigBeeNodeDescriptorImpl implements ZigBeeNodeDescriptor {

	private Short	logicalType;
	private short	frequencyBand;
	private Integer	manufacturerCode;
	private int		maxBufferSize;
	private boolean	isComplexDescriptorAvailable;
	private boolean	isUserDescriptorAvailable;

	/**
	 * @param type
	 * @param band
	 * @param manufCode
	 * @param maxBufSize
	 * @param isComplexAv
	 * @param isUserAv
	 */
	public ZigBeeNodeDescriptorImpl(Short type, short band, Integer manufCode, int maxBufSize, boolean isComplexAv, boolean isUserAv) {
		this.logicalType = type;
		this.frequencyBand = band;
		this.manufacturerCode = manufCode;
		this.maxBufferSize = maxBufSize;
		this.isComplexDescriptorAvailable = isComplexAv;
		this.isUserDescriptorAvailable = isUserAv;
	}

	/**
	 * @return one of: ZigBeeNode.COORDINATOR, ZigBeeNode.ROUTER,
	 *         ZigBeeNode.END_DEVICE.
	 */
	public Short getLogicalType() {
		return logicalType;
	}

	/**
	 * @return an int corresponding to the 5 bits which indicate the frequency
	 *         range. use the frequency mask constants to get info, which are
	 *         the ranges actually supported.
	 */
	public short getFrequencyBand() {
		return frequencyBand;
	}

	/**
	 * @return the manufacurer code field.
	 */
	public Integer getManufacturerCode() {
		return manufacturerCode;
	}

	/**
	 * @return the maximum buffer size field.
	 */
	public int getMaxBufferSize() {
		return maxBufferSize;
	}

	/**
	 * @return the maximum incoming transfer size field.
	 */
	public int getMaxIncomingTransferSize() {
		return 0;
	}

	/**
	 * @return the maximum outgoing transfer size field.
	 */
	public int getMaxOutgoingTransferSize() {
		return 0;
	}

	/**
	 * @return the server mask field.
	 */
	public int getServerMask() {
		return 0;
	}

	/**
	 * @return true if this node is capable of becoming PAN coordinator or false
	 *         otherwise.
	 */
	public boolean isAlternatePANCoordinator() {
		return true;
	}

	/**
	 * @return true if this node a full function device false otherwise.
	 */
	public boolean isFullFunctionDevice() {
		return true;
	}

	/**
	 * @return true if the current power source is mains power or false
	 *         otherwise.
	 */
	public boolean isMainsPower() {
		return true;
	}

	/**
	 * @return true if the device does not disable its receiver to conserve
	 *         power during idle periods or false otherwise.
	 */
	public boolean isReceiverOnWhenIdle() {
		return true;
	}

	/**
	 * @return true if the device is capable of sending and receiving secured
	 *         frames or false otherwise.
	 */
	public boolean isSecurityCapable() {
		return true;
	}

	/**
	 * @return true if the device is address allocate or false otherwise.
	 */
	public boolean isAddressAllocate() {
		return true;
	}

	/**
	 * @return true if extended active endpoint list is available or false
	 *         otherwise.
	 */
	public boolean isExtendedActiveEndpointListAvailable() {
		return true;
	}

	/**
	 * @return true if extended simple descriptor is available or false
	 *         otherwise.
	 */
	public boolean isExtendedSimpleDescriptorListAvailable() {
		return true;
	}

	/**
	 * @return true if a complex descriptor is available or false otherwise.
	 */
	public boolean isComplexDescriptorAvailable() {
		return isComplexDescriptorAvailable;
	}

	/**
	 * @return true if a user descriptor is available or false otherwise.
	 */
	public boolean isUserDescriptorAvailable() {
		return isUserDescriptorAvailable;
	}

}
