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
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.service.composite.CompositeAdmin;
import org.osgi.service.composite.CompositeBundle;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.service.packageadmin.PackageAdmin;

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
		// Test singletons in nested composites which does not require a bundle with same BSN from the parent
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
		// Test singletons in nested composites which requires a bundle with same BSN from the parent
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

	public void testPackageImport01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		doTestImportPolicy01(manifest, "tb3v1.jar", "tb3v1client.jar", false);
	}

	public void testPackageImport01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		doTestImportPolicy01(manifest, "tb3v1.jar", "tb3v1client.jar", false);
	}

	public void testPackageImport01c() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPolicy01(manifest, "tb3v1.jar", "tb3v1client.jar", true);
	}

	public void testPackageImport01d() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPolicy01(manifest, "tb3v2.jar", "tb3v1client.jar", true);
	}

	public void testPackageImport01e() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPolicy01(manifest, "tb3v2.jar", "tb3v2client.jar", false);
	}

	public void testPackageImport01f() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPolicy01(manifest, "tb3v1.jar", "tb3v2.jar", "tb3client.jar", true);
	}

	public void testPackageImport01g() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPolicy01(manifest, "tb3v1.jar", "tb3v2.jar", "tb3v2client.jar", false);
	}

	public void testPackageImport01h() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"[1.1,2.0)\", org.osgi.test.cases.composite.tb3.params; version=\"[1.1,2.0)\"");
		doTestImportPolicy01(manifest, "tb3v1.jar", "tb3v1client.jar", true);
	}

	// TODO this is experimental to support RFP 121 requirement to import all
	public void testPackageImport01i() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"*; tb3version=1");
		doTestImportPolicy01(manifest, "tb3v1.jar", "tb3v1client.jar", false);
	}

	// TODO this is experimental to support RFP 121 requirement to import all
	public void testPackageImport01j() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"*; tb3version=2");
		doTestImportPolicy01(manifest, "tb3v1.jar", "tb3v1client.jar", true);
	}

	// TODO this is experimental to support RFP 121 requirement to import all
	public void testPackageImport01k() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.*");
		doTestImportPolicy01(manifest, "tb3v1.jar", "tb3v1client.jar", false);
	}

	private void doTestImportPolicy01(Map manifest, String tb3_1_Loc, String tb3vClient, boolean clientFail) {
		doTestImportPolicy01(manifest, tb3_1_Loc, null, tb3vClient, clientFail);
	}

	private void doTestImportPolicy01(Map manifest, String tb3_1_Loc, String tb3_2_Loc, String tb3vClient, boolean clientFail) {
		// Test importing packages into a composite
		Bundle tb3_1 = null;
		Bundle tb3_2 = null;
		try {
			tb3_1 = install(tb3_1_Loc);
			if (tb3_2_Loc != null)
				tb3_2 = install(tb3_2_Loc);
		} catch (Exception e) {
			fail("Unexpected error installing test bundle", e);
		}
		try {
			CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
			startCompositeBundle(composite);
			Bundle tb3client = installConstituent(composite, "tb3client", tb3vClient);

			try {
				tb3_1.start();
				if (tb3_2 != null)
					tb3_2.start();
			} catch (BundleException e) {
				fail("Unexpected error starting", e);
			}	
			try {
				tb3client.start();
				if (clientFail)
					fail("Expected client to fail to start: " + tb3client.getLocation());
			} catch (BundleException e) {
				if (!clientFail)
					fail("Unexpected error starting", e);
			}
			uninstallCompositeBundle(composite);
		} finally {
			try {
				tb3_1.uninstall();
				if (tb3_2 != null)
					tb3_2.uninstall();
			} catch (BundleException e) {
				// nothing
			}
		}
	}

	public void testPackageImport02a() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2, "tb3v1.jar", "tb3v1client.jar", false);
	}
	
	public void testPackageImport02b() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, "tb3v1.jar", "tb3v1client.jar", false);
	}

	public void testPackageImport02c() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, "tb3v1.jar", "tb3v1client.jar", true);
	}

	public void testPackageImport02d() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, "tb3v2.jar", "tb3v1client.jar", true);
	}

	public void testPackageImport02e() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, "tb3v2.jar", "tb3v2client.jar", false);
	}

	public void testPackageImport02f() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, "tb3v1.jar", "tb3v2.jar", "tb3client.jar", true);
	}

	public void testPackageImport02g() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, "tb3v1.jar", "tb3v2.jar", "tb3v2client.jar", false);
	}

	public void testPackageImport02h() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"[1.1,2.0)\", org.osgi.test.cases.composite.tb3.params; version=\"[1.1,2.0)\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, "tb3v1.jar", "tb3v1client.jar", true);
	}

	private void doTestImportPolicy02(Map manifest1, Map manifest2, String tb3_1_Loc, String tb3vClient, boolean clientFail) {
		doTestImportPolicy02(manifest1, manifest2, tb3_1_Loc, null, tb3vClient, clientFail);
	}

	private void doTestImportPolicy02(Map manifest1, Map manifest2, String tb3_1_Loc, String tb3_2_Loc, String tb3vClient, boolean clientFail) {
		// Test importing packages into two level nested composite
		Bundle tb3_1 = null;
		Bundle tb3_2 = null;
		try {
			tb3_1 = install(tb3_1_Loc);
			if (tb3_2_Loc != null)
				tb3_2 = install(tb3_2_Loc);
		} catch (Exception e) {
			fail("Unexpected error installing test bundle", e);
		}
		try {
			CompositeBundle composite1 = createCompositeBundle(compAdmin, getName() + "_1", manifest1, null);
			startCompositeBundle(composite1);
			CompositeAdmin compAdmin1 = (CompositeAdmin) getService(composite1.getSystemBundleContext(), CompositeAdmin.class.getName());
			CompositeBundle composite2 = createCompositeBundle(compAdmin1, getName() + "_2", manifest2, null);
			startCompositeBundle(composite2);
			Bundle tb3client = installConstituent(composite2, "tb3client", tb3vClient);

			try {
				tb3_1.start();
				if (tb3_2 != null)
					tb3_2.start();
			} catch (BundleException e) {
				fail("Unexpected error starting", e);
			}	
			try {
				tb3client.start();
				if (clientFail)
					fail("Expected client to fail to start: " + tb3client.getLocation());
			} catch (BundleException e) {
				if (!clientFail)
					fail("Unexpected error starting", e);
			}
			uninstallCompositeBundle(composite1);
		} finally {
			try {
				tb3_1.uninstall();
				if (tb3_2 != null)
					tb3_2.uninstall();
			} catch (BundleException e) {
				// nothing
			}
		}
	}

	public void testPackageExport01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		doTestExportPackage01(manifest, "tb3v1.jar", "tb3v1client.jar", false);
	}

	public void testPackageExport01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		doTestExportPackage01(manifest, "tb3v1.jar", "tb3v1client.jar", false);
	}

	public void testPackageExport01c() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportPackage01(manifest, "tb3v1.jar", "tb3v1client.jar", true);
	}

	public void testPackageExport01d() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportPackage01(manifest, "tb3v2.jar", "tb3v1client.jar", true);
	}

	public void testPackageExport01e() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportPackage01(manifest, "tb3v2.jar", "tb3v2client.jar", false);
	}

	public void testPackageExport01f() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportPackage01(manifest, "tb3v1.jar", "tb3v2.jar", "tb3client.jar", true);
	}

	public void testPackageExport01g() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportPackage01(manifest, "tb3v1.jar", "tb3v2.jar", "tb3v2client.jar", false);
	}

	public void testPackageExport01h() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"[1.1,2.0)\", org.osgi.test.cases.composite.tb3.params; version=\"[1.1,2.0)\"");
		doTestExportPackage01(manifest, "tb3v1.jar", "tb3v1client.jar", true);
	}

	// TODO this is experimental to support RFP 121 requirement to import all
	public void testPackageExport01i() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "*; tb3version=1");
		doTestExportPackage01(manifest, "tb3v1.jar", "tb3v1client.jar", false);
	}

	// TODO this is experimental to support RFP 121 requirement to import all
	public void testPackageExport01j() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "*; tb3version=2");
		doTestExportPackage01(manifest, "tb3v1.jar", "tb3v1client.jar", true);
	}

	// TODO this is experimental to support RFP 121 requirement to import all
	public void testPackageExport01k() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.*");
		doTestExportPackage01(manifest, "tb3v1.jar", "tb3v1client.jar", false);
	}

	private void doTestExportPackage01(Map manifest, String tb3_1_Loc, String tb3vClient, boolean clientFail) {
		doTestExportPackage01(manifest, tb3_1_Loc, null, tb3vClient, clientFail);
	}

	private void doTestExportPackage01(Map manifest, String tb3_1_Loc, String tb3_2_Loc, String tb3vClient, boolean clientFail) {
		// Test exporting packages to parent
		Bundle tb3client = null;
		try {
			tb3client = install(tb3vClient);
		} catch (Exception e) {
			fail("Unexpected error installing test bundle", e);
		}
		try {
			CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
			startCompositeBundle(composite);
			Bundle tb3_1 = installConstituent(composite, "tb3_1", tb3_1_Loc);
			Bundle tb3_2 = null;
			if (tb3_2_Loc != null)
				tb3_2 = installConstituent(composite, "tb3_2", tb3_2_Loc);
			try {
				tb3_1.start();
				if (tb3_2 != null)
					tb3_2.start();
			} catch (BundleException e) {
				fail("Unexpected error starting", e);
			}	
			try {
				tb3client.start();
				if (clientFail)
					fail("Expected client to fail to start: " + tb3client.getLocation());
			} catch (BundleException e) {
				if (!clientFail)
					fail("Unexpected error starting", e);
			}
			uninstallCompositeBundle(composite);
		} finally {
			try {
				tb3client.uninstall();
			} catch (BundleException e) {
				// nothing
			}
		}
	}

	public void testPackageExport02a() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportPackage02(manifest1, manifest2, "tb3v1.jar", "tb3v1client.jar", false);
	}
	
	public void testPackageExport02b() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPackage02(manifest1, manifest2, "tb3v1.jar", "tb3v1client.jar", false);
	}

	public void testPackageExport02c() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPackage02(manifest1, manifest2, "tb3v1.jar", "tb3v1client.jar", true);
	}

	public void testPackageExport02d() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPackage02(manifest1, manifest2, "tb3v2.jar", "tb3v1client.jar", true);
	}

	public void testPackageExport02e() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPackage02(manifest1, manifest2, "tb3v2.jar", "tb3v2client.jar", false);
	}

	public void testPackageExport02f() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPackage02(manifest1, manifest2, "tb3v1.jar", "tb3v2.jar", "tb3client.jar", true);
	}

	public void testPackageExport02g() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPackage02(manifest1, manifest2, "tb3v1.jar", "tb3v2.jar", "tb3v2client.jar", false);
	}

	public void testPackageExport02h() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"[1.1,2.0)\", org.osgi.test.cases.composite.tb3.params; version=\"[1.1,2.0)\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPackage02(manifest1, manifest2, "tb3v1.jar", "tb3v1client.jar", true);
	}

	private void doTestExportPackage02(Map manifest1, Map manifest2, String tb3_1_Loc, String tb3vClient, boolean clientFail) {
		doTestExportPackage02(manifest1, manifest2, tb3_1_Loc, null, tb3vClient, clientFail);
	}

	private void doTestExportPackage02(Map manifest1, Map manifest2, String tb3_1_Loc, String tb3_2_Loc, String tb3vClient, boolean clientFail) {
		// test exporting packages to parent from two level nested composite
		Bundle tb3client = null;
		try {
			tb3client = install(tb3vClient);
		} catch (Exception e) {
			fail("Unexpected error installing test bundle", e);
		}
		try {
			CompositeBundle composite1 = createCompositeBundle(compAdmin, getName() + "_1", manifest1, null);
			startCompositeBundle(composite1);
			CompositeAdmin compAdmin1 = (CompositeAdmin) getService(composite1.getSystemBundleContext(), CompositeAdmin.class.getName());
			CompositeBundle composite2 = createCompositeBundle(compAdmin1, getName() + "_2", manifest2, null);
			startCompositeBundle(composite2);
			Bundle tb3_1 = installConstituent(composite2, "tb3_1", tb3_1_Loc);
			Bundle tb3_2 = null;
			if (tb3_2_Loc != null)
				tb3_2 = installConstituent(composite2, "tb3_2", tb3_2_Loc);
			try {
				tb3_1.start();
				if (tb3_2 != null)
					tb3_2.start();
			} catch (BundleException e) {
				fail("Unexpected error starting", e);
			}	
			try {
				tb3client.start();
				if (clientFail)
					fail("Expected client to fail to start: " + tb3client.getLocation());
			} catch (BundleException e) {
				if (!clientFail)
					fail("Unexpected error starting", e);
			}
			uninstallCompositeBundle(composite1);
		} finally {
			try {
				tb3client.uninstall();
			} catch (BundleException e) {
				// nothing
			}
		}
	}


	public void testRequireBundle01a() {
		// test requiring bundles into a composite
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.0, 1.1)\"");
		doTestImportPolicy01(manifest, "tb3v1.jar", "tb3v1requireClient.jar", false);
	}

	public void testRequireBundle01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		doTestImportPolicy01(manifest, "tb3v1.jar", "tb3v1requireClient.jar", true);
	}

	public void testRequireBundle01c() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		doTestImportPolicy01(manifest, "tb3v2.jar", "tb3v1requireClient.jar", true);
	}

	public void testRequireBundle01d() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		doTestImportPolicy01(manifest, "tb3v2.jar", "tb3v2requireClient.jar", false);
	}

	public void testRequireBundle01e() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		doTestImportPolicy01(manifest, "tb3v1.jar", "tb3v2.jar", "tb3v2requireClient.jar", false);
	}

	public void testRequireBundle02a() {
		// test requiring bundles into two level nested composite
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"1.0\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2, "tb3v1.jar", "tb3v1requireClient.jar", false);
	}
	

	public void testRequireBundle02b() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, "tb3v1.jar", "tb3v1requireClient.jar", true);
	}

	public void testRequireBundle02c() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, "tb3v2.jar", "tb3v1requireClient.jar", true);
	}

	public void testRequireBundle02d() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, "tb3v2.jar", "tb3v2requireClient.jar", false);
	}

	public void testRequireBundle02e() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, "tb3v1.jar", "tb3v2.jar", "tb3v2requireClient.jar", false);
	}

	public void testPackageExportImport01a() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, "tb3v1.jar", "tb3v1client.jar", false);
	}

	public void testPackageExportImport01b() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, "tb3v1.jar", "tb3v1client.jar", false);
	}

	public void testPackageExportImport01c() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, "tb3v1.jar", "tb3v1client.jar", true);
	}

	public void testPackageExportImport01d() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.1\", org.osgi.test.cases.composite.tb3.params; version=\"1.1\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, "tb3v1.jar", "tb3v1client.jar", true);
	}

	public void testPackageExportImport01e() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.1\", org.osgi.test.cases.composite.tb3.params; version=\"1.1\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, "tb3v2.jar", "tb3v1client.jar", true);
	}

	public void testPackageExportImport01f() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportImportPolicy01(manifestExport, manifestImport, "tb3v2.jar", "tb3v2client.jar", false);
	}

	public void testPackageExportImport01g() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportImportPolicy01(manifestExport, manifestImport, "tb3v1.jar", "tb3v2.jar", "tb3client.jar", true);
	}

	public void testPackageExportImport01h() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportImportPolicy01(manifestExport, manifestImport, "tb3v1.jar", "tb3v2.jar", "tb3client.jar", false);
	}

	public void testPackageExportImport01i() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");
		doTestExportImportPolicy01(manifestExport, manifestImport, "tb3v1.jar", "tb3v2.jar", "tb3client.jar", false);
	}

	private void doTestExportImportPolicy01(Map manifestExport, Map manifestImport, String tb3_1_Loc, String tb3vClient, boolean clientFail) {
		doTestExportImportPolicy01(manifestExport, manifestImport, tb3_1_Loc, null, tb3vClient, clientFail);
	}

	private void doTestExportImportPolicy01(Map manifestExport, Map manifestImport, String tb3_1_Loc, String tb3_2_Loc, String tb3vClient, boolean clientFail) {
		// test exporting packages to parent and importing into another composite
		CompositeBundle compositeExport = createCompositeBundle(compAdmin, getName() + "_export", manifestExport, null);
		startCompositeBundle(compositeExport);
		Bundle tb3_1 = installConstituent(compositeExport, "tb3_1", tb3_1_Loc);
		Bundle tb3_2 = null;
		if (tb3_2_Loc != null)
			tb3_2 = installConstituent(compositeExport, "tb3_2", tb3_2_Loc);
		try {
			tb3_1.start();
			if (tb3_2 != null)
				tb3_2.start();
		} catch (BundleException e) {
			fail("Unexpected error starting", e);
		}
		CompositeBundle compositeImport = createCompositeBundle(compAdmin, getName() + "_import", manifestImport, null);
		startCompositeBundle(compositeImport);
		Bundle tb3client = installConstituent(compositeImport, "tb3client", tb3vClient);
		try {
			tb3client.start();
			if (clientFail)
				fail("Expected client to fail to start: " + tb3client.getLocation());
		} catch (BundleException e) {
			if (!clientFail)
				fail("Unexpected error starting", e);
		}
		uninstallCompositeBundle(compositeExport);
		uninstallCompositeBundle(compositeImport);
	}

	public void testPackageExportImport02a() {
		Map manifestExport1 = new HashMap();
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifestExport2 = new HashMap(manifestExport1);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, "tb3v1.jar", "tb3v1client.jar", false);
	}

	public void testPackageExportImport02b() {
		Map manifestExport1 = new HashMap();
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifestExport2 = new HashMap(manifestExport1);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, "tb3v1.jar", "tb3v1client.jar", true);
	}

	private void doTestExportImportPolicy02(Map manifestExport1, Map manifestExport2, Map manifestImport1, Map manifestImport2, String tb3_1_Loc, String tb3vClient, boolean clientFail) {
		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, tb3_1_Loc, null, tb3vClient, clientFail);
	}

	private void doTestExportImportPolicy02(Map manifestExport1, Map manifestExport2, Map manifestImport1, Map manifestImport2, String tb3_1_Loc, String tb3_2_Loc, String tb3vClient, boolean clientFail) {
		// test exporting packages to parent from two level nested composite 
		// and importing into another two level nested composite
		CompositeBundle compositeExport1 = createCompositeBundle(compAdmin, getName() + "_export1", manifestExport1, null);
		startCompositeBundle(compositeExport1);
		CompositeAdmin compAdminExport1 = (CompositeAdmin) getService(compositeExport1.getSystemBundleContext(), CompositeAdmin.class.getName());
		CompositeBundle compositeExport2 = createCompositeBundle(compAdminExport1, getName() + "_export2", manifestExport2, null);
		startCompositeBundle(compositeExport2);
		Bundle tb3_1 = installConstituent(compositeExport2, "tb3_1", tb3_1_Loc);
		Bundle tb3_2 = null;
		if (tb3_2_Loc != null)
			tb3_2 = installConstituent(compositeExport2, "tb3_2", tb3_2_Loc);
		try {
			tb3_1.start();
			if (tb3_2 != null)
				tb3_2.start();
		} catch (BundleException e) {
			fail("Unexpected error starting", e);
		}
		CompositeBundle compositeImport1 = createCompositeBundle(compAdmin, getName() + "_import1", manifestImport1, null);
		startCompositeBundle(compositeImport1);
		CompositeAdmin compAdminImport1 = (CompositeAdmin) getService(compositeImport1.getSystemBundleContext(), CompositeAdmin.class.getName());
		CompositeBundle compositeImport2 = createCompositeBundle(compAdminImport1, getName() + "_import2", manifestImport2, null);
		startCompositeBundle(compositeImport2);
		Bundle tb3client = installConstituent(compositeImport2, "tb3client", tb3vClient);
		try {
			tb3client.start();
			if (clientFail)
				fail("Expected client to fail to start: " + tb3client.getLocation());
		} catch (BundleException e) {
			if (!clientFail)
				fail("Unexpected error starting", e);
		}
		uninstallCompositeBundle(compositeExport1);
		uninstallCompositeBundle(compositeImport1);
	}

}
