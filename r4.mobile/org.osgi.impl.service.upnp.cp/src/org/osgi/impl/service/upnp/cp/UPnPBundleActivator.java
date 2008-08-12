package org.osgi.impl.service.upnp.cp;

import org.osgi.framework.*;
import org.osgi.impl.service.upnp.cp.basedriver.UPnPBaseDriver;

public class UPnPBundleActivator implements BundleActivator {
	private UPnPControllerImpl	controller;
	private UPnPBaseDriver		basedriver;

	// This method is called when UPnP Bundle starts,so that the Bundle can
	// perform Bundle specific operations.
	public void start(BundleContext bc) {
		try {
			controller = new UPnPControllerImpl();
			controller.start(bc);
			basedriver = new UPnPBaseDriver(controller, bc);
			basedriver.start();
			//ServiceRegistration sreg =
			// bc.registerService("org.osgi.impl.service.upnp.cp.util.UPnPController",
			// controller, null);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method is called when UPnPCP Bundle stops.
	public void stop(BundleContext bc) {
		try {
			controller.stop();
			basedriver.stop();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
