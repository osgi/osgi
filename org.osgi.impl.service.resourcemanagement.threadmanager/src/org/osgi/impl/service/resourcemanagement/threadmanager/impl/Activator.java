package org.osgi.impl.service.resourcemanagement.threadmanager.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.resourcemanagement.threadmanager.ThreadManager;

public class Activator implements BundleActivator {

	/**
	 * thread manager
	 */
	private ThreadManager threadManager;
	
	/**
	 * service registration
	 */
	private ServiceRegistration serviceRegistration;
	
	public void start(BundleContext context) throws Exception {
		threadManager = new ThreadManagerImpl();
		
		serviceRegistration = context.registerService(ThreadManager.class.getName(), threadManager, null);
	}

	public void stop(BundleContext context) throws Exception {
		serviceRegistration = null;
	}

}
