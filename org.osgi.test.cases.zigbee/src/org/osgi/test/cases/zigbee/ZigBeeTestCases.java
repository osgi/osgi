/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.zigbee;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLAttributeInfo;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZCLReadStatusRecord;
import org.osgi.service.zigbee.ZDPException;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;
import org.osgi.test.cases.zigbee.config.file.ZigBeeEndpointConfig;
import org.osgi.test.cases.zigbee.config.file.ZigBeeHostConfig;
import org.osgi.test.cases.zigbee.config.file.ZigBeeNodeConfig;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

abstract class ZigBeeTestCases extends DefaultTestBundleControl {

	/**
	 * Timeout used for the timing out all the methods belonging to the
	 * ZigBeeHost, ZigBeeNode, ZigBeeEndpoint, ZigBeeCluster interfaces, that
	 * are also returning a Promise.
	 */
	protected static int		INVOKE_TIMEOUT;;

	/**
	 * Timeout used to wait for a ZigBeeNode or a ZigBeeEndpoint to be seen in
	 * the OSGi framework as a service. This constant is read from the
	 * configuration file provided by the RI.
	 */
	protected static int		DISCOVERY_TIMEOUT;

	ConfigurationFileReader		conf;

	protected TestStepLauncher	launcher;

	/**
	 * Useful constant for filtering ZigBeeEndpoint services
	 */
	protected static String		ZIGBEE_ENDPOINT_FILTER			= "(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + ZigBeeEndpoint.class.getName() + ")";

	/**
	 * Useful constant for filtering ZigBeeNode services
	 */
	protected static String		ZIGBEE_NODE_FILTER				= "(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + ZigBeeNode.class.getName() + ")";

	/**
	 * Useful constant for filtering out services marked with ZIGBEE_EXPORT
	 * property (alias imported ones).
	 */
	protected static String		ZIGBEE_NOT_EXPORT_FILTER		= "(!(" + ZigBeeEndpoint.ZIGBEE_EXPORT + "=*" + "))";

	/**
	 * This filter selects any not exported ZigBeeEndpoint service.
	 */
	protected static String		ZIGBEE_ENDPOINT_NOT_EXPORTED	= "(&" + ZIGBEE_ENDPOINT_FILTER + ZIGBEE_NOT_EXPORT_FILTER + ")";

	protected void setUp() throws Exception {
		prepareTestStart();
	}

	protected void tearDown() throws Exception {
	}

	private void prepareTestStart() throws Exception {
		launcher = TestStepLauncher.getInstance(getContext());
		conf = launcher.getConfiguration();

		/*
		 * Initialize timeout constants relevant for the CT with the values read
		 * from the ZigBee configuration file.
		 */
		INVOKE_TIMEOUT = conf.getInvokeTimeout();
		DISCOVERY_TIMEOUT = conf.getDiscoveryTimeout();
	}

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

	protected ZigBeeHost getZigBeeHost(ZigBeeHostConfig host, long timeout) {

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

		ServiceTracker<ZigBeeHost,ZigBeeHost> st = new ServiceTracker<>(bc,
				filter, null);
		st.open();

		ZigBeeHost service = null;
		try {
			service = st.waitForService(timeout);
		} catch (InterruptedException e) {
		} finally {
			st.close();
		}
		return service;
	}

	protected ServiceReference<ZigBeeHost> getZigBeeHostServiceReference() {

		ZigBeeHostConfig host = conf.getZigBeeHost();

		String classFilter = "(objectClass=" + ZigBeeHost.class.getName() + ")";
		String ieeeAddressFilter = "(" + ZigBeeNode.IEEE_ADDRESS + "=" + host.getIEEEAddress() + ")";

		String filter = "(&" + classFilter + ieeeAddressFilter + ")";

		try {
			Collection<ServiceReference<ZigBeeHost>> sRefs = getContext()
					.getServiceReferences(ZigBeeHost.class, filter);
			if (sRefs == null || (sRefs != null && sRefs.size() > 1)) {
				fail("found more than one ZigBeeHost matching the IEEE_ADDRESS " + host.getIEEEAddress());
			}
			return sRefs.iterator().next();
		} catch (InvalidSyntaxException e) {
			fail("Internal error: filter expression is wrong", e);
		}
		fail("Unable to find a ZigBeeHost for IEEE_ADDRESS " + host.getIEEEAddress());
		return null;
	}

	protected ZigBeeHost getZigBeeHost(long timeout) {
		ZigBeeHostConfig hostConfig = conf.getZigBeeHost();
		return this.getZigBeeHost(hostConfig, timeout);
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

	protected ZigBeeNode waitForZigBeeNodeService(ZigBeeNodeConfig node, long timeout) {

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

		ServiceTracker<ZigBeeNode,ZigBeeNode> st = new ServiceTracker<>(bc,
				filter, null);
		st.open();

		ZigBeeNode service = null;

		try {
			service = st.waitForService(timeout);
		} catch (InterruptedException e) {
		} finally {
			st.close();
		}
		return service;
	}

	protected <T> ServiceReference<T> waitForServiceReference(Class<T> clazz,
			String filter, long timeout) {

		BundleContext bc = getContext();

		ServiceTracker<T,T> st = new ServiceTracker<>(bc, clazz, null);
		st.open();

		ServiceReference<T> sRef = null;
		try {
			if (st.waitForService(timeout) != null) {
				Collection<ServiceReference<T>> sRefs = bc
						.getServiceReferences(clazz, filter);
				if (sRefs.size() > 0) {
					return sRefs.iterator().next();
				}
			}
		} catch (InterruptedException e) {
		} catch (InvalidSyntaxException e) {

		} finally {
			st.close();
		}
		return sRef;
	}

	/**
	 * Retrieve the ZigBeeEndpoint service that matches the expected one.
	 */
	protected ZigBeeEndpoint getZigBeeEndpointService(ZigBeeNodeConfig node, ZigBeeEndpointConfig endpoint) {

		String ieeeAddressFilter = "(" + ZigBeeNode.IEEE_ADDRESS + "=" + node.getIEEEAddress() + ")";
		String endpointIdFilter = "(" + ZigBeeEndpoint.ENDPOINT_ID + "=" + endpoint.getId() + ")";

		String filter = "(&" + ieeeAddressFilter + endpointIdFilter + ")";
		try {
			@SuppressWarnings("unchecked")
			ServiceReference<ZigBeeEndpoint>[] sRefs = (ServiceReference<ZigBeeEndpoint>[]) getContext()
					.getAllServiceReferences(ZigBeeEndpoint.class.getName(),
							filter);
			if (sRefs.length > 1) {
				fail("we expect to have just one ZigBeeEndpoint service registered with the same ENDPOINT_ID and IEEE_ADDRESS.");
			} else if (sRefs.length == 0) {
				fail("no ZigBeeEndpoint service mathing that present in ZigBee configuration file, found.");
			}

			return getContext().getService(sRefs[0]);
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

		assertNotNull(endpoint);
		assertNotNull(endpoint.getNodeAddress());

		String ieeeAddressFilter = "(" + ZigBeeNode.IEEE_ADDRESS + "=" + endpoint.getNodeAddress() + ")";
		String endpointIdFilter = "(" + ZigBeeEndpoint.ENDPOINT_ID + "=" + endpoint.getId() + ")";

		String filter = "(&" + ieeeAddressFilter + endpointIdFilter + ")";
		try {
			@SuppressWarnings("unchecked")
			ServiceReference<ZigBeeEndpoint>[] sRefs = (ServiceReference<ZigBeeEndpoint>[]) getContext()
					.getAllServiceReferences(ZigBeeEndpoint.class.getName(),
							filter);
			if (sRefs != null && sRefs.length > 1) {
				fail("we expect to have just one ZigBeeEndpoint service registered with the same ENDPOINT_ID and IEEE_ADDRESS.");
			} else if (sRefs == null) {
				fail("no ZigBeeEndpoint service mathing that present in ZigBee configuration file, found.");
			}

			return getContext().getService(sRefs[0]);
		} catch (InvalidSyntaxException e) {
			fail("CT internal error", e);
		}

		fail("no ZigBeeEndpoint service mathing that present in ZigBee configuration file, found.");
		return null;
	}

	/**
	 * Read all the service properties and put them into an Map object.
	 * 
	 * @param ref The service reference
	 * @return A Map object containing all the service properties of the service
	 *         represented by the passed ServiceReference.
	 */

	protected Map<String,Object> getProperties(ServiceReference< ? > ref) {
		Map<String,Object> map = new HashMap<>();
		String[] keys = ref.getPropertyKeys();
		for (int i = 0; i < keys.length; i++) {
			Object value = ref.getProperty(keys[i]);
			map.put(keys[i], value);
		}
		return map;
	}

	/**
	 * 
	 * @param p
	 * @throws InterruptedException
	 */

	protected void waitForPromise(Promise< ? > p, long timeout)
			throws InterruptedException {
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

	protected String printScope(ZigBeeEndpointConfig endpoint) {
		return "[ ieeeAddress: " + toHexString(endpoint.getNodeAddress(), 16) + ", ID: " + endpoint.getId() + "]";
	}

	protected String printScope(ZigBeeEndpoint endpoint) {
		return "[ ieeeAddress: " + toHexString(endpoint.getNodeAddress(), 16) + ", ID: " + endpoint.getId() + "]";
	}

	protected String printScope(ZigBeeNodeConfig node) {
		return "[ ieeeAddress: " + toHexString(node.getIEEEAddress(), 16) + "]";
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
	protected <T> T assertPromiseValueClass(Promise<T> p, Class< ? > clazz)
			throws InterruptedException {
		T value = null;
		try {
			value = p.getValue();
		} catch (InvocationTargetException e) {
			fail("internal CT error. The promise must never fail here.");
		}

		if (value != null && !clazz.isInstance(value)) {
			fail("promise did return a wrong class value. Expected " + clazz.getName() + " got " + value.getClass().getName());
		}
		return value;
	}

	protected static void assertPromiseFailure(Promise< ? > p, String message,
			Class< ? extends Throwable> want) throws InterruptedException {
		if (p.getFailure() != null) {
			assertException(message, want, p.getFailure());
		} else {
			fail(message + " " + "Promise resolved, but expected it to fail with exception: [" + want.getName() + "]");
		}
	}

	protected static void assertPromiseZCLException(Promise< ? > p,
			String message, int errorCode) throws InterruptedException {

		Class<ZCLException> want = ZCLException.class;

		if (p.getFailure() != null) {
			assertException(message, want, p.getFailure());

			ZCLException e = (ZCLException) p.getFailure();

			assertEquals(message + " " + "The exception was correct but with a wrong error code", errorCode, e.getErrorCode());
		} else {
			fail(message + " " + "Promise resolved, but expected it to fail with exception: [" + want.getName() + "]");
		}
	}

	protected static void assertPromiseZDPException(Promise< ? > p,
			String message, int errorCode) throws InterruptedException {

		Class<ZDPException> want = ZDPException.class;

		if (p.getFailure() != null) {
			assertException(message, want, p.getFailure());

			ZDPException e = (ZDPException) p.getFailure();

			assertEquals(message + " " + "The exception was correct but with a wrong error code", errorCode, e.getErrorCode());
		} else {
			fail(message + " " + "Promise resolved, but expected it to fail with exception: [" + want.getName() + "]");
		}
	}

	/**
	 * Asserts that the passed map is not null and contains keys and values of
	 * the expected classes. If expectedSize is zero or positive, it checks also
	 * for the map size. The map values cannot be null.
	 * 
	 * @param context A string that will be prefixed to the error messages.
	 * @param map
	 * @param key
	 * @param value
	 * @param length
	 */

	protected <K, V> void assertMapContent(String context, Map<K,V> map,
			Class<K> keyClass, Class<V> valueClass, int expectedSize) {
		assertNotNull(context + ": cannot return a null Map.", map);
		if (expectedSize >= 0) {
			assertEquals(context + " returned map size is wrong", expectedSize, map.size());
		}

		Set<K> keys = map.keySet();
		for (Iterator<K> iterator = keys.iterator(); iterator.hasNext();) {
			Object key = iterator.next();
			if (!keyClass.isInstance(key)) {
				fail(context + ": expected:[" + keyClass.getName() + "] but was:[" + key.getClass().getName() + "]");
			}

			Object value = map.get(key);

			assertNotNull(context + ": map contains a null value");

			if (!valueClass.isInstance(value)) {
				fail(context + ": expected:[" + valueClass.getName() + "] but was:[" + value.getClass().getName() + "]");
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <V> V getParameterChecked(String context,
			Map<String, ? > properties, String parameterName, boolean required,
			Class<V> want) {
		if (properties == null) {
			throw new NullPointerException("CT internal errror, passing null 'properties' argument");
		}

		Object value = properties.get(parameterName);

		if (value == null) {
			if (!required) {
				return null;
			} else {
				fail(context + ": missing required property: " + parameterName);
			}
		} else if (want.isInstance(value)) {
			return (V) value;
		} else {
			fail(context + ": expected:[" + want.getName() + "] but was:["
					+ value.getClass().getName() + "]");
		}
		return null;
	}

	/**
	 * Checks if the ZCLReadStatus record content is complient with the
	 * specification.
	 * 
	 * @param context
	 * @param readStatusRecord
	 * @param expectedAttributeInfo
	 * @param expectedErrorCode
	 */
	protected void assertReadStatusRecord(String context, ZCLReadStatusRecord readStatusRecord, ZCLAttributeInfo expectedAttributeInfo, int expectedErrorCode) {

		assertNotNull(context + "; ReadStatusRecord cannot be null");

		if (!expectedAttributeInfo.equals(readStatusRecord.getAttributeInfo())) {
			fail(context + ": ReadStatusRecord.getAttributeInfo() differs in content from the expected.");
		}

		if (expectedErrorCode >= 0) {
			ZigBeeException failure = readStatusRecord.getFailure();
			assertNotNull(context + "the response is successful, BUT a failure is expected in this test case reading a invalid attribute.", failure);
			assertException(context + ": wrong exception", ZCLException.class, failure);
			assertEquals(context + ": the ZCL exception error code is not correct ", expectedErrorCode, ((ZCLException) failure).getErrorCode());
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

	protected String toHexString(BigInteger number, int hexDigits) {
		String s = number.toString(16);
		if (s.length() >= hexDigits) {
			return s;
		}

		for (int i = 0; i < (hexDigits - s.length()); i++) {
			s = " " + s;
		}
		return s;
	}
}
