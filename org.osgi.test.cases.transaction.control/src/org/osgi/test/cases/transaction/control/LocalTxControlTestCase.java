/*
 * Copyright (c) OSGi Alliance (2017, 2018). All Rights Reserved.
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
package org.osgi.test.cases.transaction.control;

import static org.osgi.service.transaction.control.TransactionStatus.*;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.service.transaction.control.ScopedWorkException;
import org.osgi.service.transaction.control.TransactionException;
import org.osgi.service.transaction.control.TransactionRolledBackException;
import org.osgi.test.cases.transaction.control.resources.RecordingLocalResource;

/**
 * This test case runs against local Transaction Control services (i.e. ones
 * that aren't XA) testing the transaction lifecycle as defined in 147.4.5
 * 
 * @author $Id$
 */
public class LocalTxControlTestCase extends TxControlTestCase {
	
	/**
	 * A basic test that ensures that a Local Transaction Control reports that
	 * it supports local resources
	 * 
	 * @throws Exception
	 */
	public void testTxControlSupportsLocal() throws Exception {

		if (xaEnabled) {
			return;
		}

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				assertTrue(txControl.getCurrentContext().supportsLocal());
				return null;
			}
		});
	}

	/**
	 * A commit with a single local resource
	 * 
	 * @throws Exception
	 */
	public void testTxControlLocalCommit() throws Exception {

		if (xaEnabled) {
			return;
		}

		final RecordingLocalResource resource = new RecordingLocalResource(
				txControl);

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				txControl.getCurrentContext()
						.registerLocalResource(resource);
				return null;
			}
		});

		assertNull(resource.getRollbackStatus());
		assertEquals(COMMITTING, resource.getCommitStatus());
	}

	/**
	 * A rollback with a single local resource
	 * 
	 * @throws Exception
	 */
	public void testTxControlLocalRollback() throws Exception {

		if (xaEnabled) {
			return;
		}

		final RecordingLocalResource resource = new RecordingLocalResource(
				txControl);

		txControl.required(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				txControl.getCurrentContext().registerLocalResource(resource);

				txControl.getCurrentContext().setRollbackOnly();
				return null;
			}
		});

		assertNull(resource.getCommitStatus());
		assertEquals(ROLLING_BACK, resource.getRollbackStatus());
	}

	/**
	 * A commit with a failing first local resource
	 * 
	 * @throws Exception
	 */
	public void testTxControlLocalFirstParticipantFailure() throws Exception {

		if (xaEnabled) {
			return;
		}

		final IllegalStateException ise = new IllegalStateException("Bang");

		final RecordingLocalResource resource = new RecordingLocalResource(
				txControl) {
			@Override
			public void commit() throws TransactionException {
				super.commit();
				throw ise;
			}
		};
		final RecordingLocalResource resource2 = new RecordingLocalResource(
				txControl) {
			@Override
			public void commit() throws TransactionException {
				super.commit();
				throw ise;
			}
		};

		try {
			txControl.required(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					txControl.getCurrentContext()
							.registerLocalResource(resource);
					txControl.getCurrentContext()
							.registerLocalResource(resource2);
					return null;
				}
			});
			fail("Must throw a TransactionRolledBackException");
		} catch (TransactionRolledBackException te) {
			assertSame(ise, te.getCause());
		}

		if (resource.getCommitStatus() == null) {
			assertEquals(ROLLING_BACK, resource.getRollbackStatus());
			assertNull(resource2.getRollbackStatus());
			assertEquals(COMMITTING, resource2.getCommitStatus());
		} else {
			assertNull(resource.getRollbackStatus());
			assertEquals(ROLLING_BACK, resource2.getRollbackStatus());
			assertEquals(COMMITTING, resource.getCommitStatus());
			assertNull(resource2.getCommitStatus());
		}
	}

	/**
	 * A commit with a failing second local resource
	 * 
	 * @throws Exception
	 */
	public void testTxControlLocalSecondParticipantFailure() throws Exception {

		if (xaEnabled) {
			return;
		}

		final IllegalStateException ise = new IllegalStateException("Bang");

		final AtomicBoolean fail = new AtomicBoolean(false);

		final RecordingLocalResource resource = new RecordingLocalResource(
				txControl) {
			@Override
			public void commit() throws TransactionException {
				super.commit();
				if (fail.getAndSet(!fail.get())) {
					throw ise;
				}
			}
		};
		final RecordingLocalResource resource2 = new RecordingLocalResource(
				txControl) {
			@Override
			public void commit() throws TransactionException {
				super.commit();
				if (fail.getAndSet(!fail.get())) {
					throw ise;
				}
			}
		};
		final RecordingLocalResource resource3 = new RecordingLocalResource(
				txControl) {
			@Override
			public void commit() throws TransactionException {
				super.commit();
				if (fail.getAndSet(!fail.get())) {
					throw ise;
				}
			}
		};

		try {
			txControl.required(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					txControl.getCurrentContext()
							.registerLocalResource(resource);
					txControl.getCurrentContext()
							.registerLocalResource(resource2);
					txControl.getCurrentContext()
							.registerLocalResource(resource3);
					return null;
				}
			});
			fail("Must throw a TransactionException");
		} catch (TransactionException te) {
			assertSame(ise, te.getCause());
		}

		assertNull(resource.getRollbackStatus());
		assertEquals(COMMITTING, resource.getCommitStatus());
		assertNull(resource2.getRollbackStatus());
		assertEquals(COMMITTING, resource2.getCommitStatus());
		assertNull(resource3.getRollbackStatus());
		assertEquals(COMMITTING, resource3.getCommitStatus());
	}

	/**
	 * A rollback with a failing first local resource
	 * 
	 * @throws Exception
	 */
	public void testTxControlLocalFirstParticipantRollbackFailure()
			throws Exception {

		if (xaEnabled) {
			return;
		}

		final IllegalStateException ise = new IllegalStateException("Bang");
		final IllegalStateException ise2 = new IllegalStateException("I Win!");

		final RecordingLocalResource resource = new RecordingLocalResource(
				txControl) {
			@Override
			public void rollback() throws TransactionException {
				super.rollback();
				throw ise;
			}
		};
		final RecordingLocalResource resource2 = new RecordingLocalResource(
				txControl) {
			@Override
			public void rollback() throws TransactionException {
				super.rollback();
				throw ise;
			}
		};

		try {
			txControl.required(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					txControl.getCurrentContext()
							.registerLocalResource(resource);
					txControl.getCurrentContext()
							.registerLocalResource(resource2);

					throw ise2;
				}
			});
			fail("Must throw a TransactionRolledBackException");
		} catch (ScopedWorkException swe) {
			assertSame(ise2, swe.getCause());
			Throwable[] suppressed = swe.getSuppressed();
			assertNotNull(suppressed);
			assertEquals(2, suppressed.length);
			assertSame(ise, suppressed[0]);
			assertSame(ise, suppressed[1]);
		}

		assertNull(resource.getCommitStatus());
		assertNull(resource2.getCommitStatus());
		assertEquals(ROLLING_BACK, resource.getRollbackStatus());
		assertEquals(ROLLING_BACK, resource2.getRollbackStatus());
	}
}
