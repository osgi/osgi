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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.Properties;
import org.eclipse.osgi.framework.adaptor.*;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.internal.core.BundleResourceHandler;
import org.eclipse.osgi.framework.internal.core.Constants;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.framework.util.Headers;
import org.eclipse.osgi.internal.resolver.StateManager;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.*;

/**
 * An abstract FrameworkAdaptor class that has default implementations that most
 * FrameworkAdaptor implementations can use.
 */
public abstract class AbstractFrameworkAdaptor implements FrameworkAdaptor {
	public static final String PROP_PARENT_CLASSLOADER = "osgi.parentClassloader"; //$NON-NLS-1$
	public static final String PARENT_CLASSLOADER_APP = "app"; //$NON-NLS-1$
	public static final String PARENT_CLASSLOADER_EXT = "ext"; //$NON-NLS-1$
	public static final String PARENT_CLASSLOADER_BOOT = "boot"; //$NON-NLS-1$
	public static final String PARENT_CLASSLOADER_FWK = "fwk"; //$NON-NLS-1$

	/** Name of the Adaptor manifest file */
	protected final String ADAPTOR_MANIFEST = "ADAPTOR.MF"; //$NON-NLS-1$

	/**
	 * The EventPublisher for the FrameworkAdaptor
	 */
	protected EventPublisher eventPublisher;

	/**
	 * The ServiceRegistry object for this FrameworkAdaptor.
	 */
	protected ServiceRegistryImpl serviceRegistry;

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
	private AbstractBundleData data;
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

	protected AdaptorElementFactory elementFactory;

	public static final String METADATA_ADAPTOR_NEXTID = "METADATA_ADAPTOR_NEXTID";

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
	 * Initializes the ServiceRegistry, loads the properties for this
	 * FrameworkAdaptor reads the adaptor manifest file.
	 */
	public void initialize(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		serviceRegistry = new ServiceRegistryImpl();
		serviceRegistry.initialize();
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

	protected abstract void persistNextBundleID(long value) throws IOException;

	public FrameworkLog getFrameworkLog() {
		return frameworkLog;
	}

	public State getState() {
		return stateManager.getSystemState();
	}

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
			throw new BundleException(AdaptorMsg.formatter.getString("ADAPTOR_URL_CREATE_EXCEPTION", location), e); //$NON-NLS-1$
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

	public boolean isStopping() {
		return stopping;
	}

	public int getInitialBundleStartLevel() {
		return initialBundleStartLevel;
	}

	public void setInitialBundleStartLevel(int value) {
		initialBundleStartLevel = value;
	}

	public BundleWatcher getBundleWatcher() {
		return null;
	}

	public void setBundleData(AbstractBundleData data) {
		this.data = data;
	}

	/**
	 * The FrameworkLog for the adaptor 
	 */
	protected FrameworkLog frameworkLog;

	public static final String BUNDLE_STORE = "osgi.bundlestore";

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
	}

	//protected abstract long getNextBundleId();
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

		throw new IOException(AdaptorMsg.formatter.getString("ADAPTOR_STORAGE_EXCEPTION")); //$NON-NLS-1$
	}

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
	 * 
	 *
	 */
	abstract protected FrameworkLog createFrameworkLog();
	
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

	public ClassLoader getBundleClassLoaderParent() {
		return bundleClassLoaderParent;
	}

	public void handleRuntimeError(Throwable error) {
		// do nothing by default.
	}

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
		if (basefile.isDirectory()) {
			return new BundleFile.DirBundleFile(basefile);
		} else {
			return new BundleFile.ZipBundleFile(basefile, bundledata);
		}
	}

	/**
	 * Prepare to install a bundle from a URLConnection.
	 * <p>To complete the install,
	 * begin and then commit
	 * must be called on the returned <code>BundleOperation</code> object.
	 * If either of these methods throw a BundleException
	 * or some other error occurs,
	 * then undo must be called on the <code>BundleOperation</code> object
	 * to undo the change to persistent storage.
	 *
	 * @param location Bundle location.
	 * @param source URLConnection from which the bundle may be read.
	 * Any InputStreams returned from the source
	 * (URLConnections.getInputStream) must be closed by the
	 * <code>BundleOperation</code> object.
	 * @return BundleOperation object to be used to complete the install.
	 */
	public BundleOperation installBundle(final String location, final URLConnection source) {
		final AbstractBundleData bundleData = data;
		data = null;
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
							throw new BundleException(AdaptorMsg.formatter.getString("ADAPTOR_STORAGE_EXCEPTION"), e); //$NON-NLS-1$
						}
						if (bundleData == null) {
							data = (AbstractBundleData) getElementFactory().createBundleData(AbstractFrameworkAdaptor.this, id);
						} else {
							data = bundleData;
						}
						data.setLastModified(System.currentTimeMillis());
						data.setLocation(location);
						data.setStartLevel(getInitialBundleStartLevel());

						if (in instanceof ReferenceInputStream) {
							URL reference = ((ReferenceInputStream) in).getReference();

							if (!"file".equals(reference.getProtocol())) { //$NON-NLS-1$
								throw new BundleException(AdaptorMsg.formatter.getString("ADAPTOR_URL_CREATE_EXCEPTION", reference)); //$NON-NLS-1$
							}

							data.setReference(true);
							data.setFileName(reference.getPath());
							data.initializeNewBundle();
						} else {
							File genDir = data.createGenerationDir();
							if (!genDir.exists()) {
								throw new IOException(AdaptorMsg.formatter.getString("ADAPTOR_DIRECTORY_CREATE_EXCEPTION", genDir.getPath())); //$NON-NLS-1$
							}

							String fileName = mapLocationToName(location);
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
					throw new BundleException(AdaptorMsg.formatter.getString("BUNDLE_READ_EXCEPTION"), ioe); //$NON-NLS-1$
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
				try {
					data.save();
				} catch (IOException e) {
					throw new BundleException(AdaptorMsg.formatter.getString("ADAPTOR_STORAGE_EXCEPTION"), e); //$NON-NLS-1$
				}
				if (stateManager != null) {
					BundleDescription bundleDescription = stateManager.getFactory().createBundleDescription(data.getManifest(), data.getLocation(), data.getBundleID());
					stateManager.getSystemState().addBundle(bundleDescription);
				}
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
			stateManager.shutdown(new File(getBundleStoreRootDir(), ".state")); //$NON-NLS-1$
		} catch (IOException e) {
			frameworkLog.log(new FrameworkEvent(FrameworkEvent.ERROR, context.getBundle(), e));
		} finally {
			stateManager = null;
		}
	}

	/**
	 * Map a location string into a bundle name.
	 * This methods treats the location string as a URL.
	 *
	 * @param location bundle location string.
	 * @return bundle name.
	 */
	public String mapLocationToName(String location) {
		int end = location.indexOf('?', 0); /* "?" query */

		if (end == -1) {
			end = location.indexOf('#', 0); /* "#" fragment */

			if (end == -1) {
				end = location.length();
			}
		}

		int begin = location.replace('\\', '/').lastIndexOf('/', end);
		int colon = location.lastIndexOf(':', end);

		if (colon > begin) {
			begin = colon;
		}

		return (location.substring(begin + 1, end));
	}

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
	protected DefaultPermissionStorage permissionStore;
	protected boolean reset = false;
	/**
	 * directory containing data directories for installed bundles 
	 */
	protected File dataRootDir;
	public static final String METADATA_ADAPTOR_IBSL = "METADATA_ADAPTOR_IBSL";
	public static final String DATA_DIR_NAME = "data";

	/**
	 * Prepare to update a bundle from a URLConnection.
	 * <p>To complete the update,
	 * modify and then commit
	 * will be called on the returned BundleStorage object.
	 * If either of these methods throw a BundleException
	 * or some other error occurs,
	 * then undo will be called on the BundleStorage object
	 * to undo the change to persistent storage.
	 *
	 * @param bundledata BundleData to update.
	 * @param source URLConnection from which the bundle may be read.
	 * @return BundleOperation object to be used to complete the update.
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
								throw new BundleException(AdaptorMsg.formatter.getString("ADAPTOR_URL_CREATE_EXCEPTION", reference)); //$NON-NLS-1$
							}
							// check to make sure we are not just trying to update to the same
							// directory reference.  This would be a no-op.
							String path = reference.getPath();
							if (path.equals(data.getFileName())) {
								throw new BundleException(AdaptorMsg.formatter.getString("ADAPTOR_SAME_REF_UPDATE", reference)); //$NON-NLS-1$
							}
							try {
								newData = data.nextGeneration(reference.getPath());
							} catch (IOException e) {
								throw new BundleException(AdaptorMsg.formatter.getString("ADAPTOR_STORAGE_EXCEPTION"), e); //$NON-NLS-1$
							}
							File bundleGenerationDir = newData.createGenerationDir();
							if (!bundleGenerationDir.exists()) {
								throw new BundleException(AdaptorMsg.formatter.getString("ADAPTOR_DIRECTORY_CREATE_EXCEPTION", bundleGenerationDir.getPath())); //$NON-NLS-1$
							}
							newData.createBaseBundleFile();
						} else {
							try {
								newData = data.nextGeneration(null);
							} catch (IOException e) {
								throw new BundleException(AdaptorMsg.formatter.getString("ADAPTOR_STORAGE_EXCEPTION"), e); //$NON-NLS-1$
							}
							File bundleGenerationDir = newData.createGenerationDir();
							if (!bundleGenerationDir.exists()) {
								throw new BundleException(AdaptorMsg.formatter.getString("ADAPTOR_DIRECTORY_CREATE_EXCEPTION", bundleGenerationDir.getPath())); //$NON-NLS-1$
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
					throw new BundleException(AdaptorMsg.formatter.getString("BUNDLE_READ_EXCEPTION"), e); //$NON-NLS-1$
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
				try {
					newData.setLastModified(System.currentTimeMillis());
					newData.save();
				} catch (IOException e) {
					throw new BundleException(AdaptorMsg.formatter.getString("ADAPTOR_STORAGE_EXCEPTION"), e); //$NON-NLS-1$
				}
				long bundleId = newData.getBundleID();
				if (stateManager != null) {
					State systemState = stateManager.getSystemState();
					systemState.removeBundle(bundleId);
					BundleDescription newDescription = stateManager.getFactory().createBundleDescription(newData.getManifest(), newData.getLocation(), bundleId);
					systemState.addBundle(newDescription);
				}
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
		File stateLocation = new File(getBundleStoreRootDir(), ".state"); //$NON-NLS-1$
		stateManager = new StateManager(stateLocation);
		State systemState = stateManager.readSystemState(context);
		if (systemState != null)
			return stateManager;
		systemState = stateManager.createSystemState(context);
		Bundle[] installedBundles = context.getBundles();
		if (installedBundles == null)
			return stateManager;
		StateObjectFactory factory = stateManager.getFactory();
		for (int i = 0; i < installedBundles.length; i++) {
			Bundle toAdd = installedBundles[i];
			try {
				Dictionary manifest = toAdd.getHeaders(""); //$NON-NLS-1$
				BundleDescription newDescription = factory.createBundleDescription(manifest, toAdd.getLocation(), toAdd.getBundleId());
				systemState.addBundle(newDescription);
			} catch (BundleException be) {
				// just ignore bundle datas with invalid manifests
			}
		}
		// we need the state resolved
		systemState.resolve();
		return stateManager;
	}

	/**
	 * Prepare to uninstall a bundle.
	 * <p>To complete the uninstall,
	 * modify and then commit
	 * will be called on the returned BundleStorage object.
	 * If either of these methods throw a BundleException
	 * or some other error occurs,
	 * then undo will be called on the BundleStorage object
	 * to undo the change to persistent storage.
	 *
	 * @param bundledata BundleData to uninstall.
	 * @return BundleOperation object to be used to complete the uninstall.
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

				data.setLastModified(System.currentTimeMillis());
				if (stateManager != null)
					stateManager.getSystemState().removeBundle(data.getBundleID());
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
	 * 2 - System property org.eclipse.osgi.framework.defaultadaptor.bundledir - could be specified with -D when launching
	 * 3 - osgi.properties - org.eclipse.osgi.framework.defaultadaptor.bundledir property
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
	 *
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
	 * <i>org.eclipse.osgi.framework.defaultadaptor.bundledir</i> is specifed, its value
	 * will be used as the name of the bundle directory
	 * instead of <tt>./bundles</tt>.
	 * If reset was specified on the -adaptor command line option,
	 * then the storage will be cleared.
	 *
	 * @throws IOException If an error occurs initializing the storage.
	 */
	public void initializeStorage() throws IOException {
		boolean makedir = false;
		File bundleStore = getBundleStoreRootDir();
		if (bundleStore.exists()) {
			if (reset) {
				makedir = true;
				if (!rm(bundleStore)) {
					if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
						Debug.println("Could not remove directory: " + bundleStore.getPath()); //$NON-NLS-1$
					}
				}
			} else {
				if (!bundleStore.isDirectory()) {
					if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
						Debug.println("Exists but not a directory: " + bundleStore.getPath()); //$NON-NLS-1$
					}

					throw new IOException(AdaptorMsg.formatter.getString("ADAPTOR_STORAGE_EXCEPTION")); //$NON-NLS-1$
				}
			}
		} else {
			makedir = true;
		}
		if (makedir) {
			if (!bundleStore.mkdirs()) {
				if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
					Debug.println("Unable to create directory: " + bundleStore.getPath()); //$NON-NLS-1$
				}

				throw new IOException(AdaptorMsg.formatter.getString("ADAPTOR_STORAGE_EXCEPTION")); //$NON-NLS-1$
			}
		}

		initializeMetadata();
	}

	abstract protected void initializeMetadata() throws IOException;

	/**
	 * Returns the PermissionStorage object which will be used to
	 * to manage the permission data.
	 *
	 * <p>The PermissionStorage object will store permission data
	 * in the "permdata" subdirectory of the bundle storage directory
	 * assigned by <tt>initializeStorage</tt>.
	 *
	 * @return The PermissionStorage object for the DefaultAdaptor.
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
	 * Clean up the persistent storage.
	 *
	 * <p>Cleans up any deferred deletions in persistent storage.
	 *
	 */
	public void compactStorage() {
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
}