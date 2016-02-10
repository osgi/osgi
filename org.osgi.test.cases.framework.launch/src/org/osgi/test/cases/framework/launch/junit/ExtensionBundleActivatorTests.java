/*
 * Copyright (c) OSGi Alliance (2004, 2013). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.framework.launch.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.namespace.HostNamespace;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.wiring.FrameworkWiring;

public class ExtensionBundleActivatorTests extends LaunchTest {
	private static final String RESULTS = "org.osgi.tests.cases.framework.launch.results";
	private static final String TB1_TEST = "org.osgi.test.cases.framework.launch.extensions.tb1";
	private static final String TB2_TEST = "org.osgi.test.cases.framework.launch.extensions.tb2";
	private static final String TB3_TEST = "org.osgi.test.cases.framework.launch.extensions.tb3";

	private static Framework framework = null;
	private static Map<String, List<String>> POST_INIT_RESULTS;
	private final Collection<String> extensions = Collections
			.unmodifiableCollection(Arrays.asList("/extensions.tb3.jar",
					"/extensions.tb2.jar", "/extensions.tb1.jar"));

	public void setUp() throws Exception {
		super.setUp();
		if (framework != null) {
			return;
		}
		// Initialize an empty framework
		Map<String, String> config = getConfiguration("ExtensionBundleActivatorTests");
		config.put(Constants.FRAMEWORK_STORAGE_CLEAN,
				Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
		framework = createFramework(config);
		initFramework(framework);

		// install the test extension bundles
		Collection<Bundle> extensionBundles = new ArrayList<Bundle>();
		for (String extension : extensions) {
			extensionBundles.add(installBundle(framework, extension));
		}
		framework.adapt(FrameworkWiring.class).resolveBundles(extensionBundles);
		stopFramework(framework);

		// save the post init results for testing
		@SuppressWarnings("unchecked")
		Map<String,List<String>> castResults = (Map<String,List<String>>) System
				.getProperties()
				.remove(RESULTS);
		POST_INIT_RESULTS = castResults;
		return;
	}

	public void tearDown() {
		System.getProperties().remove(RESULTS);
		stopFramework(framework);
	}

	private static List<String> getResults(String test) {
		@SuppressWarnings("unchecked")
		Map<String, List<String>> resultMap = (Map<String, List<String>>) System
				.getProperties().get(RESULTS);
		if (resultMap == null) {
			resultMap = new HashMap<String, List<String>>();
			System.getProperties().put(RESULTS, resultMap);
		}

		List<String> results = resultMap.get(test);
		if (results == null) {
			results = new ArrayList<String>();
			resultMap.put(test, results);
		}
		return results;
	}

	public void testPostInit() {
		assertNotNull("No results found for test.", POST_INIT_RESULTS);

		List<String> tb1Results = POST_INIT_RESULTS.get(TB1_TEST);
		List<String> tb2Results = POST_INIT_RESULTS.get(TB2_TEST);
		List<String> tb3Results = POST_INIT_RESULTS.get(TB3_TEST);

		assertNotNull("No results found for tb1", tb1Results);
		assertNotNull("No results found for tb2", tb2Results);
		assertNotNull("No results found for tb3", tb3Results);

		List<String> expectedTests = Arrays.asList(
				"START",
				"SYSTEM_CONTEXT",
				"SYSTEM_STARTING",
				"FRAMEWORK_WIRING",
				"FRAMEWORK_WIRING_RESOLVED",
				"FRAMEWORK_START_LEVEL",
				"FRAMEWORK_START_LEVEL_ZERO",
				"FRAMEWORK_START_LEVEL_EXCEPTION",
				"FRAMEWORK_START_LEVEL_INIT_BSL",
				"STOP");
		assertTestResults(expectedTests, tb1Results);

		expectedTests = Arrays.asList("START");
		assertTestResults(expectedTests, tb2Results);
		assertTestResults(expectedTests, tb3Results);
	}

	public void testActivator() throws Exception {
		initFramework(framework);
		BundleContext context = framework.getBundleContext();

		Collection<ServiceReference<String>> testServices = context
				.getServiceReferences(String.class, "(test-bundle=" + TB1_TEST
						+ ")");
		List<String> serviceResults = new ArrayList<String>();
		for (ServiceReference<String> reference : testServices) {
			serviceResults.add((String) reference.getProperty("test-name"));
		}

		List<String> expectedServices = new ArrayList<String>(
				Arrays.asList("REGISTER_SERVICE"));
		assertTestResults(expectedServices, serviceResults);

		List<String> testResults = getResults(TB1_TEST);
		List<String> expectedTests = Arrays.asList(
				"START",
				"SYSTEM_CONTEXT",
				"SYSTEM_STARTING",
				"FRAMEWORK_WIRING",
				"FRAMEWORK_WIRING_INSTALLED",
				"FRAMEWORK_START_LEVEL",
				"FRAMEWORK_START_LEVEL_ZERO",
				"FRAMEWORK_START_LEVEL_EXCEPTION",
				"FRAMEWORK_START_LEVEL_INIT_BSL");
		assertTestResults(expectedTests, testResults);

		testResults.clear();
		expectedTests = new ArrayList<String>();

		stopFramework(framework);

		expectedTests.add("STOP");
		assertTestResults(expectedTests, testResults);

	}

	public void testException() throws Exception {
		initFramework(framework);

		List<String> testResults2 = getResults(TB2_TEST);
		List<String> testResults3 = getResults(TB3_TEST);
		List<String> expectedTests = Arrays.asList("START");

		assertTestResults(expectedTests, testResults2);
		assertTestResults(expectedTests, testResults3);

		testResults2.clear();
		testResults3.clear();
		expectedTests = new ArrayList<String>();
		stopFramework(framework);

		assertTestResults(expectedTests, testResults2);
		assertTestResults(expectedTests, testResults3);
	}

	static class ListenerEvent {
		public ListenerEvent(FrameworkListener l, FrameworkEvent e) {
			this.l = l;
			this.e = e;
		}

		final FrameworkListener l;
		final FrameworkEvent e;
	}

	public void testListeners() throws Exception {
		final List<ListenerEvent> events = new ArrayList<ListenerEvent>();
		FrameworkListener l1 = new FrameworkListener() {
			public void frameworkEvent(FrameworkEvent event) {
				if (event.getType() == FrameworkEvent.ERROR) {
					events.add(new ListenerEvent(this, event));
				}
			}

			public String toString() {
				return "l1";
			}
		};
		FrameworkListener l2 = new FrameworkListener() {
			public void frameworkEvent(FrameworkEvent event) {
				if (event.getType() == FrameworkEvent.ERROR) {
					events.add(new ListenerEvent(this, event));
				}
			}

			public String toString() {
				return "l2";
			}
		};
		initFramework(framework, l1, l2);
		BundleWiring systemWiring = framework.adapt(BundleWiring.class);
		Bundle tb2 = null, tb3 = null;
		for (BundleWire hostWire : systemWiring
				.getProvidedWires(HostNamespace.HOST_NAMESPACE)) {
			if (TB2_TEST.equals(hostWire.getRequirer().getSymbolicName())) {
				tb2 = hostWire.getRequirer().getBundle();
			} else if (TB3_TEST
					.equals(hostWire.getRequirer().getSymbolicName())) {
				tb3 = hostWire.getRequirer().getBundle();
			}
		}
		assertNotNull("No resolved tb2.", tb2);
		assertNotNull("No resolved tb3.", tb3);

		assertEquals("Wrong listener.", l1, popListener(tb2, events));
		assertEquals("Wrong listener.", l2, popListener(tb2, events));
		assertEquals("Wrong listener.", l1, popListener(tb3, events));
		assertEquals("Wrong listener.", l2, popListener(tb3, events));
		stopFramework(framework);
	}

	private FrameworkListener popListener(Bundle tb, List<ListenerEvent> events) {
		for (Iterator<ListenerEvent> iEvents = events.iterator(); iEvents
				.hasNext();) {
			ListenerEvent event = iEvents.next();
			if (tb.equals(event.e.getBundle())) {
				Throwable t = event.e.getThrowable();
				assertTrue(
						"Event throwable is not a BundleException: "
								+ t.getClass(), t instanceof BundleException);
				assertEquals("Wrong BundleException type.",
						BundleException.ACTIVATOR_ERROR,
						((BundleException) t).getType());
				iEvents.remove();
				return event.l;
			}
		}
		return null;
	}

	private void assertTestResults(List<String> expected,
			Collection<String> results) {
		if (!expected.equals(results)) {
			Collection<String> missing = new ArrayList<String>(expected);
			missing.removeAll(results);
			Collection<String> unexpected = new ArrayList<String>(results);
			unexpected.removeAll(expected);
			fail(((!missing.isEmpty()) ? ("Failed the following tests: " + missing)
					: "")
					+ ((!unexpected.isEmpty()) ? (" Results contain unexpected values: " + unexpected)
							: ""));
		}
	}
}
