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
import org.osgi.framework.Constants;

public class GroupingChecker {
	HashMap bundles = new HashMap();

	private ArrayList getConstraints(ResolverExport constrained) {
		HashMap exports = (HashMap) bundles.get(constrained.getExporter());
		if (exports == null)
			return null;
		ArrayList constraints = (ArrayList) exports.get(constrained);
		if (constraints == null)
			return null;
		ArrayList results = new ArrayList(constraints.size());
		for (Iterator iter = constraints.iterator(); iter.hasNext();) {
			Object constraint = iter.next();
			if (constraint instanceof ResolverExport && !results.contains(constraint))
				results.add(constraint);
			else {
				ResolverImport imp = (ResolverImport) constraint;
				if (imp.getMatchingExport() != null) {
					ResolverExport impConstraint = imp.getMatchingExport().getRoot();
					if (impConstraint != null && !results.contains(impConstraint))
						results.add(impConstraint);
				}
			}
		}
		return results;
	}

	private ArrayList createConstraints(ResolverExport constrained) {
		HashMap exports = (HashMap) bundles.get(constrained.getExporter());
		if (exports == null) {
			exports = new HashMap();
			bundles.put(constrained.getExporter(), exports);
		}
		ArrayList constraints = (ArrayList) exports.get(constrained);
		if (constraints == null) {
			constraints = new ArrayList();
			exports.put(constrained, constraints);
		}
		return constraints;
	}

	private void addConstraint(ResolverExport constrained, Object constraint) {
		ArrayList list = createConstraints(constrained);
		if (!list.contains(constraint))
			list.add(constraint);
	}

	private void addConstraints(ResolverExport constrained, ArrayList newConstraints) {
		ArrayList list = createConstraints(constrained);
		for (int i = 0; i < newConstraints.size(); i++) {
			Object constraint = newConstraints.get(i);
			if (!list.contains(constraint))
				list.add(constraint);
		}
	}

	void removeAllExportConstraints(ResolverBundle bundle) {
		bundles.remove(bundle);
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
			ArrayList list = getConstraints(wire);
			if (list != null) {
				for (int j = 0; j < list.size(); j++) {
					ResolverExport re = (ResolverExport) list.get(j);
					if (re.isDropped())
						continue;
					if (re.getExporter().isResolvable() && exp.getName().equals(re.getName()) && !ResolverExport.isOnRootPath(re.getExporter(), imp.getMatchingExport()) && !imp.isOnRootPathSplit(imp.getMatchingExport().getExporter(), re.getExporter()))
						return wire;
				}
			}
			// Check previous wirings against constraints from new wiring
			list = getConstraints(exp);
			if (list != null) {
				for (int j = 0; j < list.size(); j++) {
					ResolverExport re = (ResolverExport) list.get(j);
					if (re.isDropped())
						continue;
					if (re.getExporter().isResolvable() && wire.getName().equals(re.getName()) && !ResolverExport.isOnRootPath(re.getExporter(), imports[i].getMatchingExport()) && !imp.isOnRootPathSplit(imp.getMatchingExport().getExporter(), re.getExporter()))
						return wire;
					// Check against requires
					for (int k = 0; k < requires.length; k++) {
						if (requires[k].getMatchingBundle() == null)
							continue;
						ResolverExport[] exports = requires[k].getMatchingBundle().getExportPackages();
						for (int m = 0; m < exports.length; m++) {
							if (re.getExporter().isResolvable() && exports[m].getName().equals(re.getName()) && !ResolverExport.isOnRootPath(re.getExporter(), exports[m]))
								return re;
						}
					}
				}
			}
		}
		// Check imports against requires
		for (int i = 0; i < requires.length; i++) {
			if (requires[i].getMatchingBundle() == null)
				continue;
			ResolverExport[] exports = requires[i].getMatchingBundle().getExportPackages();
			for (int j = 0; j < exports.length; j++) {
				ArrayList list = getConstraints(exports[j]);
				if (list != null) {
					for (int k = 0; k < list.size(); k++) {
						ResolverExport re = (ResolverExport) list.get(k);
						if (re.getExporter().isResolvable() && exp.getName().equals(re.getName()) && !ResolverExport.isOnRootPath(re.getExporter(), imp.getMatchingExport()))
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
				ArrayList list = getConstraints(exports[j]);
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
								if (exps[n] == constraint || ResolverExport.isOnRootPath(constraint.getExporter(), exps[n])) {
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
	void addInitialGroupingConstraints(ResolverBundle[] initBundles) {
		for (int i = 0; i < initBundles.length; i++) {
			if (bundles.containsKey(initBundles[i]))
				continue; // already processed
			ResolverExport[] exports = initBundles[i].getExportPackages();
			for (int j = 0; j < exports.length; j++) {
				String[] uses = (String[]) exports[j].getExportPackageDescription().getDirective(Constants.USES_DIRECTIVE);
				if (uses == null)
					continue;
				for (int k = 0; k < uses.length; k++) {
					Object constraint = initBundles[i].getExport(uses[k]);
					if (constraint != null) {
						addConstraint(exports[j], constraint);
						addTransitiveGroupingConstraints(exports[j], (ResolverExport) constraint);
					}
					constraint = initBundles[i].getImport(uses[k]);
					if (constraint != null)
						addConstraint(exports[j], constraint);
				}
			}
			if (bundles.get(initBundles[i]) == null)
				bundles.put(initBundles[i], null); // mark this bundle as processed
		}
	}

	// Add constraints from other exports (due to 'uses' being transitive)
	private void addTransitiveGroupingConstraints(ResolverExport export, ResolverExport constraint) {
		if (export == constraint)
			return;
		String[] uses = (String[]) constraint.getExportPackageDescription().getDirective(Constants.USES_DIRECTIVE);
		if (uses == null)
			return;
		for (int i = 0; i < uses.length; i++) {
			Object newConstraint = export.getExporter().getExport(uses[i]);
			if (newConstraint == null)
				newConstraint = export.getExporter().getImport(uses[i]);
			if (newConstraint == null || newConstraint == constraint)
				continue;
			// Check if the constraint has already been added so
			// we don't recurse infinitely
			ArrayList list = getConstraints(export);
			if (list != null && list.contains(newConstraint))
				continue;
			addConstraint(export, newConstraint);
			if (newConstraint instanceof ResolverExport)
				addTransitiveGroupingConstraints(export, (ResolverExport) newConstraint);
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
			ArrayList list = getConstraints(root);
			if (list == null)
				continue;
			addConstraints(exports[i], list);
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
					ArrayList list = getConstraints(requireExports[k]);
					if (list == null)
						continue;
					for (int m = 0; m < list.size(); m++) {
						addConstraint(exports[i], list.get(m));
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
				ArrayList list = getConstraints(requireExports[j]);
				if (list == null)
					continue;
				for (int k = 0; k < list.size(); k++) {
					addConstraint(re, list.get(k));
				}
			}
		}
	}
}
