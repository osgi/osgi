/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.service.zigbee;

/**
 * This interface represents the ZCL Frame Header.
 * 
 * @author $Id$
 */
public interface ZCLHeader {

	/**
	 * Returns the command identifier of this frame.
	 * 
	 * @return the command identifier of this frame.
	 */
	int getCommandId();

	/**
	 * Returns the manufacturer code of this frame.
	 * 
	 * @return the manufacturer code if the ZCL Frame is manufacturer specific,
	 *         otherwise returns -1.
	 */
	int getManufacturerCode();

	/**
	 * Checks the frame Type Sub-field of the frame control field.
	 * 
	 * @return true if the frame control field states that the command is
	 *         cluster specific. Returns false otherwise.
	 */
	boolean isClusterSpecificCommand();

	/**
	 * Checks if the frame is manufacturer specific.
	 * 
	 * @return true if the ZCL frame is manufacturer specific (i.e. the
	 *         Manufacturer Specific Sub-field of the ZCL Frame Frame Control
	 *         Field is 1.
	 */
	boolean isManufacturerSpecific();

	/**
	 * Checks the client server direction of the frame.
	 * 
	 * @return the isClientServerDirection value.
	 */
	boolean isClientServerDirection();

	/**
	 * Checks if the default response is disabled.
	 * 
	 * @return {@code true} if the ZCL Header Frame Control Field
	 *         "Disable Default Response Sub-field" is 1. Returns {@code false}
	 *         otherwise.
	 */
	boolean isDefaultResponseDisabled();

	/**
	 * Returns the transaction Sequence Number of this frame.
	 * 
	 * @return the transaction sequence number of this frame.
	 */
	byte getSequenceNumber();

	/**
	 * Returns the Frame Control field of this frame.
	 * 
	 * @return the frame control field of this frame.
	 */
	short getFrameControlField();
}
