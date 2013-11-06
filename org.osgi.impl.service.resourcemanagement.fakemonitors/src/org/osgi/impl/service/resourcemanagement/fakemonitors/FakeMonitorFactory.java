package org.osgi.impl.service.resourcemanagement.fakemonitors;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.resourcemanagement.ResourceContext;
import org.osgi.service.resourcemanagement.ResourceContextEvent;
import org.osgi.service.resourcemanagement.ResourceContextListener;
import org.osgi.service.resourcemanagement.ResourceMonitor;
import org.osgi.service.resourcemanagement.ResourceMonitorException;
import org.osgi.service.resourcemanagement.ResourceMonitorFactory;

public class FakeMonitorFactory implements ResourceMonitorFactory,
		ResourceContextListener {

	/**
	 * type of {@link ResourceMonitor} this factory is able to create
	 */
	private final String factoryType;

	/**
	 * bundle context
	 */
	private final BundleContext bundleContext;

	/**
	 * sampling period for all {@link ResourceMonitor} created by this factory
	 */
	private final long samplingPeriod;

	/**
	 * monitoring period for all {@link ResourceMonitor} created by this factory
	 */
	private final long monitoringPeriod;

	private final long minValue;

	private final long maxValue;


	/**
	 * fake monitors
	 */
	private final Map<ResourceContext, FakeMonitor> fakeMonitors;

	/**
	 * register the factory as a ResourceContextListener to be informed when a
	 * ResourceContext is deleted.
	 */
	private ServiceRegistration<ResourceContextListener> serviceRegistration;

	private final Semaphore semaphore;


	/**
	 * 
	 * @param pBundleContext
	 * @param pFactoryType
	 */
	public FakeMonitorFactory(BundleContext pBundleContext,
			String pFactoryType, long pSamplingPeriod, long pMonitoringPeriod,
			long pMinValue, long pMaxValue) {
		bundleContext = pBundleContext;
		factoryType = pFactoryType;
		samplingPeriod = pSamplingPeriod;
		monitoringPeriod = pMonitoringPeriod;
		minValue = pMinValue;
		maxValue = pMaxValue;

		semaphore = new Semaphore(1);
		fakeMonitors = new HashMap<ResourceContext, FakeMonitor>();

		// register this factory as a ResourceContextListener
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(ResourceContextListener.EVENT_TYPE,
				ResourceContextEvent.RESOURCE_CONTEXT_DELETED);
		properties.put(ResourceMonitorFactory.RESOURCE_TYPE_PROPERTY,
				factoryType);
		serviceRegistration = (ServiceRegistration<ResourceContextListener>) bundleContext
				.registerService(
						new String[] { ResourceContextListener.class.getName(),
								ResourceMonitorFactory.class.getName() }, this,
						properties);

	}

	/**
	 * Unregister the factory
	 */
	public void stop() {
		serviceRegistration.unregister();

		// all existing monitors must be deleted
		List<FakeMonitor> duplicatedMonitors = new ArrayList<FakeMonitor>();

		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			return;
		}

		duplicatedMonitors.addAll(fakeMonitors.values());

		semaphore.release();
		
		for(Iterator<FakeMonitor> it = duplicatedMonitors.iterator(); it.hasNext();) {
			FakeMonitor tobeDeleted = it.next();
			tobeDeleted.delete();
		}

	}

	public String getType() {
		return factoryType;
	}

	public ResourceMonitor createResourceMonitor(ResourceContext resourceContext)
			throws ResourceMonitorException {
		ResourceMonitor resourceMonitor = null;
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			throw new ResourceMonitorException(
					"Unable to create a ResourceMonitor");
		}

		// at this point we are in a thread-safe section
		if (fakeMonitors.containsKey(resourceContext)) {
			semaphore.release();
			throw new ResourceMonitorException(
					"A Monitor of this type already exists for this ResourceContext");
		} else {
			FakeMonitor fm = new FakeMonitor(this, resourceContext,
					factoryType, samplingPeriod, monitoringPeriod, minValue,
					maxValue,
					bundleContext);
			fakeMonitors.put(resourceContext, fm);
			resourceMonitor = fm;
		}

		semaphore.release();

		return resourceMonitor;
	}

	/**
	 * This method is called when a {@link ResourceContext} has been deleted. We
	 * have to delete the {@link ResourceMonitor} if it was previously created.
	 */
	public void notify(ResourceContextEvent event) {
		if (event.getType() == ResourceContextEvent.RESOURCE_CONTEXT_DELETED) {
			// find out if this factory created a ResourceMonitor for this
			// factory
			ResourceContext resourceContext = event.getContext();

			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				return;
			}

			ResourceMonitor resourceMonitor = fakeMonitors.get(resourceContext);

			semaphore.release();

			if (resourceMonitor != null) {
				resourceMonitor.delete();
			}

		}

	}


	public void removeResourceMonitor(ResourceMonitor resourceMonitor) {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			return;
		}
		
		fakeMonitors.values().remove(resourceMonitor);

		semaphore.release();
	}
	


}
