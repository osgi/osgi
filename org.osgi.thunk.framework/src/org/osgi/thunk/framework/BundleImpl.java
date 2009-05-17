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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Permission;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ExportedPackage;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.framework.TBundleException;
import org.osgi.wrapped.framework.TServiceReference;
import org.osgi.wrapped.service.packageadmin.TPackageAdmin;
import org.osgi.wrapped.service.packageadmin.TRequiredBundle;

public class BundleImpl implements Bundle {
	final TBundle	bundle;

	BundleImpl(TBundle bundle) {
		this.bundle = bundle;
	}

	public BundleContext getBundleContext() {
		return new BundleContextImpl(bundle.getBundleContext());
	}

	public Iterable<URL> findEntries(String path, String filePattern,
			boolean recurse) {
		@SuppressWarnings("unchecked")
		Enumeration<URL> e = bundle.findEntries(path, filePattern, recurse);
		return Collections.list(e);
	}

	public long getBundleId() {
		return bundle.getBundleId();
	}

	public URL getEntry(String path) {
		return bundle.getEntry(path);
	}

	public Iterable<String> getEntryPaths(String path) {
		@SuppressWarnings("unchecked")
		Enumeration<String> e = bundle.getEntryPaths(path);
		return Collections.list(e);
	}

	public Map<String, String> getHeaders() {
		@SuppressWarnings("unchecked")
		Dictionary<String, String> headers = bundle.getHeaders();
		return T.toMap(headers);
	}

	public Map<String, String> getHeaders(String locale) {
		@SuppressWarnings("unchecked")
		Dictionary<String, String> headers = bundle.getHeaders(locale);
		return T.toMap(headers);
	}

	public long getLastModified() {
		return bundle.getLastModified();
	}

	public String getLocation() {
		return bundle.getLocation();
	}

	public Collection<ServiceReference< ? >> getRegisteredServices() {
		TServiceReference[] references = bundle.getRegisteredServices();
		return T.toReferences(references);
	}

	public URL getResource(String name) {
		return bundle.getResource(name);
	}

	public Iterable<URL> getResources(String name) throws IOException {
		@SuppressWarnings("unchecked")
		Enumeration<URL> e = bundle.getResources(name);
		return Collections.list(e);
	}

	public Collection<ServiceReference< ? >> getServicesInUse() {
		TServiceReference[] references = bundle.getServicesInUse();
		return T.toReferences(references);
	}

	@SuppressWarnings("unchecked")
	public Map<X509Certificate, List<X509Certificate>> getSignerCertificates(
			SignerOption signersType) {
		return bundle.getSignerCertificates(signersType.getValue());
	}

	public State getState() {
		int value = bundle.getState();
		for (State state : State.values()) {
			if (state.getValue() == value) {
				return state;
			}
		}
		throw new AssertionError("unknown Bundle state: " + value);
	}

	public String getSymbolicName() {
		return bundle.getSymbolicName();
	}

	public Version getVersion() {
		return T.toVersion(bundle.getVersion());
	}

	public boolean hasPermission(Permission permission) {
		return bundle.hasPermission(permission);
	}

	public Class< ? > loadClass(String name) throws ClassNotFoundException {
		return bundle.loadClass(name);
	}

	public void start(StartOption... options) {
		// EnumSet<StartOption> set = EnumSet.copyOf(Arrays.asList(options));
		int value = 0;
		for (StartOption option : options) {
			value |= option.getValue();
		}
		try {
			bundle.start(value);
		}
		catch (TBundleException e) {
			throw T.toBundleException(e);
		}
	}

	public void stop(StopOption... options) {
		int value = 0;
		for (StopOption option : options) {
			value |= option.getValue();
		}
		try {
			bundle.stop(value);
		}
		catch (TBundleException e) {
			throw T.toBundleException(e);
		}
	}

	public void uninstall() {
		try {
			bundle.uninstall();
		}
		catch (TBundleException e) {
			throw T.toBundleException(e);
		}
	}

	public void update(InputStream input) {
		try {
			bundle.update(input);
		}
		catch (TBundleException e) {
			throw T.toBundleException(e);
		}
	}

	public Collection<ExportedPackage> getExportedPackages() {
		return T.toExportedPackages(Activator.getTPackageAdmin()
				.getExportedPackages(bundle));
	}

	public Collection<Bundle> getFragments() {
		return T.toBundles(Activator.getTPackageAdmin().getFragments(bundle));
	}

	public Collection<Bundle> getHosts() {
		return T.toBundles(Activator.getTPackageAdmin().getHosts(bundle));
	}

	public EnumSet<Type> getTypes() {
		if (Activator.getTPackageAdmin().getBundleType(bundle) == TPackageAdmin.BUNDLE_TYPE_FRAGMENT) {
			return EnumSet.of(Type.FRAGMENT);
		}
		return EnumSet.of(Type.BUNDLE);
	}

	public Collection<Bundle> getRequiringBundles() {
		TRequiredBundle[] trbs = Activator.getTPackageAdmin()
				.getRequiredBundles(getSymbolicName());
		if (trbs == null) {
			return Collections.emptyList();
		}
		ArrayList<Bundle> bundles = new ArrayList<Bundle>(trbs.length);
		for (TRequiredBundle trb : trbs) {
			TBundle tbundle = trb.getBundle();
			if (tbundle.equals(bundle)) {
				bundles.add(new BundleImpl(tbundle));
			}
		}
		return bundles;
	}

	public boolean isRemovalPending() {
		TRequiredBundle[] trbs = Activator.getTPackageAdmin()
				.getRequiredBundles(getSymbolicName());
		if (trbs == null) {
			return false;
		}
		for (TRequiredBundle trb : trbs) {
			if (trb.isRemovalPending()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		return bundle.equals(T.unwrap((Bundle) o));
	}

	@Override
	public int hashCode() {
		return bundle.hashCode();
	}

	@Override
	public String toString() {
		return bundle.toString();
	}

}
