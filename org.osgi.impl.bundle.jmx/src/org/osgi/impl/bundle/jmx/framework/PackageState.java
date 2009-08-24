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
package org.osgi.impl.bundle.jmx.framework;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.management.openmbean.TabularData;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.jmx.codec.OSGiPackage;
import org.osgi.jmx.framework.PackageStateMBean;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

/** 
 * 
 */
public class PackageState implements PackageStateMBean {
	public PackageState(BundleContext bc, PackageAdmin admin) {
		this.bc = bc;
		this.admin = admin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.jmx.core.PackageStateMBean#getExportingBundle(java.lang.String,
	 * java.lang.String)
	 */
	public long getExportingBundle(String packageName, String version)
			throws IOException {
		Version v = Version.parseVersion(version);
		ExportedPackage[] exportedPackages = admin.getExportedPackages(packageName);
		if (exportedPackages == null) {
			return -1;
		}
		for (ExportedPackage pkg : exportedPackages) {
			if (pkg.getVersion().equals(v)) {
				return pkg.getExportingBundle().getBundleId();
			}
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.jmx.core.PackageStateMBean#getImportingBundles(java.lang.String,
	 * java.lang.String)
	 */
	public long[] getImportingBundles(String packageName, String version)
			throws IOException {
		Version v = Version.parseVersion(version);
		ExportedPackage[] exportedPackages = admin.getExportedPackages(packageName);
		if (exportedPackages == null) {
			return new long[0];
		}
		for (ExportedPackage pkg : exportedPackages) {
			if (pkg.getVersion().equals(v)) {
				Bundle[] bundles = pkg.getImportingBundles();
				long[] ids = new long[bundles.length];
				for (int i = 0; i < bundles.length; i++) {
					ids[i] = bundles[i].getBundleId();
				}
				return ids;
			}
		}
		return new long[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.PackageStateMBean#getPackages()
	 */
	public TabularData getPackages() {
		Set<OSGiPackage> packages = new HashSet<OSGiPackage>();
		for (Bundle bundle : bc.getBundles()) {
			ExportedPackage[] pkgs = admin.getExportedPackages(bundle);
			if (pkgs != null) {
				for (ExportedPackage pkg : pkgs) {
					packages.add(new OSGiPackage(pkg));
				} 
			}
		}
		return OSGiPackage.tableFrom(packages);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.jmx.core.PackageStateMBean#isRemovalPending(java.lang.String,
	 * java.lang.String)
	 */
	public boolean isRemovalPending(String packageName, String version)
			throws IOException {
		Version v = Version.parseVersion(version);
		ExportedPackage[] exportedPackages = admin.getExportedPackages(packageName);
		if (exportedPackages == null) {
			return false;
		}
		for (ExportedPackage pkg : exportedPackages) {
			if (pkg.getVersion().equals(v)) {
				return pkg.isRemovalPending();
			}
		}
		return false;
	}

	protected BundleContext bc;
	protected PackageAdmin admin;
}
