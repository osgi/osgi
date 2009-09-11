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
import static org.osgi.jmx.codec.Util.longArrayFrom;
import static org.osgi.jmx.framework.PackageStateMBean.BUNDLE_IDENTIFIER;
import static org.osgi.jmx.framework.PackageStateMBean.IMPORTING_BUNDLES;
import static org.osgi.jmx.framework.PackageStateMBean.PACKAGE_NAME;
import static org.osgi.jmx.framework.PackageStateMBean.PACKAGE_PENDING_REMOVAL;
import static org.osgi.jmx.framework.PackageStateMBean.PACKAGE_VERSION;

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

import org.osgi.jmx.framework.PackageStateMBean;
import org.osgi.service.packageadmin.ExportedPackage;

/**
 * <p>
 * This class represents the CODEC for the composite data representing an OSGi
 * <link>ExportedPackage</link>
 * <p>
 * It serves as both the documentation of the type structure and as the
 * codification of the mechanism to convert to/from the CompositeData.
 * <p>
 * The structure of the composite data is:
 * <table border="1">
 * <tr>
 * <td>Name</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>Version</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>PendingRemoval</td>
 * <td>boolean</td>
 * </tr>
 * <tr>
 * <td>BundleIdentifier</td>
 * <td>long</td>
 * </tr>
 * <tr>
 * <td>ImportingBundles</td>
 * <td>Array of long</td>
 * </tr>
 * </table>
 */
@SuppressWarnings("unchecked")
public class OSGiPackage {

	/**
	 * The item names in the CompositeData representing the OSGi Package
	 */
	private static final String[] PACKAGE_ITEM_NAMES = { PACKAGE_NAME,
			PACKAGE_VERSION, PACKAGE_PENDING_REMOVAL, BUNDLE_IDENTIFIER,
			IMPORTING_BUNDLES };

	/**
	 * Construct an OSGiPackage from the encoded <link>CompositeData</link>
	 * 
	 * @param data
	 *            - the <link>CompositeData</link> encoding the OSGiPackage
	 */
	public OSGiPackage(CompositeData data) {
		this((String) data.get(PackageStateMBean.PACKAGE_NAME), (String) data
				.get(PackageStateMBean.PACKAGE_VERSION), (Boolean) data
				.get(PackageStateMBean.PACKAGE_PENDING_REMOVAL), (Long) data
				.get(PackageStateMBean.BUNDLE_IDENTIFIER),
				longArrayFrom((Long[]) data
						.get(PackageStateMBean.IMPORTING_BUNDLES)));
	}

	/**
	 * Construct an OSGiPackage from the <link>ExporetedPackage</link>
	 * 
	 * @param pkg
	 *            - the <link>ExporetedPackage</link>
	 */
	public OSGiPackage(ExportedPackage pkg) {
		this(pkg.getName(), pkg.getVersion().toString(),
				pkg.isRemovalPending(), pkg.getExportingBundle().getBundleId(),
				Util.bundleIds(pkg.getImportingBundles()));
	}

	/**
	 * Construct and OSGiPackage from the supplied data
	 * 
	 * @param name
	 * @param version
	 * @param removalPending
	 * @param exportingBundle
	 * @param importingBundles
	 */
	public OSGiPackage(String name, String version, boolean removalPending,
			long exportingBundle, long[] importingBundles) {
		this.name = name;
		this.version = version;
		this.removalPending = removalPending;
		this.exportingBundle = exportingBundle;
		this.importingBundles = importingBundles;
	}

	/**
	 * Construct the tabular data from the list of OSGiPacakges
	 * 
	 * @param packages
	 * @return the tabular data representation of the OSGPacakges
	 */
	public static TabularData tableFrom(Set<OSGiPackage> packages) {
		TabularDataSupport table = new TabularDataSupport(PACKAGE_TABLE);
		for (OSGiPackage pkg : packages) {
			table.put(pkg.asCompositeData());
		}
		return table;
	}

	private static TabularType createPackageTableType() {
		try {
			return new TabularType("Packages", "The table of all packages",
					PACKAGE, new String[] { BUNDLE_IDENTIFIER, PACKAGE_NAME });
		} catch (OpenDataException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static CompositeType createPackageType() {
		String description = "This type encapsulates an OSGi package";
		String[] itemNames = PACKAGE_ITEM_NAMES;
		OpenType[] itemTypes = new OpenType[itemNames.length];
		String[] itemDescriptions = new String[itemNames.length];
		itemTypes[0] = SimpleType.STRING;
		itemTypes[1] = SimpleType.STRING;
		itemTypes[2] = SimpleType.BOOLEAN;
		itemTypes[3] = SimpleType.LONG;
		itemTypes[4] = LONG_ARRAY_TYPE;

		itemDescriptions[0] = "The package name";
		itemDescriptions[1] = "The package version";
		itemDescriptions[2] = "Whether the package is pending removal";
		itemDescriptions[3] = "The bundle the package belongs to";
		itemDescriptions[4] = "The importing bundles of the package";

		try {
			return new CompositeType("Package", description, itemNames,
					itemDescriptions, itemTypes);
		} catch (OpenDataException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Answer the receiver encoded as CompositeData
	 * 
	 * @return the CompositeData encoding of the receiver.
	 */
	public CompositeData asCompositeData() {
		String[] itemNames = PACKAGE_ITEM_NAMES;
		Object[] itemValues = new Object[5];
		itemValues[0] = name;
		itemValues[1] = version;
		itemValues[2] = removalPending;
		itemValues[3] = exportingBundle;
		itemValues[4] = LongArrayFrom(importingBundles);

		try {
			return new CompositeDataSupport(PACKAGE, itemNames, itemValues);
		} catch (OpenDataException e) {
			throw new IllegalStateException("Cannot form package open data", e);
		}
	}

	/**
	 * @return the identifier of the exporting bundle
	 */
	public long getExportingBundle() {
		return exportingBundle;
	}

	/**
	 * @return the list of identifiers of the bundles importing this package
	 */
	public long[] getImportingBundles() {
		return importingBundles;
	}

	/**
	 * @return the name of the package
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the version of the package
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return true if the package is pending removal
	 */
	public boolean isRemovalPending() {
		return removalPending;
	}

	/**
	 * The CompositeType representation of the package
	 */
	public final static CompositeType PACKAGE = createPackageType();

	/**
	 * The TabularType representation of a list of packages
	 */
	public final static TabularType PACKAGE_TABLE = createPackageTableType();

	private long exportingBundle;

	private long[] importingBundles;

	private String name;
	private boolean removalPending;
	private String version;
}
