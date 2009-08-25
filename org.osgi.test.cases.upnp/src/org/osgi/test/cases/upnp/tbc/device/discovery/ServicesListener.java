package org.osgi.test.cases.upnp.tbc.device.discovery;

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
	private UPnPDevice	last;

	public ServicesListener(BundleContext bc) throws InvalidSyntaxException {
		super(
				bc,
				bc
						.createFilter("(&(objectclass=org.osgi.service.upnp.UPnPDevice)(UPnP.device.manufacturer=ProSyst))"),
				null);
	}

	public Object addingService(ServiceReference ref) {
		UPnPDevice device = (UPnPDevice) super.addingService(ref);

		synchronized (this) {
			last = device;
			notifyAll();
		}
		return device;
	}

	public synchronized UPnPDevice getUPnPDevice() {
		return last;
	}

	public synchronized void waitFor(int i) {
		while (size() != i) {
			try {
				wait();
			}
			catch (InterruptedException e) {
				// ignored
			}
		}
	}
}