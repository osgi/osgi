/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.*;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;
import org.eclipse.osgi.framework.internal.core.Constants;
import org.eclipse.osgi.framework.internal.protocol.bundleentry.Handler;
import org.eclipse.osgi.framework.util.Headers;
import org.eclipse.osgi.service.resolver.Version;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * An abstract BundleData class that has default implementations that most
 * BundleData implementations can use.
 */
public abstract class AbstractBundleData implements BundleData {

	/** the DefaultAdaptor for this BundleData */
	protected AbstractFrameworkAdaptor adaptor;

	/**
	 * The BundleManfifest for this BundleData.
	 */
	protected Dictionary manifest = null;

	/**
	 * The Bundle object for this BundleData.
	 */
	protected Bundle bundle;

	/** bundle id */
	protected long id;

	///////////////////// Begin Meta Data for the Bundle /////////////////////

	/** bundle location */
	private String location;

	/** bundle's file name */
	private String fileName;

	/** native code paths for this BundleData */
	private String[] nativePaths;

	/** bundle generation */
	private int generation = 1;

	/** the bundles start level */
	private int startLevel = -1;

	/** the bundles status */
	private int status = 0;

	/** Is bundle a reference */
	private boolean reference;

	///////////////////// End Meta Data for the Bundle   /////////////////////

	///////////////////// Begin values from Manifest     /////////////////////
	private String symbolicName;
	private Version version;
	private String activator;
	private String classpath;
	private String executionEnvironment;
	private String dynamicImports;
	private boolean fragment = false;

	///////////////////// End values from Manifest       /////////////////////

	public AbstractBundleData(AbstractFrameworkAdaptor adaptor, long id) {
		this.adaptor = adaptor;
		this.id = id;
	}

	/**
	 * Return the BundleManifest for the BundleData.  If the manifest
	 * field is null this method will parse the bundle manifest file and
	 * construct a BundleManifest file to return.  If the manifest field is
	 * not null then the manifest object is returned.
	 * @return BundleManifest for the BundleData.
	 * @throws BundleException if an error occurred while reading the
	 * bundle manifest data.
	 */
	public Dictionary getManifest() throws BundleException {
		if (manifest == null) {
			synchronized (this) {
				// make sure the manifest is still null after we have aquired the lock.
				if (manifest == null) {
					URL url = getEntry(Constants.OSGI_BUNDLE_MANIFEST);
					if (url == null) {
						throw new BundleException(AdaptorMsg.formatter.getString("MANIFEST_NOT_FOUND_EXCEPTION", Constants.OSGI_BUNDLE_MANIFEST, getLocation())); //$NON-NLS-1$
					}
					try {
						manifest = Headers.parseManifest(url.openStream());
					} catch (IOException e) {
						throw new BundleException(AdaptorMsg.formatter.getString("MANIFEST_NOT_FOUND_EXCEPTION", Constants.OSGI_BUNDLE_MANIFEST, getLocation()), e); //$NON-NLS-1$
					}
				}
			}
		}
		return manifest;
	}

	/**
	 * Sets the Bundle object for this BundleData.
	 * @param bundle The Bundle Object for this BundleData.
	 */
	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * Returns the Bundle object for this BundleData.
	 * @return the Bundle object for this BundleData.
	 */
	public Bundle getBundle() {
		return bundle;
	}

	/**
	 * Get the BundleData bundle ID.  This will be used as the bundle
	 * ID by the framework.
	 * @return The BundleData ID.
	 */
	public long getBundleID() {
		return (id);
	}

	/**
	 * Gets a <code>URL</code> to the bundle entry specified by path. This
	 * method must not use the BundleClassLoader to find the bundle entry since
	 * the ClassLoader will delegate to find the resource.
	 * 
	 * @param path
	 *            The bundle entry path.
	 * @return A URL used to access the entry or null if the entry does not
	 *         exist.
	 */
	public URL getEntry(String path) {
		BundleEntry entry = getBaseBundleFile().getEntry(path);
		if (entry == null) {
			return null;
		}
		try {
			return new URL(null, getBundleEntryURL(id, path), new Handler(entry));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * Gets all of the bundle entries that exist under the specified path. For
	 * example:
	 * <p>
	 * <code>getEntryPaths("/META-INF")</code>
	 * <p>
	 * This will return all entries from the /META-INF directory of the bundle.
	 * 
	 * @param path
	 *            The path to a directory in the bundle.
	 * @return An Enumeration of the entry paths or null if the specified path
	 *         does not exist.
	 */
	public Enumeration getEntryPaths(String path) {
		return getBaseBundleFile().getEntryPaths(path);
	}

	/**
	 * Creates the ClassLoader for the BundleData.  The ClassLoader created
	 * must use the <code>ClassLoaderDelegate</code> to delegate class, resource
	 * and library loading.  The delegate is responsible for finding any resource
	 * or classes imported by the bundle or provided by bundle fragments or 
	 * bundle hosts.  The <code>ProtectionDomain</code> domain must be used
	 * by the Classloader when defining a class.  
	 * @param delegate The <code>ClassLoaderDelegate</code> to delegate to.
	 * @param domain The <code>ProtectionDomain</code> to use when defining a class.
	 * @param bundleclasspath An array of bundle classpaths to use to create this
	 * classloader.  This is specified by the Bundle-ClassPath manifest entry.
	 * @return The new ClassLoader for the BundleData.
	 */
	public org.eclipse.osgi.framework.adaptor.BundleClassLoader createClassLoader(ClassLoaderDelegate delegate, ProtectionDomain domain, String[] bundleclasspath) {
		return getAdaptor().getElementFactory().createClassLoader(delegate, domain, bundleclasspath, this);
	}

	/**
	 * Return the generation directory for the bundle data.  The generation
	 * directory can be used by the framework to cache files from the bundle
	 * to the file system.
	 * @return The generation directory for the bundle data or null if not
	 * supported.
	 */
	abstract public File createGenerationDir();

	/**
	 * Return the base BundleFile for this BundleData.  The base BundleFile
	 * is the BundleFile that contains all the content of the bundle.
	 * @return the base BundleFile.
	 */
	abstract public BundleFile getBaseBundleFile();

	public AbstractFrameworkAdaptor getAdaptor() {
		return adaptor;
	}

	public static String getBundleEntryURL(long id, String path) {
		StringBuffer url = new StringBuffer(Constants.OSGI_ENTRY_URL_PROTOCOL);
		url.append("://").append(id); //$NON-NLS-1$
		if (path.length() == 0 || path.charAt(0) != '/') {
			url.append('/');
		}
		url.append(path);
		return url.toString();
	}

	/* 
	 * Convenience method that retrieves the simbolic name string from the header.
	 * Note: clients may want to cache the returned value.
	 */
	public static String parseSymbolicName(Dictionary manifest) {
		String symbolicNameEntry = (String) manifest.get(Constants.BUNDLE_SYMBOLICNAME);
		if (symbolicNameEntry == null)
			return null;
		try {
			return ManifestElement.parseHeader(Constants.BUNDLE_SYMBOLICNAME, symbolicNameEntry)[0].getValue();
		} catch (BundleException e) {
			// here is not the place to validate a manifest			
		}
		return null;
	}

	///////////////////// Begin Meta Data Accessor Methods ////////////////////
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String[] getNativePaths() {
		return nativePaths;
	}

	public String getNativePathsString() {
		if (nativePaths == null || nativePaths.length == 0)
			return null;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nativePaths.length; i++) {
			sb.append(nativePaths[i]);
			if (i < nativePaths.length - 1)
				sb.append(',');
		}
		return sb.toString();
	}

	public void setNativePaths(String[] nativePaths) {
		this.nativePaths = nativePaths;
	}

	public void setNativePaths(String nativePaths) {
		if (nativePaths == null)
			return;
		ArrayList result = new ArrayList(5);
		StringTokenizer st = new StringTokenizer(nativePaths, ","); //$NON-NLS-1$
		while (st.hasMoreTokens()) {
			String path = st.nextToken();
			result.add(path);
		}
		setNativePaths((String[]) result.toArray(new String[result.size()]));
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	public int getStartLevel() {
		return startLevel;
	}

	public void setStartLevel(int startLevel) {
		this.startLevel = startLevel;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isReference() {
		return reference;
	}

	public void setReference(boolean reference) {
		this.reference = reference;
	}

	///////////////////// End Meta Data Accessor Methods   ////////////////////

	///////////////////// Begin Manifest Value Accessor Methods /////////////////////

	public String getSymbolicName() {
		return symbolicName;
	}

	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public String getActivator() {
		return activator;
	}

	public void setActivator(String activator) {
		this.activator = activator;
	}

	public String getClassPath() {
		return classpath;
	}

	public void setClassPath(String classpath) {
		this.classpath = classpath;
	}

	public String getExecutionEnvironment() {
		return executionEnvironment;
	}

	public void setExecutionEnvironment(String executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}

	public String getDynamicImports() {
		return dynamicImports;
	}

	public void setDynamicImports(String dynamicImports) {
		this.dynamicImports = dynamicImports;
	}

	public boolean isFragment() {
		return fragment;
	}

	public void setFragment(boolean fragment) {
		this.fragment = fragment;
	}
	///////////////////// End Manifest Value Accessor Methods  /////////////////////

}