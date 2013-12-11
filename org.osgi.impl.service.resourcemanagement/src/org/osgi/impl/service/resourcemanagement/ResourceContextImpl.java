package org.osgi.impl.service.resourcemanagement;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.impl.service.resourcemanagement.bundlemanagement.BundleHolder;
import org.osgi.impl.service.resourcemanagement.bundlemanagement.BundleManager;
import org.osgi.impl.service.resourcemanagement.lock.ResourceContextLock;
import org.osgi.service.resourcemanagement.ResourceContext;
import org.osgi.service.resourcemanagement.ResourceContextEvent;
import org.osgi.service.resourcemanagement.ResourceMonitor;
import org.osgi.service.resourcemanagement.ResourceMonitorException;

/**
 * Implementation of ResourceContext.
 * 
 * @author Gregory BONNARDEL (Orange)
 */
public class ResourceContextImpl implements ResourceContext, BundleHolder {

	/**
	 * lock.
	 */
	private final ResourceContextLock lock;

	/**
	 * Resource Context name.
	 */
	private final String name;

	/**
	 * Bundles belonging to the Context
	 */
	private final List/* <Long> */bundles;

	/**
	 * Resource Monitors associated to the context.
	 */
	private final Map/* <String, ResourceMonitor> */monitors;

	/**
	 * notifier for ResourceContextEvent.
	 */
	private final ResourceContextEventNotifier eventNotifier;

	/**
	 * resource manager.
	 */
	private final ResourceManagerImpl resourceManager;

	/**
	 * bundle manager service
	 */
	private final BundleManager bundleManager;

	/**
	 * is true if the current context has been deleted (i.e.
	 * {@link #removeContext(ResourceContext)} has been previously called).
	 */
	private boolean isRemoved = false;

	/**
	 * Default constructor to be used to create new context.
	 * 
	 * @param pResourceManager
	 *            resource manager (the one calling this method)
	 * @param pBundleManager
	 *            bundle manager
	 * @param pName
	 *            name of the ResourceContext
	 * @param pEventNotifier
	 *            event notifier
	 * @param pPersistedResourceMonitorTypes
	 *            types of resource monitor.
	 */
	public ResourceContextImpl(final ResourceManagerImpl pResourceManager,
			final BundleManager pBundleManager, final String pName,
			final ResourceContextEventNotifier pEventNotifier) {
		resourceManager = pResourceManager;
		bundleManager = pBundleManager;
		eventNotifier = pEventNotifier;
		name = pName;

		lock = new ResourceContextLock();

		bundles = new ArrayList/* <Long> */();

		monitors = new Hashtable/* <String, ResourceMonitor> */();

	}

	public String getName() {
		return name;
	}

	public void addBundle(long bundleId) {
		// check the Resource Context exist
		checkResourceContextExistency();

		// delegates bundle adding to the bundle manager
		bundleManager.addBundleToHolder(bundleId, this);

		// create event and notify
		ResourceContextEvent event = new ResourceContextEvent(
				ResourceContextEvent.BUNDLE_ADDED, this, bundleId);
		eventNotifier.notify(event);

	}

	public void removeBundle(long bundleId) {
		checkResourceContextExistency();

		// delegates bundle removing to the bundle manager
		bundleManager.removeBundleFromHolder(bundleId, this);

		// create event and notify
		ResourceContextEvent event = new ResourceContextEvent(
				ResourceContextEvent.BUNDLE_REMOVED, this, bundleId);
		eventNotifier.notify(event);

	}

	public void removeBundle(long bundleId, ResourceContext destination) {
		removeBundle(bundleId);

		// if destination is not null, associate the bundle to the destination
		// resource context
		if (destination != null) {
			destination.addBundle(bundleId);
		}

	}

	public synchronized ResourceMonitor getMonitor(String resourceType) {
		checkResourceContextExistency();
		ResourceMonitor monitor = null;
		synchronized (monitors) {
			monitor = (ResourceMonitor) monitors.get(resourceType);
		}
		return monitor;
	}

	public synchronized void addResourceMonitor(ResourceMonitor resourceMonitor)
			throws ResourceMonitorException {

		// check the Resource Context is still existing
		checkResourceContextExistency();

		// check the Resource Monitor is not null
		if (resourceMonitor == null) {
			throw new NullPointerException("ResourceMonitor MUST not be null");
		}

		// check the Resource Monitor is associated to this context
		if (!resourceMonitor.getContext().equals(this)) {
			throw new ResourceMonitorException(
					"This ResourceMonitor is associated to another ResourceContext");
		}

		// add into the monitor map
		synchronized (monitors) {
			if (monitors.containsKey(resourceMonitor.getResourceType())) {
				// check there is no other monitor handling the same
				// resource type for this Resource Context
				throw new ResourceMonitorException(
						"A ResourceMonitor of the same type exists for this ResourceContext");
			}

			monitors.put(resourceMonitor.getResourceType(), resourceMonitor);
		}

	}

	public synchronized void removeResourceMonitor(
			ResourceMonitor resourceMonitor) {
		// check the Resource Context is still existing
		checkResourceContextExistency();

		// check the Resource Monitor is not null
		if (resourceMonitor == null) {
			throw new NullPointerException("ResourceMonitor MUST not be null");
		}

		// check if the ResourceMonitor is associated with this Resource Context
		ResourceMonitor innerRm = getMonitor(resourceMonitor.getResourceType());
		if (!innerRm.equals(resourceMonitor)) {
			// TODO handle this kind of error
		}

		synchronized (monitors) {
			monitors.remove(resourceMonitor.getResourceType());
		}

	}

	public synchronized void removeContext(ResourceContext destination) {
		// check the Resource Context is still existing
		checkResourceContextExistency();

		// delete all bundles
		List/* <Long> */bs = new ArrayList/* <Long> */();
		synchronized (bundles) {
			bs.addAll(bundles);
		}
		for (Iterator/* <Long> */it = bs.iterator(); it.hasNext();) {
			try {
				Long bundleId = (Long) it.next();
				removeBundle(bundleId.longValue(), destination);
			} catch (RuntimeException e) {
				// this exception can be thrown if the destination context has
				// been deleted
			}
		}

		// delete all monitors
		List/* <ResourceMonitor> */rms = new ArrayList/* <ResourceMonitor> */();
		synchronized (monitors) {
			rms.addAll(monitors.values());
		}
		for (Iterator/* <ResourceMonitor> */it = rms.iterator(); it.hasNext();) {
			ResourceMonitor rm = (ResourceMonitor) it.next();
			rm.delete();
		}

		resourceManager.removeContext(this);

		isRemoved = true;

	}

	/**
	 * Check Resource Context Existency
	 * 
	 * @throws IllegalStateException
	 */
	private synchronized void checkResourceContextExistency()
			throws IllegalStateException {

		if (isRemoved) {
			throw new IllegalStateException("ResourceContext has been removed");
		}

	}

	public long[] getBundleIds() {
		long[] bundleIds = new long[bundles.size()];
		int i = 0;
		synchronized (bundles) {
			for (Iterator it = bundles.iterator(); it.hasNext();) {
				Long bundleId = (Long) it.next();
				bundleIds[i] = bundleId.longValue();
				i++;
			}
		}
		return bundleIds;
	}

	public ResourceMonitor[] getMonitors() {
		checkResourceContextExistency();
		ResourceMonitor[] array = new ResourceMonitor[0];
		synchronized (monitors) {
			array = (ResourceMonitor[]) monitors.values().toArray(array);
		}
		return array;
	}

	public void addBundleToHolder(long bundleId) {
		synchronized (bundles) {
			bundles.add(bundleId);
		}

	}

	public void removeBundleToHolder(long bundleId) {
		synchronized (bundles) {
			bundles.remove(bundleId);
		}

	}

	/**
	 * Resource Context c1 is equals to ResourceContext c2 if c1.getName()
	 * equals c2.getName().
	 */
	public boolean equals(Object resourceContext) {
		if (resourceContext == null) {
			return false;
		}
		if (!(resourceContext instanceof ResourceContext)) {
			return false;
		}
		ResourceContext rc1 = (ResourceContext) resourceContext;
		return ((rc1.getName() != null) && rc1.getName().equals(getName()));
	}

}
