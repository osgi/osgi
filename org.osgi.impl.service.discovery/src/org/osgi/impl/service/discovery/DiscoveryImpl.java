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

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.Discovery;
import org.osgi.service.discovery.FindServiceCallback;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServiceListener;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Discovery reference implementation. This implementation supports any protocol
 * implementation that implements the {@link ProtocolHandler} interface.
 * 
 * TODO: add support for second protocol, e.g. JGroups, JCS
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
	private final BundleContext context;

	// TODO do we need a logService as parameter??
	public DiscoveryImpl(final BundleContext context,
			final LogService logService) {
		this.logService = logService;
		this.context = context;

		// TODO read this from config rather than system property
		autoPublish = System.getProperty(Discovery.ORG_OSGI_DISCOVERY,
				Discovery.ORG_OSGI_DISCOVERY_AUTO_PUBLISH).equalsIgnoreCase(
				Discovery.ORG_OSGI_DISCOVERY_AUTO_PUBLISH);

		listeners = new LinkedList();
		protocolHandlers = new LinkedList();
		protocolHandlerTracker = new ServiceTracker(context,
				ProtocolHandler.class.getName(),
				new ProtocolHandlerServiceTracker(context));
		protocolHandlerTracker.open();

	}

	/**
	 * 
	 * @see org.osgi.service.discovery.Discovery#addServiceListener(org.osgi.service.discovery.RemoteServiceListener,
	 *      java.lang.String)
	 */
	public void addServiceListener(ServiceListener listener, String filter) {
		// TODO implement this
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#addServiceListener(org.osgi.service.discovery.RemoteServiceListener)
	 */
	public void addServiceListener(final ServiceListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener cannot be null");
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
	 * @see org.osgi.service.discovery.Discovery#removeServiceListener(org.osgi.service.discovery.RemoteServiceListener)
	 */
	public void removeServiceListener(final ServiceListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener cannot be null");
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
	 * @see org.osgi.service.discovery.Discovery#publish(org.osgi.service.discovery.ServiceEndpointDescription)
	 */
	public boolean publishService(
			final ServiceEndpointDescription serviceDescription) {
		return publishService(serviceDescription, autoPublish);
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#publish(org.osgi.service.discovery.ServiceEndpointDescription,
	 *      boolean)
	 */
	public boolean publishService(
			final ServiceEndpointDescription serviceDescription,
			boolean autopublish) {
		validateServiceDescription(serviceDescription);
		if (logService != null) {
			String logMessage = "publish service having following interfaces: ";
			String[] interfaceNames = serviceDescription.getInterfaceNames();
			for (int i = 0; i < interfaceNames.length; i++) {
				logMessage += interfaceNames[i] + ";";
			}
			logService.log(LogService.LOG_DEBUG, logMessage);
		}
		boolean done = false;
		// delegate to protocol implementation
		synchronized (protocolHandlers) {
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
		}
		return done;
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#unpublish(org.osgi.service.discovery.ServiceEndpointDescription)
	 */
	public void unpublishService(
			final ServiceEndpointDescription serviceDescription) {
		validateServiceDescription(serviceDescription);
		if (logService != null) {
			logService.log(LogService.LOG_DEBUG, "unpublish service "
					+ serviceDescription.toString());
		}
		synchronized (protocolHandlers) {
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
	}

	/**
	 * Cleanup.
	 */
	void destroy() {
		protocolHandlerTracker.close();
		if (logService != null) {
			logService.log(LogService.LOG_DEBUG, "destroy");
		}
		if (listeners != null) {
			listeners.clear();
			listeners = null;
		}
		if (protocolHandlers != null) {
			protocolHandlers.clear();
			protocolHandlers = null;
		}
	}

	/**
	 * Initialize this object.
	 */
	public void init() {
		if (logService != null) {
			logService.log(LogService.LOG_DEBUG, "init");
		}
	}

	/**
	 * @param logService
	 *            the reference to the LogService which get called for logging
	 * 
	 */
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
		synchronized (protocolHandlers) {
			protocolHandlers.add(pHandler);
		}
	}

	/**
	 * For test purposes only.
	 * 
	 * @param pHandler
	 */
	final void removeProtocolHandler(ProtocolHandler pHandler) {
		synchronized (protocolHandlers) {
			protocolHandlers.remove(pHandler);
		}
	}

	private ServiceEndpointDescription[] findService(String interfaceName,
			Filter filter) {
		Vector services = new Vector();
		synchronized (protocolHandlers) {
			for (int i = 0; (protocolHandlers != null)
					&& (i < protocolHandlers.size()); i++) {
				ServiceEndpointDescription[] returnedServices = ((ProtocolHandler) protocolHandlers
						.get(i)).findService(interfaceName, filter);
				if (returnedServices != null) {
					for (int k = 0; k < returnedServices.length; k++) {
						services.add(returnedServices[k]);
					}
				}
			}
		}
		// TODO add to cache
		return (ServiceEndpointDescription[]) services.toArray(new ServiceEndpointDescription[]{});
	}

	/**
	 * This method tries to create a Filter object from a given String via the
	 * {@link BundleContext#createFilter(String)}. This is used by the
	 * findService methods.
	 * 
	 * @param filter
	 *            a String that represents an LDAP filter
	 * @return the created Filter object, or null if filter is null
	 * @throws IllegalArgumentException
	 *             if a Filter object could not be created with the given String
	 */
	private Filter getFilterFromString(String filter)
			throws IllegalArgumentException {
		if (filter == null) {
			return null;
		}
		Filter f = null;
		try {
			f = context.createFilter(filter);
		} catch (InvalidSyntaxException e) {
			throw new IllegalArgumentException("filter is not an LDAP filter");
		}
		if (f == null) {
			throw new IllegalArgumentException(
					"cannot create an LDAP filter with given filter parameter");
		}
		return f;
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.Discovery#findService(java.lang.String)
	 */
	public ServiceEndpointDescription[] findService(final String interfaceName,
			final String filter) {
		Filter f = getFilterFromString(filter);
		return findService(interfaceName, f);
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#findService(String,
	 *      FindServiceCallback)
	 */
	public void findService(final String interfaceName, final String filter,
			final FindServiceCallback callback) {
		if (callback == null) {
			throw new IllegalArgumentException("callback must not be null");
		}
		final Filter f = getFilterFromString(filter);
		// create a new thread to find the services
		Thread executor = new Thread(new Runnable() {
			public void run() {
				try {
					// TODO first look at cache

					// if miss, do lookup with ProtocolHandler
					ServiceEndpointDescription[] services = findService(
							interfaceName, f);
					if (logService != null && (services.length == 0)) {
						logService.log(LogService.LOG_DEBUG,
								"no services found for " + interfaceName
										+ " and " + filter);
					}
					try {
						callback.servicesFound(services);
					} catch (Exception e) {
						if (logService != null) {
							logService.log(LogService.LOG_ERROR,
									"callback serviceUnavailable failed", e);
						}
					}
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
	 * @param serviceDescription
	 */
	private void validateServiceDescription(
			ServiceEndpointDescription serviceDescription) {
		if (serviceDescription == null)
			throw new IllegalArgumentException(
					"serviceDescription must not be null.");

		if (serviceDescription.getInterfaceNames() == null
				|| serviceDescription.getInterfaceNames().length <= 0
				|| serviceDescription.getInterfaceNames()[0] == null
				|| serviceDescription.getInterfaceNames()[0].length() <= 0) {
			throw new IllegalArgumentException(
					"serviceDescription must contain at least one service interface name.");
		}
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
			synchronized (protocolHandlers) {
				protocolHandlers.add(protocolHandler);
			}
			protocolHandler.init();
			return protocolHandler;
		}

		public void modifiedService(ServiceReference reference, Object service) {
			// ignore
		}

		public void removedService(ServiceReference reference, Object service) {
			ProtocolHandler protocolHandler = (ProtocolHandler) context
					.getService(reference);
			synchronized (protocolHandlers) {
				protocolHandlers.remove(protocolHandler);
			}
		}

	}
}
