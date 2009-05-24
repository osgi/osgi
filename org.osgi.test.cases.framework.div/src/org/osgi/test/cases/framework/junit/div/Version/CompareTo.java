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

/**
 * Test the method org.osgi.framework.Version.compareTo().
 * 
 * @version $Revision$
 */
public class CompareTo {

	private BundleContext	context;
	private String			tcHome;

	/**
	 * Creates a new CompareTo
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home path
	 */
	public CompareTo(BundleContext _context, String _tcHome) {
		context = _context;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testCompareTo0001();
		testCompareTo0002();
		testCompareTo0003();
		testCompareTo0004();
	}

	/**
	 * Test the method compareTo() with first version number less than second
	 * version number
	 * 
	 * @spec Version.compareTo(Version);
	 */
	public void testCompareTo0001() throws Exception {
		Version version1;
		Version version2;

		version1 = new Version(1, 1, 1);
		version2 = new Version(2, 1, 1);

		if (version1.compareTo(version2) >= 0) {
			throw new VersionTestException(
					"Testing the method compareTo() with first version number less than second version number");
		}
	}

	/**
	 * Test the method compareTo() with first version number greater than second
	 * version number
	 * 
	 * @spec Version.compareTo(Version);
	 */
	public void testCompareTo0002() throws Exception {
		Version version1;
		Version version2;

		version1 = new Version(2, 1, 1);
		version2 = new Version(1, 1, 1);

		if (version1.compareTo(version2) <= 0) {
			throw new VersionTestException(
					"Testing the method compareTo() with first version number greater than second version number");
		}
	}

	/**
	 * Test the method compareTo() with same version numbers
	 * 
	 * @spec Version.compareTo(Version);
	 */
	public void testCompareTo0003() throws Exception {
		Version version1;
		Version version2;

		version1 = new Version(1, 1, 1);
		version2 = new Version(1, 1, 1);

		if (version1.compareTo(version2) != 0) {
			throw new VersionTestException(
					"Testing the method compareTo() with same version numbers");
		}
	}

	/**
	 * Test the method compareTo() with an incorrect object
	 * 
	 * @spec Version.compareTo(Version);
	 */
	public void testCompareTo0004() throws Exception {
		String incorrect;
		Version version;

		incorrect = "";
		version = new Version(1, 1, 1);

		try {
			version.compareTo(incorrect);
			throw new VersionTestException(
					"Testing the method compareTo() with an incorrect object");
		}
		catch (ClassCastException ex) {
			// This is an expected exception and can be ignored
		}
	}

}