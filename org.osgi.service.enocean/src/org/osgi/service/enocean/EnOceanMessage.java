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

import org.osgi.service.enocean.channels.EnOceanChannel;

/**
 * Holds the necessary methods to interact with an EnOcean message.
 *  
 * @version 1.0
 */
public interface EnOceanMessage {
	
	/**
	 * Property name for the radiotelegram main type.
	 */
	public final static String RORG = "enocean.profile.rorg";

	
	/**
	 * Property name for the sender ID of this message (mandatory)
	 */
	public final static String SENDER_ID = "enocean.profile.sender_id";
	
	/**
	 * Property name for the optional destination ID of this message.
	 */
	public final static String DESTINATION_ID = "enocean.profile.destination_id";
	
	/**
	 * Gets the current EnOcean status of the Message.
	 * 
	 * @return the current EnOcean status of this message.
	 */
	public int getStatus();
	
	/**
	 * Sets the EnOcean status of this message.
	 * 
	 * @param status the status to be set.
	 */
	public void setStatus(int status);
	
	/**
	 * Serializes the EnOceanMessage into an array of bytes, if possible.
	 * 
	 * @return The serialized byte list corresponding to the binary message.
	 * @throws EnOceanException
	 */
	public byte[] serialize() throws EnOceanException;
	
	/**
	 * Deserializes an array of bytes into an EnOceanMessage, if possible.
	 * 
	 * @return The deserialized object from a sequence of bytes. The actual return type depends
	 * 			on the implementation.
	 * @throws EnOceanException
	 */
	public Object deserialize(byte[] bytes) throws EnOceanException;

	/**
	 * Get the list of associated channels.
	 * 
	 * @return The list of associated channels.
	 */
	public EnOceanChannel[] getChannels();
	
	/**
	 * Returns the number of subtelegrams (usually 1) this Message carries.
	 * 
	 * @return The number of subtelegrams in the case of multiframe messages.
	 */
	public int getSubTelegramCount();
	
	/**
	 * Returns the number of redundant Messages, out of 3, actually received.
	 * 
	 * @return The number of subtelegrams received.
	 */
	public int getRedundancyInfo();
	
	/**
	 * Returns the average RSSI on all the received subtelegrams, including redundant ones.
	 * 
	 * @return The average RSSI perceived.
	 */
	public int getRSSI();


}
