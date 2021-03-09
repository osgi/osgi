/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.resourcemonitoring;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.osgi.impl.service.resourcemonitoring.bundlemanagement.BundleHolder;
import org.osgi.impl.service.resourcemonitoring.bundlemanagement.BundleManager;
import org.osgi.impl.service.resourcemonitoring.bundlemanagement.BundleManagerException;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;
import org.osgi.service.resourcemonitoring.ResourceContextException;
import org.osgi.service.resourcemonitoring.ResourceMonitor;
import org.osgi.service.resourcemonitoring.ResourceMonitorException;

/**
 * Implementation of ResourceContext.
 * 
 * @author Gregory BONNARDEL (Orange)
 */
public class ResourceContextImpl implements ResourceContext, BundleHolder {

	/**
	 * Resource Context name.
	 */
	private final String						name;

	/**
	 * Bundles belonging to the Context. List<Long> bundles.
	 */
	private final List<Long>					bundles;

	/**
	 * Resource Monitors associated to the context. Map<String, ResourceMonitor>
	 * monitors.
	 */
	private final Map<String,ResourceMonitor< ? >>	monitors;

	/**
	 * notifier for ResourceContextEvent.
	 */
	private final ResourceContextEventNotifier	eventNotifier;

	/**
	 * ResourceMonitoringServiceImpl.
	 */
	private final ResourceMonitoringServiceImpl	resourceMonitoringServiceImpl;

	/**
	 * bundle manager service
	 */
	private final BundleManager					bundleManager;

	/**
	 * is true if the current context has been deleted (i.e.
	 * {@link #removeContext(ResourceContext)} has been previously called).
	 */
	private boolean								isRemoved	= false;

	/**
	 * Default constructor to be used to create new context.
	 * 
	 * @param resourceMonitoringServiceImpl resourceMonitoringServiceImpl (the
	 *        one calling this method)
	 * @param pBundleManager bundle manager
	 * @param pName name of the ResourceContext
	 * @param pEventNotifier event notifier
	 */
	public ResourceContextImpl(final ResourceMonitoringServiceImpl resourceMonitoringServiceImpl,
			final BundleManager pBundleManager, final String pName,
			final ResourceContextEventNotifier pEventNotifier) {
		this.resourceMonitoringServiceImpl = resourceMonitoringServiceImpl;
		bundleManager = pBundleManager;
		eventNotifier = pEventNotifier;
		name = pName;
		bundles = new ArrayList<>();
		monitors = new Hashtable<>();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void addBundle(long bundleId) throws ResourceContextException {
		// check the Resource Context exist
		checkResourceContextExistency();

		// delegates bundle adding to the bundle manager
		try {
			bundleManager.addBundleToHolder(bundleId, this);
		} catch (BundleManagerException e) {
			throw new ResourceContextException(e.getMessage(), e);
		}

		// create event and notify
		ResourceContextEvent event = new ResourceContextEvent(
				ResourceContextEvent.BUNDLE_ADDED, this, bundleId);
		eventNotifier.notify(event);

	}

	@Override
	public void removeBundle(long bundleId) throws ResourceContextException {
		checkResourceContextExistency();

		// delegates bundle removing to the bundle manager
		try {
			bundleManager.removeBundleFromHolder(bundleId, this);
		} catch (BundleManagerException e) {
			throw new ResourceContextException(e.getMessage(), e);
		}

		// create event and notify
		ResourceContextEvent event = new ResourceContextEvent(
				ResourceContextEvent.BUNDLE_REMOVED, this, bundleId);
		eventNotifier.notify(event);

	}

	@Override
	public void removeBundle(long bundleId, ResourceContext destination) throws ResourceContextException {
		removeBundle(bundleId);

		// if destination is not null, associate the bundle to the destination
		// resource context
		if (destination != null) {
			destination.addBundle(bundleId);
		}

	}

	@Override
	public synchronized ResourceMonitor< ? > getMonitor(String resourceType)
			throws ResourceContextException {
		checkResourceContextExistency();
		ResourceMonitor< ? > monitor = null;
		synchronized (monitors) {
			monitor = monitors.get(resourceType);
		}
		return monitor;
	}

	@Override
	public synchronized void addResourceMonitor(
			ResourceMonitor< ? > resourceMonitor)
			throws ResourceContextException {

		// check the Resource Context is still existing
		checkResourceContextExistency();

		// check the Resource Monitor is not null
		if (resourceMonitor == null) {
			throw new NullPointerException("ResourceMonitor MUST not be null");
		}

		// check the Resource Monitor is associated to this context
		if (!resourceMonitor.getContext().equals(this)) {
			throw new ResourceContextException(
					"This ResourceMonitor is associated to another ResourceContext");
		}

		// add into the monitor map
		synchronized (monitors) {
			if (monitors.containsKey(resourceMonitor.getResourceType())) {
				// check there is no other monitor handling the same
				// resource type for this Resource Context
				throw new ResourceContextException(
						"A ResourceMonitor of the same type exists for this ResourceContext");
			}

			monitors.put(resourceMonitor.getResourceType(), resourceMonitor);
		}

	}

	@Override
	public synchronized void removeResourceMonitor(
			ResourceMonitor< ? > resourceMonitor)
			throws ResourceContextException {
		// check the Resource Context is still existing
		checkResourceContextExistency();

		// check the Resource Monitor is not null
		if (resourceMonitor == null) {
			Logger.getLogger(this.getClass().getName()).finest("ResourceMonitor MUST not be null");
			return;
		}

		// check if the ResourceMonitor is associated with this Resource Context
		ResourceMonitor< ? > innerRm = getMonitor(
				resourceMonitor.getResourceType());
		if (!innerRm.equals(resourceMonitor)) {
			// TODO handle this kind of error
		}

		synchronized (monitors) {
			monitors.remove(resourceMonitor.getResourceType());
		}

	}

	@Override
	public synchronized void removeContext(ResourceContext destination) throws ResourceContextException {
		// check the Resource Context is still existing
		checkResourceContextExistency();

		// delete all bundles
		List<Long> bs = new ArrayList<>();
		synchronized (bundles) {
			bs.addAll(bundles);
		}
		for (Iterator<Long> it = bs.iterator(); it.hasNext();) {
			try {
				Long bundleId = it.next();
				removeBundle(bundleId.longValue(), destination);
			} catch (ResourceContextException e) {
				// this exception can be thrown if the destination context has
				// been deleted
				e.printStackTrace();
			}
		}

		// delete all monitors
		List<ResourceMonitor< ? >> resourceMonitors = new ArrayList<>();
		synchronized (monitors) {
			resourceMonitors.addAll(monitors.values());
		}
		for (Iterator<ResourceMonitor< ? >> it = resourceMonitors.iterator(); it
				.hasNext();) {
			ResourceMonitor< ? > rm = it.next();
			try {
				rm.delete();
			} catch (ResourceMonitorException e) {
				throw new ResourceContextException(e.getMessage(), e);
			}
		}

		resourceMonitoringServiceImpl.removeContext(this);

		isRemoved = true;

	}

	/**
	 * Check Resource Context Existency
	 * 
	 * @throws ResourceContextException
	 */
	private synchronized void checkResourceContextExistency() throws ResourceContextException {
		if (isRemoved) {
			throw new ResourceContextException("ResourceContext has been removed.");
		}
	}

	@Override
	public long[] getBundleIds() {
		long[] bundleIds = new long[bundles.size()];
		int i = 0;
		synchronized (bundles) {
			for (Iterator<Long> it = bundles.iterator(); it.hasNext();) {
				Long bundleId = it.next();
				bundleIds[i] = bundleId.longValue();
				i++;
			}
		}
		return bundleIds;
	}

	@Override
	public ResourceMonitor< ? >[] getMonitors()
			throws ResourceContextException {
		checkResourceContextExistency();
		ResourceMonitor< ? >[] array = new ResourceMonitor[0];
		synchronized (monitors) {
			array = monitors.values().toArray(array);
		}
		return array;
	}

	@Override
	public void addBundleToHolder(long bundleId) {
		synchronized (bundles) {
			bundles.add(Long.valueOf(Long.toString(bundleId)));
		}

	}

	@Override
	public void removeBundleToHolder(long bundleId) {
		synchronized (bundles) {
			bundles.remove(Long.valueOf(Long.toString(bundleId)));
		}

	}

	/**
	 * Resource Context c1 is equals to ResourceContext c2 if c1.getName()
	 * equals c2.getName().
	 */
	@Override
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
