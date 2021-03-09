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

import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Dictionary;

import org.osgi.service.device.DriverLocator;
import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * The locator used in the device detection test. For all IDs it returns an
 * InputStream to the basic driver (in fact this is the only driver bundle in
 * the whole test case)
 */
public class BasicTestLocator implements DriverLocator {
	final TestBundleControl	master;

	/**
	 * @param master the master of test case - used for logging
	 */
	public BasicTestLocator(TestBundleControl master) {
		this.master = master;
	}

	/**
	 * Searches for drivers
	 * 
	 * @param props the properties of the registered Device service
	 * @return a single element String array containing the property with key
	 *         "deviceID" from the props Dictionary
	 */
	public String[] findDrivers(Dictionary<String, ? > props) {
		log("searching for " + props.get("deviceID"));
		String[] toReturn = new String[1];
		toReturn[0] = (String) props.get("deviceID");
		return toReturn;
	}

	/**
	 * Finds an InputStream to a driver with the passed id. This implementation
	 * returns always InputStream to the same bundle.
	 * 
	 * @param id the id of the driver to be loaded
	 * @return an InputStream to the basicdrv.jar bundle
	 */
	public InputStream loadDriver(String id) {
		log("loading for " + id);
		try {
			return AccessController
					.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
						public InputStream run() throws Exception {
							URL url = new URL(master.getWebServer()
									+ "drv1.jar");
							return url.openStream();
						}
					});
		}
		catch (PrivilegedActionException e) {
			e.getException().printStackTrace();
			log("error while reading the /drivers/basicdrv.jar resource");
			return null;
		}
	}

}
