/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.packageadmin.junit.tc1;

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
 * @author Ericsson Telecom AB
 */
public class TC1 extends DefaultTestBundleControl {
	private final String	TP1				= "org.osgi.test.cases.packageadmin.tc1.tb1";
	private final String	TP2				= "org.osgi.test.cases.packageadmin.tc1.tb2";

	/**
	 *  
	 */
	public void testTC1() throws Exception {
		Bundle tb1;
		Bundle tb2;
		Bundle tb3;
		PackageAdmin pa = null;
		// 2.15.1 Testcase1 (tc1), exporting packages and re-exporting packages
		//Tb1 will export package ...testpackage1
		// Tb2 actively has chosen ...testpackage1, tb2 exports ...testpackage2
		// Tb3 actively has chosen ...testpackage2,
		ServiceReference sr = getContext().getServiceReference(PackageAdmin.class
				.getName());
		if (sr != null)
			pa = (PackageAdmin) getContext().getService(sr);
		else
			fail("Can't find ServiceReference to PackageAdmin.");
		//Check that the packages doesn?t exist before and do not run if they
		// do
		if ((pa.getExportedPackage(TP1) != null)
				|| (pa.getExportedPackage(TP2) != null)) {
			fail("The packages used for the test are already exported, i.e. the test is impossible to make.");
		}
		else {
			assertEquals("The PackageAdmin is not in the System Bundle", 0, sr.getBundle().getBundleId());
			// install tb1, tb2 and tb3

			tb1 = installBundle("tc1.tb1.jar");
			tb2 = installBundle("tc1.tb2.jar");
			tb3 = installBundle("tc1.tb3.jar");
			log("Install Testbundle 1, 2 and 3.");
			// Call PackageAdmin.getExportedPackages(Bundle bundle) for tb1
			ExportedPackage[] tps = pa.getExportedPackages(tb1);
			//See that it exports only ...testpackage1
			int only = 0;
			for (int i = 0; i < tps.length; i++) {
				if (tps[i].getName().equals(TP1)) {
					only++;
				}
			}
			assertEquals("Test Bundle 1 is not the only exporter TestPackage1", 1, only);
			// (null as argument could be tried for framework where only the
			// framework is being run)
			try {
				ExportedPackage[] tpnull = pa.getExportedPackages((Bundle)null);
			}
			catch (Exception e) {
				fail("The method getExportedPackages(null) could not handle a null argument.", e);
			}
			//uninstall tb1
			log("Uninstall Testbundle 1.");
			tb1.stop();
			tb1.uninstall();
			// Call PackageAdmin.getExportedPackages(Bundle bundle) for tb1
			// ...testpackage1 still exported? (yes)
			ExportedPackage tp1 = pa.getExportedPackage(TP1);
			assertNotNull(TP1 + " was not a package that was exported.", tp1);
			//is tb2 active (yes)
			assertEquals("Is testbundle 2 still active", Bundle.ACTIVE, tb2.getState());
			// Call PackageAdmin.getExportedPackages(Bundle bundle) for tb2
			// ...testpackage2 still exported? (yes)
			boolean tp2present = false;
			ExportedPackage[] pas = pa.getExportedPackages(tb2);
			if (pas != null) {
				for (int i = 0; i < pas.length; i++) {
					if (pas[i].getName().equals(TP2)) {
						tp2present = true;
					}
				}
			}
			assertTrue("Is testpackage 2 still exported:", tp2present);
			// is tb3 active (yes)
			assertEquals("Is testbundle 3 still active:", Bundle.ACTIVE, tb3.getState());
			// Call ExportedPackage.isRemovalPending()
			//Answer should be true
			assertTrue("Is TestPackage 1 scheduled for removal:", tp1.isRemovalPending());
			// Call PackageAdmin.refreshPackages()
			Bundle[] b = new Bundle[3];
			b[0] = tb1;
			b[1] = tb2;
			b[2] = tb3;
			log("The Framework is being refreshed.");
      
			PackagesRefreshedListener prl = new PackagesRefreshedListener();
			try {
				getContext().addFrameworkListener(prl);
				pa.refreshPackages(b);
				prl.waitForPackagesRefreshedEvent();
			} finally {
				getContext().removeFrameworkListener(prl);
			}
      
			//Call PackageAdmin.getExportedPackages(Bundle bundle) for tb1
			//...testpackage1 still exported? (no)
			boolean tp1present = false;
			ExportedPackage[] tps1 = pa.getExportedPackages(tb1);
			if (tps1 != null)
				for (int i = 0; i < tps1.length; i++) {
					if (tps1[i].getName().equals(TP1)) {
						tp1present = true;
					}
				}
			assertFalse("After refresh. Is Testpackage 1 exported:", tp1present);
			//is tb2 active (no)
			assertFalse("Is testbundle 2 still active:", tb2.getState() == Bundle.ACTIVE);
			//Call PackageAdmin.getExportedPackages(Bundle bundle) for tb2
			//...testpackage2 still exported? (no)
			assertNull("Is TestPackage 2 still exported:", pa.getExportedPackage(TP2));
			//is tb3 active (no)
			assertFalse("Is testbundle 3 still active:", tb3.getState() == Bundle.ACTIVE);
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
			log("PackageAdmin TestCase 1 is completed.");
		}
	}
  
  class PackagesRefreshedListener implements FrameworkListener {
    int count = 0;

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
            wait(30000); // wait maximum 30 sec to avoid infinite wait
          }
          if (count == 0) {
            log("Timeout occured while waiting for PACKAGES_REFRESHED event");
          }
        } catch (Exception e) {}
      }
    }
  }
}
