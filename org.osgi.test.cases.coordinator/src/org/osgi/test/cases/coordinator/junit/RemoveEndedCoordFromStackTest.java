/*
 * Copyright (c) OSGi Alliance (2011, 2017). All Rights Reserved.
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

import junit.framework.AssertionFailedError;

import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.CoordinationException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Coordinations can only be removed from the thread local stack by calling the
 * Coordination.end() method. The calling thread must be the same one that
 * pushed it onto the stack. If not, a CoordinationException of type
 * CoordinationException.WRONG_THREAD must be thrown.
 */
public class RemoveEndedCoordFromStackTest extends CoordinatorTest {
	/**
	 * A basic test for the removal of a coordination from the thread local
	 * stack when ended.
	 */
	public void testRemovalBasic() {
		Coordination c = coordinator.begin("c", 0);
		assertAtTopOfStack(c);
		c.end();
		assertTerminated(c);
		assertEmptyStack();
	}
	
	/**
	 * A basic test for throwing a CoordinationException of type WRONG_THREAD
	 * when attempting to end a coordination from a thread other than the one
	 * that pushed it to the stack
	 * @throws InterruptedException
	 */
	public void testWrongThreadBasic() throws InterruptedException {
		final Coordination c = coordinator.create("c", 0);
		final AtomicReference<CoordinationException> e = new AtomicReference<CoordinationException>();
		Thread t = new Thread() {
			public void run() {
				try {
					c.end();
				}
				catch (CoordinationException ce) {
					e.set(ce);
				}
			}
		};
		try {
			c.push();
			assertAtTopOfStack(c);
			t.start();
			t.join();
			assertCoordinationException(e.get(), CoordinationException.WRONG_THREAD, null);
			assertAtTopOfStack(c);
			c.end();
		}
		catch (AssertionFailedError afe) {
			try {
				c.end();
			}
			catch (CoordinationException ce) {
				// Eat this so it doesn't hide the test failure.
			}
			throw afe;
		}
	}
}
