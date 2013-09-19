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

/**
 * This interface represents the ZigBee data type abstraction.
 * 
 * @version 1.0
 */
public interface ZigBeeDataTypeDescription {

	/**
	 * @return The data type identifier
	 */
	public short getId();

	/**
	 * @return The associated data type name string.
	 */
	public String getName();

	/**
	 * @return The data type invalid number if exists, otherwise returns null
	 */
	public Object getInvalidNumber();

	/**
	 * @return true, if the data type is analog.
	 */
	public boolean isAnalog();

	/**
	 * @return The corresponding Java type class.
	 */
	public Class getJavaDataType();

	/**
	 * @param param Object to be serialized using the associated type
	 * @return An array of bytes that represents the serialized value of param
	 */
	public byte[] serialize(Object param);

	/**
	 * @param data Array of bytes to be deserialized using associated type
	 * @return An object that represents the deserialized value of data
	 */
	public Object deserialize(byte[] data);

}
