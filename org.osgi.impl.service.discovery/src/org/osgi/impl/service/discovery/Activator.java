/* 
 * $Header$
 * 
 * (c) Copyright 2008 Siemens Communications
 *
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Siemens Communications and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 */
package org.osgi.impl.service.discovery;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.*;
import org.osgi.impl.service.discovery.equinox.DiscoveryCommandProvider;
import org.osgi.service.discovery.Discovery;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * 
 * Activator class. Creates DiscoveryImpl instance and registers it as a service. 
 * 
 */
public class Activator implements BundleActivator {
	private DiscoveryImpl discoveryImpl;
	private ServiceRegistration registration;
	private LogService logService = DEFAULT_LogService;
	private ServiceTracker logServiceTracker;
	
	private ServiceRegistration commandProvider;
	
	/**
	 * Start is called when the bundle is started. Creates an instance of the implementation and
	 * registers the object as a service in the OSGi service registry.
	 * 
	 * @param context BundleContext
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		commandProvider = context.registerService(CommandProvider.class.getName(), new DiscoveryCommandProvider(context), null);
		
		discoveryImpl = new DiscoveryImpl(context, logService);

		logServiceTracker = new ServiceTracker(context, LogService.class.getName(), new ServiceTrackerCustomizer() {

			public Object addingService(ServiceReference reference) {
				LogService logger = (LogService) context.getService(reference);
				
				setLogService(logger);
				
				return logger;
			}

			public void modifiedService(ServiceReference reference,
					Object service) {
				LogService logger = (LogService) context.getService(reference);
				
				setLogService(logger);
			}

			public void removedService(ServiceReference reference,
					Object service) {
				context.ungetService(reference);
				setLogService(null);
			}
			
		});
		logServiceTracker.open();
		
		// TODO: make the instance configurable, e.g. via CAS or DS
		registration = context.registerService(Discovery.class.getName(), discoveryImpl, null);
		
		discoveryImpl.init();
		
		logService.log(LogService.LOG_INFO, "discovery service started");
	}

	/**
	 * Stop is called by the framework when the bundle is stopped. All internal resources are
	 * cleaned up and services unregistered.
	 * 
	 * @param context BundleContext
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		logService.log(LogService.LOG_INFO, "stop discovery service");
		
		if (commandProvider != null) {
			commandProvider.unregister();
			commandProvider = null;
		}
		
		if (registration != null) {
			registration.unregister();
			registration = null;
		}
		
		if (logServiceTracker != null) {
			logServiceTracker.close();
			logServiceTracker = null;
		}
		
		if (discoveryImpl != null) {
			discoveryImpl.destroy();
			discoveryImpl = null;
		}
	}
	
	void setLogService(LogService logger) {
		if (logger != null) {
			logService = logger;
		} else {
			logService = DEFAULT_LogService;
		}
		
		discoveryImpl.setLogService(logService);
	}
	
	private static LogService DEFAULT_LogService = new LogService() {
		private final String[] LEVELS = new String[] {"[FATAL] ", "[ERROR] ", "[WARNING] ", "[INFO] ", "[DEBUG] "};
		
		public void log(int level, String message) {
			System.out.println(LEVELS[level] + message);
		}

		public void log(int level, String message, Throwable exception) {
			System.out.println(LEVELS[level] + message + ", " + exception.getMessage());
		}

		public void log(ServiceReference sr, int level, String message) {
			System.out.println(LEVELS[level] + message);
		}

		public void log(ServiceReference sr, int level, String message,
				Throwable exception) {
			System.out.println(LEVELS[level] + message + ", " + exception.getMessage());
		}
		
	};
}