
package org.osgi.impl.service.resourcemanagement.threadmanager.impl;

import org.osgi.framework.BundleContext;
import org.osgi.impl.service.resourcemanagement.threadmanager.ThreadManager;
import org.osgi.service.resourcemanagement.ResourceContext;

/**
 *
 */
public class ThreadManagerImpl implements ThreadManager {

	/**
	 * @param context
	 */
	public void start(BundleContext context) {
		// nothing to do.
	}

	/**
	 * @param context
	 */
	public void stop(BundleContext context) {
		// nothing to do.
	}

	public ResourceContext getResourceContext(Thread t)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	public void switchContext(Thread t, ResourceContext rc) {
		// TODO Auto-generated method stub

	}

}
