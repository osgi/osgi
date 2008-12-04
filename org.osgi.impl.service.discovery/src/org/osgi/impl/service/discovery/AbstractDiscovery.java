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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.DiscoveredServiceNotification;
import org.osgi.service.discovery.DiscoveredServiceTracker;
import org.osgi.service.discovery.Discovery;
import org.osgi.service.discovery.FindServiceCallback;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServicePublication;
import org.osgi.service.log.LogService;

/**
 * Discovery reference implementation. This implementation supports any protocol
 * implementation that implements the {@link ProtocolHandler} interface.
 * 
 * @author Tim Diekmann
 * @author Thomas Kiesslich
 */
public abstract class AbstractDiscovery implements Discovery {
	private static final boolean DEFAULT_AUTOPUBLISH = true;

	private final BundleContext context;

	private LogService logService;
	private boolean autoPublish = DEFAULT_AUTOPUBLISH;

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
		autoPublish = System.getProperty(Discovery.PROP_KEY_PUBLISH_STRATEGY,
				Discovery.PROP_VAL_PUBLISH_STRATEGY_PUSH).equalsIgnoreCase(
				Discovery.PROP_VAL_PUBLISH_STRATEGY_PUSH);

	}

	/**
	 * Cleanup.
	 */
	protected void destroy() {
		log(LogService.LOG_DEBUG, "destroy");
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
	 * @see org.osgi.service.discovery.Discovery#publishService(java.util.Map,
	 *      java.util.Map, java.util.Map)
	 */
	public ServicePublication publishService(
			Map/* <String, String> */javaInterfacesAndVersions,
			Map/* <String, String> */javaInterfacesAndEndpointInterfaces,
			Map/* <String, Object> */properties) {
		return publishService(javaInterfacesAndVersions,
				javaInterfacesAndEndpointInterfaces, properties,
				Discovery.PROP_VAL_PUBLISH_STRATEGY_PUSH);
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
		if (serviceDescription.getProvidedInterfaces() == null) {
			throw new IllegalArgumentException(
					"Given set of Java interfaces must not be null");
		}
		String ifName = (String) serviceDescription.getProvidedInterfaces()
				.iterator().next();
		if (serviceDescription.getProvidedInterfaces() == null
				|| serviceDescription.getProvidedInterfaces().size() <= 0
				|| ifName == null || ifName.length() <= 0) {
			throw new IllegalArgumentException(
					"serviceDescription must contain at least one service interface name.");
		}
	}

	protected Map getRegisteredServiceTracker() {
		Map l = new HashMap();
		ServiceReference[] refs = null;
		try {
			refs = context.getServiceReferences(DiscoveredServiceTracker.class
					.getName(), null);
		} catch (InvalidSyntaxException e) {
			log(LogService.LOG_ERROR, "Unexpected exception.");
		}
		if (refs != null) {
			for (int i = 0; i < refs.length; i++) {
				HashMap props = new HashMap();
				String[] keys = refs[i].getPropertyKeys();
				for (int k = 0; (keys != null) && (k < keys.length); k++) {
					if (refs[i].getProperty(keys[k]) instanceof String[]) {
						props.put(keys[k], (String[]) refs[i]
								.getProperty(keys[k]));
					} else {
						props.put(keys[k], refs[i].getProperty(keys[k]));
					}
				}
				l.put((DiscoveredServiceTracker) context.getService(refs[i]),
						props);
			}
		}

		return l;
	}

	protected void notifyListenersOnNewServiceDescription(
			ServiceEndpointDescription svcDescr) {
		// can we iterate over that array w/o threadsafety??
		Map discoveredSTs = getRegisteredServiceTracker();
		Iterator it = discoveredSTs.keySet().iterator();
		while (it.hasNext()) {
			DiscoveredServiceTracker st = (DiscoveredServiceTracker) it.next();
			Map trackerProps = (Map) discoveredSTs.get(st);
			boolean notify = checkMatch(svcDescr, trackerProps);
			if (notify) {
				try {
					st.serviceChanged(new DiscoveredServiceNotificationImpl(
							svcDescr, DiscoveredServiceNotification.AVAILABLE));
				} catch (Exception e) {
					log(
							LogService.LOG_ERROR,
							"Exceptions where thrown while notifying about a new remote service.",
							e);
				}

			}
		}
	}

	protected void notifyListenersOnRemovedServiceDescription(
			ServiceEndpointDescription svcDescr) {
		Map discoveredSTs = getRegisteredServiceTracker();
		Iterator it = discoveredSTs.keySet().iterator();
		while (it.hasNext()) {
			DiscoveredServiceTracker st = (DiscoveredServiceTracker) it.next();
			Map trackerProps = (Map) discoveredSTs.get(st);
			// inform it if the listener has no Filter set
			// or the filter matches the criteria
			boolean notify = checkMatch(svcDescr, trackerProps);
			if (notify) {

				try {
					st
							.serviceChanged(new DiscoveredServiceNotificationImpl(
									svcDescr,
									DiscoveredServiceNotification.UNAVAILABLE));
				} catch (Exception e) {
					log(
							LogService.LOG_ERROR,
							"Exceptions where thrown while notifying about removal of a remote service.",
							e);
				}
			}
		}

	}

	public boolean checkMatch(ServiceEndpointDescription svcDescr,
			Map/* String, Object */trackerProperties) {
		String[] interfaceFilter = (String[]) trackerProperties
				.get(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_INTERFACES);
		String[] filter = (String[]) trackerProperties
				.get(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_FILTERS);
		boolean notify = false;
		if (interfaceFilter == null && filter == null) {
			notify = true;
		} else {
			if (interfaceFilter != null) {
				// check whether tracker's interface-list contains one of SED's
				// interfaces
				for (int i = 0; i < interfaceFilter.length; i++) {
					if (svcDescr.getProvidedInterfaces().contains(
							interfaceFilter[i])) {
						notify = true;
					}
				}
			}
			if (filter != null) {
				// check whether one filter of tracker's filter-list matches to
				// SED's properties
				for (int i = 0; i < filter.length; i++) {
					try {
						Filter f = getContext().createFilter(filter[i]);
						if (f.match(new Hashtable(svcDescr.getProperties()))) {
							notify = true;
						}
					} catch (InvalidSyntaxException e) {
						String errMsg = "A filter provided by a DiscoveredServiceTracker is invalid.";
						errMsg += " Filter = " + filter[i];
						errMsg += "; DiscoveredServiceTracker service.id = "
								+ trackerProperties.get(Constants.SERVICE_ID);
						log(LogService.LOG_WARNING, errMsg, e);
					}
				}
			}
		}
		return notify;
	}

	protected void notifyListenersOnModifiedServiceDescription(
			ServiceEndpointDescription svcDescr) {
		Map discoveredSTs = getRegisteredServiceTracker();
		Iterator it = discoveredSTs.keySet().iterator();
		while (it.hasNext()) {
			DiscoveredServiceTracker st = (DiscoveredServiceTracker) it.next();
			Map trackerProps = (Map) discoveredSTs.get(st);
			// inform it if the listener has no Filter set
			// or the filter matches the criteria
			boolean notify = checkMatch(svcDescr, trackerProps);
			if (notify) {
				try {
					st.serviceChanged(new DiscoveredServiceNotificationImpl(
							svcDescr, DiscoveredServiceNotification.MODIFIED));
				} catch (Exception e) {
					log(
							LogService.LOG_ERROR,
							"Exceptions where thrown while notifying about modification of a remote service.",
							e);
				}
			}
		}
	}

	protected Filter validateFilter(final String filter) {
		// check validity of the given filter
		Filter f = null;
		if (filter != null) {
			try {
				f = getContext().createFilter(filter);
			} catch (InvalidSyntaxException e1) {
				// TODO log
				throw new IllegalArgumentException(
						"filter is not an LDAP filter");
			}
		}
		return f;
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.Discovery#findService(java.lang.String,
	 *      java.lang.String, org.osgi.service.discovery.FindServiceCallback)
	 */
	public void findService(final String interfaceName, final String filter,
			final FindServiceCallback callback) {
		if (callback == null) {
			throw new IllegalArgumentException("callback must not be null");
		}
		validateFilter(filter);
		Thread executor = new Thread(new Runnable() {
			public void run() {
				try {
					// do lookup
					Collection/* <ServiceEndpointDescription> */services = findService(
							interfaceName, filter);
					// return result via callback
					try {
						callback.servicesFound(services);
					} catch (Exception e) {
						log(
								LogService.LOG_ERROR,
								"Exceptions where thrown in the callback of findService operation.",
								e);
					}
				} catch (Exception e) {
					log(LogService.LOG_ERROR,
							"Failed to execute async findService", e);
				}
			}
		});
		executor.start();
	}
}
