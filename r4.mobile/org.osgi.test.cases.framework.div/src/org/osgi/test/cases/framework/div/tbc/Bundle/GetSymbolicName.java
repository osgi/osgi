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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.test.service.TestCaseLink;

/**
 * Test the method org.osgi.framework.Bundle.getSymbolicName().
 * 
 * @version $Revision$
 */
public class GetSymbolicName {

	private BundleContext	context;
	private TestCaseLink	link;
	private String			tcHome;

	/**
	 * Creates a new GetSymbolicName
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home path
	 */
	public GetSymbolicName(BundleContext _context, TestCaseLink _link,
			String _tcHome) {
		context = _context;
		link = _link;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testGetSymbolicName0001();
		testGetSymbolicName0002();
		testGetSymbolicName0003();
		testGetSymbolicName0004();
	}

	/**
	 * Test the method getSymbolicName() with a symbolic name in the manifest
	 * 
	 * @spec Bundle.getSymbolicName()
	 */
	public void testGetSymbolicName0001() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb10.jar");

		if (!bundle.getSymbolicName().equals("org.osgi.test.cases.framework.div.tb10")) {
			throw new BundleTestException(
					"Testing the method getSymbolicName() with a symbolic name in the manifest");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method getSymbolicName() without a symbolic name in the manifest
	 * 
	 * @spec Bundle.getSymbolicName()
	 */
	public void testGetSymbolicName0002() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb11.jar");

		if (bundle.getSymbolicName() != null) {
			throw new BundleTestException(
					"Testing the method getSymbolicName() without a symbolic name in the manifest");
		}

		bundle.uninstall();
	}

	/**
	 * Test the method getSymbolicName() after uninstall the bundle (with a
	 * symbolic name in the manifest)
	 * 
	 * @spec Bundle.getSymbolicName()
	 */
	public void testGetSymbolicName0003() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb10.jar");
		bundle.uninstall();

		if (!bundle.getSymbolicName().equals("org.osgi.test.cases.framework.div.tb10")) {
			throw new BundleTestException(
					"Testing the method getSymbolicName() after uninstall the bundle (with a symbolic name in the manifest)");
		}
	}

	/**
	 * Test the method getSymbolicName() after uninstall the bundle (without a
	 * symbolic name in the manifest)
	 * 
	 * @spec Bundle.getSymbolicName()
	 */
	public void testGetSymbolicName0004() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb11.jar");
		bundle.uninstall();

		if (bundle.getSymbolicName() != null) {
			throw new BundleTestException(
					"Testing the method getSymbolicName() after uninstall the bundle (without a symbolic name in the manifest)");
		}
	}

}