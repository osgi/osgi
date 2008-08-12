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

package org.osgi.test.cases.packageadmin.tc5.tbc.ExportedPackage;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.cases.packageadmin.tc5.tbc.TestControl;

/**
 * 
 * Test the method
 * org.osgi.service.packageadmin.ExportedPackage.getImportingBundles().
 * 
 * @author left
 * @version $Revision$
 */
public class GetImportingBundles {

	private TestControl	control;

	/**
	 * Creates a new GetImportingBundles
	 * 
	 * @param _control the bundle control for this test
	 */
	public GetImportingBundles(TestControl _control) {
		control = _control;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testGetImportingBundles0001();
		testGetImportingBundles0002();
	}

	/**
	 * Check the return value with a non stale bundle
	 */
	public void testGetImportingBundles0001() throws Exception {
		Bundle[] bundles;
		ExportedPackage exportedPackage;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.assertNull("Checking if the packages already exists",
				packageAdmin.getExportedPackage(TestControl.TEST_BUNDLE_1));

		control.installTestBundles();

		exportedPackage = packageAdmin
				.getExportedPackage(TestControl.TEST_BUNDLE_1);

		bundles = exportedPackage.getImportingBundles();
		control.assertNotNull("Checking the returned importing bundles",
				bundles);
		control.assertEquals("Checking the number of importing bundles", 2,
				bundles.length);
		control.assertEquals("Checking the first importing bundle", control
				.getTestBundle2(), bundles[0]);
		control.assertEquals("Checking the second importing bundle", control
				.getTestBundle4(), bundles[1]);

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Check the return value with a stale bundle
	 */
	public void testGetImportingBundles0002() throws Exception {
		Bundle[] bundles;
		ExportedPackage exportedPackage;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.assertNull("Checking if the packages already exists",
				packageAdmin.getExportedPackage(TestControl.TEST_BUNDLE_1));

		control.installTestBundles();

		exportedPackage = packageAdmin
				.getExportedPackage(TestControl.TEST_BUNDLE_1);

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		bundles = exportedPackage.getImportingBundles();
		control.assertNull("Checking the returned importing bundles", bundles);

		control.getContext().ungetService(serviceReference);
	}

}