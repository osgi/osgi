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
	 * Key of {@link String} containing the {@link ZigBeeHost} pid.<br>
	 * The host pid is intended to uniquely identify the ZigBee local host,
	 * since there could be many hosts on the same platform. All the nodes that
	 * belong to a specific network MUST specify the same value for this
	 * property.
	 */
	public static final String	HOST_PID				= "zigbee.node.host.pid";

	/**
	 * Key of {@link String} containing the device node network PAN ID
	 */
	public static final String	PAN_ID					= "zigbee.node.pan.id";

	/**
	 * Key of {@link String} containing the device node network extended PAN ID.
	 * If the device type is “Coordinator”, the extended pan id may be available
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
	 * @param handler that will be used in order to return the node descriptor
	 *        {@link ZigBeeNodeDescriptor}.
	 * @throws ZCLException
	 */
	public void getNodeDescriptor(ZigBeeHandler handler) throws ZCLException;

	/**
	 * @param handler that will be used in order to return the node power
	 *        descriptor {@link ZigBeePowerDescriptor}.
	 * @throws ZCLException
	 */
	public void getPowerDescriptor(ZigBeeHandler handler) throws ZCLException;

	/**
	 * @param handler that will be used in order to return the node complex
	 *        descriptor {@link ZigBeeComplexDescriptor}. Can be null if complex
	 *        descriptor is not provided.
	 * @throws ZCLException
	 */
	public void getComplexDescriptor(ZigBeeHandler handler) throws ZCLException;

	/**
	 * @param handler that will be used in order to return the node user
	 *        descriptor {@link ZigBeeUserDescriptor}. Can be null if user
	 *        descriptor is not provided.
	 * @throws ZCLException
	 */
	public void getUserDescriptor(ZigBeeHandler handler) throws ZCLException;

	/**
	 * Request to leave the network.
	 * 
	 * @param handler
	 */
	public void leave(ZigBeeHandler handler);

	/**
	 * Requests the device to leave the network. The ZigBeeHandler onSuccess
	 * method is called if and only if the ZigBeeDeviceNode has been removed.
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
