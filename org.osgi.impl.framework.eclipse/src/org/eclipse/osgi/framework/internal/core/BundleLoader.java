/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import org.eclipse.osgi.framework.adaptor.*;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.service.resolver.*;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.*;

/**
 * This object is responsible for all classloader delegation for a bundle.
 * It represents the loaded state of the bundle.  BundleLoader objects
 * are created lazily; care should be taken not to force the creation
 * of a BundleLoader unless it is necessary.
 * @see org.eclipse.osgi.framework.internal.core.BundleLoaderProxy
 */
public class BundleLoader implements ClassLoaderDelegate {
	protected final static String DEFAULT_PACKAGE = "."; //$NON-NLS-1$

	/* the proxy */
	BundleLoaderProxy proxy;
	/* Bundle object */
	BundleHost bundle;
	/* The is the BundleClassLoader for the bundle */
	BundleClassLoader classloader;

	/* cache of imported packages. Key is packagename, Value is PackageSource */
	KeyedHashSet importedPackages;
	/* flag that indicates this bundle has dynamic imports */
	boolean hasDynamicImports = false;
	/* If true, import all packages dynamically. */
	boolean dynamicImportPackageAll;
	/* If not null, list of package stems to import dynamically. */
	String[] dynamicImportPackageStems;
	/* If not null, list of package names to import dynamically. */
	String[] dynamicImportPackages;
	/* List of package names that are provided by this BundleLoader */
	ArrayList providedPackages;
	/* cache of required package sources. Key is packagename, value is PackageSource */
	KeyedHashSet requiredPackagesCache;
	/* List of required bundle BundleLoaderProxy objects */
	BundleLoaderProxy[] requiredBundles;
	/* List of indexes into the requiredBundles list of reexported bundles */
	int[] reexportTable;

	/**
	 * Returns the package name from the specified class name.
	 * The returned package is dot seperated.
	 *
	 * @param name   Name of a class.
	 * @return Dot separated package name or null if the class
	 *         has no package name.
	 */
	protected static String getPackageName(String name) {
		if (name != null) {
			int index = name.lastIndexOf('.'); /* find last period in class name */
			if (index > 0)
				return name.substring(0, index);
		}
		return DEFAULT_PACKAGE;
	}

	/**
	 * Returns the package name from the specified resource name.
	 * The returned package is dot seperated.
	 *
	 * @param name   Name of a resource.
	 * @return Dot separated package name or null if the resource
	 *         has no package name.
	 */
	protected static String getResourcePackageName(String name) {
		if (name != null) {
			/* check for leading slash*/
			int begin = ((name.length() > 1) && (name.charAt(0) == '/')) ? 1 : 0;
			int end = name.lastIndexOf('/'); /* index of last slash */
			if (end > begin)
				return name.substring(begin, end).replace('/', '.');
		}
		return DEFAULT_PACKAGE;
	}

	/**
	 * BundleLoader runtime constructor. This object is created lazily
	 * when the first request for a resource is made to this bundle.
	 *
	 * @param bundle Bundle object for this loader.
	 * @param proxy the BundleLoaderProxy for this loader.
	 * @exception org.osgi.framework.BundleException
	 */
	protected BundleLoader(BundleHost bundle, BundleLoaderProxy proxy) throws BundleException {
		this.bundle = bundle;
		this.proxy = proxy;
		try {
			bundle.getBundleData().open(); /* make sure the BundleData is open */
		} catch (IOException e) {
			throw new BundleException(Msg.BUNDLE_READ_EXCEPTION, e); //$NON-NLS-1$
		}
		initialize(proxy.getBundleDescription());
	}

	protected void initialize(BundleDescription description) {
		if (!(this instanceof SystemBundleLoader) && SystemBundleLoader.getSystemPackages() != null) {
			hasDynamicImports = true;
			importedPackages = new KeyedHashSet();
		}

		// init the imported packages list taking the bundle...
		addImportedPackages(description.getResolvedImports());

		// init the require bundles list.
		BundleDescription[] required = description.getResolvedRequires();
		if (required.length > 0) {
			// get a list of re-exported symbolic names
			HashSet reExportSet = new HashSet(required.length);
			BundleSpecification[] requiredSpecs = description.getRequiredBundles();
			if (requiredSpecs != null && requiredSpecs.length > 0)
				for (int i = 0; i < requiredSpecs.length; i++)
					if (requiredSpecs[i].isExported())
						reExportSet.add(requiredSpecs[i].getName());

			requiredBundles = new BundleLoaderProxy[required.length];
			int[] reexported = new int[required.length];
			int reexportIndex = 0;
			for (int i = 0; i < required.length; i++) {
				requiredBundles[i] = (BundleLoaderProxy) getLoaderProxy(required[i]);
				if (reExportSet.contains(required[i].getSymbolicName()))
					reexported[reexportIndex++] = i;							
			}
			if (reexportIndex > 0) {
				reexportTable = new int[reexportIndex];
				System.arraycopy(reexported, 0, reexportTable, 0, reexportIndex);
			}
		}

		// init the provided packages set
		ExportPackageDescription[] exports = description.getSelectedExports();
		if (exports != null && exports.length > 0) {
			providedPackages = new ArrayList(exports.length);
			for (int i = 0; i < exports.length; i++) {
				// must force filtered and reexport sources to be created early
				// to prevent lazy normal package source creation.
				proxy.createPackageSource(exports[i], true);
				if (!providedPackages.contains(exports[i].getName()))
					providedPackages.add(exports[i].getName());
			}
		}
		//This is the fastest way to access to the description for fragments since the hostdescription.getFragments() is slow
		org.osgi.framework.Bundle[] fragmentObjects = bundle.getFragments();
		BundleDescription[] fragments = new BundleDescription[fragmentObjects == null ? 0 : fragmentObjects.length];
		for (int i = 0; i < fragments.length; i++)
			fragments[i] = ((AbstractBundle) fragmentObjects[i]).getBundleDescription();			
		// init the dynamic imports tables
		if (description.hasDynamicImports())
			addDynamicImportPackage(description.getImportPackages());
		// ...and its fragments
		for (int i = 0; i < fragments.length; i++)
			if (fragments[i].isResolved() && fragments[i].hasDynamicImports())
				addDynamicImportPackage(fragments[i].getImportPackages());
	}

	protected void initializeFragment(AbstractBundle fragment) throws BundleException {
		BundleDescription description = fragment.getBundleDescription();
		// if the fragment dynamically imports a package not already 
		// dynamically imported throw an exception.
		try {
			ImportPackageSpecification[] imports = description.getImportPackages();
			if (imports != null && imports.length > 0) {
				for (int i = 0; i < imports.length; i++) {
					if (imports[i].getResolution() != ImportPackageSpecification.RESOLUTION_DYNAMIC)
						continue;
					String name = imports[i].getName();
					if (!isDynamicallyImported(name))
						throw new BundleException(NLS.bind(Msg.BUNDLE_FRAGMENT_IMPORT_CONFLICT, new Object[] {fragment.getLocation(), imports[i], bundle.getLocation()})); //$NON-NLS-1$
				}
			}
		} catch (BundleException e) {
			bundle.framework.publishFrameworkEvent(FrameworkEvent.ERROR, bundle, e);
		}

	}

	private void addImportedPackages(ExportPackageDescription[] packages) {
		if (packages != null && packages.length > 0) {
			if (importedPackages == null)
				importedPackages = new KeyedHashSet();
			for (int i = 0; i < packages.length; i++) {
				PackageSource source = createExportPackageSource(packages[i]);
				if (source == null)
					return;
				importedPackages.add(source);
			}
		}
	}

	protected PackageSource createExportPackageSource(ExportPackageDescription export) {
		BundleLoaderProxy exportProxy = getLoaderProxy(export.getExporter());
		if (exportProxy == null)
			// TODO log error!!
			return null;
		return exportProxy.createPackageSource(export, false);
	}

	private static PackageSource createMultiSource(String packageName, PackageSource[] sources) {
		if (sources.length == 1)
			return sources[0];
		ArrayList sourceList = new ArrayList(sources.length);
		for (int i = 0; i < sources.length; i++) {
			SingleSourcePackage[] innerSources = sources[i].getSuppliers();
			for (int j = 0; j < innerSources.length; j++)
				if (!sourceList.contains(innerSources[j]))
					sourceList.add(innerSources[j]);
		}
		return new MultiSourcePackage(packageName, (SingleSourcePackage[]) sourceList.toArray(new SingleSourcePackage[sourceList.size()]));
	}

	/*
	 * get the loader proxy for a bundle description
	 */
	BundleLoaderProxy getLoaderProxy(BundleDescription source) {
		BundleLoaderProxy proxy = (BundleLoaderProxy) source.getUserObject();
		if (proxy == null) {
			// may need to force the proxy to be created
			long exportingID = source.getBundleId();
			BundleHost exportingBundle = (BundleHost) bundle.framework.getBundle(exportingID);
			if (exportingBundle == null)
				return null;
			proxy = exportingBundle.getLoaderProxy();
		}
		return proxy;
	}

	/*
	 * Close the the BundleLoader.
	 *
	 */
	protected void close() {
		if (bundle == null)
			return;
		importedPackages = null;

		if (classloader != null)
			classloader.close();
		classloader = null;
		bundle = null; /* This indicates the BundleLoader is destroyed */
	}

	/**
	 * This method loads a class from the bundle.  The class is searched for in the
	 * same manner as it would if it was being loaded from a bundle (i.e. all
	 * hosts, fragments, import, required bundles and local resources are searched.
	 *
	 * @param      name     the name of the desired Class.
	 * @return     the resulting Class
	 * @exception  java.lang.ClassNotFoundException  if the class definition was not found.
	 */
	protected Class loadClass(String name) throws ClassNotFoundException {
		return createClassLoader().loadClass(name);
	}

	/**
	 * This method gets a resource from the bundle.  The resource is searched 
	 * for in the same manner as it would if it was being loaded from a bundle 
	 * (i.e. all hosts, fragments, import, required bundles and 
	 * local resources are searched).
	 *
	 * @param name the name of the desired resource.
	 * @return the resulting resource URL or null if it does not exist.
	 */
	protected URL getResource(String name) {
		return createClassLoader().getResource(name);
	}

	/**
	 * This method gets resources from the bundle.  The resource is searched 
	 * for in the same manner as it would if it was being loaded from a bundle 
	 * (i.e. all hosts, fragments, import, required bundles and 
	 * local resources are searched).
	 *
	 * @param name the name of the desired resource.
	 * @return the resulting resource URL or null if it does not exist.
	 */
	protected Enumeration getResources(String name) throws IOException {
		return createClassLoader().getResources(name);
	}

	protected BundleClassLoader createClassLoader() {
		if (classloader != null)
			return classloader;
		synchronized (this) {
			if (classloader != null)
				return classloader;

			try {
				String[] classpath = bundle.getBundleData().getClassPath();
				if (classpath != null) {
					classloader = createBCLPrevileged(bundle.getProtectionDomain(), classpath);
				} else {
					bundle.framework.publishFrameworkEvent(FrameworkEvent.ERROR, bundle, new BundleException(Msg.BUNDLE_NO_CLASSPATH_MATCH)); //$NON-NLS-1$
				}
			} catch (BundleException e) {
				bundle.framework.publishFrameworkEvent(FrameworkEvent.ERROR, bundle, e);
			}

		}
		return classloader;
	}

	/**
	 * Finds a class local to this bundle.  Only the classloader for this bundle is searched.
	 * @param name The name of the class to find.
	 * @return The loaded Class or null if the class is not found.
	 */
	protected Class findLocalClass(String name) {
		if (Debug.DEBUG && Debug.DEBUG_LOADER)
			Debug.println("BundleLoader[" + this + "].findLocalClass(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		try {
			Class clazz = createClassLoader().findLocalClass(name);
			if (Debug.DEBUG && Debug.DEBUG_LOADER && clazz != null)
				Debug.println("BundleLoader[" + this + "] found local class " + name); //$NON-NLS-1$ //$NON-NLS-2$
			return clazz;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Finds the class for a bundle.  This method is used for delegation by the bundle's classloader.
	 */
	public Class findClass(String name) throws ClassNotFoundException {
		if (isClosed())
			throw new ClassNotFoundException(name);

		if (Debug.DEBUG && Debug.DEBUG_LOADER) {
			Debug.println("BundleLoader[" + this + "].loadBundleClass(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		String packageName = getPackageName(name);

		Class result = findImportedClass(name, packageName);
		if (result == null)
			result = findRequiredClass(name, packageName);
		if (result == null) {
			result = findLocalClass(name);
			if (result == null) {
				throw new ClassNotFoundException(name);
			}
		}

		return result;
	}

	boolean isClosed() {
		return bundle == null;
	}

	/**
	 * Finds the resource for a bundle.  This method is used for delegation by the bundle's classloader.
	 */
	public URL findResource(String name) {
		if (isClosed())
			return null;
		if ((name.length() > 1) && (name.charAt(0) == '/')) /* if name has a leading slash */
			name = name.substring(1); /* remove leading slash before search */

		String packageName = getResourcePackageName(name);

		URL resource = findImportedResource(name, packageName);
		if (resource == null)
			resource = findRequiredResource(name, packageName);
		if (resource == null)
			resource = findLocalResource(name);

		return resource;
	}

	/**
	 * Finds the resources for a bundle.  This  method is used for delegation by the bundle's classloader.
	 */
	public Enumeration findResources(String name) throws IOException {
		if (isClosed())
			return null;
		if ((name.length() > 1) && (name.charAt(0) == '/')) /* if name has a leading slash */
			name = name.substring(1); /* remove leading slash before search */

		String packageName = getResourcePackageName(name);

		Enumeration result = findImportedResources(name, packageName);
		if (result == null)
			result = findRequiredResources(name, packageName);
		if (result == null)
			result = findLocalResources(name);

		return result;
	}

	/**
	 * Finds a resource local to this bundle.  Only the classloader for this bundle is searched.
	 * @param name The name of the resource to find.
	 * @return The URL to the resource or null if the resource is not found.
	 */
	protected URL findLocalResource(final String name) {
		if (System.getSecurityManager() == null)
			return createClassLoader().findLocalResource(name);
		return (URL) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return createClassLoader().findLocalResource(name);
			}
		});
	}

	/**
	 * Returns an Enumeration of URLs representing all the resources with
	 * the given name. Only the classloader for this bundle is searched.
	 *
	 * @param  name the resource name
	 * @return an Enumeration of URLs for the resources
	 */
	protected Enumeration findLocalResources(String name) {
		if ((name.length() > 1) && (name.charAt(0) == '/')) /* if name has a leading slash */
			name = name.substring(1);
		return createClassLoader().findLocalResources(name);
	}

	/**
	 * Returns the absolute path name of a native library.
	 *
	 * @param      name   the library name
	 * @return     the absolute path of the native library or null if not found
	 */
	public String findLibrary(final String name) {
		if (isClosed())
			return null;
		if (System.getSecurityManager() == null)
			return findLocalLibrary(name);
		return (String) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return findLocalLibrary(name);
			}
		});
	}

	protected String findLocalLibrary(final String name) {
		String result = bundle.getBundleData().findLibrary(name);
		if (result != null)
			return result;

		org.osgi.framework.Bundle[] fragments = bundle.getFragments();
		if (fragments == null || fragments.length == 0)
			return null;

		// look in fragments imports ...
		for (int i = 0; i < fragments.length; i++) {
			result = ((AbstractBundle) fragments[i]).getBundleData().findLibrary(name);
			if (result != null)
				return result;
		}
		return result;
	}

	/*
	 * Return the bundle we are associated with.
	 */
	protected AbstractBundle getBundle() {
		return bundle;
	}

	private BundleClassLoader createBCLPrevileged(final BundleProtectionDomain pd, final String[] cp) {
		// Create the classloader as previleged code if security manager is present.
		if (System.getSecurityManager() == null)
			return createBCL(pd, cp);
		else
			return (BundleClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					return createBCL(pd, cp);
				}
			});

	}
	
	BundleClassLoader createBCL(final BundleProtectionDomain pd, final String[] cp) {
		BundleClassLoader bcl = bundle.getBundleData().createClassLoader(BundleLoader.this, pd, cp);
		// attach existing fragments to classloader
		org.osgi.framework.Bundle[] fragments = bundle.getFragments();
		if (fragments != null)
			for (int i = 0; i < fragments.length; i++) {
				AbstractBundle fragment = (AbstractBundle) fragments[i];
				try {
					bcl.attachFragment(fragment.getBundleData(), fragment.domain, fragment.getBundleData().getClassPath());
				} catch (BundleException be) {
					bundle.framework.publishFrameworkEvent(FrameworkEvent.ERROR, bundle, be);
				}
			}

		// finish the initialization of the classloader.
		bcl.initialize();

		return bcl;
	}

	/**
	 * Return a string representation of this loader.
	 * @return String
	 */
	public String toString() {
		BundleData result = bundle.getBundleData();
		return result == null ? "BundleLoader.bundledata == null!" : result.toString(); //$NON-NLS-1$
	}

	/**
	 * Get the BundleLoader for the package if it is imported.
	 * @param pkgname The name of the package to import.
	 * @return BundleLoader to load from or null if the package is not imported.
	 */
	protected PackageSource getImportPackageSource(String pkgname) {
		if (pkgname == null)
			return null;
		PackageSource source = null;
		if (importedPackages != null) {
			source = (PackageSource) importedPackages.getByKey(pkgname);
			if (source != null)
				return source;
		}

		if (isDynamicallyImported(pkgname)) {
			ExportPackageDescription exportPackage = bundle.framework.adaptor.getState().linkDynamicImport(proxy.getBundleDescription(), pkgname);
			if (exportPackage != null) {
				source = createExportPackageSource(exportPackage);
				importedPackages.add(source);
				return source;
			}
		}
		return null;
	}

	/**
	 * Return true if the target package name matches
	 * a name in the DynamicImport-Package manifest header.
	 *
	 * @param pkgname The name of the requested class' package.
	 * @return true if the package should be imported.
	 */
	protected boolean isDynamicallyImported(String pkgname) {
		if (this instanceof SystemBundleLoader)
			return false; // system bundle cannot dynamically import
		// must check for startsWith("java.") to satisfy R3 section 4.7.2
		if (pkgname.startsWith("java.")) //$NON-NLS-1$
			return true;

		/* quick shortcut check */
		if (!hasDynamicImports)
			return false;

		/* "*" shortcut */
		if (dynamicImportPackageAll)
			return true;

		/* 
		 * If including the system bundle packages by default, dynamically import them.
		 * Most OSGi framework implementations assume the system bundle packages
		 * are on the VM classpath.  As a result some bundles neglect to import
		 * framework packages (e.g. org.osgi.framework).
		 */
		String[] systemPackages = SystemBundleLoader.getSystemPackages();
		if (systemPackages != null) {
			for (int i = 0; i < systemPackages.length; i++)
				if (pkgname.equals(systemPackages[i]))
					return true;
		}

		/* match against specific names */
		if (dynamicImportPackages != null)
			for (int i = 0; i < dynamicImportPackages.length; i++)
				if (pkgname.equals(dynamicImportPackages[i]))
					return true;

		/* match against names with trailing wildcards */
		if (dynamicImportPackageStems != null)
			for (int i = 0; i < dynamicImportPackageStems.length; i++)
				if (pkgname.startsWith(dynamicImportPackageStems[i]))
					return true;

		return false;
	}

	/**
	 * Find a class using the imported packages for this bundle.  Only the 
	 * ImportClassLoader is used for the search. 
	 * @param name The name of the class to find.
	 * @return The loaded class or null if the class does not belong to a package
	 * that is imported by the bundle.
	 * @throws ImportClassNotFoundException If the class does belong to a package
	 * that is imported by the bundle but the class is not found.
	 */
	protected Class findImportedClass(String name, String packageName) throws ImportClassNotFoundException {
		if (Debug.DEBUG && Debug.DEBUG_LOADER)
			Debug.println("ImportClassLoader[" + this + "].findImportedClass(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		Class result = null;

		try {
			PackageSource source = getImportPackageSource(packageName);
			if (source != null) {
				result = source.loadClass(name, packageName);
				if (result == null)
					throw new ImportClassNotFoundException(name);
			}
		} finally {
			if (result == null) {
				if (Debug.DEBUG && Debug.DEBUG_LOADER)
					Debug.println("ImportClassLoader[" + this + "] class " + name + " not found in imported package " + packageName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				if (Debug.DEBUG && Debug.DEBUG_LOADER)
					Debug.println("BundleLoader[" + this + "] found imported class " + name); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return result;
	}

	protected void addExportedProvidersFor(String packageName, ArrayList result, KeyedHashSet visited) {
		if (!visited.add(bundle))
			return;

		// See if we locally provide the package.
		PackageSource local = null;
		if (isProvidedPackage(packageName))
			local = proxy.getPackageSource(packageName);
		// Must search required bundles that are exported first.
		if (requiredBundles != null) {
			int size = reexportTable == null ? 0 : reexportTable.length;
			int reexportIndex = 0;
			for (int i = 0; i < requiredBundles.length; i++) {
				if (local != null) {
					// always add required bundles first if we locally provide the package
					// This allows a bundle to provide a package from a required bundle without 
					// re-exporting the whole required bundle.
					requiredBundles[i].getBundleLoader().addExportedProvidersFor(packageName, result, visited);
				} else if (reexportIndex < size && reexportTable[reexportIndex] == i) {
					reexportIndex++;
					requiredBundles[i].getBundleLoader().addExportedProvidersFor(packageName, result, visited);
				}
			}
		}

		// now add the locally provided package.
		if (local != null)
			result.add(local);
	}

	/**
	 * Gets the PackageSource for the package name specified.  Only
	 * the required bundles are searched.
	 * @param packageName The name of the package to find the PackageSource for.
	 * @return The loaded class or null if the class is not found.
	 */
	protected PackageSource getProvidersFor(String packageName) {
		// first look in the required packages cache
		if (requiredPackagesCache != null) {
			PackageSource result = (PackageSource) requiredPackagesCache.getByKey(packageName);

			if (result != null) {
				if (result.isNullSource()) {
					return null;
				}
				return result;
			}
		}

		// didn't find it in the cache search the actual required bundles
		if (requiredBundles == null)
			return null;
		KeyedHashSet visited = new KeyedHashSet(false);
		ArrayList result = new ArrayList(3);
		for (int i = 0; i < requiredBundles.length; i++) {
			BundleLoader requiredLoader = requiredBundles[i].getBundleLoader();
			requiredLoader.addExportedProvidersFor(packageName, result, visited);
		}

		// found some so cache the result for next time and return
		if (requiredPackagesCache == null)
			requiredPackagesCache = new KeyedHashSet();
		if (result.size() == 0) {
			// did not find it in our required bundles lets record the failure
			// so we do not have to do the search again for this package.
			requiredPackagesCache.add(new NullPackageSource(packageName));
			return null;
		} else if (result.size() == 1) {
			// if there is just one source, remember just the single source 
			PackageSource source = (PackageSource) result.get(0);
			requiredPackagesCache.add(source);
			return source;
		} else {
			// if there was more than one source, build a multisource and cache that.
			PackageSource[] sources = (PackageSource[]) result.toArray(new PackageSource[result.size()]);
			PackageSource source = createMultiSource(packageName, sources);
			requiredPackagesCache.add(source);
			return source;
		}
	}

	/**
	 * Find a class using the required bundles for this bundle.  Only the
	 * required bundles are used to search for the class.
	 * @param name The name of the class to find.
	 * @return The loaded class or null if the class is not found.
	 */
	protected Class findRequiredClass(String name, String packageName) {
		if (Debug.DEBUG && Debug.DEBUG_LOADER)
			Debug.println("ImportClassLoader[" + this + "].findRequiredClass(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		PackageSource source = getProvidersFor(packageName);
		if (source == null)
			return null;
		return source.loadClass(name, packageName);
	}

	protected boolean isProvidedPackage(String name) {
		return providedPackages == null ? false : providedPackages.contains(name);
	}

	/**
	 * Find a resource using the imported packages for this bundle.  Only the 
	 * ImportClassLoader is used for the search. 
	 * @param name The name of the resource to find.
	 * @return The URL of the resource or null if the resource does not belong to a package
	 * that is imported by the bundle.
	 * @throws ImportResourceNotFoundException If the resource does belong to a package
	 * that is imported by the bundle but the resource is not found.
	 */
	protected URL findImportedResource(String name, String packageName) {
		if (Debug.DEBUG && Debug.DEBUG_LOADER)
			Debug.println("ImportClassLoader[" + this + "].findImportedResource(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		PackageSource source = getImportPackageSource(packageName);
		if (source != null) {
			URL url = source.getResource(name, packageName);
			if (url != null)
				return url;
			if (Debug.DEBUG && Debug.DEBUG_LOADER)
				Debug.println("ImportClassLoader[" + this + "] resource " + name + " not found in imported package " + packageName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			throw new ImportResourceNotFoundException(name);
		}
		return null;
	}

	/**
	 * Find a resource using the required bundles for this bundle.  Only the
	 * required bundles are used to search.
	 * @param name The name of the resource to find.
	 * @return The URL for the resource or null if the resource is not found.
	 */
	protected URL findRequiredResource(String name, String packageName) {
		if (Debug.DEBUG && Debug.DEBUG_LOADER)
			Debug.println("ImportClassLoader[" + this + "].findRequiredResource(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		PackageSource source = getProvidersFor(packageName);
		if (source == null)
			return null;
		return source.getResource(name, packageName);
	}

	/**
	 * Returns an Enumeration of URLs representing all the resources with
	 * the given name.
	 *
	 * If the resource is in a package that is imported, call the exporting
	 * bundle. Otherwise return null.
	 *
	 * @param  name the resource name
	 * @return an Enumeration of URLs for the resources if the package is
	 * imported, null otherwise.
	 */
	protected Enumeration findImportedResources(String name, String packageName) throws IOException{
		if (Debug.DEBUG && Debug.DEBUG_LOADER)
			Debug.println("ImportClassLoader[" + this + "].findImportedResources(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		PackageSource source = getImportPackageSource(packageName);
		if (source != null)
			return source.getResources(name, packageName);
		return null;
	}

	/**
	 * Returns an Enumeration of URLs representing all the resources with
	 * the given name.
	 * Find the resources using the required bundles for this bundle.  Only the
	 * required bundles are used to search.
	 *
	 * If the resource is in a package that is imported, call the exporting
	 * bundle. Otherwise return null.
	 *
	 * @param  name the resource name
	 * @return an Enumeration of URLs for the resources if the package is
	 * imported, null otherwise.
	 */
	protected Enumeration findRequiredResources(String name, String packageName) throws IOException {
		if (Debug.DEBUG && Debug.DEBUG_LOADER)
			Debug.println("ImportClassLoader[" + this + "].findRequiredResources(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		PackageSource source = getProvidersFor(packageName);
		if (source == null)
			return null;
		return source.getResources(name, packageName);
	}

	private void addDynamicImportPackage(ImportPackageSpecification[] packages) {
		if (packages == null)
			return;
		ArrayList dynamicImports = new ArrayList(packages.length);
		for (int i = 0; i < packages.length; i++)
			if (packages[i].getResolution() == ImportPackageSpecification.RESOLUTION_DYNAMIC)
				dynamicImports.add(packages[i].getName());
		if (dynamicImports.size() > 0)
			addDynamicImportPackage((String[]) dynamicImports.toArray(new String[dynamicImports.size()]));
	}

	/**
	 * Adds a list of DynamicImport-Package manifest elements to the dynamic
	 * import tables of this BundleLoader.  Duplicate packages are checked and
	 * not added again.  This method is not thread safe.  Callers should ensure
	 * synchronization when calling this method.
	 * @param packages the DynamicImport-Package elements to add.
	 */
	private void addDynamicImportPackage(String[] packages) {
		if (packages == null && SystemBundleLoader.getSystemPackages() == null)
			return;

		hasDynamicImports = true;
		// make sure importedPackages is not null;
		if (importedPackages == null) {
			importedPackages = new KeyedHashSet();
		}

		if (packages == null)
			return;

		int size = packages.length;
		ArrayList stems;
		if (dynamicImportPackageStems == null) {
			stems = new ArrayList(size);
		} else {
			stems = new ArrayList(size + dynamicImportPackageStems.length);
			for (int i = 0; i < dynamicImportPackageStems.length; i++) {
				stems.add(dynamicImportPackageStems[i]);
			}
		}

		ArrayList names;
		if (dynamicImportPackages == null) {
			names = new ArrayList(size);
		} else {
			names = new ArrayList(size + dynamicImportPackages.length);
			for (int i = 0; i < dynamicImportPackages.length; i++) {
				names.add(dynamicImportPackages[i]);
			}
		}

		for (int i = 0; i < size; i++) {
			String name = packages[i];
			if (isDynamicallyImported(name))
				continue;
			if (name.equals("*")) { /* shortcut */ //$NON-NLS-1$
				dynamicImportPackageAll = true;
				return;
			}

			if (name.endsWith(".*")) //$NON-NLS-1$
				stems.add(name.substring(0, name.length() - 1));
			else
				names.add(name);
		}

		size = stems.size();
		if (size > 0)
			dynamicImportPackageStems = (String[]) stems.toArray(new String[size]);

		size = names.size();
		if (size > 0)
			dynamicImportPackages = (String[]) names.toArray(new String[size]);
	}

	/**
	 * Adds a list of DynamicImport-Package manifest elements to the dynamic
	 * import tables of this BundleLoader.  Duplicate packages are checked and
	 * not added again.  This method is not thread safe.  Callers should ensure
	 * synchronization when calling this method.
	 * @param packages the DynamicImport-Package elements to add.
	 */
	public void addDynamicImportPackage(ManifestElement[] packages) {
		if (packages == null)
			return;
		ArrayList dynamicImports = new ArrayList(packages.length);
		for (int i = 0; i < packages.length; i++)
			dynamicImports.add(packages[i].getValue());
		if (dynamicImports.size() > 0)
			addDynamicImportPackage((String[]) dynamicImports.toArray(new String[dynamicImports.size()]));
	}

	protected void clear() {
		providedPackages = null;
		requiredBundles = null;
		requiredPackagesCache = null;
		reexportTable = null;
		importedPackages = null;
		dynamicImportPackages = null;
		dynamicImportPackageStems = null;
	}

	protected void attachFragment(BundleFragment fragment) throws BundleException {
		initializeFragment(fragment);
		if (classloader == null)
			return;

		try {
			String[] classpath = fragment.getBundleData().getClassPath();
			if (classpath != null)
				classloader.attachFragment(fragment.getBundleData(), fragment.domain, classpath);
			else
				bundle.framework.publishFrameworkEvent(FrameworkEvent.ERROR, bundle, new BundleException(Msg.BUNDLE_NO_CLASSPATH_MATCH)); //$NON-NLS-1$
		} catch (BundleException e) {
			bundle.framework.publishFrameworkEvent(FrameworkEvent.ERROR, bundle, e);
		}

	}

	protected FilterImpl createFilter(String filterString) throws InvalidSyntaxException {
		if (filterString == null)
			return null;
		int length = filterString.length();
		if (length <= 2) {
			throw new InvalidSyntaxException(Msg.FILTER_INVALID, filterString); //$NON-NLS-1$
		}
		return new FilterImpl(filterString);
	}

	PackageSource getPackageSource(String pkgname) {
		PackageSource result = getImportPackageSource(pkgname);
		if (result != null) {
			return result;
		}
		PackageSource requireSource = getProvidersFor(pkgname);
		PackageSource localSource = isProvidedPackage(pkgname) ? proxy.getPackageSource(pkgname) : null;
		if (localSource instanceof BundleLoaderProxy.ReexportPackageSource)
			localSource = new SingleSourcePackage(pkgname, proxy);
		if (requireSource == null)
			return localSource;
		if (localSource == null)
			return requireSource;
		return createMultiSource(pkgname, new PackageSource[] {requireSource, localSource});
	}

}
