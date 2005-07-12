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
import org.eclipse.osgi.service.resolver.BundleSpecification;
import org.osgi.framework.Constants;

/*
 * The GroupingChecker checks the 'uses' directive on exported packages for consistency
 */
public class GroupingChecker {
	// Maps bundles to their exports; keyed by
	//   ResolverBundle -> exports HashMap
	//     the exports HashMap is keyed by
	//       ResolverExport -> Object[] {'uses' constraint ArrayList, transitive constraint cache} 
	HashMap bundles = new HashMap();

	// Gets all constraints for an exported package.
	// this will perform transitive closure on the uses constraints.
	// this method also will cache the transitive results if the bundle is resolved
	private ResolverExport[] getConstraints(ResolverExport constrained) {
		// check the cache first
		Object[] cachedResults = getCachedConstraints(constrained);
		if (cachedResults != null && cachedResults[1] != null)
			// we have aready cached the results for this return the cached results
			return (ResolverExport[]) cachedResults[1];
		ArrayList resultlist = getConstraintsList(constrained);
		ResolverExport[] results = (ResolverExport[]) resultlist.toArray(new ResolverExport[resultlist.size()]);
		if (constrained.getExporter().isResolved()) {
			// if the bundle is resolved then we can add the results to the cached results
			if (cachedResults == null)
				cachedResults = createConstraintsCache(constrained); // should have been created by getConstraintsList
			cachedResults[1] = results;
		}
		return results;
	}

	private Object[] getCachedConstraints(ResolverExport constrained) {
		// get the cached constraints for this export
		HashMap exports = (HashMap) bundles.get(constrained.getExporter());
		return exports == null ? null : (Object[]) exports.get(constrained);
	}

	// Gets all constraints for an exported package
	// same as getConstraints only the raw ArrayList is returned and
	// the cache is not consulted.
	private ArrayList getConstraintsList(ResolverExport constrained) {
		ArrayList results = new ArrayList();
		getTransitiveConstraints(constrained, results);
		return results;
	}

	// adds the transitive 'uses' constraints for the specified constrained ResolverExport
	// to the results ArrayList.
	private void getTransitiveConstraints(ResolverExport constrained, ArrayList results) {
		if (constrained.isDropped())
			return;
		// first get any constraints that may come from required bundle exports
		ResolverExport[] constrainedRoots = constrained.getRoots();
		for (int i = 0; i < constrainedRoots.length; i++)
			// only search for transitive constraints on other exports (i.e. from required bundles)
			if (constrainedRoots[i] != constrained)
				getTransitiveConstraints(constrainedRoots[i], results);
		// now get the constraints that are specified by this export
		Object[] cachedConstraints = getCachedConstraints(constrained);
		ArrayList constraints = (ArrayList) (cachedConstraints != null ? cachedConstraints[0] : null);
		if (constraints == null)
			return;
		for (Iterator iter = constraints.iterator(); iter.hasNext();) {
			Object constraint = iter.next();
			ResolverExport[] moreConstraints = null;
			if (constraint instanceof ResolverExport) {
				// the constraint is to another export from this bundle
				ResolverExport export = (ResolverExport) constraint;
				// get the roots from this export; incase it is split from a required bundle
				moreConstraints = export.getRoots();
			} else if (constraint instanceof ResolverImport) {
				// the constraint is to an imported package
				ResolverImport imp = (ResolverImport) constraint;
				// if the import is resolved then we need to add the constraints of the roots from the export
				if (imp.getMatchingExport() != null)
					moreConstraints = imp.getMatchingExport().getRoots();
			} else if (constraint instanceof UsesRequiredExport) {
				// the constraint is on a package from a required bundle; get the roots from the required bundles
				moreConstraints = ((UsesRequiredExport) constraint).getRoots();
			}
			if (moreConstraints != null)
				// now add each root as a constraint
				for (int i = 0; i < moreConstraints.length; i++)
					if (!results.contains(moreConstraints[i])) {
						results.add(moreConstraints[i]);
						if (moreConstraints[i] != constraint)
							// add the constraints for each root
							getTransitiveConstraints(moreConstraints[i], results);
					}
		}
	}

	// creates the Object[] used to cache constraint definitions and transitive
	// closure results.
	private Object[] createConstraintsCache(ResolverExport constrained) {
		HashMap exports = (HashMap) bundles.get(constrained.getExporter());
		if (exports == null) {
			exports = new HashMap();
			bundles.put(constrained.getExporter(), exports);
		}
		Object[] constraints = (Object[]) exports.get(constrained);
		if (constraints == null) {
			constraints = new Object[2];
			exports.put(constrained, constraints);
		}
		return constraints;
	}

	// creates an empty list to hold constraints for the specified export
	// if a list already exists then a new list is NOT created.
	private ArrayList createConstraints(ResolverExport constrained) {
		Object[] constraints = createConstraintsCache(constrained);
		if (constraints[0] == null)
			constraints[0] = new ArrayList();
		return (ArrayList) constraints[0];
	}

	// adds a 'uses' constraint to a ResolverExport.  A new 'uses' constraint list
	// is created if one does not exist.
	private void addConstraint(ResolverExport constrained, Object constraint) {
		ArrayList list = createConstraints(constrained);
		if (!list.contains(constraint))
			list.add(constraint);
	}

	// removes all constraints specified by this bundles exports
	void removeAllExportConstraints(ResolverBundle bundle) {
		bundles.remove(bundle);
	}

	// checks the 'uses' consistency of the required BundleConstraint with other required 
	// BundleConstraints of the specified ResolverBundle
	ResolverBundle isConsistent(BundleConstraint req, ResolverBundle bundle) {
		BundleConstraint[] requires = req.getBundle().getRequires();
		ArrayList visited = new ArrayList(requires.length);
		for (int i = 0; i < requires.length; i++) {
			ResolverBundle match = requires[i].getMatchingBundle();
			if (match == bundle || match == null)
				continue; // the constraint has not been resolved or is to ourselves
			// check the consistency of each exported package from the new required match
			ResolverExport[] exports = match.getSelectedExports();
			for (int j = 0; j < exports.length; j++)
				if (checkReqExpConflict(exports[j], getConstraints(exports[j]), bundle, visited) != null)
					return match;
		}
		return null;
	}

	// checks the 'uses consistency of the imported ResolverImport with specified
	// ResolverExport.  This will also check 'uses' consistency with other imported packages
	// and other required bundles of the importing bundle.
	ResolverExport isConsistent(ResolverImport imp, ResolverExport exp) {
		ResolverExport[] expConstraints = getConstraints(exp);
		// Check imports are consistent
		ResolverImport[] imports = imp.getBundle().getImportPackages();
		for (int i = 0; i < imports.length; i++) {
			// Check new wiring constraints against constraints from previous wirings
			ResolverExport conflict = checkImpExpConflict(imp, imports[i].getMatchingExport(), exp, expConstraints);
			if (conflict != null)
				return conflict;
		}
		// Check new wiring against required exports
		BundleConstraint[] requires = imp.getBundle().getRequires();
		ArrayList visited = new ArrayList(requires.length); // visited list prevents recursive cycles
		for (int i = 0; i < requires.length; i++) {
			ResolverExport conflict = checkReqExpConflict(exp, expConstraints, requires[i].getMatchingBundle(), visited);
			if (conflict != null)
				return conflict;
		}
		// No clash, so return null
		return null;
	}

	// checks the 'uses' constraints ResolverImport's matching export with the constraints
	// of the candidate ResolverExport.  If a conflict exists then the conflict is returned.
	private ResolverExport checkImpExpConflict(ResolverImport imp, ResolverExport existing, ResolverExport candidate, ResolverExport[] candConstraints) {
		if (existing == null)
			return null; // for an import that has not been resolved
		// check the constraints of the existing wire against the candidate only 
		// if the existing wire is not the candidate
		if (existing != candidate) {
			ResolverExport[] impConstraints = getConstraints(existing);
			for (int j = 0; j < impConstraints.length; j++)
				if (isConflict(candidate, impConstraints[j]))
					return existing;
		}
		// Check existing wirings against constraints from candidate wiring
		for (int i = 0; i < candConstraints.length; i++) {
			// check if we export this package
			ResolverExport[] importerExp = imp != null ? imp.getBundle().getExports(candConstraints[i].getName()) : new ResolverExport[0];
			for (int j = 0; j < importerExp.length; j++)
				if (!importerExp[j].isDropped() && !isOnRoot(importerExp[j].getRoots(), candConstraints[i]))
					// if we export the package and it is not the same one then we have a clash
					return existing;
			if (isConflict(existing, candConstraints[i]))
				return existing;
		}
		return null;
	}

	// checks the 'uses' constraints ResolverExport with the constraints of the ResolverExports
	// from the specified ResolverBundle.  If a conflict exists then the conflict is returned.
	private ResolverExport checkReqExpConflict(ResolverExport exp, ResolverExport expConstraints[], ResolverBundle bundle, ArrayList visited) {
		if (bundle == null)
			return null;
		if (visited.contains(bundle)) // return if we visited this bundle; prevent endless loops
			return null;
		visited.add(bundle); // mark as visited
		// check the consistency of all the selected exports
		ResolverExport[] exports = bundle.getSelectedExports();
		for (int i = 0; i < exports.length; i++) {
			// first check the constraints of the 'exp' with the exported packages of the 'bundle'
			for (int j = 0; j < expConstraints.length; j++)
				if (isConflict(exports[i], expConstraints[j]))
					return exp;
			// next check the constraints of each exported package of the 'bundle' with the 'exp'
			ResolverExport[] constraints = getConstraints(exports[i]);
			for (int j = 0; j < constraints.length; j++)
				if (isConflict(exp, constraints[j]))
					return exp;
		}
		// check the consistency of all the required bundles which we reexport
		BundleConstraint[] requires = bundle.getRequires();
		for (int i = 0; i < requires.length; i++)
			if (((BundleSpecification) requires[i].getVersionConstraint()).isExported()) {
				ResolverExport conflict = checkReqExpConflict(exp, expConstraints, requires[i].getMatchingBundle(), visited);
				if (conflict != null)
					return exp;
			}
		return null;
	}

	// returns true if the candidateRoot is NOT a root of the candidate ResolverExport
	private boolean isConflict(ResolverExport candidate, ResolverExport candidateRoot) {
		return candidateRoot.getExporter().isResolvable() && candidateRoot.getName().equals(candidate.getName()) && !isOnRoot(candidate.getRoots(), candidateRoot);
	}

	// checks that the specified ResolverExport is contained in the list of roots
	private boolean isOnRoot(ResolverExport[] roots, ResolverExport re) {
		for (int i = 0; i < roots.length; i++)
			// check the exporter; this handles multple exports of the same package name from a bundle
			if (roots[i].getExporter() == re.getExporter())
				return true;
		if (roots.length == 1 && !roots[0].getExportPackageDescription().isRoot())
			return true; // this is for a reexporter that does not have the import satisfied yet
		return false;
	}

	// Add initial 'uses' constraints for a list of bundles
	void addInitialGroupingConstraints(ResolverBundle initBundle) {
		if (bundles.containsKey(initBundle))
			return; // already processed
		// for each export; add it uses constraints
		ResolverExport[] exports = initBundle.getExportPackages();
		for (int j = 0; j < exports.length; j++)
			addInitialGroupingConstraints(exports[j], null);
		if (bundles.get(initBundle) == null)
			bundles.put(initBundle, null); // mark this bundle as processed
	}

	// Add constraints from other exports (due to 'uses' being transitive)
	private void addInitialGroupingConstraints(ResolverExport export, ResolverExport constraint) {
		if (export == constraint)
			return;
		if (constraint == null)
			constraint = export;
		String[] uses = (String[]) constraint.getExportPackageDescription().getDirective(Constants.USES_DIRECTIVE);
		if (uses == null)
			return;
		for (int i = 0; i < uses.length; i++) {
			ResolverExport[] constraintExports = export.getExporter().getExports(uses[i]);
			for (int j = 0; j < constraintExports.length; j++) {
				// Check if the constraint has already been added so we don't recurse infinitely
				if (!getConstraintsList(export).contains(constraintExports[j])) {
					addConstraint(export, constraintExports[j]);
					addInitialGroupingConstraints(export, constraintExports[j]);
				}
			}
			ResolverImport constraintImport = export.getExporter().getImport(uses[i]);
			if (constraintImport != null && !constraintImport.isDynamic())
				addConstraint(export, constraintImport);
			if (constraintExports.length == 0 && (constraintImport == null || constraintImport.isDynamic()))
				addConstraint(export, new UsesRequiredExport(constraint, uses[i]));
		}
	}

	// Used to hold a 'uses' constraint on a package that is accessed through a required bundle
	private class UsesRequiredExport {
		// the exported package which 'uses' a package from a required bundle
		private ResolverExport export;
		// the name of the package which is 'used'
		private String usesName;

		UsesRequiredExport(ResolverExport export, String usesName) {
			this.export = export;
			this.usesName = usesName;
		}

		public ResolverExport[] getRoots() {
			ArrayList results = new ArrayList(1); // rare to have more than 1
			BundleConstraint[] requires = export.getExporter().getRequires();
			for (int i = 0; i < requires.length; i++) {
				if (requires[i].getMatchingBundle() == null)
					continue;
				ResolverExport requiredExport = requires[i].getMatchingBundle().getExport(usesName);
				if (requiredExport != null && !requiredExport.isDropped()) {
					ResolverExport[] roots = requiredExport.getRoots();
					for (int j = 0; j < roots.length; j++)
						if (!results.contains(requiredExport))
							results.add(roots[j]);
				}
			}
			return (ResolverExport[]) results.toArray(new ResolverExport[results.size()]);
		}

		public boolean equals(Object o) {
			if (!(o instanceof UsesRequiredExport))
				return false;
			return ((UsesRequiredExport) o).export.getExporter() == export.getExporter() && usesName.equals(((UsesRequiredExport) o).usesName);
		}
	}
}
