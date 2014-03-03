
package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.framework.BundleContext;
import org.osgi.impl.service.zigbee.descriptions.ZigBeeAttributeDescriptionImpl;
import org.osgi.impl.service.zigbee.descriptions.ZigBeeClusterDescriptionImpl;
import org.osgi.impl.service.zigbee.descriptions.ZigBeeGlobalClusterDescriptionImpl;
import org.osgi.impl.service.zigbee.descriptions.ZigBeeParameterDescriptionImpl;
import org.osgi.impl.service.zigbee.descriptors.ZigBeeNodeDescriptorImpl;
import org.osgi.impl.service.zigbee.descriptors.ZigBeePowerDescriptorImpl;
import org.osgi.impl.service.zigbee.descriptors.ZigBeeSimpleDescriptorImpl;
import org.osgi.impl.service.zigbee.util.ZigBeeDeviceNodeListener;
import org.osgi.service.zigbee.ZigBeeCluster;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptions.ZigBeeAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeClusterDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeDataTypeDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeGlobalClusterDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeParameterDescription;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.osgi.service.zigbee.types.ZigBeeBoolean;
import org.osgi.service.zigbee.types.ZigBeeCharacterString;
import org.osgi.service.zigbee.types.ZigBeeEnumeration8;
import org.osgi.service.zigbee.types.ZigBeeUnsignedInteger8;

/**
 * Mocked impl of ZigBeeDeviceNodeListener.
 */
public class ZigBeeBaseDriver implements ZigBeeDeviceNodeListener {

	private BundleContext					bc;
	private ZigBeeHost						host;
	private ZigBeeNode						node1, node2;
	private ZigBeeNodeDescriptor			nodeDesc;
	private ZigBeePowerDescriptor			powerDesc;
	private ZigBeeEndpoint					endpoint1;
	private ZigBeeEndpoint					endpoint2;
	private ZigBeeSimpleDescriptor			simpledesc1;
	private ZigBeeSimpleDescriptor			simpledesc2;
	private ZigBeeCluster[]					serverCluster;
	private ZigBeeCluster[]					clientCluster;
	private ZigBeeGlobalClusterDescription	globalDescription;
	private ZigBeeClusterDescription		serverClusterDescription;
	private ZigBeeClusterDescription		clientClusterDescription;
	private ZigBeeAttributeImpl[]			attributesServer;
	private ZigBeeAttributeDescription[]	attributesDescription;
	// private ZigBeeCommandDescription commandDescription;
	private ZigBeeParameterDescription[]	param;
	private ZigBeeDataTypeDescription[]		attributesType;
	private int[]							commandIdsServer;

	/**
	 * This constructor creates the ZigBeeBaseDriver object based on the
	 * controller and the BundleContext object.
	 * 
	 * @param bc
	 */
	public ZigBeeBaseDriver(BundleContext bc) {
		this.bc = bc;
	}

	/**
	 * This method starts the base driver. And registers with controller for
	 * getting notifications.
	 */
	public void start() {
		// types
		attributesType = new ZigBeeDataTypeDescription[4];
		attributesType[0] = ZigBeeUnsignedInteger8.getInstance();
		attributesType[1] = ZigBeeCharacterString.getInstance();
		attributesType[2] = ZigBeeEnumeration8.getInstance();
		attributesType[3] = ZigBeeBoolean.getInstance();

		attributesDescription = new ZigBeeAttributeDescription[9];
		attributesDescription[0] = new ZigBeeAttributeDescriptionImpl(0x0000, false, new Integer(0x00), "ZCLVersion", false, false, attributesType[0]);
		attributesDescription[1] = new ZigBeeAttributeDescriptionImpl(0x0001, false, new Integer(0x00), "ApplicationVersion", false, false, attributesType[0]);
		attributesDescription[2] = new ZigBeeAttributeDescriptionImpl(0x0002, false, new Integer(0x00), "StackVersion", false, false, attributesType[0]);
		attributesDescription[3] = new ZigBeeAttributeDescriptionImpl(0x0003, false, new Integer(0x00), "HWVersion", false, false, attributesType[0]);
		attributesDescription[4] = new ZigBeeAttributeDescriptionImpl(0x0004, false, "", "ManufacturerName", false, false, attributesType[1]);
		attributesDescription[5] = new ZigBeeAttributeDescriptionImpl(0x0005, false, "", "ModelIdentifier", false, false, attributesType[1]);
		attributesDescription[6] = new ZigBeeAttributeDescriptionImpl(0x0006, false, "", "DateCode", false, false, attributesType[1]);
		attributesDescription[7] = new ZigBeeAttributeDescriptionImpl(0x0007, false, "", "PowerSource", false, false, attributesType[2]);
		attributesDescription[8] = new ZigBeeAttributeDescriptionImpl(0x0008, false, new Boolean(true), "DeviceEnabled", true, true, attributesType[3]);

		// a server endpoint example
		attributesServer = new ZigBeeAttributeImpl[9];
		attributesServer[0] = new ZigBeeAttributeImpl(attributesDescription[0]);
		attributesServer[1] = new ZigBeeAttributeImpl(attributesDescription[1]);
		attributesServer[2] = new ZigBeeAttributeImpl(attributesDescription[2]);
		attributesServer[3] = new ZigBeeAttributeImpl(attributesDescription[3]);
		attributesServer[4] = new ZigBeeAttributeImpl(attributesDescription[4]);
		attributesServer[5] = new ZigBeeAttributeImpl(attributesDescription[5]);
		attributesServer[6] = new ZigBeeAttributeImpl(attributesDescription[6]);
		attributesServer[7] = new ZigBeeAttributeImpl(attributesDescription[7]);
		attributesServer[8] = new ZigBeeAttributeImpl(attributesDescription[8]);

		globalDescription = new ZigBeeGlobalClusterDescriptionImpl(54, "Basic", "General", null, null);
		serverClusterDescription = new ZigBeeClusterDescriptionImpl(88, globalDescription);
		clientClusterDescription = new ZigBeeClusterDescriptionImpl(67, globalDescription);

		// a client endpoint example
		clientCluster = new ZigBeeClusterImpl[1];
		clientCluster[0] = new ZigBeeClusterImpl(null, null, clientClusterDescription);

		simpledesc1 = new ZigBeeSimpleDescriptorImpl(6, (byte) 1, 5);
		endpoint1 = new ZigBeeEndpointImpl((byte) 0x21, null, clientCluster, simpledesc1);

		// a server endpoint example
		param = new ZigBeeParameterDescriptionImpl[1];
		param[0] = new ZigBeeParameterDescriptionImpl(attributesType[0]);
		serverCluster = new ZigBeeClusterImpl[1];
		commandIdsServer = new int[1];
		commandIdsServer[0] = 0;
		// commandDescription = new ZigBeeCommandDescriptionImpl(0x00,
		// "Reset to Factory Defaults", false, param);
		serverCluster[0] = new ZigBeeClusterImpl(commandIdsServer, attributesServer, serverClusterDescription);

		simpledesc2 = new ZigBeeSimpleDescriptorImpl(8, (byte) 4, 3);
		endpoint2 = new ZigBeeEndpointImpl((byte) 0x19, serverCluster, null, simpledesc2);

		// ZigBeeHost that owns node1, and node2.
		int panId = 33;
		int channel = 16;
		int baud = 115000;
		int securityLevel = 0;
		// 64-bit 802.15.4 IEEE Address, e.g. 00:25:96:FF:FE:AB:37:56
		Long IEEEAddress = Long.valueOf("0123456789");
		// 16-bit ZigBee Network Address
		short nwkAddress = Short.valueOf("2468");
		ZigBeeEndpoint[] endpointsHost = new ZigBeeEndpoint[2];
		endpointsHost[0] = endpoint1;
		endpointsHost[1] = endpoint2;

		host = new ZigBeeHostImpl(panId, channel, baud, securityLevel, IEEEAddress, nwkAddress, endpointsHost);

		// register host
		System.out.println(this.getClass().getName() + " - Register (hardcoded) host: " + host + " in the OSGi services registry.");
		bc.registerService(ZigBeeHost.class.getName(),
				host, null);

		// node descriptor
		nodeDesc = new ZigBeeNodeDescriptorImpl(ZigBeeNode.END_DEVICE, (short) 868, 45, 36, false, false);
		powerDesc = new ZigBeePowerDescriptorImpl((short) 2, (short) 1, (short) 1, true);

		// nodes
		ZigBeeEndpoint[] endpointsNode1 = new ZigBeeEndpoint[1];
		endpointsNode1[0] = endpoint1;
		node1 = new ZigBeeNodeImpl(Long.valueOf("812345689"), (short) 12345, endpointsNode1);

		ZigBeeEndpoint[] endpointsNode2 = new ZigBeeEndpoint[1];
		endpointsNode2[0] = endpoint2;
		node2 = new ZigBeeNodeImpl(Long.valueOf("6628417766"), (short) 88507, endpointsNode2, nodeDesc, powerDesc);

		// register node1
		System.out.println(this.getClass().getName() + " - Register (hardcoded) node1: " + node1 + " in the OSGi services registry.");
		bc.registerService(ZigBeeNode.class.getName(),
				node1, null);

		// register node2
		System.out.println(this.getClass().getName() + " - Register (hardcoded) node2: " + node2 + " in the OSGi services registry.");
		bc.registerService(ZigBeeNode.class.getName(),
				node2, null);

		// // Launch a mocked events generator.
		// eventsManager(bc);
	}

	/**
	 * This method stops the base driver. And unregisters with controller for
	 * getting notifications.
	 */
	public void stop() {
		// TODO
	}

	synchronized public void addDevice(String uuid) {
		// TODO
	}

	synchronized public void removeDevice(String uuid) {
		// TODO
	}

}
