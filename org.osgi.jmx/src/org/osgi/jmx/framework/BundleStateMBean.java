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

import java.io.*;

import javax.management.openmbean.*;

import org.osgi.jmx.*;

/**
 * This MBean represents the Bundle state of the framework. This MBean also
 * emits events that clients can use to get notified of the changes in the
 * bundle state of the framework.
 */
public interface BundleStateMBean {
	/**
	 * The Object Name for a Bundle State MBean.
	 */
	public static final String	OBJECTNAME					= JmxConstants.OSGI_CORE
																	+ ":type=bundleState,version=1.5";

	/**
	 * The key KEY.
	 */
	String						KEY							= "Key";

	/**
	 * The item describing the key of a bundle header entry. The key is
	 * {@link #KEY} and the type is {@link SimpleType#STRING}.
	 */
	Item						KEY_ITEM					= new Item(
																	KEY,
																	"The bundle header key",
																	SimpleType.STRING);
	/**
	 * The key VALUE
	 */
	String						VALUE						= "Value";
	/**
	 * The item describing the value of a bundle header entry. The key is
	 * {@link #VALUE} and the type is {@link SimpleType#STRING}.
	 */
	Item						VALUE_ITEM					= new Item(
																	VALUE,
																	"The bundle header value",
																	SimpleType.STRING);

	/**
	 * The Composite Type describing an entry in bundle headers. It consists of
	 * {@link #KEY_ITEM} and {@link #VALUE_ITEM}.
	 */
	CompositeType				HEADER_TYPE					= Item
																	.compositeType(
																			"HEADER",
																			"This type encapsulates OSGi bundle header key/value pairs",
																			KEY_ITEM,
																			VALUE_ITEM);

	/**
	 * The Tabular Type describing the type of the Tabular Data value that is
	 * returned from {@link #getHeaders(long)} method. The primary item is
	 * {@link #KEY_ITEM}.
	 */
	TabularType					HEADERS_TYPE				= Item
																	.tabularType(
																			"HEADERS",
																			"The table of bundle headers",
																			HEADER_TYPE,
																			new String[] {KEY});

	/**
	 * The key LOCATION.
	 */
	String						LOCATION					= "Location";
	/**
	 * The item containing the bundle location in {@link #BUNDLE_TYPE}. The key
	 * is {@link #LOCATION} and the the type is {@link SimpleType#STRING}.
	 */
	Item						LOCATION_ITEM				= new Item(
																	LOCATION,
																	"The location of the bundle",
																	SimpleType.STRING);

	/**
	 * The key IDENTIFIER.
	 */
	String						IDENTIFIER					= "Identifier";
	/**
	 * The item containing the bundle identifier in {@link #BUNDLE_TYPE}. The
	 * key is {@link #IDENTIFIER} and the the type is {@link SimpleType#LONG}.
	 */
	Item						IDENTIFIER_ITEM				= new Item(
																	IDENTIFIER,
																	"The id of the bundle",
																	SimpleType.LONG);
	/**
	 * The key SYMBOLIC_NAME.
	 */
	String						SYMBOLIC_NAME				= "SymbolicName";
	/**
	 * The item containing the symbolic name in {@link #BUNDLE_TYPE}. The key
	 * is {@link #SYMBOLIC_NAME} and the the type is {@link SimpleType#STRING}.
	 */
	Item						SYMBOLIC_NAME_ITEM			= new Item(
																	SYMBOLIC_NAME,
																	"The symbolic name of the bundle",
																	SimpleType.STRING);
	/**
	 * The key SYMBOLIC_NAME.
	 */
	String						VERSION				= "Version";
	/**
	 * The item containing the symbolic name in {@link #BUNDLE_TYPE}. The key
	 * is {@link #SYMBOLIC_NAME} and the the type is {@link SimpleType#STRING}.
	 */
	Item						VERSION_ITEM			= new Item(
																	VERSION,
																	"The version of the bundle",
																	SimpleType.STRING);
	/**
	 * The key START_LEVEL.
	 */
	String						START_LEVEL					= "StartLevel";

	/**
	 * The item containing the start level in {@link #BUNDLE_TYPE}. The key is
	 * {@link #START_LEVEL} and the the type is {@link SimpleType#INTEGER}. The
	 * returned values must be one of the following strings:
	 * <ul>
	 * <li>"INSTALLED"</li>
	 * <li>"RESOLVED"</li>
	 * <li>"STARTING"</li>
	 * <li>"ACTIVE"</li>
	 * <li>"STOPPING"</li>
	 * <li>"UNINSTALLED"</li>
	 * <li>"UNKNOWN"</li>
	 * </ul>
	 */
	Item						START_LEVEL_ITEM			= new Item(
																	START_LEVEL,
																	"The start level of the bundle",
																	SimpleType.INTEGER);
	/**
	 * The key STATE.
	 */
	String						STATE						= "State";

	/**
	 * Constant INSTALLED for the {@link #STATE}
	 */
	String						INSTALLED					= "INSTALLED";
	/**
	 * Constant RESOLVED for the {@link #STATE}
	 */
	String						RESOLVED					= "RESOLVED";
	/**
	 * Constant STARTING for the {@link #STATE}
	 */
	String						STARTING					= "STARTING";
	/**
	 * Constant ACTIVE for the {@link #STATE}
	 */
	String						ACTIVE						= "ACTIVE";
	/**
	 * Constant STOPPING for the {@link #STATE}
	 */
	String						STOPPING					= "STOPPING";
	/**
	 * Constant UNINSTALLED for the {@link #STATE}
	 */
	String						UNINSTALLED					= "UNINSTALLED";
	/**
	 * Constant UNKNOWN for the {@link #STATE}
	 */
	String						UNKNOWN						= "UNKNOWN";
	/**
	 * The item containing the bundle state in {@link #BUNDLE_TYPE}. The key is
	 * {@link #STATE} and the the type is {@link SimpleType#STRING}.
	 */
	Item						STATE_ITEM					= new Item(
																	STATE,
																	"The state of the bundle",
																	SimpleType.STRING,
																	INSTALLED,
																	RESOLVED,
																	STARTING,
																	ACTIVE,
																	STOPPING,
																	UNINSTALLED,
																	UNKNOWN);
	/**
	 * The key LAST_MODIFIED.
	 */
	String						LAST_MODIFIED				= "LastModified";

	/**
	 * The item containing the last modified time in the {@link #BUNDLE_TYPE}.
	 * The key is {@link #LAST_MODIFIED} and the the type is
	 * {@link SimpleType#LONG}.
	 */
	Item						LAST_MODIFIED_ITEM			= new Item(
																	LAST_MODIFIED,
																	"The last modification time of the bundle",
																	SimpleType.LONG);
	/**
	 * The key PERSISTENTLY_STARTED.
	 */
	String						PERSISTENTLY_STARTED		= "PeristentlyStarted";

	/**
	 * The item containing the indication of persistently started in
	 * {@link #BUNDLE_TYPE}. The key is {@link #PERSISTENTLY_STARTED} and the
	 * the type is {@link SimpleType#BOOLEAN}.
	 */
	Item						PERSISTENTLY_STARTED_ITEM	= new Item(
																	PERSISTENTLY_STARTED,
																	"Whether the bundle is persistently started",
																	SimpleType.BOOLEAN);
	/**
	 * The key REMOVAL_PENDING.
	 */
	String						REMOVAL_PENDING				= "RemovalPending";
	/**
	 * The item containing the indication of removal pending in
	 * {@link #BUNDLE_TYPE}. The key is {@link #REMOVAL_PENDING} and the type
	 * is {@link SimpleType#BOOLEAN}.
	 */
	Item						REMOVAL_PENDING_ITEM		= new Item(
																	REMOVAL_PENDING,
																	"Whether the bundle is pending removal",
																	SimpleType.BOOLEAN);
	/**
	 * The key REQUIRED.
	 */
	String						REQUIRED					= "Required";
	/**
	 * The item containing the required status in {@link #BUNDLE_TYPE}. The key
	 * is {@link #REQUIRED} and the the type is {@link SimpleType#BOOLEAN}.
	 */
	Item						REQUIRED_ITEM				= new Item(
																	REQUIRED,
																	"Whether the bundle is required",
																	SimpleType.BOOLEAN);
	/**
	 * The key FRAGMENT.
	 */
	String						FRAGMENT					= "Fragment";
	/**
	 * The item containing the fragment status in {@link #BUNDLE_TYPE}. The key
	 * is {@link #FRAGMENT} and the the type is {@link SimpleType#BOOLEAN}.
	 */
	Item						FRAGMENT_ITEM				= new Item(
																	FRAGMENT,
																	"Whether the bundle is a fragment",
																	SimpleType.BOOLEAN);
	/**
	 * The key REGISTERED_SERVICES.
	 */
	String						REGISTERED_SERVICES			= "RegisteredServices";
	/**
	 * The item containing the registered services of the bundle in
	 * {@link #BUNDLE_TYPE}. The key is {@link #REGISTERED_SERVICES} and the
	 * the type is {@link JmxConstants#LONG_ARRAY_TYPE}.
	 */
	Item						REGISTERED_SERVICES_ITEM	= new Item(
																	REGISTERED_SERVICES,
																	"The registered services of the bundle",
																	JmxConstants.LONG_ARRAY_TYPE);
	/**
	 * The key SERVICES_IN_USE.
	 */
	String						SERVICES_IN_USE				= "ServicesInUse";
	/**
	 * The item containing the services in use by this bundle in
	 * {@link #BUNDLE_TYPE}. The key is {@link #SERVICES_IN_USE} and the the
	 * type is {@link JmxConstants#LONG_ARRAY_TYPE}.
	 */
	Item						SERVICES_IN_USE_ITEM		= new Item(
																	SERVICES_IN_USE,
																	"The services in use by the bundle",
																	JmxConstants.LONG_ARRAY_TYPE);
	/**
	 * The key HEADERS.
	 */
	String						HEADERS						= "Headers";
	/**
	 * The item containing the bundle headers in {@link #BUNDLE_TYPE}. The key
	 * is {@link #HEADERS} and the the type is {@link #HEADERS_TYPE}.
	 */
	Item						HEADERS_ITEM				= new Item(
																	HEADERS,
																	"The headers of the bundle",
																	HEADERS_TYPE);

	/**
	 * The key EXPORTED_PACKAGES.
	 */
	String						EXPORTED_PACKAGES			= "ExportedPackages";
	/**
	 * The item containing the exported package names in {@link #BUNDLE_TYPE}.The
	 * key is {@link #EXPORTED_PACKAGES} and the the type is
	 * {@link JmxConstants#STRING_ARRAY_TYPE}.
	 */
	Item						EXPORTED_PACKAGES_ITEM		= new Item(
																	EXPORTED_PACKAGES,
																	"The exported packages of the bundle",
																	JmxConstants.STRING_ARRAY_TYPE);
	/**
	 * The key IMPORTED_PACKAGES.
	 */
	String						IMPORTED_PACKAGES			= "ImportedPackages";
	/**
	 * The item containing the imported package names in {@link #BUNDLE_TYPE}.The
	 * key is {@link #IMPORTED_PACKAGES} and the the type is
	 * {@link JmxConstants#STRING_ARRAY_TYPE}.
	 */
	Item						IMPORTED_PACKAGES_ITEM		= new Item(
																	IMPORTED_PACKAGES,
																	"The imported packages of the bundle",
																	JmxConstants.STRING_ARRAY_TYPE);
	/**
	 * The key FRAGMENTS.
	 */
	String						FRAGMENTS					= "Fragments";
	/**
	 * The item containing the list of fragments the bundle is host to in
	 * {@link #BUNDLE_TYPE}. The key is {@link #FRAGMENTS} and the type is
	 * {@link JmxConstants#LONG_ARRAY_TYPE}.
	 */
	Item						FRAGMENTS_ITEM				= new Item(
																	FRAGMENTS,
																	"The fragments of which the bundle is host",
																	JmxConstants.LONG_ARRAY_TYPE);
	/**
	 * The key HOSTS.
	 */
	String						HOSTS						= "Hosts";
	/**
	 * The item containing the bundle identifiers representing the hosts in
	 * {@link #BUNDLE_TYPE}. The key is {@link #HOSTS} and the type is
	 * {@link JmxConstants#LONG_ARRAY_TYPE}
	 */
	Item						HOSTS_ITEM					= new Item(
																	HOSTS,
																	"The fragments of which the bundle is host",
																	JmxConstants.LONG_ARRAY_TYPE);
	/**
	 * The key REQUIRED_BUNDLES.
	 */
	String						REQUIRED_BUNDLES			= "RequiredBundles";
	/**
	 * The item containing the required bundles in {@link #BUNDLE_TYPE}. The
	 * key is {@link #REQUIRED_BUNDLES} and the type is
	 * {@link JmxConstants#LONG_ARRAY_TYPE}
	 */
	Item						REQUIRED_BUNDLES_ITEM		= new Item(
																	REQUIRED_BUNDLES,
																	"The required bundles the bundle",
																	JmxConstants.LONG_ARRAY_TYPE);
	/**
	 * The key REQUIRING_BUNDLES.
	 */
	String						REQUIRING_BUNDLES			= "RequiringBundles";
	/**
	 * The item containing the bundles requiring this bundle in
	 * {@link #BUNDLE_TYPE}. The key is {@link #REQUIRING_BUNDLES} and the type
	 * is {@link JmxConstants#LONG_ARRAY_TYPE}
	 */
	Item						REQUIRING_BUNDLES_ITEM		= new Item(
																	REQUIRING_BUNDLES,
																	"The bundles requiring the bundle",
																	JmxConstants.LONG_ARRAY_TYPE);

	/**
	 * The Composite Type that represents a bundle. This composite consist of:
	 * <ul>
	 * <li>{@link #EXPORTED_PACKAGES_ITEM}</li>
	 * <li>{@link #FRAGMENT_ITEM}</li>
	 * <li>{@link #FRAGMENTS_ITEM}</li>
	 * <li>{@link #HEADERS_ITEM}</li>
	 * <li>{@link #HOSTS_ITEM}</li>
	 * <li>{@link #IDENTIFIER_ITEM}</li>
	 * <li>{@link #IMPORTED_PACKAGES_ITEM}</li>
	 * <li>{@link #LAST_MODIFIED_ITEM}</li>
	 * <li>{@link #LOCATION_ITEM}</li>
	 * <li>{@link #PERSISTENTLY_STARTED_ITEM}</li>
	 * <li>{@link #REGISTERED_SERVICES_ITEM}</li>
	 * <li>{@link #REMOVAL_PENDING_ITEM}</li>
	 * <li>{@link #REQUIRED_ITEM}</li>
	 * <li>{@link #REQUIRED_BUNDLES_ITEM}</li>
	 * <li>{@link #REQUIRING_BUNDLES_ITEM}</li>
	 * <li>{@link #START_LEVEL_ITEM}</li>
	 * <li>{@link #STATE_ITEM}</li>
	 * <li>{@link #SERVICES_IN_USE_ITEM}</li>
	 * <li>{@link #SYMBOLIC_NAME_ITEM}</li>
	 * </ul>
	 * It is used by {@link #BUNDLES_TYPE}.
	 */
	CompositeType				BUNDLE_TYPE					= Item
																	.compositeType(
																			"BUNDLE",
																			"This type encapsulates OSGi bundles",
																			EXPORTED_PACKAGES_ITEM,
																			FRAGMENT_ITEM,
																			FRAGMENTS_ITEM,
																			HEADERS_ITEM,
																			HOSTS_ITEM,
																			IDENTIFIER_ITEM,
																			IMPORTED_PACKAGES_ITEM,
																			LAST_MODIFIED_ITEM,
																			LOCATION_ITEM,
																			PERSISTENTLY_STARTED_ITEM,
																			REGISTERED_SERVICES_ITEM,
																			REMOVAL_PENDING_ITEM,
																			REQUIRED_ITEM,
																			REQUIRED_BUNDLES_ITEM,
																			REQUIRING_BUNDLES_ITEM,
																			START_LEVEL_ITEM,
																			STATE_ITEM,
																			SERVICES_IN_USE_ITEM,
																			SYMBOLIC_NAME_ITEM);

	/**
	 * The Tabular Type for a list of bundles. The row type is
	 * {@link #BUNDLE_TYPE}.
	 */
	TabularType					BUNDLES_TYPE				= Item
																	.tabularType(
																			"BUNDLES",
																			"A list of bundles",
																			BUNDLE_TYPE,
																			new String[] {IDENTIFIER});

	/**
	 * Answer the list of identifiers of the bundles this bundle depends upon
	 * 
	 * @param bundleIdentifier the bundle identifier
	 * @return the list of bundle identifiers
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	long[] getRequiredBundles(long bundleIdentifier) throws IOException;

	/**
	 * Answer the bundle state of the system in tabular form.
	 * 
	 * Each row of the returned table represents a single bundle. The Tabular
	 * Data consists of Composite Data that is type by {@link #BUNDLES_TYPE}.
	 * 
	 * @return the tabular representation of the bundle state
	 * @throws IOException
	 */
	TabularData listBundles() throws IOException;

	/**
	 * Answer the list of exported packages for this bundle.
	 * 
	 * @param bundleId
	 * @return the array of package names, combined with their version in the
	 *         format &lt;packageName;version&gt;
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	String[] getExportedPackages(long bundleId) throws IOException;

	/**
	 * Answer the list of the bundle ids of the fragments associated with this
	 * bundle
	 * 
	 * @param bundleId
	 * @return the array of bundle identifiers
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	long[] getFragments(long bundleId) throws IOException;

	/**
	 * Answer the headers for the bundle uniquely identified by the bundle id.
	 * The Tabular Data is typed by the {@link #HEADERS_TYPE}.
	 * 
	 * @param bundleId the unique identifier of the bundle
	 * @return the table of associated header key and values
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	TabularData getHeaders(long bundleId) throws IOException;

	/**
	 * Answer the list of bundle ids of the bundles which host a fragment
	 * 
	 * @param fragment the bundle id of the fragment
	 * @return the array of bundle identifiers
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	long[] getHosts(long fragment) throws IOException;

	/**
	 * Answer the array of the packages imported by this bundle
	 * 
	 * @param bundleId the bundle identifier
	 * @return the array of package names, combined with their version in the
	 *         format &lt;packageName;version&gt;
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	String[] getImportedPackages(long bundleId) throws IOException;

	/**
	 * Answer the last modified time of a bundle
	 * 
	 * @param bundleId the unique identifier of a bundle
	 * @return the last modified time
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	long getLastModified(long bundleId) throws IOException;

	/**
	 * Answer the list of service identifiers representing the services this
	 * bundle exports
	 * 
	 * @param bundleId the bundle identifier
	 * @return the list of service identifiers
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	long[] getRegisteredServices(long bundleId) throws IOException;

	/**
	 * Answer the list of identifiers of the bundles which require this bundle
	 * 
	 * @param bundleIdentifier the bundle identifier
	 * @return the list of bundle identifiers
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	long[] getRequiringBundles(long bundleIdentifier) throws IOException;

	/**
	 * Answer the list of service identifiers which refer to the the services
	 * this bundle is using
	 * 
	 * @param bundleIdentifier the bundle identifier
	 * @return the list of service identifiers
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	long[] getServicesInUse(long bundleIdentifier) throws IOException;

	/**
	 * Answer the start level of the bundle
	 * 
	 * @param bundleId the identifier of the bundle
	 * @return the start level
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	int getStartLevel(long bundleId) throws IOException;

	/**
	 * Answer the symbolic name of the state of the bundle
	 * 
	 * @param bundleId the identifier of the bundle
	 * @return the string name of the bundle state
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	String getState(long bundleId) throws IOException;

	/**
	 * Answer the symbolic name of the bundle
	 * 
	 * @param bundleId the identifier of the bundle
	 * @return the symbolic name
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	String getSymbolicName(long bundleId) throws IOException;

	/**
	 * Answer if the bundle is persistently started when its start level is
	 * reached
	 * 
	 * @param bundleId the identifier of the bundle
	 * @return true if the bundle is persistently started
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	boolean isPersistentlyStarted(long bundleId) throws IOException;

	/**
	 * Answer whether the bundle is a fragment or not
	 * 
	 * @param bundleId the identifier of the bundle
	 * @return true if the bundle is a fragment
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	boolean isFragment(long bundleId) throws IOException;

	/**
	 * Answer true if the bundle is pending removal
	 * 
	 * @param bundleId the identifier of the bundle
	 * @return true if the bundle is pending removal
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	boolean isRemovalPending(long bundleId) throws IOException;

	/**
	 * Answer true if the bundle is required by another bundle
	 * 
	 * @param bundleId the identifier of the bundle
	 * @return true if the bundle is required by another bundle
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	boolean isRequired(long bundleId) throws IOException;
	
	/**
	 * Answer the location of the bundle.
	 * 
	 * @param bundleId the identifier of the bundle
	 * @return The location string of this bundle
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	String getLocation(long bundleId) throws IOException;
	
	/**
	 * Answer the location of the bundle.
	 * 
	 * @param bundleId the identifier of the bundle
	 * @return The location string of this bundle
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the bundle indicated does not exist
	 */
	String getVersion(long bundleId) throws IOException;
	
}
