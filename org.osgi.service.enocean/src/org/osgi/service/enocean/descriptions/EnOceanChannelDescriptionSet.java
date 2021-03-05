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
 * This interface represents an EnOcean Channel Description Set.
 * {@link EnOceanChannelDescriptionSet} is registered as an OSGi Service.
 * Provides a method to retrieve the {@link EnOceanChannelDescription} objects
 * it documents.
 * 
 * @version 1.0
 * @author $Id$
 */
public interface EnOceanChannelDescriptionSet {

    /**
     * Retrieves a {@link EnOceanChannelDescription} object according to its
     * identifier.
     * 
     * @param channelId the unique string identifier of the description object.
     * @return The corresponding {@link EnOceanChannelDescription} object, or
     *         null.
     * @throws IllegalArgumentException if the supplied String is invalid, null,
     *         or other reason.
     */
    public EnOceanChannelDescription getChannelDescription(String channelId);

}
