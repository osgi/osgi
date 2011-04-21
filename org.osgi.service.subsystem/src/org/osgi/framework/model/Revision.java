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

package org.osgi.framework.model;

import java.util.List;

import org.osgi.framework.Version;

/**
 * A generic revision. Revisions represent any 'things' that can be wired
 * together via capabilities and requirements.
 * 
 * TODO decide on identity characteristics of a revision. Given in OSGi 
 * multiple bundles can be installed with same bsn/version this cannot be
 * used as a key.
 * 
 * What then is identity of a revision? Object identity? URI (needs getter method?)
 * 
 * @param <R>
 * @param <C>
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface Revision<C extends Capability, R extends Requirement> {

  /**
   * The unique name of the revision
   * @return the name of the revision
   */
  String getSymbolicName();

	/**
	 * The version of this revision.
	 * 
	 * @return the version of this revision
	 */
	Version getVersion();

	/**
	 * Returns the capabilities declared by this revision.
	 * 
	 * @param namespace The name space of the declared capabilities to return or
	 *        {@code null} to return the declared capabilities from all name
	 *        spaces.
	 * @return A list containing a snapshot of the declared
	 *         {@link Capability}s, or an empty list if this
	 *         revision declares no capabilities in the specified name space.
	 */
	List<C> getDeclaredCapabilities(String namespace);

	/**
	 * Returns the requirements declared by this bundle revision.
	 * 
	 * @param namespace The name space of the declared requirements to return or
	 *        {@code null} to return the declared requirements from all name
	 *        spaces.
	 * @return A list containing a snapshot of the declared
	 *         {@link Requirement}s, or an empty list if this bundle
	 *         revision declares no requirements in the specified name space.
	 */
	List<R> getDeclaredRequirements(String namespace);
}
