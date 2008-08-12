package org.osgi.test.cases.device.drv5;

import org.osgi.framework.*;
import org.osgi.service.device.Driver;
import org.osgi.service.device.Device;
import org.osgi.test.cases.device.tbc.TestBundleControl;
import java.util.Hashtable;

/**
 * A driver that returns NO_MATCH from its match method. Used just to check that
 * no such drivers will be attached
 */
public class NotMatchingDriver implements Driver, BundleActivator {
	private ServiceRegistration	driverRegistration	= null;
	private BundleContext		bc					= null;
	private TestBundleControl	master				= null;
	private ServiceReference	masterRef			= null;

	/**
	 * The start method of the activator of this Driver bundle. As required
	 * registers the Driver service synchronously with the bundle start.
	 */
	public void start(BundleContext bc) {
		/* get the master of this test case */
		masterRef = bc.getServiceReference(TestBundleControl.class.getName());
		master = (TestBundleControl) bc.getService(masterRef);
		logRemark("starting bundle");
		Hashtable h = new Hashtable();
		h.put("DRIVER_ID", "not matching driver");
		logRemark("registering service");
		driverRegistration = bc.registerService(
				"org.osgi.service.device.Driver", this, h);
	}

	/**
	 * Unregisters the Driver service and ungets the master service
	 */
	public void stop(BundleContext bc) {
		bc.ungetService(masterRef);
		driverRegistration.unregister();
	}

	/**
	 * Checks if this dirver matches to the Device service reference passed.
	 * 
	 * @param reference service reference to the registred device.
	 * @returns Device.MATCH_NONE
	 */
	public int match(ServiceReference reference) {
		return Device.MATCH_NONE;
	}

	/**
	 * Basic implementation of a driver. Attaches successfully every time but
	 * should not be attached so it sets the message in the master to
	 * TestCAse.MESSAGE_ERROR
	 * 
	 * @param reference service reference to the registred device.
	 * @returns null
	 */
	public String attach(ServiceReference reference) throws Exception {
		// should not be called but I should force test case failure if called
		log("attaching to " + (String) reference.getProperty("deviceID"));
		master.setMessage(TestBundleControl.MESSAGE_ERROR);
		return null;
	}

	/**
	 * Logs the results i the master's log.
	 */
	public void log(String toLog) {
		// should not be called but I should force test case failure if called
		master.log("not matching dirver", toLog);
	}

	/**
	 * Logs the results i the master's log as remarks.
	 */
	public void logRemark(String toLog) {
		//  	master.logRemark("not matching driver", toLog);
	}
}