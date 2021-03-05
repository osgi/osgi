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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.resourcemonitoring.bundlemanagement.BundleManager;
import org.osgi.impl.service.resourcemonitoring.lock.ResourceContextLock;
import org.osgi.impl.service.resourcemonitoring.persistency.ResourceContextInfo;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;
import org.osgi.service.resourcemonitoring.ResourceContextException;
import org.osgi.service.resourcemonitoring.ResourceMonitor;
import org.osgi.service.resourcemonitoring.ResourceMonitorException;
import org.osgi.service.resourcemonitoring.ResourceMonitorFactory;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 *
 */
public class ResourceMonitoringServiceImpl implements ResourceMonitoringService,
		ServiceTrackerCustomizer<ResourceMonitorFactory<Object>,ResourceMonitorFactory<Object>> {

	private ResourceContextLock					lock;

	/**
	 * Map containing Resource Context object. Keys are the name of the
	 * ResourceContext. Map<String, ResourceContext> resourceContexts.
	 */
	private final Map<String,ResourceContext>										resourceContexts;

	/**
	 * list of available factories. Map<String, ResourceMonitorFactory>
	 * resourceMonitorFactories.
	 */
	private final Map<String,ResourceMonitorFactory<Object>>						resourceMonitorFactories;

	/**
	 * Notifier for ResourceContext events
	 */
	private final ResourceContextEventNotifier	eventNotifier;

	/**
	 * Bundle manager.
	 */
	private final BundleManager					bundleManager;

	/**
	 * Service tracker (for ResourceMonitorFactory)
	 */
	private ServiceTracker<ResourceMonitorFactory<Object>,ResourceMonitorFactory<Object>>	serviceTracker;

	/**
	 * Bundle context
	 */
	private BundleContext						context;

	/**
	 * system resource context
	 */
	private ResourceContext						systemResourceContext;

	/**
	 * framework resource context
	 */
	private ResourceContext						frameworkResourceContext;

	/**
	 * persistence manager
	 */
	private PersistenceManager					persistenceManager;

	/**
	 * @param pBundleManager
	 * @param pEventNotifier
	 */
	public ResourceMonitoringServiceImpl(BundleManager pBundleManager,
			ResourceContextEventNotifier pEventNotifier) {

		lock = new ResourceContextLock();

		bundleManager = pBundleManager;
		eventNotifier = pEventNotifier;

		resourceContexts = new Hashtable<>();
		resourceMonitorFactories = new Hashtable<>();

	}

	/**
	 * @param persistedResourceContexts
	 */
	public void restoreContext(ResourceContextInfo[] persistedResourceContexts) {
		for (int i = 0; i < persistedResourceContexts.length; i++) {
			ResourceContextInfo persistedResourceContext = persistedResourceContexts[i];

			ResourceContext newly = createContext(persistedResourceContext
					.getName());
			for (Iterator<Long> it = persistedResourceContext
					.getBundleIds()
					.iterator(); it.hasNext();) {
				long bundleId = it.next().longValue();
				try {
					newly.addBundle(bundleId);
				} catch (ResourceContextException e) {
					System.out
							.println("WARNING - bundle "
									+ bundleId
									+ " is not installed and thus can not be associated to context "
									+ persistedResourceContext.getName());
				}
			}
		}
	}

	/**
	 * @param pContext
	 * @throws IllegalArgumentException, see
	 *         {@link PersistenceManager#restoreContexts()},
	 */
	public void start(BundleContext pContext) throws IllegalArgumentException {
		context = pContext;
		persistenceManager = new PersistenceManager(pContext, this);
		persistenceManager.restoreContexts();

		resourceMonitorFactories.clear();
		setDefaultResourceContexts();

		serviceTracker = new ServiceTracker<>(
				context,
				ResourceMonitorFactory.class.getName(), this);
		serviceTracker.open();
	}

	/**
	 * @param pContext
	 */
	public void stop(BundleContext pContext) {
		serviceTracker.close();
		serviceTracker = null;

		persistenceManager.persist();
	}

	@Override
	public ResourceContext[] listContext() {
		ResourceContext[] rcArray = new ResourceContext[0];
		synchronized (resourceContexts) {
			rcArray = resourceContexts.values().toArray(
					rcArray);
		}
		return rcArray;
	}

	@Override
	public ResourceContext createContext(String name, ResourceContext template)
			throws IllegalArgumentException {

		lock.acquire();

		// check if a ResourceContext exists with the same name
		ResourceContext existingResourceContext = getContext(name);
		if (existingResourceContext != null) {
			lock.release();
			throw new IllegalArgumentException(
					"A ResourceContext with the same name already exists");
		}

		ResourceContext resourceContext = createContext(name);

		// release the lock
		lock.release();

		// create ResourceMonitor based on those associated with the provided
		// template
		if (template != null) {
			ResourceMonitor< ? >[] templateMonitors = null;
			try {
				templateMonitors = template.getMonitors();
			} catch (ResourceContextException e) {
				// in the case where template has been deleted.
			}
			if (templateMonitors != null) {
				for (int i = 0; i < templateMonitors.length; i++) {
					ResourceMonitor< ? > resourceMonitor = templateMonitors[i];

					ResourceMonitorFactory<Object> factory = getFactory(
							resourceMonitor
							.getResourceType());

					if (factory != null) {
						try {
							factory.createResourceMonitor(resourceContext);
						} catch (ResourceMonitorException e) {
							System.out
									.println("WARNING - unable to create a ResourceMonitor of type "
											+ resourceMonitor.getResourceType()
											+ " for newly created ResourceContext "
											+ name);
						}
					}
				}
			}
		}

		return resourceContext;
	}

	/**
	 * @param resourceContext
	 */
	protected void removeContext(ResourceContext resourceContext) {

		lock.acquire();

		synchronized (resourceContexts) {
			resourceContexts.remove(resourceContext.getName());
		}

		// send a notification
		ResourceContextEvent event = new ResourceContextEvent(
				ResourceContextEvent.RESOURCE_CONTEXT_REMOVED, resourceContext);
		eventNotifier.notify(event);

		lock.release();

	}

	@Override
	public ResourceContext getContext(String name) {
		ResourceContext resourceContext;
		synchronized (resourceContexts) {
			resourceContext = resourceContexts.get(name);
		}
		return resourceContext;
	}

	@Override
	public ResourceContext getContext(long bundleId) {
		ResourceContext resourceContext = bundleManager
				.getResourceContext(bundleId);
		return resourceContext;
	}

	@Override
	public String[] getSupportedTypes() {
		String[] supportedTypes = new String[0];
		synchronized (resourceMonitorFactories) {
			supportedTypes = resourceMonitorFactories.keySet()
					.toArray(
							supportedTypes);
		}
		return supportedTypes;
	}

	/**
	 * This method is called when a new ResourceMonitorFactory is available on
	 * the framework.
	 * 
	 * @param reference service reference of the new factory
	 * @return the ResourceMonitorFactory object
	 */
	@Override
	public ResourceMonitorFactory<Object> addingService(
			ServiceReference<ResourceMonitorFactory<Object>> reference) {
		ResourceMonitorFactory<Object> factory = context.getService(reference);
		if (factory != null) {
			synchronized (resourceMonitorFactories) {
				resourceMonitorFactories.put(factory.getType(), factory);
			}
		}
		return factory;
	}

	/**
	 * This method is called when a ResourceMonitorFactory service is modified.
	 * Nothing to do.
	 */
	@Override
	public void modifiedService(
			ServiceReference<ResourceMonitorFactory<Object>> reference,
			ResourceMonitorFactory<Object> service) {
		// nothing to do.
	}

	/**
	 * This method is called by the ServiceTracker to report a
	 * ResourceMonitorFactory is being unavailable.
	 */
	@Override
	public void removedService(
			ServiceReference<ResourceMonitorFactory<Object>> reference,
			ResourceMonitorFactory<Object> service) {
		ResourceMonitorFactory<Object> factory = service;
		synchronized (resourceMonitorFactories) {
			resourceMonitorFactories.remove(factory.getType());
		}
	}

	/**
	 * Retrieves all ResourceMonitorFactories
	 * 
	 * @param factoryType
	 * 
	 * @return factories
	 */
	public ResourceMonitorFactory<Object> getResourceMonitorFactory(
			String factoryType) {
		ResourceMonitorFactory<Object> factory = null;
		synchronized (resourceMonitorFactories) {
			factory = resourceMonitorFactories
					.get(factoryType);
		}
		return factory;
	}

	/**
	 * Set default resource context settings. This method configures the
	 * FRAMEWORK Resource Context as well as the SYSTEM Resource Context.
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link ResourceMonitoringServiceImpl#createContext(String, ResourceContext)}
	 *         , or this exception wraps {@link ResourceContext#addBundle(long)}
	 */
	private void setDefaultResourceContexts() throws IllegalArgumentException {
		if (getContext(SYSTEM_CONTEXT_NAME) == null) {
			// system resource context
			systemResourceContext = createContext(SYSTEM_CONTEXT_NAME,
					(ResourceContext) null);
			try {
				systemResourceContext.addBundle(0);
			} catch (ResourceContextException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}

		if (getContext(FRAMEWORK_CONTEXT_NAME) == null) {
			// framework resource context
			frameworkResourceContext = createContext(FRAMEWORK_CONTEXT_NAME,
					(ResourceContext) null);
			System.out.println("DEBUG: frameworkResourceContext: " + frameworkResourceContext);
		}
	}

	/**
	 * Create a new ResourceContext
	 * 
	 * @param name resource context name
	 * @param resourceMonitorTypes types of resource monitor associated with
	 *        this context
	 * @return a new ResourceContext
	 */
	private ResourceContext createContext(String name) {
		// create the new ResourceContext object
		ResourceContext resourceContext = new ResourceContextImpl(this,
				bundleManager, name, eventNotifier);

		// add the context to the list
		synchronized (resourceContexts) {
			resourceContexts.put(name, resourceContext);
		}

		// send a notification about the new Resource Context creation.
		ResourceContextEvent event = new ResourceContextEvent(
				ResourceContextEvent.RESOURCE_CONTEXT_CREATED, resourceContext);
		eventNotifier.notify(event);
		return resourceContext;
	}

	/**
	 * Retrieves the ResourceMonitorFactory based on the type of ResourceMonitor
	 * it can create.
	 * 
	 * @param resourceType type of resource
	 * @return the ResourceMonitorFactory or null
	 */
	private ResourceMonitorFactory<Object> getFactory(String resourceType) {
		ResourceMonitorFactory<Object> factory = null;

		StringBuffer filter = new StringBuffer();
		filter.append("(");
		filter.append(ResourceMonitorFactory.RESOURCE_TYPE_PROPERTY);
		filter.append("=");
		filter.append(resourceType);
		filter.append(")");
		try {
			@SuppressWarnings({
					"unchecked", "rawtypes"
			})
			Collection<ServiceReference<ResourceMonitorFactory<Object>>> srs = (Collection) context
					.getServiceReferences(ResourceMonitorFactory.class,
							filter.toString());

			if ((srs != null) && (!srs.isEmpty())) {
				ServiceReference<ResourceMonitorFactory<Object>> sr = srs
						.iterator()
						.next();
				factory = context.getService(sr);
			}
		} catch (InvalidSyntaxException e) {
			// not expected
			e.printStackTrace();
		}

		return factory;
	}
}
