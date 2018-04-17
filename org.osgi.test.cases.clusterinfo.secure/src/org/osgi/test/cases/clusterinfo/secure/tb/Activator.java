package org.osgi.test.cases.clusterinfo.secure.tb;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
	private ServiceRegistration<String> reg;

	@Override
	public void start(BundleContext context) throws Exception {
		Hashtable<String,Object> props = new Hashtable<>();
		props.put("org.osgi.service.clusterinfo.tags", new String[] {
				"foo", "bar"
		});
		reg = context.registerService(String.class, "something", props);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		reg.unregister();
	}
}
