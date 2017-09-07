package org.osgi.test.cases.pushstream.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;

import org.osgi.util.promise.Promise;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamProvider;

/**
 * Section 706.3 The Push Stream
 */
public class PushStreamTerminalOperationTest extends PushStreamComplianceTest {
	
	
	int done;

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * forEach register a function to be called back with the data from each
	 * event in the stream
	 * 
	 * @throws Exception
	 */
	public void testTerminalOperationForEach() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		this.done = 0;
		Promise<Void> p = ps.forEach(i -> {
			done++;
		});

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertEquals(5, done);
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * forEach register a function to be called back with the data from each
	 * event in the stream
	 * 
	 * @throws Exception
	 */
	public void testTerminalOperationForEachException() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		RuntimeException re = new RuntimeException("bang");

		Promise<Void> p = ps.forEach(i -> {
			throw re;
		});

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertSame(re, p.getFailure());
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * forEachEvent registers a PushEventConsumer to be called with each event
	 * in the stream. If negative back pressure is returned then the stream will
	 * be closed
	 */
	public void testTerminalOperationForEachEvent() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		this.done = 0;
		Promise<Long> p = ps.forEachEvent(i -> {
			if (!i.isTerminal())
			done++;
			return 5;
		});

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		@SuppressWarnings("unused")
		ExtGeneratorStatus status = gen.status();

		assertTrue(p.isDone());
		assertEquals(5, done);
		assertTrue(gen.fixedBackPressure());
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
	public void testTerminalOperationForEachEventException() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		RuntimeException re = new RuntimeException("bang");

		Promise<Long> p = ps.forEachEvent(i -> {
			throw re;
		});

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertSame(re, p.getFailure());
	}

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

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertSame(re, p.getFailure());
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * toArray collects together all the event data in a single array which is
	 * used to resolve the returned promise
	 */
	public void testTerminalOperationToArray() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Object[]> p = ps.toArray();

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(gen.fixedBackPressure());
		assertEquals(Arrays.asList(0, 1, 2, 3, 4),
				Arrays.asList(p.getValue()));
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * reduce uses a Binary Operator function to combine event data into a
	 * single object. The promise is resolved with the final result when the
	 * stream finishes
	 */
	public void testTerminalOperationReduce() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Optional<Integer>> p = ps.reduce((a, b) -> {
			return a + b;
		});

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(gen.fixedBackPressure());
		assertEquals(10, (int) p.getValue().get());
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * reduce uses a Binary Operator function to combine event data into a
	 * single object. The promise is resolved with the final result when the
	 * stream finishes
	 */
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

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(gen.fixedBackPressure());
		assertEquals("abcde", p.getValue());
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * collect uses the Java Collector API to collect the data from events into
	 * a single Collection, Map, or other type
	 */
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

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertEquals(Arrays.asList(0, 1, 2, 3, 4), p.getValue());
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
	public void testTerminalOperationMin() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Optional<Integer>> p = ps.min((i1, i2) -> {
			return i1.intValue() == i2.intValue() ? 0
					: (i1.intValue() < i2.intValue() ? -1 : 1);
		});

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertEquals(0, (int) p.getValue().get());
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * max uses a Comparator to find the largest data element in the stream of
	 * data. The promise is resolved with the final result when the stream
	 * finishes
	 */
	public void testTerminalOperationMax() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Optional<Integer>> p = ps.max((i1, i2) -> {
			return i1.intValue() == i2.intValue() ? 0
					: (i1.intValue() < i2.intValue() ? -1 : 1);
		});

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertEquals(4, (int) p.getValue().get());
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * count operation counts the number of events that reach the end of the
	 * stream pipeline
	 */
	public void testTerminalOperationCount() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Long> p = ps.count();

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertEquals(5l, (long) p.getValue());
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * allMatch resolves with false if any event reaches the end of the stream
	 * pipeline does that does not match the predicate. If the stream ends
	 * without any data that doesn't match the predicate then the promise
	 * resolves with true
	 */
	public void testTerminalOperationAllMatch() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Boolean> p = ps.allMatch(i -> {
			return i.intValue() % 2 == 0;
		});

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertFalse(p.getValue());

		p = ps.allMatch(i -> {
			return i.intValue() > -1;
		});

		gen.getExecutionThread().join();

		timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertTrue(p.getValue());
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
	public void testTerminalOperationAnyMatch() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Boolean> p = ps.anyMatch(i -> {
			return i.intValue() % 2 == 0;
		});

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertTrue(p.getValue());
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * Not really specified
	 */
	public void testTerminalOperationNoneMatch() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Boolean> p = ps.noneMatch(i -> {
			return i.intValue() % 2 == 0;
		});

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertFalse(p.getValue());

		p = ps.noneMatch(i -> {
			return i.intValue() < 0;
		});

		gen.getExecutionThread().join();

		timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertTrue(p.getValue());
	}

	/**
	 * 706.3.1.3 : Terminal Operations
	 * <p/>
	 * findFirst resolves with an Optional representing the data from the first
	 * event that reaches the end of the pipeline. If the stream ends without
	 * any data reaching the end of the pipeline then the promise resolves with
	 * an empty Optional
	 */
	public void testTerminalOperationFindFirst() throws Exception {

		ExtGenerator gen = new ExtGenerator(5);
		PushStream<Integer> ps = new PushStreamProvider().createStream(gen);

		Promise<Optional<Integer>> p = ps.findFirst();

		gen.getExecutionThread().join();

		int timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertEquals(0, (int) p.getValue().get());

		gen = new ExtGenerator(0);
		ps = new PushStreamProvider().createStream(gen);

		p = ps.findFirst();

		gen.getExecutionThread().join();

		timeout = 5100;
		while (!p.isDone() && (timeout -= 100) > 0)
			Thread.sleep(100);

		assertTrue(p.isDone());
		assertFalse(p.getValue().isPresent());
	}
}
