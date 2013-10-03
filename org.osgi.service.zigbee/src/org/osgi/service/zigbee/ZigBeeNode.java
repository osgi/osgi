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
 * Each physical may contain up 240 logical devices which are represented by the
 * {@link ZigBeeEndpoint}<br>
 * class. Each logical device is identified by an <i>EndPoint</i> address, but
 * shares either the:<br>
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
	public static final String	IEEE_ADDRESS					= "zigbee.node.ieee.address";

	/**
	 * Property key for the device node type
	 */
	public static final String	NODE_TYPE						= "zigbee.node.description.node.type";

	/**
	 * Property key for a manufacturer code that is allocated by the ZigBee
	 * Alliance, relating the manufacturer to the device.
	 */
	public static final String	MANUFACTURER_CODE				= "zigbee.node.description.manufacturer.code";

	/**
	 * Property key of the {@link String} containing the name of the
	 * manufacturer of the device.
	 */
	public static final String	MANUFACTURER_NAME				= "zigbee.node.description.manufacturer.name";

	/**
	 * Property key of the {@link String} containing the name of the
	 * manufacturers model of the device. <br>
	 * It is <b>optional</b> property for this service
	 */
	public static final String	MODEL_NAME						= "zigbee.node.description.model.name";

	/**
	 * Property key of the {@link String} containing the manufacturers serial
	 * number of the device. <br>
	 * It is <b>optional</b> property for this service
	 */
	public static final String	SERIAL_NAME						= "zigbee.node.description.serial.number";

	/**
	 * Property key of the {@link String} containing the URL through which more
	 * information relating to the device can be obtained. <br>
	 * It is <b>optional</b> property for this service
	 */
	public static final String	NODE_URL						= "zigbee.node.description.url";

	/**
	 * Property key of the {@link String} containing the URL through which the
	 * icon for the device can be obtained. <br>
	 * It is <b>optional</b> property for this service
	 */
	public static final String	ICON_URL						= "zigbee.node.description.icon.url";

	/**
	 * Property key of the {@link String} containing information that allow the
	 * user to identify the device using a user-friendly character string <br>
	 * It is <b>optional</b> property for this service
	 */
	public static final String	USER_DESCRIPTION				= "zigbee.node.description.user.desc";

	/**
	 * Property key for the node capability as required by the IEEE802.15.4 MAC
	 * sub-layer.
	 */
	public static final String	CAPABILITIES_MAC				= "zigbee.node.description.capabilities.mac";

	/**
	 * Property key for the maximum size, in octets, of the network sub-layer
	 * data unit for this node. <br>
	 * It is <b>optional</b> property for this service
	 */
	public static final String	MAXIMUM_BUFFER_SIZE				= "zigbee.node.description.maximum.buffer.size";

	/**
	 * Property key for the maximum size, in octets, of the application
	 * sub-layer data unit that can be transferred to this node in one single
	 * message transfer. <br>
	 * It is <b>optional</b> property for this service
	 */
	public static final String	MAXIMUM_INCOMING_TRANSFERT_SIZE	= "zigbee.node.description.maximum.incoming.transfet.size";

	/**
	 * Property key for the current sleep/power-saving mode of the node.
	 */
	public static final String	CURRENT_POWER_MODE				= "zigbee.node.description.current.power.mode";

	/**
	 * Property key for the current power source being utilized by the node.
	 */
	public static final String	CURRENT_POWER_SOURCE			= "zigbee.node.description.current.power.source";

	/**
	 * Property key for the power sources available on the node.
	 */
	public static final String	AVAILABLE_POWER_SOURCE			= "zigbee.node.description.available.power.source";

	/**
	 * Property key for the level of charge of the power source.
	 */
	public static final String	CURRENT_POWER_SOURCE_LEVEL		= "zigbee.node.description.power.source.level";

	/**
	 * Key of {@link String} containing the {@link ZigBeeHost} pid.<br>
	 * The host pid is intended to uniquely identify the ZigBee local host,
	 * since there could be many hosts on the same platform. All the nodes that
	 * belong to a specific network MUST specify the same value for this
	 * property.
	 */
	public static final String	HOST_PID						= "zigbee.node.host.pid";

	/**
	 * Key of {@link String} containing the device node network PAN ID
	 */
	public static final String	PAN_ID							= "zigbee.node.pan.id";

	/**
	 * Key of {@link String} containing the device node network extended PAN ID
	 */
	public static final String	EXTENDED_PAN_ID					= "zigbee.node.extended.pan.id";

	/**
	 * ZigBee coordinator
	 */
	public static final short	COORDINATOR						= 0;

	/**
	 * ZigBee router
	 */
	public static final short	ROUTER							= 1;

	/**
	 * ZigBee end device
	 */
	public static final short	END_DEVICE						= 2;

	/**
	 * @return The ZigBee device node IEEE Address.
	 */
	public Long getIEEEAddress();

	/**
	 * @return The ZigBee device node current network address.
	 */
	public short getNetworkAddress();

	/**
	 * @return The ZigBee local Host PID
	 */
	public String getHostPId();

	/**
	 * @return The network Personal Area Network identifier(PAND ID)
	 */
	public int getPanId();

	/**
	 * @return The network Extended PAN identifier(EPID)
	 */
	public Long getExtendedPanId();

	/**
	 * @return an array of all the endpoints associated with the ZigBee device
	 *         node.
	 */
	public ZigBeeEndpoint[] getEndpoints();

	/**
	 * @param id The endpoint identifier to be retrieved.
	 * @return The endpoint associated with id.
	 */
	public ZigBeeEndpoint getEndpoint(short id);

	/**
	 * @return the node descriptor.
	 * @throws ZigBeeException
	 */
	public ZigBeeNodeDescriptor getNodeDescriptor() throws ZigBeeException;

	/**
	 * @return the node power descriptor.
	 * @throws ZigBeeException
	 */
	public ZigBeePowerDescriptor getPowerDescriptor() throws ZigBeeException;

	/**
	 * @return the node complex descriptor. Can be null if complex descriptor is
	 *         not provided.
	 * @throws ZigBeeException
	 */
	public ZigBeeComplexDescriptor getComplexDescriptor() throws ZigBeeException;

	/**
	 * @return the node user descriptor. Can be null if user descriptor is not
	 *         provided.
	 * @throws ZigBeeException
	 */
	public ZigBeeUserDescriptor getUserDescriptor() throws ZigBeeException;

	/**
	 * Request to leave the network.
	 * 
	 * @param handler
	 * @throws ZigBeeException
	 */
	public void leave(ZigBeeHandler handler) throws ZigBeeException;

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
	 * @throws ZigBeeException
	 */
	public void leave(boolean rejoin, boolean removeChildren, ZigBeeHandler handler) throws ZigBeeException;

}
