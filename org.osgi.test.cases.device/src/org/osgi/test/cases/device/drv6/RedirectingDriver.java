package org.osgi.test.cases.device.drv6;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Driver;
import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * Used in the redirection test - it's attach method redirects to the winner
 * driver
 */
public class RedirectingDriver implements Driver, BundleActivator {
	private ServiceRegistration<Driver>			driverRegistration	= null;
	@SuppressWarnings("unused")
	private TestBundleControl	master				= null;
	private ServiceReference<TestBundleControl>	masterRef			= null;

	/**
	 * The start method of the activator of this Driver bundle. As required
	 * registers the Driver service synchronously with the bundle start.
	 */
	public void start(BundleContext bc) {
		masterRef = bc.getServiceReference(TestBundleControl.class);
		master = bc.getService(masterRef);
		log("starting driver bundle");
		Hashtable<String,Object> h = new Hashtable<>();
		h.put("DRIVER_ID", "redirecting_driver");
		h.put(org.osgi.framework.Constants.SERVICE_RANKING, Integer.valueOf(42));
		log("registering service");
		driverRegistration = bc.registerService(
				Driver.class, this, h);
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
	public int match(ServiceReference< ? > reference) {
		return 255;
	}

	/**
	 * Redirects to the winner dirver
	 * 
	 * @param reference service reference to the registred device.
	 * @returns a string that redirects to the winner dirver
	 */
	public String attach(ServiceReference< ? > reference) throws Exception {
		log("redirecting to winner driver");
		return "Driver_Winner";
	}

	public void log(String toLog) {
		//    master.logRemark("redirecting driver", toLog);
	}
}
