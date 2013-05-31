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

import org.osgi.service.zigbee.datatype.ZigBeeDataTypeDescription;
import org.osgi.service.zigbee.description.ZigBeeCommandDescription;
import org.osgi.service.zigbee.handler.ZigBeeHandler;

/**
 * This interface represents a ZigBee Command
 * 
 * @version 1.0
 */
public interface ZigBeeCommand {

	/**
	 * @return The command identifier
	 */
	public int getId();

	/**
	 * Invokes the action. Each attribute value, must have his corresponding
	 * data type at the same entry in types array. The handler will provide the
	 * invocation response in an asynchronously way.
	 * 
	 * @param values An array of attributes values. May be null if no input
	 *        arguments exist.
	 * @param inputTypes An array of attributes data types. May be null if no
	 *        input arguments exist.
	 * @param outputTypes An array of attributes data types. May be null if no
	 *        outputs arguments for the command.
	 * @param handler The handler
	 */
	public void invoke(Object[] values, ZigBeeDataTypeDescription[] inputTypes, ZigBeeDataTypeDescription[] outputTypes, ZigBeeHandler handler) throws ZigBeeException;

	/**
	 * Invokes the action. Invokes an action using the frame. The handler will
	 * provide the invocation response in an asynchronously way.
	 * 
	 * @param bytes An array of bytes containing a command frame sequence.
	 * @param handler The handler
	 */
	public void invoke(byte[] bytes, ZigBeeHandler handler) throws ZigBeeException;

	/**
	 * @return An array containing command inputs parameters types.
	 */
	public ZigBeeDataTypeDescription[] getInputParametersTypes();

	/**
	 * @return An array containing command outputs parameters types.
	 */
	public ZigBeeDataTypeDescription[] getOutputParametersTypes();

	/**
	 * @return If exists, the command description - otherwise returns null.
	 */
	public ZigBeeCommandDescription getDescription();
}
