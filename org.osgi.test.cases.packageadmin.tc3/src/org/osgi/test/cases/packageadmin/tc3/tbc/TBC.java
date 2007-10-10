/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.packageadmin.tc3.tbc;

import java.io.*;
import org.osgi.framework.*;
import org.osgi.service.packageadmin.*;
import org.osgi.test.service.*;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson Telecom AB
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
	private final String	TP1				= "org.osgi.test.cases.packageadmin.tc3.tb1";
	private final String	TP2				= "org.osgi.test.cases.packageadmin.tc3.tb2";
	private final String	TP3				= "org.osgi.test.cases.packageadmin.tc3.tb3";
	ServiceReference		serviceRef;
	static String[]			methods			= new String[] {"tc3"};

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
				tc3();
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
	void tc3() throws Exception {
		Bundle tb1;
		Bundle tb2;
		Bundle tb3;
		//2.15.3 Testcase3 (tc3), new bundle exporting same package
		//Tb1 and tb3 contains package ...testpackage1
		//Tb2 has actively chosen ...testpackage1
		//ServiceReference sr =
		// context.getServiceReference("org.osgi.service.packageadmin.PackageAdmin");
		ServiceReference sr = context.getServiceReference(PackageAdmin.class
				.getName());
		PackageAdmin pa = (PackageAdmin) context.getService(sr);
		//Check that the packages doesn?t exist before and do not run if they
		// do
		if ((pa.getExportedPackage(TP1) != null)
				|| (pa.getExportedPackage(TP2) != null)
				|| (pa.getExportedPackage(TP3) != null)) {
			log("The packages used for the test are already exported, i.e. the test is impossible to make.");
		}
		else {
			//Install tb1 and tb2
			tb1 = context.installBundle(tcHome + "tb1.jar");
			tb2 = context.installBundle(tcHome + "tb2.jar");
			tb1.start();
			tb2.start();
			log("Install Testbundle 1 and 2.");
			//Call ExportedPackage.getSpecificationVersion() for
			// ...testpackage1
			log("The specification version for TestPackage 1: 1.0.1 : '"
					+ pa.getExportedPackage(TP1).getSpecificationVersion()
					+ "'");
			//Install tb3
			log("Install Testbundle 3. Testbundle 3 exports the same package as Testbundle 1.");
			tb3 = context.installBundle(tcHome + "tb3.jar");
			tb3.start();
			//Call PackageAdmin.getExportedPackages(Bundle bundle) for tb1
			//See that it exports only ...testpackage1
			ExportedPackage[] eps = pa.getExportedPackages(tb1);
			log("Testbundle 1 exports only TestPackage 1: true : '"
					+ (eps.length == 1) + "'");
			log("Testbundle 1 exports: " + TP1 + " : '" + (eps[0].getName())
					+ "'");
			link.send("25");
			//ExportingPackage.getExportingBundle() for package ...testpackage1
			ExportedPackage tp1 = pa.getExportedPackage(TP1);
			Bundle eb = tp1.getExportingBundle();
			//Is it tb1? (yes)
			log("What bundle is exporting TestPackage 1. Is it Testbundle 1: true : '"
					+ (eb.equals(tb1)) + "'");
			//Is it tb3? (no)
			log("What bundle is exporting TestPackage 1. Is it Testbundle 3: false : '"
					+ (eb.equals(tb3)) + "'");
			//Call ExportedPackage.getImportingBundles()
			Bundle[] ib = tp1.getImportingBundles();
			//Is it tb2? (yes)
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
			log("What bundles is importing TestPackage 1. Is it Testbundle 1: false : '"
					+ i1 + "'");
			log("What bundles is importing TestPackage 1. Is it Testbundle 2: true : '"
					+ i2 + "'");
			log("What bundles is importing TestPackage 1. Is it Testbundle 3: true : '"
					+ i3 + "'");
			link.send("50");
			//Uninstall tb1
			tb1.stop();
			tb1.uninstall();
			log("Uninstall Testbundle 1.");
			//ExportingPackage.getExportingBundle() for package ...testpackage1
			eb = tp1.getExportingBundle();
			//Is it tb1? (yes)
			if (eb != null)
				log("What bundle is exporting TestPackage 1. Is it Testbundle 1: true : '"
						+ (eb.equals(tb1)) + "'");
			else
				log("TestPackage 1 is not being exported by anyone.");
			//Call PackageAdmin.refreshPackages()
			Bundle[] b = new Bundle[3];
			b[0] = tb1;
			b[1] = tb2;
			b[2] = tb3;
			log("Refresh all packages.");
      
      // refresh the packages
      PackagesRefreshedListener prl = new PackagesRefreshedListener();
      try {
        context.addFrameworkListener(prl);
        pa.refreshPackages(b);
        prl.waitForPackagesRefreshedEvent();
      } finally {
        context.removeFrameworkListener(prl);
      }
      
			//ExportingPackage.getExportingBundle() for package ...testpackage1
			tp1 = pa.getExportedPackage(TP1);
			eb = tp1.getExportingBundle();
			if (eb != null) {
				//Is it tb1? (no)
				log("What bundle is exporting TestPackage 1. Is it Testbundle 1: false : '"
						+ (eb.equals(tb1)) + "'");
				//Is it tb3? (yes)
				log("What bundle is exporting TestPackage 1. Is it Testbundle 3: true : '"
						+ (eb.equals(tb3)) + "'");
			}
			else
				log("Nobody is exporting TestPackage 1.");
			//Call ExportedPackage.getSpecificationVersion() for
			// ...testpackage1
			log("The specification version for TestPackage 1: 1.0.2 : '"
					+ pa.getExportedPackage(TP1).getSpecificationVersion()
					+ "'");
			//Call ExportedPackage.getName() for ...testpackage1
			log("What is TestPackage 1's name: org.osgi.test.cases.packageadmin.tc3.tb1 : '"
					+ pa.getExportedPackage(TP1).getName() + "'");
			link.send("75");
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
