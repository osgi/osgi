/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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

/**
 * A wire connecting a {@link Capability} to a {@link Requirement}.
 * 
 * @ThreadSafe
 * @noimplement
 * @since 1.1
 * @version $Id$
 */
public interface Wire {
	/**
	 * Returns the {@link Capability} for this wire.
	 * 
	 * @return The {@link Capability} for this wire.
	 */
	Capability getCapability();

	/**
	 * Return the {@link Requirement} for this wire.
	 * 
	 * @return The {@link Requirement} for this wire.
	 */
	Requirement getRequirement();

	/**
	 * Returns the bundle wiring providing the {@link #getCapability()
	 * capability}.
	 * 
	 * @return The bundle wiring providing the capability. If the bundle wiring
	 *         providing the capability is not {@link BundleWiring#isInUse() in
	 *         use}, {@code null} will be returned.
	 */
	BundleWiring getCapabilityWiring();

	/**
	 * Returns the bundle wiring whose {@link #getRequirement() requirement} is
	 * wired to the {@link #getCapability() capability}.
	 * 
	 * @return The bundle wiring whose requirement is wired to the capability.
	 *         If the bundle wiring requiring the capability is not
	 *         {@link BundleWiring#isInUse() in use}, {@code null} will be
	 *         returned.
	 */
	BundleWiring getRequirementWiring();
}
