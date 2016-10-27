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

import java.math.BigInteger;
import java.util.Map;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.util.promise.Promise;

/**
 * This interface represents a ZigBee node, means a physical device that can
 * communicate using the ZigBee protocol.
 * <p>
 * Each physical device may contain up 240 logical devices which are represented
 * by the {@link ZigBeeEndpoint} class.
 * <p>
 * Each logical device is identified by an <i>EndPoint</i> address, but shares:
 * <ul>
 * <li>either the <i>64-bit 802.15.4 IEEE Address</i></li>
 * <li>or the <i>16-bit ZigBee Network Address</i>.</li>
 * </ul>
 * 
 * @noimplement
 * 
 * @author $Id$
 */
public interface ZigBeeNode {

	/**
	 * Property key for the mandatory node IEEE Address representing node MAC
	 * address. MAC Address is a 12-digit(48-bit) or 16-digit(64-bit)
	 * hexadecimal numbers. There is no need to use 0x hexadecimal notation. For
	 * example:
	 * <ul>
	 * <li><i>zigbee.node.ieee.address="00:25:96:AB:37:56"</i> for a 48-bit
	 * address</li>
	 * <li><i>zigbee.node.ieee.address="00:25:96:FF:FE:AB:37:56"</i> for a
	 * 64-bit address</li>
	 * </ul>
	 * 
	 * A ZigBee Event Listener service can announce for what ZigBee device nodes
	 * it wants notifications.
	 */
	public static final String	IEEE_ADDRESS			= "zigbee.node.ieee.address";

	/**
	 * Property key for the device logical type.
	 */
	public static final String	LOGICAL_TYPE			= "zigbee.node.description.node.type";

	/**
	 * Property key for a manufacturer code that is allocated by the ZigBee
	 * Alliance, relating the manufacturer to the device.
	 */
	public static final String	MANUFACTURER_CODE		= "zigbee.node.description.manufacturer.code";

	/**
	 * Key of {@link String} containing the device node network PAN ID.
	 */
	public static final String	PAN_ID					= "zigbee.node.pan.id";

	/**
	 * Key of {@link String} containing the device node network extended PAN ID.
	 * If the device type is "Coordinator", the extended pan id may be available
	 * only after the network is started. It means that internally the
	 * ZigBeeHost interface must update the service properties.
	 */
	public static final String	EXTENDED_PAN_ID			= "zigbee.node.extended.pan.id";

	/**
	 * ZigBee power source, that is, 3rd bit of "MAC Capabilities" in Node
	 * Descriptor. Set to 1 if the current power source is mains power, set to 0
	 * otherwise.
	 */
	public static final String	POWER_SOURCE			= "zigbee.node.power.source";

	/**
	 * ZigBee receiver on when idle, that is, 4th bit of "MAC Capabilities" in
	 * Node Descriptor. Set to 1 if the device does not disable its receiver to
	 * conserve power during idle periods, set to 0 otherwise.
	 */
	public static final String	RECEIVER_ON_WHEN_IDLE	= "zigbee.node.receiver.on.when.idle";

	/**
	 * The Node is a ZigBee End Device.
	 */
	public static final short	ZED						= 0x01;

	/**
	 * The Node is a ZigBee Coordinator.
	 */
	public static final short	COORDINATOR				= 0x02;

	/**
	 * The Node is a ZigBee Router.
	 */
	public static final short	ROUTER					= 0x03;

	/**
	 * Returns the ZigBee device node IEEE Address of this node.
	 * 
	 * @return the ZigBee device node IEEE Address of this node.
	 */
	public BigInteger getIEEEAddress();

	/**
	 * Returns the current network address of this node.
	 * 
	 * @return the current network address of this node.
	 */
	public int getNetworkAddress();

	/**
	 * Returns the OSGi service PID of the ZigBee Host that is on the network of
	 * this node.
	 * 
	 * @return the OSGi service PID of the ZigBee Host that is on the network of
	 *         this node.
	 */
	public String getHostPid();

	/**
	 * Returns the network Personal Area Network identifier (PAN ID).
	 * 
	 * @return the network Personal Area Network identifier (PAN ID).
	 */
	public int getPanId();

	/**
	 * Returns the network Extended PAN identifier (EPID).
	 * 
	 * @return the network Extended PAN identifier (EPID).
	 */
	public BigInteger getExtendedPanId();

	/**
	 * 
	 * Returns the array of the endpoints hosted by this node.
	 * 
	 * @return the array of the endpoints hosted by this node, returns an empty
	 *         array if this node does not host any endpoint.
	 */
	public ZigBeeEndpoint[] getEndpoints();

	/**
	 * Retrieves the ZigBee node Node Descriptor.
	 * 
	 * As described in <em>Table 2.91 Fields of the Node_Desc_rsp Command</em>
	 * of the ZigBee specification, a <em>Node_Desc_rsp</em> command can return
	 * with the following status codes:
	 * <ul>
	 * <li>{@link ZDPException#SUCCESS}</li>
	 * <li>{@link ZDPException#DEVICE_NOT_FOUND}</li>
	 * <li>{@link ZDPException#INV_REQUESTTYPE}</li>
	 * <li>{@link ZDPException#NO_DESCRIPTOR}</li>
	 * </ul>
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         It will be used in order to return the node descriptor
	 *         {@link ZigBeeNodeDescriptor}. If the ZDP <em>Node_Desc_rsp</em>
	 *         do not return success, the promise must fail with a
	 *         {@link ZDPException} exception with the correct status code.
	 */
	public Promise /* <ZigBeeNodeDescriptor> */ getNodeDescriptor();

	/**
	 * Retrieves the ZigBee node Power Descriptor.
	 * 
	 * As described in <em>Table 2.92 Fields of the Power_Desc_rsp Command</em>
	 * of the ZigBee specification, a <em>Power_Desc_rsp</em> command can return
	 * with the following status codes:
	 * <ul>
	 * <li>{@link ZDPException#SUCCESS}</li>
	 * <li>{@link ZDPException#DEVICE_NOT_FOUND}</li>
	 * <li>{@link ZDPException#INV_REQUESTTYPE}</li>
	 * <li>{@link ZDPException#NO_DESCRIPTOR}</li>
	 * </ul>
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         It will be used in order to return the node power descriptor
	 *         {@link ZigBeePowerDescriptor}. If the ZDP <em>Power_Desc_rsp</em>
	 *         do not return success, the promise must fail with a
	 *         {@link ZDPException} exception with the correct status code.
	 */
	public Promise /* <ZigBeePowerDescriptor> */ getPowerDescriptor();

	/**
	 * 
	 * Retrieves the ZigBee node Complex Descriptor.
	 * 
	 * <p>
	 * As described in <em>Table 2.92 Fields of the Complex_Desc_rsp
	 * Command</em> of the ZigBee specification, a <em>Complex_Desc_rsp</em>
	 * command can return with the following status codes:
	 * <ul>
	 * <li>{@link ZDPException#SUCCESS}</li>
	 * <li>{@link ZDPException#DEVICE_NOT_FOUND}</li>
	 * <li>{@link ZDPException#INV_REQUESTTYPE}</li>
	 * <li>{@link ZDPException#NO_DESCRIPTOR}</li>
	 * </ul>
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         It will be used in order to return the complex descriptor
	 *         {@link ZigBeeComplexDescriptor}. If the ZDP
	 *         <em>Complex_Desc_rsp</em> do not return success, the promise must
	 *         fail with a {@link ZDPException} exception with the correct
	 *         status code.
	 */
	public Promise /* <ZigBeeComplexDescriptor> */ getComplexDescriptor();

	/**
	 * 
	 * Retrieves the link quality information to the neighbor nodes.
	 * 
	 * <p>
	 * An implementation of this method may use the <em>Mgmt_Lqi_req</em> and
	 * <em>Mgmt_Lqi_rsp</em> messages to retrieve the Link Quality table (also
	 * known as NeighborTableList in the ZigBee Specification).
	 * 
	 * <p>
	 * The method limit the Link Quality table to the {@link ZigBeeNode} service
	 * discovered.
	 * 
	 * <p>
	 * In case of failure the target device may report error code
	 * {@link ZDPException#NOT_SUPPORTED}.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         It will be resolved with the result of this operation. In case of
	 *         success the resolved value will be a {@link Map} containing the
	 *         Service.PID as {@link String} key of the {@link ZigBeeNode}
	 *         service and the value the {@link ZigBeeLinkQuality} for that
	 *         node. In case of errors the promise must fail with the correct
	 *         {@link ZDPException}.
	 */
	public Promise /* <ZigBeeLinkQuality> */ getLinksQuality();

	/**
	 * 
	 * Retrieves the routing table information of the node. This routing table
	 * is also known as RoutingTableList in the ZigBee Specification.
	 * 
	 * <p>
	 * An implementation of this method may use the <em>Mgmt_Rtg_req</em> ZDP
	 * command to retrieve the Routing Table .
	 * 
	 * <p>
	 * The target device may report a status code
	 * {@link ZDPException#NOT_SUPPORTED} in case of failure.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         In case of success, the resolved value will be a {@link Map}
	 *         containing the Service.PID as {@link String} key of the
	 *         {@link ZigBeeNode} service and the value the {@link ZigBeeRoute}
	 *         for that node. In case of failure a {@link ZDPException}
	 *         exception with the correct status code must be used to fail the
	 *         promise.
	 */
	public Promise /* <Map<String,ZigBeeRoute>> */ getRoutingTable();

	/**
	 * Requests this node to leave the ZigBee network.
	 * 
	 * <p>
	 * As described in <em>Table 2.131 Fields of the Mgmt_Leave_rsp Command</em>
	 * of the ZigBee specification, a Mgmt_Leave_rsp ZDP command may return the
	 * following status values:
	 * <ul>
	 * <li>{@link ZDPException#SUCCESS}</li>
	 * <li>{@link ZDPException#NOT_SUPPORTED}</li>
	 * <li>{@link ZDPException#NOT_AUTHORIZED}</li>
	 * <li>any status code returned from the NLMELEAVE.confirm primitive</li>
	 * </ul>
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         In case of success, the promise is resolved with a {@code null}
	 *         value, otherwise with the correct {@link ZDPException} exception.
	 */
	public Promise leave();

	/**
	 * Requests the device to leave the network.
	 * 
	 * <p>
	 * As described in <em>Table 2.131 Fields of the Mgmt_Leave_rsp Command</em>
	 * of the ZigBee specification, a Mgmt_Leave_rsp command could return the
	 * following status values:
	 * 
	 * <ul>
	 * <li>{@link ZDPException#SUCCESS}</li>
	 * <li>{@link ZDPException#NOT_SUPPORTED}</li>
	 * <li>{@link ZDPException#NOT_AUTHORIZED}</li>
	 * <li>any status code returned from the NLMELEAVE.confirm primitive</li>
	 * </ul>
	 * 
	 * @param rejoin {@code true} if the device being asked to leave from the
	 *        current parent is requested to rejoin the network. Otherwise,
	 *        {@code false}.
	 * @param removeChildren {@code true} if the device being asked to leave the
	 *        network is also being asked to remove its child devices, if any.
	 *        Otherwise, {@code false}.
	 * @return A promise representing the completion of this asynchronous call.
	 *         In case of success, the ZigBeeNode service must be unregistered,
	 *         first and then the promise may be resolved with a {@code null}
	 *         value, otherwise with the correct {@link ZDPException} exception.
	 */
	public Promise leave(boolean rejoin, boolean removeChildren);

	/**
	 * Sends the {@link ZDPFrame} to this {@link ZigBeeNode} with the specified
	 * cluster id. This method expects a specific cluster in the response to the
	 * request.
	 * 
	 * @param clusterIdReq the cluster Id of the {@link ZDPFrame} that will be
	 *        sent to the device.
	 * @param expectedClusterIdRsp the expected cluster Id of the response to
	 *        the {@link ZDPFrame} sent.
	 * @param message the {@link ZDPFrame} containing the message.
	 * @return A promise representing the completion of this asynchronous call.
	 *         In case of success the promise resolves with the response
	 *         {@link ZDPFrame}.
	 */
	public Promise /* <ZDPFrame> */ invoke(int clusterIdReq, int expectedClusterIdRsp, ZDPFrame message);

	/**
	 * Sends the {@link ZDPFrame} to this {@link ZigBeeNode} with the specified
	 * cluster id. This method expects a specific cluster in the response to the
	 * request.
	 * 
	 * This method considers that the 0x8000 + clusterIdReq is the clusterId
	 * expected from messaged received for the message sent by this request.
	 * 
	 * @param clusterIdReq the cluster Id of the {@link ZDPFrame} that will be
	 *        sent to the device.
	 * @param message the {@link ZDPFrame} containing the message.
	 * @return A promise representing the completion of this asynchronous call.
	 *         In case of success the promise resolves with the response
	 *         {@link ZDPFrame}.
	 */
	public Promise /* <ZDPFrame> */ invoke(int clusterIdReq, ZDPFrame message);

	/**
	 * Broadcasts a given ZCL Frame to cluster {@code clusterID} on all the
	 * {@link ZigBeeEndpoint} that are running on this node (endpoint
	 * broadcasting).
	 *
	 * @param clusterID the cluster ID the broadcast message is directed.
	 * @param frame a ZCL Frame that contains the command that have to be
	 *        broadcasted to the specific cluster of all the endpoints running
	 *        on the node.
	 * @return a response stream instance that collects and allows a client to
	 *         be asynchronously notified about the ZCLFrame responses sent back
	 *         by the ZigBee nodes.
	 */
	ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame);

	/**
	 * Broadcasts a given ZCL Frame to cluster {@code clusterID} on all the
	 * {@link ZigBeeEndpoint} that are running on this node (endpoint
	 * broadcasting). The source endpoint of the APS message sent, is set to the
	 * endpoint identifier of the {@code exportedServicePID} service.
	 * 
	 * @param clusterID the cluster ID the broadcast message is directed.
	 * @param frame a ZCL Frame that contains the command that have to be
	 *        broadcasted to the specific cluster of all the endpoints running
	 *        on the node.
	 * @param exportedServicePID the source endpoint of the command request. In
	 *        targeted situations, the source endpoint is the valid service PID
	 *        of an exported endpoint.
	 * @return a response stream instance that collects and allows the caller to
	 *         be asynchronously notified about the ZCLFrame responses sent back
	 *         by the ZigBee nodes.
	 */
	ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame, String exportedServicePID);

	/**
	 * Returns the user description of this node. As described in <em>Table 2.97
	 * Fields of the User_Desc_rsp Command</em> of the ZigBee specification, a
	 * User_Desc_rsp may return the following status:
	 * <ul>
	 * <li>{@link ZDPException#SUCCESS}</li>
	 * <li>{@link ZDPException#NOT_SUPPORTED}</li>
	 * <li>{@link ZDPException#DEVICE_NOT_FOUND}</li>
	 * <li>{@link ZDPException#INV_REQUESTTYPE}</li>
	 * <li>{@link ZDPException#NO_DESCRIPTOR}</li>
	 * </ul>
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         It will be used in order to return the node user description
	 *         (String). In case of errors the promise will fail with a
	 *         {@link ZDPException} exception containing the response status
	 *         code value.
	 */
	Promise /* <String> */ getUserDescription();

	/**
	 * Sets the user description of this node. As described in <em>Table 2.137
	 * ZDP Enumerations Description</em> of the ZigBee specification, a
	 * Set_User_Desc_rsp request may return the following status:
	 * 
	 * <ul>
	 * <li>{@link ZDPException#SUCCESS}</li>
	 * <li>{@link ZDPException#DEVICE_NOT_FOUND}</li>
	 * <li>{@link ZDPException#INV_REQUESTTYPE}</li>
	 * <li>{@link ZDPException#NO_DESCRIPTOR}</li>
	 * </ul>
	 * 
	 * @param userDescription the user description.
	 * @return A promise representing the completion of this asynchronous call.
	 *         In case of success the promise returns a {@code null} value. In
	 *         case of errors the promise must fail with a {@link ZDPException}
	 *         exception containing the response status code value.
	 */
	Promise /* <Void> */ setUserDescription(String userDescription);
}
