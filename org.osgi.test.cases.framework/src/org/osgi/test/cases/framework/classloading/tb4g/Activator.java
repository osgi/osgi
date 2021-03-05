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

package org.osgi.test.cases.framework.classloading.tb4g;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.framework.classloading.exports.service.SomeService;

/**
 * A bundle activator which execute some tests
 * 
 * @author left
 * @author $Id$
 */
public class Activator implements BundleActivator {

	private ServiceReference< ? > sr;

	/**
	 * Creates a new instance of Activator
	 */
	public Activator() {

	}

	/**
	 * Start the bundle, getting a SomeService instance and checking its version
	 * and bundle
	 * 
	 * @param context the bundle context
	 * @throws Exception if some problem occur
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		SomeService someService;

		sr = context.getServiceReferences(SomeService.class.getName(),
				"(version=2.0.0)")[0];

		// Check service bundle
		if (!sr.getBundle().getSymbolicName().equals(
				"org.osgi.test.cases.framework.classloading.tb2a")) {
			throw new BundleException("The service bundle is not the expected");
		}

		someService = (SomeService) context.getService(sr);

		// Check the service bundle
		if (!someService.getRegistrantBundle().equals(sr.getBundle())) {
			throw new BundleException("The service version is not the expected");
		}
	}

	/**
	 * Stop the bundle
	 * 
	 * @param context the bundle context
	 * @throws Exception if some problem occur
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		context.ungetService(sr);
		sr = null;
	}
}
