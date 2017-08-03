package org.osgi.test.cases.pushstream.junit;

import static java.time.Duration.ofSeconds;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.*;
import static java.util.stream.Collectors.toList;
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
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.util.promise.Promise;
import org.osgi.util.pushstream.PushEvent;
import org.osgi.util.pushstream.PushEventConsumer;
import org.osgi.util.pushstream.PushEventSource;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamProvider;

import junit.framework.TestCase;

@SuppressWarnings("boxing")
public class PushStreamTest extends TestCase {

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
	
	@Override
	public void setUp() {
		impl = new PushStreamProvider();
	}
	
	private <T> PushStream<T> withLessBackPressure(PushEventSource<T> source) {
		return impl.buildStream(source)
				.withPushbackPolicy(LINEAR, 20)
				.withQueuePolicy(FAIL)
				.build();
	}
	
	public void testSimple() throws Exception {
		doTestSimple(impl.createStream(new Generator()));
	}

	public void testSimpleWithLessBackPressure() throws Exception {
		doTestSimple(withLessBackPressure(new Generator()));
	}

	private void doTestSimple(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		assertEquals(Long.valueOf(5L), es
				.filter((x) -> (x&1) == 0)
				.count().getValue());
		
		es = impl.createStream(new Generator());
		assertEquals(Integer.valueOf(9),
				es.max(Integer::compare).getValue().get());
		
		es = impl.createStream(new Generator());
		assertEquals(Integer.valueOf(0), es.findFirst().getValue().get());
		
		es = impl.createStream(new Generator());
		assertEquals(Integer.valueOf(45),
				es.reduce(0, (a, b) -> a + b).getValue());
	}
	
	public void testFork() throws Exception {
		doTestFork(impl.createStream(new Generator()));
	}

	public void testForkWithLessBackPressure() throws Exception {
		doTestFork(withLessBackPressure(new Generator()));
	}

	private void doTestFork(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		ExecutorService e = Executors.newCachedThreadPool();
		try {
			assertEquals(Integer.valueOf(45),
				es.fork(4, 0, e).reduce(0, (a, b) -> a + b).getValue());
		} finally {
			e.shutdown();
		}
	}
	
	public void testCoalesce() throws Exception {
		doTestCoalesce(impl.createStream(new Generator(50)));
	}

	public void testCoalesceWithLessBackPressure() throws Exception {
		doTestCoalesce(withLessBackPressure(new Generator(50)));
	}

	private void doTestCoalesce(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		AtomicInteger sum = new AtomicInteger();
		AtomicInteger counter = new AtomicInteger();
		
		List<Integer> list = es.sequential().coalesce( (element) -> {
			int s = sum.accumulateAndGet(element.intValue(), (a, b) -> a + b);
			if ( (counter.incrementAndGet() % 3) == 0) {
				sum.set(0);
				return Optional.of(Integer.valueOf(s));
			}
			else
				return Optional.empty();
		}).collect(toList()).getValue();
		
		assertEquals(asList(3,12,21,30,39,48,57,66,75,84,93,102,111,120,129,138),list);
	}

	public void testCoalesce2() throws Exception {
		doTestCoalesce2(impl.createStream(new Generator(50)));
	}

	public void testCoalesce2WithLessBackPressure() throws Exception {
		doTestCoalesce2(withLessBackPressure(new Generator(50)));
	}

	private void doTestCoalesce2(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		List<Integer> list = es.coalesce(3, (elements) -> {
			int sum = 0;
			for(Integer i : elements) {
				sum += i.intValue();
			}
			return Integer.valueOf(sum);
		}).collect(toList()).getValue();
		
		assertEquals(asList(3,12,21,30,39,48,57,66,75,84,93,102,111,120,129,138,97),list);
	}

	public void testCoalesce3() throws Exception {
		doTestCoalesce3(impl.createStream(new Generator(50)));
	}

	public void testCoalesce3WithLessBackPressure() throws Exception {
		doTestCoalesce3(withLessBackPressure(new Generator(50)));
	}

	private void doTestCoalesce3(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		List<Integer> list = es.sequential().coalesce(3, 
				(elements) -> elements.stream().reduce(0, (a,b) -> a + b))
				.collect(toList()).getValue();
		
		assertEquals(asList(3,12,21,30,39,48,57,66,75,84,93,102,111,120,129,138,97),list);
	}

	public void testWindow() throws Exception {
		doTestWindow(impl.createStream(new Generator(50)));
	}

	public void testWindowWithLessBackPressure() throws Exception {
		doTestWindow(withLessBackPressure(new Generator(50)));
	}

	private void doTestWindow(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		List<Integer> list = es.window(Duration.ofMillis(200), Collection::size)
			.collect(toList()).getValue();
		
		assertFalse("List is too big: " + list, list.size() > 10);
		
		assertEquals(list.toString(), Integer.valueOf(50), list.stream().reduce(0, (a,b) -> a + b));
	}

	public void testWindow2() throws Exception {
		doTestWindow2(impl.createStream(new Generator(50)));
	}

	public void testWindow2WithLessBackPressure() throws Exception {
		doTestWindow2(withLessBackPressure(new Generator(50)));
	}

	private void doTestWindow2(PushStream<Integer> es)
			throws InvocationTargetException, InterruptedException {
		List<Integer> list = es.window(() -> Duration.ofMillis(200), () -> 5,
				(t, c) -> c.size())
				.collect(toList()).getValue();
		
		assertTrue("List is too small: " + list, list.size() > 9);
		assertFalse("List is too big: " + list, list.size() > 14);
		
		try {
			assertEquals(list.toString(), Integer.valueOf(50), list.stream().reduce(0, 
					(a,b) -> {
						System.out.println("" + a + " " + b);
								return Integer.valueOf(a + b);
					}));
		} catch (Exception e) {
			System.out.println(list);
		}
	}

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
		assertTrue(counts.isDone());
		assertEquals(2, counts.getValue().size());

	}

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
		assertTrue(counts.isDone());
		assertEquals(Integer.valueOf(0), counts.getValue().get());

	}
}
