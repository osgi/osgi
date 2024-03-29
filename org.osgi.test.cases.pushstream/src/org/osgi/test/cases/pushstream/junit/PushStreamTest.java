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
package org.osgi.test.cases.pushstream.junit;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.osgi.test.assertj.promise.PromiseAssert.assertThat;
import static org.osgi.test.cases.pushstream.junit.PushStreamComplianceTest.PROMISE_RESOLVE_DURATION;
import static org.osgi.util.pushstream.PushbackPolicyOption.LINEAR;
import static org.osgi.util.pushstream.QueuePolicyOption.FAIL;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.util.promise.Promise;
import org.osgi.util.pushstream.PushEvent;
import org.osgi.util.pushstream.PushEventConsumer;
import org.osgi.util.pushstream.PushEventSource;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamProvider;

public class PushStreamTest {

	static class Generator implements PushEventSource<Integer> {
		int count = 10;
		public Generator(int i) {
			this.count=i;
		}

		public Generator() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public Closeable open(PushEventConsumer< ? super Integer> l) {
			Thread t = new Thread("generator") {
				@Override
				public void run() {
					boolean closed=false;
					try {
						try {
							for (int i = 0; i < count && !Thread.currentThread().isInterrupted(); i++) {
								long r = l.accept(PushEvent.data(i));
								if ( r < 0 ) {
									return;
								}
								
								if ( r > 0)
									Thread.sleep(r);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							l.accept(PushEvent.error(e));
							closed = true;
						} finally {
							if ( !closed)
								l.accept(PushEvent.close());
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			t.start();
			return () -> {
				t.interrupt();
			};
		}

	}
	
	PushStreamProvider impl;
	
	@BeforeEach
	public void setUp() {
		impl = new PushStreamProvider();
	}
	
	private <T> PushStream<T> withLessBackPressure(PushEventSource<T> source) {
		return impl.buildStream(source)
				.withPushbackPolicy(LINEAR, 20)
				.withQueuePolicy(FAIL)
				.build();
	}
	
	@Test
	public void testSimple() throws Exception {
		doTestSimple(impl.createStream(new Generator()));
	}

	@Test
	public void testSimpleWithLessBackPressure() throws Exception {
		doTestSimple(withLessBackPressure(new Generator()));
	}

	private void doTestSimple(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		assertThat(es.filter((x) -> (x & 1) == 0).count())
				.resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.LONG)
				.isEqualTo(5L);
		
		es = impl.createStream(new Generator());
		assertThat(es.max(Integer::compare))
				.resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(9);
		
		es = impl.createStream(new Generator());
		assertThat(es.findFirst()).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(0);
		
		es = impl.createStream(new Generator());
		assertThat(es.reduce(0, (a, b) -> a + b))
				.resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.INTEGER)
				.isEqualTo(45);
	}
	
	@Test
	public void testFork() throws Exception {
		doTestFork(impl.createStream(new Generator()));
	}

	@Test
	public void testForkWithLessBackPressure() throws Exception {
		doTestFork(withLessBackPressure(new Generator()));
	}

	private void doTestFork(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		ExecutorService e = Executors.newCachedThreadPool();
		try {
			assertThat(es.fork(4, 0, e).reduce(0, (a, b) -> a + b))
					.resolvesWithin(PROMISE_RESOLVE_DURATION)
					.hasValueThat(InstanceOfAssertFactories.INTEGER)
					.isEqualTo(45);
		} finally {
			e.shutdown();
		}
	}
	
	@Test
	public void testCoalesce() throws Exception {
		doTestCoalesce(impl.createStream(new Generator(50)));
	}

	@Test
	public void testCoalesceWithLessBackPressure() throws Exception {
		doTestCoalesce(withLessBackPressure(new Generator(50)));
	}

	private void doTestCoalesce(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		AtomicInteger sum = new AtomicInteger();
		AtomicInteger counter = new AtomicInteger();
		
		Promise<List<Integer>> list = es.sequential().coalesce((element) -> {
			int s = sum.accumulateAndGet(element.intValue(), (a, b) -> a + b);
			if ( (counter.incrementAndGet() % 3) == 0) {
				sum.set(0);
				return Optional.of(Integer.valueOf(s));
			}
			else
				return Optional.empty();
		}).collect(toList());
		assertThat(list).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.list(Integer.class))
				.containsExactly(3, 12, 21, 30, 39, 48, 57, 66, 75, 84,
				93, 102, 111, 120, 129, 138);
	}

	@Test
	public void testCoalesce2() throws Exception {
		doTestCoalesce2(impl.createStream(new Generator(50)));
	}

	@Test
	public void testCoalesce2WithLessBackPressure() throws Exception {
		doTestCoalesce2(withLessBackPressure(new Generator(50)));
	}

	private void doTestCoalesce2(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		Promise<List<Integer>> list = es.coalesce(3, (elements) -> {
			int sum = 0;
			for(Integer i : elements) {
				sum += i.intValue();
			}
			return Integer.valueOf(sum);
		}).collect(toList());
		
		assertThat(list).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.list(Integer.class))
				.containsExactly(3, 12, 21, 30, 39, 48, 57, 66, 75, 84,
				93, 102, 111, 120, 129, 138, 97);
	}

	@Test
	public void testCoalesce3() throws Exception {
		doTestCoalesce3(impl.createStream(new Generator(50)));
	}

	@Test
	public void testCoalesce3WithLessBackPressure() throws Exception {
		doTestCoalesce3(withLessBackPressure(new Generator(50)));
	}

	private void doTestCoalesce3(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		Promise<List<Integer>> list = es.sequential()
				.coalesce(3,
				(elements) -> elements.stream().reduce(0, (a,b) -> a + b))
				.collect(toList());
		
		assertThat(list).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.list(Integer.class))
				.containsExactly(3, 12, 21, 30, 39, 48, 57, 66, 75, 84,
				93, 102, 111, 120, 129, 138, 97);
	}

	@Test
	public void testWindow() throws Exception {
		doTestWindow(impl.createStream(new Generator(50)));
	}

	@Test
	public void testWindowWithLessBackPressure() throws Exception {
		doTestWindow(withLessBackPressure(new Generator(50)));
	}

	private void doTestWindow(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		Promise<List<Integer>> list = es
				.window(Duration.ofMillis(200), Collection::size)
				.collect(toList());
		
		assertThat(list).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.list(Integer.class))
				.hasSizeLessThanOrEqualTo(10);
		
		assertThat(list).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat()
				.extracting(l -> l.stream().reduce(0, (a, b) -> a + b),
						InstanceOfAssertFactories.INTEGER)
				.isEqualTo(50);
	}

	@Test
	public void testWindow2() throws Exception {
		doTestWindow2(impl.createStream(new Generator(50)));
	}

	@Test
	public void testWindow2WithLessBackPressure() throws Exception {
		doTestWindow2(withLessBackPressure(new Generator(50)));
	}

	private void doTestWindow2(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		Promise<List<Integer>> list = es.adjustBackPressure(l -> 1)
				.window(() -> Duration.ofMillis(200), () -> 5,
						(t, c) -> c.size())
				.collect(toList());
		
		assertThat(list).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.list(Integer.class))
				.hasSizeBetween(10, 14);
		
		assertThat(list).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat()
				.extracting(l -> l.stream().reduce(0, (a, b) -> a + b),
						InstanceOfAssertFactories.INTEGER)
				.isEqualTo(50);
	}

	@Test
	public void testWindowClosing() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);

		PushEventSource<Integer> pes = pec -> {

			new Thread(() -> {
				int i = 0;
				for (;;) {
					try {
						if (latch.await(0, SECONDS)) {
							break;
						}
						if (pec.accept(PushEvent.data(i++)) < 0) {
							latch.countDown();
						} else {
							Thread.sleep(200);
						}
					} catch (Exception e) {
						latch.countDown();
					}
				}
			}).start();

			return () -> latch.countDown();
		};

		Promise<List<Integer>> counts = impl.createStream(pes)
				.window(() -> ofSeconds(1), () -> 20, (t, l) -> l.size())
				.limit(2)
				.collect(toList());

		assertTrue(latch.await(3, SECONDS));
		assertThat(counts).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.list(Integer.class))
				.hasSize(2);

	}

	@Test
	public void testFindFirstClosing() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);

		PushEventSource<Integer> pes = pec -> {

			new Thread(() -> {
				int i = 0;
				for (;;) {
					try {
						if (latch.await(0, SECONDS)) {
							break;
						}
						if (pec.accept(PushEvent.data(i++)) < 0) {
							latch.countDown();
						} else {
							Thread.sleep(200);
						}
					} catch (Exception e) {
						latch.countDown();
					}
				}
			}).start();

			return () -> latch.countDown();
		};

		Promise<Optional<Integer>> counts = impl.createStream(pes).findFirst();

		assertTrue(latch.await(500, MILLISECONDS));
		assertThat(counts).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(0);
	}

	@Test
	public void testClosePropagatesInBothDirections() throws Exception {
		Semaphore s = new Semaphore(0);

		PushEventSource<Integer> pes = pec -> {

			Thread t = new Thread(() -> {
				try {
					pec.accept(PushEvent.data(1));
					pec.accept(PushEvent.data(2));

					s.release();

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						s.release();
						return;
					}
					pec.accept(PushEvent.close());
				} catch (Exception e) {

				}
			});
			t.start();

			return () -> t.interrupt();
		};

		PushStream<String> midway = impl.buildStream(pes)
				.unbuffered()
				.build()
				.map(Object::toString);

		Promise<String> aggregate = midway.reduce("", String::concat);

		assertTrue(s.tryAcquire(500, MILLISECONDS));
		assertThat(aggregate).isNotDone();

		midway.close();

		assertTrue(s.tryAcquire(500, MILLISECONDS));

		assertThat(aggregate).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.STRING)
				.isEqualTo("12");
	}

	@Test
	public void testClosePropagatesInBothDirectionsThroughABuffer()
			throws Exception {
		Semaphore s = new Semaphore(0);

		PushEventSource<Integer> pes = pec -> {

			Thread t = new Thread(() -> {
				try {
					pec.accept(PushEvent.data(1));
					pec.accept(PushEvent.data(2));

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						s.release();
						return;
					}
					pec.accept(PushEvent.close());
				} catch (Exception e) {

				}
			});
			t.start();

			return () -> t.interrupt();
		};

		PushStream<String> midway = impl.buildStream(pes)
				.unbuffered()
				.build()
				.map(Object::toString)
				.buffer();

		Promise<String> aggregate = midway.limit(2).reduce("", String::concat);

		assertThat(aggregate).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.STRING)
				.isEqualTo("12");
		assertTrue(s.tryAcquire(500, MILLISECONDS));
	}

	@Test
	public void testClosePropagatesBackwardsFromTimeout() throws Exception {
		Semaphore s = new Semaphore(0);

		PushEventSource<Integer> pes = pec -> {

			Thread t = new Thread(() -> {
				try {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						s.release();
						return;
					}
					pec.accept(PushEvent.close());
				} catch (Exception e) {

				}
			});
			t.start();

			return () -> t.interrupt();
		};

		Promise<String> aggregate = impl.buildStream(pes)
				.unbuffered()
				.build()
				.timeout(Duration.ofSeconds(1))
				.map(Object::toString)
				.reduce("", String::concat);

		assertThat(aggregate).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasFailed();
		assertTrue(s.tryAcquire(500, MILLISECONDS));
	}

	@Test
	public void testClosePropagatesBackwardsFromFork() throws Exception {
		Semaphore s = new Semaphore(0);

		PushEventSource<Integer> pes = pec -> {

			Thread t = new Thread(() -> {
				try {

					pec.accept(PushEvent.data(1));
					pec.accept(PushEvent.data(2));

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						s.release();
						return;
					}
					pec.accept(PushEvent.close());
				} catch (Exception e) {

				}
			});
			t.start();

			return () -> t.interrupt();
		};

		Promise<String> aggregate = impl.buildStream(pes)
				.unbuffered()
				.build()
				.fork(1, 100, Executors.newSingleThreadExecutor())
				.limit(2)
				.map(Object::toString)
				.reduce("", String::concat);

		assertThat(aggregate).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.STRING)
				.isEqualTo("12");
		assertTrue(s.tryAcquire(500, MILLISECONDS));

	}

	@Test
	public void testClosePropagatesBackwardsFromWindow() throws Exception {
		Semaphore s = new Semaphore(0);

		PushEventSource<Integer> pes = pec -> {

			Thread t = new Thread(() -> {
				try {

					pec.accept(PushEvent.data(1));
					pec.accept(PushEvent.data(2));

					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						s.release();
						return;
					}
					pec.accept(PushEvent.close());
				} catch (Exception e) {

				}
			});
			t.start();

			return () -> t.interrupt();
		};

		Promise<Optional<String>> aggregate = impl.buildStream(pes)
				.unbuffered()
				.build()
				.window(Duration.ofSeconds(1), c -> c.stream()
						.map(Object::toString)
						.reduce("", String::concat))
				.findFirst();

		assertThat(aggregate).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(String.class))
				.contains("12");
		assertTrue(s.tryAcquire(500, MILLISECONDS));

	}

	@Test
	public void testClosePropagatesBackwardsToBothMergedStreams()
			throws Exception {
		Semaphore s = new Semaphore(0);

		PushEventSource<Integer> pes = pec -> {

			Thread t = new Thread(() -> {
				try {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						s.release();
						return;
					}
					pec.accept(PushEvent.close());
				} catch (Exception e) {

				}
			});
			t.start();

			return () -> t.interrupt();
		};

		PushStream<Integer> a = impl.buildStream(pes).unbuffered().build();
		PushStream<Integer> b = impl.buildStream(pes).unbuffered().build();
		
		PushStream<Integer> merged = a.merge(b);
		
		Promise<Long> totalEvents = merged
			.count();
		
		merged.close();

		assertThat(totalEvents).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.LONG)
				.isEqualTo(0L);
		assertTrue(s.tryAcquire(2, 500, MILLISECONDS));

	}
}
