/*
 * Copyright (c) OSGi Alliance (2006, 2009). All Rights Reserved.
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

package org.osgi.service.obr;

import java.net.URL;
import java.util.Collection;

import org.osgi.framework.wiring.Capability;
import org.osgi.framework.wiring.Requirement;
import org.osgi.framework.wiring.Resource;

/**
 * Represents a repository that contains {@link Resource resources}.
 * 
 * <p>
 * Repositories may be registered as services and may be used as inputs to an
 * {@link Environment#findProviders(Requirement...)} operation.
 * 
 * <p>
 * Repositories registered as services may be filtered using standard service
 * properties.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface Repository {
  /**
   * Find any capabilities from resources contained in this repository that can
   * potentially satisfy the supplied requirements.
   * 
   * @param requirements The requirements that should be matched or empty list if
   * all capabilities should be matched.
   * 
   * @return A collection capabilities that match any of the supplied requirements
   *  
   * @throws NullPointerException if any of the requirements are null
   */
  Collection<Capability> findProviders(Requirement... requirements) throws NullPointerException;

  /**
   * Lookup the URL where the supplied resource may be accessed, if any.
   * 
   * <p>
   * Successive calls to this method do not have to return the same value this
   * allows for mirroring behaviors to be built into a repository.
   * 
   * @param resource
   *          - The resource whose content is desired.
   * @return The URL for the supplied resource or null if this resource has no
   *         binary content or is not accessible for any reason
   *         
   * @throws NullPointerException if the resource is null 
   */
  URL getContent(Resource resource) throws NullPointerException;
}
