package org.osgi.test.cases.device.dev1;

import org.osgi.framework.*;
import org.osgi.service.device.Device;
import org.osgi.test.cases.device.tbc.TestBundleControl;
import java.util.Hashtable;

/**
 * This class is used as an activator fo the device bundle - the common device
 * that is used by all subtests.
 * 
 * @author ProSyst
 * @version 1.0
 */
public class BasicDevice implements BundleActivator, Device {
	private ServiceRegistration	deviceSR	= null;
	private String[]			category	= {"test"};
	private TestBundleControl	master		= null;
	private ServiceReference	masterRef	= null;

	/**
	 * The start method of the activator of the device bundle. Registers
	 * different devices depending on the device.test.mode system property.
	 * Following values of this property are recognized: 0 - registers a device
	 * that implements org.osgi.service.device.Device and there is matching
	 * driver for it 1 - registers a device that doesn't implement
	 * org.osgi.service.device.Device and there is matching driver 2 - registers
	 * a device that implements org.osgi.service.device.Device and there is NO
	 * matching driver for it 3 - registers a device that dooesn't implement
	 * org.osgi.service.device.Device and there is NO matching driver for it 4 -
	 * registers a device that implements org.osgi.service.device.Driver but
	 * does not set DEVICE_CATEGOR property 100 - registers a general device
	 * 
	 * @param bc the bundle context of this bundle
	 */
	public void start(BundleContext bc) {
		// get the master of this test case - it is used for logging
		masterRef = bc.getServiceReference(TestBundleControl.class.getName());
		master = (TestBundleControl) bc.getService(masterRef);
		// get the test mode
		Hashtable sysProps = System.getProperties();
		String m = (String) sysProps.get("device.test.mode");
		int mode = Integer.parseInt(m);
		switch (mode) {
			case 0 : // basic device that implements
				// org.osgi.service.device.Device with matching driver
				Hashtable h_0 = new Hashtable();
				h_0.put("deviceID", "basicDevice");
				h_0.put("DEVICE_CATEGORY", category);
				h_0.put("device.test", Boolean.TRUE);
				log("Registering device that implements Device and there is a driver for it");
				deviceSR = bc.registerService("org.osgi.service.device.Device",
						this, h_0);
				break;
			case 1 : // basic device that doesn't implement
				// org.osgi.service.device.Device with matching driver
				Hashtable h_1 = new Hashtable();
				h_1.put("deviceID", "basicDevice_noDevice");
				h_1.put("DEVICE_CATEGORY", category);
				h_1.put("device.test", Boolean.TRUE);
				log("Registering device that doesn't implement Device and there is a driver for it");
				deviceSR = bc.registerService("java.lang.Object", this, h_1);
				break;
			case 2 : // basic device that implements
				// org.osgi.service.device.Device without matching driver
				Hashtable h_2 = new Hashtable();
				h_2.put("deviceID", "basicDevice_noDriver");
				h_2.put("device.test", Boolean.TRUE);
				log("Registering device that implement Device and there is NO driver for it");
				deviceSR = bc.registerService("org.osgi.service.device.Device",
						this, h_2);
				break;
			case 3 : // basic device that doesn't implement
				// org.osgi.service.device.Device without matching driver
				Hashtable h_3 = new Hashtable();
				h_3.put("deviceID", "basicDevice_noDevice_noDriver");
				h_3.put("DEVICE_CATEGORY", category);
				h_3.put("device.test", Boolean.TRUE);
				log("Registering device that doesn't implement Device and there is NO driver for it");
				deviceSR = bc.registerService("java.lang.Object", this, h_3);
				break;
			case 4 : // basic device that implements
				// org.osgi.service.device.Driver but does not set
				// DEVICE_CATEGOR property - it must be recognized too
				Hashtable h_4 = new Hashtable();
				h_4.put("deviceID", "basicDevice_noCategory");
				h_4.put("device.test", Boolean.TRUE);
				log("Registering device that implements Device but does not set DEVICE_CATEGORY property");
				deviceSR = bc.registerService("org.osgi.service.device.Device",
						this, h_4);
				break;
			case 100 : // general device - used by the driver selection test
				Hashtable h_100 = new Hashtable();
				h_100.put("deviceID", "generalDevice");
				h_100.put("DEVICE_CATEGORY", category);
				h_100.put("device.test", Boolean.TRUE);
				log("Registering a device for general use");
				deviceSR = bc.registerService("org.osgi.service.device.Device",
						this, h_100);
				break;
			default :
				log("Unknown test mode");
				break;
		}
	}

	/**
	 * Unregisters the device service
	 * 
	 * @param bc the bundle context of this bundle
	 */
	public void stop(BundleContext bc) {
		deviceSR.unregister();
		bc.ungetService(masterRef);
	}

	/**
	 * Should be called when no dirver is found for this device - only for
	 * devices that implement org.osgi.service.device.Device.
	 */
	public void noDriverFound() {
		master.noDriverFoundCalled = true;
	}

	/* Calls the log method of the master test case */
	private void log(String toLog) {
		master.log("basic device", toLog);
	}
}