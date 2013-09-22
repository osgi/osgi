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
 * @author Victor Perron <victor.perron@orange.fr>
 */
public interface EnOceanMessage {
	
	/**
	 * @return the message's RORG
	 */
	public int getRorg();
	
	/**
	 * @return the message's FUNC
	 */
	public int getFunc();
	
	/**
	 * @return the message's TYPE
	 */
	public int getType();
		
	/**
	 * @return the message's Sender ID
	 */
	public int getSenderId();

	/**
	 * @return the message's destination ID, or -1
	 */
	public int getDestinationId();
	
	/**
	 * Serializes the EnOceanMessage into an array of bytes, if possible.
	 * 
	 * @return The serialized byte list corresponding to the binary message.
	 * @throws EnOceanException
	 */
	public byte[] serialize() throws EnOceanException;
	
	/**
	 * Deserializes an array of bytes into the inner datafields of the Message, if possible.
	 * 
	 * @throws EnOceanException
	 */
	public void deserialize(byte[] bytes) throws EnOceanException, IllegalArgumentException;

	/**
	 * Get the list of associated channels.
	 * 
	 * @return The list of associated channels.
	 */
	public EnOceanChannel[] getChannels();
	
	/**
	 * Gets the current EnOcean status of the Message. 
	 * The 'status' byte is actually a bitfield that mainly 
	 * holds repeater information, teach-in status, and more
	 * or less information depending on the radiotelegram type. 
	 * 
	 * 
	 * @return the current EnOcean status of this message.
	 */
	public int getStatus();
	
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
