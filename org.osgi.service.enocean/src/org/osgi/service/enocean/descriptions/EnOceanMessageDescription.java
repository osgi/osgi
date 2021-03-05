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

import org.osgi.service.enocean.EnOceanChannel;

/**
 * This interface represents an EnOcean Message Description.
 * 
 * @version 1.0
 * @author $Id$
 */
public interface EnOceanMessageDescription {

    /**
     * Serializes a series of {@link EnOceanChannel} objects into the
     * corresponding byte[] sequence.
     * 
     * @param channels to be serialized.
     * @return serialized value.
     * @throws IllegalArgumentException if the given channels is null.
     */
    public byte[] serialize(EnOceanChannel[] channels);

    /**
     * Deserializes an array of bytes into the EnOceanChannels available to the
     * payload, if possible.
     * 
     * @param bytes to be deserialized.
     * @return deserialized value.
     * 
     * @throws IllegalArgumentException if the actual instance type of the
     *         message is not compatible with the bytes it is fed with (RORG to
     *         begin with).
     */
    public EnOceanChannel[] deserialize(byte[] bytes);

    /**
     * @return the message description containing the RORG, (and the FUNC, and
     *         the TYPE if available), as well as, the EEP's "title" (e.g. for
     *         F60201: Rocker Switch, 2 Rocker; Light and Blind Control -
     *         Application Style 1).
     */
    public String getMessageDescription();

}
