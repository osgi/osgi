
package org.osgi.impl.service.enocean;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.enocean.basedriver.EnOceanBaseDriver;

/**
 * Reference Implementation's Activator.
 */
public class EnOceanBundleActivator implements BundleActivator {

	private EnOceanBaseDriver	basedriver;

	@Override
	public void start(BundleContext bc) {
		basedriver = new EnOceanBaseDriver(bc);
		basedriver.start();
	}

	@Override
	public void stop(BundleContext bc) {
		basedriver.stop();
	}
}
