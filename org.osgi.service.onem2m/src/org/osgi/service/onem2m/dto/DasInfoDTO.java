package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * DTO expresses DasInfo. DAS is short for Dynamic Authorization Server.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.45</a>
 * @see <a href=
 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L1135-1147">oneM2M
 *      XSD dynAuthTokenReqInfo and dasInfo</a>
 * 
 */
public class DasInfoDTO extends DTO {
	/**
	 * Dynamic Authorization Server URI
	 */
	public java.lang.String uri;

	/**
	 * Information to send to the Dynamic Authorization Server
	 * 
	 * @see <a href=
	 *      "https://git.onem2m.org/PRO/XSD/blob/master/v3_11_0/CDT-commonTypes-v3_11_0.xsd#L1149-1198">oneM2M
	 *      XSD dynAuthDasRequest</a>
	 * 
	 */
	public GenericDTO dasRequest;

	/**
	 * Secured Information to send to the Dynamic Authorization Server. JWS or JWE
	 * is assigned to this field.
	 */
	public java.lang.String securedDasRequest;
}
