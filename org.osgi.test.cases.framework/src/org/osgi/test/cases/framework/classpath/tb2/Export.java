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
package org.osgi.test.cases.framework.classpath.tb2;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Bundle for the Export test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class Export implements BundleActivator {
	/**
	 * Starts the bundle. Creates an object of the imported class
	 * "org.osgi.test.cases.framework.classpath.tbc.exp.Exported".
	 */
	public void start(BundleContext bc) {
		@SuppressWarnings("unused")
		org.osgi.test.cases.framework.classpath.exported.Exported e;
		e = new org.osgi.test.cases.framework.classpath.exported.Exported();
	}

	/**
	 * Stops the bundle.
	 */
	public void stop(BundleContext bc) {
	}
}
