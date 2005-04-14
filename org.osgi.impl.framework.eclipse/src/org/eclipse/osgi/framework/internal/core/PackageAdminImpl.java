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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.service.resolver.*;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.*;
import org.osgi.service.packageadmin.*;

/**
 * PackageAdmin service for the OSGi specification.
 *
 * Framework service which allows bundle programmers to inspect the packages
 * exported in the framework and eagerly update or uninstall bundles.
 *
 * If present, there will only be a single instance of this service
 * registered in the framework.
 *
 * <p> The term <i>exported package</i> (and the corresponding interface
 * {@link ExportedPackage}) refers to a package that has actually been
 * exported (as opposed to one that is available for export).
 *
 * <p> Note that the information about exported packages returned by this
 * service is valid only until the next time {@link #refreshPackages(org.osgi.framework.Bundle[])} is
 * called.
 * If an ExportedPackage becomes stale, (that is, the package it references
 * has been updated or removed as a result of calling
 * PackageAdmin.refreshPackages()),
 * its getName() and getSpecificationVersion() continue to return their
 * old values, isRemovalPending() returns true, and getExportingBundle()
 * and getImportingBundles() return null.
 */
public class PackageAdminImpl implements PackageAdmin {
	/** framework object */
	protected Framework framework;

	/**
	 * Constructor.
	 *
	 * @param framework Framework object.
	 */
	protected PackageAdminImpl(Framework framework) {
		this.framework = framework;
	}

	public ExportedPackage[] getExportedPackages(Bundle bundle) {
		ArrayList allExports = new ArrayList();
		synchronized (framework.bundles) {
			ExportPackageDescription[] allDescriptions = framework.adaptor.getState().getExportedPackages();
			for (int i = 0; i < allDescriptions.length; i++) {
				if (!allDescriptions[i].isRoot())
					continue;
				ExportedPackageImpl exportedPackage = createExportedPackage(allDescriptions[i]);
				if (exportedPackage == null)
					continue;
				if (bundle == null || exportedPackage.supplier.getBundle() == bundle)
					allExports.add(exportedPackage);
			}
		}
		return (ExportedPackage[]) (allExports.size() == 0 ? null : allExports.toArray(new ExportedPackage[allExports.size()]));
	}

	private ExportedPackageImpl createExportedPackage(ExportPackageDescription description) {
		BundleDescription exporter = description.getExporter();
		if (exporter == null || exporter.getHost() != null)
			return null;
		BundleLoaderProxy proxy = (BundleLoaderProxy) exporter.getUserObject();
		if (proxy == null) {
			BundleHost bundle = (BundleHost) framework.getBundle(exporter.getBundleId());
			if (bundle == null)
				return null;
			proxy = bundle.getLoaderProxy();
		}
		return new ExportedPackageImpl(description, proxy);
	}

	public ExportedPackage getExportedPackage(String name) {
		ExportedPackage[] allExports = getExportedPackages((Bundle) null);
		if (allExports == null)
			return null;
		ExportedPackage result = null;
		for (int i = 0; i < allExports.length; i++) {
			if (name.equals(allExports[i].getName())) {
				if (result == null) {
					result = allExports[i];
				} else {
					// TODO not efficient but this is not called very often
					Version curVersion = Version.parseVersion(result.getSpecificationVersion());
					Version newVersion = Version.parseVersion(allExports[i].getSpecificationVersion());
					if (newVersion.compareTo(curVersion) >= 0)
						result = allExports[i];
				}
			}
		}
		return result;
	}

	public ExportedPackage[] getExportedPackages(String name) {
		ExportedPackage[] allExports = getExportedPackages((Bundle) null);
		if (allExports == null)
			return null;
		ArrayList result = new ArrayList(1); // rare to have more than one
		for (int i = 0; i < allExports.length; i++)
			if (name.equals(allExports[i].getName()))
				result.add(allExports[i]);
		return (ExportedPackage[]) (result.size() == 0 ? null : result.toArray(new ExportedPackage[result.size()]));
	}

	public void refreshPackages(Bundle[] input) {
		framework.checkAdminPermission(framework.systemBundle, AdminPermission.RESOLVE);

		AbstractBundle[] copy = null;
		if (input != null) {
			synchronized (input) {
				copy = new AbstractBundle[input.length];
				System.arraycopy(input, 0, copy, 0, input.length);
			}
		}

		final AbstractBundle[] bundles = copy;
		Thread refresh = framework.secureAction.createThread(new Runnable() {
			public void run() {
				doResolveBundles(bundles, true);
			}
		}, "Refresh Packages"); //$NON-NLS-1$

		refresh.start();
	}

	public boolean resolveBundles(Bundle[] bundles) {
		framework.checkAdminPermission(framework.systemBundle, AdminPermission.RESOLVE);
		doResolveBundles(null, false);
		if (bundles == null)
			bundles = framework.getAllBundles();
		for (int i = 0; i < bundles.length; i++)
			if (!((AbstractBundle) bundles[i]).isResolved())
				return false;

		return true;
	}

	synchronized void doResolveBundles(AbstractBundle[] bundles, boolean refreshPackages) {
		try {
			framework.publishBundleEvent(Framework.BATCHEVENT_BEGIN, framework.systemBundle);
			AbstractBundle[] refreshedBundles = null;
			BundleDescription[] descriptions = null;
			synchronized (framework.bundles) {
				int numBundles = bundles == null ? 0 : bundles.length;
				if (!refreshPackages)
					// in this case we must make descriptions non-null so we do
					// not force the removal pendings to be processed when resolving
					// the state.
					descriptions = new BundleDescription[0];
				else if (numBundles > 0) {
					ArrayList results = new ArrayList(numBundles);
					for (int i = 0; i < numBundles; i++) {
						BundleDescription description = bundles[i].getBundleDescription();
						if (description != null && description.getBundleId() != 0 && !results.contains(description))
							results.add(description);
						// add in any singleton bundles if needed
						AbstractBundle[] sameNames = framework.bundles.getBundles(bundles[i].getSymbolicName());
						if (sameNames != null && sameNames.length > 1) {
							for (int j = 0; j < sameNames.length; j++)
								if (sameNames[j] != bundles[i]) {
									BundleDescription sameName = sameNames[j].getBundleDescription();
									if (sameName != null && sameName.getBundleId() != 0 && sameName.isSingleton() && !results.contains(sameName))
										results.add(sameName);
								}
						}
					}
					descriptions = (BundleDescription[]) (results.size() == 0 ? null : results.toArray(new BundleDescription[results.size()]));
				}
			}
			StateDelta stateDelta = framework.adaptor.getState().resolve(descriptions);
			refreshedBundles = processDelta(stateDelta.getChanges(), refreshPackages);
			if (refreshPackages) {
				AbstractBundle[] allBundles = framework.getAllBundles();
				for (int i = 0; i < allBundles.length; i++)
					allBundles[i].unresolvePermissions(refreshedBundles);
				resumeBundles(refreshedBundles);
			}
		} catch (Throwable t) {
			if (Debug.DEBUG && Debug.DEBUG_PACKAGEADMIN) {
				Debug.println("PackageAdminImpl.doResolveBundles: Error occured :"); //$NON-NLS-1$
				Debug.printStackTrace(t);
			}
			if (t instanceof RuntimeException)
				throw (RuntimeException) t;
			if (t instanceof Error)
				throw (Error) t;
		} finally {
			framework.publishBundleEvent(Framework.BATCHEVENT_END, framework.systemBundle);
			if (refreshPackages)
				framework.publishFrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED, framework.systemBundle, null);
		}
	}

	private void resumeBundles(AbstractBundle[] bundles) {
		if (Debug.DEBUG && Debug.DEBUG_PACKAGEADMIN) {
			Debug.println("PackageAdminImpl: restart the bundles"); //$NON-NLS-1$
		}
		if (bundles == null)
			return;
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].isResolved())
				framework.resumeBundle(bundles[i]);
		}
	}

	private void suspendBundle(AbstractBundle bundle) throws BundleException {
		if (bundle.isActive() && !bundle.isFragment()) {
			boolean suspended = framework.suspendBundle(bundle, true);
			if (!suspended) {
				throw new BundleException(Msg.BUNDLE_STATE_CHANGE_EXCEPTION); //$NON-NLS-1$
			}
		} else {
			if (bundle.getStateChanging() != Thread.currentThread())
				bundle.beginStateChange();
		}

		if (Debug.DEBUG && Debug.DEBUG_PACKAGEADMIN) {
			if (bundle.stateChanging == null) {
				Debug.println("Bundle state change lock is clear! " + bundle); //$NON-NLS-1$
				Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
			}
		}
	}

	private void applyRemovalPending(BundleDelta bundleDelta) throws BundleException {
		if ((bundleDelta.getType() & BundleDelta.REMOVAL_COMPLETE) != 0) {
			BundleDescription bundle = bundleDelta.getBundle();
			if (bundle.getDependents() != null && bundle.getDependents().length > 0) {
				/* Reaching here is an internal error */
				if (Debug.DEBUG && Debug.DEBUG_PACKAGEADMIN) {
					Debug.println("Bundles still depend on removed bundle! " + bundle); //$NON-NLS-1$
					Debug.printStackTrace(new Exception("Stack trace")); //$NON-NLS-1$
				}
				throw new BundleException(Msg.OSGI_INTERNAL_ERROR); //$NON-NLS-1$
			}
			BundleLoaderProxy proxy = (BundleLoaderProxy) bundle.getUserObject();
			if (proxy != null) {
				BundleHost.closeBundleLoader(proxy);
				try {
					proxy.getBundleHost().getBundleData().close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	private AbstractBundle setResolved(BundleDescription bundleDescription) {
		if (!bundleDescription.isResolved())
			return null;
		AbstractBundle bundle = framework.getBundle(bundleDescription.getBundleId());
		if (bundle == null) {
			BundleException be = new BundleException(NLS.bind(Msg.BUNDLE_NOT_IN_FRAMEWORK, bundleDescription)); //$NON-NLS-1$
			framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, be);
			return null;
		}
		boolean resolve = true;
		if (bundle.isFragment()) {
			BundleDescription[] hosts = bundleDescription.getHost().getHosts();
			for (int i = 0; i < hosts.length; i++) {
				BundleHost host = (BundleHost) framework.getBundle(hosts[0].getBundleId());
				resolve = ((BundleFragment) bundle).addHost(host.getLoaderProxy());
			}
		}
		if (resolve)
			bundle.resolve();
		return bundle;
	}

	private AbstractBundle[] applyDeltas(BundleDelta[] bundleDeltas) throws BundleException {
		ArrayList results = new ArrayList(bundleDeltas.length);
		for (int i = 0; i < bundleDeltas.length; i++) {
			int type = bundleDeltas[i].getType();
			if ((type & (BundleDelta.REMOVAL_PENDING | BundleDelta.REMOVAL_COMPLETE)) != 0)
				applyRemovalPending(bundleDeltas[i]);
			if ((type & BundleDelta.RESOLVED) != 0) {
				AbstractBundle bundle = setResolved(bundleDeltas[i].getBundle());
				if (bundle != null && bundle.isResolved())
					results.add(bundle);
			}
		}
		return (AbstractBundle[]) (results.size() == 0 ? null : results.toArray(new AbstractBundle[results.size()]));
	}

	private AbstractBundle[] processDelta(BundleDelta[] bundleDeltas, boolean refreshPackages) {
		Util.sort(bundleDeltas, 0, bundleDeltas.length);
		ArrayList bundlesList = new ArrayList(bundleDeltas.length);
		// get all the bundles that are going to be refreshed
		for (int i = 0; i < bundleDeltas.length; i++) {
			AbstractBundle changedBundle = framework.getBundle(bundleDeltas[i].getBundle().getBundleId());
			if (changedBundle != null)
				bundlesList.add(changedBundle);
		}
		AbstractBundle[] refresh = (AbstractBundle[]) bundlesList.toArray(new AbstractBundle[bundlesList.size()]);
		boolean[] previouslyResolved = new boolean[refresh.length];
		AbstractBundle[] resolved = null;
		try {
			try {
				if (Debug.DEBUG && Debug.DEBUG_PACKAGEADMIN) {
					Debug.println("refreshPackages: Suspend each bundle and acquire its state change lock"); //$NON-NLS-1$
				}
				// find which bundles were previously resolved and handle extension bundles
				boolean restart = false;
				for (int i = refresh.length - 1; i >= 0; i--) {
					previouslyResolved[i] = refresh[i].isResolved();
					if (refresh[i] == framework.systemBundle)
						restart = true;
					else if (((refresh[i].bundledata.getType() & BundleData.TYPE_FRAMEWORK_EXTENSION) != 0) && previouslyResolved[i])
						restart = true;
					else if ((refresh[i].bundledata.getType() & BundleData.TYPE_BOOTCLASSPATH_EXTENSION) != 0)
						restart = true;
				}
				if (restart) {
					// must publish PACKAGE_REFRESHED event here because we are done.
					if (refreshPackages)
						framework.publishFrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED, framework.systemBundle, null);
					restartFramework();
				}
				// now suspend each bundle and grab its state change lock.
				if (refreshPackages)
					for (int i = refresh.length - 1; i >= 0; i--)
						suspendBundle(refresh[i]);
				/*
				 * Refresh the bundles which will unexport the packages.
				 * This will move RESOLVED bundles to the INSTALLED state.
				 */
				if (Debug.DEBUG && Debug.DEBUG_PACKAGEADMIN) {
					Debug.println("refreshPackages: refresh the bundles"); //$NON-NLS-1$
				}

				synchronized (framework.bundles) {
					for (int i = 0; i < refresh.length; i++)
						refresh[i].refresh();
				}
				// send out unresolved events outside synch block (defect #80610)
				for (int i = 0; i < refresh.length; i++) {
					// send out unresolved events
					if (previouslyResolved[i])
						framework.publishBundleEvent(BundleEvent.UNRESOLVED, refresh[i]);
				}

				/*
				 * apply Deltas.
				 */
				if (Debug.DEBUG && Debug.DEBUG_PACKAGEADMIN) {
					Debug.println("refreshPackages: applying deltas to bundles"); //$NON-NLS-1$
				}
				synchronized (framework.bundles) {
					resolved = applyDeltas(bundleDeltas);
				}

			} finally {
				/*
				 * Release the state change locks.
				 */
				if (Debug.DEBUG && Debug.DEBUG_PACKAGEADMIN) {
					Debug.println("refreshPackages: release the state change locks"); //$NON-NLS-1$
				}
				if (refreshPackages)
					for (int i = 0; i < refresh.length; i++) {
						AbstractBundle changedBundle = refresh[i];
						changedBundle.completeStateChange();
					}
			}
			/*
			 * Take this opportunity to clean up the adaptor storage.
			 */
			if (refreshPackages) {
				if (Debug.DEBUG && Debug.DEBUG_PACKAGEADMIN)
					Debug.println("refreshPackages: clean up adaptor storage"); //$NON-NLS-1$
				try {
					framework.adaptor.compactStorage();
				} catch (IOException e) {
					if (Debug.DEBUG && Debug.DEBUG_PACKAGEADMIN) {
						Debug.println("refreshPackages exception: " + e.getMessage()); //$NON-NLS-1$
						Debug.printStackTrace(e);
					}
					framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, new BundleException(Msg.BUNDLE_REFRESH_FAILURE, e)); //$NON-NLS-1$
				}
			}
		} catch (BundleException e) {
			if (Debug.DEBUG && Debug.DEBUG_PACKAGEADMIN) {
				Debug.println("refreshPackages exception: " + e.getMessage()); //$NON-NLS-1$
				Debug.printStackTrace(e.getNestedException());
			}
			framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, new BundleException(Msg.BUNDLE_REFRESH_FAILURE, e)); //$NON-NLS-1$
		}

		// send out any resolved.  This must be done after the state change locks have been release.
		if (Debug.DEBUG && Debug.DEBUG_PACKAGEADMIN)
			Debug.println("refreshPackages: send out RESOLVED events"); //$NON-NLS-1$
		if (resolved != null)
			for (int i = 0; i < resolved.length; i++)
				framework.publishBundleEvent(BundleEvent.RESOLVED, resolved[i]);

		return refresh;
	}

	private void restartFramework() {
		System.getProperties().put("osgi.forcedRestart", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		framework.shutdown();
		System.exit(23);
	}

	public RequiredBundle[] getRequiredBundles(String symbolicName) {
		AbstractBundle[] bundles;
		if (symbolicName == null)
			bundles = framework.getAllBundles();
		else
			bundles = framework.getBundleBySymbolicName(symbolicName);
		if (bundles == null || bundles.length == 0)
			return null;

		ArrayList result = new ArrayList(bundles.length);
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].isFragment() || !bundles[i].isResolved() || bundles[i].getSymbolicName() == null)
				continue;
			result.add(((BundleHost) bundles[i]).getLoaderProxy());
		}
		return result.size() == 0 ? null : (RequiredBundle[]) result.toArray(new RequiredBundle[result.size()]);
	}

	public Bundle[] getBundles(String symbolicName, String versionRange) {
		if (symbolicName == null) {
			throw new IllegalArgumentException();
		}
		AbstractBundle bundles[] = framework.getBundleBySymbolicName(symbolicName);
		if (bundles == null)
			return null;

		if (versionRange == null) {
			AbstractBundle[] result = new AbstractBundle[bundles.length];
			System.arraycopy(bundles, 0, result, 0, result.length);
			return result;
		}

		// This code depends on the array of bundles being in descending
		// version order.
		ArrayList result = new ArrayList(bundles.length);
		VersionRange range = new VersionRange(versionRange);
		for (int i = 0; i < bundles.length; i++) {
			if (range.isIncluded(bundles[i].getVersion())) {
				result.add(bundles[i]);
			}
		}

		if (result.size() == 0)
			return null;
		return (AbstractBundle[]) result.toArray(new AbstractBundle[result.size()]);
	}

	public Bundle[] getFragments(Bundle bundle) {
		return ((AbstractBundle) bundle).getFragments();
	}

	public Bundle[] getHosts(Bundle bundle) {
		BundleLoaderProxy[] hosts = ((AbstractBundle) bundle).getHosts();
		if (hosts == null)
			return null;
		Bundle[] result = new Bundle[hosts.length];
		for (int i = 0; i < hosts.length; i++)
			result[i] = hosts[i].getBundleHost();
		return result;
	}

	Bundle getBundlePriv(Class clazz) {
		ClassLoader cl = clazz.getClassLoader();
		if (cl instanceof BundleClassLoader)
			return ((BundleLoader) ((BundleClassLoader) cl).getDelegate()).bundle;
		if (cl == getClass().getClassLoader())
			return framework.systemBundle;
		return null;
	}

	public Bundle getBundle(final Class clazz) {
		if (System.getSecurityManager() == null)
			return getBundlePriv(clazz);
		return (Bundle) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return getBundlePriv(clazz);
			}
		});
	}

	public int getBundleType(Bundle bundle) {
		return ((AbstractBundle) bundle).isFragment() ? PackageAdmin.BUNDLE_TYPE_FRAGMENT : 0;
	}

	protected void cleanup() { //This is only called when the framework is shutting down
	}

	protected void setResolvedBundles(SystemBundle systemBundle) {
		checkSystemBundle(systemBundle);
		// Now set the actual state of the bundles from the persisted state.
		State state = framework.adaptor.getState();
		BundleDescription[] descriptions = state.getBundles();
		for (int i = 0; i < descriptions.length; i++) {
			long bundleId = descriptions[i].getBundleId();
			if (bundleId == 0)
				continue;
			setResolved(descriptions[i]);
		}
	}

	private void checkSystemBundle(SystemBundle systemBundle) {
		try {
			// first check that the system bundle has not changed since last saved state.
			State state = framework.adaptor.getState();
			BundleDescription newSystemBundle = state.getFactory().createBundleDescription(state, systemBundle.getHeaders(""), systemBundle.getLocation(), 0); //$NON-NLS-1$
			if (newSystemBundle == null)
				throw new BundleException(Msg.OSGI_SYSTEMBUNDLE_DESCRIPTION_ERROR); //$NON-NLS-1$
			BundleDescription oldSystemBundle = state.getBundle(0);
			if (oldSystemBundle != null) {
				boolean different = false;
				if (newSystemBundle.getVersion() != null && !newSystemBundle.getVersion().equals(oldSystemBundle.getVersion()))
					different = true;
				// need to check to make sure the system bundle description
				// is up to date in the state.
				ExportPackageDescription[] oldPackages = oldSystemBundle.getExportPackages();
				ExportPackageDescription[] newPackages = newSystemBundle.getExportPackages();
				if (oldPackages.length >= newPackages.length) {
					for (int i = 0; i < newPackages.length; i++) {
						if (oldPackages[i].getName().equals(newPackages[i].getName())) {
							Object oldVersion = oldPackages[i].getVersion();
							Object newVersion = newPackages[i].getVersion();
							if (oldVersion == null) {
								if (newVersion != null) {
									different = true;
									break;
								}
							} else if (!oldVersion.equals(newVersion)) {
								different = true;
								break;
							}
						} else {
							different = true;
							break;
						}
					}
				} else {
					different = true;
				}
				if (different) {
					state.removeBundle(0);
					state.addBundle(newSystemBundle);
					// force resolution so packages are properly linked
					state.resolve(false);
				}
			} else {
				state.addBundle(newSystemBundle);
				// force resolution so packages are properly linked
				state.resolve(false);
			}
			ExportPackageDescription[] packages = newSystemBundle.getExportPackages();
			if (packages != null) {
				String[] systemPackages = new String[packages.length];
				for (int i = 0; i < packages.length; i++) {
					ExportPackageDescription spec = packages[i];
					if (spec.getName().equals(Constants.OSGI_FRAMEWORK_PACKAGE)) {
						String version = spec.getVersion().toString();
						if (version != null)
							System.getProperties().put(Constants.FRAMEWORK_VERSION, version);
					}
					systemPackages[i] = spec.getName();
				}
			}
		} catch (BundleException e) /* fatal error */{
			e.printStackTrace();
			throw new RuntimeException(NLS.bind(Msg.OSGI_SYSTEMBUNDLE_CREATE_EXCEPTION, e.getMessage())); //$NON-NLS-1$
		}
	}
}
