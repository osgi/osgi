
package org.osgi.impl.service.enocean;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.enocean.basedriver.EnOceanBaseDriver;

public class EnOceanBundleActivator implements BundleActivator {
	private EnOceanBaseDriver	basedriver;

	public void start(BundleContext bc) {
		basedriver = new EnOceanBaseDriver(bc);
		basedriver.start();
	}

	public void stop(BundleContext bc) {
		basedriver.stop();
	}
}
