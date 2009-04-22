/*
 * Copyright 2008 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.osgi.jmx.framework;

import java.io.IOException;

import javax.management.openmbean.TabularData;

/**
 * <p>
 * This MBean represents the Package state of the framework.
 */
public interface PackageStateMBean {
	/**
	 * Answer the identifier of the bundle exporting the package
	 * 
	 * @param packageName
	 *            - the package name
	 * @param version
	 *            - the version of the package
	 * @return the bundle identifier or -1 if there is no bundle
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the package indicated does not exist
	 */
	long getExportingBundle(String packageName, String version)
			throws IOException;

	/**
	 * Answer the list of identifiers of the bundles importing the package
	 * 
	 * @param packageName
	 *            - the package name
	 * @param version
	 *            - the version of the package
	 * @return the list of bundle identifiers
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the package indicated does not exist
	 */
	long[] getImportingBundles(String packageName, String version)
			throws IOException;

	/**
	 * Answer the package state of the system in tabular form
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiPackage for the details of the TabularType.
	 *      <p>
	 *      Each row of the returned table represents a single package. For each
	 *      package, the following row is returned
	 *      <ul>
	 *      <li>name - String</li>
	 *      <li>version - String</li>
	 *      <li>removal pending - boolean</li>
	 *      <li>exporting bundle - long</li>
	 *      <li>importing bundles - long[]</li>
	 *      </ul>
	 * 
	 * @return the tabular respresentation of the package state
	 */
	TabularData getPackages();

	/**
	 * Answer if this package is exported by a bundle which has been updated or
	 * uninstalled
	 * 
	 * @param packageName
	 *            - the package name
	 * @param version
	 *            - the version of the package
	 * @return true if this package is being exported by a bundle that has been
	 *         updated or uninstalled.
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the package indicated does not exist
	 */
	boolean isRemovalPending(String packageName, String version)
			throws IOException;

	/**
	 * The name of the item containing the bundle identifier in the
	 * CompositeData
	 */
	String BUNDLE_IDENTIFIER = "BundleIdentifier";

	/**
	 * The name of the item containing the importing bundles in the
	 * CompositeData
	 */
	String IMPORTING_BUNDLES = "ImportingBundles";

	/**
	 * The name of the item containing the package name in the CompositeData
	 */
	String PACKAGE_NAME = "Name";

	/**
	 * The name of the item containing the pending removal status of the package
	 * in the CompositeData
	 */
	String PACKAGE_PENDING_REMOVAL = "PendingRemoval";

	/**
	 * The name of the item containing the package version in the CompositeData
	 */
	String PACKAGE_VERSION = "Version";

	/**
	 * The item names in the CompositeData representing the OSGi Package
	 */
	String[] PACKAGE = { PACKAGE_NAME, PACKAGE_VERSION,
			PACKAGE_PENDING_REMOVAL, BUNDLE_IDENTIFIER, IMPORTING_BUNDLES };
}
