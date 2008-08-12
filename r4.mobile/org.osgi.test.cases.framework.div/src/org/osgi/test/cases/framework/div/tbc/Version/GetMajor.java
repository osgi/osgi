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

package org.osgi.test.cases.framework.div.tbc.Version;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.test.cases.framework.div.tbc.Bundle.BundleTestException;
import org.osgi.test.service.TestCaseLink;

/**
 * Test the method org.osgi.framework.Version.getMajor().
 * 
 * @version $Revision$
 */
public class GetMajor {

	private BundleContext	context;
	private TestCaseLink	link;
	private String			tcHome;

	/**
	 * Creates a new GetMajor
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home path
	 */
	public GetMajor(BundleContext _context, TestCaseLink _link, String _tcHome) {
		context = _context;
		link = _link;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests for the method GetMajor
	 */
	public void run() throws Exception {
		testGetMajor0001();
		testGetMajor0002();
	}

	/**
	 * Test the method getMajor() using the constructor Version(int,int,int)
	 * 
	 * @spec Version.getMajor()
	 */
	public void testGetMajor0001() throws Exception {
		Version version;

		version = new Version(1, 1, 1);

		if (version.getMajor() != 1) {
			throw new BundleTestException(
					"Testing the method getMajor() using the constructor Version(int,int,int)");
		}
	}

	/**
	 * Test the method getMajor() using the constructor Version(String)
	 * 
	 * @spec Version.getMajor()
	 */
	public void testGetMajor0002() throws Exception {
		Version version;

		version = new Version("1.1.1");

		if (version.getMajor() != 1) {
			throw new BundleTestException(
					"Testing the method getMajor() using the constructor Version(String)");
		}
	}

}