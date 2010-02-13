/*
 * Copyright (c) OSGi Alliance 2002.
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.framework.junit.dynpkgimport;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.cases.framework.dynpkgimport.exported.TestService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class DynPkgImportTests extends DefaultTestBundleControl implements
		FrameworkListener {
	private PackageAdmin	pa	= null;

	protected void setUp() throws Exception {
		pa = (PackageAdmin) getService(PackageAdmin.class);
		getContext().addFrameworkListener(this);
	}

	protected void tearDown() throws Exception {
		getContext().removeFrameworkListener(this);
		pa.refreshPackages(null);
		ungetService(pa);
		Thread.sleep(2000);
	}

	public void testInitial() throws Exception {
		try {
			Bundle tlx = getContext().installBundle(
					getWebServer() + "dynpkgimport.tlx.jar");
			tlx.start();
			Bundle tb0 = getContext().installBundle(
					getWebServer() + "dynpkgimport.tb0.jar");
			tb0.start();
			ServiceReference tsR = getContext().getServiceReference(
					TestService.class.getName());
			if (tsR == null) {
				fail("failed to get TestService reference");
			}
			TestService ts = (TestService) getContext().getService(tsR);
			if (ts == null) {
				fail("failed to get TestService");
			}
			ts.test1();
			getContext().ungetService(tsR);
			tb0.stop();
			tb0.uninstall();
			tlx.uninstall();
		}
		catch (BundleException be) {
			Throwable t = be.getNestedException();
			t.printStackTrace();
			be.printStackTrace();
			fail(be.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	public void testBasicImport1() throws Exception {
		try {
			Bundle tlx = getContext().installBundle(
					getWebServer() + "dynpkgimport.tlx.jar");
			tlx.start();
			Bundle tb1 = getContext().installBundle(
					getWebServer() + "dynpkgimport.tb1.jar");
			tb1.start();
			ServiceReference tsR = getContext().getServiceReference(
					TestService.class.getName());
			if (tsR == null) {
				fail("failed to get TestService reference");
			}
			TestService ts = (TestService) getContext().getService(tsR);
			if (ts == null) {
				fail("failed to get TestService");
			}
			ts.test1();
			getContext().ungetService(tsR);
			tb1.stop();
			tb1.uninstall();
			tlx.uninstall();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	public void testBasicImport2() throws Exception {
		try {
			Bundle tlx = getContext().installBundle(
					getWebServer() + "dynpkgimport.tlx.jar");
			tlx.start();
			Bundle tb2 = getContext().installBundle(
					getWebServer() + "dynpkgimport.tb2.jar");
			tb2.start();
			ServiceReference tsR = getContext().getServiceReference(
					TestService.class.getName());
			if (tsR == null) {
				fail("failed to get TestService reference");
			}
			TestService ts = (TestService) getContext().getService(tsR);
			if (ts == null) {
				fail("failed to get TestService");
			}
			ts.test1();
			getContext().ungetService(tsR);
			tb2.stop();
			tb2.uninstall();
			tlx.uninstall();
		}
		catch (Throwable t) {
			t.printStackTrace();
			fail(t.toString());
		}
	}

	public void testPrecedence() throws Exception {
		Bundle tlx = getContext().installBundle(
				getWebServer() + "dynpkgimport.tlx.jar");
		tlx.start();
		Bundle tb3 = getContext().installBundle(
				getWebServer() + "dynpkgimport.tb3.jar");
		tb3.start();
		ServiceReference tsR = getContext().getServiceReference(
				TestService.class.getName());
		if (tsR == null) {
			fail("failed to get TestService reference");
		}
		TestService ts = (TestService) getContext().getService(tsR);
		if (ts == null) {
			fail("failed to get TestService");
		}
		ts.test1();
		getContext().ungetService(tsR);
		tb3.stop();
		tb3.uninstall();
		tlx.uninstall();
	}

	public void testUninstall() throws Exception {
		Bundle tlx = getContext().installBundle(
				getWebServer() + "dynpkgimport.tlx.jar");
		tlx.start();
		tlx.stop();
		tlx.uninstall();
		pa.refreshPackages(null);
		Thread.sleep(2000);
		Bundle tb2 = getContext().installBundle(
				getWebServer() + "dynpkgimport.tb2.jar");
		tb2.start();
		ServiceReference tsR = getContext().getServiceReference(
				TestService.class.getName());
		if (tsR == null) {
			fail("failed to get TestService reference");
		}
		TestService ts = (TestService) getContext().getService(tsR);
		if (ts == null) {
			fail("failed to get TestService");
		}
		try {
			ts.test1();
		}
		catch (NoClassDefFoundError e) {
			getContext().ungetService(tsR);
			tb2.stop();
			tb2.uninstall();
			return;
		}
//		System.out.println("ts class " + ts.getClass());
//		System.out.println("tlx state" + tlx.getState());
//		ServiceReference ref = getContext().getServiceReference(PackageAdmin.class.getName());
//		PackageAdmin admin = (PackageAdmin) getContext().getService(ref);
//		ExportedPackage ep = admin.getExportedPackage("org.osgi.test.cases.framework.dynpkgimport.tlx");
//		System.out.println("tlx pack " + ep.getExportingBundle().getLocation());
		fail("got no NoClassDefFoundError");
	}

	public void frameworkEvent(FrameworkEvent event) {
		switch (event.getType()) {
			case FrameworkEvent.ERROR :
				log("got framework event " + event.getType() + ": "
						+ event.getThrowable().getClass().getName());
				log("stack trace:");
				event.getThrowable().printStackTrace();
				break;
			default :
				//log("got framework event " + event.getType() + ": ");
				break;
		}
	}
}
