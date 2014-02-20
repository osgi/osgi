/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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

package org.osgi.test.cases.promise.junit;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

public class PromiseTest extends TestCase {
	public static final long	WAIT_TIME	= 2L;
	static Timer				timer		= new Timer();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPromiseSuccess1() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<String> p = d.getPromise();
		p.onResolve(new Runnable() {
			public void run() {
				latch.countDown();
			}
		});
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p.isDone());
		d.resolve("value");
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertNull("wrong error", p.getError());
		assertEquals("wrong value", "value", p.getValue());
	}

	public void testPromiseSuccess2() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<String> p = d.getPromise();
		p.then(new Success<String, String>() {
			public Promise<String> call(Promise<String> resolved) throws Exception {
				latch.countDown();
				return resolved;
			}
		});
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p.isDone());
		d.resolve("value");
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertNull("wrong error", p.getError());
		assertEquals("wrong value", "value", p.getValue());
	}

	public void testPromiseSuccess3() throws Exception {
		final Deferred<Integer> d = new Deferred<Integer>();
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<Integer> p = d.getPromise();
		Promise<String> p2 = p.then(new Success<String, Number>() {
			public Promise<String> call(Promise<Number> resolved) throws Exception {
				final Promise<String> returned = Promises.newResolvedPromise(resolved.getValue().toString());
				returned.onResolve(new Runnable() {
					public void run() {
						latch.countDown();
					}
				});
				return returned;
			}
		}, new Failure() {
			public void fail(Promise<?> resolved) throws Exception {
			}
		});
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p.isDone());
		assertFalse("callback ran before resolved", p2.isDone());
		d.resolve(Integer.valueOf(15));
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertTrue("callback did not run after resolved", p2.isDone());
		assertEquals("wrong value", "15", p2.getValue());
		assertNull("wrong error", p2.getError());
		assertNull("wrong error", p.getError());
		assertEquals("wrong value", Integer.valueOf(15), p.getValue());
	}

	public void testPromiseSuccess4() throws Exception {
		final Deferred<Integer> d1 = new Deferred<Integer>();
		final Deferred<String> d2 = new Deferred<String>();
		final AtomicReference<String> result = new AtomicReference<String>();
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<Integer> p = d1.getPromise();
		Promise<String> p2 = p.then(new Success<String, Number>() {
			public Promise<String> call(Promise<Number> resolved) throws Exception {
				result.set(resolved.getValue().toString());
				Promise<String> returned = d2.getPromise();
				returned.onResolve(new Runnable() {
					public void run() {
						latch.countDown();
					}
				});
				return returned;
			}
		}, new Failure() {
			public void fail(Promise<?> resolved) throws Exception {
			}
		});
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p.isDone());
		assertFalse("callback ran before resolved", p2.isDone());
		d1.resolve(Integer.valueOf(15));
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertFalse("callback ran before resolved", p2.isDone());
		d2.resolve(result.get());
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertTrue("callback did not run after resolved", p2.isDone());
		assertEquals("wrong value", "15", p2.getValue());
		assertNull("wrong error", p2.getError());
		assertNull("wrong error", p.getError());
		assertEquals("wrong value", Integer.valueOf(15), p.getValue());
	}

	public void testPromiseFail1() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<String> p = d.getPromise();
		p.onResolve(new Runnable() {
			public void run() {
				latch.countDown();
			}
		});
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p.isDone());
		Throwable failure = new RuntimeException();
		d.fail(failure);
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertSame("wrong error", failure, p.getError());
		try {
			p.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong error", failure, e.getCause());
		}
	}

	public void testPromiseFail2() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<String> p = d.getPromise();
		p.then(null, new Failure() {
			public void fail(Promise<?> resolved) throws Exception {
				latch.countDown();
			}
		});
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p.isDone());
		Throwable failure = new RuntimeException();
		d.fail(failure);
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p.isDone());
		assertSame("wrong error", failure, p.getError());
		try {
			p.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong error", failure, e.getCause());
		}
	}

	/**
	 * Test if we can get the errors when there is a chain. The idea is that you
	 * only specify the failure callback on the last
	 * {@link Promise#then(Success,Failure)} method. Any failures will bubble
	 * up.
	 * 
	 * @throws Exception
	 */
	public void testErrorChain() throws Exception {
		Deferred<String> d = new Deferred<String>();
		final Promise<String> p1 = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicInteger callbackCallCount = new AtomicInteger(0);

		Success<String, String> doubler = new Success<String, String>() {
			public Promise<String> call(Promise<String> promise) throws Exception {
				callbackCallCount.incrementAndGet();
				return Promises.newResolvedPromise(promise.getValue() + promise.getValue());
			}
		};
		final Promise<String> p2 = p1.then(doubler).then(doubler).then(doubler);

		p2.onResolve(new Runnable() {
			public void run() {
				try {
					if (p2.getError() != null)
						latch.countDown();
				} catch (Exception e) {
					fail("unexpected exception", e);
				}
			}
		});

		Exception failure = new Exception("Y");
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p2.isDone());
		d.fail(failure);
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p2.isDone());

		assertSame("wrong exception", failure, p2.getError());
		try {
			p2.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong error", failure, e.getCause());
		}
		assertEquals("wrong number of callbacks called", 0, callbackCallCount.get());
	}

	public void testSuccessChain() throws Exception {
		Deferred<String> d = new Deferred<String>();
		final Promise<String> p1 = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicInteger successCallbackCallCount = new AtomicInteger(0);
		final AtomicInteger failureCallbackCallCount = new AtomicInteger(0);

		Success<String, String> doubler = new Success<String, String>() {
			public Promise<String> call(Promise<String> promise) throws Exception {
				successCallbackCallCount.incrementAndGet();
				return Promises.newResolvedPromise(promise.getValue() + promise.getValue());
			}
		};
		Failure wrapper = new Failure() {
			public void fail(Promise<?> promise) throws Exception {
				failureCallbackCallCount.incrementAndGet();
				throw new Exception(promise.getError());
			}
		};
		final Promise<String> p2 = p1.then(doubler, wrapper).then(doubler, wrapper).then(doubler, wrapper);

		p2.onResolve(new Runnable() {
			public void run() {
				try {
					if (p2.getError() == null)
						latch.countDown();
				} catch (Exception e) {
					fail("unexpected exception", e);
				}
			}
		});

		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p2.isDone());
		d.resolve("Y");
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p2.isDone());

		assertEquals("wrong value", "YYYYYYYY", p2.getValue());
		assertNull("wrong error", p2.getError());
		assertEquals("wrong number of success callbacks called", 3, successCallbackCallCount.get());
		assertEquals("wrong number of failure callbacks called", 0, failureCallbackCallCount.get());
	}

	public void testExceptionOverride() throws Exception {
		Deferred<String> d = new Deferred<String>();
		final Promise<String> p1 = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicInteger successCallbackCallCount = new AtomicInteger(0);
		final AtomicInteger failureCallbackCallCount = new AtomicInteger(0);

		Success<String, String> doubler = new Success<String, String>() {
			public Promise<String> call(Promise<String> promise) throws Exception {
				successCallbackCallCount.incrementAndGet();
				return Promises.newResolvedPromise(promise.getValue() + promise.getValue());
			}
		};
		Failure wrapper = new Failure() {
			public void fail(Promise<?> promise) throws Exception {
				failureCallbackCallCount.incrementAndGet();
				throw new Exception(promise.getError());
			}
		};
		final Promise<String> p2 = p1.then(doubler, wrapper).then(doubler, wrapper).then(doubler, wrapper);

		p2.onResolve(new Runnable() {
			public void run() {
				try {
					if (p2.getError() != null)
						latch.countDown();
				} catch (Exception e) {
					fail("unexpected exception", e);
				}
			}
		});

		Exception failure = new Exception("Y");
		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse("callback ran before resolved", p2.isDone());
		d.fail(failure);
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue("callback did not run after resolved", p2.isDone());

		assertEquals("wrong number of success callbacks called", 0, successCallbackCallCount.get());
		assertEquals("wrong number of failure callbacks called", 3, failureCallbackCallCount.get());
		assertSame("wrong exception", failure, p2.getError().getCause().getCause().getCause());
		try {
			p2.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong error", failure, e.getCause().getCause().getCause().getCause());
		}
	}

	/**
	 * Check if a promise can be called after it has already called back. This
	 * is a common use case for promises. I.e. you create a promise and whenever
	 * someone needs the value he uses 'then' instead of directly getting the
	 * value. Does take some getting used to.
	 */
	public void testRepeat() throws Exception {
		Deferred<String> d = new Deferred<String>();
		Promise<String> p1 = d.getPromise();
		d.resolve("10");
		assertTrue("promise not resolved", p1.isDone());

		final CountDownLatch latch1 = new CountDownLatch(1);
		p1.onResolve(new Runnable() {
			public void run() {
				latch1.countDown();
			}
		});
		assertTrue("callback did not run after resolved", latch1.await(WAIT_TIME, TimeUnit.SECONDS));

		final CountDownLatch latch2 = new CountDownLatch(1);
		p1.onResolve(new Runnable() {
			public void run() {
				latch2.countDown();
			}
		});
		assertTrue("callback did not run after resolved", latch2.await(WAIT_TIME, TimeUnit.SECONDS));

		final CountDownLatch latch3 = new CountDownLatch(1);
		Promise<Integer> p2 = p1.then(new Success<Integer, String>() {
			public Promise<Integer> call(Promise<String> promise) throws Exception {
				latch3.countDown();
				return Promises.newResolvedPromise(Integer.valueOf(promise.getValue()));
			}
		});
		assertTrue("callback did not run after resolved", latch3.await(WAIT_TIME, TimeUnit.SECONDS));

		assertEquals("wrong value", 10, p2.getValue().intValue());
		assertNull("wrong error", p2.getError());
	}

	/**
	 * Test the basic chaining functionality.
	 */
	public void testThen() throws Exception {
		Deferred<String> d = new Deferred<String>();
		Promise<String> p1 = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(1);
		Promise<Integer> p2 = p1.then(new Success<Integer, String>() {
			public Promise<Integer> call(final Promise<String> promise)
					throws Exception {
				latch.countDown();
				final Deferred<Integer> n = new Deferred<Integer>();
				timer.schedule(new TimerTask() {
					public void run() {
						try {
							n.resolve(Integer.valueOf(promise.getValue()));
						} catch (Exception e) {
							n.fail(e);
						}
					}
				}, 500);
				return n.getPromise();
			}
		});

		assertFalse("callback ran before resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertFalse(p1.isDone());
		assertFalse(p2.isDone());

		d.resolve("20");
		assertTrue("callback did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
		assertTrue(p1.isDone());
		assertFalse(p2.isDone());

		assertEquals(20, p2.getValue().intValue());
		assertNull("wrong error", p2.getError());
	}

	public void testValueInterrupted() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		Promise<String> p = d.getPromise();
		final Thread thread = Thread.currentThread();
		assertFalse(p.isDone());
		timer.schedule(new TimerTask() {
			public void run() {
				thread.interrupt();
				timer.schedule(new TimerTask() {
					public void run() {
						d.resolve("failsafe");
					}
				}, 1000);
			}
		}, 500);
		try {
			p.getValue();
			fail("failed to throw InterruptedException");
		} catch (InterruptedException e) {
			// expected
		}
	}

	public void testErrorInterrupted() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		Promise<String> p = d.getPromise();
		final Thread thread = Thread.currentThread();
		assertFalse(p.isDone());
		timer.schedule(new TimerTask() {
			public void run() {
				thread.interrupt();
				timer.schedule(new TimerTask() {
					public void run() {
						d.resolve("failsafe");
					}
				}, 1000);
			}
		}, 500);
		try {
			p.getError();
			fail("failed to throw InterruptedException");
		} catch (InterruptedException e) {
			// expected
		}
	}

	public void testNullCallback() throws Exception {
		Deferred<String> d = new Deferred<String>();
		Promise<String> p = d.getPromise();
		try {
			p.onResolve(null);
			fail("failed to error on null callback");
		} catch (NullPointerException e) {
			// expected
		}
	}

	public void testMultiResolve() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		Promise<String> p = d.getPromise();
		final AtomicBoolean fail = new AtomicBoolean(false);
		assertFalse(p.isDone());
		p.onResolve(new Runnable() {
			public void run() {
				try {
					d.resolve("onResolve");
					fail.set(true);
				} catch (IllegalStateException e) {
					// expected
				}
			}
		});
		d.resolve("first");
		assertTrue(p.isDone());
		assertFalse("failed to error on callback resolve", fail.get());
		try {
			d.resolve("second");
			fail("failed to error on second resolve");
		} catch (IllegalStateException e) {
			// expected
		}
		try {
			d.fail(new Exception("second"));
			fail("failed to error on fail after resolve");
		} catch (IllegalStateException e) {
			// expected
		}
	}

	public void testMultiFail() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		Promise<String> p = d.getPromise();
		final AtomicBoolean fail = new AtomicBoolean(false);
		assertFalse(p.isDone());
		p.onResolve(new Runnable() {
			public void run() {
				try {
					d.fail(new Exception("onResolve"));
					fail.set(true);
				} catch (IllegalStateException e) {
					// expected
				}
			}
		});
		d.fail(new Exception("first"));
		assertTrue(p.isDone());
		assertFalse("failed to error on callback fail", fail.get());
		try {
			d.fail(new Exception("second"));
			fail("failed to error on second fail");
		} catch (IllegalStateException e) {
			// expected
		}
		try {
			d.resolve("second");
			fail("failed to error on resolve after fail");
		} catch (IllegalStateException e) {
			// expected
		}
	}

	public void testCallbackException1() throws Exception {
		final int size = 20;
		final Deferred<String> d = new Deferred<String>();
		final Promise<String> p = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(size);
		final AtomicInteger count = new AtomicInteger(0);
		Random random = new Random();
		int next = random.nextInt(size);
		final int runtimeFail = next;
		do {
			next = random.nextInt(size);
		} while (next == runtimeFail);
		final int errorFail = next;
		for (int i = 0; i < size; i++) {
			p.onResolve(new Runnable() {
				public void run() {
					final int callback = count.getAndIncrement();
					latch.countDown();
					if (callback == runtimeFail) {
						throw new RuntimeException("bad callback " + callback);
					} else if (callback == errorFail) {
						throw new Error("bad callback " + callback);
					}
				}
			});
		}
		assertFalse("should not be resolved", p.isDone());
		assertEquals("some callbacks called", size, latch.getCount());
		final String result = "value";
		d.resolve(result);
		assertTrue("should be resolved", p.isDone());
		assertNull("wrong error", p.getError());
		assertEquals("wrong value", result, p.getValue());
		assertTrue("all callbacks did not run after resolved", latch.await(WAIT_TIME, TimeUnit.SECONDS));
	}

	public void testCallbackException2() throws Exception {
		final Deferred<String> d = new Deferred<String>();
		final Promise<String> p = d.getPromise();
		assertFalse("should not be resolved", p.isDone());
		Throwable failure = new RuntimeException();
		d.fail(failure);
		p.onResolve(new Runnable() {
			public void run() {
				throw new Error("bad callback upon onResolve");
			}
		});
		assertTrue("should be resolved", p.isDone());
		assertSame("wrong error", failure, p.getError());
		try {
			p.getValue();
			fail("getValue failed to throw InvocationTargetException");
		} catch (InvocationTargetException e) {
			assertSame("wrong error", failure, e.getCause());
		}
	}

	/**
	 * Fail with cause t.
	 * 
	 * @param message Failure message.
	 * @param t Cause of the failure.
	 */
	public static void fail(String message, Throwable t) {
		AssertionFailedError e = new AssertionFailedError(message + ": "
				+ t.getMessage());
		e.initCause(t);
		throw e;
	}

}
