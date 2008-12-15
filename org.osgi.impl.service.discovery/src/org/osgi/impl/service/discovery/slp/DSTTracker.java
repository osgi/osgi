/**
 * 
 */
package org.osgi.impl.service.discovery.slp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.DiscoveredServiceNotification;
import org.osgi.service.discovery.DiscoveredServiceTracker;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * This class monitors the lifecycle of DiscoveredServicetrackers. They will be
 * notified on registration and modification if any service has been published.
 * 
 * @author kt32483
 * 
 */
public class DSTTracker implements ServiceTrackerCustomizer {

	private SLPHandlerImpl discovery = null;

	private Map dsTrackers = Collections.synchronizedMap(new HashMap());

	private BundleContext context = null;

	public DSTTracker(final BundleContext ctx, final SLPHandlerImpl disco) {
		context = ctx;
		discovery = disco;
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	public Object addingService(final ServiceReference arg0) {
		DiscoveredServiceTracker tracker = (DiscoveredServiceTracker) context
				.getService(arg0);
		if (!dsTrackers.keySet().contains(tracker)) {
			addTracker(arg0);
		}
		notifyOnAvailableSEDs(arg0);
		return tracker;
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void modifiedService(final ServiceReference arg0, final Object arg1) {
		DiscoveredServiceTracker tracker = (DiscoveredServiceTracker) context
				.getService(arg0);
		if (dsTrackers.keySet().contains(tracker)) {
			dsTrackers.remove(tracker);
		}
		addTracker(arg0);
		notifyOnAvailableSEDs(arg0);
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void removedService(final ServiceReference arg0, final Object arg1) {
		DiscoveredServiceTracker tracker = (DiscoveredServiceTracker) context
				.getService(arg0);
		if (dsTrackers.keySet().contains(tracker)) {
			dsTrackers.remove(tracker);
		}
	}

	/**
	 * This method informs a just registered or modified service tracker if a
	 * service matches its properties.
	 * 
	 * @param tracker
	 *            the just registered or modified DiscoveredServiceTracker
	 */
	private void notifyOnAvailableSEDs(final ServiceReference tracker) {
		synchronized (discovery.getInMemoryCache()) {
			if (discovery.getInMemoryCache() != null
					&& discovery.getInMemoryCache().isEmpty()) {
				Iterator it = discovery.getInMemoryCache().iterator();
				while (it.hasNext()) {
					ServiceEndpointDescription svcDescr = (ServiceEndpointDescription) it
							.next();
					DiscoveredServiceTracker dsTracker = (DiscoveredServiceTracker) context
							.getService(tracker);
					if (discovery.checkMatch(svcDescr, (Map) dsTrackers
							.get(dsTracker))) {
						DiscoveredServiceNotificationImpl impl = new DiscoveredServiceNotificationImpl(
								svcDescr,
								DiscoveredServiceNotification.AVAILABLE);
						dsTracker.serviceChanged(impl);
					}
				}
			}
		}
	}

	/**
	 * Adds a DiscoveredServiceTracker with its properties to our map.
	 * 
	 * @param ref
	 *            reference to the just registered or modified
	 *            DiscoveredServiceTracker
	 */
	private void addTracker(final ServiceReference ref) {
		Map props = new HashMap();
		String[] keys = ref.getPropertyKeys();
		for (int k = 0; (keys != null) && (k < keys.length); k++) {
			if (ref.getProperty(keys[k]) instanceof Collection) {
				props.put(keys[k], (Collection) ref.getProperty(keys[k]));
			} else {
				props.put(keys[k], ref.getProperty(keys[k]));
			}
		}
		dsTrackers.put((DiscoveredServiceTracker) context.getService(ref),
				props);
	}

	/**
	 * @return the dsTrackers
	 */
	public Map getDsTrackers() {
		return dsTrackers;
	}
}
