package org.osgi.test.cases.dmt.plugins.tbc.TreeStructure;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;

public class TestManagedService implements ManagedService {
	Dictionary properties;

	ServiceRegistration registration;

	public synchronized void start(BundleContext context) throws Exception {
		properties = new Hashtable();
		properties.put(Constants.SERVICE_PID, Configuration.BUNDLE_PID);
		registration = context.registerService(ManagedService.class.getName(),
				this, properties);
	}

	public synchronized void updated(Dictionary np) {
		if (np != null) {
			properties = np;
			properties.put(Constants.SERVICE_PID, Configuration.BUNDLE_PID);
			
		} else {
			registration.unregister();
		}
			
		registration.setProperties(properties);
	}

}