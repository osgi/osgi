package org.osgi.test.cases.coordinator.junit;

import java.util.concurrent.*;

import junit.framework.*;

import org.osgi.framework.*;
import org.osgi.service.coordinator.*;
import org.osgi.util.tracker.*;

public class SpecExamples extends TestCase {
	Coordinator	coordinator;

	SpecExamples() throws Exception {
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
