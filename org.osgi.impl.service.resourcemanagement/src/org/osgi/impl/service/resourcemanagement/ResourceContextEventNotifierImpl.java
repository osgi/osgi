
package org.osgi.impl.service.resourcemanagement;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemanagement.ResourceContextEvent;
import org.osgi.service.resourcemanagement.ResourceContextListener;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 *
 */
public class ResourceContextEventNotifierImpl implements
		ResourceContextEventNotifier, ServiceTrackerCustomizer {

	private BundleContext	context;
	private ServiceTracker	serviceTracker;

	// private final Map<ResourceContextListener,
	// ServiceReference<ResourceContextListener>> listeners;
	private final Map		listeners;

	/**
	 * 
	 */
	public ResourceContextEventNotifierImpl() {
		listeners = new Hashtable();
	}

	public void start(BundleContext pcontext) {
		this.context = pcontext;
		serviceTracker = new ServiceTracker(this.context,
				ResourceContextListener.class.getName(), this);
		serviceTracker.open();
	}

	public void stop(BundleContext pcontext) {
		serviceTracker.close();
		serviceTracker = null;

		listeners.clear();
	}

	public void notify(ResourceContextEvent event) {
		synchronized (listeners) {
			for (Iterator it = listeners.keySet()
					.iterator(); it.hasNext();) {
				ResourceContextListener currentRcl = (ResourceContextListener) it
						.next();
				ServiceReference currentSr = (ServiceReference) listeners
						.get(currentRcl);

				int[] eventTypeFilter = getEventTypeFilter(currentSr);
				String[] resourceContextFilter = getResourceContextFilter(currentSr);

				if (checkEventTypeFilter(event.getType(), eventTypeFilter)
						&& checkResourceContextFilter(event.getContext()
								.getName(), resourceContextFilter)) {
					// send a notification to the current
					// ResourceContextListener
					try {
						currentRcl.notify(event);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			}
		}
	}

	public Object addingService(ServiceReference reference) {
		ResourceContextListener rcl = (ResourceContextListener) context.getService(reference);

		synchronized (listeners) {
			listeners.put(rcl, reference);
		}

		return rcl;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		synchronized (listeners) {
			listeners.put(service, reference);
		}
	}

	public void removedService(ServiceReference reference, Object service) {
		synchronized (listeners) {
			listeners.remove(service);
		}

	}

	private static String[] getResourceContextFilter(ServiceReference serviceReference) {
		String[] filter = null;
		Object propertyValue = serviceReference.getProperty(ResourceContextListener.RESOURCE_CONTEXT);
		try {
			filter = (String[]) propertyValue;
		} catch (ClassCastException e) {
			// should be a string
			filter = new String[] {(String) propertyValue};
		}

		return filter;
	}

	private static int[] getEventTypeFilter(ServiceReference serviceReference) {
		int[] filter = null;
		Object propertyValue = serviceReference.getProperty(ResourceContextListener.EVENT_TYPE);
		try {
			filter = (int[]) propertyValue;
		} catch (ClassCastException e) {
			// should be an int
			filter = new int[] {(Integer) propertyValue};
		}

		return filter;
	}

	private static boolean checkEventTypeFilter(int currentEventType,
			int[] resourceContextEventTypeFilter) {

		if ((resourceContextEventTypeFilter == null)
				|| (resourceContextEventTypeFilter.length == 0)) {
			return true;
		}

		for (int i = 0; i < resourceContextEventTypeFilter.length; i++) {
			int eventType = resourceContextEventTypeFilter[i];
			if (eventType == currentEventType) {
				return true;
			}
		}

		return false;
	}

	private static boolean checkResourceContextFilter(
			String currentResourceContext, String[] resourceContextFilter) {

		if ((resourceContextFilter == null)
				|| (resourceContextFilter.length == 0)) {
			return true;
		}

		for (int i = 0; i < resourceContextFilter.length; i++) {
			String resourceContext = resourceContextFilter[i];
			if (resourceContext.equals(currentResourceContext)) {
				return true;
			}
		}

		return false;
	}

}
