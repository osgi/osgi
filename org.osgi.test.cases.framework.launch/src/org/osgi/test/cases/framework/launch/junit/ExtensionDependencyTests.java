/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
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
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.namespace.ExecutionEnvironmentNamespace;
import org.osgi.framework.namespace.HostNamespace;
import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
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
	private static final String	EXTENSION_TEST_NAMESPACE	= "extension.test";

	private static final String	V1					= ".v1";
	private static final String	V2					= ".v2";
	private static final String	V3					= ".v3";
	private static final String	JAR					= ".jar";

	private static final String	EXTENSION_1				= "dependency.tb1";
	private static final String	EXTENSION_1_V1_JAR		= "/" + EXTENSION_1 + V1
			+ JAR;
	private static final String	EXTENSION_1_V2_JAR		= "/" + EXTENSION_1 + V2
			+ JAR;
	private static final String	EXTENSION_1_V3_JAR		= "/" + EXTENSION_1 + V3
			+ JAR;

	private static final String	EXTENSION_1_CLASS_NAME	= "org.osgi.test.cases.framework.launch.dependency.tb1.TB1";
	private static final String	EXTENSION_2_RESOURCE	= "org/osgi/test/cases/framework/launch/dependency/tb1/tb1.txt";

	private static final String	EXTENSION_2				= "dependency.tb2";
	private static final String	EXTENSION_2_V1_JAR		= "/" + EXTENSION_2 + V1
			+ JAR;

	private static final String	EXTENSION_3				= "dependency.tb3";
	private static final String	EXTENSION_3_V1_JAR		= "/" + EXTENSION_3 + V1
			+ JAR;

	private static final String	EXTENSION_4				= "dependency.tb4";
	private static final String	EXTENSION_4_V1_JAR		= "/" + EXTENSION_4 + V1
			+ JAR;

	private Framework framework;
	private int					originalExtensionCount;
	
	protected void setUp() throws Exception {
		super.setUp();
		framework = createFramework(true);
		originalExtensionCount = getExtensionCount(framework);
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
		Bundle extensionA_V1 = installAndResolve(framework, true,
				EXTENSION_1_V1_JAR)
				.get(0);
		int currentExtensionCount = getExtensionCount(framework);
		assertEquals("Wrong number of fragments.",
				originalExtensionCount + 1, currentExtensionCount);

		Future<FrameworkEvent> stopFuture = waitForStop(framework);

		// uninstall and refresh the extension which causes a framework stop
		extensionA_V1.uninstall();
		framework.adapt(FrameworkWiring.class)
				.refreshBundles(Collections.singleton(extensionA_V1));

		// wait for stop
		assertSystemRefreshedEvent(stopFuture);

		reCreateFramework();

		// make sure there are no fragments attached
		currentExtensionCount = getExtensionCount(framework);
		assertEquals("Wrong number of fragments.", originalExtensionCount,
				currentExtensionCount);
	}

	public void testMultipleVersions() throws BundleException, IOException,
			InterruptedException, ExecutionException, ClassNotFoundException {
		if (!isFrameworkExtensionSupported()) {
			return;
		}

		Bundle extensionA_V1 = installAndResolve(framework, true,
				EXTENSION_1_V1_JAR).get(0);

		int currentExtensionCount = getExtensionCount(framework);
		assertEquals("Wrong number of fragments.",
				originalExtensionCount + 1, currentExtensionCount);

		assertExtensionClass(EXTENSION_1_CLASS_NAME, EXTENSION_2_RESOURCE);

		List<Bundle> v2ANDv3 = installAndResolve(framework, false,
				EXTENSION_1_V2_JAR, EXTENSION_1_V3_JAR);

		String location_V3 = v2ANDv3.get(1).getLocation();

		Future<FrameworkEvent> stopFuture = waitForStop(framework);

		// now update and refresh V1; causes a framework stop
		extensionA_V1.update(getBundleInput(EXTENSION_1_V1_JAR).openStream());
		framework.adapt(FrameworkWiring.class)
				.refreshBundles(Collections.singleton(extensionA_V1));
		// wait for stop
		assertSystemRefreshedEvent(stopFuture);

		reCreateFramework();
		currentExtensionCount = getExtensionCount(framework);
		assertEquals("Wrong number of fragments.", originalExtensionCount + 1,
				currentExtensionCount);

		Bundle extensionA_V3 = framework.getBundleContext()
				.getBundle(location_V3);
		assertWires(framework.adapt(BundleWiring.class),
				extensionA_V3.adapt(BundleWiring.class),
				HostNamespace.HOST_NAMESPACE);

		assertExtensionClass(EXTENSION_1_CLASS_NAME, EXTENSION_2_RESOURCE);
	}

	public void testImportPackage()
			throws BundleException, IOException, InvalidSyntaxException {
		if (!isFrameworkExtensionSupported()) {
			return;
		}

		List<Bundle> extensions = installAndResolve(framework, true,
				EXTENSION_2_V1_JAR, EXTENSION_1_V1_JAR);
		BundleRevision extension1 = extensions.get(1)
				.adapt(BundleRevision.class);
		BundleRevision extension2 = extensions.get(0)
				.adapt(BundleRevision.class);

		// make sure the build included package and ee requirements for
		// extension2
		assertFalse("No package requirements found.", extension2
				.getRequirements(PackageNamespace.PACKAGE_NAMESPACE).isEmpty());

		// make sure both extensions are attached and the host and ee
		// requirements wired
		BundleWiring hostWiring = framework.adapt(BundleWiring.class);
		assertWires(hostWiring, extension1.getWiring(),
				HostNamespace.HOST_NAMESPACE);
		assertWires(hostWiring, extension1.getWiring(),
				ExecutionEnvironmentNamespace.EXECUTION_ENVIRONMENT_NAMESPACE);
		assertWires(hostWiring, extension2.getWiring(),
				HostNamespace.HOST_NAMESPACE);
		assertWires(hostWiring, extension2.getWiring(),
				ExecutionEnvironmentNamespace.EXECUTION_ENVIRONMENT_NAMESPACE);

		// make sure the package wires did not get created.
		// system bundle can only export packages.
		List<BundleWire> packageWires = hostWiring
				.getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE);
		assertEquals("Found some package wires: " + packageWires, 0,
				packageWires.size());

		// ensure the service from extension 2 is registered
		ServiceReference< ? >[] serviceRef = framework.getBundleContext()
				.getServiceReferences((String) null, "(test=tb2)");
		assertNotNull("No service found.", serviceRef);
		assertEquals("Wrong number of services.", 1, serviceRef.length);
	}

	public void testRequireCapabilityFromExtension()
			throws BundleException, IOException {
		if (!isFrameworkExtensionSupported()) {
			return;
		}
		List<Bundle> extensions = installAndResolve(framework, true,
				EXTENSION_1_V1_JAR, EXTENSION_2_V1_JAR, EXTENSION_3_V1_JAR);
		Bundle extension3 = extensions.get(2);

		BundleWiring hostWiring = framework.adapt(BundleWiring.class);
		assertWires(hostWiring, extension3.adapt(BundleWiring.class),
				EXTENSION_TEST_NAMESPACE);
	}

	public void testMissingCapabilityFromExtension()
			throws BundleException, IOException, InvalidSyntaxException {
		if (!isFrameworkExtensionSupported()) {
			return;
		}
		List<Bundle> extensions = installAndResolve(framework, false,
				EXTENSION_3_V1_JAR);
		Bundle extension3 = extensions.get(0);

		assertEquals("Wrong number of fragments.", originalExtensionCount,
				getExtensionCount(framework));

		installAndResolve(framework, true, EXTENSION_1_V1_JAR);
		assertEquals("Wrong number of fragments.", originalExtensionCount + 1,
				getExtensionCount(framework));

		installAndResolve(framework, true, EXTENSION_2_V1_JAR);
		// request extension3 to resolve incase it was not pulled in by the
		// framework
		framework.adapt(FrameworkWiring.class)
				.resolveBundles(Collections.singleton(extension3));

		assertEquals("Wrong number of fragments.", originalExtensionCount + 3,
				getExtensionCount(framework));
		BundleWiring hostWiring = framework.adapt(BundleWiring.class);
		assertWires(hostWiring, extension3.adapt(BundleWiring.class),
				EXTENSION_TEST_NAMESPACE);

		// ensure the service from extension 2 is registered
		ServiceReference< ? >[] serviceRef = framework.getBundleContext()
				.getServiceReferences((String) null, "(test=tb2)");
		assertNotNull("No service found.", serviceRef);
		assertEquals("Wrong number of services.", 1, serviceRef.length);

		// ensure the service from extension 3 is registered
		serviceRef = framework.getBundleContext()
				.getServiceReferences((String) null, "(test=tb3)");
		assertNotNull("No service found.", serviceRef);
		assertEquals("Wrong number of services.", 1, serviceRef.length);
	}

	public void testRequireIdentity() throws BundleException, IOException {
		if (!isFrameworkExtensionSupported()) {
			return;
		}
		List<Bundle> extensions = installAndResolve(framework, true,
				EXTENSION_1_V1_JAR,
				EXTENSION_4_V1_JAR);
		assertEquals("Wrong number of fragments.", originalExtensionCount + 2,
				getExtensionCount(framework));
		Bundle extension1 = extensions.get(0);
		Bundle extension4 = extensions.get(1);

		BundleWiring fwkBundleWiring = framework.adapt(BundleWiring.class);
		BundleWiring extension1Wiring = extension1.adapt(BundleWiring.class);
		BundleWiring extension4Wiring = extension4.adapt(BundleWiring.class);
		// The osgi.identity capability is not payload.
		// Check that extension1 wiring is providing the wires.
		assertEquals("Framework is providing another identity.", 1,
				fwkBundleWiring
						.getCapabilities(IdentityNamespace.IDENTITY_NAMESPACE)
						.size());
		assertEquals("Extension4 wiring is requireing another identity.", 0,
				extension4Wiring
						.getRequiredWires(IdentityNamespace.IDENTITY_NAMESPACE)
						.size());
		List<BundleWire> identityRequiredWires = fwkBundleWiring
				.getRequiredWires(IdentityNamespace.IDENTITY_NAMESPACE);
		assertEquals("Wrong number of required wires found.", 1,
				identityRequiredWires.size());
		assertEquals("Wrong wires found.",
				extension1Wiring.getProvidedResourceWires(
						IdentityNamespace.IDENTITY_NAMESPACE),
				identityRequiredWires);
	}

	private void assertExtensionClass(String extensionAClassName,
			String extensionAResource)
			throws ClassNotFoundException, IOException {
		ClassLoader fwkClassLoader = framework.adapt(BundleWiring.class)
				.getClassLoader();
		Class< ? > extensionClass = fwkClassLoader
				.loadClass(extensionAClassName);
		assertNotNull("Failed to find extension class: " + extensionAClassName,
				extensionClass);

		// make sure there is only one resource found;
		// trying to make sure the other versions are not on the classpath
		List<URL> resourceList = new ArrayList<>();
		for (Enumeration<URL> resources = fwkClassLoader
				.getResources(EXTENSION_2_RESOURCE); resources
						.hasMoreElements();) {
			resourceList.add(resources.nextElement());
		}
		assertEquals("Wrong number of resources found.", 1,
				resourceList.size());
	}

	private static int getExtensionCount(Framework framework) {
		return framework.adapt(BundleWiring.class)
				.getProvidedWires(HostNamespace.HOST_NAMESPACE)
				.size();
	}

	private final void reCreateFramework() {
		// close the factory to throw away the class loader
		frameworkFactory.close();
		// create a new framework with a new class loader; but don't delete the
		// storage area
		framework = createFramework(false);
	}

	private final List<Bundle> installAndResolve(Framework framework,
			boolean expectResolve, String... bundles)
			throws BundleException, IOException {
		List<Bundle> installed = new ArrayList<Bundle>();
		for (String bundle : bundles) {
			installed.add(installBundle(framework, bundle));
		}
		framework.adapt(FrameworkWiring.class).resolveBundles(installed);
		for (Bundle bundle : installed) {
			if (expectResolve) {
				assertTrue("Expected bundle to resolve: " + bundle,
						(bundle.getState() & Bundle.RESOLVED) != 0);
				assertWires(framework.adapt(BundleWiring.class),
						bundle.adapt(BundleWiring.class),
						HostNamespace.HOST_NAMESPACE);
			} else {
				assertTrue("Expected bundle to not resolve: " + bundle,
						(bundle.getState() & Bundle.INSTALLED) != 0);
			}
		}
		return installed;
	}

	private void assertWires(BundleWiring hostWiring,
			BundleWiring fragmentWiring, String namespace) {
		boolean payloadReq = !(ExecutionEnvironmentNamespace.EXECUTION_ENVIRONMENT_NAMESPACE
				.equals(namespace)
				|| HostNamespace.HOST_NAMESPACE.equals(namespace));
		assertNotNull("No host wiring!", hostWiring);
		assertNotNull("No fragment wiring!", fragmentWiring);
		List<BundleRequirement> fragmentReqs = fragmentWiring.getRevision()
				.getDeclaredRequirements(namespace);
		assertFalse("No requirements found by fragment.",
				fragmentReqs.isEmpty());
		List<BundleWire> providedWires = hostWiring
				.getProvidedWires(namespace);
		assertFalse("No provided wires found.", providedWires.isEmpty());
		List<BundleWire> requiredWires = payloadReq
				? hostWiring.getRequiredWires(namespace)
				: fragmentWiring.getRequiredWires(namespace);
		assertFalse("No required wires found.", requiredWires.isEmpty());
		int numFound = 0;
		for (BundleWire requiredWire : requiredWires) {
			if (fragmentReqs.contains(requiredWire.getRequirement())) {
				if (providedWires.contains(requiredWire)) {
					numFound++;
				}
			}
		}
		assertEquals(
				"The expected wires are not found: " + providedWires
						+ requiredWires,
				fragmentReqs.size(), numFound);
	}

	private static void assertSystemRefreshedEvent(
			Future<FrameworkEvent> stopFuture)
			throws InterruptedException, ExecutionException {
		FrameworkEvent stopEvent = stopFuture.get();
		assertNotNull("No event found.", stopEvent);
		assertEquals("Wrong event: " + stopEvent,
				FrameworkEvent.STOPPED_SYSTEM_REFRESHED,
				stopEvent.getType());
	}

	private static Future<FrameworkEvent> waitForStop(
			final Framework framework) {
		FutureTask<FrameworkEvent> stoppedFuture = new FutureTask<>(
				new Callable<FrameworkEvent>() {
					@Override
					public FrameworkEvent call() throws Exception {
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
