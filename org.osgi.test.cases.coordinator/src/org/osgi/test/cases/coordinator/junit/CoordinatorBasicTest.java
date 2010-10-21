package org.osgi.test.cases.coordinator.junit;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import junit.framework.*;

import org.osgi.framework.*;
import org.osgi.service.coordinator.*;
import org.osgi.util.tracker.*;

public class CoordinatorBasicTest extends TestCase {

	
	/**
	 * Timeout
	 */
	public void testTableTimeout() throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
		assertNotNull("Expect the service to be there", c);
		clear(c);
		
		Coordination cc = c.begin("table-timeout-active", 100);
		try {
			Thread.sleep(200);
			fail("Should have been interrupted");
		}
		catch (InterruptedException e) {
			// good!
		}
		assertTrue( cc.isTerminated());
	}	
	
	
	
	/**
	 * Test the fail method according to the table
	 */

	public void testTableFail() throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
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
	 */
	public void testTableAddParticipant() throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
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
						.getReason());
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
							Thread.sleep(200);
							System.out.println("Will interrupt thread "
									+ cc2.getThread());
							cc2.getThread().interrupt();
							Thread.sleep(100);
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
			Thread.sleep(100);

			try {
				cc2.addParticipant(tp); // will block
			}
			catch (CoordinationException e) {
				assertEquals(CoordinationException.LOCK_INTERRUPTED, e
						.getReason());
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
			assertEquals(CoordinationException.ALREADY_ENDED, e.getReason());
		}

		// FAILED in FAILED state
		cc = c.begin("table-addParticipant-failed", 0);
		Exception failure = new Exception();
		try {

			cc.fail(failure);
			cc.addParticipant(new TestParticipant());
		}
		catch (CoordinationException e) {
			assertEquals(CoordinationException.FAILED, e.getReason());
			assertEquals(failure, e.getFailure());
		}

	}

	/**
	 * Table showing exceptions for the end method.
	 */

	public void testTableEnd() throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
		assertNotNull("Expect the service to be there", c);
		clear(c);

		// end method

		Coordination cc = c.create("table-end-no-exception", 0);
		cc.end(); // No exception

		cc = c.create("table-end-partially-ended", 0);
		cc.addParticipant(new Participant() {

			public void ended(Coordination c) throws Exception {
				throw new Exception();
			}

			public void failed(Coordination c) throws Exception {

			}

		});
		try {
			cc.end();
		}
		catch (CoordinationException ce) {
			assertEquals(CoordinationException.PARTIALLY_ENDED, ce.getReason());
		}

		cc = c.create("table-end-already-ended", 0);
		cc.end();
		try {
			cc.end();
		}
		catch (CoordinationException ce) {
			assertEquals(CoordinationException.ALREADY_ENDED, ce.getReason());
		}

		cc = c.create("table-end-failed", 0);
		cc.fail(new Exception());
		try {
			cc.end();
		}
		catch (CoordinationException ce) {
			assertEquals(CoordinationException.FAILED, ce.getReason());
		}
	}

	/**
	 * CO0021 – All coordinations can be passed as parameters in method call
	 */
	public void testPassAsArgument() throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
		assertNotNull("Expect the service to be there", c);
		clear(c);

		Queue<UUID> queue = new LinkedBlockingQueue<UUID>();
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

	void foo(Coordination c, final int count, final Queue<UUID> queue) {
		ThreadParticipant t = new ThreadParticipant() {
			public void run() {
				for (int i = 0; i < count; i++) {
					queue.add(UUID.randomUUID());
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
	 */
	public void testFailInfo() throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
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
				assertEquals(CoordinationException.FAILED, e.getReason());
				assertTrue(e.getFailure().getClass() == IllegalArgumentException.class);
			}
		}
	}

	/**
	 * CO0015 – Provide an identifying mechanism for coordinations
	 * 
	 * CO0018 – A Coordination must have a unique identification for management
	 * purposes
	 */
	public void testId() throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
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
	 * initiating/active thread and ensure that the associated thread (and
	 * thereby the coordinated task) is interrupted.
	 */

	public void testInterrupt() throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
		assertNotNull("Expect the service to be there", c);
		clear(c);

		final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
		final Semaphore s = new Semaphore(0);
		final AtomicReference<Coordination> coord = new AtomicReference<Coordination>();

		Thread t1 = new Thread() {
			public void run() {
				Coordination c1 = c.begin("co0012-1", 0);
				coord.set(c1);
				s.release(1);
				try {
					while (true) {
						Thread.sleep(100);
						System.out.print(".");
					}
				}
				catch (InterruptedException e) {
					exception.set(e);
				}
			}
		};
		t1.start();
		s.acquire(1);
		coord.get().fail(new Exception());
		t1.join();

		assertFalse(t1.isAlive());
		assertTrue(exception.get().getClass() == InterruptedException.class);
	}

	void clear(Coordinator c) {
		Thread.interrupted();

		for (Coordination cc : c.getCoordinations())
			cc.fail(new Exception());
		assertEquals(0, c.getCoordinations().size());
	}

	/**
	 * CO0013 – It must be possible to enumerate the active coordinations
	 */

	public void testEnumerateCoordinations() throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
		assertNotNull("Expect the service to be there", c);
		clear(c);

		Coordination c1 = c.begin("co0012-1", 0);
		Coordination c2 = c.begin("co0012-2", 0);
		Coordination c3 = c.create("co0012-3", 0);
		Coordination c4 = c.create("co0012-4", 0);

		assertEquals(c2, c.getCurrentCoordination());

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

		c1.end();
		c2.end();
		c4.end();
		assertEquals(0, c.getCoordinations().size());
		assertEquals(4, initial.size());

		for (Coordination x : initial) {
			assertTrue(x.isTerminated());
		}

		try {
			initial.add(null);
			fail("Must be immutable");
		}
		catch (Exception e) {
			// pass
		}
		try {
			initial.remove(0);
			fail("Must be immutable");
		}
		catch (Exception e) {
			// pass
		}
	}

	/**
	 * CO0012 – The initiator must be informed if one of the participants throws
	 * an exception in the termination method for an ok outcome.
	 */

	public void testPartiallyEnded() throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp1 = new TestParticipant();
		TestParticipant tp2 = new TestParticipant() {
			public void ended(Coordination c) throws Exception {
				super.ended(c);
				throw new RuntimeException();
			}
		};

		Coordination c1 = c.begin("co0012", 0);
		assertEquals(c1, c.getCurrentCoordination());
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
						.getReason());
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
	 */
	public void testNestingOk() throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp1 = new TestParticipant();
		TestParticipant tp2 = new TestParticipant();
		Coordination c1 = c.begin("co0011-1", 0);
		try {
			c1.addParticipant(tp1);
			assertEquals(c1, c.getCurrentCoordination());

			Coordination c2 = c.begin("co0011-2", 0);
			assertEquals(c2, c.getCurrentCoordination());
			assertFalse(c1.isTerminated());
			assertFalse(c2.isTerminated());
			assertEquals(0, tp1.ended.get());
			assertEquals(0, tp1.failed.get());

			assertEquals(c2, c.pop());
			assertEquals(c1, c.getCurrentCoordination());
			c.push(c2);
			assertEquals(c2, c.getCurrentCoordination());

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
	 */
	public void testNestingInnerFails() throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp1 = new TestParticipant();
		TestParticipant tp2 = new TestParticipant();
		Coordination c1 = c.begin("co0011-1", 0);
		try {
			c1.addParticipant(tp1);
			assertEquals(c1, c.getCurrentCoordination());

			Coordination c2 = c.begin("co0011-2", 0);
			assertEquals(c2, c.getCurrentCoordination());
			assertFalse(c1.isTerminated());
			assertFalse(c2.isTerminated());
			assertEquals(0, tp1.ended.get());
			assertEquals(0, tp1.failed.get());

			assertEquals(c2, c.pop());
			assertEquals(c1, c.getCurrentCoordination());
			c.push(c2);
			assertEquals(c2, c.getCurrentCoordination());

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
					assertEquals(CoordinationException.FAILED, e.getReason());
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

	public void testPreventBlocking() throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
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
	 */

	public void testNoParticipationOnTwoCoordinationsInTheSameThread()
			throws Exception {
		final Coordinator c = (Coordinator) getService(Coordinator.class);
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
			assertEquals(CoordinationException.DEADLOCK_DETECTED, e.getReason());
			assertTrue(c2.getParticipants().isEmpty());
			c2.end();
			assertEquals(c1, c.getCurrentCoordination());
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
		assertEquals(CoordinationException.FAILED, exception.get()
				.getReason());
		assertEquals(Coordination.TIMEOUT, exception.get().getFailure());

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
		Thread.sleep(1000);
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
	 */
	public void testEnumerateParticipants() throws Exception {
		Coordinator c = (Coordinator) getService(Coordinator.class);
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
						CoordinationException.ALREADY_ENDED, e.getReason());
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
		Coordinator	coordinator;

		FailingParticipant() throws Exception {
			coordinator = (Coordinator) getService(Coordinator.class);
		}

		public void doWork() {
			coordinator.addParticipant(this);
			coordinator.failed(failure);
		}

		public void ended(Coordination c) throws Exception {
			throw new AssertionFailedError("I did fail this coordination!!");
		}

	}

	public void testParticipantFailure() throws Exception {
		Coordinator c = (Coordinator) getService(Coordinator.class);
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
				assertEquals("Must be FAILED", e.getReason(),
						CoordinationException.FAILED);
				assertEquals("And our original failure", failure, e
						.getFailure());
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
	 */
	public void testControlOutcome() throws Exception {
		Coordinator c = (Coordinator) getService(Coordinator.class);
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp1 = new TestParticipant();

		Exception failure = new Exception("failed");
		Coordination cc = c.create("co0002", 0);
		try {
			cc.addParticipant(tp1);
			cc.fail(failure);
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
				assertEquals(e.getName(), "co0002");
				assertEquals(cc.getId(), e.getId());
				assertEquals("Must be FAILED", e.getReason(),
						CoordinationException.FAILED);
				assertEquals("And our original failure", failure, e
						.getFailure());
			}
		}

	}

	/**
	 * CO0001 – Provide a solution to allow multiple parties that collaborate to
	 * coordinate the outcome of a task initiated by an initiator.
	 */

	public void testCollaboration() throws Exception {
		Coordinator c = (Coordinator) getService(Coordinator.class);
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
		Coordinator c = (Coordinator) getService(Coordinator.class);
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp = new TestParticipant();
		Coordination cc = c.create("test1", 100); // timeout after 100 ms

		try {
			cc.addParticipant(tp);
			Thread.sleep(200);
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
						ce.getReason());
				assertEquals("except a timeout exception ",
						Coordination.TIMEOUT, ce.getFailure());
			}
		}

	}

	/**
	 * Test the basic normal setup.
	 * 
	 * @throws Exception
	 */
	public void testBasic() throws Exception {
		Coordinator c = (Coordinator) getService(Coordinator.class);
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

	private Object getService(Class< ? > c) throws InterruptedException {
		BundleContext context = FrameworkUtil.getBundle(
				CoordinatorBasicTest.class).getBundleContext();
		ServiceTracker t = new ServiceTracker(context, c.getName(), null) {
			public Object addingService(ServiceReference ref) {
				Object o = super.addingService(ref);
				return o;
			}
		};
		t.open();
		return t.waitForService(1000);
	}
}

class TestParticipant implements Participant {
	AtomicInteger	ended	= new AtomicInteger(0);
	AtomicInteger	failed	= new AtomicInteger(0);

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
