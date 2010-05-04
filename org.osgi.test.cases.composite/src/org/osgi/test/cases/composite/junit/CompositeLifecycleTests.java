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
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.composite.CompositeAdmin;
import org.osgi.service.composite.CompositeBundle;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.cases.composite.AbstractCompositeTestCase;
import org.osgi.test.cases.composite.AbstractCompositeTestCase.TestBundleListener;

public class CompositeLifecycleTests extends AbstractCompositeTestCase {
	public void testCompositeCreate01() {
		// test create and uninstall
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
		uninstallCompositeBundle(composite);
	}

	public void testCompositeCreate02() {
		// test create, start, stop and uninstall
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
		startCompositeBundle(composite);
		stopCompositeBundle(composite);
		uninstallCompositeBundle(composite);
	}

	public void testCompositeCreate03() {
		// test creating nested composites
		CompositeBundle composite1 = createCompositeBundle(compAdmin, getName(), null, null);

		// create two nested composites under composite1
		CompositeAdmin compAdmin1 = (CompositeAdmin) getService(composite1.getSystemBundleContext(), CompositeAdmin.class.getName());
		CompositeBundle composite1_1 = createCompositeBundle(compAdmin1, getName()+".1_1", null, null);
		CompositeBundle composite1_2 = createCompositeBundle(compAdmin1, getName()+".1_2", null, null);

		// create two nested composites under each composite
		CompositeAdmin compAdmin1_1 = (CompositeAdmin) getService(composite1_1.getSystemBundleContext(), CompositeAdmin.class.getName());
		CompositeBundle composite1_1_1 = createCompositeBundle(compAdmin1_1, getName()+".1_1_1", null, null);
		CompositeBundle composite1_1_2 = createCompositeBundle(compAdmin1_1, getName()+".1_1_2", null, null);

		CompositeAdmin compAdmin1_2 = (CompositeAdmin) getService(composite1_2.getSystemBundleContext(), CompositeAdmin.class.getName());
		CompositeBundle composite1_2_1 = createCompositeBundle(compAdmin1_2, getName()+".1_2_1", null, null);
		CompositeBundle composite1_2_2 = createCompositeBundle(compAdmin1_2, getName()+".1_2_2", null, null);

		startCompositeBundle(composite1);
		startCompositeBundle(composite1_1);
		startCompositeBundle(composite1_1_1);
		startCompositeBundle(composite1_1_2);
		startCompositeBundle(composite1_2);
		startCompositeBundle(composite1_2_1);
		startCompositeBundle(composite1_2_2);
		uninstallCompositeBundle(composite1);
	}

	public void testCompositeCreate04() {
		// test create a composite at an existing normal bundle location
		Bundle tb1 = install("tb1.jar");
		// expected to fail because a non composite is already installed at the location
		createCompositeBundle(compAdmin, tb1.getLocation(), null, null, true);
	}

	public void testCompositeCreate05() {
		// test create a composite at an existing composite location
		CompositeBundle composite1 = createCompositeBundle(compAdmin, getName(), null, null);
		CompositeBundle composite2 = createCompositeBundle(compAdmin, getName(), null, null);
		assertTrue("Expected the CompositeBundle objects to be identical", composite1 == composite2);
		uninstallCompositeBundle(composite1);
	}

	public void testCompositeCreate06() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=true");
		manifest1.put(Constants.BUNDLE_VERSION, "1.0.0");
		CompositeBundle composite1 = createCompositeBundle(compAdmin, getName() + 1, manifest1, null);
	
		Map manifest2 = new HashMap();
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=true");
		manifest2.put(Constants.BUNDLE_VERSION, "2.0.0");
		CompositeBundle composite2 = createCompositeBundle(compAdmin, getName() + 2, manifest2, null);

		assertFalse("Composites should not be the same", composite1 == composite2);
	}

	public void testCompositeCreate07() {
		// test updating nested composites
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=true");
		manifest1.put(Constants.BUNDLE_VERSION, "1.0.0");
		CompositeBundle composite1 = createCompositeBundle(compAdmin, getName(), manifest1, null);

		// create two nested composites under composite1
		CompositeAdmin compAdmin1 = (CompositeAdmin) getService(composite1.getSystemBundleContext(), CompositeAdmin.class.getName());
		CompositeBundle composite1_1 = createCompositeBundle(compAdmin1, getName()+".1_1", null, null);
		CompositeBundle composite1_2 = createCompositeBundle(compAdmin1, getName()+".1_2", null, null);

		// create two nested composites under each composite
		CompositeAdmin compAdmin1_1 = (CompositeAdmin) getService(composite1_1.getSystemBundleContext(), CompositeAdmin.class.getName());
		CompositeBundle composite1_1_1 = createCompositeBundle(compAdmin1_1, getName()+".1_1_1", null, null);
		CompositeBundle composite1_1_2 = createCompositeBundle(compAdmin1_1, getName()+".1_1_2", null, null);

		CompositeAdmin compAdmin1_2 = (CompositeAdmin) getService(composite1_2.getSystemBundleContext(), CompositeAdmin.class.getName());
		CompositeBundle composite1_2_1 = createCompositeBundle(compAdmin1_2, getName()+".1_2_1", null, null);
		CompositeBundle composite1_2_2 = createCompositeBundle(compAdmin1_2, getName()+".1_2_2", null, null);

		startCompositeBundle(composite1);
		startCompositeBundle(composite1_1);
		startCompositeBundle(composite1_1_1);
		startCompositeBundle(composite1_1_2);
		startCompositeBundle(composite1_2);
		startCompositeBundle(composite1_2_1);
		startCompositeBundle(composite1_2_2);

		stopCompositeBundle(composite1);
		manifest1.put(Constants.BUNDLE_VERSION, "2.0.0");
		updateCompositeBundle(composite1, manifest1);
		uninstallCompositeBundle(composite1);
	}

	public void testCompositeCreate08() {
		// Test update a composite with nested composite with a constituent which imports a package from a parent bundle.
		Bundle tb3 = install("tb3v1.jar");

		// install that imports packages
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=true");
		manifest1.put(Constants.BUNDLE_VERSION, "1.0.0");
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");
		CompositeBundle composite1 = createCompositeBundle(compAdmin, getName(), manifest1, null);

		Map manifest2 = new HashMap();
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".1_1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=true");
		manifest2.put(Constants.BUNDLE_VERSION, "1.0.0");
		manifest2.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb3, org.osgi.test.cases.composite.tb3.params");

		CompositeAdmin compAdmin1 = (CompositeAdmin) getService(composite1.getSystemBundleContext(), CompositeAdmin.class.getName());
		CompositeBundle composite1_1 = createCompositeBundle(compAdmin1, getName()+".1_1", manifest2, null);
		startCompositeBundle(composite1_1, false);
		startCompositeBundle(composite1);
		
		Bundle tb3client = installConstituent(composite1_1, "tb3client", "tb3client.jar");

		PackageAdmin parentPA = (PackageAdmin) getService(getContext(), PackageAdmin.class.getName());
		parentPA.resolveBundles(new Bundle[] {tb3});

		PackageAdmin compositePA = (PackageAdmin) getService(composite1_1.getSystemBundleContext(), PackageAdmin.class.getName());
		compositePA.resolveBundles(new Bundle[] {tb3client});

		assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3.getState());
		assertEquals("Resolution is incorrect.", Bundle.RESOLVED, tb3client.getState());

		try {
			tb3client.start();
		} catch (BundleException e) {
			fail("Failed to start test bundle", e);
		}

		TestBundleListener testListener =  new TestBundleListener(tb3client, BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.UNRESOLVED | BundleEvent.RESOLVED);
		composite1_1.getSystemBundleContext().addBundleListener(testListener);

		manifest1.put(Constants.BUNDLE_VERSION, "1.1.0");
		updateCompositeBundle(composite1, manifest1);
		BundleEvent[] expected = new BundleEvent[] {
				new BundleEvent(BundleEvent.STOPPED, tb3client),
				new BundleEvent(BundleEvent.UNRESOLVED, tb3client),
				new BundleEvent(BundleEvent.RESOLVED, tb3client),
				new BundleEvent(BundleEvent.STARTED, tb3client)
		};
		BundleEvent[] results = (BundleEvent[]) testListener.getResults(new BundleEvent[expected.length]);
		compareEvents(expected, results);

		// update the composite to not import the necessary package for tb3client to resolve
		manifest1.remove(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY);
		updateCompositeBundle(composite1, manifest1);

		expected = new BundleEvent[] {
				new BundleEvent(BundleEvent.STOPPED, tb3client),
				new BundleEvent(BundleEvent.UNRESOLVED, tb3client)
		};
		results = (BundleEvent[]) testListener.getResults(new BundleEvent[expected.length]);
		compareEvents(expected, results);

		uninstallCompositeBundle(composite1);

	}

	public void testConstituentLifeCycle01() {
		// test create, install constituent and uninstall
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
		installConstituent(composite, "tb1", "tb1.jar");
		uninstallCompositeBundle(composite);
	}

	public void testConstituentLifeCycle02() {
		// test create, install constituent
		// persistently start constituent
		// finally uninstall
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
		Bundle tb1 = installConstituent(composite, "tb1", "tb1.jar");
		try {
			tb1.start();
		} catch (BundleException e) {
			fail("Failed to start: " + tb1.getLocation(), e);
		}
		assertTrue("Expecting bundle to be installed or resolved: " + tb1.getLocation(), (tb1.getState() & (Bundle.INSTALLED | Bundle.RESOLVED)) != 0);
		startCompositeBundle(composite);
		assertEquals("Expecting bundle to be active: " + tb1.getLocation(), Bundle.ACTIVE, tb1.getState());
		stopCompositeBundle(composite);
		assertEquals("Expecting bundle to be resolved: " + tb1.getLocation(), Bundle.RESOLVED, tb1.getState());
		startCompositeBundle(composite);
		assertEquals("Expecting bundle to be active: " + tb1.getLocation(), Bundle.ACTIVE, tb1.getState());
		uninstallCompositeBundle(composite);
	}

	public void testConstituentLifeCycle03() {
		// test create, install constituent
		// test scoped bundle events
		// finally uninstall
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
		Bundle tb1 = installConstituent(composite, "tb1", "tb1.jar");
		TestBundleListener compositeBundleListener = new SyncTestBundleListener(tb1, BundleEvent.STARTED | BundleEvent.STOPPED);
		TestBundleListener parentBundleListener = new SyncTestBundleListener(tb1, 0);
		getContext().addBundleListener(parentBundleListener);
		composite.getSystemBundleContext().addBundleListener(compositeBundleListener);
		try {
			startCompositeBundle(composite);
			tb1.start();
			stopCompositeBundle(composite);
			tb1.uninstall();
			BundleEvent[] parentEvents = (BundleEvent[]) parentBundleListener.getResults(new BundleEvent[0]);
			assertEquals("Wrong number of parent events", 0, parentEvents.length);
			BundleEvent[] compositeEvents = (BundleEvent[]) compositeBundleListener.getResults(new BundleEvent[2]);
			BundleEvent[] expectedEvents = new BundleEvent[2];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTED, tb1);
			expectedEvents[1] = new BundleEvent(BundleEvent.STOPPED, tb1);
			AbstractCompositeTestCase.compareEvents(expectedEvents, compositeEvents);
		} catch (BundleException e) {
			fail("Unexpected exception", e);
		} finally {
			getContext().removeBundleListener(parentBundleListener);
			composite.getSystemBundleContext().removeBundleListener(compositeBundleListener);
			uninstallCompositeBundle(composite);
		}
	}

	public void testConstituentLifeCycle04() {
		// test create, install framework extension constituent
		// finally uninstall
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
		try {
			installConstituent(composite, null, "tb7Frag.jar", true);
		} finally {
			uninstallCompositeBundle(composite);
		}
	}

	public void testConstituentLifeCycle05() {
		// test installing a bundle in a constituent using the same location
		// as another bundle installed in another constituent.
		CompositeBundle composite1 = createCompositeBundle(compAdmin, getName() + ".1", null, null);
		CompositeBundle composite2 = createCompositeBundle(compAdmin, getName() + ".2", null, null);
		installConstituent(composite1, "tb1", "tb1.jar");
		installConstituent(composite2, "tb1", "tb1.jar", true);
	}

	public void testFrameworkEvent01() {
		// Test scoping of framework events for STARTED and STARTLEVEL_CHAGED
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
		TestFrameworkListener compositeListener = new TestFrameworkListener(FrameworkEvent.STARTED | FrameworkEvent.STARTLEVEL_CHANGED);
		TestFrameworkListener parentListener= new TestFrameworkListener(FrameworkEvent.STARTED | FrameworkEvent.STARTLEVEL_CHANGED);
		getContext().addFrameworkListener(parentListener);
		composite.getSystemBundleContext().addFrameworkListener(compositeListener);
		try {
			startCompositeBundle(composite);
			FrameworkEvent[] compositeEvents = (FrameworkEvent[]) compositeListener.getResults(new FrameworkEvent[2]);
			FrameworkEvent[] expectedEvents = new FrameworkEvent[2];
			expectedEvents[0] = new FrameworkEvent(FrameworkEvent.STARTED, composite.getSystemBundleContext().getBundle(), null);
			expectedEvents[1] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, composite.getSystemBundleContext().getBundle(), null);
			AbstractCompositeTestCase.compareEvents(expectedEvents , compositeEvents);
			FrameworkEvent[] parentEvents = (FrameworkEvent[]) parentListener.getResults(new FrameworkEvent[0]);
			assertEquals("Wrong number of parent events", 0, parentEvents.length);
		} finally {
			composite.getSystemBundleContext().removeFrameworkListener(compositeListener);
			getContext().removeFrameworkListener(parentListener);
			uninstallCompositeBundle(composite);
		}
	}

	public void testFrameworkEvent02() {
		// Test scoping of framework events for STOPPED
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
		TestFrameworkListener compositeListener = new TestFrameworkListener(FrameworkEvent.STOPPED | FrameworkEvent.STOPPED_UPDATE | FrameworkEvent.STARTED);
		TestFrameworkListener parentListener= new TestFrameworkListener(FrameworkEvent.STOPPED | FrameworkEvent.STOPPED_UPDATE | FrameworkEvent.STARTED);
		getContext().addFrameworkListener(parentListener);
		try {
			startCompositeBundle(composite);
			composite.getSystemBundleContext().addFrameworkListener(compositeListener);

			Map manifest = createMap(composite.getHeaders(""));
			updateCompositeBundle(composite, manifest);
			FrameworkEvent[] expectedEvents = new FrameworkEvent[] {
					new FrameworkEvent(FrameworkEvent.STOPPED_UPDATE, composite.getSystemBundleContext().getBundle(), null),
					new FrameworkEvent(FrameworkEvent.STARTED, composite.getSystemBundleContext().getBundle(), null)
			};
			FrameworkEvent[] compositeEvents = (FrameworkEvent[]) compositeListener.getResults(new FrameworkEvent[expectedEvents.length]);
			AbstractCompositeTestCase.compareEvents(expectedEvents , compositeEvents);

			FrameworkEvent[] parentEvents = (FrameworkEvent[]) parentListener.getResults(new FrameworkEvent[0]);
			assertEquals("Wrong number of parent events", 0, parentEvents.length);

			stopCompositeBundle(composite);
			expectedEvents = new FrameworkEvent[] {
				new FrameworkEvent(FrameworkEvent.STOPPED, composite.getSystemBundleContext().getBundle(), null)
			};
			compositeEvents = (FrameworkEvent[]) compositeListener.getResults(new FrameworkEvent[expectedEvents.length]);
			AbstractCompositeTestCase.compareEvents(expectedEvents , compositeEvents);

			assertEquals("Wrong number of parent events", 0, parentEvents.length);	
		} finally {
			composite.getSystemBundleContext().removeFrameworkListener(compositeListener);
			getContext().removeFrameworkListener(parentListener);
			uninstallCompositeBundle(composite);
		}
	}

}
