/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.event;

import org.osgi.framework.*;

/**
 * A batch <code>BundleEvent</code> listener.
 * 
 * <p>
 * <code>BatchBundleListener</code> is a listener interface that may be
 * implemented by a bundle developer.
 * <p>
 * A <code>BatchBundleListener</code> object is registered with the
 * Framework using the {@link BundleContext#addBundleListener} method.
 * <code>BatchBundleListener</code> objects are called with a
 * <code>BundleEvent</code> object when a bundle has been installed, resolved,
 * started, stopped, updated, unresolved, or uninstalled.
 * <p>
 * A <code>BatchBundleListener</code> acts like a <code>BundleListener</code> 
 * except the framework will call the {@link #batchBegin()} method at the beginning
 * of a batch process and call the {@link #batchEnd()} at the end of a batch
 * process.  For example, the framework may notify a <code>BatchBundleListener</code>
 * of a batching process during a refresh packages operation or a resolve bundles 
 * operation.
 * <p>
 * During a batching operation the framework will continue to deliver any events using
 * the {@link BundleListener#bundleChanged(BundleEvent)} method to the
 * <code>BatchBundleListener</code>.  It is the responsiblity of the
 * <code>BatchBundleListener</code> to decide how to handle events when a
 * batching operation is in progress.
 * <p>
 * Note that the framework does not guarantee that batching operations will not
 * overlap.  This can result in the method {@link #batchBegin()} being called
 * multiple times before the first {@link #batchEnd()} is called. 
 * 
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.1
 * @see BundleEvent
 * @see BundleListener
 */
public interface BatchBundleListener extends BundleListener {
	/**
	 * Indicates that a batching process has begun.
	 */
	public abstract void batchBegin();

	/**
	 * Indicates that a batching process has ended.
	 */
	public abstract void batchEnd();
}
