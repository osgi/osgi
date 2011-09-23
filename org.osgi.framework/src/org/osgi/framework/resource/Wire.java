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

package org.osgi.framework.resource;

/**
 * A wire connecting a {@link Capability} to a {@link Requirement}.
 * 
 * @ThreadSafe
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
	 * Return the providing {@link Resource resource} of the {@link #getCapability() capability}.
	 * <p>
	 * The resource returned may differ from the resource referenced by the {@link #getCapability()
	 * capability}.
	 * @return the providing {@link Resource resource}.
	 */
	Resource getProvider();

	/**
	 * Return the requiring {@link Resource resource} of the {@link #getRequirement() requirement}.
	 * <p>
	 * The resource returned my differ from the resource referenced by the {@link #getRequirement()
	 * requirement}
	 * @return the requiring {@link Resource resource}.
	 */
	Resource getRequirer();
}
