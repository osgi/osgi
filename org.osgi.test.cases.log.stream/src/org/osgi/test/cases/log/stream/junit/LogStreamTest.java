/*******************************************************************************
 * Copyright (c) 2007, 2014 IBM Corporation and others All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.osgi.test.cases.log.stream.junit;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.service.log.stream.LogStreamProvider;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.promise.Promise;
import org.osgi.util.pushstream.PushStream;

public class LogStreamTest extends OSGiTestCase {

	ServiceReference<LogService>		logServiceReference;
	LogService							logService;
	ServiceReference<LogReaderService>	logReaderServiceReference;
	LogReaderService					logReaderService;
	ServiceReference<LogStreamProvider>	logStreamProviderReference;
	LogStreamProvider					logStreamProvider;

	public void setUp() throws Exception {
		logServiceReference = getContext()
				.getServiceReference(LogService.class);
		logService = getContext().getService(logServiceReference);

		logReaderServiceReference = getContext()
				.getServiceReference(LogReaderService.class);
		logReaderService = getContext().getService(logReaderServiceReference);

		logStreamProviderReference = getContext()
				.getServiceReference(LogStreamProvider.class);
		logStreamProvider = getContext().getService(logStreamProviderReference);

	}

	public void tearDown() throws Exception {
		getContext().ungetService(logServiceReference);
		getContext().ungetService(logReaderServiceReference);
		getContext().ungetService(logStreamProviderReference);

	}

	public void testLogStreamIsBuffered() throws Exception {
		LongUnaryOperator backPressure = i -> 100L;
		for (int i = 0; i < 5; i++) {
			logService.getLogger(getName()).audit(String.valueOf(i + 1));
		}

		CountDownLatch latch = new CountDownLatch(30);

		AtomicLong lastEventTime = new AtomicLong();
		try (PushStream<LogEntry> stream = logStreamProvider
				.createStream(LogStreamProvider.Options.HISTORY)) {

			for (int i = 5; i < 10; i++) {
				logService.getLogger(getName()).audit(String.valueOf(i + 1));
			}

			long startTime = System.nanoTime();
			stream.adjustBackPressure(backPressure)
					.filter(l -> getName().equals(l.getLoggerName()))
					.forEach(l -> {
						System.out.printf("%s[%s]: %s%n", l.getLoggerName(),
								l.getSequence(), l.getMessage());
						latch.countDown();
						lastEventTime.set(System.nanoTime());
					});

			for (int i = 10; i < 30; i++) {
				logService.getLogger(getName()).audit(String.valueOf(i + 1));
			}

			assertTrue("Latch is not zero indicating we blocked.",
					latch.getCount() > 0);

			latch.await(4, TimeUnit.SECONDS);
			assertEquals("Did not get full history", 0, latch.getCount());

			long timeForConsumer = lastEventTime.get() - startTime;
			// making sure the timeForConsumer is greater than 2900
			// We fire 30 events but the back-pressure is only used for the
			// first 29 events, not the last event 30.
			assertTrue(
					"Did not take long enough: "
							+ TimeUnit.NANOSECONDS.toMillis(timeForConsumer),
					TimeUnit.NANOSECONDS.toMillis(timeForConsumer) > 2900);
		}
	}

	public void testLogWithHistory() throws Exception {
		for (int i = 0; i < 50; i++) {
			logService.getLogger(getName()).audit(String.valueOf(i + 1));
		}

		CountDownLatch latch = new CountDownLatch(150);
		try (PushStream<LogEntry> stream = logStreamProvider
				.createStream(LogStreamProvider.Options.HISTORY)) {

			for (int i = 50; i < 100; i++) {
				logService.getLogger(getName()).audit(String.valueOf(i + 1));
			}

			stream.filter(l -> getName().equals(l.getLoggerName()))
					.forEach(l -> {
						System.out.printf("%s[%s]: %s%n", l.getLoggerName(),
								l.getSequence(), l.getMessage());
						latch.countDown();
					});

			for (int i = 100; i < 150; i++) {
				logService.getLogger(getName()).audit(String.valueOf(i + 1));
			}

			latch.await(100, TimeUnit.MILLISECONDS);
			assertEquals("Did not get full history", 0, latch.getCount());
		}
	}

	public void testStreamSize() throws Exception {
		Set<String> messageSet = new HashSet<String>();
		messageSet.add("test1");
		messageSet.add("test2");
		messageSet.add("test3");

		CountDownLatch latch = new CountDownLatch(messageSet.size());
		try (PushStream<LogEntry> ps = logStreamProvider.createStream()) {
			ps.filter(l -> getName().equals(l.getLoggerName())).forEach(m -> {
				messageSet.remove(m.getMessage());
				latch.countDown();
			});

			logService.getLogger(getName()).audit("test1");
			logService.getLogger(getName()).audit("test2");
			logService.getLogger(getName()).audit("test3");

			latch.await(10, TimeUnit.MILLISECONDS);
		}
		assertEquals("Some number of message, >0 not in stream", 0,
				messageSet.size());
	}

	public void testCloseOnUnget() throws Exception {
		Promise<Long> p = null;
		CountDownLatch latch = new CountDownLatch(1);
		PushStream<LogEntry> stream = logStreamProvider.createStream();
		try {
			p = stream.filter(l -> getName().equals(l.getLoggerName()))
					.filter(m -> m.getMessage().startsWith("count"))
					.count();

			logService.getLogger(getName()).audit("count1");
			logService.getLogger(getName()).audit("count2");
			logService.getLogger(getName()).audit("nocount1");
			logService.getLogger(getName()).audit("count3");
			logService.getLogger(getName()).audit("nocount2");

			latch.await(10, TimeUnit.MILLISECONDS);
		} finally {
			// ungetService closes the stream
			getContext().ungetService(logStreamProviderReference);
		}
		long count = p.getValue();
		assertEquals("Incorrect count", 3, count);
	}

}
