/*
 * Copyright (c) OSGi Alliance (2010, 2016). All Rights Reserved.
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

package org.osgi.test.cases.framework.secure.junit.hooks.weaving;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.weaving.WovenClassListener;
import org.osgi.test.cases.framework.secure.junit.hooks.weaving.export.TestConstants;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.wiring.Wiring;

public class WeavingHookTests extends OSGiTestCase {
	private final List<Bundle> bundles = new ArrayList<Bundle>();
	private final List<ServiceRegistration<?>> registrations = new ArrayList<ServiceRegistration<?>>();
	
	private TestWovenClassListener listener;
	
	public Bundle install(String bundle) {
		Bundle result = null;
		try {
			result = super.install(bundle);
		} catch (BundleException e) {
			fail("failed to install bundle: " + bundle, e);
		} catch (IOException e) {
			fail("failed to install bundle: " + bundle, e);
		}
		if (bundle == null)
			fail("Failed to install bundle: " + bundle);
		if (!bundles.contains(result))
			bundles.add(result);
		return result;
	}

	protected void setUp() throws Exception {
		bundles.clear();
		resetSystemProperties();
		setUpRegistrations();
		setUpBundles();
	}
	
	private void setUpBundles() throws BundleException {
		Bundle b = install("weaving.tb.woven.jar");
		bundles.add(b);
		b.start();
	}
	
	private void setUpRegistrations() {
		listener = new TestWovenClassListener();
		registrations.add(getContext().registerService(WovenClassListener.class, listener, null));
	}

	protected void tearDown() throws Exception {
		tearDownBundles();
		tearDownRegistrations();
		Wiring.synchronousRefreshBundles(getContext(), bundles);
		bundles.clear();
	}
	
	private void tearDownBundles() {
		for (Iterator<Bundle> iBundles = bundles.iterator(); iBundles.hasNext();) {
			Bundle bundle = iBundles.next();
			try {
				if (!(bundle.getState() == Bundle.UNINSTALLED))
					bundle.uninstall();
			} catch (BundleException e) {
				// nothing
			}
		}
	}
	
	private void tearDownRegistrations() {
		for (ServiceRegistration<?> registration : registrations) {
			try {
				registration.unregister();
			}
			catch (Exception e) {}
		}
	}

	/*
	 * Test that a security exception is thrown from all affected methods when
	 * neither AdminPermission.WEAVE nor PackagePermission.IMPORT is granted.
	 *  
	 * Also tests that the WovenClass and woven bundle are in the expected 
	 * states in TRANSFORMED and DEFINED.
	 */
	public void testNoPermissions() {
		System.setProperty(TestConstants.PROP_EXPECT_SECURITYEXCEPTION_ALL, Boolean.TRUE.toString());
		doTest("weaving.tb1.jar");
	}
	
	/*
	 * Test that a security exception is not thrown from all affected methods 
	 * when both AdminPermission.WEAVE and PackagePermission.IMPORT is granted.
	 *  
	 * Also tests that the WovenClass and woven bundle are in the expected 
	 * states in TRANSFORMED and DEFINED.
	 */
	public void testAllPermissions() {
		doTest("weaving.tb4.jar");
	}
	
	/*
	 * Test that a security exception is not thrown from all affected methods 
	 * when both AdminPermission.WEAVE and PackagePermission.IMPORT is granted.
	 *  
	 * Also tests that the WovenClass and woven bundle are in the expected 
	 * states in TRANSFORMED and DEFINE_FAILED.
	 */
	public void testAllPermissionsInDefineFailed() {
		System.setProperty(TestConstants.PROP_INVALID_SETBYTES, Boolean.TRUE.toString());
		doTest("weaving.tb4.jar");
	}
	
	/*
	 * Test that a security exception is thrown from all affected methods when
	 * AdminPermission.WEAVE is not granted but PackagePermission.IMPORT is
	 * granted.
	 * 
	 * Also tests that the WovenClass and woven bundle are in the expected 
	 * states in TRANSFORMED and DEFINED.
	 */
	public void testNoWeavingPermission() {
		System.setProperty(TestConstants.PROP_EXPECT_SECURITYEXCEPTION_ALL, Boolean.TRUE.toString());
		doTest("weaving.tb2.jar");
	}
	
	/*
	 * Test that a security exception is thrown from all affected methods when
	 * PackagePermission.IMPORT is not granted but AdminPermission.WEAVE is
	 * granted.
	 * 
	 * The SecurityException thrown by WovenClass.getDynamicImports().add(pkg)
	 * is not rethrown by the weaver.
	 * 
	 * Also tests that the WovenClass and woven bundle are in the expected 
	 * states in TRANSFORMED and DEFINED.
	 */
	public void testNoPackagePermission() {
		System.setProperty(TestConstants.PROP_EXPECT_SECURITYEXCEPTION_ADDDYNAMICIMPORT, Boolean.TRUE.toString());
		doTest("weaving.tb3.jar");
	}
	
	/*
	 * Test that a security exception is thrown from all affected methods when
	 * PackagePermission.IMPORT is not granted but AdminPermission.WEAVE is
	 * granted.
	 * 
	 * The SecurityException thrown by WovenClass.getDynamicImports().add(pkg)
	 * is rethrown by the weaver.
	 *  
	 * Also tests that the WovenClass and woven bundle are in the expected 
	 * states in TRANSFORMING_FAILED.
	 */
	public void testNoPackagePermissionRethrowSecurityException() {
		System.setProperty(TestConstants.PROP_EXPECT_SECURITYEXCEPTION_ADDDYNAMICIMPORT, Boolean.TRUE.toString());
		System.setProperty(TestConstants.PROP_RETHROW_SECURITYEXCEPTION, Boolean.TRUE.toString());
		doTest("weaving.tb3.jar");
	}
	
	private void resetSystemProperties() {
		System.setProperty(TestConstants.PROP_EXPECT_SECURITYEXCEPTION_ALL, Boolean.FALSE.toString());
		System.setProperty(TestConstants.PROP_EXPECT_SECURITYEXCEPTION_ADDDYNAMICIMPORT, Boolean.FALSE.toString());
		System.setProperty(TestConstants.PROP_RETHROW_SECURITYEXCEPTION, Boolean.FALSE.toString());
		System.setProperty(TestConstants.PROP_INVALID_SETBYTES, Boolean.FALSE.toString());
	}
	
	private void uninstallQuietly(Bundle bundle) {
		if (bundle == null)
			return;
		try {
			bundle.uninstall();
		}
		catch (Exception e) {}
	}
	
	private void assertWovenClassListener() {
		TestCase.assertTrue("Failed to call woven class listener", listener.isCalled());
		if (listener.getError() != null)
			throw listener.getError();
	}
	
	private void doTest(String bundle) {
		Bundle tb = install(bundle);
		try {
			tb.start();
		} catch (BundleException e) {
			if (e.getCause() instanceof AssertionError)
				throw (AssertionError) e.getCause();
			fail("Failed to start test bundle", e);
		} finally {
			uninstallQuietly(tb);
		}
		assertWovenClassListener();
	}
}
