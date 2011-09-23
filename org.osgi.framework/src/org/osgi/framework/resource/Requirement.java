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

import java.util.Map;

import org.osgi.framework.Constants;

/**
 * A requirement that has been declared from a {@link Resource} .
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface Requirement {
	/**
	 * Returns the name space of this requirement.
	 * 
	 * @return The name space of this requirement.
	 */
	String getNamespace();

	/**
	 * Returns the directives of this requirement.
	 * 
	 * <p>
	 * Only the following list of directives have specified semantics:
	 * <ul>
	 * <li> {@link ResourceConstants#REQUIREMENT_EFFECTIVE_DIRECTIVE effective}
	 * <li> {@link ResourceConstants#REQUIREMENT_FILTER_DIRECTIVE filter}
	 * <li> {@link ResourceConstants#REQUIREMENT_CARDINALITY_DIRECTIVE
	 * cardinality}
	 * <li> {@link ResourceConstants#REQUIREMENT_RESOLUTION_DIRECTIVE resolution}
	 * <li> {@link ResourceConstants#REQUIREMENT_VISIBILITY_DIRECTIVE visibility}
	 * - only recognized for the
	 * {@link ResourceConstants#WIRING_BUNDLE_NAMESPACE osgi.wiring.bundle} name
	 * space.
	 * </ul>
	 * All other directives have no specified semantics and are considered extra
	 * user defined information. The OSGi Alliance reserves the right to extend
	 * the set of directives which have specified semantics.
	 * 
	 * @return An unmodifiable map of directive names to directive values for
	 *         this requirement, or an empty map if this requirement has no
	 *         directives.
	 */
	Map<String, String> getDirectives();

	/**
	 * Returns the attributes of this requirement.
	 * 
	 * <p>
	 * Requirement attributes have no specified semantics and are considered
	 * extra user defined information.
	 * 
	 * @return An unmodifiable map of attribute names to attribute values for
	 *         this requirement, or an empty map if this requirement has no
	 *         attributes.
	 */
	Map<String, Object> getAttributes();

	/**
	 * Returns the resource declaring this requirement.
	 * 
	 * @return The resource declaring this requirement.
	 */
	Resource getResource();

	/**
	 * Returns whether the specified capability matches this requirement.
	 * 
	 * <p>
	 * A capability matches this requirement when all of the following are true:
	 * <ul>
	 * <li>The specified capability has the same {@link #getNamespace() name
	 * space} as this requirement.
	 * <li>The filter specified by the {@link Constants#FILTER_DIRECTIVE filter}
	 * directive of this requirement matches the
	 * {@link Capability#getAttributes() attributes of the specified capability}.
	 * <li>The standard capability {@link Capability#getDirectives() directives}
	 * that influence matching and that apply to the name space are satisfied.
	 * See the capability
	 * {@link ResourceConstants#CAPABILITY_MANDATORY_DIRECTIVE mandatory}
	 * directive.
	 * </ul>
	 * 
	 * @param capability The capability to match to this requirement.
	 * @return {@code true} if the specified capability matches this this
	 *         requirement; {@code false} otherwise.
	 */
	boolean matches(Capability capability);
}
