package org.osgi.test.cases.subsystem.weaving.b;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
	public void start(BundleContext context) throws Exception {
		Class.forName("org.osgi.test.cases.subsystem.weaving.a.A");
	}

	public void stop(BundleContext context) throws Exception {
	}
}
