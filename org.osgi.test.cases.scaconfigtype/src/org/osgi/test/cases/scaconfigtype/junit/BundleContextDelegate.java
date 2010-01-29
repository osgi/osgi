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
package org.osgi.test.cases.scaconfigtype.junit;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class BundleContextDelegate implements BundleContext {
	private final BundleContext delegate;
	
	public BundleContextDelegate(BundleContext delegate) {
		this.delegate = delegate;
	}

	public void addBundleListener(BundleListener listener) {
		delegate.addBundleListener(listener);
	}

	public void addFrameworkListener(FrameworkListener listener) {
		delegate.addFrameworkListener(listener);
	}

	public void addServiceListener(ServiceListener listener) {
		delegate.addServiceListener(listener);
	}

	public void addServiceListener(ServiceListener listener, String filter)
			throws InvalidSyntaxException {
		delegate.addServiceListener(listener, filter);
	}

	public Filter createFilter(String filter) throws InvalidSyntaxException {
		return delegate.createFilter(filter);
	}

	public ServiceReference[] getAllServiceReferences(String clazz,
			String filter) throws InvalidSyntaxException {
		return delegate.getAllServiceReferences(clazz, filter);
	}

	public Bundle getBundle() {
		return delegate.getBundle();
	}

	public Bundle getBundle(long id) {
		return delegate.getBundle(id);
	}

	public Bundle[] getBundles() {
		return delegate.getBundles();
	}

	public File getDataFile(String filename) {
		return delegate.getDataFile(filename);
	}

	public String getProperty(String key) {
		return delegate.getProperty(key);
	}

	public Object getService(ServiceReference reference) {
		return delegate.getService(reference);
	}

	public ServiceReference getServiceReference(String clazz) {
		return delegate.getServiceReference(clazz);
	}

	public ServiceReference[] getServiceReferences(String clazz, String filter)
			throws InvalidSyntaxException {
		return delegate.getServiceReferences(clazz, filter);
	}

	public Bundle installBundle(String location) throws BundleException {
		return delegate.installBundle(location);
	}

	public Bundle installBundle(String location, InputStream input)
			throws BundleException {
		return delegate.installBundle(location, input);
	}

	public ServiceRegistration registerService(String[] clazzes,
			Object service, Dictionary properties) {
		return delegate.registerService(clazzes, service, properties);
	}

	public ServiceRegistration registerService(String clazz, Object service,
			Dictionary properties) {
		return delegate.registerService(clazz, service, properties);
	}

	public void removeBundleListener(BundleListener listener) {
		delegate.removeBundleListener(listener);
	}

	public void removeFrameworkListener(FrameworkListener listener) {
		delegate.removeFrameworkListener(listener);
	}

	public void removeServiceListener(ServiceListener listener) {
		delegate.removeServiceListener(listener);
	}

	public boolean ungetService(ServiceReference reference) {
		return delegate.ungetService(reference);
	}
}
