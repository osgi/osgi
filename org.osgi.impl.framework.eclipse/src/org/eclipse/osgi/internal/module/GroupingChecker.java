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
package org.eclipse.osgi.internal.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GroupingChecker {
	HashMap constraints = new HashMap();

	void addConstraint(ResolverExport constrained, ResolverExport constraint) {
		ArrayList list = (ArrayList) constraints.get(constrained);
		if (list == null) {
			list = new ArrayList();
			constraints.put(constrained, list);
		}
		if (!list.contains(constraint))
			list.add(constraint);
	}

	private void addConstraints(ResolverExport constrained, ArrayList newConstraints) {
		ArrayList list = (ArrayList) constraints.get(constrained);
		if (list == null) {
			constraints.put(constrained, newConstraints);
		} else {
			for (int i = 0; i < newConstraints.size(); i++)
				list.add(newConstraints.get(i));
		}
	}

	void removeConstraint(ResolverExport constrained, ResolverExport constraint) {
		ArrayList list = (ArrayList) constraints.get(constrained);
		if (list == null)
			return;
		list.remove(constraint);
	}

	void removeAllConstraints(ResolverExport constrained) {
		constraints.put(constrained, null);
	}

	ResolverExport isConsistent(ResolverImport imp, ResolverExport exp) {
		// Check imports are consistent
		ResolverImport[] imports = imp.getBundle().getImportPackages();
		BundleConstraint[] requires = imp.getBundle().getRequires();
		for (int i = 0; i < imports.length; i++) {
			// Check new wiring against constraints from previous wirings
			ResolverExport wire = imports[i].getMatchingExport();
			if (wire == null)
				continue;
			ArrayList list = (ArrayList) constraints.get(wire);
			if (list != null) {
				for (int j = 0; j < list.size(); j++) {
					ResolverExport re = (ResolverExport) list.get(j);
					if (re.getExporter().isResolvable() && exp.getName().equals(re.getName()) && !imp.isOnRootPath(re.getExporter()) && !imp.isOnRootPathSplit(imp.getMatchingExport().getExporter(), re.getExporter()))
						return wire;
				}
			}
			// Check previous wirings against constraints from new wiring
			list = (ArrayList) constraints.get(exp);
			if (list != null) {
				for (int j = 0; j < list.size(); j++) {
					ResolverExport re = (ResolverExport) list.get(j);
					if (re.getExporter().isResolvable() && wire.getName().equals(re.getName()) && !imports[i].isOnRootPath(re.getExporter()) && !imp.isOnRootPathSplit(imp.getMatchingExport().getExporter(), re.getExporter()))
						return wire;
					// Check against requires
					for (int k = 0; k < requires.length; k++) {
						ResolverExport[] exports = requires[k].getMatchingBundle().getExportPackages();
						for (int m = 0; m < exports.length; m++) {
							if (re.getExporter().isResolvable() && exports[m].getName().equals(re.getName()) && !exports[m].isOnRootPath(re.getExporter()))
								return re;
						}
					}
				}
			}
			// Check against requires (split packages)
			for (int j = 0; j < requires.length; j++) {
				ResolverBundle matchingBundle = requires[j].getMatchingBundle();
				if (matchingBundle == null)
					continue;
				ResolverExport[] exports = matchingBundle.getExportPackages();
				for (int k = 0; k < exports.length; k++) {
					if (imports[i].getName().equals(exports[k].getName()) && imports[i].getMatchingExport() != exports[k])
						return imports[i].getMatchingExport();
				}
			}
		}
		// Check imports against requires
		for (int i = 0; i < requires.length; i++) {
			ResolverExport[] exports = requires[i].getMatchingBundle().getExportPackages();
			for (int j = 0; j < exports.length; j++) {
				ArrayList list = (ArrayList) constraints.get(exports[j]);
				if (list != null) {
					for (int k = 0; k < list.size(); k++) {
						ResolverExport re = (ResolverExport) list.get(k);
						if (re.getExporter().isResolvable() && exp.getName().equals(re.getName()) && !imp.isOnRootPath(re.getExporter()))
							return re;
					}
				}
			}
		}
		// No clash, so return null
		return null;
	}

	boolean checkRequiresConstraints(ResolverBundle bundle) {
		BundleConstraint[] requires = bundle.getRequires();
		if (requires == null)
			return true;
		for (int i = 0; i < requires.length; i++) {
			ResolverBundle matchingBundle = requires[i].getMatchingBundle();
			if (matchingBundle == null)
				continue;
			ResolverExport[] exports = matchingBundle.getExportPackages();
			for (int j = 0; j < exports.length; j++) {
				ArrayList list = (ArrayList) constraints.get(exports[j]);
				if (list == null)
					continue;
				for (int k = 0; k < list.size(); k++) {
					ResolverExport constraint = (ResolverExport) list.get(k);
					boolean foundPotential = false;
					boolean found = false;
					for (int m = 0; m < requires.length; m++) {
						if (requires[m].getMatchingBundle() == null)
							continue;
						ResolverExport[] exps = requires[m].getMatchingBundle().getExportPackages();
						for (int n = 0; n < exps.length; n++) {
							if (constraint.getExporter().isResolvable() && constraint.getName().equals(exps[n].getName())) {
								foundPotential = true;
								if (exps[n] == constraint || exps[n].isOnRootPath(constraint.getExporter())) {
									found = true;
									break;
								}
							}
						}
						if (found)
							break;
					}
					if (foundPotential && !found) {
						return false;
					}
				}
			}
		}
		return true;
	}

	void addInitialGroupingConstraints(ResolverBundle[] bundles) {
		for (int i = 0; i < bundles.length; i++) {
			HashMap groups = new HashMap();
			ResolverExport[] exports = bundles[i].getExportPackages();
			for (int j = 0; j < exports.length; j++) {
				if (!exports[j].getExportPackageDescription().isRoot())
					continue;
				String group = exports[j].getGrouping();
				ArrayList list = (ArrayList) groups.get(group);
				if (list == null) {
					list = new ArrayList();
					groups.put(group, list);
				}
				list.add(exports[j]);
			}
			Iterator it = groups.values().iterator();
			while (it.hasNext()) {
				ArrayList list = (ArrayList) it.next();
				if (list.size() > 1)
					for (int j = 0; j < list.size(); j++)
						addConstraints((ResolverExport) list.get(j), list);
			}
		}
	}

	void addPropagationAndReExportConstraints(ResolverBundle bundle) {
		// Add propagation constraints
		ResolverImport[] imports = bundle.getImportPackages();
		ResolverExport[] exports = bundle.getExportPackages();
		for (int i = 0; i < imports.length; i++) {
			String[] propagates = imports[i].getImportPackageSpecification().getPropagate();
			if (propagates == null)
				continue;
			for (int j = 0; j < propagates.length; j++) {
				for (int k = 0; k < exports.length; k++) {
					String grouping = exports[k].getGrouping();
					if (propagates[j].equals(grouping)) {
						addConstraint(exports[k], imports[i].getMatchingExport());
						addConstraint(exports[k], exports[k]); // making sure the export is included in the constraints
					}
				}
			}
		}
		// Add re-export constraints
		for (int i = 0; i < exports.length; i++) {
			String grouping = exports[i].getGrouping();
			if (grouping != null && !exports[i].getName().equals(grouping)) {
				for (int j = 0; j < exports.length; j++) {
					if (grouping.equals(exports[j].getGrouping())) {
						// we add the constraint both ways.  This allows us to 
						// optimize the default case where grouping == package name.
						// if two packages use one package name as a group then the constraint
						// will get added to both packages.
						// if only one package uses its name as a group then no constraints will
						// get added unless one of the imports uses the same name.
						addConstraint(exports[j], exports[i]);
						addConstraint(exports[i], exports[j]);
					}
					if (!exports[j].getExportPackageDescription().isRoot())
						addConstraint(exports[i], exports[j].getRoot());
				}
			}
			if (exports[i].getExportPackageDescription().isRoot())
				continue;
			ResolverImport imp = bundle.getImport(exports[i]);
			if (imp == null) {
				// Reexport must come from a require
				addReprovideConstraints(exports[i]);
			} else {
				// Reexport is from an import
				addReExportChainConstraints(exports[i], imp.getMatchingExport());
			}
		}
		// Fix up root exports with constraints from re-exports
		for (int i = 0; i < exports.length; i++) {
			if (!exports[i].getExportPackageDescription().isRoot())
				continue;
			String grouping = exports[i].getGrouping();
			if (grouping != null) {
				for (int j = 0; j < exports.length; j++) {
					if (exports[j].getExportPackageDescription().isRoot())
						continue;
					if (grouping.equals(exports[j].getGrouping())) {
						ArrayList list = (ArrayList) constraints.get(exports[j]);
						for (int k = 0; k < list.size(); k++) {
							addConstraint(exports[i], (ResolverExport) list.get(k));
						}
					}
				}
			}
		}
	}

	private void addReExportChainConstraints(ResolverExport reexport, ResolverExport next) {
		if (next == null)
			return;
		// Add constraints from this bundle
		ResolverExport[] grouped = next.getExporter().getGroupedExports(next.getGrouping());
		for (int i = 0; i < grouped.length; i++) {
			addConstraint(reexport, grouped[i]);
		}
		// Recurse if chain continues
		if (!next.getExportPackageDescription().isRoot()) {
			ResolverImport imp = next.getExporter().getImport(next);
			if (imp == null || imp.getMatchingExport() == null)
				return;
			addReExportChainConstraints(reexport, imp.getMatchingExport());

			// Add grouped propagates
			String[] groups = imp.getImportPackageSpecification().getPropagate();
			if (groups == null)
				return;
			ResolverExport[] exports = imp.getBundle().getExportPackages();
			for (int i = 0; i < exports.length; i++) {
				if (exports[i] == next)
					continue;
				for (int j = 0; j < groups.length; j++) {
					if (exports[i].getGrouping().equals(groups[j])) {
						addConstraint(reexport, exports[i]);
						if (!exports[i].getExportPackageDescription().isRoot()) {
							ResolverImport imp2 = exports[i].getExporter().getImport(exports[i]);
							if (imp2 == null || imp2.getMatchingExport() == null)
								continue;
							addReExportChainConstraints(reexport, imp2.getMatchingExport());
						}
					}
				}
			}
		}
	}

	void addRequireConstraints(ResolverExport[] exports, ResolverBundle bundle) {
		BundleConstraint[] requires = bundle.getRequires();
		for (int i = 0; i < exports.length; i++) {
			if (exports[i].getExportPackageDescription().isRoot() && !exports[i].isReprovide())
				continue;
			for (int j = 0; j < requires.length; j++) {
				if (requires[j].getMatchingBundle() == null || exports[i].getExporter() == requires[j].getMatchingBundle())
					continue;
				ResolverExport[] requireExports = requires[j].getMatchingBundle().getExportPackages();
				for (int k = 0; k < requireExports.length; k++) {
					if (!exports[i].getName().equals(requireExports[k].getName()))
						continue;
					ArrayList list = (ArrayList) constraints.get(requireExports[k]);
					if (list == null)
						continue;
					for (int m = 0; m < list.size(); m++) {
						addConstraint(exports[i], (ResolverExport) list.get(m));
					}
				}
			}
		}
	}

	void addReprovideConstraints(ResolverExport re) {
		// Add constraints for reprovided exports
		BundleConstraint[] requires = re.getExporter().getRequires();
		for (int i = 0; i < requires.length; i++) {
			ResolverExport[] requireExports = requires[i].getMatchingBundle().getExportPackages();
			for (int j = 0; j < requireExports.length; j++) {
				if (!re.getName().equals(requireExports[j].getName()))
					continue;
				ArrayList list = (ArrayList) constraints.get(requireExports[j]);
				if (list == null)
					continue;
				for (int k = 0; k < list.size(); k++) {
					addConstraint(re, (ResolverExport) list.get(k));
				}
			}
		}
	}

}
