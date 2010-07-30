package org.osgi.framework.hooks.bundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

/** 
 * @ThreadSafe
 * @version $Id$
 */
public interface BundleHook {

	/**
	 * Determines if the specified source bundle is visible to the specified target bundle. 
	 * This method is called by the framework for two different operations:
	 * <ul>
	 * <li> When a source {@link BundleEvent} is being delivered to a target {@link BundleListener}.
	 * <li> When a target {@link BundleContext} is calling {@link BundleContext#getBundles() getBundles()}
	 *      or {@link BundleContext#getBundle(long) getBundle(long)} methods to obtain source bundles.
	 * </ul>
	 * Removing a source bundle will prevent the target bundle from accessing it with a bundle event
	 * or the target's bundle context.
	 * <p>
	 * 
	 * @param client a bundle trying to get access to a source bundle
	 * @param source a bundle which a target bundle wants access to
	 * @return true if the source bundle is visible to the target bundle
	 */
	boolean isVisible(Bundle client, Bundle source);
}
