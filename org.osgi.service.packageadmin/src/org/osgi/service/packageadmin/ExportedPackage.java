/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.packageadmin;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * An exported package.
 * 
 * Objects implementing this interface are created by the Package Admin service.
 * 
 * <p>
 * The term <i>exported package</i> refers to a package that has been exported
 * from a resolved bundle. This package may or may not be currently wired to
 * other bundles.
 * 
 * <p>
 * The information about an exported package provided by this object may change.
 * An <code>ExportedPackage</code> object becomes stale if the package it
 * references has been updated or removed as a result of calling
 * <code>PackageAdmin.refreshPackages()</code>.
 * 
 * If this object becomes stale, its <code>getName()</code> and
 * <code>getVersion()</code> methods continue to return their original values,
 * <code>isRemovalPending()</code> returns <code>true</code>, and
 * <code>getExportingBundle()</code> and <code>getImportingBundles()</code>
 * return <code>null</code>.
 * 
 * @version $Revision$
 */
public interface ExportedPackage {
	/**
	 * Returns the name of the package associated with this exported package.
	 * 
	 * @return The name of this exported package.
	 */
	public String getName();

	/**
	 * Returns the bundle exporting the package associated with this exported
	 * package.
	 * 
	 * @return The exporting bundle, or <code>null</code> if this
	 *         <code>ExportedPackage</code> object has become stale.
	 */
	public Bundle getExportingBundle();

	/**
	 * Returns the resolved bundles that are currently wired to this exported
	 * package.
	 * 
	 * <p>
	 * Bundles which require the exporting bundle associated with this exported
	 * package are considered to be wired to this exported package are included
	 * in the returned array. See {@link RequiredBundle#getRequiringBundles()}.
	 * 
	 * @return The array of resolved bundles currently wired to this exported
	 *         package, or <code>null</code> if this
	 *         <code>ExportedPackage</code> object has become stale.
	 */
	public Bundle[] getImportingBundles();

	/**
	 * Returns the version of this exported package.
	 * 
	 * @return The version of this exported package, or <code>null</code> if
	 *         no version information is available.
	 * @deprecated Since 1.3 this method has been replaced by
	 *             {@link #getVersion}.
	 */
	public String getSpecificationVersion();

	/**
	 * Returns the version of this exported package.
	 * 
	 * @return The version of this exported package, or
	 *         <code>Version.emptyVersion</code> if no version information is
	 *         available.
	 * @since 1.3
	 */
	public Version getVersion();

	/**
	 * Returns <code>true</code> if the package associated with this
	 * <code>ExportedPackage</code> object has been exported by a bundle that
	 * has been updated or uninstalled.
	 * 
	 * @return <code>true</code> if the associated package is being exported
	 *         by a bundle that has been updated or uninstalled, or if this
	 *         <code>ExportedPackage</code> object has become stale;
	 *         <code>false</code> otherwise.
	 */
	public boolean isRemovalPending();
}