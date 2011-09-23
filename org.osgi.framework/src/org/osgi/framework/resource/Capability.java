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

/**
 * A capability that has been declared from a {@link Resource}.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface Capability {

	/**
	 * Returns the name space of this capability.
	 * 
	 * @return The name space of this capability.
	 */
	String getNamespace();

	/**
	 * Returns the directives of this capability.
	 * 
	 * <p>
	 * Only the following list of directives have specified semantics:
	 * <ul>
	 * <li> {@link ResourceConstants#CAPABILITY_EFFECTIVE_DIRECTIVE effective}
	 * <li> {@link ResourceConstants#CAPABILITY_USES_DIRECTIVE uses}
	 * <li> {@link ResourceConstants#CAPABILITY_MANDATORY_DIRECTIVE mandatory} -
	 * only recognized for the {@link ResourceConstants#WIRING_BUNDLE_NAMESPACE
	 * osgi.wiring.bundle} and
	 * {@link ResourceConstants#WIRING_PACKAGE_NAMESPACE osgi.wiring.package}
	 * name spaces.
	 * <li> {@link ResourceConstants#CAPABILITY_EXCLUDE_DIRECTIVE exclude} - only
	 * recognized for the {@link ResourceConstants#WIRING_PACKAGE_NAMESPACE
	 * osgi.wiring.package} name space.
	 * <li> {@link ResourceConstants#CAPABILITY_INCLUDE_DIRECTIVE include} - only
	 * recognized for the {@link ResourceConstants#WIRING_PACKAGE_NAMESPACE
	 * osgi.wiring.package} name space.
	 * </ul>
	 * All other directives have no specified semantics and are considered extra
	 * user defined information. The OSGi Alliance reserves the right to extend
	 * the set of directives which have specified semantics.
	 * 
	 * @return An unmodifiable map of directive names to directive values for
	 *         this capability, or an empty map if this capability has no
	 *         directives.
	 */
	Map<String, String> getDirectives();

	/**
	 * Returns the attributes of this capability.
	 * 
	 * @return An unmodifiable map of attribute names to attribute values for
	 *         this capability, or an empty map if this capability has no
	 *         attributes.
	 */
	Map<String, Object> getAttributes();

	/**
	 * Returns the resource declaring this capability.
	 * 
	 * @return The resource declaring this capability.
	 */
	Resource getResource();
}
