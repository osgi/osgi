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
import org.osgi.test.cases.zigbee.config.file.NetworkAttributeIds;
import org.osgi.test.cases.zigbee.mock.ZCLAttributeImpl;
import org.osgi.test.cases.zigbee.mock.ZCLClusterConf;
import org.osgi.test.cases.zigbee.mock.ZCLEventListenerImpl;
import org.osgi.test.cases.zigbee.mock.ZigBeeEndpointConf;
import org.osgi.test.cases.zigbee.mock.ZigBeeEndpointImpl;
import org.osgi.test.cases.zigbee.mock.ZigBeeNodeConf;
import org.osgi.test.cases.zigbee.mock.ZigBeeNodeImpl;
import org.osgi.test.cases.zigbee.mock.ZigBeeTestOSGiIdEndpointImpl;
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

	private ServicesListener	listener;
	private TestStepLauncher	launcher;
	public static int			HANDLER_TIMEOUT		= 3000;
	public static int			DISCOVERY_TIMEOUT	= 3000;
	private static final String	EVENT_REPORTABLE	= "event reportable";

	ConfigurationFileReader		conf;

	private String				confFilePath		= "zigbee-template.xml";

	protected void setUp() throws Exception {
		log("Prepare for ZigBee Test Case");

		prepareTestStart();
		log("Prepared for ZigBee Test Case");
	}

	protected void tearDown() throws Exception {
		log("Tear down ZigBee Test Case");
	}

	private void prepareTestStart() throws Exception {
		launcher = TestStepLauncher.launch(confFilePath, getContext());
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
	public void testNodeDiscovery() throws Exception {
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

			Promise p = dev.getUserDescription();
			waitForPromise(p);

			String userDescription = (String) p.getValue();
			assertNotNull("user description is NULL", userDescription);
			assertEquals("user description not matched", userConfDescription, userDescription);

			ZigBeeNodeDescriptor zigBeeNodeDescriptor;
			ZigBeeNodeDescriptor nodeDescConf = (ZigBeeNodeDescriptor) devConf.getNodeDescriptor().getValue();
			p = dev.getNodeDescriptor();
			waitForPromise(p);
			zigBeeNodeDescriptor = (org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor) p.getValue();
			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode LOGICAL_TYPE: " + zigBeeNodeDescriptor.getLogicalType());
			assertEquals("Logical type not matched",
					nodeDescConf.getLogicalType(),
					zigBeeNodeDescriptor.getLogicalType());

			p = dev.getNodeDescriptor();
			waitForPromise(p);
			zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) p.getValue();
			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode MANUFACTURER_CODE: " + zigBeeNodeDescriptor.getManufacturerCode());
			assertEquals("Manufacturer code not matched",
					nodeDescConf.getManufacturerCode(),
					zigBeeNodeDescriptor.getManufacturerCode());

			p = dev.getNodeDescriptor();
			waitForPromise(p);
			zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) p.getValue();
			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode MAXIMUM_BUFFER_SIZE: " + String.valueOf(zigBeeNodeDescriptor.getMaxBufferSize()));
			assertEquals("Maximum buffer size not matched",
					nodeDescConf.getMaxBufferSize(),
					zigBeeNodeDescriptor.getMaxBufferSize());

			p = dev.getNodeDescriptor();
			waitForPromise(p);
			zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) p.getValue();
			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode MAXIMUM_INCOMING_TRANSFERT_SIZE: " + zigBeeNodeDescriptor.getMaxIncomingTransferSize());
			assertEquals("Maximum incoming transfert size not matched",
					nodeDescConf.getMaxIncomingTransferSize(),
					zigBeeNodeDescriptor.getMaxIncomingTransferSize());

			ZigBeePowerDescriptor zigBeePowerDescriptor;
			ZigBeePowerDescriptor PowerDescConf = (ZigBeePowerDescriptor) devConf.getPowerDescriptor().getValue();
			p = dev.getPowerDescriptor();
			waitForPromise(p);
			zigBeePowerDescriptor = (ZigBeePowerDescriptor) p.getValue();
			assertNotNull("power descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode CURRENT_POWER_MODE: " + zigBeePowerDescriptor.getCurrentPowerMode());
			assertEquals("Current power mode not matched",
					PowerDescConf.getCurrentPowerMode(),
					zigBeePowerDescriptor.getCurrentPowerMode());

			p = dev.getPowerDescriptor();
			waitForPromise(p);
			zigBeePowerDescriptor = (ZigBeePowerDescriptor) p.getValue();
			assertNotNull("power descriptor is NULL", zigBeeNodeDescriptor);
			log("ZigBeeNode CURRENT_POWER_SOURCE: " + zigBeePowerDescriptor.getCurrentPowerSource());
			assertEquals("Current power source not matched",
					PowerDescConf.getCurrentPowerSource(),
					zigBeePowerDescriptor.getCurrentPowerSource());

			p = dev.getPowerDescriptor();
			waitForPromise(p);
			zigBeePowerDescriptor = (ZigBeePowerDescriptor) p.getValue();
			assertNotNull("power descriptor is NULL", zigBeePowerDescriptor);
			log("ZigBeeNode AVAILABLE_POWER_SOURCE: " + zigBeePowerDescriptor.isConstantMainsPowerAvailable());
			assertEquals("Availability of power source not matched",
					PowerDescConf.isConstantMainsPowerAvailable(),
					zigBeePowerDescriptor.isConstantMainsPowerAvailable());

			p = dev.getPowerDescriptor();
			waitForPromise(p);

			zigBeePowerDescriptor = (ZigBeePowerDescriptor) p.getValue();
			assertNotNull("power descriptor is NULL", zigBeePowerDescriptor);
			log("ZigBeeNode CURRENT_POWER_SOURCE_LEVEL: " + zigBeePowerDescriptor.getCurrentPowerSourceLevel());
			assertEquals("Current power source not matched",
					PowerDescConf.getCurrentPowerSourceLevel(),
					zigBeePowerDescriptor.getCurrentPowerSourceLevel());

			p = dev.getNodeDescriptor();
			waitForPromise(p);

			zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) p.getValue();
			assertNotNull("node descriptor is NULL", zigBeeNodeDescriptor);
			if (zigBeeNodeDescriptor.isComplexDescriptorAvailable()) {

				p = dev.getComplexDescriptor();
				ZigBeeComplexDescriptor complexDescConf = (ZigBeeComplexDescriptor) devConf.getComplexDescriptor().getValue();
				waitForPromise(p);

				ZigBeeComplexDescriptor zigBeeComplexDescriptor = (ZigBeeComplexDescriptor) p.getValue();
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
		ZigBeeNodeImpl node = conf.getNode0();
		ZigBeeEndpointConf endpointConf = (ZigBeeEndpointConf) conf.getEnpoints(node)[0];
		ZigBeeSimpleDescriptor simpleDescConf = (ZigBeeSimpleDescriptor) endpointConf.getSimpleDescriptor().getValue();

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

	}

	/**
	 * Tests related to Command Description.
	 */
	public void testCommandDescription() throws Exception {
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

		Promise p = cluster.getCommandIds();
		waitForPromise(p);
		int commandId = ((int[]) (p.getValue()))[0];
		log("ZCLCommand ID: " + commandId);
		// assertNotNull("ZCLCommand ID is NULL", commandId);
		assertEquals("Command identifier not matched",
				((int[]) ((ZCLClusterConf) endpointConf.getServerClusters()[0]).getCommandIds().getValue())[0],
				commandId);
	}

	/**
	 * Tests related to Attribute Description.
	 */
	public void testAttributeDescription() throws Exception {
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
		ZCLAttribute attributeConf = ((ZCLAttribute[]) ((ZCLClusterConf) clusterConf).getAttributes().getValue())[0];

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
		final int attrId = ((ZCLAttribute[]) ((ZCLClusterConf) endpointConf.getServerClusters()[0])
				.getAttributes().getValue())[0].getId();
		ZCLAttributeInfo[] zclAttributeInfos = {new ZCLAttributeInfo() {

			public boolean isManufacturerSpecific() {
				return false;
			}

			public int getManufacturerCode() {
				return 0;
			}

			public int getId() {

				return attrId;
			}

			public ZCLDataTypeDescription getDataType() {

				return null;
			}
		}};
		boolean isSuccess;

		// cluster.readAttributes
		Promise p = cluster.readAttributes(zclAttributeInfos);
		waitForPromise(p);

		isSuccess = p.getFailure() == null;
		assertNotNull("handlerCluster response is NULL", endpoint);
		assertTrue("isSuccess is expected not to be false. ", isSuccess);
		log("handlerCluster.getSuccessResponse(): " + p.getValue());

		// cluster.writeAttributes(undivided, attributesRecords,
		// handlerCluster);
		boolean undivided = true;
		Map attributesIdsAndValues = null;
		p = cluster.writeAttributes(undivided, attributesIdsAndValues);
		waitForPromise(p);
		isSuccess = p.getFailure() == null;
		assertNotNull("handlerCluster response is NULL", endpoint);
		assertTrue("isSuccess is expected not to be false.", isSuccess);
		log("handlerCluster.getSuccessResponse(): " + p.getValue());

		// Test "control" methods of ZCLAttribute.

		// attributes
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

		// Test "control" methods of ZigBeeCluster.

		// cluster
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

		NetworkAttributeIds attrIds = conf.getFirstReportableAttribute();
		// create, and launch a test event listener.
		ZCLEventListenerImpl aZCLEventListenerImpl = new ZCLEventListenerImpl(getContext());
		Dictionary properties = new Properties();
		properties.put(ZCLEventListener.MAX_REPORT_INTERVAL, new Integer(3));
		aZCLEventListenerImpl.start(properties);

		TestStepProxy testStep = launcher.getTeststepProxy();

		testStep.execute(EVENT_REPORTABLE, "ensure that the first device with a reportable attribute defined in the configuration file is plugged and press [ENTER]");
		// assert that eventing works: the sent, and the received events must be
		// equal.
		try {
			// It takes several seconds for the event to "travel" inside the
			// test framework.
			int sleepinms = DISCOVERY_TIMEOUT;
			log("Thread.sleep(" + sleepinms + ")");
			Thread.sleep(sleepinms);
		} catch (

		InterruptedException e)

		{
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
		ZigBeeNodeImpl node = conf.getNode0();

		ZigBeeEndpointConf endpointConf = (ZigBeeEndpointConf) conf.getEnpoints(node)[0];

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
		assertNotNull("No attribute found with the Boolean datatype, please modify the configuration file",
				booleanDatatypeAttr);

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

	private int getInvalidId(ZCLClusterConf clusterConf) throws Exception {

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