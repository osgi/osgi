
package org.osgi.impl.service.resourcemonitoring;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;
import org.osgi.service.resourcemonitoring.ResourceContextListener;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 *
 */
public class ResourceContextEventNotifierImpl implements
		ResourceContextEventNotifier,
		ServiceTrackerCustomizer<ResourceContextListener,ResourceContextListener> {

	private BundleContext	context;
	private ServiceTracker<ResourceContextListener,ResourceContextListener>					serviceTracker;

	// private final Map<ResourceContextListener,
	// ServiceReference<ResourceContextListener>> listeners;
	private final Map<ResourceContextListener,ServiceReference<ResourceContextListener>>	listeners;

	/**
	 * 
	 */
	public ResourceContextEventNotifierImpl() {
		listeners = new Hashtable<>();
	}

	@Override
	public void start(BundleContext pcontext) {
		this.context = pcontext;
		serviceTracker = new ServiceTracker<>(this.context,
				ResourceContextListener.class, this);
		serviceTracker.open();
	}

	@Override
	public void stop(BundleContext pcontext) {
		serviceTracker.close();
		serviceTracker = null;

		listeners.clear();
	}

	@Override
	public void notify(ResourceContextEvent event) {
		synchronized (listeners) {
			for (Iterator<ResourceContextListener> it = listeners.keySet()
					.iterator(); it.hasNext();) {
				ResourceContextListener currentRcl = it
						.next();
				ServiceReference<ResourceContextListener> currentSr = listeners
						.get(currentRcl);

				String[] resourceContextFilter = getResourceContextFilter(currentSr);

				if (checkResourceContextFilter(event.getContext()
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

	@Override
	public ResourceContextListener addingService(
			ServiceReference<ResourceContextListener> reference) {
		ResourceContextListener rcl = context.getService(reference);

		synchronized (listeners) {
			listeners.put(rcl, reference);
		}

		return rcl;
	}

	@Override
	public void modifiedService(
			ServiceReference<ResourceContextListener> reference,
			ResourceContextListener service) {
		synchronized (listeners) {
			listeners.put(service, reference);
		}
	}

	@Override
	public void removedService(
			ServiceReference<ResourceContextListener> reference,
			ResourceContextListener service) {
		synchronized (listeners) {
			listeners.remove(service);
		}

	}

	private static String[] getResourceContextFilter(
			ServiceReference<ResourceContextListener> serviceReference) {
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
