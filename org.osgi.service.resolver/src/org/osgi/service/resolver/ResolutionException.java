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

package org.osgi.service.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.osgi.framework.resource.Requirement;

/**
 * Indicates failure to resolve a set of requirements.
 * 
 * <p>
 * If a resolution failure is caused by a missing mandatory dependency a
 * resolver may include any requirements it has considered in the resolution
 * exception. Clients may access this set of dependencies via the
 * {@link #getUnresolvedRequirements()} method.
 * 
 * <p>
 * Resolver implementations may subclass this class to provide extra state
 * information about the reason for the resolution failure.
 * 
 * @ThreadSafe
 * @Immutable
 */
public class ResolutionException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  // NOTE used requirement[] not collection to avoid accidental serialization
  // issues
  private Requirement[] unresolvedRequirements;

  /**
   * Creates an exception of type {@code ResolutionException}.
   * 
   * <p>
   * This method creates an {@code ResolutionException} object with the
   * specified message, cause and unresolvedRequirements.
   * 
   * @param message
   *          The message.
   * @param cause
   *          The cause of this exception.
   * @param unresolvedRequirements
   *          the requirements that are unresolved or null if no unresolved requirements
   *          information is provided.
   */
  public ResolutionException(String message, Throwable cause,
      Collection<Requirement> unresolvedRequirements) {
    super(message, cause);
    if (unresolvedRequirements != null) {
      // copy array both fixes serialization issues and
      // ensures exception is immutable
      this.unresolvedRequirements = unresolvedRequirements
          .toArray(new Requirement[unresolvedRequirements.size()]);
    }
  }

  /**
   * Creates an exception of type {@code ResolutionException}.
   * 
   * <p>
   * This method creates an {@code ResolutionException} object with the
   * specified message.
   * 
   * @param message
   *          The message.
   */
  public ResolutionException(String message) {
    super(message);
  }

  /**
   * Creates an exception of type {@code ResolutionException}.
   * 
   * <p>
   * This method creates an {@code ResolutionException} object with the
   * specified cause.
   * 
   * @param cause
   *          The cause of this exception.
   */
  public ResolutionException(Throwable cause) {
    super(cause);
  }

  /**
   * May contain one or more unresolved mandatory requirements from mandatory
   * resources.
   * 
   * <p>
   * This exception is provided for informational purposes and the specific set
   * of requirements that are returned after a resolve failure is not defined.
   * 
   * @return a collection of requirements that are unsatisfied
   */
  public Collection<Requirement> getUnresolvedRequirements() {
    // creating at each call ensures internal data is immutable
    // TODO could use a transient field to reduce CPU cost at expense of RAM -
    // both trivial compared to code complexity
    if (unresolvedRequirements == null) {
      return Collections.EMPTY_LIST;
    } else {
      ArrayList<Requirement> requirements = new ArrayList<Requirement>(
          unresolvedRequirements.length);
      for (Requirement r : unresolvedRequirements) {
        requirements.add(r);
      }
      return requirements;
    }
  }
}
