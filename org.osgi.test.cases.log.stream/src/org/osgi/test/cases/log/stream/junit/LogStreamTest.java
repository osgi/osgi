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
	
	public void testLogWithHistory() throws Exception {
		for (int i = 0; i < 50; i++) {
			logService.getLogger("Test1").audit(String.valueOf(i + 1));
		}

		PushStream<LogEntry> stream = logStreamProvider
				.createStream(LogStreamProvider.Options.HISTORY);
		CountDownLatch latch = new CountDownLatch(150);

		for (int i = 50; i < 100; i++) {
			logService.getLogger("Test1").audit(String.valueOf(i + 1));
		}

		stream.forEach(l -> {
			System.out.println(l.getMessage());
			latch.countDown();
		}).onResolve(
				() -> System.out.println("Stream closed")
				);

		for (int i = 100; i < 150; i++) {
			logService.getLogger("Test1").audit(String.valueOf(i + 1));
		}

		latch.await(100, TimeUnit.MILLISECONDS);
		assertEquals("Did not get full history", 0, latch.getCount());
		stream.close();
	}

	public void testStreamSize() throws Exception {

		PushStream<LogEntry> ps = logStreamProvider
				.createStream();
		@SuppressWarnings("serial")
		final Set<String> messageSet = new HashSet<String>() {

			{
				add("test1");
				add("test2");
				add("test3");
			}
		};

		CountDownLatch latch = new CountDownLatch(messageSet.size());
		ps.forEach(m -> {
			messageSet.remove(m.getMessage());
			latch.countDown();
		});

		logService.getLogger("Test2").audit("test1");
		logService.getLogger("Test2").audit("test2");
		logService.getLogger("Test2").audit("test3");

		latch.await(10, TimeUnit.MILLISECONDS);
		ps.close();
		assertEquals("Some number of message, >0 not in stream", 0,
				messageSet.size());

	}

	public void testCloseOnUnget() throws Exception {

		PushStream<LogEntry> stream = logStreamProvider.createStream();
		CountDownLatch latch = new CountDownLatch(1);

		Promise<Long> p = stream.filter(m -> m.getMessage().equals("test1"))
				.count();

		logService.getLogger("Test3").audit("test1");
		logService.getLogger("Test3").audit("test1");
		logService.getLogger("Test3").audit("test2");
		logService.getLogger("Test3").audit("test3");

		latch.await(10, TimeUnit.MILLISECONDS);
		
		// ungetService closes the stream
		getContext().ungetService(logStreamProviderReference);

		long count = p.getValue();

		assertEquals("Incorrect count", 2, count);

	}


}
