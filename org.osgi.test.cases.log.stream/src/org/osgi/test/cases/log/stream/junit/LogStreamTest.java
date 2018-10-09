/*******************************************************************************
 * Copyright (c) 2007, 2014 IBM Corporation and others All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.osgi.test.cases.log.stream.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogService;
import org.osgi.service.log.stream.LogStreamProvider;
import org.osgi.test.support.junit4.AbstractOSGiTestCase;
import org.osgi.test.support.tracker.Tracker;
import org.osgi.util.promise.Promise;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.tracker.ServiceTracker;

public class LogStreamTest extends AbstractOSGiTestCase {
	private static int									SLEEP	= 1000;

	ServiceTracker<LogService,LogService>				logServiceTracker;
	LogService											logService;
	ServiceTracker<LogStreamProvider,LogStreamProvider>	logStreamProviderTracker;
	LogStreamProvider									logStreamProvider;

	@Before
	public void setUp() throws Exception {
		logServiceTracker = new ServiceTracker<LogService,LogService>(
				getContext(), LogService.class, null);
		logServiceTracker.open();

		logStreamProviderTracker = new ServiceTracker<LogStreamProvider,LogStreamProvider>(
				getContext(), LogStreamProvider.class, null);
		logStreamProviderTracker.open();

		logService = Tracker.waitForService(logServiceTracker, SLEEP);
		logStreamProvider = Tracker.waitForService(logStreamProviderTracker,
				SLEEP);
	}

	@After
	public void tearDown() throws Exception {
		logServiceTracker.close();

		logStreamProviderTracker.close();
	}

	@Test
	public void testLogStreamIsBuffered() throws Exception {
		LongUnaryOperator backPressure = i -> 100L;
		for (int i = 0; i < 5; i++) {
			logService.getLogger(testName.getMethodName())
					.audit(String.valueOf(i + 1));
		}

		CountDownLatch latch = new CountDownLatch(30);

		AtomicLong lastEventTime = new AtomicLong();
		try (PushStream<LogEntry> stream = logStreamProvider
				.createStream(LogStreamProvider.Options.HISTORY)) {

			for (int i = 5; i < 10; i++) {
				logService.getLogger(testName.getMethodName())
						.audit(String.valueOf(i + 1));
			}

			long startTime = System.nanoTime();
			stream.adjustBackPressure(backPressure)
					.filter(l -> testName.getMethodName()
							.equals(l.getLoggerName()))
					.forEach(l -> {
						System.out.printf("%s[%s]: %s%n", l.getLoggerName(),
								l.getSequence(), l.getMessage());
						latch.countDown();
						lastEventTime.set(System.nanoTime());
					});

			for (int i = 10; i < 30; i++) {
				logService.getLogger(testName.getMethodName())
						.audit(String.valueOf(i + 1));
			}

			assertThat(latch.getCount())
					.as("Latch is not zero indicating we blocked.")
					.isPositive();

			latch.await(4, TimeUnit.SECONDS);
			assertThat(latch.getCount()).as("Did not get full history")
					.isZero();

			long timeForConsumer = TimeUnit.NANOSECONDS
					.toMillis(lastEventTime.get() - startTime);
			// making sure the timeForConsumer is greater than 2900
			// We fire 30 events but there can be jitter on the delay
			// so we allow a small margin
			assertThat(timeForConsumer)
					.as("Did not take long enough: %s", timeForConsumer)
					.isGreaterThan(2900);
		}
	}

	@Test
	public void testLogWithHistory() throws Exception {
		for (int i = 0; i < 50; i++) {
			logService.getLogger(testName.getMethodName())
					.audit(String.valueOf(i + 1));
		}

		CountDownLatch latch = new CountDownLatch(150);
		try (PushStream<LogEntry> stream = logStreamProvider
				.createStream(LogStreamProvider.Options.HISTORY)) {

			for (int i = 50; i < 100; i++) {
				logService.getLogger(testName.getMethodName())
						.audit(String.valueOf(i + 1));
			}

			stream.filter(
					l -> testName.getMethodName().equals(l.getLoggerName()))
					.forEach(l -> {
						System.out.printf("%s[%s]: %s%n", l.getLoggerName(),
								l.getSequence(), l.getMessage());
						latch.countDown();
					});

			for (int i = 100; i < 150; i++) {
				logService.getLogger(testName.getMethodName())
						.audit(String.valueOf(i + 1));
			}

			latch.await(SLEEP, TimeUnit.MILLISECONDS);
			assertThat(latch.getCount()).as("Did not get full history")
					.isZero();
		}
	}

	@Test
	public void testStreamSize() throws Exception {
		Set<String> messageSet = new HashSet<String>();
		messageSet.add("test1");
		messageSet.add("test2");
		messageSet.add("test3");

		CountDownLatch latch = new CountDownLatch(messageSet.size());
		try (PushStream<LogEntry> ps = logStreamProvider.createStream()) {
			ps.filter(l -> testName.getMethodName().equals(l.getLoggerName()))
					.forEach(l -> {
						System.out.printf("%s[%s]: %s%n", l.getLoggerName(),
								l.getSequence(), l.getMessage());
						messageSet.remove(l.getMessage());
						latch.countDown();
					});

			logService.getLogger(testName.getMethodName()).audit("test1");
			logService.getLogger(testName.getMethodName()).audit("test2");
			logService.getLogger(testName.getMethodName()).audit("test3");

			latch.await(SLEEP, TimeUnit.MILLISECONDS);
		}
		assertThat(messageSet).as("Some number of messages, >0 not in stream")
				.isEmpty();
	}

	@Test
	public void testCloseOnUnget() throws Exception {
		Promise<Long> p = null;
		CountDownLatch latch = new CountDownLatch(1);
		PushStream<LogEntry> stream = logStreamProvider.createStream();
		try {
			p = stream.filter(
					l -> testName.getMethodName().equals(l.getLoggerName()))
					.filter(l -> l.getMessage().startsWith("count"))
					.count();

			logService.getLogger(testName.getMethodName()).audit("count1");
			logService.getLogger(testName.getMethodName()).audit("count2");
			logService.getLogger(testName.getMethodName()).audit("nocount1");
			logService.getLogger(testName.getMethodName()).audit("count3");
			logService.getLogger(testName.getMethodName()).audit("nocount2");

			latch.await(SLEEP, TimeUnit.MILLISECONDS);
		} finally {
			// ungetService closes the stream
			logStreamProviderTracker.close();
		}
		long count = p.getValue();
		assertThat(count).as("Incorrect count").isEqualTo(3);
	}

}
