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

package org.eclipse.osgi.framework.adaptor.core;

import java.io.IOException;
import java.net.URL;
import java.security.*;
import java.util.Enumeration;
import org.eclipse.osgi.framework.adaptor.*;
import org.eclipse.osgi.framework.debug.Debug;

/**
 * The AbstractClassLoader provides some basic functionality that all
 * BundleClassLoaders must provide.  It properly delegates resource and
 * class lookups to a parent classloader and the to a ClassLoaderDelegate.
 */
public abstract class AbstractClassLoader extends ClassLoader implements BundleClassLoader {
	/**
	 * The delegate used to get classes and resources from.  The delegate
	 * must always be queried first before the local ClassLoader is searched for
	 * a class or resource.
	 */
	protected ClassLoaderDelegate delegate;

	/**
	 * The host ProtectionDomain to use to define classes.
	 */
	protected ProtectionDomain hostdomain;

	/**
	 * The host classpath entries for this classloader
	 */
	protected String[] hostclasspath;

	/**
	 * BundleClassLoader constructor.
	 * @param delegate The ClassLoaderDelegate for this bundle.
	 * @param domain The ProtectionDomain for this bundle.
	 * @param parent The parent classloader to use.  Must not be null.
	 * @param classpath The classpath entries to use for the host.
	 */
	public AbstractClassLoader(ClassLoaderDelegate delegate, ProtectionDomain domain, String[] classpath, ClassLoader parent) {
		super(parent);
		this.delegate = delegate;
		this.hostdomain = domain;
		this.hostclasspath = classpath;
	}

	/**
	 * Loads a class for the bundle.  First delegate.findClass(name) is called.
	 * The delegate will query the system class loader, bundle imports, bundle
	 * local classes, bundle hosts and fragments.  The delegate will call 
	 * BundleClassLoader.findLocalClass(name) to find a class local to this 
	 * bundle.  
	 * @param name the name of the class to load.
	 * @param resolve indicates whether to resolve the loaded class or not.
	 * @return The Class object.
	 * @throws ClassNotFoundException if the class is not found.
	 */
	protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (Debug.DEBUG && Debug.DEBUG_LOADER)
			Debug.println("BundleClassLoader[" + delegate + "].loadClass(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		try {
			// Just ask the delegate.  This could result in findLocalClass(name) being called.
			Class clazz = delegate.findClass(name);
			// resolve the class if asked to.
			if (resolve)
				resolveClass(clazz);
			return (clazz);
		} catch (Error e) {
			if (Debug.DEBUG && Debug.DEBUG_LOADER) {
				Debug.println("BundleClassLoader[" + delegate + "].loadClass(" + name + ") failed."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				Debug.printStackTrace(e);
			}
			throw e;
		} catch (ClassNotFoundException e) {
			// If the class is not found do not try to look for it locally.
			// The delegate would have already done that for us.
			if (Debug.DEBUG && Debug.DEBUG_LOADER) {
				Debug.println("BundleClassLoader[" + delegate + "].loadClass(" + name + ") failed."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				Debug.printStackTrace(e);
			}
			throw e;
		}
	}

	/**
	 * Finds a class local to this bundle.  The bundle class path is used
	 * to search for the class.  The delegate must not be used.  This method
	 * is abstract to force extending classes to implement this method instead
	 * of using the ClassLoader.findClass(String) method.
	 * @param name The classname of the class to find
	 * @return The Class object.
	 * @throws ClassNotFoundException if the class is not found.
	 */
	abstract protected Class findClass(String name) throws ClassNotFoundException;

	/**
	 * Gets a resource for the bundle.  First delegate.findResource(name) is 
	 * called. The delegate will query the system class loader, bundle imports,
	 * bundle local resources, bundle hosts and fragments.  The delegate will 
	 * call BundleClassLoader.findLocalResource(name) to find a resource local 
	 * to this bundle.  
	 * @param name The resource path to get.
	 * @return The URL of the resource or null if it does not exist.
	 */
	public URL getResource(String name) {
		if (Debug.DEBUG && Debug.DEBUG_LOADER) {
			Debug.println("BundleClassLoader[" + delegate + "].getResource(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		URL url = delegate.findResource(name);
		if (url != null)
			return (url);

		if (Debug.DEBUG && Debug.DEBUG_LOADER) {
			Debug.println("BundleClassLoader[" + delegate + "].getResource(" + name + ") failed."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		return (null);
	}

	/**
	 * Finds a resource local to this bundle.  Simply calls 
	 * findResourceImpl(name) to find the resource.
	 * @param name The resource path to find.
	 * @return The URL of the resource or null if it does not exist.
	 */
	abstract protected URL findResource(String name);

	/**
	 * Finds all resources with the specified name.  This method must call
	 * delegate.findResources(name) to find all the resources.
	 * @param name The resource path to find.
	 * @return An Enumeration of all resources found or null if the resource.
	 * @throws IOException 
	 */
	protected Enumeration findResources(String name) throws IOException {
		return (delegate.findResources(name));
	}

	/**
	 * Finds a library for this bundle.  Simply calls 
	 * delegate.findLibrary(libname) to find the library.
	 * @param libname The library to find.
	 * @return The URL of the resource or null if it does not exist.
	 */
	protected String findLibrary(String libname) {
		return delegate.findLibrary(libname);
	}

	/**
	 * Finds a local resource in the BundleClassLoader without
	 * consulting the delegate.
	 * @param resource the resource path to find.
	 * @return a URL to the resource or null if the resource does not exist.
	 */
	public URL findLocalResource(String resource) {
		return findResource(resource);
	}

	/**
	 * Finds a local class in the BundleClassLoader without
	 * consulting the delegate.
	 * @param classname the classname to find.
	 * @return The class object found.
	 * @throws ClassNotFoundException if the classname does not exist locally.
	 */
	public Class findLocalClass(String classname) throws ClassNotFoundException {
		return findClass(classname);
	}

	abstract public Object findLocalObject(String path);

	abstract public Enumeration findLocalObjects(String path);

	public ClassLoaderDelegate getDelegate() {
		return delegate;
	}

	public void close() {
		// do nothing
	}

}
