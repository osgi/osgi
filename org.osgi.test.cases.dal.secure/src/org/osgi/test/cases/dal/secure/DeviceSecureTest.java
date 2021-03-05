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

package org.osgi.test.cases.dal.secure;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Collection;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.DevicePermission;
import org.osgi.test.cases.dal.secure.step.SecureDeviceTestSteps;
import org.osgi.test.cases.dal.step.TestStepDeviceProxy;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Test class validates the device operations and properties.
 */
public class DeviceSecureTest extends DefaultTestBundleControl {

	private TestStepDeviceProxy	testStepProxy;

	protected void setUp() throws Exception { // NOPMD
		super.setUp();
		this.testStepProxy = new TestStepDeviceProxy(super.getContext());
	}

	protected void tearDown() throws Exception { // NOPMD
		this.testStepProxy.close();
		super.tearDown();
	}

	/**
	 * Tests device remove operation in security mode. The test method requires
	 * at least one device with remove support.
	 * 
	 * @throws PrivilegedActionException If an expected error is available while
	 *         removing the device.
	 */
	public void testRemoveDeviceAllow() throws PrivilegedActionException {
		this.testStepProxy.execute(
				SecureDeviceTestSteps.STEP_ID_AVAILABLE_DEVICE,
				SecureDeviceTestSteps.STEP_MESSAGE_AVAILABLE_DEVICE);
		AccessControlContext acc = prepareACC(
				new DevicePermission("*", DevicePermission.REMOVE));
		Collection<ServiceReference<Device>> deviceSRefs = getDeviceSRefs(null);
		boolean isRemoved = false;
		for (ServiceReference<Device> deviceSRef : deviceSRefs) {
			final Device device = super.getContext().getService(deviceSRef);
			if (null == device) {
				continue;
			}
			try {
				AccessController
						.doPrivileged((PrivilegedExceptionAction<Void>) () -> {
							device.remove();
							return null;
						}, acc);
				assertNull("The device service is not unregistered.",
						super.getContext().getService(deviceSRef));
				isRemoved = true;
				break;
			} catch (UnsupportedOperationException upe) {
				// expected
			}
		}
		assertTrue("No device with remove support.", isRemoved);
	}

	/**
	 * Tests the security protection of the device remove operation. The test
	 * method requires at least one device with remove support.
	 * 
	 * @throws PrivilegedActionException If an unexpected error is available
	 *         while removing the device.
	 */
	public void testRemoveDeviceDeny() throws PrivilegedActionException {
		this.testStepProxy.execute(
				SecureDeviceTestSteps.STEP_ID_AVAILABLE_DEVICE,
				SecureDeviceTestSteps.STEP_MESSAGE_AVAILABLE_DEVICE);
		AccessControlContext acc = prepareACC(null);
		Collection<ServiceReference<Device>> deviceSRefs = getDeviceSRefs(null);
		boolean securityCheck = false;
		for (ServiceReference<Device> deviceSRef : deviceSRefs) {
			final Device device = super.getContext().getService(deviceSRef);
			if (null == device) {
				continue;
			}
			try {
				AccessController
						.doPrivileged((PrivilegedExceptionAction<Void>) () -> {
							device.remove();
							fail("The device remove method is not protected with security check.");
							return null;
				}, acc);
			} catch (UnsupportedOperationException upe) {
				// expected
			} catch (SecurityException se) {
				securityCheck = true;
			}
		}
		assertTrue("At least one device remvoe method must be protected with security check.", securityCheck);
	}

	private static AccessControlContext prepareACC(Permission permission) {
		PermissionCollection permissionCollection = new Permissions();
		if (null != permission) {
			permissionCollection.add(permission);
		}
		return new AccessControlContext(new ProtectionDomain[] {
				new ProtectionDomain(null, permissionCollection)
		});
	}

	private Collection<ServiceReference<Device>> getDeviceSRefs(String filter) {
		try {
			Collection<ServiceReference<Device>> deviceSRefs = super.getContext()
					.getServiceReferences(Device.class, filter);
			assertThat(deviceSRefs).as("No device services; filter=%s.", filter)
					.isNotEmpty();
			return deviceSRefs;
		} catch (InvalidSyntaxException e) {
			// null is a valid filter
			return null;
		}
	}
}
