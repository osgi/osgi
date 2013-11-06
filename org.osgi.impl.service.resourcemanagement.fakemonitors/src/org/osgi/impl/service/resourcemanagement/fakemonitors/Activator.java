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

	public void start(BundleContext context) throws Exception {
		memoryFactory = new FakeMonitorFactory(context,
				ResourceManager.RES_TYPE_MEMORY, 1000, 1000, 0, 50000000);
		
		cpuFactory = new FakeMonitorFactory(context,
				ResourceManager.RES_TYPE_CPU, 1000, 1000, 0, 100);
		
		threadFactory = new FakeMonitorFactory(context,
				ResourceManager.RES_TYPE_THREADS, 1000, 1000, 0, 25);
		
		socketFactory = new FakeMonitorFactory(context,
				ResourceManager.RES_TYPE_SOCKET, 1000, 1000, 0, 25);

		diskStorageFactory = new FakeMonitorFactory(context,
				ResourceManager.RES_TYPE_DISK_STORAGE, 1000, 1000, 0, 10000000);
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
	}

}
