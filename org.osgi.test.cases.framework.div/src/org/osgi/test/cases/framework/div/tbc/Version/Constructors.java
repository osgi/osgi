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

package org.osgi.test.cases.framework.div.tbc.Version;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.test.service.TestCaseLink;

/**
 * 
 * This class tests the constructors of class org.osgi.framework.Version.
 * 
 * @version $Revision$
 */
public class Constructors {

	private BundleContext	context;
	private String			tcHome;
	private TestCaseLink	link;

	/**
	 * Creates a new Constructors
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home
	 */
	public Constructors(BundleContext _context, TestCaseLink _link,
			String _tcHome) {
		context = _context;
		link = _link;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testConstructors0001();
		testConstructors0002();
		testConstructors0003();
		testConstructors0004();
		testConstructors0005();
		testConstructors0006();
	}

	/**
	 * Test the Version constructor with legal parameters
	 * 
	 * @spec Version.<init>(int,int,int)
	 */
	public void testConstructors0001() throws Exception {
		Version version;

		version = new Version(0, 0, 0);
	}

	/**
	 * Test the Version constructor with legal parameters
	 * 
	 * @spec Version.<init>(int,int,int)
	 */
	public void testConstructors0002() throws Exception {
		Version version;

		version = new Version(0, 0, 0, "a");
	}

	/**
	 * Test the Version constructor with legal parameters
	 * 
	 * @spec Version.<init>(int,int,int)
	 */
	public void testConstructors0003() throws Exception {
		Version version;

		version = new Version("0.0.0");
	}

	/**
	 * Test the Version constructor with illegal parameters
	 * 
	 * @spec Version.<init>(int,int,int)
	 */
	public void testConstructors0004() throws Exception {
		Version version;

		try {
			version = new Version(-1, -1, -1);
			throw new VersionTestException("Version created with illegal constructors");
		} catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}
	}

	/**
	 * Test the Version constructor with legal parameters
	 * 
	 * @spec Version.<init>(int,int,int)
	 */
	public void testConstructors0005() throws Exception {
		Version version;

		try {
			version = new Version(null);
			throw new VersionTestException("Version created with illegal constructors");
		} catch (Exception ex) {
			// This is an expected exception and may be ignored
		}
	}

	/**
	 * Test the Version constructor with legal parameters
	 * 
	 * @spec Version.<init>(int,int,int)
	 */
	public void testConstructors0006() throws Exception {
		Version version;

		try {
			version = new Version("");
			throw new VersionTestException("Version created with illegal constructors");
		} catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}
	}

}