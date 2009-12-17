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
import java.util.List;

/**
 * A bundle's execution context within the Framework. The context is used to
 * grant access to methods of the Framework in which the bundle is installed.
 * 
 * <p>
 * <code>FrameworkContext</code> methods allow a bundle to:
 * <ul>
 * <li>Retrieve <code>ServiceReferences</code> from the Framework service
 * registry.</li>
 * <li>Install new bundles in the Framework.</li>
 * <li>Get the list of bundles installed in the Framework.</li>
 * <li>Get framework environment properties.</li>
 * <li>Refresh Bundles.</li>
 * <li>Modify the framework start level.</li>
 * </ul>
 * 
 * <p>
 * A <code>FrameworkContext</code> object will be created and provided to the
 * bundle associated with this context when it is started using the
 * {@link BundleActivator#start} method. The same <code>FrameworkContext</code>
 * object will be passed to the bundle associated with this context when it is
 * stopped using the {@link BundleActivator#stop} method.
 * 
 * <p>
 * The <code>Bundle</code> object associated with a
 * <code>FrameworkContext</code> object is called the <em>context bundle</em>.
 * 
 * <p>
 * The <code>FrameworkContext</code> object is only valid during the execution
 * of its context bundle; that is, during the period from when the context
 * bundle is in the <code>STARTING</code>, <code>STOPPING</code>, and
 * <code>ACTIVE</code> bundle states. If the <code>FrameworkContext</code>
 * object is used subsequently, an <code>IllegalStateException</code> must be
 * thrown. The <code>FrameworkContext</code> object must never be reused after
 * its context bundle is stopped.
 * 
 * <p>
 * The Framework is the only entity that can create
 * <code>FrameworkContext</code> objects and they are only valid within the
 * Framework that created them.
 * 
 * @since 1.6
 * @ThreadSafe
 * @version $Revision$
 */
public interface FrameworkContext {
	/**
	 * Returns the value of the specified property. If the key is not found in
	 * the Framework properties, the system properties are then searched. The
	 * method returns <code>null</code> if the property is not found.
	 * 
	 * <p>
	 * All bundles must have permission to read properties whose names start
	 * with &quot;org.osgi.&quot;.
	 * 
	 * @param key The name of the requested property.
	 * @return The value of the requested property, or <code>null</code> if the
	 *         property is undefined.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>PropertyPermission</code> to read the property, and the
	 *         Java Runtime Environment supports permissions.
	 * @since 1.0
	 */
	String getProperty(String key);

	/**
	 * Installs a bundle from the specified <code>InputStream</code> object.
	 * 
	 * <p>
	 * If the specified <code>InputStream</code> is <code>null</code>, the
	 * Framework must create the <code>InputStream</code> from which to read the
	 * bundle by interpreting, in an implementation dependent manner, the
	 * specified <code>location</code>.
	 * 
	 * <p>
	 * The specified <code>location</code> identifier will be used as the
	 * identity of the bundle. Every installed bundle is uniquely identified by
	 * its location identifier which is typically in the form of a URL.
	 * 
	 * <p>
	 * The following steps are required to install a bundle:
	 * <ol>
	 * <li>If a bundle containing the same location identifier is already
	 * installed, the <code>Bundle</code> object for that bundle is returned.
	 * 
	 * <li>The bundle's content is read from the input stream. If this fails, a
	 * {@link BundleException} is thrown.
	 * 
	 * <li>The bundle's associated resources are allocated. The associated
	 * resources minimally consist of a unique identifier and a persistent
	 * storage area if the platform has file system support. If this step fails,
	 * a <code>BundleException</code> is thrown.
	 * 
	 * <li>The bundle's state is set to <code>INSTALLED</code>.
	 * 
	 * <li>A bundle event of type {@link BundleEvent#INSTALLED} is fired.
	 * 
	 * <li>The <code>Bundle</code> object for the newly or previously installed
	 * bundle is returned.
	 * </ol>
	 * 
	 * <b>Postconditions, no exceptions thrown </b>
	 * <ul>
	 * <li><code>getState()</code> in &#x007B; <code>INSTALLED</code>,
	 * <code>RESOLVED</code> &#x007D;.
	 * <li>Bundle has a unique ID.
	 * </ul>
	 * <b>Postconditions, when an exception is thrown </b>
	 * <ul>
	 * <li>Bundle is not installed and no trace of the bundle exists.
	 * </ul>
	 * 
	 * @param location The location identifier of the bundle to install.
	 * @param input The <code>InputStream</code> object from which this bundle
	 *        will be read or <code>null</code> to indicate the Framework must
	 *        create the input stream from the specified location identifier.
	 *        The input stream must always be closed when this method completes,
	 *        even if an exception is thrown.
	 * @return The <code>Bundle</code> object of the installed bundle.
	 * @throws BundleException If the input stream cannot be read or the
	 *         installation failed.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>AdminPermission[installed bundle,LIFECYCLE]</code>, and the
	 *         Java Runtime Environment supports permissions.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @since 1.0
	 */
	Bundle installBundle(String location, InputStream input)
			throws BundleException;

	/**
	 * Installs a bundle from the specified <code>location</code> identifier.
	 * 
	 * <p>
	 * This method performs the same function as calling
	 * {@link #installBundle(String,InputStream)} with the specified
	 * <code>location</code> identifier and a <code>null</code> InputStream.
	 * 
	 * @param location The location identifier of the bundle to install.
	 * @return The <code>Bundle</code> object of the installed bundle.
	 * @throws BundleException If the installation failed.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>AdminPermission[installed bundle,LIFECYCLE]</code>, and the
	 *         Java Runtime Environment supports permissions.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @see #installBundle(String, InputStream)
	 * @since 1.0
	 */
	Bundle installBundle(String location) throws BundleException;

	/**
	 * Returns the bundle with the specified identifier.
	 * 
	 * @param id The identifier of the bundle to retrieve.
	 * @return A <code>Bundle</code> object or <code>null</code> if the
	 *         identifier does not match any installed bundle.
	 * @since 1.0
	 */
	Bundle getBundle(long id);

	/**
	 * Returns a list of all installed bundles.
	 * <p>
	 * This method returns a list of all bundles installed in the OSGi
	 * environment at the time of the call to this method. However, since the
	 * Framework is a very dynamic environment, bundles can be installed or
	 * uninstalled at anytime.
	 * 
	 * @return An array of <code>Bundle</code> objects, one object per installed
	 *         bundle.
	 * @since 1.0
	 */
	Bundle[] getBundles();

	/**
	 * Returns an array of <code>ServiceReference</code> objects. The returned
	 * array of <code>ServiceReference</code> objects contains services that
	 * were registered under the specified class, match the specified filter
	 * expression, and the packages for the class names under which the services
	 * were registered match the context bundle's packages as defined in
	 * {@link ServiceReference#isAssignableTo(Bundle, String)}.
	 * 
	 * <p>
	 * The list is valid at the time of the call to this method. However since
	 * the Framework is a very dynamic environment, services can be modified or
	 * unregistered at any time.
	 * 
	 * <p>
	 * The specified <code>filter</code> expression is used to select the
	 * registered services whose service properties contain keys and values
	 * which satisfy the filter expression. See {@link Filter} for a description
	 * of the filter syntax. If the specified <code>filter</code> is
	 * <code>null</code>, all registered services are considered to match the
	 * filter. If the specified <code>filter</code> expression cannot be parsed,
	 * an {@link InvalidSyntaxException} will be thrown with a human readable
	 * message where the filter became unparsable.
	 * 
	 * <p>
	 * The result is an array of <code>ServiceReference</code> objects for all
	 * services that meet all of the following conditions:
	 * <ul>
	 * <li>If the specified class name, <code>clazz</code>, is not
	 * <code>null</code>, the service must have been registered with the
	 * specified class name. The complete list of class names with which a
	 * service was registered is available from the service's
	 * {@link Constants#OBJECTCLASS objectClass} property.
	 * <li>If the specified <code>filter</code> is not <code>null</code>, the
	 * filter expression must match the service.
	 * <li>If the Java Runtime Environment supports permissions, the caller must
	 * have <code>ServicePermission</code> with the <code>GET</code> action for
	 * at least one of the class names under which the service was registered.
	 * <li>For each class name with which the service was registered, calling
	 * {@link ServiceReference#isAssignableTo(Bundle, String)} with the context
	 * bundle and the class name on the service's <code>ServiceReference</code>
	 * object must return <code>true</code>
	 * </ul>
	 * 
	 * @param clazz The class name with which the service was registered or
	 *        <code>null</code> for all services.
	 * @param filter The filter expression or <code>null</code> for all
	 *        services.
	 * @return An array of <code>ServiceReference</code> objects or
	 *         <code>null</code> if no services are registered which satisfy the
	 *         search.
	 * @throws InvalidSyntaxException If the specified <code>filter</code>
	 *         contains an invalid filter expression that cannot be parsed.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @since 1.0
	 */
	ServiceReference< ? >[] getServiceReferences(String clazz,
			String filter)
			throws InvalidSyntaxException;

	/**
	 * Returns an array of <code>ServiceReference</code> objects. The returned
	 * array of <code>ServiceReference</code> objects contains services that
	 * were registered under the specified class and match the specified filter
	 * expression.
	 * 
	 * <p>
	 * The list is valid at the time of the call to this method. However since
	 * the Framework is a very dynamic environment, services can be modified or
	 * unregistered at any time.
	 * 
	 * <p>
	 * The specified <code>filter</code> expression is used to select the
	 * registered services whose service properties contain keys and values
	 * which satisfy the filter expression. See {@link Filter} for a description
	 * of the filter syntax. If the specified <code>filter</code> is
	 * <code>null</code>, all registered services are considered to match the
	 * filter. If the specified <code>filter</code> expression cannot be parsed,
	 * an {@link InvalidSyntaxException} will be thrown with a human readable
	 * message where the filter became unparsable.
	 * 
	 * <p>
	 * The result is an array of <code>ServiceReference</code> objects for all
	 * services that meet all of the following conditions:
	 * <ul>
	 * <li>If the specified class name, <code>clazz</code>, is not
	 * <code>null</code>, the service must have been registered with the
	 * specified class name. The complete list of class names with which a
	 * service was registered is available from the service's
	 * {@link Constants#OBJECTCLASS objectClass} property.
	 * <li>If the specified <code>filter</code> is not <code>null</code>, the
	 * filter expression must match the service.
	 * <li>If the Java Runtime Environment supports permissions, the caller must
	 * have <code>ServicePermission</code> with the <code>GET</code> action for
	 * at least one of the class names under which the service was registered.
	 * </ul>
	 * 
	 * @param clazz The class name with which the service was registered or
	 *        <code>null</code> for all services.
	 * @param filter The filter expression or <code>null</code> for all
	 *        services.
	 * @return An array of <code>ServiceReference</code> objects or
	 *         <code>null</code> if no services are registered which satisfy the
	 *         search.
	 * @throws InvalidSyntaxException If the specified <code>filter</code>
	 *         contains an invalid filter expression that cannot be parsed.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @since 1.3
	 */
	ServiceReference< ? >[] getAllServiceReferences(String clazz,
			String filter) throws InvalidSyntaxException;

	/**
	 * Returns a <code>ServiceReference</code> object for a service that
	 * implements and was registered under the specified class.
	 * 
	 * <p>
	 * The returned <code>ServiceReference</code> object is valid at the time of
	 * the call to this method. However as the Framework is a very dynamic
	 * environment, services can be modified or unregistered at any time.
	 * 
	 * <p>
	 * This method is the same as calling
	 * {@link #getServiceReferences(String, String)} with a <code>null</code>
	 * filter expression. It is provided as a convenience for when the caller is
	 * interested in any service that implements the specified class.
	 * <p>
	 * If multiple such services exist, the service with the highest ranking (as
	 * specified in its {@link Constants#SERVICE_RANKING} property) is returned.
	 * <p>
	 * If there is a tie in ranking, the service with the lowest service ID (as
	 * specified in its {@link Constants#SERVICE_ID} property); that is, the
	 * service that was registered first is returned.
	 * 
	 * @param clazz The class name with which the service was registered.
	 * @return A <code>ServiceReference</code> object, or <code>null</code> if
	 *         no services are registered which implement the named class.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @see #getServiceReferences(String, String)
	 * @since 1.0
	 */
	ServiceReference< ? > getServiceReference(String clazz);

	/**
	 * Creates a <code>Filter</code> object. This <code>Filter</code> object may
	 * be used to match a <code>ServiceReference</code> object or a
	 * <code>Dictionary</code> object.
	 * 
	 * <p>
	 * If the filter cannot be parsed, an {@link InvalidSyntaxException} will be
	 * thrown with a human readable message where the filter became unparsable.
	 * 
	 * @param filter The filter string.
	 * @return A <code>Filter</code> object encapsulating the filter string.
	 * @throws InvalidSyntaxException If <code>filter</code> contains an invalid
	 *         filter string that cannot be parsed.
	 * @throws NullPointerException If <code>filter</code> is null.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @see "Framework specification for a description of the filter string syntax."
	 * @see FrameworkUtil#createFilter(String)
	 * @since 1.1
	 */
	Filter createFilter(String filter) throws InvalidSyntaxException;

	/**
	 * Returns a <code>ServiceReference</code> object for a service that
	 * implements and was registered under the specified class.
	 * 
	 * <p>
	 * The returned <code>ServiceReference</code> object is valid at the time of
	 * the call to this method. However as the Framework is a very dynamic
	 * environment, services can be modified or unregistered at any time.
	 * 
	 * <p>
	 * This method is the same as calling
	 * {@link #getServiceReferences(String, String)} with a <code>null</code>
	 * filter expression. It is provided as a convenience for when the caller is
	 * interested in any service that implements the specified class.
	 * <p>
	 * If multiple such services exist, the service with the highest ranking (as
	 * specified in its {@link Constants#SERVICE_RANKING} property) is returned.
	 * <p>
	 * If there is a tie in ranking, the service with the lowest service ID (as
	 * specified in its {@link Constants#SERVICE_ID} property); that is, the
	 * service that was registered first is returned.
	 * 
	 * @param <S> Type of Service.
	 * @param clazz The class name with which the service was registered.
	 * @return A <code>ServiceReference</code> object, or <code>null</code> if
	 *         no services are registered which implement the named class.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @see #getServiceReferences(String, String)
	 * @since 1.6
	 */
	<S> ServiceReference<S> getServiceReference(Class<S> clazz);

	/**
	 * Returns a collection of <code>ServiceReference</code> objects. The returned
	 * collection of <code>ServiceReference</code> objects contains services that
	 * were registered under the specified class, match the specified filter
	 * expression, and the packages for the class names under which the services
	 * were registered match the context bundle's packages as defined in
	 * {@link ServiceReference#isAssignableTo(Bundle, String)}.
	 * 
	 * <p>
	 * The collection is valid at the time of the call to this method. However since
	 * the Framework is a very dynamic environment, services can be modified or
	 * unregistered at any time.
	 * 
	 * <p>
	 * The specified <code>filter</code> expression is used to select the
	 * registered services whose service properties contain keys and values
	 * which satisfy the filter expression. See {@link Filter} for a description
	 * of the filter syntax. If the specified <code>filter</code> is
	 * <code>null</code>, all registered services are considered to match the
	 * filter. If the specified <code>filter</code> expression cannot be parsed,
	 * an {@link InvalidSyntaxException} will be thrown with a human readable
	 * message where the filter became unparsable.
	 * 
	 * <p>
	 * The result is a collection of <code>ServiceReference</code> objects for all
	 * services that meet all of the following conditions:
	 * <ul>
	 * <li>If the specified class name, <code>clazz</code>, is not
	 * <code>null</code>, the service must have been registered with the
	 * specified class name. The complete list of class names with which a
	 * service was registered is available from the service's
	 * {@link Constants#OBJECTCLASS objectClass} property.
	 * <li>If the specified <code>filter</code> is not <code>null</code>, the
	 * filter expression must match the service.
	 * <li>If the Java Runtime Environment supports permissions, the caller must
	 * have <code>ServicePermission</code> with the <code>GET</code> action for
	 * at least one of the class names under which the service was registered.
	 * <li>For each class name with which the service was registered, calling
	 * {@link ServiceReference#isAssignableTo(Bundle, String)} with the context
	 * bundle and the class name on the service's <code>ServiceReference</code>
	 * object must return <code>true</code>
	 * </ul>
	 * 
	 * @param <S> Type of Service
	 * @param clazz The class name with which the service was registered. Must
	 *        not be <code>null</code>.
	 * @param filter The filter expression or <code>null</code> for all
	 *        services.
	 * @return A collection of <code>ServiceReference</code> objects. May be
	 *         empty if no services are registered which satisfy the search.
	 * @throws InvalidSyntaxException If the specified <code>filter</code>
	 *         contains an invalid filter expression that cannot be parsed.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @since 1.6
	 */
	<S> Collection<ServiceReference<S>> getServiceReferences(
			Class<S> clazz, String filter) throws InvalidSyntaxException;

	/**
	 * Gets the exported packages for the specified package name.
	 * 
	 * @param name The name of the exported packages to be returned or
	 *        <code>null</code> to return all exported packages.
	 * 
	 * @return A <code>Collection</code> of exported {@link Package}s, or an
	 *         empty collection if no exported packages with the specified name
	 *         exist.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @since 1.6
	 */
	Collection<Package> getExportedPackages(String name);

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
	 * <li>A framework event of type {@link FrameworkEvent#PACKAGES_REFRESHED}
	 * is fired.
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
	 *        removed. Specify no bundles to select all bundles updated or
	 *        uninstalled since the last call to this method.
	 * @throws SecurityException If the caller does not have
	 *         <code>AdminPermission[System Bundle,RESOLVE]</code> and the Java
	 *         runtime environment supports permissions.
	 * @throws IllegalArgumentException If the specified <code>Bundle</code>s
	 *         were not created by this framework.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @since 1.6
	 */
	void refreshBundles(Bundle... bundles);

	/**
	 * Resolve the specified bundles. The Framework must attempt to resolve the
	 * specified bundles that are unresolved. Additional bundles that are not
	 * included in the specified bundles may be resolved as a result of calling
	 * this method. A permissible implementation of this method is to attempt to
	 * resolve all unresolved bundles installed in the framework.
	 * 
	 * <p>
	 * If no bundles are specified then the Framework will attempt to resolve
	 * all unresolved bundles. This method must not cause any bundle to be
	 * refreshed, stopped, or started. This method will not return until the
	 * operation has completed.
	 * 
	 * @param bundles The bundles to resolve. Specify no bundles to resolve all
	 *        unresolved bundles installed in the Framework.
	 * @return <code>true</code> if all specified bundles are resolved;
	 * @throws SecurityException If the caller does not have
	 *         <code>AdminPermission[System Bundle,RESOLVE]</code> and the Java
	 *         runtime environment supports permissions.
	 * @throws IllegalArgumentException If the specified <code>Bundle</code>s
	 *         were not created by this framework.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @since 1.6
	 */
	boolean resolveBundles(Bundle... bundles);

	/**
	 * Returns the bundles with the specified symbolic name whose bundle version
	 * is within the specified version range. If no bundles are installed that
	 * have the specified symbolic name, then an empty list is returned. If a
	 * version range is specified, then only the bundles that have the specified
	 * symbolic name and whose bundle versions belong to the specified version
	 * range are returned. The returned bundles are ordered by version in
	 * descending version order so that the first bundle in the list is the
	 * bundle with the highest version.
	 * 
	 * @see Constants#BUNDLE_VERSION
	 * @param symbolicName The symbolic name of the desired bundles.
	 * @param versionRange The version range of the desired bundles, or
	 *        <code>null</code> if all versions are desired.
	 * @return A <code>List</code> of <code>Bundle</code>s with the specified
	 *         name belonging to the specified version range ordered in
	 *         descending version order, or an empty list if no bundles are
	 *         found.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @since 1.6
	 */
	List<Bundle> getBundles(String symbolicName, String versionRange);

	/**
	 * Return the active start level value of the Framework.
	 * 
	 * If the Framework is in the process of changing the start level this
	 * method must return the active start level if this differs from the
	 * requested start level.
	 * 
	 * @return The active start level value of the Framework.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @see #setStartLevel(int)
	 * @since 1.6
	 */
	public int getStartLevel();

	/**
	 * Modify the active start level of the Framework.
	 * 
	 * <p>
	 * The Framework will move to the requested start level. This method will
	 * return immediately to the caller and the start level change will occur
	 * asynchronously on another thread.
	 * 
	 * <p>
	 * If the specified start level is higher than the active start level, the
	 * Framework will continue to increase the start level until the Framework
	 * has reached the specified start level.
	 * 
	 * At each intermediate start level value on the way to and including the
	 * target start level, the Framework must:
	 * <ol>
	 * <li>Change the active start level to the intermediate start level value.
	 * <li>Start bundles at the intermediate start level whose autostart setting
	 * indicate they must be started. They are started as described in the
	 * {@link Bundle#start(int)} method using the {@link Bundle#START_TRANSIENT}
	 * option. The {@link Bundle#START_ACTIVATION_POLICY} option must also be
	 * used if {@link Bundle#isActivationPolicyUsed()} returns <code>true</code>
	 * for the bundle.
	 * </ol>
	 * When this process completes after the specified start level is reached,
	 * the Framework will fire a Framework event of type
	 * <code>FrameworkEvent.STARTLEVEL_CHANGED</code> to announce it has moved
	 * to the specified start level.
	 * 
	 * <p>
	 * If the specified start level is lower than the active start level, the
	 * Framework will continue to decrease the start level until the Framework
	 * has reached the specified start level.
	 * 
	 * At each intermediate start level value on the way to and including the
	 * specified start level, the framework must:
	 * <ol>
	 * <li>Stop bundles at the intermediate start level as described in the
	 * {@link Bundle#stop(int)} method using the {@link Bundle#STOP_TRANSIENT}
	 * option.
	 * <li>Change the active start level to the intermediate start level value.
	 * </ol>
	 * When this process completes after the specified start level is reached,
	 * the Framework will fire a Framework event of type
	 * <code>FrameworkEvent.STARTLEVEL_CHANGED</code> to announce it has moved
	 * to the specified start level.
	 * 
	 * <p>
	 * If the specified start level is equal to the active start level, then no
	 * bundles are started or stopped, however, the Framework must fire a
	 * Framework event of type <code>FrameworkEvent.STARTLEVEL_CHANGED</code> to
	 * announce it has finished moving to the specified start level. This event
	 * may arrive before this method return.
	 * 
	 * @param startlevel The requested start level for the Framework.
	 * @throws IllegalArgumentException If the specified start level is less
	 *         than or equal to zero.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @throws SecurityException If the caller does not have
	 *         <code>AdminPermission[System Bundle,STARTLEVEL]</code> and the
	 *         Java runtime environment supports permissions.
	 * @since 1.6
	 */
	void setStartLevel(int startlevel);

	/**
	 * Return the initial start level value that is assigned to a Bundle when it
	 * is first installed.
	 * 
	 * @return The initial start level value for Bundles.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @see #setInitialBundleStartLevel(int)
	 * @since 1.6
	 */
	int getInitialBundleStartLevel();

	/**
	 * Set the initial start level value that is assigned to a Bundle when it is
	 * first installed.
	 * 
	 * <p>
	 * The initial bundle start level will be set to the specified start level.
	 * The initial bundle start level value will be persistently recorded by the
	 * Framework.
	 * 
	 * <p>
	 * When a Bundle is installed via
	 * <code>FrameworkContext.installBundle</code>, it is assigned the initial
	 * bundle start level value.
	 * 
	 * <p>
	 * The default initial bundle start level value is 1 unless this method has
	 * been called to assign a different initial bundle start level value.
	 * 
	 * <p>
	 * This method does not change the start level values of installed bundles.
	 * 
	 * @param startlevel The initial start level for newly installed bundles.
	 * @throws IllegalArgumentException If the specified start level is less
	 *         than or equal to zero.
	 * @throws IllegalStateException If this FrameworkContext is no longer
	 *         valid.
	 * @throws SecurityException If the caller does not have
	 *         <code>AdminPermission[System Bundle,STARTLEVEL]</code> and the
	 *         Java runtime environment supports permissions.
	 * @since 1.6
	 */
	void setInitialBundleStartLevel(int startlevel);
}
