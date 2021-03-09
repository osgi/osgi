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
package org.osgi.test.cases.device.tbc.locators;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;

import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * Simulates crash in findDrivers method
 * 
 * @author ProSyst
 * @version 1.0
 */
public class DriverLoadingLocator3 implements
		org.osgi.service.device.DriverLocator {
	private TestBundleControl	master	= null;

	public DriverLoadingLocator3(TestBundleControl master) {
		this.master = master;
	}

	/**
	 * Crashes to test the exception handling for locators
	 * 
	 * @throws RuntimeException every time
	 */
	public String[] findDrivers(Dictionary<String, ? > props) {
		throw new RuntimeException(
				"[findDrivers] Testing the device manager exception handling");
	}

	/**
	 * Should not be called so the call will cause test case failure
	 * 
	 * @returns null
	 */
	public InputStream loadDriver(String id) throws IOException {
		master.log("[Exception throwing locator]",
				"loaddDriver called with ID " + id + " error!!!!");
		return null;
	}
}
