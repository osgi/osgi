package org.osgi.impl.service.residentialmanagement.plugins;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ResidentialPluginActivator implements BundleActivator {
	private FiltersPluginActivator filters;
	private FrameworkPluginActivator framework;
	
	public void start(BundleContext context) throws Exception {
		filters = new FiltersPluginActivator();
		filters.start(context);
		
		framework = new FrameworkPluginActivator();
		framework.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		filters.stop(context);
		framework.stop(context);
	}

}
