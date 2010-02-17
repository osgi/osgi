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

package org.osgi.impl.bundle.jmx.framework.codec;

import static org.osgi.impl.bundle.jmx.codec.Util.LongArrayFrom;
import static org.osgi.impl.bundle.jmx.codec.Util.getBundleExportedPackages;
import static org.osgi.impl.bundle.jmx.codec.Util.getBundleFragments;
import static org.osgi.impl.bundle.jmx.codec.Util.getBundleHeaders;
import static org.osgi.impl.bundle.jmx.codec.Util.getBundleImportedPackages;
import static org.osgi.impl.bundle.jmx.codec.Util.getBundleState;
import static org.osgi.impl.bundle.jmx.codec.Util.getBundlesRequiring;
import static org.osgi.impl.bundle.jmx.codec.Util.getDependencies;
import static org.osgi.impl.bundle.jmx.codec.Util.isBundleFragment;
import static org.osgi.impl.bundle.jmx.codec.Util.isBundlePersistentlyStarted;
import static org.osgi.impl.bundle.jmx.codec.Util.isBundleRequired;
import static org.osgi.impl.bundle.jmx.codec.Util.isRequiredBundleRemovalPending;
import static org.osgi.impl.bundle.jmx.codec.Util.longArrayFrom;
import static org.osgi.impl.bundle.jmx.codec.Util.serviceIds;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.impl.bundle.jmx.codec.Util;
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
	@SuppressWarnings("boxing")
	public OSGiBundle(CompositeData data) {
		this((String) data.get(BundleStateMBean.LOCATION), ((Long) data
				.get(BundleStateMBean.IDENTIFIER)).longValue(), (String) data
				.get(BundleStateMBean.SYMBOLIC_NAME), (String) data
				.get(BundleStateMBean.VERSION), ((Integer) data
				.get(BundleStateMBean.START_LEVEL)).intValue(), (String) data
				.get(BundleStateMBean.STATE), ((Long) data
				.get(BundleStateMBean.LAST_MODIFIED)).longValue(),
				(Boolean) data.get(BundleStateMBean.PERSISTENTLY_STARTED),
				(Boolean) data.get(BundleStateMBean.REMOVAL_PENDING),
				(Boolean) data.get(BundleStateMBean.REQUIRED), (Boolean) data
						.get(BundleStateMBean.FRAGMENT),
				longArrayFrom((Long[]) data
						.get(BundleStateMBean.REGISTERED_SERVICES)),
				longArrayFrom((Long[]) data
						.get(BundleStateMBean.SERVICES_IN_USE)),
				mapFrom((TabularData) data.get(BundleStateMBean.HEADERS)),
				(String[]) data.get(BundleStateMBean.EXPORTED_PACKAGES),
				(String[]) data.get(BundleStateMBean.IMPORTED_PACKAGES),
				longArrayFrom((Long[]) data.get(BundleStateMBean.FRAGMENTS)),
				longArrayFrom((Long[]) data.get(BundleStateMBean.HOSTS)),
				longArrayFrom((Long[]) data
						.get(BundleStateMBean.REQUIRED_BUNDLES)),
				longArrayFrom((Long[]) data
						.get(BundleStateMBean.REQUIRING_BUNDLES)));

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
						.bundleIds(admin.getHosts(b)), getDependencies(b,
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
		TabularDataSupport table = new TabularDataSupport(
				BundleStateMBean.BUNDLES_TYPE);
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
		TabularDataSupport table = new TabularDataSupport(
				BundleStateMBean.HEADERS_TYPE);
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
		TabularDataSupport table = new TabularDataSupport(
				BundleStateMBean.HEADERS_TYPE);
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			table.put(headerData(entry.getKey(), entry.getValue()));
		}
		return table;
	}

	private static CompositeData headerData(String key, String value) {
		Object[] itemValues = new Object[HEADER_PROPERTY_ITEM_NAMES.length];
		itemValues[0] = key;
		itemValues[1] = value;

		try {
			return new CompositeDataSupport(BundleStateMBean.HEADER_TYPE,
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
	@SuppressWarnings("boxing")
	public CompositeData asCompositeData() {
		Map<String, Object> items = new HashMap<String, Object>();
		items.put(BundleStateMBean.LOCATION, location);
		items.put(BundleStateMBean.IDENTIFIER, identifier);
		items.put(BundleStateMBean.SYMBOLIC_NAME, symbolicName);
		items.put(BundleStateMBean.VERSION, version);
		items.put(BundleStateMBean.START_LEVEL, startLevel);
		items.put(BundleStateMBean.STATE, state);
		items.put(BundleStateMBean.LAST_MODIFIED, lastModified);
		items.put(BundleStateMBean.PERSISTENTLY_STARTED, persistentlyStarted);
		items.put(BundleStateMBean.REMOVAL_PENDING, removalPending);
		items.put(BundleStateMBean.REQUIRED, required);
		items.put(BundleStateMBean.FRAGMENT, fragment);
		items.put(BundleStateMBean.REGISTERED_SERVICES,
				LongArrayFrom(registeredServices));
		items.put(BundleStateMBean.SERVICES_IN_USE,
				LongArrayFrom(servicesInUse));
		items.put(BundleStateMBean.HEADERS, headerTable(headers));
		items.put(BundleStateMBean.EXPORTED_PACKAGES, exportedPackages);
		items.put(BundleStateMBean.IMPORTED_PACKAGES, importedPackages);
		items.put(BundleStateMBean.FRAGMENTS, LongArrayFrom(fragments));
		items.put(BundleStateMBean.HOSTS, LongArrayFrom(hosts));
		items.put(BundleStateMBean.REQUIRING_BUNDLES,
				LongArrayFrom(requiringBundles));
		items.put(BundleStateMBean.REQUIRED_BUNDLES,
				LongArrayFrom(requiredBundles));

		try {
			return new CompositeDataSupport(BundleStateMBean.BUNDLE_TYPE, items);
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

	@SuppressWarnings( { "unchecked", "cast" })
	private static Map<String, String> mapFrom(TabularData data) {
		Map<String, String> headers = new HashMap<String, String>();
		Set<List<?>> keySet = (Set<List<?>>) data.keySet();
		for (List<?> key : keySet) {
			headers.put((String) key.get(0), (String) data.get(
					new Object[] { key.get(0) }).get(VALUE));

		}
		return headers;
	}

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
