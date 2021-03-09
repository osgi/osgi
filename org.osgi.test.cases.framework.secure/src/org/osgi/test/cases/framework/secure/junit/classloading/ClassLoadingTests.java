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
package org.osgi.test.cases.framework.secure.junit.classloading;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.wiring.Wiring;

/**
 * This class contains tests related with the framework class loading policies.
 * 
 * @author left
 * @author $Id$
 */
public class ClassLoadingTests extends DefaultTestBundleControl {
	
	protected void tearDown() {
		Wiring.synchronousRefreshBundles(getContext());
	}

	// Service Registry --------------------------

	/**
	 * Since multiple modules may export permission classes with the same class
	 * name, the framework must make sure that permission checks are performed
	 * using the correct class. The net result is that the framework needs to
	 * look up permissions based on class rather than class name and when it
	 * needs to instantiate a permission it needs to use the class of the
	 * permission being checked to do the instantiation. This is a complication
	 * for framework implementers; bundle programmers are not affected.
	 * 
	 * @spec Bundle.start()
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPermissionChecking001() throws Exception {
		Bundle tb1;
		Bundle tb6a;
		Bundle tb6b;
		Bundle tb6c;
		Bundle tb6d;

		tb1 = installBundle("classloading.tb1.jar");
		tb1.start();
		tb6a = installBundle("classloading.tb6a.jar");
		tb6a.start();
		tb6b = installBundle("classloading.tb6b.jar");
		tb6b.start();
		tb6c = installBundle("classloading.tb6c.jar");
		tb6c.start();

		tb6d = installBundle("classloading.tb6d.jar", false);
		try {
			trace("Changing a permission class to avoid authorization checking");
			tb6d.start();
			tb6d.stop();
		}
		catch (IllegalStateException ex) {
			fail(ex.getMessage());
		}
		finally {
			tb6d.uninstall();

			tb6c.stop();
			tb6c.uninstall();
			tb6b.stop();
			tb6b.uninstall();
			tb6a.stop();
			tb6a.uninstall();
			tb1.stop();
			tb1.uninstall();
		}
	}

	// Exporting System Classes --------------------

	/**
	 * Test that in order to be allowed to require a name bundle, the requiring
	 * bundle must have:
	 * 
	 * BundlePermission[ <required bundle symbolic name>, REQUIRE_BUNDLE]
	 * 
	 * Test that when the resolution directive has a value of "mandatory" the
	 * required bundle must be resolved if the requiring module is resolved.
	 * 
	 * @spec Bundle.start()
	 * @throws Exception if any failure occurs or any assert fails
	 */
	public void testRequiredBundle003() throws Exception {
		Bundle tb16b = getContext().installBundle(
				getWebServer() + "classloading.tb16b.jar");
		Bundle tb16d = getContext().installBundle(
				getWebServer() + "classloading.tb16d.jar");
		try {
			tb16d.start();
			tb16d.stop();
			fail("Expecting BundleException");
		}
		catch (BundleException e) {
			// expected
		}
		finally {
			uninstallBundle(tb16d);
			uninstallBundle(tb16b);
		}
	}

}
