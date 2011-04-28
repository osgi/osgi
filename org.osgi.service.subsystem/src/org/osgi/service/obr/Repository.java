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

import java.util.Collection;
import java.util.Iterator;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.model.resource.Capability;
import org.osgi.model.resource.Requirement;
import org.osgi.model.resource.Resource;

/**
 * Represents a repository that contains {@link Resource
 * resources}.
 * 
 * <p>
 * Repositories may be registered as services and may be used as inputs to an
 * {@link Environment#findProviders(Requirement...)} operation.
 * 
 * <p>
 * Repositories registered as services may be filtered using standard service
 * properties.
 * 
 * <p>
 * Repositories that can be modified externally should monitor the OSGi registry
 * for {@link RepositoryListener RepositoryListeners} and fire
 * {@link RepositoryChangeEvent RepositoryChangeEvents} to inform listeners of
 * any changes so they can take appropriate actions.
 * 
 * @version $Id$
 */
public interface Repository {

  /**
   * Discover any resources that specify
   * {@link Resource#getAttributes() attributes} that match the given
   * filter.
   * 
   * @param filterExpr
   *          A standard OSGi filter
   * @return List of resources matching the filters.
   * @throws InvalidSyntaxException
   * @throws IllegalArgumentException
   *           If the filter expression is invalid.
   */
  Iterator<Resource> discoverResources(String filterExpr)
      throws InvalidSyntaxException;

  /**
   * Lookup a revision based on a supplied namespace, name and version.
   * 
   * TODO should we allow wild cards/ranges for version? What are semantics if
   * version is open - highest, lowest, repository specific?
   * 
   * @param namespace
   * @param symbolicName
   * @param version
   * 
   * @return
   */
  Resource getResource(String namespace, String symbolicName,
      String version);

  /**
   * Find any capabilities from revisions contained in this repository that can
   * potentially satisfy the supplied requirements.
   * 
   * @param requirements
   * @return
   */
  Collection<Capability> findProviders(
      Requirement... requirements);

  /**
   * A counter to indicate the state of the repository, clients can use this to
   * check if there have been any changes to the revisions contained in this
   * repository.
   * 
   * <p>
   * A repository implementation that supports external modifications should
   * return a different increment.
   * 
   * @return
   */
  long getIncrement();

  /**
   * Provides a mechanism to query the changes that have occurred to this
   * repository since the specified increment.
   * 
   * @param sinceIncrement
   * @return
   */
  RepositoryDelta getDelta(long sinceIncrement);
}
