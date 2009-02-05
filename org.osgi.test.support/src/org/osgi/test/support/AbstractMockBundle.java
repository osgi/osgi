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

package org.osgi.test.support;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

public abstract class AbstractMockBundle implements Bundle {

	public Enumeration findEntries(String arg0, String arg1, boolean arg2) {
		throw new UnsupportedOperationException();
	}

	public BundleContext getBundleContext() {
		throw new UnsupportedOperationException();
	}

	public long getBundleId() {
		throw new UnsupportedOperationException();
	}

	public URL getEntry(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Enumeration getEntryPaths(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Dictionary getHeaders() {
		throw new UnsupportedOperationException();
	}

	public Dictionary getHeaders(String arg0) {
		throw new UnsupportedOperationException();
	}

	public long getLastModified() {
		throw new UnsupportedOperationException();
	}

	public String getLocation() {
		throw new UnsupportedOperationException();
	}

	public ServiceReference[] getRegisteredServices() {
		throw new UnsupportedOperationException();
	}

	public URL getResource(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Enumeration getResources(String arg0) throws IOException {
		throw new UnsupportedOperationException();
	}

	public ServiceReference[] getServicesInUse() {
		throw new UnsupportedOperationException();
	}

	public Map getSignerCertificates(int arg0) {
		throw new UnsupportedOperationException();
	}

	public int getState() {
		throw new UnsupportedOperationException();
	}

	public String getSymbolicName() {
		throw new UnsupportedOperationException();
	}

	public Version getVersion() {
		throw new UnsupportedOperationException();
	}

	public boolean hasPermission(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public Class loadClass(String arg0) throws ClassNotFoundException {
		throw new UnsupportedOperationException();
	}

	public void start() throws BundleException {
		throw new UnsupportedOperationException();
	}

	public void start(int arg0) throws BundleException {
		throw new UnsupportedOperationException();
	}

	public void stop() throws BundleException {
		throw new UnsupportedOperationException();
	}

	public void stop(int arg0) throws BundleException {
		throw new UnsupportedOperationException();
	}

	public void uninstall() throws BundleException {
		throw new UnsupportedOperationException();
	}

	public void update() throws BundleException {
		throw new UnsupportedOperationException();
	}

	public void update(InputStream arg0) throws BundleException {
		throw new UnsupportedOperationException();
	}
}
