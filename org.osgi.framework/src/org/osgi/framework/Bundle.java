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

import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 * An installed bundle in the Framework.
 * 
 * <p>
 * A <tt>Bundle</tt> object is the access point to define the life cycle of an
 * installed bundle. Each bundle installed in the OSGi environment will have an
 * associated <tt>Bundle</tt> object.
 * 
 * <p>
 * A bundle will have a unique identity, a <tt>long</tt>, chosen by the
 * Framework. This identity will not change during the life cycle of a bundle,
 * even when the bundle is updated. Uninstalling and then reinstalling the
 * bundle will create a new unique identity.
 * 
 * <p>
 * A bundle can be in one of six states:
 * <ul>
 * <li>{@link #UNINSTALLED}
 * <li>{@link #INSTALLED}
 * <li>{@link #RESOLVED}
 * <li>{@link #STARTING}
 * <li>{@link #STOPPING}
 * <li>{@link #ACTIVE}
 * </ul>
 * <p>
 * Values assigned to these states have no specified ordering; they represent
 * bit values that may be ORed together to determine if a bundle is in one of
 * the valid states.
 * 
 * <p>
 * A bundle should only execute code when its state is one of <tt>STARTING</tt>,
 * <tt>ACTIVE</tt>, or <tt>STOPPING</tt>. An <tt>UNINSTALLED</tt> bundle
 * can not be set to another state; it is a zombie and can only be reached
 * because references are kept somewhere.
 * 
 * <p>
 * The Framework is the only entity that is allowed to create <tt>Bundle</tt>
 * objects, and these objects are only valid within the Framework that created
 * them.
 * 
 * @version $Revision$
 */
public abstract interface Bundle {
	/**
	 * This bundle is uninstalled and may not be used.
	 * 
	 * <p>
	 * The <tt>UNINSTALLED</tt> state is only visible after a bundle is
	 * uninstalled; the bundle is in an unusable state but references to the
	 * <tt>Bundle</tt> object may still be available and used for
	 * introspection.
	 * <p>
	 * The value of <tt>UNINSTALLED</tt> is 0x00000001.
	 */
	public static final int	UNINSTALLED	= 0x00000001;

	/**
	 * This bundle is installed but not yet resolved.
	 * 
	 * <p>
	 * A bundle is in the <tt>INSTALLED</tt> state when it has been installed
	 * in the Framework but cannot run.
	 * <p>
	 * This state is visible if the bundle's code dependencies are not resolved.
	 * The Framework may attempt to resolve an <tt>INSTALLED</tt> bundle's
	 * code dependencies and move the bundle to the <tt>RESOLVED</tt> state.
	 * <p>
	 * The value of <tt>INSTALLED</tt> is 0x00000002.
	 */
	public static final int	INSTALLED	= 0x00000002;

	/**
	 * This bundle is resolved and is able to be started.
	 * 
	 * <p>
	 * A bundle is in the <tt>RESOLVED</tt> state when the Framework has
	 * successfully resolved the bundle's dependencies. These dependencies
	 * include:
	 * <ul>
	 * <li>The bundle's class path from its {@link Constants#BUNDLE_CLASSPATH}
	 * Manifest header.
	 * <li>The bundle's package dependencies from its
	 * {@link Constants#EXPORT_PACKAGE}and {@link Constants#IMPORT_PACKAGE}
	 * Manifest headers.
	 * </ul>
	 * <p>
	 * Note that the bundle is not active yet. A bundle must be put in the
	 * <tt>RESOLVED</tt> state before it can be started. The Framework may
	 * attempt to resolve a bundle at any time.
	 * <p>
	 * The value of <tt>RESOLVED</tt> is 0x00000004.
	 */
	public static final int	RESOLVED	= 0x00000004;

	/**
	 * This bundle is in the process of starting.
	 * 
	 * <p>
	 * A bundle is in the <tt>STARTING</tt> state when the {@link #start}
	 * method is active. A bundle will be in this state when the bundle's
	 * {@link BundleActivator#start}is called. If this method completes without
	 * exception, then the bundle has successfully started and will move to the
	 * <tt>ACTIVE</tt> state.
	 * <p>
	 * The value of <tt>STARTING</tt> is 0x00000008.
	 */
	public static final int	STARTING	= 0x00000008;

	/**
	 * This bundle is in the process of stopping.
	 * 
	 * <p>
	 * A bundle is in the <tt>STOPPING</tt> state when the {@link #stop}
	 * method is active. A bundle will be in this state when the bundle's
	 * {@link BundleActivator#stop}method is called. When this method completes
	 * the bundle is stopped and will move to the <tt>RESOLVED</tt> state.
	 * <p>
	 * The value of <tt>STOPPING</tt> is 0x00000010.
	 */
	public static final int	STOPPING	= 0x00000010;

	/**
	 * This bundle is now running.
	 * 
	 * <p>
	 * A bundle is in the <tt>ACTIVE</tt> state when it has been successfully
	 * started.
	 * <p>
	 * The value of <tt>ACTIVE</tt> is 0x00000020.
	 */
	public static final int	ACTIVE		= 0x00000020;

	/**
	 * Returns this bundle's current state.
	 * 
	 * <p>
	 * A bundle can be in only one state at any time.
	 * 
	 * @return An element of <tt>UNINSTALLED</tt>,<tt>INSTALLED</tt>,
	 *         <tt>RESOLVED</tt>,<tt>STARTING</tt>,<tt>STOPPING</tt>,
	 *         <tt>ACTIVE</tt>.
	 */
	public abstract int getState();

	/**
	 * Starts this bundle.
	 * 
	 * If the Framework implements the optional Start Level service and the
	 * current start level is less than this bundle's start level, then the
	 * Framework must persistently mark this bundle as started and delay the
	 * starting of this bundle until the Framework's current start level becomes
	 * equal or more than the bundle's start level.
	 * <p>
	 * Otherwise, the following steps are required to start a bundle:
	 * <ol>
	 * <li>If this bundle's state is <tt>UNINSTALLED</tt> then an
	 * <tt>IllegalStateException</tt> is thrown.
	 * 
	 * <li>If this bundle's state is <tt>STARTING</tt> or <tt>STOPPING</tt>
	 * then this method will wait for this bundle to change state before
	 * continuing. If this does not occur in a reasonable time, a
	 * <tt>BundleException</tt> is thrown to indicate this bundle was unable
	 * to be started.
	 * 
	 * <li>If this bundle's state is <tt>ACTIVE</tt> then this method returns
	 * immediately.
	 * 
	 * <li>If this bundle's state is not <tt>RESOLVED</tt>, an attempt is
	 * made to resolve this bundle's package dependencies. If the Framework
	 * cannot resolve this bundle, a <tt>BundleException</tt> is thrown.
	 * 
	 * <li>This bundle's state is set to <tt>STARTING</tt>.
	 * 
	 * <li>The {@link BundleActivator#start}method of this bundle's
	 * <tt>BundleActivator</tt>, if one is specified, is called. If the
	 * <tt>BundleActivator</tt> is invalid or throws an exception, this
	 * bundle's state is set back to <tt>RESOLVED</tt>.<br>
	 * Any services registered by the bundle will be unregistered. <br>
	 * Any services used by the bundle will be released. <br>
	 * Any listeners registered by the bundle will be removed. <br>
	 * A <tt>BundleException</tt> is then thrown.
	 * 
	 * <li>If this bundle's state is <tt>UNINSTALLED</tt>, because the
	 * bundle was uninstalled while the <tt>BundleActivator.start</tt> method
	 * was running, a <tt>BundleException</tt> is thrown.
	 * 
	 * <li>Persistently record that this bundle has been started. When the
	 * Framework is restarted, this bundle will be automatically started.
	 * 
	 * <li>This bundle's state is set to <tt>ACTIVE</tt>.
	 * 
	 * <li>A bundle event of type {@link BundleEvent#STARTED}is broadcast.
	 * </ol>
	 * 
	 * <b>Preconditions </b>
	 * <ul>
	 * <li><tt>getState()</tt> in {<tt>INSTALLED</tt>}, {
	 * <tt>RESOLVED</tt>}.
	 * </ul>
	 * <b>Postconditions, no exceptions thrown </b>
	 * <ul>
	 * <li><tt>getState()</tt> in {<tt>ACTIVE</tt>}.
	 * <li><tt>BundleActivator.start()</tt> has been called and did not throw
	 * an exception.
	 * </ul>
	 * <b>Postconditions, when an exception is thrown </b>
	 * <ul>
	 * <li><tt>getState()</tt> not in {<tt>STARTING</tt>}, {
	 * <tt>ACTIVE</tt>}.
	 * </ul>
	 * 
	 * @exception BundleException If this bundle couldn't be started. This could
	 *            be because a code dependency could not be resolved or the
	 *            specified <tt>BundleActivator</tt> could not be loaded or
	 *            threw an exception.
	 * @exception java.lang.IllegalStateException If this bundle has been
	 *            uninstalled or this bundle tries to change its own state.
	 * @exception java.lang.SecurityException If the caller does not have the
	 *            appropriate <tt>AdminPermission</tt>, and the Java Runtime
	 *            Environment supports permissions.
	 */
	public abstract void start() throws BundleException;

	/**
	 * Stops this bundle.
	 * 
	 * <p>
	 * The following steps are required to stop a bundle:
	 * <ol>
	 * <li>If this bundle's state is <tt>UNINSTALLED</tt> then an
	 * <tt>IllegalStateException</tt> is thrown.
	 * 
	 * <li>If this bundle's state is <tt>STARTING</tt> or <tt>STOPPING</tt>
	 * then this method will wait for this bundle to change state before
	 * continuing. If this does not occur in a reasonable time, a
	 * <tt>BundleException</tt> is thrown to indicate this bundle was unable
	 * to be stopped.
	 * 
	 * <li>Persistently record that this bundle has been stopped. When the
	 * Framework is restarted, this bundle will not be automatically started.
	 * 
	 * <li>If this bundle's state is not <tt>ACTIVE</tt> then this method
	 * returns immediately.
	 * 
	 * <li>This bundle's state is set to <tt>STOPPING</tt>.
	 * 
	 * <li>The {@link BundleActivator#stop}method of this bundle's
	 * <tt>BundleActivator</tt>, if one is specified, is called. If this
	 * method throws an exception, it will continue to stop this bundle. A
	 * <tt>BundleException</tt> will be thrown after completion of the
	 * remaining steps.
	 * 
	 * <li>Any services registered by this bundle must be unregistered.
	 * <li>Any services used by this bundle must be released.
	 * <li>Any listeners registered by this bundle must be removed.
	 * 
	 * <li>If this bundle's state is <tt>UNINSTALLED</tt>, because the
	 * bundle was uninstalled while the <tt>BundleActivator.stop</tt> method
	 * was running, a <tt>BundleException</tt> must be thrown.
	 * 
	 * <li>This bundle's state is set to <tt>RESOLVED</tt>.
	 * 
	 * <li>A bundle event of type {@link BundleEvent#STOPPED}is broadcast.
	 * </ol>
	 * 
	 * <b>Preconditions </b>
	 * <ul>
	 * <li><tt>getState()</tt> in {<tt>ACTIVE</tt>}.
	 * </ul>
	 * <b>Postconditions, no exceptions thrown </b>
	 * <ul>
	 * <li><tt>getState()</tt> not in {<tt>ACTIVE</tt>,<tt>STOPPING</tt>}.
	 * <li><tt>BundleActivator.stop</tt> has been called and did not throw an
	 * exception.
	 * </ul>
	 * <b>Postconditions, when an exception is thrown </b>
	 * <ul>
	 * <li>None.
	 * </ul>
	 * 
	 * @exception BundleException If this bundle's <tt>BundleActivator</tt>
	 *            could not be loaded or threw an exception.
	 * @exception java.lang.IllegalStateException If this bundle has been
	 *            uninstalled or this bundle tries to change its own state.
	 * @exception java.lang.SecurityException If the caller does not have the
	 *            appropriate <tt>AdminPermission</tt>, and the Java Runtime
	 *            Environment supports permissions.
	 */
	public abstract void stop() throws BundleException;

	/**
	 * Updates this bundle.
	 * 
	 * <p>
	 * If this bundle's state is <tt>ACTIVE</tt>, it will be stopped before
	 * the update and started after the update successfully completes.
	 * 
	 * <p>
	 * If the bundle being updated has exported any packages, these packages
	 * will not be updated. Instead, the previous package version will remain
	 * exported until the <tt>PackageAdmin.refreshPackages</tt> method has
	 * been has been called or the Framework is relaunched.
	 * 
	 * <p>
	 * The following steps are required to update a bundle:
	 * <ol>
	 * <li>If this bundle's state is <tt>UNINSTALLED</tt> then an
	 * <tt>IllegalStateException</tt> is thrown.
	 * 
	 * <li>If this bundle's state is <tt>ACTIVE</tt>,<tt>STARTING</tt> or
	 * <tt>STOPPING</tt>, the bundle is stopped as described in the
	 * <tt>Bundle.stop</tt> method. If <tt>Bundle.stop</tt> throws an
	 * exception, the exception is rethrown terminating the update.
	 * 
	 * <li>The download location of the new version of this bundle is
	 * determined from either the bundle's
	 * {@link Constants#BUNDLE_UPDATELOCATION}Manifest header (if available) or
	 * the bundle's original location.
	 * 
	 * <li>The location is interpreted in an implementation dependent manner,
	 * typically as a URL, and the new version of this bundle is obtained from
	 * this location.
	 * 
	 * <li>The new version of this bundle is installed. If the Framework is
	 * unable to install the new version of this bundle, the original version of
	 * this bundle will be restored and a <tt>BundleException</tt> will be
	 * thrown after completion of the remaining steps.
	 * 
	 * <li>If the bundle has declared an Bundle-RequiredExecutionEnvironment
	 * header, then the listed execution environments must be verified against
	 * the installed execution environments. If they do not all match, the
	 * original version of this bundle will be restored and a
	 * <tt>BundleException</tt> will be thrown after completion of the
	 * remaining steps.
	 * 
	 * <li>This bundle's state is set to <tt>INSTALLED</tt>.
	 * 
	 * 
	 * <li>If this bundle has not declared an <tt>Import-Package</tt> header
	 * in its Manifest file (specifically, this bundle does not depend on any
	 * packages from other bundles), this bundle's state may be set to
	 * <tt>RESOLVED</tt>.
	 * 
	 * <li>If the new version of this bundle was successfully installed, a
	 * bundle event of type {@link BundleEvent#UPDATED}is broadcast.
	 * 
	 * <li>If this bundle's state was originally <tt>ACTIVE</tt>, the
	 * updated bundle is started as described in the <tt>Bundle.start</tt>
	 * method. If <tt>Bundle.start</tt> throws an exception, a Framework event
	 * of type {@link FrameworkEvent#ERROR}is broadcast containing the
	 * exception.
	 * </ol>
	 * 
	 * <b>Preconditions </b>
	 * <ul>
	 * <li><tt>getState()</tt> not in {<tt>UNINSTALLED</tt>}.
	 * </ul>
	 * <b>Postconditions, no exceptions thrown </b>
	 * <ul>
	 * <li><tt>getState()</tt> in {<tt>INSTALLED</tt>,<tt>RESOLVED</tt>,
	 * <tt>ACTIVE</tt>}.
	 * <li>This bundle has been updated.
	 * </ul>
	 * <b>Postconditions, when an exception is thrown </b>
	 * <ul>
	 * <li><tt>getState()</tt> in {<tt>INSTALLED</tt>,<tt>RESOLVED</tt>,
	 * <tt>ACTIVE</tt>}.
	 * <li>Original bundle is still used; no update occurred.
	 * </ul>
	 * 
	 * @exception BundleException If the update fails.
	 * @exception java.lang.IllegalStateException If this bundle has been
	 *            uninstalled or this bundle tries to change its own state.
	 * @exception java.lang.SecurityException If the caller does not have the
	 *            appropriate <tt>AdminPermission</tt>, and the Java Runtime
	 *            Environment supports permissions.
	 * @see #stop()
	 * @see #start()
	 */
	public abstract void update() throws BundleException;

	/**
	 * Updates this bundle from an <tt>InputStream</tt>.
	 * 
	 * <p>
	 * This method performs all the steps listed in <tt>Bundle.update()</tt>,
	 * except the bundle will be read from the supplied <tt>InputStream</tt>,
	 * rather than a <tt>URL</tt>.
	 * <p>
	 * This method will always close the <tt>InputStream</tt> when it is done,
	 * even if an exception is thrown.
	 * 
	 * @param in The <tt>InputStream</tt> from which to read the new bundle.
	 * @exception BundleException If the provided stream cannot be read or the
	 *            update fails.
	 * @exception java.lang.IllegalStateException If this bundle has been
	 *            uninstalled or this bundle tries to change its own state.
	 * @exception java.lang.SecurityException If the caller does not have the
	 *            appropriate <tt>AdminPermission</tt>, and the Java Runtime
	 *            Environment supports permissions.
	 * @see #update()
	 */
	public abstract void update(InputStream in) throws BundleException;

	/**
	 * Uninstalls this bundle.
	 * 
	 * <p>
	 * This method causes the Framework to notify other bundles that this bundle
	 * is being uninstalled, and then puts this bundle into the
	 * <tt>UNINSTALLED</tt> state. The Framework will remove any resources
	 * related to this bundle that it is able to remove.
	 * 
	 * <p>
	 * If this bundle has exported any packages, the Framework will continue to
	 * make these packages available to their importing bundles until the
	 * <tt>PackageAdmin.refreshPackages</tt> method has been called or the
	 * Framework is relaunched.
	 * 
	 * <p>
	 * The following steps are required to uninstall a bundle:
	 * <ol>
	 * <li>If this bundle's state is <tt>UNINSTALLED</tt> then an
	 * <tt>IllegalStateException</tt> is thrown.
	 * 
	 * <li>If this bundle's state is <tt>ACTIVE</tt>,<tt>STARTING</tt> or
	 * <tt>STOPPING</tt>, this bundle is stopped as described in the
	 * <tt>Bundle.stop</tt> method. If <tt>Bundle.stop</tt> throws an
	 * exception, a Framework event of type {@link FrameworkEvent#ERROR}is
	 * broadcast containing the exception.
	 * 
	 * <li>This bundle's state is set to <tt>UNINSTALLED</tt>.
	 * 
	 * <li>A bundle event of type {@link BundleEvent#UNINSTALLED}is broadcast.
	 * 
	 * <li>This bundle and any persistent storage area provided for this bundle
	 * by the Framework are removed.
	 * </ol>
	 * 
	 * <b>Preconditions </b>
	 * <ul>
	 * <li><tt>getState()</tt> not in {<tt>UNINSTALLED</tt>}.
	 * </ul>
	 * <b>Postconditions, no exceptions thrown </b>
	 * <ul>
	 * <li><tt>getState()</tt> in {<tt>UNINSTALLED</tt>}.
	 * <li>This bundle has been uninstalled.
	 * </ul>
	 * <b>Postconditions, when an exception is thrown </b>
	 * <ul>
	 * <li><tt>getState()</tt> not in {<tt>UNINSTALLED</tt>}.
	 * <li>This Bundle has not been uninstalled.
	 * </ul>
	 * 
	 * @exception BundleException If the uninstall failed. This can occur if
	 *            another thread is attempting to change the bundle's state and
	 *            does not complete in a timely manner.
	 * @exception java.lang.IllegalStateException If this bundle has been
	 *            uninstalled or this bundle tries to change its own state.
	 * @exception java.lang.SecurityException If the caller does not have the
	 *            appropriate <tt>AdminPermission</tt>, and the Java Runtime
	 *            Environment supports permissions.
	 * @see #stop()
	 */
	public abstract void uninstall() throws BundleException;

	/**
	 * Returns this bundle's Manifest headers and values. This method returns
	 * all the Manifest headers and values from the main section of the bundle's
	 * Manifest file; that is, all lines prior to the first blank line.
	 * 
	 * <p>
	 * Manifest header names are case-insensitive. The methods of the returned
	 * <tt>Dictionary</tt> object will operate on header names in a
	 * case-insensitive manner.
	 * 
	 * If a Manifest header value starts with &quot;%&quot;, it will be
	 * localized according to the default locale.
	 * 
	 * <p>
	 * For example, the following Manifest headers and values are included if
	 * they are present in the Manifest file:
	 * 
	 * <pre>
	 *      Bundle-Name
	 *      Bundle-Vendor
	 *      Bundle-Version
	 *      Bundle-Description
	 *      Bundle-DocURL
	 *      Bundle-ContactAddress
	 * </pre>
	 * 
	 * <p>
	 * This method will continue to return Manifest header information while
	 * this bundle is in the <tt>UNINSTALLED</tt> state.
	 * 
	 * @return A <tt>Dictionary</tt> object containing this bundle's Manifest
	 *         headers and values.
	 * 
	 * @exception java.lang.SecurityException If the caller does not have the
	 *            <tt>AdminPermission</tt>, and the Java Runtime Environment
	 *            supports permissions.
	 * 
	 * @see Constants#BUNDLE_LOCALIZATION
	 */
	public abstract Dictionary getHeaders();

	/**
	 * Returns this bundle's identifier. The bundle is assigned a unique
	 * identifier by the Framework when it is installed in the OSGi environment.
	 * 
	 * <p>
	 * A bundle's unique identifier has the following attributes:
	 * <ul>
	 * <li>Is unique and persistent.
	 * <li>Is a <tt>long</tt>.
	 * <li>Its value is not reused for another bundle, even after the bundle is
	 * uninstalled.
	 * <li>Does not change while the bundle remains installed.
	 * <li>Does not change when the bundle is updated.
	 * </ul>
	 * 
	 * <p>
	 * This method will continue to return this bundle's unique identifier while
	 * this bundle is in the <tt>UNINSTALLED</tt> state.
	 * 
	 * @return The unique identifier of this bundle.
	 */
	public abstract long getBundleId();

	/**
	 * Returns this bundle's location identifier.
	 * 
	 * <p>
	 * The bundle location identifier is the location passed to
	 * <tt>BundleContext.installBundle</tt> when a bundle is installed.
	 * 
	 * <p>
	 * This method will continue to return this bundle's location identifier
	 * while this bundle is in the <tt>UNINSTALLED</tt> state.
	 * 
	 * @return The string representation of this bundle's location identifier.
	 * @exception java.lang.SecurityException If the caller does not have the
	 *            appropriate <tt>AdminPermission</tt>, and the Java Runtime
	 *            Environment supports permissions.
	 */
	public abstract String getLocation();

	/**
	 * Returns this bundle's <tt>ServiceReference</tt> list for all services
	 * it has registered or <tt>null</tt> if this bundle has no registered
	 * services.
	 * 
	 * <p>
	 * If the Java runtime supports permissions, a <tt>ServiceReference</tt>
	 * object to a service is included in the returned list only if the caller
	 * has the <tt>ServicePermission</tt> to get the service using at least
	 * one of the named classes the service was registered under.
	 * 
	 * <p>
	 * The list is valid at the time of the call to this method, however, as the
	 * Framework is a very dynamic environment, services can be modified or
	 * unregistered at anytime.
	 * 
	 * @return An array of <tt>ServiceReference</tt> objects or <tt>null</tt>.
	 * @exception java.lang.IllegalStateException If this bundle has been
	 *            uninstalled.
	 * @see ServiceRegistration
	 * @see ServiceReference
	 * @see ServicePermission
	 */
	public abstract ServiceReference[] getRegisteredServices();

	/**
	 * Returns this bundle's <tt>ServiceReference</tt> list for all services
	 * it is using or returns <tt>null</tt> if this bundle is not using any
	 * services. A bundle is considered to be using a service if its use count
	 * for that service is greater than zero.
	 * 
	 * <p>
	 * If the Java Runtime Environment supports permissions, a
	 * <tt>ServiceReference</tt> object to a service is included in the
	 * returned list only if the caller has the <tt>ServicePermission</tt> to
	 * get the service using at least one of the named classes the service was
	 * registered under.
	 * <p>
	 * The list is valid at the time of the call to this method, however, as the
	 * Framework is a very dynamic environment, services can be modified or
	 * unregistered at anytime.
	 * 
	 * @return An array of <tt>ServiceReference</tt> objects or <tt>null</tt>.
	 * @exception java.lang.IllegalStateException If this bundle has been
	 *            uninstalled.
	 * @see ServiceReference
	 * @see ServicePermission
	 */
	public abstract ServiceReference[] getServicesInUse();

	/**
	 * Determines if this bundle has the specified permissions.
	 * 
	 * <p>
	 * If the Java Runtime Environment does not support permissions, this method
	 * always returns <tt>true</tt>.
	 * <p>
	 * <tt>permission</tt> is of type <tt>Object</tt> to avoid referencing
	 * the <tt>java.security.Permission</tt> class directly. This is to allow
	 * the Framework to be implemented in Java environments which do not support
	 * permissions.
	 * 
	 * <p>
	 * If the Java Runtime Environment does support permissions, this bundle and
	 * all its resources including nested JAR files, belong to the same
	 * <tt>java.security.ProtectionDomain</tt>; that is, they will share the
	 * same set of permissions.
	 * 
	 * @param permission The permission to verify.
	 * 
	 * @return <tt>true</tt> if this bundle has the specified permission or
	 *         the permissions possessed by this bundle imply the specified
	 *         permission; <tt>false</tt> if this bundle does not have the
	 *         specified permission or <tt>permission</tt> is not an
	 *         <tt>instanceof</tt> <tt>java.security.Permission</tt>.
	 * 
	 * @exception java.lang.IllegalStateException If this bundle has been
	 *            uninstalled.
	 */
	public abstract boolean hasPermission(Object permission);

	/**
	 * Find the specified resource in this bundle.
	 * 
	 * This bundle's class loader is called to search for the named resource. If
	 * this bundle's state is <tt>INSTALLED</tt>, then only this bundle will
	 * be searched for the specified resource. Imported packages cannot be
	 * searched when a bundle has not been resolved. If this bundle is a
	 * fragment bundle then <tt>null</tt> is returned.
	 * 
	 * @param name The name of the resource. See
	 *        <tt>java.lang.ClassLoader.getResource</tt> for a description of
	 *        the format of a resource name.
	 * @return a URL to the named resource, or <tt>null</tt> if the resource
	 *         could not be found or if this bundle is a fragment bundle or if
	 *         the caller does not have the <tt>AdminPermission</tt>, and the
	 *         Java Runtime Environment supports permissions.
	 * 
	 * @since 1.1
	 * @exception java.lang.IllegalStateException If this bundle has been
	 *            uninstalled.
	 */
	public abstract URL getResource(String name);

	/**
	 * Returns this bundle's Manifest headers and values localized to the
	 * specifed locale.
	 * 
	 * <p>
	 * This method performs the same function as <tt>Bundle.getHeaders()</tt>
	 * except the manifest header values are localized to the specified locale.
	 * 
	 * If a Manifest header value starts with &quot;%&quot;, it will be
	 * localized according to the specified locale.
	 * 
	 * If <tt>null</tt> is specified as the locale string, the header values
	 * will be localized using the default locale. If the empty string
	 * (&quot;&quot;) is specified as the locale string, the header values will
	 * not be localized and the raw (unlocalized) header values, including any
	 * leading &quot;%&quot;, will be returned.
	 * 
	 * <p>
	 * This method will continue to return Manifest header information while
	 * this bundle is in the <tt>UNINSTALLED</tt> state, however the header
	 * values will only be localized to the default locale.
	 * 
	 * @return A <tt>Dictionary</tt> object containing this bundle's Manifest
	 *         headers and values.
	 * 
	 * @exception java.lang.SecurityException If the caller does not have the
	 *            <tt>AdminPermission</tt>, and the Java Runtime Environment
	 *            supports permissions.
	 * 
	 * @see #getHeaders()
	 * @see Constants#BUNDLE_LOCALIZATION
	 * @since 1.3
	 */
	public Dictionary getHeaders(String localeString);

	/**
	 * Returns the symbolic name of this bundle as specified by its
	 * <tt>Bundle-SymbolicName</tt> manifest header. The name must be unique,
	 * it is recommended to use a reverse domain name naming convention like
	 * that used for java packages. If the bundle does not have a specified
	 * symbolic name then <tt>null</tt> is returned.
	 * 
	 * <p>
	 * This method will continue to return this bundle's symbolic name while
	 * this bundle is in the <tt>UNINSTALLED</tt> state.
	 * 
	 * @return The symbolic name of this bundle.
	 * @since 1.3
	 */
	public String getSymbolicName();

	/**
	 * 
	 * Loads the specified class using this bundle's classloader.
	 * 
	 * <p>
	 * If this bundle's state is <tt>INSTALLED</tt>, this method will attempt
	 * to resolve the bundle before attempting to load the class.
	 * 
	 * <p>
	 * If the bundle cannot be resolved, a Framework event of type
	 * {@link FrameworkEvent#ERROR}is broadcast containing a
	 * <tt>BundleException</tt> with details of the reason the bundle could
	 * not be resolved. This method must then throw a
	 * <tt>ClassNotFoundException</tt>.
	 * 
	 * <p>
	 * If the bundle is a fragment bundle then this method must throw a
	 * <tt>ClassNotFoundException</tt>.
	 * 
	 * <p>
	 * If this bundle's state is <tt>UNINSTALLED</tt>, then an
	 * <tt>IllegalStateException</tt> is thrown.
	 * 
	 * @param name The name of the class to load.
	 * @return The Class object for the requested class.
	 * @exception java.lang.ClassNotFoundException If no such class can be found
	 *            or if this bundle is a fragment bundle or if the caller does
	 *            not have the <tt>AdminPermission</tt>, and the Java Runtime
	 *            Environment supports permissions.
	 * @exception java.lang.IllegalStateException If this bundle has been
	 *            uninstalled.
	 * @since 1.3
	 */
	public Class loadClass(String name) throws ClassNotFoundException;

	/**
	 * Find the specified resources in this bundle.
	 * 
	 * This bundle's class loader is called to search for the named resource. If
	 * this bundle's state is <tt>INSTALLED</tt>, then only this bundle will
	 * be searched for the specified resource. Imported packages cannot be
	 * searched when a bundle has not been resolved. If this bundle is a
	 * fragment bundle then <tt>null</tt> is returned.
	 * 
	 * @param name The name of the resource. See
	 *        <tt>java.lang.ClassLoader.getResources</tt> for a description of
	 *        the format of a resource name.
	 * @return an Enumeration of URLs to the named resources, or <tt>null</tt>
	 *         if the resource could not be found or if this bundle is a
	 *         fragment bundle or if the caller does not have the
	 *         <tt>AdminPermission</tt>, and the Java Runtime Environment
	 *         supports permissions.
	 * 
	 * @since 1.3
	 * @exception java.lang.IllegalStateException If this bundle has been
	 *            uninstalled.
	 */
	public Enumeration getResources(String name);

	/**
	 * Returns enumeration of all the paths to entries within the bundle whose
	 * longest sub-path matches the supplied path argument. The bundle's
	 * classloader is not used to search for entries. Only the contents of the
	 * bundle is searched. A specified path of &quot;/&quot; indicates the root
	 * of the bundle.
	 * 
	 * <p>
	 * Returned paths indicating subdirectory paths end with a &quot;/&quot;.
	 * The returned paths are all relative to the root of the bundle and have a
	 * leading &quot;/&quot;.
	 * 
	 * <p>
	 * This method returns <code>null</code> if no entries could be found that
	 * match the specified path or if the caller does not have
	 * <tt>AdminPermission</tt> and the Java Runtime Environment supports
	 * permissions.
	 * 
	 * @param path the path name to get the entry path names for.
	 * @return An Enumeration of the entry paths (<tt>String</tt> objects###)that are contained in the
	 *         specified path or <tt>null</tt> if the resource could not be
	 *         found or if the caller does not have the <tt>AdminPermission</tt>,
	 *         and the Java Runtime Environment supports permissions.
	 * @exception java.lang.IllegalStateException If this bundle has been
	 *            uninstalled.
	 * @since 1.3
	 */
	public Enumeration getEntryPaths(String path);

	/**
	 * Returns a URL to the specified entry in this bundle. The bundle's
	 * classloader is not used to search for the specified entry. Only the
	 * contents of the bundle is searched for the specified entry. A specified
	 * path of &quot;/&quot; indicates the root of the bundle.
	 * 
	 * <p>
	 * This method returns a URL to the specified entry, or <tt>null</tt> if
	 * the entry could not be found or if the caller does not have the
	 * <tt>AdminPermission</tt> and the Java Runtime Environment supports
	 * permissions.
	 * 
	 * @param name The name of the entry. See
	 *        <tt>java.lang.ClassLoader.getResource</tt> for a description of
	 *        the format of a resource name.
	 * @return A URL to the specified entry, or <tt>null</tt> if the entry
	 *         could not be found or if the caller does not have the
	 *         <tt>AdminPermission</tt> and the Java Runtime Environment
	 *         supports permissions.
	 * 
	 * @exception java.lang.IllegalStateException If this bundle has been
	 *            uninstalled.
	 * @since 1.3
	 */
	public URL getEntry(String name);

	/**
	 * Returns the time when this bundle was last modified. A bundle is
	 * considered to be modified when it is installed, updated or uninstalled.
	 * 
	 * <p>
	 * The time value is the number of milliseconds since January 1, 1970,
	 * 00:00:00 GMT.
	 * 
	 * @return The time when this bundle was last modified.
	 * @since 1.3
	 */
	public long getLastModified();
}