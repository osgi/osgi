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
 * communicate using the ZigBee protocol.<br>
 * Each physical device may contain up 240 logical devices which are represented
 * by the {@link ZigBeeEndpoint} class.<br>
 * Each logical device is identified by an <i>EndPoint</i> address, but shares
 * either the:<br>
 * - <i>64-bit 802.15.4 IEEE Address</i><br>
 * - <i>16-bit ZigBee Network Address</i><br>
 * 
 * @author $Id$
 */
public interface ZigBeeNode {

	/**
	 * Property key for the mandatory node IEEE Address representing node MAC
	 * address. MAC Address is a 12-digit(48-bit) or 16-digit(64-bit)
	 * hexadecimal numbers. There is no need to use 0x hexadecimal notation.
	 * <i>i.e zigbee.node.ieee.address="00:25:96:AB:37:56"</i> for a 48-bit
	 * address and <i>i.e zigbee.node.ieee.address="00:25:96:FF:FE:AB:37:56"</i>
	 * for a 64-bit address
	 * 
	 * A ZigBee Event Listener service can announce for what ZigBee device nodes
	 * it wants notifications.
	 */
	public static final String	IEEE_ADDRESS			= "zigbee.node.ieee.address";

	/**
	 * Property key for the device logical type
	 */
	public static final String	LOGICAL_TYPE			= "zigbee.node.description.node.type";

	/**
	 * Property key for a manufacturer code that is allocated by the ZigBee
	 * Alliance, relating the manufacturer to the device.
	 */
	public static final String	MANUFACTURER_CODE		= "zigbee.node.description.manufacturer.code";

	/**
	 * Key of {@link String} containing the device node network PAN ID
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
	 * ZigBee power source, i.e. 3rd bit of "MAC Capabilities" in Node
	 * Descriptor. Set to 1 if the current power source is mains power, set to 0
	 * otherwise.
	 */
	public static final String	POWER_SOURCE			= "zigbee.node.power.source";

	/**
	 * ZigBee receiver on when idle, i.e. 4th bit of "MAC Capabilities" in Node
	 * Descriptor. Set to 1 if the device does not disable its receiver to
	 * conserve power during idle periods, set to 0 otherwise.
	 */
	public static final String	RECEIVER_ON_WHEN_IDLE	= "zigbee.node.receiver.on.when.idle";

	/**
	 * The Node is a ZigBee End Device
	 */
	public static final short	ZED						= 0x01;

	/**
	 * The Node is a ZigBee Coordinator
	 */
	public static final short	COORDINATOR				= 0x02;

	/**
	 * The Node is a ZigBee Router
	 */
	public static final short	ROUTER					= 0x03;

	/**
	 * @return The ZigBee device node IEEE Address.
	 */
	public BigInteger getIEEEAddress();

	/**
	 * @return The ZigBee device node current network address.
	 */
	public int getNetworkAddress();

	/**
	 * @return The ZigBee Host OSGi service PID.
	 */
	public String getHostPid();

	/**
	 * @return The network Personal Area Network identifier(PAND ID)
	 */
	public int getPanId();

	/**
	 * @return The network Extended PAN identifier(EPID)
	 */
	public BigInteger getExtendedPanId();

	/**
	 * 
	 * @return An array of embedded endpoints, returns an empty array if it does
	 *         not provide any endpoint.
	 */
	public ZigBeeEndpoint[] getEndpoints();

	/**
	 * As described in "Table 2.91 Fields of the Node_Desc_rsp Command" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * node_decr request can have the following status: SUCCESS,
	 * DEVICE_NOT_FOUND ,INV_REQUESTTYPE or NO_DESCRIPTOR.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         It will be used in order to return the node descriptor
	 *         {@link ZigBeeNodeDescriptor}.
	 */
	public Promise getNodeDescriptor();

	/**
	 * As described in "Table 2.92 Fields of the Power_Desc_rsp Command" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * power_decr request can have the following status: SUCCESS,
	 * DEVICE_NOT_FOUND, INV_REQUESTTYPE or NO_DESCRIPTOR.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         It will be used in order to return the node power descriptor
	 *         {@link ZigBeePowerDescriptor}.
	 */
	public Promise getPowerDescriptor();

	/**
	 * As described in "Table 2.96 Fields of the Complex_Desc_rsp Command" of
	 * the ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * complex_desc request can have the following status: SUCCESS,
	 * DEVICE_NOT_FOUND, INV_REQUESTTYPE or NO_DESCRIPTOR.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         It will be used in order to return the node complex descriptor
	 *         {@link ZigBeeComplexDescriptor}. Can be null no complex
	 *         descriptor is not provided.
	 */
	public Promise getComplexDescriptor();

	/**
	 * The ZigBee Base Driver may use the Mgmt_Lqi_req / Mgmt_Lqi_rsp messages
	 * to retrieve the Link Quality table (i.e also known as NeighborTableList
	 * in the ZigBee Specification). <br>
	 * The method limit the Link Quality table to the {@link ZigBeeNode} service
	 * discovered.<br>
	 * The target device may report error code NOT_SUPPORTED, or
	 * UNSUPPORTED_ATTRIBUTE in case of failure that will be notified to the
	 * handler.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         It will be resolved with the result of this operation. In case of
	 *         success the resolved value will be a {@link Map} containing the
	 *         Service.PID as {@link String} key of the {@link ZigBeeNode}
	 *         service and the value the {@link ZigBeeLinkQuality} for that
	 *         node.
	 */
	public Promise getLinksQuality();

	/**
	 * The ZigBee Base Drive may use the Mgmt_Rtg_req / Mgmt_Rtg_rsp messages to
	 * retrieve the Routing Table (i.e also known as RoutingTableList in the
	 * ZigBee Specification). <br>
	 * The target device may report error code NOT_SUPPORTED, or
	 * UNSUPPORTED_ATTRIBUTE in case of failure that will be notified to the
	 * handler.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         In case of success, the resolved value will be a {@link Map}
	 *         containing the Service.PID as {@link String} key of the
	 *         {@link ZigBeeNode} service and the value the {@link ZigBeeRoute}
	 *         for that node.
	 */
	public Promise getRoutingTable();

	/**
	 * Requests to leave the network. <br>
	 * 
	 * As described in "Table 2.131 Fields of the Mgmt_Leave_rsp Command" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * mgmt_leave request can have the following status: NOT_SUPPORTED,
	 * NOT_AUTHORIZED or any status code returned from the NLMELEAVE.confirm
	 * primitive (see {@link ZDPException}). <br>
	 * 
	 * The response object given to the handler is a Boolean set to true if the
	 * leave succeeds. In case of an error has occurred, onFailure is called
	 * with a ZCLException.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 */
	public Promise leave();

	/**
	 * Requests the device to leave the network. The ZigBeeHandler onSuccess
	 * method is called if and only if the ZigBeeDeviceNode has been removed.
	 * <br>
	 * 
	 * As described in "Table 2.131 Fields of the Mgmt_Leave_rsp Command" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * mgmt_leave request can have the following status: NOT_SUPPORTED,
	 * NOT_AUTHORIZED or any status code returned from the NLMELEAVE.confirm
	 * primitive (see {@link ZDPException}). <br>
	 * 
	 * The response object given to the handler is a Boolean set to true if the
	 * leave succeeds. In case of an error has occurred, onFailure is called
	 * with a ZCLException.
	 * 
	 * @param rejoin This field has a value of 1 if the device being asked to
	 *        leave from the current parent is requested to rejoin the network.
	 *        Otherwise, it has a value of 0.
	 * @param removeChildren This field has a value of 1 if the device being
	 *        asked to leave the network is also being asked to remove its child
	 *        devices, if any. Otherwise, it has a value of 0.
	 * @return A promise representing the completion of this asynchronous call.
	 */
	public Promise leave(boolean rejoin, boolean removeChildren);

	/**
	 * This method sends the {@link ZDPFrame} to this {@link ZigBeeNode} with
	 * the specified cluster id and it will expect a specific cluster as
	 * response to the request.
	 * 
	 * @param clusterIdReq the cluster Id of the {@link ZDPFrame} that will be
	 *        sent to the device.
	 * @param expectedClusterIdRsp the expected cluster Id of the response to
	 *        the {@link ZDPFrame} sent.
	 * @param message the {@link ZDPFrame} containing the message.
	 * @param handler The handler for the response to the {@link ZDPFrame}.
	 */
	public Promise invoke(int clusterIdReq, int expectedClusterIdRsp, ZDPFrame message);

	/**
	 * This method sends the {@link ZDPFrame} to this {@link ZigBeeNode} with
	 * the specified cluster id and it will expect a specific cluster as
	 * response to the request.
	 * 
	 * This method considers that the 0x8000 + clusterIdReq is the clusterId
	 * expected from messaged received for the message sent by this request.
	 * 
	 * @param clusterIdReq the cluster Id of the {@link ZDPFrame} that will be
	 *        sent to the device.
	 * @param message the {@link ZDPFrame} containing the message.
	 * @param handler The handler for the response to the {@link ZDPFrame}.
	 */
	public Promise invoke(int clusterIdReq, ZDPFrame message);

	/**
	 * Enable to broadcast a given frame of a specific cluster to all the
	 * {@link ZigBeeEndpoint} that are running on this node.
	 * 
	 * FIXME: this description must be improved.
	 * 
	 * @param clusterID the cluster ID.
	 * @param frame a command frame sequence.
	 * @param handler The handler that manages the command response.
	 */
	ZCLCommandMultiResponse broadcast(int clusterID, ZCLFrame frame);

	/**
	 * Enable to broadcast a given frame of a specific cluster to all the
	 * {@link ZigBeeEndpoint} that are running on this node from a specific
	 * exported endpoint.
	 * 
	 * FIXME: this description must be improved.
	 * 
	 * @param clusterID the cluster ID.
	 * @param frame a command frame sequence.
	 * @param handler The handler that manages the command response.
	 * @param exportedServicePID : the source endpoint of the command request.
	 *        In targeted situations, the source endpoint is the valid service
	 *        PID of an exported endpoint.
	 */
	ZCLCommandMultiResponse broadcast(int clusterID, ZCLFrame frame, String exportedServicePID);

	/**
	 * As described in "Table 2.97 Fields of the User_Desc_rsp Command" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * user_desc request can have the following status: SUCCESS, NOT_SUPPORTED,
	 * DEVICE_NOT_FOUND, INV_REQUESTTYPE or NO_DESCRIPTOR. These constants are
	 * defined in {@link ZDPException}.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         It will be used in order to return the node user description
	 *         (String). The promise will fail with NO_DESCRIPTOR when no user
	 *         descriptor is available.
	 */
	Promise getUserDescription();

	/**
	 * As described in "Table 2.137 ZDP Enumerations Description" of the ZigBee
	 * specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a set user desc
	 * request may throw: NOT_SUPPORTED, DEVICE_NOT_FOUND, INV_REQUESTTYPE, or
	 * NO_DESCRIPTOR. These constants are defined in {@link ZDPException}.
	 * 
	 * @param userDescription the user description
	 * @return A promise representing the completion of this asynchronous call.
	 */
	Promise setUserDescription(String userDescription);

}
