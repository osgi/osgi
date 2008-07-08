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
import java.util.LinkedList;
import java.util.List;

import org.eclipse.osgi.framework.internal.core.FilterImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.Discovery;
import org.osgi.service.discovery.FindServiceCallback;
import org.osgi.service.discovery.ServiceDescription;
import org.osgi.service.discovery.ServiceListener;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Discovery implementation. This implementation supports the SLP protocol
 * implementation.
 * 
 * TODO: add support for second protocol, e.g. JGroups
 * 
 * @author Tim Diekmann
 * @author Thomas Kiesslich
 */
public class DiscoveryImpl implements Discovery {
	private static final boolean DEFAULT_AUTOPUBLISH = true;

	private List listeners;
	private List protocolHandlers;
	private LogService logService;
	private boolean autoPublish = DEFAULT_AUTOPUBLISH;

	private ServiceTracker protocolHandlerTracker;

	//TODO do we need a logService as parameter??
	public DiscoveryImpl(final BundleContext context,
			final LogService logService) {
		this.logService = logService;

		// TODO read this from config rather than system property
		autoPublish = System.getProperty(Discovery.ORG_OSGI_DISCOVERY,
				Discovery.ORG_OSGI_DISCOVERY_AUTO_PUBLISH).equalsIgnoreCase(
				Discovery.ORG_OSGI_DISCOVERY_AUTO_PUBLISH);

		listeners = new LinkedList();
		protocolHandlers = new LinkedList();
		protocolHandlerTracker = new ServiceTracker(context,
				ProtocolHandler.class.getName(), new ProtocolHandlerServiceTracker(context));
		protocolHandlerTracker.open();

	}

	/**
	 * @see org.osgi.service.discovery.Discovery#addServiceListener(org.osgi.service.discovery.ServiceListener)
	 */
	public void addServiceListener(final ServiceListener listener) {
		// TODO throw IllegalArgumentException if listener == null??
		if (listener == null) {
			return;
		}
		synchronized (listeners) {
			boolean done = false;
			if (!listeners.contains(listener)) {
				done = listeners.add(listener);
			}
			if (logService != null) {
				logService.log(LogService.LOG_DEBUG, "listener " + listener
						+ " add successfull ? " + done);
			}
		}
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#removeServiceListener(org.osgi.service.discovery.ServiceListener)
	 */
	public void removeServiceListener(final ServiceListener listener) {
		// TODO throw IllegalArgumentException if listener == null??
		if (listener == null) {
			return;
		}
		synchronized (listeners) {
			boolean done = false;
			done = listeners.remove(listener);
			if (logService != null) {
				logService.log(LogService.LOG_DEBUG, "listener removed, "
						+ listener + " successfull? " + done);
			}
		}
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#findService(org.osgi.service.discovery.ServiceDescription,
	 *      org.osgi.service.discovery.ServiceListener)
	 */
	public void findService(final ServiceDescription serviceDescription,
			final FindServiceCallback callback) {
		if (serviceDescription == null
				|| serviceDescription.getInterfaceName() == null
				|| serviceDescription.getInterfaceName().equals("")) {
			throw new IllegalArgumentException(
					"serviceDescription must not be null or incomplete");
		}
		if (callback == null) {
			throw new IllegalArgumentException("callback must not be null");
		}

		Thread executor = new Thread(new Runnable() {

			public void run() {
				try {
					// TODO first look at cache

					// if miss, do lookup with ProtocolHandler
					Collection services = findService(serviceDescription);

					// call callback listener
					// call callback with unavailable if none found
					if (logService != null) {
						logService.log(LogService.LOG_DEBUG,
								"no services found for " + serviceDescription);
					}
					try {
						callback.servicesFound(services);
					} catch (Exception e) {
						if (logService != null) {
							logService.log(LogService.LOG_ERROR,
									"callback serviceUnavailable failed", e);
						}
					}

					return; // leave this thread
				} catch (Exception e) {
					if (logService != null) {
						logService.log(LogService.LOG_ERROR,
								"Failed to execute async findService", e);
					}
				}
			}
		});
		executor.start();
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#findService(org.osgi.service.discovery.ServiceDescription)
	 */
	public Collection findService(final ServiceDescription serviceDescription) {
		if (logService != null) {
			logService.log(LogService.LOG_DEBUG, "find services for "
					+ serviceDescription.getInterfaceName());
		}

		if (serviceDescription == null) {
			throw new IllegalArgumentException(
					"serviceDescription must not be null or incomplete");
		}

		Collection services = new LinkedList();
		for (int i = 0; (protocolHandlers != null)
				&& (i < protocolHandlers.size()); i++) {
			Collection returnedServices = ((ProtocolHandler) protocolHandlers
					.get(i)).findService(serviceDescription);
			if (returnedServices != null) {
				services.addAll(returnedServices);
			}
		}
		// TODO add to cache

		return services;
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#getCachedServiceDescriptions()
	 */
	public Collection getCachedServiceDescriptions() {
		// TODO return cached instances
		return new LinkedList();
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#isCached(org.osgi.service.discovery.ServiceDescription)
	 */
	public boolean isCached(final ServiceDescription serviceDescription) {
		// TODO delegate to cache
		return false;
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#publish(org.osgi.service.discovery.ServiceDescription)
	 */
	public boolean publishService(final ServiceDescription serviceDescription) {
		return publishService(serviceDescription, autoPublish);
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#publish(org.osgi.service.discovery.ServiceDescription,
	 *      boolean)
	 */
	public boolean publishService(final ServiceDescription serviceDescription,
			boolean autopublish) {
		if (serviceDescription == null
				|| serviceDescription.getInterfaceName() == null
				|| serviceDescription.getInterfaceName().equals("")) {
			throw new IllegalArgumentException(
					"serviceDescription must not be null or incomplete");
		}

		if (logService != null) {
			logService.log(LogService.LOG_DEBUG, "publish service "
					+ serviceDescription.getInterfaceName());
		}
		boolean done = false;
		// delegate to protocol implementation
		for (int i = 0; (protocolHandlers != null)
				&& (i < protocolHandlers.size()); i++) {
			try {
				done = ((ProtocolHandler) protocolHandlers.get(i))
						.publishService(serviceDescription);
			} catch (Exception e) {
				e.printStackTrace();
				logService.log(LogService.LOG_ERROR,
						"Failed to publish service", e);
			}
		}

		return done;
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#unpublish(org.osgi.service.discovery.ServiceDescription)
	 */
	public void unpublishService(final ServiceDescription serviceDescription) {
		if (serviceDescription == null
				|| serviceDescription.getInterfaceName() == null
				|| serviceDescription.getInterfaceName().equals("")) {
			throw new IllegalArgumentException(
					"serviceDescription must not be null or incomplete");
		}

		if (logService != null) {
			logService.log(LogService.LOG_DEBUG, "unpublish service "
					+ serviceDescription.toString());
		}

		for (int i = 0; (protocolHandlers != null)
				&& (i < protocolHandlers.size()); i++) {
			try {
				((ProtocolHandler) protocolHandlers.get(i))
						.unpublishService(serviceDescription);
			} catch (Exception e) {
				e.printStackTrace();
				if (logService != null) {
					logService.log(LogService.LOG_ERROR,
							"Failed to unpublish service", e);
				}
			}
		}
	}

	void destroy() {
		if (logService != null) {
			logService.log(LogService.LOG_DEBUG, "destroy");
		}
		if (listeners != null) {
			listeners.clear();
			listeners = null;
		}
	}

	public void init() {
		if (logService != null) {
			logService.log(LogService.LOG_DEBUG, "init");
		}
	}

	public void setLogService(final LogService logService) {
		this.logService = logService;
	}

	/**
	 * For test purposes only
	 * 
	 * @return the list of listeners
	 */
	final List getListeners() {
		return listeners;
	}

	/**
	 * For test purposes only.
	 * 
	 * @param pHandler
	 */
	final void addProtocolHandler(ProtocolHandler pHandler) {
		protocolHandlers.add(pHandler);
	}

	/**
	 * For test purposes only.
	 * 
	 * @param pHandler
	 */
	final void removeProtocolHandler(ProtocolHandler pHandler) {
		protocolHandlers.remove(pHandler);
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.Discovery#addServiceListener(org.osgi.service.discovery.ServiceListener,
	 *      org.osgi.service.discovery.ServiceDescription)
	 */
	public void addServiceListener(ServiceListener listener,
			ServiceDescription serviceDescription) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @see org.osgi.service.discovery.Discovery#addServiceListener(org.osgi.service.discovery.ServiceListener,
	 *      java.lang.String)
	 */
	public void addServiceListener(ServiceListener listener, String filter) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @see org.osgi.service.discovery.Discovery#findService(java.lang.String)
	 */
	public Collection findService(String filter) {
		if (filter == null) {
			throw new IllegalArgumentException(
					"filter must not be null or incomplete");
		}
		Filter f = null;
		try {
			f = new FilterImpl(filter);
		} catch (InvalidSyntaxException e) {
			throw new IllegalArgumentException("filter is not an LDAP filter");
		}
		Collection services = new LinkedList();
		for (int i = 0; (protocolHandlers != null)
				&& (i < protocolHandlers.size()); i++) {
			Collection returnedServices = ((ProtocolHandler) protocolHandlers
					.get(i)).findService(f);
			if (returnedServices != null) {
				services.addAll(returnedServices);
			}
		}
		// TODO add to cache

		return services;
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#findService(String,
	 *      FindServiceCallback)
	 */
	public void findService(final String filter,
			final FindServiceCallback callback) {
		if (callback == null) {
			throw new IllegalArgumentException("callback must not be null");
		}

		if (filter == null) {
			throw new IllegalArgumentException(
					"filter must not be null or incomplete");
		}

		try {
			new FilterImpl(filter);
		} catch (InvalidSyntaxException e) {
			throw new IllegalArgumentException("filter is not an LDAP filter");
		}

		Thread executor = new Thread(new Runnable() {

			public void run() {
				try {
					// TODO first look at cache

					// if miss, do lookup with ProtocolHandler
					Collection services = findService(filter);

					// call callback listener
					// call callback with unavailable if none found
					if (logService != null) {
						logService.log(LogService.LOG_DEBUG,
								"no services found for " + filter);
					}
					try {
						callback.servicesFound(services);
					} catch (Exception e) {
						if (logService != null) {
							logService.log(LogService.LOG_ERROR,
									"callback serviceUnavailable failed", e);
						}
					}

					return; // leave this thread
				} catch (Exception e) {
					if (logService != null) {
						logService.log(LogService.LOG_ERROR,
								"Failed to execute async findService", e);
					}
				}
			}
		});
		executor.start();
	}

	/**
	 * Private tracker class.
	 * 
	 * @author kt32483
	 *
	 */
	private class ProtocolHandlerServiceTracker implements
			ServiceTrackerCustomizer {
		BundleContext context = null;

		public ProtocolHandlerServiceTracker(BundleContext bc) {
			context = bc;
		}

		public Object addingService(ServiceReference reference) {
			ProtocolHandler protocolHandler = (ProtocolHandler) context
					.getService(reference);
			protocolHandlers.add(protocolHandler);
			protocolHandler.init();
			return protocolHandler;
		}

		public void modifiedService(ServiceReference reference, Object service) {
			// ignore
		}

		public void removedService(ServiceReference reference, Object service) {
			ProtocolHandler protocolHandler = (ProtocolHandler) context
					.getService(reference);
			protocolHandlers.remove(protocolHandler);
		}

	}

}
