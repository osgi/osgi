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
package org.osgi.impl.service.discovery.slp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;

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
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import ch.ethz.iks.slp.Advertiser;
import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceURL;

/**
 * TODO: remove printStackTrace and do logging instead
 * 
 * @author Phillip Konradi
 * @author Thomas Kiesslich
 * 
 */
public class SLPHandlerImpl implements Discovery {
	private ServiceTracker locatorTracker = null;
	private ServiceTracker advertiserTracker = null;
	private ServiceTracker spTracker = null;

	private DSTTracker discoTrackerCustomizer = null;
	private ServiceTracker discoTracker = null;

	private Locator locator = null;
	private Advertiser advertiser = null;

	private final int POLLDELAY = 10000; // 10 sec

	private Timer t = null;

	private static final boolean DEFAULT_AUTOPUBLISH = true;

	private final BundleContext context;

	private LogService logService;
	private boolean autoPublish = DEFAULT_AUTOPUBLISH;

	private List inMemoryCache = Collections.synchronizedList(new ArrayList());

	/**
	 * 
	 * @param context
	 * @param logService
	 */
	public SLPHandlerImpl(final BundleContext context,
			final LogService logService) {
		this.logService = logService;
		this.context = context;
		locatorTracker = new ServiceTracker(context, Locator.class.getName(),
				new LocatorServiceTracker(context));
		advertiserTracker = new ServiceTracker(context, Advertiser.class
				.getName(), new AdvertiserServiceTracker(context));

	}

	public void init() {
		log(LogService.LOG_DEBUG, "init");
		autoPublish = System.getProperty(Discovery.PROP_KEY_PUBLISH_STRATEGY,
				Discovery.PROP_VAL_PUBLISH_STRATEGY_PUSH).equalsIgnoreCase(
				Discovery.PROP_VAL_PUBLISH_STRATEGY_PUSH);
		locatorTracker.open();
		advertiserTracker.open();
		spTracker = new ServiceTracker(context, ServicePublication.class
				.getName(), new ServicePublicationTracker(context, this));
		spTracker.open();
		discoTrackerCustomizer = new DSTTracker(context, this);
		discoTracker = new ServiceTracker(context,
				DiscoveredServiceTracker.class.getName(),
				discoTrackerCustomizer);
		discoTracker.open();
		t = new Timer(false);
		t.schedule(new InformListenerTask(this), 0, POLLDELAY);
	}

	public void destroy() {
		log(LogService.LOG_DEBUG, "destroy");
		discoTracker.close();
		spTracker.close();
		locatorTracker.close();
		advertiserTracker.close();
		if (t != null) {
			t.cancel();
			t = null;
		}
	}

	private synchronized Locator getLocator() {
		return locator;
	}

	private synchronized void setLocator(final Locator locator) {
		this.locator = locator;
	}

	private synchronized Advertiser getAdvertiser() {
		return advertiser;
	}

	private synchronized void setAdvertiser(final Advertiser advertiser) {
		this.advertiser = advertiser;
	}

	/**
	 * 
	 * @see org.osgi.impl.service.discovery.ProtocolHandler#findService(org.osgi.service.discovery.ServiceDescription)
	 */
	public Collection/* <ServiceEndpointDescription> */findService(
			final String interfaceName, final String filter) {
		validateFilter(filter);
		// check whether SLP-Locator service exists
		Locator locator = getLocator();
		if (locator == null) {
			log(LogService.LOG_WARNING,
					"No SLP-Locator. Find operation is not executed.");
			return new ArrayList();
		}
		// TODO first look at cache
		// find appropriate services
		ServiceLocationEnumeration se;
		try {
			ServiceURL svcURL = SLPServiceDescriptionAdapter.createServiceURL(
					interfaceName, null, null, null);
			log(LogService.LOG_DEBUG, "try to find services with URL="
					+ svcURL.toString());
			se = locator.findServices(svcURL.getServiceType(), null, filter);
			// TODO: in case result is empty do a search implying that service
			// interface is not java???
		} catch (Exception e) {
			log(LogService.LOG_ERROR, "Failed to find service", e);
			return new ArrayList();
		}
		inMemoryCache.clear();
		// iterate through found services and retrieve their attributes
		while (se.hasMoreElements()) {
			try {
				ServiceURL url = (ServiceURL) se.next();
				log(LogService.LOG_DEBUG, "try to find attributes for " + url);
				ServiceLocationEnumeration a = locator.findAttributes(url,
						null, null); // takes some time :-(
				SLPServiceDescriptionAdapter descriptionAdapter = new SLPServiceDescriptionAdapter(
						url);
				while (a.hasMoreElements()) {
					String attributes = (String) a.next();
					String key = null;
					Object value = null;
					attributes = attributes.substring(1,
							attributes.length() - 1);
					key = attributes.substring(0, attributes.indexOf("="));
					// if the value is not a String we cannot handle that value!
					// This is a limitation of the API.
					value = attributes.substring(attributes.indexOf("=") + 1);
					if (value instanceof String) {
						String val = (String) value;
						if (val.startsWith("[")) {
							val = val.substring(1);
						}
						if (val.endsWith("]")) {
							val = val.substring(0, val.length() - 1);
						}
						value = val;
					}
					descriptionAdapter.addProperty(key, value);
				}
				log(LogService.LOG_DEBUG, "adding serviceURL=" + url);
				inMemoryCache.add(descriptionAdapter);
			} catch (Exception e) {
				log(LogService.LOG_ERROR, "Failed to find service", e);
			}
		}
		if (!inMemoryCache.isEmpty()) {
			StringBuffer buff = new StringBuffer();
			buff.append("number of services = ");
			buff.append(inMemoryCache.size());
			buff.append("; services = ");
			synchronized (inMemoryCache) {
				Iterator it = inMemoryCache.iterator();
				while (it.hasNext()) {
					buff.append("(");
					ServiceEndpointDescription sed = (ServiceEndpointDescription) it
							.next();
					Collection interfaces = sed.getProvidedInterfaces();
					if (interfaces == null) {
						log(LogService.LOG_ERROR, "no interfaces provided by " + sed);
						break;
					}
					Iterator/* String */ifNames = interfaces.iterator();
					while (ifNames.hasNext()) {
						buff.append((String) ifNames.next());
						if (ifNames.hasNext()) {
							buff.append(",");
						}
					}
					buff.append(")");
					if (it.hasNext()) {
						buff.append(",(");
					}
				}
			}
			log(LogService.LOG_DEBUG, buff.toString());
		} else {
			log(LogService.LOG_DEBUG, "0 services found");
		}
		return new ArrayList(inMemoryCache);
	}

	/**
	 * 
	 */
	protected ServiceEndpointDescription publishService(
			Collection/* <String> */javaInterfaces,
			Collection/* <String> */javaInterfacesAndVersions,
			Collection/* <String> */javaInterfacesAndEndpointInterfaces,
			Map/* <String, Object> */properties, String strategy,
			String endpointID) {
		SLPServiceDescriptionAdapter svcDescr;
		try {
			svcDescr = new SLPServiceDescriptionAdapter(javaInterfaces,
					javaInterfacesAndVersions,
					javaInterfacesAndEndpointInterfaces, properties, endpointID);
		} catch (ServiceLocationException e1) {
			e1.printStackTrace();
			log(LogService.LOG_ERROR, "Unable to create Service Description",
					e1);
			return null;
		}
		// TODO: act according strategy parameter
		Advertiser advertiser = getAdvertiser();
		if (advertiser != null) {
			Iterator interfaces = svcDescr.getProvidedInterfaces().iterator();
			while (interfaces.hasNext()) {
				try {
					advertiser.register(svcDescr
							.getServiceURL((String) interfaces.next()),
							new Hashtable(svcDescr.getProperties()));
					log(LogService.LOG_DEBUG,
							"Following service is published: " + svcDescr);
				} catch (ServiceLocationException e) {
					e.printStackTrace();
					log(LogService.LOG_ERROR, "failed registering service", e);
				}
			}
		} else {
			log(LogService.LOG_WARNING, "no Advertiser");
		}
		// inform the listener about the new available service
		notifyListenersOnNewServiceDescription(svcDescr);
		return svcDescr;
	}

	/**
	 * @see org.osgi.service.discovery.Discovery#unpublish(org.osgi.service.discovery.ServiceEndpointDescription)
	 */
	protected void unpublishService(
			final ServiceEndpointDescription serviceDescription) {
		validateServiceDescription(serviceDescription);
		log(LogService.LOG_DEBUG, "unpublish service "
				+ serviceDescription.toString());
		if (serviceDescription instanceof SLPServiceDescriptionAdapter) {
			SLPServiceDescriptionAdapter slpSvcDescr = (SLPServiceDescriptionAdapter) serviceDescription;
			Advertiser advertiser = getAdvertiser();
			if (advertiser != null) {
				Iterator interfaceNames = slpSvcDescr.getProvidedInterfaces()
						.iterator();
				while (interfaceNames.hasNext()) {
					String interfaceName = (String) interfaceNames.next();
					try {

						advertiser.deregister(slpSvcDescr
								.getServiceURL(interfaceName));
						log(LogService.LOG_DEBUG, "service "
								+ slpSvcDescr.getServiceURL(interfaceName)
								+ " unpublished");
						// inform listeners about removal
						notifyListenersOnRemovedServiceDescription(serviceDescription);
					} catch (ServiceLocationException e) {
						e.printStackTrace();
						log(LogService.LOG_ERROR,
								"failed to deregister service for interface "
										+ interfaceName, e);
					}
				}
			} else {
				log(LogService.LOG_WARNING, "no Advertiser");
			}
		}
	}

	/**
	 * Private tracker class to track the locator services.
	 * 
	 * @author kt32483
	 * 
	 */
	private class LocatorServiceTracker implements ServiceTrackerCustomizer {
		BundleContext context = null;

		public LocatorServiceTracker(BundleContext bc) {
			context = bc;
		}

		public Object addingService(ServiceReference reference) {
			Locator loc = (Locator) context.getService(reference);
			setLocator(loc);
			log(LogService.LOG_INFO, "bound Locator");
			return loc;
		}

		public void modifiedService(ServiceReference reference, Object service) {
			Locator loc = (Locator) context.getService(reference);
			setLocator(loc);
			log(LogService.LOG_INFO, "rebound Locator");
		}

		public void removedService(ServiceReference reference, Object service) {
			context.ungetService(reference);
			setLocator(null);
			log(LogService.LOG_INFO, "unbound Locator");
		}
	}

	/**
	 * 
	 */
	private class AdvertiserServiceTracker implements ServiceTrackerCustomizer {
		BundleContext context = null;

		public AdvertiserServiceTracker(BundleContext bc) {
			context = bc;
		}

		public Object addingService(final ServiceReference reference) {
			Advertiser adv = (Advertiser) context.getService(reference);
			setAdvertiser(adv);
			log(LogService.LOG_INFO, "bound Advertiser");
			return adv;
		}

		public void modifiedService(final ServiceReference reference,
				Object service) {
			Advertiser adv = (Advertiser) context.getService(reference);
			setAdvertiser(adv);
			log(LogService.LOG_INFO, "rebound Advertiser");
		}

		public void removedService(final ServiceReference reference,
				Object service) {
			context.ungetService(reference);
			setAdvertiser(null);
			log(LogService.LOG_INFO, "unbound Advertiser");
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
	 * @param logService
	 *            the reference to the LogService which get called for logging
	 * 
	 */
	public void setLogService(final LogService logService) {
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
		return discoTrackerCustomizer.getDsTrackers();
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
		Collection interfaceFilter = (Collection) trackerProperties
				.get(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_INTERFACES);
		Collection filter = (Collection) trackerProperties
				.get(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_FILTERS);
		boolean notify = false;
		if (interfaceFilter == null && filter == null) {
			notify = true;
		} else {
			if (interfaceFilter != null && !interfaceFilter.isEmpty()) {
				// check whether tracker's interface-list contains one of SED's
				// interfaces
				Iterator it = interfaceFilter.iterator();
				while (it.hasNext()) {
					Collection interfaces = svcDescr.getProvidedInterfaces();
					if (interfaces == null) {
						log(LogService.LOG_ERROR, "no interfaces provided by " + svcDescr);
						break;
					}
					if (interfaces.contains((String) it.next())) {
						notify = true;
					}
				}
			}

			if (filter != null && !filter.isEmpty()) {
				// check whether one filter of tracker's filter-list matches to
				// SED's properties
				Iterator it = filter.iterator();
				while (it.hasNext()) {
					String currentFilter = (String) it.next();
					try {
						Filter f = getContext().createFilter(currentFilter);
						if (f.match(new Hashtable(svcDescr.getProperties()))) {
							notify = true;
						}
					} catch (InvalidSyntaxException e) {
						e.printStackTrace();
						String errMsg = "A filter provided by a DiscoveredServiceTracker is invalid.";
						errMsg += " Filter = " + currentFilter;
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
					e.printStackTrace();
					log(LogService.LOG_ERROR,
							"Failed to execute async findService", e);
				}
			}
		});
		executor.start();
	}

	/**
	 * @return the inMemoryCache
	 */
	public List getInMemoryCache() {
		return inMemoryCache;
	}

}
