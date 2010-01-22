package org.osgi.test.cases.jmx.junit;

import java.io.IOException;

import org.osgi.framework.Bundle;
import org.osgi.jmx.framework.PackageStateMBean;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

public class PackageStateMBeanTestCase extends MBeanGeneralTestCase {

	private PackageStateMBean pMBean;
	private PackageAdmin pAdmin;
	private static final String EXPORTED_PACKAGE = "org.osgi.test.cases.jmx.tb2.api";
	private Bundle testBundle1;
	private Bundle testBundle2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testBundle2 = super.install("tb2.jar");
		testBundle2.start();

		testBundle1 = super.install("tb1.jar");
		testBundle1.start();

		super
				.waitForRegistering(createObjectName(PackageStateMBean.OBJECTNAME));
		pMBean = getMBeanFromServer(PackageStateMBean.OBJECTNAME,
				PackageStateMBean.class);
		pAdmin = (PackageAdmin) getContext().getService(
				getContext().getServiceReference(PackageAdmin.class.getName()));
	}

	public void testGetExportingBundle() throws IOException {
		assertNotNull(pMBean);
		assertNotNull(pAdmin);

		String bundleVersion = (String) testBundle1.getHeaders().get(
				"Bundle-Version");

		long[] mBeanBundleIds = pMBean.getExportingBundles(EXPORTED_PACKAGE,
				bundleVersion);
		ExportedPackage[] exportedPackages = pAdmin
				.getExportedPackages(EXPORTED_PACKAGE);

		boolean found = false;
		for (ExportedPackage exportPack : exportedPackages) {
			for (long id : mBeanBundleIds) {
				if (exportPack.getExportingBundle().getBundleId() == id) {
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		assertTrue(
				"was not able to find the right bundle that exports package "
						+ EXPORTED_PACKAGE + "; exptected bundle "
						+ testBundle2.getSymbolicName(), found);
	}

	public void testGetImportingBundles() throws IOException {
		assertNotNull(pMBean);
		assertNotNull(pAdmin);

		String bundleVersion = (String) testBundle2.getHeaders().get(
				"Bundle-Version");
		long[] bundleIds = pMBean.getImportingBundles(EXPORTED_PACKAGE,
				bundleVersion, testBundle2.getBundleId());
		boolean found = false;
		for (long bundleId : bundleIds) {
			if (bundleId == testBundle1.getBundleId()) {
				found = true;
				break;
			}
		}
		assertTrue(
				"was not able to find the right bundle that imports package "
						+ EXPORTED_PACKAGE + ": expected bundle "
						+ testBundle1.getSymbolicName(), found);
	}

	public void testGetPackages() throws IOException {
		assertNotNull(pMBean);
		assertNotNull(pAdmin);
		/*
		 * TODO FIXME: https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1385
		 */
		pMBean.listPackages();
	}

	public void testIsRemovalPending() throws IOException {
		assertNotNull(pMBean);
		assertNotNull(pAdmin);
		String bundleVersion = (String) testBundle2.getHeaders().get(
				"Bundle-Version");

		boolean mBeanRemovalPending = pMBean.isRemovalPending(EXPORTED_PACKAGE,
				bundleVersion, testBundle2.getBundleId());
		boolean bundleStateRemovalPending = testBundle2.getState() == Bundle.STOPPING;

		assertTrue(
				"received inconsistent state information for bundle exporting package "
						+ EXPORTED_PACKAGE + "."
						+ testBundle2.getSymbolicName(),
				mBeanRemovalPending == bundleStateRemovalPending);

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super
				.waitForUnRegistering(createObjectName(PackageStateMBean.OBJECTNAME));
	}
}
