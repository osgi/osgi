/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2007). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.util.tracker;

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
 * Since <tt>BundleEvent</tt>s may be received synchronously by the
 * <tt>BundleTracker</tt>, it is highly recommended that implementations of
 * these methods do not alter bundle states while being synchronized on any
 * object.
 * 
 * <p>
 * The <code>BundleTracker</code> class is thread-safe. It does not call a
 * <code>BundleTrackerCustomizer</code> object while holding any locks.
 * <code>BundleTrackerCustomizer</code> implementations must also be
 * thread-safe.
 * 
 * @ThreadSafe
 * @version $Revision$
 * @since 1.4
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
	 * available from the {@link BundleTracker#getBundles() getBundles} method.
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
	 * @param object The tracked object for the modified bundle.
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
	 * @param object The tracked object for the removed bundle.
	 */
	public abstract void removedBundle(Bundle bundle, Object object);
}
