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
package org.osgi.test.cases.device.tbc.selectors;

import org.osgi.framework.ServiceReference;
import org.osgi.service.device.DriverSelector;
import org.osgi.service.device.Match;
import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * Second selector. As stated in the specification this selector should be
 * ignored
 * 
 * @author ProSyst
 * @version 1.0
 */
public class DriverLoadingTestSelector2 implements DriverSelector {
	private TestBundleControl	master	= null;

	public DriverLoadingTestSelector2(TestBundleControl master) {
		this.master = master;
	}

	public int select(ServiceReference< ? > reference, Match[] matches) {
		master.log("[Driver loading test selector 2]",
				"second selector called! Error!");
		throw new RuntimeException(
				"[Driver loading test selector 2] second selector called! Error!");
	}
}
