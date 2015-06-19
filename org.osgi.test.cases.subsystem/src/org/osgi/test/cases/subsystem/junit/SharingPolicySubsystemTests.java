/*
 * Copyright (c) OSGi Alliance (2012, 2014). All Rights Reserved.
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
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.namespace.ExecutionEnvironmentNamespace;
import org.osgi.framework.namespace.NativeNamespace;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.test.support.wiring.Wiring;



public class SharingPolicySubsystemTests extends SubsystemTest{
	// TestPlan item 3A1a - applications
	public void test3A1a_BundleIsolationApplication() {
		doTestBundleIsolation(SUBSYSTEM_ISOLATE_APPLICATION_A, true);
	}

	// TestPlan item 3A1a - composites
	public void test3A1a_BundleIsolationComposite() {
		doTestBundleIsolation(SUBSYSTEM_ISOLATE_COMPOSITE_B, true);
	}

	// TestPlan item 3A1b - features
	public void test3A1b_BundleIsolationFeatures() {
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
	public void test3A2a_ServiceIsolationApplication() {
		doTestServiceIsolation(SUBSYSTEM_ISOLATE_APPLICATION_A, true);
	}

	// TestPlan item 3A2a - composites
	public void test3A2a_ServiceIsolationComposite() {
		doTestServiceIsolation(SUBSYSTEM_ISOLATE_COMPOSITE_B, true);
	}

	// TestPlan item 3A2b - features
	public void test3A2b_ServiceIsolationFeatures() {
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
	public void test3A3a_PackageIsolationApplication() {
		doTestPackageIsolation(SUBSYSTEM_EMPTY_A, true);
	}

	// TestPlan item 3A3a - composites
	public void test3A3a_PackageIsolationComposite() {
		doTestPackageIsolation(SUBSYSTEM_EMPTY_COMPOSITE_A, true);
	}

	// TestPlan item 3A3b - features
	public void test3A3b_PackageIsolationFeatures() {
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
	public void test3A4a_RequireBundleIsolationApplication() {
		doTestRequireBundleIsolation(SUBSYSTEM_EMPTY_A, true);
	}

	// TestPlan item 3A4a - composites
	public void test3A4a_RequireBundleIsolationComposite() {
		doTestRequireBundleIsolation(SUBSYSTEM_EMPTY_COMPOSITE_A, true);
	}

	// TestPlan item 3A4b - features
	public void test3A4b_RequireIsolationFeatures() {
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
	public void test3A5a_CapabilityIsolationApplication() {
		doTestCapabilityIsolation(SUBSYSTEM_EMPTY_A, true);
	}

	// TestPlan item 3A5a - composites
	public void test3A5a_CapabilityIsolationComposite() {
		doTestCapabilityIsolation(SUBSYSTEM_EMPTY_COMPOSITE_A, true);
	}

	// TestPlan item 3A5b - features
	public void test3A5b_CapabilityIsolationFeatures() {
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
	public void test3B1_ImportService() {
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
		checkService(compositeContext, testNameFilter, rootReference, scopedReference);
		checkService(aContext, testNameFilter, rootReference, scopedReference);

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

	// TestPlan item 3B2
	public void test3B2_ImportPackage() {
		doTestImportPolicy(SUBSYSTEM_IMPORT_PACKAGE_COMPOSITE_A, BUNDLE_SHARE_A, BUNDLE_SHARE_C);
	}

	// TestPlan item 3B3
	public void test3B3_RequireBundle() {
		doTestImportPolicy(SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_A, BUNDLE_SHARE_A, BUNDLE_SHARE_D);
	}

	// TestPlan item 3B4
	public void test3B4_RequireCapability() {
		doTestImportPolicy(SUBSYSTEM_REQUIRE_CAPABILITY_COMPOSITE_A, BUNDLE_SHARE_B, BUNDLE_SHARE_E);
	}

	private void doTestImportPolicy(String subsystemName, String providerName, String requirerName) {
		Subsystem root = getRootSubsystem();
		Subsystem composite = doSubsystemInstall(getName(), root, getName(), subsystemName, false);

		BundleContext rootContext = root.getBundleContext();
		assertNotNull("The root context is null.", rootContext);

		BundleContext compositeContext = composite.getBundleContext();
		assertNotNull("The composite context is null.", compositeContext);


		doSubsystemOperation("Could not start the composite subsystem.", composite, Operation.START, false);

		Bundle provider = doBundleInstall(getName(), rootContext, null, providerName, false);
		Bundle requirer = doBundleInstall(getName(), compositeContext, null, requirerName, false);

		Wiring.resolveBundles(getContext(), provider, requirer);
		assertEquals("Wrong state for bundle: " + providerName, Bundle.RESOLVED, provider.getState());
		assertEquals("Wrong state for bundle: " + requirerName, Bundle.RESOLVED, requirer.getState());
	}

	// testPlan item 3B5
	public void test3B5_FragmentHost() {
		Subsystem root = getRootSubsystem();
		Subsystem composite = doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_A, false);
		doSubsystemOperation("Could not start the composite subsystem.", composite, Operation.START, false);

		BundleContext rootContext = root.getBundleContext();
		assertNotNull("The root context is null.", rootContext);

		BundleContext compositeContext = composite.getBundleContext();
		assertNotNull("The composite context is null.", compositeContext);

		Bundle host = doBundleInstall(getName(), rootContext, null, BUNDLE_SHARE_A, false);
		Bundle fragment =  doBundleInstall(getName(), compositeContext, null, BUNDLE_SHARE_H, false);

		Wiring.resolveBundles(getContext(), host, fragment);
		assertEquals("Wrong state for bundle: " + host.getSymbolicName(), Bundle.RESOLVED, host.getState());
		assertEquals("Wrong state for bundle: " + fragment.getSymbolicName(), Bundle.INSTALLED, fragment.getState());
	}

	// TestPlan item 3C1
	public void test3C1_ExportService() {
		Subsystem root = getRootSubsystem();
		Subsystem composite = doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_EXPORT_SERVICE_COMPOSITE_A, false);


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

		checkService(rootContext, testNameFilter, rootReference, scopedReference);
		checkService(compositeContext, testNameFilter, scopedReference);
		checkService(aContext, testNameFilter, scopedReference);

		rootService.unregister();
		scopedService.unregister();


		scopedListener.assertEvents("scoped events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, scopedReference),
				new ServiceEvent(ServiceEvent.MODIFIED, scopedReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, scopedReference)));

		aListener.assertEvents("constituent events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, scopedReference),
				new ServiceEvent(ServiceEvent.MODIFIED, scopedReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, scopedReference)));

		rootListener.assertEvents("root events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, rootReference),
				new ServiceEvent(ServiceEvent.MODIFIED, rootReference),
				new ServiceEvent(ServiceEvent.REGISTERED, scopedReference),
				new ServiceEvent(ServiceEvent.MODIFIED, scopedReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, rootReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, scopedReference)));

	}

	// TestPlan item 3C2
	public void test3C2_ExportPackage() {
		doTestExportPolicy(SUBSYSTEM_EXPORT_PACKAGE_COMPOSITE_A, BUNDLE_SHARE_A, BUNDLE_SHARE_C);
	}

	// TestPlan item 3C3
	public void test3C3_ExportCapability() {
		doTestExportPolicy(SUBSYSTEM_PROVIDE_CAPABILITY_COMPOSITE_A, BUNDLE_SHARE_B, BUNDLE_SHARE_E);
	}

	private void doTestExportPolicy(String subsystemName, String providerName, String requirerName) {
		Subsystem root = getRootSubsystem();
		Subsystem composite = doSubsystemInstall(getName(), root, getName(), subsystemName, false);

		BundleContext rootContext = root.getBundleContext();
		assertNotNull("The root context is null.", rootContext);

		BundleContext compositeContext = composite.getBundleContext();
		assertNotNull("The composite context is null.", compositeContext);


		doSubsystemOperation("Could not start the composite subsystem.", composite, Operation.START, false);

		Bundle provider = doBundleInstall(getName(), compositeContext, null, providerName, false);
		Bundle requirer = doBundleInstall(getName(), rootContext, null, requirerName, false);

		Wiring.resolveBundles(getContext(), provider, requirer);
		assertEquals("Wrong state for bundle: " + providerName, Bundle.RESOLVED, provider.getState());
		assertEquals("Wrong state for bundle: " + requirerName, Bundle.RESOLVED, requirer.getState());
	}

	// TestPlan item 3D1
	public void test3D1_ExportImportServcie() {
		Subsystem root = getRootSubsystem();
		Subsystem export1 = doSubsystemInstall(getName(), root, SUBSYSTEM_EXPORT_SERVICE_COMPOSITE_A, SUBSYSTEM_EXPORT_SERVICE_COMPOSITE_A, false);
		Subsystem export2 = doSubsystemInstall(getName(), export1, SUBSYSTEM_EXPORT_SERVICE_COMPOSITE_B, SUBSYSTEM_EXPORT_SERVICE_COMPOSITE_B, false);
		Subsystem import1 = doSubsystemInstall(getName(), root, SUBSYSTEM_IMPORT_SERVICE_COMPOSITE_A, SUBSYSTEM_IMPORT_SERVICE_COMPOSITE_A, false);
		Subsystem import2 = doSubsystemInstall(getName(), import1, SUBSYSTEM_IMPORT_SERVICE_COMPOSITE_B, SUBSYSTEM_IMPORT_SERVICE_COMPOSITE_B, false);


		BundleContext rootContext = root.getBundleContext();
		assertNotNull("The root context is null.", rootContext);

		doSubsystemOperation("Could not start the composite subsystem.", export1, Operation.START, false);
		doSubsystemOperation("Could not start the composite subsystem.", export2, Operation.START, false);
		doSubsystemOperation("Could not start the composite subsystem.", import1, Operation.START, false);
		doSubsystemOperation("Could not start the composite subsystem.", import2, Operation.START, false);

		Bundle rootA = doBundleInstall("Explicit bundle install to root.", rootContext, null, BUNDLE_NO_DEPS_A_V1, false);
		doBundleOperation("Bundle rootA", rootA, Operation.START, false);

		Bundle export1A = getBundle(export1, BUNDLE_NO_DEPS_A_V1);
		Bundle export2A = getBundle(export2, BUNDLE_NO_DEPS_A_V1);
		Bundle import1A = getBundle(import1, BUNDLE_NO_DEPS_A_V1);
		Bundle import2A = getBundle(import2, BUNDLE_NO_DEPS_A_V1);

		BundleContext rootAContext = rootA.getBundleContext();
		assertNotNull("rootAContext is null.", rootAContext);
		BundleContext export1AContext = export1A.getBundleContext();
		assertNotNull("export1AContext is null.", export1AContext);
		BundleContext export2AContext = export2A.getBundleContext();
		assertNotNull("export2AContext is null.", export2AContext);
		BundleContext import1AContext = import1A.getBundleContext();
		assertNotNull("import1AContext is null.", import1AContext);
		BundleContext import2AContext = import2A.getBundleContext();
		assertNotNull("import2AContext is null.", import2AContext);

		String testNameFilter = "(test=value)";
		TestServiceListener rootListener = new TestServiceListener();
		addServiceListener(rootAContext, rootListener, "(&(objectClass=java.lang.Object)" + testNameFilter + ")");

		TestServiceListener export1Listener = new TestServiceListener();
		addServiceListener(export1AContext, export1Listener, "(&(objectClass=java.lang.Object)" + testNameFilter + ")");

		TestServiceListener export2Listener = new TestServiceListener();
		addServiceListener(export2AContext, export2Listener, "(&(objectClass=java.lang.Object)" + testNameFilter + ")");

		TestServiceListener import1Listener = new TestServiceListener();
		addServiceListener(import1AContext, import1Listener, "(&(objectClass=java.lang.Object)" + testNameFilter + ")");

		TestServiceListener import2Listener = new TestServiceListener();
		addServiceListener(import2AContext, import2Listener, "(&(objectClass=java.lang.Object)" + testNameFilter + ")");

		Hashtable<String, String> serviceProps1 = new Hashtable<String, String>();
		serviceProps1.put("test", "value");
		Hashtable<String, String> serviceProps2 = new Hashtable<String, String>(serviceProps1);
		serviceProps2.put("testModify", "modified");

		ServiceRegistration<Object> rootService = rootAContext.registerService(Object.class, new Object(), serviceProps1);
		ServiceReference<Object> rootReference = rootService.getReference();
		rootService.setProperties(serviceProps2);

		ServiceRegistration<Object> export1Service = export1AContext.registerService(Object.class, new Object(), serviceProps1);
		ServiceReference<Object> export1Reference = export1Service.getReference();
		export1Service.setProperties(serviceProps2);

		ServiceRegistration<Object> export2Service = export2AContext.registerService(Object.class, new Object(), serviceProps1);
		ServiceReference<Object> export2Reference = export2Service.getReference();
		export2Service.setProperties(serviceProps2);

		checkService(export2AContext, testNameFilter, export2Reference);
		checkService(export1AContext, testNameFilter, export1Reference, export2Reference);
		checkService(rootAContext, testNameFilter, rootReference, export1Reference, export2Reference);
		checkService(import1AContext, testNameFilter, rootReference, export1Reference, export2Reference);
		checkService(import2AContext, testNameFilter, rootReference, export1Reference, export2Reference);

		rootService.unregister();
		export1Service.unregister();
		export2Service.unregister();


		export2Listener.assertEvents("scoped events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, export2Reference),
				new ServiceEvent(ServiceEvent.MODIFIED, export2Reference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, export2Reference)));

		export1Listener.assertEvents("constituent events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, export1Reference),
				new ServiceEvent(ServiceEvent.MODIFIED, export1Reference),
				new ServiceEvent(ServiceEvent.REGISTERED, export2Reference),
				new ServiceEvent(ServiceEvent.MODIFIED, export2Reference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, export1Reference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, export2Reference)));

		rootListener.assertEvents("root events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, rootReference),
				new ServiceEvent(ServiceEvent.MODIFIED, rootReference),
				new ServiceEvent(ServiceEvent.REGISTERED, export1Reference),
				new ServiceEvent(ServiceEvent.MODIFIED, export1Reference),
				new ServiceEvent(ServiceEvent.REGISTERED, export2Reference),
				new ServiceEvent(ServiceEvent.MODIFIED, export2Reference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, rootReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, export1Reference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, export2Reference)));

		import1Listener.assertEvents("constituent events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, rootReference),
				new ServiceEvent(ServiceEvent.MODIFIED, rootReference),
				new ServiceEvent(ServiceEvent.REGISTERED, export1Reference),
				new ServiceEvent(ServiceEvent.MODIFIED, export1Reference),
				new ServiceEvent(ServiceEvent.REGISTERED, export2Reference),
				new ServiceEvent(ServiceEvent.MODIFIED, export2Reference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, rootReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, export1Reference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, export2Reference)));

		import2Listener.assertEvents("constituent events.", Arrays.asList(
				new ServiceEvent(ServiceEvent.REGISTERED, rootReference),
				new ServiceEvent(ServiceEvent.MODIFIED, rootReference),
				new ServiceEvent(ServiceEvent.REGISTERED, export1Reference),
				new ServiceEvent(ServiceEvent.MODIFIED, export1Reference),
				new ServiceEvent(ServiceEvent.REGISTERED, export2Reference),
				new ServiceEvent(ServiceEvent.MODIFIED, export2Reference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, rootReference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, export1Reference),
				new ServiceEvent(ServiceEvent.UNREGISTERING, export2Reference)));
	}
	// TestPlan item 3D2
	public void test3D2_ExportImportPackage() {
		doTestMultiLevelSharing(
				SUBSYSTEM_EXPORT_PACKAGE_COMPOSITE_A, SUBSYSTEM_EXPORT_PACKAGE_COMPOSITE_B,
				SUBSYSTEM_IMPORT_PACKAGE_COMPOSITE_A, SUBSYSTEM_IMPORT_PACKAGE_COMPOSITE_B,
				BUNDLE_SHARE_A, BUNDLE_SHARE_C);
	}
	
	// TestPlan item 3D3
	public void test3D3_MultiLevelRequire() {
		Subsystem root = getRootSubsystem();

		Subsystem subsystemR1 = doSubsystemInstall(getName(), root, SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_A, SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_A, false);
		Subsystem subsystemR2 = doSubsystemInstall(getName(), subsystemR1, SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_B, SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_B, false);

		BundleContext rootContext = root.getBundleContext();
		assertNotNull("The root context is null.", rootContext);

		BundleContext r1Context = subsystemR1.getBundleContext();
		assertNotNull("The composite context is null.", r1Context);
		BundleContext r2Context = subsystemR2.getBundleContext();
		assertNotNull("The composite context is null.", r2Context);

		doSubsystemOperation("Could not start the composite subsystem.", subsystemR1, Operation.START, false);
		doSubsystemOperation("Could not start the composite subsystem.", subsystemR2, Operation.START, false);

		Bundle provider = doBundleInstall(getName(), rootContext, null, BUNDLE_SHARE_A, false);
		Bundle requirer = doBundleInstall(getName(), r2Context, null, BUNDLE_SHARE_D, false);

		Wiring.resolveBundles(getContext(), provider, requirer);
		assertEquals("Wrong state for bundle: " + BUNDLE_SHARE_A, Bundle.RESOLVED, provider.getState());
		assertEquals("Wrong state for bundle: " + BUNDLE_SHARE_D, Bundle.RESOLVED, requirer.getState());

		doBundleOperation("uninstall: " + BUNDLE_SHARE_D, requirer, Operation.UNINSTALL, false);
		requirer = doBundleInstall(getName(), r1Context, null, BUNDLE_SHARE_D, false);

		Wiring.resolveBundles(getContext(), requirer);
		assertEquals("Wrong state for bundle: " + BUNDLE_SHARE_D, Bundle.RESOLVED, requirer.getState());
	}

	// TestPlan item 3D4
	public void test3D4_ProvideRequireCapability() {
		doTestMultiLevelSharing(
				SUBSYSTEM_PROVIDE_CAPABILITY_COMPOSITE_A, SUBSYSTEM_PROVIDE_CAPABILITY_COMPOSITE_B,
				SUBSYSTEM_REQUIRE_CAPABILITY_COMPOSITE_A, SUBSYSTEM_REQUIRE_CAPABILITY_COMPOSITE_B,
				BUNDLE_SHARE_B, BUNDLE_SHARE_E);
	}

	private void doTestMultiLevelSharing(
			String subsystemProvider1, String subsystemProvider2, 
			String subsystemRequirer1,String subsystemRequirer2, 
			String bundleProvider, String bundleRequirer) {
		Subsystem root = getRootSubsystem();
		Subsystem subsystemP1 = doSubsystemInstall(getName(), root, subsystemProvider1, subsystemProvider1, false);
		Subsystem subsystemP2 = doSubsystemInstall(getName(), subsystemP1, subsystemProvider2, subsystemProvider2, false);

		Subsystem subsystemR1 = doSubsystemInstall(getName(), root, subsystemRequirer1, subsystemRequirer1, false);
		Subsystem subsystemR2 = doSubsystemInstall(getName(), subsystemR1, subsystemRequirer2, subsystemRequirer2, false);

		BundleContext rootContext = root.getBundleContext();
		assertNotNull("The root context is null.", rootContext);

		BundleContext p1Context = subsystemP1.getBundleContext();
		assertNotNull("The composite context is null.", p1Context);
		BundleContext p2Context = subsystemP2.getBundleContext();
		assertNotNull("The composite context is null.", p2Context);
		BundleContext r1Context = subsystemR1.getBundleContext();
		assertNotNull("The composite context is null.", r1Context);
		BundleContext r2Context = subsystemR2.getBundleContext();
		assertNotNull("The composite context is null.", r2Context);


		doSubsystemOperation("Could not start the composite subsystem.", subsystemP1, Operation.START, false);
		doSubsystemOperation("Could not start the composite subsystem.", subsystemP2, Operation.START, false);
		doSubsystemOperation("Could not start the composite subsystem.", subsystemR1, Operation.START, false);
		doSubsystemOperation("Could not start the composite subsystem.", subsystemR2, Operation.START, false);

		Bundle provider = doBundleInstall(getName(), p2Context, null, bundleProvider, false);
		Bundle requirer = doBundleInstall(getName(), r2Context, null, bundleRequirer, false);

		Wiring.resolveBundles(getContext(), provider, requirer);
		assertEquals("Wrong state for bundle: " + bundleProvider, Bundle.RESOLVED, provider.getState());
		assertEquals("Wrong state for bundle: " + bundleRequirer, Bundle.RESOLVED, requirer.getState());

		doBundleOperation("uninstall: " + bundleRequirer, requirer, Operation.UNINSTALL, false);
		requirer = doBundleInstall(getName(), r1Context, null, bundleRequirer, false);

		Wiring.resolveBundles(getContext(), requirer);
		assertEquals("Wrong state for bundle: " + bundleRequirer, Bundle.RESOLVED, requirer.getState());

		doBundleOperation("uninstall: " + bundleRequirer, requirer, Operation.UNINSTALL, false);
		requirer = doBundleInstall(getName(), p1Context, null, bundleRequirer, false);

		Wiring.resolveBundles(getContext(), requirer);
		assertEquals("Wrong state for bundle: " + bundleRequirer, Bundle.RESOLVED, requirer.getState());
	}

	// Test that application subsystems get implicit access to osgi.ee and
	// osgi.native namespaces
	public void testImplicitAccessToEEandNativeCapability_app() {
		doTestImplicitAccessToEEandNativeCapability(SUBSYSTEM_EMPTY_A);
	}

	// Test that composite subsystems get implicit access to osgi.ee and
	// osgi.native namespaces
	public void testImplicitAccessToEEandNativeCapability_comp() {
		doTestImplicitAccessToEEandNativeCapability(SUBSYSTEM_EMPTY_COMPOSITE_A);
	}

	private void doTestImplicitAccessToEEandNativeCapability(String subsystemName) {
		Subsystem root = getRootSubsystem();

		Subsystem subsystemApp = doSubsystemInstall(getName(), root, subsystemName, subsystemName, false);

		BundleContext appContext = subsystemApp.getBundleContext();
		assertNotNull("The subsystem context is null.", appContext);

		doSubsystemOperation("Could not start the subsystem.", subsystemApp, Operation.START, false);

		Bundle b1 = doBundleInstall(getName(), appContext, BUNDLE_REQUIRE_EE_NATIVE + 1, BUNDLE_REQUIRE_EE_NATIVE,
				false);

		Wiring.resolveBundles(getContext(), b1);
		assertEquals("Wrong state for bundle: " + b1, Bundle.RESOLVED, b1.getState());
		BundleWiring b1Wiring = b1.adapt(BundleWiring.class);
		assertNotNull("No wiring found.", b1Wiring);
		List<BundleWire> eeWires = b1Wiring
				.getRequiredWires(ExecutionEnvironmentNamespace.EXECUTION_ENVIRONMENT_NAMESPACE);
		assertEquals("Wrong number of ee wires.", 2, eeWires.size());
		List<BundleWire> nativeWires = b1Wiring.getRequiredWires(NativeNamespace.NATIVE_NAMESPACE);
		assertEquals("Wrong number of native wires.", 1, nativeWires.size());
		List<BundleWire> wires = eeWires;
		wires.addAll(nativeWires);
		BundleRevision systemRevision = getContext().getBundle(Constants.SYSTEM_BUNDLE_LOCATION).adapt(
				BundleRevision.class);
		for (BundleWire wire : wires) {
			assertEquals("Wrong provider of " + wire.getCapability().getNamespace(), systemRevision, wire.getProvider());
		}
	}

	private void checkService(BundleContext context, String filter, ServiceReference<?>... references) {
		Collection<ServiceReference<Object>> services = null;
		try {
			services = context.getServiceReferences(Object.class, filter);
		} catch (InvalidSyntaxException e) {
			fail("Invalid filter.", e);
		}
		assertEquals("Wrong number of services.", references.length, services.size());
		for (ServiceReference<?> reference : references) {
			assertTrue("Could not get expected service reference: " + reference, services.contains(reference));
		}
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
