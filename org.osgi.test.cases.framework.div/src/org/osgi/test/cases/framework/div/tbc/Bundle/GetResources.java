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

import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.test.service.TestCaseLink;

/**
 * This class tests the method org.osgi.framework.Bundle.getResources().
 * 
 * @author left
 * @version $Revision$
 */
public class GetResources {

	private BundleContext	context;
	private String			tcHome;
	private TestCaseLink	link;

	/**
	 * Creates a new GetResources
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home path
	 */
	public GetResources(BundleContext _context, TestCaseLink _link,
			String _tcHome) {
		context = _context;
		link = _link;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testGetResources0001();
		testGetResources0002();
		testGetResources0003();
		testGetResources0004();
		testGetResources0005();
	}

	/**
	 * Test the method getResources() with an existing resource path (using a
	 * absolute path)
	 * 
	 * @spec Bundle.getResources(String)
	 */
	public void testGetResources0001() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb10.jar");

		if (bundle
				.getResources("/org/osgi/test/cases/framework/div/tb10/Foo.class") == null) {
			throw new BundleTestException(
					"Testing the method getResources() with an existing resource path (using a absolute path)");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method getResources() with a bundle fragment and an existing
	 * resource
	 * 
	 * @spec Bundle.getResources(String)
	 */
	public void testGetResources0002() throws Exception {
		Bundle bundle;
		Enumeration enumeration;

		bundle = context.installBundle(tcHome + "tb13.jar");

		enumeration = bundle
				.getResources("/org/osgi/test/cases/framework/div/tb13/Foo.class");
		if (enumeration != null) {
			throw new BundleTestException(
					"A fragment bundle cannot return a resource using the method getResources()");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method getResources() with a bundle fragment and a nonexistent
	 * resource
	 * 
	 * @spec Bundle.getResources(String)
	 */
	public void testGetResources0003() throws Exception {
		Bundle bundle;
		Enumeration enumeration;

		bundle = context.installBundle(tcHome + "tb13.jar");

		enumeration = bundle
				.getResources("/org/osgi/test/cases/framework/div/tb13/Nonexistent");
		if (enumeration != null) {
			throw new BundleTestException(
					"A fragment bundle cannot return a resource using the method getResources()");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method getResources() with a nonexistent resource path
	 * 
	 * @spec Bundle.getResources(String)
	 */
	public void testGetResources0004() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb10.jar");

		if (bundle.getResources(
				"/test/cases/framework/div/nonexistent/Nonexistent.class")
				!= null) {
			throw new BundleTestException(
					"Testing the method getResources() with a nonexistent resource path");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method getResources() after uninstall the bundle
	 * 
	 * @spec Bundle.getResources(String)
	 */
	public void testGetResources0005() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb11.jar");
		bundle.uninstall();

		try {
			bundle.getResources("/test/cases/framework/div/tb10/Foo.class");
			throw new BundleTestException(
					"Testing the method getResource() after uninstall the bundle");
		}
		catch (IllegalStateException ex) {
			// This is an expected exception and can be ignored
		}
	}

}