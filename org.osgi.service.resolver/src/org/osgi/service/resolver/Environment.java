/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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

package org.osgi.service.resolver;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

import org.osgi.framework.Constants;
import org.osgi.framework.resource.Capability;
import org.osgi.framework.resource.Requirement;
import org.osgi.framework.resource.Resource;
import org.osgi.framework.resource.ResourceConstants;
import org.osgi.framework.resource.Wire;
import org.osgi.framework.resource.Wiring;

/**
 * An environment provides options and constraints to the potential solution of
 * a {@link Resolver#resolve(Environment, Collection, Collection)} operation.
 *
 * <p>
 * Environments:
 * <ul>
 * <li>Provide {@link Capability capabilities} that the Resolver can use to
 * satisfy {@link Requirement requirements} via the
 * {@link #findProviders(Requirement)} method</li>
 *
 * <li>Constrain solutions via the {@link #getWirings()} method. A wiring
 * consists of a map of existing {@link Resource resources} to {@link Wire
 * wires}.
 *
 * <li>Filter transitive requirements that are brought in as part of a resolve
 * operation via the {@link #isEffective(Requirement)}.
 * </ul>
 *
 * <p>
 * An environment may be used to provide capabilities via local {@link Resource
 * resources} and/or remote {@link org.osgi.service.repository.Repository
 * repositories}.
 * 
 * <p>
 * A resolver may call the {@link #findProviders(Requirement)},
 * {@link #isEffective(Requirement)} and {@link #getWirings()} method any number
 * of times during a resolve using any thread. Environments may also be shared
 * between several resolvers. As such implementors should ensure that this class
 * is properly synchronized.
 * 
 * @ThreadSafe
 */
public interface Environment {
	/**
	 * Find any capabilities that match the supplied requirement.
	 *
	 * <p>
	 * A resolver should use the iteration order or the returned capability
	 * collection to infer preference in the case where multiple capabilities
	 * match a requirement. Capabilities at the start of the iteration are
	 * implied to be preferred over capabilities at the end.
	 * 
	 * <p>
	 * The set returned by this call should be mutable to support ordering
	 * of {@link Synthesized} resources created by the resolution process.
	 *
	 * <h3>Matching</h3>
	 * <p>
	 * A capability matches a requirement when all of the following are true:
	 * <ul>
	 * <li>The specified capability has the same {@link Capability#getNamespace() name
	 * space} as the requirement.
	 * <li>The filter specified by the {@link Constants#FILTER_DIRECTIVE filter}
	 * directive of the requirement matches the
	 * {@link Capability#getAttributes() attributes of the specified capability}.
	 * <li>The standard capability {@link Capability#getDirectives() directives}
	 * that influence matching and that apply to the name space are satisfied.
	 * See the capability
	 * {@link ResourceConstants#CAPABILITY_MANDATORY_DIRECTIVE mandatory}
	 * directive.
	 * </ul>
	 *
	 * @param requirement the requirement that a resolver is attempting to
	 *        satisfy
	 *
	 * @return an collection of capabilities that match the supplied requirement
	 *
	 * @throws NullPointerException if the requirement is null
	 */
	SortedSet<Capability> findProviders(Requirement requirement);

	/**
	 * Find any capabilities that match the supplied requirement.
	 * 
	 * <p>
	 * The set returned by this call should be mutable to support ordering of
	 * {@link Synthesized} resources created by the resolution process.
	 * 
	 * <p>
	 * See {@link #findProviders} for a discussion on matching.
	 * 
	 * @param requirements the requirements that should be matched
	 * 
	 * @return A map of requirements to capabilities that match the supplied
	 *         requirements
	 * 
	 * @throws NullPointerException if requirements is null
	 * 
	 * 
	 * @see #findProviders
	 */
	Map<Requirement, SortedSet<Capability>> findProviders(Collection<? extends Requirement> requirements);

	/**
	 * Test if a given requirement should be wired in a given resolve operation.
	 * If this method returns false then the resolver should ignore this
	 * requirement during this resolve operation.
	 *
	 * <p>
	 * The primary use case for this is to test the <code>effective</code>
	 * directive on the requirement, though implementations are free to use this
	 * for any other purposes.
	 *
	 * @param requirement the Requirement to test
	 *
	 * @return true if the requirement should be considered as part of this
	 *         resolve operation
	 *
	 * @throws NullPointerException if requirement is null
	 */
	boolean isEffective(Requirement requirement);

	/**
	 * An immutable map of wirings for resources. Multiple calls to this method
	 * for the same environment object must result in the same set of wirings.
	 *
	 * @return the wirings already defined in this environment
	 */
	Map<Resource, Wiring> getWirings();
}
