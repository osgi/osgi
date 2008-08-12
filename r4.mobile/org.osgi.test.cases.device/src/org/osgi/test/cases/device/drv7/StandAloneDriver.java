package org.osgi.test.cases.device.drv7;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Driver;
import org.osgi.service.device.Device;
import org.osgi.test.cases.device.tbc.TestBundleControl;
import java.util.Hashtable;

/**
 * Will match and should attach the standalone test device only
 * 
 * @author Vasil Panushev
 * @version 1.0
 */
public class StandAloneDriver implements BundleActivator, Driver {
	private BundleContext		bc					= null;
	private ServiceReference	deviceRef			= null;
	private ServiceRegistration	driverRegistration	= null;
	private ServiceReference	masterRef			= null;
	private TestBundleControl	master				= null;
	private Object				device				= null;

	/**
	 * Will register the standalone driver
	 * 
	 * @param bc bundle context received from the fw
	 * @exception Exception
	 */
	public void start(BundleContext bc) throws Exception {
		this.bc = bc;
		/* get the master of this test case */
		masterRef = bc.getServiceReference(TestBundleControl.class.getName());
		master = (TestBundleControl) bc.getService(masterRef);
		/* register the driver service */
		Hashtable h = new Hashtable();
		h.put("DRIVER_ID", "sadriver");
		h.put(org.osgi.framework.Constants.SERVICE_RANKING, new Integer(4242));
		driverRegistration = bc.registerService(
				"org.osgi.service.device.Driver", this, h);
	}

	/**
	 * unregisters the driver
	 * 
	 * @param bc
	 * @exception Exception
	 */
	public void stop(BundleContext bc) throws Exception {
		if (deviceRef != null) {
			bc.ungetService(deviceRef);
			deviceRef = null;
		}
		driverRegistration.unregister();
	}

	/**
	 * Will attach only to a device with ID
	 * 
	 * @param ref Reference to the device this driver will try to attach to
	 * @return null
	 * @exception Exception no way
	 */
	
	static int n = 0;
	
	public String attach(ServiceReference ref) throws Exception {
		if ("standalone driver test device".equals(ref.getProperty("deviceID"))) {
			deviceRef = ref;
			device = bc.getService(deviceRef); // to catch the device
			log("attaching to standalone device" );
			master.setMessage(TestBundleControl.MESSAGE_OK);
		}
		else {
			log("attaching to: " + ref.getProperty("deviceID"));
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param ref
	 * @return
	 * @exception Exception
	 */
	public int match(ServiceReference ref) throws Exception {
		System.out.println("match called !!!!!!!!!!!!!!!!!!!! "
				+ ref.getProperty("deviceID"));
		if ("standalone driver test device".equals(ref.getProperty("deviceID"))) {
			return 255;
		}
		else {
			return Device.MATCH_NONE;
		}
	}

	private void log(String toLog) {
		master.log("sadriver", toLog);
	}
}