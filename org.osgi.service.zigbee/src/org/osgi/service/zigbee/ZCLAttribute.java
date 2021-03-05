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

package org.osgi.service.zigbee;

import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;
import org.osgi.util.promise.Promise;

/**
 * This interface represents a ZCLAttribute.
 * <p>
 * Its extends ZCLAttributeInfo to add methods to read and write the ZCL
 * attribute from and to the ZigBee node with respectively the
 * {@link #getValue()} and {@link #setValue(Object)} methods.
 * 
 * @author $Id$
 */
public interface ZCLAttribute extends ZCLAttributeInfo {

	/**
	 * Property key for the optional attribute id of a ZigBee Event Listener.
	 */
	public final static String ID = "zigbee.attribute.id";

	/**
	 * Gets the current value of the attribute.
	 * 
	 * <p>
	 * As described in section <em>2.4.1.3 Effect on Receipt</em> of the ZCL
	 * specification, a <em>Read attributes</em> command can have the following
	 * status: {@link ZCLException#SUCCESS},
	 * {@link ZCLException#UNSUPPORTED_ATTRIBUTE}, or
	 * {@link ZCLException#INVALID_VALUE}.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         The response object returned by {@link Promise#getValue()} is the
	 *         requested attribute value in the relevant Java data type (see
	 *         {@link #getDataType()} method and
	 *         {@link ZCLDataTypeDescription#getJavaDataType()}) or in
	 *         {@code byte[]} if {@link #getDataType()} returns null. The
	 *         response object is null if an
	 *         {@link ZCLException#UNSUPPORTED_ATTRIBUTE} or
	 *         {@link ZCLException#INVALID_VALUE} error occurs and the adequate
	 *         ZCLException is returned by {@link Promise#getFailure()} .
	 */
	public Promise<Object> getValue();

	/**
	 * Sets the current value of the attribute.
	 * 
	 * <p>
	 * As described in section <em>2.4.3.3 Effect on Receipt</em> of the ZCL
	 * specification, a <em>Write attributes</em> command may return the
	 * following status: {@link ZCLException#SUCCESS},
	 * {@link ZCLException#UNSUPPORTED_ATTRIBUTE},
	 * {@link ZCLException#INVALID_DATA_TYPE}, {@link ZCLException#READ_ONLY},
	 * {@link ZCLException#INVALID_VALUE}, or
	 * {@link ZDPException#NOT_AUTHORIZED}.
	 * 
	 * @param value the Java value to set.
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         {@link Promise#getFailure()} returns null if the attribute value
	 *         has been successfully written. The adequate ZigBeeException is
	 *         returned otherwise.
	 */
	public Promise<Void> setValue(Object value);

}
