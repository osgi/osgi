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

import java.util.Collection;

/**
 * A resolved capability that has been provided from a {@link BundleWiring bundle wiring}.
 * This capability may or may not be required by any bundle wiring.
 * <p>
 * A resolved capability represents a capability from a resolved bundle wiring.
 * @ThreadSafe
 * @version $Id$
 */
public interface ResolvedCapability extends Capability {
	/**
	 * Returns the bundle wiring providing this capability.
	 * 
	 * @return The bundle wiring providing this capability. If the bundle wiring
	 *         providing this capability is not {@link BundleWiring#isInUse() in
	 *         use}, {@code null} will be returned.
	 */
	BundleWiring getProviderWiring();

	/**
	 * Returns the bundle wirings that require this capability.
	 * 
	 * <p>
	 * The result of this method can change if this capability becomes required
	 * by additional bundle wirings.
	 * 
	 * @return A collection containing a snapshot of the bundle wirings
	 *         currently requiring this capability, or an empty collection if no
	 *         bundle wirings require this capability. If the bundle wiring
	 *         providing this capability is not {@link BundleWiring#isInUse() in
	 *         use}, {@code null} will be returned.
	 */
	Collection<BundleWiring> getRequirerWirings();
}
