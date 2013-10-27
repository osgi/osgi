/*
 * Copyright (c) OSGi Alliance (2004, 2013). All Rights Reserved.
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

package org.osgi.service.component.runtime;

import java.util.Collection;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;

/**
 * The {@code ServiceComponentRuntime} service represents the Declarative
 * Services main controller also known as the Service Component Runtime or SCR
 * for short. It provides access to the components managed by the Service
 * Component Runtime.
 * <p>
 * This service differentiates between {@link ComponentDescription} and
 * {@link ComponentConfiguration}. A {@link ComponentDescription} is the
 * declaration of the component in the Declarative Services descriptor. A
 * {@link ComponentConfiguration} is an actual instance of a declared
 * {@link ComponentDescription} and is backed by an object instance of the
 * {@linkplain ComponentDescription#implementationClass implementation class
 * name declared in the component}.
 * <p>
 * Access to this service requires the
 * {@code ServicePermission[org.osgi.service.component.ServiceComponentRuntime, GET]}
 * permission. It is intended that only administrative bundles should be granted
 * this permission to limit access to the potentially intrusive methods provided
 * by this service.
 * 
 * @ThreadSafe
 * @noimplement
 * @since 1.3
 * @version $Id$
 */
@ProviderType
public interface ServiceComponentRuntime {

    /**
	 * Returns the component descriptions declared by the given bundles or the
	 * component descriptions declared by all active bundles if {@code bundles}
	 * is {@code null}. If the bundles have no declared components or the
	 * bundles are not active an empty collection is returned.
	 * 
	 * @param bundles The bundles whose declared components are to be returned
	 *        or {@code null} if the declared components from all active bundles
	 *        are to be returned.
	 * @return The declared component descriptions of the given (active)
	 *         {@code bundles} or the declared component descriptions from all
	 *         active bundles if {@code bundle} is <code>null</code>. An empty
	 *         collection is returned if no components are declared by active
	 *         bundles.
	 */
	Collection<ComponentDescription> getComponentDescriptions(Bundle... bundles);

    /**
	 * Return the {@link ComponentDescription} declared with the given
	 * {@code name} or {@code null} if no such component is declared by the
	 * given {@code bundle} or the bundle is not active.
	 * 
	 * @param bundle The bundle declaring the requested component
	 * @param name The name of the {@link ComponentDescription} to return
	 * @return The named component or {@code null} if none of the active bundles
	 *         declare a component with that name.
	 * @throws NullPointerException if {@code name} or {@code bundle} is
	 *         {@code null}.
	 */
	ComponentDescription getComponentDescription(Bundle bundle, String name);

    /**
	 * Return a collection of {@linkplain ComponentConfiguration component
	 * configurations} created for the component description. If there are no
	 * component configurations currently created, the collection is empty. This
	 * collection of configurations represents a snapshot of the current state.
	 * 
	 * @param description The component description.
	 * @return The component configurations created for the given component
	 *         description. An empty collection is returned if there are non.
	 * @throws NullPointerException if {@code description} or {@code null}.
	 */
	Collection<ComponentConfiguration> getComponentConfigurations(ComponentDescription description);

    /**
	 * Whether this component is currently enabled ({@code true}) or not.
	 * <p>
	 * Initially this follows the {@code Component.enabled} attribute of the
	 * declaration and can be changed by the
	 * {@link ServiceComponentRuntime#enableComponent(ComponentDescription)}
	 * method,
	 * {@link ServiceComponentRuntime#disableComponent(ComponentDescription)}
	 * method, {@link ComponentContext#disableComponent(String)} or
	 * {@link ComponentContext#enableComponent(String)}.
	 * 
	 * @param description The component description.
	 * @return Whether this component is currently enabled or not.
	 * @throws NullPointerException if {@code description} or {@code null}.
	 * @see ComponentDescription#defaultEnabled
	 */
	boolean isComponentEnabled(ComponentDescription description);

	/**
	 * Enables this ComponentDescription if it is disabled. If the
	 * ComponentDescription is not currently disabled this method has no effect.
	 * 
	 * @param description The component description to enable.
	 * @throws NullPointerException if {@code description} or is {@code null}.
	 */
	void enableComponent(ComponentDescription description);

    /**
	 * Disables this ComponentDescription if it is enabled. If the
	 * ComponentDescription is currently disabled this method has no effect.
	 * 
	 * @param description The component description to disable.
	 * @throws NullPointerException if {@code description} or is {@code null}.
	 */
	void disableComponent(ComponentDescription description);
}
