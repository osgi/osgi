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
package org.eclipse.osgi.internal.resolver;

import java.util.*;

import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.debug.DebugOptions;
import org.eclipse.osgi.framework.internal.core.*;
import org.eclipse.osgi.framework.internal.core.KeyedElement;
import org.eclipse.osgi.framework.internal.core.KeyedHashSet;
import org.eclipse.osgi.service.resolver.*;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

public abstract class StateImpl implements State {
	public static final String[] PROPS = {"osgi.os", "osgi.ws", "osgi.nl", "osgi.arch", Constants.OSGI_FRAMEWORK_SYSTEM_PACKAGES, Constants.OSGI_RESOLVER_MODE}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 

	transient private Resolver resolver;
	transient private StateDeltaImpl changes;
	transient private boolean resolving = false;
	transient private HashSet removalPendings = new HashSet();
	private boolean resolved = true;
	private long timeStamp = System.currentTimeMillis();
	private KeyedHashSet bundleDescriptions = new KeyedHashSet(false);
	private StateObjectFactory factory;
	private KeyedHashSet resolvedBundles = new KeyedHashSet();
	boolean fullyLoaded = false;
	// only used for lazy loading of BundleDescriptions
	private StateReader reader;
	private Dictionary[] platformProperties = {new Hashtable(PROPS.length)}; // Dictionary here because of Filter API
	private ExportPackageDescription[] systemExports = new ExportPackageDescription[0];

	private static long cumulativeTime;

	protected StateImpl() {
		// to prevent extra-package instantiation 
	}

	public boolean addBundle(BundleDescription description) {
		if (!basicAddBundle(description))
			return false;
		resolved = false;
		getDelta().recordBundleAdded((BundleDescriptionImpl) description);
		if (resolver != null)
			resolver.bundleAdded(description);
		return true;
	}

	public boolean updateBundle(BundleDescription newDescription) {
		BundleDescriptionImpl existing = (BundleDescriptionImpl) bundleDescriptions.get((BundleDescriptionImpl) newDescription);
		if (existing == null)
			return false;
		if (!bundleDescriptions.remove(existing))
			return false;
		resolvedBundles.remove(existing);
		if (!basicAddBundle(newDescription))
			return false;
		resolved = false;
		getDelta().recordBundleUpdated((BundleDescriptionImpl) newDescription);
		if (resolver != null) {
			boolean pending = existing.getDependents().length > 0;
			resolver.bundleUpdated(newDescription, existing, pending);
			if (pending) {
				getDelta().recordBundleRemovalPending(existing);
				removalPendings.add(existing);
			} else {
				// an existing bundle has been updated with no dependents it can safely be unresolved now
				synchronized (this) {
					try {
						resolving = true;
						resolveBundle(existing, false, null, null, null, null);
					} finally {
						resolving = false;
					}
				}
			}
		}
		return true;
	}

	public BundleDescription removeBundle(long bundleId) {
		BundleDescription toRemove = getBundle(bundleId);
		if (toRemove == null || !removeBundle(toRemove))
			return null;
		return toRemove;
	}

	public boolean removeBundle(BundleDescription toRemove) {
		if (!bundleDescriptions.remove((KeyedElement) toRemove))
			return false;
		resolvedBundles.remove((KeyedElement) toRemove);
		resolved = false;
		getDelta().recordBundleRemoved((BundleDescriptionImpl) toRemove);
		if (resolver != null) {
			boolean pending = toRemove.getDependents().length > 0;
			resolver.bundleRemoved(toRemove, pending);
			if (pending) {
				getDelta().recordBundleRemovalPending((BundleDescriptionImpl) toRemove);
				removalPendings.add(toRemove);
			} else {
				// a bundle has been removed with no dependents it can safely be unresolved now
				synchronized (this) {
					try {
						resolving = true;
						resolveBundle(toRemove, false, null, null, null, null);
					} finally {
						resolving = false;
					}
				}
			}
		}
		return true;
	}

	public StateDelta getChanges() {
		return getDelta();
	}

	private StateDeltaImpl getDelta() {
		if (changes == null)
			changes = new StateDeltaImpl(this);
		return changes;
	}

	public BundleDescription[] getBundles(final String symbolicName) {
		final List bundles = new ArrayList();
		for (Iterator iter = bundleDescriptions.iterator(); iter.hasNext();) {
			BundleDescription bundle = (BundleDescription) iter.next();
			if (symbolicName.equals(bundle.getSymbolicName()))
				bundles.add(bundle);
		}
		return (BundleDescription[]) bundles.toArray(new BundleDescription[bundles.size()]);
	}

	public BundleDescription[] getBundles() {
		return (BundleDescription[]) bundleDescriptions.elements(new BundleDescription[bundleDescriptions.size()]);
	}

	public BundleDescription getBundle(long id) {
		BundleDescription result = (BundleDescription) bundleDescriptions.getByKey(new Long(id));
		if (result != null)
			return result;
		// need to look in removal pending bundles;
		for (Iterator iter = removalPendings.iterator(); iter.hasNext();) {
			BundleDescription removedBundle = (BundleDescription) iter.next();
			if (removedBundle.getBundleId() == id) // just return the first matching id
				return removedBundle;
		}
		return null;
	}

	// TODO: this does not comply with the spec	
	public BundleDescription getBundle(String name, Version version) {
		for (Iterator i = bundleDescriptions.iterator(); i.hasNext();) {
			BundleDescription current = (BundleDescription) i.next();
			if (name.equals(current.getSymbolicName()) && (version == null || current.getVersion().equals(version)))
				return current;
		}
		return null;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public boolean isResolved() {
		return resolved || isEmpty();
	}

	public void resolveConstraint(VersionConstraint constraint, BaseDescription supplier) {
		((VersionConstraintImpl) constraint).setSupplier(supplier);
	}

	public void resolveBundle(BundleDescription bundle, boolean status, BundleDescription[] hosts, ExportPackageDescription[] selectedExports, BundleDescription[] resolvedRequires, ExportPackageDescription[] resolvedImports) {
		if (!resolving)
			throw new IllegalStateException(); // TODO need error message here!
		BundleDescriptionImpl modifiable = (BundleDescriptionImpl) bundle;
		// must record the change before setting the resolve state to 
		// accurately record if a change has happened.
		getDelta().recordBundleResolved(modifiable, status);
		// force the new resolution data to stay in memory; we will not read this from disk anymore
		modifiable.setLazyLoaded(false);
		modifiable.setStateBit(BundleDescriptionImpl.RESOLVED, status);
		if (status) {
			resolveConstraints(modifiable, hosts, selectedExports, resolvedRequires, resolvedImports);
			resolvedBundles.add(modifiable);
		} else {
			// ensures no links are left 
			unresolveConstraints(modifiable);
			// remove the bundle from the resolved pool
			resolvedBundles.remove(modifiable);
		}
	}

	public void removeBundleComplete(BundleDescription bundle) {
		if (!resolving)
			throw new IllegalStateException(); // TODO need error message here!
		getDelta().recordBundleRemovalComplete((BundleDescriptionImpl) bundle);
		removalPendings.remove(bundle);
	}

	private void resolveConstraints(BundleDescriptionImpl bundle, BundleDescription[] hosts, ExportPackageDescription[] selectedExports, BundleDescription[] resolvedRequires, ExportPackageDescription[] resolvedImports) {
		HostSpecificationImpl hostSpec = (HostSpecificationImpl) bundle.getHost();
		if (hostSpec != null) {
			if (hosts != null) {
				hostSpec.setHosts(hosts);
				for (int i = 0; i < hosts.length; i++)
					((BundleDescriptionImpl) hosts[i]).addDependency(bundle);
			}
		}

		bundle.setSelectedExports(selectedExports);
		bundle.setResolvedRequires(resolvedRequires);
		bundle.setResolvedImports(resolvedImports);

		bundle.addDependencies(hosts);
		bundle.addDependencies(resolvedRequires);
		bundle.addDependencies(resolvedImports);
	}

	private void unresolveConstraints(BundleDescriptionImpl bundle) {
		HostSpecificationImpl host = (HostSpecificationImpl) bundle.getHost();
		if (host != null)
			host.setHosts(null);

		bundle.setSelectedExports(null);
		bundle.setResolvedImports(null);
		bundle.setResolvedRequires(null);

		bundle.removeDependencies();
	}

	private synchronized StateDelta resolve(boolean incremental, BundleDescription[] reResolve) {
		try {
			resolving = true;
			if (resolver == null)
				throw new IllegalStateException("no resolver set"); //$NON-NLS-1$
			fullyLoad();
			long start = 0;
			if (StateManager.DEBUG_PLATFORM_ADMIN_RESOLVER)
				start = System.currentTimeMillis();
			if (!incremental) {
				resolved = false;
				reResolve = getBundles();
				// need to get any removal pendings before flushing
				if (removalPendings.size() > 0) {
					BundleDescription[] removed = getRemovalPendings();
					reResolve = mergeBundles(reResolve, removed);
				}
				flush(reResolve);
			}
			if (resolved && reResolve == null)
				return new StateDeltaImpl(this);
			if (removalPendings.size() > 0) {
				BundleDescription[] removed = getRemovalPendings();
				reResolve = mergeBundles(reResolve, removed);
			}
			resolver.resolve(reResolve, platformProperties);
			resolved = true;

			StateDelta savedChanges = changes == null ? new StateDeltaImpl(this) : changes;
			changes = new StateDeltaImpl(this);

			if (StateManager.DEBUG_PLATFORM_ADMIN_RESOLVER) {
				long time = System.currentTimeMillis() - start;
				Debug.println("Time spent resolving: " + time); //$NON-NLS-1$
				cumulativeTime = cumulativeTime + time;
				DebugOptions.getDefault().setOption("org.eclipse.core.runtime.adaptor/resolver/timing/value", Long.toString(cumulativeTime)); //$NON-NLS-1$
			}

			return savedChanges;
		} finally {
			resolving = false;
		}
	}

	private BundleDescription[] mergeBundles(BundleDescription[] reResolve, BundleDescription[] removed) {
		if (reResolve == null)
			return removed; // just return all the removed bundles
		if (reResolve.length == 0)
			return reResolve; // if reResolve length==0 then we want to prevent pending removal
		// merge in all removal pending bundles that are not already in the list
		ArrayList result = new ArrayList(reResolve.length + removed.length);
		for (int i = 0; i < reResolve.length; i++)
			result.add(reResolve[i]);
		for (int i = 0; i < removed.length; i++) {
			boolean found = false;
			for (int j = 0; j < reResolve.length; j++) {
				if (removed[i] == reResolve[j]) {
					found = true;
					break;
				}
			}
			if (!found)
				result.add(removed[i]);
		}
		return (BundleDescription[]) result.toArray(new BundleDescription[result.size()]);
	}

	private void flush(BundleDescription[] bundles) {
		resolver.flush();
		resolved = false;
		if (resolvedBundles.isEmpty())
			return;
		for (int i = 0; i < bundles.length; i++) {
			resolveBundle(bundles[i], false, null, null, null, null);
		}
		resolvedBundles.clear();
	}

	public StateDelta resolve() {
		return resolve(true, null);
	}

	public StateDelta resolve(boolean incremental) {
		return resolve(incremental, null);
	}

	public StateDelta resolve(BundleDescription[] reResolve) {
		return resolve(true, reResolve);
	}

	public void setOverrides(Object value) {
		throw new UnsupportedOperationException();
	}

	public BundleDescription[] getResolvedBundles() {
		return (BundleDescription[]) resolvedBundles.elements(new BundleDescription[resolvedBundles.size()]);
	}

	public boolean isEmpty() {
		return bundleDescriptions.isEmpty();
	}

	void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	boolean basicAddBundle(BundleDescription description) {
		((BundleDescriptionImpl) description).setContainingState(this);
		return bundleDescriptions.add((BundleDescriptionImpl) description);
	}

	void addResolvedBundle(BundleDescriptionImpl resolvedBundle) {
		resolvedBundles.add(resolvedBundle);
	}

	public ExportPackageDescription[] getExportedPackages() {
		fullyLoad();
		final List allExportedPackages = new ArrayList();
		for (Iterator iter = resolvedBundles.iterator(); iter.hasNext();) {
			BundleDescription bundle = (BundleDescription) iter.next();
			ExportPackageDescription[] bundlePackages = bundle.getSelectedExports();
			if (bundlePackages == null)
				continue;
			for (int i = 0; i < bundlePackages.length; i++)
				allExportedPackages.add(bundlePackages[i]);
		}
		for (Iterator iter = removalPendings.iterator(); iter.hasNext();) {
			BundleDescription bundle = (BundleDescription) iter.next();
			ExportPackageDescription[] bundlePackages = bundle.getSelectedExports();
			if (bundlePackages == null)
				continue;
			for (int i = 0; i < bundlePackages.length; i++)
				allExportedPackages.add(bundlePackages[i]);
		}
		return (ExportPackageDescription[]) allExportedPackages.toArray(new ExportPackageDescription[allExportedPackages.size()]);
	}

	BundleDescription[] getFragments(final BundleDescription host) {
		final List fragments = new ArrayList();
		for (Iterator iter = bundleDescriptions.iterator(); iter.hasNext();) {
			BundleDescription bundle = (BundleDescription) iter.next();
			HostSpecification hostSpec = bundle.getHost();

			if (hostSpec != null) {
				BundleDescription[] hosts = hostSpec.getHosts();
				if (hosts != null)
					for (int i = 0; i < hosts.length; i++)
						if (hosts[i] == host) {
							fragments.add(bundle);
							break;
						}
			}
		}
		return (BundleDescription[]) fragments.toArray(new BundleDescription[fragments.size()]);
	}

	public void setTimeStamp(long newTimeStamp) {
		timeStamp = newTimeStamp;
	}

	public StateObjectFactory getFactory() {
		return factory;
	}

	void setFactory(StateObjectFactory factory) {
		this.factory = factory;
	}

	public BundleDescription getBundleByLocation(String location) {
		for (Iterator i = bundleDescriptions.iterator(); i.hasNext();) {
			BundleDescription current = (BundleDescription) i.next();
			if (location.equals(current.getLocation()))
				return current;
		}
		return null;
	}

	public Resolver getResolver() {
		return resolver;
	}

	public void setResolver(Resolver newResolver) {
		if (resolver == newResolver)
			return;
		if (resolver != null) {
			Resolver oldResolver = resolver;
			resolver = null;
			oldResolver.setState(null);
		}
		resolver = newResolver;
		if (resolver == null)
			return;
		resolver.setState(this);
	}

	public synchronized boolean setPlatformProperties(Dictionary platformProperties) {
		if (this.platformProperties.length != 1)
			this.platformProperties = new Dictionary[] {new Hashtable(PROPS.length)};
		return setProps(this.platformProperties[0], platformProperties);
	}

	public boolean setPlatformProperties(Dictionary[] platformProperties) {
		if (platformProperties.length == 0)
			throw new IllegalArgumentException();
		if (this.platformProperties.length != platformProperties.length) {
			this.platformProperties = new Dictionary[platformProperties.length];
			for (int i = 0; i < platformProperties.length; i++)
				this.platformProperties[i] = new Hashtable(PROPS.length);
		}
		boolean result = false;
		for (int i = 0; i < platformProperties.length; i++)
			result |= setProps(this.platformProperties[i], platformProperties[0]);
		return result;
	}

	Dictionary[] getPlatformProperties() {
		return platformProperties;
	}

	private boolean checkProp(Object origObj, Object newObj) {
		if ((origObj == null && newObj != null) || (origObj != null && newObj == null))
			return true;
		if (origObj == null)
			return false;
		if (origObj.getClass() != newObj.getClass())
			return true;
		if (origObj instanceof String)
			return !origObj.equals(newObj);
		String[] origProps = (String[]) origObj;
		String[] newProps = (String[]) newObj;
		if (origProps.length != newProps.length)
			return true;
		for (int i = 0; i < origProps.length; i++) {
			if (!origProps[i].equals(newProps[i]))
				return true;
		}
		return false;
	}

	private boolean setProps(Dictionary origProps, Dictionary newProps) {
		boolean changed = false;
		for (int i = 0; i < PROPS.length; i++) {
			Object origProp = origProps.get(PROPS[i]);
			Object newProp = newProps.get(PROPS[i]);
			if (checkProp(origProp, newProp)) {
				changed = true;
				if (newProp == null)
					origProps.remove(PROPS[i]);
				else
					origProps.put(PROPS[i], newProp);
				if (PROPS[i].equals(Constants.OSGI_FRAMEWORK_SYSTEM_PACKAGES))
					setSystemExports((String) newProp);
			}
		}
		return changed;
	}

	BundleDescription[] getRemovalPendings() {
		return (BundleDescription[]) removalPendings.toArray(new BundleDescription[removalPendings.size()]);
	}

	public synchronized ExportPackageDescription linkDynamicImport(BundleDescription importingBundle, String requestedPackage) {
		if (resolver == null)
			throw new IllegalStateException("no resolver set"); //$NON-NLS-1$
		fullyLoad();
		// ask the resolver to resolve our dynamic import
		ExportPackageDescriptionImpl result = (ExportPackageDescriptionImpl) resolver.resolveDynamicImport(importingBundle, requestedPackage);
		if (result == null)
			return null;
		// need to add the result to the list of resolved imports
		((BundleDescriptionImpl) importingBundle).addDynamicResolvedImport(result);
		return result;
	}

	void setReader(StateReader reader) {
		this.reader = reader;
	}

	StateReader getReader() {
		return reader;
	}

	void fullyLoad() {
		if (fullyLoaded == true)
			return;
		if (reader != null && reader.isLazyLoaded())
			reader.fullyLoad();
		fullyLoaded = true;
	}

	void unloadLazyData(long expireTime) {
		long currentTime = System.currentTimeMillis();
		BundleDescription[] bundles = getBundles();
		// make sure no other thread is trying to unload or load
		synchronized (reader) {
			for (int i = 0; i < bundles.length; i++)
				((BundleDescriptionImpl) bundles[i]).unload(currentTime, expireTime);
		}
	}

	void setSystemExports(String exportSpec) {
		try {
			ManifestElement[] elements = ManifestElement.parseHeader(Constants.EXPORT_PACKAGE, exportSpec);
			// we can pass false for strict mode here because we never want to mark the system
			// exports as internal.
			systemExports = StateBuilder.createExportPackages(elements, null, null, null, 2, false);
		} catch (BundleException e) {
			// TODO consider throwing this... 
		}
	}

	ExportPackageDescription[] getSystemExports() {
		return systemExports;
	}
}
