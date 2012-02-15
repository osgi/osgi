/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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
package org.osgi.test.cases.subsystem.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.test.cases.subsystem.junit.SubsystemTest.Operation;



public class SharingPolicySubsystemTests extends SubsystemTest{
	// TestPlan item 3A1a - applications
	public void testBundleIsolationApplication() {
		doTestBundleIsolationScoped(SUBSYSTEM_SHARING_APPLICATION_A, true);
	}

	// TestPlan item 3A1a - composites
	public void testBundleIsolationComposite() {
		doTestBundleIsolationScoped(SUBSYSTEM_SHARING_COMPOSITE_B, true);
	}

	// TestPlan item 3A1b - features
	public void testBundleIsolationFeatures() {
		doTestBundleIsolationScoped(SUBSYSTEM_SHARING_FEATURE_C, false);
	}


	private void doTestBundleIsolationScoped(String subsystemName, boolean scopedTest) {
		Subsystem root = getRootSubsystem();
		Subsystem scoped;
		if (scopedTest) {
			scoped = doSubsystemInstall(getName(), root, getName(), subsystemName, false);
		} else {
			scoped = doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_EMPTY_COMPOSITE_A, false);
			doSubsystemInstall(getName(), scoped, "c", subsystemName, false);
		}

		BundleContext rootContext = root.getBundleContext();
		assertNotNull("The root context is null.", rootContext);
		TestBundleListener rootListener = new TestBundleListener();
		addBundleListener(rootContext, rootListener);

		BundleContext scopedContext = scoped.getBundleContext();
		assertNotNull("The scoped context is null.", scopedContext);
		TestBundleListener scopedListener = new TestBundleListener();
		addBundleListener(scopedContext, scopedListener);


		doSubsystemOperation("Could not start the scoped subsystem.", scoped, Operation.START, false);
		Bundle[] scopedBundles = scopedContext.getBundles();
		// Expecting context and a bundles
		assertEquals("Wrong number of bundles in scoped.", 2, scopedBundles.length);
		Bundle a = null;
		for (Bundle bundle : scopedBundles) {
			if (getSymbolicName(BUNDLE_NO_DEPS_A_V1).equals(bundle.getSymbolicName())) {
				a = bundle;
				break;
			}
		}
		assertNotNull("Could not find bundle a:", a);

		BundleContext aContext = a.getBundleContext();
		assertNotNull("aContext is null.", aContext);

		TestBundleListener aListener = new TestBundleListener();
		addBundleListener(aContext, aListener);

		Bundle c = doBundleInstall("Explicit bundle install to root.", rootContext, null, BUNDLE_NO_DEPS_C_V1, false);
		Bundle d = doBundleInstall("Explicit bundle install to scoped.", scopedContext, null, BUNDLE_NO_DEPS_D_V1, false);
		doBundleOperation("Bundle c", c, Operation.START, false);
		doBundleOperation("Bundle d", d, Operation.START, false);

		scopedBundles = scopedContext.getBundles();
		// Expecting context, a and d bundles
		assertEquals("Wrong number of bundles in scoped.", 3, scopedBundles.length);

		// Expecting the original root bundles + c
		Bundle[] rootBundles = rootContext.getBundles();
		assertEquals("Wrong number of bundles in root.", initialRootConstituents.size() + 1, rootBundles.length);

		rootListener.assertEvents("root events.", 
				Arrays.asList(
						new BundleEvent(BundleEvent.INSTALLED, c),
						new BundleEvent(BundleEvent.RESOLVED, c),
						new BundleEvent(BundleEvent.STARTING, c),
						new BundleEvent(BundleEvent.STARTED, c)
						));
		scopedListener.assertEvents("scoped events.",
				Arrays.asList(
						new BundleEvent(BundleEvent.RESOLVED, a),
						new BundleEvent(BundleEvent.STARTING, a),
						new BundleEvent(BundleEvent.STARTED, a),
						new BundleEvent(BundleEvent.INSTALLED, d),
						new BundleEvent(BundleEvent.RESOLVED, d),
						new BundleEvent(BundleEvent.STARTING, d),
						new BundleEvent(BundleEvent.STARTED, d)
						));
	}

	static class TestBundleListener implements SynchronousBundleListener {
		List<BundleEvent> events = new ArrayList<BundleEvent>();
		public void bundleChanged(BundleEvent event) {
			synchronized (events) {
				events.add(event);
			}
		}
		public void assertEvents(String message, List<BundleEvent> expected) {
			List<BundleEvent> actual;
			synchronized (events) {
				actual = new ArrayList<BundleEvent>(events);
			}
			assertEquals("Wrong number of events: " + message, expected.size(), actual.size());
			int size = actual.size();
			for (int i = 0; i < size; i++) {
				assertEquals("Wrong bundle at event index '" + i + ": " + message, expected.get(i).getBundle(), actual.get(i).getBundle());
				assertEquals("Wrong event type at event index '" + i + ": " + message, expected.get(i).getType(), actual.get(i).getType());
			}
		}
	}
}
