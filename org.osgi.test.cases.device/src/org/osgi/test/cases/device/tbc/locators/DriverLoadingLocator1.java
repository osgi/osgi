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

import static org.osgi.test.support.compatibility.DefaultTestBundleControl.log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Dictionary;

import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * Used in the driver loading test. This is the only locator that will return
 * some real drivers to be installed
 * 
 * @author ProSyst
 * @version 1.0
 */
public class DriverLoadingLocator1 implements
		org.osgi.service.device.DriverLocator {
	final TestBundleControl	master;

	/**
	 * @param master the master of test case - used for logging
	 */
	public DriverLoadingLocator1(TestBundleControl master) {
		this.master = master;
	}

	/**
	 * Searches for drivers
	 * 
	 * @param props the properties of the registed Device service
	 * @retunrs a String array of 3 elements - the IDs of the drivers that will
	 *          be used in the driver loading test
	 */
	public String[] findDrivers(Dictionary<String, ? > props) {
		log("searching for drivers");
		String[] toReturn = new String[3];
		toReturn[0] = "Driver_Common";
		toReturn[1] = "Driver_Winner";
		toReturn[2] = "Driver_NotMatch";
		return toReturn;
	}

	/**
	 * Finds an InputStream to a driver with the passed id. This implementation
	 * recognizes the IDs of the drivers that are loaded in the driver loading
	 * selection test.
	 * 
	 * @param id the id of the driver to be loaded
	 * @return an InputStream to the driver bundle corresponding to the passed
	 *         id.
	 */
	public InputStream loadDriver(final String id) throws IOException {
		try {
			return AccessController
					.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
						public InputStream run() throws Exception {
							log("loading for " + id);
							if ("Driver_Winner".equals(id)) {
								URL url = new URL(master.getWebServer()
										+ "drv4.jar");
								return url.openStream();
							}
							else
								if ("Driver_Common".equals(id)) {
									URL url = new URL(master.getWebServer()
											+ "drv2.jar");
									return url.openStream();
								}
								else
									if ("Driver_NotMatch".equals(id)) {
										URL url = new URL(
												master.getWebServer()
														+ "drv5.jar");
										return url.openStream();
									}
									else {
										master.log(
												"driver loader test locator 2",
												"unknown driver ID passed "
														+ id);
										return null;
									}
						}
					});
		}
		catch (PrivilegedActionException ex) {
			throw ((IOException) ex.getException());
		}
	}

}
