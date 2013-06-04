package org.osgi.service.zigbee;

import java.math.BigInteger;

import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeUserDescriptor;
import org.osgi.service.zigbee.handler.ZigBeeHandler;

/**
 * This interface represents a ZigBee Device node, means a physical device that can communicate
 * using the ZigBee protocol.<br>Each physical may contain up 240 logical devices which are represented by the {@link ZigBeeEndpoint}<br>
 * class. Each logical device is identified by an <i>EndPoint</i> address, but shares iether the:<br>
 *  - <i>64-bit 802.15.4 IEEE Address</i><br>
 *  - <i>16-bit ZigBee Network Address</i><br>
 *  
 * @version 1.0
 */
public interface ZigBeeDeviceNode {
	
	/**
	 * Property key for the optional device node id.
	 * A ZigBee Event Listener service can announce for what ZigBee device nodes 
	 * it wants notifications.
	 */
	public final static String ID = "zigbee.listener.node.id";
	
	/**
	 * Property key for the device node type
	 */
	public final static String NODE_TYPE = "zigbee.node.description.node.type";
	
	/**
	 * Property key for a manufacturer code that is allocated by the ZigBee Alliance, 
	 * relating the manufacturer to the device.
	 */
	public final static String MANUFACTURER_CODE = "zigbee.node.description.manufacturer.code";
	
	/**
	 * Property key of the {@link String} containing the name of the manufacturer of the device.
	 */
	public final static String MANUFACTURER_NAME = "zigbee.node.description.manufacturer.name";
	
	/**
	 * Property key of the {@link String} containing the name of the manufacturers model of the device.
	 * <br>It is <b>optional</b> property for this service
	 */
	public final static String MODEL_NAME = "zigbee.node.description.model.name";
	
	/**
	 * Property key of the {@link String} containing the manufacturers serial number of the device.
	 * <br>It is <b>optional</b> property for this service
	 */
	public final static String SERIAL_NAME = "zigbee.node.description.serial.number";
	
	/**
	 * Property key of the {@link String} containing the URL through which more information relating to 
	 * the device can be obtained.
	 * <br>It is <b>optional</b> property for this service
	 */
	public final static String NODE_URL = "zigbee.node.description.url";
	
	/**
	 * Property key of the {@link String} containing the URL through which the icon for the device can 
	 * be obtained.
	 * <br>It is <b>optional</b> property for this service
	 */
	public final static String ICON_URL = "zigbee.node.description.icon.url";
	
	/**
	 * Property key of the {@link String} containing information that allow the user to identify the device 
	 * using a user-friendly character string
	 * <br>It is <b>optional</b> property for this service
	 */
	public final static String USER_DESCRIPTION = "zigbee.node.description.user.desc";
	
	/**
	 * Property key for the node capability as required by the IEEE802.15.4 MAC sub-layer.
	 */
	public final static String CAPABILITIES_MAC = "zigbee.node.description.capabilities.mac";
	
	/**
	 * Property key for the maximum size, in octets, of the network sub-layer 
	 * data unit for this node.
	 * <br>It is <b>optional</b> property for this service
	 */
	public final static String MAXIMUM_BUFFER_SIZE = "zigbee.node.description.maximum.buffer.size";
	
	/**
	 * Property key for the maximum size, in octets, of the application sub-layer data unit 
	 * that can be transferred to this node in one single message transfer.
	 * <br>It is <b>optional</b> property for this service
	 */
	public final static String MAXIMUM_INCOMING_TRANSFERT_SIZE = "zigbee.node.description.maximum.incoming.transfet.size";
	
	/**
	 * Property key for the current sleep/power-saving mode of the node.
	 */
	public final static String CURRENT_POWER_MODE = "zigbee.node.description.current.power.mode";
	
	/**
	 * Property key for the current power source being utilized by the node.
	 */
	public final static String CURRENT_POWER_SOURCE = "zigbee.node.description.current.power.source";
	
	/**
	 * Property key for the power sources available on the node.
	 */
	public final static String AVAILABLE_POWER_SOURCE = "zigbee.node.description.available.power.source";
	
	/**
	 * Property key for the level of charge of the power source.
	 */
	public final static String CURRENT_POWER_SOURCE_LEVEL = "zigbee.node.description.power.source.level";
	
	/**
	 * Key of {@link String} containing the device node network PAN ID
	 */
	public final String PAN_ID = "zigbee.node.pan.id";
	
	/**
	 *  Key of {@link String} containing the device node network extended PAN ID
	 */
	public final String EXTENDED_PAN_ID = "zigbee.node.extended.pan.id";
	
	/**
	 * ZigBee coordinator
	 */
	public static final short COORDINATOR = 0;
	
	/**
	 * ZigBee router
	 */
	public static final short ROUTER = 1;
	
	/**
	 * ZigBee end device
	 */
	public static final short END_DEVICE = 2;
	
	/**
	 * @return A String representing the ZigBee device node IEEE Address.
	 */
	public String getIEEEAddress();
	
	/**
	 * @return The ZigBee device node current network address.
	 */
	public short getNetworkAddress();
	
	/**
	 * @return The network Personal Area Network identifier(PAND ID)
	 */
	public int getPanId();
	
	/**
	 * @return The network Extended PAN identifier(EPID)
	 */
	public BigInteger getExtendedPanId();
	
	/**
	 * @param id endpoint identifier to be retrieved.
	 * @return The endpoint associated with id.
	 */
	public ZigBeeEndpoint getEndpoint(short id);
	
	/**
	 * @return an array of all the endpoints associated with the ZigBee device node.
	 */
	public ZigBeeEndpoint[] getEndpoints();
	
	/**
	 * Requests the device to leave the network.
	 * 
	 * @param rejoin This field has a value of 1 if the device being asked to leave from the current parent is requested to rejoin the network. Otherwise, it has a value of 0.
	 * @param removeChildren This field has a value of 1 if the device being asked to leave the network is also being asked to remove its child devices, if any. Otherwise, it has a value of 0.
	 * @param handler The handler
	 * @return true if and only if the ZigBeeDeviceNode has been removed
	 */
	public void leave(boolean rejoin, boolean removeChildren, ZigBeeHandler handler) throws ZigBeeException;
	
	/**
	 * Request to leave the network.
	 */
	public void leave(ZigBeeHandler handler) throws ZigBeeException;
	
	/**
	 * @return the node descriptor.
	 */
	public ZigBeeNodeDescriptor getNodeDescriptor() throws ZigBeeException;
	
	/**
	 * @return the node power descriptor.
	 */
	public ZigBeePowerDescriptor getPowerDescriptor() throws ZigBeeException;
	
	/**
	 * @return the node complex descriptor. Can be null if complex descriptor is not provided.
	 */
	public ZigBeeComplexDescriptor getComplexDescriptor() throws ZigBeeException;
	
	/**
	 * @return the node user descriptor. Can be null if user descriptor is not provided.
	 */
	public ZigBeeUserDescriptor getUserDescriptor() throws ZigBeeException;
	
}
