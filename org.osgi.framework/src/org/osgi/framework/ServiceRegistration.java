/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2000, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.framework;

import java.util.Dictionary;

/**
 * A registered service.
 *
 * <p> The Framework returns a <tt>ServiceRegistration</tt> object when a
 * <tt>BundleContext.registerService</tt> method is successful. The <tt>ServiceRegistration</tt>
 * object is for the private use of the registering bundle and should not be shared with other bundles.
 * <p>The <tt>ServiceRegistration</tt> object may be used to update the properties of the service or to
 * unregister the service.
 *
 * @version $Revision$
 * @see BundleContext#registerService
 */

public abstract interface ServiceRegistration
{
    /**
     * Returns a <tt>ServiceReference</tt> object for a service being registered.
     * <p>The <tt>ServiceReference</tt> object may be shared with other bundles.
     *
     * @exception java.lang.IllegalStateException If this <tt>ServiceRegistration</tt> object
     * has already been unregistered.
     * @return <tt>ServiceReference</tt> object.
     */
    public abstract ServiceReference getReference();

    /**
     * Updates the properties associated with a service.
     *
     * <p>The {@link Constants#OBJECTCLASS}and {@link Constants#SERVICE_ID}keys cannot be
     * modified by this method. These values are set by the Framework when the service is
     * registered in the OSGi environment.
     *
     * <p>The following steps are required to modify service properties:
     * <ol>
     * <li>The service's properties are replaced with the provided properties.
     * <li>A service event of type {@link ServiceEvent#MODIFIED}is synchronously sent.
     * </ol>
     *
     * @param properties The properties for this service.
     * See {@link Constants}for a list of standard service property keys.
     * Changes should not be made to this object after calling this method.
     * To update the service's properties this method should be called again.
     *
     * @exception IllegalStateException If this <tt>ServiceRegistration</tt> object has already
     * been unregistered.
     *
     * @exception IllegalArgumentException If <tt>properties</tt> contains case
     * variants of the same key name.
     */
    public abstract void setProperties(Dictionary properties);

    /**
     * Unregisters a service.
     * Remove a <tt>ServiceRegistration</tt> object from the Framework service registry.
     * All <tt>ServiceReference</tt> objects associated with this <tt>ServiceRegistration</tt>
     * object can no longer be used to interact with the service.
     *
     * <p>The following steps are required to unregister a service:
     * <ol>
     * <li>The service is removed from the Framework service registry so that it can no
     * longer be used. <tt>ServiceReference</tt> objects for the service may no longer be used
     * to get a service object for the service.
     * <li>A service event of type {@link ServiceEvent#UNREGISTERING} is synchronously
     * sent so that bundles using this service can release their use of it.
     * <li>For each bundle whose use count for this service is greater than zero:
     * <br>The bundle's use count for this service is set to zero.
     * <br>If the service was registered with a {@link ServiceFactory} object, the
     * <tt>ServiceFactory.ungetService</tt> method is called to release the service object for the bundle.
     * </ol>
     *
     * @exception java.lang.IllegalStateException If this <tt>ServiceRegistration</tt> object has already been unregistered.
     * @see BundleContext#ungetService
     * @see ServiceFactory#ungetService
     */
    public abstract void unregister();
}



