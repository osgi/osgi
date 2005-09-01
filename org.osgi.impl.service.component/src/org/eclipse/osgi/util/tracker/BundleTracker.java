/*
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

import java.util.*;
import org.osgi.framework.*;

/**
 * 
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class BundleTracker implements BundleTrackerCustomizer {
	/* set this to true to compile in debug messages */
	static final boolean					DEBUG			= false;

	/**
	 * Bundle context this <tt>BundleTracker</tt> object is tracking against.
	 */
	protected final BundleContext			context;

	/**
	 * <tt>BundleTrackerCustomizer</tt> object for this tracker.
	 */
	private final BundleTrackerCustomizer	customizer;

	/**
	 * Tracked bundles: <tt>Bundle</tt> object -> customized Object and
	 * <tt>BundleListener</tt> object
	 */
	private Tracked							tracked;

	/**
	 * Modification count. This field is initialized to zero by open, set to -1
	 * by close and incremented by modified. This field is volatile since it is
	 * accessed by multiple threads.
	 */
	private volatile int					trackingCount	= -1;

	private final int						mask;

	public BundleTracker(BundleContext context, int stateMask,
			BundleTrackerCustomizer customizer) {
		this.context = context;
		this.mask = stateMask;
		this.customizer = (customizer == null) ? this : customizer;
	}

	public synchronized void open() {
		if (tracked != null) {
			return;
		}
		if (DEBUG) {
			System.out.println("BundleTracker.open"); //$NON-NLS-1$
		}
		tracked = new Tracked();
		trackingCount = 0;
		Bundle[] bundles;
		synchronized (tracked) {
			context.addBundleListener(tracked);
			bundles = context.getBundles();
		}
		/* Call tracked outside of synchronized region */
		if (bundles != null) {
			int length = bundles.length;
			for (int i = 0; i < length; i++) {
				Bundle bundle = bundles[i];
				int state = bundle.getState();
				if ((state & mask) != 0) {
					tracked.track(bundle);
				}
			}
		}
	}

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
				outgoing.untrack(bundles[i]);
			}
		}
		trackingCount = -1;
	}

	/**
	 * @param bundle
	 * @return bundle
	 * @see org.eclipse.osgi.util.tracker.BundleTrackerCustomizer#addingBundle(org.osgi.framework.Bundle)
	 */
	public Object addingBundle(Bundle bundle) {
		return bundle;
	}

	/**
	 * @param bundle
	 * @param object
	 * @see org.eclipse.osgi.util.tracker.BundleTrackerCustomizer#modifiedBundle(org.osgi.framework.Bundle,
	 *      java.lang.Object)
	 */
	public void modifiedBundle(Bundle bundle, Object object) {
	}

	/**
	 * @param bundle
	 * @param object
	 * @see org.eclipse.osgi.util.tracker.BundleTrackerCustomizer#removedBundle(org.osgi.framework.Bundle,
	 *      java.lang.Object)
	 */
	public void removedBundle(Bundle bundle, Object object) {
	}

	/**
	 * Called by the Tracked object whenever the set of tracked bundles is
	 * modified. Increments the tracking count.
	 */
	/*
	 * This method must not be synchronized since it is called by Tracked while
	 * Tracked is synchronized. We don't want synchronization interactions
	 * between the BundleListener thread and the user thread.
	 */
	private void modified() {
		trackingCount++; /* increment modification count */
		if (DEBUG) {
			System.out.println("BundleTracker.modified"); //$NON-NLS-1$
		}
	}

	/**
	 * Return an array of <tt>Bundle</tt> objects for all bundles being
	 * tracked by this <tt>BundleTracker</tt> object.
	 * 
	 * @return Array of <tt>Bundle</tt> objects or <tt>null</tt> if no
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
			Enumeration keys = tracked.keys();
			for (int i = 0; i < length; i++) {
				bundles[i] = (Bundle) keys.nextElement();
			}
			return bundles;
		}
	}

	/**
	 * Returns an object for the specified <tt>Bundle</tt> object if the
	 * bundle is being tracked by this <tt>BundleTracker</tt> object.
	 * 
	 * @param bundle Bundle.
	 * @return Object returned by <tt>BundleTrackerCustomizer.addingBundle</tt>
	 *         when the specified <tt>Bundle</tt> was added to this
	 *         <tt>BundleTracker</tt> or <tt>null</tt> if the the specified
	 *         <tt>Bundle</tt> object is not being tracked.
	 */
	public Object getObject(Bundle bundle) {
		Tracked tracked = this.tracked; /*
										 * use local var since we are not
										 * synchronized
										 */
		if (tracked == null) /* if BundleTracker is not open */
		{
			return null;
		}
		synchronized (tracked) {
			return tracked.get(bundle);
		}
	}

	/**
	 * Remove a bundle from this <tt>BundleTracker</tt> object.
	 * 
	 * The specified bundle will be removed from this <tt>BundleTracker</tt>
	 * object. If the specified bundle was being tracked then the
	 * <tt>BundleTrackerCustomizer.removedBundle</tt> method will be called
	 * for that bundle.
	 * 
	 * @param bundle Bundle to be removed.
	 */
	public void remove(Bundle bundle) {
		Tracked tracked = this.tracked; /*
										 * use local var since we are not
										 * synchronized
										 */
		if (tracked == null) /* if BundleTracker is not open */
		{
			return;
		}
		tracked.untrack(bundle);
	}

	/**
	 * Return the number of bundles being tracked by this <tt>BundleTracker</tt>
	 * object.
	 * 
	 * @return Number of bundles being tracked.
	 */
	public int size() {
		Tracked tracked = this.tracked; /*
										 * use local var since we are not
										 * synchronized
										 */
		if (tracked == null) /* if BundleTracker is not open */
		{
			return 0;
		}
		return tracked.size();
	}

	/**
	 * Returns the tracking count for this <tt>BundleTracker</tt> object.
	 * 
	 * The tracking count is initialized to 0 when this <tt>BundleTracker</tt>
	 * object is opened. Every time a bundle is added or removed from this
	 * <tt>BundleTracker</tt> object the tracking count is incremented.
	 * 
	 * <p>
	 * The tracking count can be used to determine if this
	 * <tt>BundleTracker</tt> object has added or removed a bundle by
	 * comparing a tracking count value previously collected with the current
	 * tracking count value. If the value has not changed, then no bundle has
	 * been added or removed from this <tt>BundleTracker</tt> object since the
	 * previous tracking count was collected.
	 * 
	 * @return The tracking count for this <tt>BundleTracker</tt> object or -1
	 *         if this <tt>BundleTracker</tt> object is not open.
	 */
	public int getTrackingCount() {
		return trackingCount;
	}

	/**
	 * Inner class to track bundles. If a <tt>BundleTracker</tt> object is
	 * reused (closed then reopened), then a new Tracked object is used. This
	 * class is a hashtable mapping <tt>Bundle</tt> object -> customized
	 * Object. This class is the <tt>BundleListener</tt> object for the
	 * tracker. This class is used to synchronize access to the tracked bundles.
	 * This is not a public class. It is only for use by the implementation of
	 * the <tt>BundleTracker</tt> class.
	 * 
	 */
	class Tracked extends Hashtable implements SynchronousBundleListener {
		/**
		 * List of Bundles in the process of being added.
		 */
		private List				adding;
		/**
		 * true if the tracked object is closed. This field is volatile because
		 * it is set by one thread and read by another.
		 */
		private volatile boolean	closed;

		/**
		 * Tracked constructor.
		 */
		protected Tracked() {
			super();
			closed = false;
			adding = new ArrayList(6);
		}

		/**
		 * Called by the owning <tt>BundleTracker</tt> object when it is
		 * closed.
		 */
		protected void close() {
			closed = true;
		}

		/**
		 * <tt>SynchronousBundleListener</tt> method for the
		 * <tt>BundleTracker</tt> class. This method must NOT be synchronized
		 * to avoid deadlock potential.
		 * 
		 * @param event <tt>BundleEvent</tt> object from the framework.
		 */
		public void bundleChanged(BundleEvent event) {
			/*
			 * Check if we had a delayed call (which could happen when we
			 * close).
			 */
			if (closed) {
				return;
			}
			Bundle bundle = event.getBundle();
			int state = bundle.getState();
			if ((state & mask) != 0) {
				track(bundle);
				/*
				 * If the customizer throws an unchecked exception, it is safe
				 * to let it propagate
				 */
			}
			else {
				untrack(bundle);
				/*
				 * If the customizer throws an unchecked exception, it is safe
				 * to let it propagate
				 */
			}
		}

		/**
		 * Begin to track the referenced bundle.
		 * 
		 * @param bundle Bundle to be tracked.
		 */
		protected void track(Bundle bundle) {
			Object object;
			synchronized (this) {
				object = this.get(bundle);
			}
			if (object != null) /* we are already tracking the bundle */
			{
				if (DEBUG) {
					System.out
							.println("BundleTracker.Tracked.track[modified]: " + bundle); //$NON-NLS-1$
				}
				/* Call customizer outside of synchronized region */
				customizer.modifiedBundle(bundle, object);
				/*
				 * If the customizer throws an unchecked exception, it is safe
				 * to let it propagate
				 */

				return;
			}
			synchronized (this) {
				if (adding.contains(bundle)) /*
												 * if this bundle is already in
												 * the process of being added.
												 */
				{
					if (DEBUG) {
						System.out
								.println("BundleTracker.Tracked.track[already adding]: " + bundle); //$NON-NLS-1$
					}
					return;
				}
				adding.add(bundle); /* mark this bundle is being added */
			}
			if (DEBUG) {
				System.out
						.println("BundleTracker.Tracked.track[adding]: " + bundle); //$NON-NLS-1$
			}
			boolean becameUntracked = false;
			/* Call customizer outside of synchronized region */
			try {
				object = customizer.addingBundle(bundle);
				/*
				 * If the customizer throws an unchecked exception, it will
				 * propagate after the finally
				 */
			}
			finally {
				synchronized (this) {
					if (adding.remove(bundle)) /*
												 * if the bundle was not
												 * untracked during the
												 * customizer callback
												 */
					{
						if (object != null) {
							this.put(bundle, object);
							modified(); /* increment modification count */
							notifyAll();
						}
					}
					else {
						becameUntracked = true;
					}
				}
			}
			/*
			 * The bundle became untracked during the customizer callback.
			 */
			if (becameUntracked) {
				if (DEBUG) {
					System.out
							.println("BundleTracker.Tracked.track[removed]: " + bundle); //$NON-NLS-1$
				}
				/* Call customizer outside of synchronized region */
				customizer.removedBundle(bundle, object);
				/*
				 * If the customizer throws an unchecked exception, it is safe
				 * to let it propagate
				 */
			}
		}

		/**
		 * Discontinue tracking the bundle.
		 * 
		 * @param bundle Bundle to be untracked.
		 */
		protected void untrack(Bundle bundle) {
			Object object;
			synchronized (this) {
				if (adding.remove(bundle)) /*
											 * if the bundle is in the process
											 * of being added
											 */
				{
					if (DEBUG) {
						System.out
								.println("BundleTracker.Tracked.untrack[being added]: " + bundle); //$NON-NLS-1$
					}
					return; /*
							 * in case the bundle is untracked while in the
							 * process of adding
							 */
				}
				object = this.remove(bundle); /*
												 * must remove from tracker
												 * before calling customizer
												 * callback
												 */
				if (object == null) /* are we actually tracking the bundle */
				{
					return;
				}
				modified(); /* increment modification count */
			}
			if (DEBUG) {
				System.out
						.println("BundleTracker.Tracked.untrack[removed]: " + bundle); //$NON-NLS-1$
			}
			/* Call customizer outside of synchronized region */
			customizer.removedBundle(bundle, object);
			/*
			 * If the customizer throws an unchecked exception, it is safe to
			 * let it propagate
			 */
		}
	}
}
