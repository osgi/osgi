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
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.cases.packageadmin.tc5.tbc.TestControl;

/**
 * 
 * Test the method org.osgi.service.packageadmin.PackageAdmin.getBundles().
 * 
 * @author left
 * @version $Revision$
 */
public class GetBundles {

	private TestControl	control;

	/**
	 * Creates a new GetBundles
	 * 
	 * @param _control the bundle control for this test
	 */
	public GetBundles(TestControl _control) {
		control = _control;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testGetBundles0001();
		testGetBundles0002();
		testGetBundles0003();
		testGetBundles0004();
		testGetBundles0005();
		testGetBundles0006();
	}

	/**
	 * Check invocation with a existing bundle and a correct version range
	 * (the range used must return two bundles)
	 */
	public void testGetBundles0001() throws Exception {
		Bundle[] bundles;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.installTestBundles();

		bundles = packageAdmin.getBundles(TestControl.TEST_BUNDLE_1,
				"[1.0.0, 1.1.0]");
		control.assertNotNull("Checking if the result is not null", bundles);
		control.assertEquals("Checking the number of returned bundles", 2,
				bundles.length);
		control.assertEquals("Checking the bunder order", "1.1", bundles[0]
				.getHeaders().get(Constants.BUNDLE_VERSION));
		control.assertEquals("Checking the bunder order", "1.0", bundles[1]
				.getHeaders().get(Constants.BUNDLE_VERSION));

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Check invocation with a existing bundle and a correct version range
	 * (the range used must return only one bundle).
	 */
	public void testGetBundles0002() throws Exception {
		Bundle[] bundles;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.installTestBundles();

		bundles = packageAdmin.getBundles(TestControl.TEST_BUNDLE_1,
				"[1.0.1, 1.1.0]");
		control.assertNotNull("Checking if the result is not null", bundles);
		control.assertEquals("Checking the number of returned bundles", 1,
				bundles.length);
		control.assertEquals("Checking the bunder order", "1.1", bundles[0]
				.getHeaders().get(Constants.BUNDLE_VERSION));

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Check invocation with a existing bundle and a correct version range
	 * (the range used must return only one bundle).
	 */
	public void testGetBundles0003() throws Exception {
		Bundle[] bundles;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.installTestBundles();

		bundles = packageAdmin.getBundles(TestControl.TEST_BUNDLE_1,
				"[1.0.0, 1.0.9]");
		control.assertNotNull("Checking if the result is not null", bundles);
		control.assertEquals("Checking the number of returned bundles", 1,
				bundles.length);
		control.assertEquals("Checking the bunder order", "1.0", bundles[0]
				.getHeaders().get(Constants.BUNDLE_VERSION));

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Check invocation with a existing bundle and a incorrect version range
	 */
	public void testGetBundles0004() throws Exception {
		Bundle[] bundles;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.installTestBundles();

		bundles = packageAdmin.getBundles(TestControl.TEST_BUNDLE_1,
				"[2.0.0, 2.1.0]");
		control.assertNull("Checking if the result is null", bundles);

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Check invocation with a nonexistent bundle name and a correct version
	 * range
	 */
	public void testGetBundles0005() throws Exception {
		Bundle[] bundles;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.installTestBundles();

		bundles = packageAdmin.getBundles(
				"org.osgi.test.cases.packageadmin.tc5.tb1.nonexistent",
				"[1.0.0, 1.1.0]");
		control.assertNull("Checking if the result is null", bundles);

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

	/**
	 * Check invocation with a existent bundle name to get all versions
	 */
	public void testGetBundles0006() throws Exception {
		Bundle[] bundles;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		serviceReference = control.getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) control.getContext().getService(
				serviceReference);

		control.installTestBundles();

		bundles = packageAdmin.getBundles(TestControl.TEST_BUNDLE_1, null);
		control.assertNotNull("Checking if the result it not null", bundles);
		control.assertEquals("Checking the number of resulted bundles", 2,
				bundles.length);
		control.assertEquals("Checking the bundle order", "1.1", bundles[0]
				.getHeaders().get(Constants.BUNDLE_VERSION));
		control.assertEquals("Checking the bundle order", "1.0", bundles[1]
				.getHeaders().get(Constants.BUNDLE_VERSION));

		control.uninstallTestBundles();

		control.refreshPackageAdmin();

		control.getContext().ungetService(serviceReference);
	}

}