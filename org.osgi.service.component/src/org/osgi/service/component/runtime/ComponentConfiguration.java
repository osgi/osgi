/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

import java.util.Map;
import org.osgi.dto.DTO;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

/**
 * The {@code ComponentConfiguration} interface represents an actual instance of
 * a declared {@link ComponentDescription}. These instances are called
 * <i>configurations</i> in the Declarative Services specification hence the
 * name.
 * 
 * @since 1.3
 * @NotThreadSafe
 * @author $Id$
 */
public class ComponentConfiguration extends DTO {
	/**
	 * The initial state of a component (value is 1).
	 * <p>
	 * When the component becomes satisfied it enters the
	 * {@linkplain #STATE_REGISTERED activating state} to be activated.
	 */
	public static final int		STATE_UNSATISFIED	= 1;

	/**
	 * The state of a component when it becomes satisfied (value is 2).
	 * <p>
	 * A component configuration becomes satisfied when the following conditions
	 * are all satisfied:
	 * <ul>
	 * <li>The component is enabled.</li>
	 * <li>If the component description specifies configuration-policy=required,
	 * then a Configuration object for the component is present in the
	 * Configuration Admin service.</li>
	 * <li>Using the component properties of the component configuration, all
	 * the component's references are satisfied. A reference is satisfied when
	 * the reference specifies optional cardinality or there is at least one
	 * target service for the reference.</li>
	 * </ul>
	 * If the component provides a service, an entry is added to the service
	 * registry for the services the component provides.
	 * 
	 * Once any of the listed conditions are no longer true, the component
	 * configuration becomes {@linkplain #STATE_UNSATISFIED unsatisfied} and the
	 * registrations are removed from the service registry.
	 * <p>
	 * Immediate components directly enter the {@linkplain #STATE_ACTIVATING
	 * activating state}. Delayed and factory service components enter the
	 * {@linkplain #STATE_ACTIVATING activating state} when the service is
	 * retrieved from the service factory for the first time.
	 * <p>
	 * If activation fails, the component remains in the
	 * {@linkplain #STATE_REGISTERED registered state}.
	 */
	public static final int		STATE_REGISTERED	= 2;

	/**
	 * A registered component is being activated (value is 4). Depending on the
	 * type of the component this may include the following steps:
	 * <ol>
	 * <li>Create the component instance</li>
	 * <li>Bind available references</li>
	 * <li>Call the activator method (if any)</li>
	 * </ol>
	 * <p>
	 * If activation succeeds the component enters the
	 * {@linkplain #STATE_ACTIVE active state}.
	 * <p>
	 * If activation fails the component falls back to the
	 * {@linkplain #STATE_REGISTERED registered state}.
	 */
	public static final int		STATE_ACTIVATING	= 4;

	/**
	 * A component is in the active state (value is 8). The activate state means
	 * the following depending on the type of component:
	 * <ul>
	 * <li>The component is an immediate component</li>
	 * <li>The component is a delayed or service factory component and at least
	 * one consumer has retrieved the provided service</li>
	 * <li>The component is an instance of a Component Factory component created
	 * with the {@link ComponentFactory#newInstance(java.util.Dictionary)}
	 * method</li>
	 * </ul>
	 * <p>
	 * If the component becomes unsatisfied it is being deactivated and enters
	 * the {@linkplain #STATE_DEACTIVATING deactivating state}.
	 * <p>
	 * If the component is a Component Factory created instance and is disposed
	 * off with the {@link ComponentInstance#dispose()} method it is being
	 * destroyed and also enters the {@linkplain #STATE_DEACTIVATING
	 * deactivating state}.
	 * <p>
	 * If the last consumer of a delayed or service factory component ungets the
	 * provided service, the component instance is destroyed and the component
	 * enters the {@linkplain #STATE_REGISTERED registered state}.
	 */
	public static final int		STATE_ACTIVE		= 8;

	/**
	 * The Component is being deactivated either because it is being disabled or
	 * because a dependency is not satisfied any more (value is 16). After
	 * deactivation the Component enters the {@linkplain #STATE_UNSATISFIED
	 * unsatisfied state}.
	 */
	public static final int		STATE_DEACTIVATING	= 16;

	/**
	 * The declaration of this component configuration.
	 */
	public ComponentDescription	component;

	/**
	 * The current state of this component configuration, which is one of the
	 * {@code STATE_*} constants defined in this interface.
	 */
	public int					state;

	/**
	 * The {@code service.pid} property of the configuration properties provided
	 * by the Configuration Admin service for this component configuration or
	 * {@code null} if no configuration from the Configuration Admin is provided
	 * to this component configuration.
	 */
	public String				configurationPid;

	/**
	 * A map of the actual properties provided to the component configuration.
	 * This map provides the same content as the
	 * {@link org.osgi.service.component.ComponentContext#getProperties()}
	 * method.
	 */
	public Map<String, Object>	properties;

	/**
	 * An array of {@link BoundReference} instances representing the service
	 * references bound to this component configuration. {@code null} is
	 * returned if the component configuration has no bound references.
	 */
	public BoundReference[]		boundReferences;
}
