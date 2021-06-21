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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.osgi.test.cases.pushstream.junit.PushStreamComplianceTest.ExtGenerator;
import org.osgi.util.pushstream.PushEvent.EventType;
import org.osgi.util.pushstream.PushEventConsumer;
import org.osgi.util.pushstream.PushEventSource;
import org.osgi.util.pushstream.PushStream;
import org.osgi.util.pushstream.PushStreamProvider;
import org.osgi.util.pushstream.PushbackPolicyOption;

public class PushEventStreamToPushEventSourceTest {

	@Test
	public void testPushStreamToSource() throws Exception {
		
		PushStreamProvider psp = new PushStreamProvider();
		
		ExtGenerator generator = new ExtGenerator(100);

		PushStream<Integer> stream = psp.buildStream(generator)
				.unbuffered()
				.build()
				.adjustBackPressure(l -> Math.max(l, 20));
		
		PushEventSource<Integer> source = psp
				.buildEventSourceFromStream(stream)
				.withBuffer(new ArrayBlockingQueue<>(4))
				.withPushbackPolicy(PushbackPolicyOption.ON_FULL_FIXED, 100)
				.build();
		
		List<Integer> list = Collections.synchronizedList(new ArrayList<>());
		Semaphore latch = new Semaphore(0);
		AtomicReference<Throwable> failure = new AtomicReference<>();

		PushEventConsumer< ? super Integer> consumer = e -> {
			if (e.isTerminal()) {
				if (e.getType() == EventType.ERROR) {
					failure.set(e.getFailure());
				}
				latch.release();
				return -1;
			} else {
				list.add(e.getData());
				return 0;
				}
		};
			
		source.open(consumer);

		assertTrue(latch.tryAcquire(5, TimeUnit.SECONDS));
		assertEquals(100, list.size());
		assertNull(failure.get());

		list.clear();

		// Check the terminal is remembered
		source.open(consumer);
		
		assertTrue(latch.tryAcquire(500, TimeUnit.MILLISECONDS));
		assertTrue(list.isEmpty());
		assertNull(failure.get());

		assertTrue(generator.fixedBackPressure());
		assertEquals(20, generator.maxBackPressure());
	}

	@Test
	public void testPushStreamToSourceDoesNotAutoClose() throws Exception {

		PushStreamProvider psp = new PushStreamProvider();

		ExtGenerator generator = new ExtGenerator(10000);

		PushStream<Integer> stream = psp.buildStream(generator)
				.unbuffered()
				.build()
				.adjustBackPressure(l -> Math.max(l, 20));

		PushEventSource<Integer> source = psp
				.createEventSourceFromStream(stream);

		List<Integer> list = Collections.synchronizedList(new ArrayList<>());
		Semaphore latch = new Semaphore(0);
		AtomicReference<Throwable> failure = new AtomicReference<>();

		PushEventConsumer< ? super Integer> consumer = e -> {
			if (e.isTerminal()) {
				if (e.getType() == EventType.ERROR) {
					failure.set(e.getFailure());
				}
				latch.release();
				return -1;
			} else {
				list.add(e.getData());
				return list.size() < 20 ? 0 : -1;
			}
		};

		source.open(consumer);

		assertTrue(latch.tryAcquire(2, TimeUnit.SECONDS));
		assertTrue(list.size() == 20);
		assertTrue(list.get(0) == 0);
		assertNull(failure.get());

		list.clear();

		source.open(consumer);
		assertTrue(latch.tryAcquire(2, TimeUnit.SECONDS));
		assertTrue(list.size() == 20);
		assertFalse(list.get(0) == 0);
		assertNull(failure.get());

		list.clear();

		source.open(consumer);
		Thread.sleep(50);
		stream.close();

		assertTrue(latch.tryAcquire(1, TimeUnit.SECONDS));
		assertFalse(list.isEmpty());
		assertTrue(list.size() < 20);
		assertNull(failure.get());
	}

	@Test
	public void testPushStreamToSourceMultiplexing() throws Exception {

		PushStreamProvider psp = new PushStreamProvider();

		ExtGenerator generator = new ExtGenerator(10000);

		PushStream<Integer> stream = psp.buildStream(generator)
				.unbuffered()
				.build()
				.adjustBackPressure(l -> Math.max(l, 20));

		PushEventSource<Integer> source = psp
				.buildEventSourceFromStream(stream)
				.withBuffer(new ArrayBlockingQueue<>(10))
				.withPushbackPolicy(PushbackPolicyOption.LINEAR, 100)
				.build();

		List<Integer> list1 = Collections.synchronizedList(new ArrayList<>());
		Semaphore latch1 = new Semaphore(0);
		AtomicReference<Throwable> failure1 = new AtomicReference<>();

		PushEventConsumer< ? super Integer> consumer1 = e -> {
			if (e.isTerminal()) {
				if (e.getType() == EventType.ERROR) {
					failure1.set(e.getFailure());
				}
				latch1.release();
				return -1;
			} else {
				list1.add(e.getData());
				return list1.size() < 100 ? 0 : -1;
			}
		};

		List<Integer> list2 = Collections.synchronizedList(new ArrayList<>());
		Semaphore latch2 = new Semaphore(0);
		AtomicReference<Throwable> failure2 = new AtomicReference<>();

		PushEventConsumer< ? super Integer> consumer2 = e -> {
			if (e.isTerminal()) {
				if (e.getType() == EventType.ERROR) {
					failure2.set(e.getFailure());
				}
				latch2.release();
				return -1;
			} else {
				list2.add(e.getData());
				return list2.size() < 20 ? 80 : -1;
			}
		};

		List<Integer> list3 = Collections.synchronizedList(new ArrayList<>());
		Semaphore latch3 = new Semaphore(0);
		AtomicReference<Throwable> failure3 = new AtomicReference<>();

		RuntimeException re = new RuntimeException("Bang!");

		PushEventConsumer< ? super Integer> consumer3 = e -> {
			if (e.isTerminal()) {
				if (e.getType() == EventType.ERROR) {
					failure3.set(e.getFailure());
				}
				latch3.release();
				return -1;
			} else {
				list3.add(e.getData());
				if (list3.size() >= 10) {
					throw re;
				}
				return 0;
			}
		};

		source.open(consumer1);

		Thread.sleep(100);
		source.open(consumer2);

		Thread.sleep(100);
		source.open(consumer3);

		// 3 should finish first with an error

		assertTrue(latch3.tryAcquire(2, TimeUnit.SECONDS));
		assertEquals(10, list3.size());
		assertTrue(list3.get(0) > 1);
		assertSame(re, failure3.get());

		assertFalse(latch1.tryAcquire());
		assertFalse(latch2.tryAcquire());

		// Next is consumer 2 with no error

		assertTrue(latch2.tryAcquire(2, TimeUnit.SECONDS));
		assertEquals(20, list2.size());
		assertTrue(list2.get(0) > 1);
		assertNull(failure2.get());

		assertFalse(latch1.tryAcquire());

		generator.getExecutionThread().interrupt();

		assertTrue(latch1.tryAcquire(500, TimeUnit.MILLISECONDS));
		assertFalse(list1.isEmpty());
		assertTrue(list1.get(0) == 0);
		assertTrue(list1.size() < 100);
		assertTrue(failure1.get() instanceof InterruptedException);

		assertFalse(generator.fixedBackPressure());
		assertEquals(20, generator.minBackPressure());
		assertTrue(generator.maxBackPressure() < 100);
		assertTrue(generator.maxBackPressure() >= 80);

		Throwable ie = failure1.getAndSet(null);
		source.open(consumer1);

		assertTrue(latch1.tryAcquire(500, TimeUnit.MILLISECONDS));
		assertSame(ie, failure1.get());
	}

}
