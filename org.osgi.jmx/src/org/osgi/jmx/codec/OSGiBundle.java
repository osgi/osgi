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

package org.osgi.jmx.codec;

import static org.osgi.jmx.codec.Util.LONG_ARRAY_TYPE;
import static org.osgi.jmx.codec.Util.LongArrayFrom;
import static org.osgi.jmx.codec.Util.STRING_ARRAY_TYPE;
import static org.osgi.jmx.codec.Util.getBundleDependencies;
import static org.osgi.jmx.codec.Util.getBundleExportedPackages;
import static org.osgi.jmx.codec.Util.getBundleFragments;
import static org.osgi.jmx.codec.Util.getBundleHeaders;
import static org.osgi.jmx.codec.Util.getBundleImportedPackages;
import static org.osgi.jmx.codec.Util.getBundleState;
import static org.osgi.jmx.codec.Util.getBundlesRequiring;
import static org.osgi.jmx.codec.Util.isBundleFragment;
import static org.osgi.jmx.codec.Util.isBundlePersistentlyStarted;
import static org.osgi.jmx.codec.Util.isBundleRequired;
import static org.osgi.jmx.codec.Util.isRequiredBundleRemovalPending;
import static org.osgi.jmx.codec.Util.longArrayFrom;
import static org.osgi.jmx.codec.Util.serviceIds;
import static org.osgi.jmx.framework.BundleStateMBean.BUNDLE_ID;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

/**
 * <p>
 * This class represents the CODEC for the composite data representing a single
 * OSGi <link>Bundle</link>.
 * <p>
 * It serves as both the documentation of the type structure and as the
 * codification of the mechanism to convert to/from the CompositeData.
 * <p>
 * The structure of the composite data is:
 * <table border="1">
 * <tr>
 * <td>Location</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>Identifier</td>
 * <td>long</td>
 * </tr>
 * <tr>
 * <td>SymbolicName</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>StartLevel</td>
 * <td>int</td>
 * </tr>
 * <tr>
 * <td>State</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>LastModified</td>
 * <td>long</td>
 * </tr>
 * <tr>
 * <td>PersistentlyStarted</td>
 * <td>boolean</td>
 * </tr>
 * <tr>
 * <td>RemovalPending</td>
 * <td>boolean</td>
 * </tr>
 * <tr>
 * <td>Required</td>
 * <td>boolean</td>
 * </tr>
 * <tr>
 * <td>Fragment</td>
 * <td>boolean</td>
 * </tr>
 * <tr>
 * <td>RegisteredServices</td>
 * <td>Array of long</td>
 * </tr>
 * <tr>
 * <td>ServicesInUse</td>
 * <td>Array of long</td>
 * </tr>
 * <tr>
 * <td>Headers</td>
 * <td>TabularData of Key/Value String pairs</td>
 * </tr>
 * <tr>
 * <td>ExportedPackages</td>
 * <td>Array of String</td>
 * </tr>
 * <tr>
 * <td>ImportedPackages</td>
 * <td>Array of String</td>
 * </tr>
 * <tr>
 * <td>Fragments</td>
 * <td>Array of long</td>
 * </tr>
 * <tr>
 * <td>Hosts</td>
 * <td>Array of long</td>
 * </tr>
 * <tr>
 * <td>RequiredBundles</td>
 * <td>Array of long</td>
 * </tr>
 * <tr>
 * <td>RequiringBundles</td>
 * <td>Array of long</td>
 * </tr>
 * </table>
 */
public class OSGiBundle {

	private static final String VALUE = "Value";

	private static final String KEY = "Key";

	private static final String[] HEADER_PROPERTY_ITEM_NAMES = new String[] {
			KEY, VALUE };

	/**
	 * Construct an OSGiBundle from the encoded CompositeData
	 * 
	 * @param data
	 *            - the encoded representation of the bundle
	 */
	public OSGiBundle(CompositeData data) {
		this(
				(String) data.get(BundleStateMBean.BUNDLE_LOCATION),
				((Long) data.get(BundleStateMBean.BUNDLE_ID)).longValue(),
				(String) data.get(BundleStateMBean.BUNDLE_SYMBOLIC_NAME),
				(String) data.get(BundleStateMBean.BUNDLE_VERSION),
				((Integer) data.get(BundleStateMBean.BUNDLE_START_LEVEL))
						.intValue(),
				(String) data.get(BundleStateMBean.BUNDLE_STATE),
				((Long) data.get(BundleStateMBean.BUNDLE_LAST_MODIFIED))
						.longValue(),
				(Boolean) data
						.get(BundleStateMBean.BUNDLE_PERSISTENTLY_STARTED),
				(Boolean) data.get(BundleStateMBean.BUNDLE_REMOVAL_PENDING),
				(Boolean) data.get(BundleStateMBean.BUNDLE_REQUIRED),
				(Boolean) data.get(BundleStateMBean.BUNDLE_FRAGMENT),
				longArrayFrom((Long[]) data
						.get(BundleStateMBean.BUNDLE_REGISTERED_SERVICES)),
				longArrayFrom((Long[]) data
						.get(BundleStateMBean.BUNDLE_SERVICES_IN_USE)),
				mapFrom((TabularData) data.get(BundleStateMBean.BUNDLE_HEADERS)),
				(String[]) data.get(BundleStateMBean.BUNDLE_EXPORTED_PACKAGES),
				(String[]) data.get(BundleStateMBean.BUNDLE_IMPORTED_PACKAGES),
				longArrayFrom((Long[]) data
						.get(BundleStateMBean.BUNDLE_FRAGMENTS)),
				longArrayFrom((Long[]) data.get(BundleStateMBean.BUNDLE_HOSTS)),
				longArrayFrom((Long[]) data
						.get(BundleStateMBean.BUNDLE_REQUIRED_BUNDLES)),
				longArrayFrom((Long[]) data
						.get(BundleStateMBean.BUNDLE_REQUIRING_BUNDLES)));

	}

	/**
	 * Construct an OSGiBundle representation
	 * 
	 * @param bc
	 *            - the BundleContext to be used.
	 * @param admin
	 *            - the PackageAdmin service
	 * @param sl
	 *            - the StartLevel service
	 * @param b
	 *            - the Bundle to represent
	 */
	public OSGiBundle(BundleContext bc, PackageAdmin admin, StartLevel sl,
			Bundle b) {
		this(b.getLocation(), b.getBundleId(), b.getSymbolicName(), b
				.getVersion().toString(), sl.getBundleStartLevel(b),
				getBundleState(b), b.getLastModified(),
				isBundlePersistentlyStarted(b, sl),
				isRequiredBundleRemovalPending(b, bc, admin), isBundleRequired(
						b, bc, admin), isBundleFragment(b, admin), serviceIds(b
						.getRegisteredServices()), serviceIds(b
						.getServicesInUse()), getBundleHeaders(b),
				getBundleExportedPackages(b, admin), getBundleImportedPackages(
						b, bc, admin), getBundleFragments(b, admin), Util
						.bundleIds(admin.getHosts(b)), getBundleDependencies(b,
						admin), getBundlesRequiring(b, bc, admin));
	}

	/**
	 * Construct and OSGiBundle
	 * 
	 * @param location
	 * @param identifier
	 * @param symbolicName
	 * @param version
	 * @param startLevel
	 * @param state
	 * @param lastModified
	 * @param persistentlyStarted
	 * @param removalPending
	 * @param required
	 * @param fragment
	 * @param registeredServices
	 * @param servicesInUse
	 * @param headers
	 * @param exportedPackages
	 * @param importedPackages
	 * @param fragments
	 * @param hosts
	 * @param requiredBundles
	 * @param requiringBundles
	 */
	public OSGiBundle(String location, long identifier, String symbolicName,
			String version, int startLevel, String state, long lastModified,
			boolean persistentlyStarted, boolean removalPending,
			boolean required, boolean fragment, long[] registeredServices,
			long[] servicesInUse, Map<String, String> headers,
			String[] exportedPackages, String[] importedPackages,
			long[] fragments, long[] hosts, long[] requiredBundles,
			long[] requiringBundles) {
		this.location = location;
		this.identifier = identifier;
		this.symbolicName = symbolicName;
		this.version = version;
		this.startLevel = startLevel;
		this.state = state;
		this.lastModified = lastModified;
		this.persistentlyStarted = persistentlyStarted;
		this.removalPending = removalPending;
		this.required = required;
		this.fragment = fragment;
		this.registeredServices = registeredServices;
		this.servicesInUse = servicesInUse;
		this.headers = headers;
		this.exportedPackages = exportedPackages;
		this.importedPackages = importedPackages;
		this.fragments = fragments;
		this.hosts = hosts;
		this.requiredBundles = requiredBundles;
		this.requiringBundles = requiringBundles;
	}

	/**
	 * Answer the TabularData representing the list of OSGiBundle state
	 * 
	 * @param bundles
	 *            - the list of bundles to represent
	 * 
	 * @return the Tabular data which represents the list of bundles
	 */
	public static TabularData tableFrom(ArrayList<OSGiBundle> bundles) {
		TabularDataSupport table = new TabularDataSupport(BUNDLE_TABLE);
		for (OSGiBundle bundle : bundles) {
			table.put(bundle.asCompositeData());
		}
		return table;
	}

	/**
	 * Answer the TabularData representing the list of bundle headers for a
	 * bundle
	 * 
	 * @param b
	 * @return the bundle headers
	 */
	@SuppressWarnings("unchecked")
	public static TabularData headerTable(Bundle b) {
		TabularDataSupport table = new TabularDataSupport(BUNDLE_HEADER_TABLE);
		Dictionary map = b.getHeaders();
		for (Enumeration headers = map.keys(); headers.hasMoreElements();) {
			String key = (String) headers.nextElement();
			table.put(headerData(key, (String) map.get(key)));
		}
		return table;
	}

	/**
	 * Answer the TabularData representing the supplied map of bundle headers
	 * 
	 * @param headers
	 * @return the bundle headers
	 */
	public static TabularData headerTable(Map<String, String> headers) {
		TabularDataSupport table = new TabularDataSupport(BUNDLE_HEADER_TABLE);
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			table.put(headerData(entry.getKey(), entry.getValue()));
		}
		return table;
	}

	private static TabularType createBundleTableType() {
		try {
			return new TabularType("Bundles", "The table of all bundles",
					BUNDLE, new String[] { BUNDLE_ID });
		} catch (OpenDataException e) {
			throw new IllegalStateException(
					"Unable to build bundle table type", e);
		}
	}

	@SuppressWarnings("unchecked")
	private static CompositeType createBundleType() {
		String description = "This type encapsulates OSGi bundles";
		String[] itemNames = BundleStateMBean.BUNDLE;
		OpenType[] itemTypes = new OpenType[itemNames.length];
		String[] itemDescriptions = new String[itemNames.length];
		itemTypes[0] = SimpleType.STRING;
		itemTypes[1] = SimpleType.LONG;
		itemTypes[2] = SimpleType.STRING;
		itemTypes[3] = SimpleType.STRING;
		itemTypes[4] = SimpleType.INTEGER;
		itemTypes[5] = SimpleType.STRING;
		itemTypes[6] = SimpleType.LONG;
		itemTypes[7] = SimpleType.BOOLEAN;
		itemTypes[8] = SimpleType.BOOLEAN;
		itemTypes[9] = SimpleType.BOOLEAN;
		itemTypes[10] = SimpleType.BOOLEAN;
		itemTypes[11] = LONG_ARRAY_TYPE;
		itemTypes[12] = LONG_ARRAY_TYPE;
		itemTypes[13] = BUNDLE_HEADER_TABLE;
		itemTypes[14] = STRING_ARRAY_TYPE;
		itemTypes[15] = STRING_ARRAY_TYPE;
		itemTypes[16] = LONG_ARRAY_TYPE;
		itemTypes[17] = LONG_ARRAY_TYPE;
		itemTypes[18] = LONG_ARRAY_TYPE;
		itemTypes[19] = LONG_ARRAY_TYPE;

		itemDescriptions[0] = "The location of the bundle";
		itemDescriptions[1] = "The id of the bundle";
		itemDescriptions[2] = "The symbolic name of the bundle";
		itemDescriptions[3] = "The version of the bundle";
		itemDescriptions[4] = "The start level of the bundle";
		itemDescriptions[5] = "The state of the bundle";
		itemDescriptions[6] = "The last modification time of the bundle";
		itemDescriptions[7] = "Whether the bundle is persistently started";
		itemDescriptions[8] = "Whether the bundle is pending removal";
		itemDescriptions[9] = "Whether the bundle is required";
		itemDescriptions[10] = "Whether the bundle is a fragment";
		itemDescriptions[11] = "The registered services of the bundle";
		itemDescriptions[12] = "The services in use by the bundle";
		itemDescriptions[13] = "The headers of the bundle";
		itemDescriptions[14] = "The exported packages of the bundle";
		itemDescriptions[15] = "The imported packages of the bundle";
		itemDescriptions[16] = "The fragments of which the bundle is host";
		itemDescriptions[17] = "The hosts of the bundle";
		itemDescriptions[18] = "The required bundles the bundle";
		itemDescriptions[19] = "The bundles requiring the bundle";
		try {
			return new CompositeType("Bundle", description, itemNames,
					itemDescriptions, itemTypes);
		} catch (OpenDataException e) {
			throw new IllegalStateException("Unable to build bundle type", e);
		}
	}

	@SuppressWarnings("unchecked")
	private static CompositeType createBundleHeaderType() {
		String description = "This type encapsulates OSGi bundle header key/value pairs";
		String[] itemNames = HEADER_PROPERTY_ITEM_NAMES;
		OpenType[] itemTypes = new OpenType[itemNames.length];
		String[] itemDescriptions = new String[itemNames.length];
		itemTypes[0] = SimpleType.STRING;
		itemTypes[1] = SimpleType.STRING;

		itemDescriptions[0] = "The bundle header key";
		itemDescriptions[1] = "The bundle header value";
		try {
			return new CompositeType(BundleStateMBean.BUNDLE_HEADER_TYPE,
					description, itemNames, itemDescriptions, itemTypes);
		} catch (OpenDataException e) {
			throw new IllegalStateException(
					"Unable to build bundle header type", e);
		}
	}

	private static TabularType createBundleHeaderTableType() {
		try {
			return new TabularType(BundleStateMBean.BUNDLE_HEADERS_TYPE,
					"The table of bundle headers", BUNDLE_HEADER,
					new String[] { KEY });
		} catch (OpenDataException e) {
			throw new IllegalStateException(
					"Unable to build bundle header table type", e);
		}
	}

	private static CompositeData headerData(String key, String value) {
		Object[] itemValues = new Object[HEADER_PROPERTY_ITEM_NAMES.length];
		itemValues[0] = key;
		itemValues[1] = value;

		try {
			return new CompositeDataSupport(BUNDLE_HEADER,
					HEADER_PROPERTY_ITEM_NAMES, itemValues);
		} catch (OpenDataException e) {
			throw new IllegalStateException(
					"Cannot form bundle header open data", e);
		}
	}

	/**
	 * Answer the receiver encoded as CompositeData
	 * 
	 * @return the CompositeData encoding of the receiver.
	 */
	public CompositeData asCompositeData() {
		String[] itemNames = BundleStateMBean.BUNDLE;
		Object[] itemValues = new Object[itemNames.length];
		itemValues[0] = location;
		itemValues[1] = identifier;
		itemValues[2] = symbolicName;
		itemValues[3] = startLevel;
		itemValues[4] = state;
		itemValues[5] = lastModified;
		itemValues[6] = persistentlyStarted;
		itemValues[7] = removalPending;
		itemValues[8] = required;
		itemValues[9] = fragment;
		itemValues[10] = LongArrayFrom(registeredServices);
		itemValues[11] = LongArrayFrom(servicesInUse);
		itemValues[12] = headerTable(headers);
		itemValues[13] = exportedPackages;
		itemValues[14] = importedPackages;
		itemValues[15] = LongArrayFrom(fragments);
		itemValues[16] = LongArrayFrom(hosts);
		itemValues[17] = LongArrayFrom(requiredBundles);
		itemValues[18] = LongArrayFrom(requiringBundles);

		try {
			return new CompositeDataSupport(BUNDLE, itemNames, itemValues);
		} catch (OpenDataException e) {
			throw new IllegalStateException("Cannot form bundle open data", e);
		}
	}

	/**
	 * @return The list of exported packages by this bundle, in the form of
	 *         <packageName>;<version>
	 * 
	 */
	public String[] getExportedPackages() {
		return exportedPackages;
	}

	/**
	 * @return the list of identifiers of the bundle fragments which use this
	 *         bundle as a host
	 */
	public long[] getFragments() {
		return fragments;
	}

	/**
	 * @return the map of headers for this bundle
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * @return list of identifiers of the bundles which host this fragment
	 */
	public long[] getHosts() {
		return hosts;
	}

	/**
	 * @return the identifier of this bundle
	 */
	public long getIdentifier() {
		return identifier;
	}

	/**
	 * @return The list of imported packages by this bundle, in the form of
	 *         <packageName>;<version>
	 */
	public String[] getImportedPackages() {
		return importedPackages;
	}

	/**
	 * @return the last modified time of this bundle
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * @return the name of this bundle
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @return the list of identifiers of the services registered by this bundle
	 */
	public long[] getRegisteredServices() {
		return registeredServices;
	}

	/**
	 * @return the list of identifiers of bundles required by this bundle
	 */
	public long[] getRequiredBundles() {
		return requiredBundles;
	}

	/**
	 * @return the list of identifiers of bundles which require this bundle
	 */
	public long[] getRequiringBundles() {
		return requiringBundles;
	}

	/**
	 * @return the list of identifiers of services in use by this bundle
	 */
	public long[] getServicesInUse() {
		return servicesInUse;
	}

	/**
	 * @return the start level of this bundle
	 */
	public int getStartLevel() {
		return startLevel;
	}

	/**
	 * @return the state of this bundle
	 */
	public String getState() {
		return state;
	}

	/**
	 * @return the symbolic name of this bundle
	 */
	public String getSymbolicName() {
		return symbolicName;
	}
	
	/**
	 * @return the version of this bundle
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return true if this bundle represents a fragment
	 */
	public boolean isFragment() {
		return fragment;
	}

	/**
	 * @return true if this bundle is persistently started
	 */
	public boolean isPersistentlyStarted() {
		return persistentlyStarted;
	}

	/**
	 * @return true if this bundle is pending removal
	 */
	public boolean isRemovalPending() {
		return removalPending;
	}

	/**
	 * @return true if this bundle is required
	 */
	public boolean isRequired() {
		return required;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, String> mapFrom(TabularData data) {
		Map<String, String> headers = new HashMap<String, String>();
		Set<List<?>> keySet = (Set<List<?>>) data.keySet();
		for (List<?> key : keySet) {
			headers.put((String) key.get(0), (String) data.get(
					new Object[] { key.get(0) }).get(VALUE));

		}
		return headers;
	}

	/**
	 * The CompositeType which represents a key/value header pair
	 */
	public final static CompositeType BUNDLE_HEADER = createBundleHeaderType();

	/**
	 * The TabularType which represents the map of bundle headers
	 */
	public static final TabularType BUNDLE_HEADER_TABLE = createBundleHeaderTableType();

	/**
	 * The CompositeType which represents a single OSGi bundle
	 */
	public final static CompositeType BUNDLE = createBundleType();

	/**
	 * The TabularType which represents a list of bundles
	 */
	public final static TabularType BUNDLE_TABLE = createBundleTableType();

	private String[] exportedPackages;
	private boolean fragment;
	private long[] fragments;
	private Map<String, String> headers;
	private long[] hosts;
	private long identifier;
	private String[] importedPackages;
	private long lastModified;
	private String location;
	private boolean persistentlyStarted;
	private long[] registeredServices;
	private boolean removalPending;
	private boolean required;
	private long[] requiredBundles;
	private long[] requiringBundles;
	private long[] servicesInUse;
	private int startLevel;
	private String state;
	private String symbolicName;
	private String version;
}
