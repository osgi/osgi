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

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;

/**
 * This interface is used for representing any of the simple ZigBee Data Types
 * defined in the ZCL.
 * 
 * <p>
 * The interface extends the {@link ZCLDataTypeDescription} by providing
 * serialize and deserialize methods to marshal and unmarshal the data into the
 * {@link ZigBeeDataInput} and from {@link ZigBeeDataOutput} streams.
 * 
 * <dl>
 * Related documentation:
 * <dd>[1] ZigBee Cluster Library specification, Document 075123r04ZB, May 29,
 * 2012.
 * </dl>
 * 
 * @author $Id$
 */
public interface ZCLSimpleTypeDescription extends ZCLDataTypeDescription {

	/**
	 * Method for serializing a ZigBee data type into a {@code ZigBeeDataOutput}
	 * stream. An implementation of this method must throw an
	 * {@code IllegalArgumentException} if the passed value does not belong to
	 * the expected class or its value exceeds the possible values allowed (in
	 * terms of range or length).
	 * 
	 * <p>
	 * An implementation of this method must interpret (where it makes sense) a
	 * {@code null} {@code value} as the request to serialize the so called
	 * <em>Invalid Value</em>.
	 * 
	 *
	 * @param os a {@link ZigBeeDataOutput} stream where to the passed value
	 *        will be appended. This parameter cannot be {@code null}. If
	 *        {@code null} a {@link NullPointerException} must be thrown.
	 * 
	 * @param value The value that have to be serialized on the output stream.
	 *        If null is passed this method outputs on the stream the ZigBee
	 *        invalid value related the specific data type. If the data type do
	 *        not allow any invalid value and the passed value is null an
	 *        {@link IllegalArgumentException} is thrown.
	 * 
	 * @throws IOException
	 * 
	 * @throws IllegalArgumentException Must be thrown if the passed value does
	 *         not belong to the expected class or its value exceeds the
	 *         possible values allowed (range or length).
	 * 
	 */

	public void serialize(ZigBeeDataOutput os, Object value) throws IOException;

	/**
	 * Method for deserializing a value from the passed {@link ZigBeeDataInput}
	 * stream.
	 *
	 * @param is the {@link ZigBeeDataInput} from where the value of data type
	 *        is read from.
	 * 
	 * @return An object that represents the deserialized value of data. Return
	 *         {@code  null} if the read value represents the
	 *         <em>Invalid Value</em> for the specific ZigBee data type.
	 * 
	 * @throws IOException if an I/O error occurs while reading the
	 *         {@code ZigBeeDataInput}
	 */
	public Object deserialize(ZigBeeDataInput is) throws IOException;

}
