
package org.osgi.impl.service.zigbee;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.zigbee.basedriver.ZigBeeBaseDriver;

/**
 * Activator for mocked ZigBee basedriver.
 * 
 * @author $Id$
 */
public class ZigBeeBundleActivator implements BundleActivator {

	private ZigBeeBaseDriver basedriver;

	/**
	 * This method is called when ZigBee Bundle starts, so that the Bundle can
	 * perform Bundle specific operations.
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bc) {
		try {
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - Instantiate the ZigBeeBaseDriver.");
			basedriver = new ZigBeeBaseDriver(bc);
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - Start the ZigBeeBaseDriver instance.");
			basedriver.start();
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - The ZigBeeBaseDriver instance is started.");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
			System.out.println(this.getClass().getName() + " - ////////// ////////// ////////// ////////// //////////");
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
			System.out.println(this.getClass().getName() + " - Stop the ZigBeeBaseDriver instance.");
			basedriver.stop();
			System.out.println(this.getClass().getName() + " - The ZigBeeBaseDriver instance is stopped.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
