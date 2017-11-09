package org.osgi.test.cases.upnp.tbc.device.discovery;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Seems to track the latest registration/modification/unregistration of a
 * device. It was using a service listener with all dangers associated. It also
 * did not discriminate between the looked for devices and the existing or found
 * devices on the network.
 * 
 */
public class ServicesListener extends ServiceTracker {
	private final Semaphore	waiter;
	private final int		desiredCount;
	private UPnPDevice	last;
	private int				size;

	public ServicesListener(BundleContext bc, int count)
			throws InvalidSyntaxException {
		super(
				bc,
				bc
						.createFilter("(&(objectclass=org.osgi.service.upnp.UPnPDevice)(UPnP.device.manufacturer=ProSyst))"),
				null);
		waiter = new Semaphore(0);
		desiredCount = count;
		size = 0;
	}

	public void open() {
		super.open();
		synchronized (this) {
			size = super.size();
		}
	}

	public void open(boolean trackAllServices) {
		super.open(trackAllServices);
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
		UPnPDevice device = (UPnPDevice) super.addingService(ref);

		DefaultTestBundleControl.log("adding UPnP Device " + device);
		synchronized (this) {
			size++;
			last = device;
			if (size != desiredCount) {
				return device;
			}
		}
		DefaultTestBundleControl.log(desiredCount
				+ " UPnP Devices arrived, signaling waiter");
		waiter.release();
		return device;
	}

	public void removedService(ServiceReference reference, Object service) {
		DefaultTestBundleControl.log("removing UPnP Device " + service);
		super.removedService(reference, service);
		synchronized (this) {
			size--;
		}
	}

	public synchronized UPnPDevice getUPnPDevice() {
		return last;
	}

	public void waitFor(long timeout) throws InterruptedException {
		DefaultTestBundleControl.log("waiting for UPnP Devices " + timeout);
		waiter.tryAcquire(timeout, TimeUnit.MILLISECONDS);
	}
}
