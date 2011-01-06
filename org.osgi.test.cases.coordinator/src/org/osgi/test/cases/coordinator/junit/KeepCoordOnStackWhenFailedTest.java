/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.coordinator.junit;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.CoordinationException;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.test.support.OSGiTestCase;

/**
 * Coordinations can be failed from any thread. This is necessary to allow for
 * asynchronous failures such as timeouts or bundle stoppage. However, failed
 * coordinations must not be removed from the stack because this could hide the
 * failure in the case of a thread local coordination. Moreover, in the case of
 * nested coordinations, participants could be added to the wrong coordination.
 */
public class KeepCoordOnStackWhenFailedTest extends OSGiTestCase {
	private Coordinator coordinator;
	private ServiceReference coordinatorReference;
	
	/**
	 * The basic test.
	 * @throws InterruptedException
	 */
	public void testBasic() throws InterruptedException {
		final Coordination c = coordinator.begin("c", 0);
		assertEquals("Coordination 'c' must be at the top of the thread local stack.", c, coordinator.peek());
		Thread thread = new Thread() {
			public void run() {
				c.fail(Coordination.TIMEOUT);
			}
		};
		thread.start();
		thread.join();
		assertTrue("The coordination must be terminated due to the timeout failure.", c.isTerminated());
		assertEquals("The failure cause must be Coordination.TIMEOUT.", Coordination.TIMEOUT, c.getFailure());
		assertEquals("A coordination's failure must not result in its removal from the thread local stack", c, coordinator.peek());
		try {
			c.end();
			fail("Ending the coordination must result in a CoordinationException since it was already terminated.");
		}
		catch (CoordinationException e) {
			assertEquals("The exception type must be CoordinationException.FAILED.", CoordinationException.FAILED, e.getType());
			assertEquals("The exception cause must be Coordination.TIMEOUT.", Coordination.TIMEOUT, e.getCause());
		}
		assertNull("Ending a coordination must result in its removal from the thread local stack.", coordinator.peek());
	}
	
	protected void setUp() throws Exception {
		// Clean up any coordinations that may be lingering on the thread local
		// stack from previous tests.
		Bundle bundle = getContext().getServiceReference(Coordinator.class.getName()).getBundle();
		bundle.stop();
		bundle.start();
		coordinatorReference = getContext().getServiceReference(Coordinator.class.getName());
		coordinator = (Coordinator)getContext().getService(coordinatorReference);
	}
	
	protected void tearDown() throws Exception {
		getContext().ungetService(coordinatorReference);
	}
}
