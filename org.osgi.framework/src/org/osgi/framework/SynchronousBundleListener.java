/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
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

package org.osgi.framework;

/**
 * A synchronous <code>BundleEvent</code> listener.
 * 
 * <p>
 * <code>SynchronousBundleListener</code> is a listener interface that may be
 * implemented by a bundle developer.
 * <p>
 * A <code>SynchronousBundleListener</code> object is registered with the
 * Framework using the {@link BundleContext#addBundleListener}method.
 * <code>SynchronousBundleListener</code> objects are called with a
 * <code>BundleEvent</code> object when a bundle has been installed, started,
 * stopped, updated, or uninstalled.
 * <p>
 * Unlike normal <code>BundleListener</code> objects,
 * <code>SynchronousBundleListener</code> s are synchronously called during bundle
 * lifecycle processing. The bundle lifecycle processing will not proceed
 * until all <code>SynchronousBundleListener</code> s have completed.
 * <code>SynchronousBundleListener</code> objects will be called prior to
 * <code>BundleListener</code> objects.
 * <p>
 * <code>AdminPermission</code> is required to add or remove a
 * <code>SynchronousBundleListener</code> object.
 * 
 * @version $Revision$
 * @since 1.1
 * @see BundleEvent
 */

public abstract interface SynchronousBundleListener extends BundleListener {
}

