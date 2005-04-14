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
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;

/**
 * This object is responsible for all classloader delegation for a bundle.
 * It represents the loaded state of the bundle.  BundleLoader objects
 * are created lazily; care should be taken not to force the creation
 * of a BundleLoader unless it is necessary.
 * @see org.eclipse.osgi.framework.internal.core.BundleLoaderProxy
 */
public class BundleLoader implements ClassLoaderDelegate {
	public final static String DEFAULT_PACKAGE = "."; //$NON-NLS-1$
	public final static String JAVA_PACKAGE = "java."; //$NON-NLS-1$

	/* the proxy */
	BundleLoaderProxy proxy;
	/* Bundle object */
	BundleHost bundle;
	/* The is the BundleClassLoader for the bundle */
	BundleClassLoader classloader;
	ClassLoader parent;

	/* cache of imported packages. Key is packagename, Value is PackageSource */
	KeyedHashSet importedSources;
	boolean importsInit = false;
	/* cache of required package sources. Key is packagename, value is PackageSource */
	KeyedHashSet requiredSources;
	/* flag that indicates this bundle has dynamic imports */
	boolean hasDynamicImports = false;
	/* If true, import all packages dynamically. */
	boolean dynamicImportPackageAll;
	/* If not null, list of package stems to import dynamically. */
	String[] dynamicImportPackageStems;
	/* If not null, list of package names to import dynamically. */
	String[] dynamicImportPackages;
	/* List of package names that are exported by this BundleLoader */
	ArrayList exportedPackages;
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
	public final static String getPackageName(String name) {
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
	public final static String getResourcePackageName(String name) {
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

	final void initialize(BundleDescription description) {
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
				requiredBundles[i] = getLoaderProxy(required[i]);
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
			exportedPackages = new ArrayList(exports.length);
			for (int i = 0; i < exports.length; i++) {
				if (!exportedPackages.contains(exports[i].getName())) {
					exportedPackages.add(exports[i].getName());
					// must force filtered and reexport sources to be created early
					// to prevent lazy normal package source creation.
					// We only do this for the first export of a package name. 
					proxy.createPackageSource(exports[i], true);
				}
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

	private synchronized void addImportedPackages(ExportPackageDescription[] packages) {
		if (importsInit)
			return;
		if (packages != null && packages.length > 0) {
			if (importedSources == null)
				importedSources = new KeyedHashSet(packages.length, false);
			for (int i = 0; i < packages.length; i++) {
				PackageSource source = createExportPackageSource(packages[i]);
				if (source != null)
					importedSources.add(source);
			}
		}
		importsInit = true;
	}

	final PackageSource createExportPackageSource(ExportPackageDescription export) {
		BundleLoaderProxy exportProxy = getLoaderProxy(export.getExporter());
		if (exportProxy == null)
			// TODO log error!!
			return null;
		PackageSource requiredSource = exportProxy.getBundleLoader().findRequiredSource(export.getName());
		PackageSource exportSource = exportProxy.createPackageSource(export, false);
		if (requiredSource == null)
			return exportSource;
		return createMultiSource(export.getName(), new PackageSource[] {requiredSource, exportSource});
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
	final BundleLoaderProxy getLoaderProxy(BundleDescription source) {
		BundleLoaderProxy sourceProxy = (BundleLoaderProxy) source.getUserObject();
		if (sourceProxy == null) {
			// may need to force the proxy to be created
			long exportingID = source.getBundleId();
			BundleHost exportingBundle = (BundleHost) bundle.framework.getBundle(exportingID);
			if (exportingBundle == null)
				return null;
			sourceProxy = exportingBundle.getLoaderProxy();
		}
		return sourceProxy;
	}

	/*
	 * Close the the BundleLoader.
	 *
	 */
	void close() {
		if (bundle == null)
			return;
		importedSources = null;

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
	final Class loadClass(String name) throws ClassNotFoundException {
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
	final URL getResource(String name) {
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
	final Enumeration getResources(String name) throws IOException {
		return createClassLoader().getResources(name);
	}

	final BundleClassLoader createClassLoader() {
		if (classloader != null)
			return classloader;
		synchronized (this) {
			if (classloader != null)
				return classloader;

			try {
				String[] classpath = bundle.getBundleData().getClassPath();
				if (classpath != null) {
					BundleClassLoader bcl = createBCLPrevileged(bundle.getProtectionDomain(), classpath);
					parent = getParentPrivileged(bcl);
					classloader = bcl;
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
	Class findLocalClass(String name) {
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
		return findClass(name, true);
	}

	Class findClass(String name, boolean checkParent) throws ClassNotFoundException {
		if (Debug.DEBUG && Debug.DEBUG_LOADER)
			Debug.println("BundleLoader[" + this + "].loadBundleClass(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// follow the OSGi delegation model
		if (checkParent && parent != null) {
			if (name.startsWith(JAVA_PACKAGE))
				// 1) if startsWith "java." delegate to parent and terminate search
				// we want to throw ClassNotFoundExceptions if a java.* class cannot be loaded from the parent.
				return parent.loadClass(name);
			else if (isBootDelegationPackage(name))
				// 2) if part of the bootdelegation list then delegate to parent and continue of failure
				try {
					return parent.loadClass(name);
				} catch (ClassNotFoundException cnfe) {
					// we want to continue
				}
		}

		if (isClosed())
			throw new ClassNotFoundException(name);
		String pkgName = getPackageName(name);
		Class result = null;
		// 3) search the imported packages
		PackageSource source = findImportedSource(pkgName);
		if (source != null)
			// 3) found import source terminate search at the source
			return source.loadClass(name);
		// 4) search the required bundles
		source = findRequiredSource(pkgName);
		if (source != null)
			// 4) attempt to load from source but continue on failure
			result = source.loadClass(name);
		// 5) search the local bundle
		if (result == null)
			result = findLocalClass(name);
		if (result != null)
			return result;
		// 6) attempt to find a dynamic import source
		source = findDynamicSource(pkgName);
		if (source != null)
			result = source.loadClass(name);
		if (result == null)
			throw new ClassNotFoundException(name);
		return result;
	}

	final boolean isClosed() {
		return bundle == null;
	}

	/**
	 * Finds the resource for a bundle.  This method is used for delegation by the bundle's classloader.
	 */
	public URL findResource(String name) {
		return findResource(name, true);
	}

	URL findResource(String name, boolean checkParent) {
		if ((name.length() > 1) && (name.charAt(0) == '/')) /* if name has a leading slash */
			name = name.substring(1); /* remove leading slash before search */
		String pkgName = getResourcePackageName(name);
		// follow the OSGi delegation model
		// First check the parent classloader for system resources, if it is a java resource.
		if (checkParent && parent != null) {
			if (pkgName.startsWith(JAVA_PACKAGE))
				// 1) if startsWith "java." delegate to parent and terminate search
				// we never delegate java resource requests past the parent
				return parent.getResource(name);
			else if (isBootDelegationPackage(pkgName)) {
				// 2) if part of the bootdelegation list then delegate to parent and continue of failure
				URL result = parent.getResource(name);
				if (result != null)
					return result;
			}
		}
		if (isClosed())
			return null;

		URL result = null;
		// 3) search the imported packages
		PackageSource source = findImportedSource(pkgName);
		if (source != null)
			// 3) found import source terminate search at the source
			return source.getResource(name);
		// 4) search the required bundles
		source = findRequiredSource(pkgName);
		if (source != null)
			// 4) attempt to load from source but continue on failure
			result = source.getResource(name);
		// 5) search the local bundle
		if (result == null)
			result = findLocalResource(name);
		if (result != null)
			return result;
		// 6) attempt to find a dynamic import source
		source = findDynamicSource(pkgName);
		if (source != null)
			result = source.getResource(name);
		return result;
	}

	private boolean isBootDelegationPackage(String name) {
		if (bundle.framework.bootDelegateAll)
			return true;
		if (bundle.framework.bootDelegation == null)
			return false;
		for (int i = 0; i < bundle.framework.bootDelegation.length; i++)
			if (name.startsWith(bundle.framework.bootDelegation[i]))
				return true;
		return false;
	}

	/**
	 * Finds the resources for a bundle.  This  method is used for delegation by the bundle's classloader.
	 */
	public Enumeration findResources(String name) throws IOException {
		// do not delegate to parent because ClassLoader#getResources already did and it is final!!
		if (isClosed())
			return null;
		if ((name.length() > 1) && (name.charAt(0) == '/')) /* if name has a leading slash */
			name = name.substring(1); /* remove leading slash before search */
		String pkgName = getResourcePackageName(name);
		Enumeration result = null;
		// start at step 3 because of the comment above about ClassLoader#getResources
		// 3) search the imported packages
		PackageSource source = findImportedSource(pkgName);
		if (source != null)
			// 3) found import source terminate search at the source
			return source.getResources(name);
		// 4) search the required bundles
		source = findRequiredSource(pkgName);
		if (source != null)
			// 4) attempt to load from source but continue on failure
			result = source.getResources(name);
		// 5) search the local bundle
		if (result == null)
			result = findLocalResources(name);
		if (result != null)
			return result;
		// 6) attempt to find a dynamic import source
		source = findDynamicSource(pkgName);
		if (source != null)
			result = source.getResources(name);
		return result;
	}

	/**
	 * Finds a resource local to this bundle.  Only the classloader for this bundle is searched.
	 * @param name The name of the resource to find.
	 * @return The URL to the resource or null if the resource is not found.
	 */
	URL findLocalResource(final String name) {
		return createClassLoader().findLocalResource(name);
	}

	/**
	 * Returns an Enumeration of URLs representing all the resources with
	 * the given name. Only the classloader for this bundle is searched.
	 *
	 * @param  name the resource name
	 * @return an Enumeration of URLs for the resources
	 */
	Enumeration findLocalResources(String name) {
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

	final String findLocalLibrary(final String name) {
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
	final AbstractBundle getBundle() {
		return bundle;
	}

	private BundleClassLoader createBCLPrevileged(final BundleProtectionDomain pd, final String[] cp) {
		// Create the classloader as previleged code if security manager is present.
		if (System.getSecurityManager() == null)
			return createBCL(pd, cp);

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
	public final String toString() {
		BundleData result = bundle.getBundleData();
		return result == null ? "BundleLoader.bundledata == null!" : result.toString(); //$NON-NLS-1$
	}

	/**
	 * Return true if the target package name matches
	 * a name in the DynamicImport-Package manifest header.
	 *
	 * @param pkgname The name of the requested class' package.
	 * @return true if the package should be imported.
	 */
	final boolean isDynamicallyImported(String pkgname) {
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

	final void addExportedProvidersFor(String symbolicName, String packageName, ArrayList result, KeyedHashSet visited) {
		if (!visited.add(bundle))
			return;

		// See if we locally provide the package.
		PackageSource local = null;
		if (isExportedPackage(packageName))
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
					requiredBundles[i].getBundleLoader().addExportedProvidersFor(symbolicName, packageName, result, visited);
				} else if (reexportIndex < size && reexportTable[reexportIndex] == i) {
					reexportIndex++;
					requiredBundles[i].getBundleLoader().addExportedProvidersFor(symbolicName, packageName, result, visited);
				}
			}
		}

		// now add the locally provided package.
		if (local != null && local.isFriend(symbolicName))
			result.add(local);
	}

	final boolean isExportedPackage(String name) {
		return exportedPackages == null ? false : exportedPackages.contains(name);
	}

	private void addDynamicImportPackage(ImportPackageSpecification[] packages) {
		if (packages == null)
			return;
		ArrayList dynamicImports = new ArrayList(packages.length);
		for (int i = 0; i < packages.length; i++)
			if (ImportPackageSpecification.RESOLUTION_DYNAMIC.equals(packages[i].getDirective(Constants.RESOLUTION_DIRECTIVE)))
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
		if (packages == null)
			return;

		hasDynamicImports = true;
		// make sure importedPackages is not null;
		if (importedSources == null) {
			importedSources = new KeyedHashSet(10, false);
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
			if (name.equals("*")) { /* shortcut *///$NON-NLS-1$
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
	public final void addDynamicImportPackage(ManifestElement[] packages) {
		if (packages == null)
			return;
		ArrayList dynamicImports = new ArrayList(packages.length);
		for (int i = 0; i < packages.length; i++)
			dynamicImports.add(packages[i].getValue());
		if (dynamicImports.size() > 0)
			addDynamicImportPackage((String[]) dynamicImports.toArray(new String[dynamicImports.size()]));
	}

	final void clear() {
		exportedPackages = null;
		requiredBundles = null;
		requiredSources = null;
		reexportTable = null;
		importedSources = null;
		dynamicImportPackages = null;
		dynamicImportPackageStems = null;
	}

	final void attachFragment(BundleFragment fragment) throws BundleException {
		if (classloader == null)
			return;
		String[] classpath = fragment.getBundleData().getClassPath();
		if (classpath != null)
			classloader.attachFragment(fragment.getBundleData(), fragment.domain, classpath);
	}

	/*
	 * Finds a packagesource that is either imported or required from another bundle.
	 * This will not include an local package source
	 */
	private PackageSource findSource(String pkgName) {
		if (pkgName == null)
			return null;
		PackageSource result = findImportedSource(pkgName);
		if (result != null)
			return result;
		result = findRequiredSource(pkgName);
		if (result == null)
			result = findDynamicSource(pkgName);
		return result;
	}

	private PackageSource findImportedSource(String pkgName) {
		if (!importsInit)
			addImportedPackages(proxy.getBundleDescription().getResolvedImports());
		return importedSources == null ? null : (PackageSource) importedSources.getByKey(pkgName);
	}

	private PackageSource findDynamicSource(String pkgName) {
		if (isDynamicallyImported(pkgName)) {
			ExportPackageDescription exportPackage = bundle.framework.adaptor.getState().linkDynamicImport(proxy.getBundleDescription(), pkgName);
			if (exportPackage != null) {
				PackageSource source = createExportPackageSource(exportPackage);
				importedSources.add(source);
				return source;
			}
		}
		return null;
	}

	private PackageSource findRequiredSource(String pkgName) {
		if (requiredBundles == null)
			return null;
		if (requiredSources != null) {
			PackageSource result = (PackageSource) requiredSources.getByKey(pkgName);
			if (result != null)
				return result.isNullSource() ? null : result;
		}
		KeyedHashSet visited = new KeyedHashSet(false);
		ArrayList result = new ArrayList(3);
		for (int i = 0; i < requiredBundles.length; i++) {
			BundleLoader requiredLoader = requiredBundles[i].getBundleLoader();
			requiredLoader.addExportedProvidersFor(proxy.getSymbolicName(), pkgName, result, visited);
		}
		if (requiredSources == null)
			requiredSources = new KeyedHashSet(10, false);
		// found some so cache the result for next time and return
		if (result.size() == 0) {
			// did not find it in our required bundles lets record the failure
			// so we do not have to do the search again for this package.
			requiredSources.add(NullPackageSource.getNullPackageSource(pkgName));
			return null;
		} else if (result.size() == 1) {
			// if there is just one source, remember just the single source 
			PackageSource source = (PackageSource) result.get(0);
			requiredSources.add(source);
			return source;
		} else {
			// if there was more than one source, build a multisource and cache that.
			PackageSource[] srcs = (PackageSource[]) result.toArray(new PackageSource[result.size()]);
			PackageSource source = createMultiSource(pkgName, srcs);
			requiredSources.add(source);
			return source;
		}
	}

	/*
	 * Gets the package source for the pkgName.  This will include the local package source
	 * if the bundle exports the package.  This is used to compare the PackageSource of a 
	 * package from two different bundles.
	 */
	final PackageSource getPackageSource(String pkgName) {
		PackageSource result = findSource(pkgName);
		if (!isExportedPackage(pkgName))
			return result;
		// if the package is exported then we need to get the local source
		PackageSource localSource = proxy.getPackageSource(pkgName);
		if (localSource instanceof BundleLoaderProxy.ReexportPackageSource)
			localSource = new SingleSourcePackage(pkgName, -1, proxy);
		if (result == null)
			return localSource;
		if (localSource == null)
			return result;
		return createMultiSource(pkgName, new PackageSource[] {result, localSource});
	}

	private ClassLoader getParentPrivileged(final BundleClassLoader bcl) {
		if (System.getSecurityManager() == null)
			return bcl.getParent();

		return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return bcl.getParent();
			}
		});
	}
}
