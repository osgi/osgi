package org.osgi.test.cases.coordinator.junit;

import org.osgi.framework.ServiceReference;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.test.support.OSGiTestCase;

/**
 * A NullPointerException must be thrown if the reason specified for a 
 * coordination's failure is null.
 */
public class NPENullFailureReasonTest extends OSGiTestCase {
	private Coordinator coordinator;
	private ServiceReference coordinatorReference;
	
	/**
	 * Coordination.fail(Throwable)
	 */
	public void testCoordinationFail() {
		NullPointerException npe = null;
		Coordination c = coordinator.create("c", 0);
		try {
			c.fail(null);
		}
		catch (NullPointerException e) {
			npe = e;
		}
		c.end();
		assertNullPointerException(npe);
	}
	
	/**
	 * Coordinator.fail(Throwable)
	 */
	public void testCoordinatorFail() {
		NullPointerException npe = null;
		Coordination c = coordinator.begin("c", 0);
		try {
			coordinator.fail(null);
		}
		catch (NullPointerException e) {
			npe = e;
		}
		c.end();
		assertNullPointerException(npe);
	}
	
	protected void setUp() throws Exception {
		coordinatorReference = getContext().getServiceReference(Coordinator.class.getName());
		coordinator = (Coordinator)getContext().getService(coordinatorReference);
	}
	
	protected void tearDown() throws Exception {
		getContext().ungetService(coordinatorReference);
	}
	
	private void assertNullPointerException(NullPointerException e) {
		assertNotNull("An NPE must be thrown if the reason for failure is null", e);
	}
}
