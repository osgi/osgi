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

package org.osgi.service.zigbee.descriptions;

import org.osgi.service.zigbee.ZigBeeDataTypes;

/**
 * This interface is used for representing any of the ZigBee Data Types defined
 * in the ZCL. Each of these data types has a set of associated information that
 * this interface definition permit to retrieve using the specific methods.
 * <ul>
 * <li>The data type identifier
 * <li>The data type name
 * <li>The data type is analog or digital
 * <li>The java class used to represent the data type
 * </ul>
 * 
 * 
 * @author $Id$
 */
public interface ZCLDataTypeDescription {

	/**
	 * @return The data type identifier. The data types identifiers supported by
	 *         this specification are defined in the {@link ZigBeeDataTypes}
	 *         interface.
	 */
	public short getId();

	/**
	 * The associated data type name.
	 * 
	 * @return The associated data type name string.
	 */
	public String getName();

	/**
	 * Returns whether the data type is analog.
	 * 
	 * @return true, if the data type is Analog, otherwise is Discrete.
	 */
	public boolean isAnalog();

	/**
	 * The corresponding Java type class.
	 * 
	 * @return The corresponding Java type class.
	 */
	public Class getJavaDataType();

}
