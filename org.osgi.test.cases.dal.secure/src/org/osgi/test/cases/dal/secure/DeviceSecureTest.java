/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal.secure;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.DevicePermission;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.step.TestStepProxy;

/**
 * Test class validates the device operations and properties.
 */
public class DeviceSecureTest extends DefaultTestBundleControl {

	private TestStepProxy	testStepProxy;

	protected void setUp() throws Exception { // NOPMD
		super.setUp();
		this.testStepProxy = new TestStepProxy(super.getContext());
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
		ServiceReference[] deviceSRefs = getDeviceSRefs();
		boolean isRemoved = false;
		for (int i = 0; i < deviceSRefs.length; i++) {
			final Device device = (Device) super.getContext().getService(deviceSRefs[i]);
			if (null == device) {
				continue;
			}
			try {
				AccessController.doPrivileged(new PrivilegedExceptionAction() {
					public Object run() throws DeviceException {
						device.remove();
						return null;
					}
				}, acc);
				assertNull("The device service is not unregistered.", super.getContext().getService(deviceSRefs[i]));
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
		ServiceReference[] deviceSRefs = getDeviceSRefs();
		boolean securityCheck = false;
		for (int i = 0; i < deviceSRefs.length; i++) {
			final Device device = (Device) super.getContext().getService(deviceSRefs[i]);
			if (null == device) {
				continue;
			}
			try {
				AccessController.doPrivileged(new PrivilegedExceptionAction() {
					public Object run() throws DeviceException {
						device.remove();
						fail("The device remove method is not protected with security check.");
						return null;
					}
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

	private ServiceReference[] getDeviceSRefs() {
		try {
			ServiceReference[] deviceSRefs = super.getContext().getServiceReferences(Device.class.getName(), null);
			assertNotNull("Device validation needs at least one device service.", deviceSRefs);
			return deviceSRefs;
		} catch (InvalidSyntaxException e) {
			// null is a valid filter
			return null;
		}
	}
}
