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
 * The lifecycle of constituent bundles are tied to the lifecycle of composite 
 * bundles. See {@link #start}
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
	 * When the composite bundles state is set to {@link Bundle#ACTIVE} state 
	 * the following steps are required to activate the constiuents:
	 * <ol>
	 * <li>The composite framework start-level is set to the beginning start-level
	 * and the {@link FrameworkEvent#STARTED} event is fired.  The constituent
	 * bundles are started according to the start-level specification.</li>
	 * <li>The bundle event of type {@link BundleEvent#STARTED} is fired for the 
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
	public void update(InputStream input);

	public void update(Map compositeManifest);

	public Class loadClass(String name) throws ClassNotFoundException;
	public URL getResource(String name);
	public Enumeration/* <URL> */getResources(String name) throws IOException;
	public URL getEntry(String path);
	public Enumeration/* <String> */getEntryPaths(String path);
	public Enumeration/* <URL> */findEntries(String path, String filePattern,
			boolean recurse);
}
