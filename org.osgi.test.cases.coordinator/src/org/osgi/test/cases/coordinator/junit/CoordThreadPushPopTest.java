package org.osgi.test.cases.coordinator.junit;

import org.osgi.framework.ServiceReference;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.test.support.OSGiTestCase;

/**
 * The thread associated with a coordination must be consistent with it being 
 * pushed to or popped from a thread local stack.
 */
public class CoordThreadPushPopTest extends OSGiTestCase {
	private Coordinator coordinator;
	private ServiceReference coordinatorReference;
	
	/**
	 * Pushing a coordination.
	 */
	public void testPushCoordination() {
		Coordination c = coordinator.create("c", 0);
		assertCoordinationThreadSame(c, null);
		c.push();
		assertCoordinationThreadSame(c, Thread.currentThread());
		coordinator.pop();
		assertCoordinationThreadSame(c, null);
		c.end();
	}
	
	/**
	 * Beginning a coordination.
	 */
	public void testBeginCoordination() {
		Coordination c = coordinator.begin("c", 0);
		assertCoordinationThreadSame(c, Thread.currentThread());
		coordinator.pop();
		assertCoordinationThreadSame(c, null);
		c.end();
	}
	
	protected void setUp() throws Exception {
		coordinatorReference = getContext().getServiceReference(Coordinator.class.getName());
		coordinator = (Coordinator)getContext().getService(coordinatorReference);
	}
	
	protected void tearDown() throws Exception {
		getContext().ungetService(coordinatorReference);
	}
	
	private void assertCoordinationThreadSame(Coordination c, Thread expected) {
		assertSame("A coordination's thread must be consistent with push and pop", expected, c.getThread());
	}
}
