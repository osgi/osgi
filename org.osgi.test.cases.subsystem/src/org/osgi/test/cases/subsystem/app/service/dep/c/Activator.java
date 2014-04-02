package org.osgi.test.cases.subsystem.app.service.dep.c;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.subsystem.app.service.dep.b.B;

public class Activator implements BundleActivator {
	
	public void start(BundleContext context) throws Exception {
		ServiceReference<B> bRef = context.getServiceReference(B.class);
		B b = context.getService(bRef);
		b.getClass();
		context.ungetService(bRef);
	}

	public void stop(BundleContext context) throws Exception {
	}
}
