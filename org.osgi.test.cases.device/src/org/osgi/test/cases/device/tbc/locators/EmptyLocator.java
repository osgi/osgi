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

import java.io.InputStream;
import java.util.Dictionary;

import org.osgi.service.device.DriverLocator;
import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * This locator is registered to check that it will be called when a driver is
 * unregistered.
 * 
 * @author Vasil Panushev
 * @version 1.0
 * @since
 */
public class EmptyLocator implements DriverLocator {
	private TestBundleControl	master	= null;

	public EmptyLocator(TestBundleControl master) {
		this.master = master;
	}

	/**
	 * Will just log if drivers are searched for device with ID "standalone
	 * driver test device" Searching drivers for other devices will fail the
	 * test
	 * 
	 * @param props the props of the device
	 * @return null every time
	 */
	public String[] findDrivers(Dictionary<String, ? > props) {
		// Also gets called for UPnP devices it seems ...
		if (props.get("device.test").equals(Boolean.TRUE)) {
			if ("standalone driver test device".equals(props.get("deviceID"))) {
				master.setMessage(TestBundleControl.MESSAGE_OK);
				log("find drivers invoked for the correct device - OK!");
			}
			else {
				master.setMessage(TestBundleControl.MESSAGE_ERROR);
				log("find drivers invoked for: " + props.get("deviceID")
						+ " - Error!");
			}
		} 
		return null;
	}

	/**
	 * Should be never invoked as the findDrivers method returns null
	 * 
	 * @param name
	 * @return null
	 */
	public InputStream loadDriver(String name) {
		log("load driver invoked! Error");
		return null;
	}

	private void log(String toLog) {
		master.log("empty driver locator", toLog);
	}
}
