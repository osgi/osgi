/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.util.tracker;

import org.osgi.framework.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;

/**
 * The <tt>AllServiceTracker</tt> class simplifies using services from the
 * Framework's service registry.
 * <p>
 * A <tt>AllServiceTracker</tt> object acts the same as a <tt>ServiceTracker</tt> object
 * except it tracks all service references using an <tt>AllServiceListener</tt> to track
 * all services from the Framework's service registry.
 * 
 * @version $ $
 */
public class AllServiceTracker extends ServiceTracker {
	
	/**
	 * Create an <code>AllServiceTracker</code> object on the specified
	 * <code>Filter</code> object.
	 * 
	 * <p>
	 * Services which match the specified <code>Filter</code> object will be
	 * tracked by this <code>ServiceTracker</code> object.
	 * 
	 * @param context <code>BundleContext</code> object against which the tracking
	 *        is done.
	 * @param filter <code>Filter</code> object to select the services to be
	 *        tracked.
	 * @param customizer The customizer object to call when services are added,
	 *        modified, or removed in this <code>AllServiceTracker</code> object. If
	 *        customizer is null, then this <code>AllServiceTracker</code> object
	 *        will be used as the <code>ServiceTrackerCustomizer</code> object and
	 *        the <code>AllServiceTracker</code> object will call the
	 *        <code>ServiceTrackerCustomizer</code> methods on itself.
	 * @since 1.2
	 */
	public AllServiceTracker(BundleContext context, Filter filter, ServiceTrackerCustomizer customizer) {
		super(context, filter, customizer);
	}

	/**
	 * Create an <code>AllServiceTracker</code> object on the specified
	 * <code>ServiceReference</code> object.
	 * 
	 * <p>
	 * The service referenced by the specified <code>ServiceReference</code>
	 * object will be tracked by this <code>AllServiceTracker</code> object.
	 * 
	 * @param context <code>BundleContext</code> object against which the tracking
	 *        is done.
	 * @param reference <code>ServiceReference</code> object for the service to be
	 *        tracked.
	 * @param customizer The customizer object to call when services are added,
	 *        modified, or removed in this <code>AllServiceTracker</code> object. If
	 *        customizer is <code>null</code>, then this <code>AllServiceTracker</code>
	 *        object will be used as the <code>ServiceTrackerCustomizer</code>
	 *        object and the <code>AllServiceTracker</code> object will call the
	 *        <code>ServiceTrackerCustomizer</code> methods on itself.
	 */
	public AllServiceTracker(BundleContext context, ServiceReference reference, ServiceTrackerCustomizer customizer) {
		super(context, reference, customizer);
	}

	/**
	 * Create an <code>AllServiceTracker</code> object on the specified class name.
	 * 
	 * <p>
	 * Services registered under the specified class name will be tracked by
	 * this <code>AllServiceTracker</code> object.
	 * 
	 * @param context <code>BundleContext</code> object against which the tracking
	 *        is done.
	 * @param clazz Class name of the services to be tracked.
	 * @param customizer The customizer object to call when services are added,
	 *        modified, or removed in this <code>AllServiceTracker</code> object. If
	 *        customizer is <code>null</code>, then this <code>AllServiceTracker</code>
	 *        object will be used as the <code>ServiceTrackerCustomizer</code>
	 *        object and the <code>AllServiceTracker</code> object will call the
	 *        <code>ServiceTrackerCustomizer</code> methods on itself.
	 */
	public AllServiceTracker(BundleContext context, String clazz, ServiceTrackerCustomizer customizer) {
		super(context, clazz, customizer);
	}

	/**
	 * Returns a new <code>AllTracked</code> object for this <code>AllServiceTracker</code> object.
	 * @return a new <code>AllTracked</code> object for this <code>AllServiceTracker</code> object.
	 */
	protected Tracked createTracked() {
		return new AllTracked();
	}

	/**
	 * Returns the list of initial <code>ServiceReference</code> objects that
	 * will be tracked by this <code>AllServiceTracker</code> object.
	 * @param trackClass the class name with which the service was registered, 
	 * or null for all services.
	 * @param filter the filter criteria or null for all services.
	 * @return the list of initial <code>ServiceReference</code> objects.
	 * @throws InvalidSyntaxException if the filter uses an invalid syntax.
	 */
	protected ServiceReference[] getInitialReferences(String trackClass, Filter filter) throws InvalidSyntaxException {
		return context.getAllServiceReferences(trackClass, filter == null ? null : filter.toString()); 
	}

	/**
	 * Inner class to track services. If a <code>AllServiceTracker</code> object is
	 * reused (closed then reopened), then a new AllTracked object is used. This
	 * class is a hashtable mapping <code>ServiceReference</code> object ->
	 * customized Object. This class is the <code>AllServiceListener</code> object
	 * for the tracker. This class is used to synchronize access to the tracked
	 * services. This is not a public class. It is only for use by the
	 * implementation of the <code>ServiceTracker</code> class.
	 */
	class AllTracked extends Tracked implements AllServiceListener{
		private static final long serialVersionUID = 4050764875305137716L;
	}
}
