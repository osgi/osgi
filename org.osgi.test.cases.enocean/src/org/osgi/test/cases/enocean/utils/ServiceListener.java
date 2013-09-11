
package org.osgi.test.cases.enocean.utils;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.test.cases.enocean.BaseDriverConformanceTest;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.Semaphore;
import org.osgi.util.tracker.ServiceTracker;


public class ServiceListener extends ServiceTracker {
	private final Semaphore	waiter;

	private EnOceanDevice		device			= null;
	private ServiceReference	deviceReference	= null;

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
			waitFor(OSGiTestCaseProperties.getTimeout()
					* OSGiTestCaseProperties.getScaling());
		} catch (InterruptedException e) {
			BaseDriverConformanceTest.log("test was interrupted");
		}
		if (getDevice() == null) {
			close();
			BaseDriverConformanceTest.fail("timeout waiting for a device");
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
			if (service instanceof EnOceanDevice) {
				device = (EnOceanDevice) service;
				deviceReference = ref;
			}
			waiter.signal();
			return service;
		}
	}

	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
	}

	public synchronized EnOceanDevice getDevice() {
		return device;
	}

	public synchronized ServiceReference getDeviceReference() {
		return deviceReference;
	}

	public void waitFor(long timeout) throws InterruptedException {
		waiter.waitForSignal(timeout);
	}
}
