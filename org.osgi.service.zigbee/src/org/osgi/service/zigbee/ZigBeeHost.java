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

package org.osgi.service.zigbee;

import org.osgi.service.zigbee.handler.ZigBeeHandler;

/**
 * This interface represents the machine that hosts the code to run a ZigBee
 * device or client ZigBeeHost must be registered as a service
 * 
 * @version 1.0
 */
public interface ZigBeeHost extends ZigBeeNode {
	/**
	 * Starts the host
	 */
	public void start() throws ZigBeeException;

	/**
	 * Indicates if a ZigBee device can join the network.
	 * 
	 * @param duration The time during which associations are permitted.
	 * @return true, false, or null(if the ZigBeeHost is an end device)
	 */
	public void permitJoin(short duration) throws ZigBeeException;

	/**
	 * Sets the host operational mode. The mode will then be available when the
	 * host will restart. ZigBee defines three different types of node,
	 * coordinator({@link ZigBeeNode.COORDINATOR}), router(
	 * {@link ZigBeeNode.ROUTER}) and end device({@link ZigBeeNode.END_DEVICE})
	 * 
	 * @param operationalMode The node operational mode.
	 */
	public void setOperationalMode(short operationalMode) throws ZigBeeException;

	/**
	 * Gets the current network channel
	 * 
	 * @return The current network channel
	 */
	public int getChannel() throws ZigBeeException;

	/**
	 * Sets the network channel. 802.15.4 and ZigBee break the 2.4Ghz band into
	 * 16 channels, numbered from 11 to 26.
	 * 
	 * @param handler The handler that manages the command response.
	 * @param channel The network channel.
	 */
	public void setChannel(ZigBeeHandler handler, byte channel) throws ZigBeeException;

	/**
	 * Gets the current network channel mask
	 * 
	 * @return The current network channel mask
	 */
	public int getChannelMask() throws ZigBeeException;

	/**
	 * Set the network channel mask
	 * 
	 * @param handler The handler that manages the command response.
	 * @param mask A value representing the channel mask.
	 */
	public void setChannelMask(ZigBeeHandler handler, int mask) throws ZigBeeException;

	/**
	 * Updates the list of devices in the network, by adding the new devices
	 * that joined the network and removing the devices that left the network
	 * since the last refresh.
	 */
	public void refreshNetwork() throws ZigBeeException;

	/**
	 * Gets the network security level
	 * 
	 * @return 0 if security is disabled, an int code if enabled
	 */
	public int getSecurityLevel() throws ZigBeeException;

	/**
	 * Gets the current network key
	 * 
	 * @return The current Network key.
	 */
	public String getNetworkKey() throws ZigBeeException;
}
