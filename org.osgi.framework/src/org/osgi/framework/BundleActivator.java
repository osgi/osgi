/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2000-2001).
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

/**
 * Customizes the starting and stopping of this bundle.
 * <p><tt>BundleActivator</tt> is an interface that may be implemented
 * when this bundle is started or stopped. The Framework can create instances of this bundle's
 * <tt>BundleActivator</tt> as required. If an instance's <tt>BundleActivator.start</tt>
 * method executes successfully, it is guaranteed that the same instance's <tt>BundleActivator.stop</tt>
 * method will be called when this bundle is to be stopped.
 *
 * <p><tt>BundleActivator</tt> is specified through the
 * <tt>Bundle-Activator</tt> Manifest header. A bundle can only specify
 * a single <tt>BundleActivator</tt> in the Manifest file.
 * The form of the Manifest header is:
 * <pre>
 * Bundle-Activator: <i>class-name</i>
 * </pre>
 * where <code>class-name</code> is a fully qualified Java classname.
 * <p>The specified <tt>BundleActivator</tt> class must have a public constructor
 * that takes no parameters so that a <tt>BundleActivator</tt> object can
 * be created by <tt>Class.newInstance()</tt>.
 *
 * @version $Revision$
 */

public abstract interface BundleActivator
{
    /**
     * Called when this bundle is started so the Framework can perform the bundle-specific
     * activities necessary to start this bundle. This method can be used to register services or to
     * allocate any resources that this bundle needs.
     *
     * <p>This method must complete and return to its caller in a timely manner.
     *
     * @param context The execution context of the bundle being started.
     * @exception java.lang.Exception If this method throws an exception, this bundle is marked
     * as stopped and the Framework will remove this bundle's listeners, unregister all services
     * registered by this bundle, and release all services used by this bundle.
     * @see Bundle#start
     */
    public abstract void start(BundleContext context) throws Exception;

    /**
     * Called when this bundle is stopped so the Framework can perform the bundle-specific
     * activities necessary to stop the bundle.
     * In general, this method should undo the work that the <tt>BundleActivator.start</tt> method started.
     * There should be no active threads that were started by this bundle when this bundle returns.
     * A stopped bundle should be stopped and should not call any Framework objects.
     *
     * <p>This method must complete and return to its caller in a timely manner.
     *
     * @param context The execution context of the bundle being stopped.
     * @exception java.lang.Exception If this method throws an exception,
     * the bundle is still marked as stopped, and the Framework will
     * remove the bundle's listeners, unregister all services
     * registered by the bundle, and release all services
     * used by the bundle.
     * @see Bundle#stop
     */
    public abstract void stop(BundleContext context) throws Exception;
}


