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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
 * TODO: think whether the concept with Protocolhandlers was realy a good idea.
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

	private Map listenerAndFilter = null;

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
		listenerAndFilter = new HashMap();
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
		if (listener == null) {
			throw new IllegalArgumentException("listener cannot be null");
		}
		Filter f = getFilterFromString(filter);
		//TODO: Why do we have listeners and listeners&filters? One is enough, right?
		synchronized (listeners) {
			boolean done = false;
			if (!listeners.contains(listener)) {
				done = listeners.add(listener);
				//TODO: the same listener object might be registered with several filters. Our Map is not capable of multiple keys. Discuss whether it's the required behaviour.
				synchronized (listenerAndFilter) {
					listenerAndFilter.put(listener, f);
					if (logService != null) {
						logService.log(LogService.LOG_DEBUG,
								"filter associated with listener");
					}
				}
			}
			if (logService != null) {
				logService.log(LogService.LOG_DEBUG, "listener " + listener
						+ " add successfull ? " + done);
			}
		}
		//TODO: Why there are no actions taken after adding a listener?
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
			return;
		}
		//TODO: this listener might have had filters as well?
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

	//TODO: think whether we need a version with autopublish parameter
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
					if (done) {
						// inform the listener about the new available service
						synchronized (listeners) {
							Iterator it = listeners.iterator();
							while (it.hasNext()) {
								ServiceListener sl = (ServiceListener) it
										.next();
								Filter f = (Filter) listenerAndFilter.get(sl);
								// inform it if the listener has no Filter set
								// or the filter matches the criteria
								// TODO check whether the matching operation is
								// a specficication or implementation problem.
								// We currently use the Filter class of the
								// Equinox. It is not possible to define an
								// array (interface names) as a String
								// representation (parameter of
								// addServiceListener()). So we cannot match for
								// interfaces. So we have to write our own match
								// method. Is that intendend?

								if (f == null
										|| (f != null && match(f,
												serviceDescription))) {
									sl.serviceAvailable(serviceDescription);
								}
							}
						}
					}
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
	 * Checks whether a ServiceEndpointDescription matches a given Filter.
	 * 
	 * TODO check whether this is appropriate!!
	 * 
	 * @param f
	 *            the given Filter
	 * @param sd
	 *            the ServiceEndpointDescription to check
	 * @return true if the ServiceEndpointDescriptioin matches the Filter, else
	 *         false.
	 */
	private boolean match(Filter f, ServiceEndpointDescription sd) {
		boolean isMatching = false;
		//TODO: all properties are already in the property map. Describe it explicitely in JavaDoc.
		Dictionary dict = new Hashtable();
		String[] interfaceNames = sd.getInterfaceNames();
		int i = 0;
		for (i = 0; i < interfaceNames.length; i++) {
			dict.put(ServiceEndpointDescription.PROP_KEY_INTERFACE_NAME,
					interfaceNames[i]);
			if (f.matchCase(dict)) {
				isMatching = true;
			}
		}
		// TODO fowolling code makes not sense if we have a complex filter, e.g.
		// (interfaceName=234) & (protocol-specific-interface-name=frgfrg) &
		// (version=3.4.3)
		for (i = 0; i < interfaceNames.length; i++) {
			String protocolSpecificIfName = sd
					.getProtocolSpecificInterfaceName(interfaceNames[i]);
			if (protocolSpecificIfName != null) {
				dict
						.put(
								ServiceEndpointDescription.PROP_KEY_PROTOCOL_SPECIFIC_INTERFACE_NAME,
								protocolSpecificIfName);
			}
			String version = sd.getVersion(interfaceNames[i]);
			if (version != null) {
				dict.put(ServiceEndpointDescription.PROP_KEY_VERSION, version);
			}
			//TODO: matching is tried without adding other properties?
			if (f.matchCase(dict)) {
				isMatching = true;
			}
		}
		dict = new Hashtable(sd.getProperties());
		if (f.matchCase(dict)) {
			isMatching = true;
		}

		return isMatching;
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
		//Shouldn't registered listeners tried out with new pHandler?
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
			// TODO first look at cache
			//TODO: think whether in order to decrease response time we should parallize lookup process or use async invocation
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
		return (ServiceEndpointDescription[]) services
				.toArray(new ServiceEndpointDescription[] {});
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
			//TODO: add filter
			throw new IllegalArgumentException("filter is not an LDAP filter");
		}
		if (f == null) {
			//TODO: add filter
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
		//TODO: think whether it would be not more efficient to make async to the base impl
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
