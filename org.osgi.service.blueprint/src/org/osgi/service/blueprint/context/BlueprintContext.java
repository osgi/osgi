/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.service.blueprint.context;

import java.util.Collection;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.reflect.ComponentMetadata;

/**
 * BlueprintContext providing access to the components, service exports, and
 * service references of a bundle using blueprint services. Only bundles in the
 * ACTIVE state may have an associated BlueprintContext. A given BundleContext
 * has at most one associated BlueprintContext.
 *
 * An instance of BlueprintContext may be obtained from within a blueprint context by
 * injecting the predefined "blueprintContext" component.
 * Alternatively you can look up BlueprintContext services in the service registry.
 * The Constants.BUNDLE_SYMBOLICNAME and Constants.BUNDLE_VERSION service
 * properties can be used to determine which bundle the published BlueprintContext
 * service is associated with.
 *
 * A BlueprintContext implementation must support safe concurrent access. It is
 * legal for the set of named components and component metadata to change
 * between invocations on the same thread if another thread is concurrently
 * modifying the same mutable BlueprintContext implementation object.
 *
 * @see org.osgi.framework.Constants
 *
 */
public interface BlueprintContext {

	/**
	 * reason code for destroy method callback of a managed service factory
	 * created component, when the component is being disposed because the
	 * corresponding configuration admin object was deleted.
	 */
	static final int CONFIGURATION_ADMIN_OBJECT_DELETED = 1;

	/**
	 * reason code for destroy method callback of a managed service factory
	 * created component, when the component is being disposed because the
	 * bundle is being stopped.
	 */
	static final int BUNDLE_STOPPING = 2;

	/**
	 * The set of component names recognized by the blueprint context.
	 *
	 * @return an immutable set (of Strings) containing the names of all of the components within the
	 * context.
	 */
	Set getComponentNames();

	/**
	 * Get the component instance for a given named component. If the component has
	 * not yet been instantiated, calling this operation will cause the component instance
	 * to be created and initialized. If the component
	 * has a prototype scope then each call to getComponent will return a new
	 * component instance. If the component has a bundle scope then the component
	 * instance returned will be the instance for the caller's bundle (and that
	 * instance will be instantiated if it has not already been created).
	 *
	 * Note: calling getComponent from logic executing during the instantiation and
	 * configuration of a component, before the init method (if specified) has returned,
	 * may trigger a circular dependency (for a trivial example, consider a component
	 * that looks itself up by name during its init method). Implementations of the
	 * Blueprint Service are not required to support cycles in the dependency graph
	 * and may throw an exception if a cycle is detected. Implementations that can
	 * support certain kinds of cycles are free to do so.
	 *
	 * @param name the name of the component for which the instance is to be
	 * retrieved.
	 *
	 * @return the component instance, the type of the returned object is
	 * dependent on the component definition, and may be determined by
	 * introspecting the component metadata.
	 *
	 * @throws NoSuchComponentException if the name specified is not the
	 * name of a component within the context.
	 */
	Object getComponent(String name);

	/**
	 * Get the component metadata for a given named component.
	 *
	 * @param name the name of the component for which the metadata is to be
	 * retrieved.
	 *
	 * @return the component metadata for the component.
	 *
	 * @throws NoSuchComponentException if the name specified is not the
	 * name of a component within the context.
	 */
	ComponentMetadata getComponentMetadata(String name);

	/**
	 * Get the service reference metadata for every OSGi service referenced by
	 * this context.
	 *
	 * @return an immutable collection of ServiceComponentMetadata, with one entry for each referenced service.
	 */
	Collection /*<ServiceReferenceMetadata>*/ getReferencedServicesMetadata();

	/**
	 * Get the service export metadata for every service exported by this
	 * context.
	 *
	 * @return an immutable collection of ServiceMetadata, with one entry for each service export.
	 */
	Collection /*<ServiceMetadata>*/ getExportedServicesMetadata();

	/**
	 * Get the metadata for all components defined locally within this context.
	 *
	 * @return an immutable collection of BeanMetadata, with one entry for each component.
	 */
	Collection /*<BeanMetadata>*/ getBeanComponentsMetadata();

	/**
	 * Get the bundle context of the bundle this blueprint context is associated
	 * with.
	 *
	 * @return the blueprint context's bundle context
	 */
	BundleContext getBundleContext();
}
