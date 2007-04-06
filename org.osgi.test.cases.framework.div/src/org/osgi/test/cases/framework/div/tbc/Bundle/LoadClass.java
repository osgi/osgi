/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.test.cases.framework.div.tbc.Bundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.service.TestCaseLink;

/**
 * 
 * This class tests the method org.osgi.framework.Bundle.loadClass().
 * 
 * @version $Revision$
 */
public class LoadClass {

	private BundleContext	context;
	private String			tcHome;
	private TestCaseLink	link;

	/**
	 * Creates a new LoadClass
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home path
	 */
	public LoadClass(BundleContext _context, TestCaseLink _link, String _tcHome) {
		context = _context;
		link = _link;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testLoadClass0001();
		testLoadClass0002();
		testLoadClass0003();
		testLoadClass0004();
		testLoadClass0005();
	}

	/**
	 * Test the method loadClass() with an installed bundle and a existing class
	 * 
	 * @spec Bundle.loadClass(String)
	 */
	public void testLoadClass0001() throws Exception {
		Bundle bundle;
		Class clazz;
		ClassLoader classLoader;
		Object service;
		ServiceReference sr;

		bundle = context.installBundle(tcHome + "tb10.jar");
		bundle.start();

		try {
			clazz = bundle
					.loadClass("org.osgi.test.cases.framework.div.tb10.Foo");
		}
		catch (ClassNotFoundException ex) {
			throw new BundleTestException(
					"Testing the method loadClass() with an installed bundle and a existing class");
		}

		sr = context
				.getServiceReference("org.osgi.test.cases.framework.div.tb10.TestService");

		service = context.getService(sr);
		classLoader = (ClassLoader) service.getClass().getMethod(
				"getClassLoader", null).invoke(service, null);
		if (!classLoader.equals(clazz.getClassLoader())) {
			throw new BundleTestException(
					"Expecting the ClassLoader of the class and the bundle to be the same");
		}

		bundle.stop();
		bundle.uninstall();
	}

	/**
	 * Test the method loadClass() with an installed bundle and a nonexistent
	 * class
	 * 
	 * @spec Bundle.loadClass(String)
	 */
	public void testLoadClass0002() throws Exception {
		Bundle bundle;
		Class clazz;

		bundle = context.installBundle(tcHome + "tb10.jar");

		try {
			clazz = bundle
					.loadClass("org.osgi.test.cases.framework.div.tb10.NonExistent");
			throw new BundleTestException(
					"Testing the method loadClass() with an installed bundle and a nonexistent class");
		}
		catch (ClassNotFoundException ex) {
			// This is an expected exception and can be ignored
		}

		bundle.uninstall();
	}

	/**
	 * Test the method loadClass() with a fragment bundle using a nonexistent
	 * class
	 * 
	 * @spec Bundle.loadClass(String)
	 */
	public void testLoadClass0003() throws Exception {
		Bundle bundle;
		Class clazz;

		bundle = context.installBundle(tcHome + "tb13.jar");

		try {
			clazz = bundle
					.loadClass("org.osgi.test.cases.framework.div.tb13.Nonexistent");
			throw new BundleTestException(
					"Testing the method loadClass() with a fragment bundle");
		}
		catch (ClassNotFoundException ex) {
			// This is an expected exception and can be ignored
		}

		bundle.uninstall();
	}

	/**
	 * Test the method loadClass() with a fragment bundle using a existing class
	 * 
	 * @spec Bundle.loadClass(String)
	 */
	public void testLoadClass0004() throws Exception {
		Bundle bundle;
		Class clazz;

		bundle = context.installBundle(tcHome + "tb13.jar");

		try {
			clazz = bundle
					.loadClass("org.osgi.test.cases.framework.div.tb13.Foo");
			throw new BundleTestException(
					"Testing the method loadClass() with a fragment bundle");
		}
		catch (ClassNotFoundException ex) {
			// This is an expected exception and can be ignored
		}

		bundle.uninstall();
	}

	/**
	 * Test the method after uninstall the bundle
	 * 
	 * @spec Bundle.loadClass(String)
	 */
	public void testLoadClass0005() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb10.jar");
		bundle.uninstall();

		try {
			bundle.loadClass("org.osgi.test.cases.framework.div.tb10.Foo");
			throw new BundleTestException(
					"Testing the method after uninstall the bundle");
		}
		catch (IllegalStateException ex) {
			// This is an expected exception and can be ignored
		}
	}

}