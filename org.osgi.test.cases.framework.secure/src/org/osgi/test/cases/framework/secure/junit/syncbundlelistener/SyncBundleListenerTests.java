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
package org.osgi.test.cases.framework.secure.junit.syncbundlelistener;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson Telecom AB
 */
public class SyncBundleListenerTests extends DefaultTestBundleControl {
	/**
	 * control that a security exception is thrown if we try to add a
	 * synchronous bundle listener without adminPermission
	 */
	public void testPermission() throws Exception {
		Bundle permissionBundle = getContext().installBundle(
				getWebServer() + "syncbundlelistener.tb1a.jar");
		try {
			permissionBundle.start();
		}
		catch (BundleException e) {
			fail("Could not add SynchronousBundleListener", e);
		}
		finally {
			permissionBundle.uninstall();
		}
	}

	/**
	 * control that a security exception is thrown if we try to add a
	 * synchronous bundle listener without adminPermission
	 */
	public void testNoPermission() throws Exception {
		Bundle permissionBundle = getContext().installBundle(
				getWebServer() + "syncbundlelistener.tb1b.jar");
		try {
			permissionBundle.start();
		}
		catch (BundleException e) {
			Throwable cause = e.getCause();
			if (cause instanceof SecurityException) {
				return;
			}
		}
		finally {
			permissionBundle.uninstall();
		}
		fail("Was able to add SynchronousBundleListener");
	}

}
