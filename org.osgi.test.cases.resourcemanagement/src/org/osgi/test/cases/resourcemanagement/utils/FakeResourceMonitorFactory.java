
package org.osgi.test.cases.resourcemanagement.utils;

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.resourcemanagement.ResourceContext;
import org.osgi.service.resourcemanagement.ResourceContextEvent;
import org.osgi.service.resourcemanagement.ResourceContextListener;
import org.osgi.service.resourcemanagement.ResourceMonitor;
import org.osgi.service.resourcemanagement.ResourceMonitorException;
import org.osgi.service.resourcemanagement.ResourceMonitorFactory;

/**
 *
 */
public class FakeResourceMonitorFactory implements ResourceMonitorFactory,
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
	 * Register the factory as a ResourceContextListener to be informed when a
	 * ResourceContext is deleted. ServiceRegistration<ResourceContextListener>
	 */
	private ServiceRegistration	serviceRegistration;

	/**
	 * Create, and register this FakeResourceMonitorFactory as a service in the
	 * OSGi services registry.
	 * 
	 * @param bundleContext
	 * @param factoryType
	 */
	public FakeResourceMonitorFactory(BundleContext bundleContext,
			String factoryType) {
		this.bundleContext = bundleContext;
		this.factoryType = factoryType;

		// register this factory as a ResourceContextListener.
		// Dictionary<String, Object> properties.
		Dictionary properties = new Hashtable();
		properties.put(ResourceContextListener.EVENT_TYPE,
				Integer.valueOf(Integer.toString(ResourceContextEvent.RESOURCE_CONTEXT_REMOVED)));
		properties.put(ResourceMonitorFactory.RESOURCE_TYPE_PROPERTY,
				factoryType);
		serviceRegistration = this.bundleContext
				.registerService(
						new String[] {ResourceContextListener.class.getName(),
								ResourceMonitorFactory.class.getName()}, this,
						properties);
	}

	/**
	 * Unregister the factory from the OSGi services registry.
	 */
	public void stop() {
		serviceRegistration.unregister();
	}

	public String getType() {
		return factoryType;
	}

	public void notify(ResourceContextEvent event) {
		// TODO Auto-generated method stub

	}

	public ResourceMonitor createResourceMonitor(ResourceContext resourceContext) throws ResourceMonitorException {
		// TODO Auto-generated method stub
		return null;
	}

}
