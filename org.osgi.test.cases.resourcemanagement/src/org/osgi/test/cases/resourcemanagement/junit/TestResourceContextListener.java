
package org.osgi.test.cases.resourcemanagement.junit;

import java.util.ArrayList;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.resourcemanagement.ResourceContextEvent;
import org.osgi.service.resourcemanagement.ResourceContextListener;

/**
 *
 */
public class TestResourceContextListener implements ResourceContextListener {

	/**
	 * service registration ServiceRegistration<ResourceContextListener>
	 */
	private ServiceRegistration	serviceRegistration;

	/**
	 * list of received events
	 */
	private final List			events;

	/**
	 * 
	 */
	public TestResourceContextListener() {
		events = new ArrayList();
	}

	/**
	 * Register this instance as a {@link ResourceContextListener} service. Once
	 * registered, this instance will receive {@link ResourceContextEvent}.
	 * 
	 * @param bundleContext
	 */
	public void start(BundleContext bundleContext) {
		serviceRegistration = bundleContext
				.registerService(
						ResourceContextListener.class.getName(),
						this, null);
	}

	/**
	 * Unregister this instance as a {@link ResourceContextListener} service.
	 * Once unregistered, this instance won't receive anymore
	 * {@link ResourceContextEvent}.
	 */
	public void stop() {
		serviceRegistration.unregister();
		events.clear();
	}

	/**
	 * this method is called when a {@link ResourceContextEvent} is sent.
	 */
	public void notify(ResourceContextEvent event) {

		// store received event
		events.add(event);

	}

	/**
	 * Returns the last received event got through
	 * {@link #notify(ResourceContextEvent)} method.
	 * 
	 * @return last received resource context event.
	 */
	public ResourceContextEvent getLastEvent() {
		int index = events.size() - 1;
		if (index < 0) {
			return null;
		} else {
			return (ResourceContextEvent) events.get(index);
		}
	}

	/**
	 * Returns the last last event got through
	 * {@link #notify(ResourceContextEvent)} method.
	 * 
	 * @return last last received event
	 */
	public ResourceContextEvent getLastLastEvent() {
		int index = events.size() - 2;
		if (index < 0) {
			return null;
		} else {
			return (ResourceContextEvent) events.get(index);
		}
	}

	/**
	 * Retrieves the received events
	 * 
	 * @return received events.
	 */
	public List getReceivedEvents() {
		return events;
	}

}
