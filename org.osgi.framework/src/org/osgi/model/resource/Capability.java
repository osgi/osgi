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

package org.osgi.model.resource;

import java.util.Map;

import org.osgi.framework.wiring.BundleRevision;

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
	 * Returns the directives of this capability. Only the following list of
	 * directives are allowed in the returned {@link Map map} of directives:
	 * <ul>
	 * <li> {@link Resource#CAPABILITY_EFFECTIVE_DIRECTIVE effective}
	 * <li> {@link Resource#CAPABILITY_USES_DIRECTIVE uses}
	 * <li> {@link Resource#CAPABILITY_MANDATORY_DIRECTIVE mandatory} - can only be present
	 * for the {@link BundleRevision#BUNDLE_NAMESPACE osgi.wiring.bundle} and
	 * {@link BundleRevision#PACKAGE_NAMESPACE osgi.wiring.package} name
	 * spaces.
	 * <li> {@link Resource#CAPABILITY_EXCLUDE_DIRECTIVE exclude} - can only be present
	 * for the {@link BundleRevision#PACKAGE_NAMESPACE osgi.wiring.package} name
	 * space.
	 * <li> {@link Resource#CAPABILITY_INCLUDE_DIRECTIVE include} - can only be present
	 * for the {@link BundleRevision#PACKAGE_NAMESPACE osgi.wiring.package} name
	 * space.
	 * </ul>
	 * No other directives will be present in the returned map. OSGi Alliance
	 * reserves the right to extend the set of directives.
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
	 * The resource that declares this capability.
	 * 
	 * @return the resource
	 */
	Resource getResource();
}
