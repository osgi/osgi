/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.test.cases.remoteserviceadmin.junit;

import static org.osgi.framework.Constants.*;
import static org.osgi.service.remoteserviceadmin.RemoteConstants.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.test.support.MockFactory;

public class EndpointDescriptionTests extends TestCase {

	public void testFromMap() {
		EndpointDescription ed;
		Map<String, Object> props;

		props = new HashMap<String, Object>();
		try {
			ed = newEndpointDescription(props);
			fail("missing required properties");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		String testUUID = "testUUID";
		String someId = "someId";
		props.put(ENDPOINT_ID, someId);
		try {
			ed = newEndpointDescription(props);
			fail("missing required properties");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		props.put(OBJECTCLASS, new String[] {"foo"});
		try {
			ed = newEndpointDescription(props);
			fail("missing required properties");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		props.put(SERVICE_IMPORTED_CONFIGS, "config");
		ed = newEndpointDescription(props);
		assertEquals("wrong remote id", someId, ed.getId());

		assertEquals("remote service id should be zero", 0l, ed
.getServiceId());
		assertNull("remote framework uuid should be null", ed
				.getFrameworkUUID());

		props.put(ENDPOINT_FRAMEWORK_UUID, testUUID);
		ed = newEndpointDescription(props);
		assertEquals("wrong uuid", testUUID, ed.getFrameworkUUID());

		props.put(ENDPOINT_SERVICE_ID, "not a valid long");
		try {
			ed = newEndpointDescription(props);
			fail("invalid endpoint.id property");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		props.put(ENDPOINT_SERVICE_ID, new Object());
		try {
			ed = newEndpointDescription(props);
			fail("invalid endpoint.id property");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		Long someID = new Long(12l);
		props.put(ENDPOINT_SERVICE_ID, someID);
		ed = newEndpointDescription(props);
		assertEquals("wrong id", someID.longValue(), ed.getServiceId());

		props.put(OBJECTCLASS, "not a String[]");
		try {
			ed = newEndpointDescription(props);
			fail("invalid objectClass property");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		props.put(OBJECTCLASS, new String[] {});
		try {
			ed = newEndpointDescription(props);
			fail("invalid objectClass property");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		String[] objectClass = new String[] {"com.acme.Foo", "com.acme.FOO"};
		props.put(OBJECTCLASS, objectClass);
		ed = newEndpointDescription(props);
		testMutability(ed);
		List<String> interfs = ed.getInterfaces();
		assertEquals("should have 2 interfaces", 2, interfs.size());
		assertEquals("first interface wrong", objectClass[0], interfs.get(0));
		assertEquals("second interface wrong", objectClass[1], interfs.get(1));
		assertEquals("package version wrong", Version.emptyVersion, ed
				.getPackageVersion(getPackageName(objectClass[0])));
		assertEquals("package version wrong", Version.emptyVersion, ed
				.getPackageVersion(getPackageName(objectClass[1])));
		assertEquals("package version wrong", Version.emptyVersion, ed
				.getPackageVersion("xxx"));

		props.put(ENDPOINT_PACKAGE_VERSION_ + getPackageName(objectClass[0]),
				"bad version");
		try {
			ed = newEndpointDescription(props);
			fail("invalid package version property");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		Version someVersion = new Version(1, 2, 3, "somequalifier");
		props.put(ENDPOINT_PACKAGE_VERSION_ + getPackageName(objectClass[0]),
				someVersion
				.toString());
		ed = newEndpointDescription(props);
		assertEquals("package version wrong", someVersion, ed
				.getPackageVersion(getPackageName(objectClass[0])));

		props
				.remove(ENDPOINT_PACKAGE_VERSION_
						+ getPackageName(objectClass[0]));
		props.put(ENDPOINT_PACKAGE_VERSION_ + getPackageName(objectClass[1]),
				"bad version");
		try {
			ed = newEndpointDescription(props);
			fail("invalid package version property");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		props.put(ENDPOINT_PACKAGE_VERSION_ + getPackageName(objectClass[1]),
				someVersion
				.toString());
		ed = newEndpointDescription(props);
		assertEquals("package version wrong", someVersion, ed
				.getPackageVersion(getPackageName(objectClass[1])));

	}

	public void testBadMap() {
		EndpointDescription ed;
		Map<String, Object> props = new HashMap<String, Object>();
		String testUUID = "testUUID";
		String someId = "someId";
		props.put(ENDPOINT_ID, someId);
		props.put(OBJECTCLASS, new String[] {"foo"});
		props.put(SERVICE_IMPORTED_CONFIGS, "config");
		BundleContext testContext = newMockBundleContext(testUUID);
		Bundle testBundle = newMockBundle(1, "testName", "testLocation",
				testContext);
		Map<String, Object> serviceProps = new TreeMap<String, Object>(
				String.CASE_INSENSITIVE_ORDER);
		ServiceReference ref = newMockServiceReference(testBundle, serviceProps);

		props.put("foo", "bar");
		props.put("Foo", "bar");

		try {
			ed = newEndpointDescription(props);
			fail("duplicate keys");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		try {
			ed = newEndpointDescription(ref, props);
			fail("duplicate keys");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		props.remove("Foo");
		props.put("foo", "bar");
		Map<Object, Object> bad = (Map) props;
		bad.put(this, "bar");

		try {
			ed = newEndpointDescription(props);
			fail("non string key");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			ed = newEndpointDescription(ref, props);
			fail("non string key");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testFromReference() {
		EndpointDescription ed;
		Map<String, Object> props = new HashMap<String, Object>();
		String testUUID = "testUUID";
		BundleContext testContext = newMockBundleContext(testUUID);
		Bundle testBundle = newMockBundle(1, "testName", "testLocation",
				testContext);
		Map<String, Object> serviceProps = new TreeMap<String, Object>(
				String.CASE_INSENSITIVE_ORDER);
		ServiceReference ref = newMockServiceReference(testBundle, serviceProps);

		try {
			ed = newEndpointDescription(ref, null);
			fail("missing required properties");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		try {
			ed = newEndpointDescription(ref, props);
			fail("missing required properties");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		String someId = "someId";
		props.put(ENDPOINT_ID, someId);
		try {
			ed = newEndpointDescription(ref, null);
			fail("missing required properties");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		serviceProps.put(ENDPOINT_ID, someId);
		try {
			ed = newEndpointDescription(ref, props);
			fail("missing required properties");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		props.put(OBJECTCLASS, new String[] {"foo"});
		try {
			ed = newEndpointDescription(ref, null);
			fail("missing required properties");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		serviceProps.put(OBJECTCLASS, new String[] {"foo"});
		try {
			ed = newEndpointDescription(ref, props);
			fail("missing required properties");
		}
		catch (IllegalArgumentException e) {
			// expected
		}


		serviceProps.put(SERVICE_IMPORTED_CONFIGS, "config");
		ed = newEndpointDescription(ref, null);
		assertEquals("wrong remote id", someId, ed.getId());

		assertEquals("remote service id should be zero", 0l, ed
.getServiceId());
		assertEquals("wrong uuid", testUUID, ed.getFrameworkUUID());

		props.put(ENDPOINT_FRAMEWORK_UUID, "newUUID");
		props.put(SERVICE_IMPORTED_CONFIGS, "config");
		ed = newEndpointDescription(props);
		assertEquals("wrong uuid", "newUUID", ed.getFrameworkUUID());
		props.remove(ENDPOINT_FRAMEWORK_UUID);

		props.put(ENDPOINT_SERVICE_ID, "not a valid long");
		try {
			ed = newEndpointDescription(ref, props);
			fail("invalid endpoint.id property");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		props.put(ENDPOINT_SERVICE_ID, new Object());
		try {
			ed = newEndpointDescription(ref, props);
			fail("invalid endpoint.id property");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		Long someID = new Long(12l);
		props.remove(ENDPOINT_SERVICE_ID);
		serviceProps.put(SERVICE_ID, someID);
		ed = newEndpointDescription(ref, props);
		assertEquals("wrong id", someID.longValue(), ed.getServiceId());

		String[] objectClass = new String[] {"com.acme.Foo", "com.acme.FOO"};
		props.put(OBJECTCLASS, objectClass);
		serviceProps.put(OBJECTCLASS, "not a String[]");
		ed = newEndpointDescription(ref, props);

		serviceProps.put("OBJECTCLASS", objectClass);
		props.put(OBJECTCLASS, "not a String[]");
		try {
			ed = newEndpointDescription(ref, props);
			fail("invalid objectClass property");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		props.remove(OBJECTCLASS);
		ed = newEndpointDescription(ref, props);
		testMutability(ed);
		List<String> interfs = ed.getInterfaces();
		assertEquals("should have 2 interfaces", 2, interfs.size());
		assertEquals("first interface wrong", objectClass[0], interfs.get(0));
		assertEquals("second interface wrong", objectClass[1], interfs.get(1));
		assertEquals("package version wrong", Version.emptyVersion, ed
				.getPackageVersion(getPackageName(objectClass[0])));
		assertEquals("package version wrong", Version.emptyVersion, ed
				.getPackageVersion(getPackageName(objectClass[1])));
		assertEquals("package version wrong", Version.emptyVersion, ed
				.getPackageVersion("xxx"));

		serviceProps.put(ENDPOINT_PACKAGE_VERSION_
				+ getPackageName(objectClass[0]),
				"bad version");
		try {
			ed = newEndpointDescription(ref, props);
			fail("invalid package version property");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		Version someVersion = new Version(1, 2, 3, "somequalifier");
		serviceProps.put(ENDPOINT_PACKAGE_VERSION_
				+ getPackageName(objectClass[0]),
				someVersion.toString());
		ed = newEndpointDescription(ref, props);
		assertEquals("package version wrong", someVersion, ed
				.getPackageVersion(getPackageName(objectClass[0])));

		serviceProps.put(ENDPOINT_PACKAGE_VERSION_
				+ getPackageName(objectClass[1]),
				"bad version");
		try {
			ed = newEndpointDescription(ref, props);
			fail("invalid package version property");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testIntents() {
		EndpointDescription ed;
		Map<String, Object> props = new HashMap<String, Object>();
		String someId = "someId";
		props.put(ENDPOINT_ID, someId);
		props.put(OBJECTCLASS, new String[] {"foo"});
		props.put(SERVICE_IMPORTED_CONFIGS, "config");
		String testUUID = "testUUID";
		BundleContext testContext = newMockBundleContext(testUUID);
		Bundle testBundle = newMockBundle(1, "testName", "testLocation",
				testContext);
		Map<String, Object> serviceProps = new TreeMap<String, Object>(
				String.CASE_INSENSITIVE_ORDER);
		serviceProps.put(ENDPOINT_ID, someId);
		serviceProps.put(OBJECTCLASS, new String[] {"foo"});
		serviceProps.put(SERVICE_IMPORTED_CONFIGS, "config");
		ServiceReference ref = newMockServiceReference(testBundle, serviceProps);
		List<String> intents;

		ed = newEndpointDescription(props);
		intents = ed.getIntents();
		assertNotNull("intents null", intents);
		assertTrue("intents not empty", intents.isEmpty());
		testListMutability(intents);

		ed = newEndpointDescription(ref, null);
		intents = ed.getIntents();
		assertNotNull("intents null", intents);
		assertTrue("intents not empty", intents.isEmpty());
		testListMutability(intents);

		props.put(SERVICE_INTENTS, this);
		serviceProps.put(SERVICE_INTENTS, this);

		ed = newEndpointDescription(props);
		intents = ed.getIntents();
		assertNotNull("intents null", intents);
		assertTrue("intents not empty", intents.isEmpty());
		testListMutability(intents);

		ed = newEndpointDescription(ref, null);
		intents = ed.getIntents();
		assertNotNull("intents null", intents);
		assertTrue("intents not empty", intents.isEmpty());
		testListMutability(intents);

		String scalarIntent = "some.intent";
		props.put(SERVICE_INTENTS, scalarIntent);
		serviceProps.put(SERVICE_INTENTS, scalarIntent);

		ed = newEndpointDescription(props);
		intents = ed.getIntents();
		assertNotNull("intents null", intents);
		assertFalse("intents not empty", intents.isEmpty());
		assertEquals("only one element", 1, intents.size());
		assertEquals("wrong intent value", scalarIntent, intents.get(0));
		testListMutability(intents);

		ed = newEndpointDescription(ref, null);
		intents = ed.getIntents();
		assertNotNull("intents null", intents);
		assertFalse("intents not empty", intents.isEmpty());
		assertEquals("only one element", 1, intents.size());
		assertEquals("wrong intent value", scalarIntent, intents.get(0));
		testListMutability(intents);

		String[] arrayIntents = new String[] {"some.intent1", "some.intent2"};
		props.put(SERVICE_INTENTS, arrayIntents);
		serviceProps.put(SERVICE_INTENTS, arrayIntents);

		ed = newEndpointDescription(props);
		intents = ed.getIntents();
		assertNotNull("intents null", intents);
		assertFalse("intents not empty", intents.isEmpty());
		assertEquals("wrong number of elements", arrayIntents.length, intents
				.size());
		for (int i = 0; i < arrayIntents.length; i++) {
			assertEquals("wrong intent value", arrayIntents[i], intents.get(i));
		}
		testListMutability(intents);

		ed = newEndpointDescription(ref, null);
		intents = ed.getIntents();
		assertNotNull("intents null", intents);
		assertFalse("intents not empty", intents.isEmpty());
		assertEquals("wrong number of elements", arrayIntents.length, intents
				.size());
		for (int i = 0; i < arrayIntents.length; i++) {
			assertEquals("wrong intent value", arrayIntents[i], intents.get(i));
		}
		testListMutability(intents);

		List<String> listIntents = Arrays.asList("some.intent3",
				"some.intent4", "some.intent5");
		props.put(SERVICE_INTENTS, listIntents);
		serviceProps.put(SERVICE_INTENTS, listIntents);

		ed = newEndpointDescription(props);
		intents = ed.getIntents();
		assertNotNull("intents null", intents);
		assertFalse("intents not empty", intents.isEmpty());
		assertEquals("wrong number of elements", listIntents.size(), intents
				.size());
		for (int i = 0; i < listIntents.size(); i++) {
			assertEquals("wrong intent value", listIntents.get(i), intents
					.get(i));
		}
		testListMutability(intents);

		ed = newEndpointDescription(ref, null);
		intents = ed.getIntents();
		assertNotNull("intents null", intents);
		assertFalse("intents not empty", intents.isEmpty());
		assertEquals("wrong number of elements", listIntents.size(), intents
				.size());
		for (int i = 0; i < listIntents.size(); i++) {
			assertEquals("wrong intent value", listIntents.get(i), intents
					.get(i));
		}
		testListMutability(intents);

	}

	public void testConfigurationsTypes() {
		EndpointDescription ed;
		Map<String, Object> props = new HashMap<String, Object>();
		String someId = "someId";
		props.put(ENDPOINT_ID, someId);
		props.put(OBJECTCLASS, new String[] {"foo"});
		props.put(SERVICE_IMPORTED_CONFIGS, "config");
		String testUUID = "testUUID";
		BundleContext testContext = newMockBundleContext(testUUID);
		Bundle testBundle = newMockBundle(1, "testName", "testLocation",
				testContext);
		Map<String, Object> serviceProps = new TreeMap<String, Object>(
				String.CASE_INSENSITIVE_ORDER);
		serviceProps.put(ENDPOINT_ID, someId);
		serviceProps.put(OBJECTCLASS, new String[] {"foo"});
		serviceProps.put(SERVICE_IMPORTED_CONFIGS, "config");
		ServiceReference ref = newMockServiceReference(testBundle, serviceProps);
		List<String> configTypes;

		props.put(SERVICE_IMPORTED_CONFIGS, this);
		serviceProps.put(SERVICE_IMPORTED_CONFIGS, this);

		try {
			ed = newEndpointDescription(props);
			fail("config type empty");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		try {
			ed = newEndpointDescription(ref, null);
			fail("config type empty");
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		String scalarConfigType = "some.configtype";
		props.put(SERVICE_IMPORTED_CONFIGS, scalarConfigType);
		serviceProps.put(SERVICE_IMPORTED_CONFIGS, scalarConfigType);

		ed = newEndpointDescription(props);
		configTypes = ed.getConfigurationTypes();
		assertNotNull("configtypes null", configTypes);
		assertFalse("configtypes not empty", configTypes.isEmpty());
		assertEquals("only one element", 1, configTypes.size());
		assertEquals("wrong configtype value", scalarConfigType, configTypes
				.get(0));
		testListMutability(configTypes);

		ed = newEndpointDescription(ref, null);
		configTypes = ed.getConfigurationTypes();
		assertNotNull("configtypes null", configTypes);
		assertFalse("configtypes not empty", configTypes.isEmpty());
		assertEquals("only one element", 1, configTypes.size());
		assertEquals("wrong configtype value", scalarConfigType, configTypes
				.get(0));
		testListMutability(configTypes);

		String[] arrayConfigTypes = new String[] {"some.configtype1",
				"some.configtype2"};
		props.put(SERVICE_IMPORTED_CONFIGS, arrayConfigTypes);
		serviceProps.put(SERVICE_IMPORTED_CONFIGS, arrayConfigTypes);

		ed = newEndpointDescription(props);
		configTypes = ed.getConfigurationTypes();
		assertNotNull("configtypes null", configTypes);
		assertFalse("configtypes not empty", configTypes.isEmpty());
		assertEquals("wrong number of elements", arrayConfigTypes.length,
				configTypes.size());
		for (int i = 0; i < arrayConfigTypes.length; i++) {
			assertEquals("wrong configtype value", arrayConfigTypes[i],
					configTypes.get(i));
		}
		testListMutability(configTypes);

		ed = newEndpointDescription(ref, null);
		configTypes = ed.getConfigurationTypes();
		assertNotNull("configtypes null", configTypes);
		assertFalse("configtypes not empty", configTypes.isEmpty());
		assertEquals("wrong number of elements", arrayConfigTypes.length,
				configTypes.size());
		for (int i = 0; i < arrayConfigTypes.length; i++) {
			assertEquals("wrong configtype value", arrayConfigTypes[i],
					configTypes.get(i));
		}
		testListMutability(configTypes);

		List<String> listConfigTypes = Arrays.asList("some.configtype3",
				"some.configtype4", "some.configtype5");
		props.put(SERVICE_IMPORTED_CONFIGS, listConfigTypes);
		serviceProps.put(SERVICE_IMPORTED_CONFIGS, listConfigTypes);

		ed = newEndpointDescription(props);
		configTypes = ed.getConfigurationTypes();
		assertNotNull("configtypes null", configTypes);
		assertFalse("configtypes not empty", configTypes.isEmpty());
		assertEquals("wrong number of elements", listConfigTypes.size(),
				configTypes.size());
		for (int i = 0; i < listConfigTypes.size(); i++) {
			assertEquals("wrong configtype value", listConfigTypes.get(i),
					configTypes.get(i));
		}
		testListMutability(configTypes);

		ed = newEndpointDescription(ref, null);
		configTypes = ed.getConfigurationTypes();
		assertNotNull("configtypes null", configTypes);
		assertFalse("configtypes not empty", configTypes.isEmpty());
		assertEquals("wrong number of elements", listConfigTypes.size(),
				configTypes.size());
		for (int i = 0; i < listConfigTypes.size(); i++) {
			assertEquals("wrong configtype value", listConfigTypes.get(i),
					configTypes.get(i));
		}
		testListMutability(configTypes);

	}

	public void testHashcode() {
		Map<String, Object> props = new HashMap<String, Object>();
		String someId = "someId";
		props.put(ENDPOINT_ID, someId);
		props.put(OBJECTCLASS, new String[] {"foo"});
		props.put(SERVICE_IMPORTED_CONFIGS, "config");
		String testUUID = "testUUID";
		BundleContext testContext = newMockBundleContext(testUUID);
		Bundle testBundle = newMockBundle(1, "testName", "testLocation",
				testContext);
		Map<String, Object> serviceProps = new TreeMap<String, Object>(
				String.CASE_INSENSITIVE_ORDER);
		serviceProps.put(ENDPOINT_ID, someId);
		serviceProps.put(OBJECTCLASS, new String[] {"foo"});
		serviceProps.put(SERVICE_IMPORTED_CONFIGS, "config");
		ServiceReference ref = newMockServiceReference(testBundle, serviceProps);
		EndpointDescription ed1, ed2, ed3;

		ed1 = newEndpointDescription(props);
		ed2 = newEndpointDescription(ref, null);
		props.put(ENDPOINT_ID, "other.id");
		ed3 = newEndpointDescription(props);

		assertTrue("hashCode should equal", ed1.hashCode() == ed2.hashCode());
		assertTrue("hashCode should not equal", ed1.hashCode() != ed3
				.hashCode());
		assertTrue("hashCode should not equal", ed2.hashCode() != ed3
				.hashCode());
	}

	public void testEquals() {
		Map<String, Object> props = new HashMap<String, Object>();
		String someId = "someId";
		props.put(ENDPOINT_ID, someId);
		props.put(OBJECTCLASS, new String[] {"foo"});
		props.put(SERVICE_IMPORTED_CONFIGS, "config");
		String testUUID = "testUUID";
		BundleContext testContext = newMockBundleContext(testUUID);
		Bundle testBundle = newMockBundle(1, "testName", "testLocation",
				testContext);
		Map<String, Object> serviceProps = new TreeMap<String, Object>(
				String.CASE_INSENSITIVE_ORDER);
		serviceProps.put(ENDPOINT_ID, someId);
		serviceProps.put(OBJECTCLASS, new String[] {"foo"});
		serviceProps.put(SERVICE_IMPORTED_CONFIGS, "config");
		ServiceReference ref = newMockServiceReference(testBundle, serviceProps);
		EndpointDescription ed1, ed2, ed3;

		ed1 = newEndpointDescription(props);
		ed2 = newEndpointDescription(ref, null);
		props.put(ENDPOINT_ID, "other.id");
		ed3 = newEndpointDescription(props);

		assertTrue("should equal", ed1.equals(ed2));
		assertTrue("should equal", ed2.equals(ed1));
		assertFalse("should not equal", ed1.equals(ed3));
		assertFalse("should not equal", ed3.equals(ed1));
		assertFalse("should not equal", ed2.equals(ed3));
		assertFalse("should not equal", ed3.equals(ed1));
		assertTrue("should equal", ed1.equals(ed1));
		assertTrue("should equal", ed2.equals(ed2));
		assertTrue("should equal", ed3.equals(ed3));
		assertFalse("should not equal", ed2.equals(this));
	}

	public void testIsSame() {
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(ENDPOINT_ID, "id1");
		props.put(OBJECTCLASS, new String[] {"foo"});
		props.put(SERVICE_IMPORTED_CONFIGS, "config");
		String testUUID = "testUUID";
		BundleContext testContext = newMockBundleContext(testUUID);
		Bundle testBundle = newMockBundle(1, "testName", "testLocation",
				testContext);
		Map<String, Object> serviceProps = new TreeMap<String, Object>(
				String.CASE_INSENSITIVE_ORDER);
		serviceProps.put(ENDPOINT_ID, "id2");
		serviceProps.put(OBJECTCLASS, new String[] {"foo"});
		serviceProps.put(SERVICE_IMPORTED_CONFIGS, "config");
		Long someID = new Long(12l);
		props.put(ENDPOINT_SERVICE_ID, someID);
		serviceProps.put(SERVICE_ID, someID);
		ServiceReference ref = newMockServiceReference(testBundle, serviceProps);
		EndpointDescription ed1, ed2, ed3, ed4;

		ed1 = newEndpointDescription(props);
		ed2 = newEndpointDescription(ref, null);
		ed3 = newEndpointDescription(props);
		props.put(ENDPOINT_ID, "id4");
		props.put(ENDPOINT_FRAMEWORK_UUID, testUUID);
		ed4 = newEndpointDescription(props);

		assertTrue("should be same", ed1.isSameService(ed1));
		assertTrue("should be same", ed3.isSameService(ed3));
		assertTrue("should be same", ed1.isSameService(ed3));
		assertTrue("should be same", ed3.isSameService(ed1));
		assertTrue("should be same", ed2.isSameService(ed4));
		assertTrue("should be same", ed4.isSameService(ed2));
		assertFalse("should not be same", ed1.isSameService(ed2));
		assertFalse("should not be same", ed2.isSameService(ed1));
	}

	public void testMatches() {
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(ENDPOINT_ID, "id1");
		props.put(OBJECTCLASS, new String[] {"foo"});
		props.put(SERVICE_IMPORTED_CONFIGS, "config");
		String testUUID = "testUUID";
		BundleContext testContext = newMockBundleContext(testUUID);
		Bundle testBundle = newMockBundle(1, "testName", "testLocation",
				testContext);
		Map<String, Object> serviceProps = new TreeMap<String, Object>(
				String.CASE_INSENSITIVE_ORDER);
		serviceProps.put(ENDPOINT_ID, "id2");
		serviceProps.put(OBJECTCLASS, new String[] {"foo"});
		serviceProps.put(SERVICE_IMPORTED_CONFIGS, "config");
		Long someID = new Long(12l);
		props.put(ENDPOINT_SERVICE_ID, someID);
		serviceProps.put(SERVICE_ID, someID);
		ServiceReference ref = newMockServiceReference(testBundle, serviceProps);
		EndpointDescription ed1, ed2;

		ed1 = newEndpointDescription(props);
		ed2 = newEndpointDescription(ref, null);

		String filter = "(" + ENDPOINT_ID + "=id1)";
		assertTrue("filter does not match", ed1.matches(filter));
		assertFalse("filter matches", ed2.matches(filter));

		filter = "(ENDPOINT.id=id2)";
		assertTrue("filter does not match", ed2.matches(filter));
		assertFalse("filter matches", ed1.matches(filter));

		filter = "(" + ENDPOINT_FRAMEWORK_UUID + "=" + testUUID + ")";
		assertTrue("filter does not match", ed2.matches(filter));
		assertFalse("filter matches", ed1.matches(filter));

		filter = "(" + ENDPOINT_FRAMEWORK_UUID + "=*)";
		assertTrue("filter does not match", ed2.matches(filter));
		assertFalse("filter matches", ed1.matches(filter));

		try {
			ed1.matches("(xx=foo");
			fail("invalid filter syntax allowed");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	private EndpointDescription newEndpointDescription(ServiceReference ref,
			Map<String, Object> props) {
		EndpointDescription ed = new EndpointDescription(ref, props);
		testMutability(ed);
		return ed;
	}

	private EndpointDescription newEndpointDescription(Map<String, Object> props) {
		EndpointDescription ed = new EndpointDescription(props);
		testMutability(ed);
		return ed;
	}

	private void testMutability(EndpointDescription ed) {
		Map<String, Object> p = ed.getProperties();
		try {
			if (!p.isEmpty()) {
				p.clear();
				fail("properties is mutable");
			}
		}
		catch (RuntimeException e) {
			// expected
		}
		try {
			p.put("foo", "bar");
			fail("properties is mutable");
		}
		catch (RuntimeException e) {
			// expected
		}
		List<String> interfs = ed.getInterfaces();
		testListMutability(interfs);
	}

	private void testListMutability(List list) {
		try {
			if (!list.isEmpty()) {
				list.clear();
				fail("list is mutable");
			}
		}
		catch (RuntimeException e) {
			// expected
		}
		try {
			list.add("foo");
			fail("list is mutable");
		}
		catch (RuntimeException e) {
			// expected
		}
	}

	private String getPackageName(String className) {
		int index = className.lastIndexOf('.');
		if (index == -1) {
			return "";
		}
		return className.substring(0, index);
	}

	public static BundleContext newMockBundleContext(String uuid) {
		return (BundleContext) MockFactory.newMock(BundleContext.class,
				new MockBundleContext(uuid));
	}

	private static class MockBundleContext {
		private final String	uuid;

		MockBundleContext(String uuid) {
			this.uuid = uuid;
		}

		public String getProperty(String key) {
			if (key.equals("org.osgi.framework.uuid")) {
				return uuid;
			}
			return null;
		}
	}

	public static Bundle newMockBundle(long id, String name, String location,
			BundleContext context) {
		return (Bundle) MockFactory.newMock(Bundle.class, new MockBundle(id,
				name, location, context));
	}

	private static class MockBundle {
		private final long			id;
		private final String		name;
		private final String		location;
		private final BundleContext	context;

		MockBundle(long id, String name, String location, BundleContext context) {
			this.id = id;
			this.name = name;
			this.location = location;
			this.context = context;
		}

		public long getBundleId() {
			return id;
		}

		public String getLocation() {
			return location;
		}

		public String getSymbolicName() {
			return name;
		}

		public BundleContext getBundleContext() {
			return context;
		}
	}

	public static ServiceReference newMockServiceReference(Bundle bundle,
			Map<String, Object> properties) {
		return (ServiceReference) MockFactory.newMock(ServiceReference.class,
				new MockServiceReference(bundle, properties));
	}

	private static class MockServiceReference {
		private final Bundle				bundle;
		private final Map<String, Object>	properties;

		MockServiceReference(Bundle bundle, Map<String, Object> properties) {
			this.bundle = bundle;
			this.properties = properties;
		}

		public Bundle getBundle() {
			return bundle;
		}

		public Object getProperty(String key) {
			Object result = properties.get(key);
			if (result != null) {
				return result;
			}
			for (Iterator<String> iter = properties.keySet().iterator(); iter
					.hasNext();) {
				String k = iter.next();
				if (k.equalsIgnoreCase(key)) {
					return properties.get(k);
				}
			}
			return null;
		}

		public String[] getPropertyKeys() {
			String[] result = new String[properties.size()];
			properties.keySet().toArray(result);
			return result;
		}
	}

}
