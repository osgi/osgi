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
import org.osgi.service.coordinator.CoordinationException;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.test.support.OSGiTestCase;

/**
 *	Contains utilities used by other tests.
 */
public abstract class CoordinatorTest extends OSGiTestCase {
	/**
	 * The Coordinator service.
	 */
	protected Coordinator coordinator;
	/**
	 * The Coordinator service reference.
	 */
	protected ServiceReference<Coordinator> coordinatorReference;
	
	private Coordination peeked;
	
	/**
	 * Ensures the provided Coordination is at the top of the current thread's
	 * local stack.
	 * @param c The Coordination whose stack will be used.
	 */
	protected void assertAtTopOfStack(Coordination c) {
		assertEquals("The coordination must be at the top of the thread local stack.", c, coordinator.peek());
	}
	
	/**
	 * Ensures a CoordinationException occurred.
	 * @param t The CoordinationException that occurred.
	 */
	protected void assertCoordinationException(Throwable t) {
		assertTrue("Not a CoordinationException.", t instanceof CoordinationException);
	}
	
	/**
	 * Ensures a CoordinationException occurred and has the expected cause.
	 * @param e The CoordinationException that occurred.
	 * @param expectedCause The expected cause of the CoordinationException.
	 */
	protected void assertCoordinationExceptionCause(CoordinationException e, Throwable expectedCause) {
		assertEquals("The CoordinationException cause was incorrect", expectedCause, e.getCause());
	}
	
	/**
	 * Ensures a CoordinationException occurred and is of the expected type.
	 * @param e The CoordinationException that occurred.
	 * @param expectedType The expected type of CoordinationException.
	 */
	protected void assertCoordinationExceptionType(CoordinationException e, int expectedType) {
		assertEquals("The CoordinationException type was incorrect.", expectedType, e.getType());
	}
	
	/**
	 * Ensures a CoordinationException occurred, is of the expected type, and
	 * has the expected cause.
	 * @param t The CoordinationException that occurred.
	 * @param expectedType The expected type of CoordinationException.
	 * @param expectedCause The expected cause of the CoordinationException.
	 */
	protected void assertCoordinationException(Throwable t, int expectedType, Throwable expectedCause) {
		assertCoordinationException(t);
		assertCoordinationExceptionType((CoordinationException)t, expectedType);
		assertCoordinationExceptionCause((CoordinationException)t, expectedCause);
	}
	
	/**
	 * Ensures a CoordinationException occurred and is of the expected type.
	 * @param t The CoordinationException that occurred.
	 * @param expectedType The expected type of CoordinationException.
	 */
	protected void assertCoordinationException(Throwable t, int expectedType) {
		assertCoordinationException(t);
		assertCoordinationExceptionType((CoordinationException)t, expectedType);
	}
	
	/**
	 * Ensures the current thread's local stack is empty.
	 */
	protected void assertEmptyStack() {
		assertNull("The thread local stack must be empty.", coordinator.peek());
	}
	
	/**
	 * Ensures the provided Coordination ends normally.
	 * @param c The Coordination to end.
	 */
	protected void assertEnd(Coordination c) {
		try {
			c.end();
		}
		catch (CoordinationException e) {
			fail("A CoordinationException should not have occurred.");
		}
	}
	
	/**
	 * Ensures the provided Coordination does not end successfully.
	 * @param c The Coordination to end.
	 * @return The CoordinationException that occurred.
	 */
	protected CoordinationException assertEndFailed(Coordination c) {
		CoordinationException result = null;
		try {
			c.end();
			fail("Ending the coordination must result in a CoordinationException.");
		}
		catch (CoordinationException e) {
			result = e;
		}
		return result;
	}
	
	/**
	 * Ensures the provided Coordination does not end successfully and that the
	 * resulting CoordinationException is of the expected type.
	 * @param c The Coordination to end.
	 * @param expectedType The expected type of the CoordinationException.
	 */
	protected void assertEndFailed(Coordination c, int expectedType) {
		try {
			c.end();
			fail("Ending the coordination must result in a CoordinationException.");
		}
		catch (CoordinationException e) {
			assertEquals("The CoordinationException type was incorrect.", expectedType, e.getType());
		}
	}
	
	/**
	 * Ensures the provided Coordination does not end successfully and that the
	 * resulting CoordinationException is of the expected type and has the
	 * expected cause.
	 * @param c The Coordination to end.
	 * @param expectedType The expected type of the CoordinationException.
	 * @param expectedCause The expected cause of the CoordinationException.
	 */
	protected void assertEndFailed(Coordination c, int expectedType, Throwable expectedCause) {
		assertCoordinationException(assertEndFailed(c), expectedType, expectedCause);
	}
	
	/**
	 * Ensures the provided Coordination has a failure cause that is of the
	 * expected type.
	 * @param c The Coordination whose failure will be inspected.
	 * @param expectedFailure The expected failure.
	 */
	protected void assertFailure(Coordination c, Throwable expectedFailure) {
		assertEquals("The failure cause was incorrect.", expectedFailure, c.getFailure());
	}
	
	/**
	 * Ensures the provided Coordination is not terminated.
	 * @param c The Coordination that should not be terminated.
	 */
	protected void assertNotTerminated(Coordination c) {
		assertFalse("The coordination should not be terminated.", c.isTerminated());
	}

	/**
	 * Ensures the Coordination's enclosing coordination matches the provided
	 * one.
	 * 
	 * @param c The Coordination whose enclosing coordination will be inspected.
	 * @param expectedEnclosing The expected enclosing coordination.
	 */
	protected void assertEnclosingCoordination(Coordination c,
			Coordination expectedEnclosing) {
		assertEquals("The parent coordination was incorrect.",
				expectedEnclosing, c.getEnclosingCoordination());
	}
	
	/**
	 * Ensures the provided Coordination is terminated.
	 * @param c The Coordination that should be terminated.
	 */
	protected void assertTerminated(Coordination c) {
		assertTrue("The coordination must be terminated.", c.isTerminated());
	}
	
	/**
	 * Ensures the provided Coordination terminated normally.
	 * @param c The Coordination to inspect.
	 */
	protected void assertTerminatedNormally(Coordination c) {
		assertTerminated(c);
		assertFailure(c, null);
	}
	
	/**
	 * Ensures the provided Coordination is terminated and has the expected
	 * failure.
	 * @param c The Coordination to inspect.
	 * @param expectedFailure The expected failure.
	 */
	protected void assertTerminatedFailed(Coordination c, Throwable expectedFailure) {
		assertTerminated(c);
		assertFailure(c, expectedFailure);
	}
	
	/**
	 * Ensures the provided Coordination is terminated and has a
	 * CoordinationException as the failure of the expected type.
	 * @param c The Coordination to inspect.
	 * @param expectedType The expected type of CoordinationException.
	 */
	protected void assertTerminatedFailed(Coordination c, int expectedType) {
		assertTerminated(c);
		assertCoordinationException(c.getFailure(), expectedType);
	}
	
	/**
	 * Ensures the current deadline is at least as much as expected. If there
	 * is no timeout, the current deadline must be zero.
	 * @param start The start time of the coordination.
	 * @param timeout The total amount of timeout requested.
	 * @param c The coordination affected by the timeout.
	 */
	protected void assertDeadline(long start, long timeout, Coordination c) {
		String message = "Wrong deadline";
		long deadline = c.extendTimeout(0);
		if (timeout == 0)
			assertEquals(message, 0, deadline);
		else
			assertTrue(message, deadline >= start + timeout);
	}

	/**
	 * Ensures the coordination timed out according to the requested timeout
	 * allowing for a small deviation.
	 * @param start The start time of the coordination.
	 * @param timeout The total amount of timeout requested.
	 */
	protected void assertTimeoutDuration(long start, long timeout) {
		assertTrue("Timeout too long", System.currentTimeMillis() - start <= timeout + 500);
	}
	
	protected void setUp() throws Exception {
		coordinatorReference = getContext().getServiceReference(Coordinator.class);
		coordinator = getContext().getService(coordinatorReference);
		peeked = coordinator.peek();
	}
	
	protected void tearDown() throws Exception {
		// Clean up any coordinations that might be lingering on the thread 
		// local stack.
		while (coordinator.peek() != null && !coordinator.peek().equals(peeked))
			coordinator.pop();
		coordinator = null;
		getContext().ungetService(coordinatorReference);
		coordinatorReference = null;
	}
}
