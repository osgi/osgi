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
import java.util.IdentityHashMap;
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

	final TBundleContext										context;
	private volatile BundleActivator							activator;

	private final Map<BundleListener, TBundleListener>			bundleListeners		= new IdentityHashMap<BundleListener, TBundleListener>();
	private final Map<FrameworkListener, TFrameworkListener>	frameworkListeners	= new IdentityHashMap<FrameworkListener, TFrameworkListener>();
	private final Map<ServiceListener, TServiceListener>		serviceListeners	= new IdentityHashMap<ServiceListener, TServiceListener>();

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
		final BundleActivator a = activator;
		if (a != null) {
			a.start(this);
		}
	}

	public void addBundleListener(BundleListener listener) {
		synchronized (bundleListeners) {
			TBundleListener l = bundleListeners.remove(listener);
			if (l != null) {
				context.removeBundleListener(l);
			}
			l = (listener instanceof SynchronousBundleListener) ? new TSynchronousBundleListenerImpl(
					listener)
					: new TBundleListenerImpl(listener);
			bundleListeners.put(listener, l);
			context.addBundleListener(l);
		}
	}

	public void removeBundleListener(BundleListener listener) {
		synchronized (bundleListeners) {
			context.removeBundleListener(bundleListeners.remove(listener));
		}
	}

	public void addFrameworkListener(FrameworkListener listener) {
		synchronized (frameworkListeners) {
			TFrameworkListener l = frameworkListeners.remove(listener);
			if (l != null) {
				context.removeFrameworkListener(l);
			}
			l = new TFrameworkListenerImpl(listener);
			frameworkListeners.put(listener, l);
			context.addFrameworkListener(l);
		}
	}

	public void removeFrameworkListener(FrameworkListener listener) {
		synchronized (frameworkListeners) {
			context
					.removeFrameworkListener(frameworkListeners
							.remove(listener));
		}
	}

	public void addServiceListener(ServiceListener listener, String filter)
			throws InvalidSyntaxException {
		synchronized (serviceListeners) {
			TServiceListener l = serviceListeners.remove(listener);
			if (l != null) {
				context.removeServiceListener(l);
			}
			l = (listener instanceof AllServiceListener) ? new TAllServiceListenerImpl(
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
		synchronized (serviceListeners) {
			context.removeServiceListener(serviceListeners.remove(listener));
		}
	}

	public Filter createFilter(String filter) throws InvalidSyntaxException {
		try {
			return new FilterImpl(context.createFilter(filter));
		}
		catch (TInvalidSyntaxException e) {
			throw T.toInvalidSyntaxException(e);
		}
	}

	public Collection< ? extends ServiceReference<?>> getAllServiceReferences(
			String clazz, String filter) throws InvalidSyntaxException {
		try {
			TServiceReference[] references = context.getAllServiceReferences(
					clazz, filter);
			return T.toReferences(references);
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
		return T.toBundles(bundles);
	}

	public File getDataFile(String filename) {
		return context.getDataFile(filename);
	}

	public String getProperty(String key) {
		return context.getProperty(key);
	}

	@SuppressWarnings("unchecked")
	public <S> S getService(ServiceReference<S> reference) {
		return (S) context.getService(T.unwrap(reference));
	}

	public ServiceReference<?> getServiceReference(String clazz) {
		return new ServiceReferenceImpl<Object>(context.getServiceReference(clazz));
	}

	public Collection< ? extends ServiceReference<?>> getServiceReferences(
			String clazz, String filter) throws InvalidSyntaxException {
		try {
			TServiceReference[] references = context.getServiceReferences(
					clazz, filter);
			return T.toReferences(references);
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

	public ServiceRegistration<?> registerService(String[] clazzes,
			Object service, Map<String, Object> properties) {
		if (service instanceof ServiceFactory) {
			service = new TServiceFactoryImpl((ServiceFactory<?>) service);
		}
		return new ServiceRegistrationImpl<Object>(context.registerService(clazzes,
				service, T.toDictionary(properties)));
	}

	public ServiceRegistration<?> registerService(String clazz, Object service,
			Map<String, Object> properties) {
		return registerService(new String[] {clazz}, service, properties);
	}

	@Override
	public boolean equals(Object o) {
		return context.equals(T.unwrap((BundleContext) o));
	}

	@Override
	public int hashCode() {
		return context.hashCode();
	}

	@Override
	public String toString() {
		return context.toString();
	}

	@SuppressWarnings("unchecked")
	public <S> Collection<? extends ServiceReference<S>> getAllServiceReferences(
			Class<S> clazz, String filter) throws InvalidSyntaxException {
		return (Collection<? extends ServiceReference<S>>) getAllServiceReferences(clazz.getName(), filter);
	}


	@SuppressWarnings("unchecked")
	public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
		return (ServiceReference<S>) getServiceReference(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public <S> Collection<? extends ServiceReference<S>> getServiceReferences(
			Class<S> clazz, String filter) throws InvalidSyntaxException {
		return (Collection<? extends ServiceReference<S>>) getServiceReferences(clazz.getName(), filter);
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceRegistration<S> registerService(Class<S> clazz, S service,
			Map<String, Object> properties) {
		
		return (ServiceRegistration<S>) registerService(clazz.getName(), service, properties);
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceRegistration<S> registerService(S service,
			Map<String, Object> properties, Class<S> clazz, Class<?>[] moreClasses) {
		String classes[] = new String[moreClasses.length+1];
		classes[0] = clazz.getName();
		for ( int i =0; i<moreClasses.length; i++ ) {
			classes[i+1] = moreClasses[i].getName();
		}
		return (ServiceRegistration<S>) registerService(classes, service, properties);
	}

	public boolean ungetService(ServiceReference<?> arg0) {
		context.ungetService(T.unwrap(arg0));
		return false;
	}
}
