
package org.osgi.impl.service.resourcemonitoring.fakemonitors;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;

/**
 *
 */
public class Activator implements BundleActivator {

	/**
	 * memory factory
	 */
	private FakeResourceMonitorFactory	memoryFactory;

	/**
	 * cpu factory
	 */
	private FakeResourceMonitorFactory	cpuFactory;

	/**
	 * thread factory
	 */
	private FakeResourceMonitorFactory	threadFactory;

	/**
	 * socket factory
	 */
	private FakeResourceMonitorFactory	socketFactory;

	/**
	 * disk storage factory
	 */
	private FakeResourceMonitorFactory	diskStorageFactory;

	/**
	 * bandwidth factory
	 */
	private FakeResourceMonitorFactory	bandwidthFactory;

	/**
	 * flash factory
	 */
	private FakeResourceMonitorFactory	flashFactory;

	@Override
	public void start(BundleContext context) throws Exception {
		memoryFactory = new FakeResourceMonitorFactory(context,
				ResourceMonitoringService.RES_TYPE_MEMORY, 1000, 1000, 250000,
				350000,
				1000,
				300000);

		cpuFactory = new FakeResourceMonitorFactory(context,
				ResourceMonitoringService.RES_TYPE_CPU, 1000, 1000, 0, 20, 1, 10);

		threadFactory = new FakeResourceMonitorFactory(context,
				ResourceMonitoringService.RES_TYPE_THREADS, 1000, 1000, 0, 2, 1, 1);

		socketFactory = new FakeResourceMonitorFactory(context,
				ResourceMonitoringService.RES_TYPE_SOCKET, 1000, 1000, 0, 5, 1, 2);

		// diskStorageFactory = new FakeResourceMonitorFactory(context,
		// ResourceMonitoringService.RES_TYPE_DISK_STORAGE, 1000, 1000, 0,
		// 150000,
		// 1000, 100000);

		// orange otb demo
		bandwidthFactory = new FakeResourceMonitorFactory(context,
				"resource.type.bandwidth", 1000, 1000, 0, 100000, 1, 0);

		// orange otb demo
		flashFactory = new FakeResourceMonitorFactory(context,
				"resource.type.flash",
				1000, 1000, 250000, 350000, 1000, 300000);

	}

	@Override
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
