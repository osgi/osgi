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

package org.osgi.framework.wiring;

import java.util.Map;

import org.osgi.framework.resource.Capability;
import org.osgi.framework.resource.Requirement;

/**
 * A requirement that has been declared from a {@link BundleRevision bundle
 * revision}.
 *
 * @ThreadSafe
 * @noimplement
 * @version $Id$
 */
public interface BundleRequirement extends Requirement {
	/**
	 * Returns the bundle revision declaring this requirement.
	 *
	 * @return The bundle revision declaring this requirement.
	 */
	BundleRevision getRevision();

	/**
	 * Returns whether the specified capability matches this requirement.
	 *
	 * @param capability The capability to match to this requirement.
	 * @return {@code true} if the specified capability has the same
	 *         {@link #getNamespace() name space} as this requirement and the
	 *         filter for this requirement matches the
	 *         {@link BundleCapability#getAttributes() attributes of the
	 *         specified capability}; {@code false} otherwise.
	 */
	boolean matches(BundleCapability capability);

	/**
	 * {@inheritDoc}
	 */
	String getNamespace();

	/**
	 * {@inheritDoc}
	 */
	Map<String, String> getDirectives();

	/**
	 * {@inheritDoc}
	 */
	Map<String, Object> getAttributes();

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This method returns the same value as {@link #getRevision()}.
	 *
	 * @since 1.1
	 */
	BundleRevision getResource();

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.1
	 */
	boolean matches(Capability capability);
}
