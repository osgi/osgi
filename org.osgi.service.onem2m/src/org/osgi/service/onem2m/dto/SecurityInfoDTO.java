package org.osgi.service.onem2m.dto;
import java.util.Map;

import org.osgi.dto.DTO;
/**
 * DTO expressing Security Info.
 */
public class SecurityInfoDTO extends DTO{
//	@javax.xml.bind.annotation.XmlElement( required  = false)
	public SecurityInfoType securityInfoType;
	public Map<String,Object>  dasRequest;// DynAuthDasRequestDTO
	public Map<String,Object>  dasResponse;//DynAuthDasResponseDTO
	public Map<String,Object> esprimRandObject;//ReceiverESPrimRandObjectDTO
	public String esprimObject;
	public byte[] escertkeMessage;


	public static enum SecurityInfoType{
		 DynamicAuthorizationRequest(1),
		 DynamicAuthorizationResponse(2),
		 ReceiverESPrimRandObjectRequest(3),
		 ReceiverESPrimRandObjectResponse(4),
		 ESPrimObject(5),
		 ESCertKEMessage(6),
		 DynamicAuthorizationRelationshipMappingRequest(7),
		 DynamicAuthorizationRelationshipMappingResponse(8);

		 int type;
		 private SecurityInfoType(int type) {
			 this.type = type;
		 }
		 public int getValue() {
			 return type;
		 }
	}

}
