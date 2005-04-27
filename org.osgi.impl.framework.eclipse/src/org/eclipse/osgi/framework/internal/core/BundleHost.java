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

package org.eclipse.osgi.framework.internal.core;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.adaptor.BundleWatcher;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.*;

public class BundleHost extends AbstractBundle {

	/** 
	 * The BundleLoader proxy; a lightweight object that acts as a proxy
	 * to the BundleLoader and allows lazy creation of the BundleLoader object
	 */
	private BundleLoaderProxy proxy;

	/** The BundleContext that represents this Bundle and all of its fragments */
	protected BundleContextImpl context;

	/** The List of BundleFragments */
	protected BundleFragment[] fragments;

	public BundleHost(BundleData bundledata, Framework framework) throws BundleException {
		super(bundledata, framework);
		context = null;
		fragments = null;
	}

	/**
	 * Load the bundle.
	 */
	protected void load() {
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			if ((state & (INSTALLED)) == 0) {
				Debug.println("Bundle.load called when state != INSTALLED: " + this); //$NON-NLS-1$
				Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
			}
			if (proxy != null) {
				Debug.println("Bundle.load called when proxy != null: " + this); //$NON-NLS-1$
				Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
			}
		}

		if (framework.isActive()) {
			SecurityManager sm = System.getSecurityManager();

			if (sm != null && framework.permissionAdmin != null) {
				domain = framework.permissionAdmin.createProtectionDomain(this);
			}

		}
		proxy = null;
	}

	/**
	 * Reload from a new bundle.
	 * This method must be called while holding the bundles lock.
	 *
	 * @param newBundle Dummy Bundle which contains new data.
	 * @return  true if an exported package is "in use". i.e. it has been imported by a bundle
	 */
	protected boolean reload(AbstractBundle newBundle) {
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			if ((state & (INSTALLED | RESOLVED)) == 0) {
				Debug.println("Bundle.reload called when state != INSTALLED | RESOLVED: " + this); //$NON-NLS-1$
				Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
			}
		}

		boolean exporting = false;

		if (framework.isActive()) {
			if (state == RESOLVED) {
				BundleLoaderProxy curProxy = getLoaderProxy();
				exporting = curProxy.inUse();
				if (exporting)
					// make sure the BundleLoader is created.
					curProxy.getBundleLoader().createClassLoader();
				else
					closeBundleLoader(proxy);
				state = INSTALLED;
				proxy = null;
				fragments = null;
			}

		} else {
			/* close the outgoing jarfile */
			try {
				this.bundledata.close();
			} catch (IOException e) {
				// Do Nothing
			}
		}
		this.bundledata = newBundle.bundledata;
		this.bundledata.setBundle(this);
		// create a new domain for the bundle because its signers/symbolic-name may have changed
		if (framework.isActive() && System.getSecurityManager() != null && framework.permissionAdmin != null)
			domain = framework.permissionAdmin.createProtectionDomain(this);
		return (exporting);
	}

	/**
	 * Refresh the bundle. This is called by Framework.refreshPackages.
	 * This method must be called while holding the bundles lock.
	 */
	protected void refresh() {
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			if ((state & (UNINSTALLED | INSTALLED | RESOLVED)) == 0) {
				Debug.println("Bundle.reload called when state != UNINSTALLED | INSTALLED | RESOLVED: " + this); //$NON-NLS-1$
				Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
			}
		}
		if (state == RESOLVED) {
			closeBundleLoader(proxy);
			proxy = null;
			fragments = null;
			state = INSTALLED;
			// Do not publish UNRESOLVED event here.  This is done by caller 
			// to resolve if appropriate.
		}
		manifestLocalization = null;
	}

	/**
	 * Unload the bundle.
	 * This method must be called while holding the bundles lock.
	 *
	 * @return  true if an exported package is "in use". i.e. it has been imported by a bundle
	 */
	protected boolean unload() {
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			if ((state & (UNINSTALLED | INSTALLED | RESOLVED)) == 0) {
				Debug.println("Bundle.unload called when state != UNINSTALLED | INSTALLED | RESOLVED: " + this); //$NON-NLS-1$
				Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
			}
		}

		boolean exporting = false;

		if (framework.isActive()) {
			if (state == RESOLVED) {
				BundleLoaderProxy curProxy = getLoaderProxy();
				exporting = curProxy.inUse();
				if (exporting)
					// make sure the BundleLoader is created.
					curProxy.getBundleLoader().createClassLoader();
				else
					closeBundleLoader(proxy);

				state = INSTALLED;
				proxy = null;
				fragments = null;
				domain = null;
			}
		}
		if (!exporting){
			try {
				this.bundledata.close();
			} catch (IOException e) { // Do Nothing.
			}
		}

		return (exporting);
	}

	private BundleLoader checkLoader() {
		checkValid();

		// check to see if the bundle is resolved
		if (!isResolved()) {
			if (!framework.packageAdmin.resolveBundles(new Bundle[] {this})) {
				return null;
			}
		}
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			if ((state & (STARTING | ACTIVE | STOPPING | RESOLVED)) == 0) {
				Debug.println("Bundle.checkLoader() called when state != STARTING | ACTIVE | STOPPING | RESOLVED: " + this); //$NON-NLS-1$ //$NON-NLS-2$
				Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
			}
		}

		BundleLoader loader = getBundleLoader();
		if (loader == null) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Bundle.checkLoader() called when loader == null: " + this); //$NON-NLS-1$ //$NON-NLS-2$
				Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
			}
			return null;
		}
		return loader;
	}

	/**
	 * This method loads a class from the bundle.
	 *
	 * @param      name     the name of the desired Class.
	 * @param      checkPermission indicates whether a permission check should be done.
	 * @return     the resulting Class
	 * @exception  java.lang.ClassNotFoundException  if the class definition was not found.
	 */
	protected Class loadClass(String name, boolean checkPermission) throws ClassNotFoundException {
		if (checkPermission) {
			try {
				framework.checkAdminPermission(this, AdminPermission.CLASS);
			} catch (SecurityException e) {
				throw new ClassNotFoundException();
			}
		}
		BundleLoader loader = checkLoader();
		if (loader == null)
			throw new ClassNotFoundException(NLS.bind(Msg.BUNDLE_CNFE_NOT_RESOLVED, getLocation(), name)); //$NON-NLS-1$
		return (loader.loadClass(name));
	}

	/**
	 * Find the specified resource in this bundle.
	 *
	 * This bundle's class loader is called to search for the named resource.
	 * If this bundle's state is <tt>INSTALLED</tt>, then only this bundle will
	 * be searched for the specified resource. Imported packages cannot be searched
	 * when a bundle has not been resolved.
	 *
	 * @param name The name of the resource.
	 * See <tt>java.lang.ClassLoader.getResource</tt> for a description of
	 * the format of a resource name.
	 * @return a URL to the named resource, or <tt>null</tt> if the resource could
	 * not be found or if the caller does not have
	 * the <tt>AdminPermission</tt>, and the Java Runtime Environment supports permissions.
	 * 
	 * @exception java.lang.IllegalStateException If this bundle has been uninstalled.
	 */
	public URL getResource(String name) {
		BundleLoader loader = null;
		try {
			checkResourcePermission();
		} catch (SecurityException e) {
			try {
				framework.checkAdminPermission(this, AdminPermission.RESOURCE);
			} catch (SecurityException ee) {
				return null;
			}
		}
		loader = checkLoader();
		if (loader == null)
			return null;
		return (loader.getResource(name));
	}

	public Enumeration getResources(String name) throws IOException {
		BundleLoader loader = null;
		try {
			checkResourcePermission();
		} catch (SecurityException e) {
			try {
				framework.checkAdminPermission(this, AdminPermission.RESOURCE);
			} catch (SecurityException ee) {
				return null;
			}
		}
		loader = checkLoader();
		if (loader == null)
			return null;
		return loader.getResources(name);
	}

	/**
	 * Internal worker to start a bundle.
	 *
	 * @param persistent if true persistently record the bundle was started.
	 */
	protected void startWorker(boolean persistent) throws BundleException {
		long start = 0;
		if (framework.active) {
			if ((state & (STARTING | ACTIVE)) != 0) {
				return;
			}

			try {
				if (state == INSTALLED) {
					if (!framework.packageAdmin.resolveBundles(new Bundle[] {this})) {
						throw new BundleException(getResolutionFailureMessage());
					}
				}

				if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
					Debug.println("Bundle: Active sl = " + framework.startLevelManager.getStartLevel() + "; Bundle " + getBundleId() + " sl = " + getStartLevel()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}

				if (getStartLevel() <= framework.startLevelManager.getStartLevel()) {
					//STARTUP TIMING Start here					
					if (Debug.DEBUG) {
						if (Debug.MONITOR_ACTIVATION) {
							BundleWatcher bundleStats = framework.adaptor.getBundleWatcher();
							if (bundleStats != null)
								bundleStats.startActivation(this);
						}
						if (Debug.DEBUG_BUNDLE_TIME) {
							start = System.currentTimeMillis();
							System.out.println("Starting " + getSymbolicName()); //$NON-NLS-1$
						}
					}
					state = STARTING;

					context = createContext();
					try {
						context.start();

						if (framework.active) {
							state = ACTIVE;

							if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
								Debug.println("->started " + this); //$NON-NLS-1$
							}

							framework.publishBundleEvent(BundleEvent.STARTED, this);
						}

					} catch (BundleException e) {
						context.close();
						context = null;

						state = RESOLVED;

						throw e;
					}

					if (state == UNINSTALLED) {
						context.close();
						context = null;
						throw new BundleException(NLS.bind(Msg.BUNDLE_UNINSTALLED_EXCEPTION, getLocation())); //$NON-NLS-1$
					}
				}
			} finally {
				if (Debug.DEBUG && state == ACTIVE) {
					if (Debug.MONITOR_ACTIVATION) {
						BundleWatcher bundleStats = framework.adaptor.getBundleWatcher();
						if (bundleStats != null)
							bundleStats.endActivation(this);
					}
					if (Debug.DEBUG_BUNDLE_TIME)
						System.out.println("End starting " + getSymbolicName() + " " + (System.currentTimeMillis() - start)); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}

		if (persistent) {
			setStatus(Constants.BUNDLE_STARTED, true);
		}
	}

	/**
	 * Create a BundleContext for this bundle.
	 *
	 * @return BundleContext for this bundle.
	 */
	protected BundleContextImpl createContext() {
		return (new BundleContextImpl(this));
	}

	/**
	 * Return the current context for this bundle.
	 *
	 * @return BundleContext for this bundle.
	 */
	protected BundleContextImpl getContext() {
		return (context);
	}

	/**
	 * Internal worker to stop a bundle.
	 *
	 * @param persistent if true persistently record the bundle was stopped.
	 */
	protected void stopWorker(boolean persistent) throws BundleException {
		if (persistent) {
			setStatus(Constants.BUNDLE_STARTED, false);
		}

		if (framework.active) {
			if ((state & (STOPPING | RESOLVED | INSTALLED)) != 0) {
				return;
			}

			state = STOPPING;

			try {
				context.stop();
			} finally {
				context.close();
				context = null;

				checkValid();

				state = RESOLVED;

				if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
					Debug.println("->stopped " + this); //$NON-NLS-1$
				}

				framework.publishBundleEvent(BundleEvent.STOPPED, this);
			}
		}
	}

	/**
	 * Provides a list of {@link ServiceReferenceImpl}s for the services
	 * registered by this bundle
	 * or <code>null</code> if the bundle has no registered
	 * services.
	 *
	 * <p>The list is valid at the time
	 * of the call to this method, but the framework is a very dynamic
	 * environment and services can be modified or unregistered at anytime.
	 *
	 * @return An array of {@link ServiceReferenceImpl} or <code>null</code>.
	 * @exception java.lang.IllegalStateException If the
	 * bundle has been uninstalled.
	 * @see ServiceRegistrationImpl
	 * @see ServiceReferenceImpl
	 */
	public org.osgi.framework.ServiceReference[] getRegisteredServices() {
		checkValid();

		if (context == null) {
			return (null);
		}

		return (context.getRegisteredServices());
	}

	/**
	 * Provides a list of {@link ServiceReferenceImpl}s for the
	 * services this bundle is using,
	 * or <code>null</code> if the bundle is not using any services.
	 * A bundle is considered to be using a service if the bundle's
	 * use count for the service is greater than zero.
	 *
	 * <p>The list is valid at the time
	 * of the call to this method, but the framework is a very dynamic
	 * environment and services can be modified or unregistered at anytime.
	 *
	 * @return An array of {@link ServiceReferenceImpl} or <code>null</code>.
	 * @exception java.lang.IllegalStateException If the
	 * bundle has been uninstalled.
	 * @see ServiceReferenceImpl
	 */
	public org.osgi.framework.ServiceReference[] getServicesInUse() {
		checkValid();

		if (context == null) {
			return (null);
		}

		return (context.getServicesInUse());
	}

	protected Bundle[] getFragments() {
		synchronized (framework.bundles) {
			if (fragments == null)
				return null;
			Bundle[] result = new Bundle[fragments.length];
			System.arraycopy(fragments, 0, result, 0, result.length);
			return result;
		}
	}

	/**
	 * Attaches a fragment to this BundleHost.  Fragments must be attached to
	 * the host by ID order.  If the ClassLoader of the host is already created
	 * then the fragment must be attached to the host ClassLoader
	 * @param fragment The fragment bundle to attach
	 * return true if the fragment successfully attached; false if the fragment
	 * could not be logically inserted at the end of the fragment chain.
	 */
	protected void attachFragment(BundleFragment fragment) throws BundleException {
		// do not force the creation of the bundle loader here
		BundleLoader loader = getLoaderProxy().getBasicBundleLoader();
		// If the Host ClassLoader exists then we must attach
		// the fragment to the ClassLoader.
		if (loader != null)
			loader.attachFragment(fragment);

		if (fragments == null) {
			fragments = new BundleFragment[] {fragment};
		} else {
			boolean inserted = false;
			// We must keep our fragments ordered by bundle ID; or 
			// install order.
			BundleFragment[] newFragments = new BundleFragment[fragments.length + 1];
			for (int i = 0; i < fragments.length; i++) {
				if (fragment == fragments[i])
					return; // this fragment is already attached
				if (!inserted && fragment.getBundleId() < fragments[i].getBundleId()) {
					// if the loader has already been created
					// then we cannot attach a fragment into the middle
					// of the fragment chain.
					if (loader != null) {
						throw new BundleException(NLS.bind(Msg.BUNDLE_LOADER_ATTACHMENT_ERROR, fragments[i].getSymbolicName(), getSymbolicName())); //$NON-NLS-1$
					}
					newFragments[i] = fragment;
					inserted = true;
				}
				newFragments[inserted ? i + 1 : i] = fragments[i];
			}
			if (!inserted)
				newFragments[newFragments.length - 1] = fragment;
			fragments = newFragments;
		}
	}

	protected BundleLoader getBundleLoader() {
		return getLoaderProxy().getBundleLoader();
	}

	protected BundleLoaderProxy getLoaderProxy() {
		if (proxy == null) {
			synchronized (this) {
				if (proxy == null) {
					BundleDescription bundleDescription = getBundleDescription();
					proxy = new BundleLoaderProxy(this, bundleDescription);
					bundleDescription.setUserObject(proxy);
				}
			}
		}
		return proxy;
	}

	static void closeBundleLoader(BundleLoaderProxy proxy) {
		if (proxy == null)
			return;
		// First close the BundleLoader
		BundleLoader loader = proxy.getBasicBundleLoader();
		if (loader != null)
			loader.close();
		proxy.setStale();
		// if proxy is not null then make sure to unset user object
		// associated with the proxy in the state
		BundleDescription description = proxy.getBundleDescription();
		description.setUserObject(null);
	}
}
