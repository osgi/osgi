package org.osgi.impl.service.resourcemanagement.fakemonitors;

import org.osgi.framework.BundleContext;
import org.osgi.impl.service.resourcemanagement.util.EventNotifier;
import org.osgi.service.resourcemanagement.ResourceContext;
import org.osgi.service.resourcemanagement.ResourceListener;
import org.osgi.service.resourcemanagement.ResourceMonitor;
import org.osgi.service.resourcemanagement.ResourceMonitorException;

public class FakeMonitor implements ResourceMonitor, Runnable {

	/**
	 * {@link ResourceContext} associated with this monitor.
	 */
	private final ResourceContext resourceContext;

	/**
	 * Type of resource this {@link ResourceMonitor} is able to monitor
	 */
	private final String resourceType;

	/**
	 * report if this {@link ResourceMonitor} is enabled or not. by default, the
	 * {@link ResourceMonitor} is disabled.
	 */
	private boolean isEnable = false;

	/**
	 * report if this {@link ResourceMonitor} has been deleted.
	 */
	private boolean isDeleted = false;

	/**
	 * sampling period.
	 */
	private final long samplingPeriod;

	/**
	 * monitoring period.
	 */
	private final long monitoringPeriod;

	/**
	 * min value. This value is used for simulating the monitored value.
	 */
	private final long minValue;

	/**
	 * max value.
	 */
	private final long maxValue;

	/**
	 * thread simulating monitoring
	 */
	private Thread thread;

	/**
	 * used to send Event to {@link ResourceListener}
	 */
	private final EventNotifier eventNotifier;

	/**
	 * Factory of this ResourceMonitor
	 */
	private final FakeMonitorFactory factory;


	/**
	 * currentUsage
	 */
	private Long currentUsage = new Long(0);

	/**
	 * Create a new FakeMonitor
	 * 
	 * @param pFactory
	 *            factory
	 * @param pResourceContext
	 *            Resource Context associated with this instance of monitor.
	 * @param pResourceType
	 *            Type of Resource this monitor is able to monitor.
	 * @param pSamplingPeriod
	 *            sampling period
	 * @param pMonitoringPeriod
	 *            monitoring period
	 * @param pBundleContext
	 *            bundle context
	 */
	public FakeMonitor(FakeMonitorFactory pFactory,
			ResourceContext pResourceContext, String pResourceType,
			long pSamplingPeriod, long pMonitoringPeriod, long pMinValue,
			long pMaxValue, BundleContext pBundleContext) {
		factory = pFactory;
		resourceContext = pResourceContext;
		resourceType = pResourceType;
		samplingPeriod = pSamplingPeriod;
		monitoringPeriod = pMonitoringPeriod;
		minValue = pMinValue;
		maxValue = pMaxValue;

		eventNotifier = new EventNotifier(resourceType, resourceContext,
				pBundleContext);
		eventNotifier.start();

		try {
			resourceContext.addResourceMonitor(this);
		} catch (ResourceMonitorException e) {
		}

	}


	public ResourceContext getContext() {
		return resourceContext;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void delete() {
		isDeleted = true;

		resourceContext.removeResourceMonitor(this);
		factory.removeResourceMonitor(this);
		eventNotifier.stop();
	}

	public boolean isEnabled() {
		return isEnable;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void enable() throws ResourceMonitorException, IllegalStateException {
		checkExistency("This monitor has been deleted, it can not be enabled anymore.");

		if (!isEnable) {
			isEnable = true;
			thread = new Thread(this);
			thread.start();
			eventNotifier.reportEnableDisable();
		}



	}

	public void disable() throws IllegalStateException {
		checkExistency("This monitor has been deleted, it can not be disabled anymore.");
		isEnable = false;
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread = null;
		currentUsage = new Long(0);
		eventNotifier.reportEnableDisable();
	}

	public Comparable getUsage() throws IllegalStateException {
		checkExistency("This monitor has been deleted");
		return currentUsage;
	}

	public long getSamplingPeriod() {
		return 0;
	}

	public long getMonitoredPeriod() {
		return 0;
	}

	public void notifyIncomingThread(Thread t) {
	}

	public void notifyOutgoingThread(Thread t) {
	}

	public void run() {
		while(isEnable) {
			
			// generate a new value of consumption
			double random = Math.random();
			// random is double between 0.0 and 1.0
			long usage = (long) (random * maxValue);
			setUsage(usage);
			
			// wait for MONITORING period of time
			try {
				Thread.sleep(monitoringPeriod);
			} catch (InterruptedException e) {
				// nothing to catch
			}
		}

	}

	/**
	 * Set new usage value and notifies all {@link ResourceListener}
	 * 
	 * @param pNewUsage
	 */
	private void setUsage(long pNewUsage) {
		currentUsage = new Long(pNewUsage);
		eventNotifier.notify(currentUsage);
	}

	/**
	 * Check if this instance is still existing (i.e {@link ResourceMonitor#i}
	 * 
	 * @param msg
	 */
	private void checkExistency(String msg) {
		if (isDeleted) {
			throw new IllegalStateException(msg);
		}
	}

}
