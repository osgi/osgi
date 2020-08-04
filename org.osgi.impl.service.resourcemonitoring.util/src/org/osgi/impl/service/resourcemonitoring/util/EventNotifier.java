
package org.osgi.impl.service.resourcemonitoring.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceEvent;
import org.osgi.service.resourcemonitoring.ResourceListener;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * This class is used to send ResourceEvents to interested-in ResourceListeners.
 * 
 * @author mpcy8647
 * 
 */
public class EventNotifier implements
		ServiceTrackerCustomizer<ResourceListener<Long>,ResourceListener<Long>> {

	/**
	 * context of the bundle.
	 */
	private final BundleContext		bundleContext;

	/**
	 * Tracks {@link ResourceListener} services.
	 */
	private final ServiceTracker<ResourceListener<Long>,ResourceListener<Long>>	serviceTracker;

	/**
	 * Used to filter {@link ResourceListener}
	 */
	private final String			resourceType;

	/**
	 * Used to filter {@link ResourceListener}
	 */
	private final ResourceContext	resourceContext;

	/**
	 * Each {@link ResourceListener} of this list will receive a
	 * {@link ResourceEvent} for each call to
	 */
	private final List<ResourceListener<Long>>									resourceListeners;

	/**
	 * Map of <ResourceListener, ResourceEvent> Keep track of the last sent
	 * notifications for each {@link ResourceListener}.
	 */
	private final Map<ResourceListener<Long>,ResourceEvent<Long>>				lastNotifications;

	/**
	 * Create a new EventNotifier.
	 * 
	 * @param pResourceType type of resource. Used to filter to which
	 *        {@link ResourceListener} this EventNotifier will send
	 *        {@link ResourceEvent}
	 * @param pResourceContext resourceContext associated to this notifier
	 * @param pBundleContext bundle context
	 */
	public EventNotifier(final String pResourceType,
			final ResourceContext pResourceContext,
			final BundleContext pBundleContext) {
		bundleContext = pBundleContext;
		resourceType = pResourceType;
		resourceContext = pResourceContext;

		resourceListeners = new ArrayList<>();
		lastNotifications = new Hashtable<>();

		// set filter for ServiceTracker
		Filter serviceTrackerFilter = null;
		try {
			serviceTrackerFilter = bundleContext.createFilter("(&("
					+ Constants.OBJECTCLASS + "="
					+ ResourceListener.class.getName() + ")("
					+ ResourceListener.RESOURCE_CONTEXT + "="
					+ resourceContext.getName() + ")("
					+ ResourceListener.RESOURCE_TYPE + "=" + resourceType
					+ "))");
		} catch (InvalidSyntaxException e) {
			// SHOULD not OCCURRED
			e.printStackTrace();
		}

		// create ServiceTracker tracking ResourceListener
		serviceTracker = new ServiceTracker<>(bundleContext,
				serviceTrackerFilter, this);
	}

	/**
	 * Start the event notifier. Must be call prior to send any
	 * {@link ResourceEvent}.
	 */
	public void start() {
		serviceTracker.open();
	}

	/**
	 * Stop the event notifier.
	 */
	public void stop() {
		serviceTracker.close();
	}

	/**
	 * Notify all ResourceListeners.
	 * 
	 * @param value new resource usage value
	 */
	public void notify(final Long value) {
		List<ResourceListener<Long>> currentResourceListeners = getResourceListeners();

		// iterate over the list of ResourceListeners related to this
		// EventNotifier
		for (Iterator<ResourceListener<Long>> it = currentResourceListeners
				.iterator(); it.hasNext();) {
			ResourceListener<Long> listener = it.next();
			ResourceEvent<Long> event = createResourceEvent(listener, value,
					resourceContext);

			ResourceEvent<Long> lastNotification = lastNotifications
					.get(listener);

			if ((lastNotification != null)
					&& ((lastNotification.getType() == ResourceEvent.ERROR) || (lastNotification
							.getType() == ResourceEvent.WARNING))) {
				// in the case where this listener has previously received a
				// notification
				// and this notification is either of type ERROR or WARNING

				// if the current event is null => then generate a NORMAL event
				// type
				if (event == null) {
					event = new ResourceEvent<>(ResourceEvent.NORMAL,
							resourceContext,
							lastNotification.isUpperThreshold(), value);
				} else {
					// the current event is either a WARNING event or an ERROR
					// event
					// check if the last notification type
					// if it is the same type, do not send this event
					if (event.getType() == lastNotification.getType()) {
						event = null;
					}
				}

			}

			if (event != null) {
				try {
					listener.notify(event);

					// add to lastnotifications
					lastNotifications.put(listener, event);
				} catch (Exception e) {
					// catch all Exceptions to be sure all listeners will
					// receive this event
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * this method is called when a compatible ResourceListener is available on
	 * the framework. This ResourceListener is added into the resourceListeners
	 * list.
	 * 
	 * @param reference reference of the new available {@link ResourceListener}
	 * @return listener service object.
	 */
	@Override
	public ResourceListener<Long> addingService(
			ServiceReference<ResourceListener<Long>> reference) {
		System.out.println("add a ResourceListener");

		ResourceListener<Long> listener = bundleContext.getService(reference);

		addResourceListener(listener);

		return listener;
	}

	/**
	 * Nothing to do.
	 */
	@Override
	public void modifiedService(
			ServiceReference<ResourceListener<Long>> reference,
			ResourceListener<Long> service) {
		// TODO Auto-generated method stub

	}

	/**
	 * This method is called when a tracked {@link ResourceListener} is no
	 * longer available.
	 * 
	 * @param reference reference
	 * @param service service
	 */
	@Override
	public void removedService(
			ServiceReference<ResourceListener<Long>> reference,
			ResourceListener<Long> service) {
		ResourceListener<Long> listener = service;
		removeResourceListener(listener);
	}

	/**
	 * The {@link ResourceMonitor} must call this method when it is enabled or
	 * disabled. This method clears the lastNotification map.
	 */
	public void reportEnableDisable() {
		// when the monitor is enabled or disabled,
		// delete last notifications
		lastNotifications.clear();
	}

	/**
	 * Add a new tracked ResourceListener. Thread safe.
	 * 
	 * @param resourceListener resource listener to be added
	 */
	private void addResourceListener(ResourceListener<Long> resourceListener) {
		synchronized (resourceListeners) {
			resourceListeners.add(resourceListener);
		}
	}

	/**
	 * Remove a ResourceListener from the list. Thread-safe
	 * 
	 * @param resourceListener resourceListener to be removed
	 */
	private void removeResourceListener(
			ResourceListener<Long> resourceListener) {
		synchronized (resourceListeners) {
			resourceListeners.remove(resourceListener);
		}
	}

	/**
	 * Get a list of current tracked ResourceListeners. Thread-safe.
	 * 
	 * @return a duplicated list of tracked ResourceListeners.
	 */
	private List<ResourceListener<Long>> getResourceListeners() {
		List<ResourceListener<Long>> duplicated = new ArrayList<>();

		synchronized (resourceListeners) {
			duplicated.addAll(resourceListeners);
		}

		return duplicated;
	}

	/**
	 * Create a ResourceEvent by checking if one of the thresholds of listener
	 * is violated.
	 * 
	 * @return event or null if this listener MUST not be notified
	 */
	private static ResourceEvent<Long> createResourceEvent(
			ResourceListener<Long> listener, Long value,
			ResourceContext resourceContext) {

		ResourceEvent<Long> event = null;

		Comparable<Long> upperErrorThreshold = listener
				.getUpperErrorThreshold();
		if ((upperErrorThreshold != null)
				&& (compare(upperErrorThreshold, value, -1) || compare(
						upperErrorThreshold, value, 0))) {
			event = new ResourceEvent<>(ResourceEvent.ERROR, resourceContext,
					true, value);
			return event;
		}

		Comparable<Long> upperWarningThreshold = listener
				.getUpperWarningThreshold();
		if ((upperWarningThreshold != null)
				&& (compare(upperWarningThreshold, value, -1) || compare(
						upperWarningThreshold, value, 0))) {
			event = new ResourceEvent<>(ResourceEvent.WARNING, resourceContext,
					true, value);
			return event;
		}

		Comparable<Long> lowerErrorThreshold = listener
				.getLowerErrorThreshold();
		if ((lowerErrorThreshold != null)
				&& (compare(lowerErrorThreshold, value, 1) || compare(
						lowerErrorThreshold, value, 0))) {
			event = new ResourceEvent<>(ResourceEvent.ERROR, resourceContext,
					false, value);
			return event;
		}

		Comparable<Long> lowerWarningThreshold = listener
				.getLowerWarningThreshold();
		if ((lowerWarningThreshold != null)
				&& (compare(lowerWarningThreshold, value, 1) || compare(
						lowerWarningThreshold, value, 0))) {
			event = new ResourceEvent<>(ResourceEvent.WARNING, resourceContext,
					false, value);
			return event;
		}

		return null;
	}

	/**
	 * Compare value with the comparable object.
	 * 
	 * @param comparable comparable
	 * @param value value to be compared with comparable
	 * @param expected expected result of the comparison
	 * @return true if the result of the comparison between comparable and value
	 *         is equal to expected
	 */
	private static boolean compare(Comparable<Long> comparable, Long value,
			int expected) {

		try {
			int compare = comparable.compareTo(value);
			if (((compare < 0) && (expected < 0))
					|| ((compare == 0) && (expected == 0))
					|| ((compare > 0) && (expected > 0))) {
				return true;
			}
		} catch (Exception e) {
			// catch all Exceptions
			// these Exceptions are not relevant
		}

		return false;
	}

}
