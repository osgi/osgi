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
import org.eclipse.osgi.framework.adaptor.FrameworkAdaptor;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.debug.DebugOptions;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.*;

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
	private GroupingChecker groupingChecker;
	private BundleContext context;

	public ResolverImpl(BundleContext context, boolean checkPermissions) {
		this.context = context;
		this.permissionChecker = new PermissionChecker(context, checkPermissions);
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
		groupingChecker = new GroupingChecker();

		ArrayList fragmentBundles = new ArrayList();
		// Add each bundle to the resolver's internal state
		for (int i = 0; i < bundles.length; i++)
			initResolverBundle(bundles[i], fragmentBundles, false);
		// Add each removal pending bundle to the resolver's internal state
		BundleDescription[] removedBundles = getRemovalPending();
		for (int i = 0; i < removedBundles.length; i++)
			initResolverBundle(removedBundles[i], fragmentBundles, true);
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
		rewireBundles(); // Reconstruct wirings
		setDebugOptions();
		initialized = true;
	}

	private void initResolverBundle(BundleDescription bundleDesc, ArrayList fragmentBundles, boolean pending) {
		ResolverBundle bundle = new ResolverBundle(bundleDesc, this);
		bundleMapping.put(bundleDesc, bundle);
		if (pending)
			return;
		resolverBundles.put(bundle);
		if (bundleDesc.isResolved()) {
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
		for (Iterator iter = bundleMapping.values().iterator(); iter.hasNext();) {
			ResolverBundle rb = (ResolverBundle) iter.next();
			if (!rb.getBundle().isResolved() || rb.isFragment())
				continue;
			rewireBundle(rb);
		}
	}

	private void rewireBundle(ResolverBundle rb) {
		if (rb.isFullyWired())
			return;
		// Wire requires to bundles
		BundleConstraint[] requires = rb.getRequires();
		for (int i = 0; i < requires.length; i++) {
			rewireRequire(requires[i]);
		}
		// Wire imports to exports
		ResolverImport[] imports = rb.getImportPackages();
		for (int i = 0; i < imports.length; i++) {
			rewireImport(imports[i]);
		}
	}

	private void rewireRequire(BundleConstraint req) {
		if (req.getMatchingBundle() != null)
			return;
		ResolverBundle matchingBundle = (ResolverBundle) bundleMapping.get(req.getVersionConstraint().getSupplier());
		req.setMatchingBundle(matchingBundle);
		if (matchingBundle == null && !req.isOptional()) {
			System.err.println("Could not find matching bundle for " + req.getVersionConstraint()); //$NON-NLS-1$
			// TODO log error!!
		}
		if (matchingBundle != null) {
			rewireBundle(matchingBundle);
		}
	}

	private void rewireImport(ResolverImport imp) {
		if (imp.isDynamic() || imp.getMatchingExport() != null)
			return;
		// Re-wire 'imp'
		ResolverExport matchingExport = null;
		ExportPackageDescription importSupplier = (ExportPackageDescription) imp.getImportPackageSpecification().getSupplier();
		ResolverBundle exporter = importSupplier == null ? null : (ResolverBundle) bundleMapping.get(importSupplier.getExporter());
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
		if (matchingExport == null && exporter != null) {
			ResolverExport reprovidedExport = new ResolverExport(exporter, importSupplier);
			if (exporter.getExport(imp) == null) {
				exporter.addExport(reprovidedExport);
				resolverExports.put(reprovidedExport);
			}
			imp.setMatchingExport(reprovidedExport);
		}
		// If we still have a null wire and it's not optional, then we have an error
		if (imp.getMatchingExport() == null && !imp.isOptional()) {
			System.err.println("Could not find matching export for " + imp.getImportPackageSpecification()); //$NON-NLS-1$
			// TODO log error!!
		}
		if (imp.getMatchingExport() != null) {
			rewireBundle(imp.getMatchingExport().getExporter());
		}
	}

	// Checks a bundle to make sure it is valid.  If this method returns false for
	// a given bundle, then that bundle will not even be considered for resolution
	private boolean isResolvable(BundleDescription bundle, Dictionary[] platformProperties) {
		ImportPackageSpecification[] imports = bundle.getImportPackages();
		for (int i = 0; i < imports.length; i++) {
			// Don't allow non-dynamic imports to specify wildcards
			if (!ImportPackageSpecification.RESOLUTION_DYNAMIC.equals(imports[i].getDirective(Constants.RESOLUTION_DIRECTIVE)) && imports[i].getName().endsWith("*")) //$NON-NLS-1$
				return false;
			// Don't allow multiple imports of the same package
			for (int j = 0; j < i; j++) {
				if (imports[i] != imports[j] && imports[i].getName().equals(imports[j]))
					return false;
			}
		}
		String platformFilter = bundle.getPlatformFilter();
		if (platformFilter == null) {
			return true;
		}
		if (platformProperties == null)
			return false;
		try {
			Filter filter = context.createFilter(platformFilter);
			for (int i = 0; i < platformProperties.length; i++)
				if (filter.match(platformProperties[i]))
					return true;
		} catch (InvalidSyntaxException e) {
			return false;
		}
		return false;
	}

	// Attach fragment to its host
	private void attachFragment(ResolverBundle bundle) {
		if (!bundle.isFragment() || !bundle.isResolvable())
			return;
		BundleConstraint hostConstraint = bundle.getHost();
		VersionSupplier[] hosts = resolverBundles.getArray(hostConstraint.getVersionConstraint().getName());
		for (int i = 0; i < hosts.length; i++) {
			if (((ResolverBundle) hosts[i]).isResolvable() && hostConstraint.isSatisfiedBy((ResolverBundle) hosts[i]))
				resolverExports.put(((ResolverBundle) hosts[i]).attachFragment(bundle, true));
		}
	}

	public synchronized void resolve(BundleDescription[] reRefresh, Dictionary[] platformProperties) {
		if (DEBUG)
			ResolverImpl.log("*** BEGIN RESOLUTION ***"); //$NON-NLS-1$
		if (state == null)
			throw new IllegalStateException("RESOLVER_NO_STATE"); //$NON-NLS-1$

		if (!initialized) {
			initialize();
		}

		// Unresolve all the supplied bundles and their dependents
		if (reRefresh != null)
			for (int i = 0; i < reRefresh.length; i++) {
				ResolverBundle rb = (ResolverBundle) bundleMapping.get(reRefresh[i]);
				if (rb != null)
					unresolveBundle(rb, false);
			}
		// Initialize the resolving and resolved bundle lists
		resolvingBundles = new ArrayList(unresolvedBundles.size());
		resolvedBundles = new ArrayList(unresolvedBundles.size());

		ResolverBundle[] bundles = (ResolverBundle[]) unresolvedBundles.toArray(new ResolverBundle[unresolvedBundles.size()]);
		groupingChecker.addInitialGroupingConstraints(bundles);
		// First check that all the meta-data is valid for each unresolved bundle
		// This will reset the resolvable flag for each bundle
		for (int i = 0; i < bundles.length; i++)
			bundles[i].setResolvable(isResolvable(bundles[i].getBundle(), platformProperties));

		// First attach all fragments to the matching hosts
		for (int i = 0; i < bundles.length; i++)
			attachFragment(bundles[i]);

		// Attempt to resolve all unresolved bundles
		for (int i = 0; i < bundles.length; i++) {
			if (DEBUG)
				ResolverImpl.log("** RESOLVING " + bundles[i] + " **"); //$NON-NLS-1$ //$NON-NLS-2$
			resolveBundle(bundles[i]);
			// Check for any bundles still in RESOLVING state - these can now be
			// moved to RESOLVED as they are dependent on each other.  Any that
			// could not be resolved would have been moved back to UNRESOLVED
			while (resolvingBundles.size() > 0) {
				ResolverBundle rb = (ResolverBundle) resolvingBundles.get(0);
				// Check that we haven't wired to any dropped exports
				ResolverImport[] imports = rb.getImportPackages();
				boolean needRewire = false;
				for (int j = 0; j < imports.length; j++) {
					if (imports[j].getMatchingExport() != null && !resolverExports.contains(imports[j].getMatchingExport())) {
						imports[j].setMatchingExport(null);
						needRewire = true;
					}
				}
				if (needRewire)
					resolveBundle(rb);
				if (rb.isFullyWired()) {
					if (DEBUG || DEBUG_CYCLES)
						ResolverImpl.log("Pushing " + rb + " to RESOLVED"); //$NON-NLS-1$ //$NON-NLS-2$
					setBundleResolved(rb);
				}
			}
		}

		// Resolve all fragments that are still attached to at least one host.
		if (unresolvedBundles.size() > 0) {
			bundles = (ResolverBundle[]) unresolvedBundles.toArray(new ResolverBundle[unresolvedBundles.size()]);
			for (int i = 0; i < bundles.length; i++)
				resolveFragment(bundles[i]);
		}

		if (DEBUG_WIRING)
			printWirings();
		stateResolveBundles();

		// throw away the temporary resolving and resolved bundle lists
		resolvingBundles = null;
		resolvedBundles = null;
		if (DEBUG)
			ResolverImpl.log("*** END RESOLUTION ***"); //$NON-NLS-1$
	}

	private void resolveFragment(ResolverBundle fragment) {
		if (!fragment.isFragment())
			return;
		if (fragment.getHost().foundMatchingBundles())
			setBundleResolved(fragment);
	}

	// This method will attempt to resolve the supplied bundle and any bundles that it is dependent on
	private boolean resolveBundle(ResolverBundle bundle) {
		if (bundle.isFragment())
			return false;
		if (!bundle.isResolvable()) {
			if (DEBUG)
				ResolverImpl.log("  - " + bundle + " is unresolvable"); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		if (bundle.getState() == ResolverBundle.RESOLVED) {
			// 'bundle' is already resolved so just return
			if (DEBUG)
				ResolverImpl.log("  - " + bundle + " already resolved"); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		} else if (bundle.getState() == ResolverBundle.UNRESOLVED) {
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
						continue; // Ignore the bundle we are resolving and non-singletons
					if (((ResolverBundle) sameName[i]).isResolved()) {
						failed = true; // Must fail since there is already a resolved bundle
						break;
					}
					if (sameName[i].getVersion().compareTo(bundle.getVersion()) > 0)
						// Attempt to resolve higher version first
						if (resolveBundle((ResolverBundle) sameName[i])) {
							failed = true; // Must fail since we were able to resolve a higher version
							break;
						}
				}
		}

		if (!failed) {
			// Iterate thru required bundles of 'bundle' trying to find matching bundles.
			BundleConstraint[] requires = bundle.getRequires();
			for (int i = 0; i < requires.length; i++) {
				if (!resolveRequire(requires[i])) {
					if (DEBUG || DEBUG_REQUIRES)
						ResolverImpl.log("** REQUIRE " + requires[i].getVersionConstraint().getName() + "[" + requires[i].getActualBundle() + "] failed to resolve"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					// If the require has failed to resolve and it is from a fragment, then remove the fragment from the host
					if (requires[i].isFromFragment()) {
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
			for (int i = 0; i < imports.length; i++) {
				// Only resolve non-dynamic imports here
				if (!imports[i].isDynamic()) {
					if (!resolveImport(imports[i], true)) {
						if (DEBUG || DEBUG_IMPORTS)
							ResolverImpl.log("** IMPORT " + imports[i].getName() + "[" + imports[i].getActualBundle() + "] failed to resolve"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						// If the import has failed to resolve and it is from a fragment, then remove the fragment from the host
						if (imports[i].isFromFragment()) {
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
		boolean fullyWired = bundle.isFullyWired();
		if (!failed && fullyWired && !bundle.isDependentOnCycle()) {
			if (!groupingChecker.checkRequiresConstraints(bundle)) {
				setBundleUnresolved(bundle, false);
				if (DEBUG)
					ResolverImpl.log(bundle + " NOT RESOLVED due to propagation or grouping constraints"); //$NON-NLS-1$
			} else {
				setBundleResolved(bundle);
				if (DEBUG)
					ResolverImpl.log(bundle + " RESOLVED"); //$NON-NLS-1$
			}
		} else if (failed || !fullyWired) {
			setBundleUnresolved(bundle, false);
			if (DEBUG)
				ResolverImpl.log(bundle + " NOT RESOLVED"); //$NON-NLS-1$
		}
		if (!failed && fullyWired) {
			groupingChecker.addReExportConstraints(bundle);
			groupingChecker.addImportConstraints(bundle);
			groupingChecker.addRequireConstraints(bundle.getExportPackages(), bundle);
		}
		// Check cyclic dependencies
		ArrayList v = cyclicDependencies.remove(bundle);
		if (v != null) {
			// We have some cyclic dependencies on 'bundle', so iterate thru
			// them and inform each one whether 'bundle' has resolved or not
			for (int i = 0; i < v.size(); i++) {
				ResolverBundle dependent = (ResolverBundle) v.get(i);
				if (bundle.isDependentOnUnresolvedFragment(dependent)) {
					dependent.cyclicDependencyFailed(bundle);
					setBundleUnresolved(dependent, false);
					if (DEBUG_CYCLES)
						ResolverImpl.log("Setting dependent bundle (" + dependent + ") to unresolved (due to fragment)"); //$NON-NLS-1$ //$NON-NLS-2$
				} else if (bundle.getState() == ResolverBundle.RESOLVED) {
					// Tell dependent bundles that we have resolved so that they can resolve
					// (they may be dependent on other bundles as well though)
					if (dependent.cyclicDependencyResolved(bundle)) {
						setBundleResolved(dependent);
						if (DEBUG_CYCLES)
							ResolverImpl.log("Telling dependent bundle (" + dependent + ") that " + bundle + " has resolved"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				} else if (bundle.getState() == ResolverBundle.UNRESOLVED) {
					// Tell dependent bundles that we have failed to
					// resolve - this forces them to be unresolved
					dependent.cyclicDependencyFailed(bundle);
					setBundleUnresolved(dependent, false);
					if (DEBUG_CYCLES)
						ResolverImpl.log("Setting dependent bundle (" + dependent + ") to unresolved"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		if (bundle.getState() == ResolverBundle.UNRESOLVED)
			bundle.setResolvable(false); // Set it to unresolvable so we don't attempt to resolve it again in this round

		// tell the state what we resolved the constraints to
		stateResolveConstraints(bundle);
		return bundle.getState() != ResolverBundle.UNRESOLVED;
	}

	// Resolve the supplied import. Returns true if the import can be resolved, false otherwise
	private boolean resolveRequire(BundleConstraint req) {
		if (DEBUG_REQUIRES)
			ResolverImpl.log("Trying to resolve: " + req.getBundle() + ", " + req.getVersionConstraint()); //$NON-NLS-1$ //$NON-NLS-2$
		if (req.getMatchingBundle() != null) {
			// Check for unrecorded cyclic dependency
			if (req.getMatchingBundle().getState() == ResolverBundle.RESOLVING) {
				cyclicDependencies.put(req.getMatchingBundle(), req.getBundle());
				req.getBundle().recordCyclicDependency(req.getMatchingBundle());
			}
			if (DEBUG_REQUIRES)
				ResolverImpl.log("  - already wired"); //$NON-NLS-1$
			return true; // Already wired (due to grouping dependencies) so just return
		}
		VersionSupplier[] bundles = resolverBundles.getArray(req.getVersionConstraint().getName());
		for (int i = 0; i < bundles.length; i++) {
			ResolverBundle bundle = (ResolverBundle) bundles[i];
			if (DEBUG_REQUIRES)
				ResolverImpl.log("CHECKING: " + bundle.getBundle()); //$NON-NLS-1$
			// Check if export matches
			if (req.isSatisfiedBy(bundle)) {
				int originalState = bundle.getState();
				req.setMatchingBundle(bundle); // Wire to the bundle
				if (req.getBundle() == bundle)
					return true; // Wired to ourselves
				if ((originalState == ResolverBundle.UNRESOLVED && !resolveBundle(bundle))) {
					req.setMatchingBundle(null);
					continue; // Bundle hasn't resolved
				}
				// Check cyclic dependencies
				if (originalState == ResolverBundle.RESOLVING) {
					// If the bundle was RESOLVING, we have a cyclic dependency
					cyclicDependencies.put(bundle, req.getBundle());
					req.getBundle().recordCyclicDependency(bundle);
				} else if (originalState == ResolverBundle.UNRESOLVED && bundle.getState() == ResolverBundle.RESOLVING) {
					// If the bundle has gone from UNRESOLVED to RESOLVING, we have a cyclic dependency
					ArrayList exportersCyclicDependencies = bundle.getCyclicDependencies();
					for (int k = 0; k < exportersCyclicDependencies.size(); k++) {
						ResolverBundle dependentOn = (ResolverBundle) exportersCyclicDependencies.get(k);
						if (dependentOn != req.getBundle()) {
							cyclicDependencies.put(dependentOn, req.getBundle());
							req.getBundle().recordCyclicDependency(dependentOn);
						}
					}
				}
				if (DEBUG_REQUIRES)
					ResolverImpl.log("Found match: " + bundle.getBundle() + ". Wiring"); //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			}
		}
		if (req.isOptional())
			return true; // If the req is optional then just return true

		return false;
	}

	// Resolve the supplied import. Returns true if the import can be resolved, false otherwise
	private boolean resolveImport(ResolverImport imp, boolean checkReexportsFromRequires) {
		if (DEBUG_IMPORTS)
			ResolverImpl.log("Trying to resolve: " + imp.getBundle() + ", " + imp.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		if (imp.getMatchingExport() != null) {
			// Check for unrecorded cyclic dependency
			if (imp.getMatchingExport().getExporter().getState() == ResolverBundle.RESOLVING) {
				cyclicDependencies.put(imp.getMatchingExport().getExporter(), imp.getBundle());
				imp.getBundle().recordCyclicDependency(imp.getMatchingExport().getExporter());
			}
			if (DEBUG_IMPORTS)
				ResolverImpl.log("  - already wired"); //$NON-NLS-1$
			return true; // Already wired (due to grouping dependencies) so just return
		}
		VersionSupplier[] exports = resolverExports.getArray(imp.getName());
		for (int i = 0; i < exports.length; i++) {
			ResolverExport export = (ResolverExport) exports[i];
			if (DEBUG_IMPORTS)
				ResolverImpl.log("CHECKING: " + export.getExporter().getBundle() + ", " + exports[i].getName()); //$NON-NLS-1$ //$NON-NLS-2$
			// Check if export matches
			if (imp.isSatisfiedBy(export) && imp.isNotAnUnresolvableWiring(export)) {
				int originalState = export.getExporter().getState();
				if (imp.isDynamic() && originalState != ResolverBundle.RESOLVED)
					return false; // Must not attempt to resolve an exporter when dynamic
				if (imp.getBundle() == export.getExporter() && !export.getExportPackageDescription().isRoot())
					continue; // Can't wire to our own re-export
				imp.setMatchingExport(export); // Wire the import to the export
				if (imp.getBundle() != export.getExporter()) {
					ResolverExport exp = imp.getBundle().getExport(imp);
					if (exp != null) {
						if (exp.getExportPackageDescription().isRoot() && !export.getExportPackageDescription().isRoot())
							continue; // TODO hack to prevent imports from getting wired to re-exports if we offer a root export
						resolverExports.remove(exp); // Import wins, remove export
						exp.setDropped(true);
					}
					if (((originalState == ResolverBundle.UNRESOLVED || !export.getExportPackageDescription().isRoot()) && !resolveBundle(export.getExporter())) || !resolverExports.contains(export)) {
						if (exp != null) {
							resolverExports.put(exp);
							exp.setDropped(false);
						}
						imp.setMatchingExport(null);
						continue; // Bundle hasn't resolved || export has not been selected and is unavailable
					}
				}
				// If the importer has become unresolvable then stop here
				if (!imp.getBundle().isResolvable())
					return false;
				// Check grouping dependencies
				if (checkAndResolveDependencies(imp, imp.getMatchingExport())) {
					if (imp.getMatchingExport() != export)
						return true; // Grouping has changed the wiring, so return here
					// Record any cyclic dependencies
					if (imp.getBundle() != export.getExporter()) {
						if (originalState == ResolverBundle.RESOLVING) {
							// If the exporter was RESOLVING, we have a cyclic dependency
							cyclicDependencies.put(export.getExporter(), imp.getBundle());
							imp.getBundle().recordCyclicDependency(export.getExporter());
						} else if (originalState == ResolverBundle.UNRESOLVED && export.getExporter().getState() == ResolverBundle.RESOLVING) {
							// If the exporter has gone from UNRESOLVED to RESOLVING, we have a cyclic dependency
							ArrayList exportersCyclicDependencies = export.getExporter().getCyclicDependencies();
							for (int k = 0; k < exportersCyclicDependencies.size(); k++) {
								ResolverBundle dependentOn = (ResolverBundle) exportersCyclicDependencies.get(k);
								if (dependentOn != imp.getBundle()) {
									cyclicDependencies.put(dependentOn, imp.getBundle());
									imp.getBundle().recordCyclicDependency(dependentOn);
								}
							}
						}
					}
					if (DEBUG_IMPORTS)
						ResolverImpl.log("Found match: " + export.getExporter() + ". Wiring " + imp.getBundle() + ":" + imp.getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					return true;
				} else if (!imp.getBundle().isResolvable()) {
					// If grouping has caused recursive calls to resolveImport, and the grouping has failed
					// then we need to catch that here, so we don't continue trying to wire here
					return false;
				}
			}
		}
		if (checkReexportsFromRequires && resolveImportReprovide(imp))
			return true; // A reprovide satisfies imp
		if (imp.isOptional())
			return true; // If the import is optional then just return true

		return false;
	}

	// Check if the import can be resolved to a reprovided package (has no export object to match to)
	private boolean resolveImportReprovide(ResolverImport imp) {
		String bsn = imp.getImportPackageSpecification().getBundleSymbolicName();
		// If no symbolic name specified then just return (since this is a
		// re-export an import not specifying a bsn will wire to the root)
		if (bsn == null) {
			return false;
		}
		if (DEBUG_IMPORTS)
			ResolverImpl.log("Checking reprovides: " + imp.getName()); //$NON-NLS-1$
		ResolverBundle rb;
		// Find bundle with specified bsn
		for (Iterator iter = bundleMapping.values().iterator(); iter.hasNext();) {
			rb = (ResolverBundle) iter.next();
			if (bsn.equals(rb.getBundle().getSymbolicName()) && !rb.isFragment()) {
				if (resolveBundle(rb))
					if (resolveImportReprovide0(imp, rb, rb))
						return true;
			}
		}
		return false;
	}

	private boolean resolveImportReprovide0(ResolverImport imp, ResolverBundle reexporter, ResolverBundle rb) {
		BundleConstraint[] requires = rb.getRequires();
		for (int i = 0; i < requires.length; i++) {
			if (!((BundleSpecification) requires[i].getVersionConstraint()).isExported())
				continue; // Skip require if it doesn't reprovide the packages
			// Check exports to see if we've found the root
			if (requires[i].getMatchingBundle() == null)
				continue;
			ResolverExport[] exports = requires[i].getMatchingBundle().getExportPackages();
			for (int j = 0; j < exports.length; j++) {
				if (imp.getName().equals(exports[j].getName())) {
					Map directives = exports[j].getExportPackageDescription().getDirectives();
					directives.remove(Constants.USES_DIRECTIVE);
					ExportPackageDescription epd = state.getFactory().createExportPackageDescription(exports[j].getName(), exports[j].getVersion(), directives, exports[j].getExportPackageDescription().getAttributes(), false, reexporter.getBundle());
					if (imp.getImportPackageSpecification().isSatisfiedBy(epd)) {
						// Create reexport and add to bundle and resolverExports
						if (DEBUG_IMPORTS)
							ResolverImpl.log(" - Creating re-export for reprovide: " + reexporter + ":" + epd.getName()); //$NON-NLS-1$ //$NON-NLS-2$
						ResolverExport re = new ResolverExport(reexporter, epd, true);
						reexporter.addExport(re);
						groupingChecker.addReprovideConstraints(re);
						resolverExports.put(re);
						// Resolve import
						if (resolveImport(imp, false))
							return true;
					}
				}
			}
			// Check requires of matching bundle (recurse down the chain)
			if (resolveImportReprovide0(imp, reexporter, requires[i].getMatchingBundle()))
				return true;
		}
		return false;
	}

	// This method checks and resolves (if possible) grouping dependencies
	// Returns true, if the dependencies can be resolved, false otherwise
	private boolean checkAndResolveDependencies(ResolverImport imp, ResolverExport exp) {
		if (DEBUG_GROUPING)
			ResolverImpl.log("  Checking grouping for " + imp.getBundle() + ":" + imp.getName() + " -> " + exp.getExporter() + ":" + exp.getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ResolverBundle importer = imp.getBundle();
		ResolverExport clash = groupingChecker.isConsistent(imp, exp);
		if (clash == null)
			return true;
		if (DEBUG_GROUPING)
			ResolverImpl.log("  * grouping clash with " + clash.getExporter() + ":" + clash.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		// Try to rewire imp
		imp.addUnresolvableWiring(exp.getExporter());
		imp.setMatchingExport(null);
		if (resolveImport(imp, false))
			return true;
		if (imp.isDynamic())
			return false;
		// Rewiring of imp has failed so try to rewire clashing import
		imp.clearUnresolvableWirings();
		imp.setMatchingExport(exp);
		ResolverImport[] imports = importer.getImportPackages();
		for (int i = 0; i < imports.length; i++) {
			if (imports[i].getMatchingExport() != null && imports[i].getMatchingExport().getName().equals(clash.getName())) {
				imports[i].addUnresolvableWiring(imports[i].getMatchingExport().getExporter());
				importer.clearWires();
				// If the clashing import package was also exported then
				// we need to put the export back into resolverExports
				ResolverExport removed = importer.getExport(imports[i]);
				if (removed != null)
					resolverExports.put(removed);
				// Try to re-resolve the bundle
				if (resolveBundle(importer))
					return true;
				else
					return false;
			}
		}
		return false;
	}

	// Move a bundle to UNRESOLVED
	private void setBundleUnresolved(ResolverBundle bundle, boolean removed) {
		if (bundle.getState() == ResolverBundle.UNRESOLVED)
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
		bundle.detachAllFragments();
		bundle.setState(ResolverBundle.UNRESOLVED);
	}

	// Move a bundle to RESOLVED
	private void setBundleResolved(ResolverBundle bundle) {
		if (bundle.getState() == ResolverBundle.RESOLVED)
			return;

		resolvingBundles.remove(bundle);
		unresolvedBundles.remove(bundle);
		resolvedBundles.add(bundle);
		bundle.setState(ResolverBundle.RESOLVED);
	}

	// Move a bundle to RESOLVING
	private void setBundleResolving(ResolverBundle bundle) {
		if (bundle.getState() == ResolverBundle.RESOLVING)
			return;

		resolvedBundles.remove(bundle);
		unresolvedBundles.remove(bundle);
		resolvingBundles.add(bundle);
		bundle.setState(ResolverBundle.RESOLVING);
	}

	// Resolves the bundles in the State
	private void stateResolveBundles() {
		for (int i = 0; i < resolvedBundles.size(); i++) {
			ResolverBundle rb = (ResolverBundle) resolvedBundles.get(i);
			if (!rb.getBundle().isResolved()) {
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
		for (int i = 0; i < exports.length; i++) {
			selectedExports.add(exports[i].getExportPackageDescription());
		}
		ExportPackageDescription[] selectedExportsArray = (ExportPackageDescription[]) selectedExports.toArray(new ExportPackageDescription[selectedExports.size()]);

		// Gather exports that have been wired to
		ResolverImport[] imports = rb.getImportPackages();
		ArrayList exportsWiredTo = new ArrayList(imports.length);
		for (int i = 0; i < imports.length; i++) {
			if (imports[i].getMatchingExport() != null) {
				exportsWiredTo.add(imports[i].getMatchingExport().getExportPackageDescription());
			}
		}
		ExportPackageDescription[] exportsWiredToArray = (ExportPackageDescription[]) exportsWiredTo.toArray(new ExportPackageDescription[exportsWiredTo.size()]);

		// Gather bundles that have been wired to
		BundleConstraint[] requires = rb.getRequires();
		ArrayList bundlesWiredTo = new ArrayList(requires.length);
		for (int i = 0; i < requires.length; i++)
			if (requires[i].getMatchingBundle() != null)
				bundlesWiredTo.add(requires[i].getMatchingBundle().getBundle());
		BundleDescription[] bundlesWiredToArray = (BundleDescription[]) bundlesWiredTo.toArray(new BundleDescription[bundlesWiredTo.size()]);

		BundleDescription[] hostBundles = null;
		if (rb.isFragment()) {
			ResolverBundle[] matchingBundles = rb.getHost().getMatchingBundles();
			if (matchingBundles != null && matchingBundles.length > 0) {
				hostBundles = new BundleDescription[matchingBundles.length];
				for (int i = 0; i < matchingBundles.length; i++) {
					hostBundles[i] = matchingBundles[i].getBundle();
					if (rb.isNewFragmentExports()) {
						// update the host's set of selected exports
						ResolverExport[] hostExports = matchingBundles[i].getSelectedExports();
						ExportPackageDescription[] hostExportsArray = new ExportPackageDescription[hostExports.length];
						for (int j = 0; j < hostExports.length; j++)
							hostExportsArray[j] = hostExports[j].getExportPackageDescription();
						state.resolveBundle(hostBundles[i], true, null, hostExportsArray, hostBundles[i].getResolvedRequires(), hostBundles[i].getResolvedImports());
					}
				}
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

		ResolverBundle rb = (ResolverBundle) bundleMapping.get(importingBundle);
		ResolverImport[] resolverImports = rb.getImportPackages();
		// Check through the ResolverImports of this bundle.
		// If there is a matching one then pass it into resolveImport()
		boolean found = false;
		for (int j = 0; j < resolverImports.length; j++) {
			// Make sure it is a dynamic import
			if (!ImportPackageSpecification.RESOLUTION_DYNAMIC.equals(resolverImports[j].getImportPackageSpecification().getDirective(Constants.RESOLUTION_DIRECTIVE)))
				continue;
			String importName = resolverImports[j].getName();
			// If the import uses a wildcard, then temporarily replace this with the requested package
			if (importName.equals("*") || //$NON-NLS-1$
					(importName.endsWith(".*") && requestedPackage.startsWith(importName.substring(0, importName.length() - 2)))) { //$NON-NLS-1$
				resolverImports[j].setName(requestedPackage);
			}
			// Resolve the import
			if (requestedPackage.equals(resolverImports[j].getName())) {
				found = true;
				boolean resolved = resolveImport(resolverImports[j], true);
				while (resolved && !checkDynamicGrouping(resolverImports[j])) {
					resolved = resolveImport(resolverImports[j], true);
				}
				if (resolved) {
					// If the import resolved then return it's matching export
					resolverImports[j].setName(null);
					if (DEBUG_IMPORTS)
						ResolverImpl.log("Resolved dynamic import: " + rb + ":" + resolverImports[j].getName() + " -> " + resolverImports[j].getMatchingExport().getExporter() + ":" + requestedPackage); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					ExportPackageDescription matchingExport = resolverImports[j].getMatchingExport().getExportPackageDescription();
					// If it is a wildcard import then clear the wire, so other
					// exported packages can be found for it
					if (importName.endsWith("*")) { //$NON-NLS-1$
						resolverImports[j].setMatchingExport(null);
					}
					return matchingExport;
				}
			}
			// Reset the import package name
			resolverImports[j].setName(null);
		}
		if (!found) {
			Map directives = new HashMap(1);
			directives.put(Constants.RESOLUTION_DIRECTIVE, ImportPackageSpecification.RESOLUTION_DYNAMIC);
			ImportPackageSpecification packageSpec = state.getFactory().createImportPackageSpecification(requestedPackage, null, null, null, directives, null, importingBundle);
			ResolverImport newImport = new ResolverImport(rb, packageSpec);
			boolean resolved = resolveImport(newImport, true);
			while (resolved && !checkDynamicGrouping(newImport))
				resolved = resolveImport(newImport, true);
			if (resolved)
				return newImport.getMatchingExport().getExportPackageDescription();
		}
		if (DEBUG || DEBUG_IMPORTS)
			ResolverImpl.log("Failed to resolve dynamic import: " + requestedPackage); //$NON-NLS-1$
		return null; // Couldn't resolve the import, so return null
	}

	// Checks that dynamic import 'imp' does not break grouping dependencies with the existing wirings
	private boolean checkDynamicGrouping(ResolverImport imp) {
		if (groupingChecker.isConsistent(imp, imp.getMatchingExport()) != null) {
			imp.addUnresolvableWiring(imp.getMatchingExport().getExporter());
			imp.setMatchingExport(null);
			if (DEBUG_GROUPING)
				ResolverImpl.log("  Dynamic grouping failed: " + imp.getName()); //$NON-NLS-1$
			return false;
		}
		return true;
	}

	public void bundleAdded(BundleDescription bundle) {
		if (!initialized)
			return;

		boolean alreadyThere = false;
		for (int i = 0; i < unresolvedBundles.size(); i++) {
			ResolverBundle rb = (ResolverBundle) unresolvedBundles.get(i);
			if (rb.getBundle() == bundle) {
				alreadyThere = true;
			}
		}
		if (!alreadyThere) {
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
		} else {
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
		if ((removedBundles = getRemovalPending(bundle.getBundle())) != null) {
			for (int i = 0; i < removedBundles.length; i++) {
				unresolveBundle((ResolverBundle) bundleMapping.get(removedBundles[i]), true);
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
		for (int i = 0; i < dependents.length; i++) {
			unresolveBundle((ResolverBundle) bundleMapping.get(dependents[i]), false);
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
		for (int k = 0; k < resolvedBundles.size(); k++) {
			ResolverBundle rb = (ResolverBundle) resolvedBundles.get(k);
			if (rb.getBundle().isResolved()) {
				continue;
			}
			ResolverImpl.log("    * WIRING for " + rb); //$NON-NLS-1$
			// Require bundles
			BundleConstraint[] requireBundles = rb.getRequires();
			if (requireBundles.length == 0) {
				ResolverImpl.log("        (r) no requires"); //$NON-NLS-1$
			} else {
				for (int i = 0; i < requireBundles.length; i++) {
					if (requireBundles[i].getMatchingBundle() == null) {
						ResolverImpl.log("        (r) " + rb.getBundle() + " -> NULL!!!"); //$NON-NLS-1$ //$NON-NLS-2$
					} else {
						ResolverImpl.log("        (r) " + rb.getBundle() + " -> " + requireBundles[i].getMatchingBundle()); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
			// Hosts
			BundleConstraint hostSpec = rb.getHost();
			if (hostSpec != null) {
				ResolverBundle[] hosts = hostSpec.getMatchingBundles();
				if (hosts != null)
					for (int i = 0; i < hosts.length; i++) {
						ResolverImpl.log("        (h) " + rb.getBundle() + " -> " + hosts[i].getBundle()); //$NON-NLS-1$ //$NON-NLS-2$
					}
			}
			// Imports
			ResolverImport[] imports = rb.getImportPackages();
			if (imports.length == 0) {
				ResolverImpl.log("        (w) no imports"); //$NON-NLS-1$
				continue;
			}
			for (int i = 0; i < imports.length; i++) {
				if (imports[i].isDynamic() && imports[i].getMatchingExport() == null) {
					ResolverImpl.log("        (w) " + imports[i].getBundle() + ":" + imports[i].getName() + " -> DYNAMIC"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else if (imports[i].isOptional() && imports[i].getMatchingExport() == null) {
					ResolverImpl.log("        (w) " + imports[i].getBundle() + ":" + imports[i].getName() + " -> OPTIONAL (could not be wired)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else if (imports[i].getMatchingExport() == null) {
					ResolverImpl.log("        (w) " + imports[i].getBundle() + ":" + imports[i].getName() + " -> NULL!!!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else {
					ResolverImpl.log("        (w) " + imports[i].getBundle() + ":" + imports[i].getName() + " -> " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							imports[i].getMatchingExport().getExporter() + ":" + imports[i].getMatchingExport().getName()); //$NON-NLS-1$
				}
			}
		}
	}

	static void log(String message) {
		Debug.println(message);
	}
}
