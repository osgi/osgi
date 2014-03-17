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

import org.osgi.framework.Constants;

/**
 * This interface represents the machine that hosts the code to run a ZigBee
 * device or client. This machine is, for example, the ZigBee chip/dongle that
 * is controlled by the basedriver (below/under the OSGi execution environment).<br>
 * ZigBeeHost is more than a ZigBeeNode.<br>
 * It must be registered as a service.
 * 
 * @version 1.0
 */
public interface ZigBeeHost extends ZigBeeNode {

	/**
	 * Starts the host.
	 * 
	 * The host status must be persitent, i.e. if the host was started, then the
	 * host must starts again when the bundle restarts. In addition, the values
	 * of channel, pan id, extended pan id, and host pid must remain the same.
	 * 
	 * With the PAN that is chosen with the host is an end device. If no PAN ID
	 * is registered, If the host is a router or an end device, the host may
	 * start without a registered PAN_ID property and set the property when the
	 * host finds and joins a network.
	 * 
	 * @throws ZCLException
	 */
	public void start() throws ZCLException;

	/**
	 * Stops the host.
	 * 
	 * @throws ZCLException
	 */
	public void stop() throws ZCLException;

	/**
	 * Get the host's start/stop state.
	 * 
	 * @return true if the host is started.
	 */
	boolean isStarted();

	/**
	 * Indicates if a ZigBee device can join the network.
	 * 
	 * @param duration The time during which associations are permitted.
	 * @throws ZCLException
	 */
	public void permitJoin(short duration) throws ZCLException;

	/**
	 * Sets the host logical node type. The logical type will then be available
	 * when the host will restart. ZigBee defines three different types of node,
	 * coordinator({@link org.osgi.service.zigbee.ZigBeeNode -> COORDINATOR}),
	 * router( {@link org.osgi.service.zigbee.ZigBeeNode ROUTER}) and end
	 * device( {@link org.osgi.service.zigbee.ZigBeeNode -> END_DEVICE})
	 * 
	 * @param logicalNodeType The logical node type.
	 * @throws ZCLException
	 */
	public void setLogicalType(short logicalNodeType) throws ZCLException;

	/**
	 * @return The current network channel.
	 * @throws ZCLException
	 */
	public int getChannel() throws ZCLException;

	/**
	 * Sets the network channel. 802.15.4 and ZigBee break the 2.4Ghz band into
	 * 16 channels, numbered from 11 to 26.
	 * 
	 * @param handler The handler that manages the command response.
	 * @param channel The network channel.
	 */
	public void setChannel(ZigBeeHandler handler, byte channel);

	/**
	 * @return The current network channel mask.
	 * @throws ZCLException
	 */
	public int getChannelMask() throws ZCLException;

	/**
	 * Set the network channel mask
	 * 
	 * @param handler The handler that manages the command response.
	 * @param mask A value representing the channel mask.
	 */
	public void setChannelMask(ZigBeeHandler handler, int mask);

	/**
	 * Updates the list of devices in the network by adding the new devices that
	 * joined the network and removing the devices that left the network since
	 * the last refresh.
	 * 
	 * @throws ZCLException
	 */
	public void refreshNetwork() throws ZCLException;

	/**
	 * @return The network security level, i.e. 0 if security is disabled, an
	 *         int code if enabled.
	 * @throws ZCLException
	 */
	public int getSecurityLevel() throws ZCLException;

	/**
	 * @return The current Network key.
	 * @throws ZCLException
	 */
	public String getNetworkKey() throws ZCLException;

	/**
	 * This method is used for creating a {@link ZigBeeGroup} service that has
	 * not yet been discovered by the ZigBee Base Driver or that does not exist
	 * on the ZigBee network yet. <br>
	 * The method may be invoked on exported endpoint or even on import
	 * endpoint. In the former case, the ZigBee Base Driver should rely on the
	 * <i>APSME-ADD-GROUP</i> API defined by the ZigBee Specification, in the
	 * former case it will use the proper commands of the <i>Groups</i> cluster
	 * of the ZigBee Specification Library
	 * 
	 * @param pid {@link String} representing the PID (see
	 *        {@link Constants#SERVICE_PID} ) of the {@link ZigBeeEndpoint} that
	 *        we want leave to this Group.
	 * @param groupAddress the address of the group to create.
	 * @param handler the {@link ZigBeeCommandHandler} that will be notified of
	 *        the result of "creation".
	 * @throws ZCLException
	 */
	public void createGroupService(String pid, int groupAddress, ZigBeeCommandHandler handler) throws ZCLException;

	/**
	 * Enable to broadcast a given frame on a given cluster.
	 * 
	 * @param clusterID the cluster ID.
	 * @param frame a command frame sequence.
	 * @param handler The handler that manages the command response.
	 */
	void broadcast(Integer clusterID, ZCLFrame frame, ZigBeeCommandHandler handler);

	/**
	 * Enable to broadcast a given frame on a given cluster.
	 * 
	 * @param clusterID the cluster ID.
	 * @param frame a command frame sequence.
	 * @param handler The handler that manages the command response.
	 * @param exportedServicePID : the source endpoint of the command request.
	 *        In targeted situations, the source endpoint is the valid service
	 *        PID of an exported endpoint.
	 */
	void broadcast(Integer clusterID, ZCLFrame frame, ZigBeeCommandHandler handler, String exportedServicePID);

}
