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

package org.osgi.service.zigbee.descriptors;

/**
 * This interface represents a Node Descriptor as described in the ZigBee
 * Specification The Node Descriptor contains information about the capabilities
 * of the node.
 * 
 * @author $Id$
 */
public interface ZigBeeNodeDescriptor {

	/**
	 * @return one of: ZigBeeNode.COORDINATOR, ZigBeeNode.ROUTER,
	 *         ZigBeeNode.END_DEVICE.
	 */
	public short getLogicalType();

	/**
	 * @return true if a complex descriptor is available or false otherwise.
	 */
	public boolean isComplexDescriptorAvailable();

	/**
	 * @return true if a user descriptor is available or false otherwise.
	 */
	public boolean isUserDescriptorAvailable();

	/**
	 * @return returns the information about the frequency band the radio is
	 *         currently operating on.
	 */
	public ZigBeeFrequencyBand getFrequencyBand();

	/**
	 * @return the MAC Capability Flags field information
	 */
	public ZigBeeMacCapabiliyFlags getMacCapabilityFlags();

	/**
	 * @return the manufacurer code field.
	 */
	public int getManufacturerCode();

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
	 * 
	 *         TODO: description
	 */
	public ZigBeeServerMask getServerMask();

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
