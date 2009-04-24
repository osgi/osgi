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

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.wrapped.framework.TAllServiceListener;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.framework.TBundleActivator;
import org.osgi.wrapped.framework.TBundleContext;
import org.osgi.wrapped.framework.TBundleException;
import org.osgi.wrapped.framework.TBundleListener;
import org.osgi.wrapped.framework.TFilter;
import org.osgi.wrapped.framework.TFrameworkListener;
import org.osgi.wrapped.framework.TInvalidSyntaxException;
import org.osgi.wrapped.framework.TServiceFactory;
import org.osgi.wrapped.framework.TServiceListener;
import org.osgi.wrapped.framework.TServiceReference;
import org.osgi.wrapped.framework.TServiceRegistration;
import org.osgi.wrapped.framework.TSynchronousBundleListener;

public class TBundleContextImpl implements TBundleContext {
	final BundleContext											context;
	private TBundleActivator									activator;

	private final Map<TBundleListener, BundleListener>			bundleListeners		= new HashMap<TBundleListener, BundleListener>();
	private final Map<TFrameworkListener, FrameworkListener>	frameworkListeners	= new HashMap<TFrameworkListener, FrameworkListener>();
	private final Map<TServiceListener, ServiceListener>		serviceListeners	= new HashMap<TServiceListener, ServiceListener>();

	TBundleContextImpl(BundleContext context) {
		this.context = context;
	}

	void start() throws Exception {
		Bundle bundle = context.getBundle();
		@SuppressWarnings("unchecked")
		Dictionary<String, String> headers = bundle.getHeaders("");
		String activatorName = headers.get("TBundle-Activator");
		if (activatorName == null) {
			return;
		}
		Class< ? > activatorClass = bundle.loadClass(activatorName);
		TBundleActivator activatorObject = (TBundleActivator) activatorClass
				.newInstance();
		activatorObject.start(this);
		activator = activatorObject;
	}

	void stop() throws Exception {
		if (activator != null) {
			activator.start(this);
		}
	}

	public void addBundleListener(TBundleListener listener) {
		BundleListener l = (listener instanceof TSynchronousBundleListener) ? new SynchronousBundleListenerImpl(
				listener)
				: new BundleListenerImpl(listener);
		bundleListeners.put(listener, l);
		context.addBundleListener(l);
	}

	public void removeBundleListener(TBundleListener listener) {
		context.removeBundleListener(bundleListeners.remove(listener));
	}

	public void addFrameworkListener(TFrameworkListener listener) {
		FrameworkListener l = new FrameworkListenerImpl(listener);
		frameworkListeners.put(listener, l);
		context.addFrameworkListener(l);
	}

	public void removeFrameworkListener(TFrameworkListener listener) {
		context.removeFrameworkListener(frameworkListeners.remove(listener));
	}

	public void addServiceListener(TServiceListener listener, String filter)
			throws TInvalidSyntaxException {
		ServiceListener l = (listener instanceof TAllServiceListener) ? new AllServiceListenerImpl(
				listener)
				: new ServiceListenerImpl(listener);
		serviceListeners.put(listener, l);
		try {
			context.addServiceListener(l, filter);
		}
		catch (InvalidSyntaxException e) {
			throw T.toTInvalidSyntaxException(e);
		}
	}

	public void addServiceListener(TServiceListener listener) {
		try {
			addServiceListener(listener, null);
		}
		catch (TInvalidSyntaxException e) {
			// empty
		}
	}

	public void removeServiceListener(TServiceListener listener) {
		context.removeServiceListener(serviceListeners.remove(listener));
	}

	public TFilter createFilter(String filter) throws TInvalidSyntaxException {
		try {
			return new TFilterImpl(context.createFilter(filter));
		}
		catch (InvalidSyntaxException e) {
			throw T.toTInvalidSyntaxException(e);
		}
	}

	public TServiceReference[] getAllServiceReferences(String clazz,
			String filter) throws TInvalidSyntaxException {
		try {
			ServiceReference[] references = context.getAllServiceReferences(
					clazz, filter);
			return T.getReferences(references);
		}
		catch (InvalidSyntaxException e) {
			throw T.toTInvalidSyntaxException(e);
		}
	}

	public TBundle getBundle() {
		return new TBundleImpl(context.getBundle());
	}

	public TBundle getBundle(long id) {
		return new TBundleImpl(context.getBundle(id));
	}

	public TBundle[] getBundles() {
		Bundle[] bundles = context.getBundles();
		return T.getBundles(bundles);
	}

	public File getDataFile(String filename) {
		return context.getDataFile(filename);
	}

	public String getProperty(String key) {
		return context.getProperty(key);
	}

	public Object getService(TServiceReference reference) {
		return context.getService(T.getWrapped(reference));
	}

	public TServiceReference getServiceReference(String clazz) {
		return new TServiceReferenceImpl(context.getServiceReference(clazz));
	}

	public TServiceReference[] getServiceReferences(String clazz, String filter)
			throws TInvalidSyntaxException {
		try {
			ServiceReference[] references = context.getServiceReferences(clazz,
					filter);
			return T.getReferences(references);
		}
		catch (InvalidSyntaxException e) {
			throw T.toTInvalidSyntaxException(e);
		}
	}

	public TBundle installBundle(String location, InputStream input)
			throws TBundleException {
		try {
			return new TBundleImpl(context.installBundle(location, input));
		}
		catch (BundleException e) {
			throw T.toTBundleException(e);
		}
	}

	public TBundle installBundle(String location) throws TBundleException {
		return installBundle(location, null);
	}

	@SuppressWarnings("unchecked")
	public TServiceRegistration registerService(String[] clazzes,
			Object service, Dictionary properties) {
		if (service instanceof TServiceFactory) {
			service = new ServiceFactoryImpl((TServiceFactory) service);
		}
		return new TServiceRegistrationImpl(context.registerService(clazzes,
				service, properties));
	}

	@SuppressWarnings("unchecked")
	public TServiceRegistration registerService(String clazz, Object service,
			Dictionary properties) {
		return registerService(new String[] {clazz},
				service, properties);
	}

	public boolean ungetService(TServiceReference reference) {
		return context.ungetService(T.getWrapped(reference));
	}

	@Override
	public boolean equals(Object o) {
		return context.equals(T.getWrapped((TBundleContext) o));
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
