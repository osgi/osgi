/*
 * Copyright (c) OSGi Alliance (2000, 2008). All Rights Reserved.
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

import java.io.File;

import org.osgi.framework.bundle.BundleActivator;

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
 * <li>Get the {@link Bundle} object for a bundle.
 * <li>Create <code>File</code> objects for files in a persistent storage
 * area provided for the bundle by the Framework.
 * </ul>
 * 
 * <p>
 * A <code>BundleContext</code> object will be created and provided to the
 * bundle associated with this context when it is started using the
 * {@link BundleActivator#start} method. The same <code>BundleContext</code>
 * object will be passed to the bundle associated with this context when it is
 * stopped using the {@link BundleActivator#stop} method. A
 * <code>BundleContext</code> object is generally for the private use of its
 * associated bundle and is not meant to be shared with other bundles in the
 * OSGi environment.
 * 
 * <p>
 * The <code>Bundle</code> object associated with a <code>BundleContext</code>
 * object is called the <em>context bundle</em>.
 * 
 * <p>
 * The <code>BundleContext</code> object is only valid during the execution of
 * its context bundle; that is, during the period from when the context bundle
 * is in the <code>STARTING</code>, <code>STOPPING</code>, and
 * <code>ACTIVE</code> bundle states. If the <code>BundleContext</code>
 * object is used subsequently, an <code>IllegalStateException</code> must be
 * thrown. The <code>BundleContext</code> object must never be reused after
 * its context bundle is stopped.
 * 
 * <p>
 * The Framework is the only entity that can create <code>BundleContext</code>
 * objects and they are only valid within the Framework that created them.
 * 
 * @ThreadSafe
 * @version $Revision$
 */

public interface BundleContext extends ServiceRegistry {
	/**
	 * Returns the value of the specified property. If the key is not found in
	 * the Framework properties, the system properties are then searched. The
	 * method returns <code>null</code> if the property is not found.
	 * 
	 * <p>
	 * The Framework defines the following standard property keys:
	 * </p>
	 * <ul>
	 * <li>{@link FrameworkConstants#FRAMEWORK_VERSION} - The OSGi Framework version.
	 * </li>
	 * <li>{@link FrameworkConstants#FRAMEWORK_VENDOR} - The Framework implementation
	 * vendor.</li>
	 * <li>{@link FrameworkConstants#FRAMEWORK_LANGUAGE} - The language being used. See
	 * ISO 639 for possible values.</li>
	 * <li>{@link FrameworkConstants#FRAMEWORK_OS_NAME} - The host computer operating
	 * system.</li>
	 * <li>{@link FrameworkConstants#FRAMEWORK_OS_VERSION} - The host computer operating
	 * system version number.</li>
	 * <li>{@link FrameworkConstants#FRAMEWORK_PROCESSOR} - The host computer processor
	 * name.</li>
	 * </ul>
	 * <p>
	 * All bundles must have permission to read these properties.
	 * 
	 * <p>
	 * Note: The last four standard properties are used by the
	 * {@link FrameworkConstants#BUNDLE_NATIVECODE} <code>Manifest</code> header's
	 * matching algorithm for selecting native language code.
	 * 
	 * @param key The name of the requested property.
	 * @return The value of the requested property, or <code>null</code> if
	 *         the property is undefined.
	 * @throws java.lang.SecurityException If the caller does not have the
	 *         appropriate <code>PropertyPermission</code> to read the
	 *         property, and the Java Runtime Environment supports permissions.
	 */
	public String getProperty(String key);

	/**
	 * Creates a <code>File</code> object for a file in the persistent storage
	 * area provided for the bundle by the Framework. This method will return
	 * <code>null</code> if the platform does not have file system support.
	 * 
	 * <p>
	 * A <code>File</code> object for the base directory of the persistent
	 * storage area provided for the context bundle by the Framework can be
	 * obtained by calling this method with an empty string as
	 * <code>filename</code>.
	 * 
	 * <p>
	 * If the Java Runtime Environment supports permissions, the Framework will
	 * ensure that the bundle has the <code>java.io.FilePermission</code> with
	 * actions <code>read</code>,<code>write</code>,<code>delete</code>
	 * for all files (recursively) in the persistent storage area provided for
	 * the context bundle.
	 * 
	 * @param filename A relative name to the file to be accessed.
	 * @return A <code>File</code> object that represents the requested file
	 *         or <code>null</code> if the platform does not have file system
	 *         support.
	 * @throws java.lang.IllegalStateException If this BundleContext is no
	 *         longer valid.
	 */
	public File getDataFile(String filename);
	
	/**
	 * Returns the <code>Bundle</code> object associated with this
	 * <code>BundleContext</code>. This bundle is called the context bundle.
	 * 
	 * @return The <code>Bundle</code> object associated with this
	 *         <code>BundleContext</code>.
	 * @throws java.lang.IllegalStateException If this BundleContext is no
	 *         longer valid.
	 */
	public Bundle getBundle();

	public Framework getFramework();
}
