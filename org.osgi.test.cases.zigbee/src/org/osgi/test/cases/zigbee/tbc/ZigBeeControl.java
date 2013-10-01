
package org.osgi.test.cases.zigbee.tbc;

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.service.zigbee.ZigBeeAttribute;
import org.osgi.service.zigbee.ZigBeeAttributeRecord;
import org.osgi.service.zigbee.ZigBeeCluster;
import org.osgi.service.zigbee.ZigBeeCommand;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptions.ZigBeeDataTypeDescription;
import org.osgi.test.cases.zigbee.tbc.device.discovery.ServicesListener;
import org.osgi.test.cases.zigbee.tbc.util.ZigBeeCommandHandlerImpl;
import org.osgi.test.cases.zigbee.tbc.util.ZigBeeEventImpl;
import org.osgi.test.cases.zigbee.tbc.util.ZigBeeEventListenerImpl;
import org.osgi.test.cases.zigbee.tbc.util.ZigBeeEventSourceImpl;
import org.osgi.test.cases.zigbee.tbc.util.ZigBeeHandlerImpl;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Contain the ZigBee testcases.
 */
public class ZigBeeControl extends DefaultTestBundleControl {

	private final int			desiredCount	= 2;
	private ServicesListener	listener;

	protected void setUp() throws Exception {
		log("Prepare for ZigBee Test Case");
		prepareTestStart();
		log("Prepared for ZigBee Test Case");
	}

	protected void tearDown() throws Exception {
		log("Tear down ZigBee Test Case");
		finalizeTestEnd();
		log("Torn down ZigBee Test Case");
	}

	private void prepareTestStart() throws Exception {
		log("Register Service Listener to listen for service changes");
		listener = new ServicesListener(getContext(), desiredCount);
		listener.open();
		listener.waitFor(OSGiTestCaseProperties.getTimeout()
				* OSGiTestCaseProperties.getScaling());
		if (listener.size() < desiredCount) {
			listener.close();
			fail("timed out waiting for " + desiredCount + " ZigBee devices");
		}
	}

	private void finalizeTestEnd() throws Exception {
		listener.close();
	}

	// ====================================================================
	// ===========================TEST=====================================
	// ===========================METHODS==================================
	// ====================================================================

	// ====================================================================
	// ===========================DISCOVERY TEST===========================
	// ===========================METHODS==================================
	// ====================================================================

	/**
	 * Tests related to Node Discovery.
	 */
	public void testNodeDiscovery() {
		ZigBeeNode dev = listener.getZigBeeNode();
		assertNotNull("ZigBeeNode is NULL", dev);

		log("ZigBeeNode IEEE_ADDRESS: " + dev.getIEEEAddress().toString());
		assertEquals("IEEE ADDRESS not matched",
				ZigBeeConstants.NODE_IEEE_ADDRESS,
				dev.getIEEEAddress().toString());

		try {
			log("ZigBeeNode NODE_TYPE: " + dev.getNodeDescriptor().getLogicalType());
			assertEquals("Node type not matched",
					ZigBeeConstants.NODE_TYPE,
					String.valueOf(dev.getNodeDescriptor().getLogicalType()));

			log("ZigBeeNode MANUFACTURER_CODE: " + dev.getNodeDescriptor().getManufacturerCode());
			assertEquals("Manufacturer code not matched",
					ZigBeeConstants.NODE_MANUFACTURER_CODE,
					String.valueOf(dev.getNodeDescriptor().getManufacturerCode()));

			log("ZigBeeNode MAXIMUM_BUFFER_SIZE: " + String.valueOf(dev.getNodeDescriptor().getMaxBufferSize()));
			assertEquals("Maximum buffer size not matched",
					ZigBeeConstants.NODE_MAXIMUM_BUFFER_SIZE,
					String.valueOf(dev.getNodeDescriptor().getMaxBufferSize()));

			log("ZigBeeNode MAXIMUM_INCOMING_TRANSFERT_SIZE: " + dev.getNodeDescriptor().getMaxIncomingTransferSize());
			assertEquals("Maximum incoming transfert size not matched",
					ZigBeeConstants.NODE_MAXIMUM_INCOMING_TRANSFERT_SIZE,
					String.valueOf(dev.getNodeDescriptor().getMaxIncomingTransferSize()));

			log("ZigBeeNode CURRENT_POWER_MODE: " + dev.getPowerDescriptor().getCurrentPowerMode());
			assertEquals("Current power mode not matched",
					ZigBeeConstants.NODE_CURRENT_POWER_MODE,
					String.valueOf(dev.getPowerDescriptor().getCurrentPowerMode()));

			log("ZigBeeNode CURRENT_POWER_SOURCE: " + dev.getPowerDescriptor().getCurrentPowerSource());
			assertEquals("Current power source not matched",
					ZigBeeConstants.NODE_CURRENT_POWER_SOURCE,
					String.valueOf(dev.getPowerDescriptor().getCurrentPowerSource()));

			log("ZigBeeNode AVAILABLE_POWER_SOURCE: " + dev.getPowerDescriptor().isConstantMainsPowerAvailable());
			assertEquals("Availability of power source not matched",
					ZigBeeConstants.NODE_AVAILABLE_POWER_SOURCE,
					String.valueOf(dev.getPowerDescriptor().isConstantMainsPowerAvailable()));

			log("ZigBeeNode CURRENT_POWER_SOURCE_LEVEL: " + dev.getPowerDescriptor().getCurrentPowerSourceLevel());
			assertEquals("Current power source not matched",
					ZigBeeConstants.NODE_CURRENT_POWER_SOURCE_LEVEL,
					String.valueOf(dev.getPowerDescriptor().getCurrentPowerSourceLevel()));

			if (dev.getNodeDescriptor().isComplexDescriptorAvailable()) {
				log("ZigBeeNode MODEL_NAME: " + dev.getComplexDescriptor().getModelName());
				assertEquals("Model name not matched",
						ZigBeeConstants.NODE_MODEL_NAME,
						dev.getComplexDescriptor().getModelName());

				log("ZigBeeNode SERIAL_NAME: " + dev.getComplexDescriptor().getSerialNumber());
				assertEquals("Serial name not matched",
						ZigBeeConstants.NODE_SERIAL_NAME,
						dev.getComplexDescriptor().getSerialNumber());

				log("ZigBeeNode NODE_URL: " + dev.getComplexDescriptor().getDeviceURL());
				assertEquals("Node url not matched",
						ZigBeeConstants.NODE_NODE_URL,
						dev.getComplexDescriptor().getDeviceURL());
			}

			if (dev.getNodeDescriptor().isUserDescriptorAvailable()) {
				log("ZigBeeNode USER_DESCRIPTION: " + dev.getUserDescriptor().getUserDescription());
				assertEquals("User description not matched",
						ZigBeeConstants.NODE_USER_DESCRIPTION,
						dev.getUserDescriptor().getUserDescription());
			}
		} catch (ZigBeeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests related to Endpoint Discovery.
	 */
	public void testEndpointDiscovery() {
		ZigBeeNode dev = listener.getZigBeeNode();
		assertNotNull("ZigBeeNode is NULL", dev);

		ZigBeeEndpoint[] endpoints = dev.getEndpoints();
		ZigBeeEndpoint endpoint = endpoints[0];
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);

		log("ZigBeeEndpoint ENDPOINT: " + endpoint.getId());
		assertEquals("Endpoint identifier not matched",
				ZigBeeConstants.ENDPOINT,
				String.valueOf(endpoint.getId()));

		try {
			log("ZigBeeEndpoint PROFILE_ID: " +
					endpoint.getSimpleDescriptor().getApplicationProfileId());
			assertEquals("Application Profile identifier not matched",
					ZigBeeConstants.ENDPOINT_APP_PROFILE_ID,
					String.valueOf(endpoint.getSimpleDescriptor
							().getApplicationProfileId()));

			log("ZigBeeEndpoint DEVICE_ID: " +
					endpoint.getSimpleDescriptor().getApplicationDeviceId());
			assertEquals("Application Device identifier not matched",
					ZigBeeConstants.ENDPOINT_APP_DEVICE_ID,
					String.valueOf(endpoint.getSimpleDescriptor
							().getApplicationDeviceId()));

			log("ZigBeeEndpoint DEVICE_VERSION: " +
					endpoint.getSimpleDescriptor().getApplicationDeviceVersion());
			assertEquals("Application device version not matched",
					ZigBeeConstants.ENDPOINT_APP_DEVICE_VERSION, String.valueOf(endpoint
							.getSimpleDescriptor().getApplicationDeviceVersion()));

			String listInput = "[]";
			ZigBeeCluster[] servers = endpoint.getServerClusters();
			if (servers != null) {
				listInput = "[";
				for (int i = 0; i < servers.length; i++) {
					listInput += endpoint.getServerCluster(i);
					if (i < endpoint.getServerClusters().length - 1)
						listInput += ",";
				}
				listInput += "]";
			}
			else {
				log("ENDPOINT server empty NULL");
			}
			log("ZigBeeEndpoint INPUT_CLUSTERS: " + listInput);
			assertEquals("Input clusters list not matched",
					ZigBeeConstants.ENDPOINT_INPUT_CLUSTERS, listInput);

			String listOuput = "[]";
			ZigBeeCluster[] clients = endpoint.getClientClusters();
			if (clients != null) {
				listOuput = "[";
				for (int i = 0; i < clients.length; i++) {
					listOuput += endpoint.getClientCluster(i);
					if (i < endpoint.getClientClusters().length - 1)
						listOuput += ",";
				}
				listOuput += "]";
			}
			log("ZigBeeEndpoint OUTPUT_CLUSTERS: " + listOuput);
			assertEquals("Output clusters list not matched",
					ZigBeeConstants.ENDPOINT_OUTPUT_CLUSTERS, listOuput);

		} catch (ZigBeeException e) {
			e.printStackTrace();
		}
	}

	// ====================================================================
	// ===========================DESCRIPTION TEST=========================
	// ===========================METHODS==================================
	// ====================================================================

	/**
	 * Tests related to Cluster Description.
	 */
	public void testClusterDescription() {
		ZigBeeNode dev = listener.getZigBeeNode();
		assertNotNull("ZigBeeNode is NULL", dev);

		ZigBeeEndpoint[] endpoints = dev.getEndpoints();
		ZigBeeEndpoint endpoint = endpoints[0];
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);

		ZigBeeCluster[] clusters = endpoint.getClientClusters();
		if (clusters == null || clusters.length == 0) {
			clusters = endpoint.getServerClusters();
		}
		ZigBeeCluster cluster = null;
		if (clusters != null && clusters.length != 0) {
			cluster = clusters[0];
		}
		assertNotNull("ZigBeeCluster is NULL", cluster);

		log("ZigBeeCluster ID: " + cluster.getId());
		assertEquals("Clusters identifier not matched",
				ZigBeeConstants.CLUSTER_ID, String.valueOf(cluster.getId()));

		// Use ZigBeeClusterImpl instead of ZigBeeCluster cluster, in order to
		// do the tests?
		//
		// log("ZigBeeCluster NAME: " +
		// cluster.getDescription().getGlobalClusterDescription().getClusterName());
		// assertEquals("Cluster name not matched",
		// ZigBeeConstants.CLUSTER_NAME,
		// cluster.getDescription().getGlobalClusterDescription().getClusterName());
		//
		// log("ZigBeeCluster DOMAIN: " +
		// cluster.getDescription().getGlobalClusterDescription().getClusterFunctionalDomain());
		// assertEquals("Cluster functionnal domain not matched",
		// ZigBeeConstants.CLUSTER_DOMAIN,
		// cluster.getDescription().getGlobalClusterDescription().getClusterFunctionalDomain());
	}

	/**
	 * Tests related to Command Description.
	 */
	public void testCommandDescription() {
		ZigBeeNode dev = listener.getZigBeeNode();
		assertNotNull("ZigBeeNode is NULL", dev);

		ZigBeeEndpoint[] endpoints = dev.getEndpoints();
		ZigBeeEndpoint endpoint = endpoints[0];
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);

		ZigBeeCluster[] clusters = endpoint.getClientClusters();
		if (clusters == null || clusters.length == 0) {
			clusters = endpoint.getServerClusters();
		}
		ZigBeeCluster cluster = null;
		if (clusters != null && clusters.length != 0) {
			cluster = clusters[0];
		}

		ZigBeeCommand command = cluster.getCommands()[0];
		assertNotNull("ZigBeeCommand is NULL", command);

		log("ZigBeeCommand ID: " + command.getId());
		assertEquals("Command identifier not matched",
				ZigBeeConstants.COMMAND_ID, String.valueOf(command.getId()));

	}

	/**
	 * Tests related to Attribute Description.
	 */
	public void testAttributeDescription() {
		ZigBeeNode dev = listener.getZigBeeNode();
		assertNotNull("ZigBeeNode is NULL", dev);

		ZigBeeEndpoint[] endpoints = dev.getEndpoints();
		ZigBeeEndpoint endpoint = endpoints[0];
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);

		ZigBeeCluster[] clusters = endpoint.getServerClusters();
		ZigBeeCluster cluster = null;
		if (clusters != null && clusters.length != 0) {
			cluster = clusters[0];
		}

		ZigBeeAttribute attribute = cluster.getAttributes()[0];
		assertNotNull("ZigBeeAttribute is NULL", attribute);

		log("ZigBeeAttribute ID: " + attribute.getId());
		assertEquals("Attribute identifier not matched",
				ZigBeeConstants.ATTRIBUTE_ID, String.valueOf(attribute.getId()));

		// Use ZigBeeAttributeImpl instead of ZigBeeAttribute attribute, in
		// order to do the tests?
		//
		// log("ZigBeeAttribute NAME: " + attribute.getDescription().getName());
		// assertEquals("Attribute name not matched",
		// ZigBeeConstants.ATTRIBUTE_NAME,
		// attribute.getDescription().getName());
		//
		// log("ZigBeeCommand MANDATORY: " +
		// attribute.getDescription().isMandatory());
		// assertEquals("Attribute mandatory state not matched",
		// ZigBeeConstants.ATTRIBUTE_MANDATORY,
		// String.valueOf(attribute.getDescription().isMandatory()));
		//
		// log("ZigBeeCommand REPORTABLE: " +
		// attribute.getDescription().isReportable());
		// assertEquals("Attribute reportable state not matched",
		// ZigBeeConstants.ATTRIBUTE_REPORTABLE,
		// String.valueOf(attribute.getDescription().isReportable()));
		//
		// log("ZigBeeCommand ACCESS_TYPE: " +
		// attribute.getDescription().getAccessType());
		// assertEquals("Attribute access type not matched",
		// ZigBeeConstants.ATTRIBUTE_ACCESS_TYPE,
		// String.valueOf(attribute.getDescription().getAccessType()));
		//
		// log("ZigBeeCommand DATA_TYPE: " +
		// attribute.getDescription().getDataTypeDescription().getName());
		// assertEquals("Attribute data type not matched",
		// ZigBeeConstants.ATTRIBUTE_DATA_TYPE,
		// attribute.getDescription().getDataTypeDescription().getName());
	}

	// ====================================================================
	// ===========================CONTROL TEST=============================
	// ===========================METHODS==================================
	// ====================================================================

	/**
	 * Tests related to control.
	 */
	public void testControl() {
		// device
		ZigBeeNode device = listener.getZigBeeNode();
		assertNotNull("ZigBeeNode is NULL", device);

		// endpoints
		ZigBeeEndpoint[] endpoints = device.getEndpoints();
		ZigBeeEndpoint endpoint = endpoints[0];
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);

		// clusters
		ZigBeeCluster[] clusters = endpoint.getServerClusters();
		ZigBeeCluster cluster = null;
		if (clusters != null && clusters.length != 0) {
			cluster = clusters[0];
		}
		assertNotNull("ZigBeeCluster is NULL", cluster);

		// Test "control" methods of ZigBeeCluster.

		// ZigBeeHandler handler = new ZigBeeHandlerImpl();
		int[] attributesIds = {8};
		ZigBeeHandlerImpl handlerCluster = new ZigBeeHandlerImpl();
		Boolean isSuccess;

		// cluster.readAttributes
		cluster.readAttributes(attributesIds, handlerCluster);
		isSuccess = handlerCluster.isSuccess();
		if (isSuccess == null) {
			fail("isSuccess is expected not to be null.");
		} else
			if (isSuccess) {
				log("handlerCluster.getResponse(): " + handlerCluster.getResponse());
			} else {
				fail("isSuccess is expected not to be false.");
			}

		// cluster.writeAttributes(undivided, attributesRecords,
		// handlerCluster);
		Boolean undivided = true;
		ZigBeeAttributeRecord[] attributesRecords = null;
		cluster.writeAttributes(undivided, attributesRecords,
				handlerCluster);
		isSuccess = handlerCluster.isSuccess();
		if (isSuccess == null) {
			fail("isSuccess is expected not to be null.");
		} else
			if (isSuccess) {
				log("handlerCluster.getResponse(): " + handlerCluster.getResponse());
			} else {
				fail("isSuccess is expected not to be false.");
			}

		// Test "control" methods of ZigBeeAttribute.

		// attributes
		ZigBeeAttribute[] attributes = cluster.getAttributes();
		log("attributes: " + attributes);

		ZigBeeAttribute attribute = attributes[8];

		try {
			ZigBeeHandlerImpl handlerAttributeGetValue1 = new ZigBeeHandlerImpl();
			attribute.getValue(handlerAttributeGetValue1);

			isSuccess = handlerAttributeGetValue1.isSuccess();
			if (isSuccess == null) {
				fail("isSuccess is expected not to be null.");
			} else
				if (isSuccess) {
					log("handlerAttributeGetValue1.getResponse(): " + handlerAttributeGetValue1.getResponse());
				} else {
					fail("isSuccess is expected not to be false.");
				}
		} catch (ZigBeeException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}

		try {
			ZigBeeHandlerImpl handlerAttributeGetValue2 = new ZigBeeHandlerImpl();
			ZigBeeDataTypeDescription outputType = ZigBeeDataTypes.BOOLEAN;
			attribute.getValue(outputType, handlerAttributeGetValue2);

			isSuccess = handlerAttributeGetValue2.isSuccess();
			if (isSuccess == null) {
				fail("isSuccess is expected not to be null.");
			} else
				if (isSuccess) {
					log("handlerAttributeGetValue2.getResponse(): " + handlerAttributeGetValue2.getResponse());
				} else {
					fail("isSuccess is expected not to be false.");
				}
		} catch (ZigBeeException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}

		try {
			Object value = new Object();
			ZigBeeHandlerImpl handler = new ZigBeeHandlerImpl();
			attribute.setValue(value, handler);

			isSuccess = handler.isSuccess();
			if (isSuccess == null) {
				fail("isSuccess is expected not to be null.");
			} else
				if (isSuccess) {
					log("handler.getResponse(): " + handler.getResponse());
				} else {
					fail("isSuccess is expected not to be false.");
				}
		} catch (ZigBeeException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}

		try {
			byte[] value = {};
			ZigBeeHandlerImpl handler = new ZigBeeHandlerImpl();
			attribute.setValue(value, handler);

			isSuccess = handler.isSuccess();
			if (isSuccess == null) {
				fail("isSuccess is expected not to be null.");
			} else
				if (isSuccess) {
					log("handler.getResponse(): " + handler.getResponse());
				} else {
					fail("isSuccess is expected not to be false.");
				}
		} catch (ZigBeeException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}

		// Test "control" methods of ZigBeeCommand.

		// commands
		ZigBeeCommand[] commands = cluster.getCommands();
		ZigBeeCommand command = null;
		if (commands != null && commands.length != 0) {
			command = commands[0];
		}
		assertNotNull("ZigBeeCommand is NULL", command);

		byte[] bytes = null;
		try {
			ZigBeeCommandHandlerImpl commandHandlerImpl = new ZigBeeCommandHandlerImpl();
			command.invoke(bytes, commandHandlerImpl);

			isSuccess = commandHandlerImpl.isSuccess();
			if (isSuccess == null) {
				fail("isSuccess is expected not to be null.");
			} else
				if (isSuccess) {
					log("commandHandlerImpl.getResponse(): " + commandHandlerImpl.getResponseSuccess());
				} else {
					fail("isSuccess is expected not to be false.");
				}
		} catch (ZigBeeException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}
	}

	// ====================================================================
	// ===========================EVENTING TEST============================
	// ===========================METHODS==================================
	// ====================================================================

	/**
	 * Tests related to eventing.
	 */
	public void testEventing() {
		// device
		ZigBeeNode device = listener.getZigBeeNode();
		assertNotNull("ZigBeeNode is NULL", device);

		// endpoints
		ZigBeeEndpoint[] endpoints = device.getEndpoints();
		ZigBeeEndpoint endpoint = endpoints[0];
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);

		// clusters
		ZigBeeCluster[] clusters = endpoint.getServerClusters();
		ZigBeeCluster cluster = null;
		if (clusters != null && clusters.length != 0) {
			cluster = clusters[0];
		}
		assertNotNull("ZigBeeCluster is NULL", cluster);

		// specify the event to be send by the event source to the event
		// listener.
		Dictionary<String, String> events = new Hashtable<String, String>();
		events.put("eventKey", "eventValue");
		ZigBeeEvent aZigbeeEvent = new ZigBeeEventImpl(cluster, events);

		// create, and launch a test event source.
		ZigBeeEventSourceImpl aZigBeeEventSourceImpl = new ZigBeeEventSourceImpl(getContext(), aZigbeeEvent);
		aZigBeeEventSourceImpl.start();

		// create, and launch a test event listener.
		ZigBeeEventListenerImpl aZigBeeEventListenerImpl = new ZigBeeEventListenerImpl(getContext());
		aZigBeeEventListenerImpl.start();

		// assert that eventing works: the sent, and the received events must be
		// equal.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}

		ZigBeeEvent lastReceivedZigBeeEvent = aZigBeeEventListenerImpl.getLastReceivedZigBeeEvent();

		// log("aZigbeeEvent: " + aZigbeeEvent);
		// log("lastReceivedZigBeeEvent: " + lastReceivedZigBeeEvent);

		assertNotNull("aZigbeeEvent can not be null", aZigbeeEvent);
		assertNotNull("lastReceivedZigBeeEvent can not be null", lastReceivedZigBeeEvent);
		assertEquals("aZigbeeEvent, and lastReceivedZigBeeEvent must be equal.", aZigbeeEvent, lastReceivedZigBeeEvent);

		// stop/destroy the test event listener.
		aZigBeeEventListenerImpl.stop();

		// stop/destroy the test event source.
		aZigBeeEventSourceImpl.stop();
	}

	// ====================================================================
	// ===========================EXPORT TEST==============================
	// ===========================METHODS==================================
	// ====================================================================

	// TODO: AAA: implement export test methods.

	// ====================================================================
	// ===========================EXCEPTIONS TEST==========================
	// ===========================METHODS==================================
	// ====================================================================

	// TODO: AAA: implement exceptions test methods.

	// ====================================================================
	// ===========================ZIGBEE DATA TYPES TEST===================
	// ===========================METHODS==================================
	// ====================================================================

	// TODO: AAA: implement zigbee data types test methods.

	// ====================================================================
	// ===========================HOST/COORDINATOR GETTERS/SETTERS TEST====
	// ===========================METHODS==================================
	// ====================================================================

	// TODO: AAA: implement host/coordinator getters/setters test methods.

}
