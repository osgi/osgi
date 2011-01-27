/*
 * Copyright (c) OSGi Alliance (2010, 2011). All Rights Reserved.
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

package org.osgi.framework.wiring;

import java.util.Map;

import org.osgi.framework.Constants;

/**
 * A requirement that has been declared from a {@link BundleRevision bundle
 * revision}.
 * 
 * <p>
 * The framework defines capability name spaces for
 * {@link Capability#PACKAGE_CAPABILITY packages},
 * {@link Capability#BUNDLE_CAPABILITY bundles} and
 * {@link Capability#HOST_CAPABILITY hosts}. These capability name spaces are
 * defined only to express wiring information by the framework. They must not be
 * used in {@link Constants#PROVIDE_CAPABILITY Provide-Capability} and
 * {@link Constants#REQUIRE_CAPABILITY Require-Capability} manifest headers.
 * 
 * @ThreadSafe
 * @noimplement
 * @since 1.1
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
	 * @return An unmodifiable map of directive names to directive values for
	 *         this requirement, or an empty map if this requirement has no
	 *         directives.
	 */
	Map<String, String> getDirectives();

	/**
	 * Returns the requirement of this capability.
	 * 
	 * @return An unmodifiable map of attribute names to attribute values for
	 *         this requirement, or an empty map if this requirement has no
	 *         attributes.
	 */
	Map<String, Object> getAttributes();

	/**
	 * Returns the bundle revision declaring this requirement.
	 * 
	 * @return The bundle revision declaring this requirement.
	 */
	BundleRevision getDeclaringRevision();
}
