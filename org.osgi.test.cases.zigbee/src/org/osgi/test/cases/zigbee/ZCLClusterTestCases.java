/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZCLHeader;
import org.osgi.service.zigbee.ZCLReadStatusRecord;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptions.ZCLCommandDescription;
import org.osgi.service.zigbee.types.ZigBeeBoolean;
import org.osgi.service.zigbee.types.ZigBeeUnsignedInteger16;
import org.osgi.test.cases.zigbee.config.file.AttributeCoordinates;
import org.osgi.test.cases.zigbee.config.file.CommandCoordinates;
import org.osgi.test.cases.zigbee.config.file.ZigBeeEndpointConfig;
import org.osgi.test.cases.zigbee.config.file.ZigBeeNodeConfig;
import org.osgi.test.cases.zigbee.descriptions.ZCLCommandDescriptionImpl;
import org.osgi.test.cases.zigbee.mock.ZCLAttributeInfoImpl;
import org.osgi.test.cases.zigbee.mock.ZCLFrameImpl;
import org.osgi.test.cases.zigbee.mock.ZCLFrameRaw;
import org.osgi.test.cases.zigbee.mock.ZCLHeaderImpl;
import org.osgi.util.promise.Promise;

/**
 * Suite of test cases for testing if the ZCLCluster interface instances.
 * 
 * @author $id$
 *
 */
public class ZCLClusterTestCases extends ZigBeeTestCases {

	private static final String TAG = ZCLClusterTestCases.class.getName();

	/**
	 * Tests the ZCLCluster.writeAttributes() method.
	 * 
	 * The currently implemented test cases are designed to be used in real
	 * scenarios and with real ZigBee device. For instance just a few ZCL
	 * clusters support more than one writable attributes, so a test that tries
	 * to write 2 attributes cannot be actually performed.
	 * 
	 * @throws Exception In case of errors not handled by the test case.
	 */

	public void testWriteAttributes() throws Exception {

		String context = "ZCLCluster.writeAttributes()";

		/*
		 * Locates an endpoint that have a at least one server cluster
		 */
		ZigBeeEndpointConfig expectedEndpoint = conf.findEndpointWithCluster(true);
		assertNotNull(context + " unable to find in the CT configuration file an endpoint with at least one configured server cluster.", expectedEndpoint);

		ZigBeeEndpoint endpoint = getZigBeeEndpointService(expectedEndpoint);
		assertNotNull(context + ": expected ZigBeeEndpoint " + printScope(expectedEndpoint) + " not registered", endpoint);

		/*
		 * Get the first cluster present in the CT configuration file.
		 */
		ZCLCluster[] clusters = endpoint.getServerClusters();
		assertNotNull(context + ": getServerClusters() must never return null", clusters);

		if (clusters.length == 0) {
			fail(context + " the CT configuration file states that ZigBeeEndpoint service " + printScope(expectedEndpoint) + " contains at least one server cluster");
		}

		ZCLCluster cluster = clusters[0];

		/*
		 * Check exception thrown in case of null Map.
		 */
		Promise<Map<Integer,Integer>> p = cluster.writeAttributes(false, null);
		waitForPromise(p, INVOKE_TIMEOUT);
		assertPromiseFailure(p, context + ": passing null map argument.", NullPointerException.class);

		/*
		 * Check returned value in case of empty Map.
		 */
		p = cluster.writeAttributes(false, new HashMap<>());
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() == null) {
			Map<Integer,Integer> map = assertPromiseValueClass(p, Map.class);
			assertEquals(context + " expected an empty map as result of asking to write no attributes.", 0, map.size());
		} else {
			fail(context + " Promise failed unexpectly.", p.getFailure());
		}

		/*
		 * Check returned failure in case of passing a mixture of manufacturer
		 * and not manufacturer specific attributes in the same call.
		 */

		Map<ZCLAttributeInfo,Object> wrongMap1 = new HashMap<>();

		ZCLAttributeInfo attributeInfo1 = new ZCLAttributeInfoImpl(0x0, 0x05, ZigBeeBoolean.getInstance());
		ZCLAttributeInfo attributeInfo2 = new ZCLAttributeInfoImpl(0x1, -1, ZigBeeBoolean.getInstance());

		wrongMap1.put(attributeInfo1, new Boolean(true));
		wrongMap1.put(attributeInfo2, new Boolean(true));

		p = cluster.writeAttributes(false, wrongMap1);
		waitForPromise(p, INVOKE_TIMEOUT);
		assertPromiseFailure(p, context + ": passing a map argument that contains a mix of not and maufacturer specific attributes.", IllegalArgumentException.class);

		/*
		 * Find a CharacterString writable attribute inside the CT configuration
		 * file.
		 */
		AttributeCoordinates attributeCoordinates = conf.findAttribute(new Boolean(true), null, ZigBeeUnsignedInteger16.getInstance());

		assertNotNull("unable to find in the CT file a CharacterSting writable attribute", attributeCoordinates);

		endpoint = getZigBeeEndpointService(attributeCoordinates.expectedEndpoint);

		ZCLAttributeInfo writableAttributeInfo = new ZCLAttributeInfoImpl(attributeCoordinates.expectedAttributeDescription);

		Map<ZCLAttributeInfo,Object> writeMap = new HashMap<>();
		writeMap.put(writableAttributeInfo, new Integer(10));

		p = cluster.writeAttributes(false, writeMap);
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() == null) {
			Map<Integer,Integer> map = assertPromiseValueClass(p, Map.class);
			assertEquals(context + " expected an empty map as result of asking to write an attribute", 0, map.size());
		} else {
			fail(context + " Promise failed unexpectly.", p.getFailure());
		}

		/*
		 * Find a read only boolean attribute inside the CT configuration file.
		 */
		attributeCoordinates = conf.findAttribute(new Boolean(false), null, ZigBeeBoolean.getInstance());

		assertNotNull("unable to find in the CT configuration file a Boolean read-only attribute", attributeCoordinates);

		endpoint = getZigBeeEndpointService(attributeCoordinates.expectedEndpoint);

		ZCLAttributeInfo readOnlyAttributeInfo = new ZCLAttributeInfoImpl(attributeCoordinates.expectedAttributeDescription);

		Map<ZCLAttributeInfo,Object> readOnlyMap = new HashMap<>();
		readOnlyMap.put(readOnlyAttributeInfo, new Boolean(false));

		p = cluster.writeAttributes(false, readOnlyMap);
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() == null) {
			Map<Integer,Integer> map = assertPromiseValueClass(p, Map.class);
			assertEquals(context + " expected a map with size 1 as result of asking to write a read-only attribute", 1, map.size());

			Object s = map.get(new Integer(readOnlyAttributeInfo.getId()));
			if (s instanceof Integer) {
				short status = ((Integer) s).shortValue();
				assertEquals(context + " must return an ZCLException.READ_ONLY error status.", ZCLException.READ_ONLY, status);
			} else {
				fail(context + " must return a Map<Integer, Short>");
			}
		} else {
			fail("unexpected failure", p.getFailure());
		}

		ZCLAttributeInfo unsupportedAttributeInfo = new ZCLAttributeInfoImpl(0xfa11, -1, ZigBeeBoolean.getInstance());

		Map<ZCLAttributeInfo,Object> readOnlyAndUndefinedMap = new HashMap<>();
		readOnlyAndUndefinedMap.put(readOnlyAttributeInfo, new Boolean(false));
		readOnlyAndUndefinedMap.put(unsupportedAttributeInfo, new Boolean(false));

		p = cluster.writeAttributes(false, readOnlyAndUndefinedMap);
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() == null) {
			Map<Integer,Integer> map = assertPromiseValueClass(p, Map.class);
			assertEquals(context + " expected a map with size 2 as result of asking to write one read-only and one undefined attribute", 2, map.size());

			Object s = map.get(new Integer(readOnlyAttributeInfo.getId()));
			if (s instanceof Integer) {
				short status = ((Integer) s).shortValue();
				assertEquals(context + " must return an ZCLException.READ_ONLY error status.", ZCLException.READ_ONLY, status);
			} else {
				fail(context + " must return a Map<Integer, Short>");
			}

			s = map.get(new Integer(unsupportedAttributeInfo.getId()));
			if (s instanceof Integer) {
				short status = ((Integer) s).shortValue();
				assertEquals(context + " must return an ZCLException.READ_ONLY error status.", ZCLException.UNSUPPORTED_ATTRIBUTE, status);
			} else {
				fail(context + " must return a Map<Integer, Short>");
			}
		} else {
			fail("unexpected failure", p.getFailure());
		}
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
		log(TAG, "starting Test testReadAttributes.");

		/*
		 * Looks for a server side Boolean attribute that the test case will try
		 * to read.
		 */
		AttributeCoordinates attributeCoord = conf.findAttribute(null, null, ZigBeeBoolean.getInstance());
		assertNotNull("The CT configuration file must contain at least one server cluster with an attribute of boolean data type", attributeCoord);

		ZigBeeEndpointConfig expectedEndpoint = attributeCoord.expectedEndpoint;
		ZigBeeEndpoint endpoint = getZigBeeEndpointService(expectedEndpoint);
		assertNotNull("expected ZigBeeEndpoint " + this.printScope(expectedEndpoint) + " not registered", endpoint);

		/*
		 * Got the cluster where the attribute reside.
		 */
		ZCLCluster cluster = endpoint.getServerCluster(attributeCoord.expectedCluster.getId());

		/*
		 * ZCLCluster.readAttributes(AttributeInfo[]): check behavior when
		 * passing a null argument.
		 */

		String context = "ZCLCluster.readAttributes()";

		log(TAG, context + ": passing null argument.");

		Promise<Map<Integer,ZCLReadStatusRecord>> p = cluster
				.readAttributes(null);
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			fail(context + ": must fail the promise with a NullPointerException");
		} else {
			assertException(context + ": wrong type of failure.", NullPointerException.class, p.getFailure());
		}

		/*
		 * ZCLCluster.readAttributes(AttributeInfo[]): check behavior when
		 * passing an empty array.
		 */

		log(TAG, context + ": passing empty array argument.");
		ZCLAttributeInfo[] attributeInfos = new ZCLAttributeInfo[0];

		p = cluster.readAttributes(attributeInfos);
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			fail(context + ": must fail the promise with a IllegalArgumentException if the passed array is empty.");
		} else {
			assertException(context + ": wrong type of failure when passing empty array", IllegalArgumentException.class, p.getFailure());
		}

		/*
		 * ZCLCluster.readAttributes(AttributeInfo[]): check behavior when
		 * passing an array with null entries.
		 */

		log(TAG, context + ": passing array with null entries.");

		attributeInfos = new ZCLAttributeInfo[] {null, null};

		p = cluster.readAttributes(attributeInfos);
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			fail(context + ": must fail the promise with a IllegalArgumentException if the passed array contains a null entry.");
		} else {
			assertException(context + ": wrong type of failure when passing array with null entries.", IllegalArgumentException.class, p.getFailure());
		}

		/*
		 * ZCLCluster.readAttributes(): check behavior on unsupported attribute
		 * identifier
		 */

		log(TAG, context + ": reading an unsupported attribute.");

		// This is an unsupported attributeId;
		int unsupportedAttributeId = 244;

		ZCLAttributeInfo[] zclAttributeInfos = {new ZCLAttributeInfoImpl(unsupportedAttributeId, -1, ZigBeeBoolean.getInstance())};
		p = cluster.readAttributes(zclAttributeInfos);
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			Map<Integer,ZCLReadStatusRecord> map = assertPromiseValueClass(p,
					Map.class);

			assertMapContent(context, map, Integer.class, ZCLReadStatusRecord.class, zclAttributeInfos.length);

			ZCLReadStatusRecord readStatusRecord = map.get(new Integer(unsupportedAttributeId));
			assertReadStatusRecord(context, readStatusRecord, zclAttributeInfos[0], ZCLException.UNSUPPORTED_ATTRIBUTE);
		} else {
			fail(context + " promise resolved with an unexpected exception.", p.getFailure());
		}

		/*
		 * ZCLCluster.readAttributes(): read an existing boolean attribute.
		 */
		log(TAG, context + ": reading an existing boolean attribute on cluster " + cluster.getId());

		zclAttributeInfos = new ZCLAttributeInfo[] {new ZCLAttributeInfoImpl(attributeCoord.expectedAttributeDescription)};

		p = cluster.readAttributes(zclAttributeInfos);
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			Map<Integer,ZCLReadStatusRecord> map = assertPromiseValueClass(p,
					Map.class);

			assertMapContent(context, map, Integer.class, ZCLReadStatusRecord.class, zclAttributeInfos.length);

			for (int i = 0; i < zclAttributeInfos.length; i++) {
				int key = zclAttributeInfos[i].getId();
				ZCLReadStatusRecord readStatusRecord = map.get(new Integer(key));
				assertNotNull(context + ": requested attributeId " + zclAttributeInfos[i].getId() + " entry not found in the response map", readStatusRecord);

				@SuppressWarnings("unused")
				Object value = readStatusRecord.getValue();
				assertReadStatusRecord(context, readStatusRecord, zclAttributeInfos[i], -1);
			}
		} else {
			fail(context + " promise resolved with an unexpected exception.", p.getFailure());
		}

		/*
		 * ZCLCluster.readAttributes(): read an existing boolean attribute but
		 * pass a wrong data type.
		 */
		log(TAG, context + ": reading an existing boolean attribute on cluster " + cluster.getId());

		int attributeId = attributeCoord.expectedAttributeDescription.getId();

		/* WRONG data type ZigBeeUnsignedInteger16 instead of ZigBeeBoolean */
		zclAttributeInfos = new ZCLAttributeInfo[] {new ZCLAttributeInfoImpl(attributeId, -1, ZigBeeUnsignedInteger16.getInstance())};

		p = cluster.readAttributes(zclAttributeInfos);
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			Map<Integer,ZCLReadStatusRecord> map = assertPromiseValueClass(p,
					Map.class);

			assertMapContent(context, map, Integer.class, ZCLReadStatusRecord.class, zclAttributeInfos.length);
			ZCLReadStatusRecord readStatusRecord = map.get(new Integer(attributeId));
			assertReadStatusRecord(context, readStatusRecord, zclAttributeInfos[0], ZCLException.INVALID_DATA_TYPE);

		} else {
			fail(context + " promise resolved with an unexpected exception.", p.getFailure());
		}
	}

	/**
	 * Checks the methods related to the ZCLAttribute class. Locates a boolean
	 * type attribute and tries to read and write it.
	 * 
	 * @throws Exception In case of unexpected failure.
	 */

	public void testZCLAttribute() throws Exception {

		AttributeCoordinates uint16attribute = conf.findAttribute(new Boolean(true), null, ZigBeeUnsignedInteger16.getInstance());

		assertNotNull("No writeable ZigBeeUnsignedInteger16 attribute found, please modify the configuration file to add a cluster with such an attribute", uint16attribute);
		ZigBeeEndpoint endpoint = this.getZigBeeEndpointService(uint16attribute.expectedEndpoint);
		assertNotNull("expected endpoint " + uint16attribute.expectedEndpoint.getId() + " not registered.", endpoint);

		ZCLCluster cluster = endpoint.getServerCluster(uint16attribute.expectedCluster.getId());

		String context = "ZCLCluster.getAttribute(attributeId)";

		log(TAG, context + ": retrieve an existent writable unsigned integer 16 attribute");

		/*
		 * Read this Boolean attribute (we must not get a failure here!)
		 */
		Promise<ZCLAttribute> p = cluster.getAttribute(
				uint16attribute.expectedAttributeDescription.getId());
		waitForPromise(p, INVOKE_TIMEOUT);
		ZCLAttribute attribute = null;

		if (p.getFailure() == null) {
			attribute = assertPromiseValueClass(p, ZCLAttribute.class);
			assertNotNull("ZCLCluster.getAttribute(attributeId), must never return null.", attribute);
		} else {
			fail(context + ": resolved unexpectly with a failure", p.getFailure().getCause());
		}

		/**
		 * Now try to retrieve the attribute value.
		 */
		log(TAG, "ZCLAttribute.getValue(): on the unsigned int 16 attribute");

		Promise<Object> p2 = attribute.getValue();
		waitForPromise(p2, INVOKE_TIMEOUT);

		if (p2.getFailure() == null) {
			assertPromiseValueClass(p2, Integer.class);
		} else {
			fail("ZCLAttribute.getValue(): resolved unexpectly with a failure", p.getFailure().getCause());
		}

		/*
		 * Now try to write a value of a wrong java type. We expect a failure
		 * here
		 */

		log(TAG, "ZCLAttribute.setValue(): on the previously read writable uint16 attribute with a wrong class value (Float instead of Integer)");

		Promise<Void> p3 = attribute.setValue(new Float(4));
		waitForPromise(p3, INVOKE_TIMEOUT);
		if (p3.getFailure() != null) {
			assertPromiseZCLException(p3,
					"ZCLAttribute.setValue(): expected ZCLException.INVALID_DATA_TYPE",
					ZCLException.INVALID_DATA_TYPE);
		} else {
			fail("ZCLAttribute.setValue(): expected a falure with ZCLException.INVALID_DATA_TYPE, got success!");
		}

		/*
		 * Now try to write a value. We expect a success here.
		 */

		log(TAG, context + ": issue ZCLAttribute.setValue() on the previously read ZCLAttribute.");

		p3 = attribute.setValue(new Integer(4));
		waitForPromise(p3, INVOKE_TIMEOUT);
		if (p3.getFailure() != null) {
			fail("ZCLAttribute.setValue(): we didn't expect to fail writing a writable attribute",
					p3.getFailure());
		} else {
			assertNull(
					"ZCLAttribute.setValue(): expecting the promise to resolve with null, since the write was successful",
					p3.getValue());
		}

		/*
		 * Check if the correct failure is returned in case of an attempt to
		 * write a read-only attribute.
		 */

		/* find any read-only attribute */

		AttributeCoordinates readOnlyAttribute = conf.findAttribute(new Boolean(false), null, null);
		endpoint = this.getZigBeeEndpointService(readOnlyAttribute.expectedEndpoint);
		assertNotNull("ZigBeeEndpoint service " + this.printScope(readOnlyAttribute.expectedEndpoint) + " not registered.", endpoint);
		cluster = endpoint.getServerCluster(readOnlyAttribute.expectedCluster.getId());

		p = cluster.getAttribute(readOnlyAttribute.expectedAttributeDescription.getId());
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() == null) {
			attribute = assertPromiseValueClass(p, ZCLAttribute.class);
			assertNotNull("ZCLCluster.getAttribute(attributeId), must never return null.", attribute);
		} else {
			fail(context + ": resolved unexpectly with a failure", p.getFailure().getCause());
		}

		/**
		 * Now try to retrieve the value
		 */

		log(TAG, "ZCLAttribute.getValue(): on the boolean attribute");

		p2 = attribute.getValue();
		waitForPromise(p2, INVOKE_TIMEOUT);
		if (p2.getFailure() != null) {
			fail(context
					+ "ZCLAttribute.getValue() resolved unexpectly with a failure",
					p2.getFailure().getCause());
		}

		Object o = p2.getValue();

		/*
		 * set the same value with what has been read to avoid range type
		 * problems
		 */

		log(TAG, "ZCLAttribute.setValue(): on the read-only boolean attribute.");

		p3 = attribute.setValue(o);
		waitForPromise(p3, INVOKE_TIMEOUT);
		assertPromiseZCLException(p3,
				"ZCLAttribute.setValue() check for ZCLException.READ_ONLY",
				ZCLException.READ_ONLY);
	}

	/**
	 * Test case for checking the ZCLCluster.getAttribute() methods.
	 * 
	 * @throws Exception In case of errors not handled by the test case.
	 */
	public void testGetAttribute() throws Exception {

		ZCLAttribute attribute = null;

		String context = "ZCLCluster.getAttribute(attributeId)";

		AttributeCoordinates booleanAttributeCoordinates = conf.findAttribute(null, null, ZigBeeBoolean.getInstance());
		assertNotNull("No attribute found with the Boolean datatype, please modify the configuration file to add a cluster with such an attribute", booleanAttributeCoordinates);
		ZigBeeEndpoint endpoint = this.getZigBeeEndpointService(booleanAttributeCoordinates.expectedEndpoint);
		assertNotNull("expected endpoint " + booleanAttributeCoordinates.expectedEndpoint.getId() + " not registered.", endpoint);

		ZCLCluster cluster = endpoint.getServerCluster(booleanAttributeCoordinates.expectedCluster.getId());

		log(TAG, context + ": reading an unsupported attribute.");

		// This is an unsupported attributeId;
		int unsupportedAttributeId = 244;
		Promise<ZCLAttribute> p = cluster.getAttribute(unsupportedAttributeId);
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() != null) {
			assertPromiseZCLException(p, context, ZCLException.UNSUPPORTED_ATTRIBUTE);
		} else {
			fail(context + " promise resolved with an unexpected exception.", p.getFailure());
		}

		log(TAG, context + ": retrieve a boolean attribute");

		/*
		 * Read this Boolean attribute (we must not get a failure here!)
		 */
		p = cluster.getAttribute(booleanAttributeCoordinates.expectedAttributeDescription.getId());
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			attribute = assertPromiseValueClass(p, ZCLAttribute.class);
			assertNotNull("ZCLCluster.getAttribute(attributeId), must never return null.", attribute);
		} else {
			fail(context + ": resolved unexpectly with a failure", p.getFailure().getCause());
		}

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

			Promise<short[]> p = cluster.getCommandIds();
			waitForPromise(p, INVOKE_TIMEOUT);

			if (p.getFailure() == null) {
				short[] commandIds = this.assertPromiseValueClass(p, short[].class);
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
				assertPromiseZCLException(p, "check for ZCLException.GENERAL_COMMAND_NOT_SUPPORTED", ZCLException.GENERAL_COMMAND_NOT_SUPPORTED);
			}
		}
	}

	/**
	 * Tests the ZCLCluster.getAttributes() method from the point of view of the
	 * consistency of the returned attributes metadata.
	 * 
	 * @throws Exception In case of errors not handled by the test case.
	 */
	public void testGetAttributes() throws Exception {
		log(TAG, "testGetAttributesDescriptions");

		ZigBeeNodeConfig expectedNode = conf.getFirstNode();
		ZigBeeEndpointConfig expectedEndpoint = conf.getEnpoints(expectedNode)[0];
		ZigBeeEndpoint endpoint = getZigBeeEndpointService(expectedNode, expectedEndpoint);

		ZCLCluster[] clusters = endpoint.getServerClusters();

		assertNotNull("ZigBeeEndpoint.getServerClusters()", clusters);

		ZCLClusterDescription[] expectedClusters = expectedEndpoint.getServerClusters();

		for (int i = 0; i < clusters.length; i++) {

			ZCLCluster cluster = clusters[i];

			ZCLClusterDescription expectedCluster = lookupCluster(expectedClusters, clusters[i].getId());
			if (expectedCluster == null) {
				continue;
			}

			Promise<ZCLAttribute[]> p = cluster.getAttributes();
			waitForPromise(p, INVOKE_TIMEOUT);
			if (p.getFailure() == null) {

				int expectedAttributesFound = 0;

				ZCLAttribute[] attributes = assertPromiseValueClass(p, ZCLAttribute[].class);

				assertNotNull("ZCLCluster.getAttributes() returned a null array", attributes);

				ZCLAttributeDescription[] attributeDescriptions = expectedCluster.getAttributeDescriptions();
				for (int j = 0; j < attributeDescriptions.length; j++) {
					ZCLAttributeDescription attributeDescription = attributeDescriptions[j];

					ZCLAttribute attribute = lookupAttribute(attributes, attributeDescription.getId());
					assertNotNull("expected configuration attributeId=" + attributeDescription.getId() + " not found in the ZigBee node.", attribute);

					expectedAttributesFound++;

					assertEquals("configuration attributeId=" + attributeDescription.getId() + ": manufacturerCode ", attributeDescription.getManufacturerCode(), attribute.getManufacturerCode());
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

		log(TAG, "testInvoke()");

		String context = "ZCLCluster.invoke(ZCLFrame)";

		/*
		 * Locates an endpoint that have a at least one server cluster
		 */
		ZigBeeEndpointConfig expectedEndpoint = conf.findEndpointWithCluster(true);
		assertNotNull(context + " unable to find in the CT configuration file an endpoint with at least one configured server cluster.", expectedEndpoint);

		ZigBeeEndpoint endpoint = getZigBeeEndpointService(expectedEndpoint);
		assertNotNull(context + ": expected ZigBeeEndpoint " + printScope(expectedEndpoint) + " not registered", endpoint);

		/*
		 * Check for invalid arguments on the first node clusters.
		 */

		log(TAG, "test " + context + ": passing an invalid argument.");

		ZCLCluster[] clusters = endpoint.getServerClusters();

		ZCLFrame frame = new ZCLFrameImpl((short) 0x00);

		for (int i = 0; i < clusters.length; i++) {
			ZCLCluster cluster = clusters[i];

			Promise<ZCLFrame> p = cluster.invoke(null);
			waitForPromise(p, INVOKE_TIMEOUT);
			assertPromiseFailure(p, context + ": passing null argument.", NullPointerException.class);

			p = cluster.invoke(frame, null);
			waitForPromise(p, INVOKE_TIMEOUT);
			assertPromiseFailure(p, context + ": passing null exportedServicePid argument.", NullPointerException.class);

			p = cluster.invoke(frame, "");
			waitForPromise(p, INVOKE_TIMEOUT);
			assertPromiseFailure(p, context + ": passing empty string exportedServicePid argument.", IllegalArgumentException.class);
		}

		/*
		 * Issues real commands.
		 */

		log(TAG, "test " + context + ": issuing a command and check the response, received on endpoint " + printScope(expectedEndpoint));

		CommandCoordinates commandCoordinates = conf.findCommand(true);
		assertNotNull(context + " unable to find in the CT configuration file a cluster with a server command defined.", commandCoordinates);

		expectedEndpoint = conf.findEndpointWithCluster(true);
		assertNotNull(context + " unable to find in the CT configuration file an endpoint with at least one configured server cluster.", expectedEndpoint);

		endpoint = getZigBeeEndpointService(expectedEndpoint);
		assertNotNull(context + ": expected ZigBeeEndpoint " + printScope(expectedEndpoint) + " not registered", endpoint);

		/*
		 * commandCoodinates.expectedCluster contains the cluster description of
		 * a cluster that receives at least a command. It is necessary to find
		 * this command and then the response command definition.
		 */

		ZCLCommandDescription[] commandDescriptions = commandCoordinates.expectedCluster.getReceivedCommandDescriptions();

		ZCLCommandDescription responseCommand = null;
		ZCLCommandDescription requestCommand = null;

		for (int i = 0; i < commandDescriptions.length; i++) {
			requestCommand = commandDescriptions[i];
			if (requestCommand.isClusterSpecificCommand() && !requestCommand.isManufacturerSpecific()) {
				// got it!
				responseCommand = conf.getResponseCommand(commandCoordinates.expectedCluster, requestCommand);
				break;
			}
		}

		ZCLFrame requestFrame = getZCLFrame(requestCommand);

		/*
		 * If responseCommand is not null we can issue a requestCommand and
		 * expects a responseCommand.
		 */

		ZCLCluster cluster = endpoint.getServerCluster(commandCoordinates.expectedCluster.getId());

		Promise<ZCLFrame> p = cluster.invoke(requestFrame);
		waitForPromise(p, INVOKE_TIMEOUT);
		frame = assertPromiseValueClass(p, ZCLFrame.class);
		ZCLHeader header = frame.getHeader();

		/*
		 * check if the returned command is compliant to the expected one
		 * described in the responseCommand ZCLCommandDescriptor instance.
		 */

		assertEquals(context + ": check on response ZCLHeader.getCommandId()", responseCommand.getId(), header.getCommandId());
		assertEquals(context + ": check on response ZCLHeader.getManufacturerCode()", responseCommand.getManufacturerCode(), header.getManufacturerCode());
		assertEquals(context + ": check on response ZCLHeader.isClientServerDirection()", responseCommand.isClientServerDirection(), header.isClientServerDirection());
		assertEquals(context + ": check on response ZCLHeader.isClientServerDirection()", responseCommand.isClusterSpecificCommand(), header.isClusterSpecificCommand());
		assertEquals(context + ": check on response ZCLHeader.isClientServerDirection()", responseCommand.isManufacturerSpecific(), header.isManufacturerSpecific());

		/* performs generic checks on the returned ZCLFrame */
		genericZCLFrameChecks(context, frame);

		/*
		 * Compares the raw frame of the actual response with the zclFrame that
		 * was found in the xml file. Skips the ZCLHeader.
		 */
		ZCLCommandDescriptionImpl responseCommandImpl = (ZCLCommandDescriptionImpl) responseCommand;

		byte[] expectedRawFrame = responseCommandImpl.getFrame();
		byte[] actualRawFrame = frame.getBytes();

		assertEquals(context + ": invalid size of returned ZCLFrame", expectedRawFrame.length, actualRawFrame.length);

		int expectedHeaderSize = header.isManufacturerSpecific() ? conf.getHeaderMaxSize() : conf.getHeaderMinSize();

		/* The raw frame cannot be shorter than the ZCL frame header! */
		if (actualRawFrame.length < expectedHeaderSize) {
			fail(context + ": ZCLFrame.getBytes() on the returned frame must return an array of at least " + expectedHeaderSize + ", got " + actualRawFrame.length);
		}

		/* we skip the header */
		for (int i = expectedHeaderSize; i < expectedRawFrame.length; i++) {
			assertEquals(context + ": expected and actual returned ZCL raw frames differs at index " + i, expectedRawFrame[i], actualRawFrame[i]);
		}
	}

	private ZCLFrame getZCLFrame(ZCLCommandDescription command) {
		ZCLCommandDescriptionImpl c = (ZCLCommandDescriptionImpl) command;
		ZCLHeader header = new ZCLHeaderImpl(c.getId(), c.isClusterSpecificCommand(), c.isClientServerDirection(), false, (byte) 0x05);

		ZCLFrame frame = new ZCLFrameRaw(header, c.getFrame());

		return frame;
	}

	private void genericZCLFrameChecks(String context, ZCLFrame frame) {

		ZCLHeader header = frame.getHeader();

		byte[] rawFrame = frame.getBytes();
		assertNotNull(context + ": ZCLFrame.getBytes(): must return a valid byte array", rawFrame);

		int expectedHeaderSize = header.isManufacturerSpecific() ? conf.getHeaderMaxSize() : conf.getHeaderMinSize();

		if (rawFrame.length < expectedHeaderSize) {
			fail(context + ": ZCLFrame.getBytes() on the returned frame must return an array of at least " + expectedHeaderSize + ", got " + rawFrame.length);

		}

		/*
		 * Check getSize()
		 */
		int size = frame.getSize();
		assertEquals(context + ": ZCLFrame.getSize(): must be equal to the buffer length returned by ZCLFrame.getBytes()", rawFrame.length, size);

		byte[] otherRawFrame = frame.getBytes();

		assertNotNull(context + ": ZCLHeader.getHeader(): the must return a not null ZCLHeader instance", header);
		assertNotSame(context + ": ZCLFrame.getBytes(): must return a different array instance each time it is called", otherRawFrame, rawFrame);

		ZigBeeDataInput is = frame.getDataInput();
		assertNotNull(context + ": ZCLFrame.getInputStream() must not return null", is);

		ZigBeeDataInput isOther = frame.getDataInput();
		assertNotNull(context + ": ZCLFrame.getDataInput(): calling this method twice must not return null", isOther);
		assertNotSame(context + ": ZCLFrame.getDataInput(): must return a different ZigBeeDataInput instance at each call.", is, isOther);

		/*
		 * Check the other flavor of getBytes()
		 */

		byte buffer[] = new byte[size + 20];

		int len = frame.getBytes(buffer);
		assertEquals(context + ": ZCLFrame.getBuffer(byte[] buffer): the returned length must be identical to ZCLFrame.getSize()", size, len);

		/*
		 * compares only the payload of the buffer with the payload of the raw
		 * frame
		 */
		for (int i = expectedHeaderSize; i < rawFrame.length; i++) {
			assertEquals(context + ": ZCLFrame: the payload section of the raw frame filled by ZCLFrame.getBytes(byte[] buffer) do not match with the ZCLFrame.getBytes() at index " + i,
					rawFrame[i],
					buffer[i]);
		}
	}
}
