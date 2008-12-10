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
 * Composite bundles are composed from other bundles which are installed into a
 * child framework. The Composite bundle is used to share packages and services
 * between a parent and child framework. A parent framework has one composite
 * bundle for each of its child frameworks. A parent framework can have zero or
 * more child composite bundles installed. A child framework must have one and
 * only one parent composite bundle. In other words a parent framework can have
 * many children frameworks but a child framework can only have one parent
 * framework.
 * <p>
 * A child composite bundle does the following as specified by the composite
 * manifest map:
 * <ul>
 * <li>Exports packages to the parent framework from the child framework. These
 * packages are imported by the companion composite bundle from the child
 * framework.</li>
 * <li>Imports packages from the parent framework. These packages are exported
 * by the companion composite bundle to the child framework.</li>
 * <li>Registers services to the parent framework from the child framework.</li>
 * <li>Gets services from the parent framework that are registered to the child
 * framework by the companion composite bundle.</li>
 * </ul>
 * <p>
 * A parent composite bundle does the following as specified by the manifest map:
 * <ul>
 * <li>Exports packages to the child framework from the parent framework.</li>
 * <li>Imports packages from the child framework that are exported to the parent
 * framework by the companion composite bundle.</li>
 * <li>Registers services to the child framework from the parent framework.</li>
 * <li>Gets services from the child framework that are registered to the parent
 * framework by the companion composite bundle.</li>
 * </ul>
 * <p>
 * 
 * A newly created child <code>Framework</code> will be in the
 * {@link Bundle#STARTING STARTING} state. This child <code>Framework</code> can
 * then be used to manage and control the child framework instance. The child
 * framework instance is persistent and uses a storage area in the child
 * composite bundle's data area. The child framework's lifecycle is tied to its
 * child composite bundle's lifecycle in the following ways:
 * <p>
 * <ul>
 * <li>If the child composite bundle is marked to be persistently started (see
 * StartLevel.isBundlePersistentlyStarted(Bundle)) then the child framework
 * instance will automatically be started when the child composite bundle's
 * start-level is met.</li>
 * <li>The child framework instance will be stopped if the child composite
 * bundle is persistently stopped or its start level is no longer met.
 * Performing operations which transiently stop a child composite bundle do not
 * cause the child framework to stop (e.g. {@link Bundle#stop(int)
 * stop(Bundle.STOP_TRANSIENT)}, {@link Bundle#update() update}, refreshPackage
 * etc.).</li>
 * </ul>
 * <p>
 * The child framework may be persistently started and stopped by persistently
 * starting and stopping the child composite bundle, but it is still possible to
 * initialize and start the child framework explicitly while the child composite
 * bundle is not persistently started. This allows for the child framework to be
 * initialized and populated with a set of bundles before starting the composite
 * bundle. The set of bundles installed into the child framework are considered
 * the bundles which compose the child composite bundle.
 * <p>
 * If the child framework is started while the child composite bundle is not
 * persistently started then the child framework lifecycle is tied to its parent
 * framework lifecycle. When the parent <code>Framework</code> enters the
 * {@link Bundle#STOPPING STOPPING} state then all active child frameworks of
 * that parent are shutdown using the to the {@link Framework#stop()} method.
 * The parent <code>Framework</code> must not enter the {@link Bundle#RESOLVED}
 * state until all the child frameworks have completed their shutdown process.
 * After the parent framework has completed the shutdown process then all child
 * framework instances become invalid and must not be allowed to re-initialize
 * or re-start.
 * 
 * @ThreadSafe
 * @version $Revision: 5897 $
 */
public interface CompositeBundle extends Bundle {
	
	/** The child composite bundle type */
	public static final int TYPE_CHILD = 0x01;
	
	/** The parent composite bundle type */
	public static final int TYPE_PARENT = 0x02;

	/**
	 * Returns the companion framework associated with this composite bundle. If
	 * this is a parent composite bundle then the parent framework is returned;
	 * otherwise the child framework is returned.
	 * 
	 * @return the companion framework.
	 */
	Framework getCompanionFramework();

	/**
	 * Returns the companion composite bundle associated with this composite
	 * bundle. The companion bundle is installed in the companion framework.
	 * 
	 * @return the companion bundle.
	 */
	CompositeBundle getCompanionComposite();

	/**
	 * Updates this child composite bundle and its companion parent composite
	 * bundle with the specified manifest.
	 * 
	 * @param compositeManifest
	 *            the new composite manifest.
	 * @throws BundleException
	 *             If the update fails or if this is a parent composite bundle.
	 */
	void update(Map /* <String, String> */compositeManifest) throws BundleException;

	/**
	 * Returns the type of composite bundle this is. A value of
	 * {@link #TYPE_CHILD} is returned if this is a child composite bundle. A
	 * value of {@link #TYPE_PARENT} is returned if this is a parent composite
	 * bundle.
	 * 
	 * @return the type of composite bundle this is.
	 */
	int getCompositeType();

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
	 * Uninstalls this composite bundle and its companion bundle.
	 * <p>
	 * If this composite bundle is a child composite then the companion child
	 * framework is shutdown and its persistent storage area is deleted.
	 */
	void uninstall() throws BundleException;
}
