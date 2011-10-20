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

package org.osgi.framework.resource;

import java.util.List;

/**
 * A wiring for a resource. A wiring is associated with a resource and
 * represents the dependencies with other wirings.
 *
 * @ThreadSafe
 * @version $Id$
 */
public interface Wiring {
	/**
	 * Returns the capabilities provided by this wiring.
	 *
	 * <p>
	 * Only capabilities considered by the resolver are returned. For example,
	 * capabilities with
	 * {@link ResourceConstants#CAPABILITY_EFFECTIVE_DIRECTIVE effective}
	 * directive not equal to {@link ResourceConstants#EFFECTIVE_RESOLVE
	 * resolve} are not returned.
	 *
	 * <p>
	 * A capability may not be required by any wiring and thus there may be no
	 * {@link #getProvidedResourceWires(String) wires} for the capability.
	 *
	 * <p>
	 * A wiring for a non-fragment resource provides a subset of the declared
	 * capabilities from the resource and all attached fragment
	 * resources<sup>&#8224;</sup>. Not all declared capabilities may be
	 * provided since some may be discarded. For example, if a package is
	 * declared to be both exported and imported, only one is selected and the
	 * other is discarded.
	 * <p>
	 * A wiring for a fragment resource with a symbolic name must provide
	 * exactly one {@link ResourceConstants#IDENTITY_NAMESPACE identity}
	 * capability.
	 * <p>
	 * &#8224; The {@link ResourceConstants#IDENTITY_NAMESPACE identity}
	 * capability provided by attached fragment resource must not be included in
	 * the capabilities of the host wiring.
	 *
	 * @param namespace The name space of the capabilities to return or
	 *        {@code null} to return the capabilities from all name spaces.
	 * @return A list containing a snapshot of the {@link Capability}s, or an
	 *         empty list if this wiring provides no capabilities in the
	 *         specified name space. For a given name space, the list contains
	 *         the wires in the order the capabilities were specified in the
	 *         manifests of the {@link #getResource() resource} and the attached
	 *         fragment resources<sup>&#8224;</sup> of this wiring. There is no
	 *         ordering defined between capabilities in different name spaces.
	 */
	List<Capability> getResourceCapabilities(String namespace);

	/**
	 * Returns the requirements of this wiring.
	 * 
	 * <p>
	 * Only requirements considered by the resolver are returned. For example,
	 * requirements with
	 * {@link ResourceConstants#REQUIREMENT_EFFECTIVE_DIRECTIVE effective}
	 * directive not equal to {@link ResourceConstants#EFFECTIVE_RESOLVE
	 * resolve} are not returned.
	 * 
	 * <p>
	 * A wiring for a non-fragment resource has a subset of the declared
	 * requirements from the resource and all attached fragment resources. Not
	 * all declared requirements may be present since some may be discarded. For
	 * example, if a package is declared to be optionally imported and is not
	 * actually imported, the requirement must be discarded.
	 * 
	 * @param namespace The name space of the requirements to return or
	 *        {@code null} to return the requirements from all name spaces.
	 * @return A list containing a snapshot of the {@link Requirement}s, or an
	 *         empty list if this wiring uses no requirements in the specified
	 *         name space. For a given name space, the list contains the wires
	 *         in the order the requirements were specified in the manifests of
	 *         the {@link #getResource() resource} and the attached fragment
	 *         resources of this wiring. There is no ordering defined between
	 *         requirements in different name spaces.
	 */
	List<Requirement> getResourceRequirements(String namespace);

	/**
	 * Returns the {@link Wire}s to the provided {@link Capability capabilities}
	 * of this wiring.
	 *
	 * @param namespace The name space of the capabilities for which to return
	 *        wires or {@code null} to return the wires for the capabilities in
	 *        all name spaces.
	 * @return A list containing a snapshot of the {@link Wire}s for the
	 *         {@link Capability capabilities} of this wiring, or an empty list
	 *         if this wiring has no capabilities in the specified name space.
	 *         For a given name space, the list contains the wires in the order
	 *         the capabilities were specified in the manifests of the
	 *         {@link #getResource() resource} and the attached fragment
	 *         resources of this wiring. There is no ordering defined between
	 *         capabilities in different name spaces.
	 */
	List<Wire> getProvidedResourceWires(String namespace);

	/**
	 * Returns the {@link Wire}s to the {@link Requirement requirements} in use
	 * by this wiring.
	 * 
	 * @param namespace The name space of the requirements for which to return
	 *        wires or {@code null} to return the wires for the requirements in
	 *        all name spaces.
	 * @return A list containing a snapshot of the {@link Wire}s for the
	 *         {@link Requirement requirements} of this wiring, or an empty list
	 *         if this wiring has no requirements in the specified name space.
	 *         For a given name space, the list contains the wires in the order
	 *         the requirements were specified in the manifests of the
	 *         {@link #getResource() resource} and the attached fragment
	 *         resources of this wiring. There is no ordering defined between
	 *         requirements in different name spaces.
	 */
	List<Wire> getRequiredResourceWires(String namespace);

	/**
	 * Returns the resource associated with this wiring.
	 *
	 * @return The resource associated with this wiring.
	 */
	Resource getResource();
}
