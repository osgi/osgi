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

package org.osgi.test.cases.dal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.Set;

import org.osgi.service.dal.Device;
import org.osgi.service.dal.DevicePermission;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Validates {@link DevicePermission} class.
 */
public final class DevicePermissionTest extends DefaultTestBundleControl {

	/**
	 * Tests correct permission filters.
	 */
	public void testFilter() {
		checkFilter("*");
		checkFilter('(' + Device.SERVICE_UID + "=testUID)");
	}

	/**
	 * Tests device permission with correct device instance.
	 */
	public void testDevice() {
		checkDevice(new TestDevice(null));
	}

	/**
	 * Tests device permission with incorrect device instance.
	 */
	public void testInvalidDevice() {
		checkInvalidDevice(null, NullPointerException.class);
		checkInvalidDevice(new TestDevice(new HashMap<>()),
				IllegalArgumentException.class);
	}

	/**
	 * Tests invalid permission filters.
	 */
	public void testInvalidFilter() {
		checkInvalidFilter(null, NullPointerException.class);
		checkInvalidFilter("", IllegalArgumentException.class);
		checkInvalidFilter("**", IllegalArgumentException.class);
		checkInvalidFilter("(", IllegalArgumentException.class);
	}

	/**
	 * Tests invalid permission actions.
	 */
	public void testInvalidAction() {
		checkInvalidAction(null, NullPointerException.class);
		checkInvalidAction("", IllegalArgumentException.class);
		checkInvalidAction(
				DevicePermission.REMOVE + DevicePermission.REMOVE, IllegalArgumentException.class);
	}

	/**
	 * Tests equals method of {@code DevicePermission} built with filter.
	 */
	public void testFilterEquals() {
		checkEquals(
				new DevicePermission("*", DevicePermission.REMOVE),
				new DevicePermission("*", DevicePermission.REMOVE));
	}

	/**
	 * Tests equals method of {@code DevicePermission} built with device
	 * instance.
	 */
	public void testDeviceEquals() {
		Map<String,Object> testDeviceProps = new HashMap<>();
		testDeviceProps.put(Device.SERVICE_UID, "test_device_id");
		Device testDevice = new TestDevice(testDeviceProps);
		checkEquals(
				new DevicePermission(testDevice, DevicePermission.REMOVE),
				new DevicePermission(testDevice, DevicePermission.REMOVE));
	}

	/**
	 * Tests different device permissions with filters, which are not equal.
	 */
	@SuppressWarnings("unlikely-arg-type")
	public void testFilterNotEquals() {
		String testDeviceId = "test_device_id";
		DevicePermission filterPermission = new DevicePermission(
				'(' + Device.SERVICE_UID + '=' + testDeviceId + ')', DevicePermission.REMOVE);
		assertFalse("The device permission is equal to a property permission.",
				filterPermission.equals(new PropertyPermission("test-property", "read")));

		Map<String,Object> testDeviceProps = new HashMap<>();
		testDeviceProps.put(Device.SERVICE_UID, testDeviceId);
		assertFalse("The device permission with a filter is equal to another one with device instance.",
				filterPermission.equals(new DevicePermission(new TestDevice(testDeviceProps), DevicePermission.REMOVE)));

		assertFalse("The device permission is equal to another one with different filter.",
				filterPermission.equals(new DevicePermission("*", DevicePermission.REMOVE)));
	}

	/**
	 * Tests different device permissions with devices, which are not equal.
	 */
	@SuppressWarnings("unlikely-arg-type")
	public void testDeviceNotEquals() {
		String testDeviceId = "test_device_id";
		Map<String,Object> testDeviceProps = new HashMap<>();
		testDeviceProps.put(Device.SERVICE_UID, testDeviceId);
		DevicePermission devicePermission = new DevicePermission(new TestDevice(testDeviceProps), DevicePermission.REMOVE);
		assertFalse("The device permission is equal to a property permission.",
				devicePermission.equals(new PropertyPermission("test-property", "read")));

		DevicePermission filterPermission = new DevicePermission(
				'(' + Device.SERVICE_UID + '=' + testDeviceId + ')', DevicePermission.REMOVE);
		assertFalse("The device permission with a device is equal to another one with filter.",
				devicePermission.equals(filterPermission));

		assertFalse("The device permission is equal to another one with different device.",
				devicePermission.equals(new DevicePermission(new TestDevice(null), DevicePermission.REMOVE)));
	}

	/**
	 * Tests the device permission hash code when the filter is used.
	 */
	public void testFilterHash() {
		String testDeviceId = "test_device_id";
		DevicePermission filterPermissionFirst = new DevicePermission(
				'(' + Device.SERVICE_UID + '=' + testDeviceId + ')', DevicePermission.REMOVE);
		DevicePermission filterPermissionSecond = new DevicePermission(
				'(' + Device.SERVICE_UID + '=' + testDeviceId + ')', DevicePermission.REMOVE);
		assertEquals("The device permission hash is not correct.",
				filterPermissionFirst.hashCode(),
				filterPermissionSecond.hashCode());
	}

	/**
	 * Tests the device permission hash code when the device is used.
	 */
	public void testDeviceHash() {
		String testDeviceId = "test_device_id";
		Map<String,Object> testDeviceProps = new HashMap<>();
		testDeviceProps.put(Device.SERVICE_UID, testDeviceId);
		DevicePermission devicePermissionFirst = new DevicePermission(
				new TestDevice(testDeviceProps), DevicePermission.REMOVE);
		DevicePermission devicePermissionSecond = new DevicePermission(
				new TestDevice(testDeviceProps), DevicePermission.REMOVE);
		assertEquals("The device permission hash is not correct.",
				devicePermissionFirst.hashCode(),
				devicePermissionSecond.hashCode());
	}

	/**
	 * Tests when {@code DevicePermission#implies(java.security.Permission)}
	 * will return {@code true}.
	 */
	public void testImply() {
		String testDeviceId = "test_device_id";
		Map<String,Object> testDeviceProps = new HashMap<>();
		testDeviceProps.put(Device.SERVICE_UID, testDeviceId);
		DevicePermission devicePermission = new DevicePermission(
				new TestDevice(testDeviceProps), DevicePermission.REMOVE);
		DevicePermission filterPermission = new DevicePermission(
				'(' + Device.SERVICE_UID + '=' + testDeviceId + ')', DevicePermission.REMOVE);
		assertTrue(
				"The device permission is not implied: " + devicePermission,
				filterPermission.implies(devicePermission));
		assertTrue(
				"The wildcard device permission cannot imply.",
				(new DevicePermission(
						"*", DevicePermission.REMOVE)).implies(devicePermission));
		assertTrue(
				"The device permission with wildcard for device UID cannot imply.",
				(new DevicePermission(
						'(' + Device.SERVICE_UID + "=*)", DevicePermission.REMOVE)).implies(devicePermission));
	}

	/**
	 * Tests when {@code DevicePermission#implies(java.security.Permission)}
	 * will return {@code false}.
	 */
	public void testNotImply() {
		String testDeviceId = "test_device_id";
		Map<String,Object> testDeviceProps = new HashMap<>();
		testDeviceProps.put(Device.SERVICE_UID, testDeviceId);
		DevicePermission devicePermission = new DevicePermission(
				new TestDevice(testDeviceProps), DevicePermission.REMOVE);
		DevicePermission filterPermission = new DevicePermission(
				'(' + Device.SERVICE_UID + '=' + testDeviceId + ')', DevicePermission.REMOVE);
		assertFalse(
				"The device permission with device instance must not imply any other permission.",
				devicePermission.implies(filterPermission));
		assertFalse(
				"The device permission with a filter implies itself.",
				filterPermission.implies(filterPermission));
		assertFalse(
				"The device permission with a filter implies another permission with a filter.",
				filterPermission.implies(new DevicePermission(
						'(' + Device.SERVICE_UID + '=' + testDeviceId + ')', DevicePermission.REMOVE)));
		assertFalse(
				"The device permission implies property permission.",
				filterPermission.implies(new PropertyPermission("test-ptop", "read")));
	}

	/**
	 * Tests successful device permission serialization.
	 * 
	 * @throws IOException In case of I/O error.
	 * @throws ClassNotFoundException If the device permission cannot be read to
	 *         the correct type.
	 */
	public void testSerialization() throws IOException, ClassNotFoundException {
		checkSerialization(new DevicePermission(
				'(' + Device.SERVICE_UID + "=test-id)", DevicePermission.REMOVE));
		checkSerialization(new DevicePermission(
				'(' + Device.SERVICE_UID + "=*)", DevicePermission.REMOVE));
		checkSerialization(new DevicePermission("*", DevicePermission.REMOVE));
	}

	/**
	 * Tests failed device permission serialization.
	 * 
	 * @throws IOException In case of I/O error.
	 */
	public void testSerializationFail() throws IOException {
		checkSerializationFail(new DevicePermission(new TestDevice(null), DevicePermission.REMOVE));
	}

	/**
	 * Tests that the device permission is correctly added to the device
	 * permission collection.
	 */
	public void testCollectionAdd() {
		String testDeviceId = "test_device_id";
		DevicePermission filterPermission = new DevicePermission(
				'(' + Device.SERVICE_UID + '=' + testDeviceId + ')', DevicePermission.REMOVE);
		PermissionCollection permCollection = filterPermission.newPermissionCollection();
		permCollection.add(filterPermission);
		Enumeration<Permission> perms = permCollection.elements();
		DevicePermission filterPermissionCollection = (DevicePermission) perms.nextElement();
		assertSame("The device permission is not found in the collection.",
				filterPermission, filterPermissionCollection);
		assertFalse("Only one permission is expected in the collection.", perms.hasMoreElements());
	}

	/**
	 * Tests that invalid permissions will not be added to the device permission
	 * collection.
	 */
	public void testCollectionFailAdd() {
		checkCollectionFailAdd(new DevicePermission(new TestDevice(null), DevicePermission.REMOVE));
		checkCollectionFailAdd(new PropertyPermission("test-prop", "read"));
	}

	/**
	 * Tests imply of the device permission collection.
	 */
	public void testCollectionImply() {
		String testDeviceId = "test_device_id";
		Map<String,Object> testDeviceProps = new HashMap<>();
		testDeviceProps.put(Device.SERVICE_UID, testDeviceId);
		DevicePermission devicePermission = new DevicePermission(
				new TestDevice(testDeviceProps), DevicePermission.REMOVE);
		DevicePermission filterPermission = new DevicePermission(
				'(' + Device.SERVICE_UID + '=' + testDeviceId + ')', DevicePermission.REMOVE);
		PermissionCollection permissionCollection = filterPermission.newPermissionCollection();
		permissionCollection.add(filterPermission);
		assertTrue(
				"The connection cannot imply the device permission: " + devicePermission,
				permissionCollection.implies(devicePermission));

		permissionCollection.add(
				new DevicePermission("*", DevicePermission.REMOVE));
		assertTrue(
				"The connection cannot imply the device permission.",
				permissionCollection.implies(new DevicePermission(
						new TestDevice(null), DevicePermission.REMOVE)));
	}

	/**
	 * Tests when the device permission collection will not imply.
	 */
	public void testCollectionNotImply() {
		String testDeviceId = "test_device_id";
		Map<String,Object> testDeviceProps = new HashMap<>();
		testDeviceProps.put(Device.SERVICE_UID, testDeviceId);
		DevicePermission devicePermission = new DevicePermission(
				new TestDevice(testDeviceProps), DevicePermission.REMOVE);
		DevicePermission filterPermission = new DevicePermission(
				'(' + Device.SERVICE_UID + '=' + testDeviceId + ')', DevicePermission.REMOVE);
		PermissionCollection permissionCollection = filterPermission.newPermissionCollection();

		assertFalse("The empty collection must not imply any permission.",
				permissionCollection.implies(devicePermission));
		permissionCollection.add(
				new DevicePermission(
						'(' + Device.SERVICE_UID + '=' + testDeviceId + testDeviceId + ')',
						DevicePermission.REMOVE));
		assertFalse("The collection doesn't contain permission to imply the device permission: " + permissionCollection,
				permissionCollection.implies(devicePermission));
		assertFalse("The permission collection must not imply property permission.",
				permissionCollection.implies(new PropertyPermission("test-ptop", "read")));
	}

	/**
	 * Tests device permission serialization.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void testCollectionSerialization() throws IOException, ClassNotFoundException {
		DevicePermission filterPermission = new DevicePermission(
				'(' + Device.SERVICE_UID + "=test_device_is)", DevicePermission.REMOVE);
		PermissionCollection permissionCollection = filterPermission.newPermissionCollection();
		checkSerialization(permissionCollection);

		permissionCollection.add(filterPermission);
		checkSerialization(permissionCollection);

		permissionCollection.add(new DevicePermission(
				'(' + Device.SERVICE_UID + "=*)", DevicePermission.REMOVE));
		checkSerialization(permissionCollection);
	}

	private void checkCollectionFailAdd(Permission permission) {
		String testDeviceId = "test_device_id";
		DevicePermission filterPermission = new DevicePermission(
				'(' + Device.SERVICE_UID + '=' + testDeviceId + ')', DevicePermission.REMOVE);
		PermissionCollection permCollection = filterPermission.newPermissionCollection();
		try {
			permCollection.add(permission);
			fail("The permission must not be added to the collection: " + permission);
		} catch (IllegalArgumentException iae) {
			// expected
		}
	}

	private void checkSerializationFail(Serializable serializable) throws IOException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
		try {
			objectOutputStream = new ObjectOutputStream(byteOutputStream);
			objectOutputStream.writeObject(serializable);
			fail("NotSerializableException is expected, but there is no exception.");
		} catch (NotSerializableException nse) {
			// expected
		} finally {
			if (null != objectOutputStream) {
				try {
					objectOutputStream.close();
				} catch (IOException ioe) {
					// go ahead
				}
			}
		}
	}

	private void checkSerialization(PermissionCollection pc) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
		objectOutputStream.writeObject(pc);
		objectOutputStream.close();

		ObjectInputStream objectInputStream = new ObjectInputStream(
				new ByteArrayInputStream(byteOutputStream.toByteArray()));
		PermissionCollection readPC = (PermissionCollection) objectInputStream.readObject();
		objectInputStream.close();

		assertEquals(
				"The device permission is not correctly read: " + pc,
				pc, readPC);
	}

	private void assertEquals(String message, PermissionCollection expected, PermissionCollection actual) {
		Set<Permission> expectedPermsSet = new HashSet<>();
		for (Enumeration<Permission> expectedPerms = expected
				.elements(); expectedPerms.hasMoreElements();/* empty */) {
			expectedPermsSet.add(expectedPerms.nextElement());
		}
		for (Enumeration<Permission> actualPerms = actual
				.elements(); actualPerms.hasMoreElements();/* empty */) {
			expectedPermsSet.remove(actualPerms.nextElement());
		}
		assertTrue(message, expectedPermsSet.isEmpty());
	}

	private void checkSerialization(DevicePermission dp) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
		objectOutputStream.writeObject(dp);
		objectOutputStream.close();

		ObjectInputStream objectInputStream = new ObjectInputStream(
				new ByteArrayInputStream(byteOutputStream.toByteArray()));
		DevicePermission readDP = (DevicePermission) objectInputStream.readObject();
		objectInputStream.close();

		assertEquals(
				"The device permission is not correctly read: " + dp,
				dp, readDP);
	}

	private void checkEquals(DevicePermission expectedPermission, DevicePermission actualPermission) {
		assertEquals("The device permission must be equal to itself.",
				actualPermission, actualPermission);
		assertEquals("The device permission must be equal to the same permission.",
				expectedPermission, actualPermission);
	}

	private void checkInvalidDevice(Device device,
			Class< ? extends Throwable> exceptionClass) {
		try {
			new DevicePermission(device, DevicePermission.REMOVE);
			fail("The device permission has been built with invalid device: " + device);
		} catch (RuntimeException re) {
			assertTrue("Unexpected exception: " + re, exceptionClass.isAssignableFrom(re.getClass()));
		}
	}

	private void checkInvalidAction(String action,
			Class< ? extends Throwable> exceptionClass) {
		// filter constructor
		try {
			new DevicePermission("*", action);
			fail("The device permission has been built with invalid action: " + action);
		} catch (RuntimeException re) {
			assertTrue("Unexpected exception: " + re, exceptionClass.isAssignableFrom(re.getClass()));
		}
		// device instance constructor
		try {
			new DevicePermission(new TestDevice(null), action);
			fail("The device permission has been built with invalid action: " + action);
		} catch (RuntimeException re) {
			assertTrue("Unexpected exception: " + re, exceptionClass.isAssignableFrom(re.getClass()));
		}
	}

	private void checkInvalidFilter(String filter,
			Class< ? extends Throwable> excpetionClass) {
		try {
			new DevicePermission(filter, DevicePermission.REMOVE);
			fail("The device permission has been built with invalid filter: " + filter);
		} catch (RuntimeException re) {
			assertTrue("Unexpected exception: " + re, excpetionClass.isAssignableFrom(re.getClass()));
		}
	}

	private void checkDevice(Device device) {
		DevicePermission devicePermission = new DevicePermission(device, DevicePermission.REMOVE);
		assertEquals("The permission filter is not correctly set.",
				'(' + Device.SERVICE_UID + '=' + device.getServiceProperty(Device.SERVICE_UID) + ')',
				devicePermission.getName());
		assertEquals("The permission action is not correctly set.",
				DevicePermission.REMOVE, devicePermission.getActions());
	}

	private void checkFilter(String filter) {
		DevicePermission devicePermission = new DevicePermission(
				filter, DevicePermission.REMOVE);
		assertEquals("The permission filter is not correctly set.",
				filter, devicePermission.getName());
		assertEquals("The permission action is not correctly set.",
				DevicePermission.REMOVE, devicePermission.getActions());
	}
}
