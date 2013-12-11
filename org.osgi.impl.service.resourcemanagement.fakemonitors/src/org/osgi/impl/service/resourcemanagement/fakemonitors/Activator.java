package org.osgi.impl.service.resourcemanagement.fakemonitors;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.resourcemanagement.ResourceManager;

public class Activator implements BundleActivator {

	/**
	 * memory factory
	 */
	private FakeMonitorFactory memoryFactory;

	/**
	 * cpu factory
	 */
	private FakeMonitorFactory cpuFactory;

	/**
	 * thread factory
	 */
	private FakeMonitorFactory threadFactory;

	/**
	 * socket factory
	 */
	private FakeMonitorFactory socketFactory;

	/**
	 * disk storage factory
	 */
	private FakeMonitorFactory diskStorageFactory;

	/**
	 * bandwidth factory
	 */
	private FakeMonitorFactory bandwidthFactory;

	/**
	 * flash factory
	 */
	private FakeMonitorFactory flashFactory;

	public void start(BundleContext context) throws Exception {
		memoryFactory = new FakeMonitorFactory(context,
				ResourceManager.RES_TYPE_MEMORY, 1000, 1000, 250000, 350000,
				1000,
				300000);
		
		cpuFactory = new FakeMonitorFactory(context,
				ResourceManager.RES_TYPE_CPU, 1000, 1000, 0, 20, 1, 10);
		
		threadFactory = new FakeMonitorFactory(context,
				ResourceManager.RES_TYPE_THREADS, 1000, 1000, 0, 2, 1, 1);
		
		socketFactory = new FakeMonitorFactory(context,
				ResourceManager.RES_TYPE_SOCKET, 1000, 1000, 0, 5, 1, 2);

		diskStorageFactory = new FakeMonitorFactory(context,
				ResourceManager.RES_TYPE_DISK_STORAGE, 1000, 1000, 0, 150000,
				1000, 100000);

		// orange otb demo
		bandwidthFactory = new FakeMonitorFactory(context,
				"resource.type.bandwidth", 1000, 1000, 0, 100000, 1, 0);

		// orange otb demo
		flashFactory = new FakeMonitorFactory(context, "resource.type.flash",
				1000, 1000, 250000, 350000, 1000, 300000);


	}

	public void stop(BundleContext context) throws Exception {
		memoryFactory.stop();
		memoryFactory = null;

		cpuFactory.stop();
		cpuFactory = null;

		threadFactory.stop();
		threadFactory = null;

		socketFactory.stop();
		socketFactory = null;

		diskStorageFactory.stop();
		diskStorageFactory = null;

		bandwidthFactory.stop();
		bandwidthFactory = null;

		flashFactory.stop();
		flashFactory = null;
	}

}
