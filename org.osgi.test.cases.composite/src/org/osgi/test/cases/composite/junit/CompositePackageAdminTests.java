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


import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.service.composite.CompositeBundle;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;
import org.osgi.test.cases.composite.AbstractCompositeTestCase;
import org.osgi.test.cases.composite.TestHandler;

public class CompositePackageAdminTests extends AbstractCompositeTestCase {

	public void testRefresh01() {
		// Tests refreshing a constituent which a parent bundle imports a package from.
		Bundle tb3client = install("tb3client.jar");

		TestBundleListener testListener = new TestBundleListener(tb3client, BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.UNRESOLVED | BundleEvent.RESOLVED);
		try {
			// test refreshing a bundle in a composite
			Map manifest = new HashMap();
			manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
			manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");
			CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
			Bundle tb3 = installConstituent(composite, "tb3", "tb3v1.jar");
	
	
			PackageAdmin compositePA = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
			compositePA.resolveBundles(new Bundle[] {tb3});
	
			PackageAdmin parentPA = (PackageAdmin) getService(getContext(), PackageAdmin.class.getName());
			parentPA.resolveBundles(new Bundle[] {tb3client});
	
			assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3.getState());
			assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3client.getState());

			try {
				tb3client.start();
			} catch (BundleException e) {
				fail("Unexpected error starting", e);
			}
			getContext().addBundleListener(testListener);
			compositePA.refreshPackages(new Bundle[] {tb3});
			BundleEvent[] expected = new BundleEvent[] {
				new BundleEvent(BundleEvent.STOPPED, tb3client),
				new BundleEvent(BundleEvent.UNRESOLVED, tb3client),
				new BundleEvent(BundleEvent.RESOLVED, tb3client),
				new BundleEvent(BundleEvent.STARTED, tb3client)
			};
			BundleEvent[] results = (BundleEvent[]) testListener.getResults(new BundleEvent[4]);
			compareEvents(expected, results);
			uninstallCompositeBundle(composite);
		} finally {
			try {
				tb3client.uninstall();
			} catch (BundleException e) {
				// nothing
			}
			getContext().removeBundleListener(testListener);
		}
	}

	public void testRefresh02() {
		// Tests refreshing a parent bundle which a constituent imports a package from.
		Bundle tb3 = install("tb3v1.jar");
		
		try {
			// test refreshing a bundle in a composite
			Map manifest = new HashMap();
			manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
			manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");
			CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
			startCompositeBundle(composite);
			Bundle tb3client = installConstituent(composite, "tb3client", "tb3client.jar");

			PackageAdmin parentPA = (PackageAdmin) getService(getContext(), PackageAdmin.class.getName());
			parentPA.resolveBundles(new Bundle[] {tb3});
	
			PackageAdmin compositePA = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
			compositePA.resolveBundles(new Bundle[] {tb3client});
	
			assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3.getState());
			assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3client.getState());

			try {
				tb3client.start();
			} catch (BundleException e) {
				fail("Unexpected error starting", e);
			}
			TestBundleListener testListener = new TestBundleListener(tb3client, BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.UNRESOLVED | BundleEvent.RESOLVED);
			composite.getSystemBundleContext().addBundleListener(testListener);
			parentPA.refreshPackages(new Bundle[] {tb3});
			BundleEvent[] expected = new BundleEvent[] {
				new BundleEvent(BundleEvent.STOPPED, tb3client),
				new BundleEvent(BundleEvent.UNRESOLVED, tb3client),
				new BundleEvent(BundleEvent.RESOLVED, tb3client),
				new BundleEvent(BundleEvent.STARTED, tb3client)
			};
			BundleEvent[] results = (BundleEvent[]) testListener.getResults(new BundleEvent[4]);
			compareEvents(expected, results);
			uninstallCompositeBundle(composite);
		} finally {
			try {
				tb3.uninstall();
			} catch (BundleException e) {
				// nothing
			}
		}
	}

	public void testRefresh03() {
		// Tests refreshing a composite with a constituent which a parent bundle imports a package from.
		Bundle tb3client = install("tb3client.jar");

		TestBundleListener testParentListener = new TestBundleListener(tb3client, BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.UNRESOLVED | BundleEvent.RESOLVED);
		try {
			// test refreshing a bundle in a composite
			Map manifest = new HashMap();
			manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
			manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");
			CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
			startCompositeBundle(composite);
			Bundle tb3 = installConstituent(composite, "tb3", "tb3v1.jar");
	
	
			PackageAdmin compositePA = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
			compositePA.resolveBundles(new Bundle[] {tb3});
	
			PackageAdmin parentPA = (PackageAdmin) getService(getContext(), PackageAdmin.class.getName());
			parentPA.resolveBundles(new Bundle[] {tb3client});
	
			assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3.getState());
			assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3client.getState());

			try {
				tb3client.start();
			} catch (BundleException e) {
				fail("Unexpected error starting", e);
			}

			
			getContext().addBundleListener(testParentListener);
			parentPA.refreshPackages(new Bundle[] {composite});
			BundleEvent[] expected = new BundleEvent[] {
				new BundleEvent(BundleEvent.STOPPED, tb3client),
				new BundleEvent(BundleEvent.UNRESOLVED, tb3client),
				new BundleEvent(BundleEvent.RESOLVED, tb3client),
				new BundleEvent(BundleEvent.STARTED, tb3client)
			};
			BundleEvent[] results = (BundleEvent[]) testParentListener.getResults(new BundleEvent[4]);
			compareEvents(expected, results);
			uninstallCompositeBundle(composite);
		} finally {
			try {
				tb3client.uninstall();
			} catch (BundleException e) {
				// nothing
			}
			getContext().removeBundleListener(testParentListener);
		}
	}

	public void testRefresh04() {
		// Tests lazy  uninstall of a composite and refreshing a composite with a constituent which a parent bundle imports a package from.
		Bundle tb3client = install("tb3client.jar");

		TestBundleListener testParentListener = new TestBundleListener(tb3client, BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.UNRESOLVED | BundleEvent.RESOLVED);
		try {
			// test refreshing a bundle in a composite
			Map manifest = new HashMap();
			manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
			manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");
			CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
			startCompositeBundle(composite);
			Bundle tb3 = installConstituent(composite, "tb3", "tb3v1.jar");
	
	
			PackageAdmin compositePA = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
			compositePA.resolveBundles(new Bundle[] {tb3});
	
			PackageAdmin parentPA = (PackageAdmin) getService(getContext(), PackageAdmin.class.getName());
			parentPA.resolveBundles(new Bundle[] {tb3client});
	
			assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3.getState());
			assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3client.getState());

			try {
				tb3client.start();
			} catch (BundleException e) {
				fail("Unexpected error starting", e);
			}
	
			uninstallCompositeBundle(composite);

			getContext().addBundleListener(testParentListener);
			parentPA.refreshPackages(new Bundle[] {tb3client});

			BundleEvent[] expected = new BundleEvent[] {
					new BundleEvent(BundleEvent.STOPPED, tb3client),
					new BundleEvent(BundleEvent.UNRESOLVED, tb3client),
					new BundleEvent(BundleEvent.RESOLVED, tb3client),
					new BundleEvent(BundleEvent.STARTED, tb3client)
				};


			parentPA.refreshPackages(null);
			expected = new BundleEvent[] {
				new BundleEvent(BundleEvent.STOPPED, tb3client),
				new BundleEvent(BundleEvent.UNRESOLVED, tb3client)
			};
			BundleEvent[] results = (BundleEvent[]) testParentListener.getResults(new BundleEvent[2]);
			compareEvents(expected, results);
		} finally {
			try {
				tb3client.uninstall();
			} catch (BundleException e) {
				// nothing
			}
			getContext().removeBundleListener(testParentListener);
		}
	}

	public void testUpdate01() {
		// Test update a composite with a constituent which a parent bundle imports a package from.
		Bundle tb3client = install("tb3client.jar");

		TestBundleListener testListener = new TestBundleListener(tb3client, BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.UNRESOLVED | BundleEvent.RESOLVED);
		try {
			// install composite with no exports
			CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
			Bundle tb3 = installConstituent(composite, "tb3", "tb3v1.jar");
	
	
			PackageAdmin compositePA = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
			compositePA.resolveBundles(new Bundle[] {tb3});
	
			PackageAdmin parentPA = (PackageAdmin) getService(getContext(), PackageAdmin.class.getName());
			parentPA.resolveBundles(new Bundle[] {tb3client});
	
			assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3.getState());
			assertEquals("Resolution is incorrect.", Bundle.INSTALLED, tb3client.getState());

			// update the composite to export the necessary package for tb3client to resolve
			Map manifest = new HashMap();
			manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
			manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");

			updateCompositeBundle(composite, manifest);

			parentPA.resolveBundles(new Bundle[] {tb3client});
			assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3client.getState());
			try {
				tb3client.start();
			} catch (BundleException e) {
				fail("Failed to start test bundle", e);
			}
			getContext().addBundleListener(testListener);
			parentPA.refreshPackages(null);
			BundleEvent[] expected = new BundleEvent[] {
				new BundleEvent(BundleEvent.STOPPED, tb3client),
				new BundleEvent(BundleEvent.UNRESOLVED, tb3client),
				new BundleEvent(BundleEvent.RESOLVED, tb3client),
				new BundleEvent(BundleEvent.STARTED, tb3client)
			};
			BundleEvent[] results = (BundleEvent[]) testListener.getResults(new BundleEvent[expected.length]);
			compareEvents(expected, results);

			manifest.remove(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY);
			updateCompositeBundle(composite, manifest);

			parentPA.refreshPackages(null);
			expected = new BundleEvent[] {
					new BundleEvent(BundleEvent.STOPPED, tb3client),
					new BundleEvent(BundleEvent.UNRESOLVED, tb3client)
			};
			results = (BundleEvent[]) testListener.getResults(new BundleEvent[expected.length]);
			compareEvents(expected, results);
			
			uninstallCompositeBundle(composite);
		} finally {
			try {
				tb3client.uninstall();
			} catch (BundleException e) {
				// nothing
			}
			getContext().removeBundleListener(testListener);
		}
	}

	public void testUpdate02() {
		// Test update a composite with a constituent which imports a package from a parent bundle.
		Bundle tb3 = install("tb3v1.jar");

		TestBundleListener testListener = null;
		try {
			// install composite with no exports
			CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
			Bundle tb3client = installConstituent(composite, "tb3client", "tb3client.jar");
			testListener =  new TestBundleListener(tb3client, BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.UNRESOLVED | BundleEvent.RESOLVED);

			PackageAdmin parentPA = (PackageAdmin) getService(getContext(), PackageAdmin.class.getName());
			parentPA.resolveBundles(new Bundle[] {tb3});

			PackageAdmin compositePA = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
			compositePA.resolveBundles(new Bundle[] {tb3client});
	
			assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3.getState());
			assertEquals("Resolution is incorrect.", Bundle.INSTALLED, tb3client.getState());

			// update the composite to import the necessary package for tb3client to resolve
			Map manifest = new HashMap();
			manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
			manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");

			updateCompositeBundle(composite, manifest);
			startCompositeBundle(composite);

			compositePA.resolveBundles(new Bundle[] {tb3client});
			assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3client.getState());
			try {
				tb3client.start();
			} catch (BundleException e) {
				fail("Failed to start test bundle", e);
			}
			// TODO need to decide if listeners on the system context should outlive stop/start of composite
			composite.getSystemBundleContext().addBundleListener(testListener);
			parentPA.refreshPackages(null);
			BundleEvent[] expected = new BundleEvent[] {
				new BundleEvent(BundleEvent.STOPPED, tb3client),
				new BundleEvent(BundleEvent.UNRESOLVED, tb3client),
				new BundleEvent(BundleEvent.RESOLVED, tb3client),
				new BundleEvent(BundleEvent.STARTED, tb3client)
			};
			BundleEvent[] results = (BundleEvent[]) testListener.getResults(new BundleEvent[expected.length]);
			compareEvents(expected, results);
			
			// remove import policy
			manifest.remove(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY);
			updateCompositeBundle(composite, manifest);

			parentPA.refreshPackages(null);
			expected = new BundleEvent[] {
					new BundleEvent(BundleEvent.STOPPED, tb3client), // STOPPED and STARTED are a result of the composite update
					new BundleEvent(BundleEvent.STARTED, tb3client),
					new BundleEvent(BundleEvent.STOPPED, tb3client), // Second stop is because of refreshing composite
					new BundleEvent(BundleEvent.UNRESOLVED, tb3client) // Unresolved because composite refresh
			};
			results = (BundleEvent[]) testListener.getResults(new BundleEvent[expected.length]);
			compareEvents(expected, results);
			uninstallCompositeBundle(composite);
		} finally {
			try {
				tb3.uninstall();
			} catch (BundleException e) {
				// nothing
			}
			getContext().removeBundleListener(testListener);
		}
	}

	public void testGetSystemBundlePackages() {
		// test getting system bundle packages
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);

		PackageAdmin compositePA = (PackageAdmin) getService(composite.getSystemBundleContext(), PackageAdmin.class.getName());
		ExportedPackage[] systemPackages = compositePA.getExportedPackages(composite.getSystemBundleContext().getBundle(0));
		assertNotNull(systemPackages);
		
		uninstallCompositeBundle(composite);
	}

	public void testPackageAdminImport01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar"}, new String[] {"tb1.jar"}, "tb3v1client.jar", false,
				new PackageAdminHandler(new String[] {"org.osgi.test.cases.composite.tb3"}, null, null, null));
	}

	public void testPackageAdminImport01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar"}, new String[] {"tb1.jar"}, "tb3v1client.jar", true,
				new PackageAdminHandler(null, null, new String[] {"org.osgi.test.cases.composite.tb3"}, null));
	}

	public void testPackageAdminExport01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		doTestExportPolicy01(manifest, new String[] {"tb3v1.jar"}, new String[] {"tb1.jar"}, "tb3v1client.jar", false,
				new PackageAdminHandler(new String[] {"org.osgi.test.cases.composite.tb3"}, null, null, null));
	}

	public void testPackageAdminExport01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, 
				"org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		doTestExportPolicy01(manifest, new String[] {"tb3v1.jar"}, new String[] {"tb1.jar"}, "tb3v1client.jar", true,
				new PackageAdminHandler(null, null, new String[] {"org.osgi.test.cases.composite.tb3"}, null));
	}

	public void testPackageAdminRequire01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.0, 1.1)\"");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar"}, new String[] {"tb1.jar"}, "tb3v1requireClient.jar", false, 
				new PackageAdminHandler(null, new String[] {"org.osgi.test.cases.composite.tb3"}, null, null));
	}

	public void testPackageAdminRequire01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_BUNDLE_REQUIRE_POLICY, "org.osgi.test.cases.composite.tb3; bundle-version=\"[1.1,1.2)\"");
		doTestImportPolicy01(manifest, new String[] {"tb3v1.jar"}, new String[] {"tb1.jar"}, "tb3v1requireClient.jar", true,
				new PackageAdminHandler(null, null, null, new String[] {"org.osgi.test.cases.composite.tb3"}));
	}

	public void testPackageAdminExportImport01a() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=1, org.osgi.test.cases.composite.tb3.params; tb3version=1");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v1.jar"}, new String[] {"tb1.jar"}, "tb3v1client.jar", false,
				new PackageAdminHandler(new String[] {"org.osgi.test.cases.composite.tb3"}, null, null, null));
	}

	public void testPackageAdminExportImport01b() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3; tb3version=2, org.osgi.test.cases.composite.tb3.params; tb3version=2");
		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3; version=\"1.0\", org.osgi.test.cases.composite.tb3.params; version=\"1.0\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb3v1.jar"}, new String[] {"tb1.jar"}, "tb3v1client.jar", true, 
				new PackageAdminHandler(null, null, new String[] {"org.osgi.test.cases.composite.tb3"}, null));
	}

	public class PackageAdminHandler implements TestHandler {
		private final String[] expectedPackages;
		private final String[] expectedBundles;
		private final String[] missingPackages;
		private final String[] missingBundles;



		public PackageAdminHandler(String[] expectedPackages,
				String[] expectedBundles, String[] missingPackages,
				String[] missingBundles) {
			this.expectedPackages = expectedPackages;
			this.expectedBundles = expectedBundles;
			this.missingPackages = missingPackages;
			this.missingBundles = missingBundles;
		}

		public void handleBundles(Bundle[] exportBundles,
				Bundle[] importBundles, Bundle client) {
			// we assume the first importBundles is active
			assertNotNull("importBundles is null", importBundles);
			assertTrue("No importBundles", importBundles.length > 0);
			Bundle importBundle = importBundles[0];
			assertEquals("Wrong state for bundle", Bundle.ACTIVE, importBundle.getState());
			PackageAdmin pa = (PackageAdmin) getService(importBundle.getBundleContext(), PackageAdmin.class.getName());
			if (expectedPackages != null)
				for (int i = 0; i < expectedPackages.length; i++) {
					ExportedPackage[] exports = pa.getExportedPackages(expectedPackages[i]);
					assertNotNull(exports);
					assertTrue(exports.length > 0);
					for (int j = 0; j < exports.length; j++) {
						boolean found = false;
						Bundle exporter = exports[j].getExportingBundle();
						for (int k = 0; k < exportBundles.length && !found; k++) {
							found = exporter == exportBundles[k];
						}
						assertTrue("Could not find exporter for: " + expectedPackages[i], found);
					}
					
				}
			if (expectedBundles != null)
				for (int i = 0; i < expectedBundles.length; i++) {
					RequiredBundle[] required = pa.getRequiredBundles(expectedBundles[i]);
					assertNotNull(required);
					assertTrue(required.length > 0);
					for (int j = 0; j < required.length; j++) {
						boolean found = false;
						Bundle exporter = required[j].getBundle();
						for (int k = 0; k < exportBundles.length && !found; k++) {
							found = exporter == exportBundles[k];
						}
						assertTrue("Could not find bundle for: " + expectedBundles[i], found);
					}
					
				}
			if (missingPackages != null)
				for (int i = 0; i < missingPackages.length; i++) {
					ExportedPackage[] exports = pa.getExportedPackages(missingPackages[i]);
					assertNull("Exports is not null for: " + missingPackages[i], exports);
				}
			if (missingBundles != null)
				for (int i = 0; i < missingBundles.length; i++) {
					RequiredBundle[] required = pa.getRequiredBundles(missingBundles[i]);
					assertNull("Bundles is not null for: " + missingBundles[i], required);
				}
		}

		public void handleException(Throwable t) {
			// nothing
		}

	}
}
