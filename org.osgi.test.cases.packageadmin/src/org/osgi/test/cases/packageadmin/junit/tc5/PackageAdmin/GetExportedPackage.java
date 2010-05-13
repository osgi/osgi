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
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * Test the method
 * org.osgi.service.packageadmin.PackageAdmin.getExportedPackage().
 * 
 * @author left
 * @version $Id$
 */
public class GetExportedPackage {

	private TestControl	control;

	/**
	 * Creates a new GetExportedPackage
	 * 
	 * @param _control the bundle control for this test
	 */
	public GetExportedPackage(TestControl _control) {
		control = _control;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testGetExportedPackage0001();
		testGetExportedPackage0002();
		testGetExportedPackage0003();
	}

	/**
	 * Check the return value when finding an existing exported package
	 */
	public void testGetExportedPackage0001() throws Exception {
		ExportedPackage exportedPackage;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		DefaultTestBundleControl
				.assertNull(
						"Checking if the packages already exists",
						packageAdmin
								.getExportedPackage("org.osgi.test.cases.packageadmin.tc5.tb1"));

		control.installTestBundles();

		exportedPackage = packageAdmin
				.getExportedPackage("org.osgi.test.cases.packageadmin.tc5.tb1");

		DefaultTestBundleControl.assertNotNull("Check if no exported package is returned",
				exportedPackage);

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Check the return value when finding an nonexisting exported package
	 */
	public void testGetExportedPackage0002() throws Exception {
		ExportedPackage exportedPackage;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		DefaultTestBundleControl
				.assertNull(
						"Checking if the packages already exists",
						packageAdmin
								.getExportedPackage("org.osgi.test.cases.packageadmin.tc5.tb1"));

		control.installTestBundles();

		exportedPackage = packageAdmin
				.getExportedPackage("org.osgi.test.cases.packageadmin.tc5.nonexistent");

		DefaultTestBundleControl.assertNull("Checking if no exported package is returned",
				exportedPackage);

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Check if the exported package with the highest version is returned
	 */
	public void testGetExportedPackage0003() throws Exception {
		ExportedPackage exportedPackage;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		DefaultTestBundleControl
				.assertNull(
						"Checking if the packages already exists",
						packageAdmin
								.getExportedPackage("org.osgi.test.cases.packageadmin.tc5.tb1"));

		control.installTestBundles();

		exportedPackage = packageAdmin
				.getExportedPackage("org.osgi.test.cases.packageadmin.tc5.tb1");

		DefaultTestBundleControl.assertEquals("Checking the returned exported package version",
				"1.1.0", exportedPackage.getSpecificationVersion());

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

}