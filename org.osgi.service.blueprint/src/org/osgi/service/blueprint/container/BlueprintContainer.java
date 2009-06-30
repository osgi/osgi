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

import java.util.*;

import org.osgi.service.blueprint.reflect.*;

/**
 * Blueprint Container provides access to all the managers. These are the beans,
 * registered services, and service references. Only bundles in the
 * <code>ACTIVE</code> (or also <code>STARTING</code> for bundles with a
 * lazy activation policy) state can have an associated Blueprint Container. A
 * given Bundle Context has at most one associated Blueprint Container.
 * 
 * A Blueprint Container can be obtained by injecting the predefined
 * "blueprintContainer" environment manager. Alternatively the Blueprint
 * Container is registered as a service and its managers can be looked up.
 * 
 * @see org.osgi.framework.Constants
 * 
 * @ThreadSafe
 */
public interface BlueprintContainer {
	/**
	 * The set of manager ids recognized by the Blueprint Container.
	 * 
	 * @return an immutable Set of Strings, containing the ids of all of the
	 *         managers within the Blueprint Container.
	 */
	Set getComponentIds();

	/**
	 * Get the component instance for a given named component. If the manager
	 * has not yet been activated, calling this operation will cause the
	 * component instance to be created and initialized. If the component has a
	 * prototype scope then each call to the <code>getComponentInstance</code>
	 * method will return a new component instance.
	 * 
	 * Calling {link #getComponentInstance} from logic executing during the
	 * construction of a component, before the <code>initMethod</code> (if
	 * specified) has returned, may trigger a circular dependency.
	 * 
	 * @param id
	 *            The id of the manager for which the instance is to be
	 *            provided.
	 * 
	 * @return The component instance, the type of the returned object is
	 *         dependent on the manager, and may be determined with the
	 *         Metadata.
	 * 
	 * @throws NoSuchComponentException
	 *             If the id specified is not the id of a manager within the
	 *             context.
	 */
	Object getComponentInstance(String id);

	/**
	 * Get the Metadata for a given manager id.
	 * 
	 * @param id
	 *            the id of the manager for which the Metadata is to be
	 *            retrieved.
	 * 
	 * @return the Metadata for the manager with the given id.
	 * 
	 * @throws NoSuchComponentException
	 *             if the id specified is not the id of a manager within the
	 *             Blueprint Container.
	 */
	ComponentMetadata getComponentMetadata(String id);

	/**
	 * Returns all {@link ComponentMetadata} instances of the given type. The
	 * supported Metadata types are {@link ComponentMetadata} (which returns the
	 * Metadata for all defined manager types), {@link BeanMetadata},
	 * {@link ServiceReferenceMetadata} (which returns both
	 * {@link ReferenceMetadata} and {@link ReferenceListMetadata} instances),
	 * and {@link ServiceMetadata}. The collection will include all Metadata
	 * instances of the requested type, including components that are declared
	 * as inline values.
	 * 
	 * @param type The super type or type of all returned Metadata 
	 * 
	 * @return An immutable collection of {@link ComponentMetadata} objects of the
	 *         matching type.
	 */
	/* <T extends ComponentMetadata> */Collection/* <T> */getMetadata(
			Class/* <T> */type);
}
