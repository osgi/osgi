/*
 * $Header$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */

package org.eclipse.osgi.util.tracker;

import org.osgi.framework.Bundle;

/**
 * The <tt>BundleTrackerCustomizer</tt> interface allows a
 * <tt>BundleTracker</tt> object to customize the bundle objects that are
 * tracked. The <tt>BundleTrackerCustomizer</tt> object is called when a
 * bundle is being added to the <tt>BundleTracker</tt> object. The
 * <tt>BundleTrackerCustomizer</tt> can then return an object for the tracked
 * bundle. The <tt>BundleTrackerCustomizer</tt> object is also called when a
 * tracked bundle has been removed from the <tt>BundleTracker</tt> object.
 * 
 * <p>
 * The methods in this interface may be called as the result of a
 * <tt>BundleEvent</tt> being received by a <tt>BundleTracker</tt> object.
 * Since <tt>BundleEvent</tt> s are synchronously received by the
 * <tt>BundleTracker</tt>, it is highly recommended that implementations of
 * these methods do not alter bundle states while being synchronized on any
 * object.
 * 
 * @version $Revision$
 */
public interface BundleTrackerCustomizer {
	/**
	 * A bundle is being added to the <tt>BundleTracker</tt> object.
	 * 
	 * <p>
	 * This method is called before a bundle which matched the search parameters
	 * of the <tt>BundleTracker</tt> object is added to it. This method should
	 * return the object to be tracked for this <tt>Bundle</tt> object. The
	 * returned object is stored in the <tt>BundleTracker</tt> object and is
	 * available from the <tt>getObject</tt> and <tt>getObjects</tt>
	 * methods.
	 * 
	 * @param bundle Bundle being added to the <tt>BundleTracker</tt> object.
	 * @return The object to be tracked for the <tt>Bundle</tt> object or
	 *         <tt>null</tt> if the <tt>Bundle</tt> object should not be
	 *         tracked.
	 */
	public abstract Object addingBundle(Bundle bundle);

	/**
	 * A bundle tracked by the <tt>BundleTracker</tt> object has been
	 * modified.
	 * 
	 * <p>
	 * This method is called when a bundle being tracked by the
	 * <tt>BundleTracker</tt> object has had its state modified.
	 * 
	 * @param bundle Bundle whose state has been modified.
	 * @param object The object for the modified bundle.
	 */
	public abstract void modifiedBundle(Bundle bundle, Object object);

	/**
	 * A bundle tracked by the <tt>BundleTracker</tt> object has been removed.
	 * 
	 * <p>
	 * This method is called after a bundle is no longer being tracked by the
	 * <tt>BundleTracker</tt> object.
	 * 
	 * @param bundle Bundle that has been removed.
	 * @param object The object for the removed bundle.
	 */
	public abstract void removedBundle(Bundle bundle, Object object);
}