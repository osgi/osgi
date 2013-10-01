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

/**
 * This interface represents the machine that hosts the code to run a ZigBee
 * device or client ZigBeeHost must be registered as a service
 * 
 * @version 1.0
 */
public interface ZigBeeHost extends ZigBeeNode {

	/**
	 * Starts the host
	 * 
	 * @throws ZigBeeException
	 */
	public void start() throws ZigBeeException;

	/**
	 * Stops the host
	 * 
	 * @throws ZigBeeException
	 */
	public void stop() throws ZigBeeException;

	/**
	 * Indicates if a ZigBee device can join the network.
	 * 
	 * @param duration The time during which associations are permitted.
	 * @throws ZigBeeException
	 */
	public void permitJoin(short duration) throws ZigBeeException;

	/**
	 * Sets the host logical node type. The logical type will then be available
	 * when the host will restart. ZigBee defines three different types of node,
	 * coordinator({@link org.osgi.service.zigbee.ZigBeeNode -> COORDINATOR}),
	 * router( {@link org.osgi.service.zigbee.ZigBeeNode ROUTER}) and end
	 * device( {@link org.osgi.service.zigbee.ZigBeeNode -> END_DEVICE})
	 * 
	 * @param logicalNodeType The logical node type.
	 * @throws ZigBeeException
	 */
	public void setLogicalType(short logicalNodeType) throws ZigBeeException;

	/**
	 * Gets the current network channel
	 * 
	 * @return The current network channel
	 * @throws ZigBeeException
	 */
	public int getChannel() throws ZigBeeException;

	/**
	 * Sets the network channel. 802.15.4 and ZigBee break the 2.4Ghz band into
	 * 16 channels, numbered from 11 to 26.
	 * 
	 * @param handler The handler that manages the command response.
	 * @param channel The network channel.
	 * @throws ZigBeeException
	 */
	public void setChannel(ZigBeeHandler handler, byte channel) throws ZigBeeException;

	/**
	 * Gets the current network channel mask
	 * 
	 * @return The current network channel mask
	 * @throws ZigBeeException
	 */
	public int getChannelMask() throws ZigBeeException;

	/**
	 * Set the network channel mask
	 * 
	 * @param handler The handler that manages the command response.
	 * @param mask A value representing the channel mask.
	 * @throws ZigBeeException
	 */
	public void setChannelMask(ZigBeeHandler handler, int mask) throws ZigBeeException;

	/**
	 * Updates the list of devices in the network, by adding the new devices
	 * that joined the network and removing the devices that left the network
	 * since the last refresh.
	 * 
	 * @throws ZigBeeException
	 */
	public void refreshNetwork() throws ZigBeeException;

	/**
	 * Gets the network security level
	 * 
	 * @return 0 if security is disabled, an int code if enabled
	 * @throws ZigBeeException
	 */
	public int getSecurityLevel() throws ZigBeeException;

	/**
	 * Gets the current network key
	 * 
	 * @return The current Network key.
	 * @throws ZigBeeException
	 */
	public String getNetworkKey() throws ZigBeeException;
}
