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

package org.osgi.test.cases.tracker.tb2;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.tracker.service.TestService2;

/**
 * Bundle for exporting packages
 * 
 * @author Ericsson Telecom AB
 */
public class TB2Activator implements BundleActivator {

	/**
	 * Starts the bundle. Installs several services later filtered by the tbc
	 */
	public void start(BundleContext bc) {
		Hashtable<String, Object> ts2Props = new Hashtable<String, Object>();
		ts2Props.put("name", "TestService2");
		ts2Props.put("version", Float.valueOf(1.0f));
		ts2Props.put("compatible", Float.valueOf(1.0f));
		ts2Props.put("description", "TestService 2");

		bc.registerService(TestService2.class.getName(), new TestService2() {
			// empty
		}, ts2Props);

	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
		// empty
	}
}
