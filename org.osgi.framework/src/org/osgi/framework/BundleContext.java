/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.framework;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;

/**
 * A bundle's execution context within the Framework. The context is used to
 * grant access to other methods so that this bundle can interact with the
 * Framework.
 * 
 * <p>
 * <code>BundleContext</code> methods allow a bundle to:
 * <ul>
 * <li>Subscribe to events published by the Framework.
 * <li>Register service objects with the Framework service registry.
 * <li>Retrieve <code>ServiceReferences</code> from the Framework service
 * registry.
 * <li>Get and release service objects for a referenced service.
 * <li>Install new bundles in the Framework.
 * <li>Get the list of bundles installed in the Framework.
 * <li>Get the {@link Bundle}object for a bundle.
 * <li>Create <code>File</code> objects for files in a persistent storage area
 * provided for the bundle by the Framework.
 * </ul>
 * 
 * <p>
 * A <code>BundleContext</code> object will be created and provided to this bundle
 * when it is started using the {@link BundleActivator#start}method. The same
 * <code>BundleContext</code> object will be passed to this bundle when it is
 * stopped using the {@link BundleActivator#stop}method. <code>BundleContext</code>
 * is generally for the private use of this bundle and is not meant to be shared
 * with other bundles in the OSGi environment. <code>BundleContext</code> is used
 * when resolving <code>ServiceListener</code> and <code>EventListener</code>
 * objects.
 * 
 * <p>
 * The <code>BundleContext</code> object is only valid during an execution
 * instance of this bundle; that is, during the period from when this bundle is
 * called by <code>BundleActivator.start</code> until after this bundle is called
 * and returns from <code>BundleActivator.stop</code> (or if
 * <code>BundleActivator.start</code> terminates with an exception). If the
 * <code>BundleContext</code> object is used subsequently, an
 * <code>IllegalStateException</code> must be thrown. When this bundle is
 * restarted, a new <code>BundleContext</code> object must be created.
 * 
 * <p>
 * The Framework is the only entity that can create <code>BundleContext</code>
 * objects and they are only valid within the Framework that created them.
 * 
 * @version $Revision$
 */

public abstract interface BundleContext {
	/**
	 * Returns the value of the specified property. If the key is not found in
	 * the Framework properties, the system properties are then searched. The
	 * method returns <code>null</code> if the property is not found.
	 * 
	 * <p>
	 * The Framework defines the following standard property keys:
	 * </p>
	 * <ul>
	 * <li>{@link Constants#FRAMEWORK_VERSION}- The OSGi Framework version.
	 * </li>
	 * <li>{@link Constants#FRAMEWORK_VENDOR}- The Framework implementation
	 * vendor.</li>
	 * <li>{@link Constants#FRAMEWORK_LANGUAGE}- The language being used. See
	 * ISO 639 for possible values.</li>
	 * <li>{@link Constants#FRAMEWORK_OS_NAME}- The host computer operating
	 * system.</li>
	 * <li>{@link Constants#FRAMEWORK_OS_VERSION}- The host computer operating
	 * system version number.</li>
	 * <li>{@link Constants#FRAMEWORK_PROCESSOR}- The host computer processor
	 * name.</li>
	 * </ul>
	 * <p>
	 * All bundles must have permission to read these properties.
	 * 
	 * <p>
	 * Note: The last four standard properties are used by the
	 * {@link Constants#BUNDLE_NATIVECODE}<code>Manifest</code> header's matching
	 * algorithm for selecting native language code.
	 * 
	 * @param key The name of the requested property.
	 * @return The value of the requested property, or <code>null</code> if the
	 *         property is undefined.
	 * @exception java.lang.SecurityException If the caller does not have the
	 *            appropriate <code>PropertyPermission</code> to read the
	 *            property, and the Java Runtime Environment supports
	 *            permissions.
	 */
	public abstract String getProperty(String key);

	/**
	 * Returns the <code>Bundle</code> object for this context bundle.
	 * 
	 * <p>
	 * The context bundle is defined as the bundle that was assigned this
	 * <code>BundleContext</code> in its <code>BundleActivator</code>.
	 * 
	 * @return The context bundle's <code>Bundle</code> object.
	 * @exception java.lang.IllegalStateException If this context bundle has
	 *            stopped.
	 */
	public abstract Bundle getBundle();

	/**
	 * Installs the bundle from the specified location string. A bundle is
	 * obtained from <code>location</code> as interpreted by the Framework in an
	 * implementation dependent manner.
	 * <p>
	 * Every installed bundle is uniquely identified by its location string,
	 * typically in the form of a URL.
	 * 
	 * <p>
	 * The following steps are required to install a bundle:
	 * <ol>
	 * <li>If a bundle containing the same location string is already
	 * installed, the <code>Bundle</code> object for that bundle is returned.
	 * 
	 * <li>The bundle's content is read from the location string. If this
	 * fails, a {@link BundleException}is thrown.
	 * 
	 * <li>The bundle's <code>Bundle-NativeCode</code> dependencies are resolved.
	 * If this fails, a <code>BundleException</code> is thrown.
	 * 
	 * <li>The bundle's associated resources are allocated. The associated
	 * resources minimally consist of a unique identifier, and a persistent
	 * storage area if the platform has file system support. If this step fails,
	 * a <code>BundleException</code> is thrown.
	 * 
	 * <li>If the bundle has declared an Bundle-RequiredExecutionEnvironment
	 * header, then the listed execution environments must be verified against
	 * the installed execution environments. If they are not all present, a
	 * <code>BundleException</code> must be thrown.
	 * 
	 * <li>The bundle's state is set to <code>INSTALLED</code>.
	 * 
	 * <li>A bundle event of type {@link BundleEvent#INSTALLED}is broadcast.
	 * 
	 * <li>The <code>Bundle</code> object for the newly installed bundle is
	 * returned.
	 * </ol>
	 * 
	 * <b>Postconditions, no exceptions thrown </b>
	 * <ul>
	 * <li><code>getState()</code> in {<code>INSTALLED</code>},<code>RESOLVED</code>}.
	 * <li>Bundle has a unique ID.
	 * </ul>
	 * <b>Postconditions, when an exception is thrown </b>
	 * <ul>
	 * <li>Bundle is not installed and no trace of the bundle exists.
	 * </ul>
	 * 
	 * @param location The location identifier of the bundle to install.
	 * @return The <code>Bundle</code> object of the installed bundle.
	 * @exception BundleException If the installation failed.
	 * @exception java.lang.SecurityException If the caller does not have the
	 *            appropriate <code>AdminPermission</code>, and the Java Runtime
	 *            Environment supports permissions.
	 */
	public abstract Bundle installBundle(String location)
			throws BundleException;

	/**
	 * Installs the bundle from the specified <code>InputStream</code> object.
	 * 
	 * <p>
	 * This method performs all of the steps listed in
	 * <code>BundleContext.installBundle(String location)</code>, except that the
	 * bundle's content will be read from the <code>InputStream</code> object. The
	 * location identifier string specified will be used as the identity of the
	 * bundle.
	 * 
	 * <p>
	 * This method must always close the <code>InputStream</code> object, even if
	 * an exception is thrown.
	 * 
	 * @param location The location identifier of the bundle to install.
	 * @param input The <code>InputStream</code> object from which this bundle
	 *        will be read.
	 * @return The <code>Bundle</code> object of the installed bundle.
	 * @exception BundleException If the provided stream cannot be read or the
	 *            installation failed.
	 * @exception java.lang.SecurityException If the caller does not have the
	 *            appropriate <code>AdminPermission</code>, and the Java Runtime
	 *            Environment supports permissions.
	 * @see #installBundle(java.lang.String)
	 */
	public abstract Bundle installBundle(String location, InputStream input)
			throws BundleException;

	/**
	 * Returns the bundle with the specified identifier.
	 * 
	 * @param id The identifier of the bundle to retrieve.
	 * @return A <code>Bundle</code> object, or <code>null</code> if the identifier
	 *         does not match any installed bundle.
	 */
	public abstract Bundle getBundle(long id);

	/**
	 * Returns a list of all installed bundles.
	 * <p>
	 * This method returns a list of all bundles installed in the OSGi
	 * environment at the time of the call to this method. However, as the
	 * Framework is a very dynamic environment, bundles can be installed or
	 * uninstalled at anytime.
	 * 
	 * @return An array of <code>Bundle</code> objects; one object per installed
	 *         bundle.
	 */
	public abstract Bundle[] getBundles();

	/**
	 * Adds the specified <code>ServiceListener</code> object with the specified
	 * <code>filter</code> to this context bundle's list of listeners.
	 * <p>
	 * See {@link #getBundle()}for a definition of context bundle, and
	 * {@link Filter}for a description of the filter syntax.
	 * <code>ServiceListener</code> objects are notified when a service has a
	 * lifecycle state change.
	 * 
	 * <p>
	 * If this context bundle's list of listeners already contains a listener
	 * <code>l</code> such that <code>(l==listener)</code>, this method replaces
	 * that listener's filter (which may be <code>null</code>) with the specified
	 * one (which may be <code>null</code>).
	 * 
	 * <p>
	 * The listener is called if the filter criteria is met. To filter based
	 * upon the class of the service, the filter should reference the
	 * {@link Constants#OBJECTCLASS}property. If <code>filter</code> is
	 * <code>null</code>, all services are considered to match the filter.
	 * 
	 * <p>
	 * When using a <code>filter</code>, it is possible that the
	 * <code>ServiceEvent</code> s for the complete life cycle of a service will
	 * not be delivered to the listener. For example, if the <code>filter</code>
	 * only matches when the property <code>x</code> has the value <code>1</code>,
	 * the listener will not be called if the service is registered with the
	 * property <code>x</code> not set to the value <code>1</code>. Subsequently,
	 * when the service is modified setting property <code>x</code> to the value
	 * <code>1</code>, the filter will match and the listener will be called with
	 * a <code>ServiceEvent</code> of type <code>MODIFIED</code>. Thus, the
	 * listener will not be called with a <code>ServiceEvent</code> of type
	 * <code>REGISTERED</code>.
	 * 
	 * <p>
	 * If the Java Runtime Environment supports permissions, the
	 * <code>ServiceListener</code> object will be notified of a service event
	 * only if the bundle that is registering it has the
	 * <code>ServicePermission</code> to get the service using at least one of the
	 * named classes the service was registered under.
	 * 
	 * @param listener The <code>ServiceListener</code> object to be added.
	 * @param filter The filter criteria.
	 * 
	 * @exception InvalidSyntaxException If <code>filter</code> contains an
	 *            invalid filter string which cannot be parsed.
	 * @exception java.lang.IllegalStateException If this context bundle has
	 *            stopped.
	 * 
	 * @see ServiceEvent
	 * @see ServiceListener
	 * @see ServicePermission
	 */
	public abstract void addServiceListener(ServiceListener listener,
			String filter) throws InvalidSyntaxException;

	/**
	 * Adds the specified <code>ServiceListener</code> object to this context
	 * bundle's list of listeners.
	 * 
	 * <p>
	 * This method is the same as calling
	 * <code>BundleContext.addServiceListener(ServiceListener listener,
	 * String filter)</code>
	 * with <code>filter</code> set to <code>null</code>.
	 * 
	 * @param listener The <code>ServiceListener</code> object to be added.
	 * @exception java.lang.IllegalStateException If this context bundle has
	 *            stopped.
	 * 
	 * @see #addServiceListener(ServiceListener, String)
	 */
	public abstract void addServiceListener(ServiceListener listener);

	/**
	 * Removes the specified <code>ServiceListener</code> object from this context
	 * bundle's list of listeners. See {@link #getBundle()}for a definition of
	 * context bundle.
	 * 
	 * <p>
	 * If <code>listener</code> is not contained in this context bundle's list of
	 * listeners, this method does nothing.
	 * 
	 * @param listener The <code>ServiceListener</code> to be removed.
	 * @exception java.lang.IllegalStateException If this context bundle has
	 *            stopped.
	 */
	public abstract void removeServiceListener(ServiceListener listener);

	/**
	 * Adds the specified <code>BundleListener</code> object to this context
	 * bundle's list of listeners if not already present. See
	 * {@link #getBundle()}for a definition of context bundle. BundleListener
	 * objects are notified when a bundle has a lifecycle state change.
	 * 
	 * <p>
	 * If this context bundle's list of listeners already contains a listener
	 * <code>l</code> such that <code>(l==listener)</code>, this method does
	 * nothing.
	 * 
	 * @param listener The <code>BundleListener</code> to be added.
	 * @exception java.lang.IllegalStateException If this context bundle has
	 *            stopped.
	 * 
	 * @see BundleEvent
	 * @see BundleListener
	 */
	public abstract void addBundleListener(BundleListener listener);

	/**
	 * Removes the specified <code>BundleListener</code> object from this context
	 * bundle's list of listeners. See {@link #getBundle()}for a definition of
	 * context bundle.
	 * 
	 * <p>
	 * If <code>listener</code> is not contained in this context bundle's list of
	 * listeners, this method does nothing.
	 * 
	 * @param listener The <code>BundleListener</code> object to be removed.
	 * @exception java.lang.IllegalStateException If this context bundle has
	 *            stopped.
	 */
	public abstract void removeBundleListener(BundleListener listener);

	/**
	 * Adds the specified <code>FrameworkListener</code> object to this context
	 * bundle's list of listeners if not already present. See
	 * {@link #getBundle()}for a definition of context bundle.
	 * FrameworkListeners are notified of general Framework events.
	 * 
	 * <p>
	 * If this context bundle's list of listeners already contains a listener
	 * <code>l</code> such that <code>(l==listener)</code>, this method does
	 * nothing.
	 * 
	 * @param listener The <code>FrameworkListener</code> object to be added.
	 * @exception java.lang.IllegalStateException If this context bundle has
	 *            stopped.
	 * 
	 * @see FrameworkEvent
	 * @see FrameworkListener
	 */
	public abstract void addFrameworkListener(FrameworkListener listener);

	/**
	 * Removes the specified <code>FrameworkListener</code> object from this
	 * context bundle's list of listeners. See {@link #getBundle()}for a
	 * definition of context bundle.
	 * 
	 * <p>
	 * If <code>listener</code> is not contained in this context bundle's list of
	 * listeners, this method does nothing.
	 * 
	 * @param listener The <code>FrameworkListener</code> object to be removed.
	 * @exception java.lang.IllegalStateException If this context bundle has
	 *            stopped.
	 */
	public abstract void removeFrameworkListener(FrameworkListener listener);

	/**
	 * Registers the specified service object with the specified properties
	 * under the specified class names into the Framework. A
	 * <code>ServiceRegistration</code> object is returned. The
	 * <code>ServiceRegistration</code> object is for the private use of the
	 * bundle registering the service and should not be shared with other
	 * bundles. The registering bundle is defined to be the context bundle. See
	 * {@link #getBundle()}for a definition of context bundle. Other bundles
	 * can locate the service by using either the {@link #getServiceReferences}
	 * or {@link #getServiceReference}method.
	 * 
	 * <p>
	 * A bundle can register a service object that implements the
	 * {@link ServiceFactory}interface to have more flexibility in providing
	 * service objects to other bundles.
	 * 
	 * <p>
	 * The following steps are required to register a service:
	 * <ol>
	 * <li>If <code>service</code> is not a <code>ServiceFactory</code>, an
	 * <code>IllegalArgumentException</code> is thrown if <code>service</code> is
	 * not an <code>instanceof</code> all the classes named.
	 * <li>The Framework adds these service properties to the specified
	 * <code>Dictionary</code> (which may be <code>null</code>): a property named
	 * {@link Constants#SERVICE_ID}identifying the registration number of the
	 * service, and a property named {@link Constants#OBJECTCLASS}containing
	 * all the specified classes. If any of these properties have already been
	 * specified by the registering bundle, their values will be overwritten by
	 * the Framework.
	 * <li>The service is added to the Framework service registry and may now
	 * be used by other bundles.
	 * <li>A service event of type {@link ServiceEvent#REGISTERED}is
	 * synchronously sent.
	 * <li>A <code>ServiceRegistration</code> object for this registration is
	 * returned.
	 * </ol>
	 * 
	 * @param clazzes The class names under which the service can be located.
	 *        The class names in this array will be stored in the service's
	 *        properties under the key {@link Constants#OBJECTCLASS}.
	 * @param service The service object or a <code>ServiceFactory</code> object.
	 * @param properties The properties for this service. The keys in the
	 *        properties object must all be <code>String</code> objects. See
	 *        {@link Constants}for a list of standard service property keys.
	 *        Changes should not be made to this object after calling this
	 *        method. To update the service's properties the
	 *        {@link ServiceRegistration#setProperties}method must be called.
	 *        <code>properties</code> may be <code>null</code> if the service has no
	 *        properties.
	 * 
	 * @return A <code>ServiceRegistration</code> object for use by the bundle
	 *         registering the service to update the service's properties or to
	 *         unregister the service.
	 * 
	 * @exception java.lang.IllegalArgumentException If one of the following is
	 *            true:
	 *            <ul>
	 *            <li><code>service</code> is <code>null</code>.
	 *            <li><code>service</code> is not a <code>ServiceFactory</code>
	 *            object and is not an instance of all the named classes in
	 *            <code>clazzes</code>.
	 *            <li><code>properties</code> contains case variants of the same
	 *            key name.
	 *            </ul>
	 * 
	 * @exception java.lang.SecurityException If the caller does not have the
	 *            <code>ServicePermission</code> to register the service for all
	 *            the named classes and the Java Runtime Environment supports
	 *            permissions.
	 * 
	 * @exception java.lang.IllegalStateException If this context bundle was
	 *            stopped.
	 * 
	 * @see ServiceRegistration
	 * @see ServiceFactory
	 */
	public abstract ServiceRegistration registerService(String[] clazzes,
			Object service, Dictionary properties);

	/**
	 * Registers the specified service object with the specified properties
	 * under the specified class name with the Framework.
	 * 
	 * <p>
	 * This method is otherwise identical to
	 * {@link #registerService(java.lang.String[], java.lang.Object,
	 * java.util.Dictionary)}and is provided as a convenience when
	 * <code>service</code> will only be registered under a single class name.
	 * Note that even in this case the value of the service's
	 * {@link Constants#OBJECTCLASS}property will be an array of strings,
	 * rather than just a single string.
	 * 
	 * @see #registerService(java.lang.String[], java.lang.Object,
	 *      java.util.Dictionary)
	 */
	public abstract ServiceRegistration registerService(String clazz,
			Object service, Dictionary properties);

	/**
	 * Returns a list of <code>ServiceReference</code> objects. This method
	 * returns a list of <code>ServiceReference</code> objects for services which
	 * implement and were registered under the specified class and match the
	 * specified filter criteria.
	 * 
	 * <p>
	 * The list is valid at the time of the call to this method, however as the
	 * Framework is a very dynamic environment, services can be modified or
	 * unregistered at anytime.
	 * 
	 * <p>
	 * <code>filter</code> is used to select the registered service whose
	 * properties objects contain keys and values which satisfy the filter. See
	 * {@link Filter}for a description of the filter string syntax.
	 * 
	 * <p>
	 * If <code>filter</code> is <code>null</code>, all registered services are
	 * considered to match the filter.
	 * <p>
	 * If <code>filter</code> cannot be parsed, an {@link InvalidSyntaxException}
	 * will be thrown with a human readable message where the filter became
	 * unparsable.
	 * 
	 * <p>
	 * The following steps are required to select a service:
	 * <ol>
	 * <li>If the Java Runtime Environment supports permissions, the caller is
	 * checked for the <code>ServicePermission</code> to get the service with the
	 * specified class. If the caller does not have the correct permission,
	 * <code>null</code> is returned.
	 * <li>If the filter string is not <code>null</code>, the filter string is
	 * parsed and the set of registered services which satisfy the filter is
	 * produced. If the filter string is <code>null</code>, then all registered
	 * services are considered to satisfy the filter.
	 * <li>If <code>clazz</code> is not <code>null</code>, the set is further
	 * reduced to those services which are an <code>instanceof</code> and were
	 * registered under the specified class. The complete list of classes of
	 * which a service is an instance and which were specified when the service
	 * was registered is available from the service's
	 * {@link Constants#OBJECTCLASS}property.
	 * <li>An array of <code>ServiceReference</code> to the selected services is
	 * returned.
	 * </ol>
	 * 
	 * @param clazz The class name with which the service was registered, or
	 *        <code>null</code> for all services.
	 * @param filter The filter criteria.
	 * @return An array of <code>ServiceReference</code> objects, or <code>null</code>
	 *         if no services are registered which satisfy the search.
	 * @exception InvalidSyntaxException If <code>filter</code> contains an
	 *            invalid filter string which cannot be parsed.
	 */
	public abstract ServiceReference[] getServiceReferences(String clazz,
			String filter) throws InvalidSyntaxException;

	/**
	 * Returns a list of <code>ServiceReference</code> objects. This method
	 * returns a list of <code>ServiceReference</code> objects for services which
	 * implement and were registered under the specified class and match the
	 * specified filter criteria.
	 * 
	 * <p>
	 * The list is valid at the time of the call to this method, however as the
	 * Framework is a very dynamic environment, services can be modified or
	 * unregistered at anytime.
	 * 
	 * <p>
	 * <code>filter</code> is used to select the registered service whose
	 * properties objects contain keys and values which satisfy the filter. See
	 * {@link Filter}for a description of the filter string syntax.
	 * 
	 * <p>
	 * If <code>filter</code> is <code>null</code>, all registered services are
	 * considered to match the filter.
	 * <p>
	 * If <code>filter</code> cannot be parsed, an {@link InvalidSyntaxException}
	 * will be thrown with a human readable message where the filter became
	 * unparsable.
	 * 
	 * <p>
	 * The following steps are required to select a service:
	 * <ol>
	 * <li>If the Java Runtime Environment supports permissions, the caller is
	 * checked for the <code>ServicePermission</code> to get the service with the
	 * specified class. If the caller does not have the correct permission,
	 * <code>null</code> is returned.
	 * <li>If the filter string is not <code>null</code>, the filter string is
	 * parsed and the set of registered services which satisfy the filter is
	 * produced. If the filter string is <code>null</code>, then all registered
	 * services are considered to satisfy the filter.
	 * <li>If <code>clazz</code> is not <code>null</code>, the set is further
	 * reduced to those services which are an <code>instanceof</code> and were
	 * registered under the specified class. The complete list of classes of
	 * which a service is an instance and which were specified when the service
	 * was registered is available from the service's
	 * {@link Constants#OBJECTCLASS}property.
	 * <li>An array of <code>ServiceReference</code> to the selected services is
	 * returned.
	 * </ol>
	 * 
	 * @param clazz The class name with which the service was registered, or
	 *        <code>null</code> for all services.
	 * @param filter The filter criteria.
	 * @return An array of <code>ServiceReference</code> objects, or <code>null</code>
	 *         if no services are registered which satisfy the search.
	 * @exception InvalidSyntaxException If <code>filter</code> contains an
	 *            invalid filter string which cannot be parsed.
	 */
	public abstract ServiceReference[] getAllServiceReferences(String clazz, 
			String filter) throws InvalidSyntaxException;

	/**
	 * Returns a <code>ServiceReference</code> object for a service that
	 * implements, and was registered under, the specified class.
	 * 
	 * <p>
	 * This <code>ServiceReference</code> object is valid at the time of the call
	 * to this method, however as the Framework is a very dynamic environment,
	 * services can be modified or unregistered at anytime.
	 * 
	 * <p>
	 * This method is the same as calling {@link #getServiceReferences}with a
	 * <code>null</code> filter string. It is provided as a convenience for when
	 * the caller is interested in any service that implements the specified
	 * class.
	 * <p>
	 * If multiple such services exist, the service with the highest ranking (as
	 * specified in its {@link Constants#SERVICE_RANKING}property) is returned.
	 * <p>
	 * If there is a tie in ranking, the service with the lowest service ID (as
	 * specified in its {@link Constants#SERVICE_ID}property); that is, the
	 * service that was registered first is returned.
	 * 
	 * @param clazz The class name with which the service was registered.
	 * @return A <code>ServiceReference</code> object, or <code>null</code> if no
	 *         services are registered which implement the named class.
	 * @see #getServiceReferences
	 */
	public abstract ServiceReference getServiceReference(String clazz);

	/**
	 * Returns the specified service object for a service.
	 * <p>
	 * A bundle's use of a service is tracked by the bundle's use count of that
	 * service. Each time a service's service object is returned by
	 * {@link #getService}the context bundle's use count for that service is
	 * incremented by one. Each time the service is released by
	 * {@link #ungetService}the context bundle's use count for that service is
	 * decremented by one.
	 * <p>
	 * When a bundle's use count for a service drops to zero, the bundle should
	 * no longer use that service. See {@link #getBundle()}for a definition of
	 * context bundle.
	 * 
	 * <p>
	 * This method will always return <code>null</code> when the service
	 * associated with this <code>reference</code> has been unregistered.
	 * 
	 * <p>
	 * The following steps are required to get the service object:
	 * <ol>
	 * <li>If the service has been unregistered, <code>null</code> is returned.
	 * <li>The context bundle's use count for this service is incremented by
	 * one.
	 * <li>If the context bundle's use count for the service is currently one
	 * and the service was registered with an object implementing the
	 * <code>ServiceFactory</code> interface, the
	 * {@link ServiceFactory#getService}method is called to create a service
	 * object for the context bundle. This service object is cached by the
	 * Framework. While the context bundle's use count for the service is
	 * greater than zero, subsequent calls to get the services's service object
	 * for the context bundle will return the cached service object. <br>
	 * If the service object returned by the <code>ServiceFactory</code> object is
	 * not an <code>instanceof</code> all the classes named when the service was
	 * registered or the <code>ServiceFactory</code> object throws an exception,
	 * <code>null</code> is returned and a Framework event of type
	 * {@link FrameworkEvent#ERROR}is broadcast.
	 * <li>The service object for the service is returned.
	 * </ol>
	 * 
	 * @param reference A reference to the service.
	 * @return A service object for the service associated with
	 *         <code>reference</code>, or <code>null</code> if the service is not
	 *         registered or does not implement the classes under which it was
	 *         registered in the case of a Service Factory.
	 * @exception java.lang.SecurityException If the caller does not have the
	 *            <code>ServicePermission</code> to get the service using at least
	 *            one of the named classes the service was registered under, and
	 *            the Java Runtime Environment supports permissions.
	 * @exception java.lang.IllegalStateException If the context bundle has
	 *            stopped.
	 * @see #ungetService
	 * @see ServiceFactory
	 */
	public abstract Object getService(ServiceReference reference);

	/**
	 * Releases the service object referenced by the specified
	 * <code>ServiceReference</code> object. If the context bundle's use count for
	 * the service is zero, this method returns <code>false</code>. Otherwise,
	 * the context bundle's use count for the service is decremented by one. See
	 * {@link #getBundle()}for a definition of context bundle.
	 * 
	 * <p>
	 * The service's service object should no longer be used and all references
	 * to it should be destroyed when a bundle's use count for the service drops
	 * to zero.
	 * 
	 * <p>
	 * The following steps are required to unget the service object:
	 * <ol>
	 * <li>If the context bundle's use count for the service is zero or the
	 * service has been unregistered, <code>false</code> is returned.
	 * <li>The context bundle's use count for this service is decremented by
	 * one.
	 * <li>If the context bundle's use count for the service is currently zero
	 * and the service was registered with a <code>ServiceFactory</code> object,
	 * the {@link ServiceFactory#ungetService}method is called to release the
	 * service object for the context bundle.
	 * <li><code>true</code> is returned.
	 * </ol>
	 * 
	 * @param reference A reference to the service to be released.
	 * @return <code>false</code> if the context bundle's use count for the
	 *         service is zero or if the service has been unregistered;
	 *         <code>true</code> otherwise.
	 * @exception java.lang.IllegalStateException If the context bundle has
	 *            stopped.
	 * @see #getService
	 * @see ServiceFactory
	 */
	public abstract boolean ungetService(ServiceReference reference);

	/**
	 * Creates a <code>File</code> object for a file in the persistent storage
	 * area provided for the bundle by the Framework. This method will return
	 * <code>null</code> if the platform does not have file system support.
	 * 
	 * <p>
	 * A <code>File</code> object for the base directory of the persistent storage
	 * area provided for the context bundle by the Framework can be obtained by
	 * calling this method with an empty string (" ") as <code>filename</code>.
	 * See {@link #getBundle()}for a definition of context bundle.
	 * 
	 * <p>
	 * If the Java Runtime Environment supports permissions, the Framework will
	 * ensure that the bundle has the <code>java.io.FilePermission</code> with
	 * actions <code>read</code>,<code>write</code>,<code>delete</code> for all
	 * files (recursively) in the persistent storage area provided for the
	 * context bundle.
	 * 
	 * @param filename A relative name to the file to be accessed.
	 * @return A <code>File</code> object that represents the requested file or
	 *         <code>null</code> if the platform does not have file system
	 *         support.
	 * @exception java.lang.IllegalStateException If the context bundle has
	 *            stopped.
	 */
	public abstract File getDataFile(String filename);

	/**
	 * Creates a <code>Filter</code> object. This <code>Filter</code> object may be
	 * used to match a <code>ServiceReference</code> object or a
	 * <code>Dictionary</code> object. See {@link Filter}for a description of the
	 * filter string syntax.
	 * 
	 * <p>
	 * If the filter cannot be parsed, an {@link InvalidSyntaxException}will be
	 * thrown with a human readable message where the filter became unparsable.
	 * 
	 * @param filter The filter string.
	 * @return A <code>Filter</code> object encapsulating the filter string.
	 * @exception InvalidSyntaxException If <code>filter</code> contains an
	 *            invalid filter string that cannot be parsed.
	 * @exception NullPointerException If <code>filter</code> is null.
	 * 
	 * @since 1.1
	 */
	public abstract Filter createFilter(String filter)
			throws InvalidSyntaxException;
}

