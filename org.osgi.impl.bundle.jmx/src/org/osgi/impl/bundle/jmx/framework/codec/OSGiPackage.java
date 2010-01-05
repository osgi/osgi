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
import static org.osgi.impl.bundle.jmx.codec.Util.longArrayFrom;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;

import org.osgi.impl.bundle.jmx.codec.Util;
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
public class OSGiPackage {

	/**
	 * Construct an OSGiPackage from the encoded <link>CompositeData</link>
	 * 
	 * @param data
	 *            - the <link>CompositeData</link> encoding the OSGiPackage
	 */
	@SuppressWarnings("boxing")
	public OSGiPackage(CompositeData data) {
		this((String) data.get(PackageStateMBean.NAME), (String) data
				.get(PackageStateMBean.VERSION), (Boolean) data
				.get(PackageStateMBean.REMOVAL_PENDING),
				longArrayFrom((Long[]) data
						.get(PackageStateMBean.EXPORTING_BUNDLES)),
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
				pkg.isRemovalPending(), new long[] { pkg.getExportingBundle()
						.getBundleId() }, Util.bundleIds(pkg
						.getImportingBundles()));
	}

	/**
	 * Construct and OSGiPackage from the supplied data
	 * 
	 * @param name
	 * @param version
	 * @param removalPending
	 * @param exportingBundles
	 * @param importingBundles
	 */
	public OSGiPackage(String name, String version, boolean removalPending,
			long[] exportingBundles, long[] importingBundles) {
		this.name = name;
		this.version = version;
		this.removalPending = removalPending;
		this.exportingBundles = exportingBundles;
		this.importingBundles = importingBundles;
	}

	/**
	 * Construct the tabular data from the list of OSGiPacakges
	 * 
	 * @param packages
	 * @return the tabular data representation of the OSGPacakges
	 */
	public static TabularData tableFrom(Set<OSGiPackage> packages) {
		TabularDataSupport table = new TabularDataSupport(
				PackageStateMBean.PACKAGES_TYPE);
		for (OSGiPackage pkg : packages) {
			table.put(pkg.asCompositeData());
		}
		return table;
	}

	/**
	 * Answer the receiver encoded as CompositeData
	 * 
	 * @return the CompositeData encoding of the receiver.
	 */
	@SuppressWarnings("boxing")
	public CompositeData asCompositeData() {
		Map<String, Object> items = new HashMap<String, Object>();
		items.put(PackageStateMBean.NAME, name);
		items.put(PackageStateMBean.VERSION, version);
		items.put(PackageStateMBean.REMOVAL_PENDING, removalPending);
		items.put(PackageStateMBean.EXPORTING_BUNDLES,
				LongArrayFrom(exportingBundles));
		items.put(PackageStateMBean.IMPORTING_BUNDLES,
				LongArrayFrom(importingBundles));

		try {
			return new CompositeDataSupport(PackageStateMBean.PACKAGE_TYPE,
					items);
		} catch (OpenDataException e) {
			throw new IllegalStateException("Cannot form package open data", e);
		}
	}

	/**
	 * @return the identifier of the exporting bundles
	 */
	public long[] getExportingBundles() {
		return exportingBundles;
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

	private long[] exportingBundles;
	private long[] importingBundles;
	private String name;
	private boolean removalPending;
	private String version;
}
