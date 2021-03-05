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
package org.osgi.test.cases.coordinator.junit;

import org.osgi.framework.ServiceReference;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.test.support.OSGiTestCase;

/**
 * A NullPointerException must be thrown if the reason specified for a 
 * coordination's failure is null.
 */
public class NullFailureReasonTest extends OSGiTestCase {
	private Coordinator coordinator;
	private ServiceReference<Coordinator> coordinatorReference;
	
	/**
	 * Coordination.fail(Throwable)
	 */
	public void testCoordinationFail() {
		RuntimeException npe = null;
		Coordination c = coordinator.create("c", 0);
		try {
			c.fail(null);
		}
		catch (RuntimeException e) {
			npe = e;
		}
		c.end();
		assertRuntimeException(npe);
	}
	
	/**
	 * Coordinator.fail(Throwable)
	 */
	public void testCoordinatorFail() {
		RuntimeException npe = null;
		Coordination c = coordinator.begin("c", 0);
		try {
			coordinator.fail(null);
		}
		catch (RuntimeException e) {
			npe = e;
		}
		c.end();
		assertRuntimeException(npe);
	}
	
	protected void setUp() throws Exception {
		coordinatorReference = getContext().getServiceReference(Coordinator.class);
		coordinator = getContext().getService(coordinatorReference);
	}
	
	protected void tearDown() throws Exception {
		getContext().ungetService(coordinatorReference);
	}
	
	private void assertRuntimeException(RuntimeException e) {
		assertNotNull("An runtime exception must be thrown if the reason for failure is null", e);
	}
}
