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

import org.osgi.util.promise.Promise;

/**
 * This interface represents a ZCLAttribute and adds to the ZCLAttributeInfo
 * interface the methods to read and write the ZCL attribute from and to the
 * ZigBee node with respectively the {@link #getValue()} and
 * {@link #setValue(Object)} methods
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
	 * specification, a <em>Read
	 * attributes</em> can have the following status: SUCCESS, or
	 * UNSUPPORTED_ATTRIBUTE (see {@link ZCLException}).
	 * 
	 * TODO: change to use promises.
	 * 
	 * FIXME: what if the data type of this attribute do not exist in the ZigBee
	 * specification?
	 * 
	 * <p>
	 * The response object given to the handler is the attribute's Java data
	 * type (see {@link #getDataType()} method) that will contain the current
	 * attribute value (or null if an UNSUPPORTED_ATTRIBUTE occurred or in case
	 * of an invalid value).
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         TODO: update here the documentation.
	 * 
	 * 
	 */
	public Promise/* <Object> */ getValue();

	/**
	 * Sets the current value of the attribute.
	 * 
	 * <p>
	 * As described in section <em>2.4.3.3 Effect on Receipt</em> of the ZCL
	 * specification, a <em>Write
	 * attributes</em> may return the following status: SUCCESS,
	 * UNSUPPORTED_ATTRIBUTE, INVALID_DATA_TYPE, READ_ONLY, INVALID_VALUE (see
	 * {@link ZCLException}), or NOT_AUTHORIZED (see {@link ZDPException}).
	 * 
	 * <p>
	 * 
	 * TODO: change to use promises. Get rid of the Boolean.
	 * 
	 * FIXME: what if the data type of this attribute do not exist in the ZigBee
	 * specification?
	 * 
	 * The response object given to the promise is a {@link Boolean} set to
	 * {@code true} if the attribute value has been written. A null value is
	 * processed as an invalid number. In case of an error has occurred,
	 * onFailure is called with a ZCLException.
	 * 
	 * @param value the Java value to set
	 * 
	 * @return A promise representing the completion of this asynchronous call.
	 *         TODO: describe the returned values.
	 * 
	 */
	public Promise/* <Boolean> */ setValue(Object value);

}
