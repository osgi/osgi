/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.util.tracker;

import org.osgi.framework.ServiceReference;

/**
 * The <tt>ServiceTrackerCustomizer</tt> interface allows a <tt>ServiceTracker</tt> object to customize
 * the service objects that are tracked. The <tt>ServiceTrackerCustomizer</tt> object
 * is called when a service is being added to the <tt>ServiceTracker</tt> object. The <tt>ServiceTrackerCustomizer</tt> can
 * then return an object for the tracked service. The <tt>ServiceTrackerCustomizer</tt> object is also
 * called when a tracked service is modified or has been removed from the
 * <tt>ServiceTracker</tt> object.
 *
 * <p>The methods in this interface may be called as the result of a <tt>ServiceEvent</tt>
 * being received by a <tt>ServiceTracker</tt> object. Since <tt>ServiceEvent</tt>s are
 * synchronously delivered by the Framework, it is highly recommended that implementations
 * of these methods do not
 * register (<tt>BundleContext.registerService</tt>), modify
 * (<tt>ServiceRegistration.setProperties</tt>) or unregister
 * (<tt>ServiceRegistration.unregister</tt>)
 * a service while being synchronized on any object.
 *
 * @version $Revision$
 */
public interface ServiceTrackerCustomizer
{
    /**
     * A service is being added to the <tt>ServiceTracker</tt> object.
     *
     * <p>This method is called before a service which matched
     * the search parameters of the <tt>ServiceTracker</tt> object is
     * added to it. This method should return the
     * service object to be tracked for this <tt>ServiceReference</tt> object.
     * The returned service object is stored in the <tt>ServiceTracker</tt> object
     * and is available from the <tt>getService</tt> and <tt>getServices</tt>
     * methods.
     *
     * @param reference Reference to service being added to the <tt>ServiceTracker</tt> object.
     * @return The service object to be tracked for the
     * <tt>ServiceReference</tt> object or <tt>null</tt> if the <tt>ServiceReference</tt> object should not
     * be tracked.
     */
    public abstract Object addingService(ServiceReference reference);

    /**
     * A service tracked by the <tt>ServiceTracker</tt> object has been modified.
     *
     * <p>This method is called when a service being tracked
     * by the <tt>ServiceTracker</tt> object has had it properties modified.
     *
     * @param reference Reference to service that has been modified.
     * @param service The service object for the modified service.
     */
    public abstract void modifiedService(ServiceReference reference, Object service);

    /**
     * A service tracked by the <tt>ServiceTracker</tt> object has been removed.
     *
     * <p>This method is called after a service is no longer being tracked
     * by the <tt>ServiceTracker</tt> object.
     *
     * @param reference Reference to service that has been removed.
     * @param service The service object for the removed service.
     */
    public abstract void removedService(ServiceReference reference, Object service);
}
