/*
 * $Id$
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
package org.osgi.test.cases.framework.junit.classpath;

import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class ClassPathControl extends DefaultTestBundleControl {

	/** *** Test methods **** */
	/**
	 * Tries to import a package that has not been installed.
	 */
	public void testImportUninstalledCode() throws Exception {
		String s = getWebServer() + "classpath.tb1.jar";
		log("Web req " + s);
		try {
			URL url = new URL(s);
			log("Length "
					+ url.openConnection().getContentLength());
			Bundle tb = getContext().installBundle(s);
			try {
				assertEquals(Bundle.INSTALLED, tb.getState());
				try {
					tb.start();
					fail("No exception thrown, Error!");
				}
				catch (BundleException be) {
					// expected
				}
			}
			finally {
				tb.uninstall();
			}
		}
		catch (Exception e) {
			fail("unexpected exception", e);
		}
	}

	/**
	 * Tests export of a package.
	 */
	public void testExport() throws Exception {
		Bundle tb = getContext().installBundle(
				getWebServer() + "classpath.tb2.jar");
		try {
			try {
				tb.start();
			}
			catch (BundleException be) {
				if (tb.getState() == Bundle.INSTALLED)
					fail("Export failed!", be);
				else
					fail("BundleException thrown, Error!", be);
			}
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests reinstallation of the same bundle.
	 */
	public void testReinstall() throws Exception {
		Bundle tb1 = getContext().installBundle(
				getWebServer() + "classpath.tb3.jar");
		Bundle tb2 = getContext().installBundle(
				getWebServer() + "classpath.tb3.jar");
		try {
			assertEquals("They are not the same", tb1, tb2);
		}
		finally {
			tb1.uninstall();
		}
	}

	/**
	 * Tests removal of exported package.
	 */
	public void testImportGone() throws Exception {
		Bundle tba = getContext().installBundle(
				getWebServer() + "classpath.tb4a.jar");
		Bundle tbb = getContext().installBundle(
				getWebServer() + "classpath.tb4b.jar");
		try {
			tba.start();
			assertEquals(Bundle.ACTIVE, tba.getState());
			tbb.start();
			assertEquals(Bundle.ACTIVE, tbb.getState());
		}
		finally {
			tba.uninstall();
			tbb.uninstall();
		}
	}
	
}