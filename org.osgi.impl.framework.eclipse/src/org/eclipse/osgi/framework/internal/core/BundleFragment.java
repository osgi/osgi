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
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.*;

public class BundleFragment extends AbstractBundle {

	/** The resolved host that this fragment is attached to */
	protected BundleLoaderProxy[] hosts;

	/**
	 * @param bundledata
	 * @param framework
	 * @throws BundleException
	 */
	public BundleFragment(BundleData bundledata, Framework framework) throws BundleException {
		super(bundledata, framework);
		hosts = null;
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
		}

		if (framework.isActive()) {
			SecurityManager sm = System.getSecurityManager();

			if (sm != null && framework.permissionAdmin != null) {
				domain = framework.permissionAdmin.createProtectionDomain(this);
			}
		}
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
			if (hosts != null) {
				if (state == RESOLVED) {
					exporting = true; // if we have a host we cannot be removed until the host is refreshed
					hosts = null;
					state = INSTALLED;
				}
			}
		} else {
			/* close the outgoing jarfile */
			try {
				this.bundledata.close();
			} catch (IOException e) {
				// Do Nothing
			}
		}
		if (!exporting) {
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
	 * this.loader.unimportPackages must have already been called before calling
	 * this method!
	 */
	protected void refresh() {
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			if ((state & (UNINSTALLED | INSTALLED | RESOLVED)) == 0) {
				Debug.println("Bundle.refresh called when state != UNINSTALLED | INSTALLED | RESOLVED: " + this); //$NON-NLS-1$
				Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
			}
		}

		if (state == RESOLVED) {
			hosts = null;
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
			if (hosts != null) {
				if (state == RESOLVED) {
					exporting = true; // if we have a host we cannot be removed until the host is refreshed
					hosts = null;
					state = INSTALLED;
				}
				domain = null;
			}
		}
		if (!exporting) {
			try {
				this.bundledata.close();
			} catch (IOException e) { // Do Nothing.
			}
		}

		return (exporting);
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
			checkValid();
		}
		// cannot load a class from a fragment because there is no classloader
		// associated with fragments.
		throw new ClassNotFoundException(NLS.bind(Msg.BUNDLE_FRAGMENT_CNFE, name)); //$NON-NLS-1$
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
		checkValid();
		// cannot get a resource for a fragment because there is no classloader
		// associated with fragments.
		return (null);

	}

	public Enumeration getResources(String name) {
		checkValid();
		// cannot get a resource for a fragment because there is no classloader
		// associated with fragments.
		return null;
	}

	/**
	 * Internal worker to start a bundle.
	 *
	 * @param persistent if true persistently record the bundle was started.
	 */
	protected void startWorker(boolean persistent) throws BundleException {
		throw new BundleException(NLS.bind(Msg.BUNDLE_FRAGMENT_START, this)); //$NON-NLS-1$
	}

	/**
	 * Internal worker to stop a bundle.
	 *
	 * @param persistent if true persistently record the bundle was stopped.
	 */
	protected void stopWorker(boolean persistent) throws BundleException {
		throw new BundleException(NLS.bind(Msg.BUNDLE_FRAGMENT_STOP, this)); //$NON-NLS-1$
	}

	/**
	 * Provides a list of {@link ServiceReference}s for the services
	 * registered by this bundle
	 * or <code>null</code> if the bundle has no registered
	 * services.
	 *
	 * <p>The list is valid at the time
	 * of the call to this method, but the framework is a very dynamic
	 * environment and services can be modified or unregistered at anytime.
	 *
	 * @return An array of {@link ServiceReference} or <code>null</code>.
	 * @exception java.lang.IllegalStateException If the
	 * bundle has been uninstalled.
	 * @see ServiceRegistrationImpl
	 * @see ServiceReference
	 */
	public ServiceReference[] getRegisteredServices() {
		checkValid();
		// Fragments cannot have a BundleContext and therefore
		// cannot have any services registered.
		return null;
	}

	/**
	 * Provides a list of {@link ServiceReference}s for the
	 * services this bundle is using,
	 * or <code>null</code> if the bundle is not using any services.
	 * A bundle is considered to be using a service if the bundle's
	 * use count for the service is greater than zero.
	 *
	 * <p>The list is valid at the time
	 * of the call to this method, but the framework is a very dynamic
	 * environment and services can be modified or unregistered at anytime.
	 *
	 * @return An array of {@link ServiceReference} or <code>null</code>.
	 * @exception java.lang.IllegalStateException If the
	 * bundle has been uninstalled.
	 * @see ServiceReference
	 */
	public ServiceReference[] getServicesInUse() {
		checkValid();
		// Fragments cannot have a BundleContext and therefore
		// cannot have any services in use.
		return null;
	}

	protected BundleLoaderProxy[] getHosts() {
		return hosts;
	}

	protected boolean isFragment() {
		return true;
	}

	/**
	 * Adds a host bundle for this fragment.
	 * @param value the BundleHost to add to the set of host bundles
	 */
	protected boolean addHost(BundleLoaderProxy host) {
		if (host == null) 
			return false;
		try {
			((BundleHost) host.getBundleHost()).attachFragment(this);
		} catch (BundleException be) {
			framework.publishFrameworkEvent(FrameworkEvent.ERROR, host.getBundleHost(), be);
			return false;
		}
		if (hosts == null) {
			hosts = new BundleLoaderProxy[] {host};
			return true;
		}
		for (int i = 0; i < hosts.length; i++) {
			if (host.getBundleHost() == hosts[i].getBundleHost())
				return true; // already a host
		}
		BundleLoaderProxy[] newHosts = new BundleLoaderProxy[hosts.length + 1];
		System.arraycopy(hosts, 0, newHosts, 0, hosts.length);
		newHosts[newHosts.length - 1] = host;
		hosts = newHosts;
		return true;
	}

	protected BundleLoader getBundleLoader() {
		// Fragments cannot have a BundleLoader.
		return null;
	}

	/**
	 * Return the current context for this bundle.
	 *
	 * @return BundleContext for this bundle.
	 */
	protected BundleContextImpl getContext() {
		// Fragments cannot have a BundleContext.
		return null;
	}
}
