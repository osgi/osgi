package org.osgi.test.cases.resourcemanagement.junit;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemanagement.ResourceContext;
import org.osgi.service.resourcemanagement.ResourceManager;
import org.osgi.service.resourcemanagement.ResourceMonitor;
import org.osgi.service.resourcemanagement.ResourceMonitorException;
import org.osgi.service.resourcemanagement.ResourceMonitorFactory;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class ResourceMonitorTestCase extends DefaultTestBundleControl {

	/**
	 * bundle context.
	 */
	private BundleContext bundleContext;

	/**
	 * resource manager.
	 */
	private ResourceManager resourceManager;

	/**
	 * cpu factory.
	 */
	private ResourceMonitorFactory cpuFactory;

	/**
	 * resourceContext
	 */
	private ResourceContext resourceContext;

	/**
	 * resource context name
	 */
	private final String resourceContextName = "context1";

	public void setBundleContext(BundleContext context) {
		bundleContext = context;

		ServiceReference<ResourceManager> serviceReference = bundleContext
				.getServiceReference(ResourceManager.class);
		resourceManager = bundleContext.getService(serviceReference);
		
		
		StringBuffer filter = new StringBuffer();
		filter.append("(&(");
		filter.append(Constants.OBJECTCLASS);
		filter.append("=");
		filter.append(ResourceMonitorFactory.class.getName());
		filter.append(")(");
		filter.append(ResourceMonitorFactory.RESOURCE_TYPE_PROPERTY);
		filter.append("=");
		filter.append(ResourceManager.RES_TYPE_CPU);
		filter.append("))");
		try {
			Collection factoryReferences = bundleContext.getServiceReferences(
					ResourceMonitorFactory.class, filter.toString());
			if (factoryReferences != null) {
				if (factoryReferences.size() > 0) {
					ServiceReference factoryReference = (ServiceReference) factoryReferences
							.iterator().next();
					cpuFactory = bundleContext.getService(factoryReference);

				}
			}

		} catch (InvalidSyntaxException e) {
		}
	}

	protected void setUp() throws Exception {
		resourceContext = resourceManager.createContext(resourceContextName,
				null);
	}

	protected void tearDown() throws Exception {
		resourceContext.removeContext(null);
	}

	/**
	 * Check if CPU Factory is available.
	 */
	public void testCpuFactoryAvailable() {
		assertNotNull(cpuFactory);
	}

	/**
	 * Create Resource Monitor.
	 * 
	 * @throws ResourceMonitorException
	 */
	public void testCreateResourceMonitor() throws ResourceMonitorException {

		// check existing resource monitors
		ResourceMonitor[] monitors = resourceContext.getMonitors();
		assertNotNull(monitors);
		assertTrue(monitors.length == 0);
		
		// create ResourceMonitor
		ResourceMonitor resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);
		assertNotNull(resourceMonitor);
		
		// check ResourceContext
		monitors = resourceContext.getMonitors();
		assertTrue(monitors.length == 1);
		assertTrue(monitors[0].equals(resourceMonitor));

		// get monitor from ResourceContext
		ResourceMonitor retrievedMonitor = resourceContext
				.getMonitor(ResourceManager.RES_TYPE_CPU);
		assertNotNull(retrievedMonitor);
		assertTrue(retrievedMonitor.equals(resourceMonitor));

		// check resource context from monitor
		ResourceContext retrievedRC = resourceMonitor.getContext();
		assertNotNull(retrievedRC);
		assertTrue(retrievedRC.equals(resourceContext));

		// check the newly ResourceMonitor
		assertFalse(resourceMonitor.isEnabled());
		assertFalse(resourceMonitor.isDeleted());
		assertTrue(resourceMonitor.getResourceType().equals(
				cpuFactory.getType()));

		// create again a CPU Resource Monitor for this context
		boolean exception = false;
		try {
			cpuFactory.createResourceMonitor(resourceContext);
		} catch (ResourceMonitorException e) {
			exception = true;
		}
		assertTrue(exception);

		// check the ResourceContext has still one ResourceContext
		monitors = resourceContext.getMonitors();
		assertTrue(monitors.length == 1);
		assertTrue(monitors[0].equals(resourceMonitor));

	}



	/**
	 * Test deleting a ResourceMonitor. Check the monitor is removed from the
	 * context.
	 * 
	 * @throws ResourceMonitorException
	 */
	public void testDeleteResourceMonitor()
			throws ResourceMonitorException {
		// create ResourceMonitor
		ResourceMonitor resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);

		// delete ResourceMonitor
		resourceMonitor.delete();

		// check resource context monitors
		ResourceMonitor[] monitors = resourceContext.getMonitors();
		assertNotNull(monitors);
		assertTrue(monitors.length == 0);

		// check ResourceMonitor state
		assertTrue(resourceMonitor.isDeleted());
	}

	/**
	 * Test enabling/disabling a Resource Monitor
	 * 
	 * @throws ResourceMonitorException
	 */
	public void testEnableMonitor() throws ResourceMonitorException {
		// create ResourceMonitor
		ResourceMonitor resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);

		// check the monitor is disabled by default
		assertFalse(resourceMonitor.isEnabled());

		// enable it
		resourceMonitor.enable();

		// check the monitor is enabled
		assertTrue(resourceMonitor.isEnabled());

		// disable it
		resourceMonitor.disable();

		// check the monitor is disabled
		assertFalse(resourceMonitor.isEnabled());

		// delete the monitor
		resourceMonitor.delete();

		// try to enable a deleted monitor ==> expect an IllegalStateException
		boolean exception = false;
		try {
			resourceMonitor.enable();
		} catch (IllegalStateException e) {
			exception = true;
		}
		assertTrue(exception);

		// try to disable it ==> expect an IllegalStateException
		exception = false;
		try {
			resourceMonitor.disable();
		} catch (IllegalStateException e) {
			exception = true;
		}
		assertTrue(exception);

	}

	/**
	 * Test retrieving resource usage
	 * 
	 * @throws ResourceMonitorException
	 */
	public void testRetrieveUsage() throws ResourceMonitorException {
		// create ResourceMonitor
		ResourceMonitor resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);

		// retrieves the resource usage
		assertNotNull(resourceMonitor.getUsage());

		// enable it
		resourceMonitor.enable();

		// retrieves the resource usage
		assertNotNull(resourceMonitor.getUsage());

		// disable it
		resourceMonitor.disable();

		// retrieves the resource usage
		assertNotNull(resourceMonitor.getUsage());

		// delete the monitor
		resourceMonitor.delete();

		// try to enable a deleted monitor ==> expect an IllegalStateException
		boolean exception = false;
		try {
			resourceMonitor.getUsage();
		} catch (IllegalStateException e) {
			exception = true;
		}
		assertTrue(exception);


	}

}
