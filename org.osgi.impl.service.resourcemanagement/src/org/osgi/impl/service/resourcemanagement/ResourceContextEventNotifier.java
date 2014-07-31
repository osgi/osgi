
package org.osgi.impl.service.resourcemanagement;

import org.osgi.framework.BundleContext;
import org.osgi.service.resourcemanagement.ResourceContextEvent;

/**
 *
 */
public interface ResourceContextEventNotifier {

	/**
	 * @param context
	 */
	public void start(BundleContext context);

	/**
	 * @param context
	 */
	public void stop(BundleContext context);

	/**
	 * @param event
	 */
	public void notify(ResourceContextEvent event);
}
