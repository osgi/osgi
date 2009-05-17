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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;
import org.osgi.wrapped.framework.TBundle;
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
import org.osgi.wrapped.framework.TVersion;
import org.osgi.wrapped.service.packageadmin.TExportedPackage;
import org.osgi.wrapped.service.packageadmin.TPackageAdmin;
import org.osgi.wrapped.service.packageadmin.TRequiredBundle;

class T {
	private T() {
		// empty
	}
	
	static ServiceRegistration unwrap(TServiceRegistration wrapped) {
		return ((TServiceRegistrationImpl) wrapped).registration;
	}
	
	static Bundle unwrap(TBundle wrapped) {
		return ((TBundleImpl) wrapped).bundle;
	}
	
	static BundleContext unwrap(TBundleContext wrapped) {
		return ((TBundleContextImpl) wrapped).context;
	}
	
	static TFrameworkListener unwrap(FrameworkListener wrapped) {
		return ((FrameworkListenerImpl) wrapped).listener;
	}
	
	static TServiceListener unwrap(ServiceListener wrapped) {
		return ((ServiceListenerImpl) wrapped).listener;
	}

	static ServiceReference unwrap(TServiceReference wrapped) {
		return ((TServiceReferenceImpl) wrapped).reference;
	}
	
	static Filter unwrap(TFilter wrapped) {
		return ((TFilterImpl) wrapped).filter;
	}
	
	static TBundleListener unwrap(BundleListener wrapped) {
		return ((BundleListenerImpl) wrapped).listener;
	}

	static TServiceFactory unwrap(ServiceFactory wrapped) {
		return ((ServiceFactoryImpl) wrapped).factory;
	}
	
	static PackageAdmin unwrap(TPackageAdmin wrapped) {
		return ((TPackageAdminImpl) wrapped).pa;
	}

	static ExportedPackage unwrap(TExportedPackage wrapped) {
		return ((TExportedPackageImpl) wrapped).ep;
	}

	static RequiredBundle unwrap(TRequiredBundle wrapped) {
		return ((TRequiredBundleImpl) wrapped).rb;
	}

	static TServiceReference toTServiceReference(ServiceReference reference) {
		if (reference == null) {
			return null;
		}
		return new TServiceReferenceImpl(reference);
	}
	
	static TServiceReference[] toTServiceReferences(ServiceReference[] references) {
		if (references == null) {
			return null;
		}
		int l = references.length;
		TServiceReference[] treferences = new TServiceReference[l];
		for (int i = 0; i < l; i++) {
			treferences[i] = new TServiceReferenceImpl(references[i]);
		}
		return treferences;
	}
	
	static TBundle toTBundle(Bundle bundle) {
		if (bundle == null) {
			return null;
		}
		return new TBundleImpl(bundle);
	}

	static TBundle[] toTBundles(Bundle[] bundles) {
		if (bundles == null) {
			return null;
		}
		int l = bundles.length;
		TBundle[] tbundles = new TBundle[l];
		for (int i = 0; i < l; i++) {
			tbundles[i] = new TBundleImpl(bundles[i]);
		}
		return tbundles;
	}
	
	static Bundle[] toBundles(TBundle[] tbundles) {
		if (tbundles == null) {
			return null;
		}
		int l = tbundles.length;
		Bundle[] bundles = new Bundle[l];
		for (int i = 0; i < l; i++) {
			bundles[i] = T.unwrap(tbundles[i]);
		}
		return bundles;
	}

	static TInvalidSyntaxException toTInvalidSyntaxException(
			InvalidSyntaxException e) {
		Throwable cause = e.getCause();
		TInvalidSyntaxException t = (cause == null) ? new TInvalidSyntaxException(
				e.getMessage(), e.getFilter())
				: new TInvalidSyntaxException(e.getMessage(), e.getFilter(),
						cause);
		t.setStackTrace(e.getStackTrace());
		return t;
	}
	
	static TBundleException toTBundleException(BundleException e) {
		Throwable cause = e.getCause();
		TBundleException t = (cause == null) ? new TBundleException(e
				.getMessage()) : new TBundleException(e.getMessage(), cause);
		t.setStackTrace(e.getStackTrace());
		return t;
	}

	static TExportedPackage toTExportedPackage(
			ExportedPackage exportedPackage) {
		if (exportedPackage == null) {
			return null;
		}
		return new TExportedPackageImpl(exportedPackage);
	}
	
	static TExportedPackage[] toTExportedPackages(
			ExportedPackage[] exportedPackages) {
		if (exportedPackages == null) {
			return null;
		}
		int l = exportedPackages.length;
		TExportedPackage[] tExportedPackages = new TExportedPackage[l];
		for (int i = 0; i < l; i++) {
			tExportedPackages[i] = new TExportedPackageImpl(exportedPackages[i]);
		}
		return tExportedPackages;
	}
	
	static TVersion toTVersion(Version version) {
		if (version == Version.emptyVersion) {
			return TVersion.emptyVersion;
		}
		return new TVersion(version.getMajor(), version.getMinor(), version
				.getMicro(), version.getQualifier());
	}

	static TRequiredBundle[] toTRequiredBundles(RequiredBundle[] requiredBundles) {
		if (requiredBundles == null) {
			return null;
		}
		int l = requiredBundles.length;
		TRequiredBundle[] tRequiredBundles = new TRequiredBundle[l];
		for (int i = 0; i < l; i++) {
			tRequiredBundles[i] = new TRequiredBundleImpl(requiredBundles[i]);
		}
		return tRequiredBundles;
	}
}
