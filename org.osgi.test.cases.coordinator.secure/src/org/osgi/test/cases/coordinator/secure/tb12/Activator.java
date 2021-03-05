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

package org.osgi.test.cases.coordinator.secure.tb12;

import java.util.Collection;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.test.cases.coordinator.secure.TestClassResult;
import org.osgi.test.cases.coordinator.secure.TestClassResultImpl;

/**
 * Gets coordinations with permission only for coordinations whose name starts
 * with com.ibm.*.
 */
public class Activator implements BundleActivator {
	private ServiceRegistration<TestClassResult>	resultsRegistration;
	
	public void start(BundleContext bc) throws Exception {
		ServiceReference<Coordinator> sr = bc
				.getServiceReference(Coordinator.class);
		Coordinator c = bc.getService(sr);
		boolean result = false;
		Collection<ServiceReference<Coordination>> srs = bc
				.getServiceReferences(Coordination.class, null);
		if (srs.size() == 2) {
			for (ServiceReference<Coordination> csr : srs) {
				Coordination co = bc.getService(csr);
				bc.ungetService(csr);
				if (co.getName().startsWith("com.ibm")) {
					result = c.getCoordination(co.getId()) != null;
				}
				else if (co.getName().startsWith("com.acme")) {
					result = c.getCoordination(co.getId()) == null;
				}
				else {
					result = false;
					break;
				}
			}
		}
		resultsRegistration = bc.registerService(TestClassResult.class,
				new TestClassResultImpl(result), null);
		bc.ungetService(sr);
	}

	public void stop(BundleContext bc) throws Exception {
		resultsRegistration.unregister();
	}
}
