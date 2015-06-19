
package org.osgi.impl.service.resourcemonitoring;

import org.osgi.framework.BundleContext;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;

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
