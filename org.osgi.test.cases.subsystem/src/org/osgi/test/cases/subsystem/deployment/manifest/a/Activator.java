package org.osgi.test.cases.subsystem.deployment.manifest.a;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class Activator implements BundleActivator {
	public void start(BundleContext context) throws Exception {
		try {
			Class.forName("org.osgi.framework.wiring");
			throw new BundleException(
					"Package org.osgi.framework.wiring should not be visible to this bundle");
		} catch (Exception e) {
			// Okay.
		}
	}

	public void stop(BundleContext context) throws Exception {
	}
}
