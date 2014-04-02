package org.osgi.test.cases.subsystem.app.service.dep.b;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
	private ServiceRegistration<B> bReg;
	
	public void start(BundleContext context) throws Exception {
		B b = new B() {};
		bReg = context.registerService(B.class, b, null);
	}

	public void stop(BundleContext context) throws Exception {
		bReg.unregister();
	}
}
