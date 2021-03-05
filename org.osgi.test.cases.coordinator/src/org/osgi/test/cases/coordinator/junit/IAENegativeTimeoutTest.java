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
 * An IllegalArgumentException must be thrown when a timeout value is negative.
 */
public class IAENegativeTimeoutTest extends OSGiTestCase {
	private Coordinator coordinator;
	private ServiceReference<Coordinator> coordinatorReference;
	
	/**
	 * Beginning a coordination.
	 */
	public void testBeginCoordination() {
		IllegalArgumentException iae = null;
		try {
			Coordination c = coordinator.begin("c", -1);
			c.end();
		}
		catch (IllegalArgumentException e) {
			iae = e;
		}
		assertIllegalArgumentException(iae);
	}
	
	/**
	 * Creating a coordination.
	 */
	public void testCreateCoordination() {
		IllegalArgumentException iae = null;
		try {
			Coordination c = coordinator.create("c", -5000);
			c.end();
		}
		catch (IllegalArgumentException e) {
			iae = e;
		}
		assertIllegalArgumentException(iae);
	}
	
	/**
	 * Extending a timeout.
	 */
	public void testCoordinationFail() {
		IllegalArgumentException iae = null;
		Coordination c = coordinator.create("c", 30000);
		try {
			c.extendTimeout(-30000);
		}
		catch (IllegalArgumentException e) {
			iae = e;
		}
		finally {
			c.end();
		}
		assertIllegalArgumentException(iae);
	}
	
	/**
	 * Joining a coordination.
	 * 
	 * @throws InterruptedException
	 */
	public void testCoordinationJoin() throws InterruptedException {
		IllegalArgumentException iae = null;
		Coordination c = coordinator.create("c", 2000);
		try {
			c.join(-100);
		}
		catch (IllegalArgumentException e) {
			iae = e;
		}
		finally {
			c.fail(new Exception());
		}
		assertIllegalArgumentException(iae);
	}
	
	protected void setUp() throws Exception {
		coordinatorReference = getContext().getServiceReference(Coordinator.class);
		coordinator = getContext().getService(coordinatorReference);
	}
	
	protected void tearDown() throws Exception {
		getContext().ungetService(coordinatorReference);
	}
	
	private void assertIllegalArgumentException(IllegalArgumentException e) {
		assertNotNull("An IllegalArgumentException must be thrown if a negative timeout is provided", e);
	}
}
