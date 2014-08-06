
package org.osgi.impl.service.resourcemanagement.threadmanager.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.resourcemanagement.threadmanager.ThreadManager;

/**
 * Activator of ThreadManager Impl.
 */
public class Activator implements BundleActivator {

	/**
	 * thread manager
	 */
	private ThreadManager		threadManager;

	/**
	 * service registration
	 */
	private ServiceRegistration	serviceRegistration;

	public void start(BundleContext context) throws Exception {
		threadManager = new ThreadManagerImpl();
		serviceRegistration = context.registerService(ThreadManager.class.getName(), threadManager, null);
		System.out.println("ThreadManager has been started, and registered as an OSGi service - serviceRegistration: " + serviceRegistration);
	}

	public void stop(BundleContext context) throws Exception {
		serviceRegistration = null;
	}

}
