
package org.osgi.test.cases.enocean.utils;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.enocean.BaseDriverConformanceTest;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.Semaphore;
import org.osgi.util.tracker.ServiceTracker;


public class ServiceListener extends ServiceTracker {

	private final Semaphore	waiter;
	private ServiceReference	serviceReference	= null;

	private int				size;
	private BundleContext	bc;

	public ServiceListener(BundleContext bc, Class cls)
			throws InvalidSyntaxException {
		super(bc, bc.createFilter("(&(objectclass=" + cls.getName() + "))"), null);
		this.bc = bc;
		waiter = new Semaphore();
	}

	public void waitForRegistration() {
		open();
		try {
			waitForService(OSGiTestCaseProperties.getTimeout());
		} catch (InterruptedException e) {
			BaseDriverConformanceTest.log("test was interrupted");
		}
	}

	public void open() {
		super.open();
		synchronized (this) {
			size = super.size();
		}
	}

	public void close() {
		super.close();
		synchronized (this) {
			size = super.size();
		}
	}

	public synchronized int size() {
		return size;
	}

	public Object addingService(ServiceReference ref) {
		Object service = bc.getService(ref);
		if (service == null) {
			return null;
		} else {
			serviceReference = ref;
			waiter.signal();
			return service;
		}
	}

	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
	}

	public synchronized ServiceReference getDeviceReference() {
		return serviceReference;
	}

	public void waitFor(long timeout) throws InterruptedException {
		waiter.waitForSignal(timeout);
	}
}
