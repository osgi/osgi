/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.framework;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;

import org.osgi.annotation.versioning.ProviderType;

/**
 * A bundle's execution context within the Framework. The context is used to
 * grant access to other methods so that this bundle can interact with the
 * Framework.
 * 
 * <p>
 * {@code BundleContext} methods allow a bundle to:
 * <ul>
 * <li>Subscribe to events published by the Framework.</li>
 * <li>Register service objects with the Framework service registry.</li>
 * <li>Retrieve {@code ServiceReferences} from the Framework service registry.</li>
 * <li>Get and release service objects for a referenced service.</li>
 * <li>Install new bundles in the Framework.</li>
 * <li>Get the list of bundles installed in the Framework.</li>
 * <li>Get the {@link Bundle} object for a bundle.</li>
 * <li>Create {@code File} objects for files in a persistent storage area
 * provided for the bundle by the Framework.</li>
 * </ul>
 * 
 * <p>
 * A {@code BundleContext} object will be created for a bundle when the bundle
 * is started. The {@code Bundle} object associated with a {@code BundleContext}
 * object is called the <em>context bundle</em>.
 * 
 * <p>
 * The {@code BundleContext} object will be passed to the
 * {@link BundleActivator#start(BundleContext)} method during activation of the
 * context bundle. The same {@code BundleContext} object will be passed to the
 * {@link BundleActivator#stop(BundleContext)} method when the context bundle is
 * stopped. A {@code BundleContext} object is generally for the private use of
 * its associated bundle and is not meant to be shared with other bundles in the
 * OSGi environment.
 * 
 * <p>
 * The {@code BundleContext} object is only valid during the execution of its
 * context bundle; that is, during the period from when the context bundle is in
 * the {@code STARTING}, {@code STOPPING}, and {@code ACTIVE} bundle states.
 * However, the {@code BundleContext} object becomes invalid after
 * {@link BundleActivator#stop(BundleContext)} returns (if the bundle has a
 * Bundle Activator). The {@code BundleContext} object becomes invalid before
 * disposing of any remaining registered services and releasing any remaining
 * services in use. Since those activities can result in other bundles being
 * called (for example, {@link ServiceListener}s for
 * {@link ServiceEvent#UNREGISTERING} events and {@link ServiceFactory}s for
 * unget operations), those other bundles can observe the stopping bundle in the
 * {@code STOPPING} state but with an invalid {@code BundleContext} object. The
 * {@code BundleContext} object must never be reused after its context bundle is
 * stopped.
 * <p>
 * Two {@code BundleContext} objects are equal if they both refer to the same
 * execution context of a bundle. The Framework is the only entity that can
 * create {@code BundleContext} objects and they are only valid within the
 * Framework that created them.
 * 
 * <p>
 * A {@link Bundle} can be {@link Bundle#adapt(Class) adapted} to its
 * {@code BundleContext}. In order for this to succeed, the caller must have the
 * appropriate {@code AdminPermission[bundle,CONTEXT]} if the Java Runtime
 * Environment supports permissions.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface BundleContext extends BundleReference {

	/**
	 * Returns the value of the specified property. If the key is not found in
	 * the Framework properties, the system properties are then searched. The
	 * method returns {@code null} if the property is not found.
	 * 
	 * <p>
	 * All bundles must have permission to read properties whose names start
	 * with &quot;org.osgi.&quot;.
	 * 
	 * @param key The name of the requested property.
	 * @return The value of the requested property, or {@code null} if the
	 *         property is undefined.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         {@code PropertyPermission} to read the property, and the Java
	 *         Runtime Environment supports permissions.
	 */
	String getProperty(String key);

	/**
	 * Returns the {@code Bundle} object associated with this
	 * {@code BundleContext}. This bundle is called the context bundle.
	 * 
	 * @return The {@code Bundle} object associated with this
	 *         {@code BundleContext}.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 */
	@Override
	Bundle getBundle();

	/**
	 * Installs a bundle from the specified {@code InputStream} object.
	 * 
	 * <p>
	 * If the specified {@code InputStream} is {@code null}, the Framework must
	 * create the {@code InputStream} from which to read the bundle by
	 * interpreting, in an implementation dependent manner, the specified
	 * {@code location}.
	 * 
	 * <p>
	 * The specified {@code location} identifier will be used as the identity of
	 * the bundle. Every installed bundle is uniquely identified by its location
	 * identifier which is typically in the form of a URL.
	 * 
	 * <p>
	 * The following steps are required to install a bundle:
	 * <ol>
	 * <li>If a bundle containing the same location identifier is already
	 * installed, the {@code Bundle} object for that bundle is returned.</li>
	 * <li>The bundle's content is read from the input stream. If this fails, a
	 * {@link BundleException} is thrown.</li>
	 * <li>The bundle's associated resources are allocated. The associated
	 * resources minimally consist of a unique identifier and a persistent
	 * storage area if the platform has file system support. If this step fails,
	 * a {@code BundleException} is thrown.</li>
	 * <li>The bundle's state is set to {@code INSTALLED}.</li>
	 * <li>A bundle event of type {@link BundleEvent#INSTALLED} is fired.</li>
	 * <li>The {@code Bundle} object for the newly or previously installed
	 * bundle is returned.</li>
	 * </ol>
	 * 
	 * <b>Postconditions, no exceptions thrown </b>
	 * <ul>
	 * <li>{@code getState()} in &#x007B; {@code INSTALLED}, {@code RESOLVED}
	 * &#x007D;.</li>
	 * <li>Bundle has a unique ID.</li>
	 * </ul>
	 * <b>Postconditions, when an exception is thrown </b>
	 * <ul>
	 * <li>Bundle is not installed. If there was an existing bundle for the
	 * specified location, then that bundle must still be in the state it was
	 * prior to calling this method.</li>
	 * </ul>
	 * 
	 * @param location The location identifier of the bundle to install.
	 * @param input The {@code InputStream} object from which this bundle will
	 *        be read or {@code null} to indicate the Framework must create the
	 *        input stream from the specified location identifier. The input
	 *        stream must always be closed when this method completes, even if
	 *        an exception is thrown.
	 * @return The {@code Bundle} object of the installed bundle.
	 * @throws BundleException If the installation failed. BundleException types
	 *         thrown by this method include: {@link BundleException#READ_ERROR}
	 *         , {@link BundleException#DUPLICATE_BUNDLE_ERROR},
	 *         {@link BundleException#MANIFEST_ERROR}, and
	 *         {@link BundleException#REJECTED_BY_HOOK}.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         {@code AdminPermission[installed bundle,LIFECYCLE]}, and the Java
	 *         Runtime Environment supports permissions.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 */
	Bundle installBundle(String location, InputStream input) throws BundleException;

	/**
	 * Installs a bundle from the specified {@code location} identifier.
	 * 
	 * <p>
	 * This method performs the same function as calling
	 * {@link #installBundle(String,InputStream)} with the specified
	 * {@code location} identifier and a {@code null} InputStream.
	 * 
	 * @param location The location identifier of the bundle to install.
	 * @return The {@code Bundle} object of the installed bundle.
	 * @throws BundleException If the installation failed. BundleException types
	 *         thrown by this method include: {@link BundleException#READ_ERROR}
	 *         , {@link BundleException#DUPLICATE_BUNDLE_ERROR},
	 *         {@link BundleException#MANIFEST_ERROR}, and
	 *         {@link BundleException#REJECTED_BY_HOOK}.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         {@code AdminPermission[installed bundle,LIFECYCLE]}, and the Java
	 *         Runtime Environment supports permissions.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see #installBundle(String, InputStream)
	 */
	Bundle installBundle(String location) throws BundleException;

	/**
	 * Returns the bundle with the specified identifier.
	 * 
	 * @param id The identifier of the bundle to retrieve.
	 * @return A {@code Bundle} object or {@code null} if the identifier does
	 *         not match any installed bundle.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
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
	 * @return An array of {@code Bundle} objects, one object per installed
	 *         bundle.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 */
	Bundle[] getBundles();

	/**
	 * Adds the specified {@code ServiceListener} object with the specified
	 * {@code filter} to the context bundle's list of listeners. See
	 * {@link Filter} for a description of the filter syntax.
	 * {@code ServiceListener} objects are notified when a service has a
	 * lifecycle state change.
	 * 
	 * <p>
	 * If the context bundle's list of listeners already contains a listener
	 * {@code l} such that {@code (l==listener)}, then this method replaces that
	 * listener's filter (which may be {@code null}) with the specified one
	 * (which may be {@code null}).
	 * 
	 * <p>
	 * The listener is called if the filter criteria is met. To filter based
	 * upon the class of the service, the filter should reference the
	 * {@link Constants#OBJECTCLASS} property. If {@code filter} is {@code null}
	 * , all services are considered to match the filter.
	 * 
	 * <p>
	 * When using a {@code filter}, it is possible that the {@code ServiceEvent}
	 * s for the complete lifecycle of a service will not be delivered to the
	 * listener. For example, if the {@code filter} only matches when the
	 * property {@code x} has the value {@code 1}, the listener will not be
	 * called if the service is registered with the property {@code x} not set
	 * to the value {@code 1}. Subsequently, when the service is modified
	 * setting property {@code x} to the value {@code 1}, the filter will match
	 * and the listener will be called with a {@code ServiceEvent} of type
	 * {@code MODIFIED}. Thus, the listener will not be called with a
	 * {@code ServiceEvent} of type {@code REGISTERED}.
	 * 
	 * <p>
	 * If the Java Runtime Environment supports permissions, the
	 * {@code ServiceListener} object will be notified of a service event only
	 * if the bundle that is registering it has the {@code ServicePermission} to
	 * get the service using at least one of the named classes the service was
	 * registered under.
	 * 
	 * @param listener The {@code ServiceListener} object to be added.
	 * @param filter The filter criteria.
	 * @throws InvalidSyntaxException If {@code filter} contains an invalid
	 *         filter string that cannot be parsed.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see ServiceEvent
	 * @see ServiceListener
	 * @see ServicePermission
	 */
	void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException;

	/**
	 * Adds the specified {@code ServiceListener} object to the context bundle's
	 * list of listeners.
	 * 
	 * <p>
	 * This method is the same as calling
	 * {@code BundleContext.addServiceListener(ServiceListener listener,
	 * String filter)} with {@code filter} set to {@code null}.
	 * 
	 * @param listener The {@code ServiceListener} object to be added.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see #addServiceListener(ServiceListener, String)
	 */
	void addServiceListener(ServiceListener listener);

	/**
	 * Removes the specified {@code ServiceListener} object from the context
	 * bundle's list of listeners.
	 * 
	 * <p>
	 * If {@code listener} is not contained in this context bundle's list of
	 * listeners or this BundleContext is no longer valid, this method does
	 * nothing.
	 * 
	 * @param listener The {@code ServiceListener} to be removed.
	 */
	void removeServiceListener(ServiceListener listener);

	/**
	 * Adds the specified {@code BundleListener} object to the context bundle's
	 * list of listeners if not already present. BundleListener objects are
	 * notified when a bundle has a lifecycle state change.
	 * 
	 * <p>
	 * If the context bundle's list of listeners already contains a listener
	 * {@code l} such that {@code (l==listener)}, this method does nothing.
	 * 
	 * @param listener The {@code BundleListener} to be added.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @throws SecurityException If listener is a
	 *         {@code SynchronousBundleListener} and the caller does not have
	 *         the appropriate {@code AdminPermission[context bundle,LISTENER]},
	 *         and the Java Runtime Environment supports permissions.
	 * @see BundleEvent
	 * @see BundleListener
	 */
	void addBundleListener(BundleListener listener);

	/**
	 * Removes the specified {@code BundleListener} object from the context
	 * bundle's list of listeners.
	 * 
	 * <p>
	 * If {@code listener} is not contained in the context bundle's list of
	 * listeners or this BundleContext is no longer valid, this method does
	 * nothing.
	 * 
	 * @param listener The {@code BundleListener} object to be removed.
	 * @throws SecurityException If listener is a
	 *         {@code SynchronousBundleListener} and the caller does not have
	 *         the appropriate {@code AdminPermission[context bundle,LISTENER]},
	 *         and the Java Runtime Environment supports permissions.
	 */
	void removeBundleListener(BundleListener listener);

	/**
	 * Adds the specified {@code FrameworkListener} object to the context
	 * bundle's list of listeners if not already present. FrameworkListeners are
	 * notified of general Framework events.
	 * 
	 * <p>
	 * If the context bundle's list of listeners already contains a listener
	 * {@code l} such that {@code (l==listener)}, this method does nothing.
	 * 
	 * @param listener The {@code FrameworkListener} object to be added.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see FrameworkEvent
	 * @see FrameworkListener
	 */
	void addFrameworkListener(FrameworkListener listener);

	/**
	 * Removes the specified {@code FrameworkListener} object from the context
	 * bundle's list of listeners.
	 * 
	 * <p>
	 * If {@code listener} is not contained in the context bundle's list of
	 * listeners or this BundleContext is no longer valid, this method does
	 * nothing.
	 * 
	 * @param listener The {@code FrameworkListener} object to be removed.
	 */
	void removeFrameworkListener(FrameworkListener listener);

	/**
	 * Registers the specified service object with the specified properties
	 * under the specified class names into the Framework. A
	 * {@code ServiceRegistration} object is returned. The
	 * {@code ServiceRegistration} object is for the private use of the bundle
	 * registering the service and should not be shared with other bundles. The
	 * registering bundle is defined to be the context bundle. Other bundles can
	 * locate the service by using one of the
	 * {@link #getServiceReferences(Class, String)},
	 * {@link #getServiceReferences(String, String)},
	 * {@link #getServiceReference(Class)} or
	 * {@link #getServiceReference(String)} methods.
	 * 
	 * <p>
	 * A bundle can register a service object that implements the
	 * {@link ServiceFactory} interface to have more flexibility in providing
	 * service objects to other bundles.
	 * 
	 * <p>
	 * The following steps are required to register a service:
	 * <ol>
	 * <li>If {@code service} does not implement {@code ServiceFactory}, an
	 * {@code IllegalArgumentException} is thrown if {@code service} is not an
	 * {@code instanceof} all the specified class names.</li>
	 * <li>The Framework adds the following service properties to the service
	 * properties from the specified {@code Dictionary} (which may be
	 * {@code null}):
	 * <ul>
	 * <li>A property named {@link Constants#SERVICE_ID} identifying the
	 * registration number of the service</li>
	 * <li>A property named {@link Constants#OBJECTCLASS} containing all the
	 * specified classes.</li>
	 * <li>A property named {@link Constants#SERVICE_SCOPE} identifying the
	 * scope of the service.</li>
	 * <li>A property named {@link Constants#SERVICE_BUNDLEID} identifying the
	 * context bundle.</li>
	 * </ul>
	 * Properties with these names in the specified {@code Dictionary} will be
	 * ignored.</li>
	 * <li>The service is added to the Framework service registry and may now be
	 * used by other bundles.</li>
	 * <li>A service event of type {@link ServiceEvent#REGISTERED} is fired.</li>
	 * <li>A {@code ServiceRegistration} object for this registration is
	 * returned.</li>
	 * </ol>
	 * 
	 * @param clazzes The class names under which the service can be located.
	 *        The class names in this array will be stored in the service's
	 *        properties under the key {@link Constants#OBJECTCLASS}.
	 * @param service The service object or an object implementing
	 *        {@code ServiceFactory}.
	 * @param properties The properties for this service. The keys in the
	 *        properties object must all be {@code String} objects. See
	 *        {@link Constants} for a list of standard service property keys.
	 *        Changes should not be made to this object after calling this
	 *        method. To update the service's properties the
	 *        {@link ServiceRegistration#setProperties(Dictionary)} method must
	 *        be called. The set of properties may be {@code null} if the
	 *        service has no properties.
	 * @return A {@code ServiceRegistration} object for use by the bundle
	 *         registering the service to update the service's properties or to
	 *         unregister the service.
	 * @throws IllegalArgumentException If one of the following is true:
	 *         <ul>
	 *         <li>{@code service} is {@code null}.</li><li>{@code service} does
	 *         not implement {@code ServiceFactory} and is not an instance of
	 *         all the specified classes.</li><li> {@code properties} contains
	 *         case variants of the same key name.</li>
	 *         </ul>
	 * @throws SecurityException If the caller does not have the
	 *         {@code ServicePermission} to register the service for all the
	 *         named classes and the Java Runtime Environment supports
	 *         permissions.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see ServiceRegistration
	 * @see PrototypeServiceFactory
	 * @see ServiceFactory
	 */
	ServiceRegistration<?> registerService(String[] clazzes, Object service, Dictionary<String, ?> properties);

	/**
	 * Registers the specified service object with the specified properties
	 * under the specified class name with the Framework.
	 * 
	 * <p>
	 * This method is otherwise identical to
	 * {@link #registerService(String[], Object, Dictionary)} and is provided as
	 * a convenience when {@code service} will only be registered under a single
	 * class name. Note that even in this case the value of the service's
	 * {@link Constants#OBJECTCLASS} property will be an array of string, rather
	 * than just a single string.
	 * 
	 * @param clazz The class name under which the service can be located.
	 * @param service The service object or an object implementing
	 *        {@code ServiceFactory}.
	 * @param properties The properties for this service.
	 * @return A {@code ServiceRegistration} object for use by the bundle
	 *         registering the service to update the service's properties or to
	 *         unregister the service.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see #registerService(String[], Object, Dictionary)
	 */
	ServiceRegistration<?> registerService(String clazz, Object service, Dictionary<String, ?> properties);

	/**
	 * Registers the specified service object with the specified properties
	 * under the name of the specified class with the Framework.
	 * 
	 * <p>
	 * This method is otherwise identical to
	 * {@link #registerService(String, Object, Dictionary)} and is provided to
	 * return a type safe {@code ServiceRegistration}.
	 * 
	 * @param <S> Type of Service.
	 * @param clazz The class under whose name the service can be located.
	 * @param service The service object or an object implementing
	 *        {@code ServiceFactory}.
	 * @param properties The properties for this service.
	 * @return A {@code ServiceRegistration} object for use by the bundle
	 *         registering the service to update the service's properties or to
	 *         unregister the service.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see #registerService(String, Object, Dictionary)
	 * @since 1.6
	 */
	<S> ServiceRegistration<S> registerService(Class<S> clazz, S service, Dictionary<String, ?> properties);

	/**
	 * Registers the specified service factory object with the specified
	 * properties under the name of the specified class with the Framework.
	 * 
	 * <p>
	 * This method is otherwise identical to
	 * {@link #registerService(Class, Object, Dictionary)} and is provided to
	 * return a type safe {@code ServiceRegistration} when registering a
	 * {@link ServiceFactory}.
	 * 
	 * @param <S> Type of Service.
	 * @param clazz The class under whose name the service can be located.
	 * @param factory The {@code ServiceFactory} object.
	 * @param properties The properties for this service.
	 * @return A {@code ServiceRegistration} object for use by the bundle
	 *         registering the service to update the service's properties or to
	 *         unregister the service.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see #registerService(Class, Object, Dictionary)
	 * @since 1.8
	 */
	<S> ServiceRegistration<S> registerService(Class<S> clazz, ServiceFactory<S> factory, Dictionary<String, ?> properties);

	/**
	 * Returns an array of {@code ServiceReference} objects. The returned array
	 * of {@code ServiceReference} objects contains services that were
	 * registered under the specified class, match the specified filter
	 * expression, and the packages for the class names under which the services
	 * were registered match the context bundle's packages as defined in
	 * {@link ServiceReference#isAssignableTo(Bundle, String)}.
	 * <p>
	 * The list is valid at the time of the call to this method. However since
	 * the Framework is a very dynamic environment, services can be modified or
	 * unregistered at any time.
	 * <p>
	 * The specified {@code filter} expression is used to select the registered
	 * services whose service properties contain keys and values which satisfy
	 * the filter expression. See {@link Filter} for a description of the filter
	 * syntax. If the specified {@code filter} is {@code null}, all registered
	 * services are considered to match the filter. If the specified
	 * {@code filter} expression cannot be parsed, an
	 * {@link InvalidSyntaxException} will be thrown with a human readable
	 * message where the filter became unparsable.
	 * <p>
	 * The result is an array of {@code ServiceReference} objects for all
	 * services that meet all of the following conditions:
	 * <ul>
	 * <li>If the specified class name, {@code clazz}, is not {@code null}, the
	 * service must have been registered with the specified class name. The
	 * complete list of class names with which a service was registered is
	 * available from the service's {@link Constants#OBJECTCLASS objectClass}
	 * property.</li>
	 * <li>If the specified {@code filter} is not {@code null}, the filter
	 * expression must match the service.</li>
	 * <li>If the Java Runtime Environment supports permissions, the caller must
	 * have {@code ServicePermission} with the {@code GET} action for at least
	 * one of the class names under which the service was registered.</li>
	 * <li>For each class name with which the service was registered, calling
	 * {@link ServiceReference#isAssignableTo(Bundle, String)} with the context
	 * bundle and the class name on the service's {@code ServiceReference}
	 * object must return {@code true}</li>
	 * </ul>
	 * 
	 * @param clazz The class name with which the service was registered or
	 *            {@code null} for all services.
	 * @param filter The filter expression or {@code null} for all services.
	 * @return An array of {@code ServiceReference} objects or {@code null} if
	 *         no services are registered which satisfy the search. There is no
	 *         guarantee that the {@code ServiceReference} objects in the array
	 *         will be in any specific order.
	 * @throws InvalidSyntaxException If the specified {@code filter} contains
	 *             an invalid filter expression that cannot be parsed.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 */
	ServiceReference<?>[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException;

	/**
	 * Returns an array of {@code ServiceReference} objects. The returned array
	 * of {@code ServiceReference} objects contains services that were
	 * registered under the specified class and match the specified filter
	 * expression.
	 * <p>
	 * The list is valid at the time of the call to this method. However since
	 * the Framework is a very dynamic environment, services can be modified or
	 * unregistered at any time.
	 * <p>
	 * The specified {@code filter} expression is used to select the registered
	 * services whose service properties contain keys and values which satisfy
	 * the filter expression. See {@link Filter} for a description of the filter
	 * syntax. If the specified {@code filter} is {@code null}, all registered
	 * services are considered to match the filter. If the specified
	 * {@code filter} expression cannot be parsed, an
	 * {@link InvalidSyntaxException} will be thrown with a human readable
	 * message where the filter became unparsable.
	 * <p>
	 * The result is an array of {@code ServiceReference} objects for all
	 * services that meet all of the following conditions:
	 * <ul>
	 * <li>If the specified class name, {@code clazz}, is not {@code null}, the
	 * service must have been registered with the specified class name. The
	 * complete list of class names with which a service was registered is
	 * available from the service's {@link Constants#OBJECTCLASS objectClass}
	 * property.</li>
	 * <li>If the specified {@code filter} is not {@code null}, the filter
	 * expression must match the service.</li>
	 * <li>If the Java Runtime Environment supports permissions, the caller must
	 * have {@code ServicePermission} with the {@code GET} action for at least
	 * one of the class names under which the service was registered.</li>
	 * </ul>
	 * 
	 * @param clazz The class name with which the service was registered or
	 *            {@code null} for all services.
	 * @param filter The filter expression or {@code null} for all services.
	 * @return An array of {@code ServiceReference} objects or {@code null} if
	 *         no services are registered which satisfy the search. There is no
	 *         guarantee that the {@code ServiceReference} objects in the array
	 *         will be in any specific order.
	 * @throws InvalidSyntaxException If the specified {@code filter} contains
	 *             an invalid filter expression that cannot be parsed.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @since 1.3
	 */
	ServiceReference<?>[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException;

	/**
	 * Returns a {@code ServiceReference} object for a service that implements
	 * and was registered under the specified class.
	 * <p>
	 * The returned {@code ServiceReference} object is valid at the time of the
	 * call to this method. However as the Framework is a very dynamic
	 * environment, services can be modified or unregistered at any time.
	 * <p>
	 * This method is the same as calling
	 * {@link #getServiceReferences(String, String)} with a {@code null} filter
	 * expression and then finding the reference with the highest priority. It
	 * is provided as a convenience for when the caller is interested in any
	 * service that implements the specified class.
	 * <p>
	 * If multiple such services exist, the service that is first in the
	 * {@link ServiceReference#compareTo(Object) ranking order} is returned.
	 * 
	 * @param clazz The class name with which the service was registered.
	 * @return A {@code ServiceReference} object, or {@code null} if no services
	 *         are registered which implement the named class.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see #getServiceReferences(String, String)
	 */
	ServiceReference<?> getServiceReference(String clazz);

	/**
	 * Returns a {@code ServiceReference} object for a service that implements
	 * and was registered under the name of the specified class.
	 * <p>
	 * The returned {@code ServiceReference} object is valid at the time of the
	 * call to this method. However as the Framework is a very dynamic
	 * environment, services can be modified or unregistered at any time.
	 * <p>
	 * This method is the same as calling
	 * {@link #getServiceReferences(Class, String)} with a {@code null} filter
	 * expression. It is provided as a convenience for when the caller is
	 * interested in any service that implements the specified class.
	 * <p>
	 * If multiple such services exist, the service that is first in the
	 * {@link ServiceReference#compareTo(Object) ranking order} is returned.
	 * 
	 * @param <S> Type of Service.
	 * @param clazz The class under whose name the service was registered. Must
	 *            not be {@code null}.
	 * @return A {@code ServiceReference} object, or {@code null} if no services
	 *         are registered which implement the specified class.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see #getServiceReferences(Class, String)
	 * @since 1.6
	 */
	<S> ServiceReference<S> getServiceReference(Class<S> clazz);

	/**
	 * Returns a collection of {@code ServiceReference} objects. The returned
	 * collection of {@code ServiceReference} objects contains services that
	 * were registered under the name of the specified class, match the
	 * specified filter expression, and the packages for the class names under
	 * which the services were registered match the context bundle's packages as
	 * defined in {@link ServiceReference#isAssignableTo(Bundle, String)}.
	 * <p>
	 * The collection is valid at the time of the call to this method. However
	 * since the Framework is a very dynamic environment, services can be
	 * modified or unregistered at any time.
	 * <p>
	 * The specified {@code filter} expression is used to select the registered
	 * services whose service properties contain keys and values which satisfy
	 * the filter expression. See {@link Filter} for a description of the filter
	 * syntax. If the specified {@code filter} is {@code null}, all registered
	 * services are considered to match the filter. If the specified
	 * {@code filter} expression cannot be parsed, an
	 * {@link InvalidSyntaxException} will be thrown with a human readable
	 * message where the filter became unparsable.
	 * <p>
	 * The result is a collection of {@code ServiceReference} objects for all
	 * services that meet all of the following conditions:
	 * <ul>
	 * <li>The service must have been registered with the name of the specified
	 * class. The complete list of class names with which a service was
	 * registered is available from the service's {@link Constants#OBJECTCLASS
	 * objectClass} property.</li>
	 * <li>If the specified {@code filter} is not {@code null}, the filter
	 * expression must match the service.</li>
	 * <li>If the Java Runtime Environment supports permissions, the caller must
	 * have {@code ServicePermission} with the {@code GET} action for at least
	 * one of the class names under which the service was registered.</li>
	 * <li>For each class name with which the service was registered, calling
	 * {@link ServiceReference#isAssignableTo(Bundle, String)} with the context
	 * bundle and the class name on the service's {@code ServiceReference}
	 * object must return {@code true}</li>
	 * </ul>
	 * 
	 * @param <S> Type of Service
	 * @param clazz The class under whose name the service was registered. Must
	 *            not be {@code null}.
	 * @param filter The filter expression or {@code null} for all services.
	 * @return A collection of {@code ServiceReference} objects. May be empty if
	 *         no services are registered which satisfy the search. There is no
	 *         guarantee that the {@code ServiceReference} objects in the
	 *         collection will be in any specific order.
	 * @throws InvalidSyntaxException If the specified {@code filter} contains
	 *             an invalid filter expression that cannot be parsed.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @since 1.6
	 */
	<S> Collection<ServiceReference<S>> getServiceReferences(Class<S> clazz, String filter) throws InvalidSyntaxException;

	/**
	 * Returns the service object for the service referenced by the specified
	 * {@code ServiceReference} object.
	 * 
	 * <p>
	 * A bundle's use of a service object obtained from this method is tracked
	 * by the bundle's use count of that service. Each time the service object
	 * is returned by {@link #getService(ServiceReference)} the context bundle's
	 * use count for the service is incremented by one. Each time the service
	 * object is released by {@link #ungetService(ServiceReference)} the context
	 * bundle's use count for the service is decremented by one.
	 * 
	 * <p>
	 * When a bundle's use count for the service drops to zero, the bundle
	 * should no longer use the service object.
	 * 
	 * <p>
	 * This method will always return {@code null} when the service associated
	 * with the specified {@code reference} has been unregistered.
	 * 
	 * <p>
	 * The following steps are required to get the service object:
	 * <ol>
	 * <li>If the service has been unregistered, {@code null} is returned.</li>
	 * <li>If the context bundle's use count for the service is currently zero
	 * and the service has {@link Constants#SCOPE_BUNDLE bundle} or
	 * {@link Constants#SCOPE_PROTOTYPE prototype} scope, the
	 * {@link ServiceFactory#getService(Bundle, ServiceRegistration)} method is
	 * called to supply the service object for the context bundle. If the
	 * service object returned by the {@code ServiceFactory} object is
	 * {@code null}, not an {@code instanceof} all the classes named when the
	 * service was registered or the {@code ServiceFactory} object throws an
	 * exception or will be recursively called for the context bundle,
	 * {@code null} is returned and a Framework event of type
	 * {@link FrameworkEvent#ERROR} containing a {@link ServiceException}
	 * describing the error is fired. The supplied service object is cached by
	 * the Framework. While the context bundle's use count for the service is
	 * greater than zero, subsequent calls to get the service object for the
	 * context bundle will return the cached service object.</li>
	 * <li>The context bundle's use count for the service is incremented by one.
	 * </li>
	 * <li>The service object for the service is returned.</li>
	 * </ol>
	 * 
	 * @param <S> Type of Service.
	 * @param reference A reference to the service.
	 * @return A service object for the service associated with
	 *         {@code reference} or {@code null} if the service is not
	 *         registered, the service object returned by a
	 *         {@code ServiceFactory} does not implement the classes under which
	 *         it was registered or the {@code ServiceFactory} threw an
	 *         exception.
	 * @throws SecurityException If the caller does not have the
	 *         {@code ServicePermission} to get the service using at least one
	 *         of the named classes the service was registered under and the
	 *         Java Runtime Environment supports permissions.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @throws IllegalArgumentException If the specified
	 *         {@code ServiceReference} was not created by the same framework
	 *         instance as this {@code BundleContext}.
	 * @see #ungetService(ServiceReference)
	 * @see ServiceFactory
	 */
	<S> S getService(ServiceReference<S> reference);

	/**
	 * Releases the service object for the service referenced by the specified
	 * {@code ServiceReference} object. If the context bundle's use count for
	 * the service is zero, this method returns {@code false}. Otherwise, the
	 * context bundle's use count for the service is decremented by one.
	 * 
	 * <p>
	 * The service object must no longer be used and all references to it should
	 * be destroyed when a bundle's use count for the service drops to zero.
	 * 
	 * <p>
	 * The following steps are required to release the service object:
	 * <ol>
	 * <li>If this BundleContext is no longer valid, {@code false} is
	 * returned.</li>
	 * <li>If the context bundle's use count for the service is zero or the
	 * service has been unregistered, {@code false} is returned.</li>
	 * <li>The context bundle's use count for the service is decremented by one.
	 * </li>
	 * <li>If the context bundle's use count for the service is now zero and the
	 * service has {@link Constants#SCOPE_BUNDLE bundle} or
	 * {@link Constants#SCOPE_PROTOTYPE prototype} scope, the
	 * {@link ServiceFactory#ungetService(Bundle, ServiceRegistration, Object)}
	 * method is called to release the service object for the context bundle.</li>
	 * <li>{@code true} is returned.</li>
	 * </ol>
	 * 
	 * @param reference A reference to the service to be released.
	 * @return {@code false} if the context bundle's use count for the service
	 *         is zero or if the service has been unregistered; {@code true}
	 *         otherwise.
	 * @throws IllegalArgumentException If the specified
	 *         {@code ServiceReference} was not created by the same framework
	 *         instance as this {@code BundleContext}.
	 * @see #getService(ServiceReference)
	 * @see ServiceFactory
	 */
	boolean ungetService(ServiceReference<?> reference);

	/**
	 * Returns the {@link ServiceObjects} object for the service referenced by
	 * the specified {@code ServiceReference} object.
	 * 
	 * <p>
	 * The {@link ServiceObjects} object can be used to obtain multiple service
	 * objects for services with {@link Constants#SCOPE_PROTOTYPE prototype}
	 * scope.
	 * 
	 * <p>
	 * For services with {@link Constants#SCOPE_SINGLETON singleton} or
	 * {@link Constants#SCOPE_BUNDLE bundle} scope, the
	 * {@link ServiceObjects#getService()} method behaves the same as the
	 * {@link #getService(ServiceReference)} method and the
	 * {@link ServiceObjects#ungetService(Object)} method behaves the same as
	 * the {@link #ungetService(ServiceReference)} method. That is, only one,
	 * use-counted service object is available from the {@link ServiceObjects}
	 * object.
	 * 
	 * <p>
	 * This method will always return {@code null} when the service associated
	 * with the specified {@code reference} has been unregistered.
	 * 
	 * @param <S> Type of Service.
	 * @param reference A reference to the service.
	 * @return A {@link ServiceObjects} object for the service associated with
	 *         the specified {@code reference} or {@code null} if the service is
	 *         not registered.
	 * @throws SecurityException If the caller does not have the
	 *         {@code ServicePermission} to get the service using at least one
	 *         of the named classes the service was registered under and the
	 *         Java Runtime Environment supports permissions.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @throws IllegalArgumentException If the specified
	 *         {@code ServiceReference} was not created by the same framework
	 *         instance as this {@code BundleContext}.
	 * @see PrototypeServiceFactory
	 * @since 1.8
	 */
	<S> ServiceObjects<S> getServiceObjects(ServiceReference<S> reference);

	/**
	 * Creates a {@code File} object for a file in the persistent storage area
	 * provided for the bundle by the Framework. This method will return
	 * {@code null} if the platform does not have file system support.
	 * 
	 * <p>
	 * A {@code File} object for the base directory of the persistent storage
	 * area provided for the context bundle by the Framework can be obtained by
	 * calling this method with an empty string as {@code filename}.
	 * 
	 * <p>
	 * If the Java Runtime Environment supports permissions, the Framework will
	 * ensure that the bundle has the {@code java.io.FilePermission} with
	 * actions {@code read},{@code write},{@code delete} for all files
	 * (recursively) in the persistent storage area provided for the context
	 * bundle.
	 * 
	 * @param filename A relative name to the file to be accessed.
	 * @return A {@code File} object that represents the requested file or
	 *         {@code null} if the platform does not have file system support.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 */
	File getDataFile(String filename);

	/**
	 * Creates a {@code Filter} object. This {@code Filter} object may be used
	 * to match a {@code ServiceReference} object or a {@code Dictionary}
	 * object.
	 * 
	 * <p>
	 * If the filter cannot be parsed, an {@link InvalidSyntaxException} will be
	 * thrown with a human readable message where the filter became unparsable.
	 * 
	 * @param filter The filter string.
	 * @return A {@code Filter} object encapsulating the filter string.
	 * @throws InvalidSyntaxException If {@code filter} contains an invalid
	 *         filter string that cannot be parsed.
	 * @throws NullPointerException If {@code filter} is null.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @see "Framework specification for a description of the filter string syntax."
	 * @see FrameworkUtil#createFilter(String)
	 * @since 1.1
	 */
	Filter createFilter(String filter) throws InvalidSyntaxException;

	/**
	 * Returns the bundle with the specified location.
	 * 
	 * @param location The location of the bundle to retrieve.
	 * @return A {@code Bundle} object or {@code null} if the location does not
	 *         match any installed bundle.
	 * @throws IllegalStateException If this BundleContext is no longer valid.
	 * @since 1.6
	 */
	Bundle getBundle(String location);
}
