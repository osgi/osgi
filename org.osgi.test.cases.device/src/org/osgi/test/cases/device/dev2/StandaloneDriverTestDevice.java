package org.osgi.test.cases.device.dev2;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import java.util.Hashtable;

/**
 * The first registered driver will not attach to this device. The second will
 * do.
 * 
 * @author ProSyst
 * @version 1.0
 */
public class StandaloneDriverTestDevice implements BundleActivator {
	private ServiceRegistration	deviceSR	= null;
	private String[]			category	= {"test"};

	/**
	 * Will register a org.osgi.service.device.Device service
	 * 
	 * @param bc BundleContext from the fw
	 * @exception Exception maybe never
	 */
	public void start(BundleContext bc) throws Exception {
		Hashtable h = new Hashtable();
		h.put("deviceID", "standalone driver test device");
		h.put("DEVICE_CATEGORY", category);
		h.put("device.test", Boolean.TRUE);
		deviceSR = bc.registerService("java.lang.Object", this, h);
	}

	/**
	 * unregisters the device service
	 * 
	 * @param bc BundleContext from the fw
	 * @exception Exception maybe never
	 */
	public void stop(BundleContext bc) throws Exception {
		deviceSR.unregister();
	}
}