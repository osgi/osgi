package org.osgi.impl.service.dmt;

import java.util.Hashtable;
import org.osgi.framework.*;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.dmt.DmtDataPlugIn;

public class ConfigurationPluginActivator implements BundleActivator {
	private ServiceRegistration	servReg;
	private ServiceReference	configRef;
	private ConfigurationPlugin	configPlugin;
	static final String			PLUGIN_ROOT	= "./OSGi/cfg";

	public void start(BundleContext bc) throws BundleException {
		System.out.println("Configuration plugin activation started.");
		//looking up the Configuration Admin
		configRef = bc.getServiceReference(ConfigurationAdmin.class.getName());
		if (configRef == null)
			throw new BundleException("Cannot find ConfigurationAdmin service.");
		ConfigurationAdmin ca = (ConfigurationAdmin) bc.getService(configRef);
		if (ca == null)
			throw new BundleException(
					"ConfigurationAdmin service no longer registered.");
		//creating the service
		configPlugin = new ConfigurationPlugin(ca);
		Hashtable properties = new Hashtable();
		properties.put("dataRootURIs", new String[] {PLUGIN_ROOT});
		//registering the service
		servReg = bc.registerService(DmtDataPlugIn.class.getName(),
				configPlugin, properties);
		System.out
				.println("Configuration plugin activation finished successfully.");
	}

	public void stop(BundleContext bc) throws BundleException {
		//unregistering the service
		servReg.unregister();
		//releasing the Configuration Admin
		bc.ungetService(configRef);
	}
}
