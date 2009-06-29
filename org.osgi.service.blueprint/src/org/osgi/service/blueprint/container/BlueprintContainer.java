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
package org.osgi.service.blueprint.container;

import java.util.Collection;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.reflect.ComponentMetadata;

/**
 * Blueprint Container providing access to the components, exported services,
 * and service references of a bundle using Blueprint services. Only bundles in
 * the <code>ACTIVE</code> (or also <code>STARTING</code> for bundles with a
 * lazy activation policy) state may have an associated Blueprint Container. A
 * given Bundle Context has at most one associated Blueprint Container.
 *
 * A Blueprint Container may be obtained by injecting the predefined
 * "blueprintContainer" component instance. Alternatively you can look up a
 * Blueprint Container services in the service registry. The
 * {@link org.osgi.framework.Constants#BUNDLE_SYMBOLICNAME} and
 * {@link org.osgi.framework.Constants#BUNDLE_VERSION} service properties can be
 * used to determine which bundle the published Blueprint Container service is
 * associated with.
 *
 * A Blueprint Container implementation must support safe concurrent access. It
 * is legal for the set of named components and component Metadata to change
 * between invocations on the same thread if another thread is concurrently
 * modifying the same mutable Blueprint Container implementation object.
 *
 * @see org.osgi.framework.Constants
 */
public interface BlueprintContainer {
	/**
	 * The set of component names recognized by the blueprint context.
	 *
	 * @return an immutable set (of Strings) containing the names of all of the
	 *         components within the context.
	 */
	Set getComponentIds();

	/**
	 * Get the component instance for a given named component. If the component
	 * has not yet been instantiated, calling this operation will cause the
	 * component instance to be created and initialized. If the component has a
	 * prototype scope then each call to getComponentInstance will return a new
	 * component instance. If the component has a bundle scope then the
	 * component instance returned will be the instance for the caller's bundle
	 * (and that instance will be instantiated if it has not already been
	 * created).
	 *
	 * Note: calling getComponent from logic executing during the instantiation
	 * and configuration of a component, before the init method (if specified)
	 * has returned, may trigger a circular dependency (for a trivial example,
	 * consider a component that looks itself up by name during its init
	 * method). Implementations of the Blueprint Service are not required to
	 * support cycles in the dependency graph and may throw an exception if a
	 * cycle is detected. Implementations that can support certain kinds of
	 * cycles are free to do so.
	 *
	 * @param id
	 *            the name of the component for which the instance is to be
	 *            retrieved.
	 *
	 * @return the component instance, the type of the returned object is
	 *         dependent on the component definition, and may be determined by
	 *         introspecting the component Metadata.
	 *
	 * @throws NoSuchComponentException
	 *             if the name specified is not the name of a component within
	 *             the context.
	 */
	Object getComponentInstance(String id);

	/**
	 * Get the component Metadata for a given named component.
	 *
	 * @param id
	 *            the name of the component for which the Metadata is to be
	 *            retrieved.
	 *
	 * @return the component Metadata for the component.
	 *
	 * @throws NoSuchComponentException
	 *             if the name specified is not the name of a component within
	 *             the context.
	 */
	ComponentMetadata getComponentMetadata(String id);

	/**
	 * Returns all ComponentMetadata instances of the given type. The supported
	 * Metadata types are ComponentMetadata (which returns the Metadata for all
	 * defined component types), BeanMetadata, ServiceReferenceMetadata (which
	 * returns both ReferenceMetadata and ReferenceListMetadata instances),
	 * ReferenceMetadata, ReferenceListMetadata, and ServiceMetadata. The collection
	 * will include all Metadata instances of the requested type, including
	 * components that are declared as inline values.
	 *
	 * @param type
	 *
	 * @return an immutable collection of ComponentMetadata objects of the
	 *         matching type.
	 */
	/* <T extends ComponentMetadata> */Collection/* <T> */getMetadata(
			Class/* <T> */type);

	/**
	 * Get the bundle context of the bundle this blueprint context is associated
	 * with.
	 *
	 * @return the blueprint context's bundle context
	 */
	BundleContext getBundleContext();
}
