/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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
 * This interface represents a physical device that communicates over the EnOcean protocol.
 *  
 * @version 1.0
 * @author Victor Perron <victor.perron@orange.fr>
 */
public interface EnOceanDevice {
	
	/**
	 * Property name for the mandatory CHIP_ID of the device
	 */
	public final static String CHIP_ID = "enocean.device.chip_id";
	
	/**
	 * Property name for the radiotelegram main type of the profile
	 * associated with this device.
	 */
	public final static String RORG = "enocean.device.profile.rorg";
	
	/**
	 * Property name for the radiotelegram functional type of the profile
	 * associated with this device.
	 */
	public final static String FUNC = "enocean.device.profile.func";
	
	/**
	 * Property name for the radiotelegram subtype of the profile
	 * associated with this device.
	 */
	public final static String TYPE = "enocean.device.profile.type";
	
	/**
	 * Property name for the 'profile name' for this device. This is nothing
	 * EnOcean-standard, you shouldn't expect something unique.
	 */
	public final static String PROFILE_NAME = "enocean.device.profile.name";

	/**
	 * Property name for the 'friendly name' of this device. Not unique.
	 */
	public final static String NAME = "enocean.device.name";

	/**
	 * Property name for the security level mask for this device.
	 * The format of that mask is specified in EnOcean Security Draft.
	 */
	public final static String SECURITY_LEVEL_FORMAT = "enocean.device.security_level_format";
	
	/**
	 * Sends a message on the EnOcean network, uses lightweight byte[] argument type.
	 * 
	 * @param the {@link EnOceanMessage} as raw bytes, to be issued.
	 * @param an optional {@link EnOceanResponseHandler} object.
	 * @throws EnOceanException
	 */
	public void send(byte[] telegram, EnOceanResponseHandler handler) throws EnOceanException;
	
	/**
	 * Sends a message on the EnOcean network, uses actual {@link EnOceanMessage} objects.
	 * 
	 * @param the {@link EnOceanMessage} to be issued.
	 * @param an optional {@link EnOceanResponseHandler} object.
	 * @throws EnOceanException
	 */
	public void send(EnOceanMessage telegram, EnOceanResponseHandler handler) throws EnOceanException;
	
	/**
	 * Switches the device into learning mode.
	 * 
	 * @param learnMode the desired state: true for learning mode, false to disable it.
	 */
	public void setLearningMode(boolean learnMode);
	
	/**
	 * Get the current rolling code of the device.
	 * 
	 * @return The current rolling code in use with this device's communications.
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
	 * Retrieves the latest known received message of this device.
	 * 
	 * @return The latest {@link EnOceanMessage} issued by this device.
	 */
	public EnOceanMessage getLastMessage();
	
	/**
	 * Gets the list of devices the device already has learned. 
	 * 
	 * @return The list of currently learned device's CHIP_IDs.
	 */
	public int[] getLearnedDevices();
	
	/**
	 * Retrieves the currently available RPCs to this device; those
	 * are stored using their manfufacturerId:commandId identifiers.
	 * 
	 * @return A list of the available RPCs, in a Map<Integer, Integer[]> form.
	 */
	public Map getRPCs();
	
}
