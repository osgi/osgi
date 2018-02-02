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

package org.osgi.test.cases.zigbee.descriptors;

import org.osgi.service.zigbee.descriptors.ZigBeeFrequencyBand;
import org.osgi.service.zigbee.descriptors.ZigBeeMacCapabiliyFlags;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeServerMask;

/**
 * Mocked implementation of a ZigBeeNodeDescriptor. Only the fields that are
 * actually tested by the CT are returning a meaningful value.
 * 
 * @author $Id$
 */
public class ZigBeeNodeDescriptorImpl implements ZigBeeNodeDescriptor {

	private short				logicalType;
	private ZigBeeFrequencyBand	frequencyBand;
	private int					manufacturerCode;
	private int					maxBufferSize;
	private boolean				isComplexDescriptorAvailable;
	private boolean				isUserDescriptorAvailable;

	public ZigBeeNodeDescriptorImpl(short logicalType, short band, int manufacturerCode, int maxBufferSize, boolean isComplexAvailable, boolean isUserAvailable) {
		this.logicalType = logicalType;
		this.frequencyBand = new ZigBeeFrequencyBandImpl(band);
		this.manufacturerCode = manufacturerCode;
		this.maxBufferSize = maxBufferSize;
		this.isComplexDescriptorAvailable = isComplexAvailable;
		this.isUserDescriptorAvailable = isUserAvailable;
	}

	public ZigBeeFrequencyBand getFrequencyBand() {
		return frequencyBand;
	}

	public int getManufacturerCode() {
		return manufacturerCode;
	}

	public int getMaxBufferSize() {
		return maxBufferSize;
	}

	public int getMaxIncomingTransferSize() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	public int getMaxOutgoingTransferSize() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	public ZigBeeServerMask getServerMask() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	public boolean isExtendedActiveEndpointListAvailable() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	public boolean isExtendedSimpleDescriptorListAvailable() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	public boolean isComplexDescriptorAvailable() {
		return isComplexDescriptorAvailable;
	}

	public boolean isUserDescriptorAvailable() {
		return isUserDescriptorAvailable;
	}

	public short getLogicalType() {
		return this.logicalType;
	}

	public ZigBeeMacCapabiliyFlags getMacCapabilityFlags() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}
}
