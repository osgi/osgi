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
