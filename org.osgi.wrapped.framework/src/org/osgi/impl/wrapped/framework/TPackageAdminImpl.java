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


package org.osgi.impl.wrapped.framework;

import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.service.packageadmin.TExportedPackage;
import org.osgi.wrapped.service.packageadmin.TPackageAdmin;
import org.osgi.wrapped.service.packageadmin.TRequiredBundle;

public class TPackageAdminImpl implements TPackageAdmin {
	final PackageAdmin	pa;

	TPackageAdminImpl(PackageAdmin pa) {
		this.pa = pa;
	}

	public TBundle getBundle(Class< ? > clazz) {
		return T.toTBundle(pa.getBundle(clazz));
	}

	public int getBundleType(TBundle bundle) {
		return pa.getBundleType(T.unwrap(bundle));
	}

	public TBundle[] getBundles(String symbolicName, String versionRange) {
		Bundle[] bundles = pa.getBundles(symbolicName, versionRange);
		return T.toTBundles(bundles);
	}

	public TExportedPackage getExportedPackage(String name) {
		return T.toTExportedPackage(pa.getExportedPackage(name));
	}

	public TExportedPackage[] getExportedPackages(TBundle bundle) {
		return T
				.toTExportedPackages(pa
						.getExportedPackages((bundle == null) ? null : T
								.unwrap(bundle)));
	}

	public TExportedPackage[] getExportedPackages(String name) {
		return T.toTExportedPackages(pa.getExportedPackages(name));
	}

	public TBundle[] getFragments(TBundle bundle) {
		return T.toTBundles(pa.getFragments(T.unwrap(bundle)));
	}

	public TBundle[] getHosts(TBundle bundle) {
		return T.toTBundles(pa.getHosts(T.unwrap(bundle)));
	}

	public TRequiredBundle[] getRequiredBundles(String symbolicName) {
		return T.toTRequiredBundles(pa.getRequiredBundles(symbolicName));
	}

	public void refreshPackages(TBundle[] bundles) {
		pa.refreshPackages((bundles == null) ? null : T.toBundles(bundles));
	}

	public boolean resolveBundles(TBundle[] bundles) {
		return pa.resolveBundles((bundles == null) ? null : T
				.toBundles(bundles));
	}
	
	@Override
	public boolean equals(Object o) {
		return pa.equals(T.unwrap((TPackageAdmin) o));
	}

	@Override
	public int hashCode() {
		return pa.hashCode();
	}

	@Override
	public String toString() {
		return pa.toString();
	}
}
