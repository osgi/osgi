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

import org.osgi.framework.ServiceReference;

/**
 * The <code>ServiceTrackerCustomizer</code> interface allows a
 * <code>ServiceTracker</code> object to customize the service objects that are
 * tracked. The <code>ServiceTrackerCustomizer</code> object is called when a
 * service is being added to the <code>ServiceTracker</code> object. The
 * <code>ServiceTrackerCustomizer</code> can then return an object for the tracked
 * service. The <code>ServiceTrackerCustomizer</code> object is also called when a
 * tracked service is modified or has been removed from the
 * <code>ServiceTracker</code> object.
 * 
 * <p>
 * The methods in this interface may be called as the result of a
 * <code>ServiceEvent</code> being received by a <code>ServiceTracker</code> object.
 * Since <code>ServiceEvent</code> s are synchronously delivered by the Framework,
 * it is highly recommended that implementations of these methods do not
 * register (<code>BundleContext.registerService</code>), modify (
 * <code>ServiceRegistration.setProperties</code>) or unregister (
 * <code>ServiceRegistration.unregister</code>) a service while being
 * synchronized on any object.
 * 
 * @version $Revision$
 */
public interface ServiceTrackerCustomizer {
	/**
	 * A service is being added to the <code>ServiceTracker</code> object.
	 * 
	 * <p>
	 * This method is called before a service which matched the search
	 * parameters of the <code>ServiceTracker</code> object is added to it. This
	 * method should return the service object to be tracked for this
	 * <code>ServiceReference</code> object. The returned service object is stored
	 * in the <code>ServiceTracker</code> object and is available from the
	 * <code>getService</code> and <code>getServices</code> methods.
	 * 
	 * @param reference Reference to service being added to the
	 *        <code>ServiceTracker</code> object.
	 * @return The service object to be tracked for the
	 *         <code>ServiceReference</code> object or <code>null</code> if the
	 *         <code>ServiceReference</code> object should not be tracked.
	 */
	public abstract Object addingService(ServiceReference reference);

	/**
	 * A service tracked by the <code>ServiceTracker</code> object has been
	 * modified.
	 * 
	 * <p>
	 * This method is called when a service being tracked by the
	 * <code>ServiceTracker</code> object has had it properties modified.
	 * 
	 * @param reference Reference to service that has been modified.
	 * @param service The service object for the modified service.
	 */
	public abstract void modifiedService(ServiceReference reference,
			Object service);

	/**
	 * A service tracked by the <code>ServiceTracker</code> object has been
	 * removed.
	 * 
	 * <p>
	 * This method is called after a service is no longer being tracked by the
	 * <code>ServiceTracker</code> object.
	 * 
	 * @param reference Reference to service that has been removed.
	 * @param service The service object for the removed service.
	 */
	public abstract void removedService(ServiceReference reference,
			Object service);
}