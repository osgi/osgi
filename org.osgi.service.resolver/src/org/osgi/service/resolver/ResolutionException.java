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

/**
 * Indicates failure to resolve a set of requirements.
 * 
 * Resolver implementations may subclass this class to provide extra state
 * information about the reason for the resolution failure.
 * 
 * @ThreadSafe
 * @Immutable
 */
public class ResolutionException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an exception of type {@code ResolutionException}.
   * 
   * <p>
   * This method creates an {@code ResolutionException} object with the
   * specified message and cause.
   * 
   * @param message
   *          The message.
   * @param cause
   *          The cause of this exception.
   */
  public ResolutionException(String message, Throwable cause) {
    super(message, cause);
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
}
