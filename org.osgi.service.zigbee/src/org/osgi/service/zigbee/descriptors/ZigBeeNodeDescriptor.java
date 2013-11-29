/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.service.zigbee.descriptors;

/**
 * This interface represents a Node Descriptor as described in the ZigBee
 * Specification The Node Descriptor contains information about the capabilities
 * of the node.
 * 
 * @version 1.0
 */
public interface ZigBeeNodeDescriptor {

	/**
	 * Frequency mask for the range 868 - 868.6 MHz.
	 */
	public static final short	FREQUENCY_RANGE_868_MASK			= 1;

	/**
	 * Frequency mask for the range 902 - 928 MHz
	 */
	public static final short	FREQUENCY_RANGE_902_928_MASK		= 4;

	/**
	 * Frequency mask for the range 2400 - 2483.5 MHz
	 */
	public static final short	FREQUENCY_RANGE_2400_2483_MASK		= 8;

	/**
	 * Primary Trust Center server mask.
	 */
	public static final int		PRIMARY_TRUST_CENTER_MASK			= 1;

	/**
	 * Backup Trust Center server mask.
	 */
	public static final int		BACKUP_TRUST_CENTER_MASK			= 2;

	/**
	 * Primary Binding Table Cache server mask.
	 */
	public static final int		PRIMARY_BINDING_TABLE_CACHE_MASK	= 4;

	/**
	 * Backup Discovery Cache server mask.
	 */
	public static final int		BACKUP_BINDING_TABLE_CACHE_MASK		= 8;

	/**
	 * Frequency mask for the range 2400 - 2483.5 MHz
	 */
	public static final int		PRIMARY_DISCOVERY_CACHE_MASK		= 16;

	/**
	 * Network Manager server mask.
	 */
	public static final int		BACKUP_DISCOVERY_CACHE_MASK			= 32;

	/**
	 * Frequency mask for the range 2400 - 2483.5 MHz
	 */
	public static int			NETWORK_MANAGER_MASK				= 64;

	/**
	 * @return one of: ZigBeeNode.COORDINATOR, ZigBeeNode.ROUTER,
	 *         ZigBeeNode.END_DEVICE.
	 */
	public Short getLogicalType();

	/**
	 * @return true if a complex descriptor is available or false otherwise.
	 */
	public boolean isComplexDescriptorAvailable();

	/**
	 * @return true if a user descriptor is available or false otherwise.
	 */
	public boolean isUserDescriptorAvailable();

	/**
	 * @return an int corresponding to the 5 bits which indicate the frequency
	 *         range. use the frequency mask constants to get info, which are
	 *         the ranges actually supported.
	 */
	public short getFrequencyBand();

	/**
	 * @return the manufacurer code field.
	 */
	public Integer getManufacturerCode();

	/**
	 * @return the maximum buffer size field.
	 */
	public int getMaxBufferSize();

	/**
	 * @return the maximum incoming transfer size field.
	 */
	public int getMaxIncomingTransferSize();

	/**
	 * @return the maximum outgoing transfer size field.
	 */
	public int getMaxOutgoingTransferSize();

	/**
	 * @return the server mask field.
	 */
	public int getServerMask();

	/**
	 * @return true if this node is capable of becoming PAN coordinator or false
	 *         otherwise.
	 */
	public boolean isAlternatePANCoordinator();

	/**
	 * @return true if this node a full function device false otherwise.
	 */
	public boolean isFullFunctionDevice();

	/**
	 * @return true if the current power source is mains power or false
	 *         otherwise.
	 */
	public boolean isMainsPower();

	/**
	 * @return true if the device does not disable its receiver to conserve
	 *         power during idle periods or false otherwise.
	 */
	public boolean isReceiverOnWhenIdle();

	/**
	 * @return true if the device is capable of sending and receiving secured
	 *         frames or false otherwise.
	 */
	public boolean isSecurityCapable();

	/**
	 * @return true if the device is address allocate or false otherwise.
	 */
	public boolean isAddressAllocate();

	/**
	 * @return true if extended active endpoint list is available or false
	 *         otherwise.
	 */
	public boolean isExtendedActiveEndpointListAvailable();

	/**
	 * @return true if extended simple descriptor is available or false
	 *         otherwise.
	 */
	public boolean isExtendedSimpleDescriptorListAvailable();

}
