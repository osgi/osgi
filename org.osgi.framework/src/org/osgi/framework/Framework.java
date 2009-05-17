/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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


package org.osgi.framework;

import java.io.InputStream;
import java.util.Collection;

import org.osgi.framework.bundle.BundleListener;
import org.osgi.framework.bundle.FrameworkListener;

/**
 * 
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public interface Framework {

	/**
	 * Installs a bundle from the specified <code>InputStream</code> object.
	 * 
	 * <p>
	 * This method performs all of the steps listed in
	 * <code>BundleContext.installBundle(String location)</code>, except that
	 * the bundle's content will be read from the <code>InputStream</code>
	 * object. The location identifier string specified will be used as the
	 * identity of the bundle.
	 * 
	 * <p>
	 * This method must always close the <code>InputStream</code> object, even
	 * if an exception is thrown.
	 * 
	 * @param location The location identifier of the bundle to install.
	 * @param input The <code>InputStream</code> object from which this bundle
	 *        will be read.
	 * @return The <code>Bundle</code> object of the installed bundle.
	 * @throws BundleException If the provided stream cannot be read or the
	 *         installation failed.
	 * @throws java.lang.SecurityException If the caller does not have the
	 *         appropriate
	 *         <code>AdminPermission[installed bundle,LIFECYCLE]</code>, and
	 *         the Java Runtime Environment supports permissions.
	 * @throws java.lang.IllegalStateException If this BundleContext is no
	 *         longer valid.
	 * @see #installBundle(java.lang.String)
	 */
	public Bundle installBundle(String location, InputStream input);

	/**
	 * Returns the bundle with the specified identifier.
	 * 
	 * @param id The identifier of the bundle to retrieve.
	 * @return A <code>Bundle</code> object or <code>null</code> if the
	 *         identifier does not match any installed bundle.
	 */
	public Bundle getBundle(long id);

	/**
	 * Returns a list of all installed bundles.
	 * <p>
	 * This method returns a list of all bundles installed in the OSGi
	 * environment at the time of the call to this method. However, since the
	 * Framework is a very dynamic environment, bundles can be installed or
	 * uninstalled at anytime.
	 * 
	 * @return An array of <code>Bundle</code> objects, one object per
	 *         installed bundle.
	 */
	public Collection<Bundle> getBundles();

	/**
	 * Adds the specified <code>BundleListener</code> object to the context
	 * bundle's list of listeners if not already present. BundleListener objects
	 * are notified when a bundle has a lifecycle state change.
	 * 
	 * <p>
	 * If the context bundle's list of listeners already contains a listener
	 * <code>l</code> such that <code>(l==listener)</code>, this method
	 * does nothing.
	 * 
	 * @param listener The <code>BundleListener</code> to be added.
	 * @throws java.lang.IllegalStateException If this BundleContext is no
	 *         longer valid.
	 * @throws java.lang.SecurityException If listener is a
	 *         <code>SynchronousBundleListener</code> and the caller does not
	 *         have the appropriate
	 *         <code>AdminPermission[context bundle,LISTENER]</code>, and the
	 *         Java Runtime Environment supports permissions.
	 * 
	 * @see BundleEvent
	 * @see BundleListener
	 */
	public void addBundleListener(BundleListener listener);

	/**
	 * Removes the specified <code>BundleListener</code> object from the
	 * context bundle's list of listeners.
	 * 
	 * <p>
	 * If <code>listener</code> is not contained in the context bundle's list
	 * of listeners, this method does nothing.
	 * 
	 * @param listener The <code>BundleListener</code> object to be removed.
	 * @throws java.lang.IllegalStateException If this BundleContext is no
	 *         longer valid.
	 * @throws java.lang.SecurityException If listener is a
	 *         <code>SynchronousBundleListener</code> and the caller does not
	 *         have the appropriate
	 *         <code>AdminPermission[context bundle,LISTENER]</code>, and the
	 *         Java Runtime Environment supports permissions.
	 */
	public void removeBundleListener(BundleListener listener);

	/**
	 * Adds the specified <code>FrameworkListener</code> object to the context
	 * bundle's list of listeners if not already present. FrameworkListeners are
	 * notified of general Framework events.
	 * 
	 * <p>
	 * If the context bundle's list of listeners already contains a listener
	 * <code>l</code> such that <code>(l==listener)</code>, this method
	 * does nothing.
	 * 
	 * @param listener The <code>FrameworkListener</code> object to be added.
	 * @throws java.lang.IllegalStateException If this BundleContext is no
	 *         longer valid.
	 * 
	 * @see FrameworkEvent
	 * @see FrameworkListener
	 */
	public void addFrameworkListener(FrameworkListener listener);

	/**
	 * Removes the specified <code>FrameworkListener</code> object from the
	 * context bundle's list of listeners.
	 * 
	 * <p>
	 * If <code>listener</code> is not contained in the context bundle's list
	 * of listeners, this method does nothing.
	 * 
	 * @param listener The <code>FrameworkListener</code> object to be
	 *        removed.
	 * @throws java.lang.IllegalStateException If this BundleContext is no
	 *         longer valid.
	 */
	public void removeFrameworkListener(FrameworkListener listener);
	
	/**
	 * Gets the exported packages for the specified package name.
	 * 
	 * @param name The name of the exported packages to be returned.
	 * 
	 * @return An array of the exported packages, or <code>null</code> if no
	 *         exported packages with the specified name exists.
	 * @since Package Admin 1.2
	 */
	public Collection<ExportedPackage> getExportedPackages(String name);

	/**
	 * Forces the update (replacement) or removal of packages exported by the
	 * specified bundles.
	 * 
	 * <p>
	 * If no bundles are specified, this method will update or remove any
	 * packages exported by any bundles that were previously updated or
	 * uninstalled since the last call to this method. The technique by which
	 * this is accomplished may vary among different Framework implementations.
	 * One permissible implementation is to stop and restart the Framework.
	 * 
	 * <p>
	 * This method returns to the caller immediately and then performs the
	 * following steps on a separate thread:
	 * 
	 * <ol>
	 * <li>Compute a graph of bundles starting with the specified bundles. If no
	 * bundles are specified, compute a graph of bundles starting with bundle
	 * updated or uninstalled since the last call to this method. Add to the
	 * graph any bundle that is wired to a package that is currently exported by
	 * a bundle in the graph. The graph is fully constructed when there is no
	 * bundle outside the graph that is wired to a bundle in the graph. The
	 * graph may contain <code>UNINSTALLED</code> bundles that are currently
	 * still exporting packages.
	 * 
	 * <li>Each bundle in the graph that is in the <code>ACTIVE</code> state
	 * will be stopped as described in the <code>Bundle.stop</code> method.
	 * 
	 * <li>Each bundle in the graph that is in the <code>RESOLVED</code> state
	 * is unresolved and thus moved to the <code>INSTALLED</code> state. The
	 * effect of this step is that bundles in the graph are no longer
	 * <code>RESOLVED</code>.
	 * 
	 * <li>Each bundle in the graph that is in the <code>UNINSTALLED</code>
	 * state is removed from the graph and is now completely removed from the
	 * Framework.
	 * 
	 * <li>Each bundle in the graph that was in the <code>ACTIVE</code> state
	 * prior to Step 2 is started as described in the <code>Bundle.start</code>
	 * method, causing all bundles required for the restart to be resolved. It
	 * is possible that, as a result of the previous steps, packages that were
	 * previously exported no longer are. Therefore, some bundles may be
	 * unresolvable until another bundle offering a compatible package for
	 * export has been installed in the Framework.
	 * <li>A framework event of type
	 * <code>FrameworkEvent.PACKAGES_REFRESHED</code> is fired.
	 * </ol>
	 * 
	 * <p>
	 * For any exceptions that are thrown during any of these steps, a
	 * <code>FrameworkEvent</code> of type <code>ERROR</code> is fired
	 * containing the exception. The source bundle for these events should be
	 * the specific bundle to which the exception is related. If no specific
	 * bundle can be associated with the exception then the System Bundle must
	 * be used as the source bundle for the event.
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
	 * @since Package Admin 1.0
	 */
	public void refreshPackages(Bundle... bundles);

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
	 * @return <code>true</code> if all specified bundles are resolved;
	 * @throws SecurityException If the caller does not have
	 *         <code>AdminPermission[System Bundle,RESOLVE]</code> and the Java
	 *         runtime environment supports permissions.
	 * @throws IllegalArgumentException If the specified <code>Bundle</code>s
	 *         were not created by the same framework instance that registered
	 *         this <code>PackageAdmin</code> service.
	 * @since Package Admin 1.2
	 */
	public boolean resolveBundles(Bundle... bundles);

	/**
	 * Returns the bundles with the specified symbolic name whose bundle version
	 * is within the specified version range. If no bundles are installed that
	 * have the specified symbolic name, then <code>null</code> is returned. If
	 * a version range is specified, then only the bundles that have the
	 * specified symbolic name and whose bundle versions belong to the specified
	 * version range are returned. The returned bundles are ordered by version
	 * in descending version order so that the first element of the array
	 * contains the bundle with the highest version.
	 * 
	 * @see org.osgi.framework.FrameworkConstants#BUNDLE_VERSION_ATTRIBUTE
	 * @param symbolicName The symbolic name of the desired bundles.
	 * @param versionRange The version range of the desired bundles, or
	 *        <code>null</code> if all versions are desired.
	 * @return An array of bundles with the specified name belonging to the
	 *         specified version range ordered in descending version order, or
	 *         <code>null</code> if no bundles are found.
	 * @since Package Admin 1.2
	 */
	public Collection<Bundle> getBundles(String symbolicName,
			String versionRange);

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
	 * @since Package Admin 1.2
	 */
	public Bundle getBundle(Class< ? > clazz);
}
