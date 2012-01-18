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

package org.osgi.framework.resource;

import java.util.List;


/**
 * A resource is the representation of a uniquely identified and typed data.
 *
 * A resources can be wired together via capabilities and requirements.
 * 
 * <p>
 * Instances of this type must be <i>effectively immutable</i>. That is, for a
 * given instance of this interface, the methods defined by this interface must
 * always return the same result.
 *
 * @ThreadSafe
 * @version $Id$
 */
public interface Resource {
	/**
	 * Returns the capabilities declared by this resource.
	 * 
	 * @param namespace The name space of the declared capabilities to return or
	 *        {@code null} to return the declared capabilities from all name
	 *        spaces.
	 * @return An unmodifiable list containing the declared {@link Capability}s
	 *         from the specified name space. The returned list will be empty if
	 *         this resource declares no capabilities in the specified name
	 *         space.
	 */
	List<Capability> getCapabilities(String namespace);

	/**
	 * Returns the requirements declared by this bundle resource.
	 *
	 * @param namespace The name space of the declared requirements to return or
	 *        {@code null} to return the declared requirements from all name
	 *        spaces.
	 * @return An unmodifiable list containing the declared {@link Requirement}
	 *         s from the specified name space. The returned list will be empty
	 *         if this resource declares no requirements in the specified name
	 *         space.
	 */
	List<Requirement> getRequirements(String namespace);

	/**
	 * Compares this {@code Resource} to another {@code Resource}.
	 * 
	 * <p>
	 * This {@code Resource} is equal to another {@code Resource} if they have
	 * they both have the same content and come from the same location. Location
	 * may be defined as the bundle location if the resource is an installed
	 * bundle or the repository location if the resource is in a repository.
	 * 
	 * @param obj The object to compare against this {@code Resource}.
	 * @return {@code true} if this {@code Resource} is equal to the other
	 *         object; {@code false} otherwise.
	 */
	boolean equals(Object obj);

	/**
	 * Returns the hashCode of this {@code Resource}.
	 * 
	 * @return The hashCode of this {@code Resource}.
	 */
	int hashCode();
}
