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

package org.osgi.test.cases.framework.div.tbc.BundleContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.test.service.TestCaseLink;

/**
 * 
 * This class tests the method
 * org.osgi.framework.BundleContext.registerService().
 * 
 * @version $Revision$
 */
public class RegisterService {

	private BundleContext	context;
	private String			tcHome;
	private TestCaseLink	link;

	/**
	 * Creates a new RegisterService instance
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home
	 */
	public RegisterService(BundleContext _context, TestCaseLink _link,
			String _tcHome) {
		context = _context;
		link = _link;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testRegisterService001();
	}

	/**
	 * Test the behavior of the method registerService() when the service class
	 * is imported from a shared package.
	 * 
	 * @spec BundleContext.registerService(String,Object,Dictionary)
	 */
	public void testRegisterService001() throws Exception {
		Bundle tb24a;
		Bundle tb24b;
		Bundle tb24c;
		
		tb24a = context.installBundle(tcHome+"tb24a.jar");
		tb24b = context.installBundle(tcHome+"tb24b.jar");
		tb24c = context.installBundle(tcHome+"tb24c.jar");
		
		tb24a.start();
		tb24b.start();
		
		try {
			tb24c.start();
			tb24c.stop();
		} catch (BundleException ex) {
			throw new BundleTestException("A bundle can register a service when the package is shared");
		} finally {
			tb24b.stop();
			tb24a.stop();
			
			tb24c.uninstall();
			tb24b.uninstall();
			tb24a.uninstall();
		}
	}

}