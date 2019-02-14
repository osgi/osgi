package org.osgi.service.onem2m.dto;

import java.util.Map;

import org.osgi.dto.DTO;

/**
 * DTO expressing DasInfo. DAS is short for Dynamic Authorization Server.
 */
public class DasInfoDTO extends DTO{
//	@javax.xml.bind.annotation.XmlElement( required  = true)
	public java.lang.String uri;
	public Map<String,Object> dasRequest;
	public java.lang.String securedDasRequest;
}
