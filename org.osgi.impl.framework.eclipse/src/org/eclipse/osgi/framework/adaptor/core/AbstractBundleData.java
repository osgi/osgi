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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.eclipse.osgi.framework.adaptor.*;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.internal.core.Constants;
import org.eclipse.osgi.framework.internal.protocol.bundleentry.Handler;
import org.eclipse.osgi.framework.util.Headers;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.*;

/**
 * An abstract BundleData class that has default implementations that most
 * BundleData implementations can use.
 * <p>
 * Clients may extend this class.
 * </p>
 * @since 3.1
 */
public abstract class AbstractBundleData implements BundleData, Cloneable {

	/** the Adaptor for this BundleData */
	protected AbstractFrameworkAdaptor adaptor;

	/**
	 * The Bundle Manifest for this BundleData.
	 */
	protected Dictionary manifest = null;

	/**
	 * The Bundle object for this BundleData.
	 */
	protected Bundle bundle;

	/** bundle id */
	protected long id;

	/** The top level storage directory for the BundleData */
	protected File bundleStoreDir;

	/** The base BundleFile object for this BundleData */
	protected BundleFile baseBundleFile;
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

	/**
	 * The BundleData data directory 
	 */
	protected File dirData;

	/** the bundles status */
	private int status = 0;

	/** Is bundle a reference */
	private boolean reference;

	/** the bundles last modified timestamp */
	private long lastModified;

	///////////////////// End Meta Data for the Bundle   /////////////////////

	///////////////////// Begin values from Manifest     /////////////////////
	private String symbolicName;
	private Version version;
	private String activator;
	private String classpath;
	private String executionEnvironment;
	private String dynamicImports;
	private int type;

	///////////////////// End values from Manifest       /////////////////////

	/**
	 * Constructor for AbstractBundleData
	 * @param adaptor The adaptor for this bundle data
	 * @param id The bundle id for this bundle data
	 */
	public AbstractBundleData(AbstractFrameworkAdaptor adaptor, long id) {
		this.adaptor = adaptor;
		this.id = id;
		initBundleStoreDirs(String.valueOf(id));
	}

	/**
	 * @see BundleData#getManifest()
	 */
	public Dictionary getManifest() throws BundleException {
		if (manifest == null) {
			synchronized (this) {
				// make sure the manifest is still null after we have aquired the lock.
				if (manifest == null) {
					URL url = getEntry(Constants.OSGI_BUNDLE_MANIFEST);
					if (url == null) {
						throw new BundleException(NLS.bind(AdaptorMsg.MANIFEST_NOT_FOUND_EXCEPTION, Constants.OSGI_BUNDLE_MANIFEST, getLocation()));
					}
					try {
						manifest = Headers.parseManifest(url.openStream());
					} catch (IOException e) {
						throw new BundleException(NLS.bind(AdaptorMsg.MANIFEST_NOT_FOUND_EXCEPTION, Constants.OSGI_BUNDLE_MANIFEST, getLocation()), e);
					}
				}
			}
		}
		return manifest;
	}

	/**
	 * @see BundleData#setBundle(Bundle)
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
	 * @see BundleData#getBundleID()
	 */
	public long getBundleID() {
		return (id);
	}

	/**
	 * @see BundleData#getEntry(String)
	 */
	public URL getEntry(String path) {
		BundleEntry entry = getBaseBundleFile().getEntry(path);
		if (entry == null) {
			return null;
		}
		if (path.length() == 0 || path.charAt(0) != '/')
			path = path = '/' + path;
		try {
			//use the constant string for the protocol to prevent duplication
			return new URL(Constants.OSGI_ENTRY_URL_PROTOCOL, Long.toString(id), 0, path, new Handler(entry));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * @see BundleData#getEntryPaths(String)
	 */
	public Enumeration getEntryPaths(String path) {
		return getBaseBundleFile().getEntryPaths(path);
	}

	/**
	 * @see BundleData#createClassLoader(ClassLoaderDelegate, BundleProtectionDomain, String[])
	 */
	public org.eclipse.osgi.framework.adaptor.BundleClassLoader createClassLoader(ClassLoaderDelegate delegate, BundleProtectionDomain domain, String[] bundleclasspath) {
		return getAdaptor().getElementFactory().createClassLoader(delegate, domain, bundleclasspath, this);
	}

	/**
	 * Returns the adaptor for this bundle data.
	 * @return the adaptor for this bundle data.
	 */
	public AbstractFrameworkAdaptor getAdaptor() {
		return adaptor;
	}

	/**
	 * Returns a list of classpath entries from a list of manifest elements
	 * @param classpath a list of ManifestElement objects
	 * @return a list of classpath entries from a list of manifest elements
	 */
	static String[] getClassPath(ManifestElement[] classpath) {
		if (classpath == null) {
			if (Debug.DEBUG && Debug.DEBUG_LOADER)
				Debug.println("  no classpath"); //$NON-NLS-1$
			/* create default BundleClassPath */
			return new String[] {"."}; //$NON-NLS-1$
		}

		ArrayList result = new ArrayList(classpath.length);
		for (int i = 0; i < classpath.length; i++) {
			if (Debug.DEBUG && Debug.DEBUG_LOADER)
				Debug.println("  found classpath entry " + classpath[i].getValueComponents()); //$NON-NLS-1$
			String[] paths = classpath[i].getValueComponents();
			for (int j = 0; j < paths.length; j++) {
				result.add(paths[j]);
			}
		}

		return (String[]) result.toArray(new String[result.size()]);
	}

	///////////////////// Begin Meta Data Accessor Methods ////////////////////
	/**
	 * @see BundleData#getLocation()
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * Sets the location for this bundle data
	 * @param location the location string
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Returns the filename for the base file of this bundle data
	 * @return the filename for the base file of this bundle data
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the filename for the base file of this bundle data
	 * @param fileName the name of the base file of this bundle data
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Returns the list of native file paths to install for this bundle
	 * @return the list of native file paths to install for this bundle
	 */
	public String[] getNativePaths() {
		return nativePaths;
	}

	/**
	 * Returns a comma separated list of native file paths to install for this bundle
	 * @return a comma separated list of native file paths to install for this bundle
	 */
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

	/**
	 * Sets the list of native file paths to install for this bundle
	 * @param nativePaths the list of native file paths to install for this bundle
	 */
	public void setNativePaths(String[] nativePaths) {
		this.nativePaths = nativePaths;
	}

	/**
	 * Sets the comma separated list of native file paths to install for this bundle
	 * @param nativePaths the comma separated list of native file paths to install for this bundle
	 */
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

	/**
	 * Returns the generation number for this bundle
	 * @return the generation number for this bundle
	 */
	public int getGeneration() {
		return generation;
	}

	/**
	 * Sets the generation number for this bundle
	 * @param generation the generation number for this bundle
	 */
	public void setGeneration(int generation) {
		this.generation = generation;
	}

	/**
	 * @see BundleData#getLastModified()
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * Sets the last modified timestamp for this bundle
	 * @param lastModified the last modified timestamp for this bundle
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @see BundleData#getStartLevel()
	 */
	public int getStartLevel() {
		return startLevel;
	}

	/**
	 * @see BundleData#setStartLevel(int)
	 */
	public void setStartLevel(int startLevel) {
		this.startLevel = startLevel;
	}

	/**
	 * @see BundleData#getStatus()
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @see BundleData#setStatus(int)
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Returns if this bundle is installed by reference
	 * @return true if this bundle is installed by reference
	 */
	public boolean isReference() {
		return reference;
	}

	/**
	 * Sets if this bundle is installed by reference
	 * @param reference indicates if this bundle is installed by reference
	 */
	public void setReference(boolean reference) {
		this.reference = reference;
	}

	///////////////////// End Meta Data Accessor Methods   ////////////////////

	///////////////////// Begin Manifest Value Accessor Methods /////////////////////

	/**
	 * @see BundleData#getSymbolicName()
	 */
	public String getSymbolicName() {
		return symbolicName;
	}

	/**
	 * Returns the base storage directory for this bundle
	 * @return the base storage directory for this bundle
	 */
	public File getBundleStoreDir() {
		return bundleStoreDir;
	}

	/**
	 * Sets the symbolic name of this bundle
	 * @param symbolicName the symbolic name of this bundle
	 */
	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	/**
	 * Loads all metadata for this bundle from the bundle manifest
	 * @throws BundleException
	 */
	protected void loadFromManifest() throws BundleException {
		getManifest();
		if (manifest == null)
			throw new BundleException(NLS.bind(AdaptorMsg.ADAPTOR_ERROR_GETTING_MANIFEST, getLocation())); //$NON-NLS-1$
		try {
			setVersion(Version.parseVersion((String) manifest.get(Constants.BUNDLE_VERSION)));
		} catch (IllegalArgumentException e) {
			setVersion(new InvalidVersion((String) manifest.get(Constants.BUNDLE_VERSION)));
		}
		ManifestElement[] bsnHeader = ManifestElement.parseHeader(Constants.BUNDLE_SYMBOLICNAME, (String) manifest.get(Constants.BUNDLE_SYMBOLICNAME));
		int bundleType = 0;
		if (bsnHeader != null) {
			setSymbolicName(bsnHeader[0].getValue());
			String singleton = bsnHeader[0].getDirective(Constants.SINGLETON_DIRECTIVE);
			if (singleton == null)
				singleton = bsnHeader[0].getAttribute(Constants.SINGLETON_DIRECTIVE);
			if ("true".equals(singleton)) //$NON-NLS-1$
					bundleType |= TYPE_SINGLETON;
		}
		setClassPathString((String) manifest.get(Constants.BUNDLE_CLASSPATH));
		setActivator((String) manifest.get(Constants.BUNDLE_ACTIVATOR));
		String host = (String) manifest.get(Constants.FRAGMENT_HOST);
		if (host != null) {
			bundleType |= TYPE_FRAGMENT;
			ManifestElement[] hostElement = ManifestElement.parseHeader(Constants.FRAGMENT_HOST, host);
			if (Constants.getInternalSymbolicName().equals(hostElement[0].getValue()) || Constants.OSGI_SYSTEM_BUNDLE.equals(hostElement[0].getValue())) {
				String extensionType = hostElement[0].getDirective("extension"); //$NON-NLS-1$
				if (extensionType == null || extensionType.equals("framework")) //$NON-NLS-1$
					bundleType |= TYPE_FRAMEWORK_EXTENSION;
				else
					bundleType |= TYPE_BOOTCLASSPATH_EXTENSION;
			}
		}
		setType(bundleType);
		setExecutionEnvironment((String) manifest.get(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT));
		setDynamicImports((String) manifest.get(Constants.DYNAMICIMPORT_PACKAGE));
	}

	/**
	 * @see BundleData#getVersion()
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Sets the version of this bundle
	 * @param version the version of this bundle
	 */
	public void setVersion(Version version) {
		this.version = version;
	}

	/**
	 * @see BundleData#getActivator()
	 */
	public String getActivator() {
		return activator;
	}

	/**
	 * Returns the data storage directory for this bundle
	 * @return the data storage directory for this bundle
	 */
	protected File getDataDir() {
		return dirData;
	}

	/**
	 * Sets the bundle store directory for this bundle
	 * @param bundleStoreDir the store directory for this bundle
	 */
	protected void setBundleStoreDir(File bundleStoreDir) {
		this.bundleStoreDir = bundleStoreDir;
	}

	/**
	 * Sets the initial bundle store directory according to the bundle ID
	 * @param bundleID the bundle ID
	 */
	protected void initBundleStoreDirs(String bundleID) {
		setBundleStoreDir(new File(((AbstractFrameworkAdaptor) adaptor).getBundleStoreRootDir(), bundleID));
	}

	/**
	 * Sets the activator for this bundle
	 * @param activator the activator for this bundle
	 */
	public void setActivator(String activator) {
		this.activator = activator;
	}

	/**
	 * @see BundleData#getClassPath()
	 */
	public String[] getClassPath() throws BundleException {
		ManifestElement[] classpathElements = ManifestElement.parseHeader(Constants.BUNDLE_CLASSPATH, classpath);
		return getClassPath(classpathElements);
	}

	/**
	 * Returns the Bundle-ClassPath value as specified in the bundle manifest file.
	 * @return the Bundle-ClassPath value as specified in the bundle manifest file.
	 */
	public String getClassPathString() {
		return classpath;
	}

	/**
	 * Sets the bundle classpath value of this bundle data.
	 * @param classpath the bundle classpath
	 */
	public void setClassPathString(String classpath) {
		this.classpath = classpath;
	}

	/**
	 * @see BundleData#getExecutionEnvironment()
	 */
	public String getExecutionEnvironment() {
		return executionEnvironment;
	}

	/**
	 * Sets the execution environment for this bundle
	 * @param executionEnvironment the execution environment for this bundle
	 */
	public void setExecutionEnvironment(String executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}

	/**
	 * @see BundleData#getDynamicImports()
	 */
	public String getDynamicImports() {
		return dynamicImports;
	}

	/**
	 * Sets the dynamic imports of this bundle data.
	 * @param dynamicImports the dynamic imports
	 */
	public void setDynamicImports(String dynamicImports) {
		this.dynamicImports = dynamicImports;
	}

	/**
	 * @see BundleData#getType()
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the type of this bundle
	 * @param type the type of this bundle
	 */
	public void setType(int type) {
		this.type = type;
	}

	///////////////////// End Manifest Value Accessor Methods  /////////////////////

	/**
	 * @see BundleData#matchDNChain(String)
	 */
	public boolean matchDNChain(String pattern) {
		if (System.getSecurityManager() == null)
			return false;

		if (getBaseBundleFile() instanceof SignedBundle)
			return ((SignedBundle) getBaseBundleFile()).matchDNChain(pattern);
		return false;
	}

	/**
	 * Return a copy of this object with the
	 * generation dependent fields updated to
	 * the next free generation level.
	 *
	 * @throws IOException If there are no more available generation levels.
	 */
	protected AbstractBundleData nextGeneration(String referenceFile) throws IOException {
		int nextGeneration = getGeneration();

		while (nextGeneration < Integer.MAX_VALUE) {
			nextGeneration++;

			File nextDirGeneration = new File(getBundleStoreDir(), String.valueOf(nextGeneration));

			if (nextDirGeneration.exists()) {
				continue;
			}

			AbstractBundleData next;
			try {
				next = (AbstractBundleData) clone();
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
					next.setFileName(AbstractFrameworkAdaptor.BUNDLEFILE_NAME);
				}
			}

			// null out the manifest to force it to be re-read.
			next.manifest = null;
			return (next);
		}

		throw new IOException(AdaptorMsg.ADAPTOR_STORAGE_EXCEPTION);
	}

	/**
	 * Initializes a new bundle and loads all its metadata from the bundle manifest
	 * @throws IOException
	 * @throws BundleException
	 */
	public void initializeNewBundle() throws IOException, BundleException {
		createBaseBundleFile();
		loadFromManifest();
	}

	/**
	 * Creates the base BundleFile for this bundle
	 * @return the base BundleFile for this bundle
	 * @throws IOException if an IOExceptions occurs
	 */
	protected BundleFile createBaseBundleFile() throws IOException {
		baseBundleFile = getAdaptor().createBaseBundleFile(getBaseFile(), this);
		return baseBundleFile;
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

	/**
	 * Returns a list of files used for the classpath of this bundle data.
	 * the contents of the bundle are searched for the classpath entries.
	 * @param classpaths the classpath entries to search for
	 * @return a list of files used for the classpath of this bundle data.
	 */
	protected File[] getClasspathFiles(String[] classpaths) {
		ArrayList results = new ArrayList(classpaths.length);
		for (int i = 0; i < classpaths.length; i++) {
			if (".".equals(classpaths[i])) //$NON-NLS-1$
				results.add(getBaseFile());
			else {
				File result = getBaseBundleFile().getFile(classpaths[i]);
				if (result != null)
					results.add(result);
			}
		}
		return (File[]) results.toArray(new File[results.size()]);
	}

	/**
	 * Sets the data directory for this bundle
	 * @param dirData the data directory for this bundle
	 */
	protected void setDataDir(File dirData) {
		this.dirData = dirData;
	}

	/**
	 * @see BundleData#findLibrary(String)
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

	/**
	 * @see BundleData#open()
	 */
	public void open() throws IOException {
		baseBundleFile.open();
	}

	/**
	 * Searches the native paths for a match against the specified libname.
	 * If a match is found then the native path is returned; otherwise a
	 * <code>null</code> value is returned.
	 * @param libname a library name
	 * @return a matching native path or <code>null</code>. 
	 */
	protected String findNativePath(String libname) {
		int slash = libname.lastIndexOf('/');
		if (slash >= 0)
			libname = libname.substring(slash + 1);
		String[] nativepaths = getNativePaths();
		if (nativepaths != null) {
			for (int i = 0; i < nativepaths.length; i++) {
				slash = nativepaths[i].lastIndexOf('/');
				String path = slash < 0 ? nativePaths[i] : nativePaths[i].substring(slash + 1);
				if (path.equals(libname)) {

					File nativeFile = baseBundleFile.getFile(nativepaths[i]);
					if (nativeFile != null)
						return nativeFile.getAbsolutePath();
				}
			}
		}
		return null;
	}

	/**
	 * Return the generation directory for the bundle data.  The generation
	 * directory can be used by the framework to cache files from the bundle
	 * to the file system. Attempt to create the directory if it does not exist.
	 * @return The generation directory for the bundle data or null if not
	 * supported.
	 */
	public File createGenerationDir() {
		File generationDir = getGenerationDir();
		if (!generationDir.exists() && (!adaptor.canWrite() || !generationDir.mkdirs())) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Unable to create bundle generation directory: " + generationDir.getPath()); //$NON-NLS-1$
			}
		}

		return generationDir;
	}

	/**
	 * Return the base BundleFile for this BundleData.  The base BundleFile
	 * is the BundleFile that contains all the content of the bundle.
	 * @return the base BundleFile.
	 */
	public BundleFile getBaseBundleFile() {
		return baseBundleFile;
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
	 * Return the bundle data directory.
	 * Attempt to create the directory if it does not exist.
	 *
	 * @return Bundle data directory.
	 */
	public File getDataFile(String path) {
		// lazily initialize dirData to prevent early access to configuration location
		if (getDataDir() == null) {
			File dataRoot = adaptor.getDataRootDir();
			if (dataRoot == null)
				throw new IllegalStateException(AdaptorMsg.ADAPTOR_DATA_AREA_NOT_SET);
			setDataDir(new File(dataRoot, id + "/" + AbstractFrameworkAdaptor.DATA_DIR_NAME)); //$NON-NLS-1$
		}
		if (!getDataDir().exists() && (!adaptor.canWrite() || !getDataDir().mkdirs())) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Unable to create bundle data directory: " + getDataDir().getPath()); //$NON-NLS-1$
			}
		}

		return (new File(getDataDir(), path));
	}

	/**
	 * @see BundleData#installNativeCode(String[])
	 */
	public void installNativeCode(String[] nativepaths) throws BundleException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nativepaths.length; i++) {
			// extract the native code
			File nativeFile = baseBundleFile.getFile(nativepaths[i]);
			if (nativeFile == null) {
				throw new BundleException(NLS.bind(AdaptorMsg.BUNDLE_NATIVECODE_EXCEPTION, nativepaths[i]));
			}
			sb.append(nativepaths[i]);
			if (i < nativepaths.length - 1) {
				sb.append(","); //$NON-NLS-1$
			}
		}
		if (sb.length() > 0)
			setNativePaths(sb.toString());
	}

	/**
	 * Returns the generation directory for the bundle data.  The returned
	 * file may not exist.
	 * @return the generation directory for the bundle data.
	 */
	protected File getGenerationDir() {
		return new File(getBundleStoreDir(), String.valueOf(getGeneration()));
	}

	/**
	 * Returns the parent generation directory for the bundle data.  The returned
	 * file may not exist.  A value of <code>null</code> is returned if there is
	 * no parent generation directory.
	 * @return the parent gneration directory for the bundle data.
	 */
	public File getParentGenerationDir() {
		return null;
	}
}
