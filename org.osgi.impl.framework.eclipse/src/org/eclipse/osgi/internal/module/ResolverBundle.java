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
package org.eclipse.osgi.internal.module;

import java.util.*;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.Constants;

/*
 * A companion to BundleDescription from the state used while resolving.
 */
public class ResolverBundle extends VersionSupplier {
	public static final int UNRESOLVED = 0;
	public static final int RESOLVING = 1;
	public static final int RESOLVED = 2;

	private Long bundleID;
	private BundleConstraint host;
	private ResolverImport[] imports;
	private ResolverExport[] exports;
	private BundleConstraint[] requires;
	// Fragment support
	private ArrayList fragments;
	private HashMap fragmentExports;
	private HashMap fragmentImports;
	private HashMap fragmentRequires;
	// Flag specifying whether this bundle is resolvable
	private boolean resolvable = true;
	// Internal resolver state for this bundle
	private int state = UNRESOLVED;

	private ResolverImpl resolver;
	private boolean newFragmentExports;

	ResolverBundle(BundleDescription bundle, ResolverImpl resolver) {
		super(bundle);
		this.bundleID = new Long(bundle.getBundleId());
		this.resolver = resolver;
		initialize(bundle.isResolved());
	}

	void initialize(boolean useSelectedExports) {
		if (getBundle().getHost() != null) {
			host = new BundleConstraint(this, getBundle().getHost());
			exports = new ResolverExport[0];
			imports = new ResolverImport[0];
			requires = new BundleConstraint[0];
			return;
		}

		ImportPackageSpecification[] actualImports = getBundle().getImportPackages();
		// Reorder imports so that optionals are at the end so that we wire statics before optionals
		ArrayList importList = new ArrayList(actualImports.length);
		for (int i = actualImports.length - 1; i >= 0; i--)
			if (ImportPackageSpecification.RESOLUTION_OPTIONAL.equals(actualImports[i].getDirective(Constants.RESOLUTION_DIRECTIVE)))
				importList.add(new ResolverImport(this, actualImports[i]));
			else
				importList.add(0, new ResolverImport(this, actualImports[i]));
		imports = (ResolverImport[]) importList.toArray(new ResolverImport[importList.size()]);

		ExportPackageDescription[] actualExports = useSelectedExports ? getBundle().getSelectedExports() : getBundle().getExportPackages();
		exports = new ResolverExport[actualExports.length];
		for (int i = 0; i < actualExports.length; i++)
			exports[i] = new ResolverExport(this, actualExports[i]);

		BundleSpecification[] actualRequires = getBundle().getRequiredBundles();
		requires = new BundleConstraint[actualRequires.length];
		for (int i = 0; i < requires.length; i++)
			requires[i] = new BundleConstraint(this, actualRequires[i]);

		fragments = null;
		fragmentExports = null;
		fragmentImports = null;
		fragmentRequires = null;
	}

	ResolverExport getExport(String name) {
		ResolverExport[] allExports = getExportPackages();
		for (int i = 0; i < allExports.length; i++)
			if (name.equals(allExports[i].getName()))
				return allExports[i];
		return null;
	}

	ResolverExport[] getExports(String name) {
		ArrayList results = new ArrayList(1); // rare to have more than one
		for (int i = 0; i < exports.length; i++)
			if (name.equals(exports[i].getName()))
				results.add(exports[i]);
		return (ResolverExport[]) results.toArray(new ResolverExport[results.size()]);
	}

	// Iterate thru the imports making sure they are wired
	boolean isFullyWired() {
		if (host != null && host.foundMatchingBundles())
			return false;

		ResolverImport[] allImports = getImportPackages();
		for (int i = 0; i < allImports.length; i++)
			if (allImports[i].getMatchingExport() == null && !allImports[i].isOptional() && !allImports[i].isDynamic())
				return false;

		BundleConstraint[] allRequires = getRequires();
		for (int i = 0; i < allRequires.length; i++)
			if (allRequires[i].getMatchingBundle() == null && !allRequires[i].isOptional())
				return false;
		return true;
	}

	void clearWires(boolean clearUnresolvable) {
		ResolverImport[] allImports = getImportPackages();
		for (int i = 0; i < allImports.length; i++) {
			allImports[i].setMatchingExport(null);
			if (clearUnresolvable)
				allImports[i].clearUnresolvableWirings();
		}

		if (host != null)
			host.removeAllMatchingBundles();
		BundleConstraint[] allRequires = getRequires();
		for (int i = 0; i < allRequires.length; i++)
			allRequires[i].setMatchingBundle(null);
	}

	boolean isResolved() {
		return getState() == ResolverBundle.RESOLVED;
	}

	boolean isFragment() {
		return host != null;
	}

	int getState() {
		return state;
	}

	void setState(int state) {
		this.state = state;
	}

	ResolverImport[] getImportPackages() {
		if (isFragment())
			return new ResolverImport[0];
		if (fragments == null || fragments.size() == 0)
			return imports;
		ArrayList resultList = new ArrayList(imports.length);
		for (int i = 0; i < imports.length; i++)
			resultList.add(imports[i]);
		for (Iterator iter = fragments.iterator(); iter.hasNext();) {
			ResolverBundle fragment = (ResolverBundle) iter.next();
			ArrayList fragImports = (ArrayList) fragmentImports.get(fragment.bundleID);
			if (fragImports != null)
				resultList.addAll(fragImports);
		}
		return (ResolverImport[]) resultList.toArray(new ResolverImport[resultList.size()]);
	}

	ResolverExport[] getExportPackages() {
		if (isFragment())
			return new ResolverExport[0];
		if (fragments == null || fragments.size() == 0)
			return exports;
		ArrayList resultList = new ArrayList(exports.length);
		for (int i = 0; i < exports.length; i++)
			resultList.add(exports[i]);
		for (Iterator iter = fragments.iterator(); iter.hasNext();) {
			ResolverBundle fragment = (ResolverBundle) iter.next();
			ArrayList fragExports = (ArrayList) fragmentExports.get(fragment.bundleID);
			if (fragExports != null)
				resultList.addAll(fragExports);
		}
		return (ResolverExport[]) resultList.toArray(new ResolverExport[resultList.size()]);
	}

	ResolverExport[] getSelectedExports() {
		ResolverExport[] allExports = getExportPackages();
		int removedExports = 0;
		for (int i = 0; i < allExports.length; i++)
			if (allExports[i].isDropped())
				removedExports++;
		if (removedExports == 0)
			return allExports;
		ResolverExport[] selectedExports = new ResolverExport[allExports.length - removedExports];
		int index = 0;
		for (int i = 0; i < allExports.length; i++) {
			if (allExports[i].isDropped())
				continue;
			selectedExports[index] = allExports[i];
			index++;
		}
		return selectedExports;
	}

	BundleConstraint getHost() {
		return host;
	}

	BundleConstraint[] getRequires() {
		if (isFragment())
			return new BundleConstraint[0];
		if (fragments == null || fragments.size() == 0)
			return requires;
		ArrayList resultList = new ArrayList(requires.length);
		for (int i = 0; i < requires.length; i++)
			resultList.add(requires[i]);
		for (Iterator iter = fragments.iterator(); iter.hasNext();) {
			ResolverBundle fragment = (ResolverBundle) iter.next();
			ArrayList fragRequires = (ArrayList) fragmentRequires.get(fragment.bundleID);
			if (fragRequires != null)
				resultList.addAll(fragRequires);
		}
		return (BundleConstraint[]) resultList.toArray(new BundleConstraint[resultList.size()]);
	}

	BundleConstraint getRequire(String name) {
		BundleConstraint[] allRequires = getRequires();
		for (int i = 0; i < allRequires.length; i++)
			if (allRequires[i].getVersionConstraint().getName().equals(name))
				return allRequires[i];
		return null;
	}

	public BundleDescription getBundle() {
		return (BundleDescription) getBaseDescription();
	}

	ResolverImport getImport(String name) {
		ResolverImport[] allImports = getImportPackages();
		for (int i = 0; i < allImports.length; i++) {
			if (allImports[i].getName().equals(name)) {
				return allImports[i];
			}
		}
		return null;
	}

	public String toString() {
		return "[" + getBundle() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void initFragments() {
		if (fragments == null)
			fragments = new ArrayList(1);
		if (fragmentExports == null)
			fragmentExports = new HashMap(1);
		if (fragmentImports == null)
			fragmentImports = new HashMap(1);
		if (fragmentRequires == null)
			fragmentRequires = new HashMap(1);
	}

	private boolean isImported(String packageName) {
		ResolverImport[] allImports = getImportPackages();
		for (int i = 0; i < allImports.length; i++)
			if (packageName.equals(allImports[i].getName()))
				return true;

		return false;
	}

	private boolean isExported(String packageName) {
		return getExport(packageName) != null;
	}

	private boolean isRequired(String bundleName) {
		return getRequire(bundleName) != null;
	}

	ResolverExport[] attachFragment(ResolverBundle fragment, boolean addExports) {
		if (isFragment())
			return new ResolverExport[0]; // cannot attach to fragments;
		if (!getBundle().attachFragments() || (isResolved() && !getBundle().dynamicFragments()))
			return new ResolverExport[0]; // host is restricting attachment
		if (fragment.getHost().getMatchingBundles() != null && !((HostSpecification) fragment.getHost().getVersionConstraint()).isMultiHost())
			return new ResolverExport[0]; // fragment is restricting attachment

		ImportPackageSpecification[] newImports = fragment.getBundle().getImportPackages();
		BundleSpecification[] newRequires = fragment.getBundle().getRequiredBundles();
		ExportPackageDescription[] newExports = fragment.getBundle().getExportPackages();

		if (constraintsConflict(newImports, newRequires))
			return new ResolverExport[0]; // do not allow fragments with conflicting constraints
		if (isResolved() && newExports.length > 0)
			fragment.setNewFragmentExports(true);

		initFragments();
		if (fragments.contains(fragment))
			return new ResolverExport[0];
		fragments.add(fragment);
		fragment.getHost().addMatchingBundle(this);

		if (newImports.length > 0) {
			ArrayList hostImports = new ArrayList(newImports.length);
			for (int i = 0; i < newImports.length; i++)
				if (!isImported(newImports[i].getName()))
					hostImports.add(new ResolverImport(this, newImports[i]));
			fragmentImports.put(fragment.bundleID, hostImports);
		}

		if (newRequires.length > 0) {
			ArrayList hostRequires = new ArrayList(newRequires.length);
			for (int i = 0; i < newRequires.length; i++)
				if (!isRequired(newRequires[i].getName()))
					hostRequires.add(new BundleConstraint(this, newRequires[i]));
			fragmentRequires.put(fragment.bundleID, hostRequires);
		}

		ArrayList hostExports = new ArrayList(newExports.length);
		if (newExports.length > 0 && addExports) {
			StateObjectFactory factory = resolver.getState().getFactory();
			for (int i = 0; i < newExports.length; i++) {
				if (!isExported(newExports[i].getName())) {
					ExportPackageDescription hostExport = factory.createExportPackageDescription(newExports[i].getName(), newExports[i].getVersion(), newExports[i].getDirectives(), newExports[i].getAttributes(), newExports[i].isRoot(), getBundle());
					hostExports.add(new ResolverExport(this, hostExport));
				}
			}
			fragmentExports.put(fragment.bundleID, hostExports);
		}
		return (ResolverExport[]) hostExports.toArray(new ResolverExport[hostExports.size()]);
	}

	private boolean constraintsConflict(ImportPackageSpecification[] newImports, BundleSpecification[] newRequires) {
		for (int i = 0; i < newImports.length; i++) {
			ResolverImport importPkg = getImport(newImports[i].getName());
			if (importPkg == null && isResolved())
				return true; // do not allow additional constraints when host is already resolved
			if (importPkg != null && !isIncluded(newImports[i].getVersionRange(), importPkg.getVersionConstraint().getVersionRange()))
				return true; // do not allow conflicting constraints from fragment 
		}
		for (int i = 0; i < newRequires.length; i++) {
			BundleConstraint constraint = getRequire(newRequires[i].getName());
			if (constraint == null && isResolved())
				return true; // do not allow additional constraints when host is already resolved
			if (constraint != null && !isIncluded(newRequires[i].getVersionRange(), constraint.getVersionConstraint().getVersionRange()))
				return true; // do not allow conflicting constraints from fragment
		}
		return false;
	}

	// checks that the inner VersionRange is included in the outer VersionRange
	private static boolean isIncluded(VersionRange outer, VersionRange inner) {
		if (!outer.isIncluded(inner.getMinimum()) && (!inner.getMinimum().equals(outer.getMinimum()) || inner.getIncludeMinimum() != outer.getIncludeMinimum()))
			return false;
		if (!outer.isIncluded(inner.getMaximum()) && (!inner.getMaximum().equals(outer.getMaximum()) || inner.getIncludeMaximum() != outer.getIncludeMaximum()))
			return false;
		return true;
	}

	private void setNewFragmentExports(boolean newFragmentExports) {
		this.newFragmentExports = newFragmentExports;
	}

	boolean isNewFragmentExports() {
		return newFragmentExports;
	}

	ResolverExport[] detachFragment(ResolverBundle fragment) {
		if (isFragment())
			return new ResolverExport[0];
		initFragments();

		if (!fragments.remove(fragment))
			return new ResolverExport[0];

		fragment.getHost().removeMatchingBundle(this);
		fragmentImports.remove(fragment.bundleID);
		fragmentRequires.remove(fragment.bundleID);
		ArrayList removedExports = (ArrayList) fragmentExports.remove(fragment.bundleID);

		return removedExports == null ? new ResolverExport[0] : (ResolverExport[]) removedExports.toArray(new ResolverExport[removedExports.size()]);
	}

	void detachAllFragments() {
		if (fragments == null)
			return;
		ResolverBundle[] allFragments = (ResolverBundle[]) fragments.toArray(new ResolverBundle[fragments.size()]);
		for (int i = 0; i < allFragments.length; i++)
			detachFragment(allFragments[i]);
	}

	boolean isResolvable() {
		return resolvable;
	}

	void setResolvable(boolean resolvable) {
		this.resolvable = resolvable;
	}

	void addExport(ResolverExport re) {
		ResolverExport[] newExports = new ResolverExport[exports.length + 1];
		for (int i = 0; i < exports.length; i++)
			newExports[i] = exports[i];
		newExports[exports.length] = re;
		exports = newExports;
	}

	ResolverImpl getResolver() {
		return resolver;
	}
}
