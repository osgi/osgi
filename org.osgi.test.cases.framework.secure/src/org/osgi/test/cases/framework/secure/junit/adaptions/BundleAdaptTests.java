/*
 * Copyright (c) IBM Corporation (2011). All Rights Reserved.
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
package org.osgi.test.cases.framework.secure.junit.adaptions;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.test.cases.framework.secure.junit.adaptions.export.AdaptTestService;
import org.osgi.test.support.OSGiTestCase;

import junit.framework.AssertionFailedError;

/**
 * Basic Bundle Wiring API security tests.
 */
public class BundleAdaptTests extends OSGiTestCase {
	private static final String WITH_PERMS = "adaptAllPerms.jar";
	private static final String WITHOUTPERMS = "adaptMissingPerms.jar";

	private Bundle withPerms;
	private Bundle withoutPerms;

	/**
	 * Bundle.adapt(AccessControlContext.class) must not throw a security exception if 
	 * the caller has AdaptPermission[AccessControlContext,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptAccessControlContextWithPermission() throws Exception {
		doAdaptTest(AccessControlContext.class, false, true);
	}
	
	/**
	 * Bundle.adapt(BundleRevision.class) must throw a security exception if the 
	 * caller does not have AdaptPermission[BundleRevision,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptAccessControlContextWithoutPermission() throws Exception {
		doAdaptTest(AccessControlContext.class, false, false);
	}

	public void testAccessControlContextPermissionCheck() {
		final Bundle b = getContext().getBundle();
		AccessControlContext accNoContextPerm = withoutPerms.adapt(AccessControlContext.class);
		try {
			AccessController.doPrivileged(new PrivilegedAction<BundleContext>() {
				public BundleContext run() {
					return b.getBundleContext();
				}
			}, accNoContextPerm);
			fail("Expected security exception.");
		} catch (SecurityException e) {
			// expected
		}

		AccessControlContext accWithContextPerm = withPerms.adapt(AccessControlContext.class);
		try {
			AccessController.doPrivileged(new PrivilegedAction<BundleContext>() {
				public BundleContext run() {
					return b.getBundleContext();
				}
			}, accWithContextPerm);
		} catch (SecurityException e) {
			fail("Unexpected security exception", e);
		}
	}

	/**
	 * Bundle.adapt(BundleContext.class) must not throw a security exception if 
	 * the caller has AdaptPermission[BundleContext,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleContextWithPermission() throws Exception {
		doAdaptTest(BundleContext.class, false, true);
	}
	
	/**
	 * Bundle.adapt(BundleRevision.class) must throw a security exception if the 
	 * caller does not have AdaptPermission[BundleRevision,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleContextWithoutPermission() throws Exception {
		doAdaptTest(BundleContext.class, false, false);
	}

	/**
	 * Bundle.adapt(BundleRevision.class) must not throw a security exception if 
	 * the caller has AdaptPermission[BundleRevision,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleRevisionWithPermission() throws Exception {
		doAdaptTest(BundleRevision.class, false, true);
	}
	
	/**
	 * Bundle.adapt(BundleRevision.class) must throw a security exception if the 
	 * caller does not have AdaptPermission[BundleRevision,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleRevisionWithoutPermission() throws Exception {
		doAdaptTest(BundleRevision.class, false, false);
	}
	
	/**
	 * Bundle.adapt(BundleRevisions.class) must not throw a security exception
	 * if the caller has AdaptPermission[BundleRevisions,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleRevisionsWithPermission() throws Exception {
		doAdaptTest(BundleRevisions.class, false, true);
	}
	
	/**
	 * Bundle.adapt(BundleRevisions.class) must throw a security exception if
	 * the caller does not have AdaptPermission[BundleRevisions,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleRevisionsWithoutPermission() throws Exception {
		doAdaptTest(BundleRevisions.class, false, false);
	}

	/**
	 * Bundle.adapt(BundleStartLevel.class) must not throw a security exception
	 * if the caller has AdaptPermission[BundleStartLevel,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleStartLevelWithPermission() throws Exception {
		doAdaptTest(BundleStartLevel.class, false, true);
	}
	
	/**
	 * Bundle.adapt(BundleStartLevel.class) must throw a security exception if
	 * the caller does not have AdaptPermission[BundleStartLevel,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleStartLevelWithoutPermission() throws Exception {
		doAdaptTest(BundleStartLevel.class, false, false);
	}

	/**
	 * Bundle.adapt(BundleWiring.class) must not throw a security exception if 
	 * the caller has AdaptPermission[BundleWiring,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleWiringWithPermission() throws Exception {
		doAdaptTest(BundleWiring.class, false, true);
	}
	
	/**
	 * Bundle.adapt(BundleWiring.class) must throw a security exception if the 
	 * caller does not have AdaptPermission[BundleWiring,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleWiringWithoutPermission() throws Exception {
		doAdaptTest(BundleWiring.class, false, false);
	}

	/**
	 * Bundle.adapt(FrameworkWiring.class) must not throw a security exception
	 * if the caller has AdaptPermission[Framework,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptFrameworkWithPermission() throws Exception {
		doAdaptTest(Framework.class, true, true);
	}
	
	/**
	 * Bundle.adapt(FrameworkWiring.class) must throw a security exception if
	 * the caller does not have AdaptPermission[Framework,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptFrameworkWithoutPermission() throws Exception {
		doAdaptTest(Framework.class, true, false);
	}

	/**
	 * Bundle.adapt(FrameworkWiring.class) must not throw a security exception
	 * if the caller has AdaptPermission[FrameworkWiring,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptFrameworkWiringWithPermission() throws Exception {
		doAdaptTest(FrameworkWiring.class, true, true);
	}
	
	/**
	 * Bundle.adapt(FrameworkWiring.class) must throw a security exception if
	 * the caller does not have AdaptPermission[FrameworkWiring,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptFrameworkWiringWithoutPermission() throws Exception {
		doAdaptTest(FrameworkWiring.class, true, false);
	}

	/**
	 * Bundle.adapt(FrameworkStartLevel.class) must not throw a security exception
	 * if the caller has AdaptPermission[FrameworkStartLevel,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptFrameworkStartLevelWithPermission() throws Exception {
		doAdaptTest(FrameworkStartLevel.class, true, true);
	}
	
	/**
	 * Bundle.adapt(FrameworkStartLevel.class) must throw a security exception if
	 * the caller does not have AdaptPermission[FrameworkStartLevel,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptFrameworkStartLevelWithoutPermission() throws Exception {
		doAdaptTest(FrameworkStartLevel.class, true, false);
	}

	/**
	 * FrameworkWiring.refreshBundles(Collection<Bundle>, FrameWorkListener...)
	 * must not throw a security exception if the caller has 
	 * AdminPermission[System Bundle,RESOLVE].
	 * @throws Exception
	 */
	public void testFrameworkWiringRefreshBundlesWithPermission() throws Exception {
		execute("wiring.tb2.jar");
	}
	
	/**
	 * FrameworkWiring.refreshBundles(Collection<Bundle>, FrameWorkListener...)
	 * must throw a security exception if the caller does not have
	 * AdminPermission[System Bundle,RESOLVE].
	 * @throws Exception
	 */
	public void testFrameworkWiringRefreshBundlesWithoutPermission() throws Exception {
		execute("wiring.tb1.jar");
	}
	
	/**
	 * FrameworkWiring.resolveBundles(Collection<Bundle>) must not throw a 
	 * security exception if the caller has 
	 * AdminPermission[System Bundle,RESOLVE].
	 * @throws Exception
	 */
	public void testFrameworkWiringResolveBundlesWithPermission() throws Exception {
		execute("wiring.tb3.jar");
	}
	
	/**
	 * FrameworkWiring.resolveBundles(Collection<Bundle>) must throw a security
	 * exception if the caller does not have 
	 * AdminPermission[System Bundle,RESOLVE].
	 * @throws Exception
	 */
	public void testFrameworkWiringResolveBundlesWithoutPermission() throws Exception {
		execute("wiring.tb4.jar");
	}
	
	private void execute(String name) throws Exception {
		Bundle b = null;
		try {
			b = install(name);
			b.start();
		}
		catch (BundleException e) {
			Throwable cause = e.getCause();
			if (cause instanceof AssertionFailedError)
				throw (AssertionFailedError)cause;
			throw e;
		}
		finally {
			if (b != null) {
				try {
					b.uninstall();
				}
				catch (Exception e) {}
			}
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		withPerms = install(WITH_PERMS);
		withoutPerms = install(WITHOUTPERMS);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		withPerms.uninstall();
		withoutPerms.uninstall();
	}

	private <T> void doAdaptTest(Class<T> adaptTo, boolean fromSystemBundle, boolean hasPermission) throws BundleException, IOException {
		Bundle testBundle = hasPermission ? withPerms : withoutPerms;
		Bundle adaptBundle = fromSystemBundle ? getContext().getBundle(0) : getContext().getBundle();
		AdaptTestService<T> service = new AdaptTestService<T>(adaptTo, hasPermission, adaptBundle);
		ServiceRegistration< ? > reg = getContext()
				.registerService(AdaptTestService.class, service, null);
		try {
			testBundle.start();
			AssertionFailedError error = service.getError();
			if (error != null)
				throw error;
		} finally {
			reg.unregister();
		}
	}
}
