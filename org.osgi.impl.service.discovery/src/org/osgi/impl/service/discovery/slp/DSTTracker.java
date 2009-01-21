/**
 * 
 */
package org.osgi.impl.service.discovery.slp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.DiscoveredServiceNotification;
import org.osgi.service.discovery.DiscoveredServiceTracker;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * This class monitors the lifecycle of DiscoveredServiceTrackers. They will be
 * notified on registration and modification if any service has been published.
 * 
 * @author kt32483 TODO: replace arg0 arg names with more descriptive ones
 */
public class DSTTracker implements ServiceTrackerCustomizer {

	private SLPHandlerImpl	discovery										= null;

	// Map of DiscoveredServiceTracker, property map
	private Map			/* <DiscoveredServiceTracker, Map> */dsTrackers	= null;

	private BundleContext	context											= null;

	/**
	 * 
	 * @param ctx
	 * @param disco
	 */
	public DSTTracker(final BundleContext ctx, final SLPHandlerImpl disco) {
		context = ctx;
		discovery = disco;
		dsTrackers = Collections.synchronizedMap(new HashMap());
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	public Object addingService(final ServiceReference arg0) {
		DiscoveredServiceTracker tracker = (DiscoveredServiceTracker) context
				.getService(arg0);
		if (!dsTrackers.keySet().contains(tracker)) {
			addTracker(arg0);
			Map changedFilterCriteria = determineChangedFilterProperties(
					tracker, arg0);
			notifyOnAvailableSEDs(tracker, changedFilterCriteria);
			return tracker;
		}
		return null;
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void modifiedService(final ServiceReference arg0, final Object arg1) {
		DiscoveredServiceTracker tracker = (DiscoveredServiceTracker) context
				.getService(arg0);
		Map changedFilterCriteria = determineChangedFilterProperties(tracker,
				arg0);
		removeTracker(tracker);
		addTracker(arg0);
		notifyOnAvailableSEDs(tracker, changedFilterCriteria);
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void removedService(final ServiceReference arg0, final Object arg1) {
		DiscoveredServiceTracker tracker = (DiscoveredServiceTracker) context
				.getService(arg0);
		removeTracker(tracker);
	}

	/**
	 * This method informs a just registered or modified service tracker if a
	 * service matches its properties.
	 * 
	 * @param tracker the just registered or modified DiscoveredServiceTracker
	 */
	private void notifyOnAvailableSEDs(final DiscoveredServiceTracker tracker,
			final Map matchingCriteria) {
		List cachedServices = discovery.getCachedServices();
		if (cachedServices != null) {
			synchronized (cachedServices) {
				Iterator it = cachedServices.iterator();
				while (it.hasNext()) {
					ServiceEndpointDescription svcDescr = (ServiceEndpointDescription) it
							.next();
					if (SLPHandlerImpl.isTrackerInterestedInSED(svcDescr,
							matchingCriteria)) {
						tracker
								.serviceChanged(new DiscoveredServiceNotificationImpl(
										svcDescr,
										DiscoveredServiceNotification.AVAILABLE));
					}
				}
			}
		}
	}
	
	/**
	 * This method fills a map with key value pairs, where the key is
	 * {@link#DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_INTERFACES} or
	 * {@linkDiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_FILTERS} and the
	 * value is the list of new entries or empty if nothing is new.
	 * 
	 * @param tracker the registered or modified tracker registration
	 * @param serviceReference
	 * @return a map that contains two entries where the value contains the
	 *         added properties. It returns empty values if no new properties
	 *         have been found. It returns null values if the new properties are null or empty
	 */
	private Map determineChangedFilterProperties(
			DiscoveredServiceTracker tracker, ServiceReference serviceReference) {
		Map result = new HashMap();

		Map props = (Map) dsTrackers.get(tracker);
		if (props != null) {
			Collection oldInterfaceCriteria = (Collection) props
					.get(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_INTERFACES);
			Collection oldFilterCriteria = (Collection) props
					.get(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_FILTERS);

			Collection newInterfaceCriteria = (Collection) serviceReference
					.getProperty(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_INTERFACES);
			Collection newFilterCriteria = (Collection) serviceReference
					.getProperty(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_FILTERS);

			result
					.put(
							DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_INTERFACES,
							getAddedEntries(oldInterfaceCriteria,
									newInterfaceCriteria));
			result.put(
					DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_FILTERS,
					getAddedEntries(oldFilterCriteria, newFilterCriteria));
		}
		else {
			// set empty lists as values
			result
					.put(
							DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_INTERFACES,
							new ArrayList());
			result.put(
					DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_FILTERS,
					new ArrayList());
		}
		return result;
	}

	/**
	 * Compares to list and returns only those who are not in the old list. Or
	 * it returns an empty list if nothing has been found.
	 * 
	 * @param oldList the existing set of properties
	 * @param newList the new set of properties
	 * @return a list of only new properties; empty list if no new has been
	 *         found; null if newList is null or empty
	 */
	private Collection getAddedEntries(Collection oldList, Collection newList) {
		ArrayList result = new ArrayList();
		if (newList == null || newList.isEmpty()) {
			return null;
		}
		if (oldList == null) {
			return newList;
		}
		Iterator it = newList.iterator();
		while (it.hasNext()) {
			Object val = it.next();
			if (!oldList.contains(val)) {
				result.add(val);
			}
		}
		return result;
	}

	/**
	 * Adds a DiscoveredServiceTracker with its properties to our map.
	 * 
	 * @param ref reference to the just registered or modified
	 *        DiscoveredServiceTracker
	 */
	private void addTracker(final ServiceReference ref) {
		// Retrieve current service properties (required later when modified to
		// compute the actual modification)
		Map props = new HashMap();
		props
				.put(
						DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_INTERFACES,
						ref
								.getProperty(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_INTERFACES));

		props
				.put(
						DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_FILTERS,
						ref
								.getProperty(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_FILTERS));
		dsTrackers.put((DiscoveredServiceTracker) context.getService(ref),
				props);
	}

	/**
	 * @param tracker
	 */
	private void removeTracker(DiscoveredServiceTracker tracker) {
		if (dsTrackers.keySet().contains(tracker)) {
			dsTrackers.remove(tracker);
		}
	}

	/**
	 * @return the dsTrackers
	 */
	public Map getDsTrackers() {
		return dsTrackers;
	}
}
