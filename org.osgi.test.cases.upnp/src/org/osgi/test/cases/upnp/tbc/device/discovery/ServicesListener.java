package org.osgi.test.cases.upnp.tbc.device.discovery;

import org.osgi.framework.*;
import org.osgi.service.upnp.*;

/**
 * 
 * 
 * @Marian Dichev
 * @m_dichev@prosyst.bg
 * @1.0
 * @since
 */
public class ServicesListener implements ServiceListener {
	public boolean				listen	= false;
	public int					count	= 0;
	private BundleContext		bc;
	private ServiceReference	sr;
	private static UPnPDevice	device;

	public ServicesListener(BundleContext bc) {
		this.bc = bc;
	}

	public void serviceChanged(ServiceEvent event) {
		if (event.getType() == ServiceEvent.REGISTERED) {
			listen = true;
			count++;
			sr = event.getServiceReference();
			device = (UPnPDevice) bc.getService(sr);
			//      String udn = (String)
			// device.getDescriptions(null).get(UPnPDevice.UDN);
			//
			//      String upc = (String)
			// device.getDescriptions(null).get(UPnPDevice.UPC);
		}
		else
			if (event.getType() == ServiceEvent.UNREGISTERING) {
				sr = event.getServiceReference();
				device = (UPnPDevice) bc.getService(sr);
				//      String udn = (String)
				// device.getDescriptions(null).get(UPnPDevice.UDN);
			}
			else
				if (event.getType() == ServiceEvent.MODIFIED) {
					sr = event.getServiceReference();
					device = (UPnPDevice) bc.getService(sr);
					//      String udn = (String)
					// device.getDescriptions(null).get(UPnPDevice.UDN);
				}
	}

	public ServiceReference getServiceRef() {
		return sr;
	}

	public static UPnPDevice getUPnPDevice() {
		return device;
	}
}