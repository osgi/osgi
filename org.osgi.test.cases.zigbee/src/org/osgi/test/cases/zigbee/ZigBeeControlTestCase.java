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

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Map;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLAttributeInfo;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZDPException;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.osgi.service.zigbee.types.ZigBeeBoolean;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;
import org.osgi.test.cases.zigbee.config.file.NetworkAttributeIds;
import org.osgi.test.cases.zigbee.config.file.ZCLClusterConfig;
import org.osgi.test.cases.zigbee.config.file.ZigBeeEndpointConfig;
import org.osgi.test.cases.zigbee.config.file.ZigBeeNodeConfig;
import org.osgi.test.cases.zigbee.mock.TestNotExportedZigBeeEndpoint;
import org.osgi.test.cases.zigbee.mock.ZCLAttributeImpl;
import org.osgi.test.cases.zigbee.mock.ZCLClusterImpl;
import org.osgi.test.cases.zigbee.mock.ZCLEventListenerImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.step.TestStepProxy;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Contain the ZigBee testcases.
 * 
 * @author $Id$
 */
public class ZigBeeControlTestCase extends DefaultTestBundleControl {

	private static final String	TAG					= ZigBeeControlTestCase.class.getName();

	private TestStepLauncher	launcher;

	/**
	 * Timeout used to wait for the promises used in the RI under test to
	 * resolve. This constant is read from the configuration file provided by
	 * the RI.
	 */
	public static int			HANDLER_TIMEOUT		= 3000;

	/**
	 * Timeout used to wait for a ZigBeeNode or a ZigBeeEndpoint to be seen in
	 * the OSGi framework as a service. This constant is read from the
	 * configuration file provided by the RI.
	 */
	public static int			DISCOVERY_TIMEOUT	= 10000;

	ConfigurationFileReader		conf;

	protected void setUp() throws Exception {
		log(TAG + " - Prepare for ZigBee Test Case");

		prepareTestStart();
		log("Prepared for ZigBee Test Case");
	}

	protected void tearDown() throws Exception {
		log("Tear down ZigBee Test Case");
	}

	private void prepareTestStart() throws Exception {
		launcher = TestStepLauncher.launch(getContext());
		conf = launcher.getConfReader();

		/*
		 * Initialize timeout constants relevant for the CT.
		 */
		HANDLER_TIMEOUT = conf.getInvokeTimeout();
		DISCOVERY_TIMEOUT = conf.getDiscoveryTimeout();
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
	 * 
	 * @throws Exception
	 * 
	 */
	public void testNodeDiscovery() throws Exception {
		log(TAG + "---- testNodeDiscovery");

		// get the endpoint values in the conf file
		ZigBeeNodeConfig nodeConfig = conf.getNode0();

		ZigBeeEndpointConfig endpointConf = conf.getEnpoints(nodeConfig)[0];
		ZigBeeNode node = getZigBeeNode(nodeConfig.getIEEEAddress());
		try {

			// checks endpoints number
			int endpointNb = nodeConfig.getActualEndpointsNumber();

			int registeredEnpointNb = getRegisteredEnpoints(nodeConfig.getIEEEAddress());
			assertEquals(
					"The number of registered endpoints by the baseDriver is not the same as declared in the configuration file for the node with IEEE Address:"
							+ nodeConfig.getIEEEAddress(),
					endpointNb,
					registeredEnpointNb);

			String userConfDescription = nodeConfig.getUserDescription();

			Promise p = node.getUserDescription();
			waitForPromise(p);

			String userDescription = (String) p.getValue();
			assertNotNull("user description is NULL", userDescription);
			assertEquals("user description not matched", userConfDescription, userDescription);

			ZigBeeNodeDescriptor zigBeeNodeDescriptor;
			ZigBeeNodeDescriptor nodeDescConf = nodeConfig.getNodeDescriptor();
			p = node.getNodeDescriptor();

			waitForPromise(p);

			zigBeeNodeDescriptor = (org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor) p.getValue();

			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);

			log(TAG + " - ZigBeeNode LOGICAL_TYPE: " + zigBeeNodeDescriptor.getLogicalType());
			assertEquals("Logical type not matched", nodeDescConf.getLogicalType(), zigBeeNodeDescriptor.getLogicalType());

			p = node.getNodeDescriptor();
			waitForPromise(p);

			zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) p.getValue();

			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);

			log("ZigBeeNode MANUFACTURER_CODE: " + zigBeeNodeDescriptor.getManufacturerCode());
			assertEquals("Manufacturer code not matched", nodeDescConf.getManufacturerCode(), zigBeeNodeDescriptor.getManufacturerCode());

			p = node.getNodeDescriptor();
			waitForPromise(p);
			zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) p.getValue();
			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode MAXIMUM_BUFFER_SIZE: " + String.valueOf(zigBeeNodeDescriptor.getMaxBufferSize()));
			assertEquals("Maximum buffer size not matched", nodeDescConf.getMaxBufferSize(), zigBeeNodeDescriptor.getMaxBufferSize());

			p = node.getNodeDescriptor();
			waitForPromise(p);
			zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) p.getValue();
			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode MAXIMUM_INCOMING_TRANSFERT_SIZE: " + zigBeeNodeDescriptor.getMaxIncomingTransferSize());
			assertEquals("Maximum incoming transfert size not matched",
					nodeDescConf.getMaxIncomingTransferSize(),
					zigBeeNodeDescriptor.getMaxIncomingTransferSize());

			ZigBeePowerDescriptor zigBeePowerDescriptor;
			ZigBeePowerDescriptor powerDescriptorConfig = nodeConfig.getPowerDescriptor();
			p = node.getPowerDescriptor();
			waitForPromise(p);
			zigBeePowerDescriptor = (ZigBeePowerDescriptor) p.getValue();

			assertNotNull("power descriptor is NULL", zigBeeNodeDescriptor);

			log("ZigBeeNode CURRENT_POWER_MODE: " + zigBeePowerDescriptor.getCurrentPowerMode());

			assertEquals("Current power mode not matched", powerDescriptorConfig.getCurrentPowerMode(), zigBeePowerDescriptor.getCurrentPowerMode());

			p = node.getPowerDescriptor();
			waitForPromise(p);
			zigBeePowerDescriptor = (ZigBeePowerDescriptor) p.getValue();
			assertNotNull("power descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode CURRENT_POWER_SOURCE: " + zigBeePowerDescriptor.getCurrentPowerSource());
			assertEquals("Current power source not matched",
					powerDescriptorConfig.getCurrentPowerSource(),
					zigBeePowerDescriptor.getCurrentPowerSource());

			p = node.getPowerDescriptor();
			waitForPromise(p);

			zigBeePowerDescriptor = (ZigBeePowerDescriptor) p.getValue();

			assertNotNull("power descriptor is NULL", zigBeePowerDescriptor);

			log("ZigBeeNode AVAILABLE_POWER_SOURCE: " + zigBeePowerDescriptor.isConstantMainsPowerAvailable());

			assertEquals("Availability of power source not matched",
					powerDescriptorConfig.isConstantMainsPowerAvailable(),
					zigBeePowerDescriptor.isConstantMainsPowerAvailable());

			p = node.getPowerDescriptor();
			waitForPromise(p);

			zigBeePowerDescriptor = (ZigBeePowerDescriptor) p.getValue();
			assertNotNull("power descriptor is NULL", zigBeePowerDescriptor);
			log("ZigBeeNode CURRENT_POWER_SOURCE_LEVEL: " + zigBeePowerDescriptor.getCurrentPowerSourceLevel());
			assertEquals("Current power source not matched", powerDescriptorConfig.getCurrentPowerSourceLevel(), zigBeePowerDescriptor.getCurrentPowerSourceLevel());

			p = node.getNodeDescriptor();
			waitForPromise(p);

			zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) p.getValue();
			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
			if (zigBeeNodeDescriptor.isComplexDescriptorAvailable()) {

				p = node.getComplexDescriptor();
				ZigBeeComplexDescriptor complexDescConf = nodeConfig.getComplexDescriptor();
				waitForPromise(p);

				ZigBeeComplexDescriptor zigBeeComplexDescriptor = (ZigBeeComplexDescriptor) p.getValue();
				assertNotNull("complex descriptor is NULL", zigBeeComplexDescriptor);

				log("ZigBeeNode MODEL_NAME: " + zigBeeComplexDescriptor.getModelName());

				assertEquals("Model name not matched", complexDescConf.getModelName(), zigBeeComplexDescriptor.getModelName());

				log("ZigBeeNode SERIAL_NAME: " + zigBeeComplexDescriptor.getSerialNumber());
				assertEquals("Serial name not matched", complexDescConf.getSerialNumber(), zigBeeComplexDescriptor.getSerialNumber());

				log("ZigBeeNode NODE_URL: " + zigBeeComplexDescriptor.getDeviceURL());
				assertEquals("Node url not matched", complexDescConf.getDeviceURL(), zigBeeComplexDescriptor.getDeviceURL());

			}

		} catch (ZCLException e) {
			e.printStackTrace();
		}
	}

	private void waitForPromise(Promise p) throws InterruptedException {
		long start = System.currentTimeMillis();
		while ((System.currentTimeMillis() - start) < HANDLER_TIMEOUT) {
			if (p.isDone()) {
				break;
			} else {
				Thread.sleep(500);
			}
		}

		if (!p.isDone()) {
			throw new RuntimeException("The promise did not resolve in the time expected");
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

			e.printStackTrace();

		} catch (InvalidSyntaxException e1) {

			e1.printStackTrace();
		}
		return endpoint;
	}

	private ZCLCluster getClusterById(ZigBeeEndpoint endpoint, int id) {

		ZCLCluster[] clusters = endpoint.getServerClusters();

		for (int i = 0; i < clusters.length; i++) {

			if (clusters[i].getId() == id) {
				return clusters[i];
			}
		}
		clusters = endpoint.getClientClusters();
		for (int j = 0; j < clusters.length; j++) {

			if (clusters[j].getId() == id) {
				return clusters[j];
			}
		}

		return null;
	}

	/**
	 * 
	 * @param endpointIeeeAddress
	 * @return the number of registered endpoints for the given IEEE address
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
			e.printStackTrace();
		} catch (InvalidSyntaxException e1) {
			e1.printStackTrace();
		}
		return node;
	}

	/**
	 * Wait till a ZigBeeHost service with the passed
	 * {@link ZigBeeNode.IEEE_ADDRESS} property equal to the passed one is
	 * registered in the OSGi framework.
	 * 
	 * @param nodeIeeeAddress The IEEE address of the ZigBeeHost service to look
	 *        for.
	 * @return The service object of the ZigBeeHost.
	 */

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
			e.printStackTrace();
		} catch (InvalidSyntaxException e1) {
			e1.printStackTrace();
		}
		return host;
	}

	/**
	 * Tests related to Endpoint Discovery.
	 */
	public void testEndpointDiscovery() throws Exception {
		log("---- testEndpointDiscovery");

		// get the endpoint values in the conf file
		ZigBeeNodeConfig node = conf.getNode0();
		ZigBeeEndpointConfig endpointConf = conf.getEnpoints(node)[0];
		ZigBeeSimpleDescriptor simpleDescConf = endpointConf.getSimpleDescriptor();

		ZigBeeEndpoint endpoint = getZigBeeEndpoint(node.getIEEEAddress());
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);
		log("ZigBeeEndpoint ENDPOINT: " + endpoint.getId());

		assertEquals("Endpoint identifier not matched", endpointConf.getId(), endpoint.getId());

		try {
			Promise p = endpoint.getSimpleDescriptor();
			waitForPromise(p);
			ZigBeeSimpleDescriptor zigBeeSimpleDescriptor = (ZigBeeSimpleDescriptor) p.getValue();
			assertNotNull("simple descriptor is NULL", zigBeeSimpleDescriptor);
			log("ZigBeeEndpoint PROFILE_ID: " + zigBeeSimpleDescriptor.getApplicationProfileId());
			assertEquals("Application Profile identifier not matched",
					simpleDescConf.getApplicationProfileId(),
					zigBeeSimpleDescriptor.getApplicationProfileId());

			p = endpoint.getSimpleDescriptor();
			waitForPromise(p);
			zigBeeSimpleDescriptor = (ZigBeeSimpleDescriptor) p.getValue();
			assertNotNull("simple descriptor is NULL", zigBeeSimpleDescriptor);
			log("ZigBeeEndpoint DEVICE_ID: " + zigBeeSimpleDescriptor.getApplicationDeviceId());
			assertEquals("Application Device identifier not matched",
					String.valueOf(simpleDescConf.getApplicationDeviceId()),
					String.valueOf(zigBeeSimpleDescriptor.getApplicationDeviceId()));

			p = endpoint.getSimpleDescriptor();
			waitForPromise(p);
			zigBeeSimpleDescriptor = (ZigBeeSimpleDescriptor) p.getValue();
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

		ZigBeeNodeConfig node = conf.getNode0();

		ZigBeeEndpointConfig endpointConf = conf.getEnpoints(node)[0];
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
	}

	/**
	 * Tests related to Command Description.
	 */
	public void testCommandDescription() throws Exception {
		log("---- testCommandDescription");

		// get the endpoint values in the conf file
		ZigBeeNodeConfig node = conf.getNode0();

		ZigBeeEndpointConfig endpointConf = conf.getEnpoints(node)[0];

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

		Promise p = cluster.getCommandIds();
		waitForPromise(p);
		int commandId = ((int[]) (p.getValue()))[0];
		log("ZCLCommand ID: " + commandId);

		assertEquals("Command identifier not matched",
				endpointConf.getServerClusters()[0].getCommandIds()[0],
				commandId);
	}

	/**
	 * Tests related to Attribute Description.
	 */
	public void testAttributeDescription() throws Exception {
		log("---- testAttributeDescription");

		// get the endpoint values in the conf file
		ZigBeeNodeConfig node = conf.getNode0();

		ZigBeeEndpointConfig endpointConf = conf.getEnpoints(node)[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpoint(node.getIEEEAddress());
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);
		log("ZigBeeEndpoint ENDPOINT: " + endpoint.getId());
		assertEquals("Endpoint identifier not matched", endpointConf.getId(), endpoint.getId());
		ZCLClusterConfig[] clustersConf = endpointConf.getServerClusters();
		ZCLClusterConfig clusterConf = clustersConf[0];
		ZCLAttribute attributeConf = clusterConf.getAttributes()[0];

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
		Promise p = cluster.getAttributes();
		waitForPromise(p);
		ZCLAttribute attribute = ((ZCLAttribute[]) p.getValue())[0];
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
	public void testControl() throws Exception {
		log("---- testControl");

		// get the endpoint values in the conf file
		ZigBeeNodeConfig node = conf.getNode0();

		ZigBeeEndpointConfig endpointConf = conf.getEnpoints(node)[0];

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
		final int attrId = endpointConf.getServerClusters()[0].getAttributes()[0].getId();
		ZCLAttributeInfo[] zclAttributeInfos = {new ZCLAttributeInfo() {
			public boolean isManufacturerSpecific() {
				return false;
			}

			public int getManufacturerCode() {
				return -1;
			}

			public int getId() {
				return attrId;
			}

			public ZCLDataTypeDescription getDataType() {
				return null;
			}
		}};

		boolean isSuccess;

		/*
		 * tests ZCLCluster.readAttributes()
		 */
		Promise p = cluster.readAttributes(zclAttributeInfos);
		waitForPromise(p);

		isSuccess = p.getFailure() == null;
		assertNotNull("handlerCluster response is NULL", endpoint);
		assertTrue("isSuccess is expected not to be false. ", isSuccess);
		log("handlerCluster.getSuccessResponse(): " + p.getValue());

		/*
		 * tests ZCLCluster.writeAttributes()
		 */
		boolean undivided = true;
		Map attributesIdsAndValues = null;
		p = cluster.writeAttributes(undivided, attributesIdsAndValues);
		waitForPromise(p);
		isSuccess = p.getFailure() == null;
		assertNotNull("handlerCluster response is NULL", endpoint);
		assertTrue("isSuccess is expected not to be false.", isSuccess);
		log("handlerCluster.getSuccessResponse(): " + p.getValue());

		/*
		 * tests ZCLCluster.getAttributes()
		 */
		p = cluster.getAttributes();
		waitForPromise(p);
		ZCLAttribute[] attributes = (ZCLAttribute[]) p.getValue();
		log("attributes: " + attributes);

		ZCLAttribute attribute = attributes[0];

		try {
			p = attribute.getValue();
			waitForPromise(p);
			isSuccess = p.getFailure() == null;
			assertNotNull("handlerAttribute response is NULL", endpoint);
			assertTrue("isSuccess is expected not to be false.", isSuccess);
			log("handlerAttributeGetValue1.getSuccessResponse(): " + p.getValue());

		} catch (ZCLException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}

		/*
		 * tests ZCLCluster.getCommandIds()
		 */
		p = cluster.getCommandIds();
		waitForPromise(p);
		int[] commandIds = (int[]) (p.getValue());
		assertNotSame("ZigBeeCluster has no command",
				Integer.valueOf("0"),
				Integer.valueOf(Integer.toString(commandIds.length)));
		int commandId = commandIds[0];
		log("DEBUG: commandId: " + commandId);

		ZCLFrame frame = new TestZCLFrame(conf.getRequestHeader(),
				conf.getRequestFullFrame());
		try {
			p = cluster.invoke(frame);
			waitForPromise(p);
			ZCLFrame frameResponse = (ZCLFrame) p.getValue();
			log("commandHandlerImpl.getResponse(): "
					+ frameResponse);
			assertTrue(
					"the response frame is not the one expected",
					Arrays.equals(conf.getResponseFullFrame(),
							frameResponse.getBytes()));
			if (!conf.getRequestHeader().isDefaultResponseDisabled()) {
				assertNotNull("Response is NULL", frameResponse);
				assertTrue(Arrays.equals(frameResponse.getBytes(), conf.getResponseFullFrame()));
			}
		} catch (ZCLException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}

		frame = null;
		String exportedServicePID = null;
		try {
			p = cluster.invoke(frame, exportedServicePID);
			waitForPromise(p);
			ZCLFrame response = (ZCLFrame) p.getValue();
			log("commandHandlerImpl.getResponse(): " + response);
		} catch (ZCLException e) {
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
		log("---- testEventing");

		// get the endpoint values in the conf file
		ZigBeeNodeConfig node = conf.getNode0();

		ZigBeeEndpointConfig endpointConf = conf.getEnpoints(node)[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpoint(node.getIEEEAddress());
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);
		// clusters
		ZCLCluster[] clusters = endpoint.getServerClusters();
		ZCLCluster cluster = null;
		if (clusters != null && clusters.length != 0) {
			cluster = clusters[0];
		}
		assertNotNull("ZigBeeCluster is NULL", cluster);

		NetworkAttributeIds attrIds = conf.getFirstReportableAttribute();
		// create, and launch a test event listener.
		ZCLEventListenerImpl aZCLEventListenerImpl = new ZCLEventListenerImpl(getContext());
		Dictionary properties = new Properties();
		properties.put(ZCLEventListener.MAX_REPORT_INTERVAL, new Integer(3));
		aZCLEventListenerImpl.start(properties);

		TestStepProxy testStep = launcher.getTeststepProxy();

		testStep.execute(TestStepLauncher.EVENT_REPORTABLE, "ensure that the first device with a reportable attribute defined in the configuration file is plugged and press [ENTER]");
		/*
		 * asserts that eventing works: the sent, and the received events must
		 * be equal.
		 */
		try {
			/*
			 * It takes several seconds for the event to "travel" inside the
			 * test framework.
			 */
			int sleepinms = DISCOVERY_TIMEOUT;
			log("Thread.sleep(" + sleepinms + ")");
			Thread.sleep(sleepinms);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("No exception is expected.");
		}

		ZigBeeEvent lastReceivedZigBeeEvent = aZCLEventListenerImpl.getLastReceivedZigBeeEvent();

		log("lastReceivedZigBeeEvent: " + lastReceivedZigBeeEvent);
		assertNotNull("aZigbeeEvent can not be null", lastReceivedZigBeeEvent);
		log("aZigbeeEvent: " + lastReceivedZigBeeEvent);

		assertEquals(lastReceivedZigBeeEvent.getIEEEAddress(), attrIds.getIeeeAddresss());
		assertEquals(lastReceivedZigBeeEvent.getClusterId(), attrIds.getClusterId());
		assertEquals(lastReceivedZigBeeEvent.getAttributeId(), attrIds.getAttributeId());
		// stop/destroy the test event listener.
		aZCLEventListenerImpl.stop();
	}

	// ====================================================================
	// ===========================EXCEPTIONS TEST==========================
	// ===========================METHODS==================================
	// ====================================================================

	public void testGeneralCommandExceptions() throws Exception {

		log("---- testExceptions");

		NetworkAttributeIds attrIds = conf.getUnsuportedAttribute();

		// get the endpoint values in the conf file
		ZigBeeNodeConfig node = conf.getNode0();

		ZigBeeEndpointConfig endpointConf = conf.getEnpoints(node)[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpoint(attrIds.getIeeeAddresss());
		assertNotNull("ZigBeeEndpoint is NULL", endpoint);

		assertNotNull("ZigBeeEndpoint is NULL", endpoint);
		log("ZigBeeEndpoint ENDPOINT: " + endpoint.getId());

		ZCLCluster cluster = getClusterById(endpoint, attrIds.getClusterId());

		final int invalidId = attrIds.getAttributeId();

		ZCLAttributeInfo[] zclAttributeInfos = {new ZCLAttributeInfo() {

			public boolean isManufacturerSpecific() {
				return false;
			}

			public int getManufacturerCode() {
				return 0;
			}

			public int getId() {
				return invalidId;
			}

			public ZCLDataTypeDescription getDataType() {

				ZCLDataTypeDescription dataType = new ZCLDataTypeDescription() {

					public boolean isAnalog() {
						return false;
					}

					public String getName() {
						return "DeviceEnabled";
					}

					public Class getJavaDataType() {
						return ZigBeeBoolean.class;
					}

					public short getId() {
						return 0;
					}
				};
				return dataType;
			}
		}};

		Promise p = cluster.readAttributes(zclAttributeInfos);
		waitForPromise(p);

		assertNotNull("The response is successfull. BUT a failure is expected in this test case reading a invalid attribute.", p.getFailure());

		assertTrue("The exception is not a ZCL exception", p.getFailure() instanceof ZCLException);

		assertEquals("The ZCL exception is not an unsupported attribute exception",
				ZCLException.UNSUPPORTED_ATTRIBUTE,
				((ZCLException) p.getFailure()).getErrorCode());

		// DATATYPE EXCEPTION

		ZCLAttribute booleanDatatypeAttr = conf.getFirstAttributeWithBooleanDatatype();
		assertNotNull("No attribute found with the Boolean datatype, please modify the configuration file", booleanDatatypeAttr);

		p = cluster.getAttribute(booleanDatatypeAttr.getId());
		waitForPromise(p);
		ZCLAttribute attr = (ZCLAttribute) p.getValue();
		p = attr.setValue(new Float(4));
		waitForPromise(p);
		assertNotNull("The response was succesfull. a failure is expected in this case testing the Invalid data type.", p.getFailure());

		assertTrue("The exception is not a ZCL exception", p.getFailure() instanceof ZCLException);
		assertEquals("The ZCL exception is not an Invalid data type exception",
				ZCLException.INVALID_DATA_TYPE,
				((ZCLException) p.getFailure()).getErrorCode());

		// READ ONLY EXCEPTION
		attrIds = conf.getFirstReadOnlyAttribute();
		endpoint = getZigBeeEndpoint(attrIds.getIeeeAddresss());
		cluster = getClusterById(endpoint, attrIds.getClusterId());

		p = cluster.getAttribute(attrIds.getAttributeId()); // mandatory
		waitForPromise(p);
		// unsigned
		// bit int

		attr = (ZCLAttribute) p.getValue();
		p = attr.getValue();
		waitForPromise(p);
		Object attrValue = p.getValue();

		// set the value with what has been read to avoid range /type problems
		p = attr.setValue(attrValue);
		waitForPromise(p);

		assertNotNull("a failure is expected", p.getFailure());

		assertTrue("The exception is not a ZCL exception as expected in this case testing the read-only exception.", p.getFailure() instanceof ZCLException);
		assertEquals("could set a value tagged as read only in the description",
				ZCLException.READ_ONLY,
				((ZCLException) p.getFailure()).getErrorCode());

	}

	private int getInvalidId(ZCLCluster clusterConf) throws Exception {

		ZCLAttribute[] attributesConf = (ZCLAttribute[]) clusterConf.getAttributes().getValue();

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

	/**
	 * This test try to verify if the base driver is correctly calling an
	 * exported endpoint when it is detected, if this exported endpoint is not
	 * suitable to be exported. It may happen because:
	 * <ul>
	 * <li>The ZigBeeEndpoint do not have the mandatory service properties set.
	 * <li>The new ZigBeeEndpoint has the same
	 * {@link ZigBeeEndpoint#ENDPOINT_ID} and the same
	 * {@link ZigBeeNode#IEEE_ADDRESS} service properties values of another
	 * endpoint.
	 * <li>The new ZigBeeEndpoint do not have the
	 * {@link ZigBeeEndpoint#ENDPOINT_ID} and {@link ZigBeeNode#IEEE_ADDRESS}
	 * </ul>
	 * 
	 * The above checks may be issued by the base driver because it have a
	 * complete overview of the services that it has registered and, for
	 * instance, it can figure out if a {@code ZigBeeEndpoint} is a valid one or
	 * one that has been registered by an application with the purpose to be
	 * exported, but that do not have the {@link ZigBeeEndpoint#ZIGBEE_EXPORT}
	 * property set.
	 */

	public void testExport() {
		log("---- testExport");

		BundleContext bc = getContext();

		BigInteger hostIeeeAddresss = conf.getZigBeeHost().getIEEEAddress();

		ZigBeeHost host = this.getZigBeeHost(hostIeeeAddresss);

		ZigBeeNodeConfig nodeConfig = conf.getNode0();

		/*
		 * The exported endpoint identifier cannot be a broadcast endpoint.
		 */
		short broadcastEndpointId = 0xff;
		this.registerInvalidEndpointId(bc, host, nodeConfig, broadcastEndpointId);

		/*
		 * The exported endpoint identifier must be in the range [0, 0xff)
		 */
		this.registerInvalidEndpointId(bc, host, nodeConfig, (short) 300);

		this.registerInvalidEndpointId(bc, host, nodeConfig, (short) -10);
	}

	/**
	 * Used to verify if the RI is calling the appropriate notExported()
	 * callback if a ZigBeeEndpoint with wrong properties is exported.
	 * 
	 * @param bc The BundleContext
	 * @param host The ZigBeeHost where to export the ZigBeeEndpoint.
	 * @param invalidEndpointId An invalid value of the endpoint identifier.
	 */

	private void registerInvalidEndpointId(BundleContext bc, ZigBeeHost host, ZigBeeNodeConfig nodeConfig, short invalidEndpointId) {

		/**
		 * Try to export an invalid ZigBeeEnpoint, by setting invalid values of
		 * the service properties. The invalid endpoint is configured with some
		 * ZCLClusters that are the exact couterpart of the first node found in
		 * the xml file provided by the user.
		 */

		ZigBeeEndpoint[] hostEndpoints = host.getEndpoints();

		assertNotNull("ZigBeeHost.getEndpoints() cannot return null", hostEndpoints);

		int initialHostEndpointsNumber = host.getEndpoints().length;

		ZigBeeEndpointConfig ep = nodeConfig.getEndpoints()[0];

		ZCLClusterConfig[] serverClustersConfig = ep.getServerClusters();
		ZCLClusterConfig[] clientClustersConfig = ep.getServerClusters();

		ZCLCluster[] serverClusters = new ZCLCluster[serverClustersConfig.length];
		ZCLCluster[] clientClusters = new ZCLCluster[clientClustersConfig.length];

		for (int i = 0; i < serverClusters.length; i++) {
			ZCLCluster cluster = new ZCLClusterImpl(serverClustersConfig[i]);
			serverClusters[i] = cluster;
		}

		for (int i = 0; i < clientClusters.length; i++) {
			ZCLCluster cluster = new ZCLClusterImpl(clientClustersConfig[i]);
			clientClusters[i] = cluster;
		}

		/**
		 * Creates a ZigBeeEndpoint swapping the client and server clusters and
		 * by using a wrong endpoint identifier (endpoint broadcast)
		 */

		ZigBeeEndpoint badEnpoint = new TestNotExportedZigBeeEndpoint(invalidEndpointId, clientClusters, serverClusters, ep.getSimpleDescriptor());

		Dictionary endpointProperties = new Properties();
		endpointProperties.put(ZigBeeNode.IEEE_ADDRESS, host.getIEEEAddress());
		endpointProperties.put(ZigBeeEndpoint.ENDPOINT_ID, String.valueOf(badEnpoint.getId()));
		endpointProperties.put(ZigBeeEndpoint.ZIGBEE_EXPORT, "exported");

		/*
		 * Register this ZigBeeEndpoint
		 */

		log("Register  endpoint: " + badEnpoint + " in the OSGi services registry");

		bc.registerService(ZigBeeEndpoint.class.getName(), badEnpoint, endpointProperties);

		try {
			/*
			 * It might takes time to register the service test framework.
			 */
			log("Waiting wait for a discovery timeout value of milliseconds = " + DISCOVERY_TIMEOUT);
			Thread.sleep(DISCOVERY_TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("No exception is expected during sleep!");
		}

		TestNotExportedZigBeeEndpoint t = (TestNotExportedZigBeeEndpoint) badEnpoint;

		ZigBeeException notExportedException = t.getNotExportedException();

		if (notExportedException == null) {
			fail("method ZigBeeEndpoint.notExported() should have been called within the discovery timeout, as expected.");
		} else if (notExportedException instanceof ZDPException) {
			assertEquals("method ZigBeeEndpoint.notExported() was called with the wrong exception error code",
					ZDPException.INVALID_EP,
					notExportedException.getErrorCode());
		} else {
			fail("the ZigBeeEndpoint.notExported() method has been called with the wrong exception (it should be a ZDPException.INVALID_EP.");
		}

		int newNb = host.getEndpoints().length;
		assertEquals("The number of enpoints returned by the ZigBeeHost.getEnpoints() is changed desipite the ZigBee endpoint is wrong.", newNb, initialHostEndpointsNumber);

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

	public void testHost() throws Exception {
		log("---- testHost");

		boolean isCoordinator = false;
		BigInteger hostieeeAddress = conf.getZigBeeHost().getIEEEAddress();
		ZigBeeHost host = getZigBeeHost(hostieeeAddress);
		Promise p = host.getNodeDescriptor();
		waitForPromise(p);
		ZigBeeNodeDescriptor zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) p.getValue();
		assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
		if (zigBeeNodeDescriptor.getLogicalType() == ZigBeeNode.COORDINATOR) {
			isCoordinator = true;
		}

		// logical type test
		BundleContext bc = getContext();

		ServiceTracker st = new ServiceTracker(
				bc,
				ZigBeeNode.class.getName(),
				null);
		st.open();
		Object[] services = st.getServices();
		for (int i = 0; i < services.length; i++) {
			ZigBeeNode node = (ZigBeeNode) services[i];
			p = node.getNodeDescriptor();
			waitForPromise(p);
			zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) p.getValue();
			assertNotNull("the node descriptor shouldn't be null",
					zigBeeNodeDescriptor);
			assertNotNull("the logical type shouldn't be null", new Short(
					zigBeeNodeDescriptor.getLogicalType()));
			if (zigBeeNodeDescriptor.getLogicalType() == ZigBeeNode.COORDINATOR) {
				isCoordinator = true;
			}
		}

		// coordinator test
		assertTrue("there must be at least one coordinator", isCoordinator);

	}

}
