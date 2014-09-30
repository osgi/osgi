/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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

package org.osgi.service.enocean.descriptions;

import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.EnOceanException;

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
     * @param channels
     * @return serialized value.
     * @throws EnOceanException
     */
    public byte[] serialize(EnOceanChannel[] channels) throws EnOceanException;

    /**
     * Deserializes an array of bytes into the EnOceanChannels available to the
     * payload, if possible. If the actual instance type of the message is not
     * compatible with the bytes it is fed with (RORG to begin with), throw an
     * IllegalArgumentException.
     * 
     * @param bytes
     * @return deserialized value.
     * 
     * @throws EnOceanException
     */
    public EnOceanChannel[] deserialize(byte[] bytes) throws EnOceanException;

}
