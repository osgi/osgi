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
 * A resource is the representation of a uniquely identified and typed data.
 *
 * A resources can be wired together via capabilities and requirements.
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
}
