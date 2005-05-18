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
package org.eclipse.osgi.service.resolver;

import java.util.Dictionary;

import org.osgi.framework.*;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

/**
 * The state of a system as reported by a resolver. This includes all bundles
 * presented to the resolver relative to this state (i.e., both resolved and
 * unresolved).
 */
public interface State {
	/**
	 * Adds the given bundle to this state.
	 * 
	 * @param description the description to add
	 * @return a boolean indicating whether the bundle was successfully added
	 */
	public boolean addBundle(BundleDescription description);

	/**
	 * Returns a delta describing the differences between this state and the
	 * given state. The given state is taken as the base so the absence of a bundle
	 * in this state is reported as a deletion, etc.
	 *<p>Note that the generated StateDelta will contain BundleDeltas with one
	 *of the following types: BundleDelta.ADDED, BundleDelta.REMOVED and 
	 *BundleDelta.UPDATED</p>
	 * 
	 * @param baseState the base state
	 * @return a delta describing differences between this and the base state state 
	 */
	public StateDelta compare(State baseState) throws BundleException;

	/**
	 * Removes a bundle description with the given bundle id.
	 * 
	 * @param bundleId the id of the bundle description to be removed
	 * @return the removed bundle description, or <code>null</code>, if a bundle
	 * 	with the given id does not exist in this state
	 */
	public BundleDescription removeBundle(long bundleId);

	/**
	 * Removes the given bundle description.
	 * 
	 * @param bundle the bundle description to be removed
	 * @return <code>true</code>, if if the bundle description was removed, 
	 * 	<code>false</code> otherwise 	
	 */
	public boolean removeBundle(BundleDescription bundle);

	/**
	 * Updates an existing bundle description with the given description. 
	 * 
	 * @param newDescription the bundle description to replace an existing one
	 * @return <code>true</code>, if if the bundle description was updated, 
	 * 	<code>false</code> otherwise 	
	 */
	public boolean updateBundle(BundleDescription newDescription);

	/**
	 * Returns the delta representing the changes from the time this state was
	 * first captured until now.
	 * 
	 * @return the state delta
	 */
	public StateDelta getChanges();

	/**
	 * Returns descriptions for all bundles known to this state.
	 * 
	 * @return the descriptions for all bundles known to this state.
	 */
	public BundleDescription[] getBundles();

	/**
	 * Returns the bundle descriptor for the bundle with the given id. 
	 * <code>null</code> is returned if no such bundle is found in 
	 * this state. 
	 * 
	 * @return the descriptor for the identified bundle
	 * @see BundleDescription#getBundleId()
	 */
	public BundleDescription getBundle(long id);

	/**
	 * Returns the bundle descriptor for the bundle with the given name and
	 * version. null is returned if no such bundle is found in this state. If
	 * the version argument is null then the bundle with the given name which
	 * is resolve and/or has the highest version number is returned.
	 * 
	 * @param symbolicName symbolic name of the bundle to query
	 * @param version version of the bundle to query. null matches any bundle
	 * @return the descriptor for the identified bundle
	 */
	public BundleDescription getBundle(String symbolicName, Version version);

	/**
	 * Returns the bundle descriptor for the bundle with the given location
	 * identifier. null is returned if no such bundle is found in this state. 
	 * 
	 * @param location location identifier of the bundle to query
	 * @return the descriptor for the identified bundle
	 */
	public BundleDescription getBundleByLocation(String location);

	/**
	 * Returns the timestamp for this state. This
	 * correlates this timestamp to the system state. For example, if
	 * the system state timestamp is 4 but then some bundles are installed,
	 * the system state timestamp is updated. By comparing 4 to the current system
	 * state timestamp it is possible to detect if the states are out of sync.
	 * 
	 * @return the timestamp of this state
	 */
	public long getTimeStamp();

	/**
	 * Sets the timestamp for this state
	 * @param newTimeStamp the new timestamp for this state
	 */
	public void setTimeStamp(long newTimeStamp);

	/**
	 * Returns true if there have been no modifications to this state since the
	 * last time resolve() was called.
	 * 
	 * @return whether or not this state has changed since last resolved.
	 */
	public boolean isResolved();

	/**
	 * Resolves the given version constraint with the given supplier. The given
	 * constraint object is destructively modified to reflect its new resolved
	 * state. Note that a constraint can be unresolved by passing null for 
	 * the supplier.
	 * <p>
	 * This method is intended to be used by resolvers in the process of
	 * determining which constraints are satisfied by which components.
	 * </p>
	 * 
	 * @param constraint the version constraint to update
	 * @param supplier the supplier which satisfies the constraint. May be null if 
	 * the constraint is to be unresolved.
	 * @throws IllegalStateException if this is not done during a call to
	 * <code>resolve</code>
	 */
	public void resolveConstraint(VersionConstraint constraint, BaseDescription supplier);

	/**
	 * Sets whether or not the given bundle is selected in this state.
	 * <p>
	 * This method is intended to be used by resolvers in the process of
	 * determining which constraints are satisfied by which components.
	 * </p>
	 * 
	 * @param bundle the bundle to update
	 * @param status whether or not the given bundle is resolved, if false the other parameters are ignored
	 * @param hosts the host for the resolve fragment, can be <code>null</code>
	 * @param selectedExports the selected exported packages for this resolved bundle, can be <code>null</code>
	 * @param resolvedRequires the BundleDescriptions that resolve the required bundles for this bundle, can be <code>null</code>
	 * @param resolvedImports the exported packages that resolve the imports for this bundle, can be <code>null</code>
	 * @throws IllegalStateException if this is not done during a call to <code>resolve</code>
	 */
	public void resolveBundle(BundleDescription bundle, boolean status, BundleDescription[] hosts, ExportPackageDescription[] selectedExports, BundleDescription[] resolvedRequires, ExportPackageDescription[] resolvedImports);

	/**
	 * Sets the given removal pending bundle to removal complete for this state.
	 * <p>
	 * This method is intended to be used by resolvers in the process of 
	 * resolving bundles.
	 * </p>
	 * @param bundle the bundle to set a removal complete.
	 * @throws IllegalStateException if this is not done during a call to
	 * <code>resolve</code>
	 */
	public void removeBundleComplete(BundleDescription bundle);

	/**
	 * Returns the resolver associated with this state. A state can work with
	 * at most one resolver at any given time. Similarly, a resolver can work
	 * with at most one state at a time.
	 * 
	 * @return the resolver for this state. null is returned if the state does
	 * not have a resolver
	 */
	public Resolver getResolver();

	/**
	 * Sets the resolver associated with this state. A state can work with at
	 * most one resolver at any given time. Similarly, a resolver can work with
	 * at most one state at a time.
	 * <p>
	 * To ensure that this state and the given resovler are properly linked,
	 * the following expression must be included in this method if the given
	 * resolver (value) is not identical to the result of this.getResolver().
	 * 
	 * <pre>
	 *  if (this.getResolver() != value) value.setState(this);
	 * </pre>
	 * 
	 * </p>
	 */
	// TODO what happens if you set the Resolver after some bundles have
	// been added to the state but it is not resolved?  Should setting
	// the resolver force a state to be unresolved?
	public void setResolver(Resolver value);

	/**
	 * Resolves the constraints contained in this state using the resolver
	 * currently associated with the state and returns a delta describing the
	 * changes in resolved states and dependencies in the state.
	 * <p>
	 * Note that this method is typically implemented using
	 * 
	 * <pre>
	 *  this.getResolver().resolve();
	 * </pre>
	 * 
	 * and is the preferred path for invoking resolution. In particular, states
	 * should refuse to perform updates (@see #select() and
	 * #resolveConstraint()) if they are not currently involved in a resolution
	 * cycle.
	 * <p>
	 * Note the given state is destructively modified to reflect the results of
	 * resolution.
	 * </p>
	 * 
	 * @param incremental a flag controlling whether resolution should be incremental
	 * @return a delta describing the changes in resolved state and 
	 * interconnections
	 */
	public StateDelta resolve(boolean incremental);

	/**
	 * Same as State.resolve(true);
	 */
	public StateDelta resolve();

	/**
	 * Resolves the constraints contained in this state using the resolver
	 * currently associated with the state in a incremental, "least-perturbing" 
	 * mode, and returns a delta describing the changes in resolved states and 
	 * dependencies in the state.
	 * 
	 * @param discard an array containing descriptions for bundles whose 
	 * 	current resolution state should be forgotten.  If <code>null</code>
	 *  then all the current removal pending BundleDescriptions are refreshed.
	 * @return a delta describing the changes in resolved state and 
	 * 	interconnections
	 */
	public StateDelta resolve(BundleDescription[] discard);

	/**
	 * Sets the version overrides which are to be applied during the resolutoin
	 * of this state. Version overrides allow external forces to
	 * refine/override the version constraints setup by the components in the
	 * state.
	 * 
	 * @param value
	 */
	// TODO the exact form of this is not defined as yet.
	public void setOverrides(Object value);

	/**
	 * Returns descriptions for all bundles currently resolved in this state.
	 * 
	 * @return the descriptions for all bundles currently resolved in this
	 * state.
	 */
	public BundleDescription[] getResolvedBundles();

	/**
	 * Returns whether this state is empty.
	 * @return <code>true</code> if this state is empty, <code>false</code> 
	 * 	otherwise
	 */
	public boolean isEmpty();

	/**
	 * Returns all exported packages in this state, according to the OSGi rules for resolution. 
	 * @see org.osgi.service.packageadmin.PackageAdmin#getExportedPackages(Bundle)
	 */
	public ExportPackageDescription[] getExportedPackages();

	/**
	 * Returns all bundle descriptions with the given bundle symbolic name.
	 * @param symbolicName symbolic name of the bundles to query
	 * @return the descriptors for all bundles known to this state with the
	 * specified symbolic name.
	 */
	public BundleDescription[] getBundles(String symbolicName);

	/**
	 * Returns the factory that created this state.
	 * @return the state object factory that created this state 
	 */
	public StateObjectFactory getFactory();

	/**
	 * Attempts to find an ExportPackageDescription that will satisfy a dynamic import
	 * for the specified requestedPackage for the specified importingBundle.  If no
	 * ExportPackageDescription is available that satisfies a dynamic import for the 
	 * importingBundle then <code>null</code> is returned.
	 * @param importingBundle the BundleDescription that is requesting a dynamic package
	 * @param requestedPackage the name of the package that is being requested
	 * @return the ExportPackageDescription that satisfies the dynamic import request; 
	 * a value of <code>null</code> is returned if none is available.
	 */
	public ExportPackageDescription linkDynamicImport(BundleDescription importingBundle, String requestedPackage);

	/**
	 * Sets the platform properties of the state.  The platform properties
	 * are used to match platform filters that are specified in Eclipse-PlatformFilter
	 * bundle manifest header.  The following propreties are supported by the
	 * state: <p>
	 * osgi.nl - the platform language setting<br>
	 * osgi.os - the platform operating system<br>
	 * osgi.arch - the platform architecture<br>
	 * osgi.ws - the platform windowing system<br>
	 * org.osgi.framework.system.packages - the packages exported by the system bundle <br>
	 * osgi.resolverMode - the resolver mode.  A value of "strict" will set the resolver mode to strict.<br>
	 * <p>
	 * The values used for the supported properties can be <tt>String</tt> type
	 * to specify a single value for the property or they can by <tt>String[]</tt>
	 * to specify a list of values for the property. 
	 * @param platformProperties the platform properties of the state
	 * @return false if the platformProperties specified do not change any of the
	 * supported properties already set.  If any of the supported property values 
	 * are changed as a result of calling this method then true is returned.
	 */
	public boolean setPlatformProperties(Dictionary platformProperties);

	/**
	 * Sets the platform properties of the state to a list of platform properties.  
	 * The platform properties are used to match platform filters that are specified 
	 * in Eclipse-PlatformFilter bundle manifest header. 
	 * @see #setPlatformProperties(Dictionary) for a list of supported properties.  
	 * 
	 * @param platformProperties a set of platform properties for the state
	 * @return false if the platformProperties specified do not change any of the
	 * supported properties already set.  If any of the supported property values 
	 * are changed as a result of calling this method then true is returned.
	 */
	public boolean setPlatformProperties(Dictionary[] platformProperties);

	/**
	 * Returns the list of platform properties currently set for this state.
	 * @return the list of platform properties currently set for this state.
	 */
	public Dictionary[] getPlatformProperties();

	/**
	 * Returns the list of system packages which are exported by the system bundle.  
	 * The list of system packages is set by the org.osgi.framework.system.packages
	 * value in the platform properties for this state.
	 * @see #setPlatformProperties(Dictionary)
	 * @return the list of system packages
	 */
	public ExportPackageDescription[] getSystemPackages();
}
