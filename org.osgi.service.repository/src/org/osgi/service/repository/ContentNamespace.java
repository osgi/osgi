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
package org.osgi.service.repository;

import org.osgi.framework.resource.Requirement;

/**
 * Constants for use in the "osgi.content" namespace. This namespace is used to
 * locate content via the {@link Repository#findProviders(Requirement)} method.
 */
public interface ContentNamespace {
  /**
   * Namespace of the content capability
   */
  final String CAPABILITY = "osgi.content";

  /**
   * Checksum attribute of a resource
   */
  final String CHECKSUM_ATTRIBUTE = "checksum";

  /**
   * The checksum algorithm used to calculate the {@link #CHECKSUM_ATTRIBUTE} if
   * not specified this is assumed to be SHA-256 - TODO need default?
   */
  final String CHECKSUM_ALGO_ATTRIBUTE = "checksumAlgo";

  /**
   * A copyright statement for the resource
   */
  final String COPYRIGHT_ATTRIBUTE = "copyright";

  /**
   * A human readable description of this resource
   */
  final String DESCRIPTION_ATTRIBUTE = "description";

  /**
   * A URL where documentation for this resource can be accessed
   */
  final String DOCUMENTATION_URL_ATTRIBUTE = "documentation";

  /**
   * Provides an optional machine readable form of license information. See
   * section 3.2.1.10 of the OSGi Core Specification for information on it's
   * usage.
   */
  final String LICENSE_ATTRIBUTE = "license";

  /**
   * A URL where source control management for this resource is located
   */
  final String SCM_URL_ATTRIBUTE = "scm";

  /**
   * The size of this resource in bytes.
   */
  final String SIZE_ATTRIBUTE = "size";

  /**
   * A URL where source code for this resource is located
   */
  final String SOURCE_URL_ATTRIBUTE = "source";

  /**
   * All attributes defined in this interface
   */
  final String[] ATTRIBUTES = { CHECKSUM_ATTRIBUTE, CHECKSUM_ALGO_ATTRIBUTE,
      COPYRIGHT_ATTRIBUTE, DESCRIPTION_ATTRIBUTE, DOCUMENTATION_URL_ATTRIBUTE,
      LICENSE_ATTRIBUTE, SCM_URL_ATTRIBUTE, SIZE_ATTRIBUTE,
      SOURCE_URL_ATTRIBUTE };
}
