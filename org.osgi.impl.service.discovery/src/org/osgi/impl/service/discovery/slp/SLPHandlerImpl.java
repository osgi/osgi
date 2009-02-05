/* 
 * Copyright (c) 2008, 2009 Siemens Enterprise Communications GmbH & Co. KG, 
 * Germany. All rights reserved.
 *
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Siemens Enterprise Communications 
 * GmbH & Co. KG and its licensors. All rights are reserved.
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
import java.util.HashMap;
import java.util.HashSet;
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
 * This class is a Distributed OSGi Discovery Service implementation based on
 * SLP using jSLP.
 * 
 * TODO: remove printStackTrace and do logging instead
 * 
 * @author Phillip Konradi
 * @author Thomas Kiesslich
 */
public class SLPHandlerImpl implements Discovery {
	/**
	 * ServiceRegistration property identifying Discovery's default strategy for
	 * distribution of published service information. It's up to the Discovery
	 * service to provide and support this property. Value of this property is
	 * of type String.
	 * 
	 * TODO do we support this property?
	 */
	public static final String		PROP_KEY_PUBLISH_STRATEGY							= "osgi.discovery.strategy.publication";

	/**
	 * Constant for a "push" publication strategy: published service information
	 * is actively pushed to the network for discovery.
	 */
	public static final String		PROP_VAL_PUBLISH_STRATEGY_PUSH						= "push";

	/**
	 * Constant for a "pull" publication strategy: published service information
	 * is available just upon lookup requests.
	 */
	public static final String		PROP_VAL_PUBLISH_STRATEGY_PULL						= "pull";

	private ServiceTracker			locatorTracker										= null;
	private ServiceTracker			advertiserTracker									= null;
	private ServiceTracker			spTracker											= null;

	private DSTTracker				discoTrackerCustomizer								= null;
	private ServiceTracker			discoTracker										= null;

	private Locator					locator												= null;
	private Advertiser				advertiser											= null;

	private final int				POLLDELAY											= 10000;									// 10
	// sec

	private Timer					t													= null;

	// private static final boolean DEFAULT_AUTOPUBLISH = true;

	private static BundleContext	context;

	private static LogService		logService;
	// private boolean autoPublish = DEFAULT_AUTOPUBLISH;

	private static Collection		/* <SLPServiceDescriptionAdapter> */inMemoryCache	= Collections
																								.synchronizedSet(new HashSet());

	/**
	 * Constructor.
	 * 
	 * @param context the BundleContext of the containing bundle.
	 * @param logService a LogService instance
	 */
	public SLPHandlerImpl(final BundleContext context, final LogService logger) {
		logService = logger;
		SLPHandlerImpl.context = context; // TODO: making it static var is not
		// good I'd say.
		locatorTracker = new ServiceTracker(context, Locator.class.getName(),
				new LocatorServiceTracker(context));
		advertiserTracker = new ServiceTracker(context, Advertiser.class
				.getName(), new AdvertiserServiceTracker(context));

	}

	/**
	 * Initialization method called by Activator.
	 */
	public void init() {
		log(LogService.LOG_DEBUG, "init");
		// autoPublish = System
		// .getProperty(SLPHandlerImpl.PROP_KEY_PUBLISH_STRATEGY,
		// SLPHandlerImpl.PROP_VAL_PUBLISH_STRATEGY_PUSH)
		// .equalsIgnoreCase(SLPHandlerImpl.PROP_VAL_PUBLISH_STRATEGY_PUSH);
		locatorTracker.open();
		advertiserTracker.open();
		discoTrackerCustomizer = new DSTTracker(context);
		discoTracker = new ServiceTracker(context,
				DiscoveredServiceTracker.class.getName(),
				discoTrackerCustomizer);
		discoTracker.open();
		spTracker = new ServiceTracker(context, ServicePublication.class
				.getName(), new ServicePublicationTracker(context, this));
		spTracker.open();
		t = new Timer(false);
		t.schedule(new InformListenerTask(this), 1, POLLDELAY);
	}

	/**
	 * Shutdown method called by Activator.
	 */
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

	private Locator getLocator() {
		return locator;
	}

	private synchronized void setLocator(final Locator locator) {
		this.locator = locator;
	}

	private Advertiser getAdvertiser() {
		return advertiser;
	}

	private synchronized void setAdvertiser(final Advertiser advertiser) {
		this.advertiser = advertiser;
	}

	/**
	 * This method looks up a service given by its interface name and filter. It
	 * does a full lookup and stores the result in the in memory cache.
	 * 
	 * @param interfaceName the interface name of the service to find
	 * @param filter a LDAP filter expression that defines the required services
	 *        as well
	 * @return a collection of SErviceEndPointDescription objects, an empty
	 *         collection if nothing has been found
	 * 
	 */
	// TODO make it thread safe
	public Collection/* <ServiceEndpointDescription> */findService(
			final String interfaceName, final String filter) {
		getFilterFromString(filter);

		// check whether SLP-Locator service exists
		Locator locator = getLocator();
		if (locator == null) {
			log(LogService.LOG_WARNING,
					"No SLP-Locator. Find operation is not executed.");
			return Collections.EMPTY_LIST;
		}

		// TODO first look at cache

		// find appropriate services
		ServiceLocationEnumeration se;
		try {
			ServiceURL svcURL = SLPServiceEndpointDescription.createServiceURL(
					interfaceName, null, null, null);
			log(LogService.LOG_DEBUG, "try to find services with URL="
					+ svcURL.toString());
			se = locator.findServices(svcURL.getServiceType(), null, filter);
			// TODO: in case result is empty do a search implying that service
			// interface is not java???
		}
		catch (Exception e) {
			log(LogService.LOG_ERROR, "Failed to find service", e);
			return new ArrayList();
		}

		Collection result = new ArrayList();
		// iterate over the found services and retrieve their attributes
		while (se.hasMoreElements()) {
			try {
				ServiceURL url = (ServiceURL) se.next();
				log(LogService.LOG_DEBUG, "try to find attributes for " + url);
				ServiceLocationEnumeration a = locator.findAttributes(url,
						null, null);
				// TODO: check for failed call
				// TODO check returning match if mandatory properties are set,
				// if not log and ignore that service
				SLPServiceEndpointDescription descriptionAdapter = new SLPServiceEndpointDescription(
						url);
				while (a.hasMoreElements()) {
					// TODO: introduce a separator util
					String attributes = (String) a.next();
					String key = null;
					Object value = null;
					attributes = attributes.substring(1,
							attributes.length() - 1);
					key = attributes.substring(0, attributes.indexOf("="));
					// if the value is not a String we cannot handle that value!
					// This is a limitation of the jSLP API.
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
				log(LogService.LOG_DEBUG,
						"findService: adding service endpoint description to result list: "
								+ descriptionAdapter);
				if (descriptionAdapter.getProvidedInterfaces() != null) {
					result.add(descriptionAdapter);
				}
				else {
					log(LogService.LOG_ERROR, "no interfaces provided by "
							+ descriptionAdapter);
				}
			}
			catch (Exception e) {
				log(LogService.LOG_ERROR, "Failed to find service", e);
			}
		}
		// log result
		if (!result.isEmpty()) {
			// TODO move creation of string to log into extra method?
			StringBuffer buff = new StringBuffer();
			buff.append("number of services = ");
			buff.append(result.size());
			buff.append("; services = ");
			Iterator it = result.iterator();
			while (it.hasNext()) {
				buff.append("(");
				ServiceEndpointDescription sed = (ServiceEndpointDescription) it
						.next();
				buff.append(sed);
				buff.append(")");
				if (it.hasNext()) {
					buff.append(",(");
				}
			}
			log(LogService.LOG_DEBUG, buff.toString());
		}
		else {
			log(LogService.LOG_DEBUG, "0 services found");
		}
		// it was a full search
		if (interfaceName == null && filter == null) {
			// atomic compound operation
			synchronized (inMemoryCache) {
				inMemoryCache.clear(); // this works only because this method
				// will
				// be called to find ALL services in the network.
				inMemoryCache.addAll(result);
			}
		}
		else {
			// add only just found entries
			inMemoryCache.addAll(result);
		}

		return result;
	}

	/**
	 * Publishes a service.
	 * 
	 * TODO: publish also for every endpoint interface TODO: use
	 * endpointLocation as service URL if given
	 * 
	 * @param javaInterfaces collection of java interface names
	 * @param javaInterfacesAndVersions collection of versions, where the order
	 *        of the version must match the order of the java interfaces
	 * @param javaInterfacesAndEndpointInterfaces optional collection of
	 *        endpointinterface names, where the order must match the order of
	 *        the java interfaces
	 * @param properties map of properties; keys must be Strings, values are of
	 *        type object
	 * @param strategy optional string that defines the publish strategy
	 * @return a ServiceEndpointDescription or null, if an error occurred during
	 *         creation of the ServiceDescription
	 */
	protected ServiceEndpointDescription publishService(
			Collection/* <String> */javaInterfaces,
			Collection/* <String> */javaInterfacesAndVersions,
			Collection/* <String> */javaInterfacesAndEndpointInterfaces,
			Map/* <String, Object> */properties, String strategy,
			String endpointID) {
		// TODO: act according strategy parameter
		Advertiser advertiser = getAdvertiser();
		if (advertiser != null) {
			SLPServiceEndpointDescription svcDescr;
			try {
				svcDescr = new SLPServiceEndpointDescription(javaInterfaces,
						javaInterfacesAndVersions,
						javaInterfacesAndEndpointInterfaces, properties,
						endpointID);
			}
			catch (ServiceLocationException e1) {
				e1.printStackTrace();
				log(LogService.LOG_ERROR,
						"Unable to create Service Description", e1);
				return null;
			}

			// Publish each interface of the service separately
			Iterator interfaces = svcDescr.getProvidedInterfaces().iterator();
			while (interfaces.hasNext()) {
				try {
					advertiser.register(svcDescr
							.getServiceURL((String) interfaces.next()),
							new Hashtable(svcDescr.getProperties()));
					log(LogService.LOG_DEBUG,
							"Following service is published: " + svcDescr);
				}
				catch (ServiceLocationException e) {
					e.printStackTrace();
					log(LogService.LOG_ERROR, "failed registering service", e);
				}
			}

			// add it to the available Services
			inMemoryCache.add(svcDescr);
			// inform the listener about the new available service
			notifyListenersOnNewServiceDescription(svcDescr);
			return svcDescr;
		}
		else {
			log(LogService.LOG_WARNING, "no Advertiser");
		}
		return null;
	}

	/**
	 * Unpublishes a given service description.
	 * 
	 * @param serviceDescription the service to unpublish
	 * @throws IllegalArgumentException if serviceDescription is null or does
	 *         not contain at least one java interface
	 */
	protected void unpublishService(
			final ServiceEndpointDescription serviceDescription) {
		validateServiceDescription(serviceDescription);
		log(LogService.LOG_DEBUG, "unpublish service "
				+ serviceDescription.toString());

		// TODO How about using SLPServiceDescriptionAdapter instead of
		// ServiceEndpointDescription classes, since it's anyway a SLPDiscovery?
		if (serviceDescription instanceof SLPServiceEndpointDescription) {
			SLPServiceEndpointDescription slpSvcDescr = (SLPServiceEndpointDescription) serviceDescription;
			// remove it from in memory cache
			inMemoryCache.remove(slpSvcDescr);
			// unregister it via SLP
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
					}
					catch (ServiceLocationException e) {
						e.printStackTrace();
						log(LogService.LOG_ERROR,
								"failed to deregister service for interface "
										+ interfaceName, e);
					}
				}
			}
			else {
				log(LogService.LOG_WARNING, "no Advertiser");
			}
		}
	}

	/**
	 * 
	 */
	protected static synchronized void log(int logLevel, String msg) {
		if (logService != null) {
			logService.log(logLevel, msg);
		}
	}

	/**
	 * 
	 */
	protected static synchronized void log(int logLevel, String msg, Exception e) {
		if (logService != null) {
			logService.log(logLevel, msg, e);
		}
	}

	/**
	 * @param logService the reference to the LogService which get called for
	 *        logging
	 * 
	 */
	public static void setLogService(final LogService logger) {
		logService = logger;
	}

	/**
	 * This method create a Filter object from a given LDPA filter string via
	 * the {@link BundleContext#createFilter(String)}. This method may also be
	 * used to validate the correctness of a filter string.
	 * 
	 * @param filter a String that represents an LDAP filter
	 * @return the created Filter object, or null if filter is null
	 * @throws IllegalArgumentException if a Filter object could not be created
	 *         with the given String
	 */
	protected Filter getFilterFromString(String filter)
			throws IllegalArgumentException {
		if (filter == null) {
			return null;
		}
		Filter f = null;
		try {
			f = context.createFilter(filter);
		}
		catch (InvalidSyntaxException e) {
			throw new IllegalArgumentException("filter [" + filter
					+ "] is not an LDAP filter");
		}
		if (f == null) {
			throw new IllegalArgumentException(
					"cannot create an LDAP filter with given filter [" + filter
							+ "] parameter");
		}
		return f;
	}

	/**
	 * This method checks if a given ServiceEndpointDescrioption follows the
	 * minimal requirements.
	 * 
	 * @param serviceDescription the given ServiceEndpointDescription
	 * @throws IllegalArgumentException if serviceDescription is null or does
	 *         not contain at least one java interface
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

	/**
	 * Returns a Map of all registered DiscoveredServiceTracker trackers.
	 * 
	 * TODO should it be a copy of the map for thread safety? This could lead to
	 * failures during usage of the copy. But we do not block the Tracker,
	 * registrations and deregistrations of DSTs.
	 * 
	 * @return a copied Map of all registered DiscoveredServiceTracker trackers.
	 */
	protected Map getRegisteredServiceTracker() {
		return new HashMap(discoTrackerCustomizer.getDsTrackers());
	}

	/**
	 * This method informs a just registered or modified service tracker if a
	 * service matches its properties.
	 * 
	 * TODO: add suppression of informing trackers twice for the same SED
	 * 
	 * @param tracker the just registered or modified DiscoveredServiceTracker
	 */
	public static void notifyOnAvailableSEDs(
			final DiscoveredServiceTracker tracker, final Map matchingCriteria) {
		List cachedServices = getCachedServices();
		System.out.println(cachedServices.size()
				+ " services are registered in Discovery.");
		if (cachedServices != null) {
			Iterator it = cachedServices.iterator();
			while (it.hasNext()) {
				ServiceEndpointDescription svcDescr = (ServiceEndpointDescription) it
						.next();
				if (isTrackerInterestedInSED(svcDescr, matchingCriteria)) {
					tracker
							.serviceChanged(new DiscoveredServiceNotificationImpl(
									svcDescr,
									DiscoveredServiceNotification.AVAILABLE));
				}
			}
		}
	}

	/**
	 * 
	 * @param svcDescr
	 */
	protected void notifyListenersOnNewServiceDescription(
			ServiceEndpointDescription svcDescr) {
		Map discoveredSTs = getRegisteredServiceTracker();
		Iterator it = discoveredSTs.keySet().iterator();
		while (it.hasNext()) {
			DiscoveredServiceTracker st = (DiscoveredServiceTracker) it.next();
			Map trackerProps = (Map) discoveredSTs.get(st);
			if (isTrackerInterestedInSED(svcDescr, trackerProps)) {
				try {
					st.serviceChanged(new DiscoveredServiceNotificationImpl(
							svcDescr, DiscoveredServiceNotification.AVAILABLE));
				}
				catch (Exception e) {
					log(
							LogService.LOG_ERROR,
							"Exceptions where thrown while notifying about a new remote service.",
							e);
				}

			}
		}
	}

	/**
	 * Notifies all DSTTrackers about an unpublished service.
	 * 
	 * @param svcDescr the unpublished ServiceEndpointDescription
	 */
	protected void notifyListenersOnRemovedServiceDescription(
			ServiceEndpointDescription svcDescr) {
		Map discoveredSTs = getRegisteredServiceTracker();
		Iterator it = discoveredSTs.keySet().iterator();
		while (it.hasNext()) {
			DiscoveredServiceTracker st = (DiscoveredServiceTracker) it.next();
			Map trackerProps = (Map) discoveredSTs.get(st);
			// inform it if the listener has no Filter set
			// or the filter matches the criteria
			if (isTrackerInterestedInSED(svcDescr, trackerProps)) {
				try {
					st
							.serviceChanged(new DiscoveredServiceNotificationImpl(
									svcDescr,
									DiscoveredServiceNotification.UNAVAILABLE));
				}
				catch (Exception e) {
					log(
							LogService.LOG_ERROR,
							"Exceptions where thrown while notifying about removal of a remote service.",
							e);
				}
			}
		}
	}

	/**
	 * 
	 * @param svcDescr
	 */
	protected void notifyListenersOnModifiedServiceDescription(
			ServiceEndpointDescription svcDescr) {
		Map discoveredSTs = getRegisteredServiceTracker();
		Iterator it = discoveredSTs.keySet().iterator();
		while (it.hasNext()) {
			DiscoveredServiceTracker st = (DiscoveredServiceTracker) it.next();
			Map trackerProps = (Map) discoveredSTs.get(st);
			// inform it if the listener has no Filter set
			// or the filter matches the criteria
			if (isTrackerInterestedInSED(svcDescr, trackerProps)) {
				try {
					st.serviceChanged(new DiscoveredServiceNotificationImpl(
							svcDescr, DiscoveredServiceNotification.MODIFIED));
				}
				catch (Exception e) {
					log(
							LogService.LOG_ERROR,
							"Exceptions where thrown while notifying about modification of a remote service.",
							e);
				}
			}
		}
	}

	/**
	 * Compares the properties of a registered DiscoveredServiceTracker with the
	 * SED properties. IF they match, it returns true.
	 * 
	 * @param svcDescr
	 * @param trackerProperties
	 * @return true if the service tracker properties match the SEDs properties,
	 *         else false
	 */
	public static boolean isTrackerInterestedInSED(
			ServiceEndpointDescription svcDescr,
			Map/* String, Object */trackerProperties) {
		Collection interfaceCriteria = (Collection) trackerProperties
				.get(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_INTERFACES);
		Collection filter = (Collection) trackerProperties
				.get(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_FILTERS);
		boolean notify = false;
		if (interfaceCriteria == null && filter == null) {
			notify = true;
		}
		else {
			// if interface-criteria are defined on tracker
			if (interfaceCriteria != null && !interfaceCriteria.isEmpty()) {
				// then check whether tracker's interface-list contains one of
				// SED's interfaces
				Collection svcInterfaces = svcDescr.getProvidedInterfaces();
				if (svcInterfaces == null) {
					throw new RuntimeException("no interfaces provided");
				}
				Collection intersectionResult = new HashSet(interfaceCriteria);
				intersectionResult.retainAll(svcInterfaces);
				if (intersectionResult.size() > 0) {
					notify = true;
				}
			}

			// if filter-criteria are defined on tracker
			if (filter != null && !filter.isEmpty()) {
				// check whether one filter of tracker's filter-list matches to
				// SED's properties
				Iterator it = filter.iterator();
				while (it.hasNext()) {
					String currentFilter = (String) it.next();
					try {
						Filter f = context.createFilter(currentFilter);
						if (f.match(new Hashtable(svcDescr.getProperties()))) {
							notify = true;
						}
					}
					catch (InvalidSyntaxException e) {
						e.printStackTrace();
						String errMsg = "A filter provided by a DiscoveredServiceTracker is invalid.";
						errMsg += " Filter = " + currentFilter;
						errMsg += "; DiscoveredServiceTracker service.id = "
								+ trackerProperties.get(Constants.SERVICE_ID);
						throw new RuntimeException(e.getMessage());
					}
				}
			}
		}
		return notify;
	}

	/**
	 * Returns the complete in MemoryCache.
	 * 
	 * @return a "shallow" copy of the inMemoryCache
	 */
	public static List getCachedServices() {
		return new ArrayList(inMemoryCache);
	}

	/**
	 * Private tracker class to track the jSLP locator services.
	 * 
	 * @author Thomas Kiesslich
	 * 
	 */
	private class LocatorServiceTracker implements ServiceTrackerCustomizer {
		BundleContext	context	= null;

		public LocatorServiceTracker(BundleContext bc) {
			context = bc;
		}

		public Object addingService(ServiceReference reference) {
			// TODO: use it only we haven't got a service yet
			if (getLocator() == null) {
				Locator loc = setLocatorService(reference);
				return loc;
			}
			return null;
		}

		/**
		 * @param reference
		 * @return
		 */
		private Locator setLocatorService(ServiceReference reference) {
			Locator loc = (Locator) context.getService(reference);
			setLocator(loc);
			log(LogService.LOG_INFO, "bound Locator");
			return loc;
		}

		public void modifiedService(ServiceReference reference, Object service) {
		}

		public void removedService(ServiceReference reference, Object service) {
			context.ungetService(reference);
			ServiceReference ref = context.getServiceReference(Locator.class
					.getName());
			if (ref != null) {
				setLocatorService(ref);
			}
			else {
				setLocator(null);
				log(LogService.LOG_INFO, "unbound Locator");
			}
		}
	}

	/**
	 * Private tracker class to track the jSLP advertiser services.
	 * 
	 * @author Thomas Kiesslich
	 */
	private class AdvertiserServiceTracker implements ServiceTrackerCustomizer {
		BundleContext	context	= null;

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
}
