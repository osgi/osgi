/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
package org.osgi.test.cases.composite.junit;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.composite.CompositeBundle;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.support.OSGiTestCase;

public class CompositeResolutionTests extends AbstractCompositeTestCase {

	public void testSingletons01() {
		// Test singletons in the same composite
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
		Bundle tb2v1 = installConstituent(composite, "tb2v1", "tb2v1.jar");
		Bundle tb2v2 = installConstituent(composite, "tb2v2", "tb2v2.jar");
		PackageAdmin pa = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
		pa.resolveBundles(new Bundle[] {tb2v1, tb2v2});
		assertTrue("Resolution is incorrect.", tb2v1.getState() == Bundle.RESOLVED ^ tb2v2.getState() == Bundle.RESOLVED);
		uninstallCompositeBundle(composite);
	}

	public void testSingletons02() {
		// Test singletons in isolated composites
		CompositeBundle composite1 = createCompositeBundle(compAdmin, getName() + ".A", null, null);
		CompositeBundle composite2 = createCompositeBundle(compAdmin, getName() + ".B", null, null);
		Bundle tb2v1 = installConstituent(composite1, "tb2v1", "tb2v1.jar");
		Bundle tb2v2 = installConstituent(composite2, "tb2v2", "tb2v2.jar");
		PackageAdmin pa1 = (PackageAdmin) getService(composite1.getSystemBundleContext(), PackageAdmin.class.getName());
		PackageAdmin pa2 = (PackageAdmin) getService(composite2.getSystemBundleContext(), PackageAdmin.class.getName());
		pa1.resolveBundles(new Bundle[] {tb2v1});
		pa2.resolveBundles(new Bundle[] {tb2v2});
		assertTrue("Resolution is incorrect: " + tb2v1.getVersion(), tb2v1.getState() == Bundle.RESOLVED);
		assertTrue("Resolution is incorrect: " + tb2v2.getVersion(), tb2v2.getState() == Bundle.RESOLVED);
		uninstallCompositeBundle(composite1);
		uninstallCompositeBundle(composite2);
	}

	public void testSingletons03() throws BundleException, IOException {
		// Test singletons in nested composites which does not import a bundle with same BSN from the parent
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
		Bundle tb2v2 = installConstituent(composite, "tb2v2", "tb2v2.jar");
		Bundle tb2v1 = install("tb2v1.jar");
		try {
			PackageAdmin pa1 = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
			pa1.resolveBundles(new Bundle[] {tb2v2});
			assertTrue("Resolution is incorrect: " + tb2v1.getVersion(), tb2v1.getState() == Bundle.RESOLVED);
			assertTrue("Resolution is incorrect: " + tb2v2.getVersion(), tb2v2.getState() == Bundle.RESOLVED);
			uninstallCompositeBundle(composite);
		} finally {
			tb2v1.uninstall();
		}
	}

	public void testSingletons04() throws BundleException, IOException {
		// Test singletons in nested composites which imports a bundle with same BSN from the parent
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb2");
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
		Bundle tb2v2 = installConstituent(composite, "tb2v2", "tb2v2.jar");
		Bundle tb2v1 = install("tb2v1.jar");
		try {
			PackageAdmin pa1 = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
			pa1.resolveBundles(new Bundle[] {tb2v2});
			assertTrue("Resolution is incorrect: " + tb2v1.getVersion(), tb2v1.getState() == Bundle.RESOLVED);
			assertTrue("Resolution is incorrect: " + tb2v2.getVersion(), tb2v2.getState() != Bundle.RESOLVED);
			uninstallCompositeBundle(composite);
		} finally {
			tb2v1.uninstall();
		}
	}

	public void testFragments01() throws BundleException, IOException {
		// Test fragment resolution in a composite which has both the fragment and host installed
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb1");
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
		Bundle tb1 = installConstituent(composite, "tb1", "tb1.jar");
		Bundle tb1Frag1 = installConstituent(composite, "tb1Frag1", "tb1Frag1.jar");

		PackageAdmin pa = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
		pa.resolveBundles(new Bundle[] {tb1Frag1});
		assertTrue("Resolution is incorrect: " + tb1Frag1.getVersion(), tb1Frag1.getState() == Bundle.RESOLVED);
		assertTrue("Resolution is incorrect: " + tb1.getVersion(), tb1.getState() == Bundle.RESOLVED);
		Bundle[] hosts = pa.getHosts(tb1Frag1);
		assertNotNull("Hosts is null", hosts);
		assertEquals("Wrong number of hosts", 1, hosts.length);
		assertEquals("Wrong host found", tb1, hosts[0]);
		uninstallCompositeBundle(composite);
	}

	public void testFragments02() throws BundleException, IOException {
		// Test fragment resolution in a composite which does not require a bundle with the BSN of the host
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
		Bundle tb1Frag1 = installConstituent(composite, "tb1Frag1", "tb1Frag1.jar");
		Bundle tb1 = install("tb1.jar");
		try {
			PackageAdmin pa1 = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
			pa1.resolveBundles(new Bundle[] {tb1Frag1});
			assertTrue("Resolution is incorrect: " + tb1.getSymbolicName(), tb1.getState() == Bundle.RESOLVED);
			assertTrue("Resolution is incorrect: " + tb1Frag1.getSymbolicName(), tb1Frag1.getState() != Bundle.RESOLVED);
			uninstallCompositeBundle(composite);
		} finally {
			tb1.uninstall();
		}
	}

	public void testFragments03() throws BundleException, IOException {
		// Test fragment resolution in a composite which does require a bundle with the BSN of the host
		// Fragment should still not resolve
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb2");
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
		Bundle tb1Frag1 = installConstituent(composite, "tb1Frag1", "tb1Frag1.jar");
		Bundle tb1 = install("tb1.jar");
		try {
			PackageAdmin pa1 = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
			pa1.resolveBundles(new Bundle[] {tb1Frag1});
			assertTrue("Resolution is incorrect: " + tb1.getSymbolicName(), tb1.getState() == Bundle.RESOLVED);
			assertTrue("Resolution is incorrect: " + tb1Frag1.getSymbolicName(), tb1Frag1.getState() != Bundle.RESOLVED);
			uninstallCompositeBundle(composite);
		} finally {
			tb1.uninstall();
		}
	}
}
