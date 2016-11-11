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

import java.lang.reflect.InvocationTargetException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.test.cases.zigbee.config.file.ZigBeeEndpointConfig;
import org.osgi.test.cases.zigbee.config.file.ZigBeeHostConfig;
import org.osgi.test.cases.zigbee.config.file.ZigBeeNodeConfig;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

public class ZigBeeTestCases extends DefaultTestBundleControl {

	/**
	 * Wait till a ZigBeeHost service with the passed
	 * {@link ZigBeeNode.IEEE_ADDRESS} property equal to the passed one is
	 * registered in the OSGi framework.
	 * 
	 * @param nodeIeeeAddress The IEEE address of the ZigBeeHost service to look
	 *        for.
	 * @return The service object of the ZigBeeHost.
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 */

	protected ZigBeeHost getZigBeeHost(ZigBeeHostConfig host, long timeout) throws InterruptedException {

		String classFilter = "(objectClass=" + ZigBeeHost.class.getName() + ")";
		String ieeeAddressFilter = "(" + ZigBeeNode.IEEE_ADDRESS + "=" + host.getIEEEAddress() + ")";

		BundleContext bc = getContext();

		Filter filter;
		try {
			filter = bc.createFilter("(&" + classFilter + ieeeAddressFilter + ")");
		} catch (InvalidSyntaxException e) {
			fail("Internal error: filter expression is wrong", e);
			return null;
		}

		ServiceTracker st = new ServiceTracker(bc, filter, null);
		st.open();

		ZigBeeHost hostService = (ZigBeeHost) st.waitForService(timeout);
		return hostService;

	}

	/**
	 * Wait until a ZigBeeNode service with the specified IEEE address is
	 * registered by the ZigBee base driver.
	 * 
	 * @param nodeIeeeAddress The IEEE address of the ZigBeeNode service we are
	 *        waiting for.
	 * @return
	 * @throws InterruptedException
	 */

	protected ZigBeeNode waitForZigBeeNodeService(ZigBeeNodeConfig node, long timeout) throws InterruptedException {

		String classFilter = "(objectClass=" + ZigBeeNode.class.getName() + ")";
		String ieeeAddressFilter = "(" + ZigBeeNode.IEEE_ADDRESS + "=" + node.getIEEEAddress() + ")";

		BundleContext bc = getContext();

		Filter filter;
		try {
			filter = bc.createFilter("(&" + classFilter + ieeeAddressFilter + ")");
		} catch (InvalidSyntaxException e) {
			fail("Internal error: filter expression is wrong", e);
			return null;
		}

		ServiceTracker st = new ServiceTracker(bc, filter, null);
		st.open();

		ZigBeeNode nodeService = (ZigBeeNode) st.waitForService(timeout);
		return nodeService;
	}

	/**
	 * Retrieve the ZigBeeEndpoint service that matches the expected one.
	 */
	protected ZigBeeEndpoint getZigBeeEndpointService(ZigBeeNodeConfig node, ZigBeeEndpointConfig endpoint) {

		String ieeeAddressFilter = "(" + ZigBeeNode.IEEE_ADDRESS + "=" + node.getIEEEAddress() + ")";
		String endpointIdFilter = "(" + ZigBeeEndpoint.ENDPOINT_ID + "=" + endpoint.getId() + ")";

		String filter = "(&" + ieeeAddressFilter + endpointIdFilter + ")";
		try {
			ServiceReference[] sRefs = getContext().getAllServiceReferences(ZigBeeEndpoint.class.getName(), filter);
			if (sRefs.length > 1) {
				fail("we expect to have just one ZigBeeEndpoint service registered with the same ENDPOINT_ID and IEEE_ADDRESS.");
			} else if (sRefs.length == 0) {
				fail("no ZigBeeEndpoint service mathing that present in ZigBee configuration file, found.");
			}

			return (ZigBeeEndpoint) getContext().getService(sRefs[0]);
		} catch (InvalidSyntaxException e) {
			fail("CT internal error", e);
		}

		fail("no ZigBeeEndpoint service mathing that present in ZigBee configuration file, found.");
		return null;
	}

	/**
	 * Retrieve the ZigBeeEndpoint service that matches the expected one.
	 */
	protected ZigBeeEndpoint getZigBeeEndpointService(ZigBeeEndpointConfig endpoint) {

		String ieeeAddressFilter = "(" + ZigBeeNode.IEEE_ADDRESS + "=" + endpoint.getNodeAddress() + ")";
		String endpointIdFilter = "(" + ZigBeeEndpoint.ENDPOINT_ID + "=" + endpoint.getId() + ")";

		String filter = "(&" + ieeeAddressFilter + endpointIdFilter + ")";
		try {
			ServiceReference[] sRefs = getContext().getAllServiceReferences(ZigBeeEndpoint.class.getName(), filter);
			if (sRefs.length > 1) {
				fail("we expect to have just one ZigBeeEndpoint service registered with the same ENDPOINT_ID and IEEE_ADDRESS.");
			} else if (sRefs.length == 0) {
				fail("no ZigBeeEndpoint service mathing that present in ZigBee configuration file, found.");
			}

			return (ZigBeeEndpoint) getContext().getService(sRefs[0]);
		} catch (InvalidSyntaxException e) {
			fail("CT internal error", e);
		}

		fail("no ZigBeeEndpoint service mathing that present in ZigBee configuration file, found.");
		return null;
	}

	/**
	 * 
	 * @param p
	 * @throws InterruptedException
	 */

	protected void waitForPromise(Promise p, long timeout) throws InterruptedException {
		long start = System.currentTimeMillis();
		while ((System.currentTimeMillis() - start) < timeout) {
			if (p.isDone()) {
				break;
			} else {
				Thread.sleep(500);
			}
		}

		if (!p.isDone()) {
			fail("The promise did not resolve within the expected timeout.");
		}
	}

	/**
	 * Checks if the promise returns a value of the expected class. If not,
	 * fails the test, otherwise returns the value. This method is intended to
	 * be called only after the promise is resolved successfully or with a
	 * failure.
	 * 
	 * @param promise The promise.
	 * @param clazz The class of the value that is returned by the promise.
	 * @return the promise value
	 */
	protected Object assertPromiseValueClass(Promise p, Class clazz) throws InterruptedException {
		Object value = null;
		try {
			value = p.getValue();
		} catch (InvocationTargetException e) {
			fail("internal CT error. The promise must never fail here.");
		}

		if (value != null && value.getClass().isAssignableFrom(clazz)) {
			fail("promise did return a wrong class value. Expected " + clazz.getName() + " got " + value.getClass().getName());
		}
		return value;
	}

	protected void assertZCLException(String message, Throwable e, int errorCode) {
		assertNotNull(message, e);

		if (e instanceof ZCLException) {
			assertEquals("wrong exception code", ((ZCLException) e).getErrorCode(), errorCode);
		} else {
			fail("expected a ZCLException, got " + e.getClass(), e);
		}
	}

	protected ZCLClusterDescription lookupCluster(ZCLClusterDescription[] clusters, int id) {
		for (int i = 0; i < clusters.length; i++) {
			if (clusters[i].getId() == id) {
				return clusters[i];
			}
		}
		return null;
	}

	protected ZCLAttribute lookupAttribute(ZCLAttribute[] attributes, int id) {
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i].getId() == id) {
				return attributes[i];
			}
		}
		return null;
	}

	public static void log(String TAG, String message) {
		log(TAG + " - " + message);
	}
}
