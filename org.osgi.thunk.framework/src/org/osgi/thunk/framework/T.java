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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.bundle.BundleListener;
import org.osgi.framework.bundle.FrameworkListener;
import org.osgi.framework.bundle.ServiceListener;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.framework.TBundleException;
import org.osgi.wrapped.framework.TBundleListener;
import org.osgi.wrapped.framework.TFilter;
import org.osgi.wrapped.framework.TFrameworkListener;
import org.osgi.wrapped.framework.TInvalidSyntaxException;
import org.osgi.wrapped.framework.TServiceListener;
import org.osgi.wrapped.framework.TServiceReference;
import org.osgi.wrapped.framework.TServiceRegistration;

class T {
	private T() {
		// empty
	}
	
	static TServiceRegistration getWrapped(ServiceRegistration wrapped) {
		return ((ServiceRegistrationImpl) wrapped).registration;
	}
	
	static TBundle getWrapped(Bundle wrapped) {
		return ((BundleImpl) wrapped).bundle;
	}
	
	static FrameworkListener getWrapped(TFrameworkListener wrapped) {
		return ((TFrameworkListenerImpl) wrapped).listener;
	}
	
	static ServiceListener getWrapped(TServiceListener wrapped) {
		return ((TServiceListenerImpl) wrapped).listener;
	}

	static BundleListener getWrapped(TBundleListener wrapped) {
		return ((TBundleListenerImpl) wrapped).listener;
	}

	static TServiceReference getWrapped(ServiceReference wrapped) {
		return ((ServiceReferenceImpl) wrapped).reference;
	}
	
	static TFilter getWrapped(Filter wrapped) {
		return ((FilterImpl) wrapped).filter;
	}
	
	static Collection<ServiceReference> getReferences(
			TServiceReference[] treferences) {
		int l = treferences.length;
		ArrayList<ServiceReference> references = new ArrayList<ServiceReference>(
				l);
		for (int i = 0; i < l; i++) {
			references.add(new ServiceReferenceImpl(treferences[i]));
		}
		return references;
	}
	
	static Collection<Bundle> getBundles(TBundle[] tbundles) {
		int l = tbundles.length;
		ArrayList<Bundle> bundles = new ArrayList<Bundle>(l);
		for (int i = 0; i < l; i++) {
			bundles.add(new BundleImpl(tbundles[i]));
		}
		return bundles;
	}

	static InvalidSyntaxException toInvalidSyntaxException(
			TInvalidSyntaxException e) {
		Throwable cause = e.getCause();
		InvalidSyntaxException t = (cause == null) ? new InvalidSyntaxException(
				e.getMessage(), e.getFilter())
				: new InvalidSyntaxException(e.getMessage(), e.getFilter(),
						cause);
		t.setStackTrace(e.getStackTrace());
		return t;
	}
	
	static BundleException toBundleException(TBundleException e) {
		Throwable cause = e.getCause();
		BundleException t = (cause == null) ? new BundleException(e
				.getMessage()) : new BundleException(e.getMessage(), cause);
		t.setStackTrace(e.getStackTrace());
		return t;
	}

	static <K, V> Map<K, V> toMap(Dictionary<K, V> dictionary) {
		Map<K, V> map = new HashMap<K, V>(dictionary.size());
		for (Enumeration<K> keys = dictionary.keys(); keys.hasMoreElements();) {
			K key = keys.nextElement();
			V value = dictionary.get(key);
			map.put(key, value);
		}
		return map;
	}

	static <K, V> Dictionary<K, V> toDictionary(Map<K, V> map) {
		return new Hashtable<K, V>(map);
	}
}
