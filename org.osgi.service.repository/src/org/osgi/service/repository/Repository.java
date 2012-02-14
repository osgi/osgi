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

import org.osgi.framework.resource.Capability;
import org.osgi.framework.resource.Requirement;
import org.osgi.framework.resource.Resource;
import org.osgi.service.resolver.Environment;

/**
 * Represents a repository that contains {@link Resource resources}.
 * 
 * <p>
 * Repositories may be registered as services and may be used as inputs to an
 * {@link Environment#findProviders(Requirement)} operation.
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
   * Service attribute to uniquely identify this repository
   */
  final String ID = "repository.id";

  /**
   * Service attribute to define the name of this repository
   */
  final String NAME = "repository.name";

  /**
   * Service attribute to provide a human readable name for this repository
   */
  final String DISPLAY_NAME = "repository.displayName";

  /**
   * Find any capabilities that match the supplied requirements.
   * 
   * <p>
   * See {@link Environment#findProviders} for a discussion on matching.
   * 
   * @param requirements the requirements that should be matched
   *
   * @return A map of requirements to capabilites that match the supplied requirements
   * 
   * @throws NullPointerException if requirements is null
   */
  Map<Requirement, Collection<Capability>> findProviders(Collection<? extends Requirement> requirements);
}
