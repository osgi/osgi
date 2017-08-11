package org.osgi.test.cases.pushstream.junit;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.TimeoutException;
import org.osgi.util.pushstream.PushEvent;
import org.osgi.util.pushstream.PushEvent.EventType;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamProvider;
import org.osgi.util.pushstream.PushbackPolicyOption;
import org.osgi.util.pushstream.QueuePolicyOption;


/**
 * Section 706.3 The Push Stream
 */
@SuppressWarnings({
		"boxing", "rawtypes", "unchecked"
})
public class PushStreamIntermediateOperationTest
		extends PushStreamComplianceTest {

	/**
	 * checks push event source behavior before the operation tests
	 * 
	 * @throws Exception
	 */
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
		assertTrue(status.bp == 0);
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
		assertTrue(status.bp == 0);
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
		assertTrue(status.bp == 0);
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
		assertTrue(status.bp < 0);
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());

		// returned positive back pressure
		gen = new ExtGenerator(5);
		long start = System.currentTimeMillis();
		closeable = gen.open(e -> {
			if (e.isTerminal()) {
				return 0;
			}
			return 200;
		});
		gen.getExecutionThread().join();
		long end = System.currentTimeMillis();
		status = gen.status();
		// System.out.println(status);

		assertNotNull(status);
		assertTrue(status.failure == null);
		assertTrue(status.bp == 200);
		assertTrue(end - start >= 1000);
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
	public void testUnableToChainNewOperationWhileIntermediateOperationRunning()
			throws InterruptedException {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Object[]> prm = ps.filter((i) -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return i % 3 == 0;
		}).toArray();

		try {
			ps.map((i) -> {
				return i * 2;
			}).toArray();

			fail("IllegalStateException expected");

		} catch (IllegalStateException e) {}
		gen.getExecutionThread().join();
	}

	/**
	 * 706.3.1.1.1 : Mapping
	 * <p/>
	 * Mapping is the act of transforming an event from one type into another
	 * 
	 * @throws Exception
	 */
	public void testIntermediateOperationMapping() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));
		Promise<Object[]> p = ps.map(e -> {
			return (char) ('a' + e);
		}).toArray();

		assertEquals("[a, b, c, d, e]", Arrays.toString(p.getValue()));
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
		assertTrue(prm.getFailure() != null);
		assertEquals(message, prm.getFailure().getMessage());

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
	public void testIntermediateOperationFlatMapping() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		Promise<Object[]> p = ps.flatMap(e -> {
			return new PushStreamProvider().createStream(new ExtGenerator(e));
		}).map(e -> {
			return (char) ('a' + e);
		}).toArray();
		// expect ('a' + 0), ('a' + 0 ,'a' + 1) ,('a' + 0 ,'a' + 1 , 'a' + 2 ),
		// ('a' + 0 ,'a' + 1 , 'a' + 2, 'a' + 3)
		assertEquals("[a, a, b, a, b, c, a, b, c, d]",
				Arrays.toString(p.getValue()));
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
		assertTrue(prm.getFailure() != null);
		assertEquals(message, prm.getFailure().getMessage());

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
	public void testIntermediateOperationFiltering() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		Promise<Object[]> p = ps.filter(e -> {
			return e % 2 == 0;
		}).toArray();

		assertEquals("[0, 2, 4]",
				Arrays.toString(p.getValue()));
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
		assertTrue(prm.getFailure() != null);
		assertEquals(message, prm.getFailure().getMessage());

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
	public void testIntermediateOperationDistinct() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		Promise<Object[]> p = ps.map(e -> {
			return e % 2;
		}).distinct().toArray();

		assertEquals("[0, 1]", Arrays.toString(p.getValue()));
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * sorted remembers all items in the stream until the stream ends. At this
	 * point the data in the stream will be propagated to the next stage of the
	 * stream, in the order defined by the Comparator
	 */
	public void testIntermediateOperationSorted() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(gen);

		// reverse order
		Comparator< ? extends Integer> comparator = (e1, e2) -> {
			int compare = e1.compareTo(e2);
			return compare == 0 ? 0 : ((compare < 0) ? 1 : -1);
		};
		Promise<Object[]> p = ps
				.sorted((Comparator< ? super Integer>) comparator).toArray();

		assertEquals("[4, 3, 2, 1, 0]", Arrays.toString(p.getValue()));

	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * sorted remembers all items in the stream until the stream ends. At this
	 * point the data in the stream will be propagated to the next stage of the
	 * stream, in the order defined by the Comparator
	 */
	public void testIntermediateOperationSortedNaturalOrder() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(gen);

		Comparator< ? extends Integer> comparator = (e1, e2) -> {
			// reverse order
			int compare = e1.compareTo(e2);
			return compare == 0 ? 0 : ((compare < 0) ? 1 : -1);
		};

		Promise<Object[]> p = ps
				.sorted((Comparator< ? super Integer>) comparator)
				.sorted()
				.toArray();

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertEquals("[0, 1, 2, 3, 4]", Arrays.toString(p.getValue()));

	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * limit operation limits the length of the stream to the defined number of
	 * elements. Once that number of elements are received then a close event is
	 * propagated to the next stage of the stream
	 */
	public void testIntermediateOperationLimit() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().buildStream(gen)
				.unbuffered()
				.build();

		Promise<Object[]> p = ps.limit(2l).toArray();

		// do not wait for the complete source events processing as we expect
		// a close event when the limit is reached
		// gen.getExecutionThread().join();

		assertEquals("[0, 1]", Arrays.toString(p.getValue()));
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
	public void testIntermediateOperationLimitWithDuration() throws Exception {

		ExtGenerator gen = new ExtGenerator(10000);
		PushStream<Integer> ps = new PushStreamProvider().buildStream(gen)
				.unbuffered()
				.build();

		Promise<Object[]> p = ps.limit(Duration.ofMillis(5)).toArray();
		gen.getExecutionThread().join();
		long elapse = (gen.stop - gen.start);
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
	public void testIntermediateOperationSplitOneFilter() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		PushStream<Integer>[] p = ps.split(e -> {
			return e % 2 == 0;
		});
		assertEquals(1, p.length);
		Promise<Object[]> even = p[0].toArray();
		assertEquals("[0, 2, 4]", Arrays.toString(even.getValue()));
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
	public void testIntermediateOperationSplitTwoFilters() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		PushStream<Integer>[] p = ps.split(e -> {
			return e % 2 == 0;
		}, e -> {
			return e % 2 == 1;
		});
		
		assertEquals(2, p.length);
		
		Promise<Object[]> even = p[0].toArray();
		Promise<Object[]> odd = p[1].toArray();

		assertEquals("[0, 2, 4]",
				Arrays.toString(even.getValue()));

		assertEquals("[1, 3]",
				Arrays.toString(odd.getValue()));

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
	public void testIntermediateOperationSplitTwoFiltersOverlapping()
			throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		PushStream<Integer>[] p = ps.split(e -> {
			return e % 2 == 1;
		}, e -> {
			return e % 3 == 0;
		});

		assertEquals(2, p.length);

		Promise<Object[]> odd = p[0].toArray();
		Promise<Object[]> modulo3 = p[1].toArray();

		assertEquals("[1, 3]", Arrays.toString(odd.getValue()));
		assertEquals("[0, 3]", Arrays.toString(modulo3.getValue()));

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
	public void testIntermediateOperationSplitOneFilterWithoutMatch()
			throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		PushStream<Integer>[] p = ps.split(e -> {
			return e % 2 == 2;
		});

		assertEquals(1, p.length);
		Promise<Object[]> empty = p[0].toArray();

		// which of this two behaviors is expected ?
		assertTrue(empty.getValue().length == 0);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * skip drops the supplied number of data events from the stream and then
	 * forwards any further data events
	 */
	public void testIntermediateOperationSkip() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		Promise<Object[]> p = ps.skip(3).toArray();
		assertEquals("[3, 4]", Arrays.toString(p.getValue()));
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * skip drops the supplied number of data events from the stream and then
	 * forwards any further data events
	 */
	public void testIntermediateOperationSkipAll() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		Promise<Object[]> p = ps.skip(5).toArray();
		assertEquals("[]", Arrays.toString(p.getValue()));
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * skip drops the supplied number of data events from the stream and then
	 * forwards any further data events
	 */
	public void testIntermediateOperationSkipNone() throws Exception {

		PushStream<Integer> ps = new PushStreamProvider()
				.createStream(new ExtGenerator(5));

		Promise<Object[]> p = ps.skip(0).toArray();
		assertEquals("[0, 1, 2, 3, 4]", Arrays.toString(p.getValue()));
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * fork pushes event processing onto one or more threads in the supplied
	 * Executor returning a fixes back-pressure
	 * 
	 * @throws Exception
	 */
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
				Integer result = new Integer(t.intValue() + u.intValue());
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

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertEquals(45, (int) p.getValue().get());
		// multi-threading
		assertTrue(executor.getLargestPoolSize() > 1);
		// no blocking threads in fork
		assertTrue(executor.getLargestPoolSize() <= 5);
		assertTrue(gen.fixedBackPressure());
		// thread overlapping in reduce
		assertTrue(threadCount.get() > 0);
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
				Integer result = new Integer(t.intValue() + u.intValue());
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

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertEquals(45, (int) p.getValue().get());
		// if threads have been blocked the back pressure must have
		// been changed during processing
		assertFalse(gen.fixedBackPressure());
		// n+1 seems to be OK and n not
		// assertEquals(6, executor.getLargestPoolSize());
		assertEquals(5, executor.getLargestPoolSize());
		ExtGeneratorStatus status = gen.status();
		assertTrue(status.bp > 0);
		// thread overlapping in reduce
		assertTrue(threadCount.get() > 0);
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

		Promise<Object[]> p = ps_first.merge(ps_second).toArray();
		
		// Keep the ordering
		LinkedHashSet<Object> one = new LinkedHashSet<>();
		LinkedHashSet<Object> two = new LinkedHashSet<>();

		for (Object o : p.getValue()) {
			if (!one.add(o)) {
				assertTrue("Object " + o + " was present three times",
						two.add(o));
			}
		}

		// The two sets of events should be in order, even though they were
		// interleaved
		assertEquals(Arrays.asList(0, 1, 2, 3, 4),
				one.stream().collect(Collectors.toList()));
		assertEquals(Arrays.asList(0, 1, 2, 3, 4),
				two.stream().collect(Collectors.toList()));

		// The two sets of events should be interleaved as they have a 100 ms
		// delay after each event, giving time for the other stream to run
		assertFalse("The two streams were processed sequentially",
				Arrays.asList(0, 1, 2, 3, 4, 0, 1, 2, 3, 4)
						.equals(Arrays.asList(p.getValue())));
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
	public void testIntermediateOperationMergeStreamAndSource()
			throws Exception {

		ExtGenerator gen_first = new ExtGenerator(5);
		ExtGenerator gen_second = new ExtGenerator(5);

		PushStream<Integer> ps_first = new PushStreamProvider()
				.createStream(gen_first);

		Promise<Object[]> p = ps_first.merge(gen_second).toArray();

		gen_first.getExecutionThread().join();
		gen_second.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertEquals(
				new HashSet(Arrays.asList(0, 0, 1, 1, 2, 2, 3, 3, 4, 4)),
				new HashSet(Arrays.asList(p.getValue())));
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * sequential forces data events to be delivered sequentially to the next
	 * stage of the stream
	 * 
	 * @throws InterruptedException
	 */
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
				Integer result = new Integer(t.intValue() + u.intValue());
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

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		// no thread overlapping in reduce
		assertEquals(0, threadCount.get());
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * 706.5.13.1 : enum QueuePolicyOption - DISCARD_OLDEST
	 * <p/>
	 * Attempt to add the supplied event to the queue. If the queue is unable to
	 * immediatly accept the value then discard the value at the head of the
	 * queue and try again. Repeat this process until the event is enqueued
	 * 
	 * @throws Exception
	 */
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
				Integer result = new Integer(t.intValue() + u.intValue());
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

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		// because we must receive a -1 back pressure value
		assertTrue(gen.fixedBackPressure());
		assertEquals(150, status.bp);
		assertTrue(45 > p.getValue().get());
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * 706.5.13.3 : enum QueuePolicyOption - FAIL
	 * <p/>
	 * Attempt to add the supplied event to the queue, throwing an exception if
	 * the queue is full.
	 * 
	 * @throws Exception
	 */
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
				Integer result = new Integer(t.intValue() + u.intValue());
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

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		// because we must receive a -1 back pressure value
		assertFalse(gen.fixedBackPressure());
		assertEquals(-1, status.bp);
		assertEquals(150, gen.maxBackPressure());
		assertTrue(p.getFailure() != null);
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * 706.5.4 1 : enum PushBackPolicyOption - FIXED
	 * <p/>
	 * Returns a fixed amount of back pressure, independent of how full the
	 * buffer is
	 * <p/>
	 * 706.5.13.3 : enum QueuePolicyOption - BLOCK
	 * <p/>
	 * Attempt to add the supplied event to the queue, blocking until the
	 * enqueue is successful.
	 * 
	 * @throws Exception
	 */
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
				Integer result = new Integer(t.intValue() + u.intValue());
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

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertTrue(gen.fixedBackPressure());
		assertEquals(150, status.bp);
		assertEquals(45, (int) p.getValue().get());

	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * 706.5.4 4 : enum PushBackPolicyOption - LINEAR
	 * <p/>
	 * Returns zero back pressure when the buffer is empty, then it returns a
	 * linearly increasing amount of back pressure based on how full the buffer
	 * is. The maximum value will be returned when the buffer is full. returns
	 * to zero
	 * 
	 * @throws Exception
	 */
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
				Integer result = new Integer(t.intValue() + u.intValue());
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

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertFalse(gen.fixedBackPressure());
		assertEquals(250, gen.maxBackPressure());
		assertEquals(45, (int) p.getValue().get());

	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * 706.5.4 4 : enum PushBackPolicyOption - ON_FULL_FIXED
	 * <p/>
	 * Returns zero back pressure until the buffer is full, then it returns a
	 * fixed value
	 * 
	 * @throws Exception
	 */
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
				Integer result = new Integer(t.intValue() + u.intValue());
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

		assertEquals(45, (int) p.getValue().get());

		assertTrue(p.isDone());
		assertFalse(gen.fixedBackPressure());
		assertEquals(250, gen.maxBackPressure());
		assertEquals(0, gen.minBackPressure());
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * buffer introduces a buffer before the next stage of the stream The buffer
	 * can be used to provide a circuit breaker, or to allow a switch of
	 * consumer thread(s)
	 * <p/>
	 * 706.5.4 4 : enum PushBackPolicyOption - ON_FULL_EXPONENTIAL
	 * <p/>
	 * Returns zero back pressure until the buffer is full, then it returns an
	 * exponentially increasing amount, starting with the supplied value and
	 * doubling it each time. Once the buffer is no longer full the back
	 * pressure returns to zero
	 * 
	 * @throws Exception
	 */
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
				Integer result = new Integer(t.intValue() + u.intValue());
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

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertFalse(gen.fixedBackPressure());
		assertEquals(0, gen.minBackPressure());
		assertEquals(45, (int) p.getValue().get());
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
	public void testIntermediateOperationCoalesce()
			throws InterruptedException, InvocationTargetException {

		ExtGenerator gen = new ExtGenerator(20);
		PushStreamProvider psp = new PushStreamProvider();

		AtomicInteger current = new AtomicInteger();

		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();
		Promise<Object[]> pr = ps.<Integer> coalesce((c) -> {
			current.set(current.get() + c);
			if (current.get() > 10) {
				Optional<Integer> opt = Optional.of(current.get());
				current.set(0);
				return opt;
			}
			return Optional.empty();
		}).toArray();

		gen.getExecutionThread().join();

		ExtGeneratorStatus status = gen.status;
		assertNotNull(status);
		// [15, 13, 17, 21, 12, 13, 14, 15, 16, 17, 18, 19] expected
		assertTrue(pr.getValue().length == 12);
		assertTrue(gen.closeCalled);
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
	public void testIntermediateOperationWindowNoEvent()
			throws InterruptedException, InvocationTargetException {

		ExtGenerator gen = new ExtGenerator(10);
		PushStreamProvider psp = new PushStreamProvider();

		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();
		Promise<Object[]> pr = ps.filter((i) -> {
			return i > 10;
		}).window(Duration.ofMillis(1000), (c) -> {
			return c.size();
		}).toArray();

		gen.getExecutionThread().join();

		ExtGeneratorStatus status = gen.status;
		assertNotNull(status);

		assertEquals(1l, pr.getValue().length);
		assertEquals(0, pr.getValue()[0]);
		assertTrue(gen.closeCalled);
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
	public void testIntermediateOperationWindowEvents()
			throws InterruptedException, InvocationTargetException {

		ExtGenerator gen = new ExtGenerator(10000);
		PushStreamProvider psp = new PushStreamProvider();

		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();
		Promise<Object[]> pr = ps.window(Duration.ofMillis(10), (c) -> {
			return c.size();
		}).toArray();

		gen.getExecutionThread().join();

		ExtGeneratorStatus status = gen.status;
		assertNotNull(status);

		assertEquals(0l, gen.maxBackPressure);
		assertTrue(pr.getValue().length > 0);
		System.out.println(Arrays.toString(pr.getValue()));
		assertTrue(gen.closeCalled);
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
	public void testIntermediateOperationWindowEventsBuffered()
			throws InterruptedException, InvocationTargetException {

		ExtGenerator gen = new ExtGenerator(500);
		PushStreamProvider psp = new PushStreamProvider();

		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().build();
		Promise<Object[]> pr = ps.window(() -> {
			return Duration.ofMillis(200);
		}, () -> {
			return 5;
		}, (t, u) -> {
			return u.size();
		}).toArray();

		gen.getExecutionThread().join();

		ExtGeneratorStatus status = gen.status;
		assertNotNull(status);

		assertEquals(0l, gen.minBackPressure);
		// We should get a max back pressure close to, but no more than 200
		assertTrue("Max backpressure was " + gen.maxBackPressure,
				gen.maxBackPressure > 160l && gen.maxBackPressure <= 200);
		Object[] values = pr.getValue();
		// There must be at least 100 entries as we allow a maximum of 5 per
		// window
		assertTrue("Not enough values " + values.length, values.length >= 100);
		assertTrue(gen.closeCalled);
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());
	}
}
