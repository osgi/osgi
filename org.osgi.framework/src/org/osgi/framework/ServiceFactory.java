/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
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

package org.osgi.framework;

/**
 * Allows services to provide customized service objects in the OSGi environment.
 *
 * <p>When registering a service, a <tt>ServiceFactory</tt> object can
 * be used instead of a service object, so that the bundle developer can gain control
 * of the specific service object granted to a bundle that is using the service.
 *
 * <p>When this happens, the <tt>BundleContext.getService(ServiceReference)</tt> method calls the
 * <tt>ServiceFactory.getService</tt> method to create a service object specifically for the
 * requesting bundle. The service object returned by the <tt>ServiceFactory</tt> object
 * is cached by the Framework until the bundle releases its use of the service.
 *
 * <p>When the bundle's use count for the service equals zero (including the bundle stopping
 * or the service being unregistered), the <tt>ServiceFactory.ungetService</tt> method is called.
 *
 * <p><tt>ServiceFactory</tt> objects are only used by the Framework and are not
 * made available to other bundles in the OSGi environment.
 *
 * @version $Revision$
 * @see BundleContext#getService
 */

public abstract interface ServiceFactory
{
    /**
     * Creates a new service object.
     *
     * <p>The Framework invokes this method the first time the specified <tt>bundle</tt> requests
     * a service object using the <tt>BundleContext.getService(ServiceReference)</tt> method.
     * The service factory can then return a specific service object for each bundle.
     *
     * <p>The Framework caches the value returned (unless it is <tt>null</tt>), and
     * will return the same service object on any future call to <tt>BundleContext.getService</tt>
     * from the same bundle.
     *
     * <p>The Framework will check if the returned service object is an instance of
     * all the classes named when the service was registered. If not, then <tt>null</tt> is
     * returned to the bundle.
     *
     * @param bundle The bundle using the service.
     * @param registration The <tt>ServiceRegistration</tt> object for the service.
     * @return A service object that <strong>must</strong> be an instance of
     * all the classes named when the service was registered.
     * @see BundleContext#getService
     */
    public abstract Object getService(Bundle bundle,
                      ServiceRegistration registration);

    /**
     * Releases a service object.
     *
     * <p>The Framework invokes this method when a service has been released by a bundle.
     * The service object may then be destroyed.
     *
     * @param bundle The bundle releasing the service.
     * @param registration The <tt>ServiceRegistration</tt> object for the service.
     * @param service The service object returned by a previous call to the <tt>ServiceFactory.getService</tt> method.
     * @see BundleContext#ungetService
     */
    public abstract void ungetService(Bundle bundle,
                      ServiceRegistration registration,
                      Object service);
}


