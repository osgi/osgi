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

package org.osgi.test.cases.packageadmin.tc5.tbc.PackageAdmin;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.cases.packageadmin.tc5.tbc.TestControl;

/**
 * 
 * Test the method org.osgi.service.packageadmin.PackageAdmin.getFragments().
 * 
 * @author left
 * @version $Revision$
 */
public class GetFragments {

	private TestControl	control;

	/**
	 * Creates a new GetFragments
	 * 
	 * @param _control the bundle control for this test
	 */
	public GetFragments(TestControl _control) {
		control = _control;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testGetFragments0001();
		testGetFragments0002();
		testGetFragments0003();
		testGetFragments0004();
	}

	/**
	 * Check the method invocation using a bundle with attached fragments
	 */
	public void testGetFragments0001() throws Exception {
		Bundle tb5;
		Bundle[] bundles;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.installTestBundles();

		tb5 = control.getTestBundle5();
		packageAdmin.resolveBundles(new Bundle[] {tb5});

		bundles = packageAdmin.getFragments(control.getTestBundle3());
		control.assertNotNull("Checking if the result is not null", bundles);
		control.assertEquals("Checking the number of returned bundles", 1,
				bundles.length);
		control.assertEquals("Checking the returned bundle",
				"org.osgi.test.cases.packageadmin.tc5.tb5", bundles[0]
						.getSymbolicName());

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Check the method invocation using a fragment bundle
	 */
	public void testGetFragments0002() throws Exception {
		Bundle tb5;
		Bundle[] bundles;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.installTestBundles();

		tb5 = control.getTestBundle5();
		packageAdmin.resolveBundles(new Bundle[] {tb5});

		bundles = packageAdmin.getFragments(tb5);
		control.assertNull("Checking if no bundle is returned", bundles);

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Check the method invocation using a fragment bundle with attached fragments
	 */
	public void testGetFragments0003() throws Exception {
		Bundle tb6;
		Bundle[] bundles;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.installTestBundles();

		tb6 = control.getContext().installBundle(control.getWebServer()+"tb6.jar");
		
		bundles = packageAdmin.getFragments(control.getTestBundle5());
		control.assertNull("Checking if no bundle is returned", bundles);

		tb6.uninstall();
		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Check the method invocation using a bundle without attached fragments
	 */
	public void testGetFragments0004() throws Exception {
		Bundle[] bundles;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.installTestBundles();

		bundles = packageAdmin.getFragments(control.getTestBundle1());
		control.assertNull("Checking if no bundle is returned", bundles);

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

}