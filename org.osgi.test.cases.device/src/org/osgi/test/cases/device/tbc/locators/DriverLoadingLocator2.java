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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Dictionary;

import org.osgi.service.device.DriverLocator;
import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * Used in the driver loadin test. Simulates some crashes that may happen in the
 * loadDriver method
 * 
 * @author ProSyst
 * @version 1.0
 */
public class DriverLoadingLocator2 implements DriverLocator {
	private TestBundleControl	master	= null;

	public DriverLoadingLocator2(TestBundleControl master) {
		this.master = master;
	}

	/**
	 * Returns some IDs that will be recognized later in the loadDriver method
	 * and the following situations will be recognized: exception thrown, null
	 * returned, invalid stream returned
	 */
	public String[] findDrivers(Dictionary<String, ? > props) {
		String[] toReturn = new String[3];
		toReturn[0] = "exceptionInLoading";
		toReturn[1] = "returnNull";
		toReturn[2] = "invalidStream";
		return toReturn;
	}

	/**
	 * Simulates some crash situations
	 * 
	 * @param id according to this id the method simulates a crash situation
	 * @returns null if id is equal to "returnNull", an InputStream that
	 *          contains invalid data if id is equal to "invalidStream"
	 * @throws RuntimeException if id equals to "exceptionInLoading" or if
	 *         unkown id is passed
	 */
	public InputStream loadDriver(String id) {
		if ("exceptionInLoading".equals(id)) {
			throw new RuntimeException(
					"[load driver] testing the device manager exception handling");
		}
		else
			if ("returnNull".equals(id)) {
				return null;
			}
			else
				if ("invalidStream".equals(id)) {
					byte[] invalid = new byte[100];
					byte i = 0;
					while (i < 100) {
						invalid[i++] = i;
					}
					return new ByteArrayInputStream(invalid);
				}
				else {
					master.log("load driver locator 2", "Invalid ID passed!!!");
					throw new RuntimeException(
							"[load driver locator 2] Invalid ID passed!!!");
				}
	}
}
