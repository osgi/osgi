package org.osgi.dmt.residential;

import static org.osgi.dmt.ddf.Scope.SCOPE.*;
import info.dmtree.*;

import org.osgi.dmt.ddf.*;

/**
 * The PackageState node maintains the information about packages. This object
 * can be used to retrieve package dependencies between bundles and maps to the
 * Wiring API.
 * 
 * @remark Does it need adaption to the Wiring API?
 * @remark Should this not be part of the BundleState
 */
public interface PackageState {
	/**
	 * The fully qualified name of the package.
	 * 
	 * @return The fully qualified name of the package
	 */
	@Scope(A)
	String Name();

	/**
	 * The version of the package or the empty string if the package has no
	 * version.
	 * 
	 * @return The version of the package.
	 */
	@Scope(A)
	String Version();

	/**
	 * Represents the Removal Pending status of the package. If a bundle
	 * exporting the package has already been uninstalled or updated but the
	 * package is still used, this node must be {@code true}, otherwise
	 * {@code false}.
	 * 
	 * @remark What is the use case for this? It is very transient
	 * @return The Removal Pending status
	 */
	@Scope(A)
	boolean RemovalPending();

	/**
	 * The Bundle Id of the bundle that exports this package.
	 * 
	 * @return The bundle ID of the exporting package.
	 */
	@Scope(A)
	long ExportingBundle();

	/**
	 * A List of Bundle IDs of Bundles that import this package.
	 * 
	 * @return A list of Bundle IDs that import this package.
	 */
	@Scope(A)
	@NodeType(DmtConstants.DDF_LIST_SUBTREE)
	LIST<Long> ImportingBundles();

	/**
	 * Extension node
	 * 
	 * @return Extension node
	 */
	@Scope(A)
	Opt<NODE> Ext();
}
