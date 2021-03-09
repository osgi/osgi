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

package org.osgi.service.zigbee.descriptions;

/**
 * This interface represents a ZCLCommandDescription.
 * 
 * @author $Id$
 */
public interface ZCLCommandDescription {
	/**
	 * Returns the command identifier.
	 * 
	 * @return the command identifier.
	 */
	short getId();

	/**
	 * Returns the command name.
	 * 
	 * @return the command name.
	 */
	String getName();

	/**
	 * Returns the command functional description.
	 * 
	 * @return the command functional description.
	 */
	String getShortDescription();

	/**
	 * Checks if this command it mandatory.
	 * 
	 * @return true, if and only if the command is mandatory.
	 */
	boolean isMandatory();

	/**
	 * Returns an array of the parameter descriptions.
	 * 
	 * @return an array of the parameter descriptions.
	 */
	ZCLParameterDescription[] getParameterDescriptions();

	/**
	 * @return the isClusterSpecificCommand value.
	 */
	boolean isClusterSpecificCommand();

	/**
	 * Returns the manufacturer code.
	 * 
	 * Default value is: -1 (no code).
	 * 
	 * @return the manufacturer code.
	 */
	int getManufacturerCode();

	/**
	 * Checks if the command is manufacturer specific.
	 * 
	 * @return {@code true} if end only if {@link #getManufacturerCode()} is
	 *         not. -1.
	 */
	public boolean isManufacturerSpecific();

	/**
	 * Checks if this is a server-side command (that is going from the client to
	 * server direction).
	 * 
	 * @return the isClientServerDirection value.
	 */
	boolean isClientServerDirection();

}
