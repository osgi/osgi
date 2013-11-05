package org.osgi.test.cases.subsystem.app.service.dep.e;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
	private ServiceRegistration<E1> e1Reg;
	private ServiceRegistration<E2> e2Reg;
	
	public void start(BundleContext context) throws Exception {
		e1Reg = context.registerService(E1.class, new E1(){}, null);
		e2Reg = context.registerService(E2.class, new E2(){}, null);
	}

	public void stop(BundleContext context) throws Exception {
		e2Reg.unregister();
		e1Reg.unregister();
	}
}
