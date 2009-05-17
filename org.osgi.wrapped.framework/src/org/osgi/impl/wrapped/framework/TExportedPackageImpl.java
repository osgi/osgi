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

import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.framework.TVersion;
import org.osgi.wrapped.service.packageadmin.TExportedPackage;

public class TExportedPackageImpl implements TExportedPackage {
	final ExportedPackage	ep;

	TExportedPackageImpl(ExportedPackage ep) {
		this.ep = ep;
	}

	public TBundle getExportingBundle() {
		return T.toTBundle(ep.getExportingBundle());
	}

	public TBundle[] getImportingBundles() {
		return T.toTBundles(ep.getImportingBundles());
	}

	public String getName() {
		return ep.getName();
	}

	@SuppressWarnings("deprecation")
	public String getSpecificationVersion() {
		return ep.getSpecificationVersion();
	}

	public TVersion getVersion() {
		return T.toTVersion(ep.getVersion());
	}

	public boolean isRemovalPending() {
		return ep.isRemovalPending();
	}

	@Override
	public boolean equals(Object o) {
		return ep.equals(T.unwrap((TExportedPackage) o));
	}

	@Override
	public int hashCode() {
		return ep.hashCode();
	}

	@Override
	public String toString() {
		return ep.toString();
	}
}
