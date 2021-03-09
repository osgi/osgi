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
 * The thread associated with a coordination must be consistent with it being 
 * pushed to or popped from a thread local stack.
 */
public class CoordThreadPushPopTest extends OSGiTestCase {
	private Coordinator coordinator;
	private ServiceReference<Coordinator> coordinatorReference;
	
	/**
	 * Pushing a coordination.
	 */
	public void testPushCoordination() {
		Coordination c = coordinator.create("c", 0);
		assertCoordinationThreadSame(c, null);
		c.push();
		assertCoordinationThreadSame(c, Thread.currentThread());
		coordinator.pop();
		assertCoordinationThreadSame(c, null);
		c.end();
	}
	
	/**
	 * Beginning a coordination.
	 */
	public void testBeginCoordination() {
		Coordination c = coordinator.begin("c", 0);
		assertCoordinationThreadSame(c, Thread.currentThread());
		coordinator.pop();
		assertCoordinationThreadSame(c, null);
		c.end();
	}
	
	protected void setUp() throws Exception {
		coordinatorReference = getContext().getServiceReference(Coordinator.class);
		coordinator = getContext().getService(coordinatorReference);
	}
	
	protected void tearDown() throws Exception {
		getContext().ungetService(coordinatorReference);
	}
	
	private void assertCoordinationThreadSame(Coordination c, Thread expected) {
		assertSame("A coordination's thread must be consistent with push and pop", expected, c.getThread());
	}
}
