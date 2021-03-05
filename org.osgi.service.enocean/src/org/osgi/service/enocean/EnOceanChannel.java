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

package org.osgi.service.enocean;

/**
 * Holds the raw value and channel identification info of an EnOceanChannel.
 * 
 * @author $Id$
 */
public interface EnOceanChannel {

    /**
     * @return The unique ID of this channel.
     */
    public String getChannelId();

    /**
     * @return The offset, in bits, where this channel is found in the telegram.
     */
    public int getOffset();

    /**
     * @return The size, in bits, of this channel.
     */
    public int getSize();

    /**
     * Gets the raw value of this channel.
     * 
     * @return corresponding value.
     */
    byte[] getRawValue();

    /**
     * Sets the raw value of a channel.
     * 
     * @param rawValue
     */
    void setRawValue(byte[] rawValue);

}
