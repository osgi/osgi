package org.osgi.test.cases.clusterinfo.tb2;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
	private ServiceRegistration<String> reg;

	@Override
	public void start(BundleContext context) throws Exception {
		Hashtable<String,Object> props = new Hashtable<>();
		props.put("testbundle", "tb2");
		props.put("some.property", "some different value");
		reg = context.registerService(String.class, "Hello from TB2", props);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		reg.unregister();
	}
}
