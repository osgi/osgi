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

package org.osgi.test.cases.zigbee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLAttributeInfo;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.osgi.service.zigbee.types.ZigBeeBoolean;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;
import org.osgi.test.cases.zigbee.impl.ZCLAttributeImpl;
import org.osgi.test.cases.zigbee.impl.ZCLClusterConf;
import org.osgi.test.cases.zigbee.impl.ZCLCommandHandlerImpl;
import org.osgi.test.cases.zigbee.impl.ZCLEventListenerImpl;
import org.osgi.test.cases.zigbee.impl.ZCLFrameImpl;
import org.osgi.test.cases.zigbee.impl.ZigBeeEndpointConf;
import org.osgi.test.cases.zigbee.impl.ZigBeeEndpointImpl;
import org.osgi.test.cases.zigbee.impl.ZigBeeEventImpl;
import org.osgi.test.cases.zigbee.impl.ZigBeeEventSourceImpl;
import org.osgi.test.cases.zigbee.impl.ZigBeeHandlerImpl;
import org.osgi.test.cases.zigbee.impl.ZigBeeNodeConf;
import org.osgi.test.cases.zigbee.impl.ZigBeeNodeImpl;
import org.osgi.test.cases.zigbee.impl.ZigBeeTestOSGiIdEndpointImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Contain the ZigBee testcases.
 * 
 * @author $Id: 15040569ced6e2d236ca05ca3c1c4c53ea8e0aff $
 */
public class ZigBeeControlTestCase extends DefaultTestBundleControl {

	private ServicesListener	listener;
	public static int			HANDLER_TIMEOUT		= 3000;
	public static int			DISCOVERY_TIMEOUT	= 3000;

	ConfigurationFileReader conf;

	private String confFilePath = "template.xml";

	protected void setUp() throws Exception {
		log("Prepare for ZigBee Test Case");

		prepareTestStart();
		log("Prepared for ZigBee Test Case");
	}

	protected void tearDown() throws Exception {
		log("Tear down ZigBee Test Case");
	}

	private void prepareTestStart() throws Exception {
		TestStepLauncher launcher = TestStepLauncher.launch(confFilePath, getContext());
		conf = launcher.getConfReader();
		HANDLER_TIMEOUT = conf.getInvokeTimeout();
		DISCOVERY_TIMEOUT = conf.getDiscoveryTimeout();
	}

	private String readFile(String file) throws IOException {
		String result = "";
		try {
			String line;
			File myFile = new File(file);
			BufferedReader inFile = new BufferedReader(new FileReader(myFile));
			while ((line = inFile.readLine()) != null) {
				result += line;
			}
			inFile.close();
		} catch (IOException e) {
			System.out.println("problem with file");
		}

		return result;
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
		log("---- testNodeDiscovery");

		// get the endpoint values in the conf file
		ZigBeeNodeConf devConf = conf.getNode0();
		ZigBeeEndpointConf endpointConf = (ZigBeeEndpointConf) conf.getEnpoints(devConf)[0];
		ZigBeeNode dev = getZigBeeNode(devConf.getIEEEAddress());
		try {

			// check endpoints number
			int endpointNb = devConf.getEndpointNb();
			int registeredEnpointNb = getRegisteredEnpoints(devConf.getIEEEAddress());
			assertEquals(
					"The number of registered endpoints by the baseDriver is not the same as declared in the configuration file for the node with IEEE Address:"
							+ devConf.getIEEEAddress(),
					endpointNb,
					registeredEnpointNb);
			// user description
			String userConfDescription = devConf.getUserDesc();
			ZigBeeHandlerImpl handler = new ZigBeeHandlerImpl();
			dev.getUserDescription(handler);
			handler.waitForResponse(HANDLER_TIMEOUT);
			String userDescription = (String) handler.getSuccessResponse();
			assertNotNull("user description is NULL", userDescription);
			assertEquals("user description not matched", userConfDescription, userDescription);

			ZigBeeNodeDescriptor zigBeeNodeDescriptor;
			ZigBeeNodeDescriptor nodeDescConf = devConf.getNodeDescriptor();
			dev.getNodeDescriptor(handler);
			handler.waitForResponse(HANDLER_TIMEOUT);
			zigBeeNodeDescriptor = (org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor) handler
					.getSuccessResponse();
			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode LOGICAL_TYPE: " + zigBeeNodeDescriptor.getLogicalType());
			assertEquals("Logical type not matched",
					nodeDescConf.getLogicalType(),
					zigBeeNodeDescriptor.getLogicalType());

			dev.getNodeDescriptor(handler);
			handler.waitForResponse(HANDLER_TIMEOUT);
			zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) handler.getSuccessResponse();
			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode MANUFACTURER_CODE: " + zigBeeNodeDescriptor.getManufacturerCode());
			assertEquals("Manufacturer code not matched",
					nodeDescConf.getManufacturerCode(),
					zigBeeNodeDescriptor.getManufacturerCode());

			dev.getNodeDescriptor(handler);
			handler.waitForResponse(HANDLER_TIMEOUT);
			zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) handler.getSuccessResponse();
			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode MAXIMUM_BUFFER_SIZE: " + String.valueOf(zigBeeNodeDescriptor.getMaxBufferSize()));
			assertEquals("Maximum buffer size not matched",
					nodeDescConf.getMaxBufferSize(),
					zigBeeNodeDescriptor.getMaxBufferSize());

			dev.getNodeDescriptor(handler);
			handler.waitForResponse(HANDLER_TIMEOUT);
			zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) handler.getSuccessResponse();
			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode MAXIMUM_INCOMING_TRANSFERT_SIZE: " + zigBeeNodeDescriptor.getMaxIncomingTransferSize());
			assertEquals("Maximum incoming transfert size not matched",
					nodeDescConf.getMaxIncomingTransferSize(),
					zigBeeNodeDescriptor.getMaxIncomingTransferSize());

			ZigBeePowerDescriptor zigBeePowerDescriptor;
			ZigBeePowerDescriptor PowerDescConf = devConf.getPowerDescriptor();
			dev.getPowerDescriptor(handler);
			handler.waitForResponse(HANDLER_TIMEOUT);
			zigBeePowerDescriptor = (ZigBeePowerDescriptor) handler.getSuccessResponse();
			assertNotNull("power descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode CURRENT_POWER_MODE: " + zigBeePowerDescriptor.getCurrentPowerMode());
			assertEquals("Current power mode not matched",
					PowerDescConf.getCurrentPowerMode(),
					zigBeePowerDescriptor.getCurrentPowerMode());

			dev.getPowerDescriptor(handler);
			handler.waitForResponse(HANDLER_TIMEOUT);
			zigBeePowerDescriptor = (ZigBeePowerDescriptor) handler.getSuccessResponse();
			assertNotNull("power descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode CURRENT_POWER_SOURCE: " + zigBeePowerDescriptor.getCurrentPowerSource());
			assertEquals("Current power source not matched",
					PowerDescConf.getCurrentPowerSource(),
					zigBeePowerDescriptor.getCurrentPowerSource());

			dev.getPowerDescriptor(handler);
			handler.waitForResponse(HANDLER_TIMEOUT);
			zigBeePowerDescriptor = (ZigBeePowerDescriptor) handler.getSuccessResponse();
			assertNotNull("power descriptor is NULL", zigBeePowerDescriptor);
			log("ZigBeeNode AVAILABLE_POWER_SOURCE: " + zigBeePowerDescriptor.isConstantMainsPowerAvailable());
			assertEquals("Availability of power source not matched",
					PowerDescConf.isConstantMainsPowerAvailable(),
					zigBeePowerDescriptor.isConstantMainsPowerAvailable());

			dev.getPowerDescriptor(handler);
			handler.waitForResponse(HANDLER_TIMEOUT);

			zigBeePowerDescriptor = (ZigBeePowerDescriptor) handler.getSuccessResponse();
			assertNotNull("power descriptor is NULL", zigBeePowerDescriptor);
			log("ZigBeeNode CURRENT_POWER_SOURCE_LEVEL: " + zigBeePowerDescriptor.getCurrentPowerSourceLevel());
			assertEquals("Current power source not matched",
					PowerDescConf.getCurrentPowerSourceLevel(),
					zigBeePowerDescriptor.getCurrentPowerSourceLevel());

			dev.getNodeDescriptor(handler);
			handler.waitForResponse(HANDLER_TIMEOUT);

			zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) handler.getSuccessResponse();
			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
			if (zigBeeNodeDescriptor.isComplexDescriptorAvailable()) {

				dev.getComplexDescriptor(handler);
				ZigBeeComplexDescriptor complexDescConf = devConf.getComplexDescriptor();
				handler.waitForResponse(HANDLER_TIMEOUT);

				ZigBeeComplexDescriptor zigBeeComplexDescriptor = (ZigBeeComplexDescriptor) handler
						.getSuccessResponse();
				assertNotNull("complex descriptor is NULL", zigBeeComplexDescriptor);
				log("ZigBeeNode MODEL_NAME: " + zigBeeComplexDescriptor.getModelName());
				assertEquals("Model name not matched",
						complexDescConf.getModelName(),
						zigBeeComplexDescriptor.getModelName());

				log("ZigBeeNode SERIAL_NAME: " + zigBeeComplexDescriptor.getSerialNumber());
				assertEquals("Serial name not matched",
						complexDescConf.getSerialNumber(),
						zigBeeComplexDescriptor.getSerialNumber());

				log("ZigBeeNode NODE_URL: " + zigBeeComplexDescriptor.getDeviceURL());
				assertEquals("Node url not matched",
						complexDescConf.getDeviceURL(),
						zigBeeComplexDescriptor.getDeviceURL());

			}

		} catch (ZCLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param endpointIeeeAddress
	 * @return the ZigBeeEndpoint having the given endpointIeeeAddress.
	 */
	private ZigBeeEndpoint getZigBeeEndpoint(BigInteger endpointIeeeAddress) {
		ZigBeeEndpoint endpoint = null;
		BundleContext bc = getContext();
		try {
			ServiceTracker st = new ServiceTracker(bc,
					bc.createFilter("(&(objectclass=org.osgi.service.zigbee.ZigBeeEndpoint)(" + ZigBeeNode.IEEE_ADDRESS
							+ "=" + endpointIeeeAddress + "))"),
					null);
			st.open();

			Object service = st.waitForService(HANDLER_TIMEOUT);
			return (ZigBeeEndpoint) service;

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (InvalidSyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return endpoint;
	}

	/**
	 * 
	 * @param endpointIeeeAddress
	 * @return the number of registered enpoints for the given IEEE address
	 */
	private int getRegisteredEnpoints(BigInteger endpointIeeeAddress) {
		int result = 0;

		try {
			ServiceReference[] srs = getContext().getAllServiceReferences(ZigBeeEndpoint.class.getName(), null);
			log("srs: " + srs);

			int srsIndex = 0;
			while (srsIndex < srs.length) {
				ServiceReference sr = srs[srsIndex];
				if (endpointIeeeAddress.equals(sr.getProperty(ZigBeeNode.IEEE_ADDRESS))) {
					// The sr's value associated to ZigBeeEndpoint.PROFILE_ID
					// may also be checked.
					result++;
				}
				srsIndex = srsIndex + 1;
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			fail("No InvalidSyntaxException is expected", e);
		}

		return result;
	}

	private ZigBeeNode getZigBeeNode(BigInteger nodeIeeeAddress) {
		ZigBeeNode node = null;
		BundleContext bc = getContext();
		try {
			ServiceTracker st = new ServiceTracker(bc,
					bc.createFilter("(&(objectclass=org.osgi.service.zigbee.ZigBeeNode)(" + ZigBeeNode.IEEE_ADDRESS
							+ "=" + nodeIeeeAddress + "))"),
					null);
			st.open();

			Object service = st.waitForService(HANDLER_TIMEOUT);
			return (ZigBeeNode) service;

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (InvalidSyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return node;
	}

	private ZigBeeHost getZigBeeHost(BigInteger nodeIeeeAddress) {
		ZigBeeHost host = null;
		BundleContext bc = getContext();
		try {
			ServiceTracker st = new ServiceTracker(
					bc,
					bc.createFilter("(&(objectclass=org.osgi.service.zigbee.ZigBeeHost)("
							+ ZigBeeNode.IEEE_ADDRESS
							+ "="
							+ nodeIeeeAddress
							+ "))"),
					null);
			st.open();

			Object service = st.waitForService(HANDLER_TIMEOUT);
			return (ZigBeeHost) service;

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (InvalidSyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return host;
	}

	/**
	 * Tests related to Endpoint Discovery.
	 */
	public void testEndpointDiscovery() {
		log("---- testEndpointDiscovery");

		// get the endpoint values in the conf file
		ZigBeeNodeImpl node = conf.getNode0();
		ZigBeeEndpointConf endpointConf = (ZigBeeEndpointConf) conf.getEnpoints(node)[0];
		ZigBeeSimpleDescriptor simpleDescConf = endpointConf.getSimpleDescriptor();

		ZigBeeEndpoint endpoint = getZigBeeEndpoint(node.getIEEEAddress());
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);
		log("ZigBeeEndpoint ENDPOINT: " + endpoint.getId());

		assertEquals("Endpoint identifier not matched", endpointConf.getId(), endpoint.getId());

		try {
			ZigBeeHandlerImpl handler = new ZigBeeHandlerImpl();
			endpoint.getSimpleDescriptor(handler);
			handler.waitForResponse(HANDLER_TIMEOUT);
			ZigBeeSimpleDescriptor zigBeeSimpleDescriptor = (ZigBeeSimpleDescriptor) handler.getSuccessResponse();
			assertNotNull("simple descriptor is NULL", zigBeeSimpleDescriptor);
			log("ZigBeeEndpoint PROFILE_ID: " + zigBeeSimpleDescriptor.getApplicationProfileId());
			assertEquals("Application Profile identifier not matched",
					simpleDescConf.getApplicationProfileId(),
					zigBeeSimpleDescriptor.getApplicationProfileId());

			handler = new ZigBeeHandlerImpl();
			endpoint.getSimpleDescriptor(handler);
			handler.waitForResponse(HANDLER_TIMEOUT);
			zigBeeSimpleDescriptor = (ZigBeeSimpleDescriptor) handler.getSuccessResponse();
			assertNotNull("simple descriptor is NULL", zigBeeSimpleDescriptor);
			log("ZigBeeEndpoint DEVICE_ID: " + zigBeeSimpleDescriptor.getApplicationDeviceId());
			assertEquals("Application Device identifier not matched",
					String.valueOf(simpleDescConf.getApplicationDeviceId()),
					String.valueOf(zigBeeSimpleDescriptor.getApplicationDeviceId()));

			handler = new ZigBeeHandlerImpl();
			endpoint.getSimpleDescriptor(handler);
			handler.waitForResponse(HANDLER_TIMEOUT);
			zigBeeSimpleDescriptor = (ZigBeeSimpleDescriptor) handler.getSuccessResponse();
			assertNotNull("simple descriptor is NULL", zigBeeSimpleDescriptor);
			log("ZigBeeEndpoint DEVICE_VERSION: " + zigBeeSimpleDescriptor.getApplicationDeviceVersion());
			assertEquals("Application device version not matched",
					String.valueOf(simpleDescConf.getApplicationDeviceVersion()),
					String.valueOf(zigBeeSimpleDescriptor.getApplicationDeviceVersion()));

			// compare server/ clients Clusters Ids
			int[] serverClustersArray = zigBeeSimpleDescriptor.getInputClusters();
			int[] serverClustersConfArray = simpleDescConf.getInputClusters();

			assertEquals("number of server clusters is not correct",
					serverClustersConfArray.length,
					serverClustersArray.length);

			int[] clientClustersArray = zigBeeSimpleDescriptor.getOutputClusters();
			int[] clientClustersConfArray = simpleDescConf.getOutputClusters();

			assertEquals("number of client clusters is not correct",
					clientClustersArray.length,
					clientClustersConfArray.length);

		} catch (Exception e) {
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
		log("---- testClusterDescription");
		// get the endpoint values in the conf file
		ZigBeeNodeImpl node = conf.getNode0();

		ZigBeeEndpointConf endpointConf = (ZigBeeEndpointConf) conf.getEnpoints(node)[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpoint(node.getIEEEAddress());
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);
		log("ZigBeeEndpoint ENDPOINT: " + endpoint.getId());
		assertEquals("Endpoint identifier not matched", endpointConf.getId(), endpoint.getId());

		ZCLCluster[] clusters = endpoint.getClientClusters();
		if (clusters == null || clusters.length == 0) {
			clusters = endpoint.getServerClusters();
		}
		ZCLCluster cluster = null;
		if (clusters != null && clusters.length != 0) {
			cluster = clusters[0];
		}
		assertNotNull("ZigBeeCluster is NULL", cluster);

		log("ZigBeeCluster ID: " + cluster.getId());
		assertEquals("Clusters identifier not matched", endpointConf.getServerClusters()[0].getId(), cluster.getId());

		// Use ZigBeeClusterImpl instead of ZigBeeCluster cluster, in order
		// to do the tests?
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
		log("---- testCommandDescription");

		// get the endpoint values in the conf file
		ZigBeeNodeImpl node = conf.getNode0();

		ZigBeeEndpointConf endpointConf = (ZigBeeEndpointConf) conf.getEnpoints(node)[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpoint(node.getIEEEAddress());

		assertNotNull("ZigBeeEndpoint is NULL", endpoint);
		log("ZigBeeEndpoint ENDPOINT: " + endpoint.getId());
		assertEquals("Endpoint identifier not matched", endpointConf.getId(), endpoint.getId());

		ZCLCluster[] clusters = endpoint.getServerClusters();
		if (clusters == null || clusters.length == 0) {
			clusters = endpoint.getServerClusters();
		}
		ZCLCluster cluster = null;
		if (clusters != null && clusters.length != 0) {
			cluster = clusters[0];
		}

		ZigBeeHandlerImpl zigBeeHandler = new ZigBeeHandlerImpl();
		cluster.getCommandIds(zigBeeHandler);
		zigBeeHandler.waitForResponse(HANDLER_TIMEOUT);
		int commandId = ((int[]) (zigBeeHandler.getSuccessResponse()))[0];
		log("ZCLCommand ID: " + commandId);
		// assertNotNull("ZCLCommand ID is NULL", commandId);
		assertEquals("Command identifier not matched",
				((ZCLClusterConf) endpointConf.getServerClusters()[0]).getCommandIds()[0],
				commandId);
	}

	/**
	 * Tests related to Attribute Description.
	 */
	public void testAttributeDescription() {
		log("---- testAttributeDescription");

		// get the endpoint values in the conf file
		ZigBeeNodeImpl node = conf.getNode0();

		ZigBeeEndpointConf endpointConf = (ZigBeeEndpointConf) conf.getEnpoints(node)[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpoint(node.getIEEEAddress());
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);
		log("ZigBeeEndpoint ENDPOINT: " + endpoint.getId());
		assertEquals("Endpoint identifier not matched", endpointConf.getId(), endpoint.getId());
		ZCLCluster[] clustersConf = endpointConf.getServerClusters();
		ZCLCluster clusterConf = clustersConf[0];
		ZCLAttribute attributeConf = ((ZCLClusterConf) clusterConf).getAttributes()[0];

		String name = "";
		if (attributeConf instanceof ZCLAttributeImpl) {
			ZCLAttributeImpl attrImpl = (ZCLAttributeImpl) attributeConf;
			name = attrImpl.getAttributeDescription().getName();
		}

		ZCLCluster[] clusters = endpoint.getServerClusters();
		ZCLCluster cluster = null;
		if (clusters != null && clusters.length != 0) {
			cluster = clusters[0];
		}
		ZigBeeHandlerImpl zigBeeHandler = new ZigBeeHandlerImpl();
		cluster.getAttributes(zigBeeHandler);
		zigBeeHandler.waitForResponse(HANDLER_TIMEOUT);
		ZCLAttribute attribute = ((ZCLAttribute[]) zigBeeHandler.getSuccessResponse())[0];
		assertNotNull("ZCLAttribute " + name + " is NULL", attribute);

		log("ZCLAttribute ID: " + attribute.getId());
		assertEquals("Attribute " + name + " identifier not matched", attributeConf.getId(), attribute.getId());

	}

	// ====================================================================
	// ===========================CONTROL TEST=============================
	// ===========================METHODS==================================
	// ====================================================================

	/**
	 * Tests related to control.
	 */
	public void testControl() {
		log("---- testControl");

		// get the endpoint values in the conf file
		ZigBeeNodeImpl node = conf.getNode0();

		ZigBeeEndpointConf endpointConf = (ZigBeeEndpointConf) conf.getEnpoints(node)[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpoint(node.getIEEEAddress());
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);
		log("ZigBeeEndpoint ENDPOINT: " + endpoint.getId());
		assertEquals("Endpoint identifier not matched", endpointConf.getId(), endpoint.getId());

		// clusters
		ZCLCluster[] clusters = endpoint.getServerClusters();
		ZCLCluster cluster = null;
		if (clusters != null && clusters.length != 0) {
			cluster = clusters[0];
		}
		assertNotNull("ZigBeeCluster is NULL", cluster);

		// Test "control" methods of ZigBeeCluster.

		// int[] attributesIds = {8};
		ZCLAttributeInfo[] zclAttributeInfos = {new ZCLAttributeInfo() {

			public boolean isManufacturerSpecific() {
				// TODO Auto-generated method stub
				return false;
			}

			public int getManufacturerCode() {
				// TODO Auto-generated method stub
				return 0;
			}

			public int getId() {
				// the attribute 0 must be present to test the READ_ONLY ZCL
				// exception
				return 0;
			}

			public ZCLDataTypeDescription getDataType() {

				return null;
			}
		}};
		ZigBeeHandlerImpl handlerCluster = new ZigBeeHandlerImpl();
		Boolean isSuccess;

		// cluster.readAttributes
		cluster.readAttributes(zclAttributeInfos, handlerCluster);
		handlerCluster.waitForResponse(HANDLER_TIMEOUT);

		isSuccess = handlerCluster.isSuccess();
		assertNotNull("handlerCluster response is NULL", endpoint);
		assertTrue("isSuccess is expected not to be false. ", isSuccess.booleanValue());
		log("handlerCluster.getSuccessResponse(): " + handlerCluster.getSuccessResponse());

		// cluster.writeAttributes(undivided, attributesRecords,
		// handlerCluster);
		boolean undivided = true;
		Map attributesIdsAndValues = null;
		cluster.writeAttributes(undivided, attributesIdsAndValues, handlerCluster);
		handlerCluster.waitForResponse(HANDLER_TIMEOUT);
		isSuccess = handlerCluster.isSuccess();
		assertNotNull("handlerCluster response is NULL", endpoint);
		assertTrue("isSuccess is expected not to be false.", isSuccess.booleanValue());
		log("handlerCluster.getSuccessResponse(): " + handlerCluster.getSuccessResponse());

		// Test "control" methods of ZCLAttribute.

		// attributes
		ZigBeeHandlerImpl zigBeeHandler = new ZigBeeHandlerImpl();
		cluster.getAttributes(zigBeeHandler);
		zigBeeHandler.waitForResponse(HANDLER_TIMEOUT);
		ZCLAttribute[] attributes = (ZCLAttribute[]) zigBeeHandler.getSuccessResponse();
		log("attributes: " + attributes);

		ZCLAttribute attribute = attributes[0];

		try {
			ZigBeeHandlerImpl handlerAttributeGetValue1 = new ZigBeeHandlerImpl();
			attribute.getValue(handlerAttributeGetValue1);
			zigBeeHandler.waitForResponse(HANDLER_TIMEOUT);
			isSuccess = handlerAttributeGetValue1.isSuccess();
			assertNotNull("handlerAttribute response is NULL", endpoint);
			assertTrue("isSuccess is expected not to be false.", isSuccess.booleanValue());
			log("handlerAttributeGetValue1.getSuccessResponse(): " + handlerAttributeGetValue1.getSuccessResponse());

		} catch (ZCLException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}

		// Test "control" methods of ZigBeeCluster.

		// cluster
		zigBeeHandler = new ZigBeeHandlerImpl();
		cluster.getCommandIds(zigBeeHandler);
		zigBeeHandler.waitForResponse(HANDLER_TIMEOUT);
		int[] commandIds = (int[]) (zigBeeHandler.getSuccessResponse());
		// assertNotNull("ZigBeeCluster has no command", commandIds.length);
		assertNotSame("ZigBeeCluster has no command",
				Integer.valueOf("0"),
				Integer.valueOf(Integer.toString(commandIds.length)));
		int commandId = commandIds[0];
		log("DEBUG: commandId: " + commandId);
		// assertNotNull("ZCLCommand ID is NULL", commandId);

		ZCLFrame frame = new TestZCLFrame(conf.getRequestHeader(),
				conf.getRequestFullFrame());
		try {
			ZCLCommandHandlerImpl commandHandlerImpl = new ZCLCommandHandlerImpl();
			cluster.invoke(frame, commandHandlerImpl);
			commandHandlerImpl.waitForResponse(HANDLER_TIMEOUT);
			ZCLFrame frameResponse = commandHandlerImpl.getResponse();
			log("commandHandlerImpl.getResponse(): "
					+ commandHandlerImpl.getResponse());
			assertTrue(
					"the response frame is not the one expected",
					Arrays.equals(conf.getResponseFullFrame(),
							frameResponse.getBytes()));
			/*
			 * Issues some checks on the returned ZCLFrame
			 */

			assertNotNull("Response is NULL", frameResponse);
			checkZCLFrame(frameResponse);

		} catch (ZCLException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}

		frame = null;
		String exportedServicePID = null;
		try {
			ZCLCommandHandlerImpl commandHandlerImpl = new ZCLCommandHandlerImpl();
			cluster.invoke(frame, commandHandlerImpl, exportedServicePID);
			commandHandlerImpl.waitForResponse(HANDLER_TIMEOUT);
			ZCLFrame response = commandHandlerImpl.getResponse();
			log("commandHandlerImpl.getResponse(): " + response);
		} catch (ZCLException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}
	}

	/**
	 * Performs checks on the passed frame.
	 * 
	 * @param zclFrame
	 */

	private void checkZCLFrame(ZCLFrame zclFrame) {

		/*
		 * Checks if the getBytes() method actually returns a copy of the actual
		 * internal ZCLFrame array. In order to do that it creates a ZCLFrame
		 * instance, using the ZCLFrameImpl class provided by this bundle
		 */

		byte[] frame = zclFrame.getBytes();

		ZCLFrame referenceZCLFrame = new ZCLFrameImpl(zclFrame.getHeader(), frame);

		assertEquals("the array returned by ZCLFrame.getBytes() contains a mismatching CommandID.",
				referenceZCLFrame.getHeader().getCommandId(),
				zclFrame.getHeader().getCommandId());
		assertEquals("the array returned by ZCLFrame.getBytes() contains a mismatching Frame Control Field.",
				referenceZCLFrame.getHeader().getFrameControlField(),
				zclFrame.getHeader().getFrameControlField());
		assertEquals("the array returned by ZCLFrame.getBytes() contains a mismatching Manufacturer Code.",
				referenceZCLFrame.getHeader().getManufacturerCode(),
				zclFrame.getHeader().getManufacturerCode());
		assertEquals("the array returned by ZCLFrame.getBytes() contains a mismatching Transaction Sequence Number.",
				referenceZCLFrame.getHeader().getSequenceNumber(),
				zclFrame.getHeader().getSequenceNumber());
		assertEquals("the array returned by ZCLFrame.getBytes() contains a mismatching is Client Server field.",
				referenceZCLFrame.getHeader().isClientServerDirection(),
				zclFrame.getHeader().isClientServerDirection());
		assertEquals("the array returned by ZCLFrame.getBytes() contains a mismatching Is Manufacturer Specific field.",
				referenceZCLFrame.getHeader().isManufacturerSpecific(),
				zclFrame.getHeader().isManufacturerSpecific());
		assertEquals(
				"the array returned by ZCLFrame.getBytes() contains a mismatching 'Is Cluster Specific Command' field.",
				referenceZCLFrame.getHeader().isClusterSpecificCommand(),
				zclFrame.getHeader().isClusterSpecificCommand());
		assertEquals(
				"the array returned by ZCLFrame.getBytes() contains a mismatching 'Disable Default Response' field.",
				referenceZCLFrame.getHeader().isDefaultResponseDisabled(),
				zclFrame.getHeader().isDefaultResponseDisabled());

		// checks if the getBytes returns back a copy of the internal array.
		if (frame == null) {
			fail("null byte[] returned by getBytes()");
		}

		byte[] frame1 = zclFrame.getBytes();
		if (frame1 == frame) {
			fail("getBytes() must return a copy of the internal array");
		}

		// adds other tests.
	}

	// ====================================================================
	// ===========================EVENTING TEST============================
	// ===========================METHODS==================================
	// ====================================================================

	/**
	 * Tests related to eventing.
	 */
	public void testEventing() {
		log("---- testEventing");

		// get the endpoint values in the conf file
		ZigBeeNodeImpl node = conf.getNode0();

		ZigBeeEndpointConf endpointConf = (ZigBeeEndpointConf) conf.getEnpoints(node)[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpoint(node.getIEEEAddress());
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);
		// clusters
		ZCLCluster[] clusters = endpoint.getServerClusters();
		ZCLCluster cluster = null;
		if (clusters != null && clusters.length != 0) {
			cluster = clusters[0];
		}
		assertNotNull("ZigBeeCluster is NULL", cluster);

		// specify the event to be send by the event source to the event
		// listener.
		// Dictionary<String, String> events = new Hashtable<String, String>();
		Dictionary events = new Hashtable();
		events.put("eventKey", "eventValue");

		// TODO AAA: Is there an easy way to get attributeId?
		int attributeId = -1;
		ZigBeeEvent aZigbeeEvent = new ZigBeeEventImpl(node.getIEEEAddress(),
				endpoint.getId(),
				cluster.getId(),
				attributeId,
				events);

		// create, and launch a test event source.
		ZigBeeEventSourceImpl aZigBeeEventSourceImpl = new ZigBeeEventSourceImpl(getContext(), aZigbeeEvent);
		aZigBeeEventSourceImpl.start();

		// create, and launch a test event listener.
		ZCLEventListenerImpl aZCLEventListenerImpl = new ZCLEventListenerImpl(getContext());
		aZCLEventListenerImpl.start();

		// assert that eventing works: the sent, and the received events must be
		// equal.
		try {
			// It takes several seconds for the event to "travel" inside the
			// test framework.
			int sleepinms = DISCOVERY_TIMEOUT;
			log("Thread.sleep(" + sleepinms + ")");
			Thread.sleep(sleepinms);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}

		ZigBeeEvent lastReceivedZigBeeEvent = aZCLEventListenerImpl.getLastReceivedZigBeeEvent();
		log("lastReceivedZigBeeEvent: " + lastReceivedZigBeeEvent);
		assertNotNull("aZigbeeEvent can not be null", aZigbeeEvent);
		log("aZigbeeEvent: " + aZigbeeEvent);

		// assertNotNull("lastReceivedZigBeeEvent can not be null",
		// lastReceivedZigBeeEvent);
		// assertEquals(
		// "aZigbeeEvent, and lastReceivedZigBeeEvent must be equal.",
		// aZigbeeEvent, lastReceivedZigBeeEvent);

		// stop/destroy the test event listener.
		aZCLEventListenerImpl.stop();

		// stop/destroy the test event source.
		aZigBeeEventSourceImpl.stop();
	}

	// ====================================================================
	// ===========================EXCEPTIONS TEST==========================
	// ===========================METHODS==================================
	// ====================================================================

	public void testExceptions() {

		log("---- testExceptions");

		// get the endpoint values in the conf file
		ZigBeeNodeImpl node = conf.getNode0();

		ZigBeeEndpointConf endpointConf = (ZigBeeEndpointConf) conf.getEnpoints(node)[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpoint(node.getIEEEAddress());
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);

		assertNotNull("ZigBeeEndpoint is NULL", endpoint);
		log("ZigBeeEndpoint ENDPOINT: " + endpoint.getId());

		ZCLCluster[] clusters = endpoint.getServerClusters();
		ZCLCluster cluster = null;
		if (clusters != null && clusters.length != 0) {
			cluster = clusters[0];
		}

		ZCLCluster[] clustersConf = endpointConf.getServerClusters();
		ZCLClusterConf clusterConf = (ZCLClusterConf) clustersConf[0];
		final int invalidId = getInvalidId(clusterConf);

		ZCLAttributeInfo[] zclAttributeInfos = {new ZCLAttributeInfo() {

			public boolean isManufacturerSpecific() {
				// TODO Auto-generated method stub
				return false;
			}

			public int getManufacturerCode() {
				// TODO Auto-generated method stub
				return 0;
			}

			public int getId() {
				return invalidId;
			}

			public ZCLDataTypeDescription getDataType() {

				ZCLDataTypeDescription dataType = new ZCLDataTypeDescription() {

					public boolean isAnalog() {
						// TODO Auto-generated method stub
						return false;
					}

					public String getName() {
						// TODO Auto-generated method stub
						return "DeviceEnabled";
					}

					public Class getJavaDataType() {
						// TODO Auto-generated method stub
						return ZigBeeBoolean.class;
					}

					public short getId() {
						// TODO Auto-generated method stub
						return 3;
					}
				};
				return dataType;
			}
		}};
		ZigBeeHandlerImpl zigBeeHandler = new ZigBeeHandlerImpl();
		cluster.readAttributes(zclAttributeInfos, zigBeeHandler);
		zigBeeHandler.waitForResponse(HANDLER_TIMEOUT);
		assertNull("readAttributes response is not null", zigBeeHandler.getSuccessResponse());

		assertTrue("The response is successfull. A failure is expected", !zigBeeHandler.isSuccess().booleanValue());

		assertTrue("The exception is not a ZCL exception", zigBeeHandler.getFailureResponse() instanceof ZCLException);

		assertEquals("The ZCL exception is not an unsupported attribute exception",
				ZCLException.UNSUPPORTED_ATTRIBUTE,
				((ZCLException) zigBeeHandler.getFailureResponse()).getErrorCode());

		// DATATYPE EXCEPTION

		ZCLAttribute booleanDatatypeAttr = conf.getFirstAttributeWithBooleanDatatype();
		assertNotNull("No attribute found with the Boolean datatype, please modify the configuration file",
				booleanDatatypeAttr);

		zigBeeHandler = new ZigBeeHandlerImpl();

		cluster.getAttribute(booleanDatatypeAttr.getId(), zigBeeHandler);

		ZCLAttribute attr = (ZCLAttribute) zigBeeHandler.getSuccessResponse();
		zigBeeHandler = new ZigBeeHandlerImpl();
		// TODO write a negative value for unsigned datatype ? need check the
		// specification for setValue behaviour
		attr.setValue(new Float(4), zigBeeHandler);
		zigBeeHandler.waitForResponse(HANDLER_TIMEOUT);
		assertTrue("The response was succesfull. a failure is expected", !zigBeeHandler.isSuccess().booleanValue());

		assertTrue("The exception is not a ZCL exception", zigBeeHandler.getFailureResponse() instanceof ZCLException);
		assertEquals("The ZCL exception is not an Invalid data type exception",
				ZCLException.INVALID_DATA_TYPE,
				((ZCLException) zigBeeHandler.getFailureResponse()).getErrorCode());

		// READ ONLY EXCEPTION
		zigBeeHandler = new ZigBeeHandlerImpl();
		// use attribute 0 which is always readOnly the read only attribute for
		// basic cluster
		cluster.getAttribute(0, zigBeeHandler); // mandatory unsigned bit int

		attr = (ZCLAttribute) zigBeeHandler.getSuccessResponse();

		zigBeeHandler = new ZigBeeHandlerImpl();
		// TODO check the type of the attribute
		attr.setValue(new Integer(0), zigBeeHandler);

		zigBeeHandler.waitForResponse(HANDLER_TIMEOUT);
		assertTrue("a failure is expected", !zigBeeHandler.isSuccess().booleanValue());

		assertTrue("The exception is not a ZCL exception", zigBeeHandler.getFailureResponse() instanceof ZCLException);
		assertEquals("could set a value tagged as read only in the description",
				ZCLException.READ_ONLY,
				((ZCLException) zigBeeHandler.getFailureResponse()).getErrorCode());

		// INVALID VALUE
		// value which is not in the enum list/// 2.4.7.3 ZCL spec

	}

	private int getInvalidId(ZCLClusterConf clusterConf) {

		ZCLAttribute[] attributesConf = clusterConf.getAttributes();

		int attributesConfLength = attributesConf.length;
		int biggestId = 0;
		int currentId = 0;
		for (int i = 0; i < attributesConfLength; i++) {
			ZCLAttribute attr = attributesConf[i];
			currentId = attr.getId();
			if (currentId > biggestId) {
				biggestId = currentId;
			}
		}

		return biggestId + 1;
	}

	public void testExport() {
		BundleContext bc = getContext();
		log("---- testExport");
		ZigBeeNodeConf devConf = conf.getNode0();
		ZigBeeEndpoint ep = devConf.getEnpoints()[0];
		ZigBeeEndpoint testEp = new ZigBeeTestOSGiIdEndpointImpl(ep.getId(),
				ep.getServerClusters(),
				ep.getClientClusters(),
				null);

		Dictionary endpointProperties = new Properties();
		endpointProperties.put(ZigBeeNode.IEEE_ADDRESS, devConf.getIEEEAddress());
		endpointProperties.put(ZigBeeEndpoint.ENDPOINT_ID, String.valueOf(testEp.getId()));
		endpointProperties.put(ZigBeeEndpoint.ZIGBEE_EXPORT, "exported");
		// register test endpoint
		log(this.getClass().getName() + " - Register  endpoint: " + testEp + " in the OSGi services registry");
		// should be already registered with the same ID !!!! ->exception
		bc.registerService(ZigBeeEndpoint.class.getName(), testEp, endpointProperties);

		try {
			// It might takes time to register the service
			// test framework.
			int sleepinms = DISCOVERY_TIMEOUT;
			log("Thread.sleep(" + sleepinms + ")");
			Thread.sleep(sleepinms);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}
		boolean notExported = ((ZigBeeTestOSGiIdEndpointImpl) testEp).notExportedHasBeenCalled();
		assertTrue("the method notExported should have been called", notExported);

		// adding an endpoint

		endpointProperties = new Properties();
		BigInteger ieeeAddress = devConf.getIEEEAddress();
		ZigBeeNode node = getZigBeeNode(ieeeAddress);
		int endpointsNb = node.getEndpoints().length;

		endpointProperties.put(ZigBeeNode.IEEE_ADDRESS, ieeeAddress);
		endpointProperties.put(ZigBeeEndpoint.ENDPOINT_ID, String.valueOf(testEp.getId()));

		// TODO remove hardcoded numbers
		ZigBeeEndpoint ep2 = new ZigBeeEndpointImpl((short) getValidEndpointId(node), null, null, null);

		bc.registerService(ZigBeeEndpoint.class.getName(), ep2, endpointProperties);

		try {
			// It might takes time to register the service
			// test framework.
			int sleepinms = DISCOVERY_TIMEOUT;
			log("Thread.sleep(" + sleepinms + ")");
			Thread.sleep(sleepinms);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}
		int newNb = node.getEndpoints().length;

		assertTrue("the nubmber of endpoints for the node " + ieeeAddress + " did not change",
				newNb == endpointsNb + 1);

	}

	private int getValidEndpointId(ZigBeeNode node) {

		int result = 0;

		ZigBeeEndpoint[] endpoints = node.getEndpoints();
		int length = endpoints.length;
		boolean isValid = false;
		while (!isValid) {
			for (int i = 0; i < length; i++) {

				if (endpoints[i].getId() == result) {
					isValid = false;
					break;
				} else {
					isValid = true;
				}

			}
			if (!isValid) {
				result++;
			}
		}
		return result;
	}

	// ====================================================================
	// ===========================HOST/COORDINATOR GETTERS/SETTERS TEST====
	// ===========================METHODS==================================
	// ====================================================================

	public void testHost() {
		log("---- testHost");

		boolean isCoordinator = false;
		BigInteger hostieeeAddress = conf.getZigBeeHost().getIEEEAddress();
		ZigBeeHost host = getZigBeeHost(hostieeeAddress);
		ZigBeeHandlerImpl handler = new ZigBeeHandlerImpl();
		host.getNodeDescriptor(handler);
		handler.waitForResponse(HANDLER_TIMEOUT);
		ZigBeeNodeDescriptor zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) handler
				.getSuccessResponse();
		assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
		if (zigBeeNodeDescriptor.getLogicalType() == ZigBeeNode.COORDINATOR) {
			isCoordinator = true;
		}
		assertEquals("the host must be a coordinatior",
				zigBeeNodeDescriptor.getLogicalType(),
				ZigBeeNode.COORDINATOR);

		// logical type test
		BundleContext bc = getContext();
		try {
			ServiceTracker st = new ServiceTracker(
					bc,

					bc.createFilter("(&(objectclass=org.osgi.service.zigbee.ZigBeeNode))"),
					null);
			st.open();
			Object[] services = st.getServices();
			for (int i = 0; i < services.length; i++) {
				ZigBeeNode node = (ZigBeeNode) services[i];
				node.getNodeDescriptor(handler);
				handler.waitForResponse(HANDLER_TIMEOUT);
				zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) handler
						.getSuccessResponse();
				assertNotNull("the node descriptor shouldn't be null",
						zigBeeNodeDescriptor);
				assertNotNull("the logical type shouldn't be null", new Short(
						zigBeeNodeDescriptor.getLogicalType()));
				if (zigBeeNodeDescriptor.getLogicalType() == ZigBeeNode.COORDINATOR) {
					isCoordinator = true;
				}
			}

			// coordinator test
			assertTrue("there must be at least one coordinatior", isCoordinator);

		} catch (InvalidSyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
