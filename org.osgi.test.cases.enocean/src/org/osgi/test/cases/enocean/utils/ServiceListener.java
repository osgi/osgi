
package org.osgi.test.cases.enocean.utils;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.Semaphore;
import org.osgi.util.tracker.ServiceTracker;


public class ServiceListener extends ServiceTracker {

	public static final String	SERVICE_ADDED		= "SERVICE_ADDED";
	public static final String	SERVICE_MODIFIED	= "SERVICE_MODIFIED";
	public static final String	SERVICE_REMOVED		= "SERVICE_REMOVED";

	private final Semaphore	waiter;

	private BundleContext	bc;
	private String				lastAction;
	private ServiceReference	serviceReference;

	public ServiceListener(BundleContext bc, Class cls)
			throws InvalidSyntaxException {
		super(bc, bc.createFilter("(&(objectclass=" + cls.getName() + "))"), null);
		this.bc = bc;
		waiter = new Semaphore();
		open();
	}

	public Object addingService(ServiceReference ref) {
		Object service = bc.getService(ref);
		if (service == null) {
			return null;
		} else {
			serviceReference = ref;
			lastAction = SERVICE_ADDED;
			waiter.signal();
			return service;
		}
	}

	public void modifiedService(ServiceReference ref, Object service) {
		super.modifiedService(ref, service);
		if (service != null) {
			serviceReference = ref;
			lastAction = SERVICE_MODIFIED;
			waiter.signal();
		}
	}

	public void removedService(ServiceReference ref, Object service) {
		super.removedService(ref, service);
		if (service != null) {
			serviceReference = ref;
			lastAction = SERVICE_REMOVED;
			waiter.signal();
		}
	}

	/**
	 * 
	 * @return the latest service reference that had known an event.
	 * 
	 * @see org.osgi.util.tracker.ServiceTracker#getServiceReference()
	 */
	public synchronized ServiceReference getServiceReference() {
		return serviceReference;
	}

	/**
	 * Waits for an event to occur.
	 * 
	 * @param timeout
	 * @return the event type (added, modified, removed) or null.
	 * @throws InterruptedException
	 */
	public String waitForEvent(long timeout) throws InterruptedException {
		if (waiter.waitForSignal(timeout)) {
			return lastAction;
		}
		return null;
	}

	public String waitForService() throws InterruptedException {
		return waitForEvent(OSGiTestCaseProperties.getTimeout());
	}

	public void close() {
		if (serviceReference != null) {
			serviceReference = null;
		}
		super.close();
	}

}
