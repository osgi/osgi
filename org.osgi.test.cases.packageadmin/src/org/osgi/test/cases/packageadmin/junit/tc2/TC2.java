/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.packageadmin.junit.tc2;

import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson
 */
public class TC2 extends DefaultTestBundleControl {
	private final String	TP1				= "org.osgi.test.cases.packageadmin.tc2.tb1";
	private final String	TP2				= "org.osgi.test.cases.packageadmin.tc2.tb2";
	private final String	TP3				= "org.osgi.test.cases.packageadmin.tc2.tb3";

	/**
	 *  
	 */
	public void testTc2() throws Exception {
		Bundle tb1;
		Bundle tb2;
		Bundle tb4;
		Bundle tb5;
		boolean tp1present = false;
		boolean tp3present = false;
		String tb1loc;

		// 2.15.2 Testcase2 (tc2), exporting packages, updating
		// Tb1 will export package ...testpackage1
		// Tb2 actively has chosen ...testpackage1, exports TestPackage2
		// sr =
		// getContext().getServiceReference("org.osgi.service.packageadmin.PackageAdmin");
		ServiceReference sr = getContext().getServiceReference(
				PackageAdmin.class.getName());
		assertNotNull("PackageAdmin not available", sr);
		PackageAdmin pa = (PackageAdmin) getContext().getService(sr);
		// Check that the packages doesn?t exist before and do not run if they
		// do
		if ((pa.getExportedPackage(TP1) != null)
				|| (pa.getExportedPackage(TP2) != null)
				|| (pa.getExportedPackage(TP3) != null)) {
			fail("The packages used for the test are already exported, i.e. the test is impossible to make.");
		}

		// Install tb1, tb2 (and start)
		tb1 = installBundle("tc2.tb1.jar");
		tb2 = installBundle("tc2.tb2.jar");
		log("Install Testbundle 1 and 2.");
		tb1loc = tb1.getLocation();
		// Call PackageAdmin.getExportedPackages(Bundle bundle) for tb1
		// Is ...testpackage1 exported (yes)
		ExportedPackage[] tps1 = pa.getExportedPackages(tb1);
		assertNotNull("No exported packages from tb1:", tps1);
		assertEquals("Wrong number of exports:", 1, tps1.length);
		assertEquals("What is the package name:", TP1, tps1[0].getName());
		assertTrue("Who is exported: " + TP1, tps1[0].getExportingBundle() == tb1);
		// Call PackageAdmin.getExportedPackage(...testpackage1)
		// Is ...testpackage1 exported (yes)
		ExportedPackage[] ep = pa.getExportedPackages(TP1);
		assertNotNull("No exported packages: " + TP1, ep);
		assertEquals("Wrong number of exports for: " + TP1, 1, ep.length);
		assertEquals("What is the package name:", TP1, ep[0].getName());
		assertTrue("Who is exported: " + TP1, ep[0].getExportingBundle() == tb1);

		// uninstall tb1
		// install tb3 called the same as tb1 (tb1.jar) but exporting
		// package ...testpackage3 (simulates updating of the bundle)
		tb5 = installBundle("tc2.tb5.jar", false);
		try {
			assertNotNull("Null resource from tc2.tb5", tb5.getResource("tc2.tb1.jar"));
		}
		catch (IllegalStateException e) {
			fail("Could not getResource on a nonstarted but installed bundle.", e);
		}
		tb5.start();
		// tb5 contains tb3 named as tb1
		log("Update TestBundle 1. Now TestBundle 1 contains TestPackage 3 instead of TestPackage 1.");
		try {
			tb1.update(tb5.getResource("tc2.tb1.jar").openStream());
		}
		catch (Exception e) {
			fail("Update failed", e);
		}
		// install tb4, tb4 actively has chosen ...testpackage3
		tb4 = installBundle("tc2.tb4.jar");
		// Call PackageAdmin.getExportedPackages(Bundle bundle) for tb3
		// Should reply ...testpackage3 and ...testpackage1
		ExportedPackage[] tps13 = pa.getExportedPackages(tb1);
		tp1present = false;
		tp3present = false;
		for (int i = 0; i < tps13.length; i++) {
			if (tps13[i].getName().equals(TP1)) {
				tp1present = true;
			}
			else
				if (tps13[i].getName().equals(TP3)) {
					tp3present = true;
				}
		}
		log("Because of lazy update, now TestBundle 1 should contain TestPackage 1 and TestPackage 3.");
		assertTrue("Does TestBundle 1 contain Testpackage 1 after update:", tp1present);
		assertTrue("Does TestBundle 1 contain Testpackage 3 after update:", tp3present);
		// Call PackageAdmin.getExportedPackage(...testpackage1) and
		// getExportedPackage(...testpackage3)
		// Is ...testpackage1 exported (yes, not null)
		// Is ...testpackage3 exported (yes, not null)
		assertNotNull("Is TestPackage 1 exported", pa.getExportedPackage(TP1));
		assertNotNull("Is TestPackage 3 exported", pa.getExportedPackage(TP3));
		// is tb4 active (yes)
		// is tb2 active (yes)
		assertTrue("Is testbundle 2 still active", tb2.getState() == Bundle.ACTIVE);
		assertTrue("Is testbundle 4 still active", tb4.getState() == Bundle.ACTIVE);
		// Call PackageAdmin.refreshPackages()
		Bundle[] b = new Bundle[4];
		b[0] = tb1;
		b[1] = tb2;
		b[2] = tb4;
		b[3] = tb5;
		log("Refresh all packages.");

		PackagesRefreshedListener prl = new PackagesRefreshedListener();
		try {
			getContext().addFrameworkListener(prl);
			pa.refreshPackages(b);
			prl.waitForPackagesRefreshedEvent();
		}
		finally {
			getContext().removeFrameworkListener(prl);
		}

		// Call PackageAdmin.getExportedPackages(Bundle bundle) for tb3
		ExportedPackage[] tps3 = pa.getExportedPackages(tb1);
		// Should reply only ...testpackage3
		tp1present = false;
		tp3present = false;
		for (int i = 0; i < tps3.length; i++) {
			if ((tps3[i].getName() != null) && tps3[i].getName().equals(TP1)) {
				tp1present = true;
			}
			else
				if ((tps3[i].getName() != null)
						&& tps3[i].getName().equals(TP3)) {
					tp3present = true;
				}
		}
		assertFalse("Does TestBundle 1 contain Testpackage 1 after refresh:", tp1present);
		assertTrue("Does TestBundle 1 contain Testpackage 3 after refresh", tp3present);
		// Call PackageAdmin.getExportedPackage(...testpackage3)
		// Is ...testpackage3 exported (yes, not null)
		assertNull("Is TestPackage 1 exported after refresh:", pa.getExportedPackage(TP1));
		assertNotNull("Is TestPackage 3 exported after refresh", pa.getExportedPackage(TP3));
		// is tb4 active (yes)
		// is tb2 active (no)
		assertFalse("Is testbundle 2 still active:", tb2.getState() == Bundle.ACTIVE);
		assertTrue("Is testbundle 4 still active:", tb4.getState() == Bundle.ACTIVE);
		URL url = new URL(tb1loc);
		tb1.update(url.openStream());
		tb1.stop();
		tb1.uninstall();
		tb2.stop();
		tb2.uninstall();
		tb4.stop();
		tb4.uninstall();
		tb5.stop();
		tb5.uninstall();
		b = new Bundle[4];
		b[0] = tb1;
		b[1] = tb2;
		b[2] = tb4;
		b[3] = tb5;
		log("Refresh all packages.");
		pa.refreshPackages(b);
		log("PackageAdmin TestCase 2 is completed.");
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
