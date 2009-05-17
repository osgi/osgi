/*
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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Framework;
import org.osgi.framework.Bundle.State;
import org.osgi.framework.bundle.SynchronousBundleListener;

/**
 * The <code>BundleTracker</code> class simplifies tracking bundles much like
 * the <code>ServiceTracker</code> simplifies tracking services.
 * <p>
 * A <code>BundleTracker</code> is constructed with state criteria and a
 * <code>BundleTrackerCustomizer</code> object. A <code>BundleTracker</code> can
 * use the <code>BundleTrackerCustomizer</code> to select which bundles are
 * tracked and to create a customized object to be tracked with the bundle. The
 * <code>BundleTracker</code> can then be opened to begin tracking all bundles
 * whose state matches the specified state criteria.
 * <p>
 * The <code>getBundles</code> method can be called to get the
 * <code>Bundle</code> objects of the bundles being tracked. The
 * <code>getObject</code> method can be called to get the customized object for
 * a tracked bundle.
 * <p>
 * The <code>BundleTracker</code> class is thread-safe. It does not call a
 * <code>BundleTrackerCustomizer</code> while holding any locks.
 * <code>BundleTrackerCustomizer</code> implementations must also be
 * thread-safe.
 * 
 * @ThreadSafe
 * @version $Revision$
 * @param <T>
 * @since 1.4
 */
public class BundleTracker<T> implements BundleTrackerCustomizer<T> {
	/* set this to true to compile in debug messages */
	static final boolean			DEBUG	= false;

	/**
	 * The Bundle Context used by this <code>BundleTracker</code>.
	 */
	protected final BundleContext	context;

	/**
	 * The <code>BundleTrackerCustomizer</code> object for this tracker.
	 */
	final BundleTrackerCustomizer<T>	customizer;

	/**
	 * Tracked bundles: <code>Bundle</code> object -> customized Object and
	 * <code>BundleListener</code> object
	 */
	private volatile Tracked		tracked;

	/**
	 * Accessor method for the current Tracked object. This method is only
	 * intended to be used by the unsynchronized methods which do not modify the
	 * tracked field.
	 * 
	 * @return The current Tracked object.
	 */
	private Tracked tracked() {
		return tracked;
	}

	/**
	 * State mask for bundles being tracked. This field contains the ORed values
	 * of the bundle states being tracked.
	 */
	final EnumSet<State>	mask;

	/**
	 * Create a <code>BundleTracker</code> for bundles whose state is present in
	 * the specified state mask.
	 * 
	 * <p>
	 * Bundles whose state is present on the specified state mask will be
	 * tracked by this <code>BundleTracker</code>.
	 * 
	 * @param context The <code>BundleContext</code> against which the tracking
	 *        is done.
	 * @param stateMask The bit mask of the <code>OR</code>ing of the bundle
	 *        states to be tracked.
	 * @param customizer The customizer object to call when bundles are added,
	 *        modified, or removed in this <code>BundleTracker</code>. If
	 *        customizer is <code>null</code>, then this
	 *        <code>BundleTracker</code> will be used as the
	 *        <code>BundleTrackerCustomizer</code> and this
	 *        <code>BundleTracker</code> will call the
	 *        <code>BundleTrackerCustomizer</code> methods on itself.
	 * @see Bundle#getState()
	 */
	public BundleTracker(BundleContext context, EnumSet<State> stateMask,
			BundleTrackerCustomizer<T> customizer) {
		this.context = context;
		this.mask = stateMask;
		this.customizer = (customizer == null) ? this : customizer;
	}

	/**
	 * Open this <code>BundleTracker</code> and begin tracking bundles.
	 * 
	 * <p>
	 * Bundle which match the state criteria specified when this
	 * <code>BundleTracker</code> was created are now tracked by this
	 * <code>BundleTracker</code>.
	 * 
	 * @throws java.lang.IllegalStateException If the <code>BundleContext</code>
	 *         with which this <code>BundleTracker</code> was created is no
	 *         longer valid.
	 * @throws java.lang.SecurityException If the caller and this class do not
	 *         have the appropriate
	 *         <code>AdminPermission[context bundle,LISTENER]</code>, and the
	 *         Java Runtime Environment supports permissions.
	 */
	public void open() {
		final Tracked t;
		synchronized (this) {
			if (tracked != null) {
				return;
			}
			if (DEBUG) {
				System.out.println("BundleTracker.open"); //$NON-NLS-1$
			}
			t = new Tracked();
			synchronized (t) {
				Framework framework = context.getFramework();
				framework.addBundleListener(t);
				List<Bundle> bundles = new ArrayList<Bundle>(framework
						.getBundles());
				for (Iterator<Bundle> i = bundles.iterator(); i.hasNext();) {
					Bundle bundle = i.next();
					State state = bundle.getState();
					if (!mask.contains(state)) {
						i.remove();
					}
				}
				/* set tracked with the initial bundles */
				t.setInitial(bundles); 
			}
			tracked = t;
		}
		/* Call tracked outside of synchronized region */
		t.trackInitial(); /* process the initial references */
	}

	/**
	 * Close this <code>BundleTracker</code>.
	 * 
	 * <p>
	 * This method should be called when this <code>BundleTracker</code> should
	 * end the tracking of bundles.
	 * 
	 * <p>
	 * This implementation calls {@link #getBundles()} to get the list of
	 * tracked bundles to remove.
	 */
	public void close() {
		final List<Bundle> bundles;
		final Tracked outgoing;
		synchronized (this) {
			outgoing = tracked;
			if (outgoing == null) {
				return;
			}
			if (DEBUG) {
				System.out.println("BundleTracker.close"); //$NON-NLS-1$
			}
			outgoing.close();
			bundles = getBundles();
			tracked = null;
			try {
				context.getFramework().removeBundleListener(outgoing);
			}
			catch (IllegalStateException e) {
				/* In case the context was stopped. */
			}
		}
		for (Bundle bundle : bundles) {
			outgoing.untrack(bundle, null);
		}
	}

	/**
	 * Default implementation of the
	 * <code>BundleTrackerCustomizer.addingBundle</code> method.
	 * 
	 * <p>
	 * This method is only called when this <code>BundleTracker</code> has been
	 * constructed with a <code>null BundleTrackerCustomizer</code> argument.
	 * 
	 * <p>
	 * This implementation simply returns the specified <code>Bundle</code>.
	 * 
	 * <p>
	 * This method can be overridden in a subclass to customize the object to be
	 * tracked for the bundle being added.
	 * 
	 * @param bundle The <code>Bundle</code> being added to this
	 *        <code>BundleTracker</code> object.
	 * @param event The bundle event which caused this customizer method to be
	 *        called or <code>null</code> if there is no bundle event associated
	 *        with the call to this method.
	 * @return The specified bundle.
	 * @see BundleTrackerCustomizer#addingBundle(Bundle, BundleEvent)
	 */
	public T addingBundle(Bundle bundle, BundleEvent event) {
		@SuppressWarnings("unchecked")
		T result = (T) bundle;
		return result;
	}

	/**
	 * Default implementation of the
	 * <code>BundleTrackerCustomizer.modifiedBundle</code> method.
	 * 
	 * <p>
	 * This method is only called when this <code>BundleTracker</code> has been
	 * constructed with a <code>null BundleTrackerCustomizer</code> argument.
	 * 
	 * <p>
	 * This implementation does nothing.
	 * 
	 * @param bundle The <code>Bundle</code> whose state has been modified.
	 * @param event The bundle event which caused this customizer method to be
	 *        called or <code>null</code> if there is no bundle event associated
	 *        with the call to this method.
	 * @param object The customized object for the specified Bundle.
	 * @see BundleTrackerCustomizer#modifiedBundle(Bundle, BundleEvent, Object)
	 */
	public void modifiedBundle(Bundle bundle, BundleEvent event, T object) {
		/* do nothing */
	}

	/**
	 * Default implementation of the
	 * <code>BundleTrackerCustomizer.removedBundle</code> method.
	 * 
	 * <p>
	 * This method is only called when this <code>BundleTracker</code> has been
	 * constructed with a <code>null BundleTrackerCustomizer</code> argument.
	 * 
	 * <p>
	 * This implementation does nothing.
	 * 
	 * @param bundle The <code>Bundle</code> being removed.
	 * @param event The bundle event which caused this customizer method to be
	 *        called or <code>null</code> if there is no bundle event associated
	 *        with the call to this method.
	 * @param object The customized object for the specified bundle.
	 * @see BundleTrackerCustomizer#removedBundle(Bundle, BundleEvent, Object)
	 */
	public void removedBundle(Bundle bundle, BundleEvent event, T object) {
		/* do nothing */
	}

	/**
	 * Return an array of <code>Bundle</code>s for all bundles being tracked by
	 * this <code>BundleTracker</code>.
	 * 
	 * @return An array of <code>Bundle</code>s or <code>null</code> if no
	 *         bundles are being tracked.
	 */
	public List<Bundle> getBundles() {
		final Tracked t = tracked();
		if (t == null) { /* if BundleTracker is not open */
			return null;
		}
		synchronized (t) {
			return t.getTracked();
		}
	}

	/**
	 * Returns the customized object for the specified <code>Bundle</code> if
	 * the specified bundle is being tracked by this <code>BundleTracker</code>.
	 * 
	 * @param bundle The <code>Bundle</code> being tracked.
	 * @return The customized object for the specified <code>Bundle</code> or
	 *         <code>null</code> if the specified <code>Bundle</code> is not
	 *         being tracked.
	 */
	public T getObject(Bundle bundle) {
		final Tracked t = tracked();
		if (t == null) { /* if BundleTracker is not open */
			return null;
		}
		synchronized (t) {
			return t.getCustomizedObject(bundle);
		}
	}

	/**
	 * Remove a bundle from this <code>BundleTracker</code>.
	 * 
	 * The specified bundle will be removed from this <code>BundleTracker</code>
	 * . If the specified bundle was being tracked then the
	 * <code>BundleTrackerCustomizer.removedBundle</code> method will be called
	 * for that bundle.
	 * 
	 * @param bundle The <code>Bundle</code> to be removed.
	 */
	public void remove(Bundle bundle) {
		final Tracked t = tracked();
		if (t == null) { /* if BundleTracker is not open */
			return;
		}
		t.untrack(bundle, null);
	}

	/**
	 * Return the number of bundles being tracked by this
	 * <code>BundleTracker</code>.
	 * 
	 * @return The number of bundles being tracked.
	 */
	public int size() {
		final Tracked t = tracked();
		if (t == null) { /* if BundleTracker is not open */
			return 0;
		}
		synchronized (t) {
			return t.size();
		}
	}

	/**
	 * Returns the tracking count for this <code>BundleTracker</code>.
	 * 
	 * The tracking count is initialized to 0 when this
	 * <code>BundleTracker</code> is opened. Every time a bundle is added,
	 * modified or removed from this <code>BundleTracker</code> the tracking
	 * count is incremented.
	 * 
	 * <p>
	 * The tracking count can be used to determine if this
	 * <code>BundleTracker</code> has added, modified or removed a bundle by
	 * comparing a tracking count value previously collected with the current
	 * tracking count value. If the value has not changed, then no bundle has
	 * been added, modified or removed from this <code>BundleTracker</code>
	 * since the previous tracking count was collected.
	 * 
	 * @return The tracking count for this <code>BundleTracker</code> or -1 if
	 *         this <code>BundleTracker</code> is not open.
	 */
	public int getTrackingCount() {
		final Tracked t = tracked();
		if (t == null) { /* if BundleTracker is not open */
			return -1;
		}
		synchronized (t) {
			return t.getTrackingCount();
		}
	}

	/**
	 * Inner class which subclasses AbstractTracked. This class is the
	 * <code>SynchronousBundleListener</code> object for the tracker.
	 * 
	 * @ThreadSafe
	 * @since 1.4
	 */
	class Tracked extends AbstractTracked<Bundle, BundleEvent, T> implements
			SynchronousBundleListener {
		/**
		 * Tracked constructor.
		 */
		Tracked() {
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
			final Bundle bundle = event.getBundle();
			final State state = bundle.getState();
			if (DEBUG) {
				System.out
						.println("BundleTracker.Tracked.bundleChanged[" + state + "]: " + bundle); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (mask.contains(state)) {
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
		 * Call the specific customizer adding method. This method must not be
		 * called while synchronized on this object.
		 * 
		 * @param item Item to be tracked.
		 * @param related Action related object.
		 * @return Customized object for the tracked item or <code>null</code>
		 *         if the item is not to be tracked.
		 */
		T customizerAdding(final Bundle item, final BundleEvent related) {
			return customizer
					.addingBundle(item, related);
		}

		/**
		 * Call the specific customizer modified method. This method must not be
		 * called while synchronized on this object.
		 * 
		 * @param item Tracked item.
		 * @param related Action related object.
		 * @param object Customized object for the tracked item.
		 */
		void customizerModified(final Bundle item, final BundleEvent related,
				final T object) {
			customizer.modifiedBundle(item, related, object);
		}

		/**
		 * Call the specific customizer removed method. This method must not be
		 * called while synchronized on this object.
		 * 
		 * @param item Tracked item.
		 * @param related Action related object.
		 * @param object Customized object for the tracked item.
		 */
		void customizerRemoved(final Bundle item, final BundleEvent related,
				final T object) {
			customizer.removedBundle(item, related, object);
		}
	}
}
