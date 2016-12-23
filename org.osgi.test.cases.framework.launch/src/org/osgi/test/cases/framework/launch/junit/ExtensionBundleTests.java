/*
 * Copyright (c) OSGi Alliance (2004, 2016). All Rights Reserved.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Test cases for testing fragment bundles and extension bundles.
 *
 * @author $Id$
 */
public class ExtensionBundleTests extends LaunchTest {
	private static final String	class6	= "org.osgi.test.cases.framework.launch.fragments.tb6.FooTB6";
	private static final String	class21	= "org.osgi.test.cases.framework.launch.fragments.tb21.FooTB21";

	private Framework framework;
	
	protected void setUp() throws Exception {
		super.setUp();
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(Constants.FRAMEWORK_STORAGE,
				getStorageArea(getName(), true).getAbsolutePath());
		framework = createFramework(configuration);
		initFramework(framework);
		Bundle tb20 = installBundle(framework, "/fragments.tb20.jar");
		tb20.start();
	}

	protected void tearDown() throws Exception {
		stopFramework(framework);
		super.tearDown();
	}
	
	/**
	 * Tests a fragment bundle where extension directive is not system.bundle.
	 * The installation of the fragment must fail.
	 *
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testBadExtensionBundle() throws Exception {
		// installing bad extension bundle
		try {
			Bundle tb4 = installBundle(framework, "/fragments.tb4.jar");
			// installation should fail
			DefaultTestBundleControl.failException(
					"Expected installation failure " + tb4.getLocation(),
					BundleException.class);
		}
		catch (BundleException e) {
			// expected
		}
	}

	/**
	 * Tests if a framework extension bundle's classpath is appended to the
	 * framework classpath. Will only perform this test if
	 * <code>SUPPORTS_FRAMEWORK_EXTENSION</code> equals <code>true</code>.
	 *
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionBundle() throws Exception {
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			Bundle tb6 = installBundle(framework, "/fragments.tb6.jar");
			framework.adapt(FrameworkWiring.class).resolveBundles(
					Collections.singleton(tb6));
			startFramework(framework);
			assertTrue("expected framework extension bundle to be resolved",
					(tb6.getState() & Bundle.RESOLVED) != 0);
			// check if classloader is framework classloader
			Class< ? > c = framework.loadClass(class6);
			assertEquals(
					"expected class to be loaded by the framework classloader",
					c.getClassLoader(), framework.getBundleContext().getClass()
							.getClassLoader());
		}
		else {
			try {
				// tries to install extension bundle
				installBundle(framework, "/fragments.tb6.jar");
				// installation should fail
				DefaultTestBundleControl.failException(
						"framework extension bundle instalation should fail",
						BundleException.class);
			}
			catch (BundleException e) {
				DefaultTestBundleControl
						.trace("framework extension bundles not supported");
			}
		}
	}

	/**
	 * Tests if a framework extension bundle is not able to load classes
	 * directly. Will only perform this test if
	 * <code>SUPPORTS_FRAMEWORK_EXTENSION</code> equals <code>true</code>.
	 *
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionBundleLoadClass() throws Exception {
		String message = "expected class in extension bundle cannot be loaded";
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			Bundle tb6 = installBundle(framework, "/fragments.tb6.jar");
			framework.adapt(FrameworkWiring.class).resolveBundles(
					Collections.singleton(tb6));
			startFramework(framework);
			assertTrue("expected framework extension bundle to be resolved",
					(tb6.getState() & Bundle.RESOLVED) != 0);
			try {
				tb6.loadClass(class6);
				// should fail, since extension bundles are not able to load
				// classes directly
				DefaultTestBundleControl.failException(message, 
						ClassNotFoundException.class);
			}
			catch (Exception e) {
				DefaultTestBundleControl.assertException(message, 
						ClassNotFoundException.class, e);
			}
		}
		else {
			DefaultTestBundleControl.trace(
					"framework extension bundles not supported");
		}
	}

	/**
	 * Tests if an extension bundle is treated as a framework extension by
	 * default. Will only perform this test if
	 * <code>SUPPORTS_FRAMEWORK_EXTENSION</code> equals <code>true</code>.
	 *
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionDefault() throws Exception {
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			Bundle tb21 = installBundle(framework, "/fragments.tb21.jar");
			framework.adapt(FrameworkWiring.class).resolveBundles(
					Collections.singleton(tb21));
			startFramework(framework);
			assertTrue("expected framework extension bundle to be resolved",
					(tb21.getState() & Bundle.RESOLVED) != 0);
			// check if classloader is framework classloader
			Class< ? > c = framework.loadClass(class21);
			assertEquals(
					"expected class to be loaded by the framework classloader",
					c.getClassLoader(), framework.getBundleContext().getClass()
							.getClassLoader());
		}
		else {
			DefaultTestBundleControl.trace(
					"framework extension bundles not supported");
		}
	}

	/**
	 * Tests if a framework extension bundle is not able to load native
	 * libraries. Will only perform this test if
	 * <code>SUPPORTS_FRAMEWORK_EXTENSION</code> equals <code>true</code>.
	 *
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionNativeCode() throws Exception {
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			String message = "expected extension bundle with Bundle-NativeCode to fail to install";
			Bundle tb22 = null;
			try {
				tb22 = installBundle(framework, "/fragments.tb22.jar");
				// should fail, since extension bundles are not able to
				// declare native code headers
				DefaultTestBundleControl.failException(message, 
						BundleException.class);
			}
			catch (Exception e) {
				DefaultTestBundleControl.assertException(message, 
						BundleException.class, e);
			} finally {
				if (tb22 != null) {
					tb22.uninstall();
				}
			}
		}
		else {
			DefaultTestBundleControl.trace(
					"framework extension bundles not supported");
		}
	}

	/**
	 * Tests if a framework extension bundle is not able to require bundles.
	 * Will only perform this test if <code>SUPPORTS_FRAMEWORK_EXTENSION</code>
	 * equals <code>true</code>.
	 *
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionRequireBundle() throws Exception {
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			String message = "expected extension bundle with Require-Bundle to fail to install";
			try {
				installBundle(framework, "/fragments.tb10.jar");
				// should fail, since extension bundles are not able to
				// require bundles
				DefaultTestBundleControl.failException(message, 
						BundleException.class);
			}
			catch (Exception e) {
				DefaultTestBundleControl.assertException(message, 
						BundleException.class, e);
			}
		}
		else {
			DefaultTestBundleControl.trace(
					"framework extension bundles not supported");
		}
	}
}
