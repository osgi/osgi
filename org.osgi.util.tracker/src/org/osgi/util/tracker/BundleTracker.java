/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2007, 2008). All Rights Reserved.
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
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

/**
 * The <code>BundleTracker</code> class simplifies tracking bundles much like
 * the <code>ServiceTracker</code> simplifies tracking services.
 * <p>
 * A <code>BundleTracker</code> object is constructed with state criteria and a
 * <code>BundleTrackerCustomizer</code> object. A <code>BundleTracker</code>
 * object can use the <code>BundleTrackerCustomizer</code> object to select
 * which bundles are tracked and to create a customized object to be tracked
 * with the bundle. The <code>BundleTracker</code> object can then be opened to
 * begin tracking all bundles whose state matches the specified state criteria.
 * <p>
 * The <code>getBundles</code> method can be called to get the
 * <code>Bundle</code> objects of the bundles being tracked. The
 * <code>getCustomizedObject</code> method can be called to get the customized
 * object for a tracked bundle.
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
public class BundleTracker implements BundleTrackerCustomizer {
	/* set this to true to compile in debug messages */
	static final boolean			DEBUG	= false;

	/**
	 * Bundle context this <code>BundleTracker</code> object is tracking
	 * against.
	 */
	protected final BundleContext	context;

	/**
	 * <code>BundleTrackerCustomizer</code> object for this tracker.
	 */
	final BundleTrackerCustomizer	customizer;

	/**
	 * Tracked bundles: <code>Bundle</code> object -> customized Object and
	 * <code>BundleListener</code> object
	 */
	private volatile Tracked		tracked;

	/**
	 * State mask for bundles being tracked. This field contains the ORed values
	 * of the bundle states being tracked.
	 */
	final int						mask;

	/**
	 * Create a <code>BundleTracker</code> object for bundles whose state is
	 * present in the specified state mask.
	 * 
	 * <p>
	 * Bundles whose state is present on the specified state mask will be
	 * tracked by this <code>BundleTracker</code> object.
	 * 
	 * @param context <code>BundleContext</code> object against which the
	 *        tracking is done.
	 * @param stateMask A bit mask of the <code>OR</code>ing of the bundle
	 *        states to be tracked.
	 * @param customizer The customizer object to call when bundles are added,
	 *        modified, or removed in this <code>BundleTracker</code> object. If
	 *        customizer is <code>null</code>, then this
	 *        <code>BundleTracker</code> object will be used as the
	 *        <code>BundleTrackerCustomizer</code> object and the
	 *        <code>BundleTracker</code> object will call the
	 *        <code>BundleTrackerCustomizer</code> methods on itself.
	 * @see Bundle#getState()
	 */
	public BundleTracker(BundleContext context, int stateMask,
			BundleTrackerCustomizer customizer) {
		this.context = context;
		this.mask = stateMask;
		this.customizer = (customizer == null) ? this : customizer;
	}

	/**
	 * Open this <code>BundleTracker</code> object and begin tracking bundles.
	 * 
	 * <p>
	 * Bundle which match the state criteria specified when this
	 * <code>BundleTracker</code> object was created are now tracked by this
	 * <code>BundleTracker</code> object.
	 * 
	 * @throws java.lang.IllegalStateException if the <code>BundleContext</code>
	 *         object with which this <code>BundleTracker</code> object was
	 *         created is no longer valid.
	 * @throws java.lang.SecurityException If the caller and this class do not
	 *         have the appropriate
	 *         <code>AdminPermission[context bundle,LISTENER]</code>, and the
	 *         Java Runtime Environment supports permissions.
	 */
	public synchronized void open() {
		if (tracked != null) {
			return;
		}
		if (DEBUG) {
			System.out.println("BundleTracker.open"); //$NON-NLS-1$
		}
		tracked = new Tracked();

		synchronized (tracked) {
			context.addBundleListener(tracked);
			Bundle[] bundles = context.getBundles();
			if (bundles != null) {
				int length = bundles.length;
				for (int i = 0; i < length; i++) {
					int state = bundles[i].getState();
					if ((state & mask) == 0) {
						bundles[i] = null; // null out bundles whose states are
						// not interesting
					}
				}
				tracked.setInitial(bundles); // set tracked with the initial
				// bundles
			}
		}
		/* Call tracked outside of synchronized region */

		tracked.trackInitial(); // process the initial references
	}

	/**
	 * Close this <code>BundleTracker</code> object.
	 * 
	 * <p>
	 * This method should be called when this <code>BundleTracker</code> object
	 * should end the tracking of bundles.
	 */
	public synchronized void close() {
		if (tracked == null) {
			return;
		}
		if (DEBUG) {
			System.out.println("BundleTracker.close"); //$NON-NLS-1$
		}
		tracked.close();
		Bundle[] bundles = getBundles();
		Tracked outgoing = tracked;
		tracked = null;
		try {
			context.removeBundleListener(outgoing);
		}
		catch (IllegalStateException e) {
			/* In case the context was stopped. */
		}
		if (bundles != null) {
			for (int i = 0; i < bundles.length; i++) {
				outgoing.untrack(bundles[i], null);
			}
		}
	}

	/**
	 * Default implementation of the
	 * <code>BundleTrackerCustomizer.addingBundle</code> method.
	 * 
	 * <p>
	 * This method is only called when this <code>BundleTracker</code> object
	 * has been constructed with a <code>null BundleTrackerCustomizer</code>
	 * argument.
	 * 
	 * The default implementation returns the specified <code>Bundle</code>
	 * object.
	 * <p>
	 * This method can be overridden in a subclass to customize the object to be
	 * tracked for the bundle being added.
	 * 
	 * @param bundle Bundle being added to this <code>BundleTracker</code>
	 *        object.
	 * @param event The bundle event which caused this customizer method to be
	 *        called or <code>null</code> if there is no bundle event associated
	 *        with the call to this method.
	 * @return The customized object to be tracked for the bundle added to this
	 *         <code>BundleTracker</code> object.
	 * @see BundleTrackerCustomizer
	 */
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		return bundle;
	}

	/**
	 * Default implementation of the
	 * <code>BundleTrackerCustomizer.modifiedBundle</code> method.
	 * 
	 * <p>
	 * This method is only called when this <code>BundleTracker</code> object
	 * has been constructed with a <code>null BundleTrackerCustomizer</code>
	 * argument.
	 * 
	 * The default implementation does nothing.
	 * 
	 * @param bundle Bundle whose state has been modified.
	 * @param event The bundle event which caused this customizer method to be
	 *        called or <code>null</code> if there is no bundle event associated
	 *        with the call to this method.
	 * @param object The customized object for the bundle.
	 * @see BundleTrackerCustomizer
	 */
	public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
	}

	/**
	 * Default implementation of the
	 * <code>BundleTrackerCustomizer.removedBundle</code> method.
	 * 
	 * <p>
	 * This method is only called when this <code>BundleTracker</code> object
	 * has been constructed with a <code>null BundleTrackerCustomizer</code>
	 * argument.
	 * 
	 * The default implementation does nothing.
	 * 
	 * @param bundle Bundle being removed.
	 * @param event The bundle event which caused this customizer method to be
	 *        called or <code>null</code> if there is no bundle event associated
	 *        with the call to this method.
	 * @param object The customized object for the bundle.
	 * @see BundleTrackerCustomizer
	 */
	public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
	}

	/**
	 * Return an array of <code>Bundle</code> objects for all bundles being
	 * tracked by this <code>BundleTracker</code> object.
	 * 
	 * @return Array of <code>Bundle</code> objects or <code>null</code> if no
	 *         bundles are being tracked.
	 */
	public Bundle[] getBundles() {
		Tracked tracked = this.tracked; /*
										 * use local var since we are not
										 * synchronized
										 */
		if (tracked == null) /* if BundleTracker is not open */
		{
			return null;
		}
		synchronized (tracked) {
			int length = tracked.size();
			if (length == 0) {
				return null;
			}
			Bundle[] bundles = new Bundle[length];
			tracked.getTracked(bundles);
			return bundles;
		}
	}

	/**
	 * Returns the customized object for the specified <code>Bundle</code>
	 * object if the bundle is being tracked by this <code>BundleTracker</code>
	 * object.
	 * 
	 * @param bundle Bundle being tracked.
	 * @return Customized object or <code>null</code> if the specified
	 *         <code>Bundle</code> object is not being tracked.
	 */
	public Object getObject(Bundle bundle) {
		Tracked tracked = this.tracked; /*
										 * use local var since we are not
										 * synchronized
										 */
		if (tracked == null) { /* if BundleTracker is not open */
			return null;
		}
		synchronized (tracked) {
			return tracked.getCustomizedObject(bundle);
		}
	}

	/**
	 * Remove a bundle from this <code>BundleTracker</code> object.
	 * 
	 * The specified bundle will be removed from this <code>BundleTracker</code>
	 * object. If the specified bundle was being tracked then the
	 * <code>BundleTrackerCustomizer.removedBundle</code> method will be called
	 * for that bundle.
	 * 
	 * @param bundle Bundle to be removed.
	 */
	public void remove(Bundle bundle) {
		Tracked tracked = this.tracked; /*
										 * use local var since we are not
										 * synchronized
										 */
		if (tracked == null) { /* if BundleTracker is not open */
			return;
		}
		tracked.untrack(bundle, null);
	}

	/**
	 * Return the number of bundles being tracked by this
	 * <code>BundleTracker</code> object.
	 * 
	 * @return Number of bundles being tracked.
	 */
	public int size() {
		Tracked tracked = this.tracked; /*
										 * use local var since we are not
										 * synchronized
										 */
		if (tracked == null) { /* if BundleTracker is not open */
			return 0;
		}
		synchronized (tracked) {
			return tracked.size();
		}
	}

	/**
	 * Returns the tracking count for this <code>BundleTracker</code> object.
	 * 
	 * The tracking count is initialized to 0 when this
	 * <code>BundleTracker</code> object is opened. Every time a bundle is
	 * added, modified or removed from this <code>BundleTracker</code> object
	 * the tracking count is incremented.
	 * 
	 * <p>
	 * The tracking count can be used to determine if this
	 * <code>BundleTracker</code> object has added, modified or removed a bundle
	 * by comparing a tracking count value previously collected with the current
	 * tracking count value. If the value has not changed, then no bundle has
	 * been added, modified or removed from this <code>BundleTracker</code>
	 * object since the previous tracking count was collected.
	 * 
	 * @return The tracking count for this <code>BundleTracker</code> object or
	 *         -1 if this <code>BundleTracker</code> object is not open.
	 */
	public int getTrackingCount() {
		Tracked tracked = this.tracked; /*
										 * use local var since we are not
										 * synchronized
										 */
		if (tracked == null) { /* if BundleTracker is not open */
			return -1;
		}
		synchronized (tracked) {
			return tracked.getTrackingCount();
		}
	}

	/**
	 * Called by the Tracked object whenever the set of tracked bundles is
	 * modified.
	 */
	/*
	 * This method must not be synchronized since it is called by Tracked while
	 * Tracked is synchronized. We don't want synchronization interactions
	 * between the listener thread and the user thread.
	 */
	void modified() {
		if (DEBUG) {
			System.out.println("BundleTracker.modified"); //$NON-NLS-1$
		}
	}

	/**
	 * Inner class which subclasses AbstractTracked. This class is the
	 * <code>SynchronousBundleListener</code> object for the tracker.
	 * 
	 * @ThreadSafe
	 * @since 1.4
	 */
	class Tracked extends AbstractTracked implements SynchronousBundleListener {
		/**
		 * Tracked constructor.
		 */
		protected Tracked() {
			super();
		}

		/**
		 * <code>BundleListener</code> method for the <code>BundleTracker</code>
		 * class. This method must NOT be synchronized to avoid deadlock
		 * potential.
		 * 
		 * @param event <code>BundleEvent</code> object from the framework.
		 */
		public void bundleChanged(final BundleEvent event) {
			/*
			 * Check if we had a delayed call (which could happen when we
			 * close).
			 */
			if (closed) {
				return;
			}
			Bundle bundle = event.getBundle();
			int state = bundle.getState();
			if (DEBUG) {
				System.out
						.println("BundleTracker.Tracked.bundleChanged[" + state + "]: " + bundle); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if ((state & mask) != 0) {
				track(bundle, event);
				/*
				 * If the customizer throws an unchecked exception, it is safe
				 * to let it propagate
				 */
			}
			else {
				untrack(bundle, event);
				/*
				 * If the customizer throws an unchecked exception, it is safe
				 * to let it propagate
				 */
			}
		}

		/**
		 * Increment the tracking count and tell the tracker there was a
		 * modification.
		 * 
		 * @GuardedBy this
		 */
		protected void modified() {
			super.modified(); /* increment the modification count */
			BundleTracker.this.modified();
		}

		/**
		 * Call the specific customizer adding method. This method must not be
		 * called while synchronized on this object.
		 * 
		 * @param item Item to be tracked.
		 * @param related Action related object.
		 * @return Customized object for the tracked item or <code>null</code>
		 *         if the item is not to be tracked.
		 */
		protected Object customizerAdding(final Object item,
				final Object related) {
			return customizer
					.addingBundle((Bundle) item, (BundleEvent) related);
		}

		/**
		 * Call the specific customizer modified method. This method must not be
		 * called while synchronized on this object.
		 * 
		 * @param item Tracked item.
		 * @param related Action related object.
		 * @param object Customized object for the tracked item.
		 */
		protected void customizerModified(final Object item,
				final Object related, final Object object) {
			customizer.modifiedBundle((Bundle) item, (BundleEvent) related,
					object);
		}

		/**
		 * Call the specific customizer removed method. This method must not be
		 * called while synchronized on this object.
		 * 
		 * @param item Tracked item.
		 * @param related Action related object.
		 * @param object Customized object for the tracked item.
		 */
		protected void customizerRemoved(final Object item,
				final Object related, final Object object) {
			customizer.removedBundle((Bundle) item, (BundleEvent) related,
					object);
		}
	}
}
