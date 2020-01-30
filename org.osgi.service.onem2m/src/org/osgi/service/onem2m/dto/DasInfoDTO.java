package org.osgi.service.onem2m.dto;

import java.util.Map;

import org.osgi.dto.DTO;

/**
 * DTO expressing DasInfo. DAS is short for Dynamic Authorization Server.
 */
public class DasInfoDTO extends DTO{
//	@javax.xml.bind.annotation.XmlElement( required  = true)
	/**
	 * Dynamic Authorization Server URI 
	 */
	public java.lang.String uri;
	
	/**
	 * Information to send to the Dynamic Authorization Server
	 */
	public Map<String,Object> dasRequest;
	
	/**
	 * Secured Information to send to the Dynamic Authorization Server.
	 * JWS or JWE is assigned to this field.
	 */
	public java.lang.String securedDasRequest;
}
