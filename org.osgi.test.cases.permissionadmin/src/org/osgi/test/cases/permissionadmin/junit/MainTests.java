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
package org.osgi.test.cases.permissionadmin.junit;

import java.io.FilePermission;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.permissionadmin.service.ContextSharer;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class MainTests extends DefaultTestBundleControl {

	protected void setUp() throws Exception {
		assertNotNull("Must have a security manager", System
				.getSecurityManager());
	}

	protected void tearDown() {
		ungetAllServices();
	}

	/** *** Test methods **** */
	public void testBasicPermissionInfo() throws Exception {
		/* Construct a permissioninfo from a permission class */
		PermissionInfo permInfo = new PermissionInfo(FilePermission.class
				.getName(), "<<ALL FILES>>", "read");
		assertEquals("Actions", "read", permInfo.getActions());
		assertEquals("Name", "<<ALL FILES>>", permInfo.getName());
		assertEquals("Type", "java.io.FilePermission", permInfo.getType());
		/* Check the output of getEncoded() */
		assertEquals("getEncoded",
				"(java.io.FilePermission \"<<ALL FILES>>\" \"read\")", permInfo
						.getEncoded());
		/* toString() should be identical with getEncoded() */
		assertEquals("toString", permInfo.getEncoded(), permInfo.toString());
	}

	public void testEqual() throws Exception {
		/* Construct an identical permissionInfo and check equal */
		PermissionInfo permInfo = new PermissionInfo(FilePermission.class
				.getName(), "<<ALL FILES>>", "read");
		PermissionInfo identicalPermInfo = new PermissionInfo(
				FilePermission.class.getName(), "<<ALL FILES>>", "read");
		assertEquals("identical", permInfo, identicalPermInfo);
		assertEquals("identical hashcodes", permInfo.hashCode(),
				identicalPermInfo.hashCode());
		/* Construct another permissionInfo, not identical and check equal */
		PermissionInfo unIdenticalPermInfo = new PermissionInfo(
				FilePermission.class.getName(), "<<ALL FILES>>", "write");
		/*
		 * QUESTION: Is this test relevant? Should there be an assert for
		 * notEqual?
		 */
		assertTrue("not identical", !permInfo.equals(unIdenticalPermInfo));
	}

	public void testEncodedPermission() throws Exception {
		/*
		 * Check if permissioninfos created from encoded are identical to the
		 * original
		 */
		PermissionInfo permInfo = new PermissionInfo(FilePermission.class
				.getName(), "<<ALL FILES>>", "read");
		String stringEncodedPermInfo = permInfo.getEncoded();
		PermissionInfo stringPermInfo = new PermissionInfo(
				stringEncodedPermInfo);
		/* Construct a normal permission info */
		PermissionInfo identicalPermInfo = new PermissionInfo(
				FilePermission.class.getName(), "<<ALL FILES>>", "read");
		assertEquals("constructed from a string", stringPermInfo,
				identicalPermInfo);
		assertEquals("toString", stringEncodedPermInfo, permInfo.toString());
	}

	/**
	 * Tests various illegal ways to construct PermissionInfo objects with an
	 * encoded string. WARNING! Many of these tests checks things that are not
	 * well defined in the current spec!
	 */
	public void testExceptionalPermissionInfo() throws Exception {
		/* !!! This is not explicitly specified in the spec */
		createBadPermissionInfo("null argument", null,
				NullPointerException.class);
		createBadPermissionInfo("Empty argument", "",
				IllegalArgumentException.class);
		createBadPermissionInfo("Only parenthesis", "()",
				IllegalArgumentException.class);
		createBadPermissionInfo("Missing open parenthesis",
				"java.io.FilePermission \"<<ALL FILES>>\" \"read\")",
				IllegalArgumentException.class);
		createBadPermissionInfo("Missing closing parentheses",
				"(java.io.FilePermission \"<<ALL FILES>>\" \"read\"",
				IllegalArgumentException.class);
		createBadPermissionInfo("Quotes on type",
				"\"java.io.FilePermission\" \"<<ALL FILES>>\" \"read\")",
				IllegalArgumentException.class);
		createBadPermissionInfo("Open quote on type",
				"\"java.io.FilePermission \"<<ALL FILES>>\" \"read\")",
				IllegalArgumentException.class);
		createBadPermissionInfo("Closing quote on type",
				"java.io.FilePermission\" \"<<ALL FILES>>\" \"read\")",
				IllegalArgumentException.class);
		createBadPermissionInfo("Missing open name quote",
				"(java.io.FilePermission <<ALL FILES>>\" \"read\")",
				IllegalArgumentException.class);
		createBadPermissionInfo("Missing close name quote",
				"(java.io.FilePermission \"<<ALL FILES>> \"read\")",
				IllegalArgumentException.class);
		createBadPermissionInfo("Missing open action quote",
				"(java.io.FilePermission \"<<ALL FILES>>\" read\")",
				IllegalArgumentException.class);
		createBadPermissionInfo("Missing close action quote",
				"(java.io.FilePermission \"<<ALL FILES>>\" \"read)",
				IllegalArgumentException.class);
		createBadPermissionInfo(
				"Too many arguments",
				"(java.io.FilePermission \"<<ALL FILES>>\" \"read\" \"write\")",
				IllegalArgumentException.class);
		createBadPermissionInfo("Comma separation between name and action",
				"(java.io.FilePermission \"<<ALL FILES>>\", \"read\")",
				IllegalArgumentException.class);
	}

	/**
	 * Tests various variations of the arguments when constructing
	 * PermissionInfo objects. WARNING! Many of these tests checks things that
	 * are not well defined in the current spec!
	 */
	public void testPermissionInfo() throws Exception {
		PermissionInfo info = null;
		/* Test constructor with null type */
		createBadPermissionInfo("null arguments", null, null, null,
				NullPointerException.class);
		createBadPermissionInfo("null arguments", null, "<<ALL FILES>>", null,
				NullPointerException.class);
		createBadPermissionInfo("null arguments", null, "<<ALL FILES>>",
				"read", NullPointerException.class);
		createBadPermissionInfo("null arguments", null, null, "read",
				NullPointerException.class);
		/* Test constructor with null name and actions arguments */
		info = new PermissionInfo("java.io.FilePermission", null, null);
		assertEquals("type", "java.io.FilePermission", info.getType());
		assertNull("no name", info.getName());
		assertNull("no actions", info.getActions());
		assertEquals("encoded", "(java.io.FilePermission)", info.getEncoded());
		assertEquals("toString", "(java.io.FilePermission)", info.toString());
		pass("hashcode " + info.hashCode());
		/* Test constructor with null actions argument */
		info = new PermissionInfo("java.io.FilePermission", "<<ALL FILES>>",
				null);
		assertEquals("type", "java.io.FilePermission", info.getType());
		assertEquals("name", "<<ALL FILES>>", info.getName());
		assertNull("no actions", info.getActions());
		assertEquals("encoded", "(java.io.FilePermission \"<<ALL FILES>>\")",
				info.getEncoded());
		assertEquals("toString", "(java.io.FilePermission \"<<ALL FILES>>\")",
				info.toString());
		pass("hashcode " + info.hashCode());
		/* Test constructor with missing name argument */
		createBadPermissionInfo("missing name", "java.io.FilePermission", null,
				"read", IllegalArgumentException.class);
		/* Test constructor with no null arguments */
		info = new PermissionInfo("java.io.FilePermission", "<<ALL FILES>>",
				"read");
		assertEquals("type", "java.io.FilePermission", info.getType());
		assertEquals("name", "<<ALL FILES>>", info.getName());
		assertEquals("actions", "read", info.getActions());
		assertEquals("encoded",
				"(java.io.FilePermission \"<<ALL FILES>>\" \"read\")", info
						.getEncoded());
		assertEquals("toString",
				"(java.io.FilePermission \"<<ALL FILES>>\" \"read\")", info
						.toString());
		pass("hashcode " + info.hashCode());
	}

	/**
	 * Makes sure that Bundle 0 (system bundle) is the one that registered the
	 * PermissionAdmin service.
	 */
	public void testBundleZero() throws Exception {
		ServiceReference<PermissionAdmin> serviceRef = getContext()
				.getServiceReference(PermissionAdmin.class);
		Bundle bundle = serviceRef.getBundle();
		assertEquals("Bundle that registered PermissionAdmin", 0L, bundle
				.getBundleId());
	}

	/**
	 * thereCanBeonlyOne. Test that there is only one permission admin
	 */
	public void testThereCanBeOnlyOne() throws Exception {
		/* Get the references to all permissions admins */
		Collection<ServiceReference<PermissionAdmin>> serviceRefs = getContext()
				.getServiceReferences(PermissionAdmin.class, null);
		assertEquals("only one PermissionAdmin", 1, serviceRefs.size());
	}

	/**	
	 */
	public void testImplicitPermissions() throws Throwable {
		PermissionInfo[] invokePerm = {new PermissionInfo(
				"java.security.AllPermission", "", "")};
		invokeWithPermissions("implicitPermissionsTest", invokePerm, false);
		invokePerm = new PermissionInfo[0];
		invokeWithPermissions("implicitPermissionsTest", invokePerm, true);
	}

	public void testNoPermissions() throws Throwable {
		Bundle tb4 = installBundle("tb4.jar", false);
		try {
			tb4.start();
		}
		catch (BundleException e) {
			fail("Unexpected Exception", e.getCause());
		}
		finally {
			tb4.uninstall();
		}
	}

	/* called by testImplicitPermissions */
	public void implicitPermissionsTest() throws Exception {
		getContext().getBundle().getBundleContext().getDataFile("nicke.txt");
	}

	private void invokeWithPermissions(String methodName,
			PermissionInfo[] perms, boolean fail) throws Throwable {
		/* Install and start the context sharing bundle */
		Bundle contextBundle = installBundle("contextsharer.jar", true);
		try {
			String contextBundleLocation = contextBundle.getLocation();

			/* Get the context sharing service */
			ServiceReference<ContextSharer> sr = getContext()
					.getServiceReference(ContextSharer.class);
			ContextSharer cs = getContext().getService(sr);

			/* Get the PermissionAdmin service */
			ServiceReference<PermissionAdmin> paRef = getContext()
					.getServiceReference(PermissionAdmin.class);
			PermissionAdmin pa = getContext().getService(
					paRef);

			/* Set the permissions for the context sharing bundle */
			pa.setPermissions(contextBundleLocation, perms);

			/*
			 * Make the context sharer invoke the specified method on this
			 * object and thereby calling the method with its permissions
			 */
			Method m = this.getClass().getDeclaredMethod(methodName,
					new Class[0]);
			try {
				cs.invoke(this, m);
				if (fail) {
					fail("Expecting a security exception");
				}
			}
			catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof SecurityException) {
					if (!fail) {
						fail("Unexpected security exception", e
								.getTargetException());
					}
				}
				else {
					throw e.getTargetException();
				}
			}
			/* Delete the permissions for the context sharing bundle */
			pa.setPermissions(contextBundleLocation, null);

		}
		finally {
			/* Uninstall the context sharing bundle */
			contextBundle.uninstall();
		}
	}

	/** *** Helper methods **** */
	/**
	 * Creates a PermissionInfo with a bad encoded string and checks that it
	 * throws a correct exception.
	 */
	private void createBadPermissionInfo(String message, String encoded,
			Class< ? extends Throwable> exceptionClass) throws Exception {
		try {
			new PermissionInfo(encoded);
			fail(message + " did not result in a " + exceptionClass.getName());
		}
		catch (Exception e) {
			assertException(message, exceptionClass, e);
		}
	}

	/**
	 * Creates a PermissionInfo with a bad encoded string and checks that it
	 * throws a correct exception.
	 */
	private void createBadPermissionInfo(String message, String type,
			String name, String actions,
			Class< ? extends Throwable> exceptionClass) throws Exception {
		try {
			new PermissionInfo(type, name, actions);
			fail(message + " did not result in a " + exceptionClass.getName());
		}
		catch (Exception e) {
			assertException(message, exceptionClass, e);
		}
	}

}
