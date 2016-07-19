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

/**
 * This interface represents a ZDP frame (see Figure 2.19 Format of the ZDP
 * Frame ZIGBEE SPECIFICATION: 1_053474r17ZB_TSC-ZigBee-Specification.pdf).
 * 
 * <p>
 * This interface MUST be implemented by the developer invoking the
 * {@link ZigBeeNode#invoke(int, int, ZDPFrame, ZDPHandler)}
 * 
 * <p>
 * <b>Notes</b>
 * <ul>
 * <li>This interface hides on purpose the Transaction Sequence Number field
 * because it MUST be handled internally by the ZigBee Base Driver</li>
 * <li>The interface does not provide any method for writing the payload because
 * the ZigBee Base Driver needs only to read the payload
 * </ul>
 * 
 * @author $Id$
 */
public interface ZDPFrame {

	/**
	 * Get (a copy of this ZDP) payload
	 * 
	 * @return a copy of the payload
	 */
	byte[] getPayload();

	/**
	 * @return an {@link ZigBeeDataInput} for the payload of the
	 *         {@link ZDPFrame}. This method, in contrary to
	 *         {@link #getPayload()}, doesn't require to create a copy of the
	 *         payload.
	 * 
	 * @throws IllegalStateException if the InputStream is not available.
	 */
	ZigBeeDataInput getDataInput();

}
