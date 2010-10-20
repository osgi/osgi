package org.osgi.test.cases.coordinator.junit;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import junit.framework.*;

import org.osgi.framework.*;
import org.osgi.service.coordinator.*;
import org.osgi.util.tracker.*;

class TestParticipant implements Participant {
	AtomicInteger	ended	= new AtomicInteger(0);
	AtomicInteger	failed	= new AtomicInteger(0);

	public void failed(Coordination c) throws Exception {
		failed.incrementAndGet();
	}

	public void ended(Coordination c) throws Exception {
		ended.incrementAndGet();
	}
}

public class CoordinatorBasicTest extends TestCase {

	
	
	
	
	
	
	/**
	 * Test if the timeout kicks in and fails the participants.
	 * 
	 * @throws Exception
	 */
	public void testTimeout() throws Exception {
		Coordinator c = (Coordinator) getService(Coordinator.class);
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp = new TestParticipant();		
		Coordination cc = c.create("test1", 100);
		
		try {
			cc.addParticipant( tp );
			Thread.sleep(200);
		} catch( Exception e ) {
			fail();
		} finally {
			try {
				assertTrue("must be terminated", cc.isTerminated());
				assertEquals("must have a timeout failure", new TimeoutException(), cc.getFailure());
				assertEquals("Ended must be 0", 0, tp.ended.get());
				assertEquals("Failed must be 1", 1, tp.failed.get());
				cc.end();
			} catch( CoordinationException ce ) {
				assertEquals("except a timeout exception ", CoordinationException.TIMEDOUT, ce.getReason());
			}
		}
		
	}
	
	/** 
	 * Test the basic normal setup.
	 * @throws Exception
	 */
	public void testBasic() throws Exception {
		Coordinator c = (Coordinator) getService(Coordinator.class);
		assertNotNull("Expect the service to be there", c);
		TestParticipant tp = new TestParticipant();		
		Coordination cc = c.create("test1", 0);
		
		try {
			cc.addParticipant( tp );
		} catch( Exception e ) {
			fail();
		} finally {
			cc.end();
		}
		
		assertTrue("must be terminated", cc.isTerminated());
		assertNull("must have no failure", cc.getFailure());
		assertEquals("Ended must be 1", 1, tp.ended.get());
		assertEquals("Failed must be 0", 0, tp.failed.get());
	}


	private Object getService(Class c) throws InterruptedException {
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
