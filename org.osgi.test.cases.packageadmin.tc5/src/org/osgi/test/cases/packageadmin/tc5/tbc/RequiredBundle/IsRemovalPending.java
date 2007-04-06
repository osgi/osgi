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

package org.osgi.test.cases.packageadmin.tc5.tbc.RequiredBundle;

import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;
import org.osgi.test.cases.packageadmin.tc5.tbc.TestControl;

/**
 * 
 * Test the method
 * org.osgi.service.packageadmin.RequiredBundle.isRemovalPending().
 * 
 * @author left
 * @version $Revision$
 */
public class IsRemovalPending {

	private TestControl	control;

	/**
	 * Creates a new IsRemovalPending
	 * 
	 * @param _control the bundle control for this test
	 */
	public IsRemovalPending(TestControl _control) {
		control = _control;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testIsRemovalPending0001();
		testIsRemovalPending0002();
		testIsRemovalPending0003();
	}

	/**
	 * Check the invocation with a started required bundle
	 */
	public void testIsRemovalPending0001() throws Exception {
		PackageAdmin packageAdmin;
		RequiredBundle[] requiredBundles;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.installTestBundles();

		requiredBundles = packageAdmin
				.getRequiredBundles("org.osgi.test.cases.packageadmin.tc5.tb1");
		control.assertTrue("Checking if the bundle removal is not pending",
				!requiredBundles[0].isRemovalPending());

		control.uninstallTestBundles();

		control.refreshPackageAdmin();
	}

	/**
	 * Check the invocation with a uninstalled required bundle
	 */
	public void testIsRemovalPending0002() throws Exception {
		PackageAdmin packageAdmin;
		RequiredBundle[] requiredBundles;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.installTestBundles();

		requiredBundles = packageAdmin
				.getRequiredBundles("org.osgi.test.cases.packageadmin.tc5.tb1");

		control.uninstallTestBundles();

		control.assertTrue("Checking if the bundle removal is pending",
				requiredBundles[0].isRemovalPending());

		control.refreshPackageAdmin();
	}

	/**
	 * Check the invocation with a stale required bundle
	 */
	public void testIsRemovalPending0003() throws Exception {
		PackageAdmin packageAdmin;
		RequiredBundle[] requiredBundles;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.installTestBundles();

		requiredBundles = packageAdmin
				.getRequiredBundles(TestControl.TEST_BUNDLE_1);

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.assertTrue("Checking if the bundle removal is pending",
				requiredBundles[0].isRemovalPending());
	}

}