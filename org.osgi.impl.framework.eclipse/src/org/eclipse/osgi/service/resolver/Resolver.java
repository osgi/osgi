/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.service.resolver;

public interface Resolver {

	/**
	 * Resolves the state associated with this resolver and returns an array of
	 * bundle deltas describing the changes.. The state and version bindings
	 * for the various bundles and packages in this state are updated and a
	 * array containing bundle deltas describing the changes returned.
	 * <p>
	 * This method is intended to be called only by State objects in response
	 * to a user invocation of State.resolve(). States will typically refuse to
	 * update their constituents (see State.resolveBundle() and
	 * State.resolveConstraint()) if their resolve method is not currently
	 * being invoked.
	 * </p>
	 * <p>
	 * Note the given state is destructively modified to reflect the results of
	 * resolution.
	 * </p>
	 */
	public void resolve();

	/**
	 * 
	 */
	public void resolve(BundleDescription[] discard);

	/**
	 * Flushes this resolver of any stored/cached data it may be keeping to
	 * facilitate incremental processing on its associated state. This is
	 * typicaly used when switching the resolver's state object.
	 */
	public void flush();

	/**
	 * Returns the state associated with this resolver. A state can work with
	 * at most one resolver at any given time. Similarly, a resolver can work
	 * with at most one state at a time.
	 * 
	 * @return the state for this resolver. null is returned if the resolver
	 * does not have a state
	 */
	public State getState();

	/**
	 * Sets the state associated with this resolver. A state can work with at
	 * most one resolver at any given time. Similarly, a resolver can work with
	 * at most one state at a time.
	 * <p>
	 * To ensure that this resolver and the given state are properly linked,
	 * the following expression must be included in this method if the given
	 * state (value) is not identical to the result of this.getState().
	 * </p>
	 * 
	 * <pre>
	 *  if (this.getState() != value) value.setResolver(this);
	 * </pre>
	 */
	public void setState(State value);

	/**
	 * Notifies the resolver a bundle has been added to the state.
	 * @param bundle
	 */
	public void bundleAdded(BundleDescription bundle);

	/**
	 * Notifies the resolver a bundle has been removed from the state.
	 * @param bundle the bundle description to remove
	 * @param pending indicates if the bundle to be remove has current dependents and
	 * will pend complete removal until the bundle has been re-resolved.
	 */
	public void bundleRemoved(BundleDescription bundle, boolean pending);

	/**
	 * Notifies the resolver a bundle has been updated in the state.
	 * @param newDescription the new description
	 * @param existingDescription the existing description
	 * @param pending indicates if the bundle to be updated has current dependents and
	 * will pend complete removal until the bundle has been re-resolved.
	 */
	public void bundleUpdated(BundleDescription newDescription, BundleDescription existingDescription, boolean pending);

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
	public ExportPackageDescription resolveDynamicImport(BundleDescription importingBundle, String requestedPackage);
}