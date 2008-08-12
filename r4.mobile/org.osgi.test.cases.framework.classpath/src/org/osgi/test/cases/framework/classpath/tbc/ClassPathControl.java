/*
 * $Header:
 * /cvshome/repository/org/osgi/test/cases/framework/classpath/tbc/ClassPathControl.java,v
 * 1.1 2002/01/09 15:15:19 nnilsson Exp $
 * 
 * Copyright (c) OSGi Alliance (2000-2001). All Rights
 * Reserved.
 * 
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL NOT
 * INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY LOSS OF
 * PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS, OR
 * FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR
 * CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE
 * INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH LOSS
 * OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.framework.classpath.tbc;

import org.osgi.framework.*;
import org.osgi.test.cases.framework.classpath.tbc.exp.*;
import org.osgi.test.cases.util.*;
import java.net.*;

public class ClassPathControl extends DefaultTestBundleControl {
	static String[]	methods	= new String[] {"testImportUninstalledCode",
			"testExport", "testReinstall", "testImportGone"};

	public String[] getMethods() {
		return methods;
	}

	/** *** Test methods **** */
	/**
	 * Tries to import a package that has not been installed.
	 */
	public void testImportUninstalledCode() throws Exception {
		Bundle tb;
		String res;
		String s = getWebServer() + "tb1.jar";
		System.out.println("Web req " + s);
		try {
			URL url = new URL(s);
			System.out.println("Length "
					+ url.openConnection().getContentLength());
			tb = getContext().installBundle(s);
			if (tb.getState() == Bundle.INSTALLED)
				res = "State is INSTALLED.";
			else
				res = "State is not INSTALLED, but " + tb.getState();
			log("Installed a bundle importing uninstalled code", res);
			try {
				tb.start();
				res = "No exception thrown, Error!";
				tb.stop();
			}
			catch (BundleException be) {
				res = "Exception thrown, Ok.";
			}
			log("Started a bundle importing uninstalled code", res);
			tb.uninstall();
		}
		catch (BundleException e) {
			e.getNestedException().printStackTrace();
		}
		catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	/**
	 * Tests export of a package.
	 */
	public void testExport() throws Exception {
		Bundle tb;
		Exported ex = new Exported();
		String res;
		tb = getContext().installBundle(getWebServer() + "tb2.jar");
		try {
			tb.start();
			res = "Code was exported, Ok.";
			tb.stop();
		}
		catch (BundleException be) {
			if (tb.getState() == Bundle.INSTALLED)
				res = "Export failed!";
			else
				res = "BundleException thrown, Error!";
		}
		log("Testing export of a package", res);
		tb.uninstall();
	}

	/**
	 * Tests reinstallation of the same bundle.
	 */
	public void testReinstall() throws Exception {
		Bundle tb1, tb2;
		org.osgi.test.cases.framework.classpath.tbc.exp.Exported ex;
		String res;
		ex = new org.osgi.test.cases.framework.classpath.tbc.exp.Exported();
		tb1 = getContext().installBundle(getWebServer() + "tb3.jar");
		tb2 = getContext().installBundle(getWebServer() + "tb3.jar");
		if (tb1 == tb2) {
			res = "They are the same, Ok.";
			tb1.uninstall();
		}
		else {
			res = "They are not the same, Error!";
			tb1.uninstall();
			tb2.uninstall();
		}
		log("Testing reinstallation of same bundle", res);
	}

	/**
	 * Tests removal of exported package.
	 */
	public void testImportGone() throws Exception {
		Bundle tba, tbb;
		String res;
		tba = getContext().installBundle(getWebServer() + "tb4a.jar");
		tba.start();
		log("Testing removal of export package",
				"Installed and started exporting bundle.");
		tbb = getContext().installBundle(getWebServer() + "tb4b.jar");
		tbb.start();
		log("Testing removal of export package",
				"Installed and started importing bundle.");
		log("Testing removal of export package", "Exporting state: "
				+ BundleState.stateName(tba.getState()) + ". Importing state: "
				+ BundleState.stateName(tbb.getState()) + ".");
		tba.stop();
		tba.uninstall();
		log("Testing removal of export package", "Exporting bundle stopped.");
		/*
		 * This test is not defined because the spec allows a package to stay
		 * resident even if it's imports are uninstalled. Unfortunately, this is
		 * implementation dependent.
		 * 
		 * log("Testing removal of export package", "Exporting state: " +
		 * BundleState.stateName(tba.getState()) + ". Importing state: " +
		 * BundleState.stateName(tbb.getState()) + ".");
		 */
		tbb.stop();
		tbb.uninstall();
	}
}