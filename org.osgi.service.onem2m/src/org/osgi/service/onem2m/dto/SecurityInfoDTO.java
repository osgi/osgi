package org.osgi.service.onem2m.dto;

import java.util.Map;

import org.osgi.dto.DTO;

/**
 * DTO expressing Security Info.
 */
public class SecurityInfoDTO extends DTO {
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	/**
	 * Security Info Type
	 */
	public SecurityInfoType securityInfoType;

	/**
	 * Das Request
	 */
	public Map<String, Object> dasRequest;// DynAuthDasRequestDTO

	/**
	 * Das Response
	 */
	public Map<String, Object> dasResponse;// DynAuthDasResponseDTO

	/**
	 * Esprim Rand Objecgt
	 */
	public Map<String, Object> esprimRandObject;// ReceiverESPrimRandObjectDTO

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
