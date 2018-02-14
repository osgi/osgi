/*
 * Copyright (c) OSGi Alliance (2008, 2009, 2010, 2017). All Rights Reserved.
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
package org.osgi.test.cases.remoteservices.junit;

import static org.junit.Assert.assertArrayEquals;
import static org.osgi.framework.Constants.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.test.cases.remoteservices.common.A;
import org.osgi.test.cases.remoteservices.common.AsyncJava8Types;
import org.osgi.test.cases.remoteservices.common.AsyncTypes;
import org.osgi.test.cases.remoteservices.common.B;
import org.osgi.test.cases.remoteservices.common.BasicTypes;
import org.osgi.test.cases.remoteservices.common.C;
import org.osgi.test.cases.remoteservices.common.DTOType;
import org.osgi.test.cases.remoteservices.common.SlowService;
import org.osgi.test.cases.remoteservices.impl.AsyncJava8TypesImpl;
import org.osgi.test.cases.remoteservices.impl.AsyncTypesImpl;
import org.osgi.test.cases.remoteservices.impl.BasicTypesTestServiceImpl;
import org.osgi.test.cases.remoteservices.impl.SlowServiceImpl;
import org.osgi.test.cases.remoteservices.impl.TestServiceImpl;
import org.osgi.test.support.sleep.Sleep;
import org.osgi.test.support.tracker.Tracker;
import org.osgi.util.function.Predicate;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 */
public class SimpleTest extends MultiFrameworkTestCase {

	/**
	 * Package to be exported by the server side System Bundle
	 */
	private static final String ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON = "org.osgi.test.cases.remoteservices.common";

	/**
	 * Magic value. Properties with this value will be replaced by a socket port number that is currently free.
	 */
    private static final String FREE_PORT = "@@FREE_PORT@@";

	private long timeout;

	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.junit.MultiFrameworkTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		timeout = getLongProperty("rsa.ct.timeout", 30000L);
	}

	/**
	 * @see org.osgi.test.cases.remoteservices.junit.MultiFrameworkTestCase#getConfiguration()
	 */
	public Map getConfiguration() {
		Map configuration = new HashMap();
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");

		//make sure that the server framework System Bundle exports the interfaces
		String systemPacakagesXtra = ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON;
		configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
				systemPacakagesXtra);
		String configuratorInitial = System
				.getProperty("server.configurator.initial");
		if (configuratorInitial != null) {
			configuration.put("configurator.initial", configuratorInitial);
		}
		return configuration;
	}

	/**
	 * @throws Exception
	 */
	public void testSimpleRegistration() throws Exception {
		Hashtable properties = registrationTestServiceProperties();

		// install server side test service in the sub-framework
		ServiceRegistration< ? > srTestService = installTestBundleAndRegisterServiceObject(
				TestServiceImpl.class.getName(), properties, A.class.getName(),
				B.class.getName());
		assertNotNull(srTestService);

		System.out.println("registered test service A and B on server side");

		Sleep.sleep(1000);

		try {
			// now check on the hosting framework for the service to become available
			ServiceTracker clientTracker = new ServiceTracker(getContext(), A.class.getName(), null);
			clientTracker.open();

			// the proxy should appear in this framework
			A client = (A) Tracker.waitForService(clientTracker, timeout);
			assertNotNull("no proxy for service A found!", client);

			ServiceReference sr = clientTracker.getServiceReference();
			System.out.println(sr);
			assertNotNull(sr);
			assertNotNull(sr.getProperty("service.imported"));
			assertNotNull(sr.getProperty("service.id"));
			assertNotNull(sr.getProperty("endpoint.id"));
			assertNotNull(sr.getProperty("service.imported.configs"));
			assertNull("private properties must not be exported", sr.getProperty(".myprop"));
			assertEquals("property implementation missing from proxy", "1", sr.getProperty("implementation"));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_INTENTS));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_INTENTS_EXTRA));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS));

			// invoke the proxy
			assertEquals("A", client.getA());

			clientTracker.close();

			// make sure C was not registered, since it wasn't exported
			assertNull("service C should not have been found as it was not exported", getContext().getServiceReference(C.class.getName()));

			// check for service B
			clientTracker = new ServiceTracker(getContext(), B.class.getName(), null);
			clientTracker.open();

			// the proxy should appear in this framework
			B clientB = (B) Tracker.waitForService(clientTracker, timeout);
			assertNotNull("no proxy for service B found!", clientB);

			sr = clientTracker.getServiceReference();
			System.out.println(sr);
			assertNotNull(sr);
			assertNotNull(sr.getProperty("service.imported"));
			assertNotNull(sr.getProperty("service.id"));
			assertNotNull(sr.getProperty("endpoint.id"));
			assertNotNull(sr.getProperty("service.imported.configs"));
			assertNull("private properties must not be exported", sr.getProperty(".myprop"));
			assertEquals("property implementation missing from proxy", "1", sr.getProperty("implementation"));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_INTENTS));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_INTENTS_EXTRA));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS));

			// invoke the proxy
			assertEquals("B", clientB.getB());

			clientTracker.close();

		} finally {
			srTestService.unregister();
		}
	}

	private Hashtable registrationTestServiceProperties() throws Exception {
		// verify that the server framework is exporting the test packages
		verifyFramework();

		Set supportedConfigTypes = getSupportedConfigTypes();

		// load the external properties file with the config types for the
		// server side service
		Hashtable properties = loadServerTCKProperties();

		// make sure the given config type is in the set of supported config
		// types
		String str = (String) properties
				.get(RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS);

		// I hate not having String.split() available
		StringTokenizer st = new StringTokenizer(str, " ");
		boolean found = false;
		while (st.hasMoreTokens()) {
			if (supportedConfigTypes.contains(st.nextToken())) {
				found = true;
				break;
			}
		}
		assertTrue(
				"the given service.exported.configs type is not supported by the installed Distribution Provider",
				found);

		// add some properties for testing
		properties.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, "*");
		properties.put("implementation", "1");
		properties.put(".myprop", "must not be visible on client side");
		processFreePortProperties(properties);
		return properties;
	}

	/**
	 * @throws Exception
	 */
	public void testBasicTypesIntent() throws Exception {

		Filter filter = getFramework().getBundleContext().createFilter(
				"(" + DistributionProviderConstants.REMOTE_CONFIGS_SUPPORTED
						+ "=*)");
		ServiceTracker< ? , ? > dpTracker = new ServiceTracker<>(
				getFramework().getBundleContext(), filter, null);
		dpTracker.open();

		Object dp = Tracker.waitForService(dpTracker, timeout);
		assertNotNull("No DistributionProvider found", dp);
		ServiceReference< ? > dpReference = dpTracker.getServiceReference();
		assertNotNull(dpReference);
		Object property = dpReference.getProperty(
				DistributionProviderConstants.REMOTE_INTENTS_SUPPORTED);
		assertNotNull("No intents supported", property);

		Collection<String> intents;
		if (property instanceof String) {
			intents = Collections.singleton((String) property);
		} else if (property instanceof String[]) {
			intents = Arrays.asList((String[]) property);
		} else if (property instanceof Collection) {
			@SuppressWarnings("unchecked")
			Collection<String> tmp = (Collection<String>) property;
			intents = tmp;
		} else {
			throw new IllegalArgumentException(
					"Supported intents are not of the correct type "
							+ property.getClass());
		}

		assertTrue("Did not support osgi.basic",
				intents.contains("osgi.basic"));
	}

	/**
	 * @throws Exception
	 */
	public void testBasicTypes() throws Exception {
		@SuppressWarnings("unchecked")
		Hashtable<String,Object> properties = registrationTestServiceProperties();

		properties.put(RemoteServiceConstants.SERVICE_EXPORTED_INTENTS,
				"osgi.basic");

		// register the service in the server side framework
		ServiceRegistration< ? > srTestService = installTestBundleAndRegisterServiceObject(
				BasicTypesTestServiceImpl.class.getName(), properties,
				BasicTypes.class.getName());

		System.out
				.println("registered basic types test service on server side");

		try {
			// now check on the hosting framework for the service to become
			// available
			ServiceTracker<BasicTypes,BasicTypes> clientTracker = new ServiceTracker<>(
					getContext(), BasicTypes.class, null);
			clientTracker.open();

			// the proxy should appear in this framework
			BasicTypes client = Tracker.waitForService(clientTracker, timeout);
			assertNotNull("no proxy for BasicTypes found!", client);

			ServiceReference<BasicTypes> sr = clientTracker
					.getServiceReference();

			Object property = sr
					.getProperty(RemoteServiceConstants.SERVICE_INTENTS);
			assertNotNull("No intents supported", property);

			Collection<String> intents;
			if (property instanceof String) {
				intents = Collections.singleton((String) property);
			} else if (property instanceof String[]) {
				intents = Arrays.asList((String[]) property);
			} else if (property instanceof Collection) {
				@SuppressWarnings("unchecked")
				Collection<String> tmp = (Collection<String>) property;
				intents = tmp;
			} else {
				throw new IllegalArgumentException(
						"Supported intents are not of the correct type "
								+ property.getClass());
			}

			assertTrue("Did not pick up the osgi.basic intent",
					intents.contains("osgi.basic"));

			// booleans
			assertFalse(client.getBooleanPrimitive(true));
			assertTrue(client.getBoolean(false));
			assertTrue(Arrays.equals(new boolean[] {
					true, false
			}, client.getBooleanPrimitiveArray(new boolean[] {
					false, true
			})));
			assertArrayEquals(new Boolean[] {
					false, true
			}, client.getBooleanArray(new Boolean[] {
					true, false
			}));
			assertEquals(Arrays.asList(true, true, false),
					client.getBooleanList(Arrays.asList(false, false, true)));
			assertEquals(Collections.singleton(true),
					client.getBooleanSet(Collections.singleton(false)));

			// bytes
			assertEquals(-1, client.getBytePrimitive((byte) 1));
			assertEquals(Byte.valueOf((byte) -1), client.getByte((byte) 1));
			assertArrayEquals(new byte[] {
					-1, -2, -3
			}, client.getBytePrimitiveArray(new byte[] {
					1, 2, 3
			}));
			assertArrayEquals(new Byte[] {
					(byte) 3, (byte) 2, (byte) 1
			}, client.getByteArray(new Byte[] {
					1, 2, 3
			}));

			assertEquals(Arrays.asList((byte) 1, (byte) 2, (byte) 3), client
					.getByteList(Arrays.asList((byte) 3, (byte) 2, (byte) 1)));
			assertEquals(
					new HashSet<>(Arrays.asList((byte) 2, (byte) 4, (byte) 6)),
					client.getByteSet(new HashSet<>(
							Arrays.asList((byte) 1, (byte) 2, (byte) 3))));

			// shorts
			assertEquals(-1, client.getShortPrimitive((short) 1));
			assertEquals(Short.valueOf((short) -1), client.getShort((short) 1));
			assertArrayEquals(new short[] {
					-1, -2, -3
			}, client.getShortPrimitiveArray(new short[] {
					1, 2, 3
			}));
			assertArrayEquals(new Short[] {
					(short) 3, (short) 2, (short) 1
			}, client.getShortArray(new Short[] {
					1, 2, 3
			}));

			assertEquals(Arrays.asList((short) 1, (short) 2, (short) 3),
					client.getShortList(
							Arrays.asList((short) 3, (short) 2, (short) 1)));
			assertEquals(
					new HashSet<>(
							Arrays.asList((short) 2, (short) 4, (short) 6)),
					client.getShortSet(new HashSet<>(
							Arrays.asList((short) 1, (short) 2, (short) 3))));

			// chars
			assertEquals('b', client.getCharPrimitive('a'));
			assertEquals(Character.valueOf('b'), client.getChar('a'));
			assertArrayEquals(new char[] {
					'b', 'c', 'd'
			}, client.getCharacterPrimitiveArray(new char[] {
					'a', 'b', 'c'
			}));
			assertArrayEquals(new Character[] {
					'c', 'b', 'a'
			}, client.getCharacterArray(new Character[] {
					'a', 'b', 'c'
			}));

			assertEquals(Arrays.asList('c', 'b', 'a'),
					client.getCharacterList(Arrays.asList('a', 'b', 'c')));
			assertEquals(new HashSet<>(Arrays.asList('A', 'B', 'C')),
					client.getCharacterSet(
							new HashSet<>(Arrays.asList('c', 'b', 'a'))));

			// ints
			assertEquals(-1, client.getIntPrimitive(1));
			assertEquals(Integer.valueOf(-1), client.getInt(1));
			assertArrayEquals(new int[] {
					-1, -2, -3
			}, client.getIntPrimitiveArray(new int[] {
					1, 2, 3
			}));
			assertArrayEquals(new Integer[] {
					3, 2, 1
			}, client.getIntegerArray(new Integer[] {
					1, 2, 3
			}));

			assertEquals(Arrays.asList(1, 2, 3),
					client.getIntegerList(Arrays.asList(3, 2, 1)));
			assertEquals(new HashSet<>(Arrays.asList(2, 4, 6)), client
					.getIntegerSet(new HashSet<>(Arrays.asList(1, 2, 3))));

			// longs
			assertEquals(-1, client.getLongPrimitive(1));
			assertEquals(Long.valueOf(-1), client.getLong((long) 1));
			assertArrayEquals(new long[] {
					-1, -2, -3
			}, client.getLongPrimitiveArray(new long[] {
					1, 2, 3
			}));
			assertArrayEquals(new Long[] {
					(long) 3, (long) 2, (long) 1
			}, client.getLongArray(new Long[] {
					(long) 1, (long) 2, (long) 3
			}));

			assertEquals(Arrays.asList((long) 1, (long) 2, (long) 3), client
					.getLongList(Arrays.asList((long) 3, (long) 2, (long) 1)));
			assertEquals(
					new HashSet<>(Arrays.asList((long) 2, (long) 4, (long) 6)),
					client.getLongSet(new HashSet<>(
							Arrays.asList((long) 1, (long) 2, (long) 3))));

			// floats
			assertEquals(-1f, client.getFloatPrimitive(1), 0.01f);
			assertEquals(Float.valueOf(-1), client.getFloat((float) 1));
			assertArrayEquals(new float[] {
					-1, -2, -3
			}, client.getFloatPrimitiveArray(new float[] {
					1, 2, 3
			}), 0.01f);
			assertArrayEquals(new Float[] {
					(float) 3, (float) 2, (float) 1
			}, client.getFloatArray(new Float[] {
					(float) 1, (float) 2, (float) 3
			}));

			assertEquals(Arrays.asList((float) 1, (float) 2, (float) 3),
					client.getFloatList(
							Arrays.asList((float) 3, (float) 2, (float) 1)));
			assertEquals(
					new HashSet<>(
							Arrays.asList((float) 2, (float) 4, (float) 6)),
					client.getFloatSet(new HashSet<>(
							Arrays.asList((float) 1, (float) 2, (float) 3))));

			// doubles
			assertEquals(-1d, client.getDoublePrimitive(1), 0.01d);
			assertEquals(Double.valueOf(-1), client.getDouble((double) 1));
			assertArrayEquals(new double[] {
					-1, -2, -3
			}, client.getDoublePrimitiveArray(new double[] {
					1, 2, 3
			}), 0.01d);
			assertArrayEquals(new Double[] {
					(double) 3, (double) 2, (double) 1
			}, client.getDoubleArray(new Double[] {
					(double) 1, (double) 2, (double) 3
			}));

			assertEquals(Arrays.asList((double) 1, (double) 2, (double) 3),
					client.getDoubleList(
							Arrays.asList((double) 3, (double) 2, (double) 1)));
			assertEquals(
					new HashSet<>(
							Arrays.asList((double) 2, (double) 4, (double) 6)),
					client.getDoubleSet(new HashSet<>(Arrays.asList((double) 1,
							(double) 2, (double) 3))));

			// Strings
			assertEquals("BANG", client.getString("bang"));
			assertArrayEquals(new String[] {
					"fum", "fo", "fi", "fee"
			}, client.getStringArray(new String[] {
					"fee", "fi", "fo", "fum"
			}));

			assertEquals(Arrays.asList("fum", "fo", "fi", "fee"), client
					.getStringList(Arrays.asList("fee", "fi", "fo", "fum")));
			assertEquals(new HashSet<>(Arrays.asList("FEE", "FI", "FO", "FUM")),
					client.getStringSet(new HashSet<>(
							Arrays.asList("fee", "fi", "fo", "fum"))));

			// Versions
			assertEquals(Version.parseVersion("2.3.4"),
					client.getVersion(Version.parseVersion("1.2.3")));
			assertArrayEquals(new Version[] {
					Version.parseVersion("4.5.6"), Version.parseVersion("1.2.3")
			}, client.getVersionArray(new Version[] {
					Version.parseVersion("1.2.3"), Version.parseVersion("4.5.6")
			}));

			assertEquals(
					Arrays.asList(Version.parseVersion("4.5.6"),
							Version.parseVersion("1.2.3")),
					client.getVersionList(
							Arrays.asList(Version.parseVersion("1.2.3"),
									Version.parseVersion("4.5.6"))));
			assertEquals(
					new HashSet<>(Arrays.asList(Version.parseVersion("2.3.4"),
							Version.parseVersion("5.6.7"))),
					client.getVersionSet(new HashSet<>(
							Arrays.asList(Version.parseVersion("1.2.3"),
									Version.parseVersion("4.5.6")))));

			// enums
			assertEquals(TimeUnit.MICROSECONDS,
					client.getEnum(TimeUnit.NANOSECONDS));
			assertArrayEquals(new TimeUnit[] {
					TimeUnit.HOURS, TimeUnit.DAYS
			}, client.getEnumArray(new TimeUnit[] {
					TimeUnit.DAYS, TimeUnit.HOURS
			}));

			assertEquals(Arrays.asList(TimeUnit.HOURS, TimeUnit.DAYS), client
					.getEnumList(Arrays.asList(TimeUnit.DAYS, TimeUnit.HOURS)));
			assertEquals(
					new HashSet<>(
							Arrays.asList(TimeUnit.HOURS, TimeUnit.SECONDS)),
					client.getEnumSet(new HashSet<>(Arrays
							.asList(TimeUnit.MILLISECONDS, TimeUnit.MINUTES))));

			// dtos
			DTOType typeA = new DTOType();
			typeA.value = "a";
			DTOType typeB = new DTOType();
			typeB.value = "b";
			DTOType typeC = new DTOType();
			typeC.value = "c";

			assertEquals("A", client.getDTO(typeA).value);

			DTOType[] array = client.getDTOTypeArray(new DTOType[] {
					typeA, typeB, typeC
			});
			assertEquals(3, array.length);
			assertEquals("c", array[0].value);
			assertEquals("b", array[1].value);
			assertEquals("a", array[2].value);

			List<DTOType> list = client
					.getDTOTypeList(Arrays.asList(typeA, typeB, typeC));
			assertEquals(3, list.size());
			assertEquals("c", list.get(0).value);
			assertEquals("b", list.get(1).value);
			assertEquals("a", list.get(2).value);

			Set<String> values = new HashSet<>();
			for (DTOType dtoType : client.getDTOTypeSet(
					new HashSet<>(Arrays.asList(typeA, typeB, typeC)))) {
				values.add(dtoType.value);
			}

			assertEquals(new HashSet<>(Arrays.asList("A", "B", "C")), values);

			
			Map<String, Integer> map = new HashMap<>();
			map.put("foo", 1);
			map.put("bar", 2);
			
			client.populateMap(map);
			
			assertEquals(map, client.getMap());
			
			clientTracker.close();

		} finally {
			srTestService.unregister();
		}
	}

	/**
	 * @throws Exception
	 */
	public void testBasicTimeout() throws Exception {
		@SuppressWarnings("unchecked")
		Hashtable<String,Object> properties = registrationTestServiceProperties();

		properties.put(RemoteServiceConstants.SERVICE_EXPORTED_INTENTS,
				"osgi.basic");

		properties.put("osgi.basic.timeout", "3000");

		// register the service in the server side framework
		ServiceRegistration< ? > srTestService = installTestBundleAndRegisterServiceObject(
				SlowServiceImpl.class.getName(), properties,
				SlowService.class.getName());

		System.out.println("registered slow test service on server side");

		try {
			// now check on the hosting framework for the service to become
			// available
			ServiceTracker<SlowService,SlowService> clientTracker = new ServiceTracker<>(
					getContext(), SlowService.class, null);
			clientTracker.open();

			// the proxy should appear in this framework
			SlowService client = Tracker.waitForService(clientTracker, timeout);
			assertNotNull("no proxy for SlowService found!", client);

			ServiceReference<SlowService> sr = clientTracker
					.getServiceReference();

			Object property = sr
					.getProperty(RemoteServiceConstants.SERVICE_INTENTS);
			assertNotNull("No intents supported", property);

			Collection<String> intents;
			if (property instanceof String) {
				intents = Collections.singleton((String) property);
			} else if (property instanceof String[]) {
				intents = Arrays.asList((String[]) property);
			} else if (property instanceof Collection) {
				@SuppressWarnings("unchecked")
				Collection<String> tmp = (Collection<String>) property;
				intents = tmp;
			} else {
				throw new IllegalArgumentException(
						"Supported intents are not of the correct type "
								+ property.getClass());
			}

			assertTrue("Did not pick up the osgi.basic intent",
					intents.contains("osgi.basic"));

			// should work fine
			assertEquals("ready", client.goSlow(1000));

			try {
				client.goSlow(5000);
				fail("Invocation should have timed out!");
			} catch (ServiceException se) {
				assertEquals(ServiceException.REMOTE, se.getType());
			}

			clientTracker.close();

		} finally {
			srTestService.unregister();
		}
	}

	/**
	 * @throws Exception
	 */
	public void testAsyncTypesIntent() throws Exception {

		Filter filter = getFramework().getBundleContext().createFilter(
				"(" + DistributionProviderConstants.REMOTE_CONFIGS_SUPPORTED
						+ "=*)");
		ServiceTracker< ? , ? > dpTracker = new ServiceTracker<>(
				getFramework().getBundleContext(), filter, null);
		dpTracker.open();

		Object dp = Tracker.waitForService(dpTracker, timeout);
		assertNotNull("No DistributionProvider found", dp);
		ServiceReference< ? > dpReference = dpTracker.getServiceReference();
		assertNotNull(dpReference);
		Object property = dpReference.getProperty(
				DistributionProviderConstants.REMOTE_INTENTS_SUPPORTED);
		assertNotNull("No intents supported", property);

		Collection<String> intents;
		if (property instanceof String) {
			intents = Collections.singleton((String) property);
		} else if (property instanceof String[]) {
			intents = Arrays.asList((String[]) property);
		} else if (property instanceof Collection) {
			@SuppressWarnings("unchecked")
			Collection<String> tmp = (Collection<String>) property;
			intents = tmp;
		} else {
			throw new IllegalArgumentException(
					"Supported intents are not of the correct type "
							+ property.getClass());
		}

		assertTrue("Did not support osgi.async",
				intents.contains("osgi.async"));
	}

	/**
	 * @throws Exception
	 */
	public void testAsyncTypes() throws Exception {
		@SuppressWarnings("unchecked")
		Hashtable<String,Object> properties = registrationTestServiceProperties();

		properties.put(RemoteServiceConstants.SERVICE_EXPORTED_INTENTS,
				"osgi.async");

		// register the service in the server side framework
		ServiceRegistration< ? > srTestService = installTestBundleAndRegisterServiceObject(
				AsyncTypesImpl.class.getName(), properties,
				AsyncTypes.class.getName());

		System.out
				.println("registered basic types test service on server side");

		try {
			// now check on the hosting framework for the service to become
			// available
			ServiceTracker<AsyncTypes,AsyncTypes> clientTracker = new ServiceTracker<>(
					getContext(), AsyncTypes.class, null);
			clientTracker.open();

			// the proxy should appear in this framework
			AsyncTypes client = Tracker.waitForService(clientTracker, timeout);
			assertNotNull("no proxy for AsyncTypes found!", client);

			ServiceReference<AsyncTypes> sr = clientTracker
					.getServiceReference();

			Object property = sr
					.getProperty(RemoteServiceConstants.SERVICE_INTENTS);
			assertNotNull("No intents supported", property);

			Collection<String> intents;
			if (property instanceof String) {
				intents = Collections.singleton((String) property);
			} else if (property instanceof String[]) {
				intents = Arrays.asList((String[]) property);
			} else if (property instanceof Collection) {
				@SuppressWarnings("unchecked")
				Collection<String> tmp = (Collection<String>) property;
				intents = tmp;
			} else {
				throw new IllegalArgumentException(
						"Supported intents are not of the correct type "
								+ property.getClass());
			}

			assertTrue("Did not pick up the osgi.async intent",
					intents.contains("osgi.async"));

			final Semaphore s = new Semaphore(0);

			// Future
			Future<String> f = client.getFuture(1000);
			assertFalse(f.isDone());
			assertEquals(AsyncTypes.RESULT, f.get(2, TimeUnit.SECONDS));

			// Promise
			Promise<String> p = client.getPromise(1000);
			p.filter(new Predicate<String>() {
				@Override
				public boolean test(String x) {
					return AsyncTypes.RESULT.equals(x);
				}
			}).then(new Success<String,Object>() {
				@Override
				public Promise<Object> call(Promise<String> x)
						throws Exception {
					s.release();
					return null;
				}
			});
			assertFalse(s.tryAcquire());
			assertTrue(s.tryAcquire(2, TimeUnit.SECONDS));

			clientTracker.close();

		} finally {
			srTestService.unregister();
		}
	}

	/**
	 * @throws Exception
	 */
	public void testAsyncJava8Types() throws Exception {
		try {
			@SuppressWarnings("unchecked")
			Hashtable<String,Object> properties = registrationTestServiceProperties();

			properties.put(RemoteServiceConstants.SERVICE_EXPORTED_INTENTS,
					"osgi.async");

			// register the service in the server side framework
			ServiceRegistration< ? > srTestService = installTestBundleAndRegisterServiceObject(
					AsyncJava8TypesImpl.class.getName(), properties,
					AsyncJava8Types.class.getName());

			System.out.println(
					"registered basic types test service on server side");

			try {
				// now check on the hosting framework for the service to become
				// available
				ServiceTracker<AsyncJava8Types,AsyncJava8Types> clientTracker = new ServiceTracker<>(
						getContext(), AsyncJava8Types.class, null);
				clientTracker.open();

				// the proxy should appear in this framework
				AsyncJava8Types client = Tracker.waitForService(clientTracker,
						timeout);
				assertNotNull("no proxy for AsyncJava8Types found!", client);

				ServiceReference<AsyncJava8Types> sr = clientTracker
						.getServiceReference();

				Object property = sr
						.getProperty(RemoteServiceConstants.SERVICE_INTENTS);
				assertNotNull("No intents supported", property);

				Collection<String> intents;
				if (property instanceof String) {
					intents = Collections.singleton((String) property);
				} else if (property instanceof String[]) {
					intents = Arrays.asList((String[]) property);
				} else if (property instanceof Collection) {
					@SuppressWarnings("unchecked")
					Collection<String> tmp = (Collection<String>) property;
					intents = tmp;
				} else {
					throw new IllegalArgumentException(
							"Supported intents are not of the correct type "
									+ property.getClass());
				}

				assertTrue("Did not pick up the osgi.async intent",
						intents.contains("osgi.async"));

				final Semaphore s = new Semaphore(0);

				// CompletableFuture
				CompletableFuture<String> cf = client
						.getCompletableFuture(1000);
				cf.thenRun(new Runnable() {
					@Override
					public void run() {
						s.release();
					}
				});
				assertFalse(s.tryAcquire());
				assertTrue(s.tryAcquire(2, TimeUnit.SECONDS));
				assertEquals(AsyncJava8Types.RESULT, cf.get());

				// CompletionStage
				CompletionStage<String> cs = client.getCompletionStage(1000);
				cs.thenAccept(new Consumer<String>() {
					@Override
					public void accept(String r) {
						if (AsyncJava8Types.RESULT.equals(r)) {
							s.release();
						} else {
							System.out
									.println("Received the wrong result " + r);
						}
					}
				});
				assertFalse(s.tryAcquire());
				assertTrue(s.tryAcquire(2, TimeUnit.SECONDS));

				clientTracker.close();

			} finally {
				srTestService.unregister();
			}
		} catch (NoClassDefFoundError e) {
			// must be less than java 8.
		}
	}

    private void processFreePortProperties(Hashtable properties) {
        String freePort = getFreePort();
        for (Iterator it = properties.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().toString().trim().equals(FREE_PORT)) {
                entry.setValue(freePort);
            }
        }
    }

    private String getFreePort() {
        try {
            ServerSocket ss = new ServerSocket(0);
            String port = "" + ss.getLocalPort();
            ss.close();
            System.out.println("Found free port " + port);
            return port;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
	 * @return
	 */
	private Hashtable loadServerTCKProperties() {
		String serverconfig = getProperty("org.osgi.test.cases.remoteservices.serverconfig");
		assertNotNull(
				"did not find org.osgi.test.cases.remoteservices.serverconfig system property",
				serverconfig);
		Hashtable properties = new Hashtable();

		for (StringTokenizer tok = new StringTokenizer(serverconfig, ","); tok
				.hasMoreTokens();) {
			String propertyName = tok.nextToken();
			String value = getProperty(propertyName);
			assertNotNull("system property not found: " + propertyName, value);
			properties.put(propertyName, value);
		}

		return properties;
	}

	private Set getSupportedConfigTypes() throws Exception {
		// make sure there is a Distribution Provider installed in the framework
//		Filter filter = getFramework().getBundleContext().createFilter("(&(objectClass=*)(" +
//				DistributionProviderConstants.REMOTE_CONFIGS_SUPPORTED + "=*))");
		Filter filter = getFramework().getBundleContext().createFilter("(" +
				DistributionProviderConstants.REMOTE_CONFIGS_SUPPORTED + "=*)");
		ServiceTracker dpTracker = new ServiceTracker(getFramework().getBundleContext(), filter, null);
		dpTracker.open();

		Object dp = Tracker.waitForService(dpTracker, timeout);
		assertNotNull("No DistributionProvider found", dp);
		ServiceReference dpReference = dpTracker.getServiceReference();
		assertNotNull(dpReference);
		assertNotNull(dpReference.getProperty(DistributionProviderConstants.REMOTE_INTENTS_SUPPORTED));

		Set supportedConfigTypes = new HashSet(); // collect all supported config types

		Object configProperty = dpReference.getProperty(DistributionProviderConstants.REMOTE_CONFIGS_SUPPORTED);
		if (configProperty instanceof String) {
			StringTokenizer st = new StringTokenizer((String)configProperty, " ");
			while (st.hasMoreTokens()) {
				supportedConfigTypes.add(st.nextToken());
			}
		} else if (configProperty instanceof Collection) {
			Collection col = (Collection) configProperty;
			for (Iterator it=col.iterator(); it.hasNext(); ) {
				supportedConfigTypes.add(it.next());
			}
		} else { // assume String[]
			String[] arr = (String[]) configProperty;
			for (int i=0; i<arr.length; i++) {
				supportedConfigTypes.add(arr[i]);
			}
		}
		dpTracker.close();

		return supportedConfigTypes;
	}

	/**
	 * Verifies the server side framework that it exports the test packages for the interface
	 * used by the test service.
	 * @throws Exception
	 */
	private void verifyFramework() throws Exception {
		Framework f = getFramework();
		BundleWiring wiring = f.getBundleContext().getBundle()
				.adapt(BundleWiring.class);
		assertNotNull(
				"Framework is not supplying a BundleWiring for the system bundle",
				wiring);
		List<BundleCapability> exportedPkgs = wiring
				.getCapabilities(BundleRevision.PACKAGE_NAMESPACE);

		for (BundleCapability exportedPkg : exportedPkgs) {
			String name = (String) exportedPkg.getAttributes().get(
					BundleRevision.PACKAGE_NAMESPACE);
			if (name.equals(ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON)) {
				return;
			}
		}
		fail("Framework System Bundle is not exporting package "
				+ ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON);
	}

	private ServiceRegistration< ? > installTestBundleAndRegisterServiceObject(
			String serviceClassName, Dictionary<String,Object> properties,
			String... ifaces) throws Exception {
		Bundle bundle = getFramework().getBundleContext()
				.installBundle("test-bundle", createTestBundle());
		bundle.start();

		Object object = bundle.loadClass(serviceClassName)
				.getConstructor()
				.newInstance();

		return bundle.getBundleContext().registerService(ifaces, object,
				properties);
	}

	private InputStream createTestBundle() throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			Manifest manifest = new Manifest();
			Attributes mainAttributes = manifest.getMainAttributes();

			mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
			mainAttributes.put(new Attributes.Name(BUNDLE_MANIFESTVERSION),
					"2");
			mainAttributes.put(new Attributes.Name(BUNDLE_SYMBOLICNAME),
					ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON + ".bundle");
			mainAttributes.put(new Attributes.Name(IMPORT_PACKAGE),
					"org.osgi.util.promise;version=\"[1,2)\",org.osgi.framework");

			try (JarOutputStream jos = new JarOutputStream(baos, manifest)) {
				Bundle bundle = getContext().getBundle();
				writePackage(jos, "/org/osgi/test/cases/remoteservices/common",
						bundle);
				writePackage(jos, "/org/osgi/test/cases/remoteservices/impl",
						bundle);
			}
			return new ByteArrayInputStream(baos.toByteArray());
		}
	}

	private void writePackage(JarOutputStream jos, String path, Bundle bundle)
			throws IOException {
		Enumeration<String> paths = bundle.getEntryPaths(path);
		while (paths.hasMoreElements()) {
			String name = paths.nextElement();
			jos.putNextEntry(new ZipEntry(name));
			try (InputStream is = bundle.getResource(name).openStream()) {
				byte[] b = new byte[4096];
				int i;
				while ((i = is.read(b)) != -1) {
					jos.write(b, 0, i);
				}
				jos.closeEntry();
			}
		}
	}
}
