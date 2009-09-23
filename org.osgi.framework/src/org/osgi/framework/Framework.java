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

/**
 * 
 * TODO Add Javadoc comment for this type.
 * 
 * @since 1.6
 * @ThreadSafe
 * @version $Revision$
 */
public interface Framework {
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
	 */
	public String getProperty(String key);

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
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 */
	public Bundle installBundle(String location, InputStream input)
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
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see #installBundle(String, InputStream)
	 */
	public Bundle installBundle(String location) throws BundleException;

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
	public Bundle[] getBundles();

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
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 */
	public ServiceReference< ? >[] getServiceReferences(String clazz,
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
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @since 1.3
	 */
	public ServiceReference< ? >[] getAllServiceReferences(String clazz,
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
	 * {@link BundleContext#getServiceReferences(String, String)} with a
	 * <code>null</code> filter expression. It is provided as a convenience for
	 * when the caller is interested in any service that implements the
	 * specified class.
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
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see #getServiceReferences(String, String)
	 */
	public ServiceReference< ? > getServiceReference(String clazz);

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
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see "Framework specification for a description of the filter string syntax."
	 * @see FrameworkUtil#createFilter(String)
	 * @since 1.1
	 */
	public Filter createFilter(String filter) throws InvalidSyntaxException;

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
	 * {@link BundleContext#getServiceReferences(String, String)} with a
	 * <code>null</code> filter expression. It is provided as a convenience for
	 * when the caller is interested in any service that implements the
	 * specified class.
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
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see #getServiceReferences(String, String)
	 * @since 1.6
	 */
	public <S> ServiceReference<S> getServiceReference(Class<S> clazz);

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
	 * @param <S> Type of Service
	 * @param clazz The class name with which the service was registered. Must
	 *        not be <code>null</code>.
	 * @param filter The filter expression or <code>null</code> for all
	 *        services.
	 * @return A collection of <code>ServiceReference</code> objects. May be
	 *         empty if no services are registered which satisfy the search.
	 * @throws InvalidSyntaxException If the specified <code>filter</code>
	 *         contains an invalid filter expression that cannot be parsed.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @since 1.6
	 */
	public <S> Collection<ServiceReference<S>> getServiceReferences(
			Class<S> clazz, String filter) throws InvalidSyntaxException;

}
