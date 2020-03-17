package org.osgi.service.onem2m.dto;

import org.osgi.dto.DTO;

/**
 * DTO expresses ResourceWrapper.
 * 
 * @see <a href=
 *      "http://www.onem2m.org/images/files/deliverables/Release3/TS-0004_Service_Layer_Core_Protocol_V3_11_2.pdf">oneM2M
 *      TS-0004 6.3.5.25</a>
 */
public class ResourceWrapperDTO extends DTO {
	/**
	 * Hierarchical URI of the resource
	 */
	public String uri;

	/**
	 * Resource
	 */
	public ResourceDTO resource;
}
