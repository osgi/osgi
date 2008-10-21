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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.discovery.Discovery;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServiceListener;
import org.osgi.service.log.LogService;

/**
 * Discovery reference implementation. This implementation supports any protocol
 * implementation that implements the {@link ProtocolHandler} interface.
 * 
 * TODO: add support for second protocol, e.g. JGroups, JCS
 * 
 * @author Tim Diekmann
 * @author Thomas Kiesslich
 */
public abstract class AbstractDiscovery implements Discovery {
	private static final boolean DEFAULT_AUTOPUBLISH = true;

	private final BundleContext context;

	private LogService logService;
	private boolean autoPublish = DEFAULT_AUTOPUBLISH;
	private Map listenerAndFilter = null;

	// TODO do we need a logService as parameter??
	public AbstractDiscovery(final BundleContext context,
			final LogService logService) {
		this.logService = logService;
		this.context = context;
	}

	/**
	 * Initialize this object.
	 */
	protected void init() {
		log(LogService.LOG_DEBUG, "init");

		// TODO read this from config rather than system property
		autoPublish = System.getProperty(Discovery.OSGI_DISCOVERY,
				Discovery.OSGI_DISCOVERY_AUTO_PUBLISH).equalsIgnoreCase(
				Discovery.OSGI_DISCOVERY_AUTO_PUBLISH);

		listenerAndFilter = new HashMap();
	}

	/**
	 * Cleanup.
	 */
	protected void destroy() {
		log(LogService.LOG_DEBUG, "destroy");
		if (listenerAndFilter != null) {
			synchronized (listenerAndFilter) {
				listenerAndFilter.clear();
			}
			listenerAndFilter = null;
		}
	}

	/**
	 * 
	 */
	protected void log(int logLevel, String msg) {
		if (getLogService() != null) {
			getLogService().log(logLevel, msg);
		}
	}

	/**
	 * 
	 */
	protected void log(int logLevel, String msg, Exception e) {
		if (getLogService() != null) {
			getLogService().log(logLevel, msg, e);
		}
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
		synchronized (listenerAndFilter) {
			// TODO: the same listener object might be registered with several
			// filters. Our Map is not capable of multiple keys. Discuss whether
			// it's the required behaviour.
			listenerAndFilter.put(listener, f);
			log(LogService.LOG_DEBUG, "listener added successfully. Listener: "
					+ listener + "; filter: " + filter);
		}
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#addServiceListener(org.osgi.service.discovery.RemoteServiceListener)
	 */
	public void addServiceListener(final ServiceListener listener) {
		addServiceListener(listener, null);
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#removeServiceListener(org.osgi.service.discovery.RemoteServiceListener)
	 */
	public void removeServiceListener(final ServiceListener listener) {
		if (listener == null) {
			return;
		}
		synchronized (listenerAndFilter) {
			boolean removed = (null == listenerAndFilter.remove(listener));
			log(
					LogService.LOG_DEBUG,
					removed == true ? "listener removed successfull"
							: "listener which had to be removed wasn't registered.");
		}
	}

	/*
	 * @see org.osgi.service.discovery.Discovery#publishService(java.util.Map,
	 *      java.util.Map, java.util.Map)
	 */
	public ServiceEndpointDescription publishService(
			Map/* <String, String> */javaInterfacesAndVersions,
			Map/* <String, String> */javaInterfacesAndEndpointInterfaces,
			Map/* <String, Object> */properties) {
		return publishService(javaInterfacesAndVersions,
				javaInterfacesAndEndpointInterfaces, properties,
				this.autoPublish);
	}

	/**
	 * @param logService
	 *            the reference to the LogService which get called for logging
	 * 
	 */
	protected void setLogService(final LogService logService) {
		this.logService = logService;
	}

	protected LogService getLogService() {
		return logService;
	}

	protected boolean isAutoPublish() {
		return autoPublish;
	}

	protected void setAutoPublish(boolean autoPublish) {
		this.autoPublish = autoPublish;
	}

	protected BundleContext getContext() {
		return context;
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
	protected Filter getFilterFromString(String filter)
			throws IllegalArgumentException {
		if (filter == null) {
			return null;
		}
		Filter f = null;
		try {
			f = context.createFilter(filter);
		} catch (InvalidSyntaxException e) {
			// TODO: add filter
			throw new IllegalArgumentException("filter is not an LDAP filter");
		}
		if (f == null) {
			// TODO: add filter
			throw new IllegalArgumentException(
					"cannot create an LDAP filter with given filter parameter");
		}
		return f;
	}

	/**
	 * @param serviceDescription
	 */
	protected void validateServiceDescription(
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

	protected void notifyListenersOnNewServiceDescription(
			ServiceEndpointDescription svcDescr) {
		synchronized (listenerAndFilter) {
			Iterator it = listenerAndFilter.keySet().iterator();
			while (it.hasNext()) {
				ServiceListener sl = (ServiceListener) it.next();
				Filter filter = (Filter) listenerAndFilter.get(sl);
				// inform it if the listener has no Filter set
				// or the filter matches the criteria
				if (filter == null
						|| (filter != null && filter.match(new Hashtable(
								svcDescr.getProperties())))) {
					try {
						sl.serviceAvailable(svcDescr);
					} catch (Exception e) {
						log(
								LogService.LOG_ERROR,
								"Exceptions where thrown while notifying about a new remote service.",
								e);
					}
				}
			}
		}
	}

	protected void notifyListenersOnRemovedServiceDescription(
			ServiceEndpointDescription svcDescr) {
		synchronized (listenerAndFilter) {
			Iterator it = listenerAndFilter.keySet().iterator();
			while (it.hasNext()) {
				ServiceListener sl = (ServiceListener) it.next();
				Filter filter = (Filter) listenerAndFilter.get(sl);
				// inform it if the listener has no Filter set
				// or the filter matches the criteria
				if (filter == null
						|| (filter != null && filter.match(new Hashtable(
								svcDescr.getProperties())))) {
					try {
						sl.serviceUnavailable(svcDescr);
					} catch (Exception e) {
						log(
								LogService.LOG_ERROR,
								"Exceptions where thrown while notifying about removal of a remote service.",
								e);
					}
				}
			}
		}
	}

	protected void notifyListenersOnModifiedServiceDescription(
			ServiceEndpointDescription oldSvcDescr,
			ServiceEndpointDescription newSvcDescr) {
		synchronized (listenerAndFilter) {
			Iterator it = listenerAndFilter.keySet().iterator();
			while (it.hasNext()) {
				ServiceListener sl = (ServiceListener) it.next();
				Filter filter = (Filter) listenerAndFilter.get(sl);
				// inform it if the listener has no Filter set
				// or the filter matches the criteria
				if (filter == null
						|| (filter != null && filter.match(new Hashtable(
								oldSvcDescr.getProperties())))) {
					try {
						sl.serviceModified(oldSvcDescr, newSvcDescr);
					} catch (Exception e) {
						log(
								LogService.LOG_ERROR,
								"Exceptions where thrown while notifying about modification of a remote service.",
								e);
					}
				}
			}
		}
	}

	protected void validateFilter(final String filter) {
		// check validity of the given filter
		if (filter != null) {
			try {
				getContext().createFilter(filter);
			} catch (InvalidSyntaxException e1) {
				// TODO log
				throw new IllegalArgumentException(
						"filter is not an LDAP filter");
			}
		}
	}

	/**
	 * @return the listenerAndFilter
	 */
	protected Map getListenerAndFilter() {
		return listenerAndFilter;
	}
}
