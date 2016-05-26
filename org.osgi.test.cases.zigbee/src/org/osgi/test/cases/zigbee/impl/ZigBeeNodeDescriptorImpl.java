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

package org.osgi.test.cases.zigbee.impl;

import org.osgi.service.zigbee.descriptors.ZigBeeFrequencyBand;
import org.osgi.service.zigbee.descriptors.ZigBeeMacCapabiliyFlags;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeServerMask;

/**
 * Mocked impl of ZigBeeNodeDescriptor.
 * 
 * @author $Id$
 */
public class ZigBeeNodeDescriptorImpl implements ZigBeeNodeDescriptor {

	private short				logicalType;
	private ZigBeeFrequencyBand	frequencyBand;
	private Integer				manufacturerCode;
	private int					maxBufferSize;
	private boolean				isComplexDescriptorAvailable;
	private boolean				isUserDescriptorAvailable;

	/**
	 * @param type
	 * @param band
	 * @param manufCode
	 * @param maxBufSize
	 * @param isComplexAv
	 * @param isUserAv
	 */
	public ZigBeeNodeDescriptorImpl(short type, short band, Integer manufCode, int maxBufSize, boolean isComplexAv,
			boolean isUserAv) {
		this.logicalType = type;
		this.frequencyBand = new ZigBeeFrequencyBandImpl(band);
		this.manufacturerCode = manufCode;
		this.maxBufferSize = maxBufSize;
		this.isComplexDescriptorAvailable = isComplexAv;
		this.isUserDescriptorAvailable = isUserAv;
	}

	/**
	 * @return {@link ZigBeeFrequencyBand} object which indicates the frequency
	 *         ranges that are actually supported.
	 */
	public ZigBeeFrequencyBand getFrequencyBand() {
		return frequencyBand;
	}

	/**
	 * @return the manufacurer code field.
	 */
	public int getManufacturerCode() {
		return manufacturerCode.intValue();
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
	 * @return the server type field.
	 */
	public ZigBeeServerMask getServerMask() {
		return null;
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

	public short getLogicalType() {
		return this.logicalType;
	}

	public ZigBeeMacCapabiliyFlags getMacCapabilityFlags() {
		return new ZigBeeMacCapabiliyFlags() {

			public boolean isSecurityCapable() {
				return false;
			}

			public boolean isReceiverOnWhenIdle() {
				return false;
			}

			public boolean isMainsPower() {
				return false;
			}

			public boolean isFullFunctionDevice() {
				return false;
			}

			public boolean isAlternatePANCoordinator() {
				return false;
			}

			public boolean isAddressAllocate() {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}
}
