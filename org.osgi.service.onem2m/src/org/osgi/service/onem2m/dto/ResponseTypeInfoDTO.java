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

package org.osgi.service.onem2m.dto;

import java.util.List;

import org.osgi.dto.DTO;

/**
 * DTO expresses ResponseTypeInfo
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.30</a>
 * @NotThreadSafe
 */
public class ResponseTypeInfoDTO extends DTO {
	/**
	 * Response Type Value
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.6</a>
	 */
	public ResponseType	responseTypeValue;

	/**
	 * Notification URI
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.5.30</a>
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 7.5.1.2.5</a>
	 */
	public List<String>	notificationURI;

	/**
	 * enum ResponseType
	 *
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
	 *      TS-0004 6.3.4.2.6</a>
	 */
	public enum ResponseType {
		/**
		 * nonBlockingRequestSynch
		 */
		nonBlockingRequestSynch(1),
		/**
		 * nonBlockingRequestAsynch
		 */
		nonBlockingRequestAsynch(2),
		/**
		 * blockingRequest
		 */
		blockingRequest(3),
		/**
		 * flexBlocking
		 */
		flexBlocking(4);

		private final int value;

		private ResponseType(int i) {
			value = i;
		}

		/**
		 * get assigned value
		 * 
		 * @return assigned integer value.
		 */
		public int getValue() {
			return value;
		}
	}
}
