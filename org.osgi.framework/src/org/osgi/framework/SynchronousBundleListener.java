/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2001).
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
 * A synchronous <tt>BundleEvent</tt> listener.
 *
 * <p><tt>SynchronousBundleListener</tt> is a listener interface that may be implemented by a bundle developer.
 * <p>A <tt>SynchronousBundleListener</tt> object is registered with the Framework using the
 * {@link BundleContext#addBundleListener}method.
 * <tt>SynchronousBundleListener</tt> objects are called with a <tt>BundleEvent</tt> object when a bundle has been
 * installed, started, stopped, updated, or uninstalled.
 * <p>Unlike normal <tt>BundleListener</tt> objects, <tt>SynchronousBundleListener</tt>s are
 * synchronously called during bundle life cycle processing. The bundle life cycle
 * processing will not proceed until all <tt>SynchronousBundleListener</tt>s have
 * completed. <tt>SynchronousBundleListener</tt> objects will be called prior to
 * <tt>BundleListener</tt> objects.
 * <p><tt>AdminPermission</tt> is required to add or remove a <tt>SynchronousBundleListener</tt> object.
 *
 * @version $Revision$
 * @since 1.1
 * @see BundleEvent
 */

public abstract interface SynchronousBundleListener extends BundleListener
{
}

