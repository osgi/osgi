/*
 * $Header$
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
 * This class tests the method org.osgi.framework.Bundle.getEntryPath().
 * 
 * @author left
 * @version $Revision$
 */
public class GetEntry {

	private BundleContext	context;
	private String			tcHome;
	private TestCaseLink	link;

	/**
	 * Creates a new GetEntry
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home
	 */
	public GetEntry(BundleContext _context, TestCaseLink _link, String _tcHome) {
		context = _context;
		link = _link;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testGetEntry001();
		testGetEntry002();
		testGetEntry003();
	}

	/**
	 * Test the method invocation with an existing entry
	 * 
	 * @spec Bundle.getEntry(String)
	 */
	public void testGetEntry001() throws Exception {
		Bundle bundle;
		URL url;

		bundle = context.installBundle(tcHome + "tb10.jar");

		url = bundle
				.getEntry("/org/osgi/test/cases/framework/div/tb10/Foo.class");
		if (url == null) {
			throw new BundleTestException(
					"Testing the method invocation with an existing entry");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method invocation with an nonexistent entries
	 * 
	 * @spec Bundle.getEntry(String)
	 */
	public void testGetEntry002() throws Exception {
		Bundle bundle;
		URL url;

		bundle = context.installBundle(tcHome + "tb10.jar");

		url = bundle
				.getEntry("/org/osgi/test/cases/framework/div/tb10/Nonexistent");
		if (url != null) {
			throw new BundleTestException(
					"Testing the method invocation with an nonexistent entries");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method invocation with an uninstalled bundle
	 * 
	 * @spec Bundle.getEntry(String)
	 */
	public void testGetEntry003() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb10.jar");
		bundle.uninstall();

		try {
			bundle
					.getEntry("/org/osgi/test/cases/framework/div/tb10/Nonexistent");
			throw new BundleTestException(
					"Testing the method invocation with an uninstalled bundle");
		}
		catch (IllegalStateException ex) {
			// This is an expected exception and can be ignored
		}
	}

}