package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * DTO expressing ResourceWrapper.
 */
public class ResourceWrapperDTO extends DTO{
//	@javax.xml.bind.annotation.XmlElement( required  = true)
	/**
	 * URI for the resource
	 */
	public String uri;

	/**
	 * Resource
	 */
	public ResourceDTO resource;
}
