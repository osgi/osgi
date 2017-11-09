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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.CoordinationException;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.service.coordinator.Participant;
import java.util.concurrent.atomic.AtomicReference;
import org.osgi.test.support.sleep.Sleep;

/**
 * Implementations must detect orphaned coordinations (i.e. coordinations that
 * have been dereferenced and are eligible for garbage collection) and fail
 * them with an ORPHANED failure reason.
 */
public class OrphanedCoordinationTest extends CoordinatorTest {
	private Bundle coordinatorBundle;

	/**
	 * Test explicit coordination.
	 *
	 * @throws InterruptedException
	 * @throws BundleException
	 */
	public void testOrphanedCoordinationExplicit() throws InterruptedException, BundleException {
		Coordination c = coordinator.create("c", 0);
		long id = c.getId();
		String name = c.getName();
		Bundle bundle = c.getBundle();
		OrphanedParticipant p = new OrphanedParticipant();
		c.addParticipant(p);
		Reference<Coordination> reference = new WeakReference<Coordination>(c, new ReferenceQueue<Coordination>());
		c = null;
		assertReferenceEnqueued(reference);
		coordinatorBundle.stop();
		assertOrphanedCoordination(p, id, name, bundle);
	}

	/**
	 * Test implicit coordination.
	 *
	 * @throws InterruptedException
	 * @throws BundleException
	 */
	public void testOrphanedCoordinationImplicit() throws InterruptedException, BundleException {
		assertEmptyStack();
		Coordination c = coordinator.begin("c", 0);
		assertAtTopOfStack(c);
		long id = c.getId();
		String name = c.getName();
		Bundle bundle = c.getBundle();
		OrphanedParticipant p = new OrphanedParticipant();
		c.addParticipant(p);
		Reference<Coordination> reference = new WeakReference<Coordination>(c, new ReferenceQueue<Coordination>());
		c = null;
		assertReferenceEnqueued(reference);
		coordinatorBundle.stop();
		assertOrphanedCoordination(p, id, name, bundle);
		coordinatorBundle.start();
		coordinatorReference = getContext().getServiceReference(Coordinator.class);
		coordinator = getContext().getService(coordinatorReference);
		assertEmptyStack();
	}

	protected void setUp() throws Exception {
		super.setUp();
		coordinatorBundle = coordinatorReference.getBundle();
	}

	protected void tearDown() throws Exception {
		coordinatorBundle.start();
		super.tearDown();
	}

	private static final long DEFAULT_INTERVAL = 500;
	private static final int DEFAULT_ITERATIONS = 10;

	private void assertOrphanedCoordination(OrphanedParticipant p, long id, String name, Bundle b) {
		assertOrphanedParticipant(p);
		assertOrphanedFailure(p.getFailedCoordination());
		assertId(id, p.getFailedCoordination());
		assertName(name, p.getFailedCoordination());
		assertBundle(b, p.getFailedCoordination());
		assertEndFailed(p.getFailedCoordination(), CoordinationException.FAILED);
	}

	private static void assertOrphanedParticipant(OrphanedParticipant p) {
		assertNotNull("Participant did not receive failed notification", p.getFailedCoordination());
	}

	private static void assertOrphanedFailure(Coordination c) {
		Throwable failure = c.getFailure();
		assertTrue("Failure must be ORPHANED or RELEASED", failure == Coordination.ORPHANED || failure == Coordination.RELEASED);
	}

	private static void assertId(long id, Coordination c) {
		assertEquals("Wrong id", id, c.getId());
	}

	private static void assertName(String name, Coordination c) {
		assertEquals("Wrong name", name, c.getName());
	}

	private static void assertBundle(Bundle b, Coordination c) {
		assertEquals("Wrong bundle", b, c.getBundle());
	}

	private static void assertReferenceEnqueued(Reference<?> reference) throws InterruptedException {
		waitForEnqueue(reference);
		assertTrue("Coordination was not garbage collected", reference.isEnqueued());
	}

	private static void waitForEnqueue(Reference<?> reference) throws InterruptedException {
		waitForEnqueue(reference, DEFAULT_ITERATIONS, DEFAULT_INTERVAL);
	}

	private static void waitForEnqueue(Reference<?> reference, int iterations, long interval) throws InterruptedException {
		for (int i = 0; i < iterations; i++) {
			System.gc();
			if (reference.isEnqueued())
				break;
			Sleep.sleep(interval);
		}
	}

	/**
	 * A participant whose failed method is called due to an orphaned
	 * coordination.
	 */
	public static class OrphanedParticipant implements Participant {
		private final AtomicReference<Coordination> reference = new AtomicReference<Coordination>();

		public void ended(Coordination coordination) throws Exception {
			// noop
		}

		public void failed(Coordination coordination) throws Exception {
			reference.set(coordination);
		}

		/**
		 * Returns the coordination received as the result of a call to this
		 * participant's failed method, if any.
		 *
		 * @return The failed coordination or null.
		 */
		public Coordination getFailedCoordination() {
			return reference.get();
		}
	}
}
