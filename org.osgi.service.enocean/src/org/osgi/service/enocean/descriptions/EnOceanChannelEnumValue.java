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
 * This transitional interface is used to define all the possible values taken
 * by an enumerated channel.
 * 
 * @version 1.0
 * @author $Id$
 */
public interface EnOceanChannelEnumValue {

    /**
     * The start value of the enumeration.
     * 
     * @return the start value.
     */
    public int getStart();

    /**
     * The stop value of the enumeration.
     * 
     * @return the stop value.
     */
    public int getStop();

    /**
     * A non-mandatory description of what this enumerated value is about.
     * 
     * @return the description of this channel.
     */
    public String getDescription();

}
