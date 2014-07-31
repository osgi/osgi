
package org.osgi.impl.service.resourcemanagement.threadmanager;

import org.osgi.service.resourcemanagement.ResourceContext;

public interface ThreadManager {

	/**
	 * <p>
	 * Start the thread manager and initializes it.
	 * </p>
	 * <p>
	 * This method registers this class as a Bundle listener and iterate over
	 * the existing bundles in order to get their classloaders.
	 * </p>
	 * 
	 * @param context context of the bundle.
	 */
	/* void start(BundleContext context); */

	/**
	 * <p>
	 * Stop the thread manager.
	 * </p>
	 * <p>
	 * Unregister the thread manager as a bundle listener.
	 * </p>
	 * 
	 * @param context
	 */
	/* void stop(BundleContext context); */

	/**
	 * Return the ResourceContext associated to the provided thread.
	 * 
	 * @param t thread
	 * @return the ResourceContext associated with this thread or null.
	 * @throws IllegalStateException if the ThreadManager has not been
	 *         previously started or has been stopped.
	 */
	ResourceContext getResourceContext(Thread t) throws IllegalStateException;

	/**
	 * Associate the thread t to the Resource Context rc
	 * 
	 * @param t thread
	 * @param rc resource context
	 */
	void switchContext(Thread t, ResourceContext rc);

}
