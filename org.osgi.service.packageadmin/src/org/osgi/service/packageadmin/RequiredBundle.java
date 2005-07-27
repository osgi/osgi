/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.packageadmin;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * A required bundle.
 * 
 * Objects implementing this interface are created by the Package Admin service.
 * 
 * <p>
 * The term <i>required bundle</i> refers to a resolved bundle that has a
 * bundle symbolic name and is not a fragment. That is, a bundle that may be
 * required by other bundles. This bundle may or may not be currently required
 * by other bundles.
 * 
 * <p>
 * The information about a required bundle provided by this object may change. A
 * <code>RequiredBundle</code> object becomes stale if the bundle it
 * references has been updated or removed as a result of calling
 * <code>PackageAdmin.refreshPackages()</code>).
 * 
 * If this object becomes stale, its <code>getSymbolicName()</code> and
 * <code>getVersion()</code> methods continue to return their original values,
 * <code>isRemovalPending()</code> returns true, and <code>getBundle()</code>
 * and <code>getRequiringBundles()</code> return <code>null</code>.
 * 
 * @since 1.2
 */
public interface RequiredBundle {
	/**
	 * Returns the symbolic name of this required bundle.
	 * 
	 * @return The symbolic name of this required bundle.
	 */
	public String getSymbolicName();

	/**
	 * Returns the bundle associated with this required bundle.
	 * 
	 * @return The bundle, or <code>null</code> if this
	 *         <code>RequiredBundle</code> object has become stale.
	 */
	public Bundle getBundle();

	/**
	 * Returns the bundles that currently require this required bundle.
	 * 
	 * <p>
	 * If this required bundle is required and then re-exported by another
	 * bundle then all the requiring bundles of the re-exporting bundle are
	 * included in the returned array.
	 * 
	 * @return An array of bundles currently requiring this required bundle, or
	 *         <code>null</code> if this <code>RequiredBundle</code> object
	 *         has become stale.
	 */
	public Bundle[] getRequiringBundles();

	/**
	 * Returns the version of this required bundle.
	 * 
	 * @return The version of this required bundle, or
	 *         <code>Version.emptyVersion</code> if no version information is
	 *         available.
	 */
	public Version getVersion();

	/**
	 * Returns <code>true</code> if the bundle associated with this
	 * <code>RequiredBundle</code> object has been updated or uninstalled.
	 * 
	 * @return <code>true</code> if the reqiured bundle has been updated or
	 *         uninstalled, or if the <code>RequiredBundle</code> object has
	 *         become stale; <code>false</code> otherwise.
	 */
	public boolean isRemovalPending();
}