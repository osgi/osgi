/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.internal.core;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.*;
import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.RequiredBundle;

/*
 * The BundleLoaderProxy proxies a BundleLoader object for a Bundle.  This
 * allows for a Bundle's depedencies to be linked without forcing the 
 * creating of the BundleLoader or BundleClassLoader objects.  This class
 * keeps track of the depedencies between the bundles installed in the 
 * Framework.
 */
public class BundleLoaderProxy implements RequiredBundle {
	/* The BundleLoader that this BundleLoaderProxy is managing */
	BundleLoader loader;
	/* The Bundle that this BundleLoaderProxy is for */
	private BundleHost bundle;
	/* the BundleDescription for the Bundle */
	private BundleDescription description;
	/*
	 * Indicates if this BundleLoaderProxy is stale; 
	 * this is true when the bundle is updated or uninstalled.
	 */
	private boolean stale = false;

	//TODO Needs a comment, and should not that belong to the Loader
	private KeyedHashSet pkgSources;

	public BundleLoaderProxy(BundleHost bundle, BundleDescription description) {
		this.bundle = bundle;
		this.description = description;
		this.pkgSources = new KeyedHashSet(false);
	}

	public BundleLoader getBundleLoader() {
		if (loader == null) {
			synchronized (this) {
				if (loader == null)
					try {
						if (bundle.getBundleId() == 0) // this is the system bundle
							loader = new SystemBundleLoader(bundle, this);
						else
							loader = new BundleLoader(bundle, this);
					} catch (BundleException e) {
						bundle.framework.publishFrameworkEvent(FrameworkEvent.ERROR, bundle, e);
						return null;
					}
			}
		}
		return loader;
	}

	public BundleLoader getBasicBundleLoader() {
		return loader;
	}

	public AbstractBundle getBundleHost() {
		return bundle;
	}

	public void setStale() {
		stale = true;
	}

	public boolean isStale() {
		return stale;
	}


	public String toString() {
		String symbolicName = bundle.getSymbolicName();
		StringBuffer sb = new StringBuffer(symbolicName == null ? bundle.getLocation() : symbolicName);
		sb.append("; ").append(Constants.BUNDLE_VERSION_ATTRIBUTE); //$NON-NLS-1$
		sb.append("=\"").append(description.getVersion().toString()).append("\""); //$NON-NLS-1$//$NON-NLS-2$
		return sb.toString();
	}

	public org.osgi.framework.Bundle getBundle() {
		if (isStale())
			return null;

		return bundle;
	}

	public org.osgi.framework.Bundle[] getRequiringBundles() {
		if (isStale())
			return null;
		// This is VERY slow; but never gets called in regular execution.
		BundleDescription[] dependents = description.getDependents();
		if (dependents == null || dependents.length == 0)
			return null;
		ArrayList result = new ArrayList(dependents.length);
		for (int i = 0; i < dependents.length; i++)
			addRequirers(dependents[i], result);
		return result.size() == 0 ? null : (Bundle[]) result.toArray(new org.osgi.framework.Bundle[result.size()]);
	}

	void addRequirers(BundleDescription dependent, ArrayList result) {
		if (dependent.getHost() != null) // don't look in fragments.
			return;
		BundleLoaderProxy dependentProxy = getBundleLoader().getLoaderProxy(dependent);
		if (dependentProxy == null)
			return; // bundle must have been uninstalled
		if (result.contains(dependentProxy.bundle))
			return; // prevent endless recusion
		BundleLoader dependentLoader = dependentProxy.getBundleLoader();
		BundleLoaderProxy[] requiredBundles = dependentLoader.requiredBundles;
		int[] reexportTable = dependentLoader.reexportTable;
		if (requiredBundles == null)
			return;
		int size = reexportTable == null ? 0 : reexportTable.length;
		int reexportIndex = 0;
		for (int i = 0; i < requiredBundles.length; i++) {
			if (requiredBundles[i] == this) {
				result.add(dependentProxy.bundle);
				if (reexportIndex < size && reexportTable[reexportIndex] == i) {
					reexportIndex++;
					BundleDescription[] dependents = dependent.getDependents();
					if (dependents == null)
						return;
					for(int j = 0; j < dependents.length; j++)
						dependentProxy.addRequirers(dependents[j], result);
				}
				return;
			}
		}
		return;
	}

	public String getSymbolicName() {
		return description.getSymbolicName();
	}

	public Version getVersion() {
		return description.getVersion();
	}

	public boolean isRemovalPending() {
		return description.isRemovalPending();
	}

	public BundleDescription getBundleDescription() {
		return description;
	}

	public PackageSource getPackageSource(String pkgName) {
		PackageSource pkgSource = (PackageSource) pkgSources.getByKey(pkgName);
		if (pkgSource == null) {
			synchronized (pkgSources) {
				pkgSource = new SingleSourcePackage(pkgName, this);
				pkgSources.add(pkgSource);
			}
		}
		return pkgSource;
	}

	public boolean inUse() {
		return description.getDependents().length > 0;
	}

	// creates a PackageSource from an ExportPackageDescription.  This is called when initializing
	// a BundleLoader to ensure that proper the proper PackageSource gets created and used for
	// filtered and reexport packages.  The storeSource flag is used by initialize to indicate
	// that the source for special case package sources (filtered or re-exported should be stored 
	// in the cache.  if this flag is set then a normal SinglePackageSource will not be created
	// (i.e. it will be created lazily)
	public PackageSource createPackageSource(ExportPackageDescription export, boolean storeSource) {
		PackageSource pkgSource = null;
		// check to see if it is a reexport
		if (!export.isRoot()) {
			pkgSource = new ReexportPackageSource(export.getName());
		}
		else {
			// check to see if it is a filtered export
			String includes = export.getInclude();
			String excludes = export.getExclude();
			if (includes != null || excludes != null)
				pkgSource = new FilteredSourcePackage(export.getName(), this, includes, excludes);
		}

		if (storeSource) {
			// if the package source is not null then store the source only if it is not already present
			//TODO Is it normal that the getByKey in the if is not synchronized?
			if (pkgSource != null && pkgSources.getByKey(export.getName()) == null)
				synchronized (pkgSources) {
					pkgSources.add(pkgSource);
				}
		}
		else {
			// we are not storing the special case sources, but pkgSource == null this means this
			// is a normal package source; get it and return it.
			if (pkgSource == null)
				pkgSource = getPackageSource(export.getName());
		}
				
		return pkgSource;
	}


	class ReexportPackageSource extends PackageSource {
		public ReexportPackageSource(String id) {
			super(id);
		}

		public synchronized SingleSourcePackage[] getSuppliers() {
			PackageSource source = getBundleLoader().getPackageSource(id);
			if (source == null)
				return null;
			return source.getSuppliers();
		}

		public Class loadClass(String name, String pkgName) {
			try {
				return getBundleLoader().findClass(name);
			}
			catch(ClassNotFoundException e) {
				return null;
			}
		}
		public URL getResource(String name, String pkgName) {
			return getBundleLoader().findResource(name);
		}
		public Enumeration getResources(String name, String pkgName) throws IOException {
			return getBundleLoader().findResources(name);
		}
	}
}
