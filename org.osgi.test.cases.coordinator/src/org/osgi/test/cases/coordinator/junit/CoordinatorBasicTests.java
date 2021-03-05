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

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.CoordinationException;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.service.coordinator.Participant;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Semaphore;
import org.osgi.test.support.sleep.Sleep;

import junit.framework.AssertionFailedError;

/**
 * Basic Coordinator test case.
 */
public class CoordinatorBasicTests extends CoordinatorTest {

	/**
	 * Timeout Fixed exception Extending timeout
	 * @throws Exception
	 */

	public void testOverallTimeout() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		clear(c);

		{
			Coordination cc = c.begin("timeout-1", 0);
			cc.extendTimeout(100);
			Sleep.sleep(500);
			assertFalse(
					"must not have timed out because it did not have a timeout to begin with",
					cc.isTerminated());
			assertEquals(null, cc.getFailure());
		}

		{
			Coordination cc = c.begin("timeout-2", 100);
			cc.join(0);
			assertTrue(cc.isTerminated());
		}
		Thread.interrupted(); // clear flag
		{
			int granularity = 1000;
			Coordination cc = c.begin("timeout-3", 2 * granularity);
			cc.extendTimeout(2 * granularity); // expires before primary
			Sleep.sleep(granularity);
			assertFalse("must not have timed out yet", cc.isTerminated());
			Sleep.sleep(2 * granularity); // expires after primary but before
											// extended
			assertFalse("must not have timed out yet because we extended", cc
					.isTerminated());
			try {
				cc.join(10 * granularity);
			}
			catch (InterruptedException ie) {
				// The spec no longer requires an implementation to
				// interrupt the thread a coordination is associated with
				// if other than the one that called the fail() method.
				// However, it doesn't prohibit it either, so ignore the
				// exception.
			}
			assertTrue("now it must be terminated", cc.isTerminated());
			assertEquals(Coordination.TIMEOUT, cc.getFailure());
		}

	}

	/**
	 * Variables Map must remain available Not shared
	 *
	 */

	class VariableParticipant extends TestParticipant {
		Object	value;

		public void ended(Coordination c) throws Exception {
			super.ended(c);
			value = c.getVariables().get(VariableParticipant.class);
		}

		public void failed(Coordination c) throws Exception {
			super.failed(c);
			value = c.getVariables().get(VariableParticipant.class);
		}
	}

	/**
	 * @throws Exception
	 */
	public void testVariables() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		clear(c);

		Coordination cc1 = c.create("variable-1", 0);
		Map<Class< ? >, Object> map1 = cc1.getVariables();
		map1.put(VariableParticipant.class, "hello");
		VariableParticipant vp = new VariableParticipant();
		cc1.addParticipant(vp);
		cc1.end();

		assertEquals(vp.value, "hello");
		assertEquals(1, vp.ended.get());
		assertEquals(0, vp.failed.get());
		assertEquals(cc1.getVariables().get(VariableParticipant.class), "hello");

		Coordination cc2 = c.create("variable-2", 0);
		Map<Class< ? >, Object> map2 = cc2.getVariables();
		map2.put(VariableParticipant.class, "goodbye");
		vp = new VariableParticipant();
		cc2.addParticipant(vp);
		cc2.fail(new Exception());

		assertEquals(vp.value, "goodbye");
		assertEquals(0, vp.ended.get());
		assertEquals(1, vp.failed.get());
		assertEquals(cc1.getVariables().get(VariableParticipant.class), "hello");
		assertEquals(cc2.getVariables().get(VariableParticipant.class),
				"goodbye");

		assertTrue(cc1.getVariables() != cc2.getVariables());
	}

	/**
	 * Stack A Coordination can be pushed at most one. Terminated Coordinations
	 * must be removed from the stack begin == create + push
	 * @throws Exception
	 */

	public void testStack() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		clear(c);

		Coordination cc = c.create("stack-1", 0);
		assertEquals(cc, cc.push());

		try {
			cc.push();
			fail("expected an error because we're already pushed");
		}
		catch (CoordinationException ce) {
			assertEquals("Must be already pushed error",
					CoordinationException.ALREADY_PUSHED, ce.getType());
		}

		assertEquals(cc, c.pop());
		assertEquals(cc, cc.push());

		cc.end(); // terminate it

		assertTrue(cc.isTerminated());
		assertNull("nothing on stack anymore", c.pop());

		Coordination c1 = c.begin("stack-1", 0);
		Coordination c2 = c.begin("stack-2", 0);
		Coordination c3 = c.begin("stack-3", 0);
		Coordination c4 = c.begin("stack-4", 0);

		c2.end();
		assertTrue(c3.isTerminated());
		assertTrue(c4.isTerminated());

		assertEquals(c1, c.pop());

		try {
			c2.push();
			fail("must throw an exception");
		}
		catch (CoordinationException e) {
			assertEquals(CoordinationException.ALREADY_ENDED, e.getType());
		}

		cc = c.begin("pushed", 0);
		assertEquals(cc, c.pop());
	}

	/**
	 * Termination.
	 *
	 * Participants must see terminated Coordinations in their callback
	 * Termination must be atomic
	 */

	class Checking extends TestParticipant {
		boolean	terminated;
		boolean	result;

		public void ended(Coordination c) throws Exception {
			super.ended(c);
			terminated = c.isTerminated();
			result = c.fail(new Exception());
		}

		public void failed(Coordination c) throws Exception {
			super.failed(c);
			terminated = c.isTerminated();
			result = c.fail(new Exception());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testTerminatedInCallbacks() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		clear(c);

		Coordination cc = c.create("termination-1", 0);
		Checking bp = new Checking();
		cc.addParticipant(bp);
		assertFalse("initialized to false ", bp.terminated);
		cc.end();
		assertTrue("must  be terminated in callback ", bp.terminated);
		assertFalse(
				"result of failure is expected to be false because it was already terminated ",
				bp.result);
		assertEquals("ended called ", 1, bp.ended.get());
		assertEquals("failed called ", 0, bp.failed.get());

		cc = c.create("termination-2", 0);
		bp = new Checking();
		cc.addParticipant(bp);
		assertFalse("initialized to false ", bp.terminated);
		cc.fail(new IllegalStateException());
		assertTrue("must  be terminated in callback ", bp.terminated);
		assertFalse(
				"result of failure is expected to be false because it was already terminated ",
				bp.result);
		assertEquals("ended called ", 0, bp.ended.get());
		assertEquals("failed called ", 1, bp.failed.get());

	}

	/**
	 * Life cycle
	 *
	 * Test if coordinations are terminated when the service is ungotten
	 *
	 * @throws IOException
	 * @throws BundleException
	 */

	public void testUngotten() throws BundleException, IOException {
		BundleContext context = getContext();

		// Ensure no outstanding references
		ServiceReference<Coordinator> ref = context.getServiceReference(Coordinator.class);
		while (context.ungetService(ref));

		// Get another Bundle Context
		Bundle other = install("dummy.jar");
		other.start();

		BundleContext otherContext = other.getBundleContext();
		// Ensure no outstanding references
		while (context.ungetService(ref));

		// Get the different Coordinators
		Coordinator cUs = context.getService(ref);
		Coordinator cThem = otherContext.getService(ref);
		assertTrue(
				"Because they're registered with a service factory they must differ",
				cUs != cThem);

		// Create a bunch of Coordinations
		Coordination ccUs1 = cUs.create("lifecyle-us-1", 0);
		Coordination ccUs2 = cUs.create("lifecyle-us-2", 0);

		Coordination ccThem1 = cThem.create("lifecyle-them-1", 0);
		Coordination ccThem2 = cThem.create("lifecyle-them-2", 0);

		// Check if we have the same view and all are visible
		assertEquals("Must have 4 coordinations now from us", 4, cUs
				.getCoordinations().size());
		assertEquals("Must have 4 coordinations now from them", 4, cThem
				.getCoordinations().size());

		// Unget their service
		while (otherContext.ungetService(ref));

		assertEquals("Must have 2 coordinations now after they are gone", 2,
				cUs.getCoordinations().size());

		// Check their coordinations are properly terminated
		assertTrue(ccThem1.isTerminated());
		// Coordinations that are failed due to a bundle releasing the
		// Coordinator service must have Coordination.RELEASED as the failure
		// cause.
		Exception e = (Exception)ccThem1.getFailure();
		assertTrue("The failure cause should have been Coordination.RELEASED", e == Coordination.RELEASED);

		assertTrue(ccThem2.isTerminated());

		// And that should not have affected us
		assertFalse(ccUs1.isTerminated());
		assertFalse(ccUs2.isTerminated());

		// Unget our service
		while (context.ungetService(ref));

		// now ours should also be gone
		assertTrue(ccUs1.isTerminated());
		assertTrue(ccUs2.isTerminated());
	}

	/**
	 * Timeout
	 * @throws Exception
	 */
	public void testTableTimeout() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		clear(c);

		Coordination cc = c.begin("table-timeout-active", 100);
		try {
			cc.join(200);
		}
		catch (InterruptedException e) {
			// The spec no longer requires an implementation to
			// interrupt the thread a coordination is associated with
			// if other than the one that called the fail() method.
			// However, it doesn't prohibit it either, so ignore the
			// exception.
		}
		assertTrue(cc.isTerminated());
	}

	/**
	 * Test the fail method according to the table
	 * @throws Exception
	 */

	public void testTableFail() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		clear(c);

		// ACTIVE, must return true no exception
		{
			Coordination cc = c.create("table-addParticipant-no-exception", 0);
			assertTrue(cc.fail(new Exception()));
		}

		// END, must return false no exception
		{
			Coordination cc = c.create("table-addParticipant-no-exception", 0);
			cc.end();
			assertFalse(cc.fail(new Exception()));
		}

		// FALSE, must return false no exception
		{
			Coordination cc = c.create("table-addParticipant-no-exception", 0);
			cc.fail(new Exception());
			assertFalse(cc.fail(new Exception()));
		}

	}

	/**
	 * Table showing exceptions for the addParticipant method.
	 * @throws Exception
	 */
	public void testTableAddParticipant() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		clear(c);

		// NONE in ACTIVE state
		Coordination cc = c.create("table-addParticipant-no-exception", 0);
		cc.addParticipant(new TestParticipant());
		cc.end(); // No exception

		// DEADLOCK same thread in ACTIVE state
		{
			Coordination cc1 = c.begin("table-addParticipant-deadlock-1", 0);
			Coordination cc2 = c.begin("table-addParticipant-deadlock-2", 0);

			try {
				TestParticipant tp = new TestParticipant();
				cc1.addParticipant(tp);
				cc2.addParticipant(tp);
			}
			catch (CoordinationException e) {
				assertEquals(CoordinationException.DEADLOCK_DETECTED, e
						.getType());
			}
		}
		// LOCK_INTERRUPTED
		{

			final TestParticipant tp = new TestParticipant();
			final Coordination cc2 = c.begin("table-addParticipant-deadlock-2",
					0);

			Thread t1 = new Thread() {
				public void run() {
					try {
						Coordination cc1 = c.begin(
								"table-addParticipant-deadlock-1", 0);
						cc1.addParticipant(tp);
						try {
							Sleep.sleep(200);
							System.out.println("Will interrupt thread "
									+ cc2.getThread());
							cc2.getThread().interrupt();
							Sleep.sleep(100);
							cc1.end();
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					catch (Throwable t) {
						t.printStackTrace();
					}
				}
			};
			t1.start();
			Sleep.sleep(100);

			try {
				cc2.addParticipant(tp); // will block
			}
			catch (CoordinationException e) {
				assertEquals(CoordinationException.LOCK_INTERRUPTED, e
						.getType());
			}

			t1.join();
		}

		// ALREADY_ENDED in END state
		cc = c.begin("table-addParticipant-already-ended", 0);
		cc.end();
		try {
			cc.addParticipant(new TestParticipant());
			fail("Must throw ALREADY_ENDED");
		}
		catch (CoordinationException e) {
			assertEquals(CoordinationException.ALREADY_ENDED, e.getType());
		}

		// FAILED in FAILED state
		cc = c.begin("table-addParticipant-failed", 0);
		Exception localFailure = new Exception();
		try {

			cc.fail(localFailure);
			cc.addParticipant(new TestParticipant());
		}
		catch (CoordinationException e) {
			assertEquals(CoordinationException.FAILED, e.getType());
			assertEquals(localFailure, e.getCause());
		}

	}

	/**
	 * Table showing exceptions for the end method.
	 * @throws Exception
	 */

	public void testTableEnd() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		clear(c);

		// end method

		Coordination cc = c.create("table-end-no-exception", 0);
		cc.end(); // No exception

		cc = c.create("table-end-partially-ended", 0);
		cc.addParticipant(new Participant() {

			public void ended(Coordination local) throws Exception {
				throw new Exception();
			}

			public void failed(Coordination local) throws Exception {
				// ignore
			}

		});
		try {
			cc.end();
		}
		catch (CoordinationException ce) {
			assertEquals(CoordinationException.PARTIALLY_ENDED, ce.getType());
		}

		cc = c.create("table-end-already-ended", 0);
		cc.end();
		try {
			cc.end();
		}
		catch (CoordinationException ce) {
			assertEquals(CoordinationException.ALREADY_ENDED, ce.getType());
		}

		cc = c.create("table-end-failed", 0);
		cc.fail(new Exception());
		try {
			cc.end();
		}
		catch (CoordinationException ce) {
			assertEquals(CoordinationException.FAILED, ce.getType());
		}
	}

	/**
	 * CO0021 – All coordinations can be passed as parameters in method call
	 * @throws Exception
	 */
	public void testPassAsArgument() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		clear(c);

		Vector<BigInteger> queue = new Vector<BigInteger>();
		Coordination cc = c.create("CO0021", 0);
		try {
			for (int i = 0; i < 10; i++)
				foo(cc, 100, queue);
		}
		finally {
			cc.end();
		}
		assertEquals(10 * 100, queue.size());
	}

	class ThreadParticipant extends Thread implements Participant {

		public void ended(Coordination c) throws Exception {
			join();
		}

		public void failed(Coordination c) throws Exception {
			interrupt();
		}
	}

	void foo(Coordination c, final int count, final Vector<BigInteger> queue) {
		final Random r = new Random();
		ThreadParticipant t = new ThreadParticipant() {
			public void run() {
				for (int i = 0; i < count; i++) {
					BigInteger big = new BigInteger(2048, r);
					queue.add(big);
				}
			}
		};
		t.start();
		c.addParticipant(t);
	}

	/**
	 * CO0017 – Allow parties that make the coordination fail to provide
	 * information why it failed, the rationale for this requirement is that it
	 * is for trouble shooting
	 * @throws Exception
	 */
	public void testFailInfo() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		clear(c);

		Coordination c1 = c.begin("CO0017", 0);
		try {
			throw new IllegalArgumentException();
		}
		catch (Exception e) {
			c1.fail(e);
		}
		finally {
			try {
				c1.end(); // throws an exception
			}
			catch (CoordinationException e) {
				assertEquals(CoordinationException.FAILED, e.getType());
				assertTrue(e.getCause().getClass() == IllegalArgumentException.class);
			}
		}
	}

	/**
	 * CO0015 – Provide an identifying mechanism for coordinations
	 *
	 * CO0018 – A Coordination must have a unique identification for management
	 * purposes
	 * @throws Exception
	 */
	public void testId() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		clear(c);

		Coordination c1 = c.begin("CO0015", 0);
		Coordination c2 = c.begin("CO0015", 0);
		Coordination c3 = c.begin("CO0015-1", 0);
		Coordination c4 = c.begin("CO0015-2", 0);

		// Check for duplicate names
		assertEquals("CO0015", c1.getName());
		assertEquals("CO0015", c2.getName());

		// Some extra
		assertEquals("CO0015-1", c3.getName());
		assertEquals("CO0015-2", c4.getName());

		// Monotonically increasing
		assertTrue(c1.getId() >= 0);
		assertTrue(c1.getId() < c2.getId());
		assertTrue(c2.getId() < c3.getId());
		assertTrue(c3.getId() < c4.getId());

		// Findable by ID
		assertEquals(c1, c.getCoordination(c1.getId()));
		assertEquals(c2, c.getCoordination(c2.getId()));
		assertEquals(c3, c.getCoordination(c3.getId()));
		assertEquals(c4, c.getCoordination(c4.getId()));

		// Remove c4, then try to find it
		// and test calling of name/id for a terminated Coordination
		c4.end();
		assertNull(c.getCoordination(c4.getId()));
		assertEquals("CO0015-2", c4.getName());
	}

	/**
	 * CO0014 – It must be possible to fail a coordination from outside of the
	 * initiating/active thread.
	 * @throws Exception
	 */

	public void testFailCoordinationFromNoninitiatingThread() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		clear(c);

		final Semaphore s = new Semaphore(0);
		final AtomicReference<Coordination> coord = new AtomicReference<Coordination>();

		Thread t1 = new Thread() {
			public void run() {
				Coordination c1 = c.begin("co0012-1", 0);
				coord.set(c1);
				s.release(1);
				try {
					c1.join(0);
				}
				catch (InterruptedException e) {
					// The spec no longer requires an implementation to
					// interrupt the thread a coordination is associated with
					// if other than the one that called the fail() method.
					// However, it doesn't prohibit it either, so ignore the
					// exception.
				}
				finally {
					s.release(1);
				}
			}
		};
		t1.start();
		s.acquire(1);
		Exception e = new Exception();
		coord.get().fail(e);
		s.acquire(1);

		assertTrue(coord.get().isTerminated());
		assertTrue(coord.get().getFailure() == e);
	}

	void clear(Coordinator c) {
		Thread.interrupted();

		for (Coordination cc : c.getCoordinations())
			cc.fail(new Exception());
		assertEquals(0, c.getCoordinations().size());
	}

	/**
	 * CO0013 – It must be possible to enumerate the active coordinations
	 * @throws Exception
	 */

	public void testEnumerateCoordinations() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		clear(c);

		Coordination c1 = c.begin("co0012-1", 0);
		Coordination c2 = c.begin("co0012-2", 0);
		Coordination c3 = c.create("co0012-3", 0);
		Coordination c4 = c.create("co0012-4", 0);

		assertEquals(c2, c.peek());

		List<Coordination> cl = new ArrayList<Coordination>(Arrays.asList(c1,
				c2, c3, c4));
		assertEquals(4, c.getCoordinations().size());
		cl.removeAll(c.getCoordinations());
		assertTrue(cl.isEmpty());

		Collection< ? extends Coordination> initial = c.getCoordinations();

		c3.end();
		assertEquals(4, initial.size());
		assertEquals(3, c.getCoordinations().size());

		cl = new ArrayList<Coordination>(Arrays.asList(c1, c2, c4));
		cl.removeAll(c.getCoordinations());
		assertTrue(cl.isEmpty());

		c2.end();
		c1.end();
		c4.end();
		assertEquals(0, c.getCoordinations().size());
		assertEquals(4, initial.size());

		for (Coordination x : initial) {
			assertTrue(x.isTerminated());
		}

		try {
			initial.add(null);
			assertEquals("Must be mutable", 5, initial.size());
		}
		catch (Exception e) {
			fail("Must be mutable");
		}
		try {
			initial.remove(null);
			assertEquals("Must be mutable", 4, initial.size());

		}
		catch (Exception e) {
			fail("Must be mutable");
		}
	}

	/**
	 * CO0012 – The initiator must be informed if one of the participants throws
	 * an exception in the termination method for an ok outcome.
	 * @throws Exception
	 */

	public void testPartiallyEnded() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp1 = new TestParticipant();
		TestParticipant tp2 = new TestParticipant() {
			public void ended(Coordination x) throws Exception {
				super.ended(x);
				throw new RuntimeException();
			}
		};

		Coordination c1 = c.begin("co0012", 0);
		assertEquals(c1, c.peek());
		try {
			c1.addParticipant(tp1);
			c1.addParticipant(tp2);
		}
		finally {
			try {
				c1.end();
				fail("Expected a PARTIALLY ENDED exception!");
			}
			catch (CoordinationException e) {
				assertEquals(CoordinationException.PARTIALLY_ENDED, e
						.getType());
				assertEquals(1, tp1.ended.get());
				assertEquals(0, tp1.failed.get());
				assertEquals(1, tp2.ended.get());
				assertEquals(0, tp2.failed.get());
			}
			assertTrue(c1.isTerminated());
		}
	}

	/**
	 * CO0011 – Coordinations must be allowed to nest where the nested
	 * coordination is independent of the outer coordinations.
	 *
	 * This is the OK case
	 * @throws Exception
	 */
	public void testNestingOk() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp1 = new TestParticipant();
		TestParticipant tp2 = new TestParticipant();
		Coordination c1 = c.begin("co0011-1", 0);
		try {
			c1.addParticipant(tp1);
			assertEquals(c1, c.peek());

			Coordination c2 = c.begin("co0011-2", 0);
			assertEquals(c2, c.peek());
			assertFalse(c1.isTerminated());
			assertFalse(c2.isTerminated());
			assertEquals(0, tp1.ended.get());
			assertEquals(0, tp1.failed.get());

			assertEquals(c2, c.pop());
			assertEquals(c1, c.peek());
			c2.push();
			assertEquals(c2, c.peek());

			try {
				c2.addParticipant(tp2);
				assertFalse(c1.isTerminated());
				assertFalse(c2.isTerminated());
				assertEquals(0, tp1.ended.get());
				assertEquals(0, tp1.failed.get());
				assertEquals(0, tp2.ended.get());
				assertEquals(0, tp2.failed.get());
			}
			finally {
				c2.end();
				assertFalse(c1.isTerminated());
				assertTrue(c2.isTerminated());
				assertEquals(0, tp1.ended.get());
				assertEquals(0, tp1.failed.get());
				assertEquals(1, tp2.ended.get());
				assertEquals(0, tp2.failed.get());
			}

		}
		finally {
			c1.end();
			assertTrue(c1.isTerminated());
			assertEquals(1, tp1.ended.get());
			assertEquals(0, tp1.failed.get());
			assertEquals(1, tp2.ended.get());
			assertEquals(0, tp2.failed.get());
		}
	}

	/**
	 * CO0011 – Coordinations must be allowed to nest where the nested
	 * coordination is independent of the outer coordinations.
	 *
	 * This is the FAILURE case
	 * @throws Exception
	 */
	public void testNestingInnerFails() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp1 = new TestParticipant();
		TestParticipant tp2 = new TestParticipant();
		Coordination c1 = c.begin("co0011-1", 0);
		try {
			c1.addParticipant(tp1);
			assertEquals(c1, c.peek());

			Coordination c2 = c.begin("co0011-2", 0);
			assertEquals(c2, c.peek());
			assertFalse(c1.isTerminated());
			assertFalse(c2.isTerminated());
			assertEquals(0, tp1.ended.get());
			assertEquals(0, tp1.failed.get());

			assertEquals(c2, c.pop());
			assertEquals(c1, c.peek());
			c2.push();
			assertEquals(c2, c.peek());

			try {
				c2.addParticipant(tp2);
				assertFalse(c1.isTerminated());
				assertFalse(c2.isTerminated());
				assertEquals(0, tp1.ended.get());
				assertEquals(0, tp1.failed.get());
				assertEquals(0, tp2.ended.get());
				assertEquals(0, tp2.failed.get());
				c2.fail(new Exception());
			}
			finally {
				try {
					c2.end();
					fail("expected c2 to throw an exception!");
				}
				catch (CoordinationException e) {
					assertEquals(CoordinationException.FAILED, e.getType());
					assertFalse(c1.isTerminated());
					assertTrue(c2.isTerminated());
					assertEquals(0, tp1.ended.get());
					assertEquals(0, tp1.failed.get());
					assertEquals(0, tp2.ended.get());
					assertEquals(1, tp2.failed.get());
				}
			}
		}
		finally {
			c1.end();
			assertTrue(c1.isTerminated());
			assertEquals(1, tp1.ended.get());
			assertEquals(0, tp1.failed.get());
			assertEquals(0, tp2.ended.get());
			assertEquals(1, tp2.failed.get());
		}
	}

	/**
	 * CO0010 – Participants must be able to prevent blocking
	 *
	 * We do this by creating a proxy.
	 */

	class ProxyParticipant implements Participant {
		Participant	proxy;

		public ProxyParticipant(Participant proxy) {
			this.proxy = proxy;
		}

		public void ended(Coordination c) throws Exception {
			proxy.ended(c);
		}

		public void failed(Coordination c) throws Exception {
			proxy.failed(c);
		}
	}

	/**
	 * @throws Exception
	 */
	public void testPreventBlocking() throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);

		final TestParticipant tp1 = new TestParticipant();

		Coordination c1 = c.begin("co0010", 0);
		c1.addParticipant(tp1);

		// Check if we block correctly
		final AtomicReference<Throwable> throwable = new AtomicReference<Throwable>();
		final Coordination c3 = c.create("co0009-3", 0);
		Thread t1 = new Thread() {
			public void run() {
				try {
					c3.addParticipant(new ProxyParticipant(tp1));
				}
				catch (Throwable e) {
					throwable.set(e);
				}
			}
		};
		t1.start();
		t1.join(); // wait for t1 to finish, means not blocked
		assertNull(throwable.get());

		c1.end(); // calls stuff on tp1,
		assertEquals("after c1 terminated", 1, tp1.ended.get());
		assertEquals("after c1 terminated", 0, tp1.failed.get());

		c3.end(); // terminates, calling tp1 again on ended()
		assertEquals("after c3 terminated", 2, tp1.ended.get());
		assertEquals("after c3 terminated", 0, tp1.failed.get());
	}

	/**
	 *
	 * CO0009 – A participant must automatically be blocked when it attempts to
	 * participate on multiple coordinations in different threads. Participating
	 * in two coordinations on the same thread with the same participant is an
	 * error.
	 * @throws Exception
	 */

	public void testNoParticipationOnTwoCoordinationsInTheSameThread()
			throws Exception {
		final Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);

		final TestParticipant tp1 = new TestParticipant();

		Coordination c1 = c.begin("co0009-1", 0);
		Coordination c2 = c.begin("co0009-2", 0);

		c1.addParticipant(tp1);

		// Try to add on the same thread.
		try {
			c2.addParticipant(tp1);
			fail("Excepted deadlock excepton");
		}
		catch (CoordinationException e) {
			assertEquals(CoordinationException.DEADLOCK_DETECTED, e.getType());
			assertTrue(c2.getParticipants().isEmpty());
			c2.end();
			assertEquals(c1, c.peek());
		}

		// Check if a timeout cancels the block
		final AtomicReference<CoordinationException> exception = new AtomicReference<CoordinationException>();
		Thread t1 = new Thread() {
			public void run() {
				Coordination c3 = c.begin("co0009-3", 1000);
				try {
					c3.addParticipant(tp1);
				}
				catch (CoordinationException e) {
					exception.set(e);
				}
			}
		};
		t1.start();
		t1.join();
		assertNotNull(exception.get());
		assertEquals(CoordinationException.FAILED, exception.get().getType());
		assertEquals(Coordination.TIMEOUT, exception.get().getCause());

		// Check if we block correctly
		final AtomicReference<Throwable> throwable = new AtomicReference<Throwable>();
		final Coordination c3 = c.create("co0009-3", 0);
		t1 = new Thread() {
			public void run() {
				try {
					c3.addParticipant(tp1);
				}
				catch (Throwable e) {
					throwable.set(e);
				}
			}
		};
		t1.start();
		Sleep.sleep(1000);
		assertTrue(t1.isAlive()); // is blocked on addParticipant
		c1.end(); // calls stuff on tp1, unblocks t1
		t1.join(); // wait for t1 to finish, can only happen when unblocks

		// c3 is still not terminated, so we have only one callback yet
		assertEquals("after c1 terminated", 1, tp1.ended.get());
		assertEquals("after c1 terminated", 0, tp1.failed.get());
		assertNull(throwable.get());

		c3.end(); // terminates, calling tp1 again on ended()
		assertEquals("after c3 terminated", 2, tp1.ended.get());
		assertEquals("after c3 terminated", 0, tp1.failed.get());
	}

	/**
	 * CO0008 – It must be possible to enumerate the participants
	 * @throws Exception
	 */
	public void testEnumerateParticipants() throws Exception {
		Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);

		TestParticipant tp1 = new TestParticipant();
		TestParticipant tp2 = new TestParticipant();
		TestParticipant tp3 = new TestParticipant();

		List< ? extends Participant> participants = Arrays
				.asList(tp1, tp2, tp3);
		Coordination cc = c.begin("co0008", 0);
		try {
			for (Participant p : participants) {
				cc.addParticipant(p);
			}
			cc.addParticipant(tp3); // these should not add the fray
			cc.addParticipant(tp2);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("No exception expected here " + e);
		}
		finally {
			assertTrue("must not be terminated", !cc.isTerminated());
			assertNull("no failure", cc.getFailure());
			List< ? extends Participant> other = cc.getParticipants();
			assertEquals(participants, other);
			cc.end();
			assertNull("no failure", cc.getFailure());
			assertTrue(cc.isTerminated());
			assertEquals(participants, other);

			// Check if we can no longer add to the list
			try {
				cc.addParticipant(new TestParticipant());
				fail("Expected exception becasue we should no longer be able to add");
			}
			catch (CoordinationException e) {
				assertEquals("Should not accept any more coordinatins",
						CoordinationException.ALREADY_ENDED, e.getType());
				assertEquals("Should not have sneaked in there", participants,
						other);
			}
		}
	}

	/**
	 */

	/**
	 * CO0003 – Any party participating in the task must be able to make the
	 * coordination always fail when ended.
	 *
	 * CO0005 – Participants must know that the coordination succeeded or failed
	 * at termination
	 */

	Exception	failure	= new Exception("failed");

	class FailingParticipant extends TestParticipant {
		private final Coordinator	c;

		FailingParticipant() throws Exception {
			c = coordinator;
		}

		public void doWork() {
			c.addParticipant(this);
			c.fail(failure);
		}

		public void ended(Coordination cc) throws Exception {
			throw new AssertionFailedError("I did fail this coordination!!");
		}

	}

	/**
	 * @throws Exception
	 */
	public void testParticipantFailure() throws Exception {
		Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		FailingParticipant tp1 = new FailingParticipant();

		Coordination cc = c.begin("co0003", 0);
		try {
			tp1.doWork();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("No exception expected here " + e);
		}
		finally {
			try {
				assertTrue("must be terminated", cc.isTerminated());
				assertEquals("failure", failure, cc.getFailure());
				assertEquals("Ended must be 0", 0, tp1.ended.get());
				assertEquals("Failed must be 1", 1, tp1.failed.get());
				cc.end();
				fail("End should throw a ConfigurationException FAILED");
			}
			catch (CoordinationException e) {
				assertEquals("Must be FAILED", e.getType(),
						CoordinationException.FAILED);
				assertEquals("And our original failure", failure, e.getCause());
			}
		}
	}

	/**
	 * CO0002 – An initiator must be able to initiate a coordination and control
	 * the final outcome
	 *
	 * CO0004 – Participants in the task must be informed when the coordination
	 * is terminated.
	 *
	 * CO0005 – Participants must know that the coordination succeeded or failed
	 * at termination
	 * @throws Exception
	 */
	public void testControlOutcome() throws Exception {
		Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp1 = new TestParticipant();

		Exception failure1 = new Exception("failed");
		Coordination cc = c.create("co0002", 0);
		try {
			cc.addParticipant(tp1);
			cc.fail(failure1);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("No exception expected here " + e);
		}
		finally {
			try {
				assertTrue("must be terminated", cc.isTerminated());
				assertEquals("failure", failure1, cc.getFailure());
				assertEquals("Ended must be 0", 0, tp1.ended.get());
				assertEquals("Failed must be 1", 1, tp1.failed.get());
				cc.end();
				fail("End should throw a ConfigurationException FAILED");
			}
			catch (CoordinationException e) {
				assertEquals(e.getName(), "co0002");
				assertEquals(cc.getId(), e.getId());
				assertEquals("Must be FAILED", e.getType(),
						CoordinationException.FAILED);
				assertEquals("And our original failure", failure1, e.getCause());
			}
		}

	}

	/**
	 * CO0001 – Provide a solution to allow multiple parties that collaborate to
	 * coordinate the outcome of a task initiated by an initiator.
	 * @throws Exception
	 */

	public void testCollaboration() throws Exception {
		Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp1 = new TestParticipant();
		TestParticipant tp2 = new TestParticipant();
		TestParticipant tp3 = new TestParticipant();

		Coordination cc = c.create("co0001", 0);
		try {
			cc.addParticipant(tp1);
			cc.addParticipant(tp2);
			cc.addParticipant(tp3);
		}
		catch (Exception e) {
			fail("No exception expected here " + e);
		}
		finally {
			cc.end();
			assertTrue("must be terminated", cc.isTerminated());
			assertNull("no failure", cc.getFailure());
			assertEquals("Ended must be 1", 1, tp1.ended.get());
			assertEquals("Failed must be 0", 0, tp1.failed.get());
			assertEquals("Ended must be 1", 1, tp2.ended.get());
			assertEquals("Failed must be 0", 0, tp2.failed.get());
			assertEquals("Ended must be 1", 1, tp3.ended.get());
			assertEquals("Failed must be 0", 0, tp3.failed.get());
		}

	}

	/**
	 *
	 * CO0006 – A coordination must have a timeout
	 *
	 * CO0007 – The timeout timer must be settable by the initiator
	 *
	 * If a Coordination is not properly terminated (either end() or
	 * fail(Throwable) is not called) it will time out. A timeout will have the
	 * effect of fail(Coordination.TIMEOUT), it will therefore follow the same
	 * process as failing. The Coordination.TIMEOUT field is a singleton
	 * Exception for this purpose. Initiators can find if a timeout occurs by
	 * reacting to the failure and then inspecting the exception from
	 * getFailure().
	 *
	 *
	 * @throws Exception
	 */
	public void testTimeout() throws Exception {
		Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp = new TestParticipant();
		Coordination cc = c.create("test1", 100); // timeout after 100 ms

		try {
			cc.addParticipant(tp);
			Sleep.sleep(200);
		}
		catch (Exception e) {
			fail();
		}
		finally {
			try {
				assertTrue("must be terminated", cc.isTerminated());
				assertEquals("must have a timeout failure",
						Coordination.TIMEOUT, cc.getFailure());
				assertEquals("Ended must be 0", 0, tp.ended.get());
				assertEquals("Failed must be 1", 1, tp.failed.get());
				cc.end();
			}
			catch (CoordinationException ce) {
				assertEquals("except a failed ", CoordinationException.FAILED,
						ce.getType());
				assertEquals("except a timeout exception ",
						Coordination.TIMEOUT, ce.getCause());
			}
		}

	}

	/**
	 * Test the basic normal setup.
	 *
	 * @throws Exception
	 */
	public void testBasic() throws Exception {
		Coordinator c = coordinator;
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp = new TestParticipant();
		Coordination cc = c.create("test1", 0);

		try {
			cc.addParticipant(tp);
		}
		catch (Exception e) {
			fail();
		}
		finally {
			cc.end();
		}

		assertTrue("must be terminated", cc.isTerminated());
		assertNull("must have no failure", cc.getFailure());
		assertEquals("Ended must be 1", 1, tp.ended.get());
		assertEquals("Failed must be 0", 0, tp.failed.get());
	}

	/**
	 * (1) Return 0 when no extension has taken place.
	 * (2) Return the current deadline when 0 is passed as an argument.
	 * (3) Return the new deadline when an extension has taken place. The 
	 *     extension must be at least as much as was requested.
	 * (4) A non-zero return value represents the system time in milliseconds 
	 *     when the coordination will time out.
	 * @throws Exception 
	 */
	public void testExtendTimeout() throws Exception {
		Coordination c = coordinator.create("c", 0);
		try {
			// (2)
			assertDeadline(0, 0, c);
			// (1)
			assertEquals("No extension should have occurred", 0, c.extendTimeout(5000));
		}
		finally {
			assertEnd(c);
		}
		long start = System.currentTimeMillis();
		c = coordinator.create("c", 3000);
		try {
			// (2), (4)
			assertDeadline(start, 3000, c);
			// (3)
			assertTrue("Wrong deadline", c.extendTimeout(2000) >= 5000);
			// (2), (4)
			assertDeadline(start, 5000, c);
			c.join(8000);
			assertTerminated(c);
			assertTimeoutDuration(start, 5000);
		}
		finally {
			assertEndFailed(c, CoordinationException.FAILED, Coordination.TIMEOUT);
		}
	}
}

class TestParticipant implements Participant {
	final AtomicInteger	ended	= new AtomicInteger(0);
	final AtomicInteger	failed	= new AtomicInteger(0);

	public void failed(Coordination c) throws Exception {
		failed.incrementAndGet();
	}

	public void ended(Coordination c) throws Exception {
		ended.incrementAndGet();
	}

	/**
	 * Create a brain damaged equals to check identity
	 */
	public boolean equals(Object other) {
		return other instanceof TestParticipant;
	}

	/**
	 * Create a brain damaged equals to check identity
	 */
	public int hashCode() {
		return 1;
	}
}
