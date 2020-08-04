
package org.osgi.impl.service.resourcemonitoring.persistency;

import org.osgi.framework.BundleContext;

/**
 *
 */
public interface Persistence {

	/**
	 * Store in the context file the provided resource contexts.
	 * <p>
	 * Persistence policies:
	 * <ul>
	 * <li>All contexts are persisted including the
	 * {@link ResourceMonitoringService#SYSTEM_CONTEXT_NAME} Resource Context
	 * and the {@link ResourceMonitoringService#FRAMEWORK_CONTEXT_NAME} Resource
	 * Context</li>
	 * <li>For each context, the following fields are persisted:
	 * <ul>
	 * <li>the Resource Context name.</li>
	 * <li>the bundle ids associated to the context</li>
	 * </ul>
	 * <li>this method should be called when the
	 * {@link ResourceMonitoringService} is stopping.</li>
	 * </ul>
	 * 
	 * @param bundleContext bundle context to be used to get a file in the
	 *        private storage area of the {@link ResourceMonitoringService}
	 *        bundle.
	 * @param resourceContextInfos resource contexts to be persisted.
	 */
	void persist(BundleContext bundleContext,
			ResourceContextInfo[] resourceContextInfos);

	/**
	 * Load from the context file the persisted resource context.
	 * 
	 * @param context the bundle context to be used to load the persisted file.
	 * @return persisted resource context
	 */
	ResourceContextInfo[] load(BundleContext context);
}
