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

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.discovery.slp.SLPHandlerImpl;
import org.osgi.service.discovery.Discovery;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * 
 * Activator class. Creates DiscoveryImpl instance and registers it as a
 * service.
 * 
 */
public class Activator implements BundleActivator {
	private ServiceRegistration	slpHandlerRegistration;
	private LogService			logService	= DEFAULT_LogService;
	private ServiceTracker		logServiceTracker;
	private SLPHandlerImpl		slpDiscovery;

	/**
	 * Start is called when the bundle is started. Creates an instance of the
	 * implementation and registers the object as a service in the OSGi service
	 * registry.
	 * 
	 * @param context BundleContext
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {

		logServiceTracker = new ServiceTracker(context, LogService.class
				.getName(), new ServiceTrackerCustomizer() {

			private LogService	logger	= null;

			public Object addingService(ServiceReference reference) {
				if (logger == null) {
					LogService logger = (LogService) context
							.getService(reference);
					setLogService(logger);
					return logger;
				}
				return null;
			}

			public void modifiedService(ServiceReference reference,
					Object service) {
			}

			public void removedService(ServiceReference reference,
					Object service) {
				context.ungetService(reference);
				ServiceReference serviceRef = context
						.getServiceReference(LogService.class.getName());
				if (serviceRef == null) {
					setLogService(null);
				}
				else {
					setLogService((LogService) context.getService(serviceRef));
				}
			}

		});
		logServiceTracker.open();

		slpDiscovery = new SLPHandlerImpl(context, logService);
		slpDiscovery.init();

		Dictionary props = new Hashtable();
		// TODO: make the instance configurable, e.g. via CAS or DS
		props.put(Discovery.PROP_KEY_VENDOR_NAME,
				"Siemens Enterprise Communications GmbH & Co KG");
		props.put(Discovery.PROP_KEY_SUPPORTED_PROTOCOLS, "jSLP 1.0.0");
		slpHandlerRegistration = context.registerService(Discovery.class
				.getName(), slpDiscovery, props);

		logService.log(LogService.LOG_INFO, "discovery service started");
	}

	/**
	 * Stop is called by the framework when the bundle is stopped. All internal
	 * resources are cleaned up and services unregistered.
	 * 
	 * @param context BundleContext
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		logService.log(LogService.LOG_INFO, "stop discovery service");

		if (slpHandlerRegistration != null) {
			slpHandlerRegistration.unregister();
			slpHandlerRegistration = null;
		}

		if (logServiceTracker != null) {
			logServiceTracker.close();
			logServiceTracker = null;
		}

		if (slpDiscovery != null) {
			slpDiscovery.destroy();
			slpDiscovery = null;
		}
	}

	/**
	 * 
	 * @param loggerLogService instance to set
	 */
	void setLogService(LogService logger) {
		if (logger != null) {
			logService = logger;
		}
		else {
			logService = DEFAULT_LogService;
		}

		if (slpDiscovery != null) {
			slpDiscovery.setLogService(logService);
		}
	}

	/**
	 * 
	 */
	private static LogService	DEFAULT_LogService	= new LogService() {
														private final String[]	LEVELS	= new String[] {
			"[FATAL] ", "[ERROR] ", "[WARNING] ", "[INFO] ", "[DEBUG] "					};

														public void log(
																int level,
																String message) {
															System.out
																	.println(LEVELS[level]
																			+ message);
														}

														public void log(
																int level,
																String message,
																Throwable exception) {
															System.out
																	.println(LEVELS[level]
																			+ message
																			+ ", "
																			+ exception
																					.getMessage());
														}

														public void log(
																ServiceReference sr,
																int level,
																String message) {
															System.out
																	.println(LEVELS[level]
																			+ message);
														}

														public void log(
																ServiceReference sr,
																int level,
																String message,
																Throwable exception) {
															System.out
																	.println(LEVELS[level]
																			+ message
																			+ ", "
																			+ exception
																					.getMessage());
														}

													};
}