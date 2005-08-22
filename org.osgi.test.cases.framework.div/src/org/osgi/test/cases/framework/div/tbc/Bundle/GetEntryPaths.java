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

import java.util.Enumeration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.test.service.TestCaseLink;

/**
 * 
 * This class tests the method org.osgi.framework.Bundle.getEntryPaths().
 * 
 * @author left
 * @version $Revision$
 */
public class GetEntryPaths {

	private BundleContext	context;
	private String			tcHome;
	private TestCaseLink	link;

	/**
	 * Creates a new GetEntryPaths
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home
	 */
	public GetEntryPaths(BundleContext _context, TestCaseLink _link,
			String _tcHome) {
		context = _context;
		link = _link;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testGetEntryPaths0001();
		testGetEntryPaths0002();
		testGetEntryPaths0003();
	}

	/**
	 * Test the method invocation with existing entries
	 * 
	 * @spec Bundle.getEntryPaths(String)
	 */
	public void testGetEntryPaths0001() throws Exception {
		int count;
		Bundle bundle;
		Enumeration enumeration;
		String entryPath;
		String[] expectedEntryPaths = {
				"org/osgi/test/cases/framework/div/tb10/Activator.class",
				"org/osgi/test/cases/framework/div/tb10/Bar.class",
				"org/osgi/test/cases/framework/div/tb10/Foo.class",
				"org/osgi/test/cases/framework/div/tb10/TestService.class",
				"org/osgi/test/cases/framework/div/tb10/TestServiceImpl.class"};

		bundle = context.installBundle(tcHome + "tb10.jar");

		enumeration = bundle
				.getEntryPaths("/org/osgi/test/cases/framework/div/tb10");
		if (enumeration == null) {
			throw new BundleTestException("Check if some resource is returned");
		}

		count = 0;
		while (enumeration.hasMoreElements()) {
			entryPath = (String) enumeration.nextElement();

			for (int i = 0; i < expectedEntryPaths.length; i++) {
				if (entryPath.equals(expectedEntryPaths[i])) {
					count++;
				}
			}
		}

		if (count != expectedEntryPaths.length) {
			throw new BundleTestException("Checking the returned entries");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method invocation with nonexistent entries
	 * 
	 * @spec Bundle.getEntryPaths(String)
	 */
	public void testGetEntryPaths0002() throws Exception {
		Bundle bundle;
		Enumeration enumeration;

		bundle = context.installBundle(tcHome + "tb10.jar");

		enumeration = bundle
				.getEntryPaths("/org/osgi/test/cases/framework/div/tb10/nonexistent");
		if (enumeration != null) {
			throw new BundleTestException(
					"Testing the method invocation with nonexistent entries");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method invocation with an uninstalled bundle
	 * 
	 * @spec Bundle.getEntryPaths(String)
	 */
	public void testGetEntryPaths0003() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb10.jar");
		bundle.uninstall();

		try {
			bundle
					.getEntryPaths("/org/osgi/test/cases/framework/div/tb10/incorrect");
			throw new BundleTestException(
					"Testing the method invocation with an uninstalled bundle");
		}
		catch (IllegalStateException ex) {
			// Ignore this exception
		}
	}

}