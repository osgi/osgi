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

public class CompositePackageAdminTests extends AbstractCompositeTestCase {

	public void testRefresh01() {
		// Tests refreshing a constituent which a parent bundle imports a package from.
		Bundle tb3client = null;
		try {
			tb3client = install("tb3client.jar");
		} catch (Exception e) {
			fail("Unexpected error installing test bundle", e);
		}
		TestBundleListener testListener = new TestBundleListener(tb3client, BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.UNRESOLVED | BundleEvent.RESOLVED);
		try {
			// test refreshing a bundle in a composite
			Map manifest = new HashMap();
			manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
			manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");
			CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
			Bundle tb3 = installConstituent(composite, "tb3", "tb3.jar");
	
	
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
		Bundle tb3 = null;
		try {
			tb3 = install("tb3.jar");
		} catch (Exception e) {
			fail("Unexpected error installing test bundle", e);
		}
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
		Bundle tb3client = null;
		try {
			tb3client = install("tb3client.jar");
		} catch (Exception e) {
			fail("Unexpected error installing test bundle", e);
		}
		TestBundleListener testParentListener = new TestBundleListener(tb3client, BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.UNRESOLVED | BundleEvent.RESOLVED);
		try {
			// test refreshing a bundle in a composite
			Map manifest = new HashMap();
			manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
			manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");
			CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
			startCompositeBundle(composite);
			Bundle tb3 = installConstituent(composite, "tb3", "tb3.jar");
	
	
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
		Bundle tb3client = null;
		try {
			tb3client = install("tb3client.jar");
		} catch (Exception e) {
			fail("Unexpected error installing test bundle", e);
		}
		TestBundleListener testParentListener = new TestBundleListener(tb3client, BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.UNRESOLVED | BundleEvent.RESOLVED);
		try {
			// test refreshing a bundle in a composite
			Map manifest = new HashMap();
			manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
			manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");
			CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
			startCompositeBundle(composite);
			Bundle tb3 = installConstituent(composite, "tb3", "tb3.jar");
	
	
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

}
