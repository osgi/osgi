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

package org.osgi.test.cases.framework.div.tbc.BundleException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.test.service.TestCaseLink;

/**
 * 
 * This class tests the method org.osgi.framework.BundleException.getCause().
 * 
 * @version $Revision$
 */
public class GetCause {

	private BundleContext	context;
	private String			tcHome;
	private TestCaseLink	link;

	/**
	 * Creates a new GetCause
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home
	 */
	public GetCause(BundleContext _context, TestCaseLink _link, String _tcHome) {
		context = _context;
		link = _link;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testGetCause0001();
		testGetCause0002();
	}

	/**
	 * Test the method getCause() with a cause
	 * 
	 * @spec BundleException.getCause()
	 */
	public void testGetCause0001() throws Exception {
		BundleException bundleException;
		Exception exception;

		exception = new Exception();
		bundleException = new BundleException("", exception);

		if (bundleException.getCause() != exception) {
			throw new BundleExceptionTestException(
					"Testing the method getCause() with a cause");
		}
	}

	/**
	 * Test the method getCause() without a cause
	 * 
	 * @spec BundleException.getCause()
	 */
	public void testGetCause0002() throws Exception {
		BundleException bundleException;

		bundleException = new BundleException("");

		if (bundleException.getCause() != null) {
			throw new BundleExceptionTestException(
					"Testing the method getCause() without a cause");
		}
	}

}