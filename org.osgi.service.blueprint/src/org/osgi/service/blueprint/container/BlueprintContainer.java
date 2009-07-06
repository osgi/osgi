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

import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ReferenceListMetadata;
import org.osgi.service.blueprint.reflect.ReferenceMetadata;
import org.osgi.service.blueprint.reflect.ServiceMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;

/**
 * A Blueprint Container represents the managed state of a Blueprint bundle.
 * 
 * A Blueprint Container provides access to all managers. These are the beans,
 * services, and service references. Only bundles in the <code>ACTIVE</code>
 * state (and also the <code>STARTING</code> state for bundles awaiting lazy
 * activation) can have an associated Blueprint Container. A given Bundle
 * Context has at most one associated Blueprint Container.
 * 
 * A Blueprint Container can be obtained by injecting the predefined
 * &quot;blueprintContainer&quot; component id. The Blueprint Container is also
 * registered as a service and its managers can be queried.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface BlueprintContainer {
	/**
	 * Returns the set of component ids managed by this Blueprint Container.
	 * 
	 * @return An immutable Set of Strings, containing the ids of all of the
	 *         components managed within this Blueprint Container.
	 */
	Set/* <String> */getComponentIds();

	/**
	 * Return the component instance for the specified component id.
	 * 
	 * If the manager has not yet been activated, calling this operation will
	 * atomically activate it. If the manager is a singleton manager, the
	 * activation will cause the component instance to be created and
	 * initialized. If the component has a prototype scope then each call to
	 * this method will return a new component instance.
	 * 
	 * @param id
	 *            The id for the requested manager.
	 * @return The component instance for the specified id
	 * @throws NoSuchComponentException
	 *             If the specified id is not managed by this Blueprint
	 *             Container.
	 */
	Object getComponentInstance(String id);

	/**
	 * Return the Component Metadata instance for the manager with the
	 * specified id.
	 * 
	 * @param id
	 *            The id of the requested Component Metadata.
	 * @return The Component Metadata for the given id.
	 * @throws NoSuchComponentException
	 *             If the specified id is not managed by this
	 *             Blueprint Container.
	 */
	ComponentMetadata getComponentMetadata(String id);

	/**
	 * Return all {@link ComponentMetadata} instances of the specified Component
	 * Metadata type. The supported Component Metadata types are
	 * {@link ComponentMetadata} (which returns the Component Metadata for all
	 * defined manager types), {@link BeanMetadata} ,
	 * {@link ServiceReferenceMetadata} (which returns both
	 * {@link ReferenceMetadata} and {@link ReferenceListMetadata} instances),
	 * and {@link ServiceMetadata}. The collection will include all Component
	 * Metadata instances of the requested type, including components that are
	 * declared inline.
	 * 
	 * @param type
	 *            The super type or type of the requested Component Metadata.
	 * @return An immutable collection of Component Metadata objects of the
	 *         specified type.
	 */
	/* <T extends ComponentMetadata> */Collection/* <T> */getMetadata(
			Class/* <T> */type);
}
