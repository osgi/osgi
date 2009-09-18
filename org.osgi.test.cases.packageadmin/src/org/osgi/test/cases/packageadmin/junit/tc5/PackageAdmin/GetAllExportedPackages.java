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

package org.osgi.test.cases.packageadmin.junit.tc5.PackageAdmin;

import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.cases.packageadmin.junit.tc5.TestControl;

/**
 * 
 * Test the method
 * org.osgi.service.packageadmin.PackageAdmin.getAllExportedPackages().
 * 
 * @author left
 * @version $Revision$
 */
public class GetAllExportedPackages {

	private TestControl	control;

	/**
	 * Creates a new GetAllExportedPackages
	 * 
	 * @param _control the bundle control for this test
	 */
	public GetAllExportedPackages(TestControl _control) {
		control = _control;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testGetAllExportedPackages0001();
		testGetAllExportedPackages0002();
		testGetAllExportedPackages0003();
	}

	/**
	 * Test the return value when finding an nonexisting exported packages
	 */
	public void testGetAllExportedPackages0001() throws Exception {
		ExportedPackage[] exportedPackages;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		// TODO: Activate this test
		//DefaultTestBundleControl.assertNull("Testing if the packages already exists",
		//		packageAdmin.getAllExportedPackages("org.osgi.test.cases.packageadmin.tc5.tb1"));

		control.installTestBundles();

		// TODO: Activate this test
		//exportedPackages = packageAdmin
		//		.getAllExportedPackages("org.osgi.test.cases.packageadmin.tc5.tb1");

		// 'TODO: Activate this test
		//control
		//		.assertNotNull(
		//				"Testing the return value when finding an nonexisting exported
		// packages",
		//				exportedPackages);

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Test the return value when finding an nonexisting exported package
	 */
	public void testGetAllExportedPackages0002() throws Exception {
		ExportedPackage[] exportedPackages;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		// TODO: Activate this test
		// control
		// 		.assertNull(
		// 				"Testing if the packages already exists",
		// 				packageAdmin
		// 						.getAllExportedPackages("org.osgi.test.cases.packageadmin.tc5.nonexistent"));

		control.installTestBundles();

		// TODO: Activate this test
		// exportedPackages = packageAdmin
		//		.getAllExportedPackages("org.osgi.test.cases.packageadmin.tc5.nonexistent");

		// TODO: Activate this test
		// control
		//		.assertNull(
		//				"Testing the return value when finding an nonexisting exported
		// package",
		//				exportedPackages);

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Test if the all exported package are returned. This method will check if
	 * the result of method invocation are two versions of the same package.
	 */
	public void testGetAllExportedPackages0003() throws Exception {
		ExportedPackage[] exportedPackages;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		// TODO: Activate this test
		// DefaultTestBundleControl.assertNull("Testing if the packages already exists",
		//		packageAdmin.getAllExportedPackages("org.osgi.test.cases.packageadmin.tc5.tb1"));

		control.installTestBundles();

		// TODO: Activate this test
		// exportedPackages = packageAdmin
		//		.getAllExportedPackages("org.osgi.test.cases.packageadmin.tc5.tb1");

		// TODO: Activate this test
		//DefaultTestBundleControl.assertEquals(
		//		"Checking the number of exported packages returned", 2,
		//		exportedPackages.length);
		//control
		//		.assertTrue("Checking the exported packages version order",
		//				(exportedPackages[0].getSpecificationVersion().equals(
		//						"1.0.0") && exportedPackages[1]
		//						.getSpecificationVersion().equals("1.1.0"))
		//						|| (exportedPackages[1]
		//								.getSpecificationVersion().equals(
		//										"1.0.0") && exportedPackages[0]
		//								.getSpecificationVersion().equals(
		//										"1.1.0")));

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

}