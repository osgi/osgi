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
package org.osgi.test.cases.framework.div.tb10;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The test bundle activator
 * 
 * @author Ericsson Radio Systems AB
 */
public class Activator implements BundleActivator {

	TestServiceImpl		service;
	ServiceRegistration<TestService>	serviceRegistration;

	/**
	 * Installs several services later filtered by the tbc
	 */
	public void start(BundleContext bc) {
		service = new TestServiceImpl();
		serviceRegistration = bc
				.registerService(TestService.class, service, null);
	}

	/**
	 * Stops the bundle by unregistering the services.
	 */
	public void stop(BundleContext bc) {
		try {
			serviceRegistration.unregister();
		}
		catch (IllegalStateException e) { /* Ignore */
		}
		
		service = null;
		serviceRegistration = null;
	}
}
