/**
 * Copyright (c) 1999 - 2002 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */

package org.osgi.impl.framework;

import java.io.*;
import java.net.URL;
import java.security.*;
import java.util.*;

/**
 * Classloader for bundle JAR files.
 *
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
class BundleClassLoader extends ClassLoader
{
    /**
     * Archives that we load code from.
     */
    private Vector /* Archive */ classArchives = new Vector();
    
    /**
     * Native code libraries.
     */
    private HashMap /* String->String */ nativeLibrary = new HashMap();
    
    /**
     * Imported java package.
     */
    private BundlePackages bpkgs;

    
    /**
     * Create class loader for specified bundle.
     */
    BundleClassLoader(BundlePackages bpkgs) {
	this.bpkgs = bpkgs;
    }
    
    //
    // ClassLoader classes
    //
    
    // NYI: Package handling isn't handle yet. It would be nice if we
    // could rely on the system provide package information so that
    // we could generate a list of which packages that the system export.
    
    /**
     * Find bundle class to load.
     * First check if this load comes from an imported package.
     * Otherwise load class from out bundle.
     *
     * @see java.lang.ClassLoader#findClass
     */
    protected Class findClass(String name) throws ClassNotFoundException
    {
	BundleClassLoader cl = this;
	int pos = name.lastIndexOf('.');
	if (pos != -1) {
	    String pkg = name.substring(0, pos);
	    BundleImpl p = bpkgs.getProviderBundle(pkg);
	    if (p != null) {
		if (p.getBundleId() != 0) {
		    cl = p.getExporterClassLoader(pkg);
		} else {
		    throw new ClassNotFoundException(name);
		}
	    }
	}
	return cl.loadOwnClass(name);
    }
    
    
    /**
     * Find native library code to load.
     *
     * @see java.lang.ClassLoader#findLibrary
     */
    protected String findLibrary(String name)
    {
	return (String)nativeLibrary.get(System.mapLibraryName(name));
    }
    
    
    /**
     * Returns an Enumeration of all the resources with the given name.
     *
     * @see java.lang.ClassLoader#findResources
     */
    protected Enumeration findResources(String name)
    {
	BundleClassLoader cl = this;
	int pos = name.lastIndexOf('/');
	if (pos > 0) {
	    int start = name.startsWith("/") ? 1 : 0;
	    String pkg = name.substring(start, pos).replace('/', '.');
	    BundleImpl p = bpkgs.getProviderBundle(pkg);
	    if (p != null && p.getBundleId() != 0) {
		cl = p.getExporterClassLoader(pkg);
	    }
	}
	return cl.findBundleResources(name);
    }
    
    
    /**
     * Finds the resource with the given name.
     *
     * @see java.lang.ClassLoader#findResource
     */
    protected URL findResource(String name)
    {
	Enumeration e = findResources(name);
	return (URL) (e.hasMoreElements() ? e.nextElement() : null);
    }
    
    //
    // BundleClassLoader specific
    //
    
    /**
     * Load of class from our bundle.
     * First check if it is already loaded. Then try all archives in this
     * bundles classpath.
     */
    synchronized Class loadOwnClass(String name) throws ClassNotFoundException
    {
	Class c = (Class) findLoadedClass(name);
	if (c == null) {
	    byte[] bytes = null;
	    String path = name.replace('.','/') + ".class";
	    for (Enumeration e = classArchives.elements(); e.hasMoreElements();) {
		Archive a = (Archive)e.nextElement();
		try {
		    bytes = a.getBytes(path);
		    if (bytes != null) {
			return defineClass(name, bytes, 0, bytes.length,
					   bpkgs.bundle.protectionDomain);
		    }
		} catch (IOException ioe) {
		    bpkgs.bundle.framework.listeners.frameworkError(bpkgs.bundle, ioe);
		}
	    }
	    throw new ClassNotFoundException(name);
	} else {
	    return c;
	}
    }
    
    
    /**
     * Add archive to this bundles classpath.
     */
    void addClassArchive(Archive a)
    {
	classArchives.add(a);
    }
    
    
    /**
     * Add native library
     */
    void addNativeLibrary(String name, String path)
    {
	nativeLibrary.put(name, path);
    }
    
    
    /**
     * Get bundle owning this class loader.
     */
    BundleImpl getBundle()
    {
	return bpkgs.bundle;
    }
    
    
    /**
     * Get resource InputStream.
     */
    InputStream getInputStream(URL burl) throws IOException
    {
	if (BundleURLStreamHandler.PROTOCOL.equals(burl.getProtocol())) {
	    int i = burl.getPort();
	    if (i < 0 || i >= classArchives.size()) {
		throw new IOException("Illegal URL port");
	    }
	    return ((Archive)classArchives.elementAt(i)).getInputStream(burl.getFile());
	} else {
	    throw new IOException("Illegal URL protocol");
	}
    }
    
    
    /**
     * Close down this classloader.
     * We don't give out any new classes. Perhaps we should
     * block all classloads.
     */
    void close()
    {
	classArchives.clear();
	nativeLibrary.clear();
    }
    
    
    /**
     * Close down this classloader and all its archives.
     * Purge all archives.
     *
     */
    void purge()
    {
	bpkgs.unregisterPackages(true);
	for (Enumeration e = classArchives.elements(); e.hasMoreElements();) {
	    ((Archive)e.nextElement()).purge();
	}
	close();
    }


    /**
     * Get bundle package handler.
     *
     */
    BundlePackages getBpkgs() {
	return bpkgs;
    }


  //
  // Private methods
  //

    /**
     * Find resources within bundle.
     *
     * @return Enumeration of resources
     */
    Enumeration findBundleResources(final String name)
    {
	final Vector answer = new Vector(1);
	for (int i = 0; i < classArchives.size(); i++) {
	    try {
		InputStream test = ((Archive)classArchives.elementAt(i)).getInputStream(name);
		if (test != null) {
		    final int idx = i;
		    answer.add(AccessController.doPrivileged(new PrivilegedAction() {
			    public Object run() {
				return bpkgs.bundle.getBundleURL(idx, name);
			    }
			}));
		}
	    } catch (Exception ignore) { }
	}
	return answer.elements();
    }

}
