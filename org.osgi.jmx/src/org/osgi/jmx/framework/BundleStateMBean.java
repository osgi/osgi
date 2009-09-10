/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

import javax.management.openmbean.TabularData;

/**
 * <p>
 * This MBean represents the Bundle state of the framework. This MBean also
 * emits events that clients can use to get notified of the changes in the
 * bundle state of the framework.
 * <p>
 * See <link>OSGiBundleEvent</link> for the precise definition of the
 * CompositeData for the notification sent.
 */
public interface BundleStateMBean {

	/**
	 * Answer the list of identifiers of the bundles this bundle depends upon
	 * 
	 * @param bundleIdentifier
	 *            - the bundle identifier
	 * @return the list of bundle identifiers
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	long[] getDependencies(long bundleIdentifier) throws IOException;

	/**
	 * Answer the bundle state of the system in tabular form
	 * 
	 * Each row of the returned table represents a single bundle. For each
	 * bundle, the following row is returned
	 * <ul>
	 * <li>location - String</li>
	 * <li>bundle identifier - String</li>
	 * <li>symbolic name - String</li>
	 * <li>start level - int</li>
	 * <li>state - String</li>
	 * <li>last modified - long</li>
	 * <li>persistently started - boolean</li>
	 * <li>removal pending - boolean</li>
	 * <li>required - boolean</li>
	 * <li>fragement - boolean</li>
	 * <li>registered services - long[]</li>
	 * <li>services in use - long[]</li>
	 * <li>headers - TabularData</li>
	 * <li>exported packages - String[]</li>
	 * <li>imported packages - String[]</li>
	 * <li>fragments - long[]</li>
	 * <li>hosts - long[]</li>
	 * <li>required bundles - long[]</li>
	 * <li>requiring bundles - long[]</li>
	 * </ul>
	 * 
	 * @see org.osgi.jmx.codec.OSGiBundle for the precise specifiction of the
	 *      CompositeType definition for each row of the table.
	 * 
	 * @return the tabular respresentation of the bundle state
	 * @throws IOException
	 */
	TabularData getBundles() throws IOException;

	/**
	 * Answer the list of exported packages for this bundle
	 * 
	 * @param bundleId
	 * @return the array of package names, combined with their version in the
	 *         format <packageName;version>
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	String[] getExportedPackages(long bundleId) throws IOException;

	/**
	 * Answer the list of the bundle ids of the fragments associated with this
	 * bundle
	 * 
	 * @param bundleId
	 * @return the array of bundle identifiers
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	long[] getFragments(long bundleId) throws IOException;

	/**
	 * Answer the headers for the bundle uniquely identified by the bundle id
	 * 
	 * @see org.osgi.jmx.codec.OSGiBundle for the precise specifiction of the
	 *      TabularType
	 * 
	 * @param bundleId
	 *            - the unique identifer of the bundle
	 * @return the table of associated header key and values
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	TabularData getHeaders(long bundleId) throws IOException;

	/**
	 * Answer the list of bundle ids of the bundles which host a fragment
	 * 
	 * @param fragment
	 *            - the bundle id of the fragment
	 * @return the array of bundle identifiers
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	long[] getHosts(long fragment) throws IOException;

	/**
	 * Answer the array of the packages imported by this bundle
	 * 
	 * @param bundleId
	 *            - the bundle identifier
	 * @return the array of package names, combined with their version in the
	 *         format <packageName;version>
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	String[] getImportedPackages(long bundleId) throws IOException;

	/**
	 * Answer the last modified time of a bundle
	 * 
	 * @param bundleId
	 *            - the unique identifier of a bundle
	 * @return the last modified time
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	long getLastModified(long bundleId) throws IOException;

	/**
	 * Answer the list of service identifiers representing the services this
	 * bundle exports
	 * 
	 * @param bundleId
	 *            - the bundle identifier
	 * @return the list of service identifiers
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	long[] getRegisteredServices(long bundleId) throws IOException;

	/**
	 * Answer the list of identifiers of the bundles which require this bundle
	 * 
	 * @param bundleIdentifier
	 *            - the bundle identifier
	 * @return the list of bundle identifiers
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	long[] getRequiringBundles(long bundleIdentifier) throws IOException;

	/**
	 * Answer the list of service identifiers which refer to the the services
	 * this bundle is using
	 * 
	 * @param bundleIdentifier
	 *            - the bundle identifier
	 * @return the list of service identifiers
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	long[] getServicesInUse(long bundleIdentifier) throws IOException;

	/**
	 * Answer the start level of the bundle
	 * 
	 * @param bundleId
	 *            - the identifier of the bundle
	 * @return the start level
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	int getStartLevel(long bundleId) throws IOException;

	/**
	 * Answer the symbolic name of the state of the bundle
	 * 
	 * @param bundleId
	 *            - the identifier of the bundle
	 * @return the string name of the bundle state
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	String getState(long bundleId) throws IOException;

	/**
	 * Answer the symbolic name of the bundle
	 * 
	 * @param bundleId
	 *            - the identifier of the bundle
	 * @return the symbolic name
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	String getSymbolicName(long bundleId) throws IOException;

	/**
	 * Answer the version of the bundle
	 * 
	 * @param bundleId
	 *            - the identifier of the bundle
	 * @return the version
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	String getVersion(long bundleId) throws IOException;

	/**
	 * Answer if the bundle is persistently started when its start level is
	 * reached
	 * 
	 * @param bundleId
	 *            - the identifier of the bundle
	 * @return true if the bundle is persistently started
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	boolean isPersistentlyStarted(long bundleId) throws IOException;

	/**
	 * Answer whether the bundle is a fragment or not
	 * 
	 * @param bundleId
	 *            - the identifier of the bundle
	 * @return true if the bundle is a fragment
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	boolean isFragment(long bundleId) throws IOException;

	/**
	 * Answer true if the bundle is pending removal
	 * 
	 * @param bundleId
	 *            - the identifier of the bundle
	 * @return true if the bundle is pending removal
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	boolean isRemovalPending(long bundleId) throws IOException;

	/**
	 * Answer true if the bundle is required by another bundle
	 * 
	 * @param bundleId
	 *            - the identifier of the bundle
	 * @return true if the bundle is required by another bundle
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the bundle indicated does not exist
	 */
	boolean isRequired(long bundleId) throws IOException;

	/**
	 * The type of the event which is emitted when bundle state changes occur in
	 * the OSGi container
	 */
	String BUNDLE_EVENT_TYPE = "org.osgi.jmx.bundleEvent";

	/**
	 * The name of the item containing the exported packages in the
	 * CompositeData
	 */
	String BUNDLE_EXPORTED_PACKAGES = "ExportedPackages";

	/**
	 * The name of the item containing the fragment status in the CompositeData
	 */
	String BUNDLE_FRAGMENT = "Fragment";

	/**
	 * The name of the item containing the list of fragments the bundle is host
	 * to in the CompositeData representing a Bundle
	 */
	String BUNDLE_FRAGMENTS = "Fragments";

	/**
	 * The name of the item containing the bundle headers in the CompositeData
	 */
	String BUNDLE_HEADERS = "Headers";

	/**
	 * The name of the item containing the bundle identifiers representing the
	 * hosts
	 */
	String BUNDLE_HOSTS = "Hosts";

	/**
	 * The name of the item containing the bundle identifier in the
	 * CompositeData
	 */
	String BUNDLE_ID = "Identifier";

	/**
	 * The name of the item containing the imported packages in the
	 * CompositeData
	 */
	String BUNDLE_IMPORTED_PACKAGES = "ImportedPackages";

	/**
	 * The name of the item containing the last modified time in the
	 * CompositeData
	 */
	String BUNDLE_LAST_MODIFIED = "LastModified";

	/**
	 * The name of the item containing the bundle location in the CompositeData
	 */
	String BUNDLE_LOCATION = "Location";

	/**
	 * The name of the item containing the indication of persistently started in
	 * the CompositeData
	 */
	String BUNDLE_PERSISTENTLY_STARTED = "PersistentlyStarted";

	/**
	 * The name of the item containing the registered services of the bundle in
	 * the CompositeData
	 */
	String BUNDLE_REGISTERED_SERVICES = "RegisteredServices";

	/**
	 * The name of the item containing the indication of removal pending in the
	 * CompositeData
	 */
	String BUNDLE_REMOVAL_PENDING = "RemovalPending";

	/**
	 * The name of the item containing the required status in the CompositeData
	 */
	String BUNDLE_REQUIRED = "Required";

	/**
	 * The name of the item containing the required bundles in the CompositeData
	 */
	String BUNDLE_REQUIRED_BUNDLES = "RequiredBundles";

	/**
	 * The name of the item containing the bundles requiring this bundle in the
	 * CompositeData
	 */
	String BUNDLE_REQUIRING_BUNDLES = "RequiringBundles";

	/**
	 * The name of the item containing the services in use by this bundle in the
	 * CompositeData
	 */
	String BUNDLE_SERVICES_IN_USE = "ServicesInUse";

	/**
	 * The name of the item containing the start level in the CompositeData
	 */
	String BUNDLE_START_LEVEL = "StartLevel";

	/**
	 * The name of the item containing the bundle state in the CompositeData
	 */
	String BUNDLE_STATE = "State";

	/**
	 * The name of the item containing the symbolic name in the CompositeData
	 */
	String BUNDLE_SYMBOLIC_NAME = "BundleSymbolicName";

	/**
	 * The name of the item containing the symbolic name in the CompositeData
	 */
	String BUNDLE_VERSION = "BundleVersion";

	/**
	 * The name CompositeData type for a bundle
	 */
	String BUNDLE_TYPE_NAME = "Bundle";

	/**
	 * The name of the item containing the event type in the CompositeData
	 */
	String EVENT_TYPE = "Type";

	/**
	 * The item names in the CompositeData representing an OSGi Bundle
	 */
	String[] BUNDLE = { BUNDLE_LOCATION, BUNDLE_ID, BUNDLE_SYMBOLIC_NAME,
			BUNDLE_VERSION, BUNDLE_START_LEVEL, BUNDLE_STATE,
			BUNDLE_LAST_MODIFIED, BUNDLE_PERSISTENTLY_STARTED,
			BUNDLE_REMOVAL_PENDING, BUNDLE_REQUIRED, BUNDLE_FRAGMENT,
			BUNDLE_REGISTERED_SERVICES, BUNDLE_SERVICES_IN_USE, BUNDLE_HEADERS,
			BUNDLE_EXPORTED_PACKAGES, BUNDLE_IMPORTED_PACKAGES,
			BUNDLE_FRAGMENTS, BUNDLE_HOSTS, BUNDLE_REQUIRED_BUNDLES,
			BUNDLE_REQUIRING_BUNDLES };

	/**
	 * The item names in the CompositeData representing the event raised for
	 * bundle events within the OSGi container by this bean
	 */
	String[] BUNDLE_EVENT = { BUNDLE_ID, BUNDLE_LOCATION, BUNDLE_SYMBOLIC_NAME,
			EVENT_TYPE };

	/**
	 * The type of the headers
	 */
	public static final String BUNDLE_HEADERS_TYPE = "BundleHeaders";

	/**
	 * the type of the individual header
	 */
	public static final String BUNDLE_HEADER_TYPE = "BundleHeader";
}
