/*
 * Copyright (c) OSGi Alliance (2009, 2010). All Rights Reserved.
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

package org.osgi.jmx.framework;

import java.io.IOException;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;

import org.osgi.jmx.Item;
import org.osgi.jmx.JmxConstants;

/**
 * This MBean provides information about the package state of the framework.
 * 
 * @version $Revision$
 * @ThreadSafe
 */
public interface PackageStateMBean {
	/**
	 * The fully qualified object name of this MBean.
	 */
	String			OBJECTNAME				= JmxConstants.OSGI_CORE
													+ ":type=packageState,version=1.5";

	/**
	 * The key EXPORTING_BUNDLE, used in {@link #EXPORTING_BUNDLES_ITEM}.
	 */
	String			EXPORTING_BUNDLES		= "ExportingBundles";

	/**
	 * The item containing the bundle identifier in {@link #PACKAGE_TYPE}. The
	 * key is {@link #EXPORTING_BUNDLES} and the type is
	 * {@link JmxConstants#LONG_ARRAY_TYPE}.
	 */
	Item			EXPORTING_BUNDLES_ITEM	= new Item(
													EXPORTING_BUNDLES,
													"The bundles the package belongs to",
													JmxConstants.LONG_ARRAY_TYPE);

	/**
	 * The key IMPORTING_BUNDLES, used in {@link #IMPORTING_BUNDLES_ITEM}.
	 */
	String			IMPORTING_BUNDLES		= "ImportingBundles";

	/**
	 * The item containing the bundle identifier in {@link #PACKAGE_TYPE}. The
	 * key is {@link #IMPORTING_BUNDLES} and the type is {@link JmxConstants#LONG_ARRAY_TYPE}.
	 */
	Item			IMPORTING_BUNDLES_ITEM	= new Item(
													IMPORTING_BUNDLES,
													"The importing bundles of the package",
													JmxConstants.LONG_ARRAY_TYPE);


	/**
	 * The key NAME, used in {@link #NAME_ITEM}.
	 */
	String			NAME					= "Name";

	/**
	 * The item containing the name of the package in {@link #PACKAGE_TYPE}.
	 * The key is {@link #NAME} and the type is {@link SimpleType#LONG}.
	 */
	Item			NAME_ITEM				= new Item(NAME,
													"The package name",
													SimpleType.STRING);

	/**
	 * The name of the item containing the pending removal status of the package
	 * in the CompositeData. Used
	 */
	String			REMOVAL_PENDING			= "RemovalPending";
	/**
	 * The item representing the removal pending status of a package. The key is
	 * {@link #REMOVAL_PENDING} and the type is {@link SimpleType#BOOLEAN}.
	 */
	Item			REMOVAL_PENDING_ITEM	= new Item(
													REMOVAL_PENDING,
													"Whether the package is pending removal",
													SimpleType.BOOLEAN);

	/**
	 * The name of the item containing the package version in the CompositeData.
	 * Used in {@link #VERSION_ITEM}.
	 */
	String			VERSION					= "Version";

	/**
	 * The item containing the version of the package in {@link #PACKAGE_TYPE}.
	 * The key is {@link #VERSION} and the type is {@link SimpleType#STRING}.
	 */
	Item			VERSION_ITEM			= new Item(
													VERSION,
													"The identifier of the bundle the service belongs to",
													SimpleType.STRING);

	/**
	 * The Composite Type for a CompositeData representing a package. This type
	 * consists of:
	 * <ul>
	 * <li>{@link #EXPORTING_BUNDLES_ITEM}</li>
	 * <li>{@link #IMPORTING_BUNDLES_ITEM}</li>
	 * <li>{@link #NAME_ITEM}</li>
	 * <li>{@link #REMOVAL_PENDING_ITEM}</li>
	 * <li>{@link #VERSION_ITEM}</li>
	 * </ul>
	 * The key is defined as {@link #NAME} and {@link #EXPORTING_BUNDLES}
	 */
	CompositeType	PACKAGE_TYPE			= Item
													.compositeType(
															"PACKAGE",
															"This type encapsulates an OSGi package",
															EXPORTING_BUNDLES_ITEM,
															IMPORTING_BUNDLES_ITEM,
															NAME_ITEM,
															REMOVAL_PENDING_ITEM,
															VERSION_ITEM);

	/**
	 * The Tabular Type used in {@link #listPackages()}. They key is
	 * {@link #NAME}, {@link #VERSION}, and {@link #EXPORTING_BUNDLES}.
	 */
	TabularType		PACKAGES_TYPE			= Item.tabularType("PACKAGES",
													"A table of packages",
													PACKAGE_TYPE, NAME,
													VERSION, EXPORTING_BUNDLES);

	/**
	 * Answer the identifier of the bundle exporting the package
	 * 
	 * @param packageName - the package name
	 * @param version - the version of the package
	 * @return the bundle identifiers exporting such a package
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the package indicated does not exist
	 */
	long[] getExportingBundles(String packageName, String version)
			throws IOException;

	/**
	 * Answer the list of identifiers of the bundles importing the package
	 * 
	 * @param packageName The package name
	 * @param version The version of the package
	 * @param exportingBundle The exporting bundle for the given package
	 * @return the list of bundle identifiers
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the package indicated does not exist
	 * 
	 */
	long[] getImportingBundles(String packageName, String version,
			long exportingBundle) throws IOException;

	/**
	 * Answer the package state of the system in tabular form
	 * 
	 * The Tabular Data is typed by {@link #PACKAGES_TYPE}, which has
	 * {@link #PACKAGE_TYPE} as its Composite Type.
	 * 
	 * @return the tabular representation of the package state
	 * @throws IOException When fails
	 */
	TabularData listPackages() throws IOException;

	/**
	 * Answer if this package is exported by a bundle which has been updated or
	 * uninstalled
	 * 
	 * @param packageName The package name
	 * @param version The version of the package
	 * @param exportingBundle The bundle exporting the package
	 * @return true if this package is being exported by a bundle that has been
	 *         updated or uninstalled.
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the package indicated does not exist
	 */
	boolean isRemovalPending(String packageName, String version, long exportingBundle)
			throws IOException;

}
