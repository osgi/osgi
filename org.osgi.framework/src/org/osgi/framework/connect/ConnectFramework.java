/*
 * Copyright (c) OSGi Alliance (2019). All Rights Reserved.
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
package org.osgi.framework.connect;

import java.io.File;
import java.util.Map;
import java.util.Optional;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

/**
 * A connect framework provides access to instances of {@link ConnectModule}
 * that are used by a {@link Framework} instance to provide content and classes
 * for a bundle installed in the Framework. A connect framework is provided when
 * {@link ConnectFrameworkFactory#newFramework(java.util.Map, ConnectFramework)
 * creating} a framework instance. Because a connect framework instance can
 * participate in the initialization of the framework and the life cycle of a
 * framework instance the connect framework instance should only be used with a
 * single framework instance.
 * 
 * @ThreadSafe
 * @author $Id$
 */
public interface ConnectFramework {

	/**
	 * Initializes the connect framework with the
	 * {@link Constants#FRAMEWORK_STORAGE framework persistent storage} file and
	 * framework properties configured for a {@link Framework} instance. This
	 * method is called once by a {@link Framework} instance and is called
	 * before any other methods on this connect framework are called.
	 * 
	 * @param configuration The framework properties to used configure the new
	 *            framework instance. An unmodifiable map of framework
	 *            configuration properties that were used to create a new
	 *            framework instance.
	 * @param storage the persistent storage area used by the {@link Framework}
	 *            or {@code null} if the if the platform does not have file
	 *            system support.
	 * @return a reference to this object
	 */
	ConnectFramework initialize(File storage, Map<String,String> configuration);

	/**
	 * Returns the connect module for the specified bundle location. If an
	 * {@link Optional#empty() empty} optional is returned the the framework
	 * must handle reading the content of the bundle itself. If a value is
	 * {@link Optional#isPresent() present} in the returned optional then the
	 * {@link Optional#get() value} from the optional must be used to access the
	 * content of the bundle.
	 * 
	 * @param location the bundle location used to install a bundle
	 * @return the connect module for the specified bundle location
	 * @throws BundleException if the location cannot be handled
	 */
	Optional<ConnectModule> getModule(String location) throws BundleException;

	/**
	 * Creates a new activator for this connect framework. A new activator is
	 * created by the framework each time the framework is
	 * {@link Framework#init() initialized}. An activator allows the connect
	 * framework to participate in the framework life cyle. When the framework
	 * is {@link Framework#init() initialized} the activator
	 * {@link BundleActivator#start(org.osgi.framework.BundleContext) start}
	 * method is called. When the framework is {@link Framework#stop() stopped}
	 * the activator
	 * {@link BundleActivator#stop(org.osgi.framework.BundleContext) stop}
	 * method is called
	 * 
	 * @return a new activator for this connect framework or
	 *         {@link Optional#empty() empty} if no activator is available
	 */
	Optional<BundleActivator> createBundleActivator();
}