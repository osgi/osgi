/**
 * Copyright (c) 1999 - 2002 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants Open Services Gateway Initiative (OSGi) an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */

package org.osgi.impl.framework;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import org.osgi.framework.*;


/**
 * Implementation of the Bundle object.
 *
 * @see org.osgi.framework.Bundle
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 */
class BundleImpl implements Bundle, Comparable
{
	/**
	 * Base for property string.
	 */
	final static String BUNDLE_BASE = "bundle.";
	
	/**
	 * Framework for bundle.
	 */
	final Framework framework;
	
	/**
	 * Bundle permission collection.
	 */
	final ProtectionDomain protectionDomain;
	
	/**
	 * Bundle identifier.
	 */
	final long id;
	
	/**
	 * Bundle location identifier.
	 */
	final String location;
	
	/**
	 * State of bundle.
	 */
	int state;
	
	/**
	 * Packages that the bundle wants to export and import.
	 */
	BundlePackages bpkgs;
	
	/**
	 * Bundle JAR data.
	 */
	Archive archive;
	
	/**
	 * Start level.
	 */
	int startLevel;
	
	/**
	 * The persistent state of the bundle.
	 */
	boolean isPersistentlyStarted;
	
	/**
	 * Classloader for bundle.
	 */
	private BundleClassLoader classLoader = null;
	
	/**
	 * Zombie classloaders for bundle.
	 */
	private Map /* String -> BundleClassLoader */ oldClassLoaders = null;
	
	/**
	 * Directory for bundle data.
	 */
	private FileTree bundleDir;
	
	/**
	 * BundleContext for bundle.
	 */
	private BundleContextImpl bundleContext = null;
	
	/**
	 * BundleActivator for bundle.
	 */
	private BundleActivator bactivator = null;
	
	/**
	 * List of all nativeLibs that can be used in this bundle.
	 */
	private List nativeLibs = null;
	
	/**
	 * Bundle Manifest data.
	 */
	HeaderDictionary headers;
	
	
	/**
	 * Construct a new Bundle empty.
	 *
	 * @param fw Framework for this bundle.
	 */
	BundleImpl(Framework fw, long id, String loc, ProtectionDomain pd)
	{
		this.framework = fw;
		this.id = id;
		this.location = loc;
		this.protectionDomain = pd;
	}
	
	/**
	 * Construct a new Bundle based on an InputStream.
	 *
	 * @param bundlesDir Directory where to store the bundles all persistent data.
	 * @param fw Framework for this bundle.
	 * @param loc Location for new bundle.
	 * @param in Bundle JAR as an inputstream.
	 * @exception IOException If we fail to read and store our JAR bundle or if
	 *            the input data is corrupted.
	 * @exception SecurityException If we don't have permission to import and export
	 *            bundle packages.
	 * @exception BundleException If can not find native code that match this JVM.
	 */
	BundleImpl(File bundlesDir, Framework fw, String loc, InputStream in)
		throws BundleException, IOException
	{
		framework = fw;
		location = loc;
		id = fw.bundles.getNewId();
		startLevel = fw.bundles.getInitialBundleStartLevel();
		isPersistentlyStarted = false;
		bundleDir = new FileTree(bundlesDir, "bundle_" + id);
		if (bundleDir.exists()) {
			// remove any old garbage
			bundleDir.delete();
		}
		bundleDir.mkdir();
		URL bundleURL = getBundleURL(-1, "");
		archive = new Archive(bundleDir, in, bundleURL);
		headers = archive.getHeaderDictionary();
    String requiredEEs = (String)headers.get(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT);
    verifyRequiredEEs(requiredEEs, loc);
		nativeLibs = getNativeCode(archive);
		state = INSTALLED;
		ProtectionDomain pd = null;
		if (framework.checkPermissions) {
			PermissionCollection pc = framework.permissions.getPermissionCollection(this);
			pd = new ProtectionDomain(new CodeSource(bundleURL, null), pc);
		}
		protectionDomain = pd;
		initPackages();
	}
	
	
	/**
	 * Construct a new Bundle based on saved property data from
	 * last session.
	 *
	 * @param bundlesDir Directory where to store the bundles all persistent data.
	 * @param fw Framework for this bundle.
	 * @param pfirst Property prefix for this bundle.
	 * @param p Properties describing this bundle.
	 * @exception IOException If we fail to read and store our JAR bundle or if
	 *            the input data is corrupted.
	 * @exception SecurityException If we don't have permission to import and export
	 *            bundle packages.
	 * @exception BundleException If can not find native code that match this JVM.
	 */
	BundleImpl(File bundlesDir, Framework fw, String pfirst, Properties p) 
		throws BundleException, IOException
	{
		framework = fw;
		if (!pfirst.startsWith(BUNDLE_BASE)) {
			throw new IOException("Illegal property");
		}
		String pbase = pfirst.substring(0, pfirst.indexOf('.',  BUNDLE_BASE.length()));
		long tid = 0;
		try {
			tid = Long.parseLong(pbase.substring(7));
		} catch (NullPointerException e) { } catch (NumberFormatException e) { }
		id = tid;
		if (framework.bundles.checkIllegalId(id)) {
			throw new IOException("Illegal bundle id");
		}
		
		location = p.getProperty(pbase + ".location");
		p.remove(pbase + ".location");
		if (framework.bundles.checkIllegalLocation(location)) {
			throw new IOException("Illegal bundle location: " + location);
		}
		try {
			startLevel = Integer.parseInt(p.getProperty(pbase + ".startLevel", Integer.toString(fw.bundles.getInitialBundleStartLevel())));
			p.remove(pbase + ".startLevel");
		} catch (NumberFormatException e) {
			startLevel = fw.bundles.getInitialBundleStartLevel();
		}
		isPersistentlyStarted = Boolean.valueOf(p.getProperty(pbase + ".isPersistentlyStarted", "false")).booleanValue();
		p.remove(pbase + ".isPersistentlyStarted");
		bundleDir = new FileTree(bundlesDir, "bundle_" + id);
		URL bundleURL = getBundleURL(-1, "");
		archive = new Archive(bundleDir, bundleURL);
		headers = archive.getHeaderDictionary();
    String requiredEEs = (String)headers.get(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT);
    verifyRequiredEEs(requiredEEs, location);
		nativeLibs = getNativeCode(archive);
		state = INSTALLED;
		ProtectionDomain pd = null;
		if (framework.checkPermissions) {
			PermissionCollection pc = framework.permissions.getPermissionCollection(this);
			pd = new ProtectionDomain(new CodeSource(bundleURL, null), pc);
		}
		protectionDomain = pd;
		initPackages();
	}
	
	//
	// Bundle interface
	//
	
	/**
	 * Get bundle state.
	 *
	 * @see org.osgi.framework.Bundle#getState
	 */
	public int getState()
	{
		return state;
	}
	
	
	/**
	 * Start this bundle.
	 *
	 * @see org.osgi.framework.Bundle#start
	 */
	synchronized public void start() throws BundleException
	{
		framework.checkAdminPermission();
		switch (getUpdatedState()) {
		case INSTALLED:
			throw new BundleException("Bundle.start: Failed, " + bpkgs.getResolveFailReason());
		case RESOLVED:
			if (startLevel <= framework.startLevel.activeStartLevel &&
			    startLevel <= framework.startLevel.requestedStartLevel) {
				state = STARTING;
				bundleContext = new BundleContextImpl(this);
				try {
					AccessController.doPrivileged(new PrivilegedExceptionAction() {
							public Object run() throws BundleException {
								final String ba = archive.getAttribute(Constants.BUNDLE_ACTIVATOR);
								if (ba != null) {
									try {
										Class c = getClassLoader().loadClass(ba.trim());
										bactivator = (BundleActivator)c.newInstance();
										bactivator.start(bundleContext);
									} catch (Throwable t) {
										throw new BundleException("Bundle.start: BundleActivator start failed", t);
									}
								}
								state = ACTIVE;
								framework.bundles.save();
								return null;
							}
						});
				} catch (PrivilegedActionException e) {
					removeBundleResources();
					bundleContext.invalidate();
					bundleContext = null;
					state = RESOLVED;
					throw (BundleException) e.getException();
				}
				framework.listeners.bundleChanged(new BundleEvent(BundleEvent.STARTED, this));
			}
			
			// The bundle has been correctly started. It must be marked as persitently started 
			isPersistentlyStarted = true;
			framework.bundles.save();
			break;
			
		case ACTIVE:
			return;
		case STARTING:
			// This happens if we call start from inside the BundleActivator.start method.
			// We don't allow it.
			throw new BundleException("Bundle.start called from BundleActivator.start");
		case STOPPING:
			// This happens if we call start from inside the BundleActivator.stop method.
			// We don't allow it.
			throw new BundleException("Bundle.start called from BundleActivator.stop");
		case UNINSTALLED:
			throw new IllegalStateException("Bundle.start: Bundle is in UNINSTALLED state");
		}
	}
	
	
	/**
	 * Stop this bundle.
	 *
	 * @see org.osgi.framework.Bundle#stop
	 */
	synchronized public void stop() throws BundleException
	{
		framework.checkAdminPermission();
		switch (state) {
		case INSTALLED:
		case RESOLVED:
			isPersistentlyStarted = false;
			framework.bundles.save();
			break;
		case ACTIVE:
			state = STOPPING;
			
			isPersistentlyStarted = true;
			framework.bundles.save();
			
			Exception savedException =
				(Exception) AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						Exception res = null;
						if (bactivator != null) {
							try {
								bactivator.stop(bundleContext);
							} catch (Exception e) {
								res = e;
							}
							bundleContext.invalidate();
							bundleContext = null;
							bactivator = null;
						}
						
						removeBundleResources();
						state = RESOLVED;
						framework.bundles.save();
						return res;
					}
				});
			
			framework.listeners.bundleChanged(new BundleEvent(BundleEvent.STOPPED, this));
			
			if (savedException != null) {
				throw new BundleException("Bundle.stop: BundleActivator stop failed",
					savedException);
			}
			break;
		case STARTING:
			// This happens if we call stop from inside the BundleActivator.start method.
			// We don't allow it.
			throw new BundleException("Bundle.start called from BundleActivator.stop");
		case STOPPING:
			// This happens if we call stop from inside the BundleActivator.stop method.
			// We don't allow it.
			throw new BundleException("Bundle.stop called from BundleActivator.stop");
		case UNINSTALLED:
			throw new IllegalStateException("Bundle.stop: Bundle is in UNINSTALLED state");
		}
	}
	
	
	/**
	 * Update this bundle.
	 *
	 * @see org.osgi.framework.Bundle#update
	 */
	public void update() throws BundleException
	{
		update(null);
	}
	
	
	/**
	 * Update this bundle.
	 *
	 * @see org.osgi.framework.Bundle#update
	 */
	synchronized public void update(final InputStream in) throws BundleException
	{
		try {
			framework.checkAdminPermission();
			final boolean wasActive = state == ACTIVE;
			switch (getUpdatedState()) {
			case ACTIVE:
				stop();
				// Fall through
			case INSTALLED:
			case RESOLVED:
				// Load new bundle
				try {
					final BundleImpl thisBundle = this;
					AccessController.doPrivileged(new PrivilegedExceptionAction() {
							public Object run() throws BundleException {
								Archive newArchive = null;
								HeaderDictionary newHeaders;
								List newNativeLibs;
								try {
									// New bundle as stream supplied?
									InputStream bin;  
                  String update = "[Unknown]";
									if (in == null) {
										// Try Bundle-UpdateLocation
										update = archive.getAttribute(Constants.BUNDLE_UPDATELOCATION);
										if (update == null) {
											// Take original location
											update = location;
										}
										bin = (new URL(update)).openConnection().getInputStream();
									} else {
                    update = "[Update from an InputStream]";
										bin = in;
									}
									newArchive = new Archive(archive, bin);
									newHeaders = archive.getHeaderDictionary();
                  String requiredEEs = (String)newHeaders.get(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT);
                  verifyRequiredEEs(requiredEEs, update);
									newNativeLibs = getNativeCode(newArchive);
								} catch (Exception e) {
									if (newArchive != null) {
										newArchive.purge();
									}
									if (wasActive) {
										try {
											start();
										} catch (BundleException be) {
											framework.listeners.frameworkError(thisBundle, be);
										}
									}
									throw new BundleException("Failed to get update bundle", e);
								}
								
								// Remove this bundles packages
								boolean allRemoved = bpkgs.unregisterPackages(false);
								
								// Loose old bundle if no exporting packages left
								if (classLoader != null) {
									if (allRemoved) {
										classLoader.purge();
									} else {
										saveZombiePackages();
									}
									classLoader = null;
								}
								
								// Activate new bundle
								state = INSTALLED;
								Archive oldArchive = archive;
								archive = newArchive;
								headers = newHeaders;
								nativeLibs = newNativeLibs;
								initPackages();
								
								// Purge old archive
								if (allRemoved) {
									oldArchive.purge();
								}
								
								// Broadcast updated event
								framework.listeners.bundleChanged(new BundleEvent(BundleEvent.UPDATED, thisBundle));
								
								// Restart bundle if previously stopped in the operation
								if (wasActive) {
									try {
										thisBundle.start();
									} catch (BundleException be) {
										framework.listeners.frameworkError(thisBundle, be);
									}
								}
								return null;
							}
						});
				} catch (PrivilegedActionException e) {
					throw (BundleException) e.getException();
				}
				break;
			case STARTING:
				// Wait for RUNNING state, this doesn't happen now
				// since we are synchronized.
				throw new IllegalStateException("Bundle.update: Bundle is in STARTING state");
			case STOPPING:
				// Wait for RESOLVED state, this doesn't happen now
				// since we are synchronized.
				throw new IllegalStateException("Bundle.update: Bundle is in STOPPING state");
			case UNINSTALLED:
				throw new IllegalStateException("Bundle.update: Bundle is in UNINSTALLED state");
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignore) {}
			}
		}
	}
	
	
	/**
	 * Uninstall this bundle.
	 *
	 * @see org.osgi.framework.Bundle#uninstall
	 */
	synchronized public void uninstall() throws BundleException
	{
		framework.checkAdminPermission();
		switch (state) {
		case ACTIVE:
			try {
				stop();
			} catch (BundleException be) {
				framework.listeners.frameworkError(this, be);
			}
			// Fall through
		case INSTALLED:
		case RESOLVED:
			framework.bundles.remove(location);
			if (bpkgs.unregisterPackages(false)) {
				if (classLoader != null) {
					AccessController.doPrivileged(new PrivilegedAction() {
							public Object run() {
								classLoader.purge();
								classLoader = null;
								return null;
							}
						});
				}
				archive.close();
			} else {
				saveZombiePackages();
				classLoader = null;
			}
			archive = null;
			bpkgs = null;
			bactivator = null;
			AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						framework.bundles.save();
						if (bundleDir != null) {
							bundleDir.delete();
							bundleDir = null;
						}
						return null;
					}
				});
			
			// id, location and headers survices after uninstall.
			state = UNINSTALLED;
			framework.listeners.bundleChanged(new BundleEvent(BundleEvent.UNINSTALLED, this));
			break;
		case STARTING:
			// Wait for RUNNING state, this doesn't happen now
			// since we are synchronized.
			throw new IllegalStateException("Bundle.uninstall: Bundle is in STARTING state");
		case STOPPING:
			// Wait for RESOLVED state, this doesn't happen now
			// since we are synchronized.
			throw new IllegalStateException("Bundle.uninstall: Bundle is in STOPPING state");
		case UNINSTALLED:
			throw new IllegalStateException("Bundle.uninstall: Bundle is in UNINSTALLED state");
		}
	}
	
	
	/**
	 * Get header data. This is all entries in the bundles
	 * MANIFEST file.
	 *
	 * @see org.osgi.framework.Bundle#getHeaders
	 */
	synchronized public Dictionary getHeaders()
	{
		framework.checkAdminPermission();
		if (headers != null) {
			return new HeaderDictionary(headers);
		} else {
			return null;
		}
	}
	
	
	/**
	 * Get bundle identifier.
	 *
	 * @see org.osgi.framework.Bundle#getBundleId
	 */
	public long getBundleId()
	{
		return id;
	}
	
	
	/**
	 * Get bundle location.
	 *
	 * @see org.osgi.framework.Bundle#getLocation
	 */
	public String getLocation()
	{
		framework.checkAdminPermission();
		return location;
	}
	
	
	/**
	 * Get services that this bundle has registrated.
	 *
	 * @see org.osgi.framework.Bundle#getRegisteredServices
	 */
	public ServiceReference[] getRegisteredServices()
	{
		Set sr = framework.services.getRegisteredByBundle(this);
		if (framework.checkPermissions) {
			filterGetServicePermission(sr);
		}
		ServiceReference[] res = new ServiceReference[sr.size()];
		int pos = 0;
		for (Iterator i = sr.iterator(); i.hasNext(); ) {
			res[pos++] = ((ServiceRegistration)i.next()).getReference();
		}
		return res;
	}
	
	
	/**
	 * Get services that this bundle uses.
	 *
	 * @see org.osgi.framework.Bundle#getServicesInUse
	 */
	public ServiceReference[] getServicesInUse()
	{
		Set sr = framework.services.getUsedByBundle(this);
		if (framework.checkPermissions) {
			filterGetServicePermission(sr);
		}
		ServiceReference[] res = new ServiceReference[sr.size()];
		int pos = 0;
		for (Iterator i = sr.iterator(); i.hasNext(); ) {
			res[pos++] = ((ServiceRegistration)i.next()).getReference();
		}
		return res;
	}
	
	
	/**
	 * Determine whether the bundle has the requested permission.
	 *
	 * @see org.osgi.framework.Bundle#hasPermission
	 */
	public boolean hasPermission(Object permission)
	{
		if (permission instanceof Permission) {
			if (framework.checkPermissions) {
				PermissionCollection pc = protectionDomain.getPermissions();
				if (pc != null) {
					return pc.implies((Permission)permission);
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	
	/**
	 * Find the specified resource in this bundle.
	 *
	 * This bundle's <tt>ClassLoader</tt> is called to search for the named resource.
	 *
	 * @param name The name of the resource.
	 * See <tt>java.lang.ClassLoader.getResource</tt> for a description of
	 * the format of a resource name.
	 * @return a URL to the named resource, or <tt>null</tt> if the resource could
	 * not be found or if the caller does not have
	 * the <tt>AdminPermission</tt>, and the Java Runtime Environment supports permissions.
	 *
	 * @since 1.1
	 */
	synchronized public URL getResource(final String name)
	{
		if (state == UNINSTALLED) {
			throw new IllegalStateException("Bundle.getResource: Bundle is in UNINSTALLED state");
		}
		try {
			framework.checkAdminPermission();
			return (URL) AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						ClassLoader cl = getClassLoader();
						if (cl != null) {
							return cl.getResource(name);
						} else {
							return null;
						}
					}
				});
		} catch (SecurityException ignore) { }
		return null;
	}
	
	//
	// Comparable interface
	//
	
	/**
	 * Compare to bundles when comes to start order.
	 * This is only used for sorting bundles in start order
	 * and is inconsistent with the equals method.
	 *
	 * @param other BundleImpl to compare to.
	 */
	public int compareTo(Object other)
	{
		if (startLevel == ((BundleImpl)other).startLevel)
			return (int) (id - ((BundleImpl)other).id);
		return (int) (startLevel - ((BundleImpl)other).startLevel);
	}
	
	//
	// Package methods
	//
	
	/**
	 * Set persistent state.
	 *
	 * @param isPersistentlyStarted Set to true if this bundle should
	 *        be marked as persistently started.
	 */
	synchronized void setPersistentlyStarted(boolean isPersistentlyStarted)
	{
		this.isPersistentlyStarted = isPersistentlyStarted;
		framework.bundles.save();
	}
	
	/**
	 * Get updated bundle state. That means check if an installed
	 * bundle has been resolved.
	 *
	 * @return Bundles state
	 */
	synchronized int getUpdatedState() {
		if (state == INSTALLED && bpkgs.resolvePackages()) {
			state = RESOLVED;
		}
		return state;
	}
	
	
	/**
	 * Get root for persistent storage area for this bundle.
	 *
	 * @return A FileTree representing the data root.
	 */
	FileTree getDataRoot()
	{
		return new FileTree(bundleDir, "data");
	}
	
	
	/**
	 * Get class loader for this bundle.
	 * Create the classloader if we haven't done this previously.
	 */
	ClassLoader getClassLoader()
	{
		if (classLoader == null) {
			synchronized (this) {
				if (classLoader == null) {
					classLoader = (BundleClassLoader)
						AccessController.doPrivileged(new PrivilegedAction() {
								public Object run() {
									return initClassLoader();
								}
							});
				}
			}
		}
		return classLoader;
	}
	
	
	/**
	 * We have lost one of our imported packages. Set state to INSTALLED
	 * and throw away our tainted classloader.
	 */
	synchronized void setStateInstalled()
	{
		if (classLoader != null) {
			classLoader.close();
			classLoader = null;
		}
		bpkgs.unregisterPackages(true);
		bpkgs.registerPackages();
		state = INSTALLED;
	}
	
	
	/**
	 * Get a Classloader object that we export from our bundle.
	 *
	 * @param pkg Name of java package to get from.
	 * @return Class object for specfied class, null if no classloader.
	 */
	BundleClassLoader getExporterClassLoader(String pkg)
	{
		BundleClassLoader cl;
		if (oldClassLoaders != null) {
			cl = (BundleClassLoader)oldClassLoaders.get(pkg);
			if (cl != null) {
				return cl;
			}
		}
		return (BundleClassLoader)getClassLoader();
	}
	
	
	/**
	 * Added our properties to a Propeties object so that we
	 * can recreate this Bundle when we restart framework.
	 *
	 * @param p Where to store our propeties.
	 */
	void toProperties(Properties p)
	{
		String pbase = BUNDLE_BASE + id + ".";
		p.put(pbase + "location", location);
		p.put(pbase + "startLevel", Integer.toString(startLevel));
		p.put(pbase + "isPersistentlyStarted", String.valueOf(isPersistentlyStarted));
	}
	
	
	/**
	 * Create an URL representing this bundle and an optional resource within.
	 *
	 * @return A magic bundle URL.
	 */
	URL getBundleURL(int archiveIndex, String name) {
		try {
			return new URL(BundleURLStreamHandler.PROTOCOL,
				Long.toString(id),
				archiveIndex,
				name,
				new BundleURLStreamHandler(this));
		} catch (MalformedURLException never) {
			return null;
		}
	}
	
	
	/**
	 * Purge any old files associated with this bundle.
	 *
	 */
	void purge() {
		if (state == UNINSTALLED) {
			framework.bundles.remove(location);
		}
		if (oldClassLoaders != null) {
			for (Iterator i = oldClassLoaders.values().iterator(); i.hasNext();) {
				((BundleClassLoader)i.next()).purge();
			}
		}
		oldClassLoaders = null;
	}
	
	/**
	 * Get exported packages.
	 *
	 * @return Iterator of all exported packages as PkgEntry.
	 */
	Iterator getExports() {
		if (oldClassLoaders != null) {
			HashSet res = new HashSet();
			for (Iterator i = oldClassLoaders.values().iterator(); i.hasNext();) {
				for (Iterator j = ((BundleClassLoader)i.next()).getBpkgs().getExports(); j.hasNext();) {
					res.add(j.next());
				}
			}
			if (bpkgs != null) {
				for (Iterator i = bpkgs.getExports(); i.hasNext();) {
					res.add(i.next());
				}
			}
			return res.iterator();
		} else if (bpkgs != null) {
			return bpkgs.getExports();
		} else {
			return (new ArrayList(0)).iterator();
		}
	}
	
	
	/**
	 * Get imported packages.
	 *
	 * @return Iterator of all imported packages as PkgEntry.
	 */
	Iterator getImports() {
		if (oldClassLoaders != null) {
			HashSet res = new HashSet();
			for (Iterator i = oldClassLoaders.values().iterator(); i.hasNext();) {
				for (Iterator j = ((BundleClassLoader)i.next()).getBpkgs().getImports(); j.hasNext();) {
					res.add(j.next());
				}
			}
			if (bpkgs != null) {
				for (Iterator i = bpkgs.getImports(); i.hasNext();) {
					res.add(i.next());
				}
			}
			return res.iterator();
		} else if (bpkgs != null) {
			return bpkgs.getImports();
		} else {
			return (new ArrayList(0)).iterator();
		}
	}
	
	//
	// Private methods
	//
	
	/**
	 * Filter out all services that we don't have permission to get.
	 *
	 * @param srs Set of ServiceRegistrationImpls to check.
	 */
	private void filterGetServicePermission(Set srs)
	{ 
		AccessControlContext acc = AccessController.getContext();
		for (Iterator i = srs.iterator(); i.hasNext();) {
			ServiceRegistrationImpl sr = (ServiceRegistrationImpl)i.next();
			String[] classes = (String[])sr.properties.get(Constants.OBJECTCLASS);
			boolean perm = false;
			for (int n = 0; n < classes.length; n++) {
				try { 
					acc.checkPermission(new ServicePermission(classes[n], ServicePermission.GET));
					perm = true;
					break;
				} catch (AccessControlException ignore) { }
			}
			if (!perm) {
				i.remove();
			}
		}
	}
	
	
	/**
	 * Check bundle manifest for native code, package import and export.
	 *
	 * @exception SecurityException If we don't have permission to import and export
	 *            bundle packages.
	 * @exception Exception If can not find an entry that match this JVM.
	 */
	private void initPackages() {
		bpkgs = new BundlePackages(this,
			archive.getAttribute(Constants.EXPORT_PACKAGE),
			archive.getAttribute(Constants.IMPORT_PACKAGE),
			archive.getAttribute(Constants.DYNAMICIMPORT_PACKAGE));
		bpkgs.registerPackages();
	}
	
	
	/**
	 * Check for native code libraries.
	 *
	 * @param bnc Is the Bundle-NativeCode string.
	 * @return A List of Strings with pathname to native code libraries or
	 *         null if input was null.
	 * @exception IllegalArgumentException If syntax error in input string.
	 * @exception BundleException If can not find an entry that match this JVM.
	 */
	private List getNativeCode(Archive a) throws BundleException
	{
		String bnc = a.getAttribute(Constants.BUNDLE_NATIVECODE);
		if (bnc != null) {
			Map best = null;
			VersionNumber bestVer = null;
			String bestLang = null;
			List perfectVer = new ArrayList();
			List okVer = new ArrayList();
			List noVer = new ArrayList();
			String p = Framework.getProperty(Constants.FRAMEWORK_PROCESSOR);
			String o =  Framework.getProperty(Constants.FRAMEWORK_OS_NAME);
			String fosLang = Framework.getProperty(Constants.FRAMEWORK_LANGUAGE);
			VersionNumber fosVer = new VersionNumber(Framework.getProperty(Constants.FRAMEWORK_OS_VERSION));
			for (Iterator i = Framework.parseEntries(bnc, false); i.hasNext(); ) {
				Map params = (Map)i.next();

				List pl = (List)params.get(Constants.BUNDLE_NATIVECODE_PROCESSOR);
				List ol = (List)params.get(Constants.BUNDLE_NATIVECODE_OSNAME);
				if ((containsIgnoreCase(pl, p) ||
					containsIgnoreCase(pl, Alias.unifyProcessor(p))) &&
					(containsIgnoreCase(ol, o) ||
					containsIgnoreCase(ol, Alias.unifyOsName(o)))) {
					List lang = (List)params.get(Constants.BUNDLE_NATIVECODE_LANGUAGE);
					String thisLang = null;
					if (lang != null) {
						for (Iterator l = lang.iterator(); l.hasNext(); ) {
							String nlang = (String)l.next();
							if (fosLang.equalsIgnoreCase(nlang)) {
								thisLang = nlang;
								break;
							}
						}
						if (thisLang == null) {
							continue;
						}
					}
				        
					List ver = (List)params.get(Constants.BUNDLE_NATIVECODE_OSVERSION);
					VersionNumber thisVer = null;
					if (ver != null) {
						for (Iterator v = ver.iterator(); v.hasNext(); ) {
							VersionNumber nov = new VersionNumber((String)v.next());
							int cmp = nov.compareTo(fosVer);
							if (cmp == 0) {
								thisVer = nov;
								break;
							}
							if (cmp < 0 && (thisVer == null ||
									thisVer.compareTo(nov) < 0)) {
								thisVer = nov;
							}
						}
						if (thisVer == null) {
							continue;
						}
					}
					// We found a valid entry is it a better match
                                        // than the current selected?
					if ((best == null ||
					     (thisVer != null && (bestVer == null ||
								  bestVer.compareTo(thisVer) < 0))) ||
					    (((bestVer != null && bestVer.compareTo(thisVer) == 0) ||
					      bestVer == thisVer) &&
					     bestLang == null && thisLang != null)) {
						best = params;
						bestVer = thisVer;
						bestLang = thisLang;
					}
				}
			}
			if (best == null) {
				throw new BundleException("Native-Code: No matching libraries found.");
			}
			List res = (List)best.get("keys");
			for (Iterator i = res.iterator(); i.hasNext(); ) {
				String s = (String)i.next();
				BundleException e;
				try {
					if (a.getInputStream(s) != null) {
						continue;
					}
					e = new BundleException("Native-Code: File not found in bundle: " + s);
				} catch (IOException ie) {
					e = new BundleException("Native-Code: Corrupt bundle!", ie);
				}
				framework.listeners.frameworkError(this, e);
				throw e;
			}
			return res;
		} else {
			// No native code in this bundle
			return null;
		}
	}
	
	
	/**
	 * Check if a string exists in a list. Ignore case when comparing.
	 */
	private boolean containsIgnoreCase(List l, String s)
	{
		for (Iterator i = l.iterator(); i.hasNext(); ) {
			if (s.equalsIgnoreCase((String)i.next())) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Remove a bundles all registered listeners, registered services and
	 * used services.
	 */
	private void removeBundleResources()
	{ 
		framework.listeners.removeAllListeners(this);
		Set srs = framework.services.getRegisteredByBundle(this);
		for (Iterator i = srs.iterator(); i.hasNext();) {
			((ServiceRegistration)i.next()).unregister();
		}
		Set s = framework.services.getUsedByBundle(this);
		for (Iterator i = s.iterator(); i.hasNext(); ) {
			((ServiceRegistrationImpl) i.next()).reference.ungetService(this, false);
		}
	}
	
	
	/**
	 * Create a class loader, that is used to load any class from within
	 * the bundles JAR file. If Bundle-ClassPath is present in the Manifest,
	 *  it is used to describe the search order for classes. Path can be either
	 * dot ('.') which represents the bundle's JAR file or it can be the path
	 * of a JAR file contained in the bundle's JAR file. Default value is dot. 
	 *
	 * @return A new class loader for this bundle.
	 */
	private BundleClassLoader initClassLoader()
	{
		BundleClassLoader cl = new BundleClassLoader(bpkgs);
		String bcp = archive.getAttribute(Constants.BUNDLE_CLASSPATH);
		if (bcp != null) {
			StringTokenizer st = new StringTokenizer(bcp, ",");
			while (st.hasMoreTokens()) {
				String path = st.nextToken().trim();
				if (".".equals(path)) {
					cl.addClassArchive(archive);
				} else {
					try {
						cl.addClassArchive(archive.getSubArchive(path));
					} catch (IOException e) {
						framework.listeners.frameworkError(this, e);
					}
				}
			}
		} else {
			cl.addClassArchive(archive);
		}
		
		// Check for native code
		if (nativeLibs != null) {
			for (Iterator p = nativeLibs.iterator(); p.hasNext();) {
				String path = (String)p.next();
				String val = null;
				try {
					val = archive.getNativeLibrary(path);
				} catch (IOException e) {
					framework.listeners.frameworkError(this, e);
				}
				if (val != null) {
					int sp = path.lastIndexOf('/');
					String key = (sp != -1) ? path.substring(sp+1) : path;
					cl.addNativeLibrary(key, val);
				}
			}
		}
		return cl;
	}
	
	/**
	 * Save classloader for active package exports.
	 *
	 */
	private void saveZombiePackages()
	{
		if (oldClassLoaders == null) {
			oldClassLoaders = new HashMap();
		}
		Iterator i = bpkgs.getExports();
		while (i.hasNext()) {
			PkgEntry pkg = (PkgEntry)i.next();
			oldClassLoaders.put(pkg.name, getClassLoader());
		}
	}

  static void verifyRequiredEEs(String requiredEEs, String loc) throws BundleException {
    if(requiredEEs == null || "".equals(requiredEEs)) return;
    String availableEEs = Framework.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT);
    if(availableEEs == null) availableEEs = "";
    StringTokenizer available = null;
    StringTokenizer required = new StringTokenizer(requiredEEs, ",");
    while(required.hasMoreTokens()) {
      String r = required.nextToken();
      available = new StringTokenizer(availableEEs, ",");
      while(available.hasMoreTokens()) {
        String a = available.nextToken().toLowerCase();
        // We just need one match, so return as soon as we find it
        if(a.equals(r.toLowerCase())) return;
      }
    }
    String message =  "No match for any required excecution environment. " +
                      "Required: " + requiredEEs +
                      "Available: " + availableEEs + 
                      "Bundle: " + loc;
    System.out.println(message);
    throw new BundleException(message);
  }

/* (non-Javadoc)
 * @see org.osgi.framework.Bundle#getHeaders(java.lang.String)
 */
public Dictionary getHeaders(String localeString) {
	// TODO Auto-generated method stub
	return null;
}

/* (non-Javadoc)
 * @see org.osgi.framework.Bundle#getSymbolicName()
 */
public String getSymbolicName() {
	// TODO Auto-generated method stub
	return null;
}

/* (non-Javadoc)
 * @see org.osgi.framework.Bundle#loadClass(java.lang.String)
 */
public Class loadClass(String name) throws ClassNotFoundException {
	// TODO Auto-generated method stub
	return null;
}

/* (non-Javadoc)
 * @see org.osgi.framework.Bundle#getEntryPaths(java.lang.String)
 */
public Enumeration getEntryPaths(String path) {
	// TODO Auto-generated method stub
	return null;
}

/* (non-Javadoc)
 * @see org.osgi.framework.Bundle#getEntry(java.lang.String)
 */
public URL getEntry(String name) {
	// TODO Auto-generated method stub
	return null;
}
}
