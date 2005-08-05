/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.framework;

/**
 * A synchronous <code>BundleEvent</code> listener. When a <code>BundleEvent</code> is
 * fired, it is synchronously delivered to a <code>BundleListener</code>.
 * 
 * <p>
 * <code>SynchronousBundleListener</code> is a listener interface that may be
 * implemented by a bundle developer.
 * <p>
 * A <code>SynchronousBundleListener</code> object is registered with the
 * Framework using the {@link BundleContext#addBundleListener} method.
 * <code>SynchronousBundleListener</code> objects are called with a
 * <code>BundleEvent</code> object when a bundle has been installed, resolved,
 * starting, started, stopping, stopped, updated, unresolved, or uninstalled.
 * <p>
 * Unlike normal <code>BundleListener</code> objects,
 * <code>SynchronousBundleListener</code>s are synchronously called during
 * bundle lifecycle processing. The bundle lifecycle processing will not proceed
 * until all <code>SynchronousBundleListener</code>s have completed.
 * <code>SynchronousBundleListener</code> objects will be called prior to
 * <code>BundleListener</code> objects.
 * <p>
 * <code>AdminPermission[bundle,LISTENER]</code> is required to add or remove a
 * <code>SynchronousBundleListener</code> object.
 * 
 * @version $Revision$
 * @since 1.1
 * @see BundleEvent
 */

public interface SynchronousBundleListener extends BundleListener {
	// This is a marker interface
}
