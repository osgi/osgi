/*
 * Copyright (c) OSGi Alliance (2006, 2012). All Rights Reserved.
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

// This document is an experimental draft to enable interoperability
// between bundle repositories. There is currently no commitment to 
// turn this draft into an official specification.  

package org.osgi.service.repository;

import java.util.Collection;
import java.util.Map;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

/**
 * A repository service that contains {@link Resource resources}.
 * 
 * <p>
 * Repositories may be registered as services and may be used as by a resolve
 * context during resolver operations.
 * 
 * <p>
 * Repositories registered as services may be filtered using standard service
 * properties.
 * 
 * @ThreadSafe
 * @noimplement
 * @version $Id$
 */
public interface Repository {
	/**
	 * Service property to provide URLs related to this repository.
	 * 
	 * <p>
	 * The value of this property must be of type {@code String},
	 * {@code String[]}, or {@code Collection<String>}.
	 */
	String	URL	= "repository.url";

	/**
	 * Find the capabilities that match the specified requirements.
	 * 
	 * @param requirements The requirements for which matching capabilities
	 *        should be returned. Must not be {@code null}.
	 * @return A map of matching capabilities for the specified requirements.
	 *         Each specified requirement must appear as a key in the map. If
	 *         there are no matching capabilities for a specified requirement,
	 *         then the value in the map for the specified requirement must be
	 *         an empty collection. The returned map is the property of the
	 *         caller and can be modified by the caller.
	 */
	Map<Requirement, Collection<Capability>> findProviders(Collection<? extends Requirement> requirements);
}
