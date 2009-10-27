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
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.composite.CompositeBundle;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.test.cases.composite.AbstractCompositeTestCase;

public class CompositeStartLevelTests extends AbstractCompositeTestCase {

	public void testStartLevel01() {
		// Test scoping of start-level service
		Map configuration1 = new HashMap();
		configuration1.put(Constants.FRAMEWORK_BEGINNING_STARTLEVEL, "3");
		CompositeBundle composite1 = createCompositeBundle(compAdmin, getName() + "_1", null, configuration1);

		Map configuration2 = new HashMap();
		configuration2.put(Constants.FRAMEWORK_BEGINNING_STARTLEVEL, "4");
		CompositeBundle composite2 = createCompositeBundle(compAdmin, getName() + "_2", null, configuration2);
	
		TestFrameworkListener composite1Listener = new TestFrameworkListener(FrameworkEvent.STARTLEVEL_CHANGED);
		TestFrameworkListener composite2Listener = new TestFrameworkListener(FrameworkEvent.STARTLEVEL_CHANGED);
		TestFrameworkListener parentListener= new TestFrameworkListener(FrameworkEvent.STARTLEVEL_CHANGED);

		StartLevel parentStartLevel = (StartLevel) getService(getContext(), StartLevel.class.getName());
		StartLevel composite1StartLevel = (StartLevel) getService(composite1.getSystemBundleContext(), StartLevel.class.getName());
		StartLevel composite2StartLevel = (StartLevel) getService(composite2.getSystemBundleContext(), StartLevel.class.getName());
		final int previousInitBundleStartLevel = parentStartLevel.getInitialBundleStartLevel();
		final int previousStartLevel = parentStartLevel.getStartLevel();
		if (previousInitBundleStartLevel != 2)
			parentStartLevel.setInitialBundleStartLevel(2);
		if (previousStartLevel != 1) {
			parentStartLevel.setStartLevel(1);
			try {
				// hack to wait for start-level change
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// nothing
			} 
		}

		composite1StartLevel.setInitialBundleStartLevel(4);
		composite2StartLevel.setInitialBundleStartLevel(5);
		try {
			startCompositeBundle(composite1);
			startCompositeBundle(composite2);

			Bundle parentTb1 = install("tb1.jar");
			Bundle composite1Tb1 = installConstituent(composite1, "tb1_1", "tb1.jar");
			Bundle composite2Tb1 = installConstituent(composite2, "tb1_2", "tb1.jar");

			assertEquals("Wrong start level for bundle: " + parentTb1.getLocation(), 2, parentStartLevel.getBundleStartLevel(parentTb1));
			assertEquals("Wrong start level for bundle: " + composite1Tb1.getLocation(), 4, composite1StartLevel.getBundleStartLevel(composite1Tb1));
			assertEquals("Wrong start level for bundle: " + composite2Tb1.getLocation(), 5, composite2StartLevel.getBundleStartLevel(composite2Tb1));

			try {
				parentTb1.start();
				composite1Tb1.start();
				composite2Tb1.start();
			} catch (BundleException e) {
				fail("Failed to start test bundles", e);
			}

			assertTrue("Bundle should not be started: " + parentTb1.getLocation(), Bundle.ACTIVE != parentTb1.getState());
			assertTrue("Bundle should not be started: " + composite1Tb1.getLocation(), Bundle.ACTIVE != composite1Tb1.getState());
			assertTrue("Bundle should not be started: " + composite2Tb1.getLocation(), Bundle.ACTIVE != composite2Tb1.getState());

			getContext().addFrameworkListener(parentListener);
			composite1.getSystemBundleContext().addFrameworkListener(composite1Listener);
			composite2.getSystemBundleContext().addFrameworkListener(composite2Listener);

			// increment start-level
			doTestStartLevel(parentStartLevel, getContext().getBundle(0).getBundleContext(), 2, parentTb1, parentListener, 
					new Bundle[] {composite1Tb1, composite2Tb1}, new TestListener[] {composite1Listener, composite2Listener});
			// decrement start-level
			doTestStartLevel(parentStartLevel, getContext().getBundle(0).getBundleContext(), 1, parentTb1, parentListener, 
					new Bundle[] {composite1Tb1, composite2Tb1}, new TestListener[] {composite1Listener, composite2Listener});

			// increment start-level
			doTestStartLevel(composite1StartLevel, composite1.getSystemBundleContext(), 4, composite1Tb1, composite1Listener, 
					new Bundle[] {parentTb1, composite2Tb1}, new TestListener[] {parentListener, composite2Listener});
			// decrement start-level
			doTestStartLevel(composite1StartLevel, composite1.getSystemBundleContext(), 1, composite1Tb1, composite1Listener, 
					new Bundle[] {parentTb1, composite2Tb1}, new TestListener[] {parentListener, composite2Listener});

			// increment start-level
			doTestStartLevel(composite2StartLevel, composite2.getSystemBundleContext(), 5, composite2Tb1, composite2Listener, 
					new Bundle[] {parentTb1, composite1Tb1}, new TestListener[] {parentListener, composite1Listener});
			// decrement start-level
			doTestStartLevel(composite2StartLevel, composite2.getSystemBundleContext(), 1, composite2Tb1, composite2Listener, 
					new Bundle[] {parentTb1, composite1Tb1}, new TestListener[] {parentListener, composite1Listener});
		} finally {
			parentStartLevel.setInitialBundleStartLevel(previousInitBundleStartLevel);
			parentStartLevel.setStartLevel(previousStartLevel);
			getContext().removeFrameworkListener(parentListener);
		}
	}

	public void testStartLevel02() {
		// Test start-level service with invalid bundles
		Map configuration1 = new HashMap();
		configuration1.put(Constants.FRAMEWORK_BEGINNING_STARTLEVEL, "3");
		CompositeBundle composite1 = createCompositeBundle(compAdmin, getName() + "_1", null, configuration1);

		StartLevel parentStartLevel = (StartLevel) getService(getContext(), StartLevel.class.getName());
		StartLevel composite1StartLevel = (StartLevel) getService(composite1.getSystemBundleContext(), StartLevel.class.getName());

		Bundle parentTb1 = install("tb1.jar");
		Bundle composite1Tb1 = installConstituent(composite1, "tb1_1", "tb1.jar");

		doTestInvalid(parentStartLevel, composite1Tb1);
		doTestInvalid(composite1StartLevel, parentTb1);
	}

	private void doTestStartLevel(StartLevel service, BundleContext slContext, int startLevel, Bundle activeBundle, TestListener activeListener, Bundle[] resolvedBundles, TestListener[] resolvedListeners) {
		int prevSL = service.getStartLevel();

			int activeBundleSL = service.getBundleStartLevel(activeBundle);
			int curFrameworkSL = service.getStartLevel();
			service.setStartLevel(startLevel);
	
			FrameworkEvent[] activeEvents = (FrameworkEvent[]) activeListener.getResults(new FrameworkEvent[1]);
			FrameworkEvent[] expectedEvents = new FrameworkEvent[1];
			expectedEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, slContext.getBundle(), null);
			AbstractCompositeTestCase.compareEvents(expectedEvents , activeEvents);

			if (activeBundleSL > curFrameworkSL && activeBundleSL <= startLevel)
				assertEquals("Bundle should be active: " + activeBundle.getLocation(), Bundle.ACTIVE, activeBundle.getState());
			else
				assertTrue("Bundle should not be active: " + activeBundle.getLocation(), Bundle.ACTIVE != activeBundle.getState());

			FrameworkEvent[] noEvents = new FrameworkEvent[0];
			for (int i = 0; i < resolvedListeners.length; i++) {
				FrameworkEvent[] actualEvents = (FrameworkEvent[]) resolvedListeners[i].getResults(noEvents);
				assertEquals("Wrong number of events", 0, actualEvents.length);
				assertTrue("Bundle should not be started: " + resolvedBundles[i].getLocation(), Bundle.ACTIVE != resolvedBundles[i].getState());
			}

	}

	private void doTestInvalid(StartLevel startLevel, Bundle bundle) {
		try {
			startLevel.isBundleActivationPolicyUsed(bundle);
			fail("Expected to fail with illegal argument: " + bundle.getLocation());
		} catch (IllegalArgumentException e) {
			// expected
		}
		try {
			startLevel.isBundlePersistentlyStarted(bundle);
			fail("Expected to fail with illegal argument: " + bundle.getLocation());
		} catch (IllegalArgumentException e) {
			// expected
		}
		try {
			startLevel.getBundleStartLevel(bundle);
			fail("Expected to fail with illegal argument: " + bundle.getLocation());
		} catch (IllegalArgumentException e) {
			// expected
		}
		try {
			startLevel.setBundleStartLevel(bundle, 1);
			fail("Expected to fail with illegal argument: " + bundle.getLocation());
		} catch (IllegalArgumentException e) {
			// expected
		}
	}
}
