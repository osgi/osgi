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


package org.osgi.thunk.framework;

import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.ExportedPackage;
import org.osgi.framework.Version;
import org.osgi.wrapped.service.packageadmin.TExportedPackage;

public class ExportedPackageImpl implements ExportedPackage {
	final TExportedPackage	ep;

	ExportedPackageImpl(TExportedPackage ep) {
		this.ep = ep;
	}

	public Bundle getExportingBundle() {
		return T.toBundle(ep.getExportingBundle());
	}

	public Collection<Bundle> getImportingBundles() {
		return T.toBundles(ep.getImportingBundles());
	}

	public String getName() {
		return ep.getName();
	}

	public Version getVersion() {
		return T.toVersion(ep.getVersion());
	}

	public boolean isRemovalPending() {
		return ep.isRemovalPending();
	}

	@Override
	public boolean equals(Object o) {
		return ep.equals(T.unwrap((ExportedPackage) o));
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
