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
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.service.zigbee.ZDPException;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.ZigBeeLinkQuality;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.ZigBeeRoute;
import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.osgi.test.cases.zigbee.config.file.AttributeCoordinates;
import org.osgi.test.cases.zigbee.config.file.ZigBeeEndpointConfig;
import org.osgi.test.cases.zigbee.config.file.ZigBeeHostConfig;
import org.osgi.test.cases.zigbee.config.file.ZigBeeNodeConfig;
import org.osgi.test.cases.zigbee.configuration.ParserUtils;
import org.osgi.test.cases.zigbee.mock.StreamQueue;
import org.osgi.test.cases.zigbee.mock.ZCLEventListenerImpl;
import org.osgi.test.support.step.TestStepProxy;
import org.osgi.util.promise.Promise;

/**
 * Contains the ZigBee testcases related to the control of the status of the
 * service. They are testing the ZigBeeHost start() and stop() methods, and if
 * after a restart of the ZigBee network the services registered have the same
 * properties.
 * 
 * @author $Id$
 */
public class ZigBeeControlTestCase extends ZigBeeTestCases {

	private static final String	TAG			= ZigBeeControlTestCase.class.getName();

	private List				servicePids	= new ArrayList();

	/**
	 * Tests any registered ZigBeeNode service. For those services that are also
	 * defined in the CT configuration file more deeper checks are issued.
	 * 
	 * @throws Exception
	 */
	public void testZigBeeServices() throws Exception {
		log(TAG, "testing ZigBeeNodes and ZigBeeHost service methods and service properties");

		String context = "testZigBeeServices()";

		ZigBeeHost host = getZigBeeHost(DISCOVERY_TIMEOUT);

		/* starts the ZigBeeHost if not started, yet */

		context = "ZigBeeHost.stop()";

		log(TAG, context);

		/*
		 * If already started, stops it.
		 */
		if (host.isStarted()) {
			host.stop();

			/*
			 * Once the stop() method returns the ZigBeeHost.isStarted() must be
			 * false.
			 */
			assertEquals(context + ": ZigBeeHost.isStarted() wrong value", false, host.isStarted());
		}

		/*
		 * We check if some ZigBeeNode services are (still) registered.
		 */
		ServiceReference[] sRefs = getContext().getAllServiceReferences(ZigBeeNode.class.getName(), null);
		if (sRefs != null) {
			assertEquals(context + ": Found ZigBeeNode services while the ZigBeeHost is not in started state.", 0, sRefs.length);
		}

		/*
		 * We check if some ZigBeeEndpoint services are (still) registered
		 * (excluding exported ones).
		 */
		sRefs = getContext().getAllServiceReferences(ZigBeeEndpoint.class.getName(), ZIGBEE_NOT_EXPORT_FILTER);
		if (sRefs != null) {
			assertEquals(context + ": Found ZigBeeEndpoint services while the ZigBeeHost is not in started state.", 0, sRefs.length);
		}

		context = "ZigBeeHost.start()";

		/*
		 * Let's go: start the ZigBeeHost again.
		 */
		host.start();

		/*
		 * Wait until all the ZigBeeNodes have been discovered.
		 */
		Thread.sleep(conf.getDiscoveryTimeout());

		sRefs = getContext().getAllServiceReferences(ZigBeeNode.class.getName(), null);

		/*
		 * First of all we check the ZigBeeNode services.
		 */
		for (int i = 0; i < sRefs.length; i++) {
			ServiceReference sRefNode = sRefs[i];
			Map serviceProperties = getProperties(sRefNode);
			ZigBeeNode node = (ZigBeeNode) getContext().getService(sRefNode);

			ZigBeeNodeConfig nodeConfig = conf.getNodeConfig(node.getIEEEAddress());
			testZigBeeNode(host, node, serviceProperties, nodeConfig);

			String servicePid = (String) serviceProperties.get("service.pid");
			if (servicePids.contains(servicePid)) {
				fail(context + ": duplicate service pid " + servicePid);
			}

			servicePids.add(servicePid);

			getContext().ungetService(sRefNode);

			String ieeeAddressFilter = "(" + ZigBeeNode.IEEE_ADDRESS + "=" + node.getIEEEAddress() + ")";
			ServiceReference[] sRefsEndpoints = getContext().getAllServiceReferences(ZigBeeEndpoint.class.getName(), ieeeAddressFilter);

			for (int j = 0; j < sRefsEndpoints.length; j++) {
				ZigBeeEndpointConfig endpointConfig = null;
				if (nodeConfig != null) {
					endpointConfig = conf.getEndpointConfig(nodeConfig);
				}

				ServiceReference sRefEndpoint = sRefsEndpoints[j];

				serviceProperties = this.getProperties(sRefEndpoint);

				ZigBeeEndpoint endpoint = (ZigBeeEndpoint) getContext().getService(sRefEndpoint);
				testZigBeeEndpoint(node, endpoint, serviceProperties, endpointConfig);

				String endpointServicePid = (String) serviceProperties.get("service.pid");

				if (servicePids.contains(endpointServicePid)) {
					fail(context + ": duplicate service pid " + endpointServicePid);
				}

				servicePids.add(endpointServicePid);

				getContext().ungetService(sRefEndpoint);
			}
		}

		/*
		 * Check the registered ZigBeeHost service.
		 */

		ServiceReference hostServiceRef = getZigBeeHostServiceReference();
		Map serviceProperties = getProperties(hostServiceRef);
		testZigBeeHost(host, serviceProperties, conf.getZigBeeHost(), sRefs);
		String hostServicePid = (String) serviceProperties.get("service.pid");
		if (servicePids.contains(hostServicePid)) {
			fail(context + ": duplicate service pid " + hostServicePid);
		}

		servicePids.add(hostServicePid);
	}

	/**
	 * Gets the ZigBeeHost service that corresponds to the one that is defined
	 * in the CT configuration file.
	 * 
	 * @return A ZigBeeHost service instance.
	 */

	protected ZigBeeHost getZigBeeHostService() {

		ZigBeeHostConfig hostConfig = conf.getZigBeeHost();
		/*
		 * Wait till a ZigBeeHost service with the requested IEEE address is
		 * registered by the ZigBee Device Service implementation.
		 */

		String ieeeAddressFilter = "(" + ZigBeeHost.IEEE_ADDRESS + "=" + hostConfig.getIEEEAddress() + ")";
		ServiceReference hostServiceRef = waitForServiceReference(ZigBeeHost.class.getName(), ieeeAddressFilter, DISCOVERY_TIMEOUT);
		assertNotNull("the ZigBeeHost service was not found registered within the configured discovery timeout", hostServiceRef);

		ZigBeeHost host = (ZigBeeHost) getContext().getService(hostServiceRef);

		return host;
	}

	/**
	 * Check some ZigBeeHost methods by passing wrong arguments or by calling
	 * them while the ZigBee host is in a state that do not allow to call them.
	 * 
	 * @throws Exception In case of error.
	 */
	public void testZigBeeHostMethods() throws Exception {

		log(TAG, "test ZigBeeHost service methods.");

		String context = "testZigBeeHostMethods()";

		ZigBeeHostConfig hostConfig = conf.getZigBeeHost();

		/*
		 * Wait till a ZigBeeHost service with the requested IEEE address is
		 * registered by the ZigBee Device Service implementation.
		 */

		String ieeeAddressFilter = "(" + ZigBeeHost.IEEE_ADDRESS + "=" + hostConfig.getIEEEAddress() + ")";
		ServiceReference hostServiceRef = waitForServiceReference(ZigBeeHost.class.getName(), ieeeAddressFilter, conf.getDiscoveryTimeout());

		assertNotNull(context + ": The ZigBeeHost service was not found registered within the configured discovery timeout", hostServiceRef);

		ZigBeeHost host = (ZigBeeHost) getContext().getService(hostServiceRef);

		/*
		 * If ZigBeeHost is not started, yet starts it!
		 */
		if (!host.isStarted()) {
			host.start();
		}

		context = "ZigBeeHost.setBroadcastRadius()";

		try {
			host.setBroadcastRadius((short) 0x01);
			fail(context + ":  must throw an IllegalStateException while ZigBeeHost in not stopped state.");
		} catch (IllegalStateException e) {
			/* Success */
		}

		try {
			host.setBroadcastRadius((short) -1);
			fail(context + ":  must throw an IllegalStateException or IllegalArgumentException while ZigBeeHost in not stopped state and the passed value in not in the range [0, 0xff].");
		} catch (IllegalStateException e) {
			/* Success */
		} catch (IllegalArgumentException e) {
			/* Success */
		}

		try {
			host.setBroadcastRadius((short) 0x100);
			fail(context + ":  must throw an IllegalStateException or IllegalArgumentException while ZigBeeHost in not stopped state and the passed value in not in the range [0, 0xff].");
		} catch (IllegalStateException e) {
			/* Success */
		} catch (IllegalArgumentException e) {
			/* Success */
		}

		context = "ZigBeeHost.setPanId()";

		try {
			host.setPanId((short) 0xf000);
			fail(context + ":  must throw an IllegalStateException while ZigBeeHost is not stopped.");
		} catch (IllegalStateException e) {
			/* Success */
		}

		try {
			host.setPanId((short) -1);
			fail(context + ":  must throw an IllegalStateException or IllegalArgumentException while ZigBeeHost not stopped and the passed value in not in the range [0, 0xff].");
		} catch (IllegalStateException e) {
			/* Success */
		} catch (IllegalArgumentException e) {
			/* Success */
		}

		try {
			host.setPanId((short) 0x1000);
			fail(context + ":  must throw an IllegalStateException or IllegalArgumentException while ZigBeeHost not stopped and the passed value in not in the range [0, 0xff].");
		} catch (IllegalStateException e) {
			/* Success */
		} catch (IllegalArgumentException e) {
			/* Success */
		}

		context = "ZigBeeHost.permitJoin()";

		try {
			host.permitJoin((short) -1);
			fail(context + ":  must throw an IllegalArgumentException because the passed value in not in the range [0, 0xff].");
		} catch (IllegalArgumentException e) {
			/* Success */
		}

		try {
			host.permitJoin((short) 0x100);
			fail(context + ":  must throw an IllegalArgumentException because the passed value in not in the range [0, 0xff].");
		} catch (IllegalArgumentException e) {
			/* Success */
		}

		try {
			host.setCommunicationTimeout(0);
			fail(context + ":  must throw an IllegalArgumentException because the passed value must greater than zero.");
		} catch (IllegalArgumentException e) {
			/* Success */
		}
	}

	/**
	 * Test a ZigBeeHost service. It performs any cross check that it is
	 * possible to do with the provided information. In particular:
	 * <ul>
	 * <li>If the hostConfig (that is the node ZigBeeHostConfig object created
	 * by reading the CT configuration file, its content is compared with the
	 * ZigBee host values returned by the ZigBeeHost interface methods.
	 * <li>Compares the ZigBeeHost service properties with the information
	 * retrieved by the ZigBeeHost interface methods.
	 * </ul>
	 * 
	 * @throws Exception In case of unexpected exceptions.
	 */
	private void testZigBeeHost(ZigBeeHost host, Map serviceProperties, ZigBeeHostConfig hostConfig, ServiceReference[] nodesServiceReferences) throws Exception {
		log(TAG, "test ZigBeeHost service and service properties of ZigBeeHost " + toHexString(host.getIEEEAddress(), 16));

		String name = "ZigBeeHost";
		String context = name + ".getNodeDescriptor()";

		log(TAG, "testing " + context);

		Promise p = host.getNodeDescriptor();
		waitForPromise(p, INVOKE_TIMEOUT);

		ZigBeeNodeDescriptor nodeDesc = null;

		if (p.getFailure() == null) {
			nodeDesc = (ZigBeeNodeDescriptor) assertPromiseValueClass(p, ZigBeeNodeDescriptor.class);
			assertNotNull(context + ": cannot return null", nodeDesc);

			if (hostConfig != null) {
				this.checkNodeDescriptor(hostConfig.getNodeDescriptor(), nodeDesc);
			}
		} else {
			fail(context + ": failure in retrieving the ZigBeeNodeDescriptor", p.getFailure());
		}

		context = name + ".setUserDescription()";

		log(TAG, "testing " + context);

		String expectedDescription = "my user descriptor";

		p = host.setUserDescription(expectedDescription);
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			if (!nodeDesc.isUserDescriptorAvailable()) {
				fail(context + ": ZigBeeNodeDescriptor states that the UserDescriptor must not be available, so we expects the promise to fail.");
			}

			assertNull(context + ": a successful user description set requires the promise to resolve to null", p.getValue());
		} else {
			if (!nodeDesc.isUserDescriptorAvailable()) {
				/*
				 * The complex descriptor is not available. We expect the
				 * NO_DESCRIPTOR failure
				 */
				assertPromiseZDPException(p, context + ": if the descriptor is missing the promise must fail with a different error code", ZDPException.NO_DESCRIPTOR);
			} else {
				fail(context + ": we didn't this exception here", p.getFailure());
			}
		}

		/*
		 * If the UserDescriptor is supported, then tries to retrieve the
		 * current value and compare it with the value set above.
		 */

		context = name + ".getUserDescription()";
		log(TAG, "testing " + context);

		p = host.getUserDescription();
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			if (!nodeDesc.isUserDescriptorAvailable()) {
				fail(context + ": ZigBeeNodeDescriptor states that the UserDescriptor must not be available, so we expects the promise to fail.");
			}

			String userDescription = (String) assertPromiseValueClass(p, String.class);

			assertNotNull(context + ": cannot return null.", userDescription);
			assertEquals("user description mismatch", expectedDescription, userDescription);
		} else {
			if (!nodeDesc.isUserDescriptorAvailable()) {
				/*
				 * The complex descriptor is not available. We expect the
				 * NO_DESCRIPTOR failure
				 */
				assertPromiseZDPException(p, context + ": if the descriptor is missing the promise must fail with a different error code", ZDPException.NO_DESCRIPTOR);
			} else {
				fail(context + ": we didn't this exception here", p.getFailure());
			}
		}

		context = name + ".getPowerDescriptor()";

		log(TAG, "testing " + context);

		ZigBeePowerDescriptor powerDesc = null;
		p = host.getPowerDescriptor();
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() == null) {
			powerDesc = (ZigBeePowerDescriptor) assertPromiseValueClass(p, ZigBeePowerDescriptor.class);
			assertNotNull(context + " cannot return null", powerDesc);
		} else {
			fail(context + ": an exeption is not expected here", p.getFailure());
		}

		context = name + ".getComplexDescriptor()";

		log(TAG, "testing " + context);

		p = host.getComplexDescriptor();
		waitForPromise(p, INVOKE_TIMEOUT);
		ZigBeeComplexDescriptor complexDesc = null;

		if (p.getFailure() == null) {
			if (!nodeDesc.isComplexDescriptorAvailable()) {
				fail(context + ": ZigBeeNodeDescriptor states that the ComplexDescriptor must not be available, so we expects the promise to fail.");
			}

			complexDesc = (ZigBeeComplexDescriptor) assertPromiseValueClass(p, ZigBeeComplexDescriptor.class);
			assertNotNull(context + ": the descriptor cannot be null", complexDesc);
		} else {
			if (!nodeDesc.isComplexDescriptorAvailable()) {
				/*
				 * The complex descriptor is not available. We expect the
				 * NO_DESCRIPTOR failure
				 */
				assertPromiseZDPException(p, context + ": if the descriptor is missing the promise must fail with a different error code", ZDPException.NO_DESCRIPTOR);
			} else {
				fail(context + ": we didn't this exception here", p.getFailure());
			}
		}

		context = name + ".getLinksQuality()";

		log(TAG, "testing " + context);

		p = host.getLinksQuality();
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			Map linksQualityMap = (Map) assertPromiseValueClass(p, Map.class);
			assertNotNull(context + ": cannot return null", linksQualityMap);
			assertMapContent(context, linksQualityMap, String.class, ZigBeeLinkQuality.class, -1);
		} else {
			assertPromiseZDPException(p, context + ": if not available, the promise must fail with a different error code", ZDPException.NOT_SUPPORTED);
		}

		p = host.getRoutingTable();
		waitForPromise(p, INVOKE_TIMEOUT);

		context = name + ".getRoutingTable()";

		if (p.getFailure() == null) {
			Map routingTable = (Map) assertPromiseValueClass(p, Map.class);
			assertNotNull(context + ": cannot return null", routingTable);
			assertMapContent(context, routingTable, String.class, ZigBeeRoute.class, -1);

		} else {
			assertPromiseZDPException(p, context + ": if not available, the promise must fail with a different error code", ZDPException.NOT_SUPPORTED);
		}

		context = name + ".getIEEEAddress()";
		log(TAG, "testing " + context);
		BigInteger ieeeAddress = host.getIEEEAddress();

		assertNotNull(context + ": cannot return null", ieeeAddress);
		assertEquals(context + ": returned IEEEAddress differs from the CT configuration one", ieeeAddress, hostConfig.getIEEEAddress());

		context = name + ".getExtendedPanId()";
		log(TAG, "testing " + context);
		BigInteger extendedPanId = host.getExtendedPanId();
		assertNotNull(context + ": cannot return null", extendedPanId);

		context = name + ".getHostPid()";
		log(TAG, "testing " + context);
		String hostPid = host.getHostPid();

		assertNotNull(context + ": cannot return null", hostPid);

		context = name + ".getPanId()";
		log(TAG, "testing " + context);
		int panId = host.getPanId();
		if (panId < 0 || panId > 0xffff) {
			fail(context + ": panId must be in the range [0, 0xffff]");
		}

		/*
		 * Checks the ZigBeeHost service properties.
		 */
		context = "ZigBeeHost serviceProperties";

		log(context + ": checking service properties of node " + toHexString(host.getIEEEAddress(), 16));

		ieeeAddress = (BigInteger) getParameterChecked(context, serviceProperties, ZigBeeNode.IEEE_ADDRESS, ParserUtils.MANDATORY, BigInteger.class);
		Short logicalType = (Short) getParameterChecked(context, serviceProperties, ZigBeeNode.LOGICAL_TYPE, ParserUtils.MANDATORY, Short.class);
		Integer manufacturerCode = (Integer) getParameterChecked(context, serviceProperties, ZigBeeNode.MANUFACTURER_CODE, ParserUtils.MANDATORY, Integer.class);
		Boolean receiverOnWhenIdle = (Boolean) getParameterChecked(context, serviceProperties, ZigBeeNode.RECEIVER_ON_WHEN_IDLE, ParserUtils.MANDATORY, Boolean.class);
		Boolean powerSource = (Boolean) getParameterChecked(context, serviceProperties, ZigBeeNode.POWER_SOURCE, ParserUtils.MANDATORY, Boolean.class);

		Integer panIdProperty = (Integer) getParameterChecked(context, serviceProperties, ZigBeeNode.PAN_ID, ParserUtils.OPTIONAL, Integer.class);
		extendedPanId = (BigInteger) getParameterChecked(context, serviceProperties, ZigBeeNode.EXTENDED_PAN_ID, ParserUtils.OPTIONAL, BigInteger.class);

		if (panIdProperty == null && extendedPanId == null) {
			/**
			 * We need one of them or both.
			 */

			fail(context + ": ZigBeeNode.PAN_ID or ZigBeeNode.EXTENDED_PAN_ID have to be present.");
		}

		assertEquals(context + ": mismatch with ZigBeeHost.getIEEEAddress()", host.getIEEEAddress(), ieeeAddress);
		assertEquals(context + ": mismatch with ZigBeeNodeDescriptor.getLogicalType()", nodeDesc.getLogicalType(), logicalType.shortValue());
		assertEquals(context + ": mismatch with ZigBeeNodeDescriptor.getManufacturerCode()", nodeDesc.getManufacturerCode(), manufacturerCode.intValue());
		assertEquals(context + ": mismatch with ZigBeeNodeDescriptor.getMacCapabilityFlags().isReceiverOnWhenIdle()",
				nodeDesc.getMacCapabilityFlags().isReceiverOnWhenIdle(),
				receiverOnWhenIdle.booleanValue());

		assertEquals(context + ": mismatch with ZigBeeNodeDescriptor.getMacCapabilityFlags().isMainsPower()",
				nodeDesc.getMacCapabilityFlags().isMainsPower(),
				powerSource.booleanValue());

		if (panId != -1) {
			assertEquals(context + ": mismatch with ZigBeeNode.getPanId()", panIdProperty.intValue(), host.getPanId());
		}

		if (extendedPanId != null) {
			assertEquals(context + ": mismatch with ZigBeeNode.getExtendedPanId()", extendedPanId, host.getExtendedPanId());
		}

		/*
		 * Check the Device Access specification DEVICE_CATEGORY service
		 * property.
		 */

		this.assertDeviceCategory(context, serviceProperties, ZigBeeEndpoint.DEVICE_CATEGORY);

		if (nodeDesc.isComplexDescriptorAvailable() && complexDesc != null) {
			/*
			 * Check the presence of the DEVICE_SERIAL and DEVICE_DESCRIPTION
			 * properties.
			 */

			String serialNumber = (String) getParameterChecked(context, serviceProperties, Constants.DEVICE_SERIAL, ParserUtils.OPTIONAL, String.class);
			assertNotNull(context + ": since the ZigBeeComplexDescriptor is available, the DEVICE_SERIAL service property is mandatory", serialNumber);
			assertEquals(context + ": since the ZigBeeComplexDescriptor is available, the DEVICE_SERIAL property must match its SerialNumber", complexDesc.getSerialNumber(), serialNumber);

			String description = (String) getParameterChecked(context, serviceProperties, Constants.DEVICE_SERIAL, ParserUtils.OPTIONAL, String.class);
			assertNotNull(context + ": since the ZigBeeComplexDescriptor is available, the DEVICE_DESCRIPTION service property is mandatory", description);
			assertEquals(context + ": since the ZigBeeComplexDescriptor is available, the DEVICE_DESCRIPTION property must match its SerialNumber", complexDesc.getModelName(), description);
		}
	}

	/**
	 * Test a ZigBeeNode service. It performs any cross check that it is
	 * possible to do with the provided information. In particular:
	 * <ul>
	 * <li>If the nodeConfig (that is the node ZigBeeNodeConfig object created
	 * by reading the CT configuration file, its content is compared with the
	 * ZigBee node values returned by the ZigBeeNode interface methods.
	 * <li>Compares the ZigBeeNode service properties with the information
	 * retrieved by the ZigBeeNode interface methods.
	 * </ul>
	 * 
	 * @throws Exception In case of unexpected exceptions.
	 */
	private void testZigBeeNode(ZigBeeHost host, ZigBeeNode node, Map serviceProperties, ZigBeeNodeConfig nodeConfig) throws Exception {
		log(TAG, "test ZigBeeNode service methods and service properties for ZigBeeNode " + toHexString(node.getIEEEAddress(), 16));

		String name = "ZigBeeNode";
		/*
		 * The ZigBeeNode services comes along with the expected number of
		 * ZigBeeEndpoint services?
		 */
		if (nodeConfig != null) {
			int expectedZigBeeEndpointNumber = nodeConfig.getActualEndpointsNumber();

			int registeredZigBeeEndpointsNumber = getZigBeeEndpointsNumber(nodeConfig.getIEEEAddress());
			assertEquals(
					"The number of registered endpoints by the baseDriver is not the same as declared in the configuration file for the node with IEEE Address:"
							+ nodeConfig.getIEEEAddress(),
					expectedZigBeeEndpointNumber,
					registeredZigBeeEndpointsNumber);
		}

		String context = name + ".getNodeDescriptor()";

		log(TAG, "testing " + context);

		Promise p = node.getNodeDescriptor();
		waitForPromise(p, INVOKE_TIMEOUT);

		ZigBeeNodeDescriptor nodeDesc = null;

		if (p.getFailure() == null) {
			nodeDesc = (ZigBeeNodeDescriptor) assertPromiseValueClass(p, ZigBeeNodeDescriptor.class);
			assertNotNull(context + ": cannot return null", nodeDesc);

			if (nodeConfig != null) {
				this.checkNodeDescriptor(nodeConfig.getNodeDescriptor(), nodeDesc);
			}
		} else {
			fail(context + ": failure in retrieving the ZigBeeNodeDescriptor", p.getFailure());
		}

		context = name + ".setUserDescription()";

		log(TAG, "testing " + context);

		String expectedDescription = "my user descriptor";

		p = node.setUserDescription(expectedDescription);
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			if (!nodeDesc.isUserDescriptorAvailable()) {
				fail(context + ": ZigBeeNodeDescriptor states that the UserDescriptor must not be available, so we expects the promise to fail.");
			}

			assertNull(context + ": a successful user description set requires the promise to resolve to null", p.getValue());
		} else {
			if (!nodeDesc.isUserDescriptorAvailable()) {
				/*
				 * The complex descriptor is not available. We expect the
				 * NO_DESCRIPTOR failure
				 */
				assertPromiseZDPException(p, context + ": if the descriptor is missing the promise must fail with a different error code", ZDPException.NO_DESCRIPTOR);
			} else {
				fail(context + ": we didn't this exception here", p.getFailure());
			}
		}

		/*
		 * If the UserDescriptor is supported, then tries to retrieve the
		 * current value and compare it with the value set above.
		 */

		context = name + ".getUserDescription()";
		log(TAG, "testing " + context);

		p = node.getUserDescription();
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			if (!nodeDesc.isUserDescriptorAvailable()) {
				fail(context + ": ZigBeeNodeDescriptor states that the UserDescriptor must not be available, so we expects the promise to fail.");
			}

			String userDescription = (String) assertPromiseValueClass(p, String.class);

			assertNotNull(context + ": cannot return null.", userDescription);
			assertEquals("user description mismatch", expectedDescription, userDescription);
		} else {
			if (!nodeDesc.isUserDescriptorAvailable()) {
				/*
				 * The complex descriptor is not available. We expect the
				 * NO_DESCRIPTOR failure
				 */
				assertPromiseZDPException(p, context + ": if the descriptor is missing the promise must fail with a different error code", ZDPException.NO_DESCRIPTOR);
			} else {
				fail(context + ": we didn't this exception here", p.getFailure());
			}
		}

		context = name + ".getPowerDescriptor()";

		log(TAG, "testing " + context);

		p = node.getPowerDescriptor();
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() == null) {
			ZigBeePowerDescriptor powerDesc = (ZigBeePowerDescriptor) assertPromiseValueClass(p, ZigBeePowerDescriptor.class);
			assertNotNull(context + " cannot return null", powerDesc);

			/*
			 * Checks if the returned ZigBeePowerDescriptor matches the expected
			 * one.
			 */
			if (nodeConfig != null) {
				ZigBeePowerDescriptor expectedPowerDesc = nodeConfig.getPowerDescriptor();
				this.checkPowerDescriptor(expectedPowerDesc, powerDesc);
			}
		} else {
			fail(context + ": an exeption is not expected here", p.getFailure());
		}

		context = name + ".getComplexDescriptor()";

		log(TAG, "testing " + context);

		p = node.getComplexDescriptor();
		waitForPromise(p, INVOKE_TIMEOUT);

		ZigBeeComplexDescriptor complexDesc = null;

		if (p.getFailure() == null) {
			if (!nodeDesc.isComplexDescriptorAvailable()) {
				fail(context + ": ZigBeeNodeDescriptor states that the ComplexDescriptor must not be available, so we expects the promise to fail.");
			}

			complexDesc = (ZigBeeComplexDescriptor) assertPromiseValueClass(p, ZigBeeComplexDescriptor.class);
			assertNotNull(context + ": the descriptor cannot be null", complexDesc);

			if (nodeConfig != null) {
				/*
				 * Checks if the returned ZigBeeComplexDescriptor matches the
				 * expected one.
				 */

				ZigBeeComplexDescriptor expectedComplexDesc = nodeConfig.getComplexDescriptor();
				this.checkComplexDescriptor(expectedComplexDesc, complexDesc);
			}
		} else {
			if (!nodeDesc.isComplexDescriptorAvailable()) {
				/*
				 * The complex descriptor is not available. We expect the
				 * NO_DESCRIPTOR failure
				 */
				assertPromiseZDPException(p, context + ": if the descriptor is missing the promise must fail with a different error code", ZDPException.NO_DESCRIPTOR);
			} else {
				fail(context + ": we didn't this exception here", p.getFailure());
			}
		}

		context = name + ".getLinksQuality()";

		log(TAG, "testing " + context);

		p = node.getLinksQuality();
		waitForPromise(p, INVOKE_TIMEOUT);

		if (p.getFailure() == null) {
			Map linksQualityMap = (Map) assertPromiseValueClass(p, Map.class);
			assertNotNull(context + ": cannot return null", linksQualityMap);
			assertMapContent(context, linksQualityMap, String.class, ZigBeeLinkQuality.class, -1);
		} else {
			assertPromiseZDPException(p, context + ": if not available, the promise must fail with a different error code", ZDPException.NOT_SUPPORTED);
		}

		p = node.getRoutingTable();
		waitForPromise(p, INVOKE_TIMEOUT);

		context = name + ".getRoutingTable()";

		if (p.getFailure() == null) {
			Map routingTable = (Map) assertPromiseValueClass(p, Map.class);
			assertNotNull(context + ": cannot return null", routingTable);
			assertMapContent(context, routingTable, String.class, ZigBeeRoute.class, -1);

		} else {
			assertPromiseZDPException(p, context + ": if not available, the promise must fail with a different error code", ZDPException.NOT_SUPPORTED);
		}

		context = name + ".getIEEEAddress()";
		log(TAG, "testing " + context);
		BigInteger ieeeAddress = node.getIEEEAddress();

		assertNotNull(context + ": cannot return null", ieeeAddress);
		assertEquals(context + ": returned IEEEAddress differs from the CT configuration one", ieeeAddress, nodeConfig.getIEEEAddress());

		context = name + ".getExtendedPanId()";
		log(TAG, "testing " + context);
		BigInteger extendedPanId = node.getExtendedPanId();
		assertNotNull(context + ": cannot return null", extendedPanId);
		assertEquals(context + ": returned EPID differs from the ZigBeeHost one.", extendedPanId, host.getExtendedPanId());

		context = name + ".getHostPid()";
		log(TAG, "testing " + context);
		String hostPid = node.getHostPid();

		assertNotNull(context + ": cannot return null", hostPid);
		assertEquals(context + ": returned hostPid differs from the ZigBeeHost one.", hostPid, host.getHostPid());

		context = name + ".getPanId()";
		log(TAG, "testing " + context);
		int panId = node.getPanId();

		assertEquals(context + ": returned panId differs from the ZigBeeHost one.", panId, host.getPanId());

		/*
		 * Checks the ZigBeeNode service properties.
		 */
		context = "ZigBeeHost serviceProperties";

		log(context + ": checking service properties of ZigBeeHost " + toHexString(host.getIEEEAddress(), 16));

		ieeeAddress = (BigInteger) getParameterChecked(context, serviceProperties, ZigBeeNode.IEEE_ADDRESS, ParserUtils.MANDATORY, BigInteger.class);
		Short logicalType = (Short) getParameterChecked(context, serviceProperties, ZigBeeNode.LOGICAL_TYPE, ParserUtils.MANDATORY, Short.class);
		Integer manufacturerCode = (Integer) getParameterChecked(context, serviceProperties, ZigBeeNode.MANUFACTURER_CODE, ParserUtils.MANDATORY, Integer.class);
		Boolean receiverOnWhenIdle = (Boolean) getParameterChecked(context, serviceProperties, ZigBeeNode.RECEIVER_ON_WHEN_IDLE, ParserUtils.MANDATORY, Boolean.class);
		Boolean powerSource = (Boolean) getParameterChecked(context, serviceProperties, ZigBeeNode.POWER_SOURCE, ParserUtils.MANDATORY, Boolean.class);

		Integer panIdProperty = (Integer) getParameterChecked(context, serviceProperties, ZigBeeNode.PAN_ID, ParserUtils.OPTIONAL, Integer.class);
		extendedPanId = (BigInteger) getParameterChecked(context, serviceProperties, ZigBeeNode.EXTENDED_PAN_ID, ParserUtils.OPTIONAL, BigInteger.class);

		/* The service Pid uniqueness is checked outside this test method. */
		String servicePid = (String) getParameterChecked(context, serviceProperties, "service.pid", ParserUtils.MANDATORY, String.class);

		if (panIdProperty == null && extendedPanId == null) {
			/**
			 * We need one of them or both.
			 */

			fail(context + ": ZigBeeNode.PAN_ID or ZigBeeNode.EXTENDED_PAN_ID have to be present.");
		}

		assertEquals(context + ": mismatch with ZigBeeNode.getIEEEAddress()", node.getIEEEAddress(), ieeeAddress);
		assertEquals(context + ": mismatch with ZigBeeNodeDescriptor.getLogicalType()", nodeDesc.getLogicalType(), logicalType.shortValue());
		assertEquals(context + ": mismatch with ZigBeeNodeDescriptor.getManufacturerCode()", nodeDesc.getManufacturerCode(), manufacturerCode.intValue());
		assertEquals(context + ": mismatch with ZigBeeNodeDescriptor.getMacCapabilityFlags().isReceiverOnWhenIdle()",
				nodeDesc.getMacCapabilityFlags().isReceiverOnWhenIdle(),
				receiverOnWhenIdle.booleanValue());

		assertEquals(context + ": mismatch with ZigBeeNodeDescriptor.getMacCapabilityFlags().isMainsPower()",
				nodeDesc.getMacCapabilityFlags().isMainsPower(),
				powerSource.booleanValue());

		if (panId != -1) {
			assertEquals(context + ": mismatch with ZigBeeNode.getPanId()", panIdProperty.intValue(), host.getPanId());
		}

		if (extendedPanId != null) {
			assertEquals(context + ": mismatch with ZigBeeNode.getExtendedPanId()", extendedPanId, host.getExtendedPanId());
		}

		/*
		 * Check the DEVICE_CATEGORY service property.
		 */

		String deviceCategory = (String) getParameterChecked(context, serviceProperties, Constants.DEVICE_CATEGORY, ParserUtils.MANDATORY, String.class);
		assertEquals(context + ": " + Constants.DEVICE_CATEGORY, ZigBeeEndpoint.DEVICE_CATEGORY, deviceCategory);

		if (nodeDesc.isComplexDescriptorAvailable() && complexDesc != null) {
			/*
			 * Check the presence of the DEVICE_SERIAL and DEVICE_DESCRIPTION
			 * properties.
			 */

			String serialNumber = (String) getParameterChecked(context, serviceProperties, Constants.DEVICE_SERIAL, ParserUtils.OPTIONAL, String.class);
			assertNotNull(context + ": since the ZigBeeComplexDescriptor is available, the DEVICE_SERIAL service property is mandatory", serialNumber);
			assertEquals(context + ": since the ZigBeeComplexDescriptor is available, the DEVICE_SERIAL property must match its SerialNumber", complexDesc.getSerialNumber(), serialNumber);

			String description = (String) getParameterChecked(context, serviceProperties, Constants.DEVICE_SERIAL, ParserUtils.OPTIONAL, String.class);
			assertNotNull(context + ": since the ZigBeeComplexDescriptor is available, the DEVICE_DESCRIPTION service property is mandatory", description);
			assertEquals(context + ": since the ZigBeeComplexDescriptor is available, the DEVICE_DESCRIPTION property must match its SerialNumber", complexDesc.getModelName(), description);
		}
	}

	private void testZigBeeEndpoint(ZigBeeNode node, ZigBeeEndpoint endpoint, Map serviceProperties, ZigBeeEndpointConfig endpointConfig) throws Exception {
		log(TAG, "test ZigBeeEndpoint service methods and service properties for ZigBeeEndpoint " + printScope(endpoint));

		String interfaceName = "ZigBeeEndpoint";

		String context = interfaceName + ".getSimpleDescriptor()";

		log(TAG, "testing " + context);

		ZigBeeSimpleDescriptor simpleDesc = null;

		Promise p = endpoint.getSimpleDescriptor();
		waitForPromise(p, INVOKE_TIMEOUT);
		if (p.getFailure() == null) {
			simpleDesc = (ZigBeeSimpleDescriptor) assertPromiseValueClass(p, ZigBeeSimpleDescriptor.class);
			assertNotNull(context + ": cannot return null", simpleDesc);

			/*
			 * Checks if the returned ZigBeePowerDescriptor matches the expected
			 * one.
			 */
			if (endpointConfig != null) {
				ZigBeeSimpleDescriptor expectedSimpleDescriptor = endpointConfig.getSimpleDescriptor();
				this.checkSimpleDescriptor(expectedSimpleDescriptor, simpleDesc);
			}
		} else {
			fail(context + ": an exeption is not expected here", p.getFailure());
		}

		/*
		 * Checks if the returned ZigBeeSimpleDescriptor is compliant with the
		 * ZigBeeEndpoint service methods.
		 */

		context = interfaceName + ".getSimpleDescriptor()";
		assertEquals(context + ": mismatch against ZigBeeEndpoint.getId()", endpoint.getId(), simpleDesc.getEndpoint());

		/*
		 * Tests the following methods available in the ZigBeeEndpoint interface
		 * and if the passed endpointConfig argument is not null, then check if
		 * the ZigBeeEndpoint clusters matches the expected one.
		 * 
		 * ZigBeeEndpoint.getServerClusters(),
		 * ZigBeeEndpoint.getServerCluster(clusterId)
		 * ZigBeeEndpoint.getClientClusters(),
		 * ZigBeeEndpoint.getClientCluster(clusterId) </ul>
		 */

		/* Server clusters */
		this.testZigBeeEndpointClusterMethods(endpoint, simpleDesc, endpointConfig, true);

		/* Client clusters */
		this.testZigBeeEndpointClusterMethods(endpoint, simpleDesc, endpointConfig, false);

		context = interfaceName + ".getIEEEAddress()";
		log(TAG, "testing " + context);
		BigInteger ieeeAddress = node.getIEEEAddress();

		assertNotNull(context + ": cannot return null", ieeeAddress);
		assertEquals(context + ": returned IEEEAddress differs from the CT configuration one", ieeeAddress, endpoint.getNodeAddress());

		/*
		 * Checks the ZigBeeEndpoint service properties.
		 */
		context = "ZigBeeEndpoint serviceProperties";

		log(TAG, "testing " + context + ": checking service properties of ZigBeeEndpoint " + this.printScope(endpoint));

		Short endpointId = (Short) getParameterChecked(context, serviceProperties, ZigBeeEndpoint.ENDPOINT_ID, ParserUtils.MANDATORY, Short.class);
		Integer profileId = (Integer) getParameterChecked(context, serviceProperties, ZigBeeEndpoint.PROFILE_ID, ParserUtils.MANDATORY, Integer.class);
		Integer deviceId = (Integer) getParameterChecked(context, serviceProperties, ZigBeeEndpoint.DEVICE_ID, ParserUtils.MANDATORY, Integer.class);
		String hostPid = (String) getParameterChecked(context, serviceProperties, ZigBeeEndpoint.HOST_PID, ParserUtils.MANDATORY, String.class);
		Byte deviceVersion = (Byte) getParameterChecked(context, serviceProperties, ZigBeeEndpoint.DEVICE_VERSION, ParserUtils.MANDATORY, Byte.class);
		int[] outputClusters = (int[]) getParameterChecked(context, serviceProperties, ZigBeeEndpoint.OUTPUT_CLUSTERS, ParserUtils.MANDATORY, int[].class);
		int[] inputClusters = (int[]) getParameterChecked(context, serviceProperties, ZigBeeEndpoint.INPUT_CLUSTERS, ParserUtils.MANDATORY, int[].class);

		/* The service Pid uniqueness is checked outside this test method. */
		String servicePid = (String) getParameterChecked(context, serviceProperties, "service.pid", ParserUtils.MANDATORY, String.class);

		assertEquals(context + ": mismatch with ZigBeeSimpelDescritor.getEndpoint()", simpleDesc.getEndpoint(), endpointId.shortValue());
		assertEquals(context + ": mismatch with ZigBeeSimpelDescritor.getApplicationDeviceId()", simpleDesc.getApplicationDeviceId(), deviceId.intValue());
		assertEquals(context + ": mismatch with ZigBeeSimpelDescritor.getApplicationProfileId()", simpleDesc.getApplicationProfileId(), profileId.intValue());
		assertEquals(context + ": mismatch with ZigBeeSimpelDescritor.getApplicationDeviceVersion()", simpleDesc.getApplicationDeviceVersion(), deviceVersion.byteValue());
		assertEquals(context + ": mismatch with ZigBeeNode.getHostPid()", node.getHostPid(), hostPid);

		this.assertClusterArray(context + ": mismatch with ZigBeeSimpelDescritor.getOutputClusters()", simpleDesc.getOutputClusters(), outputClusters);
		this.assertClusterArray(context + ": mismatch with ZigBeeSimpelDescritor.getInputClusters()", simpleDesc.getInputClusters(), inputClusters);

		/*
		 * Check the Device Access specification DEVICE_CATEGORY service
		 * property.
		 */

		this.assertDeviceCategory(context, serviceProperties, ZigBeeEndpoint.DEVICE_CATEGORY);
	}

	private void assertDeviceCategory(String message, Map props, String expectedDeviceCategory) {
		Object deviceCategoryObject = props.get(Constants.DEVICE_CATEGORY);
		if (deviceCategoryObject == null) {
			fail(message + ": unable to find " + Constants.DEVICE_CATEGORY + " service property.");
		} else if (deviceCategoryObject instanceof String[]) {
			String[] deviceCategories = (String[]) deviceCategoryObject;
			for (int i = 0; i < deviceCategories.length; i++) {
				if (deviceCategories[i].equals(expectedDeviceCategory)) {
					return;
				}
			}
		} else if (deviceCategoryObject instanceof String) {
			String deviceCategory = (String) deviceCategoryObject;
			if (deviceCategory.equals(expectedDeviceCategory)) {
				return;
			}
		}

		fail(message + ": unable to find a " + Constants.DEVICE_CATEGORY + " with value " + expectedDeviceCategory);
	}

	/**
	 * Verifies if the passed arrays contains the same entries.
	 * 
	 * @param message The error message.
	 * @param expected The expected array.
	 * @param actual The actual array.
	 */
	private void assertClusterArray(String message, int[] expected, int[] actual) {

		SortedSet expectedClusterIds = new TreeSet();
		SortedSet actualClusterIds = new TreeSet();

		for (int i = 0; i < expected.length; i++) {
			expectedClusterIds.add(new Integer(actual[i]));
		}

		for (int i = 0; i < actual.length; i++) {
			actualClusterIds.add(new Integer(actual[i]));
		}

		if (actual.length != actualClusterIds.size()) {
			fail(message + ": actual array contains duplicate entries.");
		}

		for (Iterator iterator = expectedClusterIds.iterator(); iterator.hasNext();) {
			Integer clusterId = (Integer) iterator.next();
			if (!actualClusterIds.contains(clusterId)) {
				fail(message + ": actual array do not contain cluster id " + clusterId);
			}
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
			if (sRefs == null) {
				return 0;
			}
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

	public void checkSimpleDescriptor(ZigBeeSimpleDescriptor expectedDescr, ZigBeeSimpleDescriptor simpleDescriptor) throws Exception {
		String context = "ZigBeeSimpleDescriptor";

		assertEquals(context + ": application Profile Identifier not matched", expectedDescr.getApplicationProfileId(), simpleDescriptor.getApplicationProfileId());
		assertEquals(context + ": application Device Identifier not matched", expectedDescr.getApplicationDeviceId(), simpleDescriptor.getApplicationDeviceId());
		assertEquals(context + ": application Device Version  not matched", expectedDescr.getApplicationDeviceVersion(), simpleDescriptor.getApplicationDeviceVersion());
		assertEquals(context + ": application endpointId not matched", expectedDescr.getEndpoint(), simpleDescriptor.getEndpoint());

		int[] serverClusters = simpleDescriptor.getInputClusters();
		int[] clientClusters = expectedDescr.getOutputClusters();

		assertEquals(context + ": wrong number of input clusters", expectedDescr.getInputClusters().length, serverClusters.length);
		assertEquals(context + ": wrong number of output clusters", expectedDescr.getOutputClusters().length, clientClusters.length);

		/*
		 * Tests some specific methods of the ZigBeeSimpleDescriptor interface
		 * implementation
		 */

		int[] clusters = simpleDescriptor.getInputClusters();
		for (int i = 0; i < clusters.length; i++) {
			boolean result = simpleDescriptor.providesInputCluster(clusters[i]);
			assertTrue(context + ": error in providesInputCluster() implementation. Must assert the presence of clusterId=" + clusters[i], result);
		}

		clusters = simpleDescriptor.getOutputClusters();
		for (int i = 0; i < clusters.length; i++) {
			boolean result = simpleDescriptor.providesOutputCluster(clusters[i]);
			assertTrue(context + ": error in providesOutputCluster() implementation. Must assert the presence of clusterId=" + clusters[i], result);
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
	}

	public void checkPowerDescriptor(ZigBeePowerDescriptor expectedDesc, ZigBeePowerDescriptor desc) throws Exception {
		assertEquals("ZigBeePowerDescriptor Power Mode", expectedDesc.getCurrentPowerMode(), desc.getCurrentPowerMode());
		assertEquals("ZigBeePowerDescriptor Power Source", expectedDesc.getCurrentPowerSource(), desc.getCurrentPowerSource());
		assertEquals("ZigBeePowerDescriptor Power Source Level", expectedDesc.getCurrentPowerSourceLevel(), desc.getCurrentPowerSourceLevel());
		assertEquals("ZigBeePowerDescriptor Mains Power Available", expectedDesc.isConstantMainsPowerAvailable(), desc.isConstantMainsPowerAvailable());
	}

	protected void checkComplexDescriptor(ZigBeeComplexDescriptor expectedDesc, ZigBeeComplexDescriptor desc) {
		assertEquals("ZigBeeComplexDescriptor Model Name", expectedDesc.getModelName(), desc.getModelName());
		assertEquals("ZigBeeComplexDescriptor Serial Number", expectedDesc.getSerialNumber(), desc.getSerialNumber());
		assertEquals("ZigBeeComplexDescriptor Device URL", expectedDesc.getDeviceURL(), desc.getDeviceURL());
	}

	/**
	 * This test simply stop the ZigBeeHost, if already started, verify that no
	 * more services are registered and then starts it again. It then checks
	 * that the ZigBeeNodes are registered AFTER the corresponding
	 * ZigBeeEndpoint services. It then stops the ZigBeeHost and verifies that
	 * the ZigBeeNodes are unregistered BEFORE the corresponding ZigBeeEndpoint
	 * services.
	 * 
	 * @throws Exception In case of generic failure.
	 */
	public void testRegistrationUnregistrationOrder() throws Exception {

		String context = "ZigBeeHost.start()";

		log(TAG, "testing " + context + ": checks that the registration and unregistrtion order of the ZigBeeNode and ZigBeeEndpoint services is correct.");

		ZigBeeHost host = this.getZigBeeHostService();

		/*
		 * If already started, stops it.
		 */
		if (host.isStarted()) {
			host.stop();

			/*
			 * Once the stop() method returns the ZigBeeHost.isStarted() must be
			 * false.
			 */
			assertEquals(context + ": ZigBeeHost.isStarted() wrong value", false, host.isStarted());
		}

		/*
		 * We check if some ZigBeeNode services are (still) registered.
		 */
		ServiceReference[] sRefs = getContext().getAllServiceReferences(ZigBeeNode.class.getName(), null);
		if (sRefs != null) {
			assertEquals(context + ": Found ZigBeeNode services while the ZigBeeHost is not in started state.", 0, sRefs.length);
		}

		/*
		 * We check if some ZigBeeEndpoint services are (still) registered
		 * (excluding exported ones).
		 */
		sRefs = getContext().getAllServiceReferences(ZigBeeEndpoint.class.getName(), ZIGBEE_NOT_EXPORT_FILTER);
		if (sRefs != null) {
			assertEquals(context + ": Found ZigBeeEndpoint services while the ZigBeeHost is not in started state.", 0, sRefs.length);
		}

		context = "ZigBeeHost.start()";

		/*
		 * After the ZigBeeHost.start() method is returned, we need to check for
		 * registration of the following services:
		 * 
		 * ZigBeeNode ZigBeeEndpoint
		 * 
		 * We listen for the timeout configured in the xml file. We also check
		 * if the service registration order is correct. A valid implementation,
		 * when it discovers a ZigBee node, must register the ZigBeeEndpoint
		 * services first, and then the ZigBeeNode they belongs to.
		 */

		/**
		 * This filter selects any ZigBeeNode service and any not exported
		 * ZigBeeEndpoint service.
		 */
		String filter = "(|" + ZIGBEE_NODE_FILTER + ZIGBEE_ENDPOINT_NOT_EXPORTED + ")";

		final List discoveredNodes = new ArrayList();
		final String context1 = context;

		ServiceListener listener = new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				int type = event.getType();
				if (type == ServiceEvent.REGISTERED) {
					ServiceReference sRef = event.getServiceReference();
					Object service = getContext().getService(sRef);

					BigInteger nodeIeeeAddress = (BigInteger) sRef.getProperty(ZigBeeNode.IEEE_ADDRESS);
					if (nodeIeeeAddress == null) {
						fail(context1 + ": missing " + ZigBeeNode.IEEE_ADDRESS + " property in ZigBeeNode or ZigBeeEndpoint services");
					}

					if (service instanceof ZigBeeNode) {
						if (!discoveredNodes.contains(nodeIeeeAddress)) {
							discoveredNodes.add(nodeIeeeAddress);
						}
					} else if (service instanceof ZigBeeEndpoint) {
						if (discoveredNodes.contains(nodeIeeeAddress)) {
							fail(context1 + ": ZigBeeEndpoint with IEEE_ADDRESS " + nodeIeeeAddress + " has been registered after its ZigBeeNode");
						}
					}
					getContext().ungetService(sRef);
				} else if (type == ServiceEvent.UNREGISTERING) {
					ServiceReference sRef = event.getServiceReference();
					Object service = getContext().getService(sRef);

					BigInteger nodeIeeeAddress = (BigInteger) sRef.getProperty(ZigBeeNode.IEEE_ADDRESS);
					if (nodeIeeeAddress == null) {
						fail(context1 + ": missing " + ZigBeeNode.IEEE_ADDRESS + " property in ZigBeeNode or ZigBeeEndpoint services");
					}

					if (service instanceof ZigBeeNode) {
						if (discoveredNodes.contains(nodeIeeeAddress)) {
							discoveredNodes.remove(nodeIeeeAddress);
						}
					} else if (service instanceof ZigBeeEndpoint) {
						if (discoveredNodes.contains(nodeIeeeAddress)) {
							fail(context1 + ": ZigBeeEndpoint with IEEE_ADDRESS " + nodeIeeeAddress + " is unregistering before its ZigBeeNode");
						}
					}
					getContext().ungetService(sRef);
				}
			}
		};

		getContext().addServiceListener(listener, filter);

		/*
		 * Let's go: start the ZigBeeHost again and track the registration
		 * order.
		 */
		host.start();

		Thread.sleep(conf.getDiscoveryTimeout());

		host.stop();

		Thread.sleep(conf.getDiscoveryTimeout());

		getContext().removeServiceListener(listener);

		/*
		 * At the end we must not have no more ZigBeeNode services and not
		 * exported ZigBeeEndpoint services.
		 */
		sRefs = getContext().getAllServiceReferences(ZigBeeNode.class.getName(), null);
		if (sRefs != null) {
			assertEquals(context + ": Found aZigBeeNode services while the ZigBeeHost is stopped.", 0, sRefs.length);
		}

		/*
		 * We check if some ZigBeeEndpoint services are (still) registered
		 * (excluding exported ones).
		 */
		sRefs = getContext().getAllServiceReferences(ZigBeeEndpoint.class.getName(), ZIGBEE_NOT_EXPORT_FILTER);
		if (sRefs != null) {
			assertEquals(context + ": Found a not exported ZigBeeEndpoint service while the ZigBeeHost is stopped.", 0, sRefs.length);
		}
	}

	/**
	 * This test checks if the service.pids are persistent across restarts of
	 * the ZigBee implementation bundle.
	 * 
	 * <ul>
	 * <li>Checks if ZigBeeHost service is started. If not starts it.
	 * <li>Wait for DISCOVERY_TIMEOUT milliseconds.
	 * <li>Takes a snapshot of the currently registered ZigBeeNode and NOT
	 * exported ZigBeeEndpoint services. The snapshot contains their
	 * IEEE_ADDRESS and service.pid service properties.
	 * <li>Stops the bundle that has registered the ZigBeeHost.
	 * <li>Starts the bundle again.
	 * <li>Wait for DISCOVERY_TIMEOUT milliseconds.
	 * <li>Take a second snapshot of the currently registered ZigBeeNode and NOT
	 * exported ZigBeeEndpoint services.
	 * <li>Compares the two snapshots.
	 * 
	 * @throws Exception
	 */
	public void testAServicePidPersistence() throws Exception {

		log(TAG, "Testing that the service.pids of the registered ZigBeeNode and ZigBeeEndpoint services is persistent.");

		ZigBeeHost host = this.getZigBeeHostService();

		/**
		 * Starts the ZigBeeHost
		 */
		if (!host.isStarted()) {
			host.start();
		}

		Thread.sleep(conf.getDiscoveryTimeout());

		/*
		 * Take the first snapshot.
		 */
		Map snapshot1 = new HashMap();

		snapshotServices(snapshot1);

		/*
		 * restart the bundle
		 */

		ServiceReference hostServiceRef = getZigBeeHostServiceReference();

		Bundle bundle = hostServiceRef.getBundle();

		bundle.stop();

		bundle.start();

		TestStepProxy tproxy = new TestStepProxy(getContext());

		tproxy.execute(TestStepLauncher.ACTIVATE_ZIGBEE_DEVICES, "Press to continue.");

		Thread.sleep(conf.getDiscoveryTimeout());

		/*
		 * Takes the second snapshot.
		 */
		Map snapshot2 = new HashMap();

		snapshotServices(snapshot2);

		String context;

		/*
		 * Compares snapshot2 with snapshot1
		 */

		Set keysSnapshot2 = snapshot2.keySet();
		for (Iterator iterator = keysSnapshot2.iterator(); iterator.hasNext();) {
			String servicePid = (String) iterator.next();

			Map serviceProperties2 = (Map) snapshot2.get(servicePid);

			context = "service.pid=" + servicePid;
			BigInteger ieeeAddress2 = (BigInteger) getParameterChecked(context, serviceProperties2, ZigBeeNode.IEEE_ADDRESS, ParserUtils.MANDATORY, BigInteger.class);

			/*
			 * checks if in snapshot1 there was a service with this service.pid
			 * and the same IEEE_ADDRESS.
			 */

			Map serviceProperties1 = (Map) snapshot1.get(servicePid);
			assertNotNull(context + ": missing from the snapshot taken before bundle restart.", serviceProperties1);
			BigInteger ieeeAddress1 = (BigInteger) getParameterChecked(context, serviceProperties1, ZigBeeNode.IEEE_ADDRESS, ParserUtils.MANDATORY, BigInteger.class);

			assertEquals(context + ": got two different IEEE_ADDRESS with the same service.pid", ieeeAddress2, ieeeAddress1);

			snapshot1.remove(servicePid);
		}

		/*
		 * After this process snapshot1 map must be empty
		 */
		if (snapshot1.size() > 0) {
			fail("There are some service.pids that were not present anymore after the bundle restart");
		}
	}

	/*
	 * Takes a snapshot of the currently registered ZigBeeNode and imported
	 * ZigBeeEndpoint services.
	 */
	private void snapshotServices(Map snapshot) throws InvalidSyntaxException {
		/*
		 * At the end we must not have no more ZigBeeNode services and not
		 * exported ZigBeeEndpoint services.
		 */

		ServiceReference[] sRefsZigBeeNode = getContext().getAllServiceReferences(ZigBeeNode.class.getName(), null);
		ServiceReference[] sRefsZigBeeEndpoint = getContext().getAllServiceReferences(ZigBeeEndpoint.class.getName(), ZIGBEE_NOT_EXPORT_FILTER);

		String context = "ZigBeeNodes properties";
		if (sRefsZigBeeNode != null) {
			for (int i = 0; i < sRefsZigBeeNode.length; i++) {
				Map serviceProperties = getProperties(sRefsZigBeeNode[i]);
				String servicePid = (String) getParameterChecked(context, serviceProperties, "service.pid", ParserUtils.MANDATORY, String.class);
				snapshot.put(servicePid, serviceProperties);
			}
		}

		context = "ZigBeeEndpoints properties";
		if (sRefsZigBeeEndpoint != null) {
			for (int i = 0; i < sRefsZigBeeEndpoint.length; i++) {
				Map serviceProperties = getProperties(sRefsZigBeeEndpoint[i]);
				String servicePid = (String) getParameterChecked(context, serviceProperties, "service.pid", ParserUtils.MANDATORY, String.class);
				snapshot.put(servicePid, serviceProperties);
			}
		}
	}

	/**
	 * Tests methods available in the ZigBeeEndpoint interface:
	 * <ul>
	 * <li>ZigBeeEndpoint.get<side>Clusters(),
	 * <li>ZigBeeEndpoint.get<side>Cluster(clusterId)
	 * </ul>
	 * 
	 * where <side> may be 'Server' or 'Client' according to the isServerSide
	 * parameter.
	 * 
	 * @param isServerSide true if the test must be done on the 'Server' flavor
	 *        of the above methods, false if have to be done on the 'Client'
	 *        flavor.
	 */

	protected void testZigBeeEndpointClusterMethods(ZigBeeEndpoint endpoint, ZigBeeSimpleDescriptor simpleDesc, ZigBeeEndpointConfig expectedEndpoint, boolean isServerSide) {
		String context;
		String side;

		if (isServerSide) {
			side = "Server";
		} else {
			side = "Client";
		}

		/*
		 * Checks if the ZigBeeEndpoint.getServerClusters() returns a valid
		 * array of ZCLCluster instances.
		 */

		ZCLCluster[] clusters;
		ZCLClusterDescription[] expectedClusters = null;

		context = "ZigBeeEndpoint.get" + side + "Clusters()";

		log(TAG, "testing " + context + ": checking that the number of clusters returned is the expected one and for duplicate and null entries.");

		/*
		 * Checks if the number of clusters retrieved by the
		 * ZigBeeEndpoint.get<Side>Clusters() matches the expectedClustersNumber
		 * read from the CT configuration file.
		 */

		int expectedClustersNumber = -1;

		if (isServerSide) {
			clusters = endpoint.getServerClusters();
			if (expectedEndpoint != null) {
				expectedClusters = expectedEndpoint.getServerClusters();
				expectedClustersNumber = expectedEndpoint.getServerClustersNumber();
			}
		} else {
			clusters = endpoint.getClientClusters();
			if (expectedEndpoint != null) {
				expectedClusters = expectedEndpoint.getClientClusters();
				expectedClustersNumber = expectedEndpoint.getClientClustersNumber();
			}
		}

		assertNotNull(context + ": cannot return null array.", clusters);
		if (expectedEndpoint != null) {
			assertEquals(context + ": returned clusters array length.", expectedClusters.length, clusters.length);
		}
		if (expectedEndpoint != null) {
			assertEquals(context + ": returned clusters array size differs from what it is written in the CT configuration file ", expectedClustersNumber, clusters.length);
		}

		/*
		 * checks for null entries, duplicate entries and duplicate clusterIds.
		 * A Set is used to discover duplicates.
		 */

		Set clustersSet = new HashSet();
		SortedSet clusterIdsSet = new TreeSet();
		for (int i = 0; i < clusters.length; i++) {
			assertNotNull(context + ": returned an array with null entries.", clusters[i]);
			clustersSet.add(clusters[i]);
			clusterIdsSet.add(new Integer(clusters[i].getId()));
		}

		if (clusters.length != clustersSet.size()) {
			fail(context + ": returned an array with duplicate entries.");
		}

		if (clusters.length != clusterIdsSet.size()) {
			fail(context + ": returned an array with entries that contains duplicate clusterIds.");
		}

		/*
		 * Checks if the clusters defined in the ZigBee CT configuration file
		 * are actually provided by the ZigBeeEndpoint
		 */

		for (int i = 0; i < expectedClusters.length; i++) {
			boolean containsExpectedCluster = clusterIdsSet.contains(new Integer(expectedClusters[i].getId()));
			if (!containsExpectedCluster) {
				fail(context + " do not return the cluster ids that are defined in the CT configuration file.");
			}
		}

		/*
		 * Checks if the ZigBeeEndpoint.getServerCluster(clusterId) returns the
		 * right cluster.
		 */

		context = "ZigBeeEndpoint.get" + side + "Cluster(clusterId)";

		log(TAG, "testing " + context + ": checks if the implementation is consistent with the get" + side + "Clusters()");

		for (int i = 0; i < clusters.length; i++) {
			ZCLCluster cluster;
			if (isServerSide) {
				cluster = endpoint.getServerCluster(clusters[i].getId());
			} else {
				cluster = endpoint.getClientCluster(clusters[i].getId());
			}

			assertNotNull(context + " cannot return null if asking for a correct clusterId", cluster);
			assertSame(context + " must return the same object instances returned by the ZigBeeEndpoint.get" + side + "Clusters()", clusters[i], cluster);
		}

		/*
		 * Checks if the ZigBeeEndpoint.getServerCluster(clusterId) throws or
		 * return the correct exception if a wrong clusterId is passed as
		 * parameter.
		 */
		ZCLCluster cluster;
		try {
			/* clusterId too big */
			if (isServerSide) {
				cluster = endpoint.getServerCluster(0xFFFF + 1);
			} else {
				cluster = endpoint.getClientCluster(0xFFFF + 1);
			}

			fail(context + ":  must throw an IllegalArgumentException if the clusterId is outside the range [0, 0xffff]");
		} catch (IllegalArgumentException e) {
			/* Success */
		}

		try {
			/* negative clusterId */

			if (isServerSide) {
				cluster = endpoint.getServerCluster(-1);
			} else {
				cluster = endpoint.getClientCluster(-1);
			}
			fail(context + ": must throw an IllegalArgumentException if the clusterId is outside the range [0, 0xffff]");
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
			/* not existent clusterId */
			if (isServerSide) {
				cluster = endpoint.getServerCluster(notExistentClusterId);
			} else {
				cluster = endpoint.getClientCluster(notExistentClusterId);
			}

			assertNotNull(context + ":  must return null if the requested clusterId is not implemented in the endpoint");
		} else {
			/*
			 * It it not really possible that the endpoints implements all the
			 * possible clusterIds, so it is always possible to find a cluster
			 * that is not supported.
			 */

			fail(context + ": CT internal error: unable to find a non existent cluster");
		}
	}

	/**
	 * Test case related to events handling. It looks for a Boolean reportable
	 * attribute and registers a ZCLEventListener to be notified about this
	 * attribute.
	 * 
	 * It tries also to update the event listener service properties to verify
	 * that the events will arrive at the correct interval.
	 * 
	 * @throws InterruptedException
	 */
	public void testEventing() throws InterruptedException {

		/*
		 * Find in the CT configuration file an endpoint that has a reportable
		 * attribute of data type ZigBeeBoolean
		 */
		AttributeCoordinates attributeCoordinates = conf.findAttribute(null, new Boolean(true), null);

		assertNotNull("unable to find in the CT a ZigBee reportable attribute of Boolean ZCL data type.");

		ZigBeeEndpoint endpoint = getZigBeeEndpointService(attributeCoordinates.expectedEndpoint);
		assertNotNull("service not found " + this.printScope(attributeCoordinates.expectedEndpoint));

		ZCLAttributeDescription attribute = attributeCoordinates.expectedAttributeDescription;

		Dictionary properties = new Hashtable();

		int maxReportInterval = 20;

		/*
		 * Fill the field that represents the exact attribute that has to be
		 * monitored.
		 */
		properties.put(ZigBeeNode.IEEE_ADDRESS, endpoint.getNodeAddress());
		properties.put(ZigBeeEndpoint.ENDPOINT_ID, new Short(endpoint.getId()));
		properties.put(ZCLCluster.ID, new Integer(attributeCoordinates.expectedCluster.getId()));
		properties.put(ZCLAttribute.ID, new Integer(attribute.getId()));
		properties.put(ZCLEventListener.ATTRIBUTE_DATA_TYPE, new Short(attribute.getDataType().getId()));

		/*
		 * Configure the reporting.
		 */
		properties.put(ZCLEventListener.MAX_REPORT_INTERVAL, new Integer(maxReportInterval));

		long currentTime = System.currentTimeMillis();

		ZCLEventListenerImpl eventListener = registerEventListener(properties);
		StreamQueue stream = eventListener.getStreamQueue();

		/*
		 * We wait for an error coming from the ZB implementation.
		 */
		Thread.sleep(INVOKE_TIMEOUT);

		if (eventListener.getFailure() != null) {
			fail("event listener registration failed with exception" + eventListener.getFailure());
		}

		/*
		 * We have now to verify that the first event arrives within
		 * maxReportInterval seconds, and the next ones in less than
		 * maxReportInterval.
		 */

		int iterations = 3;
		long previousEventTimestamp = -1;
		long maxIntervalAllowed = 2 * maxReportInterval;

		while (iterations-- > 0) {
			ZigBeeEvent event = (ZigBeeEvent) stream.poll(maxIntervalAllowed * 1000);
			assertNotNull("no ZigBeeEvents received within the maximum intervall allowed " + maxIntervalAllowed, event);
			long timestamp = System.currentTimeMillis();
			log(TAG, "arrived event at " + timestamp + ": " + event);
			if (previousEventTimestamp > 0) {
				if ((timestamp - previousEventTimestamp) > (maxIntervalAllowed * 1000)) {
					fail("ZigBeeEvents must arrive within the maxReportInterval: " + maxReportInterval);
				}

				/*
				 * Check the received ZigBeeEvent content.
				 */

				assertEquals(event.getIEEEAddress(), attributeCoordinates.expectedEndpoint.getNodeAddress());
				assertEquals(event.getClusterId(), attributeCoordinates.expectedCluster.getId());
				assertEquals(event.getAttributeId(), attribute.getId());

			}
			previousEventTimestamp = timestamp;
		}

		/*
		 * Update the Max Reporting Interval and issue the same checks.
		 */

		iterations = 3;
		previousEventTimestamp = -1;
		maxReportInterval = maxReportInterval / 2;
		maxIntervalAllowed = 2 * maxReportInterval;

		properties.put(ZCLEventListener.MAX_REPORT_INTERVAL, new Integer(maxReportInterval));
		eventListener.update(properties);

		/*
		 * We wait for an error coming from the ZB implementation.
		 */
		Thread.sleep(INVOKE_TIMEOUT);

		if (eventListener.getFailure() != null) {
			fail("event listener registration failed with exception" + eventListener.getFailure());
		}

		/**
		 * Issue the same checks as above but now the interval is decreased.
		 */

		while (iterations-- > 0) {
			ZigBeeEvent event = (ZigBeeEvent) stream.poll(maxIntervalAllowed * 1000);
			assertNotNull("no ZigBeeEvents received within the maximum intervall allowed " + maxIntervalAllowed);
			long timestamp = System.currentTimeMillis();
			log(TAG, "arrived event at " + timestamp + ": " + event);
			if (previousEventTimestamp > 0) {
				if ((timestamp - previousEventTimestamp) > maxIntervalAllowed * 1000) {
					fail("ZigBeeEvents must arrive within the maxReportInterval: " + maxReportInterval);
				}

				/*
				 * Check the received ZigBeeEvent content.
				 */

				assertEquals(event.getIEEEAddress(), attributeCoordinates.expectedEndpoint.getNodeAddress());
				assertEquals(event.getClusterId(), attributeCoordinates.expectedCluster.getId());
				assertEquals(event.getAttributeId(), attribute.getId());

			}
			previousEventTimestamp = timestamp;
		}

		/* stop and destroy the test event listener. */
		eventListener.stop();
	}

	/**
	 * Creates an event listener with the passed service properties.
	 */

	private ZCLEventListenerImpl registerEventListener(Dictionary properties) {
		ZCLEventListenerImpl eventListener = new ZCLEventListenerImpl(getContext());
		eventListener.start(properties);
		return eventListener;
	}

	private int getUnsupportedClusterId(ZCLCluster clusterConf) throws Exception {

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
}
