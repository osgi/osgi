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
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.test.support.wiring.Wiring;



public class SharingPolicySubsystemTests extends SubsystemTest{
	// TestPlan item 3A1a - applications
	public void testBundleIsolationApplication() {
		doTestBundleIsolation(SUBSYSTEM_ISOLATE_APPLICATION_A, true);
	}

	// TestPlan item 3A1a - composites
	public void testBundleIsolationComposite() {
		doTestBundleIsolation(SUBSYSTEM_ISOLATE_COMPOSITE_B, true);
	}

	// TestPlan item 3A1b - features
	public void testBundleIsolationFeatures() {
		doTestBundleIsolation(SUBSYSTEM_ISOLATE_FEATURE_C, false);
	}


	private void doTestBundleIsolation(String subsystemName, boolean scopedTest) {
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
		assertEquals("Wrong number of bundles in scope.", 2, scopedBundles.length);

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

	// TestPlan item 3A2a - applications
	public void testServiceIsolationApplication() {
		doTestServiceIsolation(SUBSYSTEM_ISOLATE_APPLICATION_A, true);
	}

	// TestPlan item 3A2a - composites
	public void testServiceIsolationComposite() {
		doTestServiceIsolation(SUBSYSTEM_ISOLATE_COMPOSITE_B, true);
	}

	// TestPlan item 3A2b - features
	public void testServiceIsolationFeatures() {
		doTestServiceIsolation(SUBSYSTEM_ISOLATE_FEATURE_C, false);
	}

	private void doTestServiceIsolation(String subsystemName, boolean scopedTest) {
		Subsystem root = getRootSubsystem();
		Subsystem scoped;
		if (scopedTest) {
			scoped = doSubsystemInstall(getName(), root, getName(), subsystemName, false);
		} else {
			scoped = doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_EMPTY_COMPOSITE_A, false);
			doSubsystemInstall(getName(), scoped, getName() + ".feature", subsystemName, false);
		}

		BundleContext rootContext = root.getBundleContext();
		assertNotNull("The root context is null.", rootContext);

		BundleContext scopedContext = scoped.getBundleContext();
		assertNotNull("The scoped context is null.", scopedContext);


		doSubsystemOperation("Could not start the scoped subsystem.", scoped, Operation.START, false);
		Bundle[] scopedBundles = scopedContext.getBundles();
		// Expecting context and a bundles
		assertEquals("Wrong number of bundles in scope.", 2, scopedBundles.length);

		Bundle a = null;
		for (Bundle bundle : scopedBundles) {
			if (getSymbolicName(BUNDLE_NO_DEPS_A_V1).equals(bundle.getSymbolicName())) {
				a = bundle;
				break;
			}
		}
		assertNotNull("Could not find bundle a:", a);

		Bundle c = doBundleInstall("Explicit bundle install to root.", rootContext, null, BUNDLE_NO_DEPS_C_V1, false);
		Bundle d = doBundleInstall("Explicit bundle install to scoped.", scopedContext, null, BUNDLE_NO_DEPS_D_V1, false);
		doBundleOperation("Bundle c", c, Operation.START, false);
		doBundleOperation("Bundle d", d, Operation.START, false);

		BundleContext aContext = a.getBundleContext();
		assertNotNull("aContext is null.", aContext);

		BundleContext cContext = c.getBundleContext();
		assertNotNull("cContext is null.", cContext);

		BundleContext dContext = d.getBundleContext();
		assertNotNull("dContext is null.", dContext);

		String testNameFilter = "(testName=" + getName() + ")";
		TestServiceListener rootListener = new TestServiceListener();
		addServiceListener(rootContext, rootListener, "(&(objectClass=java.lang.Object)" + testNameFilter + ")");

		TestServiceListener scopedListener = new TestServiceListener();
		addServiceListener(scopedContext, scopedListener, "(&(objectClass=java.lang.Object)" + testNameFilter + ")");

		TestServiceListener aListener = new TestServiceListener();
		addServiceListener(aContext, aListener, "(&(objectClass=java.lang.Object)" + testNameFilter + ")");

		Hashtable<String, String> serviceProps1 = new Hashtable<String, String>();
		serviceProps1.put("testName", getName());
		Hashtable<String, String> serviceProps2 = new Hashtable<String, String>(serviceProps1);
		serviceProps2.put("testModify", "modified");

		ServiceRegistration<Object> rootService = cContext.registerService(Object.class, new Object(), serviceProps1);
		ServiceReference<Object> rootReference = rootService.getReference();
		rootService.setProperties(serviceProps2);


		ServiceRegistration<Object> scopedService = dContext.registerService(Object.class, new Object(), serviceProps1);
		ServiceReference<Object> scopedReference = scopedService.getReference();
		scopedService.setProperties(serviceProps2);

		checkService(rootContext, testNameFilter, rootReference);
		checkService(scopedContext, testNameFilter, scopedReference);
		checkService(aContext, testNameFilter, scopedReference);

		rootService.unregister();
		scopedService.unregister();

		rootListener.assertEvents("root events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, rootReference),
				new ServiceEvent(ServiceEvent.MODIFIED, rootReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, rootReference)));

		scopedListener.assertEvents("scoped events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, scopedReference),
				new ServiceEvent(ServiceEvent.MODIFIED, scopedReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, scopedReference)));

		aListener.assertEvents("constituent events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, scopedReference),
				new ServiceEvent(ServiceEvent.MODIFIED, scopedReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, scopedReference)));

	}

	// TestPlan item 3A3a - applications
	public void testPackageIsolationApplication() {
		doTestPackageIsolation(SUBSYSTEM_EMPTY_A, true);
	}

	// TestPlan item 3A3a - composites
	public void testPackageIsolationComposite() {
		doTestPackageIsolation(SUBSYSTEM_EMPTY_COMPOSITE_A, true);
	}

	// TestPlan item 3A3b - features
	public void testPackageIsolationFeatures() {
		doTestPackageIsolation(SUBSYSTEM_ISOLATE_PACKAGE_FEATURE_A, false);
	}

	private void doTestPackageIsolation(String subsystemName, boolean scopedTest) {
		Subsystem root = getRootSubsystem();
		Subsystem scoped;
		if (scopedTest) {
			scoped = doSubsystemInstall(getName(), root, getName(), subsystemName, false);
		} else {
			scoped = doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_EMPTY_COMPOSITE_A, false);
		}

		BundleContext rootContext = root.getBundleContext();
		assertNotNull("The root context is null.", rootContext);

		BundleContext scopedContext = scoped.getBundleContext();
		assertNotNull("The scoped context is null.", scopedContext);


		doSubsystemOperation("Could not start the scoped subsystem.", scoped, Operation.START, false);

		Bundle a = doBundleInstall(getName(), rootContext, null, BUNDLE_SHARE_A, false);
		Bundle c = doBundleInstall(getName(), scopedContext, null, BUNDLE_SHARE_C, false);

		Wiring.resolveBundles(getContext(), a, c);
		assertEquals("Wrong state for bundle A", Bundle.RESOLVED, a.getState());
		assertEquals("Wrong state for bundle C", Bundle.INSTALLED, c.getState());

		if (scopedTest) {
			Bundle f = doBundleInstall(getName(), scopedContext, null, BUNDLE_SHARE_F, false);
			Wiring.resolveBundles(getContext(), f);
			assertEquals("Wrong state for bundle F", Bundle.RESOLVED, f.getState());
		} else {
			Subsystem feature = doSubsystemInstall(getName(), scoped, getName() + ".feature", subsystemName, false);
			doSubsystemOperation("Starting feature.", feature, Operation.START, false);
		}
		Wiring.resolveBundles(getContext(), c);
		assertEquals("Wrong state for bundle C", Bundle.RESOLVED, c.getState());

	}

	// TestPlan item 3A4a - applications
	public void testRequireBundleIsolationApplication() {
		doTestRequireBundleIsolation(SUBSYSTEM_EMPTY_A, true);
	}

	// TestPlan item 3A4a - composites
	public void testRequireBundleIsolationComposite() {
		doTestRequireBundleIsolation(SUBSYSTEM_EMPTY_COMPOSITE_A, true);
	}

	// TestPlan item 3A4b - features
	public void testRequireIsolationFeatures() {
		doTestRequireBundleIsolation(SUBSYSTEM_ISOLATE_REQUIRE_BUNDLE_FEATURE_B, false);
	}

	private void doTestRequireBundleIsolation(String subsystemName, boolean scopedTest) {
		Subsystem root = getRootSubsystem();
		Subsystem scoped;
		if (scopedTest) {
			scoped = doSubsystemInstall(getName(), root, getName(), subsystemName, false);
		} else {
			scoped = doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_EMPTY_COMPOSITE_A, false);
		}

		BundleContext rootContext = root.getBundleContext();
		assertNotNull("The root context is null.", rootContext);

		BundleContext scopedContext = scoped.getBundleContext();
		assertNotNull("The scoped context is null.", scopedContext);


		doSubsystemOperation("Could not start the scoped subsystem.", scoped, Operation.START, false);

		Bundle a1 = doBundleInstall(getName(), rootContext, null, BUNDLE_SHARE_A, false);
		Bundle d = doBundleInstall(getName(), scopedContext, null, BUNDLE_SHARE_D, false);

		Wiring.resolveBundles(getContext(), a1, d);
		assertEquals("Wrong state for bundle A", Bundle.RESOLVED, a1.getState());
		assertEquals("Wrong state for bundle D", Bundle.INSTALLED, d.getState());

		if (scopedTest) {
			Bundle a2 = doBundleInstall(getName(), scopedContext, getName() + ".a2", BUNDLE_SHARE_A, false);
			Wiring.resolveBundles(getContext(), a2);
			assertEquals("Wrong state for bundle A2", Bundle.RESOLVED, a2.getState());
		} else {
			Subsystem feature = doSubsystemInstall(getName(), scoped, getName() + ".feature", subsystemName, false);
			doSubsystemOperation("Starting feature.", feature, Operation.START, false);
		}
		Wiring.resolveBundles(getContext(), d);
		assertEquals("Wrong state for bundle D", Bundle.RESOLVED, d.getState());
	}

	// TestPlan item 3A5a - applications
	public void testCapabilityIsolationApplication() {
		doTestCapabilityIsolation(SUBSYSTEM_EMPTY_A, true);
	}

	// TestPlan item 3A5a - composites
	public void testCapabilityIsolationComposite() {
		doTestCapabilityIsolation(SUBSYSTEM_EMPTY_COMPOSITE_A, true);
	}

	// TestPlan item 3A5b - features
	public void testCapabilityIsolationFeatures() {
		doTestCapabilityIsolation(SUBSYSTEM_ISOLATE_CAPABILITY_FEATURE_C, false);
	}

	private void doTestCapabilityIsolation(String subsystemName, boolean scopedTest) {
		Subsystem root = getRootSubsystem();
		Subsystem scoped;
		if (scopedTest) {
			scoped = doSubsystemInstall(getName(), root, getName(), subsystemName, false);
		} else {
			scoped = doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_EMPTY_COMPOSITE_A, false);
		}

		BundleContext rootContext = root.getBundleContext();
		assertNotNull("The root context is null.", rootContext);

		BundleContext scopedContext = scoped.getBundleContext();
		assertNotNull("The scoped context is null.", scopedContext);


		doSubsystemOperation("Could not start the scoped subsystem.", scoped, Operation.START, false);

		Bundle b = doBundleInstall(getName(), rootContext, null, BUNDLE_SHARE_B, false);
		Bundle e = doBundleInstall(getName(), scopedContext, null, BUNDLE_SHARE_E, false);

		Wiring.resolveBundles(getContext(), b, e);
		assertEquals("Wrong state for bundle B", Bundle.RESOLVED, b.getState());
		assertEquals("Wrong state for bundle E", Bundle.INSTALLED, e.getState());

		if (scopedTest) {
			Bundle g = doBundleInstall(getName(), scopedContext, getName() + ".g", BUNDLE_SHARE_G, false);
			Wiring.resolveBundles(getContext(), g);
			assertEquals("Wrong state for bundle G", Bundle.RESOLVED, g.getState());
		} else {
			Subsystem feature = doSubsystemInstall(getName(), scoped, getName() + ".feature", subsystemName, false);
			doSubsystemOperation("Starting feature.", feature, Operation.START, false);
		}
		Wiring.resolveBundles(getContext(), e);
		assertEquals("Wrong state for bundle E", Bundle.RESOLVED, e.getState());
	}

	// TestPlan item 3B1
	public void testImportService() {
		Subsystem root = getRootSubsystem();
		Subsystem composite = doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_IMPORT_SERVICE_COMPOSITE_A, false);


		BundleContext rootContext = root.getBundleContext();
		assertNotNull("The root context is null.", rootContext);

		BundleContext compositeContext = composite.getBundleContext();
		assertNotNull("The composite context is null.", compositeContext);


		doSubsystemOperation("Could not start the composite subsystem.", composite, Operation.START, false);
		Bundle[] scopedBundles = compositeContext.getBundles();
		// Expecting context and a bundles
		assertEquals("Wrong number of bundles in scope.", 2, scopedBundles.length);

		Bundle a = null;
		for (Bundle bundle : scopedBundles) {
			if (getSymbolicName(BUNDLE_NO_DEPS_A_V1).equals(bundle.getSymbolicName())) {
				a = bundle;
				break;
			}
		}
		assertNotNull("Could not find bundle a:", a);

		Bundle c = doBundleInstall("Explicit bundle install to root.", rootContext, null, BUNDLE_NO_DEPS_C_V1, false);
		Bundle d = doBundleInstall("Explicit bundle install to composite.", compositeContext, null, BUNDLE_NO_DEPS_D_V1, false);
		doBundleOperation("Bundle c", c, Operation.START, false);
		doBundleOperation("Bundle d", d, Operation.START, false);

		BundleContext aContext = a.getBundleContext();
		assertNotNull("aContext is null.", aContext);

		BundleContext cContext = c.getBundleContext();
		assertNotNull("cContext is null.", cContext);

		BundleContext dContext = d.getBundleContext();
		assertNotNull("dContext is null.", dContext);

		String testNameFilter = "(test=value)";
		TestServiceListener rootListener = new TestServiceListener();
		addServiceListener(rootContext, rootListener, "(&(objectClass=java.lang.Object)" + testNameFilter + ")");

		TestServiceListener scopedListener = new TestServiceListener();
		addServiceListener(compositeContext, scopedListener, "(&(objectClass=java.lang.Object)" + testNameFilter + ")");

		TestServiceListener aListener = new TestServiceListener();
		addServiceListener(aContext, aListener, "(&(objectClass=java.lang.Object)" + testNameFilter + ")");

		Hashtable<String, String> serviceProps1 = new Hashtable<String, String>();
		serviceProps1.put("test", "value");
		Hashtable<String, String> serviceProps2 = new Hashtable<String, String>(serviceProps1);
		serviceProps2.put("testModify", "modified");

		ServiceRegistration<Object> rootService = cContext.registerService(Object.class, new Object(), serviceProps1);
		ServiceReference<Object> rootReference = rootService.getReference();
		rootService.setProperties(serviceProps2);


		ServiceRegistration<Object> scopedService = dContext.registerService(Object.class, new Object(), serviceProps1);
		ServiceReference<Object> scopedReference = scopedService.getReference();
		scopedService.setProperties(serviceProps2);

		checkService(rootContext, testNameFilter, rootReference);
		checkService(compositeContext, testNameFilter, scopedReference);
		checkService(aContext, testNameFilter, scopedReference);

		rootService.unregister();
		scopedService.unregister();

		rootListener.assertEvents("root events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, rootReference),
				new ServiceEvent(ServiceEvent.MODIFIED, rootReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, rootReference)));

		scopedListener.assertEvents("scoped events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, rootReference),
				new ServiceEvent(ServiceEvent.MODIFIED, rootReference),
				new ServiceEvent(ServiceEvent.REGISTERED, scopedReference),
				new ServiceEvent(ServiceEvent.MODIFIED, scopedReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, rootReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, scopedReference)));

		aListener.assertEvents("constituent events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, rootReference),
				new ServiceEvent(ServiceEvent.MODIFIED, rootReference),
				new ServiceEvent(ServiceEvent.REGISTERED, scopedReference),
				new ServiceEvent(ServiceEvent.MODIFIED, scopedReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, rootReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, scopedReference)));

	}

	private void checkService(BundleContext context, String filter, ServiceReference<Object> reference) {
		Collection<ServiceReference<Object>> services = null;
		try {
			services = context.getServiceReferences(Object.class, filter);
		} catch (InvalidSyntaxException e) {
			fail("Invalid filter.", e);
		}
		assertEquals("Wrong number of services.", 1, services.size());
		assertEquals("Wrong service found.", reference, services.iterator().next());
	}

	static class TestBundleListener implements SynchronousBundleListener {
		private List<BundleEvent> events = new ArrayList<BundleEvent>();
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

	static class TestServiceListener implements ServiceListener {
		private List<ServiceEvent> events = new ArrayList<ServiceEvent>();
		public void serviceChanged(ServiceEvent event) {
			synchronized (events) {
				events.add(event);
			}
		}

		public void assertEvents(String message, List<ServiceEvent> expected) {
			List<ServiceEvent> actual;
			synchronized (events) {
				actual = new ArrayList<ServiceEvent>(events);
			}
			assertEquals("Wrong number of events: " + message, expected.size(), actual.size());
			int size = actual.size();
			for (int i = 0; i < size; i++) {
				assertEquals("Wrong service reference at event index '" + i + ": " + message, expected.get(i).getServiceReference(), actual.get(i).getServiceReference());
				assertEquals("Wrong event type at event index '" + i + ": " + message, expected.get(i).getType(), actual.get(i).getType());
			}
		}
	}
}
