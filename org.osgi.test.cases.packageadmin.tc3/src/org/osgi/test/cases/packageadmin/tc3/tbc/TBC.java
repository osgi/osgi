/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.packageadmin.tc3.tbc;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson Telecom AB
 */
public class TBC extends DefaultTestBundleControl {
	private final String	TP1	= "org.osgi.test.cases.packageadmin.tc3.tb1";
	private final String	TP2	= "org.osgi.test.cases.packageadmin.tc3.tb2";
	private final String	TP3	= "org.osgi.test.cases.packageadmin.tc3.tb3";

	/**
	 *  
	 */
	public void testTc3() throws Exception {
		Bundle tb1;
		Bundle tb2;
		Bundle tb3;
		// 2.15.3 Testcase3 (tc3), new bundle exporting same package
		// Tb1 and tb3 contains package ...testpackage1
		// Tb2 has actively chosen ...testpackage1
		// ServiceReference sr =
		// getContext().getServiceReference("org.osgi.service.packageadmin.PackageAdmin");
		ServiceReference sr = getContext().getServiceReference(
				PackageAdmin.class.getName());
		PackageAdmin pa = (PackageAdmin) getContext().getService(sr);
		// Check that the packages doesn?t exist before and do not run if they
		// do
		if ((pa.getExportedPackage(TP1) != null)
				|| (pa.getExportedPackage(TP2) != null)
				|| (pa.getExportedPackage(TP3) != null)) {
			fail("The packages used for the test are already exported, i.e. the test is impossible to make.");
		}

		// Install tb1 and tb2
		tb1 = installBundle("tc3.tb1.jar");
		tb2 = installBundle("tc3.tb2.jar");
		log("Install Testbundle 1 and 2.");
		// Call ExportedPackage.getSpecificationVersion() for
		// ...testpackage1
		assertNotNull("TestPackage 1 is null", pa.getExportedPackage(TP1));
		assertEquals("The specification version for TestPackage 1", new Version("1.0.1").toString(), 
				pa.getExportedPackage(TP1).getVersion().toString());
		// Install tb3
		log("Install Testbundle 3. Testbundle 3 exports the same package as Testbundle 1.");
		tb3 = installBundle("tc3.tb3.jar");

		// Call PackageAdmin.getExportedPackages(Bundle bundle) for tb1
		// See that it exports only ...testpackage1
		ExportedPackage[] eps = pa.getExportedPackages(tb1);
		assertNotNull("Testbundle 1 exports are null", eps);
		assertEquals("Testbundle 1 exports only TestPackage 1:", 1, eps.length);
		assertEquals("Testbundle 1 exports: ", TP1, eps[0].getName());

		// ExportingPackage.getExportingBundle() for package ...testpackage1
		ExportedPackage tp1 = pa.getExportedPackage(TP1);
		Bundle eb = tp1.getExportingBundle();
		// Is it tb1? (yes)
		assertEquals("What bundle is exporting TestPackage 1. Is it Testbundle 1", tb1.getBundleId(), eb.getBundleId());

		// Call ExportedPackage.getImportingBundles()
		Bundle[] ib = tp1.getImportingBundles();
		// Is it tb2? (yes)
		boolean i1 = false;
		boolean i2 = false;
		boolean i3 = false;
		for (int i = 0; i < ib.length; i++) {
			if (ib[i].equals(tb2))
				i2 = true;
			else
				if (ib[i].equals(tb3))
					i3 = true;
				else
					if (ib[i].equals(tb1))
						i1 = true;
		}
		assertFalse("What bundles is importing TestPackage 1. Is it Testbundle 1:", i1);
		assertTrue("What bundles is importing TestPackage 1. Is it Testbundle 2:", i2);
		assertTrue("What bundles is importing TestPackage 1. Is it Testbundle 3:", i3);

		// Uninstall tb1
		tb1.stop();
		tb1.uninstall();
		log("Uninstall Testbundle 1.");
		// ExportingPackage.getExportingBundle() for package ...testpackage1
		eb = tp1.getExportingBundle();
		assertNotNull("TestPackage 1 is not being exported by anyone.", eb);
		// Is it tb1? (yes)
		assertEquals("What bundle is exporting TestPackage 1. Is it Testbundle 1:", tb1.getBundleId(), eb.getBundleId());
		// Call PackageAdmin.refreshPackages()
		Bundle[] b = new Bundle[3];
		b[0] = tb1;
		b[1] = tb2;
		b[2] = tb3;
		log("Refresh all packages.");

		// refresh the packages
		PackagesRefreshedListener prl = new PackagesRefreshedListener();
		try {
			getContext().addFrameworkListener(prl);
			pa.refreshPackages(b);
			prl.waitForPackagesRefreshedEvent();
		}
		finally {
			getContext().removeFrameworkListener(prl);
		}

		// ExportingPackage.getExportingBundle() for package ...testpackage1
		tp1 = pa.getExportedPackage(TP1);
		eb = tp1.getExportingBundle();
		assertNotNull("Nobody is exporting TestPackage 1.", eb);

		// Is it tb1? (no)
		// Is it tb3? (yes)
		assertEquals("What bundle is exporting TestPackage 1. Is it Testbundle 3", tb3.getBundleId(), eb.getBundleId());

		// Call ExportedPackage.getSpecificationVersion() for
		// ...testpackage1
		assertTrue("The specification version for TestPackage 1:", new Version("1.0.2").equals(pa.getExportedPackage(TP1).getVersion()));
		// Call ExportedPackage.getName() for ...testpackage1
		assertEquals("What is TestPackage 1's name:", TP1, pa.getExportedPackage(TP1).getName());

		tb2.stop();
		tb2.uninstall();
		tb3.stop();
		tb3.uninstall();
		b = new Bundle[3];
		b[0] = tb1;
		b[1] = tb2;
		b[2] = tb3;
		log("Refresh all packages.");
		pa.refreshPackages(b);
		log("PackageAdmin TestCase 3 is completed.");
	}

	class PackagesRefreshedListener implements FrameworkListener {
		int	count	= 0;

		public void frameworkEvent(FrameworkEvent event) {
			if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
				count++;
				synchronized (this) {
					notify();
				}
			}
		}

		public void waitForPackagesRefreshedEvent() {
			if (count == 0) {
				try {
					synchronized (this) {
						wait(30000); // wait maximum 30 sec to avoid infinite
										// wait
					}
					if (count == 0) {
						log("Timeout occured while waiting for PACKAGES_REFRESHED event");
					}
				}
				catch (Exception e) {
				}
			}
		}
	}

}
