package org.osgi.impl.service.resourcemanagement;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.resourcemanagement.bundlemanagement.BundleManager;
import org.osgi.impl.service.resourcemanagement.lock.ResourceContextLock;
import org.osgi.impl.service.resourcemanagement.persistency.ResourceContextInfo;
import org.osgi.impl.service.resourcemanagement.threadmanager.ThreadManager;
import org.osgi.service.resourcemanagement.ResourceContext;
import org.osgi.service.resourcemanagement.ResourceContextEvent;
import org.osgi.service.resourcemanagement.ResourceManager;
import org.osgi.service.resourcemanagement.ResourceMonitor;
import org.osgi.service.resourcemanagement.ResourceMonitorException;
import org.osgi.service.resourcemanagement.ResourceMonitorFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ResourceManagerImpl implements ResourceManager,
		ServiceTrackerCustomizer {

	private ResourceContextLock lock;

	/**
	 * Map containing Resource Context object. Keys are the name of the
	 * ResourceContext.
	 */
	private final Map/* <String, ResourceContext> */resourceContexts;

	/**
	 * list of available factories
	 */
	private final Map/* <String, ResourceMonitorFactory> */resourceMonitorFactories;

	/**
	 * Notifier for ResourceContext events
	 */
	private final ResourceContextEventNotifier eventNotifier;

	/**
	 * Thread manager
	 */
	private final ThreadManager threadManager;

	/**
	 * Bundle manager.
	 */
	private final BundleManager bundleManager;

	/**
	 * service tracker (for ResourceMonitorFactory)
	 */
	private ServiceTracker serviceTracker;

	/**
	 * Bundle context
	 */
	private BundleContext context;

	/**
	 * system resource context
	 */
	private ResourceContext systemResourceContext;

	/**
	 * framework resource context
	 */
	private ResourceContext frameworkResourceContext;

	/**
	 * persistence manager
	 */
	private PersistenceManager persistenceManager;

	public ResourceManagerImpl(BundleManager pBundleManager,
			ResourceContextEventNotifier pEventNotifier,
			ThreadManager pThreadManager) {

		lock = new ResourceContextLock();

		bundleManager = pBundleManager;
		eventNotifier = pEventNotifier;
		threadManager = pThreadManager;

		resourceContexts = new Hashtable/* <String, ResourceContext> */();
		resourceMonitorFactories = new Hashtable/*
												 * <String,
												 * ResourceMonitorFactory>
												 */();

	}

	public void restoreContext(ResourceContextInfo[] persistedResourceContexts) {
		for (int i = 0; i < persistedResourceContexts.length; i++) {
			ResourceContextInfo persistedResourceContext = persistedResourceContexts[i];

			ResourceContext newly = createContext(persistedResourceContext
					.getName());
			for (Iterator/* <Long> */it = persistedResourceContext
					.getBundleIds()
					.iterator(); it.hasNext();) {
				long bundleId = (Long) it.next();
				try {
					newly.addBundle(bundleId);
				} catch (RuntimeException e) {
					System.out
							.println("WARNING - bundle "
									+ bundleId
									+ " is not installed and thus can not be associated to context "
									+ persistedResourceContext.getName());
				}
			}
		}
	}

	public void start(BundleContext pContext) {
		context = pContext;
		persistenceManager = new PersistenceManager(pContext, this);
		persistenceManager.restoreContexts();

		resourceMonitorFactories.clear();
		setDefaultResourceContexts();

		serviceTracker = new ServiceTracker(context,
				ResourceMonitorFactory.class.getName(), this);
		serviceTracker.open();
	}

	public void stop(BundleContext pContext) {
		serviceTracker.close();
		serviceTracker = null;

		persistenceManager.persist();
	}

	public ResourceContext[] listContext() {
		ResourceContext[] rcArray = new ResourceContext[0];
		synchronized (resourceContexts) {
			rcArray = (ResourceContext[]) resourceContexts.values().toArray(
					rcArray);
		}
		return rcArray;
	}

	public ResourceContext createContext(String name, ResourceContext template)
			throws RuntimeException {

		lock.acquire();

		// check if a ResourceContext exists with the same name
		ResourceContext existingResourceContext = getContext(name);
		if (existingResourceContext != null) {
			lock.release();
			throw new RuntimeException(
					"A ResourceContext with the same name already exists");
		}

		ResourceContext resourceContext = createContext(name);

		// release the lock
		lock.release();

		// create ResourceMonitor based on those associated with the provided
		// template
		if (template != null) {
			ResourceMonitor[] templateMonitors = null;
			try {
				templateMonitors = template.getMonitors();
			} catch (IllegalStateException e) {
				// in the case where template has been deleted.
			}
			if (templateMonitors != null) {
				for (int i = 0; i < templateMonitors.length; i++) {
					ResourceMonitor resourceMonitor = templateMonitors[i];

					ResourceMonitorFactory factory = getFactory(resourceMonitor
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

	protected void removeContext(ResourceContext resourceContext) {

		lock.acquire();

		synchronized (resourceContexts) {
			resourceContexts.remove(resourceContext.getName());
		}

		// send a notification
		ResourceContextEvent event = new ResourceContextEvent(
				ResourceContextEvent.RESOURCE_CONTEXT_DELETED, resourceContext);
		eventNotifier.notify(event);

		lock.release();

	}

	public ResourceContext getContext(String name) {
		ResourceContext resourceContext;
		synchronized (resourceContexts) {
			resourceContext = (ResourceContext) resourceContexts.get(name);
		}
		return resourceContext;
	}

	public ResourceContext getContext(long bundleId) {
		ResourceContext resourceContext = bundleManager
				.getResourceContext(bundleId);
		return resourceContext;
	}

	public ResourceContext getCurrentContext() {
		ResourceContext resourceContext = getContext(Thread.currentThread());
		return resourceContext;
	}

	public ResourceContext switchCurrentContext(ResourceContext context) {
		ResourceContext previousResourceContext = getCurrentContext();

		Thread currentThread = Thread.currentThread();
		threadManager.switchContext(currentThread, context);

		// notify all ResourceMonitors of the outgoing ResourceContent
		if (previousResourceContext != null) {
			ResourceMonitor[] outgoingMonitors = previousResourceContext
					.getMonitors();
			for (int i = 0; i < outgoingMonitors.length; i++) {
				ResourceMonitor outgoingMonitor = outgoingMonitors[i];
				outgoingMonitor.notifyOutgoingThread(currentThread);
			}
		}

		// notify all ResourceMonitor of the incoming ResourceContext
		ResourceMonitor[] incomingMonitors = context.getMonitors();
		for (int i = 0; i < incomingMonitors.length; i++) {
			ResourceMonitor incomingMonitor = incomingMonitors[i];
			incomingMonitor.notifyIncomingThread(currentThread);
		}

		return previousResourceContext;
	}

	public ResourceContext getContext(Thread t) {
		ResourceContext resourceContext = threadManager.getResourceContext(t);
		return resourceContext;
	}

	public String[] getSupportedTypes() {
		String[] supportedTypes = new String[0];
		synchronized (resourceMonitorFactories) {
			supportedTypes = (String[]) resourceMonitorFactories.keySet()
					.toArray(
					supportedTypes);
		}
		return supportedTypes;
	}

	/**
	 * This method is called when a new ResourceMonitorFactory is available on
	 * the fremwork.
	 * 
	 * @param reference
	 *            service reference of the new factory
	 * @return the ResourceMonitorFactory object
	 */
	public Object addingService(ServiceReference reference) {
		ResourceMonitorFactory factory = context.getService(reference);
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
	public void modifiedService(ServiceReference reference, Object service) {
		// nothing to do.
	}

	/**
	 * This method is called by the ServiceTracker to report a
	 * ResourceMonitorFactory is being unavailable.
	 */
	public void removedService(ServiceReference reference, Object service) {
		ResourceMonitorFactory factory = (ResourceMonitorFactory) service;
		synchronized (resourceMonitorFactories) {
			resourceMonitorFactories.remove(factory.getType());
		}

	}

	/**
	 * Retrieves all ResourceMonitorFactories
	 * 
	 * @return factories
	 */
	public ResourceMonitorFactory getResourceMonitorFactory(String factoryType) {
		ResourceMonitorFactory factory = null;
		synchronized (resourceMonitorFactories) {
			factory = (ResourceMonitorFactory) resourceMonitorFactories
					.get(factoryType);
		}
		return factory;
	}

	/**
	 * Set default resource context settings. This method configures the
	 * FRAMEWORK Resource Context as well as the SYSTEM Resource Context.
	 */
	private void setDefaultResourceContexts() {

		if (getContext(SYSTEM_CONTEXT_NAME) == null) {
			// system resource context
			systemResourceContext = createContext(SYSTEM_CONTEXT_NAME,
					(ResourceContext) null);
			systemResourceContext.addBundle(0);
		}

		if (getContext(FRAMEWORK_CONTEXT_NAME) == null) {
			// framework resource context
			frameworkResourceContext = createContext(FRAMEWORK_CONTEXT_NAME,
					(ResourceContext) null);
		}
	}

	/**
	 * Create a new ResourceContext
	 * 
	 * @param name
	 *            resource context name
	 * @param resourceMonitorTypes
	 *            types of resource monitor associated with this context
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
	 * @param resourceType
	 *            type of resource
	 * @return the ResourceMonitorFactory or null
	 */
	private ResourceMonitorFactory getFactory(String resourceType) {
		ResourceMonitorFactory factory = null;

		StringBuffer filter = new StringBuffer();
		filter.append("(");
		filter.append(ResourceMonitorFactory.RESOURCE_TYPE_PROPERTY);
		filter.append("=");
		filter.append(resourceType);
		filter.append(")");
		try {
			Collection/* <ServiceReference<ResourceMonitorFactory>> */srs = context
					.getServiceReferences(ResourceMonitorFactory.class,
							filter.toString());

			if ((srs != null) && (!srs.isEmpty())) {
				ServiceReference/* <ResourceMonitorFactory> */sr = (ServiceReference) srs
						.iterator()
						.next();
				factory = context.getService(sr);
			}

		} catch (InvalidSyntaxException e) {
			// not expected
		}

		return factory;
	}
}
