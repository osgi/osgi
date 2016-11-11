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

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLAttributeInfo;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZCLReadStatusRecord;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptions.ZCLCommandDescription;
import org.osgi.service.zigbee.types.ZigBeeBoolean;
import org.osgi.service.zigbee.types.ZigBeeCharacterString;
import org.osgi.test.cases.zigbee.config.file.AttributeCoordinates;
import org.osgi.test.cases.zigbee.config.file.CommandCoordinates;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;
import org.osgi.test.cases.zigbee.config.file.ConfigurationUtils;
import org.osgi.test.cases.zigbee.config.file.ZigBeeEndpointConfig;
import org.osgi.test.cases.zigbee.config.file.ZigBeeNodeConfig;
import org.osgi.test.cases.zigbee.mock.ZCLAttributeImpl;
import org.osgi.test.cases.zigbee.mock.ZCLAttributeInfoImpl;
import org.osgi.util.promise.Promise;

/**
 * Suite of test cases for testing if the ZCLCluster interface instances.
 * 
 * @author portinaro
 *
 */
public class ZCLClusterTestCases extends ZigBeeTestCases {

	private static final String	TAG	= ZCLClusterTestCases.class.getName();

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
		log("Prepared for ZigBee Test Case");
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

		// TODO: we need to check here that the xml file contains all the info
		// we need for starting the tests!
	}

	/**
	 * Tests the ZCLCluster.getAttribute() method.
	 * 
	 * TODO
	 * 
	 * @throws Exception
	 */
	public void testGetAttribute() throws Exception {

		ConfigurationUtils.getUnsupportedServerAttribute();

		ZigBeeNodeConfig expectedNode = conf.getFirstNode();
		ZigBeeEndpointConfig expectedEndpoint = conf.getEnpoints(expectedNode)[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpointService(expectedNode, expectedEndpoint);
		assertNotNull("ZigBeeEndpoint", endpoint);
	}

	/**
	 * Tests the ZCLCluster.writeAttributes() method.
	 * 
	 * @throws Exception In case of errors not handled by the test case.
	 */

	public void testWriteAttributes() throws Exception {

		String context = "ZCLCluster.writeAttributes()";

		ZigBeeNodeConfig expectedNode = conf.getFirstNode();
		ZigBeeEndpointConfig expectedEndpoint = conf.getEnpoints(expectedNode)[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpointService(expectedEndpoint);
		assertNotNull("ZigBeeEndpoint", endpoint);

		/*
		 * Get the first cluster present in the CT configuration file.
		 */
		ZCLCluster cluster = endpoint.getServerClusters()[0];

		/*
		 * Check exception thrown in case of null Map.
		 */
		Promise p = cluster.writeAttributes(false, null);
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() != null) {
			assertException(context + " wrong exception was thrown on passing null attributes map.", NullPointerException.class, p.getFailure());
		} else {
			fail(context + ": expected an NullPointerException when passing a null frame.");
		}

		/*
		 * Check returned value in case of empty Map.
		 */
		p = cluster.writeAttributes(false, new HashMap());
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() == null) {
			Map map = (Map) assertPromiseValueClass(p, Map.class);
			assertEquals(context + " expected an empty map as result of asking to write no attributes", 0, map.size());
		} else {
			fail("unexpected failure", p.getFailure());
		}

		/*
		 * Check returned failure in case of passing a mixture of manufacturer
		 * and not manufacturer specific attributes in the same call.
		 */

		Map wrongMap1 = new HashMap();

		ZCLAttributeInfo attributeInfo1 = new ZCLAttributeInfoImpl(0x0, 0x05, ZigBeeBoolean.getInstance());
		ZCLAttributeInfo attributeInfo2 = new ZCLAttributeInfoImpl(0x1, -1, ZigBeeBoolean.getInstance());

		wrongMap1.put(attributeInfo1, new Boolean(true));
		wrongMap1.put(attributeInfo2, new Boolean(true));

		p = cluster.writeAttributes(false, wrongMap1);
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() != null) {
			assertException(context + " wrong exception was thrown on passing null attributes map.", IllegalArgumentException.class, p.getFailure());
		} else {
			fail(context + ": expected an IllegalArgumentException when passing a null frame.");
		}

		/*
		 * Find a Boolean writable attribute inside the CT configuration file.
		 */
		AttributeCoordinates attributeCoordinates = conf.findAttribute(new Boolean(true), null, ZigBeeCharacterString.getInstance());

		assertNotNull("unable to find in the CT file a CharacterSting writable attribute");

		endpoint = getZigBeeEndpointService(attributeCoordinates.expectedEndpoint);

		ZCLAttribute writableAttribute = new ZCLAttributeImpl(attributeCoordinates.expectedAttributeDescription);
		p = writableAttribute.getValue();

		String writableAttributeValue = (String) p.getValue();

		Map writeMap = new HashMap();
		writeMap.put(writableAttribute, writableAttributeValue);

		p = cluster.writeAttributes(false, writeMap);
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() == null) {
			Map map = (Map) assertPromiseValueClass(p, Map.class);
			assertEquals(context + " expected an empty map as result of asking to write an attribute", 0, map.size());
		} else {
			fail("unexpected failure", p.getFailure());
		}

		p = cluster.writeAttributes(false, writeMap);
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() == null) {
			Map map = (Map) assertPromiseValueClass(p, Map.class);
			assertEquals(context + " expected an empty map as the successful result of asking to write an attribute", 0, map.size());
		} else {
			fail("unexpected failure", p.getFailure());
		}

		/*
		 * Find an unsigned boolean attribute inside the CT configuration file.
		 * Then tries to write the above attribute and this read only.
		 */
		attributeCoordinates = conf.findAttribute(new Boolean(false), null, ZigBeeBoolean.getInstance());

		assertNotNull("unable to find in the CT configuration file a Boolean read-only attribute");

		endpoint = getZigBeeEndpointService(attributeCoordinates.expectedEndpoint);

		ZCLAttribute readOnlyAttribute = new ZCLAttributeImpl(attributeCoordinates.expectedAttributeDescription);
		p = writableAttribute.getValue();

		Boolean readOnlyAttributeValue = (Boolean) p.getValue();

		Map writeMixedMap = new HashMap();
		writeMixedMap.put(writableAttribute, writableAttributeValue);
		writeMixedMap.put(readOnlyAttribute, readOnlyAttributeValue);

		p = cluster.writeAttributes(false, writeMixedMap);
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() == null) {
			Map map = (Map) assertPromiseValueClass(p, Map.class);
			assertEquals(context + " expected a map with size 1 as result of asking to write a writable and a read-only attribute", 1, map.size());

			Object s = map.get(new Integer(readOnlyAttribute.getId()));
			if (s instanceof Short) {
				short status = ((Short) s).shortValue();
				assertEquals(context + " must return an ZCLException.READ_ONLY error status.", ZCLException.READ_ONLY, status);
			} else {
				fail(context + " must return a Map<Integer, Short>");
			}
		} else {
			fail("unexpected failure", p.getFailure());
		}

		// TODO: undivided
		// TODO: multiple writable attributes undivided.
	}

	/**
	 * Tests the ZCLCluster.readAttributes() method.
	 * 
	 * <p>
	 * To test this method and the behavior of its implementation, the test case
	 * needs to locate in the node element of the CT configuration file one
	 * endpoint that have a cluster that specifies an unsupported attribute
	 * identifier (that is an 'unsupportedAttributeId' attribute in the
	 * <cluster> element).
	 * 
	 * <p>
	 * Moreover the same cluster must have also at least one attribute to read.
	 * 
	 * @throws Exception In case of errors not handled by the test case.
	 */

	public void testReadAttributes() throws Exception {

		ZigBeeNodeConfig expectedNode = conf.getFirstNode();
		ZigBeeEndpointConfig expectedEndpoint = conf.getEnpoints(expectedNode)[0];

		ZigBeeEndpoint endpoint = getZigBeeEndpointService(expectedNode, expectedEndpoint);
		assertNotNull("ZigBeeEndpoint", endpoint);

		ZCLCluster cluster = endpoint.getServerClusters()[0];

		/*
		 * ZCLCluster.readAttributes(AttributeInfo[]): check behavior when
		 * passing a null argument.
		 */

		Promise p = cluster.readAttributes(null);
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			fail("ZCLCluster.readAttributes() must fail the promise with a NullPointerException");
		} else {
			Throwable t = p.getFailure();
			if (!(t instanceof NullPointerException)) {
				fail("expected NullPointerException, got " + t.getClass().getName());
			}
		}

		/*
		 * ZCLCluster.readAttributes(AttributeInfo[]): check behavior when
		 * passing an empty array.
		 */

		ZCLAttributeInfo[] attributeInfos = new ZCLAttributeInfo[0];

		p = cluster.readAttributes(attributeInfos);
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			fail("ZCLCluster.readAttributes() must fail the promise with a IllegalArgumentException if the passed array is empty.");
		} else {
			Throwable t = p.getFailure();
			if (!(t instanceof IllegalArgumentException)) {
				fail("expected IllegalArgumentException, got " + t.getClass().getName());
			}
		}

		/*
		 * ZCLCluster.readAttributes(AttributeInfo[]): check behavior when
		 * passing an array with null entries.
		 */

		attributeInfos = new ZCLAttributeInfo[] {null, null};

		p = cluster.readAttributes(attributeInfos);
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			fail("ZCLCluster.readAttributes() must fail the promise with a IllegalArgumentException if the passed array contains a null entry.");
		} else {
			Throwable t = p.getFailure();
			if (!(t instanceof IllegalArgumentException)) {
				fail("expected IllegalArgumentException, got " + t.getClass().getName());
			}
		}

		/*
		 * ZCLCluster.readAttributes(): check behavior on unsupported attribute
		 * identifier
		 */

		// FIXME: locate an unsupported attribute.
		int unsupportedAttributeId = -1;

		ZCLAttributeInfo[] zclAttributeInfos = {new ZCLAttributeInfoImpl(unsupportedAttributeId, -1, ZigBeeBoolean.getInstance())};
		p = cluster.readAttributes(zclAttributeInfos);
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			Map map = (Map) assertPromiseValueClass(p, Map.class);

			assertNotNull("ZCLCluster.readAttributes() cannot return a null Map.", map);

			/*
			 * The returned promise must return a map with just one entry.
			 */
			if (map.size() != 1) {
				assertEquals("as return from a readAttribute, expected a Map with a different size", 1, map.size());
			}

			ZCLReadStatusRecord readStatusRecord = (ZCLReadStatusRecord) map.get(new Integer(unsupportedAttributeId));
			Exception failure = readStatusRecord.getFailure();

			assertNotNull("The response is successful, BUT a failure is expected in this test case reading a invalid attribute.", failure);
			assertTrue("The exception is not a ZCL exception", failure instanceof ZCLException);
			assertEquals("The ZCL exception is not an unsupported attribute exception", ZCLException.UNSUPPORTED_ATTRIBUTE, ((ZCLException) failure).getErrorCode());
		} else {
			// FIXME: check for valid exceptions.
		}
	}

	/**
	 * Test case for checking the the proper functioning of the ZCLAttribute
	 * class instances returned by the ZCLCluster.getAttribute(attributeId)
	 * method.
	 * 
	 * TODO
	 * 
	 * @throws Exception In case of errors not handled by the test case.
	 */
	public void testGetAttribute1() throws Exception {

		AttributeCoordinates booleanAttributeCoordinates = conf.findAttribute(null, null, ZigBeeBoolean.getInstance());

		assertNotNull("No attribute found with the Boolean datatype, please modify the configuration file to add a cluster with such an attribute", booleanAttributeCoordinates);

		ZigBeeEndpoint endpoint = this.getZigBeeEndpointService(booleanAttributeCoordinates.expectedEndpoint);

		assertNotNull("expected endpoint " + booleanAttributeCoordinates.expectedEndpoint.getId() + " not registered.", endpoint);

		ZCLCluster cluster = endpoint.getServerCluster(booleanAttributeCoordinates.expectedCluster.getId());

		Promise p = cluster.getAttribute(booleanAttributeCoordinates.expectedAttributeDescription.getId());
		waitForPromise(p, INVOKE_TIMEOUT);

		ZCLAttribute attribute = null;

		if (p.getFailure() == null) {
			attribute = (ZCLAttribute) assertPromiseValueClass(p, ZCLAttribute.class);
			assertNotNull("ZCLCluster.getAttribute(attributeId), must never return null.", attribute);
		} else {
			fail("ZCLCluster.getAttribute(attributeId) resolved unexpectly with a failure", p.getFailure().getCause());
		}

		/**
		 * Now try to retrieve the value
		 */

		p = attribute.getValue();
		waitForPromise(p, INVOKE_TIMEOUT);

		Boolean value;

		if (p.getFailure() == null) {
			value = (Boolean) assertPromiseValueClass(p, Boolean.class);
		} else {
			fail("ZCLAttribute.getValue() resolved unexpectly with a failure", p.getFailure().getCause());
		}

		/*
		 * Now try to write a value of a wrong java type. We expect a failure.
		 */
		p = attribute.setValue(new Float(4));
		waitForPromise(p, INVOKE_TIMEOUT);
		assertZCLException("check for ZCLException.INVALID_DATA_TYPE", p.getFailure(), ZCLException.INVALID_DATA_TYPE);

		/*
		 * Check if the correct failure is returned in case of an attempt to
		 * write a read-only attribute.
		 */

		/* find any read-only attribute */
		AttributeCoordinates readOnlyAttribute = conf.findAttribute(new Boolean(false), null, null);
		endpoint = this.getZigBeeEndpointService(readOnlyAttribute.expectedEndpoint);

		assertNotNull("expected endpoint " + readOnlyAttribute.expectedEndpoint.getId() + " not registered.", endpoint);

		cluster = endpoint.getServerCluster(readOnlyAttribute.expectedCluster.getId());

		p = cluster.getAttribute(readOnlyAttribute.expectedAttributeDescription.getId());
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() == null) {
			attribute = (ZCLAttribute) assertPromiseValueClass(p, ZCLAttribute.class);
			assertNotNull("ZCLCluster.getAttribute(attributeId), must never return null.", attribute);
		} else {
			fail("ZCLCluster.getAttribute(attributeId) resolved unexpectly with a failure", p.getFailure().getCause());
		}

		/**
		 * Now try to retrieve the value
		 */
		p = attribute.getValue();
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() != null) {
			fail("ZCLAttribute.getValue() resolved unexpectly with a failure", p.getFailure().getCause());
		}

		Object o = p.getValue();

		/* set the value with what has been read to avoid range type problems */
		p = attribute.setValue(o);
		waitForPromise(p, INVOKE_TIMEOUT);
		assertZCLException("check for ZCLException.INVALID_DATA_TYPE", p.getFailure(), ZCLException.READ_ONLY);
	}

	/**
	 * Tests the ZCLCluster.getCommandIds(). This functionality may throw an
	 * error because not implemented by the ZigBee node.
	 * 
	 * @throws Exception In case of errors not handled by the test case.
	 */
	public void testGetCommandIds() throws Exception {

		ZigBeeNodeConfig expectedNode = conf.getFirstNode();
		ZigBeeEndpointConfig expectedEndpoint = conf.getEnpoints(expectedNode)[0];
		ZigBeeEndpoint endpoint = getZigBeeEndpointService(expectedEndpoint);

		ZCLClusterDescription[] expectedClusters = expectedEndpoint.getServerClusters();

		for (int i = 0; i < expectedClusters.length; i++) {
			ZCLClusterDescription expectedCluster = expectedClusters[i];

			ZCLCluster cluster = endpoint.getServerCluster(expectedCluster.getId());
			assertNotNull("ZigBeeEndpoint.getServerCluster(clusterId) returned a null value for clusterId=" + expectedCluster.getId(), cluster);

			// FIXME: change documentation for getCommandIds(), because it
			// states that the returned value is Integer[].
			Promise p = cluster.getCommandIds();
			waitForPromise(p, INVOKE_TIMEOUT);

			if (p.getFailure() == null) {
				int[] commandIds = (int[]) this.assertPromiseValueClass(p, int[].class);
				assertNotNull("ZigBeeEndpoint.getCommandIds() cannot return null", commandIds);
				if (commandIds.length == 0) {
					fail("ZigBeeCluster is empty");
				}

				/*
				 * locates in the returned array the presence of the server side
				 * commandId defined in the CT configuration file.
				 */

				ZCLCommandDescription[] receivedCommandDescriptions = expectedCluster.getReceivedCommandDescriptions();

				for (int j = 0; j < receivedCommandDescriptions.length; j++) {
					boolean found = false;
					for (int k = 0; k < commandIds.length; k++) {
						if (commandIds[k] == receivedCommandDescriptions[j].getId()) {
							found = true;
							break;
						}
					}
					if (!found) {
						fail("ZigBeeEndpoint.getCommandIds() do not contain one of the expected commandIds defined in the CT configuration file commandId=" + receivedCommandDescriptions[j].getId());
					}
				}
			} else {
				/*
				 * The getCommandIds may fail with an exception if some the
				 * specific general command is not supported by the cluster.
				 */
				assertZCLException("check for ZCLException.GENERAL_COMMAND_NOT_SUPPORTED", p.getFailure(), ZCLException.GENERAL_COMMAND_NOT_SUPPORTED);
			}
		}
	}

	/**
	 * Tests the ZCLCluster.getAttributes() method from the point of view of the
	 * consistency of the returned attributes metadata.
	 * 
	 * @throws Exception In case of errors not handled by the test case.
	 */
	public void testGetAttributesDescriptions() throws Exception {
		log(TAG, "testGetAttributesDescriptions");

		ZigBeeNodeConfig expectedNode = conf.getFirstNode();
		ZigBeeEndpointConfig expectedEndpoint = conf.getEnpoints(expectedNode)[0];
		ZigBeeEndpoint endpoint = getZigBeeEndpointService(expectedNode, expectedEndpoint);

		ZCLCluster[] clusters = endpoint.getServerClusters();

		assertNotNull("ZigBeeEndpoint.getServerClusters()", clusters);

		// TODO: do the same for the Client clusters.
		ZCLClusterDescription[] expectedClusters = expectedEndpoint.getServerClusters();

		for (int i = 0; i < clusters.length; i++) {

			ZCLCluster cluster = clusters[i];

			ZCLClusterDescription expectedCluster = lookupCluster(expectedClusters, clusters[i].getId());
			if (expectedCluster == null) {
				continue;
			}

			Promise p = cluster.getAttributes();

			waitForPromise(p, INVOKE_TIMEOUT);
			if (p.getFailure() == null) {

				int expectedAttributesFound = 0;

				ZCLAttribute[] attributes = (ZCLAttribute[]) assertPromiseValueClass(p, ZCLAttribute[].class);

				assertNotNull("ZCLCluster.getAttributes() returned a null array", attributes);

				ZCLAttributeDescription[] attributeDescriptions = expectedCluster.getAttributeDescriptions();
				for (int j = 0; j < attributeDescriptions.length; j++) {
					ZCLAttributeDescription attributeDescription = attributeDescriptions[j];

					ZCLAttribute attribute = lookupAttribute(attributes, attributeDescription.getId());
					assertNotNull("expected configuration attributeId=" + attributeDescription.getId() + " not found in the ZigBee node.", attribute);

					expectedAttributesFound++;

					assertEquals("configuration attributeId=" + attributeDescription.getId() + ": manufacturerCode ", attributeDescription.getManufacturerCode(), attribute.getManufacturerCode());
					// TODO: maybe it doesn't compare correctly the dataType.
					assertEquals("configuration attributeId=" + attributeDescription.getId() + ": manufacturerCode ", attributeDescription.getDataType(), attribute.getDataType());
				}

				assertEquals("expected attribute count differs from actual ones", attributeDescriptions.length, expectedAttributesFound);

			} else {
				fail("unexpected promise failure", p.getFailure().getCause());
			}
		}
	}

	/**
	 * Tests the following ZCLCluster methods:
	 * <ul>
	 * <li>ZCLCluster.invoke(ZCLFrame)
	 * <li>ZCLCluster.invoke(ZCLFrame, String PID);
	 * </ul>
	 * 
	 * It does that on the CT configuration file endpoint that contains the
	 * command that defines the reeuestFrame and the responseFrame elements.
	 * 
	 * @throws Exception In case of errors not handled by the test case.
	 */
	public void testInvoke() throws Exception {

		// ZigBeeNodeConfig expectedNode = conf.getFirstNode();
		// ZigBeeEndpointConfig expectedEndpoint =
		// conf.getEnpoints(expectedNode)[0];
		// ZigBeeEndpoint endpoint = getZigBeeEndpointService(expectedNode,
		// expectedEndpoint);

		String context = "ZCLCluster.invoke(ZCLFrame)";

		CommandCoordinates commandCoordinates = null;

		assertNotNull("testInvoke(): unable to find in the CT configuration file a command element that include also a requestFrame and a responseFrame elements");

		ZigBeeEndpoint endpoint = getZigBeeEndpointService(commandCoordinates.expectedEndpoint);

		/*
		 * Check for invalid parameters on the first node clusters.
		 */

		ZCLCluster[] clusters = endpoint.getServerClusters();

		for (int i = 0; i < clusters.length; i++) {
			ZCLCluster cluster = clusters[i];

			Promise p = cluster.invoke(null);
			waitForPromise(p, INVOKE_TIMEOUT);
			if (p.getFailure() != null) {
				assertException(context + ": wrong exception got", NullPointerException.class, p.getFailure());
			} else {
				fail(context + ": expected an NullPointerException when passing a null frame.");
			}

			// FIXME: pass an actual endpoint pid.

			p = cluster.invoke(null, "");
			waitForPromise(p, INVOKE_TIMEOUT);
			if (p.getFailure() != null) {
				assertException(context + ": wrong exception got", NullPointerException.class, p.getFailure());
			} else {
				fail(context + ": expected an IllegalArgumentException when passing a null frame.");
			}
		}

		/*
		 * Issues real commands.
		 * 
		 * TODO
		 */

		ZCLClusterDescription[] expectedClusters = commandCoordinates.expectedEndpoint.getServerClusters();

		for (int i = 0; i < expectedClusters.length; i++) {
			ZCLClusterDescription expectedCluster = expectedClusters[i];

		}
	}
}
