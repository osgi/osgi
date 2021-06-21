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

import static org.assertj.core.api.Assertions.assertThat;
import static org.osgi.test.assertj.promise.PromiseAssert.assertThat;
import static org.osgi.test.cases.pushstream.junit.PushStreamComplianceTest.PROMISE_RESOLVE_DURATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.osgi.test.cases.pushstream.junit.PushStreamComplianceTest.ExtGenerator;
import org.osgi.test.cases.pushstream.junit.PushStreamComplianceTest.ExtGeneratorStatus;
import org.osgi.util.promise.Promise;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamProvider;

/**
 * Section 706.3 The Push Stream
 */
public class PushStreamTerminalOperationTest {
	
	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * forEach register a function to be called back with the data from each
	 * event in the stream
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTerminalOperationForEach() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		AtomicInteger done = new AtomicInteger();
		Promise<Void> p = ps.forEach(i -> {
			done.incrementAndGet();
		});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION);

		assertThat(done).hasValue(5);
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * forEach register a function to be called back with the data from each
	 * event in the stream
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTerminalOperationForEachException() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		RuntimeException re = new RuntimeException("bang");

		Promise<Void> p = ps.forEach(i -> {
			throw re;
		});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasFailedWithThrowableThat()
				.isSameAs(re);
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * forEachEvent registers a PushEventConsumer to be called with each event
	 * in the stream. If negative back pressure is returned then the stream will
	 * be closed
	 */
	@Test
	public void testTerminalOperationForEachEvent() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		AtomicInteger done = new AtomicInteger();
		Promise<Long> p = ps.forEachEvent(i -> {
			if (!i.isTerminal())
				done.incrementAndGet();
			return 5;
		});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION);

		@SuppressWarnings("unused")
		ExtGeneratorStatus status = gen.status();

		assertThat(done).hasValue(5);
		assertThat(gen.fixedBackPressure()).isTrue();
		// what becomes the returned back pressure ?
		// assertEquals(5, status.bp);
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * forEachEvent registers a PushEventConsumer to be called with each event
	 * in the stream. If negative back pressure is returned then the stream will
	 * be closed
	 */
	@Test
	public void testTerminalOperationForEachEventException() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		RuntimeException re = new RuntimeException("bang");

		Promise<Long> p = ps.forEachEvent(i -> {
			throw re;
		});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasFailedWithThrowableThat()
				.isSameAs(re);
	}

	@Test
	public void testTerminalOperationForEachEventException2() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		RuntimeException re = new RuntimeException("bang");

		Promise<Long> p = ps.forEachEvent(i -> {
			if (i.isTerminal())
				throw re;
			return 0;
		});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasFailedWithThrowableThat()
				.isSameAs(re);
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * toArray collects together all the event data in a single array which is
	 * used to resolve the returned promise
	 */
	@Test
	public void testTerminalOperationToArray() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Integer[]> p = ps.toArray(Integer[]::new);

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(0, 1, 2, 3, 4);

		assertThat(gen.fixedBackPressure()).isTrue();
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * reduce uses a Binary Operator function to combine event data into a
	 * single object. The promise is resolved with the final result when the
	 * stream finishes
	 */
	@Test
	public void testTerminalOperationReduce() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Optional<Integer>> p = ps.reduce((a, b) -> {
			return a + b;
		});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(10);

		assertThat(gen.fixedBackPressure()).isTrue();
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * reduce uses a Binary Operator function to combine event data into a
	 * single object. The promise is resolved with the final result when the
	 * stream finishes
	 */
	@Test
	public void testTerminalOperationReduceBis() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<String> p = ps.reduce(
				"", (sum, b) -> {
					return new StringBuilder().append(sum)
							.append(new String(new char[] {
									(char) ('a' + b.intValue())
					}))
							.toString();
				},
		(sum1, sum2) -> {return new StringBuilder().append(sum1).append(sum2).toString();});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.STRING)
				.isEqualTo("abcde");

		assertThat(gen.fixedBackPressure()).isTrue();
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * collect uses the Java Collector API to collect the data from events into
	 * a single Collection, Map, or other type
	 */
	@Test
	public void testTerminalOperationCollect() throws Exception {
		
		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<List<Integer>> p = ps
				.collect(Collector.<Integer, List<Integer>> of(() -> {
					return new ArrayList<Integer>();
				}, (l, i) -> {
					l.add(i);
				}, (l1, l2) -> {
					l1.addAll(l2);
					return l1;
				}));

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.list(Integer.class))
				.containsExactly(0, 1, 2, 3, 4);
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * min uses a Comparator to find the smallest data element in the stream of
	 * data. The promise is resolved with the final result when the stream
	 * finishes
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTerminalOperationMin() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Optional<Integer>> p = ps.min((i1, i2) -> {
			return i1.intValue() == i2.intValue() ? 0
					: (i1.intValue() < i2.intValue() ? -1 : 1);
		});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(0);
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * max uses a Comparator to find the largest data element in the stream of
	 * data. The promise is resolved with the final result when the stream
	 * finishes
	 */
	@Test
	public void testTerminalOperationMax() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Optional<Integer>> p = ps.max((i1, i2) -> {
			return i1.intValue() == i2.intValue() ? 0
					: (i1.intValue() < i2.intValue() ? -1 : 1);
		});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(4);
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * count operation counts the number of events that reach the end of the
	 * stream pipeline
	 */
	@Test
	public void testTerminalOperationCount() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Long> p = ps.count();

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.LONG)
				.isEqualTo(5L);
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * allMatch resolves with false if any event reaches the end of the stream
	 * pipeline does that does not match the predicate. If the stream ends
	 * without any data that doesn't match the predicate then the promise
	 * resolves with true
	 */
	@Test
	public void testTerminalOperationAllMatch() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Boolean> p = ps.allMatch(i -> {
			return i.intValue() % 2 == 0;
		});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.BOOLEAN)
				.isFalse();

		p = ps.allMatch(i -> {
			return i.intValue() > -1;
		});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.BOOLEAN)
				.isTrue();
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * anyMatch resolves with true if any data event reaches the end of the
	 * stream pipeline and matches the supplied predicate. If the stream ends
	 * without any data matching the predicate then the promise resolves with
	 * false
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTerminalOperationAnyMatch() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Boolean> p = ps.anyMatch(i -> {
			return i.intValue() % 2 == 0;
		});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.BOOLEAN)
				.isTrue();
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * Not really specified
	 */
	@Test
	public void testTerminalOperationNoneMatch() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Boolean> p = ps.noneMatch(i -> {
			return i.intValue() % 2 == 0;
		});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.BOOLEAN)
				.isFalse();

		p = ps.noneMatch(i -> {
			return i.intValue() < 0;
		});

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.BOOLEAN)
				.isTrue();
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * findFirst resolves with an Optional representing the data from the first
	 * event that reaches the end of the pipeline. If the stream ends without
	 * any data reaching the end of the pipeline then the promise resolves with
	 * an empty Optional
	 */
	@Test
	public void testTerminalOperationFindFirst() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Optional<Integer>> p = ps.findFirst();

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(0);

		gen = new ExtGenerator(0);
		ps = new PushStreamProvider().createStream(gen);

		p = ps.findFirst();

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.isEmpty();
	}
}
