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
import org.osgi.service.composite.CompositeAdmin;
import org.osgi.service.composite.CompositeBundle;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.cases.composite.junit.AbstractCompositeTestCase.TestBundleListener;
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

	public void testPackageImport01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		doTestImportPackage01(manifest, "tb3v1.jar", "tb3v1client.jar", false);
	}

	public void testPackageImport01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		doTestImportPackage01(manifest, "tb3v1.jar", "tb3v1client.jar", false);
	}

	public void testPackageImport01c() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPackage01(manifest, "tb3v1.jar", "tb3v1client.jar", true);
	}

	public void testPackageImport01d() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPackage01(manifest, "tb3v2.jar", "tb3v1client.jar", true);
	}

	public void testPackageImport01e() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPackage01(manifest, "tb3v2.jar", "tb3v2client.jar", false);
	}

	public void testPackageImport01f() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPackage01(manifest, "tb3v1.jar", "tb3v2.jar", "tb3v2client.jar", true);
	}

	public void testPackageImport01g() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPackage01(manifest, "tb3v1.jar", "tb3v2.jar", "tb3v2client.jar", false);
	}

	public void testPackageImport01h() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"[1.1,2.0)\", org.osgi.test.cases.composite.tb3.params; version=\"[1.1,2.0)\"");
		doTestImportPackage01(manifest, "tb3v1.jar", "tb3v1client.jar", true);
	}

	private void doTestImportPackage01(Map manifest, String tb3_1_Loc, String tb3vClient, boolean clientFail) {
		doTestImportPackage01(manifest, tb3_1_Loc, null, tb3vClient, clientFail);
	}

	private void doTestImportPackage01(Map manifest, String tb3_1_Loc, String tb3_2_Loc, String tb3vClient, boolean clientFail) {
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

		doTestImportPackage02(manifest1, manifest2, "tb3v1.jar", "tb3v1client.jar", false);
	}
	
	public void testPackageImport02b() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPackage02(manifest1, manifest2, "tb3v1.jar", "tb3v1client.jar", false);
	}

	public void testPackageImport02c() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPackage02(manifest1, manifest2, "tb3v1.jar", "tb3v1client.jar", true);
	}

	public void testPackageImport02d() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPackage02(manifest1, manifest2, "tb3v2.jar", "tb3v1client.jar", true);
	}

	public void testPackageImport02e() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPackage02(manifest1, manifest2, "tb3v2.jar", "tb3v2client.jar", false);
	}

	public void testPackageImport02f() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPackage02(manifest1, manifest2, "tb3v1.jar", "tb3v2.jar", "tb3v2client.jar", true);
	}

	public void testPackageImport02g() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPackage02(manifest1, manifest2, "tb3v1.jar", "tb3v2.jar", "tb3v2client.jar", false);
	}

	public void testPackageImport02h() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"[1.1,2.0)\", org.osgi.test.cases.composite.tb3.params; version=\"[1.1,2.0)\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPackage02(manifest1, manifest2, "tb3v1.jar", "tb3v1client.jar", true);
	}

	private void doTestImportPackage02(Map manifest1, Map manifest2, String tb3_1_Loc, String tb3vClient, boolean clientFail) {
		doTestImportPackage02(manifest1, manifest2, tb3_1_Loc, null, tb3vClient, clientFail);
	}

	private void doTestImportPackage02(Map manifest1, Map manifest2, String tb3_1_Loc, String tb3_2_Loc, String tb3vClient, boolean clientFail) {
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
		doTestExportPackage01(manifest, "tb3v1.jar", "tb3v2.jar", "tb3v2client.jar", true);
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
		doTestExportPackage02(manifest1, manifest2, "tb3v1.jar", "tb3v2.jar", "tb3v2client.jar", true);
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


	public void testRequireBundle01() {
		// test requiring bundles into a composite
	}

	public void testReqiureBundle02() {
		// test requiring bundles into two level nested composite
	}

	public void testExportImportPackage01() {
		// test exporting packages to parent and importing into another composite
	}

	public void testExportImportPackage02() {
		// test exporting packages to parent from two level nested composite 
		// and importing into another two level nested composite
	}
}
