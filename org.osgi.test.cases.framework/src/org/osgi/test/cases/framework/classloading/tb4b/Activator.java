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

package org.osgi.test.cases.framework.classloading.tb4b;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

/**
 * A bundle activator which execute some tests
 * 
 * @author left
 * @author $Id$
 */
public class Activator implements BundleActivator {

	/**
	 * Creates a new instance of Activator
	 */
	public Activator() {

	}

	/**
	 * Start the bundle and execute the tests
	 * 
	 * @param context the bundle context
	 * @throws Exception if some problem occur
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		ServiceReference< ? > sr;
		ServiceReference< ? >[] srs;

		srs = context.getServiceReferences("non.existent.service",
				"(version=*)");
		sr = context.getServiceReference("non.existent.service");

		try {
			// Check the test pre-condition
			if (srs != null) {
				throw new BundleException(
						"This test cannot be executed with registered services");
			}

			// Execute the test
			if (sr != null) {
				throw new BundleException(
						"When BundleContext.getServiceReferences() returns null, the method BundleContext.getServiceReference() must return too");
			}
		}
		finally {
			if (sr != null) {
				context.ungetService(sr);
			}
			if (srs != null) {
				for (int i = 0; i < srs.length; i++) {
					if (srs[i] != null) {
						context.ungetService(srs[i]);
					}
				}
			}
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
	}

}
