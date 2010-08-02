package org.osgi.framework.hooks.bundle;

import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.SynchronousBundleListener;

/** 
 * OSGi Framework Bundle Event Hook Service.
 * 
 * <p>
 * Bundles registering this service will be called during framework lifecycle
 * (install, start, stop, update, and uninstall bundle) operations.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface EventHook {

	/**
	 * Bundle event hook method.  This method is called prior to bundle event
	 * delivery when a bundle is installed, resolved, started, stopped, unresolved, or
	 * uninstalled.  This method can filter the bundles which receive the event.
	 * <p>
	 * Note that this method may be called multiple times with the same bundle event.
	 * For example, it is acceptable to a framework implementation to call this 
	 * once for all bundles with at least one {@code SynchronousBundleListener} and 
	 * once for all bundles with at least one {@code BundleListener} registered.
	 * <p>
	 * @param event The bundle event to be delivered
	 * @param bundles A collection of bundles which have listeners to which the
	 *        specified event will be delivered.  The implementation of this
	 *        method may remove bundles from the collection to prevent the 
	 *        event from being delivered to the associated bundles.  The
	 *        collection supports all the optional {@code Collection}
	 *        operations except {@code add} and {@code addAll}.  Attempting
	 *        to add to the collection will result in an {@code 
	 *        UnsupportedOperationException}.  The collection is not 
	 *        synchronized.
	 */
	// TODO probably should somehow prevent a hook from hiding a bundle from itself.
	void event(BundleEvent event, Collection<Bundle> bundles);
}
