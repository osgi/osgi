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

package org.osgi.test.cases.promise.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.fail;
import static org.osgi.test.assertj.promise.PromiseAssert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.osgi.test.support.mock.MockFactory;
import org.osgi.util.function.Consumer;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.FailedPromisesException;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;
import org.osgi.util.promise.PromiseFactory.Option;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;
import org.osgi.util.promise.TimeoutException;

public class PromiseTest {
	ExecutorService				callbackExecutor;
	ScheduledExecutorService	scheduledExecutor;
	PromiseFactory				factory;

	public static final long	WAIT_TIME	= 2L;
	private static final Random	random		= new Random();
	private String				originalAllowCurrentThread;

	String						testMethodName;

	@BeforeEach
	public void setUp(TestInfo info) throws Exception {
		testMethodName = info.getTestMethod().map(Method::getName).get();
		boolean callbacksExecutorOnly = random.nextBoolean();
		boolean inlineExecutor = random.nextBoolean();
		originalAllowCurrentThread = System
				.getProperty("org.osgi.util.promise.allowCurrentThread");
		System.setProperty("org.osgi.util.promise.allowCurrentThread",
				Boolean.toString(!callbacksExecutorOnly));
		callbackExecutor = Executors.newFixedThreadPool(2);
		scheduledExecutor = Executors.newScheduledThreadPool(2);
		Executor executor = inlineExecutor ? PromiseFactory.inlineExecutor()
				: callbackExecutor;
		factory = callbacksExecutorOnly
				? new PromiseFactory(executor, scheduledExecutor,
						Option.CALLBACKS_EXECUTOR_THREAD)
				: new PromiseFactory(executor, scheduledExecutor);
	}

	@AfterEach
	public void tearDown() throws Exception {
		callbackExecutor.shutdown();
		scheduledExecutor.shutdown();
		if (originalAllowCurrentThread == null) {
			System.clearProperty("org.osgi.util.promise.allowCurrentThread");
		} else {
			System.setProperty("org.osgi.util.promise.allowCurrentThread",
					originalAllowCurrentThread);
		}
	}

	@Test
	public void testDeferredPromiseSuccess() throws Exception {
		final Deferred<String> d = new Deferred<>();
		final Promise<String> p = d.getPromise();
		assertThat(p).isNotDone();
		String value = new String("value");
		d.resolve(value);
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value);
	}

	@Test
	public void testPromiseSuccess1() throws Exception {
		final Deferred<String> d = factory.deferred();
		final Promise<String> p = d.getPromise();
		assertThat(p).isNotDone();
		String value = new String("value");
		d.resolve(value);
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value);
	}

	@Test
	public void testPromiseSuccess2() throws Exception {
		final Deferred<Integer> d = factory.deferred();
		final Promise<Integer> p = d.getPromise();
		Promise<Number> p2 = p.then(resolved -> factory.resolved(
				(Number) Long.valueOf(resolved.getValue().longValue())));
		assertThat(p).isNotDone();
		assertThat(p2).isNotDone();
		Integer value = Integer.valueOf(15);
		d.resolve(value);
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValueMatching(v -> v.intValue() == value.intValue());
	}

	@Test
	public void testPromiseSuccess3() throws Exception {
		final Deferred<Integer> d = factory.deferred();
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		final CountDownLatch latch3 = new CountDownLatch(1);
		final Promise<Integer> p = d.getPromise();
		Promise<Number> p2 = p.then(resolved -> {
			final Promise<Number> returned = factory.resolved(
					(Number) Long.valueOf(resolved.getValue().longValue()));
			returned.onResolve(() -> latch1.countDown());
			return returned;
		}, resolved -> latch2.countDown());
		p2.onResolve(latch3::countDown);
		assertThat(latch1.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(latch2.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p).isNotDone();
		assertThat(p2).isNotDone();
		Integer value = Integer.valueOf(15);
		d.resolve(value);
		assertThat(latch3.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(latch1.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(latch2.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p).isDone();
		assertThat(p2).isDone();
		assertThat(p).hasSameValue(value);
		assertThat(p2).hasValueMatching(v -> v.intValue() == value.intValue());
	}

	@Test
	public void testPromiseSuccess4() throws Exception {
		final Deferred<Integer> d1 = factory.deferred();
		final Deferred<String> d2 = factory.deferred();
		final AtomicReference<String> result = new AtomicReference<String>();
		final CountDownLatch latch = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		final Promise<Integer> p = d1.getPromise();
		Promise<String> p2 = p.then(resolved -> {
			result.set(resolved.getValue().toString());
			Promise<String> returned = d2.getPromise();
			returned.onResolve(latch::countDown);
			return returned;
		}, resolved -> {
		});
		p2.onResolve(latch2::countDown);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p).isNotDone();
		assertThat(p2).isNotDone();
		Integer value = Integer.valueOf(15);
		d1.resolve(value);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p).isDone();
		assertThat(p2).isNotDone();
		d2.resolve(result.get());
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p).isDone();
		assertThat(latch2.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p2).isDone();
		assertThat(p).hasSameValue(value);
		assertThat(p2).isSuccessful();
		assertThat(Integer.valueOf(result.get())).isSameAs(value);
	}

	@Test
	public void testDeferredPromiseFail() throws Exception {
		final Deferred<String> d = new Deferred<>();
		final Promise<String> p = d.getPromise();
		assertThat(p).isNotDone();
		Throwable failure = new RuntimeException();
		d.fail(failure);
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
	}

	@Test
	public void testPromiseFail1() throws Exception {
		final Deferred<String> d = factory.deferred();
		final Promise<String> p = d.getPromise();
		assertThat(p).isNotDone();
		Throwable failure = new RuntimeException();
		d.fail(failure);
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
	}

	@Test
	public void testPromiseFail2() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		final Deferred<String> d = factory.deferred();
		final AtomicReference<Throwable> result = new AtomicReference<>();
		final Throwable failure = new RuntimeException();
		final Promise<String> p = d.getPromise();
		p.then(null, resolved -> {
			result.set(resolved.getFailure());
			latch.countDown();
		});
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p).isNotDone();
		d.fail(failure);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p).hasFailedWithThrowableThat().isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(result.get()).isSameAs(failure);
	}

	/**
	 * Test if we can get the failures when there is a chain. The idea is that
	 * you only specify the failure callback on the last
	 * {@link Promise#then(Success,Failure)} method. Any failures will bubble
	 * up.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFailureChain() throws Exception {
		Deferred<String> d = factory.deferred();
		final Promise<String> p1 = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicInteger callbackCallCount = new AtomicInteger(0);

		Success<String,String> doubler = promise -> {
			callbackCallCount.incrementAndGet();
			return factory.resolved(promise.getValue() + promise.getValue());
		};
		final Promise<String> p2 = p1.then(doubler).then(doubler).then(doubler);

		p2.onResolve(() -> {
			try {
				if (p2.getFailure() != null)
					latch.countDown();
			} catch (Exception e) {
				fail("unexpected exception", e);
			}
		});

		Exception failure = new Exception("Y");
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p2).isNotDone();
		d.fail(failure);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p2).hasFailedWithThrowableThat().isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(callbackCallCount).hasValue(0);
	}

	@Test
	public void testSuccessChain() throws Exception {
		Deferred<String> d = factory.deferred();
		final Promise<String> p1 = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicInteger successCallbackCallCount = new AtomicInteger(0);
		final AtomicInteger failureCallbackCallCount = new AtomicInteger(0);

		Success<String,String> doubler = promise -> {
			successCallbackCallCount.incrementAndGet();
			return factory.resolved(promise.getValue() + promise.getValue());
		};
		Failure wrapper = promise -> {
			failureCallbackCallCount.incrementAndGet();
			throw new Exception(promise.getFailure());
		};
		final Promise<String> p2 = p1.then(doubler, wrapper)
				.then(doubler, wrapper)
				.then(doubler, wrapper);

		p2.onResolve(() -> {
			try {
				if (p2.getFailure() == null)
					latch.countDown();
			} catch (Exception e) {
				fail("unexpected exception", e);
			}
		});

		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p2).isNotDone();
		String value = new String("Y");
		d.resolve(value);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p2).hasValue("YYYYYYYY");
		assertThat(successCallbackCallCount).hasValue(3);
		assertThat(failureCallbackCallCount).hasValue(0);
	}

	@Test
	public void testExceptionOverride() throws Exception {
		Deferred<String> d = factory.deferred();
		final Promise<String> p1 = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicInteger successCallbackCallCount = new AtomicInteger(0);
		final AtomicInteger failureCallbackCallCount = new AtomicInteger(0);

		Success<String,String> doubler = promise -> {
			successCallbackCallCount.incrementAndGet();
			return factory.resolved(promise.getValue() + promise.getValue());
		};
		Failure wrapper = promise -> {
			failureCallbackCallCount.incrementAndGet();
			throw new Exception(promise.getFailure());
		};
		final Promise<String> p2 = p1.then(doubler, wrapper)
				.then(doubler, wrapper)
				.then(doubler, wrapper);

		p2.onResolve(() -> {
			try {
				if (p2.getFailure() != null)
					latch.countDown();
			} catch (Exception e) {
				fail("unexpected exception", e);
			}
		});

		Exception failure = new Exception("Y");
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p2).isNotDone();
		d.fail(failure);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p2).isDone();
		assertThat(successCallbackCallCount).hasValue(0);
		assertThat(failureCallbackCallCount).hasValue(3);
		assertThat(p2.getFailure().getCause().getCause().getCause())
				.isSameAs(failure);
		Throwable thrown = catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class);
		assertThat(thrown.getCause().getCause().getCause().getCause())
				.isSameAs(failure);
	}

	/**
	 * Check if a promise can be called after it has already called back. This
	 * is a common use case for promises. I.e. you create a promise and whenever
	 * someone needs the value he uses 'then' instead of directly getting the
	 * value. Does take some getting used to.
	 */
	@Test
	public void testRepeat() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p1 = d.getPromise();
		String value = new String("10");
		d.resolve(value);
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS);
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS);
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS);

		Promise<Integer> p2 = p1.then(promise -> factory
				.resolved(Integer.valueOf(promise.getValue())));
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(Integer.valueOf(10));
	}

	/**
	 * Test the basic chaining functionality.
	 */
	@Test
	public void testThen() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p1 = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		Promise<Number> p2 = p1.then(promise -> {
			latch.countDown();
			final Deferred<Number> n = factory.deferred();
			scheduledExecutor.schedule(() -> {
				try {
					latch2.await();
					n.resolve(Integer.valueOf(promise.getValue().toString()));
				} catch (Exception e) {
					n.fail(e);
				}
			}, 500, TimeUnit.MILLISECONDS);
			return n.getPromise();
		});

		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p1).isNotDone();
		assertThat(p2).isNotDone();

		String value = new String("20");
		d.resolve(value);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p1).isDone();
		assertThat(p2).isNotDone();
		latch2.countDown();
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS).hasValue(20);
	}

	@Test
	public void testThenSuccessNull1() throws Exception {
		String value = new String("20");
		Deferred<String> d = factory.deferred();
		Promise<String> p1 = d.getPromise();
		d.resolve(value); // resolve before then

		Promise<String> p2 = p1.then(null, null);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(null);
	}

	@Test
	public void testThenSuccessNull2() throws Exception {
		String value = new String("20");
		Deferred<String> d = factory.deferred();
		Promise<String> p1 = d.getPromise();
		Promise<String> p2 = p1.then(null, null);
		d.resolve(value); // resolve after then

		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(null);
	}

	@Test
	public void testThenSuccessNull3() throws Exception {
		String value = new String("20");
		Promise<String> p1 = factory.resolved(value);
		Promise<String> p2 = p1.then(null, null);

		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(null);
	}

	@Test
	public void testThenFailureNull1() throws Exception {
		Exception failure = new Exception("20");
		Deferred<String> d = factory.deferred();
		Promise<String> p1 = d.getPromise();
		d.fail(failure); // fail before then
		Promise<String> p2 = p1.then(null, null);

		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
	}

	@Test
	public void testThenFailureNull2() throws Exception {
		Exception failure = new Exception("20");
		Deferred<String> d = factory.deferred();
		Promise<String> p1 = d.getPromise();
		Promise<String> p2 = p1.then(null, null);
		d.fail(failure); // fail after then

		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
	}

	@Test
	public void testThenFailureNull3() throws Exception {
		Exception failure = new Exception("20");
		Promise<String> p1 = factory.failed(failure);
		Promise<String> p2 = p1.then(null, null);

		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
	}

	@Test
	public void testValueInterrupted() throws Exception {
		final Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		final Thread thread = Thread.currentThread();
		assertThat(p).isNotDone();
		scheduledExecutor.schedule(() -> {
			scheduledExecutor.schedule(() -> d.resolve("failsafe"), 1000,
					TimeUnit.MILLISECONDS);
			thread.interrupt();
		}, 500, TimeUnit.MILLISECONDS);
		assertThatThrownBy(() -> p.getValue())
				.isInstanceOf(InterruptedException.class);
	}

	@Test
	public void testFailureInterrupted() throws Exception {
		final Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		final Thread thread = Thread.currentThread();
		assertThat(p).isNotDone();
		scheduledExecutor.schedule(() -> {
			scheduledExecutor.schedule(() -> d.resolve("failsafe"), 1000,
					TimeUnit.MILLISECONDS);
			thread.interrupt();
		}, 500, TimeUnit.MILLISECONDS);
		assertThatThrownBy(() -> p.getFailure())
				.isInstanceOf(InterruptedException.class);
	}

	@Test
	public void testNullCallback() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		assertThatNullPointerException().isThrownBy(() -> p.onResolve(null));
	}

	@Test
	public void testFailNull() throws Exception {
		Deferred<String> d = factory.deferred();
		assertThatNullPointerException().isThrownBy(() -> d.fail(null));
	}

	@Test
	public void testMultiResolve() throws Exception {
		final Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		final AtomicBoolean fail = new AtomicBoolean(false);
		assertThat(p).isNotDone();
		p.onResolve(() -> {
			try {
				d.resolve("onResolve");
				fail.set(true);
			} catch (IllegalStateException e) {
				// expected
			}
		});
		d.resolve("first");
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue("first");
		assertThat(fail).isFalse();
		assertThatIllegalStateException().isThrownBy(() -> d.resolve("second"));
		assertThatIllegalStateException()
				.isThrownBy(() -> d.fail(new Exception("second")));
	}

	@Test
	public void testMultiFail() throws Exception {
		final Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		final AtomicBoolean fail = new AtomicBoolean(false);
		assertThat(p).isNotDone();
		p.onResolve(() -> {
			try {
				d.fail(new Exception("onResolve"));
				fail.set(true);
			} catch (IllegalStateException e) {
				// expected
			}
		});
		d.fail(new Exception("first"));
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.hasMessage("first");
		assertThatThrownBy(() -> p.getValue())
				.isInstanceOf(InvocationTargetException.class)
				.hasCause(new Exception("first"));
		assertThat(fail).isFalse();
		assertThatIllegalStateException()
				.isThrownBy(() -> d.fail(new Exception("second")));
		assertThatIllegalStateException().isThrownBy(() -> d.resolve("second"));
	}

	@Test
	public void testCallbackException1() throws Exception {
		final int size = 20;
		final Deferred<String> d = factory.deferred();
		final Promise<String> p = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(size);
		final AtomicInteger count = new AtomicInteger(0);
		int next = random.nextInt(size);
		final int runtimeFail = next;
		do {
			next = random.nextInt(size);
		} while (next == runtimeFail);
		final int errorFail = next;
		for (int i = 0; i < size; i++) {
			p.onResolve(() -> {
				final int callback = count.getAndIncrement();
				latch.countDown();
				if (callback == runtimeFail) {
					throw new RuntimeException("bad callback " + callback);
				} else if (callback == errorFail) {
					throw new Error("bad callback " + callback);
				}
			});
		}
		assertThat(p).isNotDone();
		assertThat(latch.getCount()).isEqualTo(size);
		final String result = "value";
		d.resolve(result);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p).hasSameValue(result);
	}

	@Test
	public void testCallbackException2() throws Exception {
		final Deferred<String> d = factory.deferred();
		final Promise<String> p = d.getPromise();
		assertThat(p).isNotDone();
		Throwable failure = new RuntimeException();
		d.fail(failure);
		p.onResolve(() -> {
			throw new Error("bad callback upon onResolve");
		});
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
	}

	@Test
	public void testFactoryResolvedPromise() throws Exception {
		String value1 = new String("value");
		final Promise<String> p1 = factory.resolved(value1);
		final Promise<String> p2 = factory.resolved(null);
		assertThat(p1).isDone()
				.resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
		assertThat(p2).isDone()
				.resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(null);
	}

	@Test
	public void testPromisesResolvedPromise() throws Exception {
		String value1 = new String("value");
		final Promise<String> p1 = Promises.resolved(value1);
		final Promise<String> p2 = Promises.resolved(null);
		assertThat(p1).isDone()
				.resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
		assertThat(p2).isDone()
				.resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(null);
	}

	@Test
	public void testFactoryFailedPromise() throws Exception {
		Throwable failure = new Exception("value");
		final Promise<String> p = factory.failed(failure);
		assertThat(p).isDone()
				.resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThatNullPointerException().isThrownBy(() -> factory.failed(null));
	}

	@Test
	public void testPromisesFailedPromise() throws Exception {
		Throwable failure = new Exception("value");
		final Promise<String> p = Promises.failed(failure);
		assertThat(p).isDone()
				.resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThatNullPointerException()
				.isThrownBy(() -> Promises.failed(null));
	}

	@Test
	public void testAllSuccess1() throws Exception {
		final Deferred<Integer> d1 = factory.deferred();
		final Promise<Integer> p1 = d1.getPromise();
		final Deferred<Long> d2 = factory.deferred();
		final Promise<Long> p2 = d2.getPromise();
		final Promise<List<Number>> latched = Promises.<Number> all(p1, p2);
		assertThat(p1).isNotDone();
		assertThat(p2).isNotDone();
		assertThat(latched).isNotDone();
		Integer value1 = Integer.valueOf(12);
		d1.resolve(value1);
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
		assertThat(p2).isNotDone();
		assertThat(latched).isNotDone();
		Long value2 = Long.valueOf(24);
		d2.resolve(value2);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value2);
		ListAssert<Number> listAssert = assertThat(
				latched).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
						.hasValueThat(
								InstanceOfAssertFactories.list(Number.class))
						.containsExactly(value1, value2);
		listAssert.element(0).isSameAs(value1);
		listAssert.element(1).isSameAs(value2);
	}

	@Test
	public void testAllSuccess2() throws Exception {
		final Deferred<Integer> d1 = factory.deferred();
		final Promise<Integer> p1 = d1.getPromise();
		final Deferred<Integer> d2 = factory.deferred();
		final Promise<Integer> p2 = d2.getPromise();
		List<Promise<Integer>> promises = new ArrayList<Promise<Integer>>();
		promises.add(p1);
		promises.add(p2);
		final Promise<List<Number>> latched = factory.all(promises);
		assertThat(p1).isNotDone();
		assertThat(p2).isNotDone();
		assertThat(latched).isNotDone();
		Integer value1 = Integer.valueOf(12);
		d1.resolve(value1);
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
		assertThat(p2).isNotDone();
		assertThat(latched).isNotDone();
		Integer value2 = Integer.valueOf(24);
		d2.resolve(value2);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value2);
		ListAssert<Number> listAssert = assertThat(
				latched).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
						.hasValueThat(
								InstanceOfAssertFactories.list(Number.class))
						.containsExactly(value1, value2);
		listAssert.element(0).isSameAs(value1);
		listAssert.element(1).isSameAs(value2);
	}

	@Test
	public void collector_success() throws Exception {
		final Deferred<Integer> d1 = factory.deferred();
		final Promise<Integer> p1 = d1.getPromise();
		final Deferred<Integer> d2 = factory.deferred();
		final Promise<Integer> p2 = d2.getPromise();
		final Promise<List<Number>> latched = Stream.of(p1, p2)
				.collect(factory.toPromise());
		assertThat(p1).isNotDone();
		assertThat(p2).isNotDone();
		assertThat(latched).isNotDone();
		Integer value1 = Integer.valueOf(12);
		d1.resolve(value1);
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
		assertThat(p2).isNotDone();
		assertThat(latched).isNotDone();
		Integer value2 = Integer.valueOf(24);
		d2.resolve(value2);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value2);
		ListAssert<Number> listAssert = assertThat(
				latched).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
						.hasValueThat(
								InstanceOfAssertFactories.list(Number.class))
						.containsExactly(value1, value2);
		listAssert.element(0).isSameAs(value1);
		listAssert.element(1).isSameAs(value2);
	}

	@Test
	public void testAllSuccess3() throws Exception {
		final Deferred<Number> d1 = factory.deferred();
		final Promise<Number> p1 = d1.getPromise();
		final Deferred<Number> d2 = factory.deferred();
		final Promise<Number> p2 = d2.getPromise();
		final Promise<List<Number>> latched = factory
				.all(Arrays.asList(p1, p2));
		assertThat(p1).isNotDone();
		assertThat(p2).isNotDone();
		assertThat(latched).isNotDone();
		Integer value1 = Integer.valueOf(12);
		d1.resolve(value1);
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
		assertThat(p2).isNotDone();
		assertThat(latched).isNotDone();
		Long value2 = Long.valueOf(24);
		d2.resolve(value2);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value2);
		ListAssert<Number> listAssert = assertThat(
				latched).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
						.hasValueThat(
								InstanceOfAssertFactories.list(Number.class))
						.containsExactly(value1, value2);
		listAssert.element(0).isSameAs(value1);
		listAssert.element(1).isSameAs(value2);
	}

	@Test
	public void testAllSuccess4() throws Exception {
		final Deferred<Integer> d1 = factory.deferred();
		final Promise<Integer> p1 = d1.getPromise();
		final Deferred<Integer> d2 = factory.deferred();
		final Promise<Integer> p2 = d2.getPromise();
		List<Promise<Integer>> promises = new ArrayList<Promise<Integer>>();
		promises.add(p1);
		promises.add(p2);
		final Promise<List<Number>> latched = factory
				.<Number, Integer> all(promises);
		assertThat(p1).isNotDone();
		assertThat(p2).isNotDone();
		assertThat(latched).isNotDone();
		Integer value1 = Integer.valueOf(12);
		d1.resolve(value1);
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
		assertThat(p2).isNotDone();
		assertThat(latched).isNotDone();
		Integer value2 = Integer.valueOf(24);
		d2.resolve(value2);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value2);
		ListAssert<Number> listAssert = assertThat(
				latched).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
						.hasValueThat(
								InstanceOfAssertFactories.list(Number.class))
						.containsExactly(value1, value2);
		listAssert.element(0).isSameAs(value1);
		listAssert.element(1).isSameAs(value2);
	}

	@Test
	public void testAllFail1() throws Exception {
		final Deferred<Number> d1 = factory.deferred();
		final Promise<Number> p1 = d1.getPromise();
		final Deferred<Number> d2 = factory.deferred();
		final Promise<Number> p2 = d2.getPromise();
		final Deferred<Long> d3 = factory.deferred();
		final Promise<Long> p3 = d3.getPromise();
		final Promise<List<Number>> latched = Promises.all(p1, p2, p3);
		assertThat(p1).isNotDone();
		assertThat(p2).isNotDone();
		assertThat(p3).isNotDone();
		assertThat(latched).isNotDone();
		Throwable f1 = new Exception("fail1");
		d1.fail(f1);
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(f1);
		assertThat(p2).isNotDone();
		assertThat(latched).isNotDone();
		Long value2 = Long.valueOf(24);
		d2.resolve(value2);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value2);
		assertThat(latched).isNotDone();
		Throwable f3 = new Exception("fail3");
		d3.fail(f3);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(f3);
		assertThat(latched).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(FailedPromisesException.class);
		assertThat(((FailedPromisesException) latched.getFailure())
				.getFailedPromises()).containsExactlyInAnyOrder(p1, p3);
		assertThatThrownBy(() -> latched.getValue())
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseInstanceOf(FailedPromisesException.class);
	}

	@Test
	public void collector_fail() throws Exception {
		final Deferred<Long> d1 = factory.deferred();
		final Promise<Long> p1 = d1.getPromise();
		final Deferred<Long> d2 = factory.deferred();
		final Promise<Long> p2 = d2.getPromise();
		final Deferred<Long> d3 = factory.deferred();
		final Promise<Long> p3 = d3.getPromise();
		final Promise<List<Number>> latched = Stream.of(p1, p2, p3)
				.collect(factory.toPromise());
		assertThat(p1).isNotDone();
		assertThat(p2).isNotDone();
		assertThat(p3).isNotDone();
		assertThat(latched).isNotDone();
		Throwable f1 = new Exception("fail1");
		d1.fail(f1);
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(f1);
		assertThat(p2).isNotDone();
		assertThat(latched).isNotDone();
		Long value2 = Long.valueOf(24);
		d2.resolve(value2);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value2);
		assertThat(latched).isNotDone();
		Throwable f3 = new Exception("fail3");
		d3.fail(f3);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(f3);
		assertThat(latched).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(FailedPromisesException.class);
		assertThat(((FailedPromisesException) latched.getFailure())
				.getFailedPromises()).containsExactlyInAnyOrder(p1, p3);
		assertThatThrownBy(() -> latched.getValue())
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseInstanceOf(FailedPromisesException.class);
	}

	@Test
	public void testAllEmptyCollection() throws Exception {
		Collection<Promise<String>> promises = Collections.emptyList();
		final Promise<List<String>> latched = factory.all(promises);
		assertThat(latched).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValueThat(InstanceOfAssertFactories.LIST)
				.isEmpty();
	}

	@Test
	public void testAllEmptyVarargs() throws Exception {
		final Promise<List<Object>> latched = Promises.all();
		assertThat(latched).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValueThat(InstanceOfAssertFactories.LIST)
				.isEmpty();
	}

	@Test
	public void testAllNull() throws Exception {
		assertThatNullPointerException()
				.isThrownBy(() -> Promises.all((Promise< ? >[]) null));
		assertThatNullPointerException().isThrownBy(
				() -> factory.all((Collection<Promise<Object>>) null));
	}

	@Test
	public void testResolveWithSuccess() throws Exception {
		final Deferred<Integer> d1 = factory.deferred();
		final Promise<Integer> p1 = d1.getPromise();
		final Deferred<Number> d2 = factory.deferred();
		final Promise<Number> p2 = d2.getPromise();
		final Promise<Void> p3 = d2.resolveWith(p1);
		assertThat(p1).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);
		assertThat(p2).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);
		assertThat(p3).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);

		Integer value = Integer.valueOf(42);
		d1.resolve(value);
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(null);
	}

	@Test
	public void resolve_with_completionstage_success() throws Exception {
		final CompletableFuture<Integer> c = new CompletableFuture<>();
		final Deferred<Number> d2 = factory.deferred();
		final Promise<Number> p2 = d2.getPromise();
		final Promise<Void> p3 = d2.resolveWith(c);
		assertThat(c).isNotCompleted();
		assertThat(p2).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);
		assertThat(p3).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);

		Integer value = Integer.valueOf(42);
		c.complete(value);
		assertThat(c).isCompleted();
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(null);
	}

	@Test
	public void testResolveWithFailure() throws Exception {
		final Deferred<Integer> d1 = factory.deferred();
		final Promise<Integer> p1 = d1.getPromise();
		final Deferred<Number> d2 = factory.deferred();
		final Promise<Number> p2 = d2.getPromise();
		final Promise<Void> p3 = d2.resolveWith(p1);
		assertThat(p1).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);
		assertThat(p2).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);
		assertThat(p3).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);

		Throwable failure = new RuntimeException();
		d1.fail(failure);
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p1.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(null);
	}

	@Test
	public void resolve_with_completionstage_failure() throws Exception {
		final CompletableFuture<Integer> c = new CompletableFuture<>();
		final Deferred<Number> d2 = factory.deferred();
		final Promise<Number> p2 = d2.getPromise();
		final Promise<Void> p3 = d2.resolveWith(c);
		assertThat(c).isNotCompleted();
		assertThat(p2).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);
		assertThat(p3).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);

		Throwable failure = new RuntimeException();
		c.completeExceptionally(failure);
		assertThat(c).isCompletedExceptionally();
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(null);
	}

	@Test
	public void testResolveWithAlready1() throws Exception {
		final Deferred<Integer> d1 = factory.deferred();
		final Promise<Integer> p1 = d1.getPromise();
		final Deferred<Number> d2 = factory.deferred();
		final Promise<Number> p2 = d2.getPromise();
		final Promise<Void> p3 = d2.resolveWith(p1);
		assertThat(p1).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);
		assertThat(p2).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);
		assertThat(p3).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);

		Integer value = Integer.valueOf(42);
		d2.resolve(value);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value);
		assertThat(p3).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);

		Throwable failure = new RuntimeException();
		d1.fail(failure);
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p1.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> p3.getValue())
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseInstanceOf(IllegalStateException.class);
	}

	@Test
	public void resolve_with_completionstage_already_success()
			throws Exception {
		final CompletableFuture<Integer> c = new CompletableFuture<>();
		final Deferred<Number> d2 = factory.deferred();
		final Promise<Number> p2 = d2.getPromise();
		final Promise<Void> p3 = d2.resolveWith(c);
		assertThat(c).isNotCompleted();
		assertThat(p2).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);
		assertThat(p3).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);

		Integer value = Integer.valueOf(42);
		d2.resolve(value);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value);
		assertThat(p3).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);

		Throwable failure = new RuntimeException();
		c.completeExceptionally(failure);
		assertThat(c).isCompletedExceptionally();
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> p3.getValue())
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseInstanceOf(IllegalStateException.class);
	}

	@Test
	public void resolve_with_completionstage_already_failed() throws Exception {
		final CompletableFuture<Integer> c = new CompletableFuture<>();
		final Deferred<Number> d2 = factory.deferred();
		final Promise<Number> p2 = d2.getPromise();
		final Promise<Void> p3 = d2.resolveWith(c);
		assertThat(c).isNotCompleted();
		assertThat(p2).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);
		assertThat(p3).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);

		Throwable value = new RuntimeException("42");
		d2.fail(value);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(value);
		assertThat(p3).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);

		Throwable failure = new RuntimeException();
		c.completeExceptionally(failure);
		assertThat(c).isCompletedExceptionally();
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> p3.getValue())
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseInstanceOf(IllegalStateException.class);
	}

	@Test
	public void testResolveWithAlready2() throws Exception {
		Integer value = Integer.valueOf(42);
		final Promise<Integer> p1 = factory.resolved(value);
		final Deferred<Number> d2 = factory.deferred();
		d2.resolve(value);

		final Promise<Void> p3 = d2.resolveWith(p1);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> p3.getValue())
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseInstanceOf(IllegalStateException.class);
	}

	@Test
	public void testResolveWithAlready3() throws Exception {
		Integer value = Integer.valueOf(42);
		Throwable failure = new RuntimeException();
		final Promise<Integer> p1 = factory.resolved(value);
		final Deferred<Number> d2 = factory.deferred();
		d2.fail(failure);

		final Promise<Void> p3 = d2.resolveWith(p1);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> p3.getValue())
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseInstanceOf(IllegalStateException.class);
	}

	@Test
	public void testResolveWithAlreadyDeferred() throws Exception {
		Integer value = Integer.valueOf(42);
		final Promise<Integer> p1 = factory.resolved(value);
		final Deferred<Number> d2 = new Deferred<>();
		final Promise<Number> p2 = d2.getPromise();
		final Promise<Void> p3 = d2.resolveWith(p1);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(null);
	}

	@Test
	public void testResolveWithAlready4() throws Exception {
		Integer value = Integer.valueOf(42);
		final Promise<Integer> p1 = factory.resolved(value);
		final Deferred<Number> d2 = factory.deferred();
		final Promise<Number> p2 = d2.getPromise();
		final Promise<Void> p3 = d2.resolveWith(p1);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(null);
	}

	@Test
	public void testResolveWithAlready5() throws Exception {
		Throwable failure = new RuntimeException();
		final Promise<Integer> p1 = factory.failed(failure);
		final Deferred<Number> d2 = factory.deferred();
		final Promise<Number> p2 = d2.getPromise();
		final Promise<Void> p3 = d2.resolveWith(p1);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(null);
	}

	@Test
	public void factory_resolved_with_success1() throws Exception {
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);
		try {
			Integer value = Integer.valueOf(42);
			final Deferred<Integer> d1 = factory.deferred();
			final Promise<Integer> p1 = d1.getPromise();
			PromiseFactory factory2 = new PromiseFactory(newFixedThreadPool,
					factory.scheduledExecutor(),
					PromiseFactory.Option.CALLBACKS_EXECUTOR_THREAD);
			final Promise<Integer> p2 = factory2.resolvedWith(p1);
			assertThat(p2).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);

			d1.resolve(value);
			assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
					.hasSameValue(value);
		} finally {
			newFixedThreadPool.shutdown();
		}
	}

	@Test
	public void factory_resolved_with_success2() throws Exception {
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);
		try {
			Integer value = Integer.valueOf(42);
			PromiseFactory factory2 = new PromiseFactory(newFixedThreadPool,
					factory.scheduledExecutor(),
					PromiseFactory.Option.CALLBACKS_EXECUTOR_THREAD);
			final Promise<Integer> p2 = factory2
					.resolvedWith(factory.resolved(value));
			assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
					.hasSameValue(value);
		} finally {
			newFixedThreadPool.shutdown();
		}
	}

	@Test
	public void factory_resolved_with_failure1() throws Exception {
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);
		try {
			Throwable failure = new RuntimeException();
			final Deferred<Integer> d1 = factory.deferred();
			final Promise<Integer> p1 = d1.getPromise();
			PromiseFactory factory2 = new PromiseFactory(newFixedThreadPool,
					factory.scheduledExecutor(),
					PromiseFactory.Option.CALLBACKS_EXECUTOR_THREAD);
			final Promise<Integer> p2 = factory2.resolvedWith(p1);
			assertThat(p2).doesNotResolveWithin(WAIT_TIME, TimeUnit.SECONDS);

			d1.fail(failure);
			assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
					.hasFailedWithThrowableThat()
					.isSameAs(failure);
		} finally {
			newFixedThreadPool.shutdown();
		}
	}

	@Test
	public void factory_resolved_with_failure2() throws Exception {
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);
		try {
			Throwable failure = new RuntimeException();
			PromiseFactory factory2 = new PromiseFactory(newFixedThreadPool,
					factory.scheduledExecutor(),
					PromiseFactory.Option.CALLBACKS_EXECUTOR_THREAD);
			final Promise<Integer> p2 = factory2
					.resolvedWith(factory.failed(failure));
			assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
					.hasFailedWithThrowableThat()
					.isSameAs(failure);
		} finally {
			newFixedThreadPool.shutdown();
		}
	}

	@Test
	public void factory_resolved_with_null() throws Exception {
		assertThatNullPointerException()
				.isThrownBy(() -> factory.resolvedWith((Promise< ? >) null));
		assertThatNullPointerException().isThrownBy(
				() -> factory.resolvedWith((CompletionStage< ? >) null));
	}

	@Test
	public void testResolveWithNull() throws Exception {
		Deferred<String> d = factory.deferred();
		assertThatNullPointerException()
				.isThrownBy(() -> d.resolveWith((Promise<String>) null));
		assertThatNullPointerException().isThrownBy(
				() -> d.resolveWith((CompletionStage<String>) null));
	}

	@Test
	public void testFilter() throws Exception {
		String value1 = new String("value");
		String value3 = new String("");
		Promise<String> p1 = factory.resolved(value1);
		Promise<String> p2 = p1.filter(t -> t.length() > 0);
		Promise<String> p3 = factory.resolved(value3);
		Promise<String> p4 = p1.filter(t -> t.length() == 0);
		Promise<String> p5 = p3.filter(t -> t.length() > 0);

		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
		assertThat(p4).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(NoSuchElementException.class);
		assertThatThrownBy(() -> p4.getValue())
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseInstanceOf(NoSuchElementException.class);
		assertThat(p5).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(NoSuchElementException.class);
		assertThatThrownBy(() -> p5.getValue())
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseInstanceOf(NoSuchElementException.class);
	}

	@Test
	public void testFilterException() throws Exception {
		String value1 = new String("value");
		final Exception failure = new Exception("fail");
		Promise<String> p1 = factory.resolved(value1);
		final CountDownLatch latch = new CountDownLatch(1);
		Promise<String> p2 = p1.filter(t -> {
			throw failure;
		}).filter(t -> {
			latch.countDown();
			return t.length() > 0;
		});

		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();

	}

	@Test
	public void testFilterFailed() throws Exception {
		final Error failure = new Error("fail");
		Promise<String> p1 = factory.failed(failure);
		final CountDownLatch latch = new CountDownLatch(1);
		Promise<String> p2 = p1.filter(t -> {
			latch.countDown();
			return t.length() > 0;
		});

		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();

	}

	@Test
	public void testFilterNull() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = factory.resolved(value1);
		assertThatNullPointerException().isThrownBy(() -> p1.filter(null));

		Error failure2 = new Error("fail2");
		Promise<String> p2 = factory.failed(failure2);
		assertThatNullPointerException().isThrownBy(() -> p2.filter(null));
	}

	@Test
	public void testMap() throws Exception {
		Integer value1 = Integer.valueOf(42);
		Promise<Integer> p1 = factory.resolved(value1);
		Promise<String> p2 = p1.map(t -> Long.valueOf(t.longValue()))
				.map(t -> t.toString());
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(value1.toString());
	}

	@Test
	public void testMapException() throws Exception {
		Integer value1 = Integer.valueOf(42);
		final Exception failure = new Exception("fail");
		Promise<Integer> p1 = factory.resolved(value1);
		final CountDownLatch latch = new CountDownLatch(1);
		Promise<String> p2 = p1.map(t -> {
			throw failure;
		}).map(t -> {
			latch.countDown();
			return t.toString();
		});
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
	}

	@Test
	public void testMapNull() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = factory.resolved(value1);
		assertThatNullPointerException().isThrownBy(() -> p1.map(null));

		Error failure2 = new Error("fail2");
		Promise<String> p2 = factory.failed(failure2);
		assertThatNullPointerException().isThrownBy(() -> p2.map(null));
	}

	@Test
	public void testFlatMap() throws Exception {
		Integer value1 = Integer.valueOf(42);
		Promise<Integer> p1 = factory.resolved(value1);
		Promise<String> p2 = p1
				.flatMap(t -> factory.resolved(Long.valueOf(t.longValue())))
				.flatMap(t -> factory.resolved(t.toString()));
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(value1.toString());
	}

	@Test
	public void testFlatMapException() throws Exception {
		Integer value1 = Integer.valueOf(42);
		final Exception failure = new Exception("fail");
		Promise<Integer> p1 = factory.resolved(value1);
		final CountDownLatch latch = new CountDownLatch(1);
		Promise<String> p2 = p1.flatMap(t -> {
			throw failure;
		}).flatMap(t -> {
			latch.countDown();
			return factory.resolved(t.toString());
		});
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
	}

	@Test
	public void testFlatMapNull() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = factory.resolved(value1);
		assertThatNullPointerException().isThrownBy(() -> p1.flatMap(null));

		Error failure2 = new Error("fail2");
		Promise<String> p2 = factory.failed(failure2);
		assertThatNullPointerException().isThrownBy(() -> p2.flatMap(null));
	}

	@Test
	public void testRecoverNoFailure() throws Exception {
		final Number value1 = Integer.valueOf(42);
		final Long value2 = Long.valueOf(43);
		final Promise<Number> p1 = factory.resolved(value1);
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<Number> p2 = p1.recover(t -> {
			latch.countDown();
			return value2;
		});
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
	}

	@Test
	public void recover_type_no_failure() throws Exception {
		final Number value1 = Integer.valueOf(42);
		final Long value2 = Long.valueOf(43);
		final Promise<Number> p1 = factory.resolved(value1);
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<Number> p2 = p1.recover(t -> {
			latch.countDown();
			return value2;
		}, TimeoutException.class);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
	}

	@Test
	public void testRecoverFailure() throws Exception {
		final Throwable failure = new Error("fail");
		final Long value2 = Long.valueOf(43);
		final Promise<Number> p1 = factory.failed(failure);
		final Promise<Number> p2 = p1.recover(t -> {
			assertThat(t).hasFailedWithThrowableThat().isSameAs(failure);
			return value2;
		});
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value2);
	}

	@Test
	public void recover_type_failure_match() throws Exception {
		final Throwable failure = new Error("fail");
		final Long value2 = Long.valueOf(43);
		final Promise<Number> p1 = factory.failed(failure);
		final Promise<Number> p2 = p1.recover(t -> {
			assertThat(t).hasFailedWithThrowableThat().isSameAs(failure);
			return value2;
		}, Error.class);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value2);
	}

	@Test
	public void recover_type_failure_no_match() throws Exception {
		final Throwable failure = new Error("fail");
		final Long value2 = Long.valueOf(43);
		final Promise<Number> p1 = factory.failed(failure);
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<Number> p2 = p1.recover(t -> {
			latch.countDown();
			return value2;
		}, TimeoutException.class);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
	}

	@Test
	public void testRecoverFailureNull() throws Exception {
		final Throwable failure = new Error("fail");
		final Promise<Number> p1 = factory.failed(failure);
		final Promise<Number> p2 = p1.recover(t -> {
			assertThat(t).hasFailedWithThrowableThat().isSameAs(failure);
			return null;
		});
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
	}

	@Test
	public void testRecoverFailureException() throws Exception {
		final Throwable failure1 = new Exception("fail1");
		final Exception failure2 = new Exception("fail2");
		final Promise<Number> p1 = factory.failed(failure1);
		final Promise<Number> p2 = p1.recover(t -> {
			assertThat(t).hasFailedWithThrowableThat().isSameAs(failure1);
			throw failure2;
		});
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure2);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure2);
	}

	@Test
	public void testRecoverNull() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = factory.resolved(value1);
		assertThatNullPointerException().isThrownBy(() -> p1.recover(null));
		assertThatNullPointerException()
				.isThrownBy(() -> p1.recover(p -> "recovered", null));
		assertThatNullPointerException()
				.isThrownBy(() -> p1.recover(null, TimeoutException.class));

		Error failure2 = new Error("fail2");
		Promise<String> p2 = factory.failed(failure2);
		assertThatNullPointerException().isThrownBy(() -> p2.recover(null));
		assertThatNullPointerException()
				.isThrownBy(() -> p2.recover(p -> "recovered", null));
		assertThatNullPointerException()
				.isThrownBy(() -> p2.recover(null, TimeoutException.class));
	}

	@Test
	public void testRecoverWithNoFailure() throws Exception {
		final Number value1 = Integer.valueOf(42);
		final Long value2 = Long.valueOf(43);
		final Promise<Number> p1 = factory.resolved(value1);
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<Number> p2 = p1.recoverWith(t -> {
			latch.countDown();
			return factory.resolved(value2);
		});
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
	}

	@Test
	public void recover_with_type_no_failure() throws Exception {
		final Number value1 = Integer.valueOf(42);
		final Long value2 = Long.valueOf(43);
		final Promise<Number> p1 = factory.resolved(value1);
		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<Number> p2 = p1.recoverWith(t -> {
			latch.countDown();
			return factory.resolved(value2);
		}, TimeoutException.class);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
	}

	@Test
	public void testRecoverWithFailure() throws Exception {
		final Throwable failure = new Error("fail");
		final Long value2 = Long.valueOf(43);
		final Promise<Number> p1 = factory.failed(failure);
		final Promise<Number> p2 = p1.recoverWith(t -> {
			assertThat(t).hasFailedWithThrowableThat().isSameAs(failure);
			return factory.resolved(value2);
		});
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value2);
	}

	@Test
	public void recover_with_type_failure_match() throws Exception {
		final Throwable failure = new Error("fail");
		final Long value2 = Long.valueOf(43);
		final Promise<Number> p1 = factory.failed(failure);
		final Promise<Number> p2 = p1.recoverWith(t -> {
			assertThat(t).hasFailedWithThrowableThat().isSameAs(failure);
			return factory.resolved(value2);
		}, Error.class);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value2);
	}

	@Test
	public void recover_with_type_failure_no_match() throws Exception {
		final Throwable failure = new Error("fail");
		final CountDownLatch latch = new CountDownLatch(1);
		final Long value2 = Long.valueOf(43);
		final Promise<Number> p1 = factory.failed(failure);
		final Promise<Number> p2 = p1.recoverWith(t -> {
			latch.countDown();
			return factory.resolved(value2);
		}, TimeoutException.class);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
	}

	@Test
	public void testRecoverWithFailureNull() throws Exception {
		final Throwable failure = new Error("fail");
		final Promise<Number> p1 = factory.failed(failure);
		final Promise<Number> p2 = p1.recoverWith(t -> {
			assertThat(t).hasFailedWithThrowableThat().isSameAs(failure);
			return null;
		});
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
	}

	@Test
	public void testRecoverWithFailureException() throws Exception {
		final Throwable failure1 = new Exception("fail1");
		final Exception failure2 = new Exception("fail2");
		final Promise<Number> p1 = factory.failed(failure1);
		final Promise<Number> p2 = p1.recoverWith(t -> {
			assertThat(t).hasFailedWithThrowableThat().isSameAs(failure1);
			throw failure2;
		});
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure2);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure2);
	}

	@Test
	public void testRecoverWithNull() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = factory.resolved(value1);
		assertThatNullPointerException().isThrownBy(() -> p1.recoverWith(null));
		assertThatNullPointerException()
				.isThrownBy(() -> p1.recoverWith(p -> p1, null));
		assertThatNullPointerException()
				.isThrownBy(() -> p1.recoverWith(null, TimeoutException.class));

		Error failure2 = new Error("fail2");
		Promise<String> p2 = factory.failed(failure2);
		assertThatNullPointerException().isThrownBy(() -> p2.recoverWith(null));
		assertThatNullPointerException()
				.isThrownBy(() -> p2.recoverWith(p -> p1, null));
		assertThatNullPointerException()
				.isThrownBy(() -> p2.recoverWith(null, TimeoutException.class));
	}

	@Test
	public void testFallbackToNoFailure() throws Exception {
		final Number value1 = Integer.valueOf(42);
		final Long value2 = Long.valueOf(43);
		final Promise<Number> p1 = factory.resolved(value1);
		final Promise<Long> p2 = factory.resolved(value2);
		final Promise<Number> p3 = p1.fallbackTo(p2);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
	}

	@Test
	public void fallback_to_type_no_failure() throws Exception {
		final Number value1 = Integer.valueOf(42);
		final Long value2 = Long.valueOf(43);
		final Promise<Number> p1 = factory.resolved(value1);
		final Promise<Long> p2 = factory.resolved(value2);
		final Promise<Number> p3 = p1.fallbackTo(p2, TimeoutException.class);
		assertThat(p3).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value1);
	}

	@Test
	public void testFallbackToFailure() throws Exception {
		final Error failure1 = new Error("fail1");
		final Error failure2 = new Error("fail2");
		final Long value3 = Long.valueOf(43);
		final Promise<Number> p1 = factory.failed(failure1);
		final Promise<Number> p2 = factory.failed(failure2);
		final Promise<Long> p3 = factory.resolved(value3);
		final Promise<Number> p4 = p1.fallbackTo(p2).fallbackTo(p3);
		assertThat(p4).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value3);
	}

	@Test
	public void fallback_to_type_failure_match() throws Exception {
		final Error failure1 = new Error("fail1");
		final Error failure2 = new Error("fail2");
		final Long value3 = Long.valueOf(43);
		final Promise<Number> p1 = factory.failed(failure1);
		final Promise<Number> p2 = factory.failed(failure2);
		final Promise<Long> p3 = factory.resolved(value3);
		final Promise<Number> p4 = p1.fallbackTo(p2, Error.class)
				.fallbackTo(p3);
		assertThat(p4).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value3);
	}

	@Test
	public void fallback_to_type_failure_no_match() throws Exception {
		final Error failure1 = new Error("fail1");
		final Long value2 = Long.valueOf(42);
		final Long value3 = Long.valueOf(43);
		final Promise<Number> p1 = factory.failed(failure1);
		final Promise<Number> p2 = factory.resolved(value2);
		final Promise<Long> p3 = factory.resolved(value3);
		final Promise<Number> p4 = p1.fallbackTo(p2, TimeoutException.class)
				.fallbackTo(p3);
		assertThat(p4).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value3);
	}

	@Test
	public void testFallbackToFailureException() throws Exception {
		final Error failure1 = new Error("fail1");
		final Error failure2 = new Error("fail2");
		final Error failure3 = new Error("fail3");
		final Promise<Number> p1 = factory.failed(failure1);
		final Promise<Number> p2 = factory.failed(failure2);
		final Promise<Long> p3 = factory.failed(failure3);
		final Promise<Number> p4 = p1.fallbackTo(p2).fallbackTo(p3);
		assertThat(p4).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure1);
		assertThat(catchThrowableOfType(() -> p4.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure1);
	}

	@Test
	public void testFallbackToNull() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = factory.resolved(value1);
		assertThatNullPointerException().isThrownBy(() -> p1.fallbackTo(null));
		assertThatNullPointerException()
				.isThrownBy(() -> p1.fallbackTo(p1, null));
		assertThatNullPointerException()
				.isThrownBy(() -> p1.fallbackTo(null, TimeoutException.class));

		Error failure2 = new Error("fail2");
		Promise<String> p2 = factory.failed(failure2);
		assertThatNullPointerException().isThrownBy(() -> p2.fallbackTo(null));
		assertThatNullPointerException()
				.isThrownBy(() -> p2.fallbackTo(p1, null));
		assertThatNullPointerException()
				.isThrownBy(() -> p2.fallbackTo(null, TimeoutException.class));
	}

	@Test
	public void testTryResolve() throws Exception {
		final Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		final AtomicBoolean fail1 = new AtomicBoolean(false);
		final AtomicBoolean fail2 = new AtomicBoolean(false);
		p.onResolve(
				() -> d.resolveWith(factory.resolved("onResolve")).then(r -> {
					fail1.set(true);
					return null;
				}, f -> fail1.set(
						!(f.getFailure() instanceof IllegalStateException))));
		p.onResolve(() -> {
			try {
				d.resolve("onResolve");
			} catch (IllegalStateException e) {
				return;
			}
			fail2.set(true);
		});
		assertThat(d.resolveWith(factory.resolved("first")))
				.resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.isSuccessful();
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue("first");
		assertThat(fail1).isFalse();
		assertThat(fail2).isFalse();
		assertThat(d.resolveWith(factory.resolved("second")))
				.resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(IllegalStateException.class);
		assertThatIllegalStateException().isThrownBy(() -> d.resolve("second"));
		assertThat(d.resolveWith(factory.failed(new Exception("second"))))
				.resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(IllegalStateException.class);
		assertThatIllegalStateException()
				.isThrownBy(() -> d.fail(new Exception("second")));
	}

	@Test
	public void testTryFail() throws Exception {
		final Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		final AtomicBoolean fail1 = new AtomicBoolean(false);
		final AtomicBoolean fail2 = new AtomicBoolean(false);
		assertThat(p).isNotDone();
		p.onResolve(
				() -> d.resolveWith(factory.failed(new Exception("onResolve")))
						.then(r -> {
							fail1.set(true);
							return null;
						}, f -> fail1.set(!(f
								.getFailure() instanceof IllegalStateException))));
		p.onResolve(() -> {
			try {
				d.fail(new Exception("onResolve"));
			} catch (IllegalStateException e) {
				return;
			}
			fail2.set(true);
		});
		assertThat(d.resolveWith(factory.failed(new Exception("first"))))
				.resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.isSuccessful();
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.hasMessage("first");
		assertThat(fail1).isFalse();
		assertThat(fail2).isFalse();
		assertThat(d.resolveWith(factory.failed(new Exception("second"))))
				.resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(IllegalStateException.class);
		assertThatIllegalStateException()
				.isThrownBy(() -> d.fail(new Exception("second")));
		assertThat(d.resolveWith(factory.resolved("second")))
				.resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(IllegalStateException.class);
		assertThatIllegalStateException().isThrownBy(() -> d.resolve("second"));
	}

	@Test
	public void testTimeoutWithTimeout() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		Promise<String> t = p.timeout(TimeUnit.SECONDS.toMillis(WAIT_TIME));
		assertThat(t).resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(TimeoutException.class);
		assertThatThrownBy(() -> t.getValue())
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseInstanceOf(TimeoutException.class);
		assertThat(p).isNotDone();
	}

	@Test
	public void testTimeoutWithSuccess1() throws Exception {
		final Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		Promise<String> t = p.timeout(TimeUnit.SECONDS.toMillis(WAIT_TIME));
		d.resolve("no timeout");
		assertThat(t).resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasValue("no timeout");
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(t.getValue());
	}

	@Test
	public void testTimeoutWithSuccess2() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		d.resolve("no timeout");
		Promise<String> t = p.timeout(TimeUnit.SECONDS.toMillis(WAIT_TIME));
		assertThat(t).resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasValue("no timeout");
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(t.getValue());
	}

	@Test
	public void testTimeoutWithFailure1() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		Promise<String> t = p.timeout(TimeUnit.SECONDS.toMillis(WAIT_TIME));
		d.fail(new Exception("no timeout"));
		assertThat(t).resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.hasMessage("no timeout");
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(t.getFailure());
	}

	@Test
	public void testTimeoutWithFailure2() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		d.fail(new Exception("no timeout"));
		Promise<String> t = p.timeout(TimeUnit.SECONDS.toMillis(WAIT_TIME));
		assertThat(t).resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.hasMessage("no timeout");
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(t.getFailure());
	}

	@Test
	public void testTimeoutWithNegativeTimeoutResolved() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		d.resolve("no timeout");
		Promise<String> t = p.timeout(TimeUnit.SECONDS.toMillis(-1));
		assertThat(t).resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasValue("no timeout");
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(t.getValue());
	}

	@Test
	public void testTimeoutWithNegativeTimeoutUnresolved() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		Promise<String> t = p.timeout(TimeUnit.SECONDS.toMillis(-1));
		assertThat(t).resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(TimeoutException.class);
		assertThatThrownBy(() -> t.getValue())
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseInstanceOf(TimeoutException.class);
		assertThat(p).isNotDone();
	}

	@Test
	public void testTimeoutWithZeroTimeoutResolved() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		d.resolve("no timeout");
		Promise<String> t = p.timeout(0);
		assertThat(t).resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasValue("no timeout");
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(t.getValue());
	}

	@Test
	public void testTimeoutWithZeroTimeoutUnresolved() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		Promise<String> t = p.timeout(0);
		assertThat(t).resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isInstanceOf(TimeoutException.class);
		assertThatThrownBy(() -> t.getValue())
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseInstanceOf(TimeoutException.class);
		assertThat(p).isNotDone();
	}

	@Test
	public void testDelayWithSuccess1() throws Exception {
		final Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		Promise<String> t = p.delay(TimeUnit.SECONDS.toMillis(WAIT_TIME));
		d.resolve("no delay");
		assertThat(t).isNotDone()
				.resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasValue("no delay");
		assertThat(p).hasSameValue(t.getValue());
	}

	@Test
	public void testDelayWithSuccess2() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		d.resolve("no delay");
		Promise<String> t = p.delay(TimeUnit.SECONDS.toMillis(WAIT_TIME));
		assertThat(t).isNotDone()
				.resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasValue("no delay");
		assertThat(p).hasSameValue(t.getValue());
	}

	@Test
	public void testDelayWithFailure1() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		Promise<String> t = p.delay(TimeUnit.SECONDS.toMillis(WAIT_TIME));
		d.fail(new Exception("no delay"));
		assertThat(t).isNotDone()
				.resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.hasMessage("no delay");
		assertThat(p).hasFailedWithThrowableThat().isSameAs(t.getFailure());
	}

	@Test
	public void testDelayWithFailure2() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		d.fail(new Exception("no delay"));
		Promise<String> t = p.delay(TimeUnit.SECONDS.toMillis(WAIT_TIME));
		assertThat(t).isNotDone()
				.resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.hasMessage("no delay");
		assertThat(p).hasFailedWithThrowableThat().isSameAs(t.getFailure());
	}

	@Test
	public void testDelayWithDelayUnresolved() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		Promise<String> t = p.delay(TimeUnit.SECONDS.toMillis(WAIT_TIME));
		assertThat(t).doesNotResolveWithin(WAIT_TIME * 2, TimeUnit.SECONDS);
		assertThat(p).isNotDone();
	}

	@Test
	public void testDelayWithNegativeDelayResolved() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		d.resolve("no delay");
		Promise<String> t = p.delay(TimeUnit.SECONDS.toMillis(-1));
		assertThat(t).resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasValue("no delay");
		assertThat(p).hasSameValue(t.getValue());
	}

	@Test
	public void testDelayWithNegativeDelayUnresolved() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		Promise<String> t = p.delay(TimeUnit.SECONDS.toMillis(-1));
		assertThat(t).doesNotResolveWithin(WAIT_TIME * 2, TimeUnit.SECONDS);
		assertThat(p).isNotDone();
	}

	@Test
	public void testDelayWithZeroDelayResolved() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		d.resolve("no delay");
		Promise<String> t = p.delay(0);
		assertThat(t).resolvesWithin(WAIT_TIME * 2, TimeUnit.SECONDS)
				.hasValue("no delay");
		assertThat(p).hasSameValue(t.getValue());
	}

	@Test
	public void testDelayWithZeroDelayUnresolved() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p = d.getPromise();
		Promise<String> t = p.delay(0);
		assertThat(t).doesNotResolveWithin(WAIT_TIME * 2, TimeUnit.SECONDS);
		assertThat(p).isNotDone();
	}

	/**
	 * Test the thenAccept functionality.
	 */
	@Test
	public void testThenAcceptSuccess() throws Exception {
		final Deferred<String> d = factory.deferred();
		final AtomicReference<String> result = new AtomicReference<String>();
		final CountDownLatch latch = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		Promise<String> p = d.getPromise();
		Promise<String> p2 = p.thenAccept(s -> {
			assertThat(latch2.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
			result.set(s);
			latch.countDown();
		});
		p2.onResolve(latch2::countDown);
		assertThat(latch.await(2 * WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p).isNotDone();
		assertThat(p2).isNotDone();
		String value = new String("value");
		d.resolve(value);
		assertThat(latch.await(2 * WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p).hasSameValue(value);
		assertThat(latch2.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p2).hasSameValue(value);
		assertThat(result.get()).isSameAs(value);
	}

	@Test
	public void testThenAcceptFailure() throws Exception {
		Deferred<String> d = factory.deferred();
		Promise<String> p1 = d.getPromise();
		final CountDownLatch latch = new CountDownLatch(1);
		Promise<String> p2 = p1.thenAccept(s -> latch.countDown());

		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p1).isNotDone();
		assertThat(p2).isNotDone();

		final Exception e = new Exception("failure");
		d.fail(e);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p1).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(e);
		assertThat(catchThrowableOfType(() -> p1.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(e);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(e);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(e);
	}

	@Test
	public void testThenAcceptThrowFailure() throws Exception {
		final Deferred<String> d = factory.deferred();
		final AtomicReference<String> result = new AtomicReference<String>();
		final Exception failure = new Exception("failure");
		final CountDownLatch latch = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		Promise<String> p = d.getPromise();
		Promise<String> p2 = p.thenAccept(s -> {
			assertThat(latch2.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
			result.set(s);
			latch.countDown();
			throw failure;
		});
		p2.onResolve(latch2::countDown);
		assertThat(latch.await(2 * WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p).isNotDone();
		assertThat(p2).isNotDone();
		String value = new String("value");
		d.resolve(value);
		assertThat(latch.await(2 * WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p).hasSameValue(value);
		assertThat(latch2.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p2).hasFailedWithThrowableThat().isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p2.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(result.get()).isSameAs(value);
	}

	@Test
	public void testThenAcceptNull() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = factory.resolved(value1);
		assertThatNullPointerException()
				.isThrownBy(() -> p1.thenAccept((Consumer<String>) null));

		Error failure2 = new Error("fail2");
		Promise<String> p2 = factory.failed(failure2);
		assertThatNullPointerException()
				.isThrownBy(() -> p2.thenAccept((Consumer<String>) null));
	}

	@Test
	public void testOnSuccessSuccess() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		final Deferred<String> d = factory.deferred();
		final AtomicReference<String> result = new AtomicReference<String>();
		final Promise<String> p = d.getPromise().onSuccess(s -> {
			result.set(s);
			latch.countDown();
		});
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p).isNotDone();
		String value = new String("value");
		d.resolve(value);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p).hasSameValue(value);
		assertThat(result.get()).isSameAs(value);
	}

	@Test
	public void testOnFailureSuccess() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		final Deferred<String> d = factory.deferred();
		final Promise<String> p = d.getPromise()
				.onFailure(t -> latch.countDown());
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p).isNotDone();
		String value = new String("value");
		d.resolve(value);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p).hasSameValue(value);
	}

	@Test
	public void on_failure_type_no_failure() throws Exception {
		final CountDownLatch latchAll = new CountDownLatch(1);
		final CountDownLatch latchType = new CountDownLatch(1);
		final Deferred<String> d = factory.deferred();
		final Promise<String> p = d.getPromise()
				.onFailure(t -> latchType.countDown(), TimeoutException.class)
				.onFailure(t -> latchAll.countDown());
		assertThat(latchAll.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(latchType.getCount()).isGreaterThan(0L);
		assertThat(p).isNotDone();
		String value = new String("value");
		d.resolve(value);
		assertThat(latchAll.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(latchType.getCount()).isGreaterThan(0L);
		assertThat(p).hasSameValue(value);
	}

	@Test
	public void testOnSuccessFailure() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		final Deferred<String> d = factory.deferred();
		final Promise<String> p = d.getPromise()
				.onSuccess(s -> latch.countDown());
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p).isNotDone();
		Throwable failure = new RuntimeException();
		d.fail(failure);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p).hasFailedWithThrowableThat().isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
	}

	@Test
	public void testOnFailureFailure() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		final Deferred<String> d = factory.deferred();
		final AtomicReference<Throwable> result = new AtomicReference<>();
		final Throwable failure = new RuntimeException();
		final Promise<String> p = d.getPromise().onFailure(t -> {
			result.set(t);
			latch.countDown();
		});

		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(p).isNotDone();
		d.fail(failure);
		assertThat(latch.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p).hasFailedWithThrowableThat().isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(result.get()).isSameAs(failure);
	}

	public interface LatchException {
		void countDown();
	}

	public static class TestException extends RuntimeException
			implements LatchException {
		private static final long serialVersionUID = 1L;
		private final CountDownLatch latch;
		public TestException(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public void countDown() {
			latch.countDown();
		}
	}

	@Test
	public void on_failure_type_failure_match() throws Exception {
		final CountDownLatch latchAll = new CountDownLatch(1);
		final CountDownLatch latchType = new CountDownLatch(1);
		final Deferred<String> d = factory.deferred();
		final AtomicReference<Throwable> result = new AtomicReference<>();
		final Throwable failure = new TestException(latchType);
		final Promise<String> p = d.getPromise()
				.onFailure(LatchException::countDown, LatchException.class)
				.onFailure(t -> {
			result.set(t);
			latchAll.countDown();
		});

		assertThat(latchAll.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(latchType.getCount()).isGreaterThan(0L);
		assertThat(p).isNotDone();
		d.fail(failure);
		assertThat(latchAll.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(latchType.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(p).hasFailedWithThrowableThat().isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(result.get()).isSameAs(failure);
	}

	@Test
	public void on_failure_type_failure_no_match() throws Exception {
		final CountDownLatch latchAll = new CountDownLatch(1);
		final CountDownLatch latchType = new CountDownLatch(1);
		final Deferred<String> d = factory.deferred();
		final AtomicReference<Throwable> result = new AtomicReference<>();
		final Throwable failure = new TestException(latchType);
		final Promise<String> p = d.getPromise()
				.onFailure(t -> latchType.countDown(), TimeoutException.class)
				.onFailure(t -> {
					result.set(t);
					latchAll.countDown();
				});

		assertThat(latchAll.await(WAIT_TIME, TimeUnit.SECONDS)).isFalse();
		assertThat(latchType.getCount()).isGreaterThan(0L);
		assertThat(p).isNotDone();
		d.fail(failure);
		assertThat(latchAll.await(WAIT_TIME, TimeUnit.SECONDS)).isTrue();
		assertThat(latchType.getCount()).isGreaterThan(0L);
		assertThat(p).hasFailedWithThrowableThat().isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
		assertThat(result.get()).isSameAs(failure);
	}

	@Test
	public void testOnResolveNull1() throws Exception {
		Deferred<String> d1 = factory.deferred();
		Promise<String> p1 = d1.getPromise();
		assertThatNullPointerException()
				.isThrownBy(() -> p1.onResolve((Runnable) null));
	}

	@Test
	public void testOnResolveNull2() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = factory.resolved(value1);
		assertThatNullPointerException()
				.isThrownBy(() -> p1.onResolve((Runnable) null));
	}

	@Test
	public void testOnSuccessNull() throws Exception {
		String value1 = new String("value");
		Promise<String> p1 = factory.resolved(value1);
		assertThatNullPointerException()
				.isThrownBy(() -> p1.onSuccess((Consumer<String>) null));

		Error failure2 = new Error("fail2");
		Promise<String> p2 = factory.failed(failure2);
		assertThatNullPointerException()
				.isThrownBy(() -> p2.onSuccess((Consumer<String>) null));
	}

	@Test
	public void testOnFailureNull() throws Exception {
		String value1 = new String("value");
		Consumer<Object> consumerNull = null;
		Consumer<Object> consumerEmpty = t -> {
		};
		Class<Throwable> classNull = null;
		Promise<String> p1 = factory.resolved(value1);
		assertThatNullPointerException()
				.isThrownBy(() -> p1.onFailure(consumerNull));
		assertThatNullPointerException()
				.isThrownBy(() -> p1.onFailure(consumerEmpty, classNull));
		assertThatNullPointerException().isThrownBy(
				() -> p1.onFailure(consumerNull, TimeoutException.class));

		Error failure2 = new Error("fail2");
		Promise<String> p2 = factory.failed(failure2);
		assertThatNullPointerException()
				.isThrownBy(() -> p2.onFailure(consumerNull));
		assertThatNullPointerException()
				.isThrownBy(() -> p2.onFailure(consumerEmpty, classNull));
		assertThatNullPointerException().isThrownBy(() -> p2
				.onFailure(consumerNull, TimeoutException.class));
	}

	@Test
	public void testSubmitSuccess() throws Exception {
		Promise<String> p = factory.submit(() -> testMethodName);
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasValue(testMethodName);
	}

	@Test
	public void testSubmitFailure() throws Exception {
		final Exception failure = new RuntimeException();
		Promise<String> p = factory.submit(() -> {
			throw failure;
		});
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
	}

	@Test
	public void testSubmitExecutorFailure() throws Exception {
		final RuntimeException failure = new RuntimeException();
		final PromiseFactory factory = new PromiseFactory(command -> {
			throw failure;
		}, scheduledExecutor);
		Promise<String> p = factory.submit(() -> testMethodName);
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
		assertThat(catchThrowableOfType(() -> p.getValue(),
				InvocationTargetException.class).getCause()).isSameAs(failure);
	}

	@Test
	public void testToCompletionStageSuccess1() throws Exception {
		final String value = new String("success");
		final Deferred<String> d = factory.deferred();
		final Promise<String> p = d.getPromise();
		final CompletionStage<String> c = p.toCompletionStage();
		assertThat(c).isNotCompleted();
		d.resolve(value);
		assertThat(c).succeedsWithin(WAIT_TIME, TimeUnit.SECONDS)
				.isSameAs(value);
	}

	@Test
	public void testToCompletionStageSuccess2() throws Exception {
		final String value = new String("success");
		final Promise<String> p = factory.resolved(value);
		final CompletionStage<String> c = p.toCompletionStage();
		assertThat(c).succeedsWithin(WAIT_TIME, TimeUnit.SECONDS)
				.isSameAs(value);
	}

	@Test
	public void testToCompletionStageFailed1() throws Exception {
		final Throwable failure = new Exception("failed");
		final Deferred<String> d = factory.deferred();
		final Promise<String> p = d.getPromise();
		final CompletionStage<String> c = p.toCompletionStage();
		assertThat(c).isNotCompleted();
		d.fail(failure);
		assertThat(c).failsWithin(WAIT_TIME, TimeUnit.SECONDS)
				.withThrowableOfType(ExecutionException.class)
				.havingCause()
				.isSameAs(failure);
	}

	@Test
	public void testToCompletionStageFailed2() throws Exception {
		final Throwable failure = new Exception("failed");
		final Promise<String> p = factory.failed(failure);
		final CompletionStage<String> c = p.toCompletionStage();
		assertThat(c).failsWithin(WAIT_TIME, TimeUnit.SECONDS)
				.withThrowableOfType(ExecutionException.class)
				.havingCause()
				.isSameAs(failure);
	}

	@Test
	public void testPromiseFromSuccess1() throws Exception {
		final String value = new String("success");
		final CompletableFuture<String> c = new CompletableFuture<>();
		final Promise<String> p = factory.resolvedWith(c);
		assertThat(c).isNotCompleted();
		assertThat(p).isNotDone();
		c.complete(value);
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value);
	}

	@Test
	public void testPromiseFromSuccess2() throws Exception {
		final String value = new String("success");
		final CompletableFuture<String> c = CompletableFuture
				.completedFuture(value);
		assertThat(c).isCompleted();
		final Promise<String> p = factory.resolvedWith(c);
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value);
	}

	@Test
	public void testPromiseFromFailed1() throws Exception {
		final Throwable failure = new Exception("failed");
		final CompletableFuture<String> c = new CompletableFuture<>();
		final Promise<String> p = factory.resolvedWith(c);
		assertThat(c).isNotCompleted();
		assertThat(p).isNotDone();
		c.completeExceptionally(failure);
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
	}

	@Test
	public void testPromiseFromFailed2() throws Exception {
		final Throwable failure = new Exception("failed");
		final CompletableFuture<String> c = new CompletableFuture<>();
		c.completeExceptionally(failure);
		assertThat(c).isCompletedExceptionally();
		final Promise<String> p = factory.resolvedWith(c);
		assertThat(p).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
	}

	@Test
	public void foreign_promise_success() throws Exception {
		Integer value = Integer.valueOf(42);
		Promise<Integer> p0 = factory.resolved(value);
		@SuppressWarnings("unchecked")
		Promise<Integer> p1 = MockFactory.newMock(Promise.class, p0);
		assertThat(p1).isInstanceOf(Proxy.class);
		Promise<Integer> p2 = factory.resolvedWith(p1);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasSameValue(value);
	}

	@Test
	public void foreign_promise_failure() throws Exception {
		Throwable failure = new RuntimeException();
		Promise<Integer> p0 = factory.failed(failure);
		@SuppressWarnings("unchecked")
		Promise<Integer> p1 = MockFactory.newMock(Promise.class, p0);
		assertThat(p1).isInstanceOf(Proxy.class);
		Promise<Integer> p2 = factory.resolvedWith(p1);
		assertThat(p2).resolvesWithin(WAIT_TIME, TimeUnit.SECONDS)
				.hasFailedWithThrowableThat()
				.isSameAs(failure);
	}

}
