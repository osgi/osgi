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

/**
 * This class represents a specific version of a bundle in the system.
 */
public interface BundleDescription extends BaseDescription{

	/**
	 * Gets the Bundle-SymbolicName of this BundleDescription.
	 * Same as calling {@link BaseDescription#getName()}.
	 * @return The bundle symbolic name or null if the bundle
	 * does not have a symbolic name.
	 */
	public String getSymbolicName();

	/**
	 * The location string for this bundle.
	 * @return The bundle location or null if the bundle description
	 * does not have a location
	 */
	public String getLocation();

	/**
	 * Returns an array of bundle specifications defined by the Require-Bundle
	 * clause in this bundle.
	 * 
	 * @return an array of bundle specifications
	 */
	public BundleSpecification[] getRequiredBundles();

	/**
	 * Returns an array of export package descriptions defined by the Export-Package clauses.
	 * All export package descriptions are returned even if they have not been selected by
	 * the resolver as an exporter of the package.
	 *
	 * @return an array of export package descriptions
	 */
	public ExportPackageDescription[] getExportPackages();

	/**
	 * Returns an array of import package specifications defined by the Import-Package clause.
	 * @return an array of import package specifications
	 */
	public ImportPackageSpecification[] getImportPackages();

	/**
	 * Returns true if this bundle has one or more dynamically imported packages.
	 * @return true if this bundle has one or more dynamically imported packages.
	 */
	public boolean hasDynamicImports();

	/**
	 * Returns all the exported packages from this bundle that have been selected by
	 * the resolver.  The returned list will include the ExportPackageDescriptions
	 * returned by {@link #getExportPackages()} that have been selected by the resolver and
	 * packages which are propagated by this bundle.
	 * @return the selected list of packages that this bundle exports.  If the bundle is
	 * unresolved or has no shared packages then an empty array is returned.
	 */
	//TODO should this be name getResolvedExports()
	public ExportPackageDescription[] getSelectedExports();

	/**
	 * Returns all the bundle descriptions that satisfy all the require bundles for this bundle.
	 * If the bundle is not resolved or the bundle does not require any bundles then an empty array is
	 * returned.
	 * @return the bundles descriptions that satisfy all the require bundles for this bundle.
	 */
	public BundleDescription[] getResolvedRequires();

	/**
	 * Returns all the export packages that satisfy all the imported packages for this bundle.
	 * If the bundle is not resolved or the bundle does not import any packages then an empty array is
	 * returned.
	 * @return the exported packages that satisfy all the imported packages for this bundle.
	 */
	public ExportPackageDescription[] getResolvedImports();

	/**
	 * Returns true if this bundle is resolved in its host state.
	 * 
	 * @return true if this bundle is resolved in its host state.
	 */
	public boolean isResolved();

	/**
	 * Returns the state object which hosts this bundle. null is returned if
	 * this bundle is not currently in a state.
	 * 
	 * @return the state object which hosts this bundle.
	 */
	public State getContainingState();

	/**
	 * Returns the string representation of this bundle.
	 * 
	 * @return String representation of this bundle.
	 */
	public String toString();

	/**
	 * Returns the host for this bundle. null is returned if this bundle is not
	 * a fragment.
	 * 
	 * @return the host for this bundle.
	 */
	public HostSpecification getHost();

	/**
	 * Returns the numeric id of this bundle.  Typically a bundle description
	 * will only have a numeric id if it represents a bundle that is installed in a 
	 * framework as the framework assigns the ids.  -1 is returned if the id is not known.
	 * 
	 * @return the numeric id of this bundle description
	 */
	public long getBundleId();

	/**
	 * Returns all fragments known to this bundle (regardless resolution status).
	 * 
	 * @return an array of BundleDescriptions containing all known fragments
	 */
	public BundleDescription[] getFragments();

	/**
	 * Returns whether this bundle is a singleton.  Singleton bundles require 
	 * that at most one single version of the bundle can be resolved at a time. 
	 * <p>
	 * The existence of a single bundle marked as singleton causes all bundles
	 * with the same symbolic name to be treated as singletons as well.  
	 * </p>
	 * 
	 * @return <code>true</code>, if this bundle is a singleton, 
	 * <code>false</code> otherwise
	 */
	public boolean isSingleton();

	/**
	 * Returns whether this bundle is pending a removal.  A bundle is pending
	 * removal if it has been removed from the state but other bundles in
	 * the state currently depend on it.
	 * @return <code>true</code>, if this bundle is pending a removal,
	 * <code>false</code> otherwise
	 */
	public boolean isRemovalPending();

	public BundleDescription[] getDependents();

	/**
	 * Returns the user object associated to this bundle description, or 
	 * <code>null</code> if none exists.
	 *  
	 * @return the user object associated to this bundle  description,
	 * or <code>null</code>
	 */
	public Object getUserObject();

	/**
	 * Associates a user-provided object to this bundle description, or
	 * removes an existing association, if <code>null</code> is provided. The 
	 * provided object is not interpreted in any ways by this bundle 
	 * description.
	 * 
	 * @param userObject an arbitrary object provided by the user, or 
	 * <code>null</code>
	 */
	public void setUserObject(Object userObject);

	/**
	 * Returns the platform filter in the form of an LDAP filter
	 * @return the platfomr filter in the form of an LDAP filter
	 */
	public String getPlatformFilter();

	/**
	 * Returns true if this bundle allows fragments to attach
	 * @return true if this bundle allows fragments to attach
	 */
	public boolean attachFragments();

	/**
	 * Returns true if this bundle allows fragments to attach dynamically
	 * after it has been resolved.
	 * @return true if this bundle allows fragments to attach dynamically
	 */
	public boolean dynamicFragments();
}
