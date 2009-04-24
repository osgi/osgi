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
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.framework.TBundleException;
import org.osgi.wrapped.framework.TServiceReference;
import org.osgi.wrapped.framework.TVersion;

public class BundleImpl implements Bundle {
	final TBundle	bundle;

	BundleImpl(TBundle bundle) {
		this.bundle = bundle;
	}

	public Iterable<URL> findEntries(String path, String filePattern,
			boolean recurse) {
		@SuppressWarnings("unchecked")
		Enumeration<URL> e = bundle.findEntries(path, filePattern, recurse);
		return Collections.list(e);
	}

	public BundleContext getBundleContext() {
		return new BundleContextImpl(bundle.getBundleContext());
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

	public Collection< ? extends ServiceReference> getRegisteredServices() {
		TServiceReference[] references = bundle.getRegisteredServices();
		return T.getReferences(references);
	}

	public URL getResource(String name) {
		return bundle.getResource(name);
	}

	public Iterable<URL> getResources(String name) throws IOException {
		@SuppressWarnings("unchecked")
		Enumeration<URL> e = bundle.getResources(name);
		return Collections.list(e);
	}

	public Collection< ? extends ServiceReference> getServicesInUse() {
		TServiceReference[] references = bundle.getServicesInUse();
		return T.getReferences(references);
	}

	@SuppressWarnings("unchecked")
	public Map<X509Certificate, List<X509Certificate>> getSignerCertificates(
			int signersType) {
		return bundle.getSignerCertificates(signersType);
	}

	public int getState() {
		return bundle.getState();
	}

	public String getSymbolicName() {
		return bundle.getSymbolicName();
	}

	public Version getVersion() {
		TVersion version = bundle.getVersion();
		if (version == TVersion.emptyVersion) {
			return Version.emptyVersion;
		}
		return new Version(version.getMajor(), version.getMinor(), version
				.getMicro(), version.getQualifier());
	}

	public boolean hasPermission(Permission permission) {
		return bundle.hasPermission(permission);
	}

	public Class< ? > loadClass(String name) throws ClassNotFoundException {
		return bundle.loadClass(name);
	}

	public void start() throws BundleException {
		start(0);
	}

	public void start(int options) throws BundleException {
		try {
			bundle.start(options);
		}
		catch (TBundleException e) {
			throw T.toBundleException(e);
		}
	}

	public void stop() throws BundleException {
		stop(0);
	}

	public void stop(int options) throws BundleException {
		try {
			bundle.stop(options);
		}
		catch (TBundleException e) {
			throw T.toBundleException(e);
		}
	}

	public void uninstall() throws BundleException {
		try {
			bundle.uninstall();
		}
		catch (TBundleException e) {
			throw T.toBundleException(e);
		}
	}

	public void update() throws BundleException {
		update(null);
	}

	public void update(InputStream input) throws BundleException {
		try {
			bundle.update(input);
		}
		catch (TBundleException e) {
			throw T.toBundleException(e);
		}
	}

	@Override
	public boolean equals(Object o) {
		return bundle.equals(T.getWrapped((Bundle) o));
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
