package org.osgi.test.cases.pushstream.junit;

import java.io.Closeable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;

import org.osgi.util.promise.Promise;
import org.osgi.util.pushstream.PushEvent;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamProvider;
import org.osgi.util.pushstream.PushbackPolicyOption;
import org.osgi.util.pushstream.QueuePolicyOption;

import junit.framework.Assert;


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

		Assert.assertEquals("[a, b, c, d, e]", Arrays.toString(p.getValue()));
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
		Assert.assertEquals("[a, a, b, a, b, c, a, b, c, d]",
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

		Assert.assertEquals("[0, 2, 4]",
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

		Assert.assertEquals("[0, 1]", Arrays.toString(p.getValue()));
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

		Assert.assertEquals("[4, 3, 2, 1, 0]", Arrays.toString(p.getValue()));

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

		Assert.assertTrue(p.isDone());
		Assert.assertEquals("[0, 1, 2, 3, 4]", Arrays.toString(p.getValue()));

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
				.create();

		Promise<Object[]> p = ps.limit(2l).toArray();

		// do not wait for the complete source events processing as we expect
		// a close event when the limit is reached
		// gen.getExecutionThread().join();

		Assert.assertEquals("[0, 1]", Arrays.toString(p.getValue()));
		// is the close event sent immediatly ?
		ExtGeneratorStatus status = gen.status();
		assertNotNull(status);
		assertTrue(status.failure == null);
		// assertTrue(gen.closeCalled);
		assertEquals(PushEvent.EventType.CLOSE, status.event.getType());

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
		Assert.assertEquals(1, p.length);
		Promise<Object[]> even = p[0].toArray();
		Assert.assertEquals("[0, 2, 4]", Arrays.toString(even.getValue()));
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
		
		Assert.assertEquals(2, p.length);
		
		Promise<Object[]> even = p[0].toArray();
		Promise<Object[]> odd = p[1].toArray();

		Assert.assertEquals("[0, 2, 4]",
				Arrays.toString(even.getValue()));

		Assert.assertEquals("[1, 3]",
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

		Assert.assertEquals(2, p.length);

		Promise<Object[]> odd = p[0].toArray();
		Promise<Object[]> modulo3 = p[1].toArray();

		Assert.assertEquals("[1, 3]", Arrays.toString(odd.getValue()));
		Assert.assertEquals("[0, 3]", Arrays.toString(modulo3.getValue()));

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

		Assert.assertEquals(1, p.length);
		Promise<Object[]> empty = p[0].toArray();

		// which of this two behaviors is expected ?
		Assert.assertTrue(empty.getValue().length == 0);
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
		Assert.assertEquals("[3, 4]", Arrays.toString(p.getValue()));
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
		Assert.assertEquals("[]", Arrays.toString(p.getValue()));
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
		Assert.assertEquals("[0, 1, 2, 3, 4]", Arrays.toString(p.getValue()));
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

		Assert.assertTrue(p.isDone());
		Assert.assertEquals(45, (int) p.getValue().get());
		// multi-threading
		Assert.assertTrue(executor.getLargestPoolSize() > 1);
		// no blocking threads in fork
		Assert.assertTrue(executor.getLargestPoolSize() <= 5);
		Assert.assertTrue(gen.fixedBackPressure());
		// thread overlapping in reduce
		Assert.assertTrue(threadCount.get() > 0);
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

		Assert.assertTrue(p.isDone());
		Assert.assertEquals(45, (int) p.getValue().get());
		// if threads have been blocked the back pressure must have
		// been changed during processing
		Assert.assertFalse(gen.fixedBackPressure());
		// n+1 seems to be OK and n not
		// Assert.assertEquals(6, executor.getLargestPoolSize());
		Assert.assertEquals(5, executor.getLargestPoolSize());
		ExtGeneratorStatus status = gen.status();
		Assert.assertTrue(status.bp > 0);
		// thread overlapping in reduce
		Assert.assertTrue(threadCount.get() > 0);
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

		PushStream<Integer> ps_first = new PushStreamProvider()
				.createStream(gen_first);
		PushStream<Integer> ps_second = new PushStreamProvider()
				.createStream(gen_second);

		Promise<Object[]> p = ps_first.merge(ps_second).toArray();
		
		gen_first.getExecutionThread().join();
		gen_second.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		Assert.assertTrue(p.isDone());
		Assert.assertEquals(
				new HashSet(Arrays.asList(0, 0, 1, 1, 2, 2, 3, 3, 4, 4)),
				new HashSet(Arrays.asList(p.getValue())));
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

		Assert.assertTrue(p.isDone());
		Assert.assertEquals(
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

		Assert.assertTrue(p.isDone());
		// no thread overlapping in reduce
		Assert.assertEquals(0, threadCount.get());
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
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().create();

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
				.create()
				.reduce(bo);

		gen.getExecutionThread().join();
		ExtGeneratorStatus status = gen.status();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		Assert.assertTrue(p.isDone());
		// because we must receive a -1 back pressure value
		Assert.assertTrue(gen.fixedBackPressure());
		Assert.assertEquals(150, status.bp);
		Assert.assertTrue(45 > p.getValue().get());
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
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().create();

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
				.create()
				.reduce(bo);

		gen.getExecutionThread().join();
		ExtGeneratorStatus status = gen.status();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		Assert.assertTrue(p.isDone());
		// because we must receive a -1 back pressure value
		Assert.assertFalse(gen.fixedBackPressure());
		Assert.assertEquals(-1, status.bp);
		Assert.assertEquals(150, gen.maxBackPressure());
		Assert.assertTrue(p.getFailure() != null);
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
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().create();

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
				.create()
				.reduce(bo);

		gen.getExecutionThread().join();
		ExtGeneratorStatus status = gen.status();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		Assert.assertTrue(p.isDone());
		Assert.assertTrue(gen.fixedBackPressure());
		Assert.assertEquals(150, status.bp);
		Assert.assertEquals(45, (int) p.getValue().get());

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
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().create();

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
				.create()
				.reduce(bo);

		gen.getExecutionThread().join();
		ExtGeneratorStatus status = gen.status();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		Assert.assertTrue(p.isDone());
		Assert.assertFalse(gen.fixedBackPressure());
		Assert.assertEquals(250, gen.maxBackPressure());
		Assert.assertEquals(45, (int) p.getValue().get());

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
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().create();

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
				.create()
				.reduce(bo);

		Assert.assertEquals(45, (int) p.getValue().get());

		Assert.assertTrue(p.isDone());
		Assert.assertFalse(gen.fixedBackPressure());
		Assert.assertEquals(250, gen.maxBackPressure());
		Assert.assertEquals(0, gen.minBackPressure());
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
		PushStream<Integer> ps = psp.buildStream(gen).unbuffered().create();

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
				.create()
				.reduce(bo);

		gen.getExecutionThread().join();
		ExtGeneratorStatus status = gen.status();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		Assert.assertTrue(p.isDone());
		Assert.assertFalse(gen.fixedBackPressure());
		Assert.assertEquals(0, gen.minBackPressure());
		Assert.assertEquals(45, (int) p.getValue().get());
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * coalesce registers a coalescing function which aggregates one or more
	 * data events into a single data event which will be passed to the next
	 * stage of stream
	 */
	public void testIntermediateOperationCoalesce() {
		// cf PushStreamTest
	}

	/**
	 * 706.3.1.2 : Stateless and Stateful Intermediate Operations
	 * <p/>
	 * window collects events over the specified time-limit, passing them to the
	 * registered handler function. If no events occur during the time limit
	 * then no events are propagated
	 */
	public void testIntermediateOperationWindow() {
		// cf PushStreamTest
	}
}
