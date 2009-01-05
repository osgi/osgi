/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
package org.osgi.service.framework;

import java.io.InputStream;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;

/**
 * Composite bundles are composed of other bundles. The component bundles which
 * make up the content of a composite bundle are installed into a child
 * framework. Like a normal bundle, a composite bundle may import packages and
 * use services from other bundles which are installed in the same framework as
 * the composite bundle. The packages imported and the services used by a
 * composite bundle are shared with the components of a composite bundle through
 * a surrogate bundle installed in the child framework. Also like a normal
 * bundle, a composite bundle may export packages and register services which
 * can be used by bundles installed in the same framework as the composite
 * bundle. The packages exported and the services registered by a composite
 * bundle are acquired from the components of a composite bundle by the
 * surrogate bundle installed in the child framework
 * <p>
 * A framework has one composite bundle for each of its child frameworks. A
 * framework can have zero or more composite bundles installed. A child
 * framework must have one and only one surrogate bundle which represents the
 * composite bundle in the parent framework. In other words, a parent framework
 * can have many child frameworks but a child framework can have only one
 * parent.
 * <p>
 * A composite bundle does the following as specified by the composite manifest
 * map:
 * <ul>
 * <li>Exports packages to the parent framework from the child framework. These
 * packages are imported by the surrogate bundle installed in the child
 * framework.</li>
 * <li>Imports packages from the parent framework. These packages are exported
 * by the surrogate bundle installed in the child framework.</li>
 * <li>Registers services to the parent framework from the child framework.
 * These services are acquired by the surrogate bundle installed in the child
 * framework.</li>
 * <li>Acquires services from the parent framework. These services are
 * registered by the surrogate bundle installed in the child framework.</li>
 * </ul>
 * 
 * A newly created child <code>Framework</code> will be in the
 * {@link Bundle#STARTING STARTING} state. This child <code>Framework</code> can
 * then be used to manage and control the child framework instance. The child
 * framework instance is persistent and uses a storage area associated with the
 * installed composite bundle. The child framework's lifecycle is tied to its
 * composite bundle's lifecycle in the following ways:
 * <p>
 * <ul>
 * <li>If the composite bundle is marked to be persistently started (see
 * StartLevel.isBundlePersistentlyStarted(Bundle)) then the child framework
 * instance will automatically be started when the composite bundle's
 * start-level is met.</li>
 * <li>The child framework instance will be stopped if the composite bundle is
 * persistently stopped or its start level is no longer met. Performing
 * operations which transiently stop a composite bundle do not cause the child
 * framework to stop (e.g. {@link Bundle#stop(int) stop(Bundle.STOP_TRANSIENT)},
 * {@link Bundle#update() update}, refreshPackages etc.).</li>
 * <li>If the composite bundle is uninstalled, the child framework's persistent
 * storage area is also uninstalled.</li>
 * </ul>
 * <p>
 * The child framework may be persistently started and stopped by persistently
 * starting and stopping the composite bundle, but it is still possible to
 * initialize and start the child framework explicitly while the composite
 * bundle is not persistently started. This allows for the child framework to be
 * initialized and populated with a set of bundles before starting the composite
 * bundle. The set of bundles installed into the child framework are the
 * component bundles which comprise the composite bundle.
 * <p>
 * The child framework's lifecycle is also tied to the lifecycle of its
 * parent framework. When the parent <code>Framework</code> enters the
 * {@link Bundle#STOPPING STOPPING} state, all active child frameworks of that
 * parent are shutdown using the {@link Framework#stop()} method. The parent
 * framework must not enter the {@link Bundle#RESOLVED} state until all the
 * child frameworks have completed their shutdown process. Just as with other
 * Bundles, references to child frameworks (or the associated composite and
 * surrogate bundles) become invalid after the parent framework has completed
 * the shutdown process, and must not be allowed to re-initialize or re-start
 * the child framework.
 * 
 * @see SurrogateBundle
 * @ThreadSafe
 * @version $Revision: 5897 $
 */
public interface CompositeBundle extends Bundle {
	/**
	 * Returns the child framework associated with this composite bundle.
	 * 
	 * @return the child framework.
	 */
	Framework getCompositeFramework();

	/**
	 * Returns the surrogate bundle associated with this composite bundle. The
	 * surrogate bundle is installed in the child framework.
	 * 
	 * @return the surrogate bundle.
	 */
	SurrogateBundle getSurrogateBundle();

	/**
	 * Updates this composite bundle with the specified manifest.
	 * 
	 * @param compositeManifest the new composite manifest.
	 * @throws BundleException If the update fails.
	 * @see CompositeBundleFactory#installCompositeBundle(Map, String, Map)
	 */
	void update(Map /* <String, String> */compositeManifest)
			throws BundleException;

	/**
	 * This operation is not supported for composite bundles. A
	 * <code>BundleException</code> of type
	 * {@link BundleException#INVALID_OPERATION invalid operation} must be
	 * thrown.
	 */
	void update() throws BundleException;

	/**
	 * This operation is not supported for composite bundles. A
	 * <code>BundleException</code> of type
	 * {@link BundleException#INVALID_OPERATION invalid operation} must be
	 * thrown.
	 */
	void update(InputStream input) throws BundleException;

	/**
	 * Uninstalls this composite bundle. The associated child framework
	 * is shutdown, and its persistent storage area is deleted.
	 */
	void uninstall() throws BundleException;
}
