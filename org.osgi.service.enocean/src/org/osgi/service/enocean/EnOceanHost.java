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
 * This interface represents an EnOcean Host, a device that offers EnOcean
 * networking features.
 * 
 * @author $Id$
 */
public interface EnOceanHost {

    /**
     * The unique ID for this Host: this matches the CHIP_ID of the EnOcean
     * Gateway Chip it embodies.
     */
    public static Object HOST_ID            = "enocean.host.id";

    /**
     * repeater level to disable repeating; this is the default.
     */
    public static int    REPEATER_LEVEL_OFF = 0;
    /**
     * repeater level to repeat every telegram at most once.
     */
    public static int    REPEATER_LEVEL_ONE = 1;
    /**
     * repeater level to repeat every telegram at most twice.
     */
    public static int    REPEATER_LEVEL_TWO = 2;

    /**
     * Reset the EnOcean Host (cf. ESP3 command 0x02: CO_WR_RESET)
     * 
     * @throws EnOceanException if any problem occurs.
     */
    public void reset() throws EnOceanException;

    /**
     * Returns the chip's application version info (cf. ESP3 command 0x03:
     * CO_RD_VERSION)
     * 
     * @return a String object containing the application version info.
     * @throws EnOceanException if any problem occurs.
     */
    public String appVersion() throws EnOceanException;

    /**
     * Returns the chip's API version info (cf. ESP3 command 0x03:
     * CO_RD_VERSION)
     * 
     * @return a String object containing the API version info.
     * @throws EnOceanException if any problem occurs.
     */
    public String apiVersion() throws EnOceanException;

    /**
     * Gets the BASE_ID of the chip, if set (cf. ESP3 command 0x08:
     * CO_RD_IDBASE)
     * 
     * @return the BASE_ID of the device as defined in EnOcean specification
     * @throws EnOceanException if any problem occurs.
     */
    public int getBaseID() throws EnOceanException;

    /**
     * Sets the base ID of the device, may be used up to 10 times (cf. ESP3
     * command 0x07: CO_WR_IDBASE)
     * 
     * @param baseID to be set.
     * @throws EnOceanException if any problem occurs.
     */
    public void setBaseID(int baseID) throws EnOceanException;

    /**
     * Sets the repeater level on the host (cf. ESP3 command 0x09:
     * CO_WR_REPEATER)
     * 
     * @param level one of the Repeater Level constants as defined above.
     * @throws EnOceanException if any problem occurs.
     */
    public void setRepeaterLevel(int level) throws EnOceanException;

    /**
     * Gets the current repeater level of the host (cf. ESP3 command 0x0A:
     * CO_RD_REPEATER)
     * 
     * @return one of the Repeater Level constants as defined above.
     * @throws EnOceanException if any problem occurs.
     */
    public int getRepeaterLevel() throws EnOceanException;

    /**
     * Retrieves the CHIP_ID associated with the given servicePID, if existing
     * on this chip.
     * 
     * @param servicePID
     * @return the associated CHIP_ID of the exported device.
     * @throws EnOceanException if any problem occurs.
     */
    public int getChipId(String servicePID) throws EnOceanException;
}
