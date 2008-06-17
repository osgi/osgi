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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.impl.service.discovery.slp.SLPHandlerImpl;
import org.osgi.impl.service.discovery.slp.SLPServiceDescriptionAdapter;
import org.osgi.service.discovery.Discovery;
import org.osgi.service.discovery.ServiceDescription;
import org.osgi.service.discovery.ServiceListener;
import org.osgi.service.log.LogService;

import ch.ethz.iks.slp.ServiceLocationException;

/**
 * Discovery implementation. This implementation supports the SLP protocol implementation.
 * 
 * TODO: add support for second protocol, e.g. JGroups
 * 
 * @author Tim Diekmann
 */
public class DiscoveryImpl implements Discovery {
	private static final boolean DEFAULT_AUTOPUBLISH = true;
	
	private List listeners;
	private SLPHandlerImpl slpHandler;
	private LogService logService;
	private boolean autoPublish = DEFAULT_AUTOPUBLISH;

	DiscoveryImpl(BundleContext context, LogService logService) {
		this.logService = logService;
		
		// TODO read this from config rather than system property
		autoPublish = System.getProperty(Discovery.ORG_OSGI_DISCOVERY,
				Discovery.ORG_OSGI_DISCOVERY_AUTO_PUBLISH).equalsIgnoreCase(Discovery.ORG_OSGI_DISCOVERY_AUTO_PUBLISH);
		
		listeners = new LinkedList();
		slpHandler = new SLPHandlerImpl(context, logService);
	}
	
	/**
	 * @see org.osgi.service.discovery.Discovery#addServiceListener(org.osgi.service.discovery.ServiceListener)
	 */
	public void addServiceListener(ServiceListener listener) {
		logService.log(LogService.LOG_DEBUG, "listener added, " + listener);
		
		synchronized (listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#removeServiceListener(org.osgi.service.discovery.ServiceListener)
	 */
	public void removeServiceListener(ServiceListener listener) {
		logService.log(LogService.LOG_DEBUG, "listener removed, " + listener);
		
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#findService(org.osgi.service.discovery.ServiceDescription, org.osgi.service.discovery.ServiceListener)
	 */
	public void findService(final ServiceDescription serviceDescription, final ServiceListener callback) {
		if (serviceDescription == null) {
			throw new IllegalArgumentException("serviceDescription must not be null");
		}
		
		Thread executor = new Thread(new Runnable() {

			public void run() {
				try {
					// TODO first look at cache

					// if miss, do lookup with SLPHandler
					Collection services = findService(serviceDescription);

					// call callback listener
					// call callback with unavailable if none found
					if (services.isEmpty()) {
						logService.log(LogService.LOG_DEBUG, "no services found for " + serviceDescription);
						
						try {
							callback.serviceUnavailable(serviceDescription);
						} catch (Exception e) {
							logService.log(LogService.LOG_ERROR, "callback serviceUnavailable failed", e);
						}
						
						return; // leave this thread
					}
					
					for (Iterator i = services.iterator(); i.hasNext();) {
						try {
							callback.serviceAvailable((ServiceDescription)i.next());
						} catch (Exception e) {
							logService.log(LogService.LOG_ERROR, "callback serviceAvailable failed", e);
						}
					}
				} catch (Exception e) {
					logService.log(LogService.LOG_ERROR, "Failed to execute async findService", e);
				}
			}
		});
		executor.start();
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#findService(org.osgi.service.discovery.ServiceDescription)
	 */
	public Collection findService(ServiceDescription serviceDescription) {
		logService.log(LogService.LOG_DEBUG, "find services for " + serviceDescription.getInterfaceName());

		Collection services;
		try {
			services = slpHandler.findService(new SLPServiceDescriptionAdapter(serviceDescription));
		}
		catch (ServiceLocationException e) {
			services = new LinkedList();
			e.printStackTrace();
			logService.log(LogService.LOG_ERROR, e.getMessage(), e);
		}
		
		// TODO add to cache
		
		return services;
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#getCachedServiceDescriptions()
	 */
	public ServiceDescription[] getCachedServiceDescriptions() {
		// TODO return cached instances
		return new ServiceDescription[]{};
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#isCached(org.osgi.service.discovery.ServiceDescription)
	 */
	public boolean isCached(ServiceDescription serviceDescription) {
		// TODO delegate to cache
		return false;
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#publish(org.osgi.service.discovery.ServiceDescription)
	 */
	public boolean publish(ServiceDescription serviceDescription) {
		return publish(serviceDescription, autoPublish);
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#publish(org.osgi.service.discovery.ServiceDescription, boolean)
	 */
	public boolean publish(ServiceDescription serviceDescription,
			boolean autopublish) {
		logService.log(LogService.LOG_DEBUG, "publish service " + serviceDescription.getInterfaceName());

		// delegate to protocol implementation
		try {
			slpHandler.publishService(new SLPServiceDescriptionAdapter(serviceDescription));
		}
		catch (Exception e) {
			e.printStackTrace();
			logService.log(LogService.LOG_ERROR, "Failed to publish service", e);
			return false;
		}
		
		return true;
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#unpublish(org.osgi.service.discovery.ServiceDescription)
	 */
	public void unpublish(ServiceDescription serviceDescription) {
		logService.log(LogService.LOG_DEBUG, "unpublish service " + serviceDescription);

		// delegate to protocol implementation
		try {
			slpHandler.unpublishService(new SLPServiceDescriptionAdapter(serviceDescription));
		}
		catch (Exception e) {
			e.printStackTrace();
			logService.log(LogService.LOG_ERROR, "Failed to unpublish service", e);
		}
	}

	void destroy() {
		logService.log(LogService.LOG_DEBUG, "destroy");

		listeners.clear();
		listeners = null;
		
		slpHandler.destroy();
	}

	public void init() {
		logService.log(LogService.LOG_DEBUG, "init");

		slpHandler.init();
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
		slpHandler.setLogService(logService);
	}
}
