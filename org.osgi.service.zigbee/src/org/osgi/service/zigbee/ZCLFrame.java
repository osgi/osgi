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

import java.io.DataInput;

/**
 * This interface models the ZigBee Cluster Library Frame.
 * 
 * @author $Id$
 */
public interface ZCLFrame {

	/**
	 * Returns the header of this frame.
	 * 
	 * @return the header of this frame.
	 */
	public ZCLHeader getHeader();

	/**
	 * Returns a byte array containing the raw ZCL frame, suitable to be sent on
	 * the wire. The returned byte array contains the whole ZCL Frame, including
	 * the ZCL Frame Header and the ZCL Frame payload.
	 * 
	 * @return a byte array containing a raw ZCL frame, suitable to be sent on
	 *         the wire. Any modifications issued on the returned array must not
	 *         affect the internal representation of the ZCLFrame interface
	 *         implementation.
	 */
	public byte[] getBytes();

	/**
	 * Copy in the passed array the internal raw ZCLFrame.
	 * 
	 * @param buffer The buffer where to copy the raw ZCL frame.
	 * 
	 * @return The actual number of bytes copied.
	 */
	public int getBytes(byte[] buffer);

	/**
	 * Retrieve the current size of the internal raw frame (that is the size of
	 * the byte[] that whould be returned if calling the {@link #getBytes()}
	 * method.
	 * 
	 * @return The size of the raw ZCL frame.
	 */
	public int getSize();

	/**
	 * Returns {@link ZigBeeDataInput} for reading the ZCLFrame payload content.
	 * Every call to this method returns a different instance. The returned
	 * instances must not share the current position to the underlying
	 * {@link ZCLFrame} payload.
	 * 
	 * @return a {@link DataInput} for the payload of the {@link ZCLFrame}. This
	 *         method does not generate a copy of the payload.
	 * 
	 * @throws IllegalStateException if the InputStream is not available.
	 */
	public ZigBeeDataInput getDataInput();
}
