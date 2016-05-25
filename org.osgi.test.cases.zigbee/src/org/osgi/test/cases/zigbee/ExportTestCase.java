/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;
import org.osgi.service.zigbee.descriptions.ZCLGlobalClusterDescription;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.osgi.service.zigbee.types.ZigBeeBoolean;
import org.osgi.service.zigbee.types.ZigBeeCharacterString;
import org.osgi.service.zigbee.types.ZigBeeEnumeration8;
import org.osgi.service.zigbee.types.ZigBeeUnsignedInteger8;
import org.osgi.test.cases.zigbee.impl.ZCLAttributeDescriptionImpl;
import org.osgi.test.cases.zigbee.impl.ZCLAttributeImpl;
import org.osgi.test.cases.zigbee.impl.ZCLClusterDescriptionImpl;
import org.osgi.test.cases.zigbee.impl.ZCLClusterImpl;
import org.osgi.test.cases.zigbee.impl.ZCLGlobalClusterDescriptionImpl;
import org.osgi.test.cases.zigbee.impl.ZigBeeEndpointImpl;
import org.osgi.test.cases.zigbee.impl.ZigBeeNodeDescriptorImpl;
import org.osgi.test.cases.zigbee.impl.ZigBeeNodeImpl;
import org.osgi.test.cases.zigbee.impl.ZigBeePowerDescriptorImpl;
import org.osgi.test.cases.zigbee.impl.ZigBeeSimpleDescriptorImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * ExportTestCase:
 * 
 * - testDeviceExport, tests device exportation: registers a local device as a
 * service to be exported by the base driver, checks that a chip ID has been
 * created and set as a property, sends a message on the EnOcean network, checks
 * that the message has been sent, received by the base driver, and checked by
 * the CT through the step service.
 *
 * @author $Id$
 */
public class ExportTestCase extends DefaultTestBundleControl {

	private final int desiredCount = 1;
	private ServicesListener listener;

	private ZCLAttribute[] attributesServer;
	private ZCLDataTypeDescription[] attributesType;
	private ZCLAttributeDescription[] attributesDescription;
	private ZCLGlobalClusterDescription globalDescription;
	private ZCLClusterDescription serverClusterDescription;
	private ZCLClusterDescription clientClusterDescription;
	private ZCLCluster[] serverClusters;
	private ZCLCluster[] clientClusters;
	private ZigBeeSimpleDescriptor simpledesc1;
	private ZigBeeEndpoint endpoint1;
	private ZigBeeNode node;

	private final String HOST_PID = "hardcoded hostPId";
	private final BigInteger IEEE_ADDRESS = BigInteger.valueOf(6628417744L);

	static public final String GET_EVENT = "Get_the_event_that_the_base_driver_should_have_received";

	protected void setUp() throws Exception {
		// log("Prepare for ZigBee Test Case");
		// prepareTestStart();
		// log("Prepared for ZigBee Test Case");
	}

	protected void tearDown() throws Exception {
		// log("Tear down ZigBee Test Case");
		// finalizeTestEnd();
		// log("Torn down ZigBee Test Case");
	}

	private void prepareTestStart() throws Exception {
		// log("Register Service Listener to listen for service changes");
		// listener = new ServicesListener(getContext(), desiredCount);
		// listener.open();
		// listener.waitFor(OSGiTestCaseProperties.getTimeout()
		// * OSGiTestCaseProperties.getScaling());
		// if (listener.size() < desiredCount) {
		// listener.close();
		// fail("timed out waiting for " + desiredCount + " ZigBee devices");
		// }
	}

	private void finalizeTestEnd() throws Exception {
		// listener.close();
	}

	/**
	 * Tests device exportation.
	 * 
	 * @throws Exception
	 */
	public void testDeviceExport() throws Exception {
		// log("--- testDeviceExport");
		//
		// BundleContext bc = getContext();
		//
		// // create here the node
		// createNode();
		//
		// // register endpoint1
		// log(this.getClass().getName() + " - Register endpoint: " + endpoint1
		// + " in the OSGi services registry");
		// ServiceRegistration endpointReg = bc.registerService(
		// ZigBeeEndpoint.class.getName(), endpoint1, null);
		//
		// // register node
		// log(this.getClass().getName() + " - Register (hardcoded) node1: "
		// + node + " in the OSGi services registry.");
		// ServiceRegistration nodeReg = bc.registerService(
		// ZigBeeNode.class.getName(), node, null);
		//
		// /* Wait for the proper and full registration */
		// Object lastServiceEvent = listener.waitForService(1000);
		//
		// assertNotNull("Timeout reached.", lastServiceEvent);
		//
		// // ZigBeeNode dev = listener.getLastZigBeeNode();
		// ZigBeeNode dev = listener.getZigBeeNode(IEEE_ADDRESS);
		//
		// assertTrue(
		// "The created IEEE Address for an exported node was not created",
		// IEEE_ADDRESS == dev.getIEEEAddress());
		// // send event
		// Dictionary events = new Hashtable();
		// events.put("Event Get", GET_EVENT);
		//
		// long IEEEAdress = 4263003265L;
		// short endpointId = 33;
		// int clusterId = 0x0006;
		// int attributeId = 0x0;
		// ZigBeeEvent aZigbeeEvent = new ZigBeeEventImpl(
		// BigInteger.valueOf(IEEEAdress), endpointId, clusterId,
		// attributeId, events);
		// ZigBeeEventSourceImpl aZigBeeEventSourceImpl = new
		// ZigBeeEventSourceImpl(
		// bc, aZigbeeEvent);
		// aZigBeeEventSourceImpl.start();
		// try {
		// // It takes several seconds for the event to "travel" inside the
		// // test framework.
		// int sleepinms = 1500;
		// log("Thread.sleep(" + sleepinms + ")");
		// Thread.sleep(sleepinms);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// fail("No exception is expected.");
		// }
		// TestStepProxy tproxy = new TestStepProxy(bc);
		// Map properties = new Hashtable();
		// // ZigBeeEvent evt = new ZigBeeEventImpl();
		// properties.put("IEEE_ADDRESS", IEEE_ADDRESS);
		//
		// String executionResult = tproxy
		// .execute(
		// GET_EVENT,
		// "An event has been sent by the test to the base driver. Just hit
		// [enter] to continue, and get this event.");
		// assertNotNull(
		// "The base driver didn't received the expected event (i.e., here, it
		// didn't received any event at all).",
		// executionResult);
		//
		// // try to get an answer with the handler for that node
		// ZigBeeHandlerImpl handler = new ZigBeeHandlerImpl();
		// dev.getUserDescription(handler);
		// handler.waitForResponse(ZigBeeControlTestCase.HANDLER_TIMEOUT);
		// String userDescription = (String) handler.getSuccessResponse();
		// assertNotNull("user description is NULL", userDescription);
		//
		// endpointReg.unregister();
		// nodeReg.unregister();

	}

	private void createNode() {

		// types
		attributesType = new ZCLDataTypeDescription[4];
		attributesType[0] = ZigBeeUnsignedInteger8.getInstance();
		attributesType[1] = ZigBeeCharacterString.getInstance();
		attributesType[2] = ZigBeeEnumeration8.getInstance();
		attributesType[3] = ZigBeeBoolean.getInstance();

		attributesDescription = new ZCLAttributeDescription[10];
		attributesDescription[0] = new ZCLAttributeDescriptionImpl(0x0000, false, new Integer(0x00), "ZCLVersion",
				false, false, attributesType[0]);
		attributesDescription[1] = new ZCLAttributeDescriptionImpl(0x0001, false, new Integer(0x00),
				"ApplicationVersion", false, false, attributesType[0]);
		attributesDescription[2] = new ZCLAttributeDescriptionImpl(0x0002, false, new Integer(0x00), "StackVersion",
				false, false, attributesType[0]);
		attributesDescription[3] = new ZCLAttributeDescriptionImpl(0x0003, false, new Integer(0x00), "HWVersion", false,
				false, attributesType[0]);
		attributesDescription[4] = new ZCLAttributeDescriptionImpl(0x0004, false, "", "ManufacturerName", false, false,
				attributesType[1]);
		attributesDescription[5] = new ZCLAttributeDescriptionImpl(0x0005, false, "", "ModelIdentifier", false, false,
				attributesType[1]);
		attributesDescription[6] = new ZCLAttributeDescriptionImpl(0x0006, false, "", "DateCode", false, false,
				attributesType[1]);
		attributesDescription[7] = new ZCLAttributeDescriptionImpl(0x0007, false, "", "PowerSource", false, false,
				attributesType[2]);
		attributesDescription[8] = new ZCLAttributeDescriptionImpl(0x0008, false, new Boolean(true), "DeviceEnabled",
				true, true, attributesType[3]);
		attributesDescription[9] = new ZCLAttributeDescriptionImpl(0x0008, true, new Boolean(true), "DeviceEnabled",
				true, true, attributesType[3]);

		attributesServer = new ZCLAttribute[10];
		attributesServer[0] = new ZCLAttributeImpl(attributesDescription[0]);
		attributesServer[1] = new ZCLAttributeImpl(attributesDescription[1]);
		attributesServer[2] = new ZCLAttributeImpl(attributesDescription[2]);
		attributesServer[3] = new ZCLAttributeImpl(attributesDescription[3]);
		attributesServer[4] = new ZCLAttributeImpl(attributesDescription[4]);
		attributesServer[5] = new ZCLAttributeImpl(attributesDescription[5]);
		attributesServer[6] = new ZCLAttributeImpl(attributesDescription[6]);
		attributesServer[7] = new ZCLAttributeImpl(attributesDescription[7]);
		attributesServer[8] = new ZCLAttributeImpl(attributesDescription[8]);
		attributesServer[9] = new ZCLAttributeImpl(attributesDescription[9]);
		globalDescription = new ZCLGlobalClusterDescriptionImpl(54, "Basic", "General", null, null);
		serverClusterDescription = new ZCLClusterDescriptionImpl(88, globalDescription);
		clientClusterDescription = new ZCLClusterDescriptionImpl(67, globalDescription);

		serverClusters = new ZCLClusterImpl[1];
		serverClusters[0] = new ZCLClusterImpl(new int[] { 0 }, null, serverClusterDescription);

		// node descriptor
		ZigBeeNodeDescriptor nodeDesc = new ZigBeeNodeDescriptorImpl(ZigBeeNode.ZED, (short) 868, new Integer(45), 36,
				false, false);
		ZigBeePowerDescriptor powerDesc = new ZigBeePowerDescriptorImpl((short) 2, (short) 1, (short) 1, true);

		clientClusters = new ZCLClusterImpl[1];
		clientClusters[0] = new ZCLClusterImpl(null, null, clientClusterDescription);

		simpledesc1 = new ZigBeeSimpleDescriptorImpl(6, (byte) 1, 5);
		short endpoint1id = (byte) 0x24;
		endpoint1 = new ZigBeeEndpointImpl(endpoint1id, serverClusters, null, simpledesc1);

		// nodes
		ZigBeeEndpoint[] endpointsNode1 = new ZigBeeEndpoint[1];
		endpointsNode1[0] = endpoint1;

		node = new ZigBeeNodeImpl(IEEE_ADDRESS, HOST_PID, endpointsNode1, nodeDesc, powerDesc, "user description");
	}

	private ZigBeeEndpoint getZigBeeEndpoint(Long endpointIeeeAddress) {
		ZigBeeEndpoint endpoint = null;
		try {
			ServiceReference[] srs = getContext().getAllServiceReferences(ZigBeeEndpoint.class.getName(), null);
			log("srs: " + srs);
			int srsIndex = 0;
			while (srsIndex < srs.length) {
				ServiceReference sr = srs[srsIndex];
				log("sr: " + sr);
				int j = 0;
				while (j < sr.getPropertyKeys().length) {
					log("sr.getPropertyKeys()[" + j + "]: " + sr.getPropertyKeys()[j] + ", sr.getProperty(key): "
							+ sr.getProperty(sr.getPropertyKeys()[j]));
					// [bnd] sr.getPropertyKeys()[0]: zigbee.node.ieee.address,
					// sr.getProperty(key): 8123456899
					// [bnd] sr.getPropertyKeys()[1]: zigbee.device.profile.id,
					// sr.getProperty(key): 0
					// [bnd] sr.getPropertyKeys()[2]: objectClass,
					// sr.getProperty(key): [Ljava.lang.String;@12f1eff
					// [bnd] sr.getPropertyKeys()[3]: service.id,
					// sr.getProperty(key): 29
					j = j + 1;
				}
				if (endpointIeeeAddress.equals(sr.getProperty(ZigBeeNode.IEEE_ADDRESS))) {
					// The sr's value associated to ZigBeeEndpoint.PROFILE_ID
					// may also be checked.
					endpoint = (ZigBeeEndpoint) getContext().getService(sr);
					break;
				}
				srsIndex = srsIndex + 1;
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			fail("No InvalidSyntaxException is expected", e);
		}
		return endpoint;
	}

	private ZigBeeNode getZigBeeNode(BigInteger nodeIeeeAddress) {
		ZigBeeNode node = null;
		try {
			ServiceReference[] srs = getContext().getAllServiceReferences(ZigBeeNode.class.getName(), null);
			log("srs: " + srs);
			int srsIndex = 0;
			while (srsIndex < srs.length) {
				ServiceReference sr = srs[srsIndex];
				log("sr: " + sr);
				int j = 0;
				while (j < sr.getPropertyKeys().length) {
					log("sr.getPropertyKeys()[" + j + "]: " + sr.getPropertyKeys()[j] + ", sr.getProperty(key): "
							+ sr.getProperty(sr.getPropertyKeys()[j]));
					// [bnd] sr.getPropertyKeys()[0]: zigbee.node.ieee.address,
					// sr.getProperty(key): 8123456899
					// [bnd] sr.getPropertyKeys()[1]: zigbee.device.profile.id,
					// sr.getProperty(key): 0
					// [bnd] sr.getPropertyKeys()[2]: objectClass,
					// sr.getProperty(key): [Ljava.lang.String;@12f1eff
					// [bnd] sr.getPropertyKeys()[3]: service.id,
					// sr.getProperty(key): 29
					j = j + 1;
				}
				if (nodeIeeeAddress.equals(sr.getProperty(ZigBeeNode.IEEE_ADDRESS))) {
					// The sr's value associated to ZigBeeEndpoint.PROFILE_ID
					// may also be checked.
					node = (ZigBeeNode) getContext().getService(sr);
					break;
				}
				srsIndex = srsIndex + 1;
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			fail("No InvalidSyntaxException is expected", e);
		}
		return node;
	}
}
