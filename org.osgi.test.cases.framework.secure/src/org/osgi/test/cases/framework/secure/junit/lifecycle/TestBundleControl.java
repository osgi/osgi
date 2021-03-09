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
package org.osgi.test.cases.framework.secure.junit.lifecycle;

import java.security.AccessControlContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServicePermission;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.test.cases.framework.secure.lifecycle.servicereferencegetter.ServiceReferenceGetter;
import org.osgi.test.cases.framework.secure.lifecycle.servicereferencegetter.TestResult;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 *
 *
 * The testPermissions test is currently removed because of unknown state. It
 * uses tb10 but this seems to be removed from the upper makefile.
 *
 * @author Ericsson Radio Systems AB
 */
public class TestBundleControl extends DefaultTestBundleControl {

	/**
	 * Test the permissions in the framework
	 */
	public void testPermissions() throws Throwable {
		Bundle tb;
		Bundle tbPerm;
		tb = installBundle("lifecycle.tb5.jar", false);
		tbPerm = installBundle("lifecycle.tb10.jar", false);
		try {
			tb.start();
			tbPerm.start();
			ServiceReferenceGetter serviceReferenceGetter = getService(
					ServiceReferenceGetter.class);
			serviceReferenceGetter.setServiceReference(getContext()
					.getServiceReference(PermissionAdmin.class.getName()));
			Throwable result = ((TestResult) serviceReferenceGetter).get();
			if (result != null)
				throw result;
		}
		finally {
			tbPerm.uninstall();
			if ((tb.getState() & Bundle.UNINSTALLED) == 0) {
				tb.uninstall();
			}
		}
	}

	public void testHasPermission() throws Throwable {
		Bundle tb = installBundle("lifecycle.tb5.jar", false);
		try {
			assertTrue(
					"missing permission",
					tb.hasPermission(new ServicePermission(
							"org.osgi.test.cases.framework.secure.lifecycle.tb5.EventTest",
							ServicePermission.REGISTER)));
			assertFalse(
					"has extra permission",
					tb.hasPermission(new ServicePermission(
							"org.osgi.test.cases.framework.secure.lifecycle.tb5.EventTest",
							ServicePermission.GET)));
		}
		finally {
			tb.uninstall();
		}
	}

	public void testAccessControlContext() throws Throwable {
		Bundle tb = installBundle("lifecycle.tb5.jar", false);
		try {
			AccessControlContext acc = tb.adapt(AccessControlContext.class);
			assertNotNull("no adapt to accesscontrolcontext", acc);
			try {
				acc.checkPermission(new ServicePermission(
						"org.osgi.test.cases.framework.secure.lifecycle.tb5.EventTest",
						ServicePermission.REGISTER));
			}
			catch (SecurityException se) {
				fail("unexpected exception", se);
			}
			try {
				acc.checkPermission(new ServicePermission(
						"org.osgi.test.cases.framework.secure.lifecycle.tb5.EventTest",
						ServicePermission.GET));
				fail("expected security exception");
			}
			catch (SecurityException se) {
				// expected a security exception
			}
		}
		finally {
			tb.uninstall();
		}
	}
}
