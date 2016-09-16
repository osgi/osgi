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
 * This interface represents the Node Descriptor MAC Capability Flags as
 * described in the ZigBee Specification.
 * 
 * @noimplement
 * 
 * @author $Id$
 */

public interface ZigBeeMacCapabiliyFlags {

	/**
	 * @return true if this node is capable of becoming PAN coordinator or false
	 *         otherwise.
	 */
	public boolean isAlternatePANCoordinator();

	/**
	 * @return true if this node a Full Function Device (FFD), false otherwise
	 *         (it is a Reduced Function Device, RFD)
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

}
