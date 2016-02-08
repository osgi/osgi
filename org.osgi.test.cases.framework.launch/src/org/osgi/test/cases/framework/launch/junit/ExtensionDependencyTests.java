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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.namespace.HostNamespace;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Test cases for testing fragment bundles and extension bundles.
 *
 * @author $Id$
 */
public class ExtensionDependencyTests extends LaunchTest {
	private static final String	V1					= ".v1";
	private static final String	V2					= ".v2";
	private static final String	V3					= ".v3";
	private static final String	JAR					= ".jar";

	private static final String	EXTENSION_A			= "dependency.tba";
	private static final String	EXTENSION_A_V1_JAR	= "/" + EXTENSION_A + V1
			+ JAR;
	private static final String	EXTENSION_A_V2_JAR	= "/" + EXTENSION_A + V2
			+ JAR;
	private static final String	EXTENSION_A_V3_JAR	= "/" + EXTENSION_A + V3
			+ JAR;

	private static final String	EXTENSION_B			= "dependency.tbb";
	private static final String	EXTENSION_B_V1_JAR	= "/" + EXTENSION_B + V1
			+ JAR;

	private static final String	EXTENSION_C			= "dependency.tbc";
	private static final String	EXTENSION_C_V1_JAR	= "/" + EXTENSION_C + V1
			+ JAR;

	private static final String	EXTENSION_D			= "dependency.tbd";
	private static final String	EXTENSION_D_V1_JAR	= "/" + EXTENSION_D + V1
			+ JAR;

	private Framework framework;
	
	protected void setUp() throws Exception {
		super.setUp();
		framework = createFramework(true);
	}

	private Framework createFramework(boolean delete) {
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(Constants.FRAMEWORK_STORAGE,
				getStorageArea(getName(), delete).getAbsolutePath());
		Framework newFramework = createFramework(configuration);
		initFramework(newFramework);
		return newFramework;
	}

	protected void tearDown() throws Exception {
		stopFramework(framework);
		super.tearDown();
	}

	/**
	 * Tests if an extension refresh results in a framework restart. Will only
	 * perform this test if <code>SUPPORTS_FRAMEWORK_EXTENSION</code> equals
	 * <code>true</code>.
	 *
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionRefresh() throws Exception {
		if (!isFrameworkExtensionSupported()) {
			return;
		}
		FrameworkWiring fwkWiring = framework.adapt(FrameworkWiring.class);
		int origNumFrameworkFragments = framework.adapt(BundleWiring.class)
				.getProvidedWires(HostNamespace.HOST_NAMESPACE)
				.size();

		Bundle extensionA_V1 = installAndResolve(framework, EXTENSION_A_V1_JAR)
				.get(0);
		int currentNumFrameworkFragments = framework.adapt(BundleWiring.class)
				.getProvidedWires(HostNamespace.HOST_NAMESPACE)
				.size();
		assertEquals("Wrong number of fragments.",
				origNumFrameworkFragments + 1, currentNumFrameworkFragments);

		startFramework(framework);
		Future<FrameworkEvent> stopFuture = waitForStop(framework);

		extensionA_V1.uninstall();
		fwkWiring.refreshBundles(Collections.singleton(extensionA_V1));

		assertClasspathModifiedEvent(stopFuture);
		// close the factory to throw away the class loader
		frameworkFactory.close();
		// create a new framework with a new class loader; but don't delete the
		// storage area
		framework = createFramework(false);
		startFramework(framework);
		// make sure there are no fragments attached
		currentNumFrameworkFragments = framework.adapt(BundleWiring.class)
				.getProvidedWires(HostNamespace.HOST_NAMESPACE)
				.size();
		assertEquals("Wrong number of fragments.", origNumFrameworkFragments,
				currentNumFrameworkFragments);
	}

	public void testMultipleVersions() {
		if (!isFrameworkExtensionSupported()) {
			return;
		}
	}

	final List<Bundle> installAndResolve(Framework framework, String... bundles)
			throws BundleException, IOException {
		List<Bundle> installed = new ArrayList<Bundle>();
		for (String bundle : bundles) {
			installed.add(installBundle(framework, bundle));
		}
		framework.adapt(FrameworkWiring.class).resolveBundles(installed);
		for (Bundle bundle : installed) {
			assertTrue("Expected bundle to resolve: " + bundle,
					(bundle.getState() & Bundle.RESOLVED) != 0);
		}
		return installed;
	}

	static void assertClasspathModifiedEvent(Future<FrameworkEvent> stopFuture)
			throws InterruptedException, ExecutionException {
		FrameworkEvent stopEvent = stopFuture.get();
		assertNotNull("No event found.", stopEvent);
		// TODO current uses FrameworkEvent#STOPPED_BOOTCLASSPATH_MODIFIED
		assertEquals("Wrong event: " + stopEvent,
				FrameworkEvent.STOPPED_BOOTCLASSPATH_MODIFIED,
				stopEvent.getType());
	}

	static Future<FrameworkEvent> waitForStop(Framework framework) {
		FutureTask<FrameworkEvent> stoppedFuture = new FutureTask<>(
				new Callable<FrameworkEvent>() {
					@Override
					public FrameworkEvent call() throws Exception {
						// TODO Auto-generated method stub
						return framework.waitForStop(OSGiTestCaseProperties.getTimeout() * OSGiTestCaseProperties.getScaling());
					}
				});
		new Thread(stoppedFuture, "Waiting for stop.").start();
		return stoppedFuture;
	}

	private boolean isFrameworkExtensionSupported() {
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			return true;
		}
		DefaultTestBundleControl
				.trace("framework extension bundles not supported");
		return false;
	}
}
