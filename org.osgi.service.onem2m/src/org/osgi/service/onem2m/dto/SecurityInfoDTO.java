/*
 * Copyright (c) OSGi Alliance (2019, 2020). All Rights Reserved.
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

package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * DTO expresses Security Info.
 * <p>
 * This class is used as union. SecurityInfoType field indicates which type of
 * content is stored.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oenM2M
 *      TS-0004 6.3.5.4.8</a>
 * @NotThreadSafe
 */
public class SecurityInfoDTO extends DTO {
	/**
	 * Security Info Type
	 *
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oenM2M
	 *      TS-0004 6.3.4.2.35</a>
	 */
	public SecurityInfoType	securityInfoType;

	/**
	 * Das Request
	 */
	public GenericDTO		dasRequest;

	/**
	 * Das Response
	 */
	public GenericDTO		dasResponse;

	/**
	 * Esprim Rand Object
	 */
	public GenericDTO		esprimRandObject;

	/**
	 * Esprim Object
	 */
	public String			esprimObject;

	/**
	 * Escertke Message
	 */
	public byte[]			escertkeMessage;

	/**
	 * Enum SecurityInfoType
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oenM2M
	 *      TS-0004 6.3.4.2.35</a>
	 */
	public enum SecurityInfoType {

		/**
		 * DynamicAuthorizationRequest
		 */
		DynamicAuthorizationRequest(1),

		/**
		 * DynamicAuthorizationResponse
		 */
		DynamicAuthorizationResponse(2),

		/**
		 * ReceiverESPrimRandObjectRequest
		 */
		ReceiverESPrimRandObjectRequest(3),

		/**
		 * ReceiverESPrimRandObjectResponse
		 */
		ReceiverESPrimRandObjectResponse(4),

		/**
		 * ESPrimObject
		 */
		ESPrimObject(5),

		/**
		 * ESCertKEMessage
		 */
		ESCertKEMessage(6),

		/**
		 * DynamicAuthorizationRelationshipMappingRequest
		 */
		DynamicAuthorizationRelationshipMappingRequest(7),

		/**
		 * DynamicAuthorizationRelationshipMappingResponse
		 */
		DynamicAuthorizationRelationshipMappingResponse(8);

		private final int type;

		private SecurityInfoType(int type) {
			this.type = type;
		}

		/**
		 * Get assigned value.
		 * 
		 * @return assigned value
		 */
		public int getValue() {
			return type;
		}
	}

}
