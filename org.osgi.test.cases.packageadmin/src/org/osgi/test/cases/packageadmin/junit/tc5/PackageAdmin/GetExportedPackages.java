/*
 * $Id: GetExportedPackages.java 6461 2009-02-24 16:52:28Z tjwatson@us.ibm.com $
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
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * Test the method
 * org.osgi.service.packageadmin.PackageAdmin.getExportedPackages().
 * 
 * @author left
 * @version $Revision: 6461 $
 */
public class GetExportedPackages {

	private TestControl	control;

	/**
	 * Creates a new GetExportedPackages
	 * 
	 * @param _control the bundle control for this test
	 */
	public GetExportedPackages(TestControl _control) {
		control = _control;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testGetExportedPackages0001();
		testGetExportedPackages0002();
		testGetExportedPackages0003();
	}

	/**
	 * Test the return value when finding an nonexisting exported packages
	 */
	public void testGetExportedPackages0001() throws Exception {
		ExportedPackage[] exportedPackages;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		DefaultTestBundleControl
				.assertNull(
						"Testing if the packages already exists",
						packageAdmin
								.getExportedPackages("org.osgi.test.cases.packageadmin.tc5.tb1"));

		control.installTestBundles();

		exportedPackages = packageAdmin
				.getExportedPackages("org.osgi.test.cases.packageadmin.tc5.tb1");

		DefaultTestBundleControl
				.assertNotNull(
						"Testing the return value when finding an nonexisting exported packages",
						exportedPackages);

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Test the return value when finding an nonexisting exported package
	 */
	public void testGetExportedPackages0002() throws Exception {
		ExportedPackage[] exportedPackages;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		DefaultTestBundleControl
				.assertNull(
						"Testing if the packages already exists",
						packageAdmin
								.getExportedPackages("org.osgi.test.cases.packageadmin.tc5.nonexistent"));

		control.installTestBundles();

		exportedPackages = packageAdmin
				.getExportedPackages("org.osgi.test.cases.packageadmin.tc5.nonexistent");

		DefaultTestBundleControl
				.assertNull(
						"Testing the return value when finding an nonexisting exported package",
						exportedPackages);

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Test if the all exported package are returned. This method will check if
	 * the result of method invocation are two versions of the same package.
	 */
	public void testGetExportedPackages0003() throws Exception {
		ExportedPackage[] exportedPackages;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		DefaultTestBundleControl
				.assertNull(
						"Testing if the packages already exists",
						packageAdmin
								.getExportedPackages("org.osgi.test.cases.packageadmin.tc5.tb1"));

		control.installTestBundles();

		exportedPackages = packageAdmin
				.getExportedPackages("org.osgi.test.cases.packageadmin.tc5.tb1");

		DefaultTestBundleControl.assertEquals(
				"Checking the number of exported packages returned", 2,
				exportedPackages.length);

		if (exportedPackages[0].getSpecificationVersion().equals("1.0.0")) {
			DefaultTestBundleControl.assertEquals(
					"Checking the exported packages version order", "1.1.0",
					exportedPackages[1].getSpecificationVersion());
		}
		else
			if (exportedPackages[0].getSpecificationVersion().equals("1.1.0")) {
				DefaultTestBundleControl.assertEquals(
						"Checking the exported packages version order",
						"1.0.0", exportedPackages[1].getSpecificationVersion());
			}
			else {
				DefaultTestBundleControl.fail("Expected packages are not returned");
			}

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

}