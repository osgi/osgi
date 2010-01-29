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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.launch.Framework;

/**
 * @author <a href="mailto:david.savage@paremus.com">David Savage</a>
 *
 */
public class FrameworkDelegate implements Framework {

	private final Framework delegate;
	private final Class<? extends BundleContextDelegate> contextDelegate;
	
	/**
	 * @param delegate
	 */
	public FrameworkDelegate(Framework delegate) {
		this.delegate = delegate;
		contextDelegate = null;
	}
	
	/**
	 * @param delegate
	 * @param contextDelegate
	 */
	public FrameworkDelegate(Framework delegate, Class<? extends BundleContextDelegate> contextDelegate) {
		this.delegate = delegate;
		this.contextDelegate = contextDelegate;
	}
	
	public long getBundleId() {
		return delegate.getBundleId();
	}

	public String getLocation() {
		return delegate.getLocation();
	}

	public String getSymbolicName() {
		return delegate.getSymbolicName();
	}

	public void init() throws BundleException {
		delegate.init();
	}

	public void start() throws BundleException {
		delegate.start();
	}

	public void start(int options) throws BundleException {
		delegate.start(options);
	}

	public void stop() throws BundleException {
		delegate.stop();
	}

	public void stop(int options) throws BundleException {
		delegate.stop(options);
	}

	public void uninstall() throws BundleException {
		delegate.uninstall();
	}

	public void update() throws BundleException {
		delegate.update();
	}

	public void update(InputStream in) throws BundleException {
		delegate.update(in);
	}

	public FrameworkEvent waitForStop(long timeout) throws InterruptedException {
		return delegate.waitForStop(timeout);
	}

	public Enumeration findEntries(String path, String filePattern,
			boolean recurse) {
		return delegate.findEntries(path, filePattern, recurse);
	}

	public BundleContext getBundleContext() {
		return contextDelegate == null ? delegate.getBundleContext() : delegateContext(delegate.getBundleContext());
	}

	private BundleContext delegateContext(BundleContext bundleContext) {
		try {
			Constructor<? extends BundleContextDelegate> c = contextDelegate.getConstructor( BundleContext.class );
			return c.newInstance( bundleContext );
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("Failed to create delegate bundle context", e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to create delegate bundle context", e);
		} catch (InstantiationException e) {
			throw new IllegalStateException("Failed to create delegate bundle context", e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException("Failed to create delegate bundle context", e);
		}
	}

	public URL getEntry(String path) {
		return delegate.getEntry(path);
	}

	public Enumeration getEntryPaths(String path) {
		return delegate.getEntryPaths(path);
	}

	public Dictionary getHeaders() {
		return delegate.getHeaders();
	}

	public Dictionary getHeaders(String locale) {
		return delegate.getHeaders(locale);
	}

	public long getLastModified() {
		return delegate.getLastModified();
	}

	public ServiceReference[] getRegisteredServices() {
		return delegate.getRegisteredServices();
	}

	public URL getResource(String name) {
		return delegate.getResource(name);
	}

	public Enumeration getResources(String name) throws IOException {
		return delegate.getResources(name);
	}

	public ServiceReference[] getServicesInUse() {
		return delegate.getServicesInUse();
	}

	public Map getSignerCertificates(int signersType) {
		return delegate.getSignerCertificates(signersType);
	}

	public int getState() {
		return delegate.getState();
	}

	public Version getVersion() {
		return delegate.getVersion();
	}

	public boolean hasPermission(Object permission) {
		return delegate.hasPermission(permission);
	}

	public Class loadClass(String name) throws ClassNotFoundException {
		return delegate.loadClass(name);
	}
}
