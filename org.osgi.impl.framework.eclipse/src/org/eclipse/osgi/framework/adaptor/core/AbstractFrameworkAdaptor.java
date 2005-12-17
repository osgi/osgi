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

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import org.eclipse.osgi.framework.adaptor.*;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.internal.core.*;
import org.eclipse.osgi.framework.internal.core.Constants;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.framework.util.Headers;
import org.eclipse.osgi.service.resolver.*;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.*;

/**
 * An abstract FrameworkAdaptor class that has default implementations that most
 * FrameworkAdaptor implementations can use.
 * <p>
 * Clients may extend this class.
 * </p>
 * @since 3.1
 */
public abstract class AbstractFrameworkAdaptor implements FrameworkAdaptor {
	/**
	 * System property used to set the parent classloader type (boot is the default)
	 */
	public static final String PROP_PARENT_CLASSLOADER = "osgi.parentClassloader"; //$NON-NLS-1$
	/**
	 * System property used to specify the list of framework extension bundles
	 */
	public static final String PROP_FRAMEWORK_EXTENSIONS = "osgi.framework.extensions"; //$NON-NLS-1$
	/**
	 * System property used to specify the bundle singing support class name
	 */
	public static final String PROP_SIGNINGSUPPORT = "osgi.bundlesigning.support"; //$NON-NLS-1$
	/**
	 * A parent classloader type that specifies the application classloader
	 */
	public static final String PARENT_CLASSLOADER_APP = "app"; //$NON-NLS-1$
	/**
	 * A parent classloader type that specifies the extension classlaoder
	 */
	public static final String PARENT_CLASSLOADER_EXT = "ext"; //$NON-NLS-1$
	/**
	 * A parent classloader type that specifies the boot classlaoder
	 */
	public static final String PARENT_CLASSLOADER_BOOT = "boot"; //$NON-NLS-1$
	/**
	 * A parent classloader type that specifies the framework classlaoder
	 */
	public static final String PARENT_CLASSLOADER_FWK = "fwk"; //$NON-NLS-1$
	/**
	 * The bundle file name used to store bundles into the bundle storage area
	 */
	public static final String BUNDLEFILE_NAME = "bundlefile"; //$NON-NLS-1$

	/**
	 * flag to indicate a framework extension is being intialized
	 */
	public static final byte EXTENSION_INITIALIZE = 0x01;
	/**
	 * flag to indicate a framework extension is being installed
	 */
	public static final byte EXTENSION_INSTALLED = 0x02;
	/**
	 * flag to indicate a framework extension is being uninstalled
	 */
	public static final byte EXTENSION_UNINSTALLED = 0x04;
	/**
	 * flag to indicate a framework extension is being updated
	 */
	public static final byte EXTENSION_UPDATED = 0x08;

	/** Name of the Adaptor manifest file */
	protected final String ADAPTOR_MANIFEST = "ADAPTOR.MF"; //$NON-NLS-1$

	/**
	 * The default bundle signing support class name
	 */
	protected final String DEFAULT_SIGNEDBUNDLE_SUPPORT = "org.eclipse.osgi.framework.pkcs7verify.SignedBundleSupportImpl"; //$NON-NLS-1$

	/**
	 * The EventPublisher for the FrameworkAdaptor
	 */
	protected EventPublisher eventPublisher;

	/**
	 * The ServiceRegistry object for this FrameworkAdaptor.
	 */
	protected ServiceRegistry serviceRegistry;

	/**
	 * The Properties object for this FrameworkAdaptor
	 */
	protected Properties properties;

	/**
	 * The System Bundle's BundleContext.
	 */
	protected BundleContext context;

	/**
	 * The initial bundle start level.
	 */
	protected int initialBundleStartLevel = 1;

	/** This adaptor's manifest file */
	protected Headers manifest = null;

	/**	
	 * Indicates the Framework is stopoing; 
	 * set to true when frameworkStopping(BundleContext) is called 
	 */
	protected boolean stopping = false;

	/**
	 * The BundleClassLoader parent to use when creating BundleClassLoaders.
	 * The behavior of the ParentClassLoader will load classes
	 * from the boot strap classloader.
	 */
	protected static ClassLoader bundleClassLoaderParent;
	/**
	 * next available bundle id 
	 */
	protected long nextId = 1;
	/**
	 * The State Manager 
	 */
	protected StateManager stateManager;
	/**
	 * directory containing installed bundles 
	 */
	protected File bundleStoreRootDir;

	/**
	 * The adaptor element factory
	 */
	protected AdaptorElementFactory elementFactory;

	static {
		// check property for specified parent
		String type = System.getProperty(PROP_PARENT_CLASSLOADER, PARENT_CLASSLOADER_BOOT);
		if (PARENT_CLASSLOADER_FWK.equalsIgnoreCase(type))
			bundleClassLoaderParent = FrameworkAdaptor.class.getClassLoader();
		else if (PARENT_CLASSLOADER_APP.equalsIgnoreCase(type))
			bundleClassLoaderParent = ClassLoader.getSystemClassLoader();
		else if (PARENT_CLASSLOADER_EXT.equalsIgnoreCase(type)) {
			ClassLoader appCL = ClassLoader.getSystemClassLoader();
			if (appCL != null)
				bundleClassLoaderParent = appCL.getParent();
		}

		// default to boot classloader
		if (bundleClassLoaderParent == null)
			bundleClassLoaderParent = new ParentClassLoader();
	}

	/**
	 * The add URL method used to support framework extensions
	 */
	protected Method addURLMethod;

	/**
	 * The list of configured framework extensions
	 */
	protected String[] configuredExtensions;

	/**
	 * Indicates if signed bundles are supported
	 */
	protected boolean supportSignedBundles = true;
	/**
	 * The SingedBundleSupport object.  This is set if signed bundles are supported
	 */
	protected SignedBundleSupport signedBundleSupport = null;

	/**
	 * Constructor for DefaultAdaptor.  This constructor parses the arguments passed
	 * and remembers them for later when initialize is called.
	 * <p>No blank spaces should be used in the arguments to the DefaultAdaptor.
	 * The options that DefaultAdaptor recognizes and handles are:
	 * <ul>
	 * <li><b>bundledir=<i>directory name</i></b>If a directory name is specified, the adaptor should initialize
	 * to store bundles in that directory.  This arg should be enclosed in "" if it contains the ":"
	 * character (example "bundledir=c:\mydir").
	 * <li><b>reset</b>Resets the bundle storage by deleting the bundledir
	 * </ul>
	 * Any other arguments are ignored.
	 *
	 * @param args An array of strings containing arguments.
	 * This object cannot be used until initialize is called.
	 *
	 */
	public AbstractFrameworkAdaptor(String[] args) {
		ClassLoader fwloader = AbstractFrameworkAdaptor.class.getClassLoader();
		if (fwloader != null)
			addURLMethod = findaddURLMethod(fwloader.getClass());
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				if (arg.equalsIgnoreCase("reset")) { //$NON-NLS-1$
					reset = true;
				} else if (arg.indexOf("=") != -1) { //$NON-NLS-1$
					StringTokenizer tok = new StringTokenizer(args[i], "="); //$NON-NLS-1$
					if (tok.countTokens() == 2) {
						String key = tok.nextToken();
						if (key.equalsIgnoreCase("bundledir")) { //$NON-NLS-1$
							// save file name for initializeStorage to use
							bundleStore = tok.nextToken();
						}
					}
				}
			}
		}
	}

	/**
	 * @see FrameworkAdaptor#initialize(EventPublisher)
	 */
	public void initialize(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		serviceRegistry = new ServiceRegistryImpl();
		((ServiceRegistryImpl) serviceRegistry).initialize();
		loadProperties();
		readAdaptorManifest();
		initBundleStoreRootDir();
		// need to create the FrameworkLog very early
		frameworkLog = createFrameworkLog();
	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.FrameworkAdaptor#getProperties()
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Returns the AdaptorElementFactory for this Adaptor.  This allows
	 * extending adaptors to control how BundleData and BundleClassLoader
	 * objects are created.
	 * @return the AdaptorElementFactory for this Adapotr.
	 */
	public abstract AdaptorElementFactory getElementFactory();

	/**
	 * Saves the next bundle id to persist storage
	 * @param value the next bundle id
	 * @throws IOException
	 */
	protected abstract void persistNextBundleID(long value) throws IOException;

	/**
	 * @see FrameworkAdaptor#getFrameworkLog()
	 */
	public FrameworkLog getFrameworkLog() {
		if (frameworkLog == null)
			frameworkLog = createFrameworkLog();
		return frameworkLog;
	}

	/**
	 * @see FrameworkAdaptor#getState()
	 */
	public State getState() {
		return stateManager.getSystemState();
	}

	/**
	 * @see FrameworkAdaptor#getPlatformAdmin()
	 */
	public PlatformAdmin getPlatformAdmin() {
		return stateManager;
	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.FrameworkAdaptor#mapLocationToURLConnection(String)
	 */
	public URLConnection mapLocationToURLConnection(String location) throws BundleException {
		try {
			return (new URL(location).openConnection());
		} catch (IOException e) {
			throw new BundleException(NLS.bind(AdaptorMsg.ADAPTOR_URL_CREATE_EXCEPTION, location), e);
		}
	}

	/**
	 * Always returns -1 to indicate that this operation is not supported by this
	 * FrameworkAdaptor.  Extending classes should override this method if
	 * they support this operation.
	 * @see org.eclipse.osgi.framework.adaptor.FrameworkAdaptor#getTotalFreeSpace()
	 */
	public long getTotalFreeSpace() throws IOException {
		return -1;
	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.FrameworkAdaptor#getServiceRegistry()
	 */
	public org.eclipse.osgi.framework.adaptor.ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.FrameworkAdaptor#frameworkStart(org.osgi.framework.BundleContext)
	 */
	public void frameworkStart(BundleContext context) throws BundleException {
		this.stopping = false;
		this.context = context;
		BundleResourceHandler.setContext(context);
		if (frameworkLog == null)
			frameworkLog = createFrameworkLog();
		if (stateManager == null)
			stateManager = createStateManager();
		State state = stateManager.getSystemState();
		checkSystemState(state);
		BundleDescription systemBundle = state.getBundle(0);
		if (systemBundle == null || !systemBundle.isResolved())
			// this would be a bug in the framework
			throw new IllegalStateException();
	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.FrameworkAdaptor#frameworkStop(org.osgi.framework.BundleContext)
	 */
	public void frameworkStop(BundleContext context) throws BundleException {
		shutdownStateManager();
		this.context = null;
		BundleResourceHandler.setContext(null);
		frameworkLog.close();
		frameworkLog = null;
	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.FrameworkAdaptor#frameworkStopping(BundleContext)
	 */
	public void frameworkStopping(BundleContext context) {
		this.stopping = true;
	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.FrameworkAdaptor#getExportPackages()
	 */
	public String getExportPackages() {
		if (manifest == null)
			return null;
		return (String) manifest.get(Constants.EXPORT_PACKAGE);
	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.FrameworkAdaptor#getExportServices()
	 */
	public String getExportServices() {
		if (manifest == null)
			return null;
		return (String) manifest.get(Constants.EXPORT_SERVICE);
	}

	/**
	 * @see FrameworkAdaptor#getProvidePackages()
	 */
	public String getProvidePackages() {
		if (manifest == null)
			return null;
		return (String) manifest.get(Constants.PROVIDE_PACKAGE);
	}

	/**
	 * Returns the EventPublisher for this FrameworkAdaptor.
	 * @return The EventPublisher.
	 */
	public EventPublisher getEventPublisher() {
		return eventPublisher;
	}

	/**
	 * Indicates if the framework is currently stopping
	 * @return true if the framework is currently stopping
	 */
	public boolean isStopping() {
		return stopping;
	}

	/**
	 * @see FrameworkAdaptor#getInitialBundleStartLevel()
	 */
	public int getInitialBundleStartLevel() {
		return initialBundleStartLevel;
	}

	/**
	 * @see FrameworkAdaptor#setInitialBundleStartLevel(int)
	 */
	public void setInitialBundleStartLevel(int value) {
		initialBundleStartLevel = value;
	}

	/**
	 * @see FrameworkAdaptor#getBundleWatcher()
	 */
	public BundleWatcher getBundleWatcher() {
		return null;
	}

	/**
	 * The FrameworkLog for the adaptor 
	 */
	protected FrameworkLog frameworkLog;

	public static final String BUNDLE_STORE = "osgi.bundlestore"; //$NON-NLS-1$

	/**
	 * This method locates and reads the osgi.properties file.
	 * If the system property <i>org.eclipse.osgi.framework.internal.core.properties</i> is specifed, its value
	 * will be used as the name of the file instead of
	 * <tt>osgi.properties</tt>.   There are 3 places to look for these properties.  These
	 *  3 places are searched in the following order, stopping when the properties are found.
	 *
	 *  <ol>
	 *  <li>Look for a file in the file system
	 *  <li>Look for a resource in the FrameworkAdaptor's package
	 *  </ol>
	 */
	protected void loadProperties() {
		properties = new Properties();

		String resource = System.getProperty(Constants.OSGI_PROPERTIES, Constants.DEFAULT_OSGI_PROPERTIES);

		try {
			InputStream in = null;
			File file = new File(resource);
			if (file.exists()) {
				in = new FileInputStream(file);
			}

			if (in == null) {
				in = getClass().getResourceAsStream(resource);
			}

			if (in != null) {
				try {
					properties.load(new BufferedInputStream(in));
				} finally {
					try {
						in.close();
					} catch (IOException ee) {
					}
				}
			} else {
				if (Debug.DEBUG && Debug.DEBUG_GENERAL)
					Debug.println("Skipping osgi.properties: " + resource); //$NON-NLS-1$
			}
		} catch (IOException e) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Unable to load osgi.properties: " + e.getMessage()); //$NON-NLS-1$
			}
		}

		// need to set some OSGi Framework properties that only the adaptor would know about
		if (addURLMethod != null)
			properties.put(Constants.SUPPORTS_FRAMEWORK_EXTENSION, "true"); //$NON-NLS-1$
	}

	/**
	 * Return the next valid, unused bundle id.
	 *
	 * @return Next valid, unused bundle id.
	 * @throws IOException If there are no more unused bundle ids.
	 */
	protected synchronized long getNextBundleId() throws IOException {
		while (nextId < Long.MAX_VALUE) {
			long id = nextId;
			nextId++;

			File bundleDir = new File(getBundleStoreRootDir(), String.valueOf(id));
			if (bundleDir.exists()) {
				continue;
			}
			persistNextBundleID(id);
			return (id);
		}

		throw new IOException(AdaptorMsg.ADAPTOR_STORAGE_EXCEPTION);
	}

	/**
	 * Initializes the root bundle data directory
	 *
	 */
	protected void initDataRootDir() {
		dataRootDir = getBundleStoreRootDir();
	}

	/**
	 * Reads and initializes the adaptor BundleManifest object.  The
	 * BundleManifest is used by the getExportPackages() and getExportServices()
	 * methods of the adpator.
	 */
	protected void readAdaptorManifest() {
		InputStream in = null;
		// walk up the class hierarchy until we find the ADAPTOR_MANIFEST.
		Class adaptorClazz = getClass();
		while (in == null && AbstractFrameworkAdaptor.class.isAssignableFrom(adaptorClazz)) {
			in = adaptorClazz.getResourceAsStream(ADAPTOR_MANIFEST);
			adaptorClazz = adaptorClazz.getSuperclass();
		}

		if (in == null) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Unable to find adaptor bundle manifest " + ADAPTOR_MANIFEST); //$NON-NLS-1$
			}
			manifest = new Headers(new Properties());
			return;
		}
		try {
			manifest = Headers.parseManifest(in);
		} catch (BundleException e) {
			Debug.println("Unable to read adaptor bundle manifest " + ADAPTOR_MANIFEST); //$NON-NLS-1$
		}
	}

	/**
	 * Creates a framework log
	 * @return a framework log
	 */
	abstract protected FrameworkLog createFrameworkLog();

	/**
	 * @see FrameworkAdaptor#createSystemBundleData()
	 */
	public BundleData createSystemBundleData() throws BundleException {
		return new SystemBundleData(this);
	}

	/**
	 * Does a recursive copy of one directory to another.
	 * @param inDir input directory to copy.
	 * @param outDir output directory to copy to.
	 * @throws IOException if any error occurs during the copy.
	 */
	public static void copyDir(File inDir, File outDir) throws IOException {
		String[] files = inDir.list();
		if (files != null && files.length > 0) {
			outDir.mkdir();
			for (int i = 0; i < files.length; i++) {
				File inFile = new File(inDir, files[i]);
				File outFile = new File(outDir, files[i]);
				if (inFile.isDirectory()) {
					copyDir(inFile, outFile);
				} else {
					InputStream in = new FileInputStream(inFile);
					readFile(in, outFile);
				}
			}
		}
	}

	/**
	 * Read a file from an InputStream and write it to the file system.
	 *
	 * @param in InputStream from which to read.
	 * @param file output file to create.
	 * @exception IOException
	 */
	public static void readFile(InputStream in, File file) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);

			byte buffer[] = new byte[1024];
			int count;
			while ((count = in.read(buffer, 0, buffer.length)) > 0) {
				fos.write(buffer, 0, count);
			}

			fos.close();
			fos = null;

			in.close();
			in = null;
		} catch (IOException e) {
			// close open streams
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ee) {
				}
			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException ee) {
				}
			}

			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Unable to read file"); //$NON-NLS-1$
				Debug.printStackTrace(e);
			}

			throw e;
		}
	}

	private static Method findaddURLMethod(Class clazz) {
		if (clazz == null)
			return null; // ends the recursion when getSuperClass returns null
		try {
			Method result = clazz.getDeclaredMethod("addURL", new Class[] {URL.class}); //$NON-NLS-1$ 
			result.setAccessible(true);
			return result;
		} catch (NoSuchMethodException e) {
			// do nothing look in super class below
		} catch (SecurityException e) {
			// if we do not have the permissions then we will not find the method
		}
		return findaddURLMethod(clazz.getSuperclass());
	}

	/**
	 * @see FrameworkAdaptor#getBundleClassLoaderParent()
	 */
	public ClassLoader getBundleClassLoaderParent() {
		return bundleClassLoaderParent;
	}

	/**
	 * Processes an extension bundle
	 * @param bundleData the extension bundle data 
	 * @param type the type of extension bundle
	 * @throws BundleException on any errors or if the extension bundle type is not supported
	 */
	protected void processExtension(BundleData bundleData, byte type) throws BundleException {
		if ((bundleData.getType() & BundleData.TYPE_FRAMEWORK_EXTENSION) != 0) {
			validateExtension(bundleData);
			processFrameworkExtension(bundleData, type);
		} else if ((bundleData.getType() & BundleData.TYPE_BOOTCLASSPATH_EXTENSION) != 0) {
			validateExtension(bundleData);
			processBootExtension(bundleData, type);
		}
	}

	/**
	 * Validates the extension bundle metadata
	 * @param bundleData the extension bundle data
	 * @throws BundleException if the extension bundle metadata is invalid
	 */
	protected void validateExtension(BundleData bundleData) throws BundleException {
		Dictionary extensionManifest = bundleData.getManifest();
		if (extensionManifest.get(Constants.IMPORT_PACKAGE) != null)
			throw new BundleException(NLS.bind(AdaptorMsg.ADAPTOR_EXTENSION_IMPORT_ERROR, bundleData.getLocation()));
		if (extensionManifest.get(Constants.REQUIRE_BUNDLE) != null)
			throw new BundleException(NLS.bind(AdaptorMsg.ADAPTOR_EXTENSION_REQUIRE_ERROR, bundleData.getLocation()));
		if (extensionManifest.get(Constants.BUNDLE_NATIVECODE) != null)
			throw new BundleException(NLS.bind(AdaptorMsg.ADAPTOR_EXTENSION_NATIVECODE_ERROR, bundleData.getLocation()));
	}

	/**
	 * Processes a framework extension bundle
	 * @param bundleData the extension bundle data
	 * @param type the type of extension bundle
	 * @throws BundleException on errors or if framework extensions are not supported
	 */
	protected void processFrameworkExtension(BundleData bundleData, byte type) throws BundleException {
		if (addURLMethod == null)
			throw new BundleException("Framework extensions are not supported.", new UnsupportedOperationException()); //$NON-NLS-1$
		if ((type & (EXTENSION_UNINSTALLED | EXTENSION_UPDATED)) != 0)
			// if uninstalled or updated then do nothing framework must be restarted.
			return;

		// first make sure this BundleData is not on the pre-configured osgi.framework.extensions list
		String[] extensions = getConfiguredExtensions();
		for (int i = 0; i < extensions.length; i++)
			if (extensions[i].equals(bundleData.getSymbolicName()))
				return;
		File[] files = getExtensionFiles(bundleData);
		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i] == null)
				continue;
			Throwable exceptionLog = null;
			try {
				addURLMethod.invoke(getClass().getClassLoader(), new Object[] {files[i].toURL()});
			} catch (InvocationTargetException e) {
				exceptionLog = e.getTargetException();
			} catch (Throwable t) {
				exceptionLog = t;
			} finally {
				if (exceptionLog != null)
					eventPublisher.publishFrameworkEvent(FrameworkEvent.ERROR, ((AbstractBundleData) bundleData).getBundle(), exceptionLog);
			}
		}
	}

	/**
	 * Returns a list of configured extensions
	 * @return a list of configured extensions
	 */
	protected String[] getConfiguredExtensions() {
		if (configuredExtensions != null)
			return configuredExtensions;
		String prop = System.getProperty(PROP_FRAMEWORK_EXTENSIONS);
		if (prop == null || prop.trim().length() == 0)
			configuredExtensions = new String[0];
		else
			configuredExtensions = ManifestElement.getArrayFromList(prop);
		return configuredExtensions;
	}

	/**
	 * Processes a boot extension bundle
	 * @param bundleData the extension bundle data
	 * @param type the type of extension bundle
	 * @throws BundleException on errors or if boot extensions are not supported
	 */
	protected void processBootExtension(BundleData bundleData, byte type) throws BundleException {
		throw new BundleException("Boot classpath extensions are not supported.", new UnsupportedOperationException()); //$NON-NLS-1$
	}

	/**
	 * Returns a list of classpath files for an extension bundle
	 * @param bundleData the bundle data for an extension bundle
	 * @return a list of classpath files for an extension bundle
	 */
	protected File[] getExtensionFiles(BundleData bundleData) {
		File[] files = null;
		try {
			String[] paths = bundleData.getClassPath();
			// TODO need to be smarter about dev path here
			if (System.getProperty("osgi.dev") != null) { //$NON-NLS-1$
				String[] origPaths = paths;
				paths = new String[origPaths.length + 1];
				System.arraycopy(origPaths, 0, paths, 0, origPaths.length);
				paths[paths.length - 1] = "bin"; //$NON-NLS-1$
			}
			files = ((AbstractBundleData) bundleData).getClasspathFiles(paths);
		} catch (BundleException e) {
			eventPublisher.publishFrameworkEvent(FrameworkEvent.ERROR, ((AbstractBundleData) bundleData).getBundle(), e);
		}
		return files;
	}

	/**
	 * @see FrameworkAdaptor#handleRuntimeError(Throwable)
	 */
	public void handleRuntimeError(Throwable error) {
		// do nothing by default.
	}

	/**
	 * Returns the root data directory
	 * @return the root data directory
	 */
	public File getDataRootDir() {
		if (dataRootDir == null)
			initDataRootDir();
		return dataRootDir;
	}

	/**
	 * Creates a BundleFile object from a File object and a BundleData
	 * object.  This implementation checks to see if the basefile is 
	 * a directory or a regular file and creates the proper BundleFile
	 * type accordingly.  If the file is a regular file this implementation
	 * assumes that the file is a zip file.
	 * @param basefile the base File object.
	 * @param bundledata the BundleData object that BundleFile is associated with.
	 * @return A new BundleFile object
	 * @throws IOException if an error occurred creating the BundleFile object.
	 */
	public BundleFile createBundleFile(File basefile, BundleData bundledata) throws IOException {
		if (basefile.isDirectory())
			return new BundleFile.DirBundleFile(basefile);
		return new BundleFile.ZipBundleFile(basefile, bundledata);
	}

	/**
	 * Creates a base bundle file for a bundle
	 * @param basefile the base file object for the bundle
	 * @param bundledata the bundle data for the bundle
	 * @return a base bundle file for a bundle
	 * @throws IOException
	 */
	public BundleFile createBaseBundleFile(File basefile, BundleData bundledata) throws IOException {
		BundleFile base = createBundleFile(basefile, bundledata);
		if (System.getSecurityManager() == null || !supportSignedBundles)
			return base;
		SignedBundleSupport support = getSignedBundleSupport();
		if (support == null)
			return base;
		SignedBundle signedBundle = support.createSignedBundle();
		signedBundle.setBundleFile(base);
		return signedBundle;
	}

	/**
	 * @see FrameworkAdaptor#matchDNChain(String, String[])
	 */
	public boolean matchDNChain(String pattern, String[] dnChain) {
		SignedBundleSupport support = getSignedBundleSupport();
		if (support != null)
			return support.matchDNChain(pattern, dnChain);
		return false;
	}

	/**
	 * Returns the SignedBundleSupport object.  If signed bundles are not
	 * supported then <code>null</code> is returned.
	 * @return the SignedBundleSupport object or <code>null</code> if signed bundles
	 * are not supported.
	 */
	protected SignedBundleSupport getSignedBundleSupport() {
		if (System.getSecurityManager() == null || !supportSignedBundles)
			return null;
		try {
			if (signedBundleSupport == null) {
				String clazzName = System.getProperty(PROP_SIGNINGSUPPORT, DEFAULT_SIGNEDBUNDLE_SUPPORT);
				Class clazz = Class.forName(clazzName);
				signedBundleSupport = (SignedBundleSupport) clazz.newInstance();
			}
			return signedBundleSupport;
		} catch (ClassNotFoundException e) {
			supportSignedBundles = false;
		} catch (IllegalAccessException e) {
			supportSignedBundles = false;
		} catch (InstantiationException e) {
			supportSignedBundles = false;
		}
		return null;
	}

	/**
	 * @see FrameworkAdaptor#installBundle(String, URLConnection)
	 */
	public BundleOperation installBundle(final String location, final URLConnection source) {
		return (new BundleOperation() {
			private AbstractBundleData data;

			/**
			 * Begin the operation on the bundle (install, update, uninstall).
			 *
			 * @return BundleData object for the target bundle.
			 * @throws BundleException If a failure occured modifiying peristent storage.
			 */
			public org.eclipse.osgi.framework.adaptor.BundleData begin() throws BundleException {
				long id;

				try {
					/*
					 * Open InputStream first to trigger prereq installs, if any,
					 * before allocating bundle id.
					 */
					InputStream in = source.getInputStream();
					URL sourceURL = source.getURL();
					String protocol = sourceURL == null ? null : sourceURL.getProtocol();
					try {
						try {
							id = getNextBundleId();
						} catch (IOException e) {
							throw new BundleException(AdaptorMsg.ADAPTOR_STORAGE_EXCEPTION, e);
						}
						data = getElementFactory().createBundleData(AbstractFrameworkAdaptor.this, id);
						data.setLastModified(System.currentTimeMillis());
						data.setLocation(location);
						data.setStartLevel(getInitialBundleStartLevel());

						if (in instanceof ReferenceInputStream) {
							URL reference = ((ReferenceInputStream) in).getReference();

							if (!"file".equals(reference.getProtocol())) { //$NON-NLS-1$
								throw new BundleException(NLS.bind(AdaptorMsg.ADAPTOR_URL_CREATE_EXCEPTION, reference));
							}

							data.setReference(true);
							data.setFileName(reference.getPath());
							data.initializeNewBundle();
						} else {
							File genDir = data.createGenerationDir();
							if (!genDir.exists()) {
								throw new IOException(NLS.bind(AdaptorMsg.ADAPTOR_DIRECTORY_CREATE_EXCEPTION, genDir.getPath()));
							}

							String fileName = BUNDLEFILE_NAME;
							File outFile = new File(genDir, fileName);
							if ("file".equals(protocol)) { //$NON-NLS-1$
								File inFile = new File(source.getURL().getPath());
								if (inFile.isDirectory()) {
									copyDir(inFile, outFile);
								} else {
									readFile(in, outFile);
								}
							} else {
								readFile(in, outFile);
							}
							data.setReference(false);
							data.setFileName(fileName);
							data.initializeNewBundle();
						}
					} finally {
						try {
							in.close();
						} catch (IOException e) {
						}
					}
				} catch (IOException ioe) {
					throw new BundleException(AdaptorMsg.BUNDLE_READ_EXCEPTION, ioe);
				}

				return (data);
			}

			public void undo() {
				if (data != null) {
					try {
						data.close();
					} catch (IOException e) {
						if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
							Debug.println("Unable to close " + data + ": " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}

				if (data != null) {
					File bundleDir = data.getBundleStoreDir();

					if (!rm(bundleDir)) {
						/* mark this bundle to be deleted to ensure it is fully cleaned up
						 * on next restart.
						 */
						File delete = new File(bundleDir, ".delete"); //$NON-NLS-1$

						if (!delete.exists()) {
							try {
								/* create .delete */
								FileOutputStream out = new FileOutputStream(delete);
								out.close();
							} catch (IOException e) {
								if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
									Debug.println("Unable to write " + delete.getPath() + ": " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
								}
							}
						}
					}
				}
			}

			public void commit(boolean postpone) throws BundleException {
				processExtension(data, EXTENSION_INSTALLED);
				try {
					data.save();
				} catch (IOException e) {
					throw new BundleException(AdaptorMsg.ADAPTOR_STORAGE_EXCEPTION, e);
				}
				updateState(data, BundleEvent.INSTALLED);
			}

		});
	}

	/**
	 * This function performs the equivalent of "rm -r" on a file or directory.
	 *
	 * @param   file file or directory to delete
	 * @return false is the specified files still exists, true otherwise.
	 */
	protected boolean rm(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				String list[] = file.list();
				if (list != null) {
					int len = list.length;
					for (int i = 0; i < len; i++) {
						// we are doing a lot of garbage collecting here
						rm(new File(file, list[i]));
					}
				}
			}
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				if (file.isDirectory()) {
					Debug.println("rmdir " + file.getPath()); //$NON-NLS-1$
				} else {
					Debug.println("rm " + file.getPath()); //$NON-NLS-1$
				}
			}

			boolean success = file.delete();

			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				if (!success) {
					Debug.println("  rm failed!!"); //$NON-NLS-1$
				}
			}

			return (success);
		}
		return (true);
	}

	/**
	 * Shutdown the StateManager for the adaptor.  This should persist the state
	 * for reading when createStateManager is called.
	 */
	protected void shutdownStateManager() {
		try {
			if (canWrite() && (getBundleStoreRootDir().exists() || getBundleStoreRootDir().mkdirs()))
				stateManager.shutdown(new File(getBundleStoreRootDir(), ".state"), new File(getBundleStoreRootDir(), ".lazy")); //$NON-NLS-1$//$NON-NLS-2$
		} catch (IOException e) {
			frameworkLog.log(new FrameworkEvent(FrameworkEvent.ERROR, context.getBundle(), e));
		} finally {
			stateManager = null;
		}
	}

	/**
	 * Returns the bundle store root directory
	 * @return the bundle store root directory
	 */
	public File getBundleStoreRootDir() {
		return bundleStoreRootDir;
	}

	/**
	 * String containing bundle store root dir 
	 */
	protected String bundleStore = null;
	/**
	 * Dictionary containing permission data 
	 */
	protected PermissionStorage permissionStore;
	/**
	 * inidicates that the bundle storage should be reset
	 */
	protected boolean reset = false;
	/**
	 * directory containing data directories for installed bundles 
	 */
	protected File dataRootDir;
	/**
	 * The name of the bundle data directory
	 */
	public static final String DATA_DIR_NAME = "data";//$NON-NLS-1$
	/**
	 * Indicates that the state has become invalid as a result of 
	 * installing/updating/uninstalling a bundle before the state mananger is 
	 * created
	 */
	protected boolean invalidState = false;

	/**
	 * @see FrameworkAdaptor#updateBundle(BundleData, URLConnection)
	 */
	public BundleOperation updateBundle(final org.eclipse.osgi.framework.adaptor.BundleData bundledata, final URLConnection source) {
		return (new BundleOperation() {
			private AbstractBundleData data;
			private AbstractBundleData newData;

			/**
			 * Perform the change to persistent storage.
			 *
			 * @return Bundle object for the target bundle.
			 */
			public org.eclipse.osgi.framework.adaptor.BundleData begin() throws BundleException {
				this.data = (AbstractBundleData) bundledata;
				try {
					InputStream in = source.getInputStream();
					URL sourceURL = source.getURL();
					String protocol = sourceURL == null ? null : sourceURL.getProtocol();
					try {
						if (in instanceof ReferenceInputStream) {
							ReferenceInputStream refIn = (ReferenceInputStream) in;
							URL reference = (refIn).getReference();
							if (!"file".equals(reference.getProtocol())) { //$NON-NLS-1$
								throw new BundleException(NLS.bind(AdaptorMsg.ADAPTOR_URL_CREATE_EXCEPTION, reference));
							}
							// check to make sure we are not just trying to update to the same
							// directory reference.  This would be a no-op.
							String path = reference.getPath();
							if (path.equals(data.getFileName())) {
								throw new BundleException(NLS.bind(AdaptorMsg.ADAPTOR_SAME_REF_UPDATE, reference));
							}
							try {
								newData = data.nextGeneration(reference.getPath());
							} catch (IOException e) {
								throw new BundleException(AdaptorMsg.ADAPTOR_STORAGE_EXCEPTION, e);
							}
							File bundleGenerationDir = newData.createGenerationDir();
							if (!bundleGenerationDir.exists()) {
								throw new BundleException(NLS.bind(AdaptorMsg.ADAPTOR_DIRECTORY_CREATE_EXCEPTION, bundleGenerationDir.getPath()));
							}
							newData.createBaseBundleFile();
						} else {
							try {
								newData = data.nextGeneration(null);
							} catch (IOException e) {
								throw new BundleException(AdaptorMsg.ADAPTOR_STORAGE_EXCEPTION, e);
							}
							File bundleGenerationDir = newData.createGenerationDir();
							if (!bundleGenerationDir.exists()) {
								throw new BundleException(NLS.bind(AdaptorMsg.ADAPTOR_DIRECTORY_CREATE_EXCEPTION, bundleGenerationDir.getPath()));
							}
							File outFile = newData.getBaseFile();
							if ("file".equals(protocol)) { //$NON-NLS-1$
								File inFile = new File(source.getURL().getPath());
								if (inFile.isDirectory()) {
									copyDir(inFile, outFile);
								} else {
									readFile(in, outFile);
								}
							} else {
								readFile(in, outFile);
							}
							newData.createBaseBundleFile();
						}
					} finally {
						try {
							in.close();
						} catch (IOException ee) {
						}
					}
					newData.loadFromManifest();
				} catch (IOException e) {
					throw new BundleException(AdaptorMsg.BUNDLE_READ_EXCEPTION, e);
				}

				return (newData);
			}

			/**
			 * Commit the change to persistent storage.
			 *
			 * @param postpone If true, the bundle's persistent
			 * storage cannot be immediately reclaimed.
			 * @throws BundleException If a failure occured modifiying peristent storage.
			 */

			public void commit(boolean postpone) throws BundleException {
				processExtension(data, EXTENSION_UNINSTALLED); // remove the old extension
				processExtension(newData, EXTENSION_UPDATED); // update to the new one
				try {
					newData.setLastModified(System.currentTimeMillis());
					newData.save();
				} catch (IOException e) {
					throw new BundleException(AdaptorMsg.ADAPTOR_STORAGE_EXCEPTION, e);
				}
				updateState(newData, BundleEvent.UPDATED);
				File originalGenerationDir = data.createGenerationDir();

				if (postpone || !rm(originalGenerationDir)) {
					/* mark this bundle to be deleted to ensure it is fully cleaned up
					 * on next restart.
					 */
					File delete = new File(originalGenerationDir, ".delete"); //$NON-NLS-1$

					if (!delete.exists()) {
						try {
							/* create .delete */
							FileOutputStream out = new FileOutputStream(delete);
							out.close();
						} catch (IOException e) {
							if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
								Debug.println("Unable to write " + delete.getPath() + ": " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
							}

							eventPublisher.publishFrameworkEvent(FrameworkEvent.ERROR, data.getBundle(), e);
						}
					}
				}
			}

			/**
			 * Undo the change to persistent storage.
			 *
			 * @throws BundleException If a failure occured modifiying peristent storage.
			 */
			public void undo() throws BundleException {
				/*if (bundleFile != null)
				 {
				 bundleFile.close();
				 } */

				if (newData != null) {
					File nextGenerationDir = newData.createGenerationDir();

					if (!rm(nextGenerationDir)) /* delete downloaded bundle */{
						/* mark this bundle to be deleted to ensure it is fully cleaned up
						 * on next restart.
						 */
						File delete = new File(nextGenerationDir, ".delete"); //$NON-NLS-1$

						if (!delete.exists()) {
							try {
								/* create .delete */
								FileOutputStream out = new FileOutputStream(delete);
								out.close();
							} catch (IOException e) {
								if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
									Debug.println("Unable to write " + delete.getPath() + ": " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
								}
							}
						}
					}
				}
			}
		});
	}

	/**
	 * Iterates over the bundles in a state and removes any bundles in state that
	 * do not exist in the framework
	 * @param state the state to check
	 */
	protected void checkSystemState(State state) {
		BundleDescription[] bundles = state.getBundles();
		if (bundles == null)
			return;
		boolean removedBundle = false;
		for (int i = 0; i < bundles.length; i++) {
			if (context.getBundle(bundles[i].getBundleId()) == null) {
				state.removeBundle(bundles[i]);
				removedBundle = true;
			}
		}
		if (removedBundle)
			state.resolve(false); // do a full resolve
	}

	/**
	 * Creates the StateManager for the adaptor
	 * @return the StateManager.
	 */
	protected StateManager createStateManager() {
		stateManager = new StateManager(new File(getBundleStoreRootDir(), ".state"), new File(getBundleStoreRootDir(), ".lazy"), context); //$NON-NLS-1$ //$NON-NLS-2$
		State systemState = null;
		if (!invalidState) {
			systemState = stateManager.readSystemState();
			if (systemState != null)
				return stateManager;
		}
		systemState = stateManager.createSystemState();
		Bundle[] installedBundles = context.getBundles();
		if (installedBundles == null)
			return stateManager;
		StateObjectFactory factory = stateManager.getFactory();
		for (int i = 0; i < installedBundles.length; i++) {
			Bundle toAdd = installedBundles[i];
			try {
				Dictionary manifest = toAdd.getHeaders(""); //$NON-NLS-1$
				BundleDescription newDescription = factory.createBundleDescription(systemState, manifest, toAdd.getLocation(), toAdd.getBundleId());
				systemState.addBundle(newDescription);
			} catch (BundleException be) {
				// just ignore bundle datas with invalid manifests
			}
		}
		// we need the state resolved
		systemState.resolve();
		invalidState = false;
		return stateManager;
	}

	/**
	 * @see FrameworkAdaptor#uninstallBundle(BundleData)
	 */
	public BundleOperation uninstallBundle(final org.eclipse.osgi.framework.adaptor.BundleData bundledata) {
		return (new BundleOperation() {
			private AbstractBundleData data;

			/**
			 * Perform the change to persistent storage.
			 *
			 * @return Bundle object for the target bundle.
			 * @throws BundleException If a failure occured modifiying peristent storage.
			 */
			public org.eclipse.osgi.framework.adaptor.BundleData begin() throws BundleException {
				this.data = (AbstractBundleData) bundledata;
				return (bundledata);
			}

			/**
			 * Commit the change to persistent storage.
			 *
			 * @param postpone If true, the bundle's persistent
			 * storage cannot be immediately reclaimed.
			 * @throws BundleException If a failure occured modifiying peristent storage.
			 */
			public void commit(boolean postpone) throws BundleException {
				File bundleDir = data.getBundleStoreDir();

				if (postpone || !rm(bundleDir)) {
					/* mark this bundle to be deleted to ensure it is fully cleaned up
					 * on next restart.
					 */

					File delete = new File(bundleDir, ".delete"); //$NON-NLS-1$

					if (!delete.exists()) {
						try {
							/* create .delete */
							FileOutputStream out = new FileOutputStream(delete);
							out.close();
						} catch (IOException e) {
							if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
								Debug.println("Unable to write " + delete.getPath() + ": " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
							}
						}
					}
				}

				processExtension(data, EXTENSION_UNINSTALLED);
				data.setLastModified(System.currentTimeMillis());
				updateState(data, BundleEvent.UNINSTALLED);
			}

			/**
			 * Undo the change to persistent storage.
			 *
			 * @throws BundleException If a failure occured modifiying peristent storage.
			 */
			public void undo() throws BundleException {
			}
		});
	}

	/**
	 * Init the directory to store the bundles in.  Bundledir can be set in 3 different ways.
	 * Priority is:
	 * 1 - OSGI Launcher command line -adaptor argument
	 * 2 - System property osgi.bundlestore - could be specified with -D when launching
	 * 3 - osgi.properties - osgi.bundlestore property
	 *
	 * Bundledir will be stored back to adaptor properties which
	 * the framework will copy into the System properties.
	 */
	protected void initBundleStoreRootDir() {
		/* if bundleStore was not set by the constructor from the -adaptor cmd line arg */
		if (bundleStore == null) {
			/* check the system properties */
			bundleStore = System.getProperty(BUNDLE_STORE);

			if (bundleStore == null) {
				/* check the osgi.properties file, but default to "bundles" */
				bundleStore = properties.getProperty(BUNDLE_STORE, "bundles"); //$NON-NLS-1$
			}
		}

		bundleStoreRootDir = new File(bundleStore);
		/* store bundleStore back into adaptor properties for others to see */
		properties.put(BUNDLE_STORE, bundleStoreRootDir.getAbsolutePath());

	}

	/**
	 * Register a service object.
	 * @param name the service class name
	 * @param service the service object
	 * @param bundle the registering bundle
	 * @return the service registration object
	 */
	protected ServiceRegistration register(String name, Object service, Bundle bundle) {
		Hashtable properties = new Hashtable(7);

		Dictionary headers = bundle.getHeaders();

		properties.put(Constants.SERVICE_VENDOR, headers.get(Constants.BUNDLE_VENDOR));

		properties.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));

		properties.put(Constants.SERVICE_PID, bundle.getBundleId() + "." + service.getClass().getName()); //$NON-NLS-1$

		return context.registerService(name, service, properties);
	}

	/**
	 * Initialize the persistent storage.
	 *
	 * <p>This method initializes the bundle persistent storage
	 * area.
	 * If a dir was specified in the -adaptor command line option, then it
	 * is used.  If not,
	 * if the property
	 * <i>osgi.bundlestore</i> is specified, its value
	 * will be used as the name of the bundle directory
	 * instead of <tt>./bundles</tt>.
	 * If reset was specified on the -adaptor command line option,
	 * then the storage will be cleared.
	 *
	 * @throws IOException If an error occurs initializing the storage.
	 */
	public void initializeStorage() throws IOException {
		File bundleStore;
		// only touch the file system if reset is specified
		// we create the area on demand later if needed
		if (reset && (bundleStore = getBundleStoreRootDir()).exists()) {
			if (!canWrite() || !rm(bundleStore)) {
					if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
						Debug.println("Could not remove directory: " + bundleStore.getPath()); //$NON-NLS-1$
					}

				throw new IOException(NLS.bind(AdaptorMsg.ADAPTOR_DIRECTORY_REMOVE_EXCEPTION, bundleStore));
				}
			}

		initializeMetadata();
	}

	/**
	 * Initialize the metadata for the adaptor
	 * @throws IOException
	 */
	abstract protected void initializeMetadata() throws IOException;

	/**
	 * @see FrameworkAdaptor#getPermissionStorage()
	 */
	public org.eclipse.osgi.framework.adaptor.PermissionStorage getPermissionStorage() throws IOException {
		if (permissionStore == null) {
			synchronized (this) {
				if (permissionStore == null) {
					permissionStore = new DefaultPermissionStorage(this);
				}
			}
		}

		return permissionStore;
	}

	/**
	 * @see FrameworkAdaptor#compactStorage()
	 */
	public void compactStorage() {
		if (canWrite())
			compact(getBundleStoreRootDir());
	}

	/**
	 * This method cleans up storage in the specified directory and
	 * any subdirectories.
	 *
	 * @param directory The directory to clean.
	 * @see #compactStorage
	 */
	private void compact(File directory) {
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			Debug.println("compact(" + directory.getPath() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		String list[] = directory.list();
		if (list == null)
			return;

		int len = list.length;

		for (int i = 0; i < len; i++) {
			if (DATA_DIR_NAME.equals(list[i])) {
				continue; /* do not examine the bundles data dir. */
			}

			File target = new File(directory, list[i]);

			/* if the file is a directory */
			if (!target.isDirectory())
				continue;
			File delete = new File(target, ".delete"); //$NON-NLS-1$

			/* and the directory is marked for delete */
			if (delete.exists()) {
				/* if rm fails to delete the directory *
				 * and .delete was removed
				 */
				if (!rm(target) && !delete.exists()) {
					try {
						/* recreate .delete */
						FileOutputStream out = new FileOutputStream(delete);
						out.close();
					} catch (IOException e) {
						if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
							Debug.println("Unable to write " + delete.getPath() + ": " + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			} else {
				compact(target); /* descend into directory */
			}
		}
	}

	/**
	 * Returns the metadata file for the adaptor
	 * @return the metadata file for the adaptor
	 */
	protected File getMetaDataFile() {
		return new File(getBundleStoreRootDir(), ".framework"); //$NON-NLS-1$
	}

	/**
	 * Empty parent classloader.  This is used by default as the BundleClassLoader
	 * parent.
	 */
	protected static class ParentClassLoader extends ClassLoader {
		protected ParentClassLoader() {
			super(null);
		}
	}

	/**
	 * Updates the state mananager with an updated/installed/uninstalled bundle
	 * @param bundleData the modified bundle
	 * @param type the type of modification
	 * @throws BundleException
	 */
	protected void updateState(BundleData bundleData, int type) throws BundleException {
		if (stateManager == null) {
			invalidState = true;
			return;
		}
		State systemState = stateManager.getSystemState();
		switch (type) {
			case BundleEvent.UPDATED :
				systemState.removeBundle(bundleData.getBundleID());
			// fall through to INSTALLED
			case BundleEvent.INSTALLED :
				BundleDescription newDescription = stateManager.getFactory().createBundleDescription(systemState, bundleData.getManifest(), bundleData.getLocation(), bundleData.getBundleID());
				systemState.addBundle(newDescription);
				break;
			case BundleEvent.UNINSTALLED :
				systemState.removeBundle(bundleData.getBundleID());
				break;
		}
	}

	/**
	 * Whether the adaptor can make changes to the file system. Default is <code>true</code>.
	 */
	public boolean canWrite() {
		return true;
	}
}
