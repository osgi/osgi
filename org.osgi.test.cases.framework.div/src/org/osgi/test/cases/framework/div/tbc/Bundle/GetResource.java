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

import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.test.service.TestCaseLink;

/**
 * 
 * This class tests the method org.osgi.framework.Bundle.getResource()
 * 
 * @author left
 * @version $Revision$
 */
public class GetResource {

	private BundleContext	context;
	private String			tcHome;
	private TestCaseLink	link;

	/**
	 * Creates a new GetResource
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home path
	 */
	public GetResource(BundleContext _context, TestCaseLink _link,
			String _tcHome) {
		context = _context;
		link = _link;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testGetResource0001();
		testGetResource0002();
		testGetResource0003();
		testGetResource0004();
		testGetResource0005();
	}

	/**
	 * Test the method invocation with an existing resource (using a absolute
	 * path)
	 * 
	 * @spec Bundle.getResource(String)
	 */
	public void testGetResource0001() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb10.jar");

		if (bundle
				.getResource("/org/osgi/test/cases/framework/div/tb10/Foo.class") == null) {
			throw new BundleTestException(
					"Testing the method invocation with an existing resource (using a absolute path)");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method invocation with a bundle fragment and a nonexistent
	 * resource
	 * 
	 * @spec Bundle.getResource(String)
	 */
	public void testGetResource0002() throws Exception {
		Bundle bundle;
		URL url;

		bundle = context.installBundle(tcHome + "tb13.jar");

		url = bundle
				.getResource("/org/osgi/test/cases/framework/div/tb13/Nonexistent");
		if (url != null) {
			throw new BundleTestException(
					"A fragment bundle cannot return a resource using the method getResource()");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method invocation with a bundle fragment and an existing
	 * resource
	 * 
	 * @spec Bundle.getResource(String)
	 */
	public void testGetResource0003() throws Exception {
		Bundle bundle;
		URL url;

		bundle = context.installBundle(tcHome + "tb13.jar");

		url = bundle
				.getResource("/org/osgi/test/cases/framework/div/tb13/Foo.class");
		if (url != null) {
			throw new BundleTestException(
					"A fragment bundle cannot return a resource using the method getResource()");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method invocation with a nonexistent resource
	 * 
	 * @spec Bundle.getResource(String)
	 */
	public void testGetResource0004() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb10.jar");

		if (bundle
				.getResource("/org/osgi/test/cases/framework/div/tb10/Nonexistent.class") != null) {
			throw new BundleTestException(
					"Testing the method invocation with a nonexiste resource");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method invocation after uninstall the bundle
	 * 
	 * @spec Bundle.getResource(String)
	 */
	public void testGetResource0005() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb10.jar");
		bundle.uninstall();

		try {
			bundle
					.getResource("/org/osgi/test/cases/framework/div/tb10/Foo.class");
			throw new BundleTestException(
					"Testing  the method invocation after uninstall the bundle");
		}
		catch (IllegalStateException ex) {
			// This is an expected exception and can be ignored
		}
	}

}