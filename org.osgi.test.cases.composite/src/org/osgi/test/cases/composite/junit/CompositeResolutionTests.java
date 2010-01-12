/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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
import org.osgi.service.composite.CompositeBundle;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.cases.composite.AbstractCompositeTestCase;

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

	public void testSingletons03() {
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
			try {
				tb2v1.uninstall();
			} catch (BundleException e) {
				// just trying to clean up.
			}
		}
	}

	public void testSingletons04() {
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
			try {
				tb2v1.uninstall();
			} catch (BundleException e) {
				// just trying to clean up.
			}
		}
	}

	public void testSingletons05() {
		// Test singletons in nested composites which requires a bundle with same BSN from the parent 
		// (but no bundle installed in parent)
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb2");
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
		Bundle tb2v2 = installConstituent(composite, "tb2v2", "tb2v2.jar");
		PackageAdmin pa1 = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
		pa1.resolveBundles(new Bundle[] {tb2v2});
		assertTrue("Resolution is incorrect: " + tb2v2.getVersion(), tb2v2.getState() != Bundle.RESOLVED);
		uninstallCompositeBundle(composite);
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

	public void testFragments02() {
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
			try {
				tb1.uninstall();
			} catch (BundleException e) {
				// just trying to clean up.
			}
		}
	}

	public void testFragments03() {
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
			try {
				tb1.uninstall();
			} catch (BundleException e) {
				// just trying to clean up.
			}
		}
	}

	public void testPackageImport01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	public void testPackageImport01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	public void testPackageImport01c() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageImport01d() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPolicy01(manifest, new String[] {"tb3v2.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageImport01e() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPolicy01(manifest, new String[] {"tb3v2.jar"}, null, "tb3v2client.jar", false, null);
	}

	public void testPackageImport01f() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3client.jar", true, null);
	}

	public void testPackageImport01g() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3v2client.jar", false, null);
	}

	public void testPackageImport01h() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"[1.1,2.0)\", org.osgi.test.cases.composite.tb3.params; version=\"[1.1,2.0)\"");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	// TODO this is experimental to support RFP 121 requirement to import all
	public void testPackageImport01i() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"*; tb3version=1");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	// TODO this is experimental to support RFP 121 requirement to import all
	public void testPackageImport01j() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"*; tb3version=2");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	// TODO this is experimental to support RFP 121 requirement to import all
	public void testPackageImport01k() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.*");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	public void testPackageImport02a() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}
	
	public void testPackageImport02b() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	public void testPackageImport02c() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageImport02d() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb3v2.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageImport02e() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb3v2.jar"}, null, "tb3v2client.jar", false, null);
	}

	public void testPackageImport02f() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3client.jar", true, null);
	}

	public void testPackageImport02g() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3v2client.jar", false, null);
	}

	public void testPackageImport02h() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"[1.1,2.0)\", org.osgi.test.cases.composite.tb3.params; version=\"[1.1,2.0)\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageExport01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		doTestExportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	public void testPackageExport01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		doTestExportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	public void testPackageExport01c() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageExport01d() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportPolicy01(manifest, new String[] {"tb3v2.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageExport01e() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportPolicy01(manifest, new String[] {"tb3v2.jar"}, null, "tb3v2client.jar", false, null);
	}

	public void testPackageExport01f() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportPolicy01(manifest, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3client.jar", true, null);
	}

	public void testPackageExport01g() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportPolicy01(manifest, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3v2client.jar", false, null);
	}

	public void testPackageExport01h() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"[1.1,2.0)\", org.osgi.test.cases.composite.tb3.params; version=\"[1.1,2.0)\"");
		doTestExportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageExport01i() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "*; tb3version=1");
		doTestExportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	public void testPackageExport01j() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "*; tb3version=2");
		doTestExportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageExport01k() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.*");
		doTestExportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	public void testPackageExport02a() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}
	
	public void testPackageExport02b() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	public void testPackageExport02c() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageExport02d() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPolicy02(manifest1, manifest2, new String[] {"tb3v2.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageExport02e() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPolicy02(manifest1, manifest2, new String[] {"tb3v2.jar"}, null, "tb3v2client.jar", false, null);
	}

	public void testPackageExport02f() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3client.jar", true, null);
	}

	public void testPackageExport02g() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3v2client.jar", false, null);
	}

	public void testPackageExport02h() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"[1.1,2.0)\", org.osgi.test.cases.composite.tb3.params; version=\"[1.1,2.0)\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestExportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testRequireBundle01a() {
		// test requiring bundles into a composite
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.0, 1.1)\"");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1requireClient.jar", false, null);
	}

	public void testRequireBundle01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar"}, null, "tb3v1requireClient.jar", true, null);
	}

	public void testRequireBundle01c() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		doTestImportPolicy01(manifest, new String[] {"tb3v2.jar"}, null, "tb3v1requireClient.jar", true, null);
	}

	public void testRequireBundle01d() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		doTestImportPolicy01(manifest, new String[] {"tb3v2.jar"}, null, "tb3v2requireClient.jar", false, null);
	}

	public void testRequireBundle01e() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3v2requireClient.jar", false, null);
	}

	public void testRequireBundle01f() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "*");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3v2requireClient.jar", false, null);
	}

	public void testRequireBundle01g() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.*");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3v2requireClient.jar", false, null);
	}

	public void testRequireBundle01h() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.fail.*");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3v2requireClient.jar", true, null);
	}

	public void testRequireBundle01i() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.*; bundle-version=\"[1.0,1.1)\"");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3v2requireClient.jar", true, null);
	}

	public void testRequireBundle02a() {
		// test requiring bundles into two level nested composite
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"1.0\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar"}, null, "tb3v1requireClient.jar", false, null);
	}
	

	public void testRequireBundle02b() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar"}, null, "tb3v1requireClient.jar", true, null);
	}

	public void testRequireBundle02c() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb3v2.jar"}, null, "tb3v1requireClient.jar", true, null);
	}

	public void testRequireBundle02d() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb3v2.jar"}, null, "tb3v2requireClient.jar", false, null);
	}

	public void testRequireBundle02e() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3v2requireClient.jar", false, null);
	}

	public void testPackageExportImport01a() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	public void testPackageExportImport01b() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	public void testPackageExportImport01c() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageExportImport01d() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.1\", org.osgi.test.cases.composite.tb3.params; version=\"1.1\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageExportImport01e() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.1\", org.osgi.test.cases.composite.tb3.params; version=\"1.1\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v2.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageExportImport01f() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v2.jar"}, null, "tb3v2client.jar", false, null);
	}

	public void testPackageExportImport01g() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3client.jar", true, null);
	}

	public void testPackageExportImport01h() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3client.jar", false, null);
	}

	public void testPackageExportImport01i() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v1.jar", "tb3v2.jar"}, null, "tb3client.jar", false, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testPackageImportCompositePeer01a() {
		// tests that when some peer constraint is specified then the package cannot be exported by a bundle in the parent
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"some.parent\", " +
				"org.osgi.test.cases.composite.tb3.params; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"some.parent\"");
		doTestImportPolicy01(manifestImport, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testPackageExportImportCompositePeer01a() {
		Map manifestExport = new HashMap();
		String exportBSN = getName() + ".export";
		String exportVersion = "2.0.0";
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, exportBSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(Constants.BUNDLE_VERSION, exportVersion);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + exportBSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\", " +
				"org.osgi.test.cases.composite.tb3.params; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + exportBSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testPackageExportImportCompositePeer01b() {
		Map manifestExport = new HashMap();
		String exportBSN = getName() + ".export";
		String exportVersion = "2.0.0";
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, exportBSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(Constants.BUNDLE_VERSION, exportVersion);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + exportBSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[1.0.0, 1.1.0)\", " +
				"org.osgi.test.cases.composite.tb3.params; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + exportBSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[1.0.0, 1.1.0)\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testPackageExportImportCompositePeer01c() {
		Map manifestExport = new HashMap();
		String exportBSN = getName() + ".export";
		String exportVersion = "2.0.0";
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, exportBSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(Constants.BUNDLE_VERSION, exportVersion);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"fail\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\", " +
				"org.osgi.test.cases.composite.tb3.params; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"fail\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	public void testPackageExportImportCompositePeer01d() {
		try {
			install("tb3v1.jar").start();
		} catch (BundleException e) {
			fail("Unexpected exception installing bundle", e);
		}
		Map manifestExport = new HashMap();
		String exportBSN = getName() + ".export";
		String exportVersion = "2.0.0";
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, exportBSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(Constants.BUNDLE_VERSION, exportVersion);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"fail\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\", " +
				"org.osgi.test.cases.composite.tb3.params; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"fail\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
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

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
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

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testPackageExportImportCompositePeer02a() {
		Map manifestExport1 = new HashMap();
		String export1BSN = getName() + ".export1";
		String export1Version = "2.0.0";
		manifestExport1.put(Constants.BUNDLE_VERSION, export1Version);
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, export1BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifestExport2 = new HashMap(manifestExport1);
		String export2BSN = getName() + ".export2";
		String export2Version = "2.0.0";
		manifestExport2.put(Constants.BUNDLE_VERSION, export2Version);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, export2BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export1BSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\", " +
				"org.osgi.test.cases.composite.tb3.params; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export1BSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\"");
		Map manifestImport2 = new HashMap();
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport2.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testPackageExportImportCompositePeer02b() {
		Map manifestExport1 = new HashMap();
		String export1BSN = getName() + ".export1";
		String export1Version = "2.0.0";
		manifestExport1.put(Constants.BUNDLE_VERSION, export1Version);
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, export1BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifestExport2 = new HashMap(manifestExport1);
		String export2BSN = getName() + ".export2";
		String export2Version = "2.0.0";
		manifestExport2.put(Constants.BUNDLE_VERSION, export2Version);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, export2BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export1BSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[1.0.0, 1.1.0)\", " +
				"org.osgi.test.cases.composite.tb3.params; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export1BSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[1.0.0, 1.1.0)\"");
		Map manifestImport2 = new HashMap();
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport2.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testPackageExportImportCompositePeer02c() {
		Map manifestExport1 = new HashMap();
		String export1BSN = getName() + ".export1";
		String export1Version = "2.0.0";
		manifestExport1.put(Constants.BUNDLE_VERSION, export1Version);
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, export1BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifestExport2 = new HashMap(manifestExport1);
		String export2BSN = getName() + ".export2";
		String export2Version = "2.0.0";
		manifestExport2.put(Constants.BUNDLE_VERSION, export2Version);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, export2BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export2BSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\", " +
				"org.osgi.test.cases.composite.tb3.params; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export2BSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\"");
		Map manifestImport2 = new HashMap();
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport2.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testPackageExportImportCompositePeer02d() {
		Map manifestExport1 = new HashMap();
		String export1BSN = getName() + ".export1";
		String export1Version = "2.0.0";
		manifestExport1.put(Constants.BUNDLE_VERSION, export1Version);
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, export1BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifestExport2 = new HashMap(manifestExport1);
		String export2BSN = getName() + ".export2";
		String export2Version = "2.0.0";
		manifestExport2.put(Constants.BUNDLE_VERSION, export2Version);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, export2BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifestImport2 = new HashMap();
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport2.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export1BSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\", " +
				"org.osgi.test.cases.composite.tb3.params; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export1BSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\"");

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testPackageExportImportCompositePeer02e() {
		Map manifestExport1 = new HashMap();
		String export1BSN = getName() + ".export1";
		String export1Version = "2.0.0";
		manifestExport1.put(Constants.BUNDLE_VERSION, export1Version);
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, export1BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifestExport2 = new HashMap(manifestExport1);
		String export2BSN = getName() + ".export2";
		String export2Version = "2.0.0";
		manifestExport2.put(Constants.BUNDLE_VERSION, export2Version);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, export2BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3.params,org.osgi.test.cases.composite.tb3; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export1BSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\"");
		Map manifestImport2 = new HashMap();
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport2.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", false, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testPackageExportImportCompositePeer02f() {
		Map manifestExport1 = new HashMap();
		String export1BSN = getName() + ".export1";
		String export1Version = "2.0.0";
		manifestExport1.put(Constants.BUNDLE_VERSION, export1Version);
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, export1BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		Map manifestExport2 = new HashMap(manifestExport1);
		String export2BSN = getName() + ".export2";
		String export2Version = "2.0.0";
		manifestExport2.put(Constants.BUNDLE_VERSION, export2Version);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, export2BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3.params,org.osgi.test.cases.composite.tb3; version=\"1.0\"; " +
				AbstractCompositeTestCase.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export2BSN + "\"; " +
				AbstractCompositeTestCase.COMPOSITE_AFFINITY_VERSION + ":=\"[2.0.0, 2.1.0)\"");
		Map manifestImport2 = new HashMap();
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport2.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb3v1.jar"}, null, "tb3v1client.jar", true, null);
	}
}
