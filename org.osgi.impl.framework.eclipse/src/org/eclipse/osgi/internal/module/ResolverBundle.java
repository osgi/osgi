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
import org.osgi.framework.Version;

public class ResolverBundle implements VersionSupplier {
	public static final int UNRESOLVED = 0;
	public static final int RESOLVING = 1;
	public static final int RESOLVED = 2;

	private BundleDescription bundle;
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
	// Store for RESOLVING modules that this module is dependent on (cyclic dependencies)
	private ArrayList cyclicDependencies = new ArrayList();

	private ResolverImpl resolver;
	private boolean newFragmentExports;

	ResolverBundle(BundleDescription bundle, ResolverImpl resolver) {
		this.bundle = bundle;
		this.bundleID = new Long(bundle.getBundleId());
		this.resolver = resolver;
		initialize(bundle.isResolved());
	}

	void initialize(boolean useSelectedExports) {
		if (bundle.getHost() != null) {
			host = new BundleConstraint(this, bundle.getHost());
			exports = new ResolverExport[0];
			imports = new ResolverImport[0];
			requires = new BundleConstraint[0];
			return;
		}

		ImportPackageSpecification[] actualImports = bundle.getImportPackages();
		// Reorder imports so that optionals are at the end so that we wire statics before optionals
		ArrayList importList = new ArrayList(actualImports.length);
		for (int i = actualImports.length - 1; i >= 0; i--) {
			if ((actualImports[i].getResolution() & ImportPackageSpecification.RESOLUTION_OPTIONAL) != 0) {
				importList.add(new ResolverImport(this, actualImports[i]));
			} else {
				importList.add(0, new ResolverImport(this, actualImports[i]));
			}
		}
		imports = (ResolverImport[]) importList.toArray(new ResolverImport[importList.size()]);

		ExportPackageDescription[] actualExports;
		if (useSelectedExports) {
			actualExports = bundle.getSelectedExports();
		} else {
			actualExports = bundle.getExportPackages();
		}
		exports = new ResolverExport[actualExports.length];
		for (int i = 0; i < actualExports.length; i++) {
			exports[i] = new ResolverExport(this, actualExports[i]);
		}

		BundleSpecification[] actualRequires = bundle.getRequiredBundles();
		requires = new BundleConstraint[actualRequires.length];
		for (int i = 0; i < requires.length; i++)
			requires[i] = new BundleConstraint(this, actualRequires[i]);

		fragments = null;
		fragmentExports = null;
		fragmentImports = null;
		fragmentRequires = null;
	}

	boolean isExported(ResolverExport exp) {
		ResolverExport[] exports = getExportPackages();
		for (int i = 0; i < exports.length; i++) {
			if (exp == exports[i]) {
				return true;
			}
		}
		return false;
	}

	ResolverImport getImport(ResolverExport exp) {
		ResolverImport[] imports = getImportPackages();
		for (int i = 0; i < imports.length; i++) {
			if (exp.getName().equals(imports[i].getName())) {
				return imports[i];
			}
		}
		return null;
	}

	ResolverExport getExport(ResolverImport imp) {
		ResolverExport[] exports = getExportPackages();
		for (int i = 0; i < exports.length; i++) {
			if (imp.getName().equals(exports[i].getName()) && exports[i].getExportPackageDescription().isRoot()) {
				return exports[i];
			}
		}
		return null;
	}

	ResolverExport getExport(String name) {
		ResolverExport[] exports = getExportPackages();
		for (int i = 0; i < exports.length; i++) {
			if (name.equals(exports[i].getName())) {
				return exports[i];
			}
		}
		return null;
	}

	// Iterate thru the imports making sure they are wired
	boolean isFullyWired() {
		if (host != null && host.foundMatchingBundles())
			return false;

		ResolverImport[] imports = getImportPackages();
		for (int i = 0; i < imports.length; i++) {
			if (imports[i].getMatchingExport() == null && !imports[i].isOptional() && !imports[i].isDynamic()) {
				return false;
			}
		}

		BundleConstraint[] requires = getRequires();
		for (int i = 0; i < requires.length; i++)
			if (requires[i].getMatchingBundle() == null && !requires[i].isOptional())
				return false;
		return true;
	}

	void clearWires() {
		ResolverImport[] allImports = getImportPackages();
		for (int i = 0; i < allImports.length; i++) {
			allImports[i].setMatchingExport(null);
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
		ResolverExport[] exports = getExportPackages();
		ArrayList removedList = null;
		for (int i = 0; i < exports.length; i++) {
			ResolverImport imp = getImport(exports[i].getName());
			if (imp != null && imp.getMatchingExport() != exports[i] && exports[i].getExportPackageDescription().isRoot()) {
				if (removedList == null)
					removedList = new ArrayList(1);
				removedList.add(exports[i]);
			}
		}
		if (removedList == null)
			return exports;
		ResolverExport[] selectedExports = new ResolverExport[exports.length - removedList.size()];
		ResolverExport[] removedExports = (ResolverExport[]) removedList.toArray(new ResolverExport[removedList.size()]);
		int index = 0;
		for (int i = 0; i < exports.length; i++) {
			boolean removed = false;
			for (int j = 0; j < removedExports.length; j++) {
				if (exports[i] == removedExports[j]) {
					removed = true;
					break;
				}
			}
			if (!removed) {
				selectedExports[index] = exports[i];
				index++;
			}
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

	// Returns true if any cyclic dependencies have been recorded
	boolean isDependentOnCycle() {
		return cyclicDependencies.size() > 0;
	}

	// Record a cyclic dependency (i.e. this module is dependent on the supplied module)
	void recordCyclicDependency(ResolverBundle dependentOn) {
		if (!cyclicDependencies.contains(dependentOn)) {
			cyclicDependencies.add(dependentOn);
		}
	}

	ArrayList getCyclicDependencies() {
		return cyclicDependencies;
	}

	// This method is called by the resolver to tell this modules that a RESOLVING module that it
	// was dependent on has now RESOLVED.  If this is the last/only cyclic dependency then the
	// resolver will move this module into RESOLVED state
	boolean cyclicDependencyResolved(ResolverBundle dependentOn) {
		for (int i = 0; i < cyclicDependencies.size(); i++) {
			if (dependentOn == cyclicDependencies.get(i)) {
				cyclicDependencies.remove(i);
			}
		}
		return !isDependentOnCycle();
	}

	// This method is called by the resolver to tell this modules that a RESOLVING module that it
	// was dependent on is unresolvable. The resolver will move this module into UNRESOLVED state
	void cyclicDependencyFailed(ResolverBundle dependentOn) {
		cyclicDependencies = new ArrayList();
		detachAllFragments();
		ResolverImport[] imports = getImportPackages();
		for (int i = 0; i < imports.length; i++) {
			imports[i].clearUnresolvableWirings();
		}
	}

	public BundleDescription getBundle() {
		return bundle;
	}

	ResolverImport getImport(String name) {
		ResolverImport[] imports = getImportPackages();
		for (int i = 0; i < imports.length; i++) {
			if (imports[i].getName().equals(name)) {
				return imports[i];
			}
		}
		return null;
	}

	public String toString() {
		return "[" + bundle + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public Version getVersion() {
		return bundle.getVersion();
	}

	public String getName() {
		return bundle.getName();
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
		for (int i = 0; i < imports.length; i++)
			if (packageName.equals(imports[i].getName()))
				return true;

		return false;
	}

	private boolean isExported(String packageName) {
		for (int i = 0; i < exports.length; i++)
			if (packageName.equals(exports[i].getName()))
				return true;

		return false;
	}

	private boolean isRequired(String bundleName) {
		for (int i = 0; i < requires.length; i++)
			if (bundleName.equals(requires[i].getVersionConstraint().getName()))
				return true;

		return false;
	}

	ResolverExport[] attachFragment(ResolverBundle fragment, boolean addExports) {
		if (isFragment())
			return new ResolverExport[0]; // cannot attach to fragments;
		if (!bundle.attachFragments() || (isResolved() && !bundle.dynamicFragments()))
			return new ResolverExport[0]; // host is restricting attachment
		if (fragment.getHost().getMatchingBundles() != null && !((HostSpecification) fragment.getHost().getVersionConstraint()).isMultiHost())
			return new ResolverExport[0]; // fragment is restricting attachment

		ImportPackageSpecification[] newImports = fragment.getBundle().getImportPackages();
		BundleSpecification[] newRequires = fragment.getBundle().getRequiredBundles();
		ExportPackageDescription[] newExports = fragment.getBundle().getExportPackages();

		if (isResolved() && (newImports.length > 0 || newRequires.length > 0))
			return new ResolverExport[0]; // do not allow fragments to require new resources on an already resolved host
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
			StateObjectFactory factory = bundle.getContainingState().getFactory();
			for (int i = 0; i < newExports.length; i++) {
				if (!isExported(newExports[i].getName())) {
					ExportPackageDescription hostExport = factory.createExportPackageDescription(newExports[i].getName(), newExports[i].getVersion(), newExports[i].getUses(), newExports[i].getInclude(), newExports[i].getExclude(), newExports[i].getAttributes(), newExports[i].getMandatory(), newExports[i].isRoot(), bundle);
					hostExports.add(new ResolverExport(this, hostExport));
				}
			}
			fragmentExports.put(fragment.bundleID, hostExports);
		}
		return (ResolverExport[]) hostExports.toArray(new ResolverExport[hostExports.size()]);
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

	boolean isDependentOnUnresolvedFragment(ResolverBundle dependent) {
		ResolverImport[] imports = dependent.getImportPackages();
		for (int i = 0; i < imports.length; i++) {
			ResolverExport exp = imports[i].getMatchingExport();
			if (exp == null || exp.getExporter() != this)
				continue;

			if (!isExported(exp))
				return true;
		}
		return false;
	}

	boolean isResolvable() {
		return resolvable;
	}

	void setResolvable(boolean resolvable) {
		this.resolvable = resolvable;
	}

	void addExport(ResolverExport re) {
		ResolverExport[] newExports = new ResolverExport[exports.length + 1];
		for (int i = 0; i < exports.length; i++) {
			newExports[i] = exports[i];
		}
		newExports[exports.length] = re;
		exports = newExports;
	}

	ResolverImpl getResolver() {
		return resolver;
	}
}
