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
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.osgi.test.assertj.promise.PromiseAssert.assertThat;
import static org.osgi.test.cases.pushstream.junit.PushStreamComplianceTest.PROMISE_RESOLVE_DURATION;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.osgi.test.cases.pushstream.junit.PushStreamComplianceTest.ExtGenerator;
import org.osgi.test.cases.pushstream.junit.PushStreamComplianceTest.ExtGeneratorStatus;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.TimeoutException;
import org.osgi.util.pushstream.PushEvent;
import org.osgi.util.pushstream.PushEvent.EventType;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamProvider;
import org.osgi.util.pushstream.PushbackPolicyOption;
import org.osgi.util.pushstream.QueuePolicyOption;
import org.osgi.util.pushstream.ThresholdPushbackPolicy;


/**
 * Section 706.3 The Push Stream
 */
@SuppressWarnings("unchecked")
public class PushStreamIntermediateOperationTest {

	/**
	 * checks push event source behavior before the operation tests
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTestingTools() throws Exception {

		// push data till the end
		// Simple event passing can be achieved by connecting
		// a Push Event Consumer directly to a Push Event Source.
		ExtGenerator gen = new ExtGenerator(5);
		gen.open(e -> {
			return 0;
		});
		gen.getExecutionThread().join();
		ExtGeneratorStatus status = gen.status();

		// System.out.println(status);

		assertNotNull(status);
		assertTrue(status.failure == null);
		assertThat(status.bp).isEqualTo(0L);
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());

		// close method called
		gen = new ExtGenerator(5);
		Closeable closeable = gen.open(e -> {
			if (e.isTerminal()) {
				return 0;
			}
			return 100;
		});
		closeable.close();
		gen.getExecutionThread().join();
		status = gen.status();

		// System.out.println(status);

		assertNotNull(status);
		assertTrue(status.failure == null);
		assertThat(status.bp).isEqualTo(0L);
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());

		// throw Exception
		String message = "Check error processing";
		gen = new ExtGenerator(5);
		gen.open(e -> {
			if (e.isTerminal())
				return 0;
			if (e.getData().intValue() == 3)
				throw new RuntimeException(message);
			return 0;
		});
		gen.getExecutionThread().join();
		status = gen.status();

		// System.out.println(status);

		assertNotNull(status);
		assertTrue(status.failure != null);
		assertThat(status.bp).isEqualTo(0L);
		assertEquals(PushEvent.EventType.ERROR, status.event.getType());
		assertEquals(message, status.event.getFailure().getMessage());

		// returned negative back pressure
		gen = new ExtGenerator(5);
		closeable = gen.open(e -> {
			if (e.isTerminal()) {
				return 0;
			}
			return (e.getData().intValue() == 3) ? -1 : 0;
		});
		gen.getExecutionThread().join();
		status = gen.status();
		// System.out.println(status);

		assertNotNull(status);
		assertTrue(status.failure == null);
		assertThat(status.bp).isLessThan(0L);
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());

		// returned positive back pressure
		gen = new ExtGenerator(5);
		long start = System.nanoTime();
		closeable = gen.open(e -> {
			if (e.isTerminal()) {
				return 0;
			}
			return 200;
		});
		gen.getExecutionThread().join();
		long end = System.nanoTime();
		status = gen.status();
		// System.out.println(status);

		assertNotNull(status);
		assertTrue(status.failure == null);
		assertThat(status.bp).isEqualTo(200L);
		assertThat(TimeUnit.NANOSECONDS.toMillis(end - start))
				.isGreaterThanOrEqualTo(1000L);
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());

	}

	/**
	 * 706.3.1: Simple Pipelines
	 * <p/>
	 * Once a Push Stream object has had an operation invoked on it then it may
	 * not have any other operations chained to it
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testUnableToChainNewOperationWhileIntermediateOperationRunning()
			throws InterruptedException {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		@SuppressWarnings("unused")
		Promise<Object[]> prm = ps.filter((i) -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return i % 3 == 0;
		}).toArray();

		assertThatIllegalStateException().isThrownBy(() -> ps.map((i) -> {
			return i * 2;
		}).toArray());

		gen.getExecutionThread().join();
	}

	/**
	 * 706.3.1.1.1 : Mapping
	 * <p/>
	 * Mapping is the act of transforming an event from one type into another
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationMapping() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));
		Promise<Character[]> p = ps.map(e -> {
			return (char) ('a' + e);
		}).toArray(Character[]::new);

		assertThat(p)
				.resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(
						InstanceOfAssertFactories.array(Character[].class))
				.containsExactly('a', 'b', 'c', 'd', 'e');
	}

	/**
	 * 706.3.1.1.1 : Mapping
	 * <p/>
	 * If the mapping function throws an Exception then an Error Event is
	 * propagated down the stream to the next pipeline step. The failure in the
	 * Error Event is set to the Exception thrown by the mapping function. The
	 * current pipeline step is also closed, and the close operation is
	 * propagated back upstream to the Event Source by returning negative back
	 * pressure
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationMappingError() throws Exception {
		
		String message = "Check error processing";
		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Object[]> prm = ps.map(e -> {
			if (e == 3) {
				throw new RuntimeException(message);
			}
			return (char) ('a' + e);
				
		}).toArray();

		gen.getExecutionThread().join();

		// the error is propagate down the stream by the way of an error event
		assertThat(prm).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasFailedWithThrowableThat()
				.hasMessage(message);

		// the error is propagate upstream by the way of an negative
		// backpressure
		ExtGeneratorStatus status = gen.status();

		assertNotNull(status);
		assertTrue(status.failure == null);

		// TODO assertTrue(status.bp < 0);
		assertTrue(gen.closeCalled || status.bp < 0);
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());
	}
	
	/**
	 * 706.3.1.1.2 : Flat Mapping
	 * <p/>
	 * Flat Mapping is the act of transforming an event from one type into
	 * multiple events of another type. This may involve taking fields from an
	 * object, or performing some simple processing on it
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationFlatMapping() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		Promise<Character[]> p = ps.flatMap(e -> {
			return new PushStreamProvider().createStream(new ExtGenerator(e));
		}).map(e -> {
			return (char) ('a' + e);
		}).toArray(Character[]::new);
		// expect ('a' + 0), ('a' + 0 ,'a' + 1) ,('a' + 0 ,'a' + 1 , 'a' + 2 ),
		// ('a' + 0 ,'a' + 1 , 'a' + 2, 'a' + 3)
		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(
						InstanceOfAssertFactories.array(Character[].class))
				.containsExactly('a', 'a', 'b', 'a', 'b', 'c', 'a', 'b', 'c',
						'd');
	}

	/**
	 * 706.3.1.1.2 : Flat Mapping
	 * <p/>
	 * If the flat mapping function throws an Exception then an Error Event is
	 * propagated down the stream to the next pipeline step. The failure in the
	 * Error Event is set to the Exception thrown by the mapping function. The
	 * current pipeline step is also closed, and the close operation is
	 * propagated back upstream to the Event Source by returning negative back
	 * pressure
	 */
	@Test
	public void testIntermediateOperationFlatMappingError() throws Exception {

		String message = "Check error processing";
		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Object[]> prm = ps.flatMap(e -> {
			if (e == 3) {
				throw new RuntimeException(message);
			}
			return new PushStreamProvider().createStream(new ExtGenerator(e));
		}).map(e -> {
			return (char) ('a' + e);
		}).toArray();

		gen.getExecutionThread().join();

		// the error is propagate down the stream by the way of an error event
		assertThat(prm).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasFailedWithThrowableThat()
				.hasMessage(message);

		// the error is propagate upstream by the way of an negative
		// backpressure
		ExtGeneratorStatus status = gen.status();

		assertNotNull(status);
		assertTrue(status.failure == null);
		assertTrue(gen.closeCalled || status.bp < 0);
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());
	}

	/**
	 * 706.3.1.1.3 : Filtering
	 * <p/>
	 * Filtering is the act of removing events from the stream based on some
	 * characteristic of the event data.[...]If the filter function returns true
	 * for an event then it will be passed to the next stage of the pipeline. If
	 * the filter function returns false then it will be discarded, and not
	 * passed to the next pipeline stage
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationFiltering() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		Promise<Number[]> p = ps.filter(e -> {
			return e % 2 == 0;
		}).toArray(Number[]::new);

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(
						InstanceOfAssertFactories.array(Number[].class))
				.containsExactly(Integer.valueOf(0), Integer.valueOf(2),
						Integer.valueOf(4));
	}

	/**
	 * 706.3.1.1.3 : Filtering
	 * <p/>
	 * If the filtering function throws an Exception then an Error Event is
	 * propagated down the stream to the next pipeline step. The failure in the
	 * Error Event is set to the Exception thrown by the mapping function. The
	 * current pipeline step is also closed, and the close operation is
	 * propagated back upstream to the Event Source by returning negative back
	 * pressure
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationFilteringError() throws Exception {

		String message = "Check error processing";
		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Object[]> prm = ps.filter(e -> {
			if (e == 3) {
				throw new RuntimeException(message);
			}
			return e % 2 == 0;
		}).toArray();

		gen.getExecutionThread().join();

		// the error is propagate down the stream by the way of an error event
		assertThat(prm).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasFailedWithThrowableThat()
				.hasMessage(message);

		// the error is propagate upstream by the way of an negative
		// backpressure
		ExtGeneratorStatus status = gen.status();

		assertNotNull(status);
		assertTrue(status.failure == null);
		assertTrue(gen.closeCalled || status.bp < 0);
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * distinct registers a variation of filter which drops data from the stream
	 * that has already been seen. Specifically if a data element equals an
	 * element which has previously been seen then it will be dropped. This
	 * stateful operation must remember all data that has been seen
	 */
	@Test
	public void testIntermediateOperationDistinct() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		Promise<Integer[]> p = ps.map(e -> {
			return e % 2;
		}).distinct().toArray(Integer[]::new);

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(0, 1);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * sorted remembers all items in the stream until the stream ends. At this
	 * point the data in the stream will be propagated to the next stage of the
	 * stream, in the order defined by the Comparator
	 */
	@Test
	public void testIntermediateOperationSorted() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(gen);

		// reverse order
		Comparator<Integer> comparator = (e1, e2) -> {
			int compare = e1.compareTo(e2);
			return compare == 0 ? 0 : ((compare < 0) ? 1 : -1);
		};
		Promise<Integer[]> p = ps
				.sorted(comparator)
				.toArray(Integer[]::new);

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(4, 3, 2, 1, 0);

	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * sorted remembers all items in the stream until the stream ends. At this
	 * point the data in the stream will be propagated to the next stage of the
	 * stream, in the order defined by the Comparator
	 */
	@Test
	public void testIntermediateOperationSortedNaturalOrder() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(gen);

		Comparator<Integer> comparator = (e1, e2) -> {
			// reverse order
			int compare = e1.compareTo(e2);
			return compare == 0 ? 0 : ((compare < 0) ? 1 : -1);
		};

		Promise<Integer[]> p = ps.sorted(comparator)
				.sorted()
				.toArray(Integer[]::new);

		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(0, 1, 2, 3, 4);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * limit operation limits the length of the stream to the defined number of
	 * elements. Once that number of elements are received then a close event is
	 * propagated to the next stage of the stream
	 */
	@Test
	public void testIntermediateOperationLimit() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().buildStream(gen)
				.unbuffered()
				.build();

		Promise<Integer[]> p = ps.limit(2l).toArray(Integer[]::new);

		// do not wait for the complete source events processing as we expect
		// a close event when the limit is reached
		// gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(0, 1);
		gen.thread.join();

		assertTrue(gen.closeCalled);
		ExtGeneratorStatus status = gen.status();
		assertNotNull(status);
		assertNull(status.failure);
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());

	}

	/**
	 * 706.3.6 : Time Limited Streams
	 * <p/>
	 * The limit() operation on a Stream can be used to limit the number of
	 * elements that are processed, however on a Push Stream that number of
	 * events may never be reached, even though the stream has not closed. Push
	 * Streams therefore also have a limit method which takes a Duration. This
	 * duration limits the time for which the stream is open, closing it after
	 * the duration has elapsed
	 */
	@Test
	public void testIntermediateOperationLimitWithDuration() throws Exception {

		ExtGenerator gen = new ExtGenerator(10000);
		PushStream<Integer> ps = new PushStreamProvider().buildStream(gen)
				.unbuffered()
				.build();

		@SuppressWarnings("unused")
		Promise<Object[]> p = ps.limit(Duration.ofMillis(5)).toArray();
		gen.getExecutionThread().join();
		// long elapse = (gen.stop - gen.start);
		// System.out.println(elapse + " : " + Arrays.toString(p.getValue()));
		ExtGeneratorStatus status = gen.status();
		assertNotNull(status);
		assertNull(status.failure);
		assertTrue(gen.closeCalled);
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());
	}

	/**
	 * 706.3.6 : Time Limited Streams
	 * <p/>
	 * The timeout operation of a Push Stream can be used to end a stream if no
	 * events are received for the given amount of time. If an event is received
	 * then this resets the timeout counter. The timeout oper Tracks the time
	 * since the last event was received. If no event is received within the
	 * supplied Duration then an error event is propagated to the next stage of
	 * the stream. The exception in the event will be an
	 * org.osgi.util.promise.TimeoutException
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testIntermediateOperationTimeout()
			throws InterruptedException, InvocationTargetException {

		ExtGenerator gen = new ExtGenerator(100);
		PushStream<Integer> ps = new PushStreamProvider().buildStream(gen)
				.unbuffered()
				.build();

		final Throwable[] failure = new Throwable[1];

		ps.timeout(Duration.ofMillis(50)).forEachEvent(e -> {
			switch (e.getType()) {
				case DATA :
					return e.getData().intValue();
				case ERROR :
					failure[0] = e.getFailure();
				case CLOSE :
			}
			return -1;
		});

		gen.getExecutionThread().join();
		ExtGeneratorStatus status = gen.status();
		assertNotNull(status);
		assertNotNull(failure[0]);
		assertEquals(TimeoutException.class, failure[0].getClass());

		// There is always a race between the timeout and the next event
		// because that is the purpose of a timeout! This means the generator
		// may get a call to close (if the timeout propagates back first)
		// *or* we may get a -1 returned from a data event closing the generator
		// Also, the thread safety in the generator may not be 100%, so we may
		// see a close call after stopping. In any event, all we care is that
		// a stop actually is propagated back.
		assertTrue(gen.closeCalled || status.event.getType() == EventType.DATA);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * split registers a set of filter functions to select elements that should
	 * be forwarded downstream. The returned streams correspond to the supplied
	 * filter functions
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationSplitOneFilter() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		PushStream<Integer>[] p = ps.split(e -> {
			return e % 2 == 0;
		});
		assertThat(p).hasSize(1);
		Promise<Integer[]> even = p[0].toArray(Integer[]::new);
		assertThat(even).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(0, 2, 4);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * split registers a set of filter functions to select elements that should
	 * be forwarded downstream. The returned streams correspond to the supplied
	 * filter functions
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationSplitTwoFilters() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		PushStream<Integer>[] p = ps.split(e -> {
			return e % 2 == 0;
		}, e -> {
			return e % 2 == 1;
		});
		
		assertThat(p).hasSize(2);
		
		Promise<Integer[]> even = p[0].toArray(Integer[]::new);
		Promise<Integer[]> odd = p[1].toArray(Integer[]::new);

		assertThat(even).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(0, 2, 4);

		assertThat(odd).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(1, 3);

	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * split registers a set of filter functions to select elements that should
	 * be forwarded downstream. The returned streams correspond to the supplied
	 * filter functions
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationSplitTwoFiltersOverlapping()
			throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		PushStream<Integer>[] p = ps.split(e -> {
			return e % 2 == 1;
		}, e -> {
			return e % 3 == 0;
		});

		assertThat(p).hasSize(2);

		Promise<Integer[]> odd = p[0].toArray(Integer[]::new);
		Promise<Integer[]> modulo3 = p[1].toArray(Integer[]::new);

		assertThat(odd).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(1, 3);

		assertThat(modulo3).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(0, 3);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * split registers a set of filter functions to select elements that should
	 * be forwarded downstream. The returned streams correspond to the supplied
	 * filter functions
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationSplitOneFilterWithoutMatch()
			throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		PushStream<Integer>[] p = ps.split(e -> {
			return e % 2 == 2;
		});

		assertThat(p).hasSize(1);
		Promise<Object[]> empty = p[0].toArray();

		// which of this two behaviors is expected ?
		assertThat(empty).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.ARRAY)
				.isEmpty();
		;
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * skip drops the supplied number of data events from the stream and then
	 * forwards any further data events
	 */
	@Test
	public void testIntermediateOperationSkip() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		Promise<Integer[]> p = ps.skip(3).toArray(Integer[]::new);
		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(3, 4);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * skip drops the supplied number of data events from the stream and then
	 * forwards any further data events
	 */
	@Test
	public void testIntermediateOperationSkipAll() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		Promise<Integer[]> p = ps.skip(5).toArray(Integer[]::new);
		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.isEmpty();
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * skip drops the supplied number of data events from the stream and then
	 * forwards any further data events
	 */
	@Test
	public void testIntermediateOperationSkipNone() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		Promise<Integer[]> p = ps.skip(0).toArray(Integer[]::new);
		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(0, 1, 2, 3, 4);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * fork pushes event processing onto one or more threads in the supplied
	 * Executor returning a fixes back-pressure
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationFork() throws Exception {

		ExtGenerator gen = new ExtGenerator(10);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		ThreadPoolExecutor executor = new ThreadPoolExecutor(0,
				Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>(), r -> {
					return new Thread(r) {
						@Override
						public void run() {
							if (r != null)
								r.run();
						}
					};
				});
		// thread overlapping detection
		AtomicInteger threadCount = new AtomicInteger(0);
		BinaryOperator<Integer> bo = new BinaryOperator<Integer>() {

			private final ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>() {

				@Override
				public Integer initialValue() {
					// if threads overlap the final threadCount will not be zero
					return threadCount.addAndGet(threadCount.get() + 1);
				}

				@Override
				public void remove() {
					threadCount.decrementAndGet();
					super.remove();
				}
			};

			@Override
			public Integer apply(Integer t, Integer u) {
				COUNTER.get();
				Integer result = Integer.valueOf(t.intValue() + u.intValue());
				try {
					Thread.sleep(50);

				} catch (InterruptedException e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				COUNTER.remove();
				return result;
			}
		};
		Promise<Optional<Integer>> p = ps.fork(5, 15, executor).reduce(bo);
		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(45);
		// multi-threading
		assertThat(executor.getLargestPoolSize()).isGreaterThan(1)
				// no blocking threads in fork
				.isLessThanOrEqualTo(5);
		assertThat(gen.fixedBackPressure()).isTrue();
		// thread overlapping in reduce
		assertThat(threadCount).hasPositiveValue();
	}

	/**
	 * 706.3.3 : Forking
	 * <p/>
	 * Forking allows users to specify a maximum number of concurrent downstream
	 * operations.Incoming events will block if this limit has been reached. If
	 * there are blocked threads then the returned back pressure for an event
	 * will be equal to the number of queued threads multiplied by the supplied
	 * timeout value. If there are no blocked threads then the back pressure
	 * will be zero
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationForkWithBlockingThreads()
			throws Exception {

		ExtGenerator gen = new ExtGenerator(10);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		ThreadPoolExecutor executor = new ThreadPoolExecutor(0,
				Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>(), r -> {
					return new Thread(r) {
						@Override
						public void run() {
							if (r != null)
								r.run();
						}
					};
				});
		// thread overlapping detection
		AtomicInteger threadCount = new AtomicInteger(0);
		BinaryOperator<Integer> bo = new BinaryOperator<Integer>() {

			private final ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>() {

				@Override
				public Integer initialValue() {
					// if threads overlap the final threadCount will not be zero
					return threadCount.addAndGet(threadCount.get() + 1);
				}
				@Override
				public void remove() {
					threadCount.decrementAndGet();
					super.remove();
				}
			};

			@Override
			public Integer apply(Integer t, Integer u) {
				COUNTER.get();
				Integer result = Integer.valueOf(t.intValue() + u.intValue());
				try {
					Thread.sleep(250);

				} catch (InterruptedException e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				COUNTER.remove();
				return result;
			}
		};
		Promise<Optional<Integer>> p = ps.fork(5, 15, executor).reduce(bo);
		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(45);
		// if threads have been blocked the back pressure must have
		// been changed during processing
		assertThat(gen.fixedBackPressure()).isFalse();
		// n+1 seems to be OK and n not
		// assertEquals(6, executor.getLargestPoolSize());
		assertThat(executor.getLargestPoolSize()).isBetween(5, 6);
		ExtGeneratorStatus status = gen.status();
		assertThat(status.bp).isGreaterThan(0L);
		// thread overlapping in reduce
		assertThat(threadCount).hasPositiveValue();
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * merge operation merges the stream and another stream into a single
	 * stream. then returned stream will not close until both parent streams are
	 * closed
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationMergeStreams() throws Exception {

		ExtGenerator gen_first = new ExtGenerator(5);
		ExtGenerator gen_second = new ExtGenerator(5);

		// Adding pushback ensures that the streams will interleave
		PushStream<Integer> ps_first = new PushStreamProvider()
				.buildStream(gen_first)
				.withPushbackPolicy(PushbackPolicyOption.FIXED, 100)
				.build();
		PushStream<Integer> ps_second = new PushStreamProvider()
				.buildStream(gen_second)
				.withPushbackPolicy(PushbackPolicyOption.FIXED, 100)
				.build();

		Promise<Integer[]> p = ps_first.merge(ps_second)
				.toArray(Integer[]::new);
		
		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.hasSize(10);
		// Keep the ordering
		LinkedHashSet<Integer> one = new LinkedHashSet<>();
		LinkedHashSet<Integer> two = new LinkedHashSet<>();

		for (Integer o : p.getValue()) {
			if (!one.add(o)) {
				assertTrue(two.add(o),
						"Object " + o + " was present three times");
			}
		}

		// The two sets of events should be in order, even though they were
		// interleaved
		assertThat(one).containsExactly(0, 1, 2, 3, 4);
		assertThat(two).containsExactly(0, 1, 2, 3, 4);

		// The two sets of events should be interleaved as they have a 100 ms
		// delay after each event, giving time for the other stream to run
		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.as("The two streams were processed sequentially")
				.doesNotContainSequence(0, 1, 2, 3, 4, 0, 1, 2, 3, 4);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * merge operation merges the stream and another stream into a single
	 * stream. then returned stream will not close until both parent streams are
	 * closed
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationMergeStreamAndSource()
			throws Exception {

		ExtGenerator gen_first = new ExtGenerator(5);
		ExtGenerator gen_second = new ExtGenerator(5);

		PushStream<Integer> ps_first = new PushStreamProvider()
				.createStream(gen_first);

		Promise<Integer[]> p = ps_first.merge(gen_second).toArray(Integer[]::new);

		gen_first.getExecutionThread().join();
		gen_second.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsOnly(0, 1, 2, 3, 4);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * sequential forces data events to be delivered sequentially to the next
	 * stage of the stream
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testIntermediateOperationSequential() throws Exception {

		ExtGenerator gen = new ExtGenerator(10);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		// same configuration as blocking fork
		ThreadPoolExecutor executor = new ThreadPoolExecutor(0,
				Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>(), r -> {
					return new Thread(r) {
						@Override
						public void run() {
							if (r != null)
								r.run();
						}
					};
				});
		// thread overlapping detection
		AtomicInteger threadCount = new AtomicInteger(0);
		BinaryOperator<Integer> bo = new BinaryOperator<Integer>() {

			private final ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>() {

				@Override
				public Integer initialValue() {
					// if threads overlap the final threadCount will not be zero
					return threadCount.addAndGet(threadCount.get() + 1);
				}
				@Override
				public void remove() {
					threadCount.decrementAndGet();
					super.remove();
				}
			};

			@Override
			public Integer apply(Integer t, Integer u) {
				COUNTER.get();
				Integer result = Integer.valueOf(t.intValue() + u.intValue());
				try {
					Thread.sleep(250);

				} catch (InterruptedException e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				COUNTER.remove();
				return result;
			}
		};
		Promise<Optional<Integer>> p = ps.fork(5, 15, executor)
				.sequential()
				.reduce(bo);
		gen.getExecutionThread().join();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION);
		// no thread overlapping in reduce
		assertThat(threadCount).hasValue(0);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * {@link QueuePolicyOption#DISCARD_OLDEST}
	 * <p/>
	 * Attempt to add the supplied event to the queue. If the queue is unable to
	 * immediatly accept the value then discard the value at the head of the
	 * queue and try again. Repeat this process until the event is enqueued
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationBufferFixedBackPressureDiscardOldestQueue()
			throws Exception {

		// cf PushStreamTest
		ExtGenerator gen = new ExtGenerator(10);
		PushStreamProvider psp = new PushStreamProvider();
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();

		// thread overlapping detection
		AtomicInteger threadCount = new AtomicInteger(0);
		BinaryOperator<Integer> bo = new BinaryOperator<Integer>() {

			private final ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>() {

				@Override
				public Integer initialValue() {
					// if threads overlap the final threadCount will not be zero
					return threadCount.addAndGet(threadCount.get() + 1);
				}

				@Override
				public void remove() {
					threadCount.decrementAndGet();
					super.remove();
				}
			};

			@Override
			public Integer apply(Integer t, Integer u) {
				COUNTER.get();
				Integer result = Integer.valueOf(t.intValue() + u.intValue());
				try {
					Thread.sleep(250);

				} catch (InterruptedException e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				COUNTER.remove();
				return result;
			}
		};
		Promise<Optional<Integer>> p = ps.buildBuffer()
				.withBuffer(new ArrayBlockingQueue<>(3))
				.withPushbackPolicy(PushbackPolicyOption.FIXED, 150)
				.withQueuePolicy(QueuePolicyOption.DISCARD_OLDEST)
				.build()
				.reduce(bo);

		gen.getExecutionThread().join();
		ExtGeneratorStatus status = gen.status();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.get(InstanceOfAssertFactories.INTEGER)
				.isLessThan(45);
		// because we must receive a -1 back pressure value
		assertThat(gen.fixedBackPressure()).isTrue();
		assertThat(status.bp).isEqualTo(150L);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * {@link QueuePolicyOption#FAIL}
	 * <p/>
	 * Attempt to add the supplied event to the queue, throwing an exception if
	 * the queue is full.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationBufferFixedBackPressureFailQueue()
			throws Exception {

		// cf PushStreamTest
		ExtGenerator gen = new ExtGenerator(10);
		PushStreamProvider psp = new PushStreamProvider();
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();

		// thread overlapping detection
		AtomicInteger threadCount = new AtomicInteger(0);
		BinaryOperator<Integer> bo = new BinaryOperator<Integer>() {

			private final ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>() {

				@Override
				public Integer initialValue() {
					// if threads overlap the final threadCount will not be zero
					return threadCount.addAndGet(threadCount.get() + 1);
				}

				@Override
				public void remove() {
					threadCount.decrementAndGet();
					super.remove();
				}
			};

			@Override
			public Integer apply(Integer t, Integer u) {
				COUNTER.get();
				Integer result = Integer.valueOf(t.intValue() + u.intValue());
				try {
					Thread.sleep(250);

				} catch (InterruptedException e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				COUNTER.remove();
				return result;
			}
		};
		Promise<Optional<Integer>> p = ps.buildBuffer()
				.withBuffer(new ArrayBlockingQueue<>(3))
				.withPushbackPolicy(PushbackPolicyOption.FIXED, 150)
				.withQueuePolicy(QueuePolicyOption.FAIL)
				.build()
				.reduce(bo);

		gen.getExecutionThread().join();
		ExtGeneratorStatus status = gen.status();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION).hasFailed();
		// because we must receive a -1 back pressure value
		assertThat(gen.fixedBackPressure()).isFalse();
		assertThat(status.bp).isEqualTo(-1L);
		assertThat(gen.maxBackPressure()).isEqualTo(150L);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * {@link PushBackPolicyOption#FIXED}
	 * <p/>
	 * Returns a fixed amount of back pressure, independent of how full the
	 * buffer is
	 * <p/>
	 * {@link QueuePolicyOption#BLOCK}
	 * <p/>
	 * Attempt to add the supplied event to the queue, blocking until the
	 * enqueue is successful.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationBufferFixedBackPressure()
			throws Exception {

		// cf PushStreamTest
		ExtGenerator gen = new ExtGenerator(10);
		PushStreamProvider psp = new PushStreamProvider();
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();

		// thread overlapping detection
		AtomicInteger threadCount = new AtomicInteger(0);
		BinaryOperator<Integer> bo = new BinaryOperator<Integer>() {

			private final ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>() {

				@Override
				public Integer initialValue() {
					// if threads overlap the final threadCount will not be zero
					return threadCount.addAndGet(threadCount.get() + 1);
				}

				@Override
				public void remove() {
					threadCount.decrementAndGet();
					super.remove();
				}
			};

			@Override
			public Integer apply(Integer t, Integer u) {
				COUNTER.get();
				Integer result = Integer.valueOf(t.intValue() + u.intValue());
				try {
					Thread.sleep(250);

				} catch (InterruptedException e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				COUNTER.remove();
				return result;
			}
		};
		Promise<Optional<Integer>> p = ps.buildBuffer()
				.withBuffer(new ArrayBlockingQueue<>(5))
				.withPushbackPolicy(PushbackPolicyOption.FIXED, 150)
				.withQueuePolicy(QueuePolicyOption.BLOCK)
				.build()
				.reduce(bo);

		gen.getExecutionThread().join();
		ExtGeneratorStatus status = gen.status();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(45);

		assertThat(gen.fixedBackPressure()).isTrue();
		assertThat(status.bp).isEqualTo(150L);

	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * {@link PushBackPolicyOption#LINEAR}
	 * <p/>
	 * Returns zero back pressure when the buffer is empty, then it returns a
	 * linearly increasing amount of back pressure based on how full the buffer
	 * is. The maximum value will be returned when the buffer is full. returns
	 * to zero
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationBufferLinearBackPressure()
			throws Exception {

		// cf PushStreamTest
		ExtGenerator gen = new ExtGenerator(10);
		PushStreamProvider psp = new PushStreamProvider();
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();

		// thread overlapping detection
		AtomicInteger threadCount = new AtomicInteger(0);
		BinaryOperator<Integer> bo = new BinaryOperator<Integer>() {

			private final ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>() {

				@Override
				public Integer initialValue() {
					// if threads overlap the final threadCount will not be zero
					return threadCount.addAndGet(threadCount.get() + 1);
				}

				@Override
				public void remove() {
					threadCount.decrementAndGet();
					super.remove();
				}
			};

			@Override
			public Integer apply(Integer t, Integer u) {
				COUNTER.get();
				Integer result = Integer.valueOf(t.intValue() + u.intValue());
				try {
					Thread.sleep(250);

				} catch (InterruptedException e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				COUNTER.remove();
				return result;
			}
		};
		Promise<Optional<Integer>> p = ps.buildBuffer()
				.withBuffer(new ArrayBlockingQueue<>(3))
				.withPushbackPolicy(PushbackPolicyOption.LINEAR, 250)
				.withQueuePolicy(QueuePolicyOption.BLOCK)
				.build()
				.reduce(bo);

		gen.getExecutionThread().join();
		@SuppressWarnings("unused")
		ExtGeneratorStatus status = gen.status();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(45);

		assertThat(gen.fixedBackPressure()).isFalse();
		assertThat(gen.maxBackPressure()).isEqualTo(250L);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * {@link PushBackPolicyOption#ON_FULL_FIXED}
	 * <p/>
	 * Returns zero back pressure until the buffer is full, then it returns a
	 * fixed value
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationBufferOnFullFixedBackPressure()
			throws Exception {

		// cf PushStreamTest
		ExtGenerator gen = new ExtGenerator(10);
		PushStreamProvider psp = new PushStreamProvider();
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();

		// thread overlapping detection
		AtomicInteger threadCount = new AtomicInteger(0);
		BinaryOperator<Integer> bo = new BinaryOperator<Integer>() {

			private final ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>() {

				@Override
				public Integer initialValue() {
					// if threads overlap the final threadCount will not be zero
					return threadCount.addAndGet(threadCount.get() + 1);
				}

				@Override
				public void remove() {
					threadCount.decrementAndGet();
					super.remove();
				}
			};

			@Override
			public Integer apply(Integer t, Integer u) {
				COUNTER.get();
				Integer result = Integer.valueOf(t.intValue() + u.intValue());
				try {
					Thread.sleep(250);

				} catch (InterruptedException e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				COUNTER.remove();
				return result;
			}
		};
		Promise<Optional<Integer>> p = ps.buildBuffer()
				.withBuffer(new ArrayBlockingQueue<>(3))
				.withPushbackPolicy(PushbackPolicyOption.ON_FULL_FIXED, 250)
				.withQueuePolicy(QueuePolicyOption.BLOCK)
				.build()
				.reduce(bo);

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(45);

		assertThat(gen.fixedBackPressure()).isFalse();
		assertThat(gen.maxBackPressure()).isEqualTo(250L);
		assertThat(gen.minBackPressure()).isEqualTo(0L);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * {@link PushBackPolicyOption#ON_FULL_EXPONENTIAL}
	 * <p/>
	 * Returns zero back pressure until the buffer is full, then it returns an
	 * exponentially increasing amount, starting with the supplied value and
	 * doubling it each time. Once the buffer is no longer full the back
	 * pressure returns to zero
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationBufferOnFullExponentialBackPressure()
			throws Exception {

		// cf PushStreamTest
		ExtGenerator gen = new ExtGenerator(10);
		PushStreamProvider psp = new PushStreamProvider();
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();

		// thread overlapping detection
		AtomicInteger threadCount = new AtomicInteger(0);
		BinaryOperator<Integer> bo = new BinaryOperator<Integer>() {

			private final ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>() {

				@Override
				public Integer initialValue() {
					// if threads overlap the final threadCount will not be zero
					return threadCount.addAndGet(threadCount.get() + 1);
				}

				@Override
				public void remove() {
					threadCount.decrementAndGet();
					super.remove();
				}
			};

			@Override
			public Integer apply(Integer t, Integer u) {
				COUNTER.get();
				Integer result = Integer.valueOf(t.intValue() + u.intValue());
				try {
					Thread.sleep(250);

				} catch (InterruptedException e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				COUNTER.remove();
				return result;
			}
		};
		Promise<Optional<Integer>> p = ps.buildBuffer()
				.withBuffer(new ArrayBlockingQueue<>(3))
				.withPushbackPolicy(PushbackPolicyOption.ON_FULL_EXPONENTIAL,
						200)
				.withQueuePolicy(QueuePolicyOption.BLOCK)
				.build()
				.reduce(bo);

		gen.getExecutionThread().join();
		@SuppressWarnings("unused")
		ExtGeneratorStatus status = gen.status();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(45);

		assertThat(gen.fixedBackPressure()).isFalse();
		assertThat(gen.minBackPressure()).isEqualTo(0L);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * {@link ThresholdPushbackPolicy}
	 * <p/>
	 * Returns zero back pressure until the threshold in the buffer is reached,
	 * then it returns an linearly increasing back pressure.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationBufferOnLinearAfterThesholdBackPressure()
			throws Exception {

		// cf PushStreamTest
		ExtGenerator gen = new ExtGenerator(12);
		PushStreamProvider psp = new PushStreamProvider();
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();

		// thread overlapping detection
		AtomicInteger threadCount = new AtomicInteger(0);
		BinaryOperator<Integer> bo = new BinaryOperator<Integer>() {

			private final ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>() {

				@Override
				public Integer initialValue() {
					// if threads overlap the final threadCount will not be zero
					return threadCount.addAndGet(threadCount.get() + 1);
				}

				@Override
				public void remove() {
					threadCount.decrementAndGet();
					super.remove();
				}
			};

			@Override
			public Integer apply(Integer t, Integer u) {
				COUNTER.get();
				Integer result = Integer.valueOf(t.intValue() + u.intValue());
				try {
					Thread.sleep(250);

				} catch (InterruptedException e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				COUNTER.remove();
				return result;
			}
		};
		Promise<Optional<Integer>> p = ps.buildBuffer()
				.withBuffer(new ArrayBlockingQueue<>(12))
				.withPushbackPolicy(ThresholdPushbackPolicy
						.createThresholdPushbackPolicy(2, 2L, 5L))
				.withQueuePolicy(QueuePolicyOption.BLOCK)
				.build()
				.reduce(bo);

		gen.getExecutionThread().join();
		@SuppressWarnings("unused")
		ExtGeneratorStatus status = gen.status();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(66);

		assertThat(gen.fixedBackPressure()).isFalse();
		assertThat(gen.minBackPressure()).isEqualTo(0L);
		assertThat(gen.maxBackPressure()).isEqualTo(37L);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * {@link ThresholdPushbackPolicy}
	 * <p/>
	 * Returns zero back pressure until the threshold in the buffer is reached,
	 * then it returns an linearly increasing back pressure.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationBufferOnLinearAfterThesholdBackPressureSimple()
			throws Exception {

		// cf PushStreamTest
		ExtGenerator gen = new ExtGenerator(12);
		PushStreamProvider psp = new PushStreamProvider();
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();

		// thread overlapping detection
		AtomicInteger threadCount = new AtomicInteger(0);
		BinaryOperator<Integer> bo = new BinaryOperator<Integer>() {

			private final ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>() {

				@Override
				public Integer initialValue() {
					// if threads overlap the final threadCount will not be zero
					return threadCount.addAndGet(threadCount.get() + 1);
				}

				@Override
				public void remove() {
					threadCount.decrementAndGet();
					super.remove();
				}
			};

			@Override
			public Integer apply(Integer t, Integer u) {
				COUNTER.get();
				Integer result = Integer.valueOf(t.intValue() + u.intValue());
				try {
					Thread.sleep(250);

				} catch (InterruptedException e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				COUNTER.remove();
				return result;
			}
		};
		Promise<Optional<Integer>> p = ps.buildBuffer()
				.withBuffer(new ArrayBlockingQueue<>(10))
				.withPushbackPolicy(ThresholdPushbackPolicy
						.createSimpleThresholdPushbackPolicy(2))
				.withQueuePolicy(QueuePolicyOption.BLOCK)
				.build()
				.reduce(bo);

		gen.getExecutionThread().join();
		@SuppressWarnings("unused")
		ExtGeneratorStatus status = gen.status();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(66);

		assertThat(gen.fixedBackPressure()).isFalse();
		assertThat(gen.minBackPressure()).isEqualTo(0L);
		assertThat(gen.maxBackPressure()).isEqualTo(8L);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * {@link ThresholdPushbackPolicy}
	 * <p/>
	 * Returns zero back pressure until the threshold in the buffer is reached,
	 * then it returns an linearly increasing back pressure.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationBufferOnLinearAfterThesholdBackPressureStep()
			throws Exception {

		// cf PushStreamTest
		ExtGenerator gen = new ExtGenerator(12);
		PushStreamProvider psp = new PushStreamProvider();
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();

		// thread overlapping detection
		AtomicInteger threadCount = new AtomicInteger(0);
		BinaryOperator<Integer> bo = new BinaryOperator<Integer>() {

			private final ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>() {

				@Override
				public Integer initialValue() {
					// if threads overlap the final threadCount will not be zero
					return threadCount.addAndGet(threadCount.get() + 1);
				}

				@Override
				public void remove() {
					threadCount.decrementAndGet();
					super.remove();
				}
			};

			@Override
			public Integer apply(Integer t, Integer u) {
				COUNTER.get();
				Integer result = Integer.valueOf(t.intValue() + u.intValue());
				try {
					Thread.sleep(250);

				} catch (InterruptedException e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				COUNTER.remove();
				return result;
			}
		};
		Promise<Optional<Integer>> p = ps.buildBuffer()
				.withBuffer(new ArrayBlockingQueue<>(10))
				.withPushbackPolicy(ThresholdPushbackPolicy
						.createIncrementalThresholdPushbackPolicy(2, 5L))
				.withQueuePolicy(QueuePolicyOption.BLOCK)
				.build()
				.reduce(bo);

		gen.getExecutionThread().join();
		@SuppressWarnings("unused")
		ExtGeneratorStatus status = gen.status();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(66);

		assertThat(gen.fixedBackPressure()).isFalse();
		assertThat(gen.minBackPressure()).isEqualTo(0L);
		assertThat(gen.maxBackPressure()).isEqualTo(40L);
	}

	/**
	 * Tests {@link ThresholdPushbackPolicy} constructor and assures an
	 * {@link IllegalArgumentException} is thrown when the threshold is
	 * negative.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testThresholdPushbackPolicyIllegalArgumentThreshold()
			throws Exception {
		Assertions.assertThatIllegalArgumentException().isThrownBy(() -> {
			ThresholdPushbackPolicy.createThresholdPushbackPolicy(-1, 1L,
					1L);
		});
	}

	/**
	 * Tests {@link ThresholdPushbackPolicy} constructor and assures an
	 * {@link IllegalArgumentException}is thrown when the initial is negative.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testThresholdPushbackPolicyIllegalArgumentInitial()
			throws Exception {
		Assertions.assertThatIllegalArgumentException().isThrownBy(() -> {
			ThresholdPushbackPolicy.createThresholdPushbackPolicy(1, -1L, 1L);
		});
	}

	/**
	 * Tests {@link ThresholdPushbackPolicy} constructor and assures an
	 * {@link IllegalArgumentException}is thrown when the step size is negative.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testThresholdPushbackPolicyIllegalArgumentStep()
			throws Exception {
		Assertions.assertThatIllegalArgumentException().isThrownBy(() -> {
			ThresholdPushbackPolicy.createThresholdPushbackPolicy(1, 1L, -11L);
		});
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * {@link ThresholdPushbackPolicy}
	 * <p/>
	 * Returns zero back pressure until the threshold in the buffer is reached,
	 * then it returns a fixed value.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationBufferOnLinearAfterThesholdBackPressureFixed()
			throws Exception {

		// cf PushStreamTest
		ExtGenerator gen = new ExtGenerator(12);
		PushStreamProvider psp = new PushStreamProvider();
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();

		// thread overlapping detection
		AtomicInteger threadCount = new AtomicInteger(0);
		BinaryOperator<Integer> bo = new BinaryOperator<Integer>() {

			private final ThreadLocal<Integer> COUNTER = new ThreadLocal<Integer>() {

				@Override
				public Integer initialValue() {
					// if threads overlap the final threadCount will not be zero
					return threadCount.addAndGet(threadCount.get() + 1);
				}

				@Override
				public void remove() {
					threadCount.decrementAndGet();
					super.remove();
				}
			};

			@Override
			public Integer apply(Integer t, Integer u) {
				COUNTER.get();
				Integer result = Integer.valueOf(t.intValue() + u.intValue());
				try {
					Thread.sleep(250);

				} catch (InterruptedException e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				COUNTER.remove();
				return result;
			}
		};
		Promise<Optional<Integer>> p = ps.buildBuffer()
				.withBuffer(new ArrayBlockingQueue<>(10))
				.withPushbackPolicy(ThresholdPushbackPolicy
						.createThresholdPushbackPolicy(2, 10L, 0L))
				.withQueuePolicy(QueuePolicyOption.BLOCK)
				.build()
				.reduce(bo);

		gen.getExecutionThread().join();
		@SuppressWarnings("unused")
		ExtGeneratorStatus status = gen.status();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.optional(Integer.class))
				.contains(66);

		assertThat(gen.fixedBackPressure()).isFalse();
		assertThat(gen.minBackPressure()).isEqualTo(0L);
		assertThat(gen.maxBackPressure()).isEqualTo(10L);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * coalesce registers a coalescing function which aggregates one or more
	 * data events into a single data event which will be passed to the next
	 * stage of stream The [...] mechanism delegates all responsibility to the
	 * coalescing function, which returns an Optional. The coalescing function
	 * is called for every data event, and returns an optional which either has
	 * a value, or is empty. If the optional has a value then this value is
	 * passed to the next stage of the processing pipeline. If the optional is
	 * empty then no data event is passed to the next stage.
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testIntermediateOperationCoalesce()
			throws InterruptedException, InvocationTargetException {

		ExtGenerator gen = new ExtGenerator(20);
		PushStreamProvider psp = new PushStreamProvider();

		AtomicInteger current = new AtomicInteger();

		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();
		Promise<Integer[]> pr = ps.coalesce((c) -> {
			int value = current.addAndGet(c);
			if (value > 10) {
				current.set(0);
				return Optional.of(value);
			}
			return Optional.empty();
		}).toArray(Integer[]::new);

		gen.getExecutionThread().join();

		ExtGeneratorStatus status = gen.status;
		assertNotNull(status);
		// [15, 13, 17, 21, 12, 13, 14, 15, 16, 17, 18, 19] expected
		assertThat(pr).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(15, 13, 17, 21, 12, 13, 14, 15, 16, 17, 18,
						19);
		assertThat(gen.closeCalled).isTrue();
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());
	}

	/**
	 * 706.3.4.1 : Coalescing
	 * <p/>
	 * [Coalescence mechanism ] to coalesce registers a coalescing function
	 * which aggregates one or more data events into a single data event which
	 * will be passed to the next stage of stream The [...] mechanism allows the
	 * stream to be configured with a (potentially variable) buffer size. The
	 * stream then stores values into this buffer. When the buffer is full then
	 * the stream passes the buffer to the handler function, which returns data
	 * to be passed to the next stage. If the stream finishes when a buffer is
	 * partially filled then the partially filled buffer will be passed to the
	 * handler function.
	 */
	@Test
	@Disabled("unimplemented")
	public void testIntermediateOperationCoalesceUsingBuffer() {

	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * window collects events over the specified time-limit, passing them to the
	 * registered handler function. If no events occur during the time limit
	 * then no events are propagated
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testIntermediateOperationWindowNoEvent()
			throws InterruptedException, InvocationTargetException {

		ExtGenerator gen = new ExtGenerator(10);
		PushStreamProvider psp = new PushStreamProvider();

		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();
		Promise<Integer[]> pr = ps.filter((i) -> {
			return i > 10;
		}).window(Duration.ofMillis(1000), (c) -> {
			return c.size();
		}).toArray(Integer[]::new);

		gen.getExecutionThread().join();

		ExtGeneratorStatus status = gen.status;
		assertNotNull(status);

		assertThat(pr).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.containsExactly(0);
		assertThat(gen.closeCalled).isTrue();
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * window collects events over the specified time-limit, passing them to the
	 * registered handler function. If no events occur during the time limit
	 * then no events are propagated As windowing requires the collected events
	 * to be delivered asynchronously there is no opportunity for back pressure
	 * from the previous stage to be applied upstream. Windowing therefore
	 * returns zero back-pressure in all cases except when a buffer size limit
	 * has been declared and is reached
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testIntermediateOperationWindowEvents()
			throws InterruptedException, InvocationTargetException {

		ExtGenerator gen = new ExtGenerator(10000);
		PushStreamProvider psp = new PushStreamProvider();

		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();
		Promise<Integer[]> pr = ps.window(Duration.ofMillis(10), (c) -> {
			return c.size();
		}).toArray(Integer[]::new);

		gen.getExecutionThread().join();

		ExtGeneratorStatus status = gen.status;
		assertNotNull(status);

		assertThat(gen.maxBackPressure).isEqualTo(0L);
		assertThat(pr).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.isNotEmpty();
		System.out.println(Arrays.toString(pr.getValue()));
		assertThat(gen.closeCalled).isTrue();
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * window collects events over the specified time-limit, passing them to the
	 * registered handler function. If no events occur during the time limit
	 * then no events are propagated As windowing requires the collected events
	 * to be delivered asynchronously there is no opportunity for back pressure
	 * from the previous stage to be applied upstream. Windowing therefore
	 * returns zero back-pressure in all cases except when a buffer size limit
	 * has been declared and is reached. If a window size limit is reached then
	 * the windowing stage returns the remaining window time as back pressure
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testIntermediateOperationWindowEventsBuffered()
			throws InterruptedException, InvocationTargetException {

		ExtGenerator gen = new ExtGenerator(500);
		PushStreamProvider psp = new PushStreamProvider();

		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();
		Promise<Integer[]> pr = ps.window(() -> {
			return Duration.ofMillis(200);
		}, () -> {
			return 5;
		}, (t, u) -> {
			return u.size();
		}).toArray(Integer[]::new);

		gen.getExecutionThread().join();

		ExtGeneratorStatus status = gen.status;
		assertNotNull(status);

		assertThat(gen.minBackPressure).isEqualTo(0L);
		// We should get a max back pressure close to, but no more than 200
		assertThat(gen.maxBackPressure).isLessThanOrEqualTo(200L)
				.isGreaterThan(160L);
		// There must be at least 100 entries as we allow a maximum of 5 per
		// window
		assertThat(pr).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.array(Integer[].class))
				.hasSizeGreaterThanOrEqualTo(100);
		assertThat(gen.closeCalled).isTrue();
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());
	}

	/**
	 * 706.3.2.1 : Back Pressure
	 * <p/>
	 * Apply back pressure mid-way through the stream
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testIntermediateOperationAdjustBackPressure()
			throws InterruptedException, InvocationTargetException {
		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().buildStream(gen)
				.unbuffered()
				.build();
		Promise<Long> p = ps.adjustBackPressure(l -> 10).count();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.LONG)
				.isEqualTo(5L);
		assertThat(gen.maxBackPressure).isEqualTo(10L);
	}

	/**
	 * 706.3.2.1 : Back Pressure
	 * <p/>
	 * Apply back pressure mid-way through the stream
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testIntermediateOperationAdjustBackPressure2()
			throws InterruptedException, InvocationTargetException {
		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().buildStream(gen)
				.unbuffered()
				.build();
		Promise<Long> p = ps.adjustBackPressure((d, l) -> 10 + d).count();

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(InstanceOfAssertFactories.LONG)
				.isEqualTo(5L);
		assertThat(gen.maxBackPressure).isEqualTo(14L);
	}

	/**
	 * 706.3.1.1.4 : Async Mapping
	 * <p/>
	 * Mapping is the act of transforming an event from one type into another
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIntermediateOperationAsyncMapping() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().buildStream(gen)
				.unbuffered()
				.build();

		Promise<Character[]> p = ps.asyncMap(2, 110, e -> {
			return Promises.resolved((char) ('a' + e)).delay(e * 100);
		}).toArray(Character[]::new);

		assertThat(p).resolvesWithin(PROMISE_RESOLVE_DURATION)
				.hasValueThat(
						InstanceOfAssertFactories.array(Character[].class))
				.containsExactly('a', 'b', 'c', 'd', 'e');

		gen.getExecutionThread().join();

		assertThat(gen.fixedBackPressure()).isFalse();
		assertThat(gen.minBackPressure()).isEqualTo(0L);
		assertThat(gen.maxBackPressure()).isEqualTo(110L);
	}
}
