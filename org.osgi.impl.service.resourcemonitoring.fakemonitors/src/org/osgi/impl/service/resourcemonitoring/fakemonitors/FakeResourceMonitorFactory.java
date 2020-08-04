
package org.osgi.impl.service.resourcemonitoring.fakemonitors;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;
import org.osgi.service.resourcemonitoring.ResourceContextListener;
import org.osgi.service.resourcemonitoring.ResourceMonitor;
import org.osgi.service.resourcemonitoring.ResourceMonitorException;
import org.osgi.service.resourcemonitoring.ResourceMonitorFactory;

/**
 *
 */
public class FakeResourceMonitorFactory
		implements ResourceMonitorFactory<Long>,
		ResourceContextListener {

	/**
	 * type of {@link ResourceMonitor} this factory is able to create
	 */
	private final String		factoryType;

	/**
	 * bundle context
	 */
	private final BundleContext	bundleContext;

	/**
	 * sampling period for all {@link ResourceMonitor} created by this factory
	 */
	private final long			samplingPeriod;

	/**
	 * monitoring period for all {@link ResourceMonitor} created by this factory
	 */
	private final long			monitoringPeriod;

	private final long			minValue;

	private final long			maxValue;

	private final long			coeff;

	private final long			initialValue;

	/**
	 * fake monitors Map<ResourceContext, FakeMonitor>
	 */
	private final Map<ResourceContext,FakeMonitor>	fakeMonitors;

	/**
	 * register the factory as a ResourceContextListener to be informed when a
	 * ResourceContext is deleted. ServiceRegistration<ResourceContextListener>
	 */
	private ServiceRegistration< ? >	serviceRegistration;

	private final Lock			semaphore;

	/**
	 * @param pBundleContext
	 * @param pFactoryType
	 * @param pSamplingPeriod
	 * @param pMonitoringPeriod
	 * @param pMinValue
	 * @param pMaxValue
	 * @param pCoeff
	 * @param pInitialValue
	 */
	public FakeResourceMonitorFactory(BundleContext pBundleContext,
			String pFactoryType, long pSamplingPeriod, long pMonitoringPeriod,
			long pMinValue, long pMaxValue, long pCoeff, long pInitialValue) {
		bundleContext = pBundleContext;
		factoryType = pFactoryType;
		samplingPeriod = pSamplingPeriod;
		monitoringPeriod = pMonitoringPeriod;
		minValue = pMinValue;
		maxValue = pMaxValue;
		coeff = pCoeff;
		initialValue = pInitialValue;

		semaphore = new Lock();
		fakeMonitors = new Hashtable<>();

		// register this factory as a ResourceContextListener.
		// Dictionary<String, Object> properties.
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(ResourceMonitorFactory.RESOURCE_TYPE_PROPERTY,
				factoryType);
		serviceRegistration = bundleContext
				.registerService(
						new String[] {ResourceContextListener.class.getName(),
								ResourceMonitorFactory.class.getName()}, this,
						properties);
	}

	/**
	 * Unregister the factory
	 */
	public void stop() {
		serviceRegistration.unregister();

		// all existing monitors must be deleted
		// List duplicatedMonitors = new ArrayList<FakeMonitor>();
		List<FakeMonitor> duplicatedMonitors = new ArrayList<>();

		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			return;
		}

		duplicatedMonitors.addAll(fakeMonitors.values());

		semaphore.release();

		for (Iterator<FakeMonitor> it = duplicatedMonitors.iterator(); it
				.hasNext();) {
			FakeMonitor tobeDeleted = it.next();
			try {
				tobeDeleted.delete();
			} catch (ResourceMonitorException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public String getType() {
		return factoryType;
	}

	@Override
	public ResourceMonitor<Long> createResourceMonitor(
			ResourceContext resourceContext)
			throws ResourceMonitorException {
		ResourceMonitor<Long> resourceMonitor = null;
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
					maxValue, initialValue, coeff, bundleContext);
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
	@Override
	public void notify(ResourceContextEvent event) {
		if (event.getType() == ResourceContextEvent.RESOURCE_CONTEXT_REMOVED) {
			// find out if this factory created a ResourceMonitor for this
			// factory
			ResourceContext resourceContext = event.getContext();

			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				return;
			}

			ResourceMonitor<Long> resourceMonitor = fakeMonitors
					.get(resourceContext);

			semaphore.release();

			if (resourceMonitor != null) {
				try {
					resourceMonitor.delete();
				} catch (ResourceMonitorException e) {
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * @param resourceMonitor
	 */
	public void removeResourceMonitor(ResourceMonitor<Long> resourceMonitor) {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			return;
		}

		fakeMonitors.values().remove(resourceMonitor);

		semaphore.release();
	}

	private class Lock {

		private int	count	= 0;

		public Lock() {

		}

		public void acquire() throws InterruptedException {
			boolean got = false;
			synchronized (this) {
				while (!got) {
					if (this.count != 0) {
						this.wait();
					}
					if (this.count == 0) {
						this.count++;
						got = true;
					}
				}
			}
		}

		public void release() {
			synchronized (this) {
				this.count--;
				this.notify();
			}
		}
	}

}
