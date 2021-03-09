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
 * Subinterface of {@link EnOceanChannelDescription} that describes physical
 * measuring channels.
 * 
 * @version 1.0
 * @author $Id$
 */
public interface EnOceanDataChannelDescription extends EnOceanChannelDescription {

    /**
     * The start of the raw input range for this channel.
     * 
     * @return the domain start.
     */
    public int getDomainStart();

    /**
     * The end of the raw input range for this channel.
     * 
     * @return the domain stop.
     */
    public int getDomainStop();

    /**
     * The scale start at which this channel will be mapped to (-20,0°C for
     * instance)
     * 
     * @return the range start.
     */
    public double getRangeStart();

    /**
     * The scale stop at which this channel will be mapped to (+30,0°C for
     * instance)
     * 
     * @return the range stop.
     */
    public double getRangeStop();

    /**
     * The non-mandatory physical unit description of this channel.
     * 
     * @return the unit as a String
     */
    public String getUnit();

}
