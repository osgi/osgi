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
 */
public class SecurityInfoDTO extends DTO {
	/**
	 * Security Info Type
	 *
	 * x@see <a href=
	 * "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oenM2M
	 * TS-0004 6.3.4.2.35</a>
	 */
	public SecurityInfoType securityInfoType;

	/**
	 * Das Request
	 */
	public GenericDTO dasRequest;

	/**
	 * Das Response
	 */
	public GenericDTO dasResponse;

	/**
	 * Esprim Rand Object
	 */
	public GenericDTO esprimRandObject;

	/**
	 * Esprim Object
	 */
	public String esprimObject;

	/**
	 * Escertke Message
	 */
	public byte[] escertkeMessage;

	/**
	 * Enum SecurityInfoType
	 * 
	 * @see <a href=
	 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oenM2M
	 *      TS-0004 6.3.4.2.35</a>
	 */
	public static enum SecurityInfoType {

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

		int type;

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
