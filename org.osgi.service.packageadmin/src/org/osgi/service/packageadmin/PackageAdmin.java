/*
 * Copyright (c) OSGi Alliance (2001, 2010). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.packageadmin;

import java.util.Collection;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkListener;

/**
 * Framework service which allows bundle programmers to inspect the package
 * wiring state of bundles in the Framework as well as other functions related
 * to the class loader network among bundles.
 * 
 * <p>
 * If present, there will only be a single instance of this service registered
 * with the Framework.
 * 
 * @ThreadSafe
 * @version $Revision$
 * @see BundlePackageAdmin
 */
public interface PackageAdmin {
	/**
	 * Returns a list of package exports for exported packages with the
	 * specified package name.
	 * 
	 * <p>
	 * The package exports in the list are ordered in descending version such
	 * that the first package export has the highest version and last package
	 * export has the lowest version. The list will only contain package exports
	 * of {@link BundleWiring#isCurrent() current} bundle wirings.
	 * 
	 * @param name The package name of the exported packages to be returned or
	 *        <code>null</code> to return all exported packages.
	 * @return A <code>List</code> containing a snapshot of
	 *         {@link PackageExport}s, or an empty list if no exported packages
	 *         with the specified name exist.
	 * @since 1.3
	 */
	List<PackageExport> getPackageExports(String name);

	/**
	 * Refresh the specified bundles. This forces the update (replacement) or
	 * removal of packages exported by the specified bundles.
	 * 
	 * <p>
	 * The technique by which this is accomplished may vary among different
	 * Framework implementations. One permissible implementation is to stop and
	 * restart the Framework.
	 * 
	 * <p>
	 * This method returns to the caller immediately and then performs the
	 * following steps on a separate thread:
	 * 
	 * <ol>
	 * <li>Compute the {@link #getDependencyClosure(Collection) dependency
	 * closure} of the specified bundles. If no bundles are specified, compute
	 * the dependency closure of the bundles returned by
	 * {@link #getRemovalPendingBundles()}.
	 * 
	 * <li>Each bundle in the dependency closure that is in the
	 * <code>ACTIVE</code> state will be stopped as described in the
	 * <code>Bundle.stop</code> method.
	 * 
	 * <li>Each bundle in the dependency closure that is in the
	 * <code>RESOLVED</code> state is unresolved and thus moved to the
	 * <code>INSTALLED</code> state. The effect of this step is that bundles in
	 * the dependency closure are no longer <code>RESOLVED</code>.
	 * 
	 * <li>Each bundle in the dependency closure that is in the
	 * <code>UNINSTALLED</code> state is removed from the dependency closure and
	 * is now completely removed from the Framework.
	 * 
	 * <li>Each bundle in the dependency closure that was in the
	 * <code>ACTIVE</code> state prior to Step 2 is started as described in the
	 * <code>Bundle.start</code> method, causing all bundles required for the
	 * restart to be resolved. It is possible that, as a result of the previous
	 * steps, packages that were previously exported no longer are. Therefore,
	 * some bundles may be unresolvable until another bundle satisfying the
	 * dependency has been installed in the Framework.
	 * <li>A framework event of type
	 * <code>FrameworkEvent.PACKAGES_REFRESHED</code> is fired.
	 * </ol>
	 * 
	 * <p>
	 * For any exceptions that are thrown during any of these steps, a framework
	 * event of type <code>FrameworkEvent.ERROR</code> is fired containing the
	 * exception. The source bundle for these events should be the specific
	 * bundle to which the exception is related. If no specific bundle can be
	 * associated with the exception then the System Bundle must be used as the
	 * source bundle for the event. All framework events fired by this method
	 * are also delivered to the specified <code>FrameworkListener</code>s in
	 * the order they are specified.
	 * 
	 * @param bundles The bundles to be refreshed, or <code>null</code> to
	 *        refresh the bundles returned by
	 *        {@link #getRemovalPendingBundles()}.
	 * @param listeners Zero or more listeners to be notified when the refresh
	 *        bundles has been completed. The specified listeners do not need to
	 *        be otherwise registered with the framework. If a specified
	 *        listener is already registered with the framework, it will be
	 *        notified twice.
	 * @throws SecurityException If the caller does not have
	 *         <code>AdminPermission[System Bundle,RESOLVE]</code> and the Java
	 *         runtime environment supports permissions.
	 * @throws IllegalArgumentException If the specified <code>Bundle</code>s
	 *         were not created by the same framework instance that registered
	 *         this <code>PackageAdmin</code> service.
	 * @since 1.3
	 */
	void refreshBundles(Collection<Bundle> bundles,
			FrameworkListener... listeners);

	/**
	 * Resolve the specified bundles. The Framework must attempt to resolve the
	 * specified bundles that are unresolved. Additional bundles that are not
	 * included in the specified bundles may be resolved as a result of calling
	 * this method. A permissible implementation of this method is to attempt to
	 * resolve all unresolved bundles installed in the framework.
	 * 
	 * <p>
	 * If <code>null</code> is specified then the Framework will attempt to
	 * resolve all unresolved bundles. This method must not cause any bundle to
	 * be refreshed, stopped, or started. This method will not return until the
	 * operation has completed.
	 * 
	 * @param bundles The bundles to resolve or <code>null</code> to resolve all
	 *        unresolved bundles installed in the Framework.
	 * @return <code>true</code> if all specified bundles are resolved.
	 * @throws SecurityException If the caller does not have
	 *         <code>AdminPermission[System Bundle,RESOLVE]</code> and the Java
	 *         runtime environment supports permissions.
	 * @throws IllegalArgumentException If the specified <code>Bundle</code>s
	 *         were not created by the same framework instance that registered
	 *         this <code>PackageAdmin</code> service.
	 * @since 1.3
	 */
	boolean resolveBundles(Collection<Bundle> bundles);

	/**
	 * Returns the bundles with the specified symbolic name whose bundle version
	 * is within the specified version range. If no bundles are installed that
	 * have the specified symbolic name, then <code>null</code> is returned.
	 * If a version range is specified, then only the bundles that have the
	 * specified symbolic name and whose bundle versions belong to the specified
	 * version range are returned. The returned bundles are ordered by version
	 * in descending version order so that the first element of the array
	 * contains the bundle with the highest version.
	 * 
	 * @see org.osgi.framework.Constants#BUNDLE_VERSION_ATTRIBUTE
	 * @param symbolicName The symbolic name of the desired bundles.
	 * @param versionRange The version range of the desired bundles, or
	 *        <code>null</code> if all versions are desired.
	 * @return An array of bundles with the specified name belonging to the
	 *         specified version range ordered in descending version order, or
	 *         <code>null</code> if no bundles are found.
	 * @since 1.2
	 */
	Bundle[] getBundles(String symbolicName, String versionRange);

	/**
	 * Returns the bundle from which the specified class is loaded. The class
	 * loader of the returned bundle must have been used to load the specified
	 * class. If the class was not loaded by a bundle class loader then
	 * <code>null</code> is returned.
	 * 
	 * @param clazz The class object from which to locate the bundle.
	 * @return The bundle from which the specified class is loaded or
	 *         <code>null</code> if the class was not loaded by a bundle class
	 *         loader created by the same framework instance that registered
	 *         this <code>PackageAdmin</code> service.
	 * @since 1.2
	 */
	Bundle getBundle(Class clazz);

	/**
	 * Return the bundles that have is {@link BundleWiring#isCurrent()
	 * non-current}, {@link BundleWiring#isInUse() in use} {@link BundleWiring}
	 * s. This is typically the bundles which have been updated or uninstalled
	 * since the last call to
	 * {@link #refreshBundles(Collection, FrameworkListener...)}.
	 * 
	 * @return A <code>Collection</code> containing a snapshot of the
	 *         <code>Bundle</code>s which have non-current, in use
	 *         <code>BundleWiring</code>s, or an empty collection if there are
	 *         no such bundles.
	 * @since 1.3
	 */
	Collection<Bundle> getRemovalPendingBundles();

	/**
	 * Return the dependency closure for the specified bundles.
	 * 
	 * <p>
	 * A graph of bundles is computed starting with the specified bundles. The
	 * graph is expanded by adding any bundle that is either wired to a package
	 * that is currently exported by a bundle in the graph or requires a bundle
	 * in the graph. The graph is fully constructed when there is no bundle
	 * outside the graph that is wired to a bundle in the graph. The graph may
	 * contain <code>UNINSTALLED</code> bundles that are removal pending.
	 * 
	 * @param bundles The initial bundles for which to generate the dependency
	 *        closure.
	 * @return A <code>Collection</code> containing a snapshot of the dependency
	 *         closure of the specified bundles, or an empty collection if there
	 *         were no specified bundles.
	 * @throws IllegalArgumentException If the specified <code>Bundle</code>s
	 *         were not created by the same framework instance that registered
	 *         this <code>PackageAdmin</code> service.
	 * @since 1.3
	 */
	Collection<Bundle> getDependencyClosure(Collection<Bundle> bundles);

	/**
	 * Gets the exported packages for the specified bundle.
	 * 
	 * @param bundle The bundle whose exported packages are to be returned, or
	 *        <code>null</code> if all exported packages are to be returned. If
	 *        the specified bundle is the system bundle (that is, the bundle
	 *        with id zero), this method returns all the packages known to be
	 *        exported by the system bundle. This will include the package
	 *        specified by the {@link Constants#FRAMEWORK_SYSTEMPACKAGES} system
	 *        property as well as any other package exported by the framework
	 *        implementation.
	 * 
	 * @return An array of exported packages, or <code>null</code> if the
	 *         specified bundle has no exported packages.
	 * @throws IllegalArgumentException If the specified <code>Bundle</code> was
	 *         not created by the same framework instance that registered this
	 *         <code>PackageAdmin</code> service.
	 * @deprecated As of 1.3. Replaced by {@link Bundle#adapt(Class)
	 *             bundle.adapt}({@link BundleWiring}.class).
	 *             {@link BundleWiring#getExported() getExported()}
	 */
	ExportedPackage[] getExportedPackages(Bundle bundle);

	/**
	 * Gets the exported packages for the specified package name.
	 * 
	 * @param name The name of the exported packages to be returned, or
	 *        <code>null</code> if all exported packages are to be returned.
	 * 
	 * @return An array of the exported packages, or <code>null</code> if no
	 *         exported packages with the specified name exist.
	 * @since 1.2
	 * @deprecated As of 1.3. Replaced by {@link #getPackageExports(String)}.
	 */
	ExportedPackage[] getExportedPackages(String name);

	/**
	 * Gets the exported package for the specified package name.
	 * 
	 * <p>
	 * If there are multiple exported packages with specified name, the exported
	 * package with the highest version will be returned.
	 * 
	 * @param name The name of the exported package to be returned.
	 * @return The exported package, or <code>null</code> if no exported package
	 *         with the specified name exists.
	 * @deprecated As of 1.3. Replaced by {@link #getPackageExports(String)}
	 *             .get(0).
	 */
	ExportedPackage getExportedPackage(String name);

	/**
	 * Forces the update (replacement) or removal of packages exported by the
	 * specified bundles.
	 * 
	 * @param bundles The bundles whose exported packages are to be updated or
	 *        removed, or <code>null</code> for all bundles updated or
	 *        uninstalled since the last call to this method.
	 * @throws SecurityException If the caller does not have
	 *         <code>AdminPermission[System Bundle,RESOLVE]</code> and the Java
	 *         runtime environment supports permissions.
	 * @throws IllegalArgumentException If the specified <code>Bundle</code>s
	 *         were not created by the same framework instance that registered
	 *         this <code>PackageAdmin</code> service.
	 * @deprecated As of 1.3. Replaced by
	 *             {@link #refreshBundles(Collection, FrameworkListener...)}
	 */
	void refreshPackages(Bundle[] bundles);

	/**
	 * Resolve the specified bundles. The Framework must attempt to resolve the
	 * specified bundles that are unresolved. Additional bundles that are not
	 * included in the specified bundles may be resolved as a result of calling
	 * this method. A permissible implementation of this method is to attempt to
	 * resolve all unresolved bundles installed in the framework.
	 * 
	 * <p>
	 * If <code>null</code> is specified then the Framework will attempt to
	 * resolve all unresolved bundles. This method must not cause any bundle to
	 * be refreshed, stopped, or started. This method will not return until the
	 * operation has completed.
	 * 
	 * @param bundles The bundles to resolve or <code>null</code> to resolve all
	 *        unresolved bundles installed in the Framework.
	 * @return <code>true</code> if all specified bundles are resolved.
	 * @throws SecurityException If the caller does not have
	 *         <code>AdminPermission[System Bundle,RESOLVE]</code> and the Java
	 *         runtime environment supports permissions.
	 * @throws IllegalArgumentException If the specified <code>Bundle</code>s
	 *         were not created by the same framework instance that registered
	 *         this <code>PackageAdmin</code> service.
	 * @deprecated As of 1.3. Replaced by {@link #resolveBundles(Collection)}
	 * @since 1.2
	 */
	boolean resolveBundles(Bundle[] bundles);

	/**
	 * Returns an array of required bundles having the specified symbolic name.
	 * 
	 * <p>
	 * If <code>null</code> is specified, then all required bundles will be
	 * returned.
	 * 
	 * @param symbolicName The bundle symbolic name or <code>null</code> for all
	 *        required bundles.
	 * @return An array of required bundles or <code>null</code> if no required
	 *         bundles exist for the specified symbolic name.
	 * @since 1.2
	 * @deprecated As of 1.3. Use {@link #getBundles(String, String)} to get
	 *             Bundle objects for the symbolic name and then call
	 *             {@link Bundle#adapt(Class) bundle.adapt}(
	 *             {@link BundleWiring}.class).
	 *             {@link BundleWiring#getRequired() getRequired()}.
	 */
	RequiredBundle[] getRequiredBundles(String symbolicName);

	/**
	 * Returns an array of attached fragment bundles for the specified bundle.
	 * If the specified bundle is a fragment then <code>null</code> is returned.
	 * If no fragments are attached to the specified bundle then
	 * <code>null</code> is returned.
	 * <p>
	 * This method does not attempt to resolve the specified bundle. If the
	 * specified bundle is not resolved then <code>null</code> is returned.
	 * 
	 * @param bundle The bundle whose attached fragment bundles are to be
	 *        returned.
	 * @return An array of fragment bundles or <code>null</code> if the bundle
	 *         does not have any attached fragment bundles or the bundle is not
	 *         resolved.
	 * @throws IllegalArgumentException If the specified <code>Bundle</code> was
	 *         not created by the same framework instance that registered this
	 *         <code>PackageAdmin</code> service.
	 * @since 1.2
	 * @deprecated As of 1.3. Replaced by {@link Bundle#adapt(Class)
	 *             bundle.adapt}( {@link BundleWiring}.class).
	 *             {@link BundleWiring#getFragmentInfos() getFragmentInfos()}.
	 */
	Bundle[] getFragments(Bundle bundle);

	/**
	 * Returns the host bundles to which the specified fragment bundle is
	 * attached.
	 * 
	 * @param bundle The fragment bundle whose host bundles are to be returned.
	 * @return An array containing the host bundles to which the specified
	 *         fragment is attached or <code>null</code> if the specified bundle
	 *         is not a fragment or is not attached to any host bundles.
	 * @throws IllegalArgumentException If the specified <code>Bundle</code> was
	 *         not created by the same framework instance that registered this
	 *         <code>PackageAdmin</code> service.
	 * @since 1.2
	 * @deprecated As of 1.3. Use {@link Bundle#adapt(Class) bundle.adapt}(
	 *             {@link BundlePackageAdmin}.class).
	 *             {@link BundlePackageAdmin#getWirings() getWirings()} to get
	 *             the bundle wirings in which the fragment participates.
	 */
	Bundle[] getHosts(Bundle bundle);

	/**
	 * Bundle type indicating the bundle is a fragment bundle.
	 * 
	 * @since 1.2
	 * @deprecated As of 1.2. Replaced by {@link Bundle#TYPE_FRAGMENT}.
	 */
	int	BUNDLE_TYPE_FRAGMENT	= 0x00000001;

	/**
	 * Returns the special type of the specified bundle. The bundle type values
	 * are:
	 * <ul>
	 * <li>{@link #BUNDLE_TYPE_FRAGMENT}
	 * </ul>
	 * 
	 * A bundle may be more than one type at a time. A type code is used to
	 * identify the bundle type for future extendability.
	 * 
	 * <p>
	 * If a bundle is not one or more of the defined types then 0x00000000 is
	 * returned.
	 * 
	 * @param bundle The bundle for which to return the special type.
	 * @return The special type of the bundle.
	 * @throws IllegalArgumentException If the specified <code>Bundle</code> was
	 *         not created by the same framework instance that registered this
	 *         <code>PackageAdmin</code> service.
	 * @since 1.2
	 * @deprecated As of 1.3. Replaced by {@link Bundle#getTypes()}.
	 */
	int getBundleType(Bundle bundle);
}
