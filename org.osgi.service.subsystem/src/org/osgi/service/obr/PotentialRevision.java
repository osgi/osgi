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

import org.osgi.framework.model.Capability;
import org.osgi.framework.model.Requirement;
import org.osgi.framework.model.Revision;

/**
 * A PotentialRevision is an abstraction of an installable thing.
 * 
 * <p>
 * PotentialRevisions may represent physical resources or meta resources.
 * 
 * <p>
 * Physical resources include things such as:
 * <ul>
 * <li>bundles
 * <li>license files
 * <li>native resources
 * </ul>
 * 
 * <p>
 * Meta resources include things such as:
 * <ul>
 * <li>groups of bundles
 * <li>bundles in a specific runtime state - such as an activated bundle
 * <li>configuration parameters
 * </ul>
 * 
 * <p>
 * The {@link #getAttributes()} method defines and extensible map of attributes
 * of a revision. Attributes may be used to discover revisions from a Repository
 * using the {@link Repository#discoverRevisions(String)} method.
 * 
 * @version $Id$
 */
public interface PotentialRevision extends
    Revision<PotentialCapability, PotentialRequirement> {

  /**
   * Namespace for OSGi bundle revisions
   */
  final String BUNDLE_NAMESPACE = "osgi.bundle";
  
  /**
   * Checksum attribute of a revision
   */
  final String CHECKSUM_ATTRIBUTE = "checksum";

  /**
   * The checksum algorithm used to calculate the {@link #CHECKSUM_ATTRIBUTE}
   * if not specified this is assumed to be SHA-256 - TODO need default?
   */
  final String CHECKSUM_ALGO_ATTRIBUTE = "checksumAlgo";

  /**
   * TODO
   */
  final String COPYRIGHT_ATTRIBUTE = "copyright";

  /**
   * A human readable description of this revision 
   */
  final String DESCRIPTION_ATTRIBUTE = "description";

  /**
   * A URL where documentation for this revision can be accessed 
   */
  final String DOCUMENTATION_URL_ATTRIBUTE = "documentation";

  /**
   * TODO
   */
  final String LICENSE_URL_ATTRIBUTE = "license";

  /**
   * If present this must match the value returned from {@link #getLocation()}
   * TODO mandatory?
   */
  final String LOCATION_ATTRIBUTE = "location";

  /**
   * Identifies the type of revision that this represents.
   * TODO mandatory? 
   */
  final String NAMESPACE_ATTRIBUTE = "namespace";

  /**
   * If present this must match the value returned from {@link #getSymbolicName()}
   * TODO mandatory
   */
  final String SYMBOLIC_NAME_ATTRIBUTE = "symbolicName";

  /**
   * TODO
   */
  final String SCM_URL_ATTRIBUTE = "scm";

  /**
   * The size of this revision in bytes. If this revision is a meta revision
   * the corresponding attribute value must equal -1
   */
  final String SIZE_ATTRIBUTE = "size";

  /**
   * If present this must match the value returned from {@link #getVersion() getVersion().toString()}
   * TODO mandatory?
   */
  final String VERSION_ATTRIBUTE = "version";

  /**
   * All attributes defined in this interface
   */
  final String[] ATTRIBUTES = { CHECKSUM_ATTRIBUTE,
      CHECKSUM_ALGO_ATTRIBUTE, COPYRIGHT_ATTRIBUTE, DESCRIPTION_ATTRIBUTE,
      DOCUMENTATION_URL_ATTRIBUTE, LICENSE_URL_ATTRIBUTE, LOCATION_ATTRIBUTE,
      SYMBOLIC_NAME_ATTRIBUTE, NAMESPACE_ATTRIBUTE, SCM_URL_ATTRIBUTE, SIZE_ATTRIBUTE,
      VERSION_ATTRIBUTE };

  /**
   * The URL to access this revision. May be null if this revision is a meta
   * revision that has no underlying physical resource.
   * 
   * @return the URL of this revision or null if this is a meta revision
   */
  String getLocation();

  /**
   * External attributes of the revision. This includes any of the
   * {@link #ATTRIBUTES predefined attributes} or any extension attributes
   * defined by third parties.
   * 
   * Third parties are advised to namespace custom attributes using a reverse
   * DNS naming convention.
   * 
   * @return TODO
   */
  Map<String, Object> getAttributes();

  /**
   * Returns the capabilities declared by this revision.
   * 
   * @param namespace
   *          The name space of the declared capabilities to return or
   *          {@code null} to return the declared capabilities from all name
   *          spaces.
   * @return A list containing a snapshot of the declared {@link Capability}s,
   *         or an empty list if this revision declares no capabilities in the
   *         specified name space.
   */
  List<PotentialCapability> getDeclaredCapabilities(String namespace);

  /**
   * Returns the requirements declared by this bundle revision.
   * 
   * @param namespace
   *          The name space of the declared requirements to return or
   *          {@code null} to return the declared requirements from all name
   *          spaces.
   * @return A list containing a snapshot of the declared {@link Requirement}s,
   *         or an empty list if this bundle revision declares no requirements
   *         in the specified name space.
   */
  List<PotentialRequirement> getDeclaredRequirements(String namespace);
}
