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
import org.eclipse.osgi.framework.adaptor.FrameworkAdaptor;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.debug.DebugOptions;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.BundleContext;


public class ResolverImpl implements org.eclipse.osgi.service.resolver.Resolver {
	// Debug fields
	private static final String RESOLVER = FrameworkAdaptor.FRAMEWORK_SYMBOLICNAME + "/resolver"; //$NON-NLS-1$
	private static final String OPTION_DEBUG = RESOLVER + "/debug";//$NON-NLS-1$
	private static final String OPTION_WIRING = RESOLVER + "/wiring"; //$NON-NLS-1$
	private static final String OPTION_IMPORTS = RESOLVER + "/imports"; //$NON-NLS-1$
	private static final String OPTION_REQUIRES = RESOLVER + "/requires"; //$NON-NLS-1$
	private static final String OPTION_GROUPING = RESOLVER + "/grouping"; //$NON-NLS-1$
	private static final String OPTION_CYCLES = RESOLVER + "/cycles"; //$NON-NLS-1$
	public static boolean DEBUG = false;
	public static boolean DEBUG_WIRING = false;
	public static boolean DEBUG_IMPORTS = false;
	public static boolean DEBUG_REQUIRES = false;
	public static boolean DEBUG_GROUPING = false;
	public static boolean DEBUG_CYCLES = false;
	
	// Set of bundles that have been removed
	private HashMap removalPending = new HashMap();
	private State state;
	// Repository for exports
	private VersionHashMap resolverExports = null;
	// Repository for bundles
	private VersionHashMap resolverBundles = null;
	// List of unresolved bundles
	private ArrayList unresolvedBundles = null;
	// Temporary repositories used during resolution
	private ArrayList resolvedBundles = null;
	private ArrayList resolvingBundles = null;
	// Lists of cyclic dependencies recording during resolving
	private CyclicDependencyHashMap cyclicDependencies = null;
	// Keys are BundleDescriptions, values are ResolverBundles
	private HashMap bundleMapping = null;
	private boolean initialized = false;
	private PermissionChecker permissionChecker;

	public ResolverImpl() {
		this(null);
	}
	
	public ResolverImpl(BundleContext context) {
		this.permissionChecker = new PermissionChecker(context);
	}

	protected PermissionChecker getPermissionChecker() {
		return permissionChecker;
	}

	// Initializes the resolver
	private void initialize() {
		resolverExports = new VersionHashMap();
		resolverBundles = new VersionHashMap();
		unresolvedBundles = new ArrayList();
		bundleMapping = new HashMap();
		cyclicDependencies = new CyclicDependencyHashMap();
		BundleDescription[] bundles = state.getBundles();

		ArrayList fragmentBundles = new ArrayList();
		// Add each bundle to the resolver's internal state
		for(int i=0; i<bundles.length; i++)
			initResolverBundle(bundles[i], fragmentBundles);
		// Add each removal pending bundle to the resolver's internal state
		BundleDescription[] removedBundles = getRemovalPending();
		for (int i = 0; i < removedBundles.length; i++)
			initResolverBundle(removedBundles[i], fragmentBundles);
		// Iterate over the resolved fragments and attach them to their hosts
		for (Iterator iter = fragmentBundles.iterator(); iter.hasNext();) {
			ResolverBundle fragment = (ResolverBundle) iter.next();
			BundleDescription[] hosts = ((HostSpecification) fragment.getHost().getVersionConstraint()).getHosts();
			for (int i = 0; i < hosts.length; i++) {
				ResolverBundle host = (ResolverBundle) bundleMapping.get(hosts[i]);
				if (host != null)
					// Do not add fragment exports here because they would have been added by the host above.
					host.attachFragment(fragment, false);
			}
		}
		rewireBundles();	// Reconstruct wirings
		setDebugOptions();
		initialized = true;
	}

	private void initResolverBundle(BundleDescription bundleDesc, ArrayList fragmentBundles) {
		ResolverBundle bundle = new ResolverBundle(bundleDesc, this);
		bundleMapping.put(bundleDesc, bundle);
		resolverBundles.put(bundle);
		if(bundleDesc.isResolved()) {
			bundle.setState(ResolverBundle.RESOLVED);
			if (bundleDesc.getHost() != null)
				fragmentBundles.add(bundle);	
		} else {
			unresolvedBundles.add(bundle);
		}
		resolverExports.put(bundle.getExportPackages());
	}
	
	// Re-wire previously resolved bundles
	private void rewireBundles() {
		for(Iterator iter = bundleMapping.values().iterator(); iter.hasNext();) {
			ResolverBundle rb = (ResolverBundle)iter.next();
			if(!rb.getBundle().isResolved() || rb.isFragment())
				continue;
			rewireBundle(rb);
		}
	}
	
	private void rewireBundle(ResolverBundle rb) {
		if(rb.isFullyWired())
			return;
		// Wire requires to bundles
		BundleConstraint[] requires = rb.getRequires();
		for (int i = 0; i < requires.length; i++) {
			rewireRequire(requires[i]);
		}
		// Wire imports to exports
		ResolverImport[] imports = rb.getImportPackages();
		for(int i=0; i<imports.length; i++) {
			rewireImport(imports[i]);
		}
	}
	
	private void rewireRequire(BundleConstraint req) {
		if(req.getMatchingBundle() != null)
			return;
		ResolverBundle matchingBundle = (ResolverBundle)bundleMapping.get(req.getVersionConstraint().getSupplier());
		req.setMatchingBundle(matchingBundle);
		if (matchingBundle == null && !req.isOptional()) {
			System.err.println("Could not find matching bundle for " + req.getVersionConstraint());
			// TODO log error!!
		}
		if(matchingBundle != null) {
			rewireBundle(matchingBundle);
			addRequirePropagationConstraints(req.getBundle(), matchingBundle);			// Add propagation constraints
			addRequireGroupingConstraints(req.getBundle(), req.getMatchingBundle());	// Add grouping constraints
		}
	}
	
	private void rewireImport(ResolverImport imp) {
		if(imp.isDynamic() || imp.getMatchingExport() != null)
			return;
		// Re-wire 'imp'
		ResolverExport matchingExport = null;
		ExportPackageDescription importSupplier = (ExportPackageDescription) imp.getImportPackageSpecification().getSupplier();
		ResolverBundle exporter = importSupplier == null ? null : (ResolverBundle)bundleMapping.get(importSupplier.getExporter());
		VersionSupplier[] matches = resolverExports.getArray(imp.getName());
		for (int j = 0; j < matches.length; j++) {
			ResolverExport export = (ResolverExport) matches[j];
			if (export.getExporter() == exporter && imp.isSatisfiedBy(export)) {
				matchingExport = export;
				break;
			}
		}
		imp.setMatchingExport(matchingExport);
		// Check if we wired to a reprovided package (in which case the ResolverExport doesn't exist)
		if(matchingExport == null && exporter != null) {
			ResolverExport reprovidedExport = new ResolverExport(exporter, importSupplier);
			exporter.addExport(reprovidedExport);
			resolverExports.put(reprovidedExport);
			imp.setMatchingExport(reprovidedExport);
		}
		// If we still have a null wire and it's not optional, then we have an error
		if (imp.getMatchingExport() == null && !imp.isOptional()) {
			System.err.println("Could not find matching export for " + imp.getImportPackageSpecification());
			// TODO log error!!
		}
		if(imp.getMatchingExport() != null) {
			rewireBundle(matchingExport.getExporter());
			checkImportPropagationConstraints(imp, matchingExport);			// Add propagation constraints
		}
	}
	
	// Checks a bundle to make sure it is valid.  If this method returns false for
	// a given bundle, then that bundle will not even be considered for resolution
	private boolean checkBundleManifest(BundleDescription bundle) {
		ImportPackageSpecification[] imports = bundle.getImportPackages();
		ExportPackageDescription[] exports = bundle.getExportPackages();
		for(int i=0; i<imports.length; i++) {
			// Don't allow non-dynamic imports to specify wildcards
			if(imports[i].getResolution() != ImportPackageSpecification.RESOLUTION_DYNAMIC && imports[i].getName().endsWith("*"))
				return false;
			// Don't allow multiple imports of the same package
			for(int j=0; j<i; j++) {
				if(imports[i] != imports[j] && imports[i].getName().equals(imports[j]))
					return false;
			}
		}
		return true;
	}
	
	// Attach fragment to its host
	private void attachFragment(ResolverBundle bundle) {
		if (!bundle.isFragment() || !bundle.isResolvable())
			return;
		VersionConstraint hostSpec = bundle.getHost().getVersionConstraint();
		VersionSupplier[] hosts = resolverBundles.getArray(hostSpec.getName());
		for (int i = 0; i < hosts.length; i++) {
			if (((ResolverBundle)hosts[i]).isResolvable() && hostSpec.isSatisfiedBy(hosts[i].getBundle()))
				resolverExports.put(((ResolverBundle)hosts[i]).attachFragment(bundle, true));
		}
	}
	
	
	public synchronized void resolve(BundleDescription[] reRefresh) {
		if(DEBUG) ResolverImpl.log("*** BEGIN RESOLUTION ***");
		if (state == null)
			throw new IllegalStateException("RESOLVER_NO_STATE"); //$NON-NLS-1$
		
		if(!initialized) {
			initialize();
		}
		
		// Unresolve all the supplied bundles and their dependents
		if (reRefresh != null)
			for(int i=0; i<reRefresh.length; i++) {
				ResolverBundle rb = (ResolverBundle)bundleMapping.get(reRefresh[i]);
				if (rb != null)
					unresolveBundle(rb, false);
			}
		// Initialize the resolving and resolved bundle lists
		resolvingBundles = new ArrayList(unresolvedBundles.size());
		resolvedBundles = new ArrayList(unresolvedBundles.size());

		ResolverBundle[] bundles = (ResolverBundle[])unresolvedBundles.toArray(new ResolverBundle[unresolvedBundles.size()]);
		// First check that all the meta-data is valid for each unresolved bundle
		// This will reset the resolvable flag for each bundle
		for (int i = 0; i < bundles.length; i++)
			bundles[i].setResolvable(checkBundleManifest(bundles[i].getBundle()));

		// First attach all fragments to the matching hosts
		for (int i = 0; i < bundles.length; i++)
			attachFragment(bundles[i]);

		// Attempt to resolve all unresolved bundles
		for(int i=0; i<bundles.length; i++) {
			if(DEBUG) ResolverImpl.log("** RESOLVING " + bundles[i] + " **");
			resolveBundle(bundles[i]);
			// Check for any bundles still in RESOLVING state - these can now be
			// moved to RESOLVED as they are dependent on each other.  Any that
			// could not be resolved would have been moved back to UNRESOLVED
			while(resolvingBundles.size() > 0) {
				ResolverBundle rb = (ResolverBundle)resolvingBundles.get(0);
				if(rb.isFullyWired()) {
					if(DEBUG || DEBUG_CYCLES) ResolverImpl.log("Pushing " + rb + " to RESOLVED");
					setBundleResolved(rb);
				}
			}
		}

		// Resolve all fragments that are still attached to at least one host.
		if (unresolvedBundles.size() > 0) {
			bundles = (ResolverBundle[])unresolvedBundles.toArray(new ResolverBundle[unresolvedBundles.size()]);
			for (int i = 0; i < bundles.length; i++)
				resolveFragment(bundles[i]);
		}

		if(DEBUG_WIRING) printWirings();
		stateResolveBundles();

		// throw away the temporary resolving and resolved bundle lists
		resolvingBundles = null;
		resolvedBundles = null;
		if(DEBUG) ResolverImpl.log("*** END RESOLUTION ***");
	}
	
	
	public synchronized void resolve() {
		resolve(null);
	}

	private void resolveFragment(ResolverBundle fragment) {
		if (!fragment.isFragment())
			return;
		if (fragment.getHost().foundMatchingBundles())
			setBundleResolved(fragment);
	}

	// This method will attempt to resolve the supplied bundle and any bundles that it is dependent on
	private boolean resolveBundle(ResolverBundle bundle) {
		if (bundle.isFragment() || !bundle.isResolvable())
			return false;
		if(bundle.getState() == ResolverBundle.RESOLVED) {
			// 'bundle' is already resolved so just return
			if(DEBUG) ResolverImpl.log("  - " + bundle + " already resolved");
			return true;
		} else if(bundle.getState() == ResolverBundle.UNRESOLVED) {
			// 'bundle' is UNRESOLVED so move to RESOLVING
			bundle.clearWires();
			setBundleResolving(bundle);
		}

		boolean failed = false;

		// Check for singletons
		if (bundle.getBundle().isSingleton()) {
			VersionSupplier[] sameName = resolverBundles.getArray(bundle.getName());
			if (sameName.length > 1) // Need to check if one is already resolved
				for (int i = 0; i < sameName.length; i++) {
					if (sameName[i] == bundle || !sameName[i].getBundle().isSingleton())
						continue;  // Ignore the bundle we are resolving and non-singletons
					if (((ResolverBundle)sameName[i]).isResolved()) {
						failed = true; // Must fail since there is already a resolved bundle
						break;
					}
					if (sameName[i].getVersion().compareTo(bundle.getVersion()) > 0)
						// Attempt to resolve higher version first
						if (resolveBundle((ResolverBundle) sameName[i])) {
							failed = true;  // Must fail since we were able to resolve a higher version
							break;
						}
				}
		}

		if (!failed) {
			// Iterate thru required bundles of 'bundle' trying to find matching bundles.
			BundleConstraint[] requires = bundle.getRequires();
			for (int i = 0; i < requires.length; i++) {
				if (!resolveRequire(requires[i])) {
					if(DEBUG || DEBUG_REQUIRES) ResolverImpl.log("** REQUIRE " + requires[i].getVersionConstraint().getName() + "[" + requires[i].getActualBundle() + "] failed to resolve");
					// If the require has failed to resolve and it is from a fragment, then remove the fragment from the host
					if(requires[i].isFromFragment()) {
						resolverExports.remove(bundle.detachFragment((ResolverBundle) bundleMapping.get(requires[i].getVersionConstraint().getBundle())));
						continue;
					}
					failed = true;
					break;
				}
			}
		}

		if (!failed) {
			// Iterate thru imports of 'bundle' trying to find matching exports.
			ResolverImport[] imports = bundle.getImportPackages();
			for(int i=0; i<imports.length; i++) {
				// Only resolve non-dynamic imports here
				if(!imports[i].isDynamic()) {
					if(!resolveImport(imports[i], true)) {
						if(DEBUG || DEBUG_IMPORTS) ResolverImpl.log("** IMPORT " + imports[i].getName() + "[" + imports[i].getActualBundle() + "] failed to resolve");
						// If the import has failed to resolve and it is from a fragment, then remove the fragment from the host
						if(imports[i].isFromFragment()) {
							resolverExports.remove(bundle.detachFragment((ResolverBundle) bundleMapping.get(imports[i].getImportPackageSpecification().getBundle())));
							continue;
						}
						failed = true;
						break;
					}
				}
			}
		}
		// Need to check that all mandatory imports are wired. If they are then
		// set the bundle RESOLVED, otherwise set it back to UNRESOLVED
		if(!failed && bundle.isFullyWired() && !bundle.isDependentOnCycle()) {
			if(!checkRequirePropagationConstraints(bundle)) {
				setBundleUnresolved(bundle, false);
				if(DEBUG) ResolverImpl.log(bundle + " NOT RESOLVED due to propagation or grouping constraints");
			} else {
				setBundleResolved(bundle);
				if(DEBUG) ResolverImpl.log(bundle + " RESOLVED");
			}
		} else if(failed || !bundle.isFullyWired()) {
			setBundleUnresolved(bundle, false);
			if(DEBUG) ResolverImpl.log(bundle + " NOT RESOLVED");
		}
		// Check cyclic dependencies
		ArrayList v = (ArrayList)cyclicDependencies.remove(bundle);
		if(v != null) {
			// We have some cyclic dependencies on 'bundle', so iterate thru
			// them and inform each one whether 'bundle' has resolved or not
			for(int i=0; i<v.size(); i++) {
				ResolverBundle dependent = (ResolverBundle)v.get(i);
				if(bundle.isDependentOnUnresolvedFragment(dependent)) {
					dependent.cyclicDependencyFailed(bundle);
					setBundleUnresolved(dependent, false);
					if(DEBUG_CYCLES) ResolverImpl.log("Setting dependent bundle (" + dependent + ") to unresolved (due to fragment)");
				} else if(bundle.getState() == ResolverBundle.RESOLVED) {
					// Tell dependent bundles that we have resolved so that they can resolve
					// (they may be dependent on other bundles as well though)
					if(dependent.cyclicDependencyResolved(bundle)) {
						setBundleResolved(dependent);
						if(DEBUG_CYCLES) ResolverImpl.log("Telling dependent bundle (" + dependent + ") that " + bundle + " has resolved");
					}
				} else if(bundle.getState() == ResolverBundle.UNRESOLVED) {
					// Tell dependent bundles that we have failed to
					// resolve - this forces them to be unresolved
					dependent.cyclicDependencyFailed(bundle);
					setBundleUnresolved(dependent, false);
					if(DEBUG_CYCLES) ResolverImpl.log("Setting dependent bundle (" + dependent + ") to unresolved");
				}
			}
		}
		if(bundle.getState() == ResolverBundle.UNRESOLVED)
			bundle.setResolvable(false);		// Set it to unresolvable so we don't attempt to resolve it again in this round
		
		// tell the state what we resolved the constraints to
		stateResolveConstraints(bundle);
		return bundle.getState() != ResolverBundle.UNRESOLVED;
	}

	// Resolve the supplied import. Returns true if the import can be resolved, false otherwise
	private boolean resolveRequire(BundleConstraint req) {
		if(DEBUG_REQUIRES) ResolverImpl.log("Trying to resolve: " + req.getBundle() + ", " + req.getVersionConstraint());
		if(req.getMatchingBundle() != null) {
			if(DEBUG_REQUIRES) ResolverImpl.log("  - already wired");
			return true;	// Already wired (due to grouping dependencies) so just return
		}
		VersionSupplier[] bundles = resolverBundles.getArray(req.getVersionConstraint().getName());
		for(int i=0; i<bundles.length; i++) {
			ResolverBundle bundle = (ResolverBundle) bundles[i];
			if(DEBUG_REQUIRES) ResolverImpl.log("CHECKING: " + bundle.getBundle());
			// Check if export matches
			if(req.isSatisfiedBy(bundle)) {
				int originalState = bundle.getState();
				req.setMatchingBundle(bundle);			// Wire to the bundle
				if(req.getBundle() == bundle) 
					return true;						// Wired to ourselves
				if((originalState == ResolverBundle.UNRESOLVED && !resolveBundle(bundle))) {
					req.setMatchingBundle(null);
					continue;							// Bundle hasn't resolved
				}
				addRequirePropagationConstraints(req.getBundle(), req.getMatchingBundle());
				addRequireGroupingConstraints(req.getBundle(), req.getMatchingBundle());
				// Check cyclic dependencies
				if(originalState == ResolverBundle.RESOLVING) {
					// If the bundle was RESOLVING, we have a cyclic dependency
					cyclicDependencies.put(bundle, req.getBundle());
					req.getBundle().recordCyclicDependency(bundle);
				} else if(originalState == ResolverBundle.UNRESOLVED && bundle.getState() == ResolverBundle.RESOLVING) {
					// If the bundle has gone from UNRESOLVED to RESOLVING, we have a cyclic dependency
					ArrayList exportersCyclicDependencies = bundle.getCyclicDependencies();
					for(int k=0; k<exportersCyclicDependencies.size(); k++) {
						ResolverBundle dependentOn = (ResolverBundle)exportersCyclicDependencies.get(k);
						if(dependentOn != req.getBundle()) {
							cyclicDependencies.put(dependentOn, req.getBundle());
							req.getBundle().recordCyclicDependency(dependentOn);
						}
					}
				}
				if(DEBUG_REQUIRES) ResolverImpl.log("Found match: " + bundle.getBundle() + ". Wiring");
				return true;
			}
		}
		if(req.isOptional())
			return true;	// If the req is optional then just return true

		return false;
	}

	// Resolve the supplied import. Returns true if the import can be resolved, false otherwise
	private boolean resolveImport(ResolverImport imp, boolean checkReexportsFromRequires) {
		if(DEBUG_IMPORTS) ResolverImpl.log("Trying to resolve: " + imp.getBundle() + ", " + imp.getName());
		if(imp.getMatchingExport() != null) {
			if(DEBUG_IMPORTS) ResolverImpl.log("  - already wired");
			return true;	// Already wired (due to grouping dependencies) so just return
		}
		VersionSupplier[] exports = resolverExports.getArray(imp.getName());
		for(int i=0; i<exports.length; i++) {
			ResolverExport export = (ResolverExport) exports[i];
			if(DEBUG_IMPORTS) ResolverImpl.log("CHECKING: " + export.getExporter().getBundle() + ", " + exports[i].getName());
			// Check if export matches
			if(imp.isSatisfiedBy(export) && imp.isNotAnUnresolvableWiring(export)) {
				int originalState = export.getExporter().getState();
				if(imp.isDynamic() && originalState != ResolverBundle.RESOLVED)
					return false; 						// Must not attempt to resolve an exporter when dynamic
				if(imp.getBundle() == export.getExporter() && !export.getExportPackageDescription().isRoot())
					continue;							// Can't wire to our own re-export
				imp.setMatchingExport(export);			// Wire the import to the export
				if(imp.getBundle() == export.getExporter()) 
					return true;						// Wired to our own export
				ResolverExport exp = imp.getBundle().getExport(imp);
				if(exp != null)
					resolverExports.remove(exp);		// Import wins, remove export
				if((originalState == ResolverBundle.UNRESOLVED && !resolveBundle(export.getExporter())) || !resolverExports.contains(export)) {
					if (exp != null)
						resolverExports.put(exp);
					imp.setMatchingExport(null);
					continue;							// Bundle hasn't resolved || export has not been selected and is unavailable
				}
				// Check grouping dependencies
				if(checkAndResolveDependencies(imp, imp.getMatchingExport())) {
					if(imp.getMatchingExport() != export)
						return true;					// Grouping has changed the wiring, so return here
					// Add dependencies on re-exports to keep wirings consistent
					if(!imp.getMatchingExport().getExportPackageDescription().isRoot()) {
						ResolverExport root = imp.getMatchingExport().getRoot();
						ResolverExport[] groupedExports = root.getExporter().getGroupedExports(root.getGrouping());
						for(int j=0; j<groupedExports.length; j++)
							imp.getBundle().addPropagationConstraint(groupedExports[j].getRoot());
					}
					// Record any cyclic dependencies
					if(originalState == ResolverBundle.RESOLVING) {
						// If the exporter was RESOLVING, we have a cyclic dependency
						cyclicDependencies.put(export.getExporter(), imp.getBundle());
						imp.getBundle().recordCyclicDependency(export.getExporter());
					} else if(originalState == ResolverBundle.UNRESOLVED && export.getExporter().getState() == ResolverBundle.RESOLVING) {
						// If the exporter has gone from UNRESOLVED to RESOLVING, we have a cyclic dependency
						ArrayList exportersCyclicDependencies = export.getExporter().getCyclicDependencies();
						for(int k=0; k<exportersCyclicDependencies.size(); k++) {
							ResolverBundle dependentOn = (ResolverBundle)exportersCyclicDependencies.get(k);
							if(dependentOn != imp.getBundle()) {
								cyclicDependencies.put(dependentOn, imp.getBundle());
								imp.getBundle().recordCyclicDependency(dependentOn);
							}
						}
					}
					if(DEBUG_IMPORTS) ResolverImpl.log("Found match: " + export.getExporter() + ". Wiring " + imp.getBundle() + ":" + imp.getName());
					return true;
				} else if(!imp.getBundle().isResolvable()) {
					// If grouping has caused recursive calls to resolveImport, and the grouping has failed
					// then we need to catch that here, so we don't continue trying to wire here
					return false;
				}
			}
		}
		if(checkReexportsFromRequires && resolveImportReprovide(imp))
			return true;	// A reprovide satisfies imp
		if(imp.isOptional())
			return true;	// If the import is optional then just return true

		return false;
	}
	
	// Check if the import can be resolved to a reprovided package (has no export object to match to)
	private boolean resolveImportReprovide(ResolverImport imp) {
		String bsn = imp.getImportPackageSpecification().getBundleSymbolicName();
		// If no symbolic name specified then just return (since this is a
		// re-export an import not specifying a bsn will wire to the root)
		if(bsn == null) {
			return false;
		}
		if(DEBUG_IMPORTS) ResolverImpl.log("Checking reprovides: " + imp.getName());
		ResolverBundle rb;
		// Find bundle with specified bsn
		for(Iterator iter = bundleMapping.values().iterator(); iter.hasNext();) {
			rb = (ResolverBundle)iter.next();
			if(bsn.equals(rb.getBundle().getSymbolicName()) && !rb.isFragment()) {
				if(resolveBundle(rb))
					if(resolveImportReprovide0(imp, rb, rb))
						return true;
			}
		}
		return false;
	}
	
	private boolean resolveImportReprovide0(ResolverImport imp, ResolverBundle reexporter, ResolverBundle rb) {
		BundleConstraint[] requires = rb.getRequires();
		for(int i=0; i<requires.length; i++) {
			if(!((BundleSpecification)requires[i].getVersionConstraint()).isExported())
				continue;		// Skip require if it doesn't reprovide the packages
			// Check exports to see if we've found the root
			ResolverExport[] exports = requires[i].getMatchingBundle().getExportPackages();
			for(int j=0; j<exports.length; j++) {
				if(imp.getName().equals(exports[j].getName())) {
					ExportPackageDescription epd = state.getFactory().createExportPackageDescription(
														exports[j].getName(),
														exports[j].getVersion(),
														exports[j].getName(),
														exports[j].getExportPackageDescription().getInclude(),
														exports[j].getExportPackageDescription().getExclude(),
														exports[j].getExportPackageDescription().getAttributes(),
														exports[j].getExportPackageDescription().getMandatory(),
														false,
														reexporter.getBundle());
					if(imp.getImportPackageSpecification().isSatisfiedBy(epd)) {
						// Create reexport and add to bundle and resolverExports
						if(DEBUG_IMPORTS) ResolverImpl.log(" - Creating re-export for reprovide: " + reexporter + ":" + epd.getName());
						ResolverExport re = new ResolverExport(reexporter, epd);
						reexporter.addExport(re);
						resolverExports.put(re);
						// Resolve import
						if(resolveImport(imp, false))
							return true;
					}
				}
			}
			// Check requires of matching bundle (recurse down the chain)
			if(resolveImportReprovide0(imp, reexporter, requires[i].getMatchingBundle()))
				return true;
		}
		return false;
	}
	
	
	
	// This method checks and resolves (if possible) grouping dependencies
	// Returns true, if the dependencies can be resolved, false otherwise
	private boolean checkAndResolveDependencies(ResolverImport imp, ResolverExport exp) {
		ResolverBundle importOwner = imp.getBundle();
		ResolverImport[] imports = importOwner.getImportPackages();
		
		if(DEBUG_GROUPING) ResolverImpl.log("Checking grouping dependencies: " + imp.getName() + ", " + imp.getImportPackageSpecification().getVersionRange());
		// Check grouping dependencies from propagating imports
		ResolverExport conflict = checkImportPropagationConstraints(imp, exp); 
		if(conflict != null) {
			// Try to wire 'imp' to a different export
			imp.addUnresolvableWiring(exp.getExporter());
			imp.setMatchingExport(null);
			boolean resolved = resolveImport(imp, false);
			if(!resolved) {
				if(imp.isDynamic())
					return false;		// Cannot back-off other wirings for a dynamic import

				imp.removeUnresolvableWiring(exp.getExporter());
				imp.setMatchingExport(exp);
				ResolverImport conflictImp = null;
				for(int i=0; i<imports.length; i++)
					if(conflict.getName().equals(imports[i].getName()))
						conflictImp = imports[i];
				// We couldn't wire 'imp' elsewhere so do the back-off
				if(conflictImp.isNotAnUnresolvableWiring(conflict)) {
					conflictImp.addUnresolvableWiring(conflictImp.getMatchingExport().getExporter());
					if(DEBUG_GROUPING) ResolverImpl.log("== " + conflictImp.getName() + "[" + conflictImp.getActualBundle() + "]" + " cannot be wired to " + conflictImp.getMatchingExport().getName() + "[" + conflictImp.getMatchingExport().getBundle() + "] as it breaks the propagation dependency with " + imp.getName() + "[" + imp.getActualBundle() + "] -> " + imp.getMatchingExport().getName() + "[" + imp.getMatchingExport().getBundle() + "]");
					importOwner.clearWires();
					resolveBundle(importOwner);
				}
				// If the bundle is not resolved at this point then we cannot resolve the propagation dependencies
				if(!importOwner.isResolved())
					return false;
			}
		}
		ResolverBundle exportSupplier = imp.getMatchingExport().getExporter();
		ResolverExport[] exports = exportSupplier.getExportPackages();
		// Check for grouping dependencies on exports
		for(int i=0; i<exports.length; i++) {
			if(exp == exports[i])
				continue;
			if(!exp.getGrouping().equals(exports[i].getGrouping()))
				continue;
			importOwner.addPropagationConstraint(exports[i].getRoot());
			for(int j=0; j<imports.length; j++) {
				if(imp == imports[j])
					continue;
				if(exports[i].getName().equals(imports[j].getName())) {
					if(imports[j].isSatisfiedBy(exports[i]) && imports[j].isNotAnUnresolvableWiring(exports[i])) {
						// We have a match
						if(doMatchingDependency(imp, imports[j], exp, exports[i]))
							continue;
						else
							return false;
					} else {
						// The dependency cannot be resolved
						if(doUnresolvableDependency(imp, imports[j], exp, exports[i]))
							continue;
						else
							return false;
					}
				}
			}
		}
		return true;
	}
	
	// Process grouping dependencies, when an import can be satisfied by the grouped export
	private boolean doMatchingDependency(ResolverImport imp, ResolverImport imp_j, ResolverExport exp, ResolverExport exp_i) {
		ResolverBundle importOwner = imp.getBundle();
		if(imp_j.getMatchingExport() != null && imp_j.getMatchingExport().getExporter() != exp.getExporter()) {
			// If the roots are the same, then it's ok
			if(imp_j.isOnRootPath(exp.getExporter()))
				return true;
			// The import has already been wired, so we have a clash - need to back off
			if(DEBUG_GROUPING) ResolverImpl.log("Grouping dependency clash for " + imp_j.getName() + ".  Backing Off");
			// If we had previously wired an optional import, unwire it and record that it
			// cannot be connected to either it's existing wiring or the conflicting wiring
			if(imp_j.isOptional()) {
				imp_j.addUnresolvableWiring(imp_j.getMatchingExport().getExporter());
				imp_j.addUnresolvableWiring(exp_i.getExporter());
				imp_j.setMatchingExport(null);
				return true;
			}
			// We need to attempt to wire 'imp' to other exports before
			// forcing a real back-off (which will affect other wirings)
			ResolverExport export = imp.getMatchingExport();
			imp.addUnresolvableWiring(export.getExporter());
			imp.setMatchingExport(null);
			if(resolveImport(imp, false)) {
				return true;
			} else if(imp.isDynamic()) {
				if(DEBUG_GROUPING) ResolverImpl.log("=- " + imp.getName() + "[" + imp.getActualBundle() + "]" + " cannot be wired to " + exp.getName() + "[" + exp.getBundle() + "] as it breaks the grouping dependency with " + imp_j.getName() + "[" + imp_j.getActualBundle() + "] -> " + exp_i.getName() + "[" + imp_j.getMatchingExport().getBundle() + "]");
				return false;		// Cannot back-off other wirings for a dynamic import
			} else {
				imp.removeUnresolvableWiring(export.getExporter());
				imp.setMatchingExport(export);
			}
			// We couldn't wire 'imp' elsewhere so do the back-off
			if(imp_j.isNotAnUnresolvableWiring(exp_i)) {
				imp_j.addUnresolvableWiring(imp_j.getMatchingExport().getExporter());
				if(DEBUG_GROUPING) ResolverImpl.log("== " + imp_j.getName() + "[" + imp_j.getActualBundle() + "]" + " cannot be wired to " + imp_j.getMatchingExport().getName() + "[" + imp_j.getMatchingExport().getBundle() + "] as it breaks the grouping dependency with " + imp.getName() + "[" + imp.getActualBundle() + "] -> " + imp.getMatchingExport().getName() + "[" + imp.getMatchingExport().getBundle() + "]");
				importOwner.clearWires();
				resolveBundle(importOwner);
			}
			// If the bundle is not resolved at this point then we cannot resolve the grouping dependencies
			if(!importOwner.isResolved())
				return false;
			else
				return true;
		}
		// There's no wiring clash so wire the import to the export
		if((imp.isDynamic() == imp_j.isDynamic())) {
			if(DEBUG_GROUPING) ResolverImpl.log("Grouping dependency for " + imp_j.getName() + ".  Wiring");
			imp_j.setMatchingExport(exp_i);
		}
		return true;
	}
	
	// Process grouping dependencies, when an import cannot be satisfied by the grouped export (but the package name matches)
	private boolean doUnresolvableDependency(ResolverImport imp, ResolverImport imp_j, ResolverExport exp, ResolverExport exp_i) {
		// If the wiring is consistent then we don't need to be wired to the exact same bundle
		if(imp_j.isOnRootPath(exp.getExporter()))
			return true;
		// Deal with dynamic imports
		if(imp.isDynamic()) {
			imp.addUnresolvableWiring(exp.getExporter());
			imp.setMatchingExport(null);
			return false;
		}
		
		ResolverBundle importOwner = imp.getBundle();
		if(DEBUG_GROUPING) ResolverImpl.log("Unresolvable grouping dependency for " + imp_j.getName() + ".  Backing Off");
		if(imp_j.isOptional()) {
			imp_j.setMatchingExport(null);
			return true;
		}
		imp.addUnresolvableWiring(exp.getExporter());
		if(DEBUG_GROUPING) ResolverImpl.log("= " + imp.getName() + "[" + imp.getActualBundle() + "]" + " cannot be wired to " + exp.getName() + "[" + exp.getBundle() + "] as a grouped export " + exp_i.getName() + "[" + exp_i.getBundle() + "] does not satisfy " + imp_j.getName() + "[" + imp_j.getActualBundle() + "]");
		importOwner.clearWires();
		if(imp_j.isNotAnUnresolvableWiring(exp_i)) {
			resolveBundle(importOwner);
		}
		// If the bundle is not resolved at this point then we cannot resolve the grouping dependencies
		if(!importOwner.isResolved())
			return false;
		else
			return true;
	}

	// Check if we are consistent with propagation constraints
	// Returns the clashing root export, or null if none
	private ResolverExport checkImportPropagationConstraints(ResolverImport imp, ResolverExport matchingExport) {
		ResolverBundle rb = imp.getBundle();
		// Gather new constraints (from matching bundle)
		// Any imports propagated into the same group as 'matchingExport' are recorded
		HashMap propagationConstraints = new HashMap();
		String exportGrouping = matchingExport.getGrouping();
		ResolverImport[] matchingImports = matchingExport.getExporter().getImportPackages();
		String[] reexportGrouping = null;
		for(int i=0; i<matchingImports.length; i++) {
			if(matchingImports[i].getImportPackageSpecification().getPropagate() != null) {
				if(!matchingExport.getExportPackageDescription().isRoot() && matchingExport.getName().equals(matchingImports[i].getName()))
					reexportGrouping = matchingImports[i].getImportPackageSpecification().getPropagate();
				String[] propagationGrouping = matchingImports[i].getImportPackageSpecification().getPropagate();
				for(int j=0; j<propagationGrouping.length; j++) {
					if(exportGrouping.equals(propagationGrouping[j])) {
						propagationConstraints.put(matchingImports[i].getName(), matchingImports[i].getRoot());
						break;
					}
				}
			}
		}
		// Add exports that are grouped with propagated imports that are then rexported
		if(reexportGrouping != null) {
			ResolverExport[] exports = matchingExport.getExporter().getExportPackages();
			for(int i=0; i<exports.length; i++)
				for(int j=0; j<reexportGrouping.length; j++)
					if(exports[i].getGrouping().equals(reexportGrouping[j])) {
						propagationConstraints.put(exports[i].getName(), exports[i].getRoot());
						break;
					}
		}
		// Check all own imports to make sure wirings are consistent
		ResolverImport[] imports = rb.getImportPackages();
		for(int i=0; i<imports.length; i++) {
			// Make sure the import is wired
			if(imports[i].getMatchingExport() == null)
				continue;
			// Check against matching bundle's constraints
			ResolverExport root = imports[i].getRoot();
			ResolverExport conflict = matchingExport.getExporter().checkPropagationConstraint(root);
			if(conflict != null) {
				// Check that this really is a conflict:
				// We record everything that could potentially be a conflict, but it is
				// only a conflict if the 'conflicting' export is imported by our matching
				// bundle and grouped with the export we want
				ResolverImport[] exporterImports = imp.getMatchingExport().getExporter().getImportPackages();
				for(int j=0; j<exporterImports.length; j++) {
					if(imports[i].getName().equals(exporterImports[j].getName()) && imp.getMatchingExport().getGrouping() != null) {
						String[] propagates = exporterImports[j].getImportPackageSpecification().getPropagate();
						if(propagates == null)
							continue;
						for(int k=0; k<propagates.length; k++) {
							if(imp.getMatchingExport().getGrouping().equals(propagates[k])) {
								if(DEBUG_GROUPING) ResolverImpl.log("  Propagation constraint FAILED - matching bundle's existing constraints: " + imports[i].getName());
								return conflict;
							}
						}
					}
				}
			}
			// Check against matching bundle's imports
			conflict = (ResolverExport)propagationConstraints.get(imports[i].getName());
			if(conflict != null && root != conflict) {
				if(DEBUG_GROUPING) ResolverImpl.log("  Propagation constraint FAILED - matching bundle's new constraints: " + imports[i].getName());
				return conflict;
			}
			// Check against constraints found so far for imp's owning bundle
			conflict = rb.checkPropagationConstraint(root);
			if(conflict != null) {
				if(DEBUG_GROUPING) ResolverImpl.log("  Propagation constraint FAILED - owning bundle's constraints: " + imports[i].getName());
				return conflict;
			}
		}
		// Add propagation constraints from matching bundle
		Iterator it = matchingExport.getExporter().getPropagationConstraints().iterator();
		while(it.hasNext()) {
			ArrayList list = (ArrayList)it.next();
			for(int i=0; i<list.size(); i++) 
				rb.addPropagationConstraint((ResolverExport)list.get(i));
		}
		// Add constraints from matching bundle's imports
		it = propagationConstraints.values().iterator();
		while(it.hasNext())
			rb.addPropagationConstraint((ResolverExport)it.next());
		// No conflicts, so return null
		return null;
	}
	
	// Record grouping constraints from a required bundle
	private void addRequireGroupingConstraints(ResolverBundle rb, ResolverBundle matchingBundle) {
		ResolverExport[] exports = matchingBundle.getExportPackages();
		for(int i=0; i<exports.length; i++) {
			if(!exports[i].getExportPackageDescription().isRoot()) {
				// Have a re-export, so need to add grouping constraints
				ResolverImport imp = matchingBundle.getImport(exports[i].getName());
				ResolverExport exp;
				if (imp != null)
					exp = imp.getMatchingExport();
				else
					exp = exports[i].getRoot();
				if(exp != null) {
					ResolverExport[] groupedExports = exp.getExporter().getGroupedExports(exp.getGrouping());
					for(int j=0; j<groupedExports.length; j++) {
						rb.addPropagationConstraint(groupedExports[j].getRoot());
						if(!groupedExports[j].getExportPackageDescription().isRoot()) {
							if(exp != null) {
								addRequireGroupingConstraints(rb, exp.getExporter());
							}
						}
					}
				}
			}
		}
	}
	
	// Record propagation constraints from a required bundle
	private void addRequirePropagationConstraints(ResolverBundle rb, ResolverBundle matchingBundle) {
		// Add propagation constraints from matchingBundle
		ResolverImport[] imports = matchingBundle.getImportPackages();
		for(int i=0; i<imports.length; i++) {
			if(imports[i].getImportPackageSpecification().getPropagate() != null) {
				ResolverExport exp = rb.getPropagationConstraint(imports[i].getName());
				rb.addPropagationConstraint(imports[i].getRoot());
			}
		}
		// Add constraints from down the chain
		Iterator it = matchingBundle.getPropagationConstraints().iterator();
		while(it.hasNext()) {
			ArrayList list = (ArrayList)it.next();
			for(int i=0; i<list.size(); i++) 
				rb.addPropagationConstraint((ResolverExport)list.get(i));
		}
	}

	// This needs to be called after all Requires and Imports have been satisfied.
	// We cannot check propagation in the same way as Imports because Requires are
	// allowed 'clashes' (i.e. multiple parts of the same package) due to split packages.
	// So now we need to iterate through all the recorded propagation constraints for this
	// bundle, and make sure we import them all either through Requires or through imports
	private boolean checkRequirePropagationConstraints(ResolverBundle rb) {
		if(rb.getRequires().length == 0)
			return true;		// No requires specified so return true
		// Iterate through the recorded propagation constraints
		PropagationTrackingHashMap constraints = rb.getPropagationConstraints();
		Iterator it = constraints.iterator();
		while(it.hasNext()) {
			ArrayList list = (ArrayList)it.next();
			for(int i=0; i<list.size(); i++) {
				boolean foundPotential = false;
				boolean foundMatch = false;
				ResolverExport re = (ResolverExport)list.get(i);
				// Check requires for wiring to 're'
				BundleConstraint[] requires = rb.getRequires();
				for(int j=0; j<requires.length; j++) {
					ResolverExport[] exports = requires[j].getMatchingBundle().getExportPackages();
					for(int k=0; k<exports.length; k++) {
						if(re.getName().equals(exports[k].getName()))
								foundPotential = true;
						if(re == exports[k].getRoot()) {
							foundMatch = true;
							break;
						}
					}
					if(foundMatch) break;
				}
				if(foundMatch) continue;
				// Check imports for wiring to 're'
				ResolverImport[] imports = rb.getImportPackages();
				for(int j=0; j<imports.length; j++) {
					if(re.getName().equals(imports[i].getName()))
						foundPotential = true;
					if(re == imports[i].getRoot()) {
						foundMatch = true;
						break;
					}
				}
				// If not imported or required, then fail
				if(foundPotential && !foundMatch) {
					if(DEBUG_GROUPING) ResolverImpl.log("  Propagation or grouping constraint FAILED for requires: " + re.getName());
					return false;
				}
			}
		}
		return true;
	}

	
	// Move a bundle to UNRESOLVED
	private void setBundleUnresolved(ResolverBundle bundle, boolean removed) {
		if(bundle.getState() == ResolverBundle.UNRESOLVED) 
			return;
		if (bundle.getBundle().isResolved()) {
			resolverExports.remove(bundle.getExportPackages());
			bundle.initialize(false);
			if (!removed)
				resolverExports.put(bundle.getExportPackages());
		}
		if (resolvingBundles != null)
			resolvingBundles.remove(bundle);
		if (resolvedBundles != null)
			resolvedBundles.remove(bundle);
		if (!removed) {
			unresolvedBundles.add(bundle);
		}
		bundle.clearPropagationContraints();
		bundle.detachAllFragments();
		bundle.setState(ResolverBundle.UNRESOLVED);
	}

	// Move a bundle to RESOLVED
	private void setBundleResolved(ResolverBundle bundle) {
		if(bundle.getState() == ResolverBundle.RESOLVED)
			return;
		
		resolvingBundles.remove(bundle);
		unresolvedBundles.remove(bundle);
		resolvedBundles.add(bundle);
		bundle.setState(ResolverBundle.RESOLVED);
	}
	
	// Move a bundle to RESOLVING
	private void setBundleResolving(ResolverBundle bundle) {
		if(bundle.getState() == ResolverBundle.RESOLVING)
			return;
		
		resolvedBundles.remove(bundle);
		unresolvedBundles.remove(bundle);
		resolvingBundles.add(bundle);
		bundle.setState(ResolverBundle.RESOLVING);
	}

	
	// Resolves the bundles in the State
	private void stateResolveBundles() {
		for(int i=0; i<resolvedBundles.size(); i++) {
			ResolverBundle rb = (ResolverBundle)resolvedBundles.get(i);
			if(!rb.getBundle().isResolved()) {
				stateResolveBundle(rb);
			}
		}
		resolverExports.reorder();
		resolverBundles.reorder();
	}

	private void stateResolveConstraints(ResolverBundle rb) {
		ResolverImport[] imports = rb.getImportPackages();
		for (int i = 0; i < imports.length; i++) {
			ResolverExport export = imports[i].getMatchingExport();
			BaseDescription supplier = export == null ? null : export.getExportPackageDescription();
			state.resolveConstraint(imports[i].getImportPackageSpecification(), supplier);
		}
		BundleConstraint[] requires = rb.getRequires();
		for (int i = 0; i < requires.length; i++) {
			ResolverBundle bundle = requires[i].getMatchingBundle();
			BundleDescription supplier = bundle == null ? null : bundle.getBundle();
			state.resolveConstraint(requires[i].getVersionConstraint(), supplier);
		}
	}

	private void stateResolveBundle(ResolverBundle rb) {
		// Gather selected exports
		ResolverExport[] exports = rb.getSelectedExports();
		ArrayList selectedExports = new ArrayList(exports.length);
		for(int i=0; i<exports.length; i++) {
			selectedExports.add(exports[i].getExportPackageDescription());
		}
		ExportPackageDescription[] selectedExportsArray =
			(ExportPackageDescription[])selectedExports.toArray(new ExportPackageDescription[selectedExports.size()]);
		
		// Gather exports that have been wired to
		ResolverImport[] imports = rb.getImportPackages();
		ArrayList exportsWiredTo = new ArrayList(imports.length);
		for(int i=0; i<imports.length; i++) {
			if(imports[i].getMatchingExport() != null) {
				exportsWiredTo.add(imports[i].getMatchingExport().getExportPackageDescription());
			}
		}
		ExportPackageDescription[] exportsWiredToArray =
			(ExportPackageDescription[])exportsWiredTo.toArray(new ExportPackageDescription[exportsWiredTo.size()]);

		// Gather bundles that have been wired to
		BundleConstraint[] requires = rb.getRequires();
		ArrayList bundlesWiredTo = new ArrayList(requires.length);
		for (int i = 0; i < requires.length; i++)
			if (requires[i].getMatchingBundle() != null)
				bundlesWiredTo.add(requires[i].getMatchingBundle().getBundle());
		BundleDescription[] bundlesWiredToArray = 
			(BundleDescription[])bundlesWiredTo.toArray(new BundleDescription[bundlesWiredTo.size()]);

		BundleDescription[] hostBundles = null;
		if (rb.isFragment()) {
			ResolverBundle[] matchingBundles = rb.getHost().getMatchingBundles();
			if (matchingBundles != null && matchingBundles.length > 0) {
				hostBundles = new BundleDescription[matchingBundles.length];
				for (int i = 0; i < matchingBundles.length; i++)
					hostBundles[i] = matchingBundles[i].getBundle();
			}
		}

		// Resolve the bundle in the state
		state.resolveBundle(rb.getBundle(), true, hostBundles, selectedExportsArray, bundlesWiredToArray, exportsWiredToArray);
	}
	
	
	// Resolve dynamic import
	public synchronized ExportPackageDescription resolveDynamicImport(BundleDescription importingBundle, String requestedPackage) {
		if (state == null)
			throw new IllegalStateException("RESOLVER_NO_STATE"); //$NON-NLS-1$
		
		// Make sure the resolver is initialized
		if (!initialized)
			initialize();
		
		ResolverBundle rb = (ResolverBundle)bundleMapping.get(importingBundle);
		ResolverImport[] resolverImports = rb.getImportPackages();
		// Check through the ResolverImports of this bundle.
		// If there is a matching one then pass it into resolveImport()
		for(int j=0; j<resolverImports.length; j++) {
			// Make sure it is a dynamic import
			if((resolverImports[j].getImportPackageSpecification().getResolution() & ImportPackageSpecification.RESOLUTION_DYNAMIC) == 0) {
				continue;
			}
			String importName = resolverImports[j].getName();
			// If the import uses a wildcard, then temporarily replace this with the requested package
			if(importName.equals("*") ||
					(importName.endsWith(".*") && requestedPackage.startsWith(importName.substring(0, importName.length() - 2)))) {
				resolverImports[j].setName(requestedPackage);
			}
			// Resolve the import
			if(requestedPackage.equals(resolverImports[j].getName())) {
				boolean resolved = resolveImport(resolverImports[j], true);
				while(resolved && !checkDynamicGrouping(resolverImports[j])) {
					resolved = resolveImport(resolverImports[j], true);
				}
				if(resolved) {
					// If the import resolved then return it's matching export
					resolverImports[j].setName(null);
					if(DEBUG_IMPORTS) ResolverImpl.log("Resolved dynamic import: " + rb + ":" + resolverImports[j].getName() + " -> " + resolverImports[j].getMatchingExport().getExporter() + ":" + requestedPackage);
					ExportPackageDescription matchingExport = resolverImports[j].getMatchingExport().getExportPackageDescription();
					// If it is a wildcard import then clear the wire, so other
					// exported packages can be found for it
					if(importName.endsWith("*")) {
						resolverImports[j].setMatchingExport(null);
					}
					return matchingExport;
				}
			}
			// Reset the import package name
			resolverImports[j].setName(null);
		}
		if(DEBUG || DEBUG_IMPORTS) ResolverImpl.log("Failed to resolve dynamic import: " + requestedPackage);
		return null;	// Couldn't resolve the import, so return null
	}
	
	// Checks that dynamic import 'imp' does not break grouping dependencies with the existing wirings
	private boolean checkDynamicGrouping(ResolverImport imp) {
		ResolverBundle rb = imp.getBundle();
		ResolverImport[] imports = rb.getImportPackages();
		
		for(int i=0; i<imports.length; i++) {
			// Don't compare grouping with itself or with unwired imports
			if(imports[i] == imp || imports[i].getMatchingExport() == null) {
				continue;
			}
			// Don't compare grouping if the exports are from the same bundle
			if(imp.getMatchingExport().getExporter() == imports[i].getMatchingExport().getExporter()) {
				continue;
			}
			
			ResolverExport[] exports = imports[i].getMatchingExport().getExporter().getExportPackages();
			String importGrouping = imports[i].getMatchingExport().getGrouping();
			
			for(int j=0; j<exports.length; j++) {
				String exportName = exports[j].getName();
				String exportGrouping = exports[j].getGrouping();
				
				if(exportName.equals(imp.getName()) && importGrouping.equals(exportGrouping)) {
					imp.addUnresolvableWiring(imp.getMatchingExport().getExporter());
					imp.setMatchingExport(null);
					if(DEBUG_GROUPING) ResolverImpl.log("  Dynamic grouping failed: " + imp.getName());
					return false;
				}
			}
		}
		return true;
	}
	
	
	
	public void bundleAdded(BundleDescription bundle) {
		if (!initialized)
			return;
		
		boolean alreadyThere = false;
		for(int i=0; i<unresolvedBundles.size(); i++) {
			ResolverBundle rb = (ResolverBundle)unresolvedBundles.get(i);
			if(rb.getBundle() == bundle) {
				alreadyThere = true;
			}
		}
		if(!alreadyThere) {
			ResolverBundle rb = new ResolverBundle(bundle, this);
			bundleMapping.put(bundle, rb);
			unresolvedBundles.add(rb);
			resolverExports.put(rb.getExportPackages());
			resolverBundles.put(rb);
		}
	}

	public void bundleRemoved(BundleDescription bundle, boolean pending) {
		// check if there are any dependants
		if (pending) {
			addRemovalPending(bundle);
		}
		if (!initialized)
			return;
		ResolverBundle rb = (ResolverBundle) bundleMapping.get(bundle);
		if (rb == null)
			return;

		if (!pending)
			bundleMapping.remove(bundle);
		unresolvedBundles.remove(rb);
		resolverExports.remove(rb.getExportPackages());
		resolverBundles.remove(rb);
		
	}
	
	private void addRemovalPending(BundleDescription bundle) {
		Long id = new Long(bundle.getBundleId());
		ArrayList removedBundles = (ArrayList) removalPending.get(id);
		if (removedBundles == null) {
			removedBundles = new ArrayList(1); // very rare that more than one version of a bundle will be pending
			removedBundles.add(bundle);
			removalPending.put(id, removedBundles);
		}
		else {
			removedBundles.add(bundle);
		}
	}

	private BundleDescription[] getRemovalPending(BundleDescription bundle) {
		Long id = new Long(bundle.getBundleId());
		ArrayList removedBundles = (ArrayList) removalPending.remove(id);
		if (removedBundles == null)
			return null;
		return (BundleDescription[]) removedBundles.toArray(new BundleDescription[removedBundles.size()]);
	}
	
	private void unresolveBundle(ResolverBundle bundle, boolean removed) {
		if (bundle == null)
			return;
		// check the removed list if unresolving then remove from the removed list
		Long id = new Long(bundle.getBundle().getBundleId());
		BundleDescription[] removedBundles = null;
		if ( (removedBundles = getRemovalPending(bundle.getBundle())) != null) {
			for (int i=0; i<removedBundles.length; i++) {
				unresolveBundle((ResolverBundle)bundleMapping.get(removedBundles[i]), true);
				state.removeBundleComplete(removedBundles[i]);
				bundleMapping.remove(removedBundles[i]);
				// the bundle is removed
				if (removedBundles[i] == bundle.getBundle())
					removed = true;
			}
		}
		
		if (!bundle.getBundle().isResolved())
			return;
		// if not removed then add to the list of unresolvedBundles
		setBundleUnresolved(bundle, removed);
		// Get bundles dependent on 'bundle' and selected exports of 'bundle'
		BundleDescription[] dependents = bundle.getBundle().getDependents();
		// Unresolve 'bundle'
		bundle.setState(ResolverBundle.UNRESOLVED);
		state.resolveBundle(bundle.getBundle(), false, null, null, null, null);
		// Unresolve dependents of 'bundle'
		for(int i=0; i<dependents.length; i++) {
			unresolveBundle((ResolverBundle)bundleMapping.get(dependents[i]), false);
		}
	}
	
	public void bundleUpdated(BundleDescription newDescription, BundleDescription existingDescription, boolean pending) {
		bundleRemoved(existingDescription, pending);
		bundleAdded(newDescription);
	}
	
	public void flush() {
		resolverExports = null;
		resolverBundles = null;
		unresolvedBundles = null;
		bundleMapping = null;
		cyclicDependencies = null;
		if (removalPending.size() > 0) {
			BundleDescription[] pending = getRemovalPending();
			for (int i = 0; i < pending.length; i++)
				state.removeBundleComplete(pending[i]);
		}
		removalPending.clear();
		initialized = false;
	}
	
	public State getState() {
		return state;
	}
	
	public void setState(State newState) {
		state = newState;
		flush();
	}

	private BundleDescription[] getRemovalPending() {
		if (removalPending.size() == 0)
			return new BundleDescription[0];
		ArrayList results = new ArrayList(removalPending.size());
		Iterator iter = removalPending.values().iterator();
		while (iter.hasNext()) {
			ArrayList removedBundles = (ArrayList) iter.next();
			results.addAll(removedBundles);
		}
		return (BundleDescription[]) results.toArray(new BundleDescription[results.size()]);
	}
	
	private void setDebugOptions() {
		DebugOptions options = DebugOptions.getDefault();
		// may be null if debugging is not enabled
		if (options == null)
			return;
		DEBUG = options.getBooleanOption(OPTION_DEBUG, false);
		DEBUG_WIRING = options.getBooleanOption(OPTION_WIRING, false);
		DEBUG_IMPORTS = options.getBooleanOption(OPTION_IMPORTS, false);
		DEBUG_REQUIRES = options.getBooleanOption(OPTION_REQUIRES, false);
		DEBUG_GROUPING = options.getBooleanOption(OPTION_GROUPING, false);
		DEBUG_CYCLES = options.getBooleanOption(OPTION_CYCLES, false);
	}

	// LOGGING METHODS
	private void printWirings() {
		for(int k=0; k<resolvedBundles.size(); k++) {
			ResolverBundle rb = (ResolverBundle)resolvedBundles.get(k);
			if(rb.getBundle().isResolved()) {
				continue;
			}
			ResolverImpl.log("    * WIRING for " + rb);
			// Require bundles
			BundleConstraint[] requireBundles = rb.getRequires();
			if(requireBundles.length == 0) {
				ResolverImpl.log("        (r) no requires");
			} else {
				for(int i=0; i<requireBundles.length; i++) {
					if(requireBundles[i].getMatchingBundle() == null) {
						ResolverImpl.log("        (r) " + rb.getBundle() + " -> NULL!!!");
					} else {
						ResolverImpl.log("        (r) " + rb.getBundle() + " -> " + requireBundles[i].getMatchingBundle());
					}
				}
			}
			// Hosts
			BundleConstraint hostSpec = rb.getHost();
			if(hostSpec != null) {
				ResolverBundle[] hosts = hostSpec.getMatchingBundles();
				if (hosts != null)
					for(int i=0; i<hosts.length; i++) {
						ResolverImpl.log("        (h) " + rb.getBundle() + " -> " + hosts[i].getBundle());
					}
			}
			// Imports
			ResolverImport[] imports = rb.getImportPackages();
			if(imports.length == 0) {
				ResolverImpl.log("        (w) no imports");
				continue;
			}
			for(int i=0; i<imports.length; i++) {
				if(imports[i].isDynamic() && imports[i].getMatchingExport() == null) {
					ResolverImpl.log("        (w) " + imports[i].getBundle() + ":" + imports[i].getName() + " -> DYNAMIC");
				} else if(imports[i].isOptional() && imports[i].getMatchingExport() == null) {
					ResolverImpl.log("        (w) " + imports[i].getBundle() + ":" + imports[i].getName() + " -> OPTIONAL (could not be wired)");
				} else if(imports[i].getMatchingExport() == null) {
					ResolverImpl.log("        (w) " + imports[i].getBundle() + ":" + imports[i].getName() + " -> NULL!!!");
				} else {
					ResolverImpl.log("        (w) " + imports[i].getBundle() + ":" + imports[i].getName() + " -> " +
							imports[i].getMatchingExport().getExporter() + ":" + imports[i].getMatchingExport().getName());
				}
			}
		}
	}
	
	static void log(String message) {
		Debug.println(message);
	}
}
