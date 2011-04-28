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

/**
 * A requirement that has been declared from a {@link Resource}
 * .
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
   * @return An unmodifiable map of directive names to directive values for this
   *         requirement, or an empty map if this requirement has no directives.
   */
  Map<String, String> getDirectives();

  /**
   * Returns the attributes of this requirement.
   * 
   * @return An unmodifiable map of attribute names to attribute values for this
   *         requirement, or an empty map if this requirement has no attributes.
   */
  Map<String, Object> getAttributes();

  /**
   * Returns the resource declaring this requirement.
   * 
   * @return The resource declaring this requirement.
   */
  Resource getResource();
}
