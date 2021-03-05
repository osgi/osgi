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

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.osgi.util.pushstream.PushStreamProvider;
import org.osgi.util.pushstream.SimplePushEventSource;

import junit.framework.TestCase;

public class SimplePushEventSourceTest extends TestCase {

	public void testSimplePushEventSource() throws Exception {
		
		PushStreamProvider psp = new PushStreamProvider();
		
		SimplePushEventSource<Integer> spes = psp.createSimpleEventSource(Integer.class);
		
		
		Thread publisher = new Thread(() -> {
			
			try {
				spes.connectPromise().getValue();
				
				for(int i=1; i <= 30; i++) {
					spes.publish(Integer.valueOf(i));
					Thread.sleep(100);
				}
			} catch (Exception e) {
				spes.error(e);
			} finally {
				spes.endOfStream();
			}

		});

		publisher.start();

		List<Integer> received = new CopyOnWriteArrayList<>();

		Semaphore latch = new Semaphore(0);

		long startTime = System.currentTimeMillis();

		spes.open(pe -> {
			if (pe.isTerminal()) {
				latch.release();
			} else {
				received.add(pe.getData());
			}
			
			return 250L;
		});
		
		publisher.join();

		// The publisher should run for 3 seconds
		long publishTime = System.currentTimeMillis() - startTime;

		assertTrue(publishTime > 2500);
		assertTrue(publishTime < 4000);

		// The receiver should not yet be done!
		assertEquals(0, latch.availablePermits());

		// The receiver should finish after another 4.5 seconds
		assertTrue(latch.tryAcquire(6, SECONDS));

		assertEquals(
				asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
						17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30),
				received);

		received.clear();

		// No more events should arrive
		assertFalse(spes.isConnected());

		spes.publish(Integer.valueOf(42));
		spes.endOfStream();

		assertFalse(latch.tryAcquire(3, SECONDS));

		spes.open(pe -> {
			if (pe.isTerminal()) {
				latch.release();
			}
			return 0;
		});

		spes.close();

		assertTrue(latch.tryAcquire(100, MILLISECONDS));
	}

}
