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

import org.osgi.framework.model.Capability;
import org.osgi.framework.model.Requirement;

/**
 * A requirement that has been declared from a {@link PotentialRevision revision}.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface PotentialRequirement extends Requirement {
  /**
   * TODO
   */
  final String CARDINALITY_DIRECTIVE = "cardinality";
  
  /**
   * TODO
   */
  final String MULTIPLE_CARDINALITY = "multiple"; 
    
  /**
   * TODO
   */
  final String SINGULAR_CARDINALITY = "singular";
  
  /**
   * Returns the revision declaring this requirement.
   * 
   * @return The revision declaring this requirement.
   */
  PotentialRevision getRevision();
  
  /**
   * Returns whether the specified capability matches this requirement.
   * 
   * @param capability The capability to match to this requirement.
   * @return {@code true} if the specified capability has the same
   *         {@link #getNamespace() name space} as this requirement and the
   *         filter for this requirement matches the
   *         {@link Capability#getAttributes() attributes of the
   *         specified capability}; {@code false} otherwise.
   */
  boolean matches(PotentialCapability capability);

}
