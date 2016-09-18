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
 * @noimplement
 * 
 * @author $Id$
 */
public interface ZigBeeNodeDescriptor {

	/**
	 * Returns the logical type of the described node.
	 * 
	 * @return one of: ZigBeeNode.COORDINATOR, ZigBeeNode.ROUTER,
	 *         ZigBeeNode.END_DEVICE.
	 */
	public short getLogicalType();

	/**
	 * Checks if a complex descriptor is available.
	 * 
	 * @return true if a complex descriptor is available or false otherwise.
	 */
	public boolean isComplexDescriptorAvailable();

	/**
	 * Checks if a user descriptor is available.
	 * 
	 * @return true if a user descriptor is available or false otherwise.
	 */
	public boolean isUserDescriptorAvailable();

	/**
	 * Returns the radio frequency band the node is currently operating on.
	 * 
	 * @return returns the information about the radio frequency band the node
	 *         is currently operating on.
	 */
	public ZigBeeFrequencyBand getFrequencyBand();

	/**
	 * Returns the MAC Capability Flags field information.
	 * 
	 * @return the MAC Capability Flags field information.
	 */
	public ZigBeeMacCapabiliyFlags getMacCapabilityFlags();

	/**
	 * Returns the manufacturer code of the described node.
	 * 
	 * @return the manufacturer code of the described node.
	 */
	public int getManufacturerCode();

	/**
	 * Returns the maximum buffer size of the described node.
	 * 
	 * @return the maximum buffer size of the described node.
	 */
	public int getMaxBufferSize();

	/**
	 * Returns the maximum incoming transfer size of the described node.
	 * 
	 * @return the maximum incoming transfer size of the described node.
	 */
	public int getMaxIncomingTransferSize();

	/**
	 * Returns the maximum outgoing transfer size of the described node.
	 * 
	 * @return the maximum outgoing transfer size of the described node.
	 */
	public int getMaxOutgoingTransferSize();

	/**
	 * Returns the server mask of the described node.
	 * 
	 * @return the server mask of the described node.
	 */
	public ZigBeeServerMask getServerMask();

	/**
	 * Checks if extended active endpoint list is available.
	 * 
	 * @return true if extended active endpoint list is available or false
	 *         otherwise.
	 */
	public boolean isExtendedActiveEndpointListAvailable();

	/**
	 * Checks if extended simple descriptor is available.
	 * 
	 * @return true if extended simple descriptor is available or false
	 *         otherwise.
	 */
	public boolean isExtendedSimpleDescriptorListAvailable();

}
