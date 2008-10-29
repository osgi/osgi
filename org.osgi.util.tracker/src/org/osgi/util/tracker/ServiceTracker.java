/*
 * Copyright (c) OSGi Alliance (2000, 2008). All Rights Reserved.
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

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.framework.AllServiceListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

/**
 * The <code>ServiceTracker</code> class simplifies using services from the
 * Framework's service registry.
 * <p>
 * A <code>ServiceTracker</code> object is constructed with search criteria and
 * a <code>ServiceTrackerCustomizer</code> object. A <code>ServiceTracker</code>
 * object can use the <code>ServiceTrackerCustomizer</code> object to customize
 * the service objects to be tracked. The <code>ServiceTracker</code> object can
 * then be opened to begin tracking all services in the Framework's service
 * registry that match the specified search criteria. The
 * <code>ServiceTracker</code> object correctly handles all of the details of
 * listening to <code>ServiceEvent</code> objects and getting and ungetting
 * services.
 * <p>
 * The <code>getServiceReferences</code> method can be called to get references
 * to the services being tracked. The <code>getService</code> and
 * <code>getServices</code> methods can be called to get the service objects for
 * the tracked service.
 * <p>
 * The <code>ServiceTracker</code> class is thread-safe. It does not call a
 * <code>ServiceTrackerCustomizer</code> object while holding any locks.
 * <code>ServiceTrackerCustomizer</code> implementations must also be
 * thread-safe.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public class ServiceTracker implements ServiceTrackerCustomizer {
	/* set this to true to compile in debug messages */
	static final boolean				DEBUG			= false;
	/**
	 * Bundle context against which this <code>ServiceTracker</code> object is
	 * tracking.
	 */
	protected final BundleContext		context;
	/**
	 * Filter specifying search criteria for the services to track.
	 * 
	 * @since 1.1
	 */
	protected final Filter				filter;
	/**
	 * <code>ServiceTrackerCustomizer</code> object for this tracker.
	 */
	final ServiceTrackerCustomizer		customizer;
	/**
	 * Filter string for use when adding the ServiceListener. If this field is
	 * set, then certain optimizations can be taken since we don't have a user
	 * supplied filter.
	 */
	final String						listenerFilter;
	/**
	 * Class name to be tracked. If this field is set, then we are tracking by
	 * class name.
	 */
	private final String				trackClass;
	/**
	 * Reference to be tracked. If this field is set, then we are tracking a
	 * single ServiceReference.
	 */
	private final ServiceReference		trackReference;
	/**
	 * Tracked services: <code>ServiceReference</code> object -> customized
	 * Object and <code>ServiceListener</code> object
	 */
	private volatile Tracked			tracked;
	/**
	 * Cached ServiceReference for getServiceReference.
	 * 
	 * This field is volatile since it is accessed by multiple threads.
	 */
	private volatile ServiceReference	cachedReference;
	/**
	 * Cached service object for getService.
	 * 
	 * This field is volatile since it is accessed by multiple threads.
	 */
	private volatile Object				cachedService;

	/**
	 * org.osgi.framework package version which introduced
	 * {@link ServiceEvent#MODIFIED_ENDMATCH}
	 */
	private static final Version		endMatchVersion	= new Version(1, 5, 0);

	/**
	 * Create a <code>ServiceTracker</code> object on the specified
	 * <code>ServiceReference</code> object.
	 * 
	 * <p>
	 * The service referenced by the specified <code>ServiceReference</code>
	 * object will be tracked by this <code>ServiceTracker</code> object.
	 * 
	 * @param context <code>BundleContext</code> object against which the
	 *        tracking is done.
	 * @param reference <code>ServiceReference</code> object for the service to
	 *        be tracked.
	 * @param customizer The customizer object to call when services are added,
	 *        modified, or removed in this <code>ServiceTracker</code> object.
	 *        If customizer is <code>null</code>, then this
	 *        <code>ServiceTracker</code> object will be used as the
	 *        <code>ServiceTrackerCustomizer</code> object and the
	 *        <code>ServiceTracker</code> object will call the
	 *        <code>ServiceTrackerCustomizer</code> methods on itself.
	 */
	public ServiceTracker(final BundleContext context,
			final ServiceReference reference,
			final ServiceTrackerCustomizer customizer) {
		this.context = context;
		this.trackReference = reference;
		this.trackClass = null;
		this.customizer = (customizer == null) ? this : customizer;
		this.listenerFilter = "(" + Constants.SERVICE_ID + "=" + reference.getProperty(Constants.SERVICE_ID).toString() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		try {
			this.filter = context.createFilter(listenerFilter);
		}
		catch (InvalidSyntaxException e) { // we could only get this exception
			// if the ServiceReference was
			// invalid
			throw new IllegalArgumentException(
					"unexpected InvalidSyntaxException: " + e.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Create a <code>ServiceTracker</code> object on the specified class name.
	 * 
	 * <p>
	 * Services registered under the specified class name will be tracked by
	 * this <code>ServiceTracker</code> object.
	 * 
	 * @param context <code>BundleContext</code> object against which the
	 *        tracking is done.
	 * @param clazz Class name of the services to be tracked.
	 * @param customizer The customizer object to call when services are added,
	 *        modified, or removed in this <code>ServiceTracker</code> object.
	 *        If customizer is <code>null</code>, then this
	 *        <code>ServiceTracker</code> object will be used as the
	 *        <code>ServiceTrackerCustomizer</code> object and the
	 *        <code>ServiceTracker</code> object will call the
	 *        <code>ServiceTrackerCustomizer</code> methods on itself.
	 */
	public ServiceTracker(final BundleContext context, final String clazz,
			final ServiceTrackerCustomizer customizer) {
		this.context = context;
		this.trackReference = null;
		this.trackClass = clazz;
		this.customizer = (customizer == null) ? this : customizer;
		this.listenerFilter = "(" + Constants.OBJECTCLASS + "=" + clazz + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		try {
			this.filter = context.createFilter(listenerFilter);
		}
		catch (InvalidSyntaxException e) { // we could only get this exception
			// if the clazz argument was
			// malformed
			throw new IllegalArgumentException(
					"unexpected InvalidSyntaxException: " + e.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Create a <code>ServiceTracker</code> object on the specified
	 * <code>Filter</code> object.
	 * 
	 * <p>
	 * Services which match the specified <code>Filter</code> object will be
	 * tracked by this <code>ServiceTracker</code> object.
	 * 
	 * @param context <code>BundleContext</code> object against which the
	 *        tracking is done.
	 * @param filter <code>Filter</code> object to select the services to be
	 *        tracked.
	 * @param customizer The customizer object to call when services are added,
	 *        modified, or removed in this <code>ServiceTracker</code> object.
	 *        If customizer is null, then this <code>ServiceTracker</code>
	 *        object will be used as the <code>ServiceTrackerCustomizer</code>
	 *        object and the <code>ServiceTracker</code> object will call the
	 *        <code>ServiceTrackerCustomizer</code> methods on itself.
	 * @since 1.1
	 */
	public ServiceTracker(final BundleContext context, final Filter filter,
			final ServiceTrackerCustomizer customizer) {
		this.context = context;
		this.trackReference = null;
		this.trackClass = null;
		final Version frameworkVersion = (Version) AccessController
				.doPrivileged(new PrivilegedAction() {
					public Object run() {
						String version = context
								.getProperty(Constants.FRAMEWORK_VERSION);
						return (version == null) ? Version.emptyVersion
								: new Version(version);
					}
				});
		final boolean endMatchSupported = (frameworkVersion
				.compareTo(endMatchVersion) >= 0);
		this.listenerFilter = endMatchSupported ? filter.toString() : null;
		this.filter = filter;
		this.customizer = (customizer == null) ? this : customizer;
		if ((context == null) || (filter == null)) { // we throw a NPE here
			// to
			// be consistent with the
			// other constructors
			throw new NullPointerException();
		}
	}

	/**
	 * Open this <code>ServiceTracker</code> object and begin tracking services.
	 * 
	 * <p>
	 * This implementation calls <code>open(false)</code>.
	 * 
	 * @throws java.lang.IllegalStateException if the <code>BundleContext</code>
	 *         object with which this <code>ServiceTracker</code> object was
	 *         created is no longer valid.
	 * @see #open(boolean)
	 */
	public void open() {
		open(false);
	}

	/**
	 * Open this <code>ServiceTracker</code> object and begin tracking services.
	 * 
	 * <p>
	 * Services which match the search criteria specified when this
	 * <code>ServiceTracker</code> object was created are now tracked by this
	 * <code>ServiceTracker</code> object.
	 * 
	 * @param trackAllServices If <code>true</code>, then this
	 *        <code>ServiceTracker</code> will track all matching services
	 *        regardless of class loader accessibility. If <code>false</code>,
	 *        then this <code>ServiceTracker</code> will only track matching
	 *        services which are class loader accessible to the bundle whose
	 *        <code>BundleContext</code> is used by this
	 *        <code>ServiceTracker</code>.
	 * @throws java.lang.IllegalStateException if the <code>BundleContext</code>
	 *         object with which this <code>ServiceTracker</code> object was
	 *         created is no longer valid.
	 * @since 1.3
	 */
	public void open(boolean trackAllServices) {
		final Tracked t;
		synchronized (this) {
			if (tracked != null) {
				return;
			}
			if (DEBUG) {
				System.out.println("ServiceTracker.open: " + filter); //$NON-NLS-1$
			}
			t = trackAllServices ? new AllTracked() : new Tracked();
			synchronized (t) {
				try {
					context.addServiceListener(t, listenerFilter);
					ServiceReference[] references;
					if (trackClass != null) {
						references = getInitialReferences(trackAllServices,
								trackClass, null);
					}
					else {
						if (trackReference != null) {
							references = new ServiceReference[] {trackReference};
						}
						else { /* user supplied filter */
							references = getInitialReferences(trackAllServices,
									null,
									(listenerFilter != null) ? listenerFilter
											: filter.toString());
						}
					}
					
					/* set tracked with the initial references */
					t.setInitial(references); 
				}
				catch (InvalidSyntaxException e) {
					throw new RuntimeException(
							"unexpected InvalidSyntaxException: " + e.getMessage()); //$NON-NLS-1$
				}
			}
			tracked = t;
		}
		/* Call tracked outside of synchronized region */
		t.trackInitial(); /* process the initial references */
	}

	/**
	 * Returns the list of initial <code>ServiceReference</code> objects that
	 * will be tracked by this <code>ServiceTracker</code> object.
	 * 
	 * @param trackAllServices If true, use getAllServiceReferences.
	 * @param className the class name with which the service was registered,
	 *        or null for all services.
	 * @param filterString the filter criteria or null for all services.
	 * @return the list of initial <code>ServiceReference</code> objects.
	 * @throws InvalidSyntaxException if the filter uses an invalid syntax.
	 */
	private ServiceReference[] getInitialReferences(boolean trackAllServices,
			String className, String filterString)
			throws InvalidSyntaxException {
		if (trackAllServices) {
			return context.getAllServiceReferences(className, filterString);
		}
		else {
			return context.getServiceReferences(className, filterString);
		}
	}

	/**
	 * Close this <code>ServiceTracker</code> object.
	 * 
	 * <p>
	 * This method should be called when this <code>ServiceTracker</code> object
	 * should end the tracking of services.
	 * 
	 * <p>
	 * This implementation calls {@link #getServiceReferences()} to get the list
	 * of tracked services to remove.
	 */
	public void close() {
		final Tracked outgoing;
		final ServiceReference[] references;
		synchronized (this) {
			outgoing = tracked;
			if (outgoing == null) {
				return;
			}
			if (DEBUG) {
				System.out.println("ServiceTracker.close: " + filter); //$NON-NLS-1$
			}
			outgoing.close();
			references = getServiceReferences();
			tracked = null;
			try {
				context.removeServiceListener(outgoing);
			}
			catch (IllegalStateException e) {
				/* In case the context was stopped. */
			}
		}
		modified(); /* clear the cache */
		synchronized (outgoing) {
			outgoing.notifyAll(); /* wake up any waiters */
		}
		if (references != null) {
			for (int i = 0; i < references.length; i++) {
				outgoing.untrack(references[i], null);
			}
		}
		if (DEBUG) {
			if ((cachedReference == null) && (cachedService == null)) {
				System.out
						.println("ServiceTracker.close[cached cleared]: " + filter); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Default implementation of the
	 * <code>ServiceTrackerCustomizer.addingService</code> method.
	 * 
	 * <p>
	 * This method is only called when this <code>ServiceTracker</code> object
	 * has been constructed with a <code>null ServiceTrackerCustomizer</code>
	 * argument. The default implementation returns the result of calling
	 * <code>getService</code>, on the <code>BundleContext</code> object with
	 * which this <code>ServiceTracker</code> object was created, passing the
	 * specified <code>ServiceReference</code> object.
	 * <p>
	 * This method can be overridden in a subclass to customize the service
	 * object to be tracked for the service being added. In that case, take care
	 * not to rely on the default implementation of removedService that will
	 * unget the service.
	 * 
	 * @param reference Reference to service being added to this
	 *        <code>ServiceTracker</code> object.
	 * @return The service object to be tracked for the service added to this
	 *         <code>ServiceTracker</code> object.
	 * @see ServiceTrackerCustomizer
	 */
	public Object addingService(ServiceReference reference) {
		return context.getService(reference);
	}

	/**
	 * Default implementation of the
	 * <code>ServiceTrackerCustomizer.modifiedService</code> method.
	 * 
	 * <p>
	 * This method is only called when this <code>ServiceTracker</code> object
	 * has been constructed with a <code>null ServiceTrackerCustomizer</code>
	 * argument. The default implementation does nothing.
	 * 
	 * @param reference Reference to modified service.
	 * @param service The service object for the modified service.
	 * @see ServiceTrackerCustomizer
	 */
	public void modifiedService(ServiceReference reference, Object service) {
	}

	/**
	 * Default implementation of the
	 * <code>ServiceTrackerCustomizer.removedService</code> method.
	 * 
	 * <p>
	 * This method is only called when this <code>ServiceTracker</code> object
	 * has been constructed with a <code>null ServiceTrackerCustomizer</code>
	 * argument. The default implementation calls <code>ungetService</code>, on
	 * the <code>BundleContext</code> object with which this
	 * <code>ServiceTracker</code> object was created, passing the specified
	 * <code>ServiceReference</code> object.
	 * <p>
	 * This method can be overridden in a subclass. If the default
	 * implementation of <code>addingService</code> method was used, this method
	 * must unget the service.
	 * 
	 * @param reference Reference to removed service.
	 * @param service The service object for the removed service.
	 * @see ServiceTrackerCustomizer
	 */
	public void removedService(ServiceReference reference, Object service) {
		context.ungetService(reference);
	}

	/**
	 * Wait for at least one service to be tracked by this
	 * <code>ServiceTracker</code>. This method will also return when this
	 * <code>ServiceTracker</code> is closed.
	 * 
	 * <p>
	 * It is strongly recommended that <code>waitForService</code> is not used
	 * during the calling of the <code>BundleActivator</code> methods.
	 * <code>BundleActivator</code> methods are expected to complete in a short
	 * period of time.
	 * 
	 * <p>
	 * This implementation calls {@link #getService()} to determine if a service
	 * is being tracked.
	 * 
	 * @param timeout time interval in milliseconds to wait. If zero, the method
	 *        will wait indefinitely.
	 * @return Returns the result of {@link #getService()}.
	 * @throws InterruptedException If another thread has interrupted the
	 *         current thread.
	 * @throws IllegalArgumentException If the value of timeout is negative.
	 */
	public Object waitForService(long timeout) throws InterruptedException {
		if (timeout < 0) {
			throw new IllegalArgumentException("timeout value is negative"); //$NON-NLS-1$
		}
		Object object = getService(); 
		while (object == null) {
			final Tracked t = tracked; /*
										 * use local var since we are not
										 * synchronized
										 */
			if (t == null) { /* if ServiceTracker is not open */
				return null;
			}
			synchronized (t) {
				if (t.size() == 0) {
					t.wait(timeout);
				}
			}
			object = getService(); 
			if (timeout > 0) {
				return object;
			}
		}
		return object;
	}

	/**
	 * Return an array of <code>ServiceReference</code> objects for all services
	 * being tracked by this <code>ServiceTracker</code>.
	 * 
	 * @return Array of <code>ServiceReference</code> objects or
	 *         <code>null</code> if no service are being tracked.
	 */
	public ServiceReference[] getServiceReferences() {
		final Tracked t = tracked; /*
									 * use local var since we are not
									 * synchronized
									 */
		if (t == null) { /* if ServiceTracker is not open */
			return null;
		}
		synchronized (t) {
			int length = t.size();
			if (length == 0) {
				return null;
			}

			return (ServiceReference[]) t
					.getTracked(new ServiceReference[length]);
		}
	}

	/**
	 * Returns a <code>ServiceReference</code> object for one of the services
	 * being tracked by this <code>ServiceTracker</code>.
	 * 
	 * <p>
	 * If multiple services are being tracked, the service with the highest
	 * ranking (as specified in its <code>service.ranking</code> property) is
	 * returned.
	 * 
	 * <p>
	 * If there is a tie in ranking, the service with the lowest service ID (as
	 * specified in its <code>service.id</code> property); that is, the service
	 * that was registered first is returned.
	 * <p>
	 * This is the same algorithm used by
	 * <code>BundleContext.getServiceReference</code>.
	 * 
	 * <p>
	 * This implementation calls {@link #getServiceReferences()} to get the list
	 * of references for the tracked services.
	 * 
	 * @return <code>ServiceReference</code> object or <code>null</code> if no
	 *         service is being tracked.
	 * @since 1.1
	 */
	public ServiceReference getServiceReference() {
		ServiceReference reference = cachedReference;
		if (reference != null) {
			if (DEBUG) {
				System.out
						.println("ServiceTracker.getServiceReference[cached]: " + filter); //$NON-NLS-1$
			}
			return reference;
		}
		if (DEBUG) {
			System.out.println("ServiceTracker.getServiceReference: " + filter); //$NON-NLS-1$
		}
		ServiceReference[] references = getServiceReferences(); 
		int length = (references == null) ? 0 : references.length;
		if (length == 0) { /* if no service is being tracked */
			return null;
		}
		int index = 0;
		if (length > 1) { /* if more than one service, select highest ranking */
			int rankings[] = new int[length];
			int count = 0;
			int maxRanking = Integer.MIN_VALUE;
			for (int i = 0; i < length; i++) {
				Object property = references[i]
						.getProperty(Constants.SERVICE_RANKING);
				int ranking = (property instanceof Integer) ? ((Integer) property)
						.intValue()
						: 0;
				rankings[i] = ranking;
				if (ranking > maxRanking) {
					index = i;
					maxRanking = ranking;
					count = 1;
				}
				else {
					if (ranking == maxRanking) {
						count++;
					}
				}
			}
			if (count > 1) { /* if still more than one service, select lowest id */
				long minId = Long.MAX_VALUE;
				for (int i = 0; i < length; i++) {
					if (rankings[i] == maxRanking) {
						long id = ((Long) (references[i]
								.getProperty(Constants.SERVICE_ID)))
								.longValue();
						if (id < minId) {
							index = i;
							minId = id;
						}
					}
				}
			}
		}
		return cachedReference = references[index];
	}

	/**
	 * Returns the service object for the specified
	 * <code>ServiceReference</code> object if the referenced service is being
	 * tracked by this <code>ServiceTracker</code>.
	 * 
	 * @param reference Reference to the desired service.
	 * @return Service object or <code>null</code> if the service referenced by
	 *         the specified <code>ServiceReference</code> object is not being
	 *         tracked.
	 */
	public Object getService(ServiceReference reference) {
		final Tracked t = tracked; /*
									 * use local var since we are not
									 * synchronized
									 */
		if (t == null) { /* if ServiceTracker is not open */
			return null;
		}
		synchronized (t) {
			return t.getCustomizedObject(reference);
		}
	}

	/**
	 * Return an array of service objects for all services being tracked by this
	 * <code>ServiceTracker</code>.
	 * 
	 * <p>
	 * This implementation calls {@link #getServiceReferences()} to get the list
	 * of references for the tracked services and then calls
	 * {@link #getService(ServiceReference)} for each reference to get the
	 * service object.
	 * 
	 * @return Array of service objects or <code>null</code> if no services are
	 *         being tracked.
	 */
	public Object[] getServices() {
		final Tracked t = tracked; /*
									 * use local var since we are not
									 * synchronized
									 */
		if (t == null) { /* if ServiceTracker is not open */
			return null;
		}
		synchronized (t) {
			ServiceReference[] references = getServiceReferences(); 
			int length = (references == null) ? 0 : references.length;
			if (length == 0) {
				return null;
			}
			Object[] objects = new Object[length];
			for (int i = 0; i < length; i++) {
				objects[i] = getService(references[i]); 
			}
			return objects;
		}
	}

	/**
	 * Returns a service object for one of the services being tracked by this
	 * <code>ServiceTracker</code>.
	 * 
	 * <p>
	 * If any services are being tracked, this implementation returns the result
	 * of calling <code>getService(getServiceReference())</code>.
	 * 
	 * @return Service object or <code>null</code> if no service is being
	 *         tracked.
	 */
	public Object getService() {
		Object service = cachedService;
		if (service != null) {
			if (DEBUG) {
				System.out
						.println("ServiceTracker.getService[cached]: " + filter); //$NON-NLS-1$
			}
			return service;
		}
		if (DEBUG) {
			System.out.println("ServiceTracker.getService: " + filter); //$NON-NLS-1$
		}
		ServiceReference reference = getServiceReference(); 
		if (reference == null) {
			return null;
		}
		return cachedService = getService(reference); 
	}

	/**
	 * Remove a service from this <code>ServiceTracker</code>.
	 * 
	 * The specified service will be removed from this
	 * <code>ServiceTracker</code>. If the specified service was being tracked
	 * then the <code>ServiceTrackerCustomizer.removedService</code> method will
	 * be called for that service.
	 * 
	 * @param reference Reference to the service to be removed.
	 */
	public void remove(ServiceReference reference) {
		final Tracked t = tracked; /*
									 * use local var since we are not
									 * synchronized
									 */
		if (t == null) { /* if ServiceTracker is not open */
			return;
		}
		t.untrack(reference, null);
	}

	/**
	 * Return the number of services being tracked by this
	 * <code>ServiceTracker</code>.
	 * 
	 * @return Number of services being tracked.
	 */
	public int size() {
		final Tracked t = tracked; /*
									 * use local var since we are not
									 * synchronized
									 */
		if (t == null) { /* if ServiceTracker is not open */
			return 0;
		}
		synchronized (t) {
			return t.size();
		}
	}

	/**
	 * Returns the tracking count for this <code>ServiceTracker</code>.
	 * 
	 * The tracking count is initialized to 0 when this
	 * <code>ServiceTracker</code> is opened. Every time a service is added,
	 * modified or removed from this <code>ServiceTracker</code> the tracking
	 * count is incremented.
	 * 
	 * <p>
	 * The tracking count can be used to determine if this
	 * <code>ServiceTracker</code> has added, modified or removed a service by
	 * comparing a tracking count value previously collected with the current
	 * tracking count value. If the value has not changed, then no service has
	 * been added, modified or removed from this <code>ServiceTracker</code>
	 * since the previous tracking count was collected.
	 * 
	 * @since 1.2
	 * @return The tracking count for this <code>ServiceTracker</code> or -1 if
	 *         this <code>ServiceTracker</code> is not open.
	 */
	public int getTrackingCount() {
		final Tracked t = tracked; /*
									 * use local var since we are not
									 * synchronized
									 */
		if (t == null) { /* if ServiceTracker is not open */
			return -1;
		}
		synchronized (t) {
			return t.getTrackingCount();
		}
	}

	/**
	 * Called by the Tracked object whenever the set of tracked services is
	 * modified. Clears the cache.
	 */
	/*
	 * This method must not be synchronized since it is called by Tracked while
	 * Tracked is synchronized. We don't want synchronization interactions
	 * between the listener thread and the user thread.
	 */
	void modified() {
		cachedReference = null; /* clear cached value */
		cachedService = null; /* clear cached value */
		if (DEBUG) {
			System.out.println("ServiceTracker.modified: " + filter); //$NON-NLS-1$
		}
	}

	/**
	 * Inner class which subclasses AbstractTracked. This class is the
	 * <code>ServiceListener</code> object for the tracker.
	 * 
	 * @ThreadSafe
	 */
	class Tracked extends AbstractTracked implements ServiceListener {
		/**
		 * Tracked constructor.
		 */
		Tracked() {
			super();
		}

		/**
		 * <code>ServiceListener</code> method for the
		 * <code>ServiceTracker</code> class. This method must NOT be
		 * synchronized to avoid deadlock potential.
		 * 
		 * @param event <code>ServiceEvent</code> object from the framework.
		 */
		public void serviceChanged(final ServiceEvent event) {
			/*
			 * Check if we had a delayed call (which could happen when we
			 * close).
			 */
			if (closed) {
				return;
			}
			ServiceReference reference = event.getServiceReference();
			if (DEBUG) {
				System.out
						.println("ServiceTracker.Tracked.serviceChanged[" + event.getType() + "]: " + reference); //$NON-NLS-1$ //$NON-NLS-2$
			}

			switch (event.getType()) {
				case ServiceEvent.REGISTERED :
				case ServiceEvent.MODIFIED :
					if (listenerFilter != null) { // service listener added with
						// filter
						track(reference, event);
						/*
						 * If the customizer throws an unchecked exception, it
						 * is safe to let it propagate
						 */
					}
					else { // service listener added without filter
						if (filter.match(reference)) {
							track(reference, event);
							/*
							 * If the customizer throws an unchecked exception,
							 * it is safe to let it propagate
							 */
						}
						else {
							untrack(reference, event);
							/*
							 * If the customizer throws an unchecked exception,
							 * it is safe to let it propagate
							 */
						}
					}
					break;
				case ServiceEvent.MODIFIED_ENDMATCH :
				case ServiceEvent.UNREGISTERING :
					untrack(reference, event);
					/*
					 * If the customizer throws an unchecked exception, it is
					 * safe to let it propagate
					 */
					break;
			}
		}

		/**
		 * Increment the tracking count and tell the tracker there was a
		 * modification.
		 * 
		 * @GuardedBy this
		 */
		void modified() {
			super.modified(); /* increment the modification count */
			ServiceTracker.this.modified();
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
		Object customizerAdding(final Object item,
				final Object related) {
			return customizer.addingService((ServiceReference) item);
		}

		/**
		 * Call the specific customizer modified method. This method must not be
		 * called while synchronized on this object.
		 * 
		 * @param item Tracked item.
		 * @param related Action related object.
		 * @param object Customized object for the tracked item.
		 */
		void customizerModified(final Object item,
				final Object related, final Object object) {
			customizer.modifiedService((ServiceReference) item, object);
		}

		/**
		 * Call the specific customizer removed method. This method must not be
		 * called while synchronized on this object.
		 * 
		 * @param item Tracked item.
		 * @param related Action related object.
		 * @param object Customized object for the tracked item.
		 */
		void customizerRemoved(final Object item,
				final Object related, final Object object) {
			customizer.removedService((ServiceReference) item, object);
		}
	}

	/**
	 * Subclass of Tracked which implements the AllServiceListener interface.
	 * This class is used by the ServiceTracker if open is called with true.
	 * 
	 * @since 1.3
	 * @ThreadSafe
	 */
	class AllTracked extends Tracked implements AllServiceListener {
		/**
		 * AllTracked constructor.
		 */
		AllTracked() {
			super();
		}
	}
}
