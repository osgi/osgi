package org.osgi.test.cases.dmt.plugins.tbc.TreeStructure;

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

public class TestManagedServiceFactory implements ManagedServiceFactory {
	Dictionary properties;

	ServiceRegistration registration;
	BundleContext context;
	public synchronized void start(BundleContext context) throws Exception {
		properties = new Hashtable();
		this.context = context;
		properties.put(Constants.SERVICE_PID, Configuration.BUNDLE_2_FACTORY_PID);
		registration = context.registerService(ManagedServiceFactory.class.getName(),
				this, properties);
	}

	public synchronized void updated(Dictionary np) {
		if (np != null) {
			properties = np;
			properties.put(Constants.SERVICE_PID, Configuration.BUNDLE_2_FACTORY_PID);
		}
		registration.setProperties(properties);
	}

	public String getName() {
		return null;
	}

	public void updated(String arg0, Dictionary arg1) throws ConfigurationException {
	}

	public void deleted(String arg0) {
	}

}