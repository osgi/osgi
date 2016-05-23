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

package org.osgi.service.zigbee.descriptions;

import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZCLHeader;

/**
 * This interface represents a ZCLCommandDescription
 * 
 * @author $Id$
 */
public interface ZCLCommandDescription {
	/**
	 * @return the command identifier
	 */
	int getId();

	/**
	 * @return the command name
	 */
	String getName();

	/**
	 * @return the command functional description
	 */
	String getShortDescription();

	/**
	 * @return true, if and only if the command is mandatory
	 */
	boolean isMandatory();

	/**
	 * @return an array of command's parameters description
	 */
	ZCLParameterDescription[] getParameterDescriptions();

	/**
	 * Serialize javaValues to a ZCLFrame that can them be used in invocations
	 * (e.g. via ZCLCluster, or ZigBeeGroup).
	 * 
	 * @param header the ZCLFrame's header
	 * @param javaValues ordered java values
	 * @return serialized javaValues as a byte[]
	 */
	ZCLFrame serialize(ZCLHeader header, Object[] javaValues);

	/**
	 * Deserialize ZCLFrame to javaValues. This ZCLFrame is expected to be a
	 * result of an invocation. (e.g. via ZCLCluster, or ZigBeeGroup).
	 * 
	 * @param frame the ZCLFrame
	 * @return deserialized Object[] as javaValues
	 */
	Object[] deserialize(ZCLFrame frame);

	/**
	 * @return the isClusterSpecificCommand value
	 */
	boolean isClusterSpecificCommand();

	/**
	 * Get manufacturerCode
	 * 
	 * Default value is: -1 (no code)
	 * 
	 * @return the manufacturerCode
	 */
	int getManufacturerCode();

	/**
	 * @return {@code true} if end only if {@link #getManufacturerCode()} is not
	 *         -1
	 */
	public boolean isManufacturerSpecific();

	/**
	 * @return the isClientServerDirection value
	 */
	boolean isClientServerDirection();

}
