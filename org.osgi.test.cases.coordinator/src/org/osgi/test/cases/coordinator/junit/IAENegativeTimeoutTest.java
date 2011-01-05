package org.osgi.test.cases.coordinator.junit;

import org.osgi.framework.ServiceReference;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.CoordinationException;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.test.support.OSGiTestCase;

/**
 * An IllegalArgumentException must be thrown when a timeout value is negative.
 */
public class IAENegativeTimeoutTest extends OSGiTestCase {
	private Coordinator coordinator;
	private ServiceReference coordinatorReference;
	
	/**
	 * Beginning a coordination.
	 */
	public void testBeginCoordination() {
		IllegalArgumentException iae = null;
		try {
			Coordination c = coordinator.begin("c", -1);
			c.end();
		}
		catch (IllegalArgumentException e) {
			iae = e;
		}
		assertIllegalArgumentException(iae);
	}
	
	/**
	 * Creating a coordination.
	 */
	public void testCreateCoordination() {
		IllegalArgumentException iae = null;
		try {
			Coordination c = coordinator.create("c", -5000);
			c.end();
		}
		catch (IllegalArgumentException e) {
			iae = e;
		}
		assertIllegalArgumentException(iae);
	}
	
	/**
	 * Extending a timeout.
	 */
	public void testCoordinationFail() {
		IllegalArgumentException iae = null;
		Coordination c = coordinator.create("c", 30000);
		try {
			c.extendTimeout(-30000);
		}
		catch (IllegalArgumentException e) {
			iae = e;
		}
		finally {
			c.end();
		}
		assertIllegalArgumentException(iae);
	}
	
	/**
	 * Joining a coordination.
	 * 
	 * @throws InterruptedException
	 */
	public void testCoordinationJoin() throws InterruptedException {
		IllegalArgumentException iae = null;
		Coordination c = coordinator.create("c", 2000);
		try {
			c.join(-100);
		}
		catch (IllegalArgumentException e) {
			iae = e;
		}
		finally {
			c.fail(new Exception());
		}
		assertIllegalArgumentException(iae);
	}
	
	protected void setUp() throws Exception {
		coordinatorReference = getContext().getServiceReference(Coordinator.class.getName());
		coordinator = (Coordinator)getContext().getService(coordinatorReference);
	}
	
	protected void tearDown() throws Exception {
		getContext().ungetService(coordinatorReference);
	}
	
	private void assertIllegalArgumentException(IllegalArgumentException e) {
		assertNotNull("An IllegalArgumentException must be thrown if a negative timeout is provided", e);
	}
}
