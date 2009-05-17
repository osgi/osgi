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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ExportedPackage;
import org.osgi.framework.Framework;
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

public class BundleContextImpl implements BundleContext, Framework {

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
		@SuppressWarnings("unchecked")
		Class<BundleActivator> activatorClass = bundle
				.loadClass(activatorName);
		BundleActivator activatorObject = activatorClass.newInstance();
		activatorObject.start(this);
		activator = activatorObject;
	}

	void stop() throws Exception {
		final BundleActivator a = activator;
		if (a != null) {
			a.stop(this);
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

	public void addServiceListener(ServiceListener listener, String filter) {
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

	public void removeServiceListener(ServiceListener listener) {
		synchronized (serviceListeners) {
			context.removeServiceListener(serviceListeners.remove(listener));
		}
	}

	public Collection<ServiceReference< ? >> getAllServiceReferences(
			String clazz, String filter) {
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
		return T.toBundle(context.getBundle());
	}

	public Bundle getBundle(long id) {
		return T.toBundle(context.getBundle(id));
	}

	public Collection<Bundle> getBundles() {
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

	public boolean ungetService(ServiceReference< ? > reference) {
		return context.ungetService(T.unwrap(reference));
	}

	public ServiceReference<?> getServiceReference(String clazz) {
		return T.toReference(context.getServiceReference(clazz));
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
		return (ServiceReference<S>) getServiceReference(clazz.getName());
	}

	public Collection<ServiceReference< ? >> getServiceReferences(String filter) {
		try {
			TServiceReference[] references = context.getServiceReferences(null,
					filter);
			return T.toReferences(references);
		}
		catch (TInvalidSyntaxException e) {
			throw T.toInvalidSyntaxException(e);
		}
	}

	public Collection<ServiceReference< ? >> getServiceReferences(
			String clazz, String filter) {
		try {
			TServiceReference[] references = context.getServiceReferences(
					clazz
					.toString(), filter);
			return T.toReferences(references);
		}
		catch (TInvalidSyntaxException e) {
			throw T.toInvalidSyntaxException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <S> Collection<ServiceReference<S>> getServiceReferences(
			Class<S> clazz, String filter) {
		return (Collection) getServiceReferences(
				clazz.getName(), filter);
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

	@SuppressWarnings("unchecked")
	public ServiceRegistration<?> registerService(String[] clazzes,
			Object service, Map<String, Object> properties) {
		if (service instanceof ServiceFactory) {
			service = new TServiceFactoryImpl((ServiceFactory<Object>) service);
		}
		return T.toRegistration(context.registerService(clazzes,
				service, T.toDictionary(properties)));
	}

	public ServiceRegistration<?> registerService(String clazz, Object service,
			Map<String, Object> properties) {
		return registerService(new String[] {clazz}, service, properties);
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceRegistration<S> registerService(Class<S> clazz,
			S service,
			Map<String, Object> properties) {
		
		return (ServiceRegistration<S>) registerService(clazz.getName(), service, properties);
	}

	public Framework getFramework() {
		return this;
	}

	private final List<GottenService< ? extends Object>>	gets	= new ArrayList<GottenService< ? extends Object>>(); 
	private static class GottenService<S> {
		final ServiceReference<S>	reference;
		final S						service;
		GottenService(ServiceReference<S> ref, S service) {
			this.reference = ref;
			this.service = service;
		}
	}
	
	public <S> S getService(Class<S> clazz) {
		ServiceReference<S> reference = getServiceReference(clazz);
		if (reference == null) {
			return null;
		}
		S service = getService(reference);
		if (service == null) {
			return null;
		}
		
		GottenService<S> reg = new GottenService<S>(reference,service);
		gets.add(reg);
		return service;
	}

	public <S> void ungetService(S service) {
		for (Iterator<GottenService< ? >> iter = gets.iterator(); iter
				.hasNext();) {
			GottenService< ? > gotten = iter.next();
			if (gotten.service == service) {
				iter.remove();
				ungetService(gotten.reference);
			}
		}
	}

	public Bundle getBundle(Class< ? > clazz) {
		return T.toBundle(Activator.getTPackageAdmin().getBundle(clazz));
	}

	public Collection<Bundle> getBundles(String symbolicName,
			String versionRange) {
		return T.toBundles(Activator.getTPackageAdmin().getBundles(
				symbolicName, versionRange));
	}

	public Collection<ExportedPackage> getExportedPackages(String name) {
		if (name == null) {
			return T.toExportedPackages(Activator.getTPackageAdmin()
					.getExportedPackages((TBundle) null));
		}
		return T.toExportedPackages(Activator.getTPackageAdmin()
				.getExportedPackages(name));
	}

	public void refreshPackages(Bundle... bundles) {
		Activator.getTPackageAdmin().refreshPackages(
				(bundles == null) ? null : T.toTBundles(bundles));
	}

	public boolean resolveBundles(Bundle... bundles) {
		return Activator.getTPackageAdmin().resolveBundles(
				(bundles == null) ? null : 
				T.toTBundles(bundles));
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
}
