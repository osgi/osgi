/*
 * Copyright (c) OSGi Alliance (2011, 2012). All Rights Reserved.
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
import java.util.List;
import java.util.Map;

import org.osgi.framework.resource.Capability;
import org.osgi.framework.resource.Requirement;
import org.osgi.framework.resource.Resource;
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
 * resources} and/or remote repositories.
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
	 * Find Capabilities that match the given Requirement.
	 * <p>
	 * The returned list contains {@link HostedCapability} objects where the
	 * Resource must be the declared Resource of the Capability. The Resolver
	 * can then add additional {@link HostedCapability} objects with the
	 * {@link #insertHostedCapability(Resource, Capability, List)} method when
	 * it, for example, attaches fragments. Those {@link HostedCapability}
	 * objects will then use the host's Resource which likely differs from the
	 * declared Resource of the corresponding Capability.
	 * <p>
	 * The returned list is in priority order, the Capabilities with a lower
	 * index have a preference over later {@link HostedCapability} objects.
	 * <p>
	 * The collection returned is unmodifiable but additional elements can be
	 * added through the
	 * {@link #insertHostedCapability(Resource, Capability, List)} method. In
	 * general, this is necessary when the Resolver uses Capabilities declared
	 * in a Resource but that must originate from an attached host.
	 * <p>
	 * Each returned Capability must match the given Requirement. This implies
	 * that the filter in the Requirement must match as well as any namespace
	 * specific directives. For example mandatory attributes for the
	 * {@code osgi.wiring.package} namespace.
	 * 
	 * @param requirement the requirement that a resolver is attempting to
	 *        satisfy
	 * 
	 * @return a List of {@link HostedCapability} objects that match the
	 *         requirement
	 * 
	 * @throws NullPointerException if the requirement is null
	 */
	List<HostedCapability> findProviders(Requirement requirement);

	/**
	 * Add a Resource/Capability tuple to the list of capabilities returned from
	 * {@link #findProviders(Requirement)}.
	 * <p>
	 * Used by the Resolver to add additional Capabilities, with a potentially
	 * different Resource as its source, to the set of Capabilities. This
	 * function is necessary to allow fragments to attach to hosts, thereby
	 * changing the origin of a Capability.
	 * <p>
	 * The given Capability must
	 * 
	 * @param resource The Resource that hosts this capability
	 * @param capability The Capability to be hosted
	 * @param capabilities The list returned from
	 *        {@link #findProviders(Requirement)}
	 * @return The newly created HostedCapability
	 * 
	 */
	HostedCapability insertHostedCapability(Resource resource, Capability capability,
			List<HostedCapability> capabilities);

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
