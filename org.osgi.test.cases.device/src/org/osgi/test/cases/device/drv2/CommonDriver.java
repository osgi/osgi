package org.osgi.test.cases.device.drv2;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.device.*;
import org.osgi.test.cases.device.tbc.*;

/**
 * A common driver implementation - a driver that will be matched with other
 * drivers but will lose the bidding (through selector or through the default
 * matching algorythm). Its SERVICE_RANKING properti is 42
 * 
 * @author ProSyst
 * @version 1.0
 */
public class CommonDriver implements Driver, BundleActivator {
	private ServiceRegistration	driverRegistration	= null;
	private BundleContext		bc					= null;
	private TestBundleControl	master				= null;
	private ServiceReference	masterRef			= null;

	/**
	 * The start method of the activator of the CommonDriver bundle. As required
	 * registers the Driver service synchronously with the bundle start.
	 */
	public void start(BundleContext bc) {
		this.bc = bc;
		/* get the master of this test case */
		masterRef = bc.getServiceReference(TestBundleControl.class.getName());
		master = (TestBundleControl) bc.getService(masterRef);
		log("starting driver bundle");
		Hashtable h = new Hashtable();
		h.put("DRIVER_ID", "common_driver");
		h.put(org.osgi.framework.Constants.SERVICE_RANKING, new Integer(42));
		log("registering service");
		driverRegistration = bc.registerService(
				"org.osgi.service.device.Driver", this, h);
	}

	/**
	 * unregisters the Driver service
	 */
	public void stop(BundleContext bc) {
		driverRegistration.unregister();
	}

	/**
	 * Checks if this dirver matches to the Device service reference passed.
	 * 
	 * @param reference service reference to the registred device.
	 * @returns 255
	 */
	public int match(ServiceReference reference) {
		return 255;
	}

	/**
	 * Basic implementation of a driver. Attaches successfully every time but
	 * sets the message in the master test case to
	 * TestBundleControl.MESSAGE_ERROR because this driver should not be
	 * attached - there is always other driver that should win.
	 * 
	 * @param reference service reference to the registred device. This dirver
	 *        attachtes always successfully
	 * @returns null
	 */
	public String attach(ServiceReference reference) throws Exception {
		log("attaching to " + (String) reference.getProperty("deviceID"));
		master.setMessage(TestBundleControl.MESSAGE_ERROR);
		bc.ungetService(masterRef);
		return null;
	}

	/**
	 * Logs the results i the master's log as remarks.
	 */
	public void log(String toLog) {
		//  	master.logRemark("common driver", toLog);
	}
}