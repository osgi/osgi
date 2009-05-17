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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.framework.TBundleContext;
import org.osgi.wrapped.framework.TBundleException;
import org.osgi.wrapped.framework.TServiceReference;
import org.osgi.wrapped.framework.TVersion;

public class TBundleImpl implements TBundle {
	final Bundle	bundle;

	TBundleImpl(Bundle bundle) {
		this.bundle = bundle;
	}

	@SuppressWarnings("unchecked")
	public Enumeration findEntries(String path, String filePattern,
			boolean recurse) {
		return bundle.findEntries(path, filePattern, recurse);
	}

	public TBundleContext getBundleContext() {
		return new TBundleContextImpl(bundle.getBundleContext());
	}

	public long getBundleId() {
		return bundle.getBundleId();
	}

	public URL getEntry(String path) {
		return bundle.getEntry(path);
	}

	@SuppressWarnings("unchecked")
	public Enumeration getEntryPaths(String path) {
		return bundle.getEntryPaths(path);
	}

	@SuppressWarnings("unchecked")
	public Dictionary getHeaders() {
		return bundle.getHeaders();
	}

	@SuppressWarnings("unchecked")
	public Dictionary getHeaders(String locale) {
		return bundle.getHeaders(locale);
	}

	public long getLastModified() {
		return bundle.getLastModified();
	}

	public String getLocation() {
		return bundle.getLocation();
	}

	public TServiceReference[] getRegisteredServices() {
		ServiceReference[] references = bundle.getRegisteredServices();
		return T.toTServiceReferences(references);
	}

	public URL getResource(String name) {
		return bundle.getResource(name);
	}

	@SuppressWarnings("unchecked")
	public Enumeration getResources(String name) throws IOException {
		return bundle.getResources(name);
	}

	public TServiceReference[] getServicesInUse() {
		ServiceReference[] references = bundle.getServicesInUse();
		return T.toTServiceReferences(references);
	}

	@SuppressWarnings("unchecked")
	public Map getSignerCertificates(int signersType) {
		return bundle.getSignerCertificates(signersType);
	}

	public int getState() {
		return bundle.getState();
	}

	public String getSymbolicName() {
		return bundle.getSymbolicName();
	}

	public TVersion getVersion() {
		Version version = bundle.getVersion();
		return T.toTVersion(version);
	}

	public boolean hasPermission(Object permission) {
		return bundle.hasPermission(permission);
	}

	public Class< ? > loadClass(String name) throws ClassNotFoundException {
		return bundle.loadClass(name);
	}

	public void start(int options) throws TBundleException {
		try {
			bundle.start(options);
		}
		catch (BundleException e) {
			throw T.toTBundleException(e);
		}
	}

	public void start() throws TBundleException {
		start(0);
	}

	public void stop(int options) throws TBundleException {
		try {
			bundle.stop(options);
		}
		catch (BundleException e) {
			throw T.toTBundleException(e);
		}
	}

	public void stop() throws TBundleException {
		stop(0);
	}

	public void uninstall() throws TBundleException {
		try {
			bundle.uninstall();
		}
		catch (BundleException e) {
			throw T.toTBundleException(e);
		}
	}

	public void update(InputStream input) throws TBundleException {
		try {
			bundle.update(input);
		}
		catch (BundleException e) {
			throw T.toTBundleException(e);
		}
	}

	public void update() throws TBundleException {
		update(null);
	}

	@Override
	public boolean equals(Object o) {
		return bundle.equals(T.unwrap((TBundle) o));
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
