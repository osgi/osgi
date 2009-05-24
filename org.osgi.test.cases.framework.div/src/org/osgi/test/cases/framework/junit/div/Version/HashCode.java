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

package org.osgi.test.cases.framework.junit.div.Version;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.test.cases.framework.junit.div.Bundle.BundleTestException;

/**
 * Test the method org.osgi.framework.Version.hashCode().
 * 
 * @version $Revision$
 */
public class HashCode {

	private BundleContext	context;
	private String			tcHome;

	/**
	 * Creates a new HashCode
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home path
	 */
	public HashCode(BundleContext _context, String _tcHome) {
		context = _context;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testHashCode0001();
		testHashCode0002();
	}

	/**
	 * Test the method hashCode() when the equals() returns true
	 * 
	 * @spec Version.hashCode();
	 */
	public void testHashCode0001() throws Exception {
		Version version1;
		Version version2;

		version1 = new Version(0, 0, 0);
		version2 = new Version(0, 0, 0);

		if (version1.hashCode() != version2.hashCode()) {
			throw new BundleTestException(
					"The method hashCode() has different return values when the method equals() returns true for two Version instances");
		}
	}

	/**
	 * Test the method hashCode() when the equals() returns false
	 * 
	 * @spec Version.hashCode();
	 */
	public void testHashCode0002() throws Exception {
		Version version1;
		Version version2;

		version1 = new Version(0, 0, 0);
		version2 = new Version(1, 0, 0);

		if (version1.hashCode() == version2.hashCode()) {
			throw new BundleTestException(
					"The method hashCode() has the same return value when the method equals() returns false for two Version instances");
		}
	}

}