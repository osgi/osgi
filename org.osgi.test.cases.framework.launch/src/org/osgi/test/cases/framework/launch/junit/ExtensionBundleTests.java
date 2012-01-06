/*
 * Copyright (c) OSGi Alliance (2004, 2011). All Rights Reserved.
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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Test cases for testing fragment bundles and extension bundles.
 *
 * @version $Id$
 */
public class ExtensionBundleTests extends LaunchTest {
	private Framework framework;
	
	protected void setUp() throws Exception {
		super.setUp();
		framework = createFramework(null);
		initFramework(framework);
		startFramework(framework);
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
	 * Tests if a boot classpath extension bundle's classpath is appended to the
	 * boot classpath. Will only perform this test if
	 * <code>SUPPORTS_BOOTCLASSPATH_EXTENSION</code> equals <code>true</code>.
	 *
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testBootClasspathExtensionBundle() throws Exception {
		String class5 = "org.osgi.test.cases.framework.fragments.tb5.FooTB5";
		Bundle tb5 = null;
		Bundle systemBundle = framework.getBundleContext().getBundle(0);
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION))) {
			try {
				// install extension bundle
				tb5 = installBundle(framework, "/fragments.tb5.jar");
				// check if classloader is boot classloader
				try {
					assertEquals("loaded by the boot classloader", systemBundle
							.loadClass(class5).getClassLoader(), Class.class
							.getClassLoader());
					assertTrue("bootclasspath extension bundle is resolved",
							(tb5.getState() & Bundle.RESOLVED) != 0);
				}
				catch (ClassNotFoundException cnfe) {
					if ((tb5.getState() & Bundle.RESOLVED) != 0)
						fail("failed loading class from a resolved bootclasspath extension bundle");
					assertTrue("bootclasspath extension bundle is installed",
							(tb5.getState() & Bundle.INSTALLED) != 0);
				}
			}
			catch (BundleException be) {
				fail("installing bootclasspath extension bundle");
			}
			finally {
				if (tb5 != null) {
					tb5.uninstall();
				}
			}
		}
		else {
			String message = "bootclasspath extension bundle installation not supported";
			try {
				// tries to install extension bundle
				tb5 = installBundle(framework, "/fragments.tb5.jar");
				// installation should fail
				DefaultTestBundleControl.failException(message,
						BundleException.class);
			}
			catch (BundleException e) {
				DefaultTestBundleControl.assertException(message, 
						UnsupportedOperationException.class,
						e.getNestedException());
			}
			finally {
				if (tb5 != null) {
					tb5.uninstall();
				}
			}
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
		String class6 = "org.osgi.test.cases.framework.fragments.tb6.FooTB6";
		Bundle tb6 = null;
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			try {
				// install extension bundle
				tb6 = installBundle(framework, "/fragments.tb6.jar");
				// check if classloader is framework classloader
				try {
					assertEquals("loaded by the framework classloader",
							framework.getBundleContext().getClass().getClassLoader().loadClass(
									class6).getClassLoader(), framework.getBundleContext()
									.getClass().getClassLoader());
					assertTrue("framework extension bundle is resolved", (tb6
							.getState() & Bundle.RESOLVED) != 0);
				}
				catch (ClassNotFoundException cnfe) {
					if ((tb6.getState() & Bundle.RESOLVED) != 0)
						fail("failed loading class from a resolved framework extension bundle");
					assertTrue("framework extension bundle is installed", (tb6
							.getState() & Bundle.INSTALLED) != 0);
				}
			}
			catch (BundleException be) {
				fail("installing framework extension bundle");
			}
			finally {
				if (tb6 != null) {
					tb6.uninstall();
				}
			}

		}
		else {
			String message = "framework extension bundle instalation should fail";
			try {
				// tries to install extension bundle
				tb6 = installBundle(framework, "/fragments.tb6.jar");
				// installation should fail
				DefaultTestBundleControl.failException(message, 
						BundleException.class);
			}
			catch (BundleException e) {
				DefaultTestBundleControl.assertException(message, 
						UnsupportedOperationException.class, 
						e.getNestedException());
			}
			finally {
				if (tb6 != null) {
					tb6.uninstall();
				}
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
		String class6 = "org.osgi.test.cases.framework.fragments.tb6.FooTB6";
		String message = "extension bundle cannot load classes";
		Bundle tb6 = null;
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			// install extension bundle
			tb6 = installBundle(framework, "/fragments.tb6.jar");
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
			finally {
				tb6.uninstall();
			}

		}
		else {
			DefaultTestBundleControl.trace(
					"framework extension bundles not supported");
		}
	}

	/**
	 * Tests if a boot classpath extension bundle is not able to load classes
	 * directly. Will only perform this test if
	 * <code>SUPPORTS_BOOTCLASSPATH_EXTENSION</code> equals <code>true</code>.
	 *
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testBootClasspathExtensionBundleLoadClass() throws Exception {
		String class5 = "org.osgi.test.cases.framework.fragments.tb5.FooTB5";
		String message = "boot extension bundle cannot load classes";
		Bundle tb5 = null;
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION))) {
			// install extension bundle
			tb5 = installBundle(framework, "/fragments.tb5.jar");
			try {
				tb5.loadClass(class5);
				// should fail, since extension bundles are not able to load
				// classes directly
				DefaultTestBundleControl.failException(message, 
						ClassNotFoundException.class);
			}
			catch (Exception e) {
				DefaultTestBundleControl.assertException(message, 
						ClassNotFoundException.class, e);
			}
			finally {
				tb5.uninstall();
			}

		}
		else {
			DefaultTestBundleControl.trace(
					"boot classpath extension bundles not supported");
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
		String class21 = "org.osgi.test.cases.framework.fragments.tb21.FooTB21";
		Bundle tb21 = null;
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			try {
				// install extension bundle
				tb21 = installBundle(framework, "/fragments.tb21.jar");
				// check if classloader is framework classloader
				try {
					assertEquals("loaded by the framework classloader",
							framework.getBundleContext().getClass().getClassLoader().loadClass(
									class21).getClassLoader(), framework.getBundleContext()
									.getClass().getClassLoader());
					assertTrue("framework extension bundle is resolved", (tb21
							.getState() & Bundle.RESOLVED) != 0);
				}
				catch (ClassNotFoundException cnfe) {
					if ((tb21.getState() & Bundle.RESOLVED) != 0)
						fail("failed loading class from a resolved framework extension bundle");
					assertTrue("framework extension bundle is installed", (tb21
							.getState() & Bundle.INSTALLED) != 0);
				}
			}
			catch (BundleException be) {
				fail("installing framework extension bundle");
			}
			finally {
				if (tb21 != null) {
					tb21.uninstall();
				}
			}
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
		String message = "extension bundle cannot load native code";
		Bundle tb22 = null;
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
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
			}
			finally {
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
	 * Tests if a boot classpath extension bundle is not able to load native
	 * libraries. Will only perform this test if
	 * <code>SUPPORTS_BOOTCLASSPATH_EXTENSION</code> equals <code>true</code>.
	 *
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testBootClasspathExtensionNativeCode() throws Exception {
		String message = "extension bundle cannot load native code";
		Bundle tb13 = null;
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION))) {
			try {
				tb13 = installBundle(framework, "/fragments.tb13.jar");
				// should fail, since extension bundles are not able to
				// declare native code headers
				DefaultTestBundleControl.failException(message, 
						BundleException.class);
			}
			catch (Exception e) {
				DefaultTestBundleControl.assertException(message, 
						BundleException.class, e);
			}
			finally {
				if (tb13 != null) {
					tb13.uninstall();
				}
			}
		}
		else {
			DefaultTestBundleControl.trace(
					"boot classpath extension bundles not supported");
		}
	}

	/**
	 * Tests if a framework extension bundle is not able to import packages.
	 * Will only perform this test if <code>SUPPORTS_FRAMEWORK_EXTENSION</code>
	 * equals <code>true</code>.
	 *
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionImportPackage() throws Exception {
		String message = "extension bundle cannot import packages";
		Bundle tb9 = null;
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			try {
				tb9 = installBundle(framework, "/fragments.tb9.jar");
				// should fail, since extension bundles are not able to
				// import packages
				DefaultTestBundleControl.failException(message, 
						BundleException.class);
			}
			catch (Exception e) {
				DefaultTestBundleControl.assertException(message, 
						BundleException.class, e);
			}
			finally {
				if (tb9 != null) {
					tb9.uninstall();
				}
			}
		}
		else {
			DefaultTestBundleControl.trace(
					"framework extension bundles not supported");
		}
	}

	/**
	 * Tests if a boot classpath extension bundle is not able to import
	 * packages. Will only perform this test if
	 * <code>SUPPORTS_BOOTCLASSPATH_EXTENSION</code> equals <code>true</code>.
	 *
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testBootClasspathExtensionImportPackage() throws Exception {
		String message = "extension bundle cannot import packages";
		Bundle tb12 = null;
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION))) {
			try {
				tb12 = installBundle(framework, "/fragments.tb12.jar");
				// should fail, since extension bundles are not able to
				// import packages
				DefaultTestBundleControl.failException(message, 
						BundleException.class);
			}
			catch (Exception e) {
				DefaultTestBundleControl.assertException(message, 
						BundleException.class, e);
			}
			finally {
				if (tb12 != null) {
					tb12.uninstall();
				}
			}
		}
		else {
			DefaultTestBundleControl.trace(
					"boot classpath extension bundles not supported");
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
		String message = "extension bundle cannot require bundles";
		Bundle tb10 = null;
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			try {
				tb10 = installBundle(framework, "/fragments.tb10.jar");
				// should fail, since extension bundles are not able to
				// require bundles
				DefaultTestBundleControl.failException(message, 
						BundleException.class);
			}
			catch (Exception e) {
				DefaultTestBundleControl.assertException(message, 
						BundleException.class, e);
			}
			finally {
				if (tb10 != null) {
					tb10.uninstall();
				}
			}
		}
		else {
			DefaultTestBundleControl.trace(
					"framework extension bundles not supported");
		}
	}

	/**
	 * Tests if a boot classpath extension bundle is not able to require
	 * bundles. Will only perform this test if
	 * <code>SUPPORTS_BOOTCLASSPATH_EXTENSION</code> equals <code>true</code>.
	 *
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testBootClasspathExtensionRequireBundle() throws Exception {
		String message = "extension bundle cannot require bundles";
		Bundle tb15 = null;
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION))) {
			try {
				tb15 = installBundle(framework, "/fragments.tb15.jar");
				// should fail, since extension bundles are not able to
				// require bundles
				DefaultTestBundleControl.failException(message, 
						BundleException.class);
			}
			catch (Exception e) {
				DefaultTestBundleControl.assertException(message, 
						BundleException.class, e);
			}
			finally {
				if (tb15 != null) {
					tb15.uninstall();
				}
			}
		}
		else {
			DefaultTestBundleControl.trace(
					"boot classpath extension bundles not supported");
		}
	}
}
