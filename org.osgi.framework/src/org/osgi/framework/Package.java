/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.framework;

import java.util.Collection;

/**
 * A package that has been exported from a resolved bundle. This package may or
 * may not be currently wired to other bundles.
 * 
 * <p>
 * Objects implementing this interface are created by the framework.
 * 
 * <p>
 * The information about a package provided by this object may change. An
 * <code>Package</code> object becomes stale if the package it references has
 * been updated or removed as a result of calling
 * <code>Framework.refreshPackages()</code>.
 * 
 * If this object becomes stale, its <code>getName()</code> and
 * <code>getVersion()</code> methods continue to return their original values,
 * <code>isRemovalPending()</code> returns <code>true</code>, and
 * <code>getExportingBundle()</code> returns <code>null</code> and
 * <code>getImportingBundles()</code> returns an empty collection.
 * 
 * @ThreadSafe
 * @version $Revision$
 * @since 1.6
 */
public interface Package {
	/**
	 * Returns the name of the package.
	 * 
	 * @return The name of this package.
	 */
	String getName();

	/**
	 * Returns the bundle exporting the package.
	 * 
	 * @return The exporting bundle, or <code>null</code> if this object has
	 *         become stale.
	 */
	Bundle getExporter();

	/**
	 * Returns the resolved bundles that are currently wired to this package.
	 * 
	 * <p>
	 * Bundles which require the bundle exporting this package are considered to
	 * be wired to this package and are included in the result. See
	 * {@link Bundle#getRequiringBundles()}.
	 * 
	 * @return A <code>Collection</code> of resolved <code>Bundle</code>s
	 *         currently wired to this package, or an empty collection if this
	 *         object has become stale. The collection will be empty if no
	 *         bundles are wired to this package.
	 */
	Collection<Bundle> getImporters();

	/**
	 * Returns the version of this package.
	 * 
	 * @return The version of this package, or {@link Version#emptyVersion} if
	 *         no version information is available.
	 */
	Version getVersion();

	/**
	 * Returns <code>true</code> if the package associated with this
	 * <code>ExportedPackage</code> object has been exported by a bundle that
	 * has been updated or uninstalled.
	 * 
	 * @return <code>true</code> if the package is being exported by a bundle
	 *         that has been updated or uninstalled, or if this object has
	 *         become stale; <code>false</code> otherwise.
	 */
	boolean isRemovalPending();
}
