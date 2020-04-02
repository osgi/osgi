/*
 * Copyright (c) OSGi Alliance (2013, 2016). All Rights Reserved.
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
package org.osgi.test.cases.framework.launch.extensions.tb1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.service.condition.Condition;

public class Activator implements BundleActivator {
	private static final String name = Activator.class.getPackage().getName();
	private static final String RESULTS = "org.osgi.tests.cases.framework.launch.results";
	Collection<ServiceRegistration<String>> services = new ArrayList<ServiceRegistration<String>>();
	public void start(BundleContext context) throws Exception {
		List<String> results = getResults(name);
		results.add("START");
		testRegisterService(context);

		Bundle systemBundle = context.getBundle();

		testSystemBundle(results, systemBundle);
		testFrameworkWiring(results, systemBundle);
		testFrameworkStartLevel(results, systemBundle);
		testTrueCondition(results, context);
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

	private Dictionary<String, String> getServiceProps(String testName) {
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.put("test-bundle", name);
		props.put("test-name", testName);
		return props;
	}

	private void testFrameworkStartLevel(List<String> results,
			Bundle systemBundle) {
		FrameworkStartLevel fwkStartLevel = systemBundle
				.adapt(FrameworkStartLevel.class);
		if (fwkStartLevel == null) {
			return;
		}
		results.add("FRAMEWORK_START_LEVEL");
		if (fwkStartLevel.getStartLevel() == 0) {
			results.add("FRAMEWORK_START_LEVEL_ZERO");
		}
		try {
			fwkStartLevel.setStartLevel(1);
		} catch (IllegalStateException e) {
			// Expected
			results.add("FRAMEWORK_START_LEVEL_EXCEPTION");
		}

		int initBSL = fwkStartLevel.getInitialBundleStartLevel();
		fwkStartLevel.setInitialBundleStartLevel(fwkStartLevel
				.getInitialBundleStartLevel() + 1);
		if (initBSL == fwkStartLevel.getInitialBundleStartLevel() - 1) {
			fwkStartLevel.setInitialBundleStartLevel(fwkStartLevel
					.getInitialBundleStartLevel() - 1);
			if (initBSL == fwkStartLevel.getInitialBundleStartLevel()) {
				// ok we passed
				results.add("FRAMEWORK_START_LEVEL_INIT_BSL");
			}
		}
	}

	private void testFrameworkWiring(List<String> results, Bundle systemBundle)
			throws IOException, BundleException {
		// test for framework wiring
		FrameworkWiring fwkWiring = systemBundle.adapt(FrameworkWiring.class);
		if (fwkWiring != null) {
			results.add("FRAMEWORK_WIRING");
			// try to install and resolve a bundle.
			// this should fail during init, but pass during post init
			Manifest manifest = new Manifest();
			Attributes attrs = manifest.getMainAttributes();
			attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
			attrs.put(new Attributes.Name(
					org.osgi.framework.Constants.BUNDLE_MANIFESTVERSION), "2");
			attrs.put(new Attributes.Name(
					org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME), name
					+ ".resolve");
			attrs.put(new Attributes.Name(
					org.osgi.framework.Constants.BUNDLE_VERSION), "1.0.0");

			ByteArrayOutputStream bundleBytes = new ByteArrayOutputStream();
			JarOutputStream jos = new JarOutputStream(bundleBytes, manifest);
			jos.close();
			Bundle testResolve = systemBundle.getBundleContext().installBundle(
					name + ".resolve",
					new ByteArrayInputStream(bundleBytes.toByteArray()));
			try {
				fwkWiring.resolveBundles(Arrays.asList(testResolve));
				switch (testResolve.getState()) {
				case Bundle.RESOLVED:
					results.add("FRAMEWORK_WIRING_RESOLVED");
					break;
				case Bundle.INSTALLED:
					results.add("FRAMEWORK_WIRING_INSTALLED");
					break;
				default:
					results.add("FRAMEWORK_WIRING_UNEXPECTED: "
							+ testResolve.getState());
					break;
				}
			} finally {
				try {
					testResolve.uninstall();
				} catch (BundleException e) {
					results.add("FRAMEWORK_WIRING_FAILED_UNINSTALL");
				}
			}
		}
	}

	private void testSystemBundle(List<String> results, Bundle systemBundle) {
		// test for system bundle
		if (systemBundle.getBundleId() == 0) {
			results.add("SYSTEM_CONTEXT");
		}
		// test for system bundle state
		if (systemBundle.getState() == Bundle.STARTING) {
			results.add("SYSTEM_STARTING");
		}
	}

	private void testRegisterService(BundleContext context) {
		// register a service test
		services.add(context.registerService(
				String.class,
				"REGISTER_SERVICE", getServiceProps("REGISTER_SERVICE")));
	}

	private void testTrueCondition(List<String> results, BundleContext context)
			throws InvalidSyntaxException {
		ServiceReference<Condition> conditionRef = context
				.getServiceReferences(Condition.class,
						'(' + Condition.CONDITION_ID + '='
								+ Condition.CONDITION_ID_TRUE + ')')
				.iterator()
				.next();
		if (conditionRef != null) {
			results.add("CONDITION_TRUE");
		}
	}

	public void stop(BundleContext context) throws Exception {
		List<String> results = getResults(name);
		results.add("STOP");
		testTrueCondition(results, context);

		for (ServiceRegistration<String> reg : services) {
			reg.unregister();
		}
		throw new RuntimeException(name);
	}

}
