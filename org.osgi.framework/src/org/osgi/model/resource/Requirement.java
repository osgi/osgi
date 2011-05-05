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

import org.osgi.framework.Constants;
import org.osgi.framework.wiring.BundleCapability;

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
	 * @return An unmodifiable map of directive names to directive values for
	 *         this requirement, or an empty map if this requirement has no
	 *         directives.
	 */
	Map<String, String> getDirectives();

	/**
	 * Returns the resource declaring this requirement.
	 * 
	 * @return The resource declaring this requirement.
	 */
	Resource getResource();

	/**
	 * Returns whether the specified capability matches this requirement. A
	 * capability matches this requirement when all of the following are true:
	 * <ul>
	 * <li>The specified capability has the same {@link #getNamespace() name
	 * space} as this requirement.
	 * <li>The filter specified by the {@link Constants#FILTER_DIRECTIVE filter}
	 * directive of this requirement matches the
	 * {@link Capability#getAttributes() attributes of the specified capability}.
	 * <li>The {@link #getDirectives() requirement directives} and the
	 * {@link Capability#getDirectives() capability directives} that apply to
	 * the name space are satisfied.
	 * </ul>
	 * 
	 * 
	 * @param capability
	 *            The capability to match to this requirement.
	 * @return {@code true} if the specified capability matches this this
	 *         requirement. {@link #getNamespace() name space} as this
	 *         requirement and the filter for this requirement matches the
	 *         {@link BundleCapability#getAttributes() attributes of the
	 *         specified capability}; {@code false} otherwise.
	 */
	boolean matches(Capability capability);
}
