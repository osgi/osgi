package org.osgi.impl.service.enocean;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.enocean.basedriver.EnOceanBaseDriver;

public class EnOceanBundleActivator implements BundleActivator {
	private EnOceanBaseDriver	basedriver;

	public void start(BundleContext bc) {


		try {
			basedriver = new EnOceanBaseDriver(bc);
			basedriver.start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void stop(BundleContext bc) {
		try {
			basedriver.stop();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
