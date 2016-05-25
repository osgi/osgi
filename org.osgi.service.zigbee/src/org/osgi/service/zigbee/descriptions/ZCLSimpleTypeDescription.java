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

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;

/**
 * This interface is used for representing any of the simple ZigBee Data Types
 * defined in the ZCL.
 * 
 * The interface extends the {@link ZCLSimpleTypeDescription} by providing
 * serialize and deserialize methods permit to marshall and unmarshall the data
 * into {@link ZigBeeDataInput} and from {@link ZigBeeDataOutput} streams.<br>
 * 
 * @author $Id$
 */
public interface ZCLSimpleTypeDescription extends ZCLDataTypeDescription {

	/**
	 * @param param Object to be serialized using the associated type. If the
	 *        the value is {@code  null}, then the invalid number will be
	 *        serialized.
	 * 
	 * @param os {@link ZigBeeDataOutput}eDataOutput in which the array of bytes
	 *        that represents the serialized value be appended.
	 */
	public void serialize(ZigBeeDataOutput os, Object param);

	/**
	 * @param is the {@link ZigBeeDataInput} from where the value of data type
	 *        is read from.
	 * @return An object that represents the deserialized value of data. Return
	 *         {@code  null} if the deserialized value is the invalid number
	 * 
	 * @throws IOException if an I/O error occurs while reading the
	 *         {@code ZigBeeDataInput}
	 */
	public Object deserialize(ZigBeeDataInput is) throws IOException;

}
