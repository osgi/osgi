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
package org.osgi.test.cases.framework.launch.secure.junit;

import java.io.InputStream;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Test cases for testing fragment bundles and extension bundles.
 * 
 * @author $Id$
 */
public class ExtensionBundleTests extends LaunchTest {
	private Framework framework;
	
	protected void setUp() throws Exception {
		super.setUp();
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(Constants.FRAMEWORK_SECURITY, "osgi");
		configuration.put(Constants.FRAMEWORK_STORAGE,
				getStorageArea(getName(), true).getAbsolutePath());
		framework = createFramework(configuration);
		initFramework(framework);
		assertNotNull("Null SecurityManager", System.getSecurityManager());
		startFramework(framework);
	}

	protected void tearDown() throws Exception {
		stopFramework(framework);
		super.tearDown();
	}

	/**
	 * Tests if a framework extension bundle has to have
	 * <code>AllPermission</code> permission to be installed. Will only
	 * perform this test if <code>SUPPORTS_FRAMEWORK_EXTENSION</code> equals
	 * <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionPermission() throws Exception {
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			String message = "expected extension bundle to fail install due to lack of permission";
			Bundle tb11 = null;
			try {
				tb11 = installBundle(framework, "/fragments.tb11.jar");
				// should fail, since extension bundles have to have
				// AllPermission to be installed
				DefaultTestBundleControl.failException(message, 
						BundleException.class);
			}
			catch (Exception e) {
				DefaultTestBundleControl.assertException(message, 
						BundleException.class, e);
			} finally {
				if (tb11 != null) {
					tb11.uninstall();
				}
			}
		}
		else {
			DefaultTestBundleControl.trace(
					"framework extension bundles not supported");
		}
	}

	/**
	 * Tests if a bundle has to have at least
	 * <code>AdminPermission[<bundle>, EXTENSIONLIFECYCLE]</code> permission
	 * to install a framework extension bundle. Will only perform this test if
	 * <code>SUPPORTS_FRAMEWORK_EXTENSION</code> equals <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionInvokerPermission() throws Exception {
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			// install regular bundle
			Bundle tb16a = installBundle(framework, "/fragments.tb16a.jar");
			Dictionary<String, String> props = new Hashtable<String, String>();
			props.put("bundle", "fragments.tb16b.jar");
			InputStream tb16b = getBundleInput("/fragments.tb16b.jar")
					.openStream();
			framework.getBundleContext().registerService(InputStream.class,
					tb16b, props);
			// start regular bundle that tries to install a framework
			// extension bundle
			tb16a.start();
			// installation inside start should fail, since
			// bundles have to have
			// AdminPermission[<bundle>, EXTENSIONLIFECYCLE]
			// to install extension bundles
			DefaultTestBundleControl
					.trace("prevented bundle without permission from installing "
							+ "a framework extension bundle");
		}
		else {
			DefaultTestBundleControl.trace(
					"framework extension bundles not supported");
		}
	}

	/**
	 * Tests if a bundle has to have at least
	 * <code>AdminPermission[<bundle>, EXTENSIONLIFECYCLE]</code> permission
	 * to install a framework extension bundle. Will only perform this test if
	 * <code>SUPPORTS_FRAMEWORK_EXTENSION</code> equals <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionInvokerPermissionOk() throws Exception {
		if ("true".equals(framework.getBundleContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			// install regular bundle
			Bundle tb18 = installBundle(framework, "/fragments.tb18.jar");
			Dictionary<String, String> props = new Hashtable<String, String>();
			props.put("bundle", "fragments.tb16b.jar");
			InputStream tb16b = getBundleInput("/fragments.tb16b.jar")
					.openStream();
			framework.getBundleContext().registerService(InputStream.class,
					tb16b, props);
			// start regular bundle that tries to install a framework
			// extension bundle
			tb18.start();
			// installation inside start should not fail, since
			// bundle has AdminPermission[<bundle>, EXTENSIONLIFECYCLE]
			DefaultTestBundleControl.trace("bundle with permission installed "
					+ "a framework extension bundle");
		}
		else {
			DefaultTestBundleControl.trace(
					"framework extension bundles not supported");
		}
	}
}
