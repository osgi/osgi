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

package org.eclipse.osgi.framework.adaptor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import org.osgi.framework.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * The <code>BundleData</code> represents a single bundle that is persistently 
 * stored by a <code>FrameworkAdaptor</code>.  A <code>BundleData</code> creates
 * the ClassLoader for a bundle, finds native libraries installed in the
 * FrameworkAdaptor for the bundle, creates data files for the bundle,
 * used to access bundle entries, manifest information, and getting and saving
 * metadata.
 */
public interface BundleData {

	/** The BundleData is for a fragment bundle */
	public static final int TYPE_FRAGMENT =					0x00000001;
	/** The BundleData is for a framework extension bundle */
	public static final int TYPE_FRAMEWORK_EXTENSION =		0x00000002;
	/** The BundleData is for a bootclasspath extension bundle */
	public static final int TYPE_BOOTCLASSPATH_EXTENSION =	0x00000004;
	

	/**
	 * Creates the ClassLoader for the BundleData.  The ClassLoader created
	 * must use the <code>ClassLoaderDelegate</code> to delegate class, resource
	 * and library loading.  The delegate is responsible for finding any resource
	 * or classes imported by the bundle through an imported package or a required
	 * bundle. <p>
	 * The <code>ProtectionDomain</code> domain must be used by the Classloader when 
	 * defining a class.  
	 * @param delegate The <code>ClassLoaderDelegate</code> to delegate to.
	 * @param domain The <code>BundleProtectionDomain</code> to use when defining a class.
	 * @param bundleclasspath An array of bundle classpaths to use to create this
	 * classloader.  This is specified by the Bundle-ClassPath manifest entry.
	 * @return The new ClassLoader for the BundleData.
	 */
	public BundleClassLoader createClassLoader(ClassLoaderDelegate delegate, BundleProtectionDomain domain, String[] bundleclasspath);

	/**
	 * Gets a <code>URL</code> to the bundle entry specified by path.
	 * This method must not use the BundleClassLoader to find the
	 * bundle entry since the ClassLoader will delegate to find the resource.
	 * @see org.osgi.framework.Bundle#getEntry(String)
	 * @param path The bundle entry path.
	 * @return A URL used to access the entry or null if the entry
	 * does not exist.
	 */
	public URL getEntry(String path);

	/**
	 * Gets all of the bundle entries that exist under the specified path.
	 * For example: <p>
	 * <code>getEntryPaths("/META-INF")</code> <p>
	 * This will return all entries from the /META-INF directory of the bundle.
	 * @see org.osgi.framework.Bundle#getEntryPaths(String path)
	 * @param path The path to a directory in the bundle.
	 * @return An Enumeration of the entry paths or null if the specified path
	 * does not exist.
	 */
	public Enumeration getEntryPaths(String path);

	/**
	 * Returns the absolute path name of a native library. The BundleData
	 * ClassLoader invokes this method to locate the native libraries that 
	 * belong to classes loaded from this BundleData. Returns 
	 * null if the library does not exist in this BundleData.
	 * @param libname The name of the library to find the absolute path to.
	 * @return The absolute path name of the native library or null if
	 * the library does not exist.
	 */
	public String findLibrary(String libname);

	/**
	 * Installs the native code paths for this BundleData.  Each
	 * element of nativepaths must be installed for lookup when findLibrary 
	 * is called.
	 * @param nativepaths The array of native code paths to install for
	 * the bundle.
	 * @throws BundleException If any error occurs during install.
	 */
	public void installNativeCode(String[] nativepaths) throws BundleException;

	/**
	 * Return the bundle data directory.
	 * Attempt to create the directory if it does not exist.
	 *
	 * @see org.osgi.framework.BundleContext#getDataFile(String)
	 * @return Bundle data directory or null if not supported.
	 */

	public File getDataFile(String path);

	/**
	 * Return the Dictionary of manifest headers for the BundleData.
	 * @return Dictionary that contains the Manifest headers for the BundleData.
	 * @throws BundleException if an error occurred while reading the
	 * bundle manifest data.
	 */
	public Dictionary getManifest() throws BundleException;

	/**
	 * Get the BundleData bundle ID.  This will be used as the bundle
	 * ID by the framework.
	 * @return The BundleData ID.
	 */
	public long getBundleID();

	/**
	 * Get the BundleData Location.  This will be used as the bundle
	 * location by the framework.
	 * @return the BundleData location.
	 */
	public String getLocation();

	/**
	 * Get the last time this BundleData was modified.
	 * @return the last time this BundleData was modified
	 */
	public long getLastModified();

	/**
	 * Close all resources for this BundleData
	 * @throws IOException If an error occurs closing.
	 */
	public void close() throws IOException;

	/**
	 * Open the BundleData. This method will reopen the BundleData if it has been
	 * previously closed.
	 * @throws IOException If an error occurs opening.
	 */
	public void open() throws IOException;

	/**
	 * Sets the Bundle object for this BundleData.
	 * @param bundle The Bundle Object for this BundleData.
	 */
	public void setBundle(Bundle bundle);

	/**
	 * Returns the start level metadata for this BundleData.
	 * @return the start level metadata for this BundleData.
	 */
	public int getStartLevel();

	/**
	 * Returns the status metadata for this BundleData.  A value of 1
	 * indicates that this bundle is started persistently.  A value of 0
	 * indicates that this bundle is not started persistently.
	 * @return the status metadata for this BundleData.
	 */
	public int getStatus();

	/**
	 * Sets the start level metatdata for this BundleData.  Metadata must be
	 * stored persistently when BundleData.save() is called.
	 * @param value the start level metadata
	 */
	public void setStartLevel(int value);

	/**
	 * Sets the status metadata for this BundleData.  Metadata must be
	 * stored persistently when BundleData.save() is called.
	 * @param value the status metadata.
	 */
	public void setStatus(int value);

	/**
	 * Persistently stores all the metadata for this BundleData
	 * @throws IOException
	 */
	public void save() throws IOException;

	/**
	 * Returns the Bundle-SymbolicName for this BundleData as specified in the bundle
	 * manifest file.
	 * @return the Bundle-SymbolicName for this BundleData.
	 */
	public String getSymbolicName();

	/**
	 * Returns the Bundle-Version for this BundleData as specified in the bundle 
	 * manifest file.
	 * @return the Bundle-Version for this BundleData.
	 */
	public Version getVersion();

	/**
	 * Indicates whether this BundleData is a fragment bundle.  A bundle
	 * is considered to be a fragment bundle if its bundle manifest file
	 * has a Fragment-Host entry.
	 * @return the value of true if this BundleData represents a fragment bundle;
	 * false otherwise.
	 * @deprecated use {@link #getType()}
	 */
	public boolean isFragment();

	/**
	 * Returns the type of bundle this BundleData is for.  
	 * @return returns the type of bundle this BundleData is for
	 */
	public int getType();

	/**
	 * Returns the Bundle-ClassPath for this BundleData as specified in 
	 * the bundle manifest file.
	 * @return the classpath for this BundleData.
	 */
	public String[] getClassPath() throws BundleException;

	/**
	 * Returns the Bundle-Activator for this BundleData as specified in 
	 * the bundle manifest file.
	 * @return the Bundle-Activator for this BundleData.
	 */
	public String getActivator();

	/**
	 * Returns the Bundle-RequiredExecutionEnvironment for this BundleData as 
	 * specified in the bundle manifest file.
	 * @return the Bundle-RequiredExecutionEnvironment for this BundleData.
	 */
	public String getExecutionEnvironment();

	/**
	 * Returns the DynamicImport-Package for this BundleData as 
	 * specified in the bundle manifest file.
	 * @return the DynamicImport-Packaget for this BundleData.
	 */
	public String getDynamicImports();

	/**
	 * Retrieves the certificate chains that signed this bundle. Only
	 * validated certificate chains are returned. Each element of the returned
	 * array will contain a chain of distinguished names (DNs) separated by
	 * semicolons. The first DN is the signer and the last is the root
	 * Certificate Authority.
	 * 
	 * @return the certificate chains that signed this repository.  If bundle
	 * signing is not supported or the bundle is not signed then null is returned.
	 */
	public String[] getBundleSigners();
}
