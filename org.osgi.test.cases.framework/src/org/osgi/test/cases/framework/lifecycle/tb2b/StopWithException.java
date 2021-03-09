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
package org.osgi.test.cases.framework.lifecycle.tb2b;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * Bundle for the StopWithException test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class StopWithException implements BundleActivator {
	/**
	 * Starts the bundle.
	 */
	public void start(BundleContext bc) {
		// empty
	}

	/**
	 * Stops the bundle. Throws a BundleException.
	 */
	public void stop(BundleContext bc) throws BundleException {
		throw new BundleException("A bundle exception.");
	}
}
