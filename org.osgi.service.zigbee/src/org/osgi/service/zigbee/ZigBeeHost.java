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

package org.osgi.service.zigbee;

import java.io.IOException;
import org.osgi.util.promise.Promise;

/**
 * This interface represents the machine that hosts the code to run a ZigBee
 * device or client. This machine is, for example, the ZigBee chip/dongle that
 * is controlled by the basedriver (below/under the OSGi execution environment).
 * <br>
 * ZigBeeHost is more than a ZigBeeNode.<br>
 * It must be registered as a OSGi service.
 * 
 * @author $Id$
 */
public interface ZigBeeHost extends ZigBeeNode {

	/**
	 * UNLIMITED_BROADCAST_RADIUS
	 */
	public static final short UNLIMITED_BROADCAST_RADIUS = 0xff;

	/**
	 * Starts the host.
	 * 
	 * If the host is a Coordinator, then it can be started with or without
	 * PAN_ID and Extended PAN_ID (i.e. if no PAN_ID, and Extended PAN_ID are
	 * given, then they will be automatically generated and then added to the
	 * service properties).
	 * 
	 * If the host is a router, or an end device, then the host may start
	 * without a registered PAN_ID property; the property will be set when the
	 * host will find and join a ZigBee network.
	 * 
	 * The host status must be persistent, i.e. if the host was started, then
	 * the host must starts again when the bundle restarts. In addition, the
	 * values of channel, pan id, extended pan id, and host pid must remain the
	 * same.
	 * 
	 * @throws Exception, any exception related to the communication with the
	 *         chip.
	 */
	public void start() throws Exception;

	/**
	 * Stops the host.
	 * 
	 * @throws Exception, any exception related to the communication with the
	 *         chip.
	 */
	public void stop() throws Exception;

	/**
	 * Get the host's start/stop state.
	 * 
	 * @return true if the host is started.
	 */
	boolean isStarted();

	/**
	 * Set the panId.
	 * 
	 * @param panId The network Personal Area Network identifier (PAND ID)
	 * @throws IllegalStateException, is thrown in case the host is still
	 *         started.
	 */
	void setPanId(int panId) throws IllegalStateException;

	/**
	 * Set the extendedPanId.
	 * 
	 * @param extendedPanId The network Extended PAN identifier(EPID)
	 * @throws IllegalStateException, is thrown in case the host is still
	 *         started.
	 */
	void setExtendedPanId(long extendedPanId) throws IllegalStateException;

	/**
	 * Indicates if a ZigBee device can join the network.
	 * 
	 * Broadcasts a Mgmt_Permit_req to all routers and the coordinator. If the
	 * duration argument is not equal to zero or 0xFF, the argument is a number
	 * of seconds and joining is permitted until it counts down to zero, after
	 * which time, joining is not permitted. If the duration is set to zero,
	 * joining is not permitted. If set to 0xFF, joining is permitted
	 * indefinitely or until another Mgmt_Permit_Joining_req is received by the
	 * coordinator.
	 * 
	 * As described in
	 * "Table 2.133 Fields of the Mgmt_Permit_Joining_rsp Command" of the ZigBee
	 * specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a permitjoin
	 * request can have the following status: SUCCESS, INVALID_REQUEST,
	 * NOT_AUTHORIZED or any status code returned from the
	 * NLMEPERMITJOINING.confirm primitive.
	 * 
	 * @param duration The time during which associations are permitted.
	 * @throws Exception, any exception related to the communication with the
	 *         chip.
	 */
	public void permitJoin(short duration) throws Exception;

	/**
	 * Sets the host logical node type. ZigBee defines three different types of
	 * node, coordinator({@link org.osgi.service.zigbee.ZigBeeNode ->
	 * COORDINATOR}), router( {@link org.osgi.service.zigbee.ZigBeeNode ROUTER})
	 * and end device( {@link org.osgi.service.zigbee.ZigBeeNode -> END_DEVICE})
	 * 
	 * @param logicalNodeType The logical node type.
	 * @throws IllegalStateException, is thrown in case the host is still
	 *         started.
	 * @throws Exception, any exception related to the communication with the
	 *         chip.
	 */
	public void setLogicalType(short logicalNodeType) throws IllegalStateException, Exception;

	/**
	 * @return The current network channel.
	 * @throws Exception, any exception related to the communication with the
	 *         chip.
	 */
	public int getChannel() throws Exception;

	/**
	 * Updates the network channel. 802.15.4 and ZigBee divide the 2.4Ghz band
	 * into 16 channels, numbered from 11 to 26.
	 * 
	 * As described in "Table 2.4.3.3.9 Mgmt_NWK_Update_req" of the ZigBee
	 * specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, this request is
	 * sent as broadcast by the network manager with a ScanDuration to be set
	 * with the channel parameter.
	 * 
	 * @param channel The network channel.
	 * @throws IllegalStateException, is thrown in case the host is still
	 *         started, or in case the host is not a network manager.
	 * @throws IOException for serial communication exception.
	 */
	public void updateNetworkChannel(byte channel) throws IllegalStateException, IOException;

	/**
	 * @return The currently configured channel mask.
	 * @throws Exception, any exception related to the communication with the
	 *         chip.
	 */
	public int getChannelMask() throws Exception;

	/**
	 * Set a new configured channel mask.
	 * 
	 * As described in "Table 2.13 APSME-SET.confirm Parameters" of the ZigBee
	 * specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a set request
	 * can have the following status: SUCCESS, INVALID_PARAMETER or
	 * UNSUPPORTED_ATTRIBUTE (see {@link APSException}).
	 * 
	 * @param mask A value representing the channel mask.
	 * @throws IllegalStateException, is thrown in case the host is still
	 *         started.
	 * @throws IOException for serial communication exception.
	 */
	public void setChannelMask(int mask) throws IllegalStateException, IOException;

	/**
	 * The method forces a new scan. It checks that the ZigBeeNode services are
	 * still representing an available node on the network. It also updates the
	 * whole representation of all nodes (endpoints, clusters, descriptors,
	 * attributes).
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         In case of success the promise will resolve with Boolean.TRUE
	 *         otherwise the promise is failed with an exception.
	 * 
	 * 
	 * @throws Exception, any exception related to the communication with the
	 *         chip.
	 */
	public Promise /* <Boolean> */ refreshNetwork() throws Exception;

	/**
	 * @return The network security level, i.e. 0 if security is disabled, an
	 *         int code if enabled (see
	 *         "Table 4.38 Security Levels Available to the NWK, and APS Layers"
	 *         of the ZigBee specification").
	 * @throws Exception, any exception related to the communication with the
	 *         chip.
	 */
	public int getSecurityLevel() throws Exception;

	/**
	 * @return The current preconfigured link key.
	 * @throws Exception, any exception related to the communication with the
	 *         chip.
	 */
	public String getPreconfiguredLinkKey() throws Exception;

	/**
	 * This method is used for creating a {@link ZigBeeGroup} service that has
	 * not yet been discovered by the ZigBee Base Driver or that does not exist
	 * on the ZigBee network yet. <br>
	 * 
	 * @param groupAddress the address of the group to create.
	 * @throws Exception when a ZigBeeGroup service with the same groupAddress
	 *         already exists.
	 */
	public void createGroupService(int groupAddress) throws Exception;

	/**
	 * Broadcast a the ZCL frame to the cluster ID of all the nodes of the
	 * ZigBee network. The {@link #setBroadcastRadius(short)} method, may be
	 * used to limit the broadcast radius used in the subsequent broadcast
	 * calls.
	 * 
	 * @param clusterID The cluster ID this ZCL frame must be sent to.
	 * @param frame A ZCL Frame.
	 * @return a response stream instance that collects and allows the caller to
	 *         be asynchronously notified about the ZCLFrame responses sent back
	 *         by the ZigBee nodes.
	 */
	ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame);

	/**
	 * Broadcast a the ZCL frame to the cluster ID of all the nodes of the
	 * ZigBee network. The passed {@code exportedServicePID} allows to force the
	 * source endpoint of the message sent to be the endpoint id of the exported
	 * ZigBeeEndPoint service having the specified service.pid property.
	 * 
	 * @param clusterID The cluster ID.
	 * @param frame A ZCL Frame.
	 * @param exportedServicePID the source endpoint of the command request. In
	 *        targeted situations, the source endpoint is the valid service PID
	 *        of an exported endpoint.
	 * @return a response stream instance that collects and allows the caller to
	 *         be asynchronously notified about the ZCLFrame responses sent back
	 *         by the ZigBee nodes.
	 * 
	 * @see #setBroadcastRadius(short) for setting the broadcast radius.
	 * 
	 */
	ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame, String exportedServicePID);

	/**
	 * @return the current broadcast radius value.
	 */
	short getBroadcastRadius();

	/**
	 * By default the {@link ZigBeeHost} must use
	 * {@link #UNLIMITED_BROADCAST_RADIUS} as default value for the broadcast
	 * 
	 * @param broadcastRadius - is the number of routers that the messages are
	 *        allowed to cross. Radius value is in the range from 0 to 0xff.
	 * 
	 * @throws IllegalArgumentException if set with a value out of the expected
	 *         range.
	 * 
	 * @throws IllegalStateException if set when the ZigBeeHost is "running".
	 */
	void setBroadcastRadius(short broadcastRadius) throws IllegalArgumentException, IllegalStateException;

	/**
	 * Set the timeout for the communication sent through this device
	 * 
	 * @param seconds the number of seconds before firing a timeout exception
	 */
	void setComunicationTimeout(int seconds);

	/**
	 * 
	 * @return the current number of seconds used for firing timeout in
	 *         communication
	 */
	int getComunicationTimeout();
}
