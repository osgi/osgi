/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.coordinator.junit;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import junit.framework.TestCase;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.util.tracker.ServiceTracker;

public class SpecExamples extends TestCase {
	Coordinator	coordinator;

	protected void setUp() throws Exception {
		coordinator = (Coordinator) getService(Coordinator.class);
	}

	public void testLooseCoordination() throws Exception {
		Executor executor = Executors.newCachedThreadPool();
		final Semaphore latch = new Semaphore(0);
		final Coordination c = coordinator.create("name", 0);

		for (int i = 0; i < 10; i++) {
			executor.execute(new Runnable() {
				public void run() {
					baz(c);
					latch.release(1);
				}
			});
		}
		latch.acquire(10);
		c.end();
	}
	
	void baz(Coordination c) {
		
	}

	private Object getService(Class< ? > c) throws InterruptedException {
		BundleContext context = FrameworkUtil.getBundle(
				CoordinatorBasicTest.class).getBundleContext();
		ServiceTracker t = new ServiceTracker(context, c.getName(), null) {
			public Object addingService(ServiceReference ref) {
				Object o = super.addingService(ref);
				return o;
			}
		};
		t.open();
		return t.waitForService(1000);
	}

}
