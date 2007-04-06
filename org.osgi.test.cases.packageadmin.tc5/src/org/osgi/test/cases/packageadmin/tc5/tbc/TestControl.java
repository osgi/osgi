/*
 * $Id$
 * 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such org.osgi.test.cases.packageadmin.tc5 third party may or may not be org.osgi.test.cases.packageadmin.tc5 member of the OSGi
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
package org.osgi.test.cases.packageadmin.tc5.tbc;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.cases.packageadmin.tc5.tbc.ExportedPackage.GetImportingBundles;
import org.osgi.test.cases.packageadmin.tc5.tbc.PackageAdmin.Constants;
import org.osgi.test.cases.packageadmin.tc5.tbc.PackageAdmin.GetExportedPackages;
import org.osgi.test.cases.packageadmin.tc5.tbc.PackageAdmin.GetBundle;
import org.osgi.test.cases.packageadmin.tc5.tbc.PackageAdmin.GetBundleType;
import org.osgi.test.cases.packageadmin.tc5.tbc.PackageAdmin.GetBundles;
import org.osgi.test.cases.packageadmin.tc5.tbc.PackageAdmin.GetExportedPackage;
import org.osgi.test.cases.packageadmin.tc5.tbc.PackageAdmin.GetFragments;
import org.osgi.test.cases.packageadmin.tc5.tbc.PackageAdmin.GetHosts;
import org.osgi.test.cases.packageadmin.tc5.tbc.PackageAdmin.GetRequiredBundles;
import org.osgi.test.cases.packageadmin.tc5.tbc.PackageAdmin.ResolveBundles;
import org.osgi.test.cases.packageadmin.tc5.tbc.RequiredBundle.GetRequiringBundles;
import org.osgi.test.cases.packageadmin.tc5.tbc.RequiredBundle.GetSymbolicName;
import org.osgi.test.cases.packageadmin.tc5.tbc.RequiredBundle.GetVersion;
import org.osgi.test.cases.packageadmin.tc5.tbc.RequiredBundle.IsRemovalPending;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * 
 * Tests for the class org.osgi.service.packageadmin.PackageAdmin
 * 
 * If you are adding new bundles for this control, do not forget to create a new
 * constant, new attribute and getTestBundleXXX method. After this update
 * methods installTestBundles, uninstallTestBundles and refreshPackageAdmin to
 * work with the new bundle.
 * 
 * @version $Revision$
 */
public class TestControl extends DefaultTestBundleControl {

	public static final String	TEST_BUNDLE_1	= "org.osgi.test.cases.packageadmin.tc5.tb1";
	public static final String	TEST_BUNDLE_2	= "org.osgi.test.cases.packageadmin.tc5.tb1";
	public static final String	TEST_BUNDLE_3	= "org.osgi.test.cases.packageadmin.tc5.tb3";
	public static final String	TEST_BUNDLE_4	= "org.osgi.test.cases.packageadmin.tc5.tb4";
	public static final String	TEST_BUNDLE_5	= "org.osgi.test.cases.packageadmin.tc5.tb5";

	private Bundle				tb1;
	private Bundle				tb2;
	private Bundle				tb3;
	private Bundle				tb4;
	private Bundle				tb5;

	/**
	 * Creates a new instance of TestControl
	 */
	public TestControl() {

	}

	/**
	 * <remove>Prepare for each run. It is important that
	 * org.osgi.test.cases.packageadmin.tc5 test run is properly initialized and
	 * that each case can run standalone. To save
	 * org.osgi.test.cases.packageadmin.tc5 lot of time in debugging, clean up
	 * all possible persistent remains before the test is run. Clean up is
	 * better don in the prepare because debugging sessions can easily cause the
	 * unprepare never to be called. </remove>
	 */
	public void prepare() {
	}

	/**
	 * <remove>Prepare for each method. It is important that each method can be
	 * executed independently of each other method. Do not keep state between
	 * methods, if possible. This method can be used to clean up any possible
	 * remaining state. </remove>
	 */
	public void setState() {
	}

	/**
	 * Tests the constants of the class PackageAdmin
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testConstants() throws Exception {
		new Constants(this).run();
	}

	/**
	 * Tests the method PackageAdmin.getAllExportedPackages()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testGetExportedPackages() throws Exception {
		new GetExportedPackages(this).run();
	}

	/**
	 * Tests the method RequiredBundle.getBundle()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testGetBundle() throws Exception {
		new GetBundle(this).run();
	}

	/**
	 * Tests the method PackageAdmin.getBundles()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testGetBundles() throws Exception {
		new GetBundles(this).run();
	}

	/**
	 * Tests the method PackageAdmin.getBundleType()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testGetBundleType() throws Exception {
		new GetBundleType(this).run();
	}

	/**
	 * Tests the method PackageAdmin.getExportedPackage()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testGetExportedPackage() throws Exception {
		new GetExportedPackage(this).run();
	}

	/**
	 * Tests the method PackageAdmin.getFragments()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testGetFragments() throws Exception {
		new GetFragments(this).run();
	}

	/**
	 * Tests the method PackageAdmin.getHosts()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testGetHosts() throws Exception {
		new GetHosts(this).run();
	}

	/**
	 * Tests the method ExportedPackage.getImportingBundles()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testGetImportingBundles() throws Exception {
		new GetImportingBundles(this).run();
	}

	/**
	 * Tests the method PackageAdmin.getRequiredBundles()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testGetRequiredBundles() throws Exception {
		new GetRequiredBundles(this).run();
	}

	/**
	 * Tests the method RequiredBundle.getRequiringBundles()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testGetRequiringBundles() throws Exception {
		new GetRequiringBundles(this).run();
	}

	/**
	 * Tests the method RequiredBundle.getSymbolicName()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testGetSymbolicName() throws Exception {
		new GetSymbolicName(this).run();
	}

	/**
	 * Tests the method RequiredBundle.getVersion()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testGetVersion() throws Exception {
		new GetVersion(this).run();
	}

	/**
	 * Tests the method RequiredBundle.isRemovalPending()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testIsRemovalPending() throws Exception {
		new IsRemovalPending(this).run();
	}

	/**
	 * Tests the method PackageAdmin.resolveBundles()
	 * 
	 * @specification org.osgi.framework.packageadmin
	 * @specificationVersion 4
	 */
	public void testResolveBundles() throws Exception {
		new ResolveBundles(this).run();
	}

	/**
	 * Clean up after each method. Notice that during debugging many times the
	 * unsetState is never reached.
	 */
	public void unsetState() throws Exception {
		refreshPackageAdmin();
	}

	/**
	 * Clean up after org.osgi.test.cases.packageadmin.tc5 run. Notice that
	 * during debugging many times the unprepare is never reached.
	 */
	public void unprepare() {
	}

	// Helpers methods for tests cases

	/**
	 * Install all test bundles
	 */
	public void installTestBundles() throws Exception {
		// Install test bundles
		tb5 = getContext().installBundle(getWebServer() + "tb5.jar");
		tb1 = installBundle("tb1.jar");
		tb2 = installBundle("tb2.jar");
		tb3 = installBundle("tb3.jar");
		tb4 = installBundle("tb4.jar");
	}

	/**
	 * Uninstall all test bundles
	 */
	public void uninstallTestBundles() throws Exception {
		// Uninstall test bundles
		tb5.uninstall();
		tb4.uninstall();
		tb3.uninstall();
		tb2.uninstall();
		tb1.uninstall();
	}

	/**
	 * Refresh package admin and block until release all test bundles
	 */
	public void refreshPackageAdmin() {
		int count;
		PackageAdmin packageAdmin;
		ServiceReference serviceReference;

		// Get PackageAdmin service reference
		serviceReference = getContext().getServiceReference(
				PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) getContext().getService(serviceReference);

		// Refresh the PackageAdmin service to remove packages from uninstalled
		// bundles
		count = 0;
		while (((packageAdmin.getExportedPackage(TEST_BUNDLE_1) != null)
				|| (packageAdmin.getExportedPackage(TEST_BUNDLE_2) != null)
				|| (packageAdmin.getExportedPackage(TEST_BUNDLE_3) != null)
				|| (packageAdmin.getExportedPackage(TEST_BUNDLE_4) != null) || (packageAdmin
				.getExportedPackage(TEST_BUNDLE_5) != null))
				&& ((count < 20))) {
			packageAdmin.refreshPackages(null);

			try {
				Thread.sleep(500);
			}
			catch (InterruptedException ex) {
				// Ignore this exception
			}
		}

		// Unget the PackageAdmin service
		getContext().ungetService(serviceReference);
	}

	/**
	 * Return the test bundle 1 instance
	 * 
	 * @return the test bundle 1 instance
	 */
	public Bundle getTestBundle1() {
		return tb1;
	}

	/**
	 * Return the test bundle 2 instance
	 * 
	 * @return the test bundle 2 instance
	 */
	public Bundle getTestBundle2() {
		return tb2;
	}

	/**
	 * Return the test bundle 3 instance
	 * 
	 * @return the test bundle 3 instance
	 */
	public Bundle getTestBundle3() {
		return tb3;
	}

	/**
	 * Return the test bundle 4 instance
	 * 
	 * @return the test bundle 4 instance
	 */
	public Bundle getTestBundle4() {
		return tb4;
	}

	/**
	 * Return the test bundle 5 instance
	 * 
	 * @return the test bundle 5 instance
	 */
	public Bundle getTestBundle5() {
		return tb5;
	}

}