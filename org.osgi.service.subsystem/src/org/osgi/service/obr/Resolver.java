/*
 * Copyright (c) OSGi Alliance (2006, 2010). All Rights Reserved.
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

import java.util.List;
import java.util.Map;

import org.osgi.model.resource.Capability;
import org.osgi.model.resource.Requirement;
import org.osgi.model.resource.Resource;
import org.osgi.model.resource.Wire;

/**
 * A resolver is a service interface that can be used to find resolutions for specified
 * {@link PotentialRequirement PotentialRequirements} based on a supplied {@link Environment}.
 * 
 * @version $Id$
 */
public interface Resolver {
  /**
   * Attempt to resolve the requirements based on the specified environment and return
   * any new revisions or wires to the caller.
   * 
   * For a given resolve call an environment should return a consistent set of
   * capabilities and wires. The simplest mechanism of achieving this is by
   * creating an immutable snapshot of the environment state and passing this to the
   * resolve method.
   * 
   * <p>TODO mention about delta characteristics
   * 
   * @param environment
   *          the environment into which to resolve the requirements
   *          
   * @param requirements 
   * @return a resolution
   * 
   * @throws ResolutionException
   * @throws IllegalArgumentException
   * @throws NullPointerException 
   */
  <C extends Capability, R extends Requirement> Map<Resource<C, R>, List<Wire<C, R>>> resolve(Environment<C, R> environment, Requirement...requirements) 
  throws ResolutionException, IllegalArgumentException, NullPointerException;
}
