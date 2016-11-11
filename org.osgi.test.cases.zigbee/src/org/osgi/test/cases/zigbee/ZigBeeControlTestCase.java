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
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.osgi.test.cases.zigbee.config.file.AttributeCoordinates;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;
import org.osgi.test.cases.zigbee.config.file.ZigBeeEndpointConfig;
import org.osgi.test.cases.zigbee.config.file.ZigBeeNodeConfig;
import org.osgi.test.cases.zigbee.mock.ZCLEventListenerImpl;
import org.osgi.test.support.step.TestStepProxy;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Contain the ZigBee testcases.
 * 
 * @author $Id$
 */
public class ZigBeeControlTestCase extends ZigBeeTestCases {

	private static final String	TAG	= ZigBeeControlTestCase.class.getName();

	private TestStepLauncher	launcher;

	/**
	 * Timeout used for the timing out all the methods belonging to the
	 * ZigBeeHost, ZigBeeNode, ZigBeeEndpoint, ZigBeeCluster interfaces, that
	 * are also returning a Promise.
	 */
	public static int			INVOKE_TIMEOUT;;

	/**
	 * Timeout used to wait for a ZigBeeNode or a ZigBeeEndpoint to be seen in
	 * the OSGi framework as a service. This constant is read from the
	 * configuration file provided by the RI.
	 */
	public static int			DISCOVERY_TIMEOUT;

	ConfigurationFileReader		conf;

	protected void setUp() throws Exception {
		log(TAG, "Prepare for ZigBee Test Case");

		prepareTestStart();
		log(TAG, "Prepared for ZigBee Test Case");
	}

	protected void tearDown() throws Exception {
		log(TAG, "Tear down ZigBee Test Case");
	}

	private void prepareTestStart() throws Exception {
		launcher = TestStepLauncher.launch(getContext());
		conf = launcher.getConfReader();

		/*
		 * Initialize timeout constants relevant for the CT with the values read
		 * from the ZigBee configuration file.
		 */
		INVOKE_TIMEOUT = conf.getInvokeTimeout();
		DISCOVERY_TIMEOUT = conf.getDiscoveryTimeout();
	}

	/**
	 * Tests related to Node Discovery. We expect that a ZigBeeNode service
	 * configured in the ZigBee CT configuration file is registered within a
	 * specified timeout. Some checks about the ZigBeeNode service properties
	 * are performed as well.
	 * 
	 * @throws Exception In case of failure of the this test case.
	 * 
	 */
	public void testNodeDiscovery() throws Exception {
		log(TAG, "testNodeDiscovery");

		// get the endpoint values in the conf file
		ZigBeeNodeConfig nodeConfig = conf.getFirstNode();

		ZigBeeEndpointConfig endpointConf = conf.getEnpoints(nodeConfig)[0];

		ZigBeeNode node = waitForZigBeeNodeService(nodeConfig, DISCOVERY_TIMEOUT);

		/*
		 * The ZigBeeNode services comes along with the expected number of
		 * ZigBeeEndpoint services?
		 */
		int expectedZigBeeEndpointNumber = nodeConfig.getActualEndpointsNumber();

		int registeredZigBeeEndpointsNumber = getZigBeeEndpointsNumber(nodeConfig.getIEEEAddress());
		assertEquals(
				"The number of registered endpoints by the baseDriver is not the same as declared in the configuration file for the node with IEEE Address:"
						+ nodeConfig.getIEEEAddress(),
				expectedZigBeeEndpointNumber,
				registeredZigBeeEndpointsNumber);

		Promise p = node.getNodeDescriptor();
		waitForPromise(p, INVOKE_TIMEOUT);

		ZigBeeNodeDescriptor nodeDesc = null;

		if (p.getFailure() == null) {
			nodeDesc = (ZigBeeNodeDescriptor) assertPromiseValueClass(p, ZigBeeNodeDescriptor.class);
			assertNotNull("ZigBeeNodeDescriptor cannot be null", nodeDesc);

			this.checkNodeDescriptor(nodeConfig.getNodeDescriptor(), nodeDesc);
		} else {
			// FIXME: check for possible errors.
			fail("unable to retrieve the ZigBeeNodeDescriptor");
		}

		String expectedUserDesc = nodeConfig.getUserDescription();

		p = node.getUserDescription();
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			if (!nodeDesc.isUserDescriptorAvailable()) {
				fail("ZigBeeNodeDescriptor states that the UserDescription must not be available.");
			}

			String userDescription = (String) assertPromiseValueClass(p, String.class);

			// TODO: check if user description may be null.
			assertNotNull("ZigbeeNode.getUserDescription() cannot return null.", userDescription);
			assertEquals("user description mismatch", expectedUserDesc, userDescription);
		} else {
			// FIXME: check for possible errors.
		}

		p = node.getPowerDescriptor();
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			ZigBeePowerDescriptor powerDesc = (ZigBeePowerDescriptor) assertPromiseValueClass(p, ZigBeePowerDescriptor.class);
			assertNotNull("ZigBeePowerDescriptor cannot be null", powerDesc);

			/*
			 * Checks if the returned ZigBeePowerDescriptor matches the expected
			 * one.
			 */
			ZigBeePowerDescriptor expectedPowerDesc = nodeConfig.getPowerDescriptor();
			this.checkPowerDescriptor(expectedPowerDesc, powerDesc);
		} else {
			// FIXME: check for possible errors.
		}

		p = node.getPowerDescriptor();
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			if (!nodeDesc.isComplexDescriptorAvailable()) {
				fail("ZigBeeNodeDescriptor states that the ComplexDescriptor must not be available.");
			}

			ZigBeeComplexDescriptor complexDesc = (ZigBeeComplexDescriptor) assertPromiseValueClass(p, ZigBeeComplexDescriptor.class);
			assertNotNull("ZigBeeComplexDescriptor cannot be null", complexDesc);

			/*
			 * Checks if the returned ZigBeeComplexDescriptor matches the
			 * expected one.
			 */
			ZigBeeComplexDescriptor expectedComplexDesc = nodeConfig.getComplexDescriptor();
			this.checkComplexDescriptor(expectedComplexDesc, complexDesc);
		} else {
			// FIXME: check for possible errors.
		}
	}

	/**
	 * Retrieves the number of ZigBeeEndpoint services that matches the passed
	 * ZigBeeNode IEEE address.
	 * 
	 * @param ieeeAddress The IEEE address of interest.
	 * @return the number of registered endpoints for the given IEEE address
	 */
	private int getZigBeeEndpointsNumber(BigInteger ieeeAddress) {

		String ieeeAddressFilter = "(" + ZigBeeNode.IEEE_ADDRESS + "=" + ieeeAddress + ")";

		try {
			ServiceReference[] sRefs = getContext().getAllServiceReferences(ZigBeeEndpoint.class.getName(), ieeeAddressFilter);
			return sRefs.length;
		} catch (InvalidSyntaxException e) {
			fail("Internal error: filter expression is wrong", e);
		}
		return -1;
	}

	/**
	 * Tests if the ZigBeeSimple descriptor returned by the
	 * ZigBeeEndpoint.getSimpleDescriptor() is correctly implemented.
	 * 
	 * @throws Exception
	 */

	public void checkSimpleDescriptor(ZigBeeSimpleDescriptor expectedDescr, ZigBeeSimpleDescriptor desc) throws Exception {
		String context = "ZigBeeSimpleDescriptor";

		assertEquals(context + ": application Profile Identifier not matched", expectedDescr.getApplicationProfileId(), desc.getApplicationProfileId());
		assertEquals(context + ": application Device Identifier not matched", expectedDescr.getApplicationDeviceId(), desc.getApplicationDeviceId());
		assertEquals(context + ": application Device Version  not matched", expectedDescr.getApplicationDeviceVersion(), desc.getApplicationDeviceVersion());
		assertEquals(context + ": application endpointId not matched", expectedDescr.getEndpoint(), desc.getEndpoint());

		int[] serverClusters = desc.getInputClusters();
		int[] clientClusters = expectedDescr.getInputClusters();

		assertEquals(context + ": Wrong number of input clusters", expectedDescr.getInputClusters().length, serverClusters.length);
		assertEquals(context + ": Wrong number of output clusters", expectedDescr.getOutputClusters().length, clientClusters.length);

		/*
		 * Tests some specific methods of the ZigBeeSimpleDescriptor interface
		 * implementation
		 */

		int[] clusters = desc.getInputClusters();
		for (int i = 0; i < clusters.length; i++) {
			assertTrue(context + ": error in providesInputCluster() implementation.", desc.providesInputCluster(clusters[i]));
		}

		clusters = desc.getOutputClusters();
		for (int i = 0; i < clusters.length; i++) {
			assertTrue(context + ": error in providesOutputCluster() implementation.", desc.providesOutputCluster(clusters[i]));
		}
	}

	public void checkNodeDescriptor(ZigBeeNodeDescriptor expectedDesc, ZigBeeNodeDescriptor desc) throws Exception {
		/*
		 * Checks if the returned ZigBeeNodeDescriptor matches the expected one.
		 */

		assertEquals("ZigBeeNodeDescriptor User Descriptor Availability", expectedDesc.isUserDescriptorAvailable(), desc.isUserDescriptorAvailable());
		assertEquals("ZigBeeNodeDescriptor ComplexDescriptor Availability", expectedDesc.isComplexDescriptorAvailable(), desc.isComplexDescriptorAvailable());
		assertEquals("ZigBeeNodeDescriptor Logical Type", expectedDesc.getLogicalType(), desc.getLogicalType());
		assertEquals("ZigBeeNodeDescriptor Manufacturer Code", expectedDesc.getManufacturerCode(), desc.getManufacturerCode());
		assertEquals("ZigBeeNodeDescriptor Maximum Buffer Size", expectedDesc.getMaxBufferSize(), desc.getMaxBufferSize());
		assertEquals("ZigBeeNodeDescriptor Maximum Transfer Size", expectedDesc.getMaxIncomingTransferSize(), desc.getMaxIncomingTransferSize());
	}

	public void checkPowerDescriptor(ZigBeePowerDescriptor expectedDesc, ZigBeePowerDescriptor desc) throws Exception {
		assertEquals("ZigBeePowerDescriptor Power Mode", expectedDesc.getCurrentPowerMode(), desc.getCurrentPowerMode());
		assertEquals("ZigBeePowerDescriptor Power Source", expectedDesc.getCurrentPowerSource(), desc.getCurrentPowerSource());
		assertEquals("ZigBeePowerDescriptor Power Source Level", expectedDesc.getCurrentPowerSourceLevel(), desc.getCurrentPowerSourceLevel());
		assertEquals("ZigBeePowerDescriptor Mains Power Available", expectedDesc.isConstantMainsPowerAvailable(), desc.isConstantMainsPowerAvailable());
	}

	protected void checkComplexDescriptor(ZigBeeComplexDescriptor expectedDesc, ZigBeeComplexDescriptor desc) {

		// FIXME: check better this!
		assertEquals("ZigBeeComplexDescriptor Model Name", expectedDesc.getModelName(), desc.getModelName());
		assertEquals("ZigBeeComplexDescriptor Serial Number", expectedDesc.getSerialNumber(), desc.getSerialNumber());
		assertEquals("ZigBeeComplexDescriptor Device URL", expectedDesc.getDeviceURL(), desc.getDeviceURL());
	}

	/**
	 * Tests related to the discovery of the ZigBeeEndpoint services. The test
	 * will also check that the registered endpoint match the information
	 * retrieved from the CT configuration file.
	 * 
	 * TODO: check if it works.
	 * 
	 */
	public void testEndpointDiscovery() throws Exception {
		log(TAG, "testEndpointDiscovery");

		ZigBeeNodeConfig node = conf.getFirstNode();
		ZigBeeEndpointConfig expectedEndpoint = conf.getEnpoints(node)[0];
		ZigBeeSimpleDescriptor expectedSimpleDesc = expectedEndpoint.getSimpleDescriptor();

		ZigBeeEndpoint endpoint = getZigBeeEndpointService(expectedEndpoint);

		assertNotNull("ZigBeeEndpoint", endpoint);
		log(TAG, "ZigBeeEndpoint ENDPOINT: " + endpoint.getId());

		assertEquals("Endpoint identifier not matched", expectedEndpoint.getId(), endpoint.getId());

		/*
		 * Retrieve the ZigBeeEndpoint Simple Descriptor.
		 */

		String context = "ZigBeeEndpoint.getSimpleDescriptor()";

		Promise p = endpoint.getSimpleDescriptor();
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			ZigBeeSimpleDescriptor simpleDesc = (ZigBeeSimpleDescriptor) assertPromiseValueClass(p, ZigBeeSimpleDescriptor.class);
			assertNotNull(context + ": null value returned", simpleDesc);
			this.checkSimpleDescriptor(expectedEndpoint.getSimpleDescriptor(), simpleDesc);
		} else {
			// TODO: fail here?
		}
	}

	/**
	 * Tests methods available in the ZigBeeEndpoint interface:
	 * <ul>
	 * <li>ZigBeeEndpoint.getServerClusters(),
	 * <li>ZigBeeEndpoint.getServerCluster(clusterId)
	 * <li>ZigBeeEndpoint.getClientClusters(),
	 * <li>ZigBeeEndpoint.getClientCluster(clusterId)
	 * </ul>
	 */

	public void testZigBeeEndpointClusterMethods() {
		log(TAG, "---- testZigBeeEndpointClusterMethods()");

		ZigBeeNodeConfig expectedNode = conf.getFirstNode();
		ZigBeeEndpointConfig expectedEndpoint = expectedNode.getEndpoints()[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpointService(expectedEndpoint);

		assertNotNull("ZigBeeEndpoint", endpoint);

		/* Server clusters */
		this.testZigBeeEndpointClusterMethods(endpoint, expectedEndpoint, true);

		/* Client clusters */
		this.testZigBeeEndpointClusterMethods(endpoint, expectedEndpoint, false);

	}

	/**
	 * Tests methods available in the ZigBeeEndpoint interface:
	 * <ul>
	 * <li>ZigBeeEndpoint.get<side>Clusters(),
	 * <li>ZigBeeEndpoint.get<side>Cluster(clusterId)
	 * </ul>
	 * 
	 * where <side> may be 'Server' or 'Client' accroding to the isServerSide
	 * parameter.
	 * 
	 * @param isServerSide true if the test must be done on the 'Server' flavor
	 *        of the above methods, false if have to be done on the 'Client'
	 *        flavor.
	 */

	protected void testZigBeeEndpointClusterMethods(ZigBeeEndpoint endpoint, ZigBeeEndpointConfig expectedEndpoint, boolean isServerSide) {
		String context;

		/*
		 * Checks if the ZigBeeEndpoint.getServerClusters() returns a valid
		 * array of ZCLCluster instances.
		 */

		ZCLCluster[] clusters;
		ZCLClusterDescription[] expectedClusters;

		if (isServerSide) {
			clusters = endpoint.getServerClusters();
			expectedClusters = expectedEndpoint.getServerClusters();
			context = "ZigBeeEndpoint.getServerClusters()";

		} else {
			clusters = endpoint.getClientClusters();
			expectedClusters = expectedEndpoint.getClientClusters();
			context = "ZigBeeEndpoint.getClientClusters()";
		}

		assertNotNull(context + " cannot return null", clusters);
		assertEquals(context + " array size", expectedClusters.length, clusters.length);

		/*
		 * checks for null entries, duplicate entries and duplicate clusterIds.
		 * A Set is used to discover duplicates.
		 */
		Set clustersSet = new HashSet();
		SortedSet clusterIdsSet = new TreeSet();
		for (int i = 0; i < clusters.length; i++) {
			assertNotNull(context + " returned an array with null entries.", clusters[i]);
			clustersSet.add(clusters[i]);
			clusterIdsSet.add(new Integer(clusters[i].getId()));
		}

		if (clusters.length != clustersSet.size()) {
			fail(context + " returned an array with duplicate entries.");
		}

		if (clusters.length != clusterIdsSet.size()) {
			fail(context + " returned an array with entries that contains duplicate clusterIds.");
		}

		/*
		 * Checks if the clusters defined in the ZigBee CT configuration file
		 * are actually provided by the ZigBeeEndpoint
		 */

		for (int i = 0; i < expectedClusters.length; i++) {
			boolean containsExpectedCluster = clusterIdsSet.contains(new Integer(expectedClusters[i].getId()));
			if (!containsExpectedCluster) {
				fail(context + " do not return the cluster ids present in the CT configuration file.");
			}
		}

		/*
		 * Checks if the ZigBeeEndpoint.getServerCluster(clusterId) returns the
		 * right cluster.
		 */
		if (isServerSide) {
			context = "ZigBeeEndpoint.getServerCluster(clusterId)";
		} else {
			context = "ZigBeeEndpoint.getClientCluster(clusterId)";
		}

		for (int i = 0; i < clusters.length; i++) {
			ZCLCluster cluster;
			if (isServerSide) {
				cluster = endpoint.getServerCluster(clusters[i].getId());
			} else {
				cluster = endpoint.getClientCluster(clusters[i].getId());
			}

			assertNotNull(context + " cannot return null if asking for a correct clusterId", cluster);
			// FIXME: is the following test valid? Do we have the same
			// constraint?
			assertEquals(context + " must return the same object instances returned by the ZigBeeEndpoint.get<side>Clusters()", clusters[i], cluster);
		}

		/*
		 * Checks if the ZigBeeEndpoint.getServerCluster(clusterId) throws the
		 * correct exception if a wrong clusterId is passed as parameter.
		 */

		try {
			/* clusterId too big */
			if (isServerSide) {
				endpoint.getServerCluster(0xFFFF + 1);
			} else {
				endpoint.getClientCluster(0xFFFF + 1);
			}
			fail(context + " must throw an IllegalArgumentException if the clusterId is outside the range [0, 0xffff]");
		} catch (IllegalArgumentException e) {
			/* Success */
		}

		try {
			/* negative clusterId */
			if (isServerSide) {
				endpoint.getServerCluster(-1);
			} else {
				endpoint.getClientCluster(-1);
			}
			fail(context + " must throw an IllegalArgumentException if the clusterId is outside the range [0, 0xffff]");
		} catch (IllegalArgumentException e) {
			/* Success */
		}

		/* find a clusterId that does not exist in the endpoint */
		int notExistentClusterId = -1;
		for (int i = 0; i < 0xffff; i++) {
			if (!clusterIdsSet.contains(new Integer(i))) {
				notExistentClusterId = i;
				break;
			}
		}

		if (notExistentClusterId != -1) {
			try {
				/* not existent clusterId */
				if (isServerSide) {
					endpoint.getServerCluster(notExistentClusterId);
				} else {
					endpoint.getClientCluster(notExistentClusterId);
				}
				fail(context + " must throw an IllegalArgumentException if the cluster is not supported by the endpoint");
			} catch (IllegalArgumentException e) {
				/* Success */
			}
		} else {
			/*
			 * It it not really possible that the endpoints implements all the
			 * possible clusterIds!
			 */
		}
	}

	/**
	 * Test case related to events handling.
	 * 
	 * TODO
	 */
	public void testEventing() {
		log("---- testEventing");

		ZigBeeNodeConfig expectedNode = conf.getFirstNode();
		ZigBeeEndpointConfig expectedEndpoint = conf.getEnpoints(expectedNode)[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpointService(expectedNode, expectedEndpoint);

		assertNotNull("ZigBeeEndpoint", endpoint);

		ZCLCluster[] clusters = endpoint.getServerClusters();
		ZCLCluster cluster = null;
		if (clusters != null && clusters.length != 0) {
			cluster = clusters[0];
		}
		assertNotNull("ZigBeeCluster", cluster);

		AttributeCoordinates attrIds = conf.findAttribute(null, new Boolean(true), null);
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

		// FIXME: uncomment the lines below:

		// assertEquals(lastReceivedZigBeeEvent.getIEEEAddress(),
		// attrIds.getIeeeAddresss());

		// assertEquals(lastReceivedZigBeeEvent.getClusterId(),
		// attrIds.getClusterId());

		// assertEquals(lastReceivedZigBeeEvent.getAttributeId(),
		// attrIds.getAttributeId());

		// stop/destroy the test event listener.
		aZCLEventListenerImpl.stop();
	}

	// ====================================================================
	// ===========================EXCEPTIONS TEST==========================
	// ===========================METHODS==================================
	// ====================================================================

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
		ZigBeeHost host = getZigBeeHost(conf.getZigBeeHost(), DISCOVERY_TIMEOUT);

		Promise p = host.getNodeDescriptor();
		waitForPromise(p, INVOKE_TIMEOUT);

		ZigBeeNodeDescriptor zigBeeNodeDescriptor = (ZigBeeNodeDescriptor) p.getValue();
		assertNotNull("node descriptor", zigBeeNodeDescriptor);
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

			waitForPromise(p, INVOKE_TIMEOUT);

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
