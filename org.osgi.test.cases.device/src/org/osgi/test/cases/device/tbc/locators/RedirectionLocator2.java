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

import org.osgi.service.device.DriverLocator;

/**
 * When using redirection all registred locators should be called. This locator
 * is used to check this. Does nothing else at all
 * 
 * @author ProSyst
 * @version 1.0
 */
public class RedirectionLocator2 implements DriverLocator {
	public static boolean	called	= false;

	public String[] findDrivers(Dictionary<String, ? > props) {
		return null;
	}

	public InputStream loadDriver(String id) throws IOException {
		called = true;
		return null;
	}
}
