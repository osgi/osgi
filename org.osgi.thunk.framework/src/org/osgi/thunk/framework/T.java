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
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ExportedPackage;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.framework.bundle.BundleListener;
import org.osgi.framework.bundle.FrameworkListener;
import org.osgi.framework.bundle.ServiceFactory;
import org.osgi.framework.bundle.ServiceListener;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.framework.TBundleContext;
import org.osgi.wrapped.framework.TBundleException;
import org.osgi.wrapped.framework.TBundleListener;
import org.osgi.wrapped.framework.TFrameworkListener;
import org.osgi.wrapped.framework.TInvalidSyntaxException;
import org.osgi.wrapped.framework.TServiceFactory;
import org.osgi.wrapped.framework.TServiceListener;
import org.osgi.wrapped.framework.TServiceReference;
import org.osgi.wrapped.framework.TServiceRegistration;
import org.osgi.wrapped.framework.TVersion;
import org.osgi.wrapped.service.packageadmin.TExportedPackage;

class T {
	private T() {
		// empty
	}
	
	static TServiceRegistration unwrap(ServiceRegistration<?> wrapped) {
		return ((ServiceRegistrationImpl<?>) wrapped).registration;
	}
	
	static TBundle unwrap(Bundle wrapped) {
		return ((BundleImpl) wrapped).bundle;
	}
	
	static TBundleContext unwrap(BundleContext wrapped) {
		return ((BundleContextImpl) wrapped).context;
	}
	
	static FrameworkListener unwrap(TFrameworkListener wrapped) {
		return ((TFrameworkListenerImpl) wrapped).listener;
	}
	
	static ServiceFactory<?> unwrap(TServiceFactory wrapped) {
		return ((TServiceFactoryImpl) wrapped).factory;
	}

	static ServiceListener unwrap(TServiceListener wrapped) {
		return ((TServiceListenerImpl) wrapped).listener;
	}
	
	static BundleListener unwrap(TBundleListener wrapped) {
		return ((TBundleListenerImpl) wrapped).listener;
	}

	static TServiceReference unwrap(ServiceReference<?> wrapped) {
		return ((ServiceReferenceImpl<?>) wrapped).reference;
	}
	
	static TExportedPackage unwrap(ExportedPackage wrapped) {
		return ((ExportedPackageImpl) wrapped).ep;
	}

	static ServiceReference<Object> toReference(TServiceReference treference) {
		if (treference == null) {
			return null;
		}
		return new ServiceReferenceImpl<Object>(treference);
	}
	
	static ServiceRegistration<Object> toRegistration(
			TServiceRegistration tregistration) {
		if (tregistration == null) {
			return null;
		}
		return new ServiceRegistrationImpl<Object>(tregistration);
	}
	
	static Collection<ServiceReference< ? >> toReferences(
			TServiceReference[] treferences) {
		if (treferences == null) {
			return Collections.emptyList();
		}
		int l = treferences.length;
		ArrayList<ServiceReference<?>> references = new ArrayList<ServiceReference<?>>(
				l);
		for (int i = 0; i < l; i++) {
			references.add(new ServiceReferenceImpl<Object>(treferences[i]));
		}
		return references;
	}
	
	static Bundle toBundle(TBundle tbundle) {
		if (tbundle == null) {
			return null;
		}
		return new BundleImpl(tbundle);
	}

	static Collection<Bundle> toBundles(TBundle[] tbundles) {
		if (tbundles == null) {
			return Collections.emptyList();
		}
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
		if (dictionary == null) {
			return Collections.emptyMap();
		}
		Map<K, V> map = new HashMap<K, V>(dictionary.size());
		for (Enumeration<K> keys = dictionary.keys(); keys.hasMoreElements();) {
			K key = keys.nextElement();
			V value = dictionary.get(key);
			map.put(key, value);
		}
		return map;
	}

	static <K, V> Dictionary<K, V> toDictionary(Map<K, V> map) {
		if (map == null) {
			return null;
		}
		return new Hashtable<K, V>(map);
	}
	
	static Version toVersion(TVersion version) {
		if (version == TVersion.emptyVersion) {
			return Version.emptyVersion;
		}
		return new Version(version.getMajor(), version.getMinor(), version
				.getMicro(), version.getQualifier());
	}


	static Collection<ExportedPackage> toExportedPackages(
			TExportedPackage[] tExportedPackages) {
		if (tExportedPackages == null) {
			return Collections.emptyList();
		}
		int l = tExportedPackages.length;
		ArrayList<ExportedPackage> bundles = new ArrayList<ExportedPackage>(l);
		for (int i = 0; i < l; i++) {
			bundles.add(new ExportedPackageImpl(tExportedPackages[i]));
		}
		return bundles;
	}

	static TBundle[] toTBundles(Bundle[] bundles) {
		if (bundles == null) {
			return null;
		}
		int l = bundles.length;
		TBundle[] tbundles = new TBundle[l];
		for (int i = 0; i < l; i++) {
			tbundles[i] = T.unwrap(bundles[i]);
		}
		return tbundles;
	}
	
}
