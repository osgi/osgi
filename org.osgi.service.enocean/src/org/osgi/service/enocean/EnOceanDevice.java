/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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

package org.osgi.service.enocean;

import java.util.Map;

/**
 * This interface represents a physical device that communicates over the
 * EnOcean protocol.
 * 
 * @author $Id$
 */
public interface EnOceanDevice {

    /**
     * Property name for the mandatory DEVICE_CATEGORY of the device
     */
    public final static String DEVICE_CATEGORY       = "EnOcean";

    /**
     * Property name for the mandatory CHIP_ID of the device
     */
    public final static String CHIP_ID               = "enocean.device.chip_id";

    /**
     * Property name for the radiotelegram main type of the profile associated
     * with this device.
     */
    public final static String RORG                  = "enocean.device.profile.rorg";

    /**
     * Property name for the radiotelegram functional type of the profile
     * associated with this device.
     */
    public final static String FUNC                  = "enocean.device.profile.func";

    /**
     * Property name for the radiotelegram subtype of the profile associated
     * with this device.
     */
    public final static String TYPE                  = "enocean.device.profile.type";

    /**
     * Property name for the manufacturer ID that may be specified by some
     * teach-in messages.
     */
    public static final String MANUFACTURER          = "enocean.device.manufacturer";

    /**
     * Property name for the security level mask for this device. The format of
     * that mask is specified in EnOcean Security Draft.
     */
    public final static String SECURITY_LEVEL_FORMAT = "enocean.device.security_level_format";

    /**
     * Property name that defines if the device is exported or not. If present,
     * the device is exported.
     */
    public static final String ENOCEAN_EXPORT        = "enocean.device.export";

    /**
     * @return The EnOcean device chip ID.
     */
    public int getChipId();

    /**
     * @return The EnOcean profile RORG.
     */
    public int getRorg();

    /**
     * @return The EnOcean profile FUNC, or -1 if unknown.
     */
    public int getFunc();

    /**
     * @return The EnOcean profile TYPE, or -1 if unknown.
     */
    public int getType();

    /**
     * @return The EnOcean manufacturer code, -1 if unknown.
     */
    public int getManufacturer();

    /**
     * @return The EnOcean security level format, or 0 as default (no security)
     */
    public int getSecurityLevelFormat();

    /**
     * Manually sets the EEP FUNC of the device.
     * 
     * @param func the EEP func of the device;
     */
    public void setFunc(int func);

    /**
     * Manually sets the EEP TYPE of the device.
     * 
     * @param type the EEP type of the device;
     */
    public void setType(int type);

    /**
     * Switches the device into learning mode.
     * 
     * @param learnMode the desired state: true for learning mode, false to
     *        disable it.
     */
    public void setLearningMode(boolean learnMode);

    /**
     * Get the current rolling code of the device.
     * 
     * @return The current rolling code in use with this device's
     *         communications.
     */
    public int getRollingCode();

    /**
     * Sets the rolling code of this device.
     * 
     * @param rollingCode the rolling code to be set or initiated.
     */
    public void setRollingCode(int rollingCode);

    /**
     * Returns the current encryption key used by this device.
     * 
     * @return The current encryption key, or null.
     */
    public byte[] getEncryptionKey();

    /**
     * Sets the encryption key of the device.
     * 
     * @param key the encryption key to be set.
     */
    public void setEncryptionKey(byte[] key);

    /**
     * Gets the list of devices the device already has learned.
     * 
     * @return The list of currently learned device's CHIP_IDs.
     */
    public int[] getLearnedDevices();

    /**
	 * Retrieves the currently available RPCs to this device; those are stored
	 * using their manfufacturerId:commandId identifiers.
	 * 
	 * @return A list of the available RPCs, in a
	 *         {@code Map<Integer, Integer[]>} form.
	 */
    public Map<Integer, Integer[]> getRPCs();

    /**
     * Sends an RPC to the remote device.
     * 
     * @param rpc
     * @param handler
     * @throws IllegalArgumentException
     */
    public void invoke(EnOceanRPC rpc, EnOceanHandler handler);

    /**
     * Removes the device's OSGi service from OSGi service platform.
     */
    public void remove();

}
