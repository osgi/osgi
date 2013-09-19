package org.osgi.impl.service.zigbee;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.zigbee.basedriver.ZigBeeBaseDriver;

/**
 * Activator for mocked ZigBee basedriver.
 */
public class ZigBeeBundleActivator implements BundleActivator {

	private ZigBeeBaseDriver	basedriver;

	/**
	 * This method is called when ZigBee Bundle starts, so that the Bundle can
	 * perform Bundle specific operations.
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bc) {
		try {
			basedriver = new ZigBeeBaseDriver(bc);
			basedriver.start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This method is called when ZigBee Bundle stops.
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bc) {
		try {
			basedriver.stop();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
