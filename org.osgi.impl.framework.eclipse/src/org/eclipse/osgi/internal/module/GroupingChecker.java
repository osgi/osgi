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

import java.util.ArrayList;
import java.util.HashMap;

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
			for (int i = 0; i < newConstraints.size(); i++) {
				ResolverExport exp = (ResolverExport) newConstraints.get(i);
				if (!list.contains(exp))
					list.add(newConstraints.get(i));
			}
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
					if (re.isDropped())
						continue;
					if (re.getExporter().isResolvable() && exp.getName().equals(re.getName()) && !imp.isOnRootPath(re.getExporter()) && !imp.isOnRootPathSplit(imp.getMatchingExport().getExporter(), re.getExporter()))
						return wire;
				}
			}
			// Check previous wirings against constraints from new wiring
			list = (ArrayList) constraints.get(exp);
			if (list != null) {
				for (int j = 0; j < list.size(); j++) {
					ResolverExport re = (ResolverExport) list.get(j);
					if (re.isDropped())
						continue;
					if (re.getExporter().isResolvable() && wire.getName().equals(re.getName()) && !imports[i].isOnRootPath(re.getExporter()) && !imp.isOnRootPathSplit(imp.getMatchingExport().getExporter(), re.getExporter()))
						return wire;
					// Check against requires
					for (int k = 0; k < requires.length; k++) {
						if (requires[k].getMatchingBundle() == null)
							continue;
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
			if (requires[i].getMatchingBundle() == null)
				continue;
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

	// Add basic 'uses' constraints
	void addInitialGroupingConstraints(ResolverBundle[] bundles) {
		for (int i = 0; i < bundles.length; i++) {
			ResolverExport[] exports = bundles[i].getExportPackages();
			for (int j = 0; j < exports.length; j++) {
				String[] uses = exports[j].getUses();
				if (uses == null)
					continue;
				for (int k = 0; k < uses.length; k++) {
					ResolverExport exp = bundles[i].getExport(uses[k]);
					if (exp == null)
						continue;
					addConstraint(exports[j], exp);
					addTransitiveGroupingConstraints(exports[j], exp);
				}
			}
		}
	}

	// Add constraints from other exports (due to 'uses' being transitive)
	private void addTransitiveGroupingConstraints(ResolverExport export, ResolverExport constraint) {
		if (export == constraint)
			return;
		String[] uses = constraint.getUses();
		if (uses == null)
			return;
		for (int i = 0; i < uses.length; i++) {
			ResolverExport exp = export.getExporter().getExport(uses[i]);
			if (exp == null || exp == constraint)
				continue;
			// Check if the constraint has already been added so
			// we don't recurse infinitely
			ArrayList list = (ArrayList) constraints.get(export);
			if (list.contains(exp))
				continue;
			addConstraint(export, exp);
			addTransitiveGroupingConstraints(export, exp);
		}
	}

	// Add root constraints to re-exports
	void addReExportConstraints(ResolverBundle bundle) {
		ResolverExport[] exports = bundle.getExportPackages();
		for (int i = 0; i < exports.length; i++) {
			if (exports[i].getExportPackageDescription().isRoot())
				continue;
			ResolverExport root = exports[i].getRoot();
			if (root == null)
				continue;
			ArrayList list = (ArrayList) constraints.get(root);
			if (list == null)
				continue;
			addConstraints(exports[i], list);
		}
	}

	// Add constraints to exports that 'use' imports
	void addImportConstraints(ResolverBundle bundle) {
		ResolverExport[] exports = bundle.getExportPackages();
		for (int i = 0; i < exports.length; i++) {
			String[] uses = exports[i].getUses();
			if (uses == null)
				continue;
			for (int j = 0; j < uses.length; j++) {
				ResolverImport imp = bundle.getImport(uses[j]);
				if (imp == null)
					continue;
				ResolverExport root = imp.getRoot();
				if (root == null)
					continue;
				addConstraint(exports[i], root);
			}
		}
	}

	// Add constraints to re-rexports (via a require)
	void addRequireConstraints(ResolverExport[] exports, ResolverBundle bundle) {
		BundleConstraint[] requires = bundle.getRequires();
		for (int i = 0; i < exports.length; i++) {
			if (exports[i].getExportPackageDescription().isRoot())
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

	// Add constraints for reprovided exports
	void addReprovideConstraints(ResolverExport re) {
		BundleConstraint[] requires = re.getExporter().getRequires();
		for (int i = 0; i < requires.length; i++) {
			if (requires[i].getMatchingBundle() == null)
				return;
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
