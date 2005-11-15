/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.packageadmin.tc2.tbc;

import java.io.*;
import java.net.*;
import org.osgi.framework.*;
import org.osgi.service.packageadmin.*;
import org.osgi.test.service.*;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson
 */
public class TBC extends Thread {
	BundleContext			context;
	//BundleContext otherContext;
	ServiceRegistration		sr;
	ServiceReference		linkRef;
	TestCaseLink			link;
	String					tcHome;
	boolean					_continue		= true;
	boolean					called;
	int						eventQueue[]	= new int[8];
	int						in, out;
	private final String	TP1				= "org.osgi.test.cases.packageadmin.tc2.tb1";
	private final String	TP2				= "org.osgi.test.cases.packageadmin.tc2.tb2";
	private final String	TP3				= "org.osgi.test.cases.packageadmin.tc2.tb3";
	ServiceReference		serviceRef;
	static String[]			methods			= new String[] {"tc2"};

	/**
	 * Constructor. Gets a reference to the TestCaseLink to communicate with the
	 * TestCase.
	 */
	TBC(BundleContext context) {
		this.context = context;
		this.linkRef = context
				.getServiceReference(TestCaseLink.class.getName());
		this.link = (TestCaseLink) context.getService(linkRef);
	}

	/**
	 * This function performs the tests.
	 */
	public void run() {
		sr = context.registerService(TBC.class.getName(), this, null);
		try {
			link.log("Test bundle control started Ok.");
			tcHome = (String) link.receive(10000);
			for (int i = 0; _continue && i < methods.length; i++) {
				tc2();
				link.send("" + 100 * (i + 1) / methods.length);
			}
			link.send("ready");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Releases the reference to the TestCaseLink.
	 */
	void quit() {
		if (_continue) {
			context.ungetService(linkRef);
			linkRef = null;
			_continue = false;
		}
	}

	void log(String test, String result) throws IOException {
		link.log(test + " " + result);
	}

	void log(String test) throws IOException {
		link.log(test);
	}

	/**
	 *  
	 */
	void tc2() throws Exception {
		Bundle tb1;
		Bundle tb2;
		Bundle tb4;
		Bundle tb5;
		boolean tp1present = false;
		boolean tp3present = false;
		String tb1loc;
		ServiceReference sr;
		//2.15.2 Testcase2 (tc2), exporting packages, updating
		//Tb1 will export package ...testpackage1
		//Tb2 actively has chosen ...testpackage1, exports TestPackage2
		// sr =
		// context.getServiceReference("org.osgi.service.packageadmin.PackageAdmin");
		sr = context.getServiceReference(PackageAdmin.class.getName());
		PackageAdmin pa = (PackageAdmin) context.getService(sr);
		//Check that the packages doesn?t exist before and do not run if they
		// do
		if ((pa.getExportedPackage(TP1) != null)
				|| (pa.getExportedPackage(TP2) != null)
				|| (pa.getExportedPackage(TP3) != null)) {
			log("The packages used for the test are already exported, i.e. the test is impossible to make.");
		}
		else {
			//Install tb1, tb2 (and start)
			tb1 = context.installBundle(tcHome + "tb1.jar");
			tb2 = context.installBundle(tcHome + "tb2.jar");
			tb1.start();
			tb2.start();
			log("Install Testbundle 1 and 2.");
			tb1loc = tb1.getLocation();
			//Call PackageAdmin.getExportedPackages(Bundle bundle) for tb1
			//Is ...testpackage1 exported (yes)
			ExportedPackage[] tps1 = pa.getExportedPackages(tb1);
			tp1present = false;
			for (int i = 0; i < tps1.length; i++) {
				if (tps1[i].getName().equals(TP1)) {
					tp1present = true;
				}
			}
			log("Is Testpackage 1 exported: true : '" + tp1present + "'");
			//Call PackageAdmin.getExportedPackage(...testpackage1)
			//Is ...testpackage1 exported (yes)
			ExportedPackage[] ep = pa.getExportedPackages(tb1);
			if (ep.length == 1) {
				log("What is TestBundle 1 exporting. : org.osgi.test.cases.packageadmin.tc2.tb1 : '"
						+ ep[0].getName() + "'");
			}
			ExportedPackage[] tps133 = pa.getExportedPackages(tb1);
			tp1present = false;
			tp3present = false;
			for (int i = 0; i < tps133.length; i++) {
				if (tps133[i].getName().equals(TP1)) {
					tp1present = true;
				}
				else
					if (tps133[i].getName().equals(TP3)) {
						tp3present = true;
					}
			}
			log("Does TestBundle 1 contain Testpackage 1 before update: true : '"
					+ tp1present + "'");
			log("Does TestBundle 1 contain Testpackage 3 before update: false : '"
					+ tp3present + "'");
			link.send("25");
			//uninstall tb1
			//install tb3 called the same as tb1 (tb1.jar) but exporting
			// package ...testpackage3 (simulates updating of the bundle)
			tb5 = context.installBundle(tcHome + "tb5.jar");
			try {
				tb5.getResource("www/tb1.jar");
			}
			catch (IllegalStateException e) {
				log("Could not getResource on a nonstarted but installed bundle."
						+ e);
			}
			tb5.start();
			//tb5 contains tb3 named as tb1
			log("Update TestBundle 1. Now TestBundle 1 contains TestPackage 3 instead of TestPackage 1.");
			try {
				tb1.update(tb5.getResource("www/tb1.jar").openStream());
			}
			catch (Exception e) {
				log("Bundle.getResource() failed.");
			}
			//install tb4, tb4 actively has chosen ...testpackage3
			tb4 = context.installBundle(tcHome + "tb4.jar");
			tb4.start();
			//Call PackageAdmin.getExportedPackages(Bundle bundle) for tb3
			//Should reply ...testpackage3 and ...testpackage1
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
			log("Does TestBundle 1 contain Testpackage 1 after update: true : '"
					+ tp1present + "'");
			log("Does TestBundle 1 contain Testpackage 3 after update: true : '"
					+ tp3present + "'");
			//Call PackageAdmin.getExportedPackage(...testpackage1) and
			// getExportedPackage(...testpackage3)
			//Is ...testpackage1 exported (yes, not null)
			//Is ...testpackage3 exported (yes, not null)
			log("Is TestPackage 1 exported: true : '"
					+ (pa.getExportedPackage(TP1) != null) + "'");
			log("Is TestPackage 3 exported: true : '"
					+ (pa.getExportedPackage(TP3) != null) + "'");
			//is tb4 active (yes)
			//is tb2 active (yes)
			log("Is testbundle 2 still active: true: '"
					+ (tb2.getState() == Bundle.ACTIVE) + "'");
			log("Is testbundle 4 still active: true: '"
					+ (tb4.getState() == Bundle.ACTIVE) + "'");
			link.send("50");
			//Call PackageAdmin.refreshPackages()
			Bundle[] b = new Bundle[4];
			b[0] = tb1;
			b[1] = tb2;
			b[2] = tb4;
			b[3] = tb5;
			log("Refresh all packages.");
      
      PackagesRefreshedListener prl = new PackagesRefreshedListener();
      try {
        context.addFrameworkListener(prl);
        pa.refreshPackages(b);
        prl.waitForPackagesRefreshedEvent();
      } finally {
        context.removeFrameworkListener(prl);
      }
      
			//Call PackageAdmin.getExportedPackages(Bundle bundle) for tb3
			ExportedPackage[] tps3 = pa.getExportedPackages(tb1);
			//Should reply only ...testpackage3
			tp1present = false;
			tp3present = false;
			for (int i = 0; i < tps3.length; i++) {
				if ((tps3[i].getName() != null)
						&& tps3[i].getName().equals(TP1)) {
					tp1present = true;
				}
				else
					if ((tps3[i].getName() != null)
							&& tps3[i].getName().equals(TP3)) {
						tp3present = true;
					}
			}
			log("Does TestBundle 1 contain Testpackage 1 after refresh: false : '"
					+ tp1present + "'");
			log("Does TestBundle 1 contain Testpackage 3 after refresh: true : '"
					+ tp3present + "'");
			//Call PackageAdmin.getExportedPackage(...testpackage3)
			//Is ...testpackage3 exported (yes, not null)
			log("Is TestPackage 1 exported after refresh: false : '"
					+ (pa.getExportedPackage(TP1) != null) + "'");
			log("Is TestPackage 3 exported after refresh: true : '"
					+ (pa.getExportedPackage(TP3) != null) + "'");
			//is tb4 active (yes)
			//is tb2 active (no)
			log("Is testbundle 2 still active: false: '"
					+ (tb2.getState() == Bundle.ACTIVE) + "'");
			log("Is testbundle 4 still active: true: '"
					+ (tb4.getState() == Bundle.ACTIVE) + "'");
			link.send("75");
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
