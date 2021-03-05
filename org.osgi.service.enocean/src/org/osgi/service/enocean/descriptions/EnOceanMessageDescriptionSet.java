/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.enocean.descriptions;

/**
 * This interface represents an EnOcean Message Description Set.
 * {@link EnOceanMessageDescriptionSet} is registered as an OSGi Service.
 * Provides method to retrieve the {@link EnOceanMessageDescription} objects it
 * documents.
 * 
 * @version 1.0
 * @author $Id$
 */
public interface EnOceanMessageDescriptionSet {

    /**
     * Retrieves a {@link EnOceanMessageDescription} object according to its
     * identifiers. See EnOcean Equipment Profile Specification for more
     * details.
     * 
     * @param rorg the radio telegram type of the message.
     * @param func The func subtype of this message.
     * @param type The type subselector.
     * @param extra Some extra information; some
     *        {@link EnOceanMessageDescription} objects need an additional
     *        specifier. If not needed, has to be set to -1.
     * @return The {@link EnOceanMessageDescription} object looked for, or null.
     * @throws IllegalArgumentException if there was an error related to the
     *         input arguments.
     */
    public EnOceanMessageDescription getMessageDescription(int rorg, int func, int type, int extra);

}
