package org.osgi.test.cases.upnp.tbc.device.discovery;

import org.osgi.framework.*;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Seems to track the latest registration/modification/unregistration of a device. 
 * It was using a service listener with all dangers associated. It also did not
 * discriminate between the looked for devices and the existing or found
 * devices on the network.
 * 
 */
public class ServicesListener extends ServiceTracker {
	public boolean				listen	= false;
	public int					count	= 0;
	private ServiceReference	sr;
	private static UPnPDevice	device;

	public ServicesListener(BundleContext bc) throws InvalidSyntaxException {
		super(bc,bc.createFilter("(&(objectclass=org.osgi.service.upnp.UPnPDevice)(UPnP.device.manufacturer=ProSyst))"), null);
	}

	
	public Object addingService(ServiceReference ref) {
		listen = true;
		count++;
		synchronized(this) {
			notifyAll();
		}
		sr = ref;
		device = (UPnPDevice) super.addingService(ref);
		return device;
	}
	
	public void removedService( Object service, ServiceReference ref ) {
		sr = ref;
		device = (UPnPDevice) service;
	}
	
	
	public void modifiedService( Object service, ServiceReference ref ) {
		sr = ref;
		device = (UPnPDevice) service;
	}

	public ServiceReference getServiceRef() {
		return sr;
	}

	public static UPnPDevice getUPnPDevice() {
		return device;
	}


	public synchronized void waitFor(int i) {
		while ( count != i ) {
			try {
				wait();
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}