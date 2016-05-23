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

package org.osgi.service.zigbee;

/**
 * This interface represents the ZCL Frame Header.
 * 
 * @author $Id$
 */
public interface ZCLHeader {

	/**
	 * Get this ZCLHeader's command id
	 * 
	 * @return the commandId
	 */
	int getCommandId();

	/**
	 * Get manufacturerCode of the ZCL Frame Header
	 * 
	 * @return the manufacturerCode if the ZCL Frame is maufacturer specific,
	 *         otherwise returns -1
	 */
	int getManufacturerCode();

	/**
	 * Returns the Frame Type Sub-field of the Frame Control Field
	 * 
	 * @return true if the Frame Control Field states that the command is
	 *         Cluster Specific. Returns false otherwise
	 */
	boolean isClusterSpecificCommand();

	/**
	 * @return true if the ZCL frame is manufacturer specific (i.e. the
	 *         Manufacturer Specific Sub-field of the ZCL Frame Frame Control
	 *         Field is 1.
	 */
	boolean isManufacturerSpecific();

	/**
	 * @return the isClientServerDirection value
	 */
	boolean isClientServerDirection();

	/**
	 * @return returns {@code true} if the ZCL Header Frame Control Field
	 *         "Disable Default Response Sub-field" is 1. Returns {@code false}
	 *         otherwise.
	 */
	boolean isDefaultResponseDisabled();

	/**
	 * The ZCL Frame Header Transaction Sequence Number
	 * 
	 * @return the transaction sequence number
	 */
	byte getSequenceNumber();

	/**
	 * Returns the Frame Control field of the ZCLHeader
	 * 
	 * @return the frame control field.
	 */
	short getFrameControlField();
}
