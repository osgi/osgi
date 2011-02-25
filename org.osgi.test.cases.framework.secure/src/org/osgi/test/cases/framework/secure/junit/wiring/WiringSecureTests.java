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
package org.osgi.test.cases.framework.secure.junit.wiring;

import junit.framework.AssertionFailedError;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.test.support.OSGiTestCase;

/**
 * Basic Bundle Wiring API security tests.
 */
public class WiringSecureTests extends OSGiTestCase {
	/**
	 * Bundle.adapt(BundleRevision.class) must not throw a security exception if 
	 * the caller has AdaptPermission[BundleRevision,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleRevisionWithPermission() throws Exception {
		execute("wiring.tb7.jar");
	}
	
	/**
	 * Bundle.adapt(BundleRevision.class) must throw a security exception if the 
	 * caller does not have AdaptPermission[BundleRevision,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleRevisionWithoutPermission() throws Exception {
		execute("wiring.tb8.jar");
	}
	
	/**
	 * Bundle.adapt(BundleRevisions.class) must not throw a security exception
	 * if the caller has AdaptPermission[BundleRevisions,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleRevisionsWithPermission() throws Exception {
		execute("wiring.tb9.jar");
	}
	
	/**
	 * Bundle.adapt(BundleRevisions.class) must throw a security exception if
	 * the caller does not have AdaptPermission[BundleRevisions,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleRevisionsWithoutPermission() throws Exception {
		execute("wiring.tb10.jar");
	}
	
	/**
	 * Bundle.adapt(BundleWiring.class) must not throw a security exception if 
	 * the caller has AdaptPermission[BundleWiring,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleWiringWithPermission() throws Exception {
		execute("wiring.tb5.jar");
	}
	
	/**
	 * Bundle.adapt(BundleWiring.class) must throw a security exception if the 
	 * caller does not have AdaptPermission[BundleWiring,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptBundleWiringWithoutPermission() throws Exception {
		execute("wiring.tb6.jar");
	}
	
	/**
	 * Bundle.adapt(FrameworkWiring.class) must not throw a security exception
	 * if the caller has AdaptPermission[FrameworkWiring,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptFrameworkWiringWithPermission() throws Exception {
		execute("wiring.tb11.jar");
	}
	
	/**
	 * Bundle.adapt(FrameworkWiring.class) must throw a security exception if
	 * the caller does not have AdaptPermission[FrameworkWiring,ADAPT].
	 * @throws Exception
	 */
	public void testAdaptFrameworkWiringWithoutPermission() throws Exception {
		execute("wiring.tb12.jar");
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
}
