/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.framework.junit.dynpkgimport;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.framework.dynpkgimport.exported.TestService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.wiring.Wiring;

public class DynPkgImportTests extends DefaultTestBundleControl implements
		FrameworkListener {

	protected void setUp() throws Exception {
		getContext().addFrameworkListener(this);
	}

	protected void tearDown() throws Exception {
		getContext().removeFrameworkListener(this);
		Wiring.synchronousRefreshBundles(getContext());
	}

	public void testInitial() throws Exception {
		try {
			Bundle tlx = getContext().installBundle(
					getWebServer() + "dynpkgimport.tlx.jar");
			tlx.start();
			Bundle tb0 = getContext().installBundle(
					getWebServer() + "dynpkgimport.tb0.jar");
			tb0.start();
			ServiceReference<TestService> tsR = getContext()
					.getServiceReference(TestService.class);
			if (tsR == null) {
				fail("failed to get TestService reference");
			}
			TestService ts = getContext().getService(tsR);
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
			ServiceReference<TestService> tsR = getContext()
					.getServiceReference(TestService.class);
			if (tsR == null) {
				fail("failed to get TestService reference");
			}
			TestService ts = getContext().getService(tsR);
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
			ServiceReference<TestService> tsR = getContext()
					.getServiceReference(TestService.class);
			if (tsR == null) {
				fail("failed to get TestService reference");
			}
			TestService ts = getContext().getService(tsR);
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
		ServiceReference<TestService> tsR = getContext()
				.getServiceReference(TestService.class);
		if (tsR == null) {
			fail("failed to get TestService reference");
		}
		TestService ts = getContext().getService(tsR);
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
		Wiring.synchronousRefreshBundles(getContext());
		Bundle tb2 = getContext().installBundle(
				getWebServer() + "dynpkgimport.tb2.jar");
		tb2.start();
		ServiceReference<TestService> tsR = getContext()
				.getServiceReference(TestService.class);
		if (tsR == null) {
			fail("failed to get TestService reference");
		}
		TestService ts = getContext().getService(tsR);
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
		// Write some wiring API code to see what bundle exports:
		// "org.osgi.test.cases.framework.dynpkgimport.tlx");
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
