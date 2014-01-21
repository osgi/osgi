/*
 * Copyright (c) OSGi Alliance (2012, 2014). All Rights Reserved.
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

package org.osgi.dto.framework.wiring;

import org.osgi.dto.resource.WiringDTO;

/**
 * Data Transfer Object for a BundleWiring.
 * 
 * <p>
 * An installed Bundle can be adapted to provide a {@code BundleWiringDTO} for
 * the current wiring Bundle. {@code BundleWiringDTO} objects for all in use
 * wirings of the Bundle can be obtained by adapting the bundle to
 * {@code BundleWiringDTO[]}.
 * 
 * <p>
 * The {@link WiringDTO#providedWires providedWires} field must contain an array
 * of {@link BundleWireDTO}s. The {@link WiringDTO#requiredWires requiredWires}
 * field must contain an array of {@link BundleWireDTO}s. The
 * {@link WiringDTO#resource resource} field must contain a
 * {@link BundleRevisionDTO}.
 * 
 * @author $Id$
 * @NotThreadSafe
 */
public class BundleWiringDTO extends WiringDTO {
    /**
     * The bundle wiring's in use setting indicates that the bundle wiring is in
     * use.
     */
    public boolean            inUse;

    /**
     * The current state of the bundle wiring. The bundle wiring's current
     * setting indicates that the bundle wiring is the current bundle wiring for
     * the bundle.
     */
    public boolean            current;
}
