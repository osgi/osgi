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


import java.util.Dictionary;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.composite.CompositeAdmin;
import org.osgi.service.composite.CompositeBundle;
import org.osgi.test.support.OSGiTestCase;

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
		// TODO Need to decide of STOPPED_UPDATE FrameworkEvent makes sense for CompositeBundle.update operation
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
		TestFrameworkListener compositeListener = new TestFrameworkListener(FrameworkEvent.STOPPED | FrameworkEvent.STARTED);
		TestFrameworkListener parentListener= new TestFrameworkListener(FrameworkEvent.STOPPED | FrameworkEvent.STARTED);
		getContext().addFrameworkListener(parentListener);
		try {
			startCompositeBundle(composite);
			composite.getSystemBundleContext().addFrameworkListener(compositeListener);

			Map manifest = createMap(composite.getHeaders(""));
			updateCompositeBundle(composite, manifest);
			FrameworkEvent[] expectedEvents = new FrameworkEvent[] {
					new FrameworkEvent(FrameworkEvent.STOPPED, composite.getSystemBundleContext().getBundle(), null),
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
