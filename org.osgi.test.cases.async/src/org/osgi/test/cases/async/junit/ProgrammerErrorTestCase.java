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
package org.osgi.test.cases.async.junit;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.async.Async;
import org.osgi.test.cases.async.services.MyService;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.tracker.ServiceTracker;

public class ProgrammerErrorTestCase extends OSGiTestCase {
	private ServiceTracker<Async, Async>	asyncTracker;
	private Async	async;
	
	private DummyMyService myServiceImpl;
	private ServiceRegistration<MyService> normalReg;
	
	static class DummyMyService implements MyService {

		public void doSlowStuff(int times) throws Exception {
		}

		public int countSlowly(int times) throws Exception {
			return 0;
		}

		public int failSlowly(int times) throws Exception {
			return 0;
		}

		public int nonDelegateCountSlowly(int times) throws Exception {
			return 0;
		}

		public int delegateFail() throws Exception {
			return 0;
		}

		public int nonDelegateFailSlowly(int times) throws Exception {
			return 0;
		}
	}

	protected void setUp() throws InterruptedException {
		myServiceImpl = new DummyMyService();
		normalReg = getContext().registerService(MyService.class, myServiceImpl, null);

		asyncTracker = new ServiceTracker<Async, Async>(getContext(), Async.class, null);
		asyncTracker.open();
		async = asyncTracker.waitForService(5000);

		assertNotNull("No Async service available within 5 seconds", async);
	}
	
	protected void tearDown() {
		asyncTracker.close();
		normalReg.unregister();
	}

	public void testAsyncCallWithoutMediatorCall() {
		async.mediate(normalReg.getReference(), MyService.class);
		try {
			async.call();
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		} catch (Exception e) {
			fail("Unexpected exception.", e);
		}
	}

	public void testAsyncCallWithMediatorCalledMultipleTimes() {
		MyService mediated = async.mediate(normalReg.getReference(), MyService.class);
		try {
			mediated.countSlowly(10);
			try {
				mediated.doSlowStuff(10);
				fail("Expected IllegalStateException");
			} catch (IllegalStateException e) {
				// expected
			}
			// make sure the pending method calls got flushed
			try {
				async.execute();
				fail("Expected IllegalStateException");
			} catch (IllegalStateException e) {
				// expected
			}
		} catch (Exception e) {
			fail("Unexpected exception.", e);
		}
	}

	public void testAsyncCallMultipleTimesWithSameMediator() {
		MyService mediated = async.mediate(normalReg.getReference(), MyService.class);
		try {
			mediated.countSlowly(10);
			async.execute();
			try {
				async.execute();
				fail("Expected IllegalStateException");
			} catch (IllegalStateException e) {
				// expected
			}
		} catch (Exception e) {
			fail("Unexpected exception.", e);
		}
	}

	public void testAsyncCallWithNoMediator() {
		try {
			async.execute();
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		} catch (Exception e) {
			fail("Unexpected exception.", e);
		}
	}

	public void testNewMediatorWithPendingMethods() {
		MyService mediated = async.mediate(normalReg.getReference(), MyService.class);
		try {
			mediated.doSlowStuff(10);
			// must be able to always create a new mediated object on the same thread
			MyService newMediated = async.mediate(normalReg.getReference(), MyService.class);
			try {
				// must not be able to create new mediated calls from the same thread
				// even if it is a different mediator
				newMediated.doSlowStuff(100);
			} catch (IllegalStateException e) {
				// expected;
			}
			// make sure the pending method call got flushed
			try {
				async.execute();
				fail("Expected IllegalStateException");
			} catch (IllegalStateException e) {
				// expected
			}
		} catch (Exception e) {
			fail("Unexpected exception.", e);
		}
	}
	public void testAsyncAndMediatorCalledWithDifferentThreads() throws InterruptedException {
		final CountDownLatch called = new CountDownLatch(1);
		final MyService mediated = async.mediate(normalReg.getReference(), MyService.class);
		final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
		Runnable callMediated = new Runnable() {
			public void run() {
				try {
					mediated.countSlowly(10);
				} catch (Throwable e) {
					error.set(e);
				} finally {
					called.countDown();
				}
			}
		};
		new Thread(callMediated, "calling mediated.").start();
		called.await();
		if (error.get() != null) {
			fail("Unexpected error.", error.get());
		}
		try {
			async.execute();
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		}
	}
}
