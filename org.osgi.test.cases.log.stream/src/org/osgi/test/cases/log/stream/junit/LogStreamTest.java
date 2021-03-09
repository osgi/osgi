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
package org.osgi.test.cases.log.stream.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.osgi.test.assertj.promise.PromiseAssert.assertThat;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogService;
import org.osgi.service.log.stream.LogStreamProvider;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.test.support.tracker.Tracker;
import org.osgi.util.promise.Promise;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.tracker.ServiceTracker;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class LogStreamTest {
	private static int									SLEEP	= 1000;

	@InjectBundleContext
	BundleContext										context;
	@InjectService
	LogService											logService;
	ServiceTracker<LogStreamProvider,LogStreamProvider>	logStreamProviderTracker;
	LogStreamProvider									logStreamProvider;

	String												testMethodName;
	@BeforeEach
	public void setUp(TestInfo info) throws Exception {
		testMethodName = info.getTestMethod().map(Method::getName).get();
		logStreamProviderTracker = new ServiceTracker<LogStreamProvider,LogStreamProvider>(
				context, LogStreamProvider.class, null);
		logStreamProviderTracker.open();

		// logService = Tracker.waitForService(logServiceTracker, SLEEP);
		logStreamProvider = Tracker.waitForService(logStreamProviderTracker,
				SLEEP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logStreamProviderTracker.close();
	}

	@Test
	public void testLogStreamIsBuffered() throws Exception {
		LongUnaryOperator backPressure = i -> 100L;
		for (int i = 0; i < 5; i++) {
			logService.getLogger(testMethodName)
					.audit(String.valueOf(i + 1));
		}

		CountDownLatch latch = new CountDownLatch(30);

		AtomicLong lastEventTime = new AtomicLong();
		try (PushStream<LogEntry> stream = logStreamProvider
				.createStream(LogStreamProvider.Options.HISTORY)) {

			for (int i = 5; i < 10; i++) {
				logService.getLogger(testMethodName)
						.audit(String.valueOf(i + 1));
			}

			long startTime = System.nanoTime();
			stream.adjustBackPressure(backPressure)
					.filter(l -> testMethodName
							.equals(l.getLoggerName()))
					.forEach(l -> {
						System.out.printf("%s[%s]: %s%n", l.getLoggerName(),
								l.getSequence(), l.getMessage());
						latch.countDown();
						lastEventTime.set(System.nanoTime());
					});

			for (int i = 10; i < 30; i++) {
				logService.getLogger(testMethodName)
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
			logService.getLogger(testMethodName)
					.audit(String.valueOf(i + 1));
		}

		CountDownLatch latch = new CountDownLatch(150);
		try (PushStream<LogEntry> stream = logStreamProvider
				.createStream(LogStreamProvider.Options.HISTORY)) {

			for (int i = 50; i < 100; i++) {
				logService.getLogger(testMethodName)
						.audit(String.valueOf(i + 1));
			}

			stream.filter(
					l -> testMethodName.equals(l.getLoggerName()))
					.forEach(l -> {
						System.out.printf("%s[%s]: %s%n", l.getLoggerName(),
								l.getSequence(), l.getMessage());
						latch.countDown();
					});

			for (int i = 100; i < 150; i++) {
				logService.getLogger(testMethodName)
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
			ps.filter(l -> testMethodName.equals(l.getLoggerName()))
					.forEach(l -> {
						System.out.printf("%s[%s]: %s%n", l.getLoggerName(),
								l.getSequence(), l.getMessage());
						messageSet.remove(l.getMessage());
						latch.countDown();
					});

			logService.getLogger(testMethodName).audit("test1");
			logService.getLogger(testMethodName).audit("test2");
			logService.getLogger(testMethodName).audit("test3");

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
					l -> testMethodName.equals(l.getLoggerName()))
					.filter(l -> l.getMessage().startsWith("count"))
					.count();

			logService.getLogger(testMethodName).audit("count1");
			logService.getLogger(testMethodName).audit("count2");
			logService.getLogger(testMethodName).audit("nocount1");
			logService.getLogger(testMethodName).audit("count3");
			logService.getLogger(testMethodName).audit("nocount2");

			latch.await(SLEEP, TimeUnit.MILLISECONDS);
		} finally {
			// ungetService closes the stream
			logStreamProviderTracker.close();
		}
		assertThat(p).as("Incorrect count")
				.resolvesWithin(5L, TimeUnit.SECONDS)
				.hasValue(3L);
	}

}
