/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2007). All Rights Reserved.
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
import org.osgi.test.cases.util.Logger;

/**
 * Test the method org.osgi.framework.Bundle.getBundleContext().
 * 
 * @version $Revision$
 */
public class GetBundleContext {

	private BundleContext	context;
	private String			tcHome;
	private Logger			logger;

	/**
	 * Creates a new GetBundleContext
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home path
	 */
	public GetBundleContext(BundleContext _context, Logger _logger,
			String _tcHome) {
		context = _context;
		logger = _logger;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testGetBundleContext0001();
		testGetBundleContext0002();
		testGetBundleContext0003();
		testGetBundleContext0004();
	}

	/**
	 * Test the method getBundleContext() to see if it is the context of the requested bundle
	 * 
	 * @spec Bundle.getBundleContext()
	 */
	public void testGetBundleContext0001() throws Exception {
		Bundle bundle;

		bundle = context.installBundle(tcHome + "tb10.jar");
		try {
			logger.assertNull("BundleContext for installed bundle must be null", bundle.getBundleContext());
			
			bundle.start();
			logger.assertNotNull("BundleContext for started bundle must not be null", bundle.getBundleContext());
			
			logger.assertEquals("Bundle id via BundleContext must equal original id", bundle.getBundleId(), bundle.getBundleContext().getBundle().getBundleId());
			
			bundle.stop();
			logger.assertNull("BundleContext for stopped bundle must be null", bundle.getBundleContext());
		}
		finally {
			bundle.uninstall();
			logger.assertNull("BundleContext for uninstalled bundle must be null", bundle.getBundleContext());
		}
		
	}

	/**
	 * Test the method getBundleContext() on the test case
	 * 
	 * @spec Bundle.getBundleContext()
	 */
	public void testGetBundleContext0002() throws Exception {
		logger.assertEquals("BundleContext for test case should match context passed to activator", context, context.getBundle().getBundleContext());
	}

	/**
	 * Test the method getBundleContext() on the System Bundle
	 * 
	 * @spec Bundle.getBundleContext()
	 */
	public void testGetBundleContext0003() throws Exception {
		Bundle bundle;
		
		bundle = context.getBundle(0);
		
		logger.assertNotNull("BundleContext for system bundle must not be null", bundle.getBundleContext());

		logger.assertEquals("Bundle id via BundleContext must equal zero", 0L, bundle.getBundleContext().getBundle().getBundleId());
	}

	/**
	 * Test the method getBundleContext() on a fragment
	 * 
	 * @spec Bundle.getBundleContext()
	 */
	public void testGetBundleContext0004() throws Exception {
		Bundle host, fragment;
		
		fragment = context.installBundle(tcHome + "tb18.jar");
		host = context.installBundle(tcHome + "tb17.jar");

		try {
			host.start(); 	// resolve the bundles
			logger.assertNotNull("BundleContext for host bundle must not be null", host.getBundleContext());
			logger.assertNull("BundleContext for fragment bundle must be null", fragment.getBundleContext());
			
			host.stop();
			logger.assertNull("BundleContext for stopped host bundle must be null", host.getBundleContext());
			logger.assertNull("BundleContext for fragment bundle must be null", fragment.getBundleContext());
		}
		finally {
			fragment.uninstall();
			host.uninstall();
		}
	}
}