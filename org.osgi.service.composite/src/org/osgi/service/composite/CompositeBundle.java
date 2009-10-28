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

package org.osgi.service.composite;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;

/**
 * A composite bundle is a bundle for which the content is composed of meta-data 
 * describing the composite and a set of bundles called constituent bundles.
 * Constituent bundles are isolated from other bundles which are not 
 * constituents of the same composite bundle.  Composites provide isolation
 * for constituent bundles in the following ways.
 * <ul>
 * <li>Class space: Constraints specified by constituent bundles (e.g.
 *     Import-Package, Require-Bundle, Fragment-Host) can only be resolved
 *     against capabilities provided by other constituents within the same 
 *     composite bundle (Export-Package, Bundle-SymbolicName/Bundle-Version).</li>
 * <li>Service Registry: Constituent bundles only have access to services 
 *     registered by other constituents within the same composite bundle.  
 *     This includes service events and service references.</li>
 * <li>Bundles: Constituent bundles only have access to other Bundle objects 
 *     that represent constituents within the same composite bundle.  This 
 *     includes bundle events and core API like BundleContext.getBundles, 
 *     PackageAdmin and StartLevel.</li>
 * </ul>
 * <p>
 * A parent framework refers to the OSGi framework where a composite bundle 
 * is installed.  A composite framework refers the framework where the 
 * constituent bundles are installed and provides the isolation for the
 * constituent bundles.  Constituent bundles are isolated
 * but there are many cases where capabilities (packages and services) need 
 * to be shared between bundles installed in the parent framework and the 
 * constituents within a composite bundle.
 * </p>
 * <p>
 * Through a sharing policy the composite is in control of what capabilities
 * are shared across framework boundaries.  The sharing policy controls what
 * capabilities provided by bundles installed in the parent framework are 
 * imported into the composite and made available to constituent bundles.
 * The sharing policy also controls what capabilities provided by 
 * constituent bundles are exported out of the composite and made available
 * to bundles installed in the parent framework.
 * </p>
 * <p>
 * The lifecycle of the constituent bundles are tied to the lifecycle of 
 * the composite bundle. See {@link #start(int)}, {@link #stop(int)}, 
 * {@link #update(Map)} and {@link #uninstall}.
 * </p>
 * 
 * @ThreadSafe
 * @version $Revision$
 * @deprecated This is proposed API. As a result, this API may never be
 *             published or the final API may change substantially by the time
 *             of final publication. You are cautioned against relying upon this
 *             API.
 */
public interface CompositeBundle extends Bundle {
	/**
	 * Returns the system bundle context for the composite framework.  This 
	 * method must return a valid {@link BundleContext} as long as this 
	 * composite bundle is installed.  Once a composite bundle is 
	 * uninstalled this method must return <code>null</code>.  The composite system 
	 * bundle context can be used to install and manage the constituent bundles.
	 * @return the system bundle context for the composite framework
	 * @throws SecurityException If the caller does not have the
	 *         appropriate <code>AdminPermission[system.bundle,CONTEXT]</code>, and
	 *         the Java Runtime Environment supports permissions.
	 */
	public BundleContext getSystemBundleContext();
	
	/**
	 * Starts this composite with no options.
	 * 
	 * <p>
	 * This method performs the same function as calling <code>start(0)</code>.
	 * 
	 * @throws BundleException If this composite could not be started.
	 * @throws IllegalStateException If this composite has been uninstalled.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>AdminPermission[this,EXECUTE]</code>, and the Java Runtime
	 *         Environment supports permissions.
	 * @see #start(int)
	 */
	public void start() throws BundleException;

	/**
	 * Starts this composite according to {@link Bundle#start(int)}.
	 * When the composite bundle's state is set to {@link Bundle#STARTING} and
	 * after the {@link BundleEvent#STARTING} event has been fired for this 
	 * composite bundle, the following steps are required to activate the 
	 * constituents:
	 * <ol>
	 * <li>The composite framework start-level is set to the beginning start-level.</li>
	 * <li>The constituent bundles are started according to the start-level 
	 * specification.
	 * <li>The {@link FrameworkEvent#STARTED} event is fired for the
	 * composite framework.</li>
	 * <li>The composite bundle state is set to {@link Bundle#ACTIVE}</li>
	 * <li>The bundle event of type {@link BundleEvent#STARTED} is fired for this 
	 * composite.</li>
	 * </ol>
	 * @param options The options for starting this composite. See
	 *        {@link #START_TRANSIENT} and {@link #START_ACTIVATION_POLICY}. The
	 *        Framework must ignore unrecognized options.
	 * @throws BundleException If this composite could not be started.
	 * @throws IllegalStateException If this composite has been uninstalled.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>AdminPermission[this,EXECUTE]</code>, and the Java Runtime
	 *         Environment supports permissions.
	 */
	public void start(int options) throws BundleException;

	/**
	 * Stops this composite with no options.
	 * 
	 * <p>
	 * This method performs the same function as calling <code>stop(0)</code>.
	 * 
	 * @throws BundleException If an error occurred while stopping this composite
	 * @throws IllegalStateException If this composite has been uninstalled.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>AdminPermission[this,EXECUTE]</code>, and the Java Runtime
	 *         Environment supports permissions.
	 * @see #start(int)
	 */
	public void stop() throws BundleException;

	/**
	 * Stops this composite according to {@link Bundle#stop(int)}.
	 * When the composite bundle's state is set to {@link Bundle#STOPPING}, 
	 * the following steps are required to stop the constituents:
	 * <ol>
	 * <li>The composite framework start-level is set to the 0.</li>
	 * <li>The constituent bundles are stopped according to the start-level 
	 * specification.</li>
	 * <li>The bundle event of type {@link BundleEvent#STARTED} is fired for the 
	 * composite.</li>
	 * </ol>
	 * @param options The options for stopping this bundle. See
	 *        {@link #STOP_TRANSIENT}. The Framework must ignore unrecognized
	 *        options.
	 * @throws BundleException If an error occurred while stopping this composite
	 * @throws IllegalStateException If this composite has been uninstalled.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>AdminPermission[this,EXECUTE]</code>, and the Java Runtime
	 *         Environment supports permissions.
	 * @since 1.4
	 */
	public void stop(int options) throws BundleException;

	/**
	 * Uninstalls this composite according to {@link Bundle#uninstall()}.
	 * When the composite bundle is uninstalled the following steps are 
	 * required:
	 * <ol>
	 * <li>After a composite's state is set to {@linkplain Bundle#INSTALLED}
	 * and before a composite's state is set to {@linkplain Bundle#UNINSTALLED},
	 * all the constituent bundles are uninstalled.</li>
	 * <li>When a composite's state is set to {@linkplain Bundle#UNINSTALLED},
	 * the composite system bundle context becomes invalid.
	 * </ol>
	 */
	public void uninstall() throws BundleException;
	/**
	 * This operation is not supported for composite bundles. A
	 * <code>BundleException</code> of type
	 * {@link BundleException#INVALID_OPERATION invalid operation} must be
	 * thrown.
	 */
	public void update() throws BundleException;

	/**
	 * This operation is not supported for composite bundles. A
	 * <code>BundleException</code> of type
	 * {@link BundleException#INVALID_OPERATION invalid operation} must be
	 * thrown.
	 */
	public void update(InputStream input) throws BundleException;

	/**
	 * Updates the meta-data for this composite from a <code>Map</code>.
	 * 
	 * <p>
	 * The specified <code>Map</code> must not be <code>null</code>.
	 * 
	 * <p>
	 * If this composite's state is <code>ACTIVE</code>, it must be stopped before
	 * the update and started after the update successfully completes.
	 * 
	 * <p>
	 * If this composite has a sharing policy for packages, any packages that are 
	 * imported by other bundles through the current sharing policy must remain exported 
	 * for existing importers.  A call <code>PackageAdmin.refreshPackages</code> method 
	 * will force all constituent bundles for this composite to be refreshed, causing the 
	 * new package sharing policy to be used for all constituent bundles.
	 * <p>
	 * The following steps are required to update a composite:
	 * <ol>
	 * <li>If this composite's state is <code>UNINSTALLED</code> then an
	 * <code>IllegalStateException</code> is thrown.
	 * <li>The component manifest <code>Map</code> is verified.  If this fails, 
	 * a {@linkplain BundleException} is thrown.
	 * 
	 * <li>If this composite's state is <code>ACTIVE</code>, <code>STARTING</code>
	 * or <code>STOPPING</code>, this bundle is stopped as described in the
	 * <code>CompositeBundle.stop</code> method. If <code>CompositeBundle.stop</code>
	 * throws an exception, the exception is rethrown terminating the update.
	 * 
	 * <li>The meta-data of this composite is updated from the <code>Map</code>.
	 * 
	 * <li>This composite's state is set to <code>INSTALLED</code>.
	 * 
	 * <li>If the updated version of this composite was successfully installed, a
	 * bundle event of type {@link BundleEvent#UPDATED} is fired.
	 * 
	 * <li>If this composite's state was originally <code>ACTIVE</code>, the
	 * updated composite is started as described in the <code>CompositeBundle.start</code>
	 * method. If <code>CompositeBundle.start</code> throws an exception, a Framework
	 * event of type {@link FrameworkEvent#ERROR} is fired containing the
	 * exception.
	 * </ol>
	 * 
	 * <b>Preconditions </b>
	 * <ul>
	 * <li><code>getState()</code> not in &#x007B; <code>UNINSTALLED</code>
	 * &#x007D;.
	 * </ul>
	 * <b>Postconditions, no exceptions thrown </b>
	 * <ul>
	 * <li><code>getState()</code> in &#x007B; <code>INSTALLED</code>,
	 * <code>RESOLVED</code>, <code>ACTIVE</code> &#x007D;.
	 * <li>This composite has been updated.
	 * </ul>
	 * <b>Postconditions, when an exception is thrown </b>
	 * <ul>
	 * <li><code>getState()</code> in &#x007B; <code>INSTALLED</code>,
	 * <code>RESOLVED</code>, <code>ACTIVE</code> &#x007D;.
	 * <li>Original composite is still used; no update occurred.
	 * </ul>
	 * 
	 * @param compositeManifest The <code>Map</code> from which to read the new
	 *        composite meta-data from.  Must not be <code>null</code>.
	 * @throws BundleException If the the update fails.
	 * @throws IllegalStateException If this composite has been uninstalled.
	 * @throws SecurityException If the caller does not have
	 *         <code>AllPermission</code>.
	 * @see #stop()
	 * @see #start()
	 */
	public void update(Map<String, String> compositeManifest)
			throws BundleException;

	/**
	 * Composite bundles do not have class content.  This method must 
	 * throw a {@link ClassNotFoundException}
	 * @throws ClassNotFoundException Always thrown for composite bundles.
	 */
	public Class< ? > loadClass(String name) throws ClassNotFoundException;
	/**
	 * Composite bundles do not have content.  This method must return
	 * <code>null</code>.
	 * @return A <code>null</code> value is always returned for composites.
	 */
	public URL getResource(String name);
	/**
	 * Composite bundles do not have content.  This method must return
	 * <code>null</code>.
	 * @return A <code>null</code> value is always returned for composites.
	 */
	public Enumeration<URL> getResources(String name) throws IOException;
	/**
	 * Composite bundles do not have content.  This method must return
	 * <code>null</code>.
	 * @return A <code>null</code> value is always returned for composites.
	 */
	public URL getEntry(String path);
	/**
	 * Composite bundles do not have content.  This method must return
	 * <code>null</code>.
	 * @return A <code>null</code> value is always returned for composites.
	 */
	public Enumeration<String> getEntryPaths(String path);
	/**
	 * Composite bundles do not have content.  This method must return
	 * <code>null</code>.
	 * @return A <code>null</code> value is always returned for composites.
	 */
	public Enumeration<URL> findEntries(String path, String filePattern,
			boolean recurse);
}
