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

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.bundle.AllServiceListener;
import org.osgi.framework.bundle.BundleActivator;
import org.osgi.framework.bundle.BundleListener;
import org.osgi.framework.bundle.FrameworkListener;
import org.osgi.framework.bundle.ServiceFactory;
import org.osgi.framework.bundle.ServiceListener;
import org.osgi.framework.bundle.SynchronousBundleListener;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.framework.TBundleContext;
import org.osgi.wrapped.framework.TBundleException;
import org.osgi.wrapped.framework.TBundleListener;
import org.osgi.wrapped.framework.TFrameworkListener;
import org.osgi.wrapped.framework.TInvalidSyntaxException;
import org.osgi.wrapped.framework.TServiceListener;
import org.osgi.wrapped.framework.TServiceReference;

public class BundleContextImpl implements BundleContext {
	
	final TBundleContext	context;
	private BundleActivator										activator;

	private final Map<BundleListener, TBundleListener>			bundleListeners		= new HashMap<BundleListener, TBundleListener>();
	private final Map<FrameworkListener, TFrameworkListener>	frameworkListeners	= new HashMap<FrameworkListener, TFrameworkListener>();
	private final Map<ServiceListener, TServiceListener>		serviceListeners	= new HashMap<ServiceListener, TServiceListener>();

	BundleContextImpl(TBundleContext context) {
		this.context = context;
	}

	void start() throws Exception {
		TBundle bundle = context.getBundle();
		@SuppressWarnings("unchecked")
		Dictionary<String, String> headers = bundle.getHeaders("");
		String activatorName = headers.get("XBundle-Activator");
		if (activatorName == null) {
			return;
		}
		Class< ? > activatorClass = bundle.loadClass(activatorName);
		BundleActivator activatorObject = (BundleActivator) activatorClass
				.newInstance();
		activatorObject.start(this);
		activator = activatorObject;
	}

	void stop() throws Exception {
		if (activator != null) {
			activator.start(this);
		}
	}
	public void addBundleListener(BundleListener listener) {
		TBundleListener l = (listener instanceof SynchronousBundleListener) ? new TSynchronousBundleListenerImpl(
				listener)
				: new TBundleListenerImpl(listener);
		bundleListeners.put(listener, l);
		context.addBundleListener(l);
	}

	public void removeBundleListener(BundleListener listener) {
		context.removeBundleListener(bundleListeners.remove(listener));
	}

	public void addFrameworkListener(FrameworkListener listener) {
		TFrameworkListener l = new TFrameworkListenerImpl(listener);
		frameworkListeners.put(listener, l);
		context.addFrameworkListener(l);
	}

	public void removeFrameworkListener(FrameworkListener listener) {
		context.removeFrameworkListener(frameworkListeners.remove(listener));
	}

	public void addServiceListener(ServiceListener listener, String filter)
			throws InvalidSyntaxException {
		TServiceListener l = (listener instanceof AllServiceListener) ? new TAllServiceListenerImpl(
				listener)
				: new TServiceListenerImpl(listener);
		serviceListeners.put(listener, l);
		try {
			context.addServiceListener(l, filter);
		}
		catch (TInvalidSyntaxException e) {
			throw T.toInvalidSyntaxException(e);
		}
	}

	public void addServiceListener(ServiceListener listener) {
		try {
			addServiceListener(listener, null);
		}
		catch (InvalidSyntaxException e) {
			// empty
		}
	}

	public void removeServiceListener(ServiceListener listener) {
		context.removeServiceListener(serviceListeners.remove(listener));
	}

	public Filter createFilter(String filter) throws InvalidSyntaxException {
		try {
			return new FilterImpl(context.createFilter(filter));
		}
		catch (TInvalidSyntaxException e) {
			throw T.toInvalidSyntaxException(e);
		}
	}

	public Collection< ? extends ServiceReference> getAllServiceReferences(
			String clazz, String filter) throws InvalidSyntaxException {
		try {
			TServiceReference[] references = context.getAllServiceReferences(
					clazz, filter);
			return T.getReferences(references);
		}
		catch (TInvalidSyntaxException e) {
			throw T.toInvalidSyntaxException(e);
		}
	}

	public Bundle getBundle() {
		return new BundleImpl(context.getBundle());
	}

	public Bundle getBundle(long id) {
		return new BundleImpl(context.getBundle(id));
	}

	public Collection< ? extends Bundle> getBundles() {
		TBundle[] bundles = context.getBundles();
		return T.getBundles(bundles);
	}

	public File getDataFile(String filename) {
		return context.getDataFile(filename);
	}

	public String getProperty(String key) {
		return context.getProperty(key);
	}

	public Object getService(ServiceReference reference) {
		return context.getService(T.getWrapped(reference));
	}

	public ServiceReference getServiceReference(String clazz) {
		return new ServiceReferenceImpl(context.getServiceReference(clazz));
	}

	public Collection< ? extends ServiceReference> getServiceReferences(
			String clazz, String filter) throws InvalidSyntaxException {
		try {
			TServiceReference[] references = context.getServiceReferences(
					clazz, filter);
			return T.getReferences(references);
		}
		catch (TInvalidSyntaxException e) {
			throw T.toInvalidSyntaxException(e);
		}
	}

	public Bundle installBundle(String location, InputStream input)
			throws BundleException {
		try {
			return new BundleImpl(context.installBundle(location, input));
		}
		catch (TBundleException e) {
			throw T.toBundleException(e);
		}
	}

	public Bundle installBundle(String location) throws BundleException {
		return installBundle(location, null);
	}

	public ServiceRegistration registerService(String[] clazzes,
			Object service, Map<String, Object> properties) {
		if (service instanceof ServiceFactory) {
			service = new TServiceFactoryImpl((ServiceFactory) service);
		}
		return new ServiceRegistrationImpl(context.registerService(clazzes,
				service, T.toDictionary(properties)));
	}

	public ServiceRegistration registerService(String clazz, Object service,
			Map<String, Object> properties) {
		return registerService(new String[] {clazz}, service, properties);
	}

	public boolean ungetService(ServiceReference reference) {
		return context.ungetService(T.getWrapped(reference));
	}

	@Override
	public boolean equals(Object arg0) {
		return context.equals(arg0);
	}

	@Override
	public int hashCode() {
		return context.hashCode();
	}

	@Override
	public String toString() {
		return context.toString();
	}

}
