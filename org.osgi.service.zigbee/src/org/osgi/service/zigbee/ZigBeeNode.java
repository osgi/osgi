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

import java.util.Map;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeUserDescriptor;

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
 * @version 1.0
 * 
 * @author see RFC 192 authors: Andre Bottaro, Arnaud Rinquin, Jean-Pierre
 *         Poutcheu, Fabrice Blache, Christophe Demottie, Antonin Chazalet,
 *         Evgeni Grigorov, Nicola Portinaro, Stefano Lenzi.
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
	 * ZigBee coordinator type
	 */
	public static final short	COORDINATOR_TYPE		= 0;

	/**
	 * ZigBee router type
	 */
	public static final short	ROUTER_TYPE				= 1;

	/**
	 * ZigBee end device type
	 */
	public static final short	END_DEVICE_TYPE			= 2;

	/**
	 * @return The ZigBee device node IEEE Address.
	 */
	public Long getIEEEAddress();

	/**
	 * @return The ZigBee device node current network address.
	 */
	public int getNetworkAddress();

	/**
	 * @return The ZigBee Host OSGi service PID.
	 */
	public String getHostPId();

	/**
	 * @return The network Personal Area Network identifier(PAND ID)
	 */
	public int getPanId();

	/**
	 * @return The network Extended PAN identifier(EPID)
	 */
	public long getExtendedPanId();

	/**
	 * As described in "Table 2.91 Fields of the Node_Desc_rsp Command" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * node_decr request can have the following status: SUCCESS,
	 * DEVICE_NOT_FOUND ,INV_REQUESTTYPE or NO_DESCRIPTOR.
	 * 
	 * @param handler that will be used in order to return the node descriptor
	 *        {@link ZigBeeNodeDescriptor}.
	 */
	public void getNodeDescriptor(ZigBeeHandler handler);

	/**
	 * As described in "Table 2.92 Fields of the Power_Desc_rsp Command" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * power_decr request can have the following status: SUCCESS,
	 * DEVICE_NOT_FOUND, INV_REQUESTTYPE or NO_DESCRIPTOR.
	 * 
	 * @param handler that will be used in order to return the node power
	 *        descriptor {@link ZigBeePowerDescriptor}.
	 */
	public void getPowerDescriptor(ZigBeeHandler handler);

	/**
	 * As described in "Table 2.96 Fields of the Complex_Desc_rsp Command" of
	 * the ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * complex_desc request can have the following status: SUCCESS,
	 * DEVICE_NOT_FOUND, INV_REQUESTTYPE or NO_DESCRIPTOR.
	 * 
	 * @param handler that will be used in order to return the node complex
	 *        descriptor {@link ZigBeeComplexDescriptor}. Can be null if complex
	 *        descriptor is not provided.
	 */
	public void getComplexDescriptor(ZigBeeHandler handler);

	/**
	 * As described in "Table 2.97 Fields of the User_Desc_rsp Command" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * user_desc request can have the following status: SUCCESS, NOT_SUPPORTED,
	 * DEVICE_NOT_FOUND, INV_REQUESTTYPE or NO_DESCRIPTOR.
	 * 
	 * @param handler that will be used in order to return the node user
	 *        descriptor {@link ZigBeeUserDescriptor}. Can be null if user
	 *        descriptor is not provided.
	 */
	public void getUserDescriptor(ZigBeeHandler handler);

	/**
	 * The ZigBee Base Drive may use the Mgmt_Lqi_req / Mgmt_Lqi_rsp messages to
	 * retrieve the Link Quality table (i.e also known as NeighborTableList in
	 * the ZigBee Specification). <br>
	 * The method limit the Link Quality table to the {@link ZigBeeNode} service
	 * discovered.<br>
	 * The device may not support in that case an empty {@link Map} will be
	 * returned
	 * 
	 * @return a {@link Map} containing the Service.PID as {@link String} key
	 *         and the {@link ZigBeeLinkQuality} for the node as value.
	 * @throws ZDPException
	 */
	public Map getLinksQuality() throws ZDPException;

	/**
	 * The ZigBee Base Drive may use the Mgmt_Rtg_req / Mgmt_Rtg_rsp messages to
	 * retrieve the Routing Table (i.e also known as RoutingTableList in the
	 * ZigBee Specification). <br>
	 * The device may not support in that case an empty {@link Map} will be
	 * returned
	 * 
	 * @return a {@link Map} containing the Service.PID of the destination of
	 *         the route as {@link String} key and the detail of the
	 *         {@link ZigBeeRoute} as value.
	 * @throws ZDPException
	 */
	public Map getRoutingTable() throws ZDPException;

	/**
	 * Request to leave the network.
	 * 
	 * As described in "Table 2.131 Fields of the Mgmt_Leave_rsp Command" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * mgmt_leave request can have the following status: NOT_SUPPORTED,
	 * NOT_AUTHORIZED or any status code returned from the NLMELEAVE.confirm
	 * primitive.
	 * 
	 * @param handler
	 */
	public void leave(ZigBeeHandler handler);

	/**
	 * Requests the device to leave the network. The ZigBeeHandler onSuccess
	 * method is called if and only if the ZigBeeDeviceNode has been removed.
	 * 
	 * As described in "Table 2.131 Fields of the Mgmt_Leave_rsp Command" of the
	 * ZigBee specification 1_053474r17ZB_TSC-ZigBee-Specification.pdf, a
	 * mgmt_leave request can have the following status: NOT_SUPPORTED,
	 * NOT_AUTHORIZED or any status code returned from the NLMELEAVE.confirm
	 * primitive.
	 * 
	 * @param rejoin This field has a value of 1 if the device being asked to
	 *        leave from the current parent is requested to rejoin the network.
	 *        Otherwise, it has a value of 0.
	 * @param removeChildren This field has a value of 1 if the device being
	 *        asked to leave the network is also being asked to remove its child
	 *        devices, if any. Otherwise, it has a value of 0.
	 * @param handler The handler
	 */
	public void leave(boolean rejoin, boolean removeChildren, ZigBeeHandler handler);

}
