package org.osgi.test.cases.device.drv6;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.device.*;
import org.osgi.test.cases.device.tbc.*;

/**
 * Used in the redirection test - it's attach method redirects to the winner
 * driver
 */
public class RedirectingDriver implements Driver, BundleActivator {
	private ServiceRegistration	driverRegistration	= null;
	private BundleContext		bc					= null;
	private TestBundleControl	master				= null;
	private ServiceReference	masterRef			= null;

	/**
	 * The start method of the activator of this Driver bundle. As required
	 * registers the Driver service synchronously with the bundle start.
	 */
	public void start(BundleContext bc) {
		this.bc = bc;
		masterRef = bc.getServiceReference(TestBundleControl.class.getName());
		master = (TestBundleControl) bc.getService(masterRef);
		log("starting driver bundle");
		Hashtable h = new Hashtable();
		h.put("DRIVER_ID", "redirecting_driver");
		h.put(org.osgi.framework.Constants.SERVICE_RANKING, new Integer(42));
		log("registering service");
		driverRegistration = bc.registerService(
				"org.osgi.service.device.Driver", this, h);
	}

	/**
	 * Unregisters the Driver service and ungets the master service
	 */
	public void stop(BundleContext bc) {
		driverRegistration.unregister();
	}

	/**
	 * Checks if this dirver matches to the Device service reference passed.
	 * 
	 * @param reference service reference to the registred device. This dirver
	 *        will match only with a device with ID (deviceID
	 *        property)"basicDevice"
	 * @returns 255
	 */
	public int match(ServiceReference reference) {
		return 255;
	}

	/**
	 * Redirects to the winner dirver
	 * 
	 * @param reference service reference to the registred device.
	 * @returns a string that redirects to the winner dirver
	 */
	public String attach(ServiceReference reference) throws Exception {
		log("redirecting to winner driver");
		return "Driver_Winner";
	}

	public void log(String toLog) {
		//    master.logRemark("redirecting driver", toLog);
	}
}