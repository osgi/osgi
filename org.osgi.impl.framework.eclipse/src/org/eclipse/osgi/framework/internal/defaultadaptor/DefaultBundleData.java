/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.defaultadaptor;

import java.io.File;
import java.io.IOException;
import org.eclipse.osgi.framework.adaptor.core.*;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.internal.core.Constants;
import org.eclipse.osgi.service.resolver.Version;
import org.osgi.framework.BundleException;

/**
 * The <code>BundleData</code> represents a single bundle that is persistently 
 * stored by a <code>FrameworkAdaptor</code>.  A <code>BundleData</code> creates
 * the ClassLoader for a bundle, finds native libraries installed in the
 * FrameworkAdaptor for the bundle, creates data files for the bundle,
 * used to access bundle entries, manifest information, and getting and saving
 * metadata.
 * 
 */
public class DefaultBundleData extends AbstractBundleData implements Cloneable {

	/** The top level storage directory for the BundleData */
	protected File bundleStoreDir;

	/** The BundleData data directory */
	protected File dirData;

	/** The base BundleFile object for this BundleData */
	protected BundleFile baseBundleFile;

	/**
	 * Constructs a DefaultBundleData for the DefaultAdaptor.
	 *
	 * @param adaptor the DefaultAdaptor for this DefaultBundleData
	 * @param id the Bundle ID for this DefaultBundleData
	 */
	public DefaultBundleData(DefaultAdaptor adaptor, long id) {
		super(adaptor, id);
		initBundleStoreDirs(String.valueOf(id));
	}
	
	public void setManifest(String manifest) {
		java.util.Hashtable dic = new java.util.Hashtable();
		java.util.StringTokenizer st = new java.util.StringTokenizer(manifest, ":\n");
		
		while(st.hasMoreTokens()) {
			String key = st.nextToken().trim();
			String value = st.nextToken().trim();
			dic.put(key, value);
		}
		this.manifest = dic;
	}

	/**
	 * Read data from an existing directory.
	 * This constructor is used by getInstalledBundles.
	 *
	 * @throws NumberFormatException if the directory is not a
	 * number, the directory contains a ".delete" file or
	 * the directory does not contain a ".bundle" file.
	 * @throws IOException If an error occurs initializing the bundle data.
	 */
	public void initializeExistingBundle() throws BundleException, IOException {
		File delete = new File(getBundleStoreDir(), ".delete"); //$NON-NLS-1$

		/* and the directory is not marked for delete */
		if (delete.exists())
			throw new IOException();

		createBaseBundleFile();

		loadFromManifest();
	}

	public void initializeNewBundle() throws IOException, BundleException {
		createBaseBundleFile();

		loadFromManifest();
	}

	protected void initBundleStoreDirs(String bundleID) {
		setBundleStoreDir(new File(((DefaultAdaptor) adaptor).getBundleStoreRootDir(), bundleID));
	}

	/**
	 * Returns the absolute path name of a native library. The BundleData
	 * ClassLoader invokes this method to locate the native libraries that 
	 * belong to classes loaded from this BundleData. Returns 
	 * null if the library does not exist in this BundleData.
	 * @param libname The name of the library to find the absolute path to.
	 * @return The absolute path name of the native library or null if
	 * the library does not exist.
	 */
	public String findLibrary(String libname) {
		String mappedName = System.mapLibraryName(libname);
		String path = null;

		if (Debug.DEBUG && Debug.DEBUG_LOADER) {
			Debug.println("  mapped library name: " + mappedName); //$NON-NLS-1$
		}

		path = findNativePath(mappedName);

		if (path == null) {
			if (Debug.DEBUG && Debug.DEBUG_LOADER) {
				Debug.println("  library does not exist: " + mappedName); //$NON-NLS-1$
			}
			path = findNativePath(libname);
		}

		if (Debug.DEBUG && Debug.DEBUG_LOADER) {
			Debug.println("  returning library: " + path); //$NON-NLS-1$
		}
		return path;
	}

	protected String findNativePath(String libname) {
		String path = null;
		if (!libname.startsWith("/")) { //$NON-NLS-1$
			libname = '/' + libname;
		}
		String[] nativepaths = getNativePaths();
		if (nativepaths != null) {
			for (int i = 0; i < nativepaths.length; i++) {
				if (nativepaths[i].endsWith(libname)) {
					File nativeFile = baseBundleFile.getFile(nativepaths[i]);
					path = nativeFile.getAbsolutePath();
				}
			}
		}
		return path;
	}

	/**
	 * Installs the native code paths for this BundleData.  Each
	 * element of nativepaths must be installed for lookup when findLibrary 
	 * is called.
	 * @param nativepaths The array of native code paths to install for
	 * the bundle.
	 * @throws BundleException If any error occurs during install.
	 */
	public void installNativeCode(String[] nativepaths) throws BundleException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nativepaths.length; i++) {
			// extract the native code
			File nativeFile = baseBundleFile.getFile(nativepaths[i]);
			if (nativeFile == null) {
				throw new BundleException(AdaptorMsg.formatter.getString("BUNDLE_NATIVECODE_EXCEPTION", nativepaths[i])); //$NON-NLS-1$
			}
			sb.append(nativepaths[i]);
			if (i < nativepaths.length - 1) {
				sb.append(","); //$NON-NLS-1$
			}
		}
		if (sb.length() > 0)
			setNativePaths(sb.toString());
	}

	protected void setDataDir(File dirData) {
		this.dirData = dirData;
	}

	protected File getDataDir() {
		return dirData;
	}

	/**
	 * Return the bundle data directory.
	 * Attempt to create the directory if it does not exist.
	 *
	 * @return Bundle data directory.
	 */
	public File getDataFile(String path) {
		// lazily initialize dirData to prevent early access to instance location
		if (getDataDir() == null) {
			File dataRoot = ((DefaultAdaptor) adaptor).getDataRootDir();
			if (dataRoot == null)
				throw new IllegalStateException(AdaptorMsg.formatter.getString("ADAPTOR_DATA_AREA_NOT_SET")); //$NON-NLS-1$
			setDataDir(new File(dataRoot, id + "/" + DefaultAdaptor.DATA_DIR_NAME)); //$NON-NLS-1$
		}
		if (!getDataDir().exists() && !getDataDir().mkdirs()) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Unable to create bundle data directory: " + getDataDir().getPath()); //$NON-NLS-1$
			}
		}

		return (new File(getDataDir(), path));
	}

	/**
	 * Close all resources for this BundleData
	 */
	public void close() throws IOException {
		if (baseBundleFile != null) {
			baseBundleFile.close();
		}
	}

	/**
	 * Opens all resource for this BundleData.  Reopens the BundleData if
	 * it was previosly closed.
	 */
	public void open() throws IOException {
		baseBundleFile.open();
	}

	protected void loadFromManifest() throws IOException, BundleException {
		getManifest();

		if (manifest == null) {
			throw new IOException(AdaptorMsg.formatter.getString("ADAPTOR_ERROR_GETTING_MANIFEST", getLocation())); //$NON-NLS-1$
		}
		setVersion(new Version((String) manifest.get(Constants.BUNDLE_VERSION)));
		setSymbolicName(AbstractBundleData.parseSymbolicName(manifest));
		setClassPath((String) manifest.get(Constants.BUNDLE_CLASSPATH));
		setActivator((String) manifest.get(Constants.BUNDLE_ACTIVATOR));
		String host = (String) manifest.get(Constants.FRAGMENT_HOST);
		setFragment(host != null);
		setExecutionEnvironment((String) manifest.get(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT));
		setDynamicImports((String) manifest.get(Constants.DYNAMICIMPORT_PACKAGE));
	}

	protected File getGenerationDir() {
		return new File(getBundleStoreDir(), String.valueOf(getGeneration()));
	}

	/**
	 * Return the bundle generation directory.
	 * Attempt to create the directory if it does not exist.
	 *
	 * @return Bundle generation directory.
	 */
	public File createGenerationDir() {
		File generationDir = getGenerationDir();
		if (!generationDir.exists() && !generationDir.mkdirs()) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Unable to create bundle generation directory: " + generationDir.getPath()); //$NON-NLS-1$
			}
		}

		return generationDir;
	}

	/**
	 * Return the base File for the bundle.
	 * Attempt to create the bundle generation directory if it does not exist.
	 *
	 * @return the base File object for the bundle.
	 */
	protected File getBaseFile() {
		return isReference() ? new File(getFileName()) : new File(createGenerationDir(), getFileName());
	}

	protected File getBundleStoreDir() {
		return bundleStoreDir;
	}

	protected void setBundleStoreDir(File bundleStoreDir) {
		this.bundleStoreDir = bundleStoreDir;
	}

	/**
	 * Return the top level bundle directory.
	 *
	 * @return Top level bundle directory.
	 */
	protected File createBundleStoreDir() {
		if (!getBundleStoreDir().exists() && !getBundleStoreDir().mkdirs()) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Unable to create bundle store directory: " + getBundleStoreDir().getPath()); //$NON-NLS-1$
			}
		}
		return getBundleStoreDir();
	}

	/**
	 * Save the bundle data in the data file.
	 *
	 * @throws IOException if a write error occurs.
	 */
	public synchronized void save() throws IOException {
		((DefaultAdaptor) adaptor).saveMetaDataFor(this);
	}

	/**
	 * Return a copy of this object with the
	 * generation dependent fields updated to
	 * the next free generation level.
	 *
	 * @throws IOException If there are no more available generation levels.
	 */
	protected DefaultBundleData nextGeneration(String referenceFile) throws IOException {
		int nextGeneration = getGeneration();

		while (nextGeneration < Integer.MAX_VALUE) {
			nextGeneration++;

			File nextDirGeneration = new File(getBundleStoreDir(), String.valueOf(nextGeneration));

			if (nextDirGeneration.exists()) {
				continue;
			}

			DefaultBundleData next;
			try {
				next = (DefaultBundleData) clone();
			} catch (CloneNotSupportedException e) {
				// this shouldn't happen, since we are Cloneable
				throw new InternalError();
			}

			next.setGeneration(nextGeneration);

			if (referenceFile != null) {
				next.setReference(true);
				next.setFileName(referenceFile);
			} else {
				if (next.isReference()) {
					next.setReference(false);
					next.setFileName(((DefaultAdaptor) adaptor).mapLocationToName(getLocation()));
				}
			}

			// null out the manifest to force it to be re-read.
			next.manifest = null;
			return (next);
		}

		throw new IOException(AdaptorMsg.formatter.getString("ADAPTOR_STORAGE_EXCEPTION")); //$NON-NLS-1$
	}

	protected BundleFile createBaseBundleFile() throws IOException {
		baseBundleFile = getAdaptor().createBundleFile(getBaseFile(), this);
		return baseBundleFile;
	}

	public BundleFile getBaseBundleFile() {
		return baseBundleFile;
	}

	public String toString() {
		return getLocation();
	}

}