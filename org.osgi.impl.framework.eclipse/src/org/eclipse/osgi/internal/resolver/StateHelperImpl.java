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
package org.eclipse.osgi.internal.resolver;

import java.util.*;

import org.eclipse.osgi.framework.internal.core.Constants;
import org.eclipse.osgi.service.resolver.*;

/**
 * An implementation for the StateHelper API. Access to this implementation is
 * provided  by the PlatformAdmin. Since this helper is a general facility for
 * state manipulation, it should not be tied to any implementation details.
 */
public class StateHelperImpl implements StateHelper {
	private static StateHelper instance = new StateHelperImpl();

	/**
	 * @see StateHelper
	 */
	public BundleDescription[] getDependentBundles(BundleDescription[] roots) {
		if (roots == null || roots.length == 0)
			return new BundleDescription[0];

		Set reachable = new HashSet(roots.length);
		for (int i = 0; i < roots.length; i++) {
			if (!roots[i].isResolved())
				continue;
			addDependentBundles(roots[i], reachable);
		}
		return (BundleDescription[]) reachable.toArray(new BundleDescription[reachable.size()]);
	}

	private void addDependentBundles(BundleDescription root, Set reachable) {
		if (reachable.contains(root))
			return;
		reachable.add(root);
		BundleDescription[] dependents = root.getDependents();
		for (int i = 0; i < dependents.length; i++)
			addDependentBundles(dependents[i], reachable);
	}

	/**
	 * @see StateHelper
	 */
	public VersionConstraint[] getUnsatisfiedConstraints(BundleDescription bundle) {
		State containingState = bundle.getContainingState();
		if (containingState == null)
			// it is a bug in the client to call this method when not attached to a state
			throw new IllegalStateException("Does not belong to a state"); //$NON-NLS-1$		
		List unsatisfied = new ArrayList();
		HostSpecification host = bundle.getHost();
		if (host != null)
			if (!host.isResolved() && !isResolvable(host))
				unsatisfied.add(host);
		BundleSpecification[] requiredBundles = bundle.getRequiredBundles();
		for (int i = 0; i < requiredBundles.length; i++)
			if (!requiredBundles[i].isResolved() && !isResolvable(requiredBundles[i]))
				unsatisfied.add(requiredBundles[i]);
		ImportPackageSpecification[] packages = bundle.getImportPackages();
		for (int i = 0; i < packages.length; i++)
			if (!packages[i].isResolved() && !isResolvable(packages[i]))
				unsatisfied.add(packages[i]);
		return (VersionConstraint[]) unsatisfied.toArray(new VersionConstraint[unsatisfied.size()]);
	}

	/**
	 * @see StateHelper
	 */
	public boolean isResolvable(ImportPackageSpecification constraint) {
		ExportPackageDescription[] exports = constraint.getBundle().getContainingState().getExportedPackages();
		for (int i = 0; i < exports.length; i++)
			if (constraint.isSatisfiedBy(exports[i]))
				return true;
		return false;
	}

	/**
	 * @see StateHelper
	 */
	public boolean isResolvable(BundleSpecification specification) {
		return isBundleConstraintResolvable(specification);
	}

	/**
	 * @see StateHelper
	 */
	public boolean isResolvable(HostSpecification specification) {
		return isBundleConstraintResolvable(specification);
	}

	/*
	 * Returns whether a bundle specification/host specification can be resolved.
	 */
	private boolean isBundleConstraintResolvable(VersionConstraint constraint) {
		BundleDescription[] availableBundles = constraint.getBundle().getContainingState().getBundles(constraint.getName());
		for (int i = 0; i < availableBundles.length; i++)
			if (availableBundles[i].isResolved() && constraint.isSatisfiedBy(availableBundles[i]))
				return true;
		return false;
	}

	public Object[][] sortBundles(BundleDescription[] toSort) {
		List references = new ArrayList(toSort.length);
		for (int i = 0; i < toSort.length; i++)
			if (toSort[i].isResolved())
				buildReferences(toSort[i], references);
		return ComputeNodeOrder.computeNodeOrder(toSort, (Object[][]) references.toArray(new Object[references.size()][]));
	}

	private void buildReferences(BundleDescription description, List references) {
		HostSpecification host = description.getHost();
		// it is a fragment
		if (host != null) {
			// just create a dependency between fragment and host
			if (host.getHosts() != null) {
				BundleDescription[] hosts = host.getHosts();
				for (int i = 0; i < hosts.length; i++)
					if (hosts[i] != description)
						references.add(new Object[] {description, hosts[i]});
			}
		} else {
			// it is a host
			buildReferences(description, ((BundleDescriptionImpl) description).getBundleDependencies(), references);
		}
	}

	private void buildReferences(BundleDescription description, List dependencies, List references) {
		for (Iterator iter = dependencies.iterator(); iter.hasNext();)
			addReference(description, (BundleDescription) iter.next(), references);
	}

	private void addReference(BundleDescription description, BundleDescription reference, List references) {
		// build the reference from the description
		if (description == reference || reference == null)
			return;

		references.add(new Object[] {description, reference});
	}

	public ExportPackageDescription[] getVisiblePackages(BundleDescription bundle) {
		StateImpl state = (StateImpl) bundle.getContainingState();
		boolean strict = false;
		if (state != null)
			strict = state.inStrictMode();
		ArrayList packageList = new ArrayList(); // list of all ExportPackageDescriptions that are visible
		ArrayList importList = new ArrayList(); // list of package names which are directly imported
		// get the list of directly imported packages first.
		ExportPackageDescription[] resolvedImports = bundle.getResolvedImports();
		for (int i = 0; i < resolvedImports.length; i++) {
			packageList.add(resolvedImports[i]);
			importList.add(resolvedImports[i].getName()); // besure to add to direct import list
		}
		// now find all the packages that are visible from required bundles
		BundleDescription[] requiredBundles = bundle.getResolvedRequires();
		ArrayList visited = new ArrayList(requiredBundles.length);
		for (int i = 0; i < requiredBundles.length; i++)
			getPackages(requiredBundles[i], bundle.getSymbolicName(), importList, packageList, visited, strict);
		return (ExportPackageDescription[]) packageList.toArray(new ExportPackageDescription[packageList.size()]);
	}

	private void getPackages(BundleDescription requiredBundle, String symbolicName, List importList, List packageList, List visited, boolean strict) {
		if (visited.contains(requiredBundle))
			return; // prevent duplicate entries and infinate loops incase of cycles
		visited.add(requiredBundle);
		// add all the exported packages from the required bundle; take x-friends into account.
		ExportPackageDescription[] exports = requiredBundle.getSelectedExports();
		for (int i = 0; i < exports.length; i++)
			if (!isSystemExport(exports[i]) && isFriend(symbolicName, exports[i], strict) && !importList.contains(exports[i].getName()))
				packageList.add(exports[i]);
		// now look for reexported bundles from the required bundle.
		BundleSpecification[] requiredBundles = requiredBundle.getRequiredBundles();
		for (int i = 0; i < requiredBundles.length; i++)
			if (requiredBundles[i].isExported() && requiredBundles[i].getSupplier() != null)
				getPackages((BundleDescription) requiredBundles[i].getSupplier(), symbolicName, importList, packageList, visited, strict);
	}

	private boolean isSystemExport(ExportPackageDescription export) {
		StateImpl state = (StateImpl) export.getExporter().getContainingState();
		if (state == null)
			return false;
		ExportPackageDescription[] systemExports = state.getSystemPackages();
		for (int i = 0; i < systemExports.length; i++)
			if (systemExports[i] == export)
				return true;
		return false;
	}

	private boolean isFriend(String consumerBSN, ExportPackageDescription export, boolean strict) {
		if (!strict)
			return true; // ignore friends rules if not in strict mode
		String[] friends = (String[]) export.getDirective(Constants.FRIENDS_DIRECTIVE);
		if (friends == null)
			return true; // no x-friends means it is wide open
		for (int i = 0; i < friends.length; i++)
			if (friends[i].equals(consumerBSN))
				return true; // the consumer is a friend
		return false;
	}

	public int getAccessCode(BundleDescription bundle, ExportPackageDescription export) {
		if (((Boolean) export.getDirective(Constants.INTERNAL_DIRECTIVE)).booleanValue())
			return ACCESS_DISCOURAGED;
		if (!isFriend(bundle.getSymbolicName(), export, true)) // pass strict here so that x-friends is processed
			return ACCESS_DISCOURAGED;
		return ACCESS_ENCOURAGED;
	}

	public static StateHelper getInstance() {
		return instance;
	}
}
