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
package org.osgi.test.cases.coordinator.secure;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;

/**
 * Performs common operations required by tests.
 */
public abstract class AbstractActivator implements BundleActivator {
	/**
	 * The coordination, if any, registered by the test.
	 */
	protected Coordination coordination;
	/**
	 * The Coordinator service.
	 */
	protected Coordinator coordinator;
	
	private ServiceReference<Coordination>			coordinationRef;
	private ServiceReference<Coordinator>			coordinatorRef;
	private ServiceRegistration<TestClassResult>	resultsRegistration;
	
	public void start(BundleContext bc) throws Exception {
		coordinationRef = bc.getServiceReference(Coordination.class);
		if (coordinationRef != null)
			coordination = bc.getService(coordinationRef);
		coordinatorRef = bc.getServiceReference(Coordinator.class);
		coordinator = bc.getService(coordinatorRef);
		SecurityException result = null;
		try {
			doStart();
		}
		catch (SecurityException e) {
			result = e;
		}
		boolean succeeded = hasPermission() ? result == null : result != null;
		resultsRegistration = bc.registerService(TestClassResult.class,
				new TestClassResultImpl(succeeded), null);
	}

	public void stop(BundleContext bc) throws Exception {
		resultsRegistration.unregister();
		bc.ungetService(coordinatorRef);
		if (coordinationRef != null)
			bc.ungetService(coordinationRef);
	}
	
	/**
	 * Subclasses use this to execute the single test that may or may not result
	 * in a SecurityException depending on permissions.
	 * @throws Exception
	 */
	protected abstract void doStart() throws Exception;
	
	/**
	 * Indicates whether or not the test should have permission and a SecurityException should not have occurred.
	 * @return true if the test should have permission and a SecurityException should not have occurred, false otherwise.
	 */
	protected abstract boolean hasPermission();
}
